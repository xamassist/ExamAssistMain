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


    fun colorGenerator(index:Int): Int {
        var position =index
        if(position>7){
            position =index%7
        }
       return when(position){
            0->{
                R.color.red
            }
            1->{
                R.color.ea_blue_900
            }
            2->{
                R.color.green
            }
            3->{
                R.color.teal_700
            }
            4->{
                R.color.ea_amber_A700
            }
            5->{
                R.color.ea_brown_900
            }

            6->{
                R.color.ea_yellow_900
            }
            7->{
                R.color.purple_700
            }

           else -> R.color.purple_700
       }
//        val colorArray = arrayListOf(
//            R.color.red,
//            R.color.ea_blue_900,
//            R.color.green,
//            R.color.teal_700,
//            R.color.ea_amber_A700,
//            R.color.ea_brown_900,
//            R.color.ea_yellow_900,
//            R.color.purple_700,
//        )

    }

}
enum class PreferenceType {
    UNIVERSITY,
    BRANCH,
    SEMESTER
}