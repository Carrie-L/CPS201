package com.adsale.chinaplas.ui.test

import android.graphics.Point
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
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
import com.adsale.chinaplas.databinding.ActivityMainTestBinding
import com.adsale.chinaplas.mSPConfig
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.dp2px
import com.adsale.chinaplas.utils.setScreenSize
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
abstract class BaseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainTestBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar
    private lateinit var rlTitleBar: RelativeLayout
    private lateinit var tvTitle: TextView
    private lateinit var navController: NavController
    private var menu: Menu? = null

    private var screenWidth: Int = 0
    private var toolbarHeight: Int = 56

    var backListener: androidx.appcompat.widget.ActionMenuView.OnMenuItemClickListener? = null

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_test)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        initView()
        initData()

        init()
        initMainToolbar()
        initDrawer()
        bottomNav()
    }

    protected abstract fun initView()
    protected abstract fun initData()

    private fun init() {
        mainViewModel.isInnerIntent.value = false
        toolbar = binding.toolbar
        tvTitle = binding.toolbarTitle
        setSupportActionBar(toolbar)
        rlTitleBar = binding.rlToolbar
        toolbarHeight = dp2px(56f)
        getScreenSize()
        setLogoSize()
        initBackMenu()
        LogUtil.i("init()")
    }

    private fun bottomNav() {
        binding.bottomNav.setOnNavigationItemSelectedListener { menuItem ->
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
        val actionMenuView = binding.actionMenuView
        menuInflater.inflate(R.menu.left_icon, actionMenuView.menu)
//        actionMenuView.setOnMenuItemClickListener {
//            navController.popBackStack()  // back
//            true
//        }
        if (backListener == null) {
            backListener = androidx.appcompat.widget.ActionMenuView.OnMenuItemClickListener {
                navController.popBackStack()  // back
                true
            }
        }
        actionMenuView.setOnMenuItemClickListener(backListener)

    }

    private fun setLogoSize() {
        // set logo size      search bar:300*44
        val logo = binding.logo
        val w = (screenWidth * 212) / 320
        val h = (w * 122) / 1152
        val p = RelativeLayout.LayoutParams(w, h)
        p.addRule(RelativeLayout.CENTER_HORIZONTAL)
        p.topMargin = (toolbarHeight - h) / 2   // 垂直居中
        logo.layoutParams = p
    }

    private fun initMainToolbar() {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mainViewModel.isInnerIntent.value = false
        rlTitleBar.setBackgroundResource(R.drawable.main_header)
        setToolbarSize(472)   // main_header图片高度

        try {
            menu?.findItem(R.id.action_calendar)?.isVisible = true
            menu?.findItem(R.id.action_home)?.isVisible = false
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
            if (mainViewModel.isInnerIntent.value == true) toolbarHeight else (screenWidth * imgHeight) / 1500
        val param = LinearLayout.LayoutParams(screenWidth, height)
        rlTitleBar.layoutParams = param
    }

    private fun getScreenSize() {
        val display = windowManager.defaultDisplay
        val point = Point()
        display.getRealSize(point)
        screenWidth = point.x
        val height = point.y
        setScreenSize(mSPConfig, screenWidth, height - 50) // 减去状态栏高度

    }

    private fun initDrawer() {
        LogUtil.i("initDrawer")
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
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
                    R.id.myChinaplasFragment
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
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_calendar -> Toast.makeText(applicationContext, "add to calendar", Toast.LENGTH_LONG).show()
            R.id.action_home -> navController.navigate(R.id.nav_home)  // 返回主界面
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =
            findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}
