package com.adsale.chinaplas.ui.home

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.AdViewPagerAdapter
import com.adsale.chinaplas.adapters.MenuAdapter
import com.adsale.chinaplas.confirmPdfPath
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIcon
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.databinding.FragmentHomeBinding
import com.adsale.chinaplas.databinding.FragmentHomePadBinding
import com.adsale.chinaplas.databinding.HomeTopImageBinding
import com.adsale.chinaplas.localConfirmPdfPath
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.utils.LogUtil.i
import com.adsale.chinaplas.viewmodels.HomeViewModel
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.File

class HomeFragment : Fragment() {
    /* 保存view. 当 popBackStack 时，返回Home, 如果已经保存，使用这种方式不会再重新初始化。
        但如果是navigate nav_home, 则不会保存状态，会重新初始化。*/
    private var isViewInited = false // 如果已经初始化数据，则不再进行初始化
    private var lastView: View? = null // 记录上次创建的view

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var phoneBinding: FragmentHomeBinding
    private lateinit var tabletBinding: FragmentHomePadBinding
    private lateinit var mContext: Context
    private lateinit var viewPager: ViewPager
    private lateinit var vpindicator: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var ivAd: ImageView

    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewPagerAdater: AdViewPagerAdapter
    private lateinit var pageListener: PageClickListener
    private var topViews: MutableList<View> = mutableListOf()

    private var adapter: MenuAdapter? = null

    private var job = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (lastView == null) {
            if (!isTablet()) {
                initPhoneViews(inflater, container)
            } else {
                initTabletViews(inflater, container)
            }
            initViewModel()
            homeViewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)
        } else {
            i("onCreateView NOT init ~~~")
        }
//        initPhoneViews(inflater, container)
//        initViewModel()
//        homeViewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)
        mainViewModel.isInnerIntent.value = false
        mContext = requireContext()
        return lastView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!isViewInited) {
            super.onViewCreated(view, savedInstanceState)
            i("onViewCreated init!!!")

            mainViewModel.initMainIcons()
            mainViewModel.initMainBanners()

            mainViewModel.topBanners.observe(this, Observer {
                if (it.isNotEmpty()) {
                    topBanner()
                }
            })

//            val rvHeight = (0.39 * getDisplayHeight()).toInt()
            if(!isTablet()){
                initPhoneTopBannerSize()
            }


            gridMenu()
            bottomAd()
            isViewInited = true
        } else {
            i("onViewCreated not init~~~")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        i("onCreate  init~~~")


    }

    private fun initPhoneViews(inflater: LayoutInflater, container: ViewGroup?) {
        i("onCreateView  init !!!")
        phoneBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        lastView = phoneBinding.root

        viewPager = phoneBinding.viewPager
        vpindicator = phoneBinding.llIndicator
        recyclerView = phoneBinding.rvMenu
        ivAd = phoneBinding.ivAd
    }

    private fun initViewModel() {
        mainViewModel = ViewModelProviders.of(
            requireActivity(),
            MainViewModelFactory(
                requireActivity().application,
                MainIconRepository.getInstance(CpsDatabase.getInstance(requireContext()).mainIconDao())
            )
        ).get(MainViewModel::class.java)
    }

    private fun initTabletViews(inflater: LayoutInflater, container: ViewGroup?) {
        i("onCreateView pad init !!!")
        tabletBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_pad, container, false)
        lastView = tabletBinding.root

        viewPager = tabletBinding.viewPager
        vpindicator = tabletBinding.llIndicator
        recyclerView = tabletBinding.rvMenu
        ivAd = tabletBinding.ivAd

        initPadTopBannerSize()
    }

    private fun initPhoneTopBannerSize() {
//        val height = (getScreenWidth() * 293) / 641
        val height = mainViewModel.topBannerHeight.value!!

//        val height = (0.26 * getDisplayHeight()).toInt()
        val params = ConstraintLayout.LayoutParams(getScreenWidth(), height)
        viewPager.layoutParams = params
        i("initPhoneTopBannerSize: height=$height, displayHeight=${getDisplayHeight()}")
    }

    /**
     * 设置平板banner部分尺寸
     */
    private fun initPadTopBannerSize() {
        if (isTablet()) {
            val height: Int = (DESIGN_MAIN_BANNER_HEIGHT_PAD * getPadHeightRate()).toInt()
            val width: Int = (0.64 * getScreenWidth()).toInt()   // banner占 0.64， menu占 0.36
            val params = ConstraintLayout.LayoutParams(width, height)
            viewPager.layoutParams = params
            i("initPadTopBannerSize: width=$width, height=$height")
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // pad switch language
    }

    private fun processMenuIntent(entity: MainIcon) {
        i("processMenuIntent:${entity.BaiDu_TJ}")
        when (entity.BaiDu_TJ) {
//            BD_VISITOR -> NavHostFragment.findNavController(this).navigate(HomeFragmentDirections.actionNavHomeToRegisterFragment())  // todo register
//            BD_VISITOR -> NavHostFragment.findNavController(this).navigate(HomeFragmentDirections.actionNavHomeToRegisterPreFragment())   // todo registerPre
            BD_VISITOR -> intentToRegister()  // todo registerPre
//            BD_VISITOR -> NavHostFragment.findNavController(requireParentFragment()).navigate(
//                HomeFragmentDirections.actionNavHomeToRegisterWebsiteFragment(
//                    ""))   // todo registerWebview
//            BD_VISITOR ->  requireActivity().showFragment<RegisterWebsiteFragment>(R.id.nav_host_fragment)
//            BD_VISITOR -> NavHostFragment.findNavController(this).navigate(R.id.registerWebsiteFragment)  // todo registerWebview
//                        BD_VISITOR -> NavHostFragment.findNavController(this).navigate(HomeFragmentDirections.actionNavHomeToRegisterActivity())  /
//            BD_EXHIBITOR_LIST -> NavHostFragment.findNavController(this).navigate(HomeFragmentDirections.actionNavHomeToMenuExhibitors())
            BD_EXHIBITOR_LIST -> NavHostFragment.findNavController(this).navigate(R.id.exhibitorListFragment)
            BD_GENERAL_INFO -> NavHostFragment.findNavController(this).navigate(HomeFragmentDirections.actionNavHomeToGeneralInfoFragment(entity.BaiDu_TJ))
            BD_VISIT_TIP -> NavHostFragment.findNavController(this).navigate(HomeFragmentDirections.actionNavHomeToVisitorTipFragment(entity.BaiDu_TJ))

        }

    }




    private fun intentToRegister() {
//        NavHostFragment.findNavController(this)
//            .navigate(HomeFragmentDirections.actionNavHomeToRegisterWebsiteFragment(""))
//        return


//        NavHostFragment.findNavController(this)
//            .navigate(R.id.registerActivity)
//        return

        if (isPaySuccess()) {
            if (File(confirmPdfPath).exists()) {
                /* open pdf */
//                val pdfPath = "file:///android_asset/pdfjs/web/viewer.html?file=$confirmPdfPath"
                val pdfPath = localConfirmPdfPath + confirmPdfPath
                NavHostFragment.findNavController(this)
                    .navigate(HomeFragmentDirections.actionNavHomeToRegisterWebsiteFragment(pdfPath))
            } else {
                /* down pdf */
                NavHostFragment.findNavController(this)
                    .navigate(HomeFragmentDirections.actionNavHomeToRegisterWebsiteFragment(getConfirmPdfUrl()))
            }
        } else {
            /* 预登记前置页，检测账户是否存在 */
            NavHostFragment.findNavController(this)
                .navigate(HomeFragmentDirections.actionNavHomeToRegisterPreFragment())
        }
    }

    private fun intentGeneralInfo(){

    }

    private fun topBanner() {
        pageListener = PageClickListener { pos ->
            i("pageListener:$pos")
        }
        initBannerViews()  // DataBinding must be created in view's UI Thread
        viewPagerAdater = AdViewPagerAdapter(topViews)
        viewPager.adapter = viewPagerAdater
        viewPager.addOnPageChangeListener(mPageChangeListener)
        i("topBanner")
        homeViewModel.rollNext.observe(this, Observer {
            if (homeViewModel.rollNext.value == true) {
                var currPagerItem = viewPager.currentItem
                if (currPagerItem == viewPagerAdater.count - 1) {
                    currPagerItem = 0
                } else {
                    currPagerItem++
                }
                viewPager.currentItem = currPagerItem
            }
        })
    }

    /**
     * asset:  file:///android_asset/
     */
    private fun initBannerViews() {
        i(">>> home fragment initViews")
        /* banner图片 */
        val options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC)

        /* 小圆点尺寸 */
        val width = dp2px(8f)
        val indParams = LinearLayout.LayoutParams(width, width)
        indParams.setMargins(dp2px(12f), 0, 0, width)/* 左 上 右 下 */

        val size = mainViewModel.topBanners.value!!.size
        var topBinding: HomeTopImageBinding
        for (i in 0 until size) {
            topBinding = HomeTopImageBinding.inflate(layoutInflater)
            topBinding.model = mainViewModel
            topBinding.listener = pageListener
            topBinding.pos = i
            Glide.with(mContext).applyDefaultRequestOptions(options)
                .load(Uri.parse(mainViewModel.topBanners.value!![i].getImage()))
                .into(topBinding.imageView)

            /*  第一张图加倒计时 */
            if (i == 0) {
                val diff = getShowCountDown()
                if (diff > 0) {
                    topBinding.tvCdd.text = diff.toString()
                    topBinding.rvCountdown.visibility = View.VISIBLE
                }
            }
            topViews.add(topBinding.root)

            /*  添加小圆点 */
            if (size > 1) {
                val ivPoint = ImageView(mContext)
                ivPoint.setBackgroundResource(if (i == 0) R.drawable.dot_focused else R.drawable.dot_normal)
                ivPoint.layoutParams = indParams
                vpindicator.addView(ivPoint)
            }
        }
    }

    private val mPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            val len = vpindicator.childCount
            for (i in 0 until len)
                vpindicator.getChildAt(i).setBackgroundResource(R.drawable.dot_normal)
            vpindicator.getChildAt(position)?.setBackgroundResource(R.drawable.dot_focused)
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }

    private fun gridMenu() {
        if (isTablet()) {
            val layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerView.layoutManager = layoutManager
        } else {
            val rvHeight = mainViewModel.rvMenuHeight.value!!
            val param = ConstraintLayout.LayoutParams(getScreenWidth(), rvHeight)
            param.topToBottom = R.id.viewPager
            param.bottomToTop = R.id.iv_ad
//            val h2 =
//                ((getScreenWidth() * MAIN_MENU_HEIGHT) / MAIN_MENU_WIDTH / 3) * 2   // 按照小menu的宽，按比例得到 小menu的高。乘2就是menu总高
//            if (rvHeight > h2) {
//                param.topMargin = (rvHeight - h2) / 2
//                param.bottomMargin = (rvHeight - h2) / 2
//                param.height = h2
//            }
            recyclerView.layoutParams = param
        }
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.bottom = dp2px(1.5f)
                outRect.left = dp2px(1.5f)
            }
        })
        mainViewModel.mainIcons.observe(this, Observer {
            i("mainIcons.observe - set adapter")
            adapter = MenuAdapter(mainViewModel.mainIcons.value!!, MenuAdapter.OnClickListener { mainIcon ->
                processMenuIntent(mainIcon)
            })
            recyclerView.adapter = adapter
        })

//        mainViewModel.language.observe(this, Observer {
//            i("language: menu notifyDataSetChanged")
////            adapter?.notifyDataSetChanged()
//        })


    }

    private fun bottomAd() {
//        val adHeight = getScreenWidth() * IMG_HEIGHT / IMG_WIDTH
//        i("adHeight=$adHeight")
        val params = ConstraintLayout.LayoutParams(getScreenWidth(), mainViewModel.d1Height.value!!)
        if (isTablet()) {
            params.topToBottom = R.id.viewPager
        } else {
            params.topToBottom = R.id.rv_menu
            if (mainViewModel.dlMargin > 0) {
                params.topMargin = mainViewModel.dlMargin / 2
            }
        }
        ivAd.layoutParams = params
        Glide.with(mContext).load("file:///android_asset/main/d2.jpg").into(ivAd)
    }

    override fun onStart() {
        super.onStart()
        i("onStart")
        homeViewModel.startTimer()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        i("onActivityCreated")
    }

    override fun onResume() {
        super.onResume()
        i("onResume")
    }

    override fun onStop() {
        super.onStop()
        i("onStop")

        homeViewModel.stopTimer()

    }

    override fun onDestroy() {
        super.onDestroy()
        i("onDestroy")
        job.cancel()
    }

    inner class PageClickListener(val listener: (position: Int) -> Unit) {
        fun onClick(pos: Int) = listener(pos)
    }
}




