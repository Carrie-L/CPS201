package com.adsale.chinaplas

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import android.os.Environment
import android.os.LocaleList
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.utils.*
import com.baidu.mobstat.StatService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tencent.bugly.Bugly
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.properties.Delegates

/**
 * Created by Carrie on 2019/10/12.
 */

var displayHeight by Delegates.notNull<Int>()
lateinit var mSPConfig: SharedPreferences
lateinit var mSPReg: SharedPreferences
lateinit var mResources: Resources
lateinit var mAssetManager: AssetManager
var isTablet: Boolean = false
//var currLanguage = MutableLiveData(0)
//var hasNetwork = MutableLiveData<Boolean>()

var hasNetwork: Boolean = true
var fileAbsPath: String = ""
/*  预登记确认信PDF本地存储路径 */
var confirmPdfPath = ""
/*  打开本地PDF链接：localConfirmPdfPath + confirmPdfPath */
var localConfirmPdfPath = "file:///android_asset/pdfjs/web/viewer.html?file="
var rootDir: String = ""

lateinit var cpsDatabase: CpsDatabase

var moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())   // 不加这句，字段值为空
    .build()

class APP : Application() {
    private var DB_PATH: String = ""

    override fun onCreate() {
        super.onCreate()
        initSetting()

        isNetworkAvailable()

        copyDbFile("$DB_PATH/$DATABASE_NAME")
        cpsDatabase = CpsDatabase.getInstance(applicationContext)


        initCrashHandler()
        // 百度移动统计，开启自动埋点。 第三个参数：autoTrackWebView：
        StatService.autoTrace(this, true, true)

        LogUtil.i("======= onCreate ======")

    }

    init {
        LogUtil.i("======= init ======")


    }

    fun initSetting() {
        mSPConfig = getSharedPreferences(SP_CONFIG, Context.MODE_PRIVATE)
        mSPReg = getSharedPreferences(SP_REGISTER, Context.MODE_PRIVATE)
        mResources = resources
        mAssetManager = assets
        fileAbsPath = filesDir.absolutePath
        rootDir = getDir("cps20", Context.MODE_PRIVATE).absolutePath + "/"
        DB_PATH = "/data" + Environment.getDataDirectory().absolutePath + "/" + packageName + "/databases"
        confirmPdfPath = filesDir.absolutePath + "/${CONFIRM_PDF_REGISTER}.pdf"
    }

    private fun copyDbFile(dbfile: String) {
        try {
            val dir = File(DB_PATH)
            if (!dir.exists())
                dir.mkdir()
            if (!File(dbfile).exists()) {
                LogUtil.e("数据库不存在，从raw中导入")
                // 判断数据库文件是否存在，若不存在则执行导入，否则直接打开数据库
                val `is` = resources.openRawResource(R.raw.cps20) // 欲导入的数据库
                val fos = FileOutputStream(dbfile)
                val buffer = ByteArray(4000)
                var count = `is`.read(buffer)
                while (count > 0) {
                    fos.write(buffer, 0, count)
                    count = `is`.read(buffer)
                }
                fos.close()
                `is`.close()
                LogUtil.e("Copy db file Success ！")
                //                db = SQLiteDatabase.openOrCreateDatabase(dbfile, null)
            } else {
                LogUtil.e("数据库存在，直接打开")
            }
        } catch (e: Exception) {
            LogUtil.i("copy database fail:${e.message}")
        }
//        catch (e: IOException) {
//            LogUtil.i("copy database fail:${e.message}")
//        }
    }

    private fun initCrashHandler() {
        // 初始化本地CrashHandler
        CrashHandler.getInstance().init(applicationContext)
        /**
         * 初始化腾讯Bugly
         * 第三个参数为SDK调试模式开关，调试模式的行为特性如下：
         * 输出详细的Bugly SDK的Log；
         * 每一条Crash都会被立即上报；
         * 自定义日志将会在Logcat中输出。
         * 建议在测试阶段建议设置成true，发布时设置为false。
         */
        //        CrashReport.initCrashReport(applicationContext, "7a3aca24a7", false)
        Bugly.init(applicationContext, "7a3aca24a7", false)
    }

    private fun isNetworkAvailable() {
        val cm: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cm.requestNetwork(NetworkRequest.Builder().build(), object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    hasNetwork = true

                    LogUtil.i("网络连接：$hasNetwork")
                }

                override fun onLost(network: Network) {
                    super.onLost(network)

                    hasNetwork = false

                    //                    hasNetwork.value = ping()
                    LogUtil.i("网络断开 $hasNetwork")
                }
            })
        } else {
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            hasNetwork = activeNetwork?.isConnectedOrConnecting == true
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        LogUtil.i("attachBaseContext")
        super.attachBaseContext(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && newBase != null) {
            updateResources(newBase)
        } else newBase)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        // 必不可少。否則平板多語言會混亂
        switchLanguage(applicationContext, getCurrLanguage())
    }

}