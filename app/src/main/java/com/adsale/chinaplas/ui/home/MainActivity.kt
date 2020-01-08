package com.adsale.chinaplas.ui.home

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.*
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.databinding.ActivityMainBinding
import com.adsale.chinaplas.mSPConfig
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.utils.LogUtil.i
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory
import com.google.android.material.navigation.NavigationView
import java.util.*


/**
 * todo:
 *
 * 平板样式
 * 切换动画
 * 主界面menu
 * 底部导航栏
 * MainIcon表
 * HelpPage  全屏
 * LoadingAty
 */
/*
* ROOM 数据库
* AWS
* 优先 ：
*       观众预登记
*
*
*  */
/**
 * {@link #initDrawer()}
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar
    private lateinit var rlTitleBar: RelativeLayout
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var tvTitle: TextView
    private lateinit var navController: NavController
    private var menu: Menu? = null

    private var screenWidth: Int = 0
    private var toolbarHeight: Int = 56
    private var oldLanguage = -1


    interface BackMenuClickListener {
        fun onBack()
    }

    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onActivityCreated, which we
     * do in this Fragment.
     *
     */
    val mainViewModel: MainViewModel by lazy {
        val activity = requireNotNull(this) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(
            this, MainViewModelFactory(
                activity.application,
                MainIconRepository.getInstance(CpsDatabase.getInstance(applicationContext).mainIconDao())
            )
        )
            .get(MainViewModel::class.java)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.i("MainActivity onCreate")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

//        initView()
//        initData()

        drawerLayout = binding.drawerLayout
        navView = binding.navView
        oldLanguage = getCurrLanguage()
        init()
        initMainToolbar()
        initDrawer()
        bottomNav()

    }

//    protected abstract fun initView()
//    protected abstract fun initData()

    private fun init() {
        mainViewModel.isInnerIntent.value = false
        toolbar = binding.main.toolbar
        tvTitle = binding.main.toolbarTitle
        setSupportActionBar(toolbar)
        rlTitleBar = binding.main.rlToolbar
        toolbarHeight = dp2px(56f)

        screenWidth = getScreenWidth()
        mainViewModel.initMainSize()

//        getScreenSize()
        setLogoSize()
        initBackMenu()
        LogUtil.i("init()")


        setupTablet()


    }

    private fun setupTablet() {
        setupPadWidth()
        setRequestedOrientation()
    }

    private fun setupPadWidth() {
        val layoutParams: FrameLayout.LayoutParams =
            FrameLayout.LayoutParams(getScreenWidth(), getScreenHeight() - getBottomNavHeight())
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL
        drawerLayout.layoutParams = layoutParams
    }

    private fun setRequestedOrientation() {
        requestedOrientation = if (isTablet()) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun bottomNav() {
        binding.main.content.bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            LogUtil.i("bottomNav: ${menuItem.title}")

            when (menuItem.itemId) {
                R.id.menu_home -> navController.navigate(R.id.nav_home)
                R.id.menu_exhibitors -> navController.navigate(R.id.menu_exhibitors)
//                R.id.menu_exhibitors -> navController.navigate(R.id.registerFragment)
//                R.id.menu_exhibitors -> navController.navigate(R.id.registerActivity)
                R.id.menu_floor -> navController.navigate(R.id.menu_floor)
                R.id.menu_tool -> navController.navigate(R.id.menu_tool)
            }
            true
        }
    }

    private fun initBackMenu() {
        val actionMenuView = binding.main.actionMenuView
        menuInflater.inflate(R.menu.left_icon, actionMenuView.menu)
//        actionMenuView.setOnMenuItemClickListener {
//            navController.popBackStack()  // back
//            true
//        }


        mainViewModel.backClicked.observe(this, Observer { click ->
            if (click) {
                LogUtil.i("backClicked 1")
                navController.popBackStack()  // back
            }
        })


//        if (mainViewModel.backListener == null) {
//            mainViewModel.backListener = androidx.appcompat.widget.ActionMenuView.OnMenuItemClickListener {
//                navController.popBackStack()  // back
//                true
//            }
//        }
//        actionMenuView.setOnMenuItemClickListener(mainViewModel.backListener)

        actionMenuView.setOnMenuItemClickListener(androidx.appcompat.widget.ActionMenuView.OnMenuItemClickListener {
            mainViewModel.backClicked.value = true
            true
        })

    }


    private fun setLogoSize() {
        // set logo size      search bar:300*44
        val logo = binding.main.logo
//        val w = (screenWidth * 212) / 320
//        val h = (w * 122) / 1152
        val w: Int
        val h: Int
        if (isTablet()) {
            h = MAIN_HEADER_HEIGHT_PAD - dp2px(36f)
            w = (TOP_LOGO_WIDTH * h) / TOP_LOGO_HEIGHT
        } else {
//            h = (0.3 * mainViewModel.mainHeaderHeight.value!!).toInt()
//            w =  (h * 1152) / 122
            w = screenWidth - dp2px(68 * 2f)
//            w = (screenWidth * 212) / 320
            h = (w * 122) / 1152

            initPhoneSearchSize(h, (toolbarHeight - h) / 2)
        }
        i("logoHeight=$h, logoWidth=$w")
        val p = RelativeLayout.LayoutParams(w, h)
        p.addRule(RelativeLayout.CENTER_HORIZONTAL)
        p.topMargin = (toolbarHeight - h) / 2   // 垂直居中
        logo.layoutParams = p
    }

    private fun initPhoneSearchSize(logoH: Int, top: Int) {
        val h: Int = (0.05 * getDisplayHeight()).toInt()
        LogUtil.i("logo h = $h")
        val param = RelativeLayout.LayoutParams(getScreenWidth(), h)
        param.marginStart = dp2px(24f)
        param.marginEnd = dp2px(24f)
        param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//        param.bottomMargin = (0.14 * mainViewModel.mainHeaderHeight.value!!).toInt()
        param.bottomMargin = (mainViewModel.mainHeaderHeight.value!! - logoH - top - h) / 2
        binding.main.etSearch.layoutParams = param

//        initBottomNavHeight()

    }


    private fun setPadLogoSize() {

    }

    private fun initMainToolbar() {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mainViewModel.isInnerIntent.value = false
        rlTitleBar.setBackgroundResource(R.drawable.main_header)
        setToolbarSize(if (isTablet()) MAIN_HEADER_HEIGHT_PAD else MAIN_HEADER_HEIGHT_PHONE)   // main_header图片高度

        // 设置主页和平板的搜索框
        if (isTablet()) {
            binding.main.etSearch.visibility = View.GONE
        } else {
            mainViewModel.isInnerIntent.observe(this, Observer { isInner ->
                binding.main.etSearch.visibility = if (isInner) View.GONE else View.VISIBLE
            })
        }

        try {
            menu?.findItem(R.id.action_calendar)?.isVisible = true
            menu?.findItem(R.id.action_home)?.isVisible = false

            mainViewModel.isInnerIntent.observe(this, Observer { isInner ->
                if (isInner) {
                    menu?.findItem(R.id.action_search)?.isVisible = false
                } else {
                    menu?.findItem(R.id.action_search)?.isVisible = isTablet()
                }
            })
        } catch (e: UninitializedPropertyAccessException) {  // menu的初始化onCreateOptionsMenu() 在initXX()后面
            LogUtil.e("lateinit property menu has not been initialized")
        }
    }

    private fun initInnerToolbar() {
        supportActionBar?.setDisplayShowTitleEnabled(true)
        mainViewModel.isInnerIntent.value = true
        rlTitleBar.setBackgroundResource(R.drawable.inner_header)
        setToolbarSize(toolbarHeight)  // 56dp

        mainViewModel.title.value = supportActionBar?.title.toString()
        toolbar.title = ""   // 只显示自己添加的TextView的Title，不显示toolbar的title

        menu?.findItem(R.id.action_calendar)?.isVisible = false
        menu?.findItem(R.id.action_home)?.isVisible = true
    }

    private fun setToolbarSize(imgHeight: Int) {
        val height: Int =
            if (mainViewModel.isInnerIntent.value == true)
                toolbarHeight
            else
                if (isTablet()) ((screenWidth * imgHeight) / MAIN_HEADER_WIDTH_PAD) else mainViewModel.mainHeaderHeight.value!!
//                if (isTablet()) ((screenWidth * imgHeight) / MAIN_HEADER_WIDTH_PAD) else (0.17 * getDisplayHeight()).toInt()

//        (screenWidth * imgHeight) / (if (isTablet()) MAIN_HEADER_WIDTH_PAD else MAIN_HEADER_WIDTH_PHONE)

        val param = LinearLayout.LayoutParams(screenWidth, height)
        i("toolbar height = $height")
        rlTitleBar.layoutParams = param
        mSPConfig.edit().putInt("main_header_height", height).apply()


    }


    private fun initBottomNavHeight() {
        val h: Int = (0.074 * getDisplayHeight()).toInt()
        val param = ConstraintLayout.LayoutParams(getScreenWidth(), h)
        param.topToBottom = R.id.guideline_bottom
        binding.main.content.bottomNav.layoutParams = param
    }


    private fun setPadSearchBar() {
        val searchBar = binding.main.etSearch


    }

    private fun initDrawer() {
        LogUtil.i("initDrawer")

        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration =
            AppBarConfiguration(
                setOf(
                    /* 想要哪个Fragment显示导航按钮，就把id添加进来 */
                    R.id.nav_home,
                    R.id.nav_download_center,
                    R.id.nav_subscribe,
                    R.id.nav_follow,
                    R.id.nav_guide,
                    R.id.nav_setting,
                    R.id.nav_calendar,
                    R.id.exhibitorDetailFragment,
                    R.id.menu_exhibitors,
                    R.id.menu_floor,
                    R.id.menu_tool,
                    R.id.registerFragment,
                    R.id.registerActivity,
                    R.id.registerPreFragment,
                    R.id.registerWebsiteFragment,
                    R.id.myChinaplasFragment,
                    R.id.globalSearchFragment,
                    R.id.generalInfoFragment,
                    R.id.visitorTipFragment,
                    R.id.webContentFragment,
                    R.id.myChinaplasLoginFragment,
                    R.id.webViewFragment
                ), drawerLayout
            )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_home) {
                initMainToolbar()
            } else if (destination.id == R.id.exhibitorDetailFragment) {
                LogUtil.i("destination.exhibitorDetailFragment ")
                initInnerToolbar()
            } else {
                initInnerToolbar()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        LogUtil.i("onCreateOptionsMenu")
        menuInflater.inflate(R.menu.main, menu)
        menu.findItem(R.id.action_home).isVisible = false  // 主页显示日历按钮，内页显示home按钮
        menu.findItem(R.id.action_search).isVisible = isTablet()
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_calendar -> Toast.makeText(applicationContext, "add to calendar", Toast.LENGTH_LONG).show()
            R.id.action_home -> navController.navigate(R.id.nav_home)  // 返回主界面
            R.id.action_search -> navController.navigate(R.id.globalSearchFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}
