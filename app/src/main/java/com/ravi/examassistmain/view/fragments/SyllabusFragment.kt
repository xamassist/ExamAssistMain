package com.ravi.examassistmain.view.fragments

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.ravi.examassistmain.R
import com.ravi.examassistmain.view.PdfActivity
import com.ravi.examassistmain.utils.LoadingUtils
import com.shockwave.pdfium.PdfPasswordException
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*


class SyllabusFragment : Fragment(), OnPageChangeListener, OnLoadCompleteListener,
    OnPageErrorListener {
    var pdfView: PDFView? = null
    private var permissionGranted: Boolean? = true
    private var downloadManger: DownloadManager? = null
    private var downloadId: Long = 0L
    private var PERMISSION_CODE = 4040
    var fileName = ""
    var fileLocation: File? = null


    val testUrl =
        "https://firebasestorage.googleapis.com/v0/b/examassist-b50d0.appspot.com/o/pdfs%2Fphusics1(2016-17)%20(1).pdf?alt=media&token=462b6351-b7c1-4004-9b3a-ddb900c6de0c"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_syllabus, container, false)
        pdfView = view.findViewById(R.id.pdfView)
        val myUri = Uri.parse(testUrl)
        downloadPdf()
        // displayFromByte(myUri)
        return view
    }

    private fun displayFromByte(bytes: ByteArray) {
        pdfView?.let {
            configurePdfViewAndLoad(it.fromBytes(bytes))
        }
    }

    private fun displayFromFile(bytes: File) {
        pdfView?.let {
            configurePdfViewAndLoad(it.fromFile(bytes))
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

        pdfView?.useBestQuality(true)
        pdfView?.minZoom = 1f
        pdfView?.midZoom = 2.0f
        pdfView?.maxZoom = 5.0f
        pdfView?.fitsSystemWindows = true

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



    private fun downloadPdf() {
        testUrl?.let { pdf ->
            try {
                if (permissionGranted!! && testUrl.isNotEmpty()) {
                    fileName = Date().time.toString()
                    val appSpecificExternalDir = File(
                        requireActivity().getExternalFilesDir(null),
                        Environment.DIRECTORY_DOCUMENTS + "/" + fileName
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
                    } else {
                        //downloadPdf()
                    }
                    try {
                        val downloadUrl = Uri.parse(pdf)
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
                            fileName
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

        testUrl?.let { pdf ->
            try {
                val filePath = requireActivity().getExternalFilesDir(
                    Environment.DIRECTORY_DOCUMENTS
                ).toString() + "/" + Date().time

                val updatedFilePath = requireActivity().getExternalFilesDir(
                    Environment.DIRECTORY_DOCUMENTS
                ).toString() + PdfActivity.PREFIX + Date().time
                val fileData = readFile(filePath)

                requireActivity().runOnUiThread {
                    LoadingUtils.hideDialog()
                    fileLocation?.let { displayFromFile(it) }
                }

//                fileData?.let {
//                    writeEncryptedFile(getEncryptedFile(updatedFilePath), it)
//                    val directory = File(updatedFilePath)
//                    try {
//                        directory.deleteRecursively()
//                    } catch (exp: Exception) {
//                        println("file read exception: ${exp.message}")
//                    }
//
//                    LoadingUtils.hideDialog()
//                    this.displayFromByte(it)
//                }


            } catch (e: Exception) {
                Log.d(requireActivity().localClassName, e.localizedMessage)
            }
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
            PapersFragment().apply {
                arguments = Bundle().apply {
                    putString("subjectCode", subjectCode)
                }
            }
    }

}
