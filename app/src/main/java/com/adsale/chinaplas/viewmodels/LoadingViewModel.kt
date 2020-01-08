package com.adsale.chinaplas.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.adsale.chinaplas.BuildConfig
import com.adsale.chinaplas.cpsDatabase
import com.adsale.chinaplas.data.dao.*
import com.adsale.chinaplas.data.entity.BmobCount
import com.adsale.chinaplas.data.entity.CountryJson
import com.adsale.chinaplas.helper.CSVHelper
import com.adsale.chinaplas.helper.LoadingReceiver.LOADING_ACTION
import com.adsale.chinaplas.network.ApiService
import com.adsale.chinaplas.network.BASE_URL
import com.adsale.chinaplas.network.CpsApi
import com.adsale.chinaplas.rootDir
import com.adsale.chinaplas.ui.LoadingActivity
import com.adsale.chinaplas.utils.*
import com.tencent.bugly.beta.Beta
import kotlinx.coroutines.*


/**
 * Created by Carrie on 2019/10/17.
 */
class LoadingViewModel(private val app: Application,
                       private val regRepo: RegisterRepository,
                       private val webContentRepo: WebContentRepository,
                       private val exhibitorDao: ExhibitorDao) : AndroidViewModel(app) {
    private var context: Context = app.applicationContext
    var langChange = MutableLiveData<Boolean>()
    var language = ObservableField(-1)

    private val apiService: ApiService = CpsApi.retrofitService

    var adCountDownFinish = MutableLiveData<Boolean>()
    //    var d1Finish = MutableLiveData<Boolean>()
    //    var txtFinish = MutableLiveData<Boolean>()
    //    var txtDownCount: Int = 0
    //    var bmobFinish = MutableLiveData<Boolean>()
    //    var canIntent = MutableLiveData<Boolean>()

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val mainIconRepository: MainIconRepository = MainIconRepository.getInstance(cpsDatabase.mainIconDao())


    init {
        // 应用设备 尺寸信息，横竖屏  isFirstRunning
        language.set(getCurrLanguage())
    }

    /**
     * 有网情况下，下载
     */
    fun runOnNet() {
        initBuglySetting()
        // down txt
        uiScope.launch(Dispatchers.IO) {
            LogUtil.i("-------------   start download  ---------------")
            downloadTxt(TXT_AD)
            downloadTxt(TXT_PDF)

            downloadAllRegOptions()

            downloadAllCountries()
            downMainIcons()
            downWebContent()
            downFileControl()


            LogUtil.i("-------------   finish download  ---------------")
        }

    }

    fun sendBroadcast() {
        val intent = Intent(LOADING_ACTION)
        app.applicationContext.sendBroadcast(intent)
    }

    /**
     * 应用更新
     */
    private fun initBuglySetting() {
        Beta.autoInit = false
        Beta.checkUpgrade(false, true)
        Beta.showInterruptedStrategy = true
        Beta.canShowUpgradeActs.add(LoadingActivity::class.java)
        Beta.enableNotification = true
        Beta.canShowApkInfo = true
        Beta.autoDownloadOnWifi = true
        Beta.enableHotfix = BuildConfig.SUPPORT_HOT_UPDATE_FEATURE  // 是否开启热更新
    }


    fun onLangClick(lang: Int) {
        language.set(lang)
        LogUtil.i("onLangClick: $lang")
        langChange.value = true
    }

    private suspend fun getRegOptionUpdateTime(): String {
        return withContext(Dispatchers.IO) {
            val updatedAt = regRepo.getRegOptionLastUpdateTime()
            LogUtil.i("getRegOptionUpdateTime updatedAt =$updatedAt")
            if (!TextUtils.isEmpty(updatedAt)) {
                timeAddOneSecond(updatedAt)
            } else {
                FIRST_TIME_BMOB
            }
        }
    }

    private suspend fun getCountryUpdateTime(): String {
        return withContext(Dispatchers.IO) {
            val updatedAt = regRepo.getRegOptionLastUpdateTime()
            LogUtil.i("getCountryUpdateTime updatedAt =$updatedAt")
            if (!TextUtils.isEmpty(updatedAt)) {
                timeAddOneSecond(updatedAt)
            } else {
                FIRST_TIME_BMOB
            }
        }
    }

    private suspend fun getMainIconUpdateTime(): String {
        return withContext(Dispatchers.IO) {
            val updatedAt = mainIconRepository.getMainIconLUT()
            LogUtil.i("getMainIconUpdateTime updatedAt =$updatedAt")
            if (!TextUtils.isEmpty(updatedAt)) timeAddOneSecond(updatedAt) else FIRST_TIME_BMOB
        }
    }

    private suspend fun getWebContentUpdateTime(): String {
        return withContext(Dispatchers.IO) {
            val updatedAt = webContentRepo.getLastUpdateTime()
            LogUtil.i("getNoSqlUpdateTime updatedAt =$updatedAt")
            if (!TextUtils.isEmpty(updatedAt)) {
                timeAddOneSecond(updatedAt)
            } else {
                FIRST_TIME_BMOB
            }
        }
    }

    private suspend fun getFileControlUpdateTime(): String {
        return withContext(Dispatchers.IO) {
            val updatedAt = webContentRepo.getFileControlLUT()
            LogUtil.i("getFileControlUpdateTime updatedAt =$updatedAt")
            if (!TextUtils.isEmpty(updatedAt)) {
                timeAddOneSecond(updatedAt)
            } else {
                FIRST_TIME_BMOB
            }
        }
    }

    /**
     * 下载所有bmob RegOptionData
     */
    private fun downloadAllRegOptions() {
        uiScope.launch {
            //            clearRegOptionData()
            val time = getRegOptionUpdateTime()
            LogUtil.i("getRegOptionUpdateTime =$time")
            getRegOptionsFromBmob(time)   //
        }
    }

    private fun getRegOptionsFromBmob(updatedAt: String) {
        uiScope.launch {
            val startMill = System.currentTimeMillis()
            val getDeferred =
                CpsApi.retrofitService.getProductsAsync("{\"updatedAt\":{\"\$gte\":{\"__type\":\"Date\",\"iso\":\"$updatedAt\"}}}",
                    500)
            try {
                val responseBody = getDeferred.await()
                var content = responseBody.string().replace("{\"results\":", "")
                content = content.substring(0, content.length - 1)

                LogUtil.i("content=$content")

                val list: List<RegOptionData>? = parseListJson(RegOptionData::class.java, content)
                withContext(Dispatchers.IO) {
                    list?.let {
                        LogUtil.i("it=$it")
                        regRepo.insertRegOptionDataAll(it)
                    }
                }
                LogUtil.i("getRegOptionsFromBmob: ${list?.size}")
                val endMill = System.currentTimeMillis()
                LogUtil.i("getRegOptionsFromBmob spend Time :${endMill - startMill} ms")
                sendBroadcast()
            } catch (e: Exception) {
                LogUtil.e("getRegOptionsFromBmob::Failure: ${e.message} ")
                sendBroadcast()
            }
        }
    }

    /**
     * 下载所有bmob country数据
     */
    private fun downloadAllCountries() {
        uiScope.launch {
            val startTime = System.currentTimeMillis()
            val time = getCountryUpdateTime()
            LogUtil.i("getCountryUpdateTime =$time")

            val count = getCountryCountFromBmob(time)
            LogUtil.i("count =$count")
            if (count > 0) {
                val max = 500 * (1 + count / 500)
                LogUtil.i("max =$max")
                for (i in 0 until max step 500) {
                    LogUtil.i("开始下载：i=$i")
                    getCountriesFromBmob(i, time)
                }
            }
            val endTime = System.currentTimeMillis()
            LogUtil.i("downloadAllCountries end :${endTime - startTime} ms")

            sendBroadcast()
        }
    }

    /**
     * bmob上updateTime > 本地LUT 的数量。
     * 根据 [count] 来决定要分几次页下载。
     */
    private suspend fun getCountryCountFromBmob(updatedAt: String): Int {
        var count = 5
        val startMill = System.currentTimeMillis()
        val getDeferred =
            CpsApi.retrofitService.getCountryCountAsync("{\"updatedAt\":{\"\$gte\":{\"__type\":\"Date\",\"iso\":\"$updatedAt\"}}}",
                0,
                1)
        try {
            val content = getDeferred.await().string()
            val countEntity = parseJson(BmobCount::class.java, content)
            LogUtil.i("getCountryCountFromBmob: $content")
            val endMill = System.currentTimeMillis()
            LogUtil.i("getCountryCountFromBmob spend Time :${endMill - startMill} ms")
            count = countEntity!!.count
        } catch (e: Exception) {
            LogUtil.e("getCountryCountFromBmob::Failure: ${e.message} ")
        }
        return count
    }

    /**
     * 分页查询
     * skip limit的意思是：
     * 例如，skip=200,limit=500
     * 跳过前200行，获取第201 - （200+500）行的数据
     */
    fun getCountriesFromBmob(skip: Int, updatedAt: String) {
        uiScope.launch {
            val startMill = System.currentTimeMillis()
            val getDeferred =
                CpsApi.retrofitService.getCountryAsync("{\"updatedAt\":{\"\$gte\":{\"__type\":\"Date\",\"iso\":\"$updatedAt\"}}}",
                    500,
                    skip)
            try {
                val responseBody = getDeferred.await()
                var content = responseBody.string().replace("{\"results\":", "")
                content = content.substring(0, content.length - 1)
                val countries: List<CountryJson>? = parseListJson(CountryJson::class.java, content)
                withContext(Dispatchers.IO) {
                    countries?.let {
                        LogUtil.i("insertConuntryAll")
                        regRepo.insertCountryAll(it)
                    }
                }
                LogUtil.i("getCountriesFromBmob: ${countries?.size}")
                val endMill = System.currentTimeMillis()
                LogUtil.i("getCountriesFromBmob spend Time :${endMill - startMill} ms")
            } catch (e: Exception) {
                LogUtil.e("getCountriesFromBmob::Failure: ${e.message} ")
            }
        }
    }

    /**
     * 下载所有bmob NoSQL
     */
    private fun downloadNoSQLData() {
//        uiScope.launch {
//            //            clearRegOptionData()
//            val time = getNoSqlUpdateTime()
//            LogUtil.i("getNoSqlUpdateTime =$time")
//            getNoSqlFromBmob(time)   //
//        }
    }

    private fun downMainIcons() {
        uiScope.launch {
            val time = getMainIconUpdateTime()
            getMainIconFromBmob(time)
        }
    }

    private fun downWebContent() {
        uiScope.launch {
            val time = getWebContentUpdateTime()
            getWebContentFromBmob(time)
        }
    }

    private fun downFileControl() {
        uiScope.launch {
            val time = getFileControlUpdateTime()
            getFileControlFromBmob(time)
        }
    }


    /**
     * 分页查询
     * skip limit的意思是：
     * 例如，skip=200,limit=500
     * 跳过前200行，获取第201 - （200+500）行的数据
     */
    private fun getNoSqlFromBmob(updatedAt: String) {
//        uiScope.launch {
//            val startMill = System.currentTimeMillis()
//            val getDeferred =
//                CpsApi.retrofitService.getNoSQL("{\"updatedAt\":{\"\$gte\":{\"__type\":\"Date\",\"iso\":\"$updatedAt\"}}}",
//                    500)
//            try {
//                val responseBody = getDeferred.await()
//                var content = responseBody.string()
//                responseBody.close()
//                content = content.replace("{\"results\":", "").substringBeforeLast("}", "")
//                if (content != "[]") {
//                    processData(content)
//                } else {
//                    LogUtil.i("!processData []")
//                }
//                val endMill = System.currentTimeMillis()
//                LogUtil.i("getNoSQL spend Time :${endMill - startMill} ms")
//                sendBroadcast()
//            } catch (e: Exception) {
//                LogUtil.e("getNoSQL::Failure: ${e.message} ")
//                sendBroadcast()
//            }
//        }
    }

    private fun getMainIconFromBmob(updatedAt: String) {
        uiScope.launch {
            val startMill = System.currentTimeMillis()
            val getDeferred =
                CpsApi.retrofitService.getMainIconsBmobAsync("{\"updatedAt\":{\"\$gte\":{\"__type\":\"Date\",\"iso\":\"$updatedAt\"}}}")
            try {
                val content = getDeferred.await().string().replace("{\"results\":", "").substringBeforeLast("}", "")
                LogUtil.i("getMainIconFromBmob :$content")
                val list: List<MainIcon>? = parseListJson(MainIcon::class.java, content)
                withContext(Dispatchers.IO) {
                    list?.let {
                        LogUtil.i("insertMainIconAll,,, ${it.toString()}")
                        mainIconRepository.insertAll(it)
                    }
                }
                val endMill = System.currentTimeMillis()
                LogUtil.i("getMainIconFromBmob spend Time :${endMill - startMill} ms")
                sendBroadcast()
            } catch (e: Exception) {
                LogUtil.e("getMainIconFromBmob::Failure: ${e.message} ")
                LogUtil.e(e)
                sendBroadcast()
            }
        }
    }

    private fun getWebContentFromBmob(updatedAt: String) {
        uiScope.launch {
            val startMill = System.currentTimeMillis()
            val getDeferred =
                CpsApi.retrofitService.getWebContentsAsync("{\"updatedAt\":{\"\$gte\":{\"__type\":\"Date\",\"iso\":\"$updatedAt\"}}}",
                    500)
            try {
                val content = getDeferred.await().string().replace("{\"results\":", "").substringBeforeLast("}", "")
                LogUtil.i("getWebContentFromBmob :$content")
                val webContents: List<WebContent>? = parseListJson(WebContent::class.java, content)
                withContext(Dispatchers.IO) {
                    webContents?.let {
                        LogUtil.i("insertWCAll,,, ${it.toString()}")
                        webContentRepo.insertWebContentAll(it)
//                        for (entity in it) {
//                            async {
//                                if (!TextUtils.isEmpty(entity.FileName)) {
//                                    downHtmlZip(entity)
//                                }
//                            }
//                        }
                    }
                }
                val endMill = System.currentTimeMillis()
                LogUtil.i("getWebContentFromBmob spend Time :${endMill - startMill} ms")
                sendBroadcast()
            } catch (e: Exception) {
                LogUtil.e("getWebContentFromBmob::Failure: ${e.message} ")
                LogUtil.e(e)
                sendBroadcast()
            }
        }
    }

    private fun getFileControlFromBmob(updatedAt: String) {
        uiScope.launch {
            val startMill = System.currentTimeMillis()
            val getDeferred =
                CpsApi.retrofitService.getFileControlAsync("{\"updatedAt\":{\"\$gte\":{\"__type\":\"Date\",\"iso\":\"$updatedAt\"}}}",
                    500)
            try {
                val content = getDeferred.await().string().replace("{\"results\":", "").substringBeforeLast("}", "")
                LogUtil.i("getFileControlFromBmob :$content")
                val list: List<FileControl>? = parseListJson(FileControl::class.java, content)
                withContext(Dispatchers.IO) {
                    list?.let {
                        LogUtil.i("insertFileConAll,,, ${it.size}")
                        webContentRepo.insertFileControlAll(it)
                        for (entity in it) {
                            async {
                                if (!TextUtils.isEmpty(entity.FileName)) {
                                    downHtmlZip(entity)
                                }
                            }
                        }
                    }
                }
                val endMill = System.currentTimeMillis()
                LogUtil.i("getWebContentFromBmob spend Time :${endMill - startMill} ms")
                sendBroadcast()
            } catch (e: Exception) {
                LogUtil.e("getWebContentFromBmob::Failure: ${e.message} ")
                LogUtil.e(e)
                sendBroadcast()
            }
        }
    }

    /**
     * 下载，将更新时间插入数据库
     */
    private suspend fun downHtmlZip(entity: FileControl) {
        withContext(Dispatchers.IO) {
            LogUtil.i("html thread: ${entity.PageID} at ${Thread.currentThread()}")
            LogUtil.i("dir = ${entity.FileName?.substringBefore("/", "")}")
            try {
                val response = CpsApi.retrofitService.downloadHtmlZip("$BASE_URL${entity.FileName}")
                    .await()
                val isUnpackSuccess = unpackZip(entity.FileName!!,
                    response.byteStream(),
                    "${rootDir}${entity.FileName.substringBefore("/", "")}/",
                    webContentRepo,
                    entity.PageID)
                LogUtil.i("isUnpackSuccess=$isUnpackSuccess")

                when (entity.PageID) {
                    "E001" -> updateExhibitorData()
//                    "Seminar"
                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                LogUtil.e(e)
            }

        }
    }

    private var csvHelper: CSVHelper? = null
    private fun initCsvHelper() {
        csvHelper = CSVHelper.getInstance(exhibitorDao)
    }

    /**
     * 解析csv，插入数据库
     */
    private fun updateExhibitorData() {
        initCsvHelper()
        uiScope.launch {
            csvHelper!!.processExhibitorCsv()
        }
    }


//    private suspend fun processData(content: String) {
//        LogUtil.i("processData: content=$content")
//        withContext(Dispatchers.IO) {
//            val results: List<NoSQLEntity>? = parseListJson(NoSQLEntity::class.java, content)
//            LogUtil.i("processData:results=${results!!.size} ... ${results.toString()}")
//
//
//            if (results != null && results.size > 0) {
////            val jsonAdapter: JsonAdapter<NoSQL> = moshi.adapter(NoSQL::class.java)
////            val noSQL = jsonAdapter.fromJson(content)
////            if (noSQL != null) {
////                val size = noSQL.results.size
//                val size = results.size
//                var result: NoSQLEntity
//                var mainIconModel: MainIconModel
//                var mainIcon: MainIcon
//                for (i in 0 until size) {
//                    result = results[i]
//                    if (result.PartName == BMOB_MAINICON) {
//                        mainIconModel = parseJson(MainIconModel::class.java, result.Info)!!
//                        mainIcon = MainIcon(result.ID,
//                            mainIconModel.TitleCN,
//                            mainIconModel.TitleTW,
//                            mainIconModel.TitleEN,
//                            mainIconModel.BaiDu_TJ,
//                            mainIconModel.Icon,
//                            mainIconModel.MenuSeq,
//                            mainIconModel.DrawerSeq,
//                            mainIconModel.IsDelete,
//                            result.updatedAt)
//                        noSQLRepository.insertItemIcon(mainIcon)
//                    }
////                    else if (result.PartName == BMOB_WEBCONTENT) {
////                        val webContent = parseJson(WebContent::class.java, result.Info)
////                        webContent?.updatedAt = result.updatedAt
////                        webContent?.ID = result.ID
////                        LogUtil.i("webContent  = " + webContent.toString())
////                    }
//                }
//            }
//        }
//    }


    //    suspend fun getTxtBytes(txt: String): ByteArray {
    //        val bytes: ByteArray
    //        val getDeferredAd = apiService.downTxtAsync(txt)
    //        try {
    //            LogUtil.i("3. start downloadTxt:$txt at thread +${Thread.currentThread()}")
    //            val responseBody = getDeferredAd.await()
    //            LogUtil.i("4. start downloadTxt:$txt at thread +${Thread.currentThread()}")
    //            bytes = responseBody.bytes()
    //            responseBody.close()
    //            LogUtil.i("downloadTxt_Success:: $txt")
    //            return bytes
    //        } catch (e: Exception) {
    //            LogUtil.e("downloadTxt::Failure: ${e.message}")
    //        }
    //       throw Throwable()
    //    }

    //    fun downTxt2(txt: String): ByteArray {
    //      return withContext(Dispatchers.Default) {
    //          getTxtBytes(txt)
    //      }
    //    }

    fun downloadTxt(txt: String) {
        LogUtil.i("1. start downloadTxt:$txt at thread +${Thread.currentThread()}")
        uiScope.launch(Dispatchers.Default) {
            //        uiScope.launch {
            LogUtil.i("2. start downloadTxt:$txt at thread +${Thread.currentThread()}")
            val getDeferredAd = apiService.downTxtAsync(txt)
            try {
                LogUtil.i("3. start downloadTxt:$txt at thread +${Thread.currentThread()}")
                val responseBody = getDeferredAd.await()
                LogUtil.i("4. start downloadTxt:$txt at thread +${Thread.currentThread()}")
                val fos = context.openFileOutput(txt, Context.MODE_PRIVATE)
                fos.write(responseBody.bytes())
                LogUtil.i("writeTxt::: TXT_AD END")
                fos.close()
                responseBody.close()
            } catch (e: Exception) {
                LogUtil.e("downloadTxt::Failure: ${e.message}")
                sendBroadcast()
            }
            withContext(Dispatchers.Main) {
                LogUtil.i("downloadTxt_end in uiScope:: $txt at ${Thread.currentThread()}")
                sendBroadcast()
                //                txtDownCount++
                //                canIntent.value == true
            }
        }
        LogUtil.i("method end:: $txt at ${Thread.currentThread()}")
    }

    private fun downAdTxt() {

    }

    fun getUpdateData() {

    }

    private lateinit var timer: CountDownTimer
    fun addCountDownTime() {
        timer = object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {   //  millisUntilFinished / ONE_SECOND = 4,3,2,1,0
                LogUtil.i("**** onTick: ${millisUntilFinished / 1000}")
                adCountDownFinish.value = false
            }

            override fun onFinish() {
                adCountDownFinish.value = true
                LogUtil.i("ad count down finish ${adCountDownFinish.value}")
                setCountDownFinish()
                sendBroadcast()
            }
        }
        timer.start()
    }

    fun onD1Close() {
        adCountDownFinish.value = true
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }


}