package com.ravi.examassistmain.view.fragments

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.ravi.examassistmain.databinding.FragmentSyllabusBinding
import com.ravi.examassistmain.models.entity.Document
import com.ravi.examassistmain.models.entity.PdfDownloads
import com.ravi.examassistmain.utils.*
import com.ravi.examassistmain.viewmodel.MainViewModel
import com.shockwave.pdfium.PdfPasswordException
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*


class SyllabusFragment : Fragment(), OnPageChangeListener, OnLoadCompleteListener,
    OnPageErrorListener {
    private var permissionGranted: Boolean? = true
    private var downloadManger: DownloadManager? = null
    private var downloadId: Long = 0L
    private var PERMISSION_CODE = 4040
    var fileName = ""
    var fileLocation: File? = null

    private lateinit var mainViewModel: MainViewModel
    var subjectCode = ""
    var pdfDocument:Document?=null
    private lateinit var networkListener: NetworkListener
    lateinit var binding: FragmentSyllabusBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            subjectCode = it.getString("subject_code") ?:""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSyllabusBinding.inflate(LayoutInflater.from(container?.context))
        initObservers()
        getDocumentData()
        return binding.root
    }

    private fun displayFromByte(bytes: ByteArray) {
        binding.pdfView.let {
            configurePdfViewAndLoad(it.fromBytes(bytes))
        }
    }
   private fun initObservers(){
       mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        mainViewModel.downloadedPdf.observe(viewLifecycleOwner){
            if(it!=null){
                loadDocument(it.pdfPath)
            }else{
                pdfDocument?.pdfUrl?.let { url ->
                    downloadPdf(url)
                }
            }
        }
    }
    private fun getData(document:Document) {
        document.documentId.let { docId ->

            mainViewModel.getPdf(docId)
        }
    }
    private fun loadDocument(pdfPath:String) {

        Log.d("pdfPath", "initObservers: ${pdfPath}")
        if (pdfPath.isNotBlank()) {
            val fileData = readFile(pdfPath)
            LoadingUtils.hideDialog()
            if (fileData != null) {
                this.displayFromByte(fileData)
            }

        }else{
            Log.d("pdfPath", "file data present but no file path present")
        }

    }

    private fun displayFromFile(bytes: File) {
        configurePdfViewAndLoad(binding.pdfView.fromFile(bytes))
    }
    private fun getDocumentData() {
        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            readDatabase()
        }
    }
    private fun requestApiData(){
        mainViewModel.getDocuments(DocumentType.SYLLABUS.value,subjectCode)
        Log.d("subjectcode", "requestApiData: $subjectCode ")
        // mainViewModel.getAllDocuments(1)
        mainViewModel.documentResponse.observe(viewLifecycleOwner) { response ->
            response?.let { res ->
                Log.v("papersAdapterres", "got something ${res.data.toString()}")

                when (res) {

                    is NetworkResult.Success -> {
                        LoadingUtils.hideDialog()
                        if (!res.data.isNullOrEmpty()) {
                            val document = res.data.firstOrNull() { it.documentType == 2 && it.subject_code?.contains(subjectCode.trim()) == true }
                            pdfDocument= document
                            if (document != null) {
                                getData(document)
                            }
                            document?.pdfUrl?.let {
                               // loadDocument(it)
                                //get doc first

                                //downloadPdf(it)
                            }
                        } else {
                            Log.v(
                                "NotesAdapter",
                                "hot was successful but no data document received"
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        LoadingUtils.hideDialog()
                        Log.v("NotesAdapter", "Firestore call error")
                    }
                    is NetworkResult.Loading -> {
                        Log.v("NotesAdapter", "still loading  \uD83E\uDD74")
                        LoadingUtils.showDialog(requireContext())

                    }
                }
            }
        }
    }
    private fun readDatabase() {
        mainViewModel.getDocumentByDocType(DocumentType.PAPERS.value)
        Log.d("requestApiData", "requestApiData:$subjectCode ")

        lifecycleScope.launch {
            mainViewModel.readDocs.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    Log.d("RecipesFragment", "readDatabase called!")

                    val document = database.firstOrNull {
                        it.documentType == 2 && it.subject_code?.contains(subjectCode.trim()) == true
                    }
                    if(document==null){
                        requestApiData()
                    }else{
                        pdfDocument = document
                        loadDocument(document.pdfPath!!)
                    }
                } else {
                    requestApiData()
                }
            }
        }
    }
    override fun onPageChanged(page: Int, pageCount: Int) {
        TODO("Not yet implemented")
    }

    override fun loadComplete(nbPages: Int) {
        TODO("Not yet implemented")
    }

    override fun onPageError(page: Int, t: Throwable?) {
        TODO("Not yet implemented")
    }

    private fun configurePdfViewAndLoad(viewConfigurator: PDFView.Configurator) {

        binding.pdfView.apply {
            useBestQuality(true)
            minZoom = 1f
            midZoom = 2.0f
            maxZoom = 5.0f
            fitsSystemWindows = true
        }

        viewConfigurator
            .defaultPage(0)
            .onPageChange { page: Int, pageCount: Int ->

            }
            .onLoad {

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
            .password("")
            .swipeHorizontal(true)
            .autoSpacing(false)
            .pageSnap(true)
            .pageFling(true)
            .fitEachPage(true)
            .load()

    }


    private fun downloadPdf(pdfUrl:String) {
            try {
                if (pdfUrl.isNotEmpty()) {
                    //fileName = Date().time.toString()
                    val docId = pdfDocument?.documentId ?:""
                    val appSpecificExternalDir = File(
                        requireActivity().getExternalFilesDir(null),
                        Environment.DIRECTORY_DOCUMENTS + "/" + docId//fileName
                    )
                    fileLocation = appSpecificExternalDir
                    if (appSpecificExternalDir.exists()) {
                        //if(isErrorOpeningFile){
                        try {
                            appSpecificExternalDir.deleteRecursively()
                        } catch (e: Error) {
                            return
                        }
                        //}
                    }
                    try {
                        val downloadUrl = Uri.parse(pdfUrl)
                        downloadManger =
                            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
                        val request = DownloadManager.Request(downloadUrl)
                        request.setAllowedNetworkTypes(
                            DownloadManager.Request.NETWORK_WIFI or
                                    DownloadManager.Request.NETWORK_MOBILE
                        )
                        request.setAllowedOverRoaming(true)
                        request.setTitle(fileName)
                        request.setDescription("Downloading $fileName")
                        request.setDestinationInExternalFilesDir(
                            requireContext(),
                            Environment.DIRECTORY_DOCUMENTS,
                            docId
                        )
                        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                        requireActivity().registerReceiver(
                            onComplete,
                            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                        )
                        LoadingUtils.showDialog(requireContext(), false)
                        downloadId = downloadManger?.enqueue(request) ?: 0
                    } catch (e: Exception) {
                        Log.v("eroor:", e.toString())
                        Toast.makeText(
                            requireContext(),
                            "Unable to download file",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {

                }
            } catch (e: Exception) {
                Log.e("Error::", e.toString())
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

    fun encryptDownloadedFile() {
        try {
            pdfDocument?.documentId?.let { docId ->
                val filePath = requireContext().getExternalFilesDir(
                    Environment.DIRECTORY_DOCUMENTS
                ).toString() + "/" + docId

                val fileData = readFile(filePath)
                fileData?.let {
                    this.displayFromByte(it)
                }
                val pdf = PdfDownloads(docId,filePath)
                mainViewModel.insertPdf(pdf)
                LoadingUtils.hideDialog()
            }

        } catch (e: Exception) {
            Log.d(this.fileName, e.message?:"")
        }

    }

    private fun getEncryptedFile(filePath: String): EncryptedFile {

        val masterKey = MasterKey.Builder(requireContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedFile.Builder(
            requireContext(),
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


    private fun handleFileOpeningError(exception: Throwable) {
        if (exception is PdfPasswordException) {

        } else if (couldNotOpenFileDueToMissingPermission(exception)) {
            Toast.makeText(
                requireContext(),
                "Error opening file due to insufficient permission",
                Toast.LENGTH_LONG
            ).show()
        } else {
        }
    }

    private fun couldNotOpenFileDueToMissingPermission(e: Throwable): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) return false
        val exceptionMessage = e.message
        return e is FileNotFoundException && exceptionMessage != null && exceptionMessage.contains("Permission denied")
    }
    companion object {

        @JvmStatic
        fun newInstance(subjectCode:String) =
            SyllabusFragment().apply {
                arguments = Bundle().apply {
                    putString("subject_code", subjectCode)
                }
            }
    }
}
