package com.ravi.examassistmain.animation

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import com.ravi.examassistmain.R

class EALoader(context: Context): Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ea_loader)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(R.color.ea_white_transparent)
    }
}