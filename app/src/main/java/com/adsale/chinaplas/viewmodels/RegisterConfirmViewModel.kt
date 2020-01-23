package com.adsale.chinaplas.viewmodels

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.confirmPdfPath
import com.adsale.chinaplas.fileAbsPath
import com.adsale.chinaplas.network.CpsApi
import com.adsale.chinaplas.network.REG_CONFIRM_LATTER_URL
import com.adsale.chinaplas.network.chargeRequestJson
import com.adsale.chinaplas.network.confirmKeyJson
import com.adsale.chinaplas.utils.*
import kotlinx.coroutines.*
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.*
import java.net.URL
import java.net.URLConnection

/**
 * Created by Carrie on 2019/12/9.
 */
const val DOWN_START = 0
const val DOWN_FINISH = 1
const val DOWN_NOT_START = -1

class RegisterConfirmViewModel : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    var progress = ObservableInt(0)
    var percent = ObservableField<String>()
    var isDownStatus = MutableLiveData(DOWN_NOT_START)  //  0 开始，1 完成
    var isRegister = true  // 默认为预登记，MyChinaplas我的登记信息过来，为false
    var confirmPath = "${fileAbsPath}/%s.pdf"
    var confirmKey: String? = ""

    init {
        LogUtil.i("!~~init RegisterConfirmViewModel~~")
    }

    suspend fun apiGetCharge(payMethod: String, guid: String, lang: String, fe: String, feCode: String): String {
        return withContext(Dispatchers.IO) {
            val json = chargeRequestJson(payMethod, guid, lang, fe, feCode)
            val requestBody: RequestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
            val chargeResponse = CpsApi.regService.PreregCharge(requestBody).await().string()
            LogUtil.i("chargeResponse=$chargeResponse")
            chargeResponse
        }
    }

    suspend fun apiPaySuccess(): Boolean {
        return withContext(Dispatchers.IO) {
            val requestBody: RequestBody =
                FormBody.Builder().add("guid", if (isRegister) getGuid() else getMyChinaplasGuid()).build()
            CpsApi.regService.PreregPaySuccess(requestBody).await().Status != "0"
        }
    }

    fun startDownload(guid: String) {
        uiScope.launch {
            confirmKey = getConfirmKey(guid)
            LogUtil.i("key = $confirmKey")
            if (confirmKey != null) {
                if (isRegister) {
                    saveConfirmPdfUrl("$REG_CONFIRM_LATTER_URL$confirmKey")
                } else {
                    saveCPSConfirmPdfUrl("$REG_CONFIRM_LATTER_URL$confirmKey")
                }
                isDownStatus.value = DOWN_START
            }
        }
    }

    private fun getConfirmUrl(): String {
        return if (isRegister) getConfirmPdfUrl()
        else getCPSConfirmPdfUrl()
    }


    private suspend fun getConfirmKey(guid: String): String? {
        return withContext(Dispatchers.IO) {
            val requestBody = RequestBody.create(
                MediaType.parse("application/json;charset=UTF-8"), confirmKeyJson(guid))
            CpsApi.regService.PreregConfirmKey(requestBody).await().Context
        }
    }

    fun downloadPDF() {
        uiScope.launch {
            downloadPDFAsync()
        }
    }

    fun confirmPath(): String {
        return if (isRegister)
            String.format(confirmPath, CONFIRM_PDF_REGISTER)
        else
            String.format(confirmPath, CONFIRM_PDF_CHINAPLAS)
    }

    suspend fun downloadPDFAsync() {
        LogUtil.i("downloadPDF:url=${getConfirmUrl()}")
        withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            DownloadTask().execute(getConfirmUrl(), confirmPath())
            val endTime = System.currentTimeMillis()
            LogUtil.i("downloadPDF： ${endTime - startTime} ms")
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class DownloadTask : AsyncTask<String, String, String>() {

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
            val progressInt = if (values[0] == null) 0 else values[0]!!.toInt()
            progress.set(progressInt)
            percent.set("$progressInt%")
            if (progressInt == 100) {
                isDownStatus.value = DOWN_FINISH
                LogUtil.i("onProgressUpdate: DOWN_FINISH")
            }
            LogUtil.i("onProgressUpdate: $progressInt")
        }

        override fun doInBackground(vararg params: String?): String {
            val url = URL(params[0])
            LogUtil.i("doInBackground_url: ${url.toString()}")
            LogUtil.i("doInBackground_path: ${params[1]!!}")
            val connection: URLConnection = url.openConnection()
            connection.connectTimeout = 30000
            connection.readTimeout = 30000


            connection.connect()
            val length: Int = connection.contentLength
            var total: Long = 0

            val bis = BufferedInputStream(url.openStream(), com.adsale.chinaplas.utils.BUFFER_SIZE)
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(8192)
                outputStream = FileOutputStream(File(params[1]!!))
                while (true) {
                    val read = bis.read(fileReader)
                    if (read == -1) {
                        LogUtil.i("doInBackground:read=-1")
                        break
                    }
                    total += read.toLong()
                    publishProgress("${((total * 100) / length).toInt()}")
                    LogUtil.i("total = $total, progress = ${((total * 100) / length).toInt()}")
                    outputStream.write(fileReader, 0, read)
                }
                outputStream.flush()
            } catch (e: IOException) {
                LogUtil.e("doInBackground: ${e.message}")
            } finally {
                bis.close()
                outputStream?.close()
            }
            return "Downloaded at confirm pdf "
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
        LogUtil.i("---onCleared--- RegisterConfirmViewModel")
    }

}