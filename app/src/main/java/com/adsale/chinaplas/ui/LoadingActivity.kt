package com.adsale.chinaplas.ui

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.telephony.TelephonyManager
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adsale.chinaplas.*
import com.adsale.chinaplas.data.dao.*
import com.adsale.chinaplas.databinding.ActivityLoadingBinding
import com.adsale.chinaplas.helper.LoadingReceiver
import com.adsale.chinaplas.helper.LoadingReceiver.LOADING_ACTION
import com.adsale.chinaplas.ui.home.MainActivity
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.utils.LogUtil.i
import com.adsale.chinaplas.viewmodels.LoadingViewModel
import com.adsale.chinaplas.viewmodels.LoadingViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tencent.bugly.Bugly
import kotlinx.coroutines.*
import java.util.*

/**
 *  设置全屏
 *  第一次运行：
 *      三种语言
 *      横竖屏
 *
 *  检查版本更新
 *
 *  升级数据库，做好备份
 *
 *  下载txt files：
 *      ① advertisement.txt
 *      ② NewTechInfo.txt   （新技术产品广告）
 *      ③ PDFCenterInfo.txt （下载中心）
 *
 *   根据updateAt,获取Bmob最新数据
 *
 *   D1广告显示，2s后消失，若前期下载完成，则进入主页面；否则，在Loading页等待。
 */

/*
1.

bmob :
1. MainIcon
2. Country
3. RegOptionData

txt:
ad.txt
pdf.txt
---------------------------
Task:
0. 显示进度条
1. 显示语言按钮；点击后，按钮消失，记录选择的语言；在进入MainAty时重启Aty
2. 后台下载.......
3. 获取设备信息，DeviceID, 尺寸
4. 记录是否是第一次启动。判断是否是第一次启动
5. 判断有无网络
6. 显示D1广告
7. 点击D1广告，进入公司资料页, 没下载完成的继续下载（下载完成了取消，没下载完成不取消）
8. 广告显示2s后消失
9. 等待下载完成
10. 进入主界面
----
todo 1. 语言按钮显示，可能需要换成， IsFirstRunning
 2. 选择语言后，主页语言没有变化
 3. 对于下载的sendBroadcast. 寻找可以等待完成的方法
 4. 平板进入主页后，两次横竖屏转换

第一次：
4
1  3
5  Y ->  2;  N ->
0
6
8
9
10

第二次：
4
0
5  Y ->  2;  N ->
6
8
9
10




 */
class LoadingActivity : AppCompatActivity() {

    private lateinit var viewModel: LoadingViewModel
    private lateinit var binding: ActivityLoadingBinding
    private lateinit var registerRepository: RegisterRepository
    private lateinit var noSQLRepository: NoSQLRepository
    private lateinit var mReceiver: LoadingReceiver
    private val startTime: Long
    private var isFirstRunning: Boolean = false

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private var beginTime = 0L

    private var isTablet = false

    init {
        startTime = System.currentTimeMillis()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beginTime = System.currentTimeMillis()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loading)
        registerRepository = RegisterRepository.getInstance(cpsDatabase.countryDao(), cpsDatabase.regOptionDao())
        noSQLRepository = NoSQLRepository.getInstance(cpsDatabase.mainIconDao())
        val webContentRepository =
            WebContentRepository.getInstance(cpsDatabase.webContentDao(),
                cpsDatabase.htmlTextDao(),
                cpsDatabase.fileControlDao())
        viewModel =
            ViewModelProviders.of(this,
                LoadingViewModelFactory(application,
                    registerRepository,
                    webContentRepository,
                    CpsDatabase.getInstance(applicationContext).exhibitorDao()))
                .get(LoadingViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()

        setDeviceType()
        setFullScreen()
        isFirstRunning = isFirstRunning()
        i("isFirstRunning=${isFirstRunning}")
        if (isFirstRunning) {
            setupDevice()
        }

        mSPConfig.edit().putBoolean(LOADING_D1_FINISH, false)
            .putBoolean(LOADING_TXT_FINISH, false)
            .putBoolean("webServicesDownFinish", false)
            .putBoolean("apkDialogFinish", false)
            .putString("M1ClickId", "")
            .apply()
        registerBroadcastReceiver()

        if (hasNetwork) {
            val testUrl = "https://cdn-adsalecdn.oss-cn-shenzhen.aliyuncs.com/App/2020/MainIcon/main_1.png"
            Glide.with(this).load(Uri.parse(testUrl)).listener(requestListener).into(binding.ivTest)
        } else {
            showD1()
        }

        i("hasNetwork=$hasNetwork")

        //        viewModel.canIntent.observe(this, Observer {
        //
        //            if (viewModel.d1Finish.value == true
        //                && viewModel.txtDownCount == 2
        //            //                || viewModel.bmobFinish.value == true
        //            ) {
        //                LogUtil.i("进入主页面啦~~~")
        //                val intent = Intent(this@LoadingActivity, MainActivity::class.java)
        //                startActivity(intent)
        //                finish()
        //                val endTime = System.currentTimeMillis()
        //                LogUtil.i("Loading页总共花费: ${endTime - beginTime}")
        //            }
        //
        //        })

        if (viewModel.language.get() != -1) {
            i("language change:${viewModel.language.get()}")
        }

        viewModel.langChange.observe(this, Observer {
            if (it) {
                i("langChange showD1  ")
                showD1()
            }

        })

    }

    private fun setDeviceType() {
        isTablet = resources.getBoolean(R.bool.isTablet)
        setIsTablet(isTablet)
        i("isTablet=$isTablet")
        setRequestedOrientation()
    }

    private fun setupDevice() {
        getScreenSize()
    }

    private fun setRequestedOrientation() {
        requestedOrientation = if (isTablet) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun switchLanguage() {

    }

    private fun getScreenSize() {
        val display = windowManager.defaultDisplay
        var displayHeight = display.height
        val point = Point()
        display.getRealSize(point)
        var width = point.x
        var height = point.y
        i("screen width = $width,height = $height")

        /*  顶部状态栏 和 底部导航栏 高度 */
        var statusBarHeight = -1  // 50
        var navBarHeight = -1   // 96
        //获取status_bar_height资源的ID
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val bottomId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) { //根据资源ID获取响应的尺寸值
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
            navBarHeight = resources.getDimensionPixelSize(bottomId)
        }

        if (isTablet) {
            i("isTablet")
            val designWidth = 2048
            val designHeight = 1536 + navBarHeight
            val temp: Int
            if (width < height) {
                temp = width
                width = height
                height = temp
            }
            val contentWidth = (2048 * height) / 1632
            i("contentWidth = $contentWidth")
            val leftMargin: Int = (width - contentWidth) / 2
            val screenWidthRate: Float =
                (contentWidth / width).toFloat() // 实际屏幕宽度 比 主界面设计图片宽度 。这样在计算显示宽度时只需用 图片宽度 * rate 即为需要的宽度
            val heightRate = height.toFloat() / 1632f
            i("heightRate = $heightRate,screenWidthRate = $screenWidthRate")
            width = contentWidth
            i("mScreenHeight = $height")
            mSPConfig.edit().putInt(PAD_LEFT_MARGIN, leftMargin).putFloat("PadWidthRate", screenWidthRate)
                .putFloat("PadHeightRate", heightRate).apply()
        } else {
            val rio: Float = 1080f / 1920f
            LogUtil.i("rio=$rio")
            val rio2 = width.toFloat() / height.toFloat()
            LogUtil.i("rio2=$rio2")
            if (rio2 < rio) {
                binding.layoutBg.setBackgroundResource(R.drawable.loadingx2)
            } else {
                binding.layoutBg.setBackgroundResource(R.drawable.loading)
            }


        }

        setScreenSize(mSPConfig, width, height) // 减去状态栏高度

        i("displayHeight1=$displayHeight")
        displayHeight = height - statusBarHeight - navBarHeight
        i("displayHeight2=$displayHeight")

        mSPConfig.edit()
            .putInt(DISPLAY_HEIGHT, displayHeight)
            .putInt(STATUS_HEIGHT, statusBarHeight)
            .putInt("NAV_BAR_HEIGHT", navBarHeight)
            .apply()

        i("displayHeight=$displayHeight, width=$width, height=$height, statusBarHeight=$statusBarHeight, navBarHeight=$navBarHeight")

        setLanguageLocation(height)

    }

    /**
     * 语言按钮的位置 设置在白线上方
     */
    private fun setLanguageLocation(sh: Int) {
        if (isTablet) {
            return
        }
//        val statusHeight: Int = mSPConfig.getInt(STATUS_HEIGHT, 0)
//        var h = 820 * sh / 1334
//        i("sh=$sh")
//        i("h=$h")
//        i("statusHeight=$statusHeight")
//        i("px2dip=" + px2dip(statusHeight.toFloat()))
//        i("dip2px=" + dp2px(statusHeight.toFloat()))
//        h = h - statusHeight
//        val lyLanguage: LinearLayout = binding.lyLanguage
//        val params = RelativeLayout.LayoutParams(640, 65 * h / 820)
//        params.topMargin = h
//        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
//        lyLanguage.layoutParams = params
//        //        LogUtil.i(TAG, "sh=" + sh + ", topMargin=" + h + ", rate = " + (820 / h) + ", y=" + ((65 * h) / 820));
//        i("statusHeight=" + statusHeight + ", 2 =" + px2dip(statusHeight.toFloat()))
    }

    private fun registerBroadcastReceiver() {
        mReceiver = LoadingReceiver()
        val intentFilter = IntentFilter(LOADING_ACTION)
        registerReceiver(mReceiver, intentFilter)
        mReceiver.setOnLoadFinishListener { companyId ->
            unregisterReceiver(mReceiver)
            if (isFirstRunning) {
                setNotFirstRunning()
                switchLanguage(applicationContext, getCurrLanguage())
            }
            val intent = Intent(this@LoadingActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            val endTime = System.currentTimeMillis()
            i("~~~~ Loading页花费时间： ${endTime - startTime} ms ~~~~")
        }
    }

//    /**
//     * @param mContext
//     * @param language 0:ZhTw; 1:en;2:ZhCn;
//     */
//    private fun switchLanguage(mContext: Context, language: Int) {
//        i("switchLanguage=$language")
//        setCurrLanguage(language)
//        val resources = mContext.resources
//        val config = resources.configuration
//        val dm = resources.displayMetrics
//        val locale: Locale = getLocale(language)!!
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            val localeList = LocaleList(locale)
//            LocaleList.setDefault(localeList)
//            config.setLocales(localeList)
//            mContext.createConfigurationContext(config)
//        } else {
//            config.setLocale(locale)
//        }
//        Locale.setDefault(locale)
//        resources.updateConfiguration(config, dm)
//
////        recreate()
////        val intent = Intent(Bugly.applicationContext, MainActivity::class.java)
////        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
////        startActivity(intent)
////        finish()
////        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
//    }

    private fun setFullScreen() {
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private var requestListener: RequestListener<Drawable> = object : RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?,
                                  model: Any,
                                  target: Target<Drawable>,
                                  isFirstResource: Boolean): Boolean {
            i("_____________________requestListener: onLoadFailed :" + e!!.message)
            hasNetwork = false
            showD1()
            return false
        }

        override fun onResourceReady(resource: Drawable,
                                     model: Any,
                                     target: Target<Drawable>,
                                     dataSource: DataSource,
                                     isFirstResource: Boolean): Boolean {
            hasNetwork = true
            if (!isFirstRunning) {
                i("_____________________requestListener: showD1")
                showD1()
            }

            i("_____________________requestListener: onResourceReady ：true")
            viewModel.runOnNet()
            return false
        }
    }

    private fun writeTxt() {
        val isTxtFinish1 = MutableLiveData<Boolean>()
        val isTxtFinish2 = MutableLiveData<Boolean>()

        //        LogUtil.i("0. start writeTxt at thread +${Thread.currentThread()}")
        //        viewModel.ad_response.observe(this@LoadingActivity, Observer {
        //            LogUtil.i("1. start writeTxt $TXT_AD at thread +${Thread.currentThread()}")
        //            val fos = openFileOutput(TXT_AD, Context.MODE_PRIVATE)
        //            fos.write(it)
        //            LogUtil.i("writeTxt::: TXT_AD END")
        //            fos.close()
        //            isTxtFinish1.value = true
        //            showD1()
        //        })
        //        LogUtil.i("00. start writeTxt at thread +${Thread.currentThread()}")
        //        viewModel.pdf_response.observe(this@LoadingActivity, Observer {
        //            LogUtil.i("11. start writeTxt $TXT_PDF at thread +${Thread.currentThread()}")
        //            val fos = openFileOutput(TXT_PDF, Context.MODE_PRIVATE)
        //            fos.write(it)
        //            LogUtil.i("writeTxt::: TXT_PDF ")
        //            fos.close()
        //            isTxtFinish2.value = true
        //        })

        //        isTxtFinish1.observe(this, Observer {
        //            if (isTxtFinish2.value == true) {
        //                //                mSPConfig.edit().putBoolean(LOADING_TXT_FINISH, true).apply()
        //                //                val intent = Intent(LOADING_ACTION)
        //                //                sendBroadcast(intent)
        ////                viewModel.txtFinish.value = true
        ////                viewModel.canIntent.value = true
        //                LogUtil.i("isTxtFinish1 2 txt finish")
        //            }
        //        })
        //        isTxtFinish2.observe(this, Observer {
        //            if (isTxtFinish1.value == true) {
        //                //                mSPConfig.edit().putBoolean(LOADING_TXT_FINISH, true).apply()
        //                //                val intent = Intent(LOADING_ACTION)
        //                //                sendBroadcast(intent)
        //                viewModel.txtFinish.value = true
        //                viewModel.canIntent.value = true
        //                LogUtil.i("isTxtFinish2 1 txt finish")
        //            }
        //        })
    }

    private fun showD1() {
        LogUtil.i("=============showD1================")
        Glide.with(applicationContext).load(R.drawable.arburg_cn).into(binding.d1)
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.bottom_to_top)
        binding.d1.startAnimation(animation)
        viewModel.addCountDownTime()
        binding.ivClose.visibility = View.VISIBLE

//        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.bottom_to_top)
//        binding.d1.startAnimation(animation)
//        Glide.with(applicationContext).load(R.drawable.arburg_cn).into(binding.d1)
//        //倒计时2s
//        viewModel.addCountDownTime()
//        binding.ivClose.visibility = View.VISIBLE

        viewModel.adCountDownFinish.observe(this, Observer {
            if (viewModel.adCountDownFinish.value == true) {
                mSPConfig.edit().putBoolean(LOADING_D1_FINISH, true).apply()
                binding.ivClose.visibility = View.GONE
                binding.d1.visibility = View.GONE
                val animEnd = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
                binding.d1.startAnimation(animEnd)
//                viewModel.sendBroadcast()
                LogUtil.i("adCountDownFinish d1 finish")
            }
        })
    }


}
