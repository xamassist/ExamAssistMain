package com.ravi.examassistmain.utils

import android.graphics.drawable.GradientDrawable
import com.ravi.examassistmain.R

class ViewUtils {
    companion object {
        val instance = ViewUtils()

    }

    fun drawCircle(backgroundColor: Int): GradientDrawable? {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.OVAL
        // shape.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        shape.setColor(backgroundColor)
        // shape.setStroke(10, borderColor)
        return shape
    }

    fun colorGenerator(): Int {
        val colorArray = arrayListOf(
            R.color.red,
            R.color.ea_blue_900,
            R.color.green,
            R.color.teal_700,
            R.color.ea_amber_A700,
            R.color.ea_brown_900,
            R.color.ea_yellow_900,
            R.color.purple_700,
        )
        return colorArray.random()
    }
    fun colorGenerator(index:Int): Int {
        val position = index%7
        val colorArray = arrayListOf(
            R.color.red,
            R.color.ea_blue_900,
            R.color.green,
            R.color.teal_700,
            R.color.ea_amber_A700,
            R.color.ea_brown_900,
            R.color.ea_yellow_900,
            R.color.purple_700,
        )
        return colorArray[position]
    }

}
enum class PreferenceType {
    UNIVERSITY,
    BRANCH,
    SEMESTER
}