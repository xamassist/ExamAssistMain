package com.ravi.examassistmain.view

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.ravi.examassistmain.R
import com.ravi.examassistmain.data.database.DocumentDatabase
import com.ravi.examassistmain.databinding.ActivityPdfBinding
import com.ravi.examassistmain.models.PdfPages
import com.ravi.examassistmain.models.entity.Document
import com.ravi.examassistmain.utils.Constants.Companion.DB_NAME
import com.ravi.examassistmain.utils.LoadingUtils
import com.ravi.examassistmain.utils.showToast
import com.ravi.examassistmain.viewmodel.PdfViewModel
import com.shockwave.pdfium.PdfPasswordException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream


@AndroidEntryPoint
class PdfActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener,
    OnPageErrorListener, View.OnTouchListener {

    private lateinit var binding: ActivityPdfBinding

    companion object {
        const val PREFIX = "/_"
    }

    private var menuItem: MenuItem? = null

    private var isDownloadable = false
    private var downloadId: Long = 0L
    var pdfView: PDFView? = null
    private var pageNumber = 0
    private var totalPageCount = 0
    private var pdfPassword: String? = null
    var spinnerPageArray: MutableList<PdfPages> = mutableListOf()
    var isErrorOpeningFile = false
    lateinit var sharedPref: SharedPreferences
    private var downloadManger: DownloadManager? = null

    private lateinit var databaseHandler: DocumentDatabase
    private lateinit var pdfViewModel: PdfViewModel
    var document: Document? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        document = intent.getParcelableExtra("document") as? Document
        LoadingUtils.showDialog(this, false)
        loadDocument()
        initData()
        initObservers()
    }

    private fun initData() {
        pdfViewModel = ViewModelProvider(this@PdfActivity)[PdfViewModel::class.java]
        sharedPref = this.getSharedPreferences(
            getString(R.string.pdf_save_key),
            MODE_PRIVATE
        )
        binding.pdfTitle.text = document?.documentTitle
        setListeners()
        setUpDB()
        getData()
    }

    private fun setListeners() {
        binding.ivNext.setOnClickListener {
            pdfView?.let { pdfView ->
                val nextPage = pageNumber + 1
                if (nextPage < totalPageCount) {
                    pdfView.jumpTo(nextPage)
                }
            }
        }
        binding.ivPrev.setOnClickListener {
            getData()
        }
    }


    private fun setCurrentPage(page: Int, pageCount: Int) {
//        pageNumber = page
//        totalPageCount = pageCount
//        title = String.format("%s %s / %s", "$pdfFileName ", page + 1, pageCount)
//        binding.tvPageInfo.text = spinnerPageArray[page].pageString
    }

    private fun downloadPdf() {
        document?.pdfUrl?.let { pdf ->
            try {
                if (document?.pdfUrl?.isNotEmpty() == true) {
                    document?.documentId?.let { docId ->
                        val fileName = document?.documentTitle ?: ""
                        val appSpecificExternalDir = File(
                            this.getExternalFilesDir(null),
                            Environment.DIRECTORY_DOCUMENTS + "/" + docId
                        )
                        if (appSpecificExternalDir.exists()) {
                            try {
                                appSpecificExternalDir.deleteRecursively()
                            } catch (e: Error) {
                                return
                            }
                        }
                        try {
                            val downloadUrl = Uri.parse(pdf)
                            downloadManger =
                                getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
                            val request = DownloadManager.Request(downloadUrl)
                            request.setAllowedNetworkTypes(
                                DownloadManager.Request.NETWORK_WIFI or
                                        DownloadManager.Request.NETWORK_MOBILE
                            )
                            request.setAllowedOverRoaming(true)
                            request.setTitle(fileName)
                            request.setDescription("Downloading $fileName")
                            request.setDestinationInExternalFilesDir(
                                this,
                                Environment.DIRECTORY_DOCUMENTS,
                                docId
                            )
                            registerReceiver(
                                onComplete,
                                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                            )

                            downloadId = downloadManger?.enqueue(request) ?: 0
                        } catch (e: Exception) {
                            Toast.makeText(
                                this,
                                getString(R.string.unable_to_load),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    showToast(this, getString(R.string.sww))
                }
            } catch (e: Exception) {
                Log.e("PDFDownload", e.toString())
            }
        }

    }

    private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null) {
                //  Toast.makeText(context, "File is Downloaded Successfully", Toast.LENGTH_SHORT).show()
                Log.v("PdfViewerActivity", "File downloaded")
                lifecycleScope.launch {
                    encryptDownloadedFile()
                }
                // pdfViewModel.setProgress(false)
            }
            context?.unregisterReceiver(this)
        }
    }

    private fun setCurrentLastPage(page: Int, pageCount: Int) {
        pdfView?.jumpTo(page)
        binding.tvPageInfo.text = spinnerPageArray[page].pageString

    }

    private fun initObservers() {
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun getData() {
        document?.documentId?.let { docId ->
            pdfViewModel.getDoc(docId)
        }
    }

    private fun loadDocument() {
        document?.let { doc ->
            Log.d("pdfPath", "initObservers: ${doc.pdfPath}")
            if (!doc.pdfPath.isNullOrBlank()) {
                val fileData = readFile(doc.pdfPath!!)
                LoadingUtils.hideDialog()
                if (fileData != null) {
                    this.displayFromByte(fileData)
                }

            } else {
                downloadPdf()
            }
        }
    }

    private fun setUpDB() {
        databaseHandler =
            Room.databaseBuilder(this, DocumentDatabase::class.java, DB_NAME)
                .allowMainThreadQueries().build()
    }

    private fun displayFromByte(bytes: ByteArray) {
        configurePdfViewAndLoad(binding.pdfView.fromBytes(bytes))
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuItem?.isVisible = isDownloadable
        return true
    }

    private fun configurePdfViewAndLoad(viewConfigurator: PDFView.Configurator) {
        pdfView?.apply {
            useBestQuality(true)
            minZoom = 1f
            midZoom = 2.0f
            maxZoom = 5.0f
            fitsSystemWindows = true
        }
        viewConfigurator
            .defaultPage(pageNumber)
            .onPageChange { page: Int, pageCount: Int ->
                setCurrentPage(
                    page,
                    pageCount
                )
            }
            .onLoad {
                binding.tvPageInfo.text = "1/$it"
            }
            .enableAnnotationRendering(true)
            .enableAntialiasing(true)
            .spacing(10) // in dp
            .onError { exception: Throwable? ->
                handleFileOpeningError(
                    exception!!
                )
            }
            .onPageError { _: Int, _: Throwable? ->

            }
            .pageFitPolicy(FitPolicy.BOTH)
            .password(pdfPassword)
            .swipeHorizontal(true)
            .autoSpacing(false)
            .pageSnap(true)
            .pageFling(true)
            .fitEachPage(true)
            .load()

    }

    private fun handleFileOpeningError(exception: Throwable) {
        if (exception is PdfPasswordException) {
            if (pdfPassword != null) {
                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
                pdfPassword = null
            }
        } else {
            isErrorOpeningFile = true
            //re-attempt file download
                downloadPdf()
        }
    }

    @Throws(Exception::class)
    fun readFile(filePath: String): ByteArray? {
        val file = File(filePath)
        return if (file.exists()) {
            val fileContents = file.readBytes()
            val inputBuffer = BufferedInputStream(
                FileInputStream(file)
            )
            inputBuffer.read(fileContents)
            inputBuffer.close()
            fileContents
        } else {
            null
        }
    }

    fun encryptDownloadedFile() {
            try {
                document?.documentId?.let { docId ->
                    val filePath = getExternalFilesDir(
                        Environment.DIRECTORY_DOCUMENTS
                    ).toString() + "/" + docId

                    val fileData = readFile(filePath)
                    fileData?.let {
                        document?.pdfPath = filePath
                        document?.let { doc ->
                            pdfViewModel.insertDocument(doc)
                        }
                        LoadingUtils.hideDialog()
                        this.displayFromByte(it)
                    }
                }

            } catch (e: Exception) {
                Log.d(this.localClassName, e.message?:"")
            }

    }


    override fun onPageChanged(page: Int, pageCount: Int) {

    }

    override fun loadComplete(nbPages: Int) {

    }

    override fun onPageError(page: Int, t: Throwable?) {

    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }
}