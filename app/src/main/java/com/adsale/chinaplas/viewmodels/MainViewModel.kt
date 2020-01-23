package com.adsale.chinaplas.viewmodels

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.navigation.fragment.findNavController
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIcon
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.data.dao.RegisterRepository
import com.adsale.chinaplas.data.entity.CountryJson
import com.adsale.chinaplas.data.entity.MainBanner
import com.adsale.chinaplas.data.entity.RegDataJson
import com.adsale.chinaplas.fileAbsPath
import com.adsale.chinaplas.network.CpsApi
import com.adsale.chinaplas.network.DownloadTask
import com.adsale.chinaplas.network.REG_CONFIRM_LATTER_URL
import com.adsale.chinaplas.network.confirmKeyJson
import com.adsale.chinaplas.ui.tools.mychinaplas.MyChinaplasLoginFragmentDirections
import com.adsale.chinaplas.utils.*
import kotlinx.coroutines.*
import okhttp3.RequestBody
import java.io.File
import java.util.*

/**
 * Created by Carrie on 2019/10/12.
 */
class MainViewModel(val app: Application, val mainRepository: MainIconRepository) : AndroidViewModel(app) {
    private val context = app.applicationContext
    var isInnerIntent = MutableLiveData<Boolean>(false)
    var isChangeRightIcon = MutableLiveData<Boolean>(false)
    var title = MutableLiveData<String>()
    //    var language = MutableLiveData(0)

    var fragmentStacks = mutableListOf<Int>()

    /*  调用 popBack */
    var backClicked = MutableLiveData(false)
    /* 设置返回方式，默认为 popBack */
    var backRoad = MutableLiveData(BACK_DEFAULT)
    /* 当 backRoad = BACK_CUSTOM 时，监听，调用自定义方法 */
    var backCustom = MutableLiveData(false)

    val mainIcons = MutableLiveData<List<MainIcon>>()
    //    var _mainIcons = MutableLiveData<MutableList<MainIcon>>()
    private var mainIconss = mutableListOf<MainIcon>()

    var topBanners = MutableLiveData<List<MainBanner>>()
    var isBannersEmpty = MutableLiveData<Boolean>(true)
    //    private var _mainIcons: MutableList<MainIcon> = mutableListOf()

    private val job = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + job)
    private val regRepo: RegisterRepository

    private var _topBannerHeight = MutableLiveData<Int>()
    val topBannerHeight: LiveData<Int>
        get() = _topBannerHeight
    private var _d1Height = MutableLiveData<Int>()
    val d1Height: LiveData<Int>
        get() = _d1Height
    var dlMargin = 0
    private var _mainHeaderHeight = MutableLiveData<Int>()
    val mainHeaderHeight: LiveData<Int>
        get() = _mainHeaderHeight
    private var _rvMenuHeight = MutableLiveData<Int>()
    val rvMenuHeight: LiveData<Int>
        get() = _rvMenuHeight

    /*侧边栏登录*/
    var isLogin = MutableLiveData<Boolean>()

    init {
        isLogin.value = isMyChinaplasLogin()
        LogUtil.i("MainViewModel init~~~~~~~~~~~~~~~")
        //        initMainIcons()
        regRepo = RegisterRepository.getInstance(CpsDatabase.getInstance(context).countryDao(),
            CpsDatabase.getInstance(context).regOptionDao())


//        initMainBanners()
//        initMainIcons()


//                getNoSQL()
        //        getRegData(0)
        //        getRegData(1)
        //        getRegData(2)
        //        getRegData()


//        uiScope.launch {
//            val startTime = System.currentTimeMillis()
//            val guid = "89EE189A0BDB4E28B8960ED54A74BCAB"
//            val key = getConfirmKey(guid)
//            LogUtil.i("key = $key")
//            key?.let {
//                downloadPDF(key)
//                val endTime = System.currentTimeMillis()
//                LogUtil.i("下载pdf话费： ${endTime - startTime} ms")
//            }
//        }

        val url =
            "https://www.chinaplasonline.com/CPS20/prereginvoice/simp/?guid=lyb%2fnpnRk1fbymBbXVLztQTE53Zy3C3mMpX50d9SmGlOyA25NX2a1XoGJ%2f2TuZGnUK%2bmoOkKQEXyR5Mn2Uy%2bzg%3d%3d"

        val guid = subGetLastString(url, "=")


    }

    /**
     *  banner\ ad 有图片是固定高度。bottomNav 在xxxhdpi是56dp, 其他上 48dp. 也是固定的。
     *  所以分配 header 和 menu 的高度，就是 去掉statusbar 和 系统底部nav 后的displayHeight - banner - ad - nav 的高度.
     *  header 分 0.15倍 resetHeight.
     *  剩下的menu 高度，如果它大于按比例算来的高度，就用比例高度。剩下的空间分配给广告，让广告居中显示。
     *  这是在刘海屏与普通屏的适配
     */
    fun initMainSize() {
        _topBannerHeight.value = (getScreenWidth() * 293) / 641
        _d1Height.value = getScreenWidth() * IMG_HEIGHT / IMG_WIDTH
        val navHeight = app.resources.getDimension(R.dimen.bottom_nav_height).toInt()

        val restHeight = getDisplayHeight() - _topBannerHeight.value!! - d1Height.value!! - navHeight
        _mainHeaderHeight.value = (restHeight * 0.294).toInt()
        _rvMenuHeight.value = restHeight - _mainHeaderHeight.value!!

        val menuRateHeight = ((getScreenWidth() * MAIN_MENU_HEIGHT) / MAIN_MENU_WIDTH / 3) * 2

        if (_rvMenuHeight.value!! > menuRateHeight) {
            dlMargin = _rvMenuHeight.value!! - menuRateHeight
            LogUtil.i("menuRateHeight=$menuRateHeight,, dlMargin=$dlMargin")
            _rvMenuHeight.value = menuRateHeight
//            _d1Height.value = _d1Height.value!! + dlMargin
        }

        setMainMenuHeight(_rvMenuHeight.value!!)

        LogUtil.i("navHeight=$navHeight,, restHeight=$restHeight,, header=${_mainHeaderHeight.value} .. menu= ${_rvMenuHeight.value},, banner=${_topBannerHeight.value},, d1=${d1Height.value}")

    }

    private suspend fun getConfirmKey(guid: String): String? {
        return withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            val requestBody = RequestBody.create(
                okhttp3.MediaType.parse("application/json;charset=UTF-8"), confirmKeyJson(guid))
            val key = CpsApi.regService.PreregConfirmKey(requestBody).await().Context
            val endTime = System.currentTimeMillis()
            LogUtil.i("getConfirmKey： ${endTime - startTime} ms ")
            key
        }
//        LogUtil.i("key = $key") // myigZtYN9Ua4kF0UTqMUvWTD2r6D%2blNV7L9FlvSAOw0FBIX3ZelHu0uJwtA%2bcwtUb%2bpNXklMcyTIWvUsYElCAg%3d%3d
    }

    private suspend fun downloadPDF(key: String) {
        LogUtil.i("downloadPDF:key=$key")
        val pdfUrl = String.format(REG_CONFIRM_LATTER_URL, key)
        withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
//            val client = setupRxtrofitProgress()
//            val responseBody = CpsApi.regService.RegregConfirmPDF(key).await()
//            writeFile(responseBody.byteStream(), app.filesDir.absolutePath + "/confirm3.pdf")
//            LogUtil.i("downloadPDF:key222=$key")

            DownloadTask().execute("$REG_CONFIRM_LATTER_URL$key")
            val endTime = System.currentTimeMillis()
            LogUtil.i("downloadPDF： ${endTime - startTime} ms")

        }
    }

    fun getCountries() {

    }

    fun getRegData() {
        uiScope.launch(Dispatchers.IO) {
            val titles = regRepo.getTitles()
            val functions = regRepo.getFunctions()
            val products = regRepo.getProducts()

            LogUtil.i("titles = ${titles.toString()}")
            LogUtil.i("functions = ${functions.toString()}")
            LogUtil.i("products = ${products.toString()}")

            val fos = context.openFileOutput("RegProducts.json", Context.MODE_PRIVATE)
            val bytes: ByteArray = products.toString().toByteArray()
            fos.write(bytes)
            fos.close()

        }

    }

    fun writeTxt(context: Context, fileName: String, bytes: ByteArray) {
        val fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        fos.write(bytes)
        fos.close()
    }

    fun initMainBanners() {
        LogUtil.i("initMainBanners")
        parseMainBannerTxt("$fileAbsPath/MainBannerInfo.txt")
    }

    fun initMainIcons() {
        LogUtil.i("initMainIcons")
        uiScope.launch {
            mainIcons.value = getMainIcons()
        }
    }

    private fun parseMainBannerTxt(absPath: String) {
        val startMills = System.currentTimeMillis()
        if (File(absPath).exists()) {
            topBanners.value = parseListJson(MainBanner::class.java, readFile(absPath))
        } else {
            topBanners.value = parseListJson(MainBanner::class.java, readAssetFile("files/MainBannerInfo.txt"))
        }
        val endMills = System.currentTimeMillis()
        LogUtil.i("parseMainBannerTxt spend time ${endMills - startMills} ms")
    }

    private suspend fun getMainIcons(): List<MainIcon> {
        return withContext(Dispatchers.IO) {
            val startMills = System.currentTimeMillis()
            val list = mainRepository.getMainIcons()
            LogUtil.i("getMainIcons:${list.size}")
            val endMills = System.currentTimeMillis()
            LogUtil.i("getMainIcons spend time ${endMills - startMills} ms")
            list
        }
    }


    fun isInnerIntent(boolean: Boolean) {
        isInnerIntent.value = boolean
    }

    fun onOkButtonClick() {
        LogUtil.i("onOkButtonClick")
//        okClicked.value = true
        backClicked.value = true
        isChangeRightIcon.value = false
    }

    fun onDrawerLogin() {
        LogUtil.i("onDrawerLogin")
    }

    fun onDrawerSync() {
        LogUtil.i("onDrawerSync")
    }

    fun onDrawerLogout() {
        LogUtil.i("onDrawerLogout")
    }

    fun onTopPicClick(pos: Int) {
        LogUtil.i("onTopPicClick:$pos")
    }

    fun getMainIconsBmob() {
        uiScope.launch {
            val startMill = System.currentTimeMillis()
            val updateTime = "2019-10-17 15:35:50"
            val getDeferred =
                CpsApi.retrofitService.getMainIconsBmobAsync("{\"updatedAt\":{\"\$gte\":{\"__type\":\"Date\",\"iso\":\"$updateTime\"}}}")  // "{\"IconID\":\"M001\"}"
            try {
                val responseBody = getDeferred.await()
                //                LogUtil.i("getMainIconsBmob::Success:${responseBody.string()}")
                val endMill = System.currentTimeMillis()
                LogUtil.i("getMainIconsBmob spend Time :${endMill - startMill} ms")
            } catch (e: Exception) {
                LogUtil.e("getMainIconsBmob::Failure: ${e.message} ms")
            }
        }
    }


    /**
     * 下载RegData
     */
    fun getRegData(lang: Int) {
        uiScope.launch(Dispatchers.Default) {
            try {
                val responseBody = CpsApi.retrofitService.getRegData("CPS20",
                    if (lang == 0) "950" else if (lang == 1) "1250" else "936")
                    .await()
                val regData = parseJson(RegDataJson::class.java, responseBody.string())
                LogUtil.i("regData=${regData.toString()}")
                regData?.let {
                    val startTime = System.currentTimeMillis()
                    val countries: List<CountryJson> = regData.Api_CountryCity
                    //                    regRepo.insertCountryAll(countries)
                    LogUtil.i("getRegDataCountry成功：${countries.size}")

                    //                    writeTxt(context, "countries.json", countries.toString().toByteArray())

                    //                    val jobTitles: List<RegDataJson.RegProperty1> = regData.JobTitleList
                    //                    val jobFunctions: List<RegDataJson.RegProperty1> = regData.JobFuctionList
                    //                    val products: List<RegDataJson.RegProperty1> = regData.ProductList
                    //
                    //                    val titleSize = jobTitles.size
                    //                    val functionSize = jobFunctions.size
                    //                    val productSize = products.size
                    //                    var regTitle: RegOptionData
                    //                    var titleEntity: RegDataJson.RegProperty1
                    //                    for (i in 0 until titleSize) {
                    //                        regTitle = RegOptionData()
                    //                        titleEntity = jobTitles[i]
                    //                        regTitle.PartName = REG_JOB_TITLE
                    //                        regTitle.DetailCode = titleEntity.DetailCode
                    //                        regTitle.setNameAndOrder(lang, titleEntity.Name, if (i < 10) "J0$i" else "J$i")
                    //                        if (lang == 0) {
                    //                            regRepo.insertItemOptionData(regTitle)
                    //                        } else if (lang == 1) {
                    //                            regRepo.updateItemOptionDataEN(regTitle)
                    //                        } else {
                    //                            regRepo.updateItemOptionDataSC(regTitle)
                    //                        }
                    //                    }
                    //                    LogUtil.i("insert titleEntity 完成 lang=$lang")
                    //
                    //                    var regFunction: RegOptionData
                    //                    var functionEntity: RegDataJson.RegProperty1
                    //                    for (i in 0 until functionSize) {
                    //                        regFunction = RegOptionData()
                    //                        functionEntity = jobFunctions[i]
                    //                        regFunction.PartName = REG_JOB_FUNCTION
                    //                        regFunction.DetailCode = functionEntity.DetailCode
                    //                        regFunction.setNameAndOrder(lang, functionEntity.Name, if (i < 10) "F0$i" else "F$i")
                    //                        if (lang == 0) {
                    //                            regRepo.insertItemOptionData(regFunction)
                    //                        } else if (lang == 1) {
                    //                            regRepo.updateItemOptionDataEN(regFunction)
                    //                        } else {
                    //                            regRepo.updateItemOptionDataSC(regFunction)
                    //                        }
                    //                    }
                    //                    LogUtil.i("insert functionEntity 完成 lang=$lang")
                    //
                    //                    var regProduct: RegOptionData
                    //                    var product: RegDataJson.RegProperty1
                    //                    for (i in 0 until productSize) {
                    //                        regProduct = RegOptionData()
                    //                        product = products[i]
                    //                        regProduct.PartName = REG_PRODUCT
                    //                        regProduct.DetailCode = product.DetailCode
                    //                        regProduct.NameTC = product.Name
                    //                        regProduct.OrderTC = if (i < 10) "P0$i" else "P$i"
                    //                        regProduct.GroupCode = product.Code
                    //                        regProduct.setNameAndOrder(lang, product.Name, if (i < 10) "P0$i" else "P$i")
                    //                        if (lang == 0) {
                    //                            regRepo.insertItemOptionData(regProduct)
                    //                        } else if (lang == 1) {
                    //                            regRepo.updateItemOptionDataEN(regProduct)
                    //                        } else {
                    //                            regRepo.updateItemOptionDataSC(regProduct)
                    //                        }
                    //                    }
                    //                    LogUtil.i("insert product 完成 lang=$lang")

                    val endTime = System.currentTimeMillis()
                    LogUtil.i("getRegData 插入数据库完成: ${endTime - startTime} ms")
                }
            } catch (e: java.lang.Exception) {
                LogUtil.i("getRegData 失败：${e.message}")
            }

        }

    }

    /**
     * 更改了back 方式后，要还原成默认返回
     */
    fun resetBackDefault() {
        backRoad.value = BACK_DEFAULT
        backCustom.value = false
    }

    fun addFragmentID(fragmentId: Int) {
        fragmentStacks.add(fragmentId)
    }

    fun findFragmentId(fragmentId: Int): Boolean {
        var findFragmentId = false
        for ((i, id) in fragmentStacks.withIndex()) {
            if (id == fragmentId) {
                fragmentStacks.removeAt(i)
                LogUtil.i("findFragmentId")
                findFragmentId = true
                break
            }
        }
        return findFragmentId
    }

    fun removeFragmentId(fragmentId: Int) {
        for ((i, id) in fragmentStacks.withIndex()) {
            if (id == fragmentId) {
                fragmentStacks.removeAt(i)
                LogUtil.i("removeFragmentId")
                break
            }
        }
    }

    /**
     * 找到当前FragmentId， 并且把它之上的所有element id 从list移出。
     * 因为会调用 [findNavController().popBackStack(R.id.menu_tool, false)] 方法
     */
    fun findFragmentIdAndRemoveAllBeforeIt(fragmentId: Int): Boolean {
        var findFragmentId = false
        val stackTemps = mutableListOf<Int>()
        for ((i, id) in fragmentStacks.withIndex()) {
            stackTemps.add(id)
            if (id == fragmentId) {
                fragmentStacks.removeAll(stackTemps)
                LogUtil.i("findFragmentId")
                findFragmentId = true
                break
            }
        }
        return findFragmentId
    }



}