package com.ravi.examassistmain.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.ravi.examassistmain.R
import com.shockwave.pdfium.PdfPasswordException
import java.io.FileNotFoundException

class PdfViewer : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener,
    OnPageErrorListener, OnTouchListener {

    private var pdfView: PDFView? = null
    private var pageNumber = 0
    private var pdfPassword: String? = null

    //views
    private lateinit var ivNext: ImageView
    private lateinit var tvPageInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)
        pdfView = findViewById(R.id.pdfView)
        ivNext = findViewById(R.id.iv_next)
    }

    private fun configurePdfViewAndLoad(viewConfigurator: PDFView.Configurator) {

        pdfView?.useBestQuality(true)
        pdfView?.minZoom = 1f
        pdfView?.midZoom = 2.0f
        pdfView?.maxZoom = 4.0f

        viewConfigurator
            .defaultPage(pageNumber)
            .onPageChange { page: Int, pageCount: Int ->
            }
            .onLoad {
                tvPageInfo.text = "page 1 of $it"
            }
            .enableAnnotationRendering(true)
            .enableAntialiasing(true)

            // .scrollHandle(DefaultScrollHandle(this))
            .spacing(10) // in dp
            .onError { exception: Throwable? ->
                handleFileOpeningError(
                    exception!!
                )
            }
            .onPageError { page: Int, err: Throwable? ->

            }
            .pageFitPolicy(FitPolicy.WIDTH)
            .password(pdfPassword)
            .swipeHorizontal(true)
            .autoSpacing(true)
            .pageSnap(true)
            .pageFling(true)
            .load()

    }

    private fun handleFileOpeningError(exception: Throwable) {
        if (exception is PdfPasswordException) {
            if (pdfPassword != null) {
                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
                pdfPassword =
                    null // prevent the toast from being shown again if the user rotates the screen
            }
            //askForPdfPassword()
        } else if (couldNotOpenFileDueToMissingPermission(exception)) {
            //  readFileErrorPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            Toast.makeText(this, "Error opening file", Toast.LENGTH_LONG).show()
            //Log.e(com.gsnathan.pdfviewer.MainActivity.TAG, "Error when opening file", exception)
        }
    }

    private fun couldNotOpenFileDueToMissingPermission(e: Throwable): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) return false
        val exceptionMessage = e.message
        return e is FileNotFoundException && exceptionMessage != null && exceptionMessage.contains("Permission denied")
    }


    override fun onPageChanged(page: Int, pageCount: Int) {
        Log.v("PdfViewerActivity", "on page change -> $page and page count $pageCount")
    }

    override fun loadComplete(nbPages: Int) {
        Log.v("PdfViewerActivity", "on load complete  -> $nbPages")
    }

    override fun onPageError(page: Int, t: Throwable?) {
        Log.v("PdfViewerActivity", "on page error")
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }

}
