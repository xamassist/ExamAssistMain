package com.ravi.examassistmain.pdf

import android.Manifest
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.ravi.examassistmain.data.database.DocumentDatabase
import com.ravi.examassistmain.databinding.ActivityPdfBinding
import com.ravi.examassistmain.models.Document
import com.ravi.examassistmain.models.PdfPages
import com.ravi.examassistmain.utils.Constants.Companion.DB_NAME
import com.ravi.examassistmain.utils.LoadingUtils
import com.ravi.examassistmain.utils.observeOnce
import com.ravi.examassistmain.viewmodel.MainViewModel
import com.shockwave.pdfium.PdfPasswordException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.net.URL

@AndroidEntryPoint
class PdfActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener,
    OnPageErrorListener, View.OnTouchListener{

    private lateinit var binding: ActivityPdfBinding

    companion object {
        const val PREFIX = "/_"
    }
    private var menuItem: MenuItem? = null
    private var permissionGranted: Boolean? = false
    private var PERMISSION_CODE = 4040
    private var isDownloadable = false
    private var downloadId: Long = 0L
    var pdfView: PDFView? = null
    private var pageNumber = 0
    private var totalPageCount = 0
    private var pdfPassword: String? = null
    private var pdfFileName = ""

    var spinnerPageArray: MutableList<PdfPages> = mutableListOf()
    var isErrorOpeningFile = false
    lateinit var sharedPref: SharedPreferences
    private var downloadManger: DownloadManager? = null

    private lateinit var databaseHandler: DocumentDatabase
    private lateinit var  pdfViewModel: PdfViewModel

    var document: Document?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        document = intent.getSerializableExtra("document") as? Document
        LoadingUtils.showDialog(this,false)
        initData()
        initObservers()
    }

    private fun initData(){
        pdfViewModel = ViewModelProvider(this@PdfActivity)[PdfViewModel::class.java]
        sharedPref = this.getSharedPreferences(
            "saveSecretKey",
            MODE_PRIVATE)

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
                    pdfView.jumpTo(nextPage);
                    //  spinnerPage.setSelection(nextPage)
                }
            }
        }
        binding.ivPrev.setOnClickListener {
            getData()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            permissionGranted = true
            downloadPdf()
        }
    }



    private fun setCurrentPage(page: Int, pageCount: Int) {
//        pageNumber = page
//        totalPageCount = pageCount
//        title = String.format("%s %s / %s", "$pdfFileName ", page + 1, pageCount)
//        binding.tvPageInfo.text = spinnerPageArray[page].pageString
    }


    private fun getPdfTitle(url: String): String {
        try {
            val changeUrl = URL(url)
            val fileName = changeUrl.path
            val fileString = fileName.substring(fileName.lastIndexOf('/') + 1)
            val fileDropId = fileString.dropLast(17)
            return fileDropId.replace("%20", " ")
        } catch (e: Exception){
            e.printStackTrace()
        }

        return ""
    }

    private fun downloadPdf() {
        document?.pdfUrl?.let { pdf ->
            try {
                if (!permissionGranted!! && (document?.pdfUrl?.isNotEmpty() ==true)) {
                    document?.documentId?.let { docId ->
                        val fileName = document?.documentTitle ?:""
                        val appSpecificExternalDir = File(
                            this.getExternalFilesDir(null),
                            Environment.DIRECTORY_DOCUMENTS + "/" + docId
                        )
                        if (appSpecificExternalDir.exists()){
                            //if(isErrorOpeningFile){
                                try {
                                    appSpecificExternalDir.deleteRecursively()
                                }catch (e:Error){
                                    return
                                }
                            //}
                        }else{
                            //downloadPdf()
                        }
                        try {
                            val downloadUrl = Uri.parse(pdf)
                            downloadManger = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
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
                            //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                            registerReceiver(
                                onComplete,
                                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                            )

                            downloadId = downloadManger?.enqueue(request) ?: 0
                        } catch (e: Exception) {
                            Toast.makeText(
                                this,
                                "Unable to download file",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }else{

                }
            } catch (e: Exception) {
                Log.e("Error", e.toString())
            }
        }

    }
    private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override  fun onReceive(context: Context?, intent: Intent?) {
            if (context != null) {
                //  Toast.makeText(context, "File is Downloaded Successfully", Toast.LENGTH_SHORT).show()
                Log.v("PdfViewerActivity", "File downloaded")
                lifecycleScope.launch{
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
    private fun initObservers(){
        pdfViewModel.getPdfDocument.observe(this) { doc ->
            if(doc!=null){
                if(!doc.pdfPath.isNullOrBlank()){
                        val fileData = readFile(doc.pdfPath!!)
                        if (fileData != null) {
                            this.displayFromByte(fileData)
                        }
                }else{
                    downloadPdf()
                }
            }else{
               downloadPdf()
            }
        }
    }
    private fun getData() {
        lifecycleScope.launch {
            pdfViewModel.readPdfDocument.observeOnce(this@PdfActivity) { database ->
                if (database.isNotEmpty()) {
                    Log.d("RecipesFragment", "readDatabase called! ${database.size}")
                    //mAdapter.setData(database)
                } else {
                   // requestApiData()
                }
            }
        }
        document?.documentId?.let { docId ->
            pdfViewModel.getDoc(docId)
        }
    }


    private fun setUpDB() {
        databaseHandler =
            Room.databaseBuilder(this, DocumentDatabase::class.java, DB_NAME)
                .allowMainThreadQueries().build()
    }

    private fun displayFromByte(bytes: ByteArray) {
        binding.pdfView?.let {
            configurePdfViewAndLoad(it.fromBytes(bytes))
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuItem?.isVisible = isDownloadable
        return true
    }

    private fun configurePdfViewAndLoad(viewConfigurator: PDFView.Configurator) {

        pdfView?.useBestQuality(true)
        pdfView?.minZoom = 1f
        pdfView?.midZoom = 2.0f
        pdfView?.maxZoom = 5.0f
        pdfView?.fitsSystemWindows = true

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
               // formSpinnerArray(it)
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
        } else if (couldNotOpenFileDueToMissingPermission(exception)) {
            Toast.makeText(
                this,
                "Error opening file due to insufficient permission",
                Toast.LENGTH_LONG
            ).show()
        } else {
            isErrorOpeningFile = true
            //re-attempt file download
            downloadPdf()
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
        document?.pdfUrl?.let { pdf->
            try {
                document?.documentId?.let { docId ->
                    val filePath = getExternalFilesDir(
                        Environment.DIRECTORY_DOCUMENTS
                    ).toString() + "/" + docId

                    val updatedFilePath = getExternalFilesDir(
                        Environment.DIRECTORY_DOCUMENTS
                    ).toString() + PREFIX + docId

                    val fileData = readFile(filePath)
                    fileData?.let {
                        writeEncryptedFile(getEncryptedFile(updatedFilePath), it)
                        val directory = File(updatedFilePath)
                        try {
                            directory.deleteRecursively()
                        } catch (exp: Exception) {
                            println("file read exception: ${exp.message}")
                        }
                        document?.pdfPath = updatedFilePath
                        document?.let { doc->
                            lifecycleScope.launch{
                                doc.pdfPath?.let { pPath ->
                                    databaseHandler.documentDao().updateDocument(doc.documentId,pPath)
                                }
                            }

                        }
                     LoadingUtils.hideDialog()
                        this.displayFromByte(it)
                    }
                }

            } catch (e: Exception) {
                Log.d(this.localClassName, e.localizedMessage)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        LoadingUtils.hideDialog()
    }


    private fun getEncryptedFile(filePath : String): EncryptedFile {

        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedFile.Builder(
            applicationContext,
            File(filePath),
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
    }

    private fun writeEncryptedFile(encryptedFile: EncryptedFile, fileData: ByteArray) {
        encryptedFile.openFileOutput().apply {
            write(fileData)
            flush()
            close()
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