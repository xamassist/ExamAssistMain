package com.ravi.examassistmain.utils

import android.content.Context
import com.ravi.examassistmain.animation.EALoader

class LoadingUtils {
    companion object {
        private var eaLoader: EALoader? = null
        fun showDialog(
            context: Context?,
            isCancelable: Boolean
        ) {
            hideDialog()
            if (context != null) {
                try {
                    eaLoader = EALoader(context)
                    eaLoader?.let { jarvisLoader->
                        jarvisLoader.setCanceledOnTouchOutside(true)
                        jarvisLoader.setCancelable(isCancelable)
                        jarvisLoader.show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun hideDialog() {
            if (eaLoader!=null && eaLoader?.isShowing!!) {
                eaLoader = try {
                    eaLoader?.dismiss()
                    null
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}