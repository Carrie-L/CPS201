package com.adsale.chinaplas.utils

import android.content.Context
import android.text.TextUtils
import com.adsale.chinaplas.data.dao.HtmlText
import com.adsale.chinaplas.data.dao.WebContentRepository
import com.adsale.chinaplas.fileAbsPath
import com.adsale.chinaplas.mAssetManager
import com.adsale.chinaplas.moshi
import com.adsale.chinaplas.rootDir
import com.adsale.chinaplas.utils.LogUtil.e
import com.adsale.chinaplas.utils.LogUtil.i
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


/**
 * Created by Carrie on 2019/10/18.
 */


/*  ------------------------ File   --------------------------              */

fun readFilesDirFile(fileName: String): String {
    if (File(fileAbsPath + fileName).exists()) {
        return readFile(fileAbsPath + fileName)
    } else {
        return readAssetFile("files/$fileName")
    }
}

/**
 * 读取文件内容
 * @param absPath 文件的绝对路径
 */
fun readFile(absPath: String): String {
    //    val bufferReader = BufferedReader(InputStreamReader(ins))
    val bufferedInputStream = BufferedInputStream(FileInputStream(absPath))
    val byteArray = ByteArray(bufferedInputStream.available())
    bufferedInputStream.read(byteArray)
    return String(byteArray)
}

fun readAssetFile(assetPath: String): String {
    val bufferedInputStream = BufferedInputStream(mAssetManager.open(assetPath))
    val byteArray = ByteArray(bufferedInputStream.available())
    bufferedInputStream.read(byteArray)
    return String(byteArray)
}

fun getAssetInputStream(assetPath: String): InputStream {
    return BufferedInputStream(mAssetManager.open(assetPath))
}

fun getFileInputStream(absPath: String): InputStream {
    return BufferedInputStream(FileInputStream(absPath))
}

fun writeFile(context: Context, bytes: ByteArray) {
    val fos = context.openFileOutput("MainBannerInfo.txt", Context.MODE_PRIVATE)
    fos.write(bytes)
    fos.close()
}

fun writeFile(context: Context, bytes: ByteArray, fileName: String) {
    val fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
    fos.write(bytes)
    fos.close()
}

fun writeFile(inputStream: InputStream, path: String): Boolean {
//    val sPath =
//        Environment.getExternalStorageDirectory().toString() + "/com.adsale.chinaplas/DocumentsPDF/" + fileName
    i("path:>>>>$path")
    val file = File(path)
    return try {
        var outputStream: OutputStream? = null
        try {
            val fileReader = ByteArray(8192)
            outputStream = FileOutputStream(file)
            while (true) {
                val read = inputStream.read(fileReader)
                if (read == -1) {
                    break
                }
                outputStream.write(fileReader, 0, read)
            }
            outputStream.flush()
            true
        } catch (e: IOException) {
            false
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    } catch (e: IOException) {
        false
    }
}

/**
 * <font color="#f97798">解压zip</font>
 *
 * @param zipname eg:temp_20160905144037995.zip
 * @param zipPath zip包的绝对文件夹（不包含.zip，.zip的上一级）eg: /data/user/0/com.adsale.ChinaPlas/app_cps18/WebContent/MI00000046/
 * @return boolean
 * @version 创建时间：2016年6月3日 上午9:33:06
 */
fun unpackZip(zipname: String,
              `is`: InputStream?,
              zipPath: String,
              webContentRepository: WebContentRepository,
              pageID: String): Boolean {
    i("zipPath=$zipPath")
    val startTime = System.currentTimeMillis()
    val file = File(zipPath)
    if (!file.exists()) {
        file.mkdir()
    }
    val zis: ZipInputStream
    try {
        var filename: String
        zis = ZipInputStream(BufferedInputStream(`is`))
        var ze: ZipEntry? = null
        val buffer = ByteArray(4086)
        var count: Int
        if (!TextUtils.isEmpty(zipname)) {
            if (!File(zipPath).exists()) {
                File(zipPath).mkdir()
            }
            var unpack = false
            val sbTxt = StringBuilder()
            while (zis.nextEntry?.also { ze = it } != null) {
                if (ze != null) {
                    filename = ze!!.name
                    if (ze!!.isDirectory) { // 例： Transportation/
                        createDir(zipPath)
                        val fmd = File(zipPath + filename)
                        fmd.mkdirs()
                        continue
                    }
                    i("filename=$filename")
                    val fout = FileOutputStream(zipPath + filename)
                    while (zis.read(buffer).also { count = it } != -1) {
                        if (filename.endsWith("txt")) {
                            sbTxt.append(String(buffer.sliceArray(0 until count)))
                        } else {
                            fout.write(buffer, 0, count)
                        }
                    }
                    fout.close()

                    if (filename.endsWith("txt")) {
                        i(" 插入数据库htmltxt: $pageID")
                        val htmlText = parseJson(HtmlText::class.java, sbTxt.toString())
//                        val htmlText = HtmlText(pageID, sbTxt.toString())
                        webContentRepository.insertItemHtmlText(htmlText!!)
                    }
//                    LogUtil.i("sbTxt=\n ${sbTxt.toString()}")
                    zis.closeEntry()
                    unpack = true
                }
            }
            zis.close()
            return unpack
        }
    } catch (e: IOException) {
        e.printStackTrace()
        LogUtil.e("unpackZip: ${e.message}")
        return false
    }
    val endTime = System.currentTimeMillis()
    e("解压" + zipname + "的时间为" + (endTime - startTime) + "ms")
    return false
}

fun createDir(path: String): Boolean {
    i("old path=$path")
    return if (!File(path).exists()) {
        if (File(path).mkdir()) {
            false
        } else {
            val mkdirs = File(path).mkdirs()
            i("mkdirs=$mkdirs")
            false
        }
    } else false
}

fun getWebContentHtmlPath(pageID: String): String {
    val path = "WebContent/$pageID/${getHtmName()}"
    if (File("$rootDir$path").exists()) {
        i("webContent open sd html")
        return "file://$rootDir$path"
    }
    i("webContent open asset html")
    return "file:///android_asset/$path"
}

fun getHtmlPath(name: String, pageID: String): String {
    val path = "$name/$pageID/${getHtmName()}"
    if (File("$rootDir$path").exists()) {
        i(" $name open sd html")
        return "file://$rootDir$path"
    }
    i("$name open asset html")
    return "file:///android_asset/$path"
}


/*  ------------------------ Files  End --------------------------              */
/*  ------------------------ MoShi Parse Json   --------------------------              */
fun <T> parseListJson(clz: Class<T>, content: String): List<T>? {
    val type = Types.newParameterizedType(List::class.java, clz)
    val jsonAdapter: JsonAdapter<List<T>> = moshi.adapter(type)
    return jsonAdapter.fromJson(content)
}

fun <T> parseJson(clz: Class<T>, content: String): T? {
    val jsonAdapter: JsonAdapter<T> = moshi.adapter(clz)
    return jsonAdapter.fromJson(content)
}

/**
 * 文件在 getFilesDir目录下， App.filesDir
 *
 * @param fileName 相对路径 e.g.: "advertisement_2019.txt" || "appContents.txt"
 */
fun <T> parseFileJson(clz: Class<T>, fileName: String): T? {
    i("parseAssetFile: $fileName")
    val content = readFilesDirFile(fileName)
    return parseJson(clz, content)
}

/*  -----------------↑↑↑    MoShi Parse Json End↑  ↑↑↑   --------------------------              */

/*  ----------------  ↓↓↓  substring   ↓↓↓  -------------------              */
/**
 * 去掉最后一个字符串，返回前面的
 */
fun subLastString(str: String): String {
    return str.substring(0, str.length - 1)
}

fun subGetLastString(str: String, c: String): String {
    val s1 = str.substringAfterLast(c, "")
    i("s1=$s1")
    return s1
}


/*  ----------------  ↑↑↑  substring   ↑↑↑  -------------------              */

/*  Network */
/**
 * 判断当前网络是否可用
 * 0:可用；其他 不可用
 */
fun ping(): Boolean {
    val runtime = Runtime.getRuntime()
    val progress = runtime.exec("ping -c 3 www.baidu.com")
    val ret = progress.waitFor()
    i("ping: $ret")
    return ret == 0
}
