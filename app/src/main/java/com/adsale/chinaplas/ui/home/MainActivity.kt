package com.adsale.chinaplas.ui.home

import android.content.Context
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
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
import com.adsale.chinaplas.helper.CalendarHelper
import com.adsale.chinaplas.mSPConfig
import com.adsale.chinaplas.ui.view.HelpView
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.utils.LogUtil.i
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory
import com.google.android.material.navigation.NavigationView


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
        i("MainActivity onCreate")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel


        drawerLayout = binding.drawerLayout
        navView = binding.navView


        oldLanguage = getCurrLanguage()
        init()
        initMainToolbar()
        initDrawer()
        bottomNav()

        binding.main.etSearch.setOnClickListener {
            navController.navigate(R.id.globalSearchFragment)
        }

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
        i("init()")


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
            i("bottomNav: ${menuItem.title}")

            when (menuItem.itemId) {
                R.id.menu_home -> {
                    navigateToHome()
                }
                R.id.menu_exhibitors -> {
//                    navController.popBackStack(R.id.menu_exhibitors, false)
                    navController.navigate(R.id.menu_exhibitors)
                }
                R.id.menu_floor -> navController.navigate(R.id.menu_floor)
                R.id.menu_tool -> navController.navigate(R.id.menu_tool)
//                R.id.menu_floor -> navController.popBackStack(R.id.menu_floor, true)
//                R.id.menu_tool -> navController.popBackStack(R.id.menu_tool, true)
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
            mainViewModel.isChangeRightIcon.value = false
//            mainViewModel.backRoad.value = BACK_DEFAULT
            if (click) {
                back()
            }
        })


//        if (mainViewModel.backListener == null) {
//            mainViewModel.backListener = androidx.appcompat.widget.ActionMenuView.OnMenuItemClickListener {
//                navController.popBackStack()  // back
//                true
//            }
//        }
//        actionMenuView.setOnMenuItemClickListener(mainViewModel.backListener)

        actionMenuView.setOnMenuItemClickListener {
            mainViewModel.backClicked.value = true
            true
        }

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
        i("logo h = $h")
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
            menu?.findItem(R.id.action_ok)?.isVisible = false

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
        setInnerMenuVisible()

        mainViewModel.isChangeRightIcon.observe(this, Observer { change ->
            i("isChangeRightIcon=$change")
            if (change) {
                menu?.findItem(R.id.action_calendar)?.isVisible = false
                menu?.findItem(R.id.action_home)?.isVisible = false
                menu?.findItem(R.id.action_ok)?.isVisible = true
            } else {
                setInnerMenuVisible()
            }
        })
    }

    private fun setInnerMenuVisible() {
        menu?.findItem(R.id.action_calendar)?.isVisible = false
        menu?.findItem(R.id.action_home)?.isVisible = true
        menu?.findItem(R.id.action_ok)?.isVisible = false
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

    private fun initDrawerHeader() {
        val headerLayout = navView.inflateHeaderView(R.layout.nav_header_main)
//        val headBinding = NavHeaderMainBinding.inflate(layoutInflater, navView, true)
//        mainViewModel = mainViewModel
//        lifecycleOwner = this
//        executePendingBindings()

        val tvLogin = headerLayout.findViewById<TextView>(R.id.tv_login)
        val ivUser = headerLayout.findViewById<ImageView>(R.id.iv_user)
        val tvSync = headerLayout.findViewById<TextView>(R.id.tv_sync)
        val tvLogout = headerLayout.findViewById<TextView>(R.id.tv_logout)

        tvLogin.setOnClickListener {
            i("onDrawerLogin")
            navController.navigate(R.id.myChinaplasLoginFragment)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        tvSync.setOnClickListener {
            i("onDrawerSync")
            Toast.makeText(applicationContext, "TBC", Toast.LENGTH_SHORT).show()
        }
        tvLogout.setOnClickListener {
            i("onDrawerLogout")
            alertDialogTwoButton(this,
                R.string.logout_message,
                R.string.logout,
                R.string.cancel,
                DialogInterface.OnClickListener { dialog, which ->
                    resetLoginInfo()
                    mainViewModel.isLogin.value = false
                })
        }
        mainViewModel.isLogin.observe(this, Observer { login ->
            LogUtil.i("isLogin=${login}")
            tvLogin.visibility = if (login) View.GONE else View.VISIBLE
            ivUser.visibility = if (login) View.VISIBLE else View.GONE
            tvSync.visibility = if (login) View.VISIBLE else View.GONE
            tvLogout.visibility = if (login) View.VISIBLE else View.GONE
            LogUtil.i("tvLogin.visibility=${tvLogin.visibility}")
            LogUtil.i("ivUser.visibility=${ivUser.visibility}")
        })
    }

    private fun initDrawer() {
        i("initDrawer")
        initDrawerHeader()


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
                    R.id.webViewFragment,
                    R.id.filterApplicationFragment,
                    R.id.exhibitorListFragment,
                    R.id.filterHallFragment,
                    R.id.filterIndustryFragment,
                    R.id.filterRegionFragment,
                    R.id.filterZoneFragment,
                    R.id.myLoginedFragment,
                    R.id.myExhibitorFragment,
                    R.id.exhibitorHistotyFragment,
                    R.id.eventSeminarFragment,
                    R.id.seminarDetailFragment,
                    R.id.eventDetailFragment,
                    R.id.newtechListFragment,
                    R.id.newtechFilterFragment,
                    R.id.newtechDetailFragment,
                    R.id.newtechDetailADFragment,
                    R.id.imageFragment,
                    R.id.scheduleFragment,
                    R.id.scheduleEditFragment
                ), drawerLayout
            )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//        navView.setNavigationItemSelectedListener { menuItem ->
//            i("nav setNavigationItemSelectedListener  ******************${menuItem.title}")
//
//            if(menuItem.itemId == R.id.nav_calendar){
//                i("nav nav_calendar us ******************")
//                true  // 显示白色背景
//            }else{
//                false  // 不显示白色背景
//            }
//        }
        navView.menu.findItem(R.id.nav_calendar).setOnMenuItemClickListener {
            i("nav nav_calendar us ******************")
            onAddToCalendar()
            false  // 不显示白色背景
        }
        navView.menu.findItem(R.id.nav_guide).setOnMenuItemClickListener {
            i("nav nav_guide us ******************")
            initHelpPager()
            drawerLayout.closeDrawer(GravityCompat.START)
            false  // 不显示白色背景
        }


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_home -> initMainToolbar()
                R.id.exhibitorDetailFragment -> initInnerToolbar()
                else -> initInnerToolbar()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        i("onCreateOptionsMenu")
        menuInflater.inflate(R.menu.main, menu)
        menu.findItem(R.id.action_home).isVisible = false  // 主页显示日历按钮，内页显示home按钮
        menu.findItem(R.id.action_ok).isVisible = false  // 主页显示日历按钮，内页显示home按钮
        menu.findItem(R.id.action_search).isVisible = isTablet()
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_calendar -> {
                onAddToCalendar()
            }
            R.id.action_home -> {  // 返回主界面
                navigateToHome()
            }
            R.id.action_search -> navController.navigate(R.id.globalSearchFragment)
            R.id.action_ok -> mainViewModel.onOkButtonClick()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onAddToCalendar() {
        CalendarHelper.getInstance(this).addToCalendar()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtil.getGrantResults(grantResults) && requestCode == PermissionUtil.PMS_CODE_READ_CALENDAR) {
            i("onRequestPermissionsResult：  有讀取日曆權限")
            onAddToCalendar()
        }
        if (PermissionUtil.getGrantResults(grantResults) && requestCode == PermissionUtil.PMS_CODE_WRITE_CALENDAR) {
            i("onRequestPermissionsResult：  有寫日曆權限")
            onAddToCalendar()
        }
    }

    private fun navigateToHome() {
        navController.popBackStack(R.id.nav_home, false)   // 跳到该Fragment, 并且弹出它之上的其他Fragment
        mainViewModel.resetBackDefault()
        hideInput(applicationContext, binding.root.windowToken)
    }

    override fun onSupportNavigateUp(): Boolean {
        i("onSupportNavigateUp")
        val navController =
            findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private lateinit var helpView: HelpView
    private var isHelperInited = false

    private fun initHelpPager() {
        if (!isHelperInited) {
            helpView = binding.main.helpView
            isHelperInited = true
        }
        helpView.visibility = View.VISIBLE
    }

    private fun back() {
        i("==backClicked==backRoad=${mainViewModel.backRoad.value}")
        if (mainViewModel.backRoad.value == BACK_DEFAULT) {
            i("backClicked popBackStack")
            navController.popBackStack()  // back
        } else {
            i("==backClicked:: backCustom ==true")
            mainViewModel.backCustom.value = true
        }
    }

    override fun onBackPressed() {
        val navDestination = navController.currentDestination
        if (navDestination == null
            || (navDestination.id == R.id.nav_home)
        ) {
            super.onBackPressed()
            i("HOME 退出")
        } else {
            back()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && newBase != null) {
                updateResources(newBase)
            } else newBase
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        switchLanguage(applicationContext, getCurrLanguage())
    }


}
