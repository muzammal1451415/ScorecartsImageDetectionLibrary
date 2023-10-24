package com.scorecarts.skudetection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.IllegalArgumentException
import java.util.Random

class Detection(private val context: Context,
                private val modelFileName: String,
                private val modelLabelFileName: String,
                private val imageInputSize:Int = 416,
                private val confidenceScore: Double = 0.5,
                private val isQuantize: Boolean = false
                ){


    // Minimum detection confidence to track a detection.
    private val MAINTAIN_ASPECT = true
    private val sensorOrientation = 90

    private var detector: ObjectDetection? = null

    private var frameToCropTransform: Matrix? = null
    private var cropToFrameTransform: Matrix? = null
    private var tracker: MultiBoxTracker? = null
    private var trackingOverlay: OverlayView? = null

    protected var previewWidth = 0
    protected var previewHeight = 0


   init {
       initBox()
   }


    @Throws
    private fun initBox() {
        previewHeight = imageInputSize
        previewWidth = imageInputSize
        frameToCropTransform = ImageUtils.getTransformationMatrix(
            previewWidth, previewHeight,
            imageInputSize, imageInputSize,
            sensorOrientation, MAINTAIN_ASPECT
        )
        cropToFrameTransform = Matrix()
        var frameToCropTransform: Matrix? = frameToCropTransform as Matrix?
        frameToCropTransform?.invert(cropToFrameTransform)
        tracker = MultiBoxTracker(context)
        trackingOverlay?.addCallback { canvas -> tracker!!.draw(canvas) }
        tracker!!.setFrameConfiguration(
            imageInputSize,
            imageInputSize,
            sensorOrientation
        )
        try {
            detector = ObjectDetection.create(
                context.assets,
                modelFileName,
                "file:///android_asset/$modelLabelFileName",
                isQuantize,
                imageInputSize
            )


        } catch (e: IOException) {
          throw IllegalArgumentException("Detection object initialization failed")
        }
    }
    private fun getRandomColor(): Int {
        val random = Random()
        val red = random.nextInt(256) // Generate a random value between 0 and 255 for red
        val green = random.nextInt(256) // Generate a random value between 0 and 255 for green
        val blue = random.nextInt(256) // Generate a random value between 0 and 255 for blue

        // Combine the values to create a color
        return Color.rgb(red, green, blue)
        //return Color.RED
    }
    private fun handleResult(bitmap: Bitmap, results: List<Recognition>):Bitmap {

        val canvas = Canvas(bitmap)
        val paint = Paint()

        val originalImageWidth = bitmap.width
        val originalImageHeight = bitmap.height

        val scaleX = originalImageWidth.toFloat() / imageInputSize
        val scaleY = originalImageHeight.toFloat() / imageInputSize

        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5.0f



        val res = results.sortedBy { it.title }
        val groupBy = res.groupBy { it.title }
        val a = groupBy.values.toList()
        val b = a.sortedBy { it.size }
        val lis = mutableListOf<Recognition>()
        for(i in b){
            lis.addAll(i)
        }

        lis.reverse()
        var randomColor = getRandomColor()
        for ((index, result) in lis.withIndex()) {
            val location = result.getLocation()
            val scaledBoundingBox = RectF(
                location.left * scaleX,
                location.top * scaleY,
                location.right * scaleX,
                location.bottom * scaleY
            )
            if(index > 1) {
                Log.i("kjlklk","lis[$index].title: $${lis[index].title}")
                Log.i("kjlklk","lis[$index-1].title: $${lis[index-1].title}")
                if ((lis[index].title ?: "") != (lis[index - 1].title ?: "")) {
                    Log.i("kjlklk","Enter-----------------------------------------")

                    randomColor = getRandomColor()
                    randomColor = getRandomColor()
                }
            }
            if ( result.confidence!! >= confidenceScore) {
                Log.i("kjlklk","Index: $index")

                Log.i("Fsfsfsdf",(result.title ?: "")+" => $randomColor")
                paint.color = randomColor
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 5.0f
                canvas.drawRect(scaledBoundingBox, paint)
                paint.textSize = 30f // Text size
                paint.style = Paint.Style.FILL_AND_STROKE // Fill and Stroke style
                paint.strokeWidth = 2f // Stroke width (adjust as needed)
                val textBounds = Rect()
                paint.getTextBounds((result.title ?: ""), 0, (result.title ?: "").length, textBounds)
                paint.style = Paint.Style.FILL
                // Set the color of the text stroke
                canvas.drawText(result.title ?: "", scaledBoundingBox.left, scaledBoundingBox.top, paint)

            }
        }

        return bitmap
    }

    suspend fun getRecognitions(bitmap: List<Bitmap>):List<String>{
        val skuTitles = mutableListOf<String>()
        for(bit in bitmap){
            val titles = getRecognitions(bit)
            skuTitles.addAll(titles)
        }
        return skuTitles.distinct()
    }

    suspend fun getRecognitions(bitmap: Bitmap): List<String> = withContext(Dispatchers.IO) {
        val cropBitmap = Utils.processBitmap(bitmap, imageInputSize)
        return@withContext detector!!.recognizeImage(cropBitmap)
            .filter { it.confidence!! >= confidenceScore }// 1. filtering on the bases of confidence
            .distinctBy { it.title }// Grouping on the bases of title
            .mapNotNull { it.title } // Removing repeated tit
    }

    suspend fun detectImage(bitmap: Bitmap): Bitmap {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val cropBitmap = Utils.processBitmap(bitmap, imageInputSize)

        return withContext(Dispatchers.IO) {
            val results: List<Recognition> = detector!!.recognizeImage(cropBitmap)
            handleResult(mutableBitmap, results)
        }
    }






}