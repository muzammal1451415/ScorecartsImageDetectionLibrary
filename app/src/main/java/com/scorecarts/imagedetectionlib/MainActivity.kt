package com.scorecarts.imagedetectionlib

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.scorecarts.skudetection.Detection
import com.scorecarts.skudetection.ImageUtils
import com.scorecarts.skudetection.MultiBoxTracker
import com.scorecarts.skudetection.ObjectDetection
import com.scorecarts.skudetection.OverlayView
import com.scorecarts.skudetection.Recognition
import com.scorecarts.skudetection.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Random

class MainActivity : AppCompatActivity() {
    private lateinit var detectButton:Button
    private var imageView: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        detectButton = findViewById(R.id.detectButton)
        imageView = findViewById(R.id.imageView)
        var sourceBitmap1 = Utils.getBitmapFromAsset(this, "hdc_test1.jpg")
        var sourceBitmap2 = Utils.getBitmapFromAsset(this, "hdc_test2.jpg")
        var sourceBitmap3 = Utils.getBitmapFromAsset(this, "hdc_test3.jpg")
        var sourceBitmap4 = Utils.getBitmapFromAsset(this, "hdc_test4.jpg")
        var sourceBitmap5 = Utils.getBitmapFromAsset(this, "hdc_test5.jpg")
        var sourceBitmap6 = Utils.getBitmapFromAsset(this, "hdc_test6.jpg")
        var sourceBitmap7 = Utils.getBitmapFromAsset(this, "hdc_test7.jpg")
        var sourceBitmap8 = Utils.getBitmapFromAsset(this, "hdc_test8.jpg")

        val bitmaps = mutableListOf<Bitmap>()
        bitmaps.add(sourceBitmap1)
        bitmaps.add(sourceBitmap2)
        bitmaps.add(sourceBitmap3)
        bitmaps.add(sourceBitmap4)
        bitmaps.add(sourceBitmap5)
        bitmaps.add(sourceBitmap6)
        bitmaps.add(sourceBitmap7)
        bitmaps.add(sourceBitmap8)

        imageView!!.setImageBitmap(sourceBitmap1)

        val detection = Detection(
            this,
            "HDC_416_Oct3.tflite",
            "hdc_classes.txt")
        detectButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                val results = detection.getRecognitions(bitmaps)
                Log.i("jlksdfdjkffd", "Results: ${results}")
                Log.i("jlksdfdjkffd", "ResultsSize: ${results.size}")
            }
        }



    }



}