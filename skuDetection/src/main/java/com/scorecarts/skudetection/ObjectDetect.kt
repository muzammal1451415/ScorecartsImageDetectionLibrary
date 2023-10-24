package com.scorecarts.skudetection

import android.graphics.Bitmap
import android.graphics.RectF

interface ObjectDetect {

    fun recognizeImage(bitmap: Bitmap): List<Recognition>

    fun enableStatLogging(debug: Boolean)

    fun getStatString(): String?

    fun close()

    fun setNumThreads(num_threads: Int)

    fun setUseNNAPI(isChecked: Boolean)

    fun getObjThresh(): Float


}
/**
 * An immutable result returned by a Classifier describing what was recognized.
 */
class Recognition {
    /**
     * A unique identifier for what has been recognized. Specific to the class, not the instance of
     * the object.
     */
    val id: String?

    /**
     * Display name for the recognition.
     */
    val title: String?

    /**
     * A sortable score for how good the recognition is relative to others. Higher should be better.
     */
    val confidence: Float?

    /**
     * Optional location within the source image for the location of the recognized object.
     */
    private var location: RectF?
    var detectedClass = 0

    constructor(
        id: String?, title: String?, confidence: Float?, location: RectF?
    ) {
        this.id = id
        this.title = title
        this.confidence = confidence
        this.location = location
    }

    constructor(
        id: String?,
        title: String?,
        confidence: Float?,
        location: RectF?,
        detectedClass: Int
    ) {
        this.id = id
        this.title = title
        this.confidence = confidence
        this.location = location
        this.detectedClass = detectedClass
    }

    fun getLocation(): RectF {
        return RectF(location)
    }

    fun setLocation(location: RectF?) {
        this.location = location
    }

    override fun toString(): String {
        var resultString = ""
        if (id != null) {
            resultString += "[$id] "
        }
        if (title != null) {
            resultString += "$title "
        }
        if (confidence != null) {
            resultString += String.format("(%.1f%%) ", confidence * 100.0f)
        }
        if (location != null) {
            resultString += location.toString() + " "
        }
        return resultString.trim { it <= ' ' }
    }
}