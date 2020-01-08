package com.adsale.chinaplas.network

import android.content.Context
import android.os.AsyncTask
import com.adsale.chinaplas.utils.LogUtil
import java.io.*
import java.lang.System.out
import java.net.URL
import java.net.URLConnection

/**
 * Created by Carrie on 2019/12/9.
 */
class DownloadTask : AsyncTask<String, String, String>() {

    override fun onProgressUpdate(vararg values: String?) {
        super.onProgressUpdate(*values)
        val percent = values[0]?.toInt()
        LogUtil.i("onProgressUpdate: $percent")
    }

    override fun doInBackground(vararg params: String?): String {
        val url = URL(params[0])
        LogUtil.i("doInBackground_url: ${url.toString()}")
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
            outputStream = FileOutputStream(File("/data/user/0/com.adsale.chinaplas/files/confirm2.pdf"))
            while (true) {
                val read = bis.read(fileReader)
                if (read == -1) {
                    break
                }
                total += read.toLong()
                val percent: Int = ((total * 100) / length).toInt()
                publishProgress(percent.toString())
                outputStream.write(fileReader, 0, read)
            }
            outputStream.flush()
        } catch (e: IOException) {
        } finally {
            bis.close()
            outputStream?.close()
        }
        return "Downloaded at confirm pdf "
    }

}