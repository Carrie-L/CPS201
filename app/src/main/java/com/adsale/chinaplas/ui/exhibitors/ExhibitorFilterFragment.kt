package com.adsale.chinaplas.ui.exhibitors


import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.data.entity.ExhibitorFilter
import com.adsale.chinaplas.data.entity.KV
import com.adsale.chinaplas.databinding.FragmentExhibitorFilterBinding
import com.adsale.chinaplas.helper.ADHelper
import com.adsale.chinaplas.ui.view.FilterView
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.viewmodels.FilterViewModel
import com.adsale.chinaplas.viewmodels.FilterViewModelFactory
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/**
 *
 */
class ExhibitorFilterFragment : Fragment() {
    private lateinit var filterViewModel: FilterViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: FragmentExhibitorFilterBinding
    private lateinit var scrollView: ScrollView
    private var isViewInited = false // 如果已经初始化数据，则不再进行初始化
    private var lastView: View? = null // 记录上次创建的view

    private lateinit var applicationView: FilterView
    private lateinit var industryView: FilterView
    private lateinit var regionView: FilterView
    private lateinit var zoneView: FilterView
    private lateinit var hallView: FilterView

    private lateinit var ivD3: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (lastView == null) {
            binding = FragmentExhibitorFilterBinding.inflate(inflater)
            scrollView = binding.scrollView
            lastView = binding.root
        }
        return lastView
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.isChangeRightIcon.value = false

        showD3()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!isViewInited) {
            mainViewModel = ViewModelProviders.of(
                requireActivity(), MainViewModelFactory(
                    requireActivity().application,
                    MainIconRepository.getInstance(CpsDatabase.getInstance(requireContext()).mainIconDao())
                )
            ).get(MainViewModel::class.java)
            val database = CpsDatabase.getInstance(requireContext())
            filterViewModel = ViewModelProviders.of(
                requireActivity(), FilterViewModelFactory(
                    database.applicationDao(),
                    database.industryDao(),
                    database.regionDao(),
                    database.hallDao(),
                    database.zoneDao()
                )
            )
                .get(FilterViewModel::class.java)
            binding.viewModel = filterViewModel
            binding.executePendingBindings()

            initFilterView()
            itemClick()
            isViewInited = true

        }
        observeFilters()

        filterViewModel.filterStartNavigate.observe(this, Observer { navigate ->
            if (navigate) {
                navigateFilter()
                filterViewModel.finishFilterNavigate()
            }
        })
        filterViewModel.clearClicked.observe(this, Observer { clear ->
            if (clear) {
                clearAll()
            }
        })
    }

    /**
     * add 所有筛选条件，跳转到展商列表，进行筛选
     */
    private fun navigateFilter() {
        // 关键字
        val keyword = binding.etFilterKeyword.text.toString()
        if (!TextUtils.isEmpty(keyword.trim())) {
            val keywordFilter = ExhibitorFilter(FILTER_INDEX_KEYWORD, "Keyword", keyword)
            filterViewModel.allFilters.add(keywordFilter)
        }

        // 新技术产品
        if (binding.cbNewTech.isChecked) {
            val filter = ExhibitorFilter(FILTER_INDEX_NEW_TECH, "NewTech", "NewTech")
            filterViewModel.allFilters.add(filter)
        }

        findNavController().navigate(
            ExhibitorFilterFragmentDirections.actionMenuExhibitorsToExhibitorListFragment(
                EXHIBITOR_FILTER, filterViewModel.filterSql()
            )
        )

        clearAll()
    }

    private fun hideInput() {
        val inputMethodManager: InputMethodManager =
            context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            binding.root.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
//        scrollView.scrollTo(0, getScreenHeight())
    }

    private fun clearAll() {
        binding.etFilterKeyword.text.clear()
        applicationView.setList(listOf())
        industryView.setList(listOf())
        regionView.setList(listOf())
        zoneView.setList(listOf())
        hallView.setList(listOf())
        binding.cbNewTech.isChecked = false
        filterViewModel.finishClear()
    }

    private fun initFilterView() {
        val appKV = KV(FILTER_INDEX_APPLICATION, getString(R.string.filter_application))
        val industryKV = KV(FILTER_INDEX_INDUSTRY, getString(R.string.filter_industry))
        val regionKV = KV(FILTER_INDEX_REGION, getString(R.string.filter_country))
        val zoneKV = KV(FILTER_INDEX_ZONE, getString(R.string.filter_booth))
        val hallKV = KV(FILTER_INDEX_HALL, getString(R.string.filter_hall))

        applicationView = binding.filterViewApplication
        applicationView.setData(filterViewModel, appKV)
        industryView = binding.filterViewIndustry
        industryView.setData(filterViewModel, industryKV)
        regionView = binding.filterViewRegion
        regionView.setData(filterViewModel, regionKV)
        zoneView = binding.filterViewZone
        zoneView.setData(filterViewModel, zoneKV)
        hallView = binding.filterViewHall
        hallView.setData(filterViewModel, hallKV)
    }

    private fun observeFilters() {
        filterViewModel.appFilters.observe(this, Observer {
            LogUtil.i("observe-appFilters=${it.size}")
            applicationView.setList(it)
        })
        filterViewModel.industryFilters.observe(this, Observer {
            LogUtil.i("observe-industryFilters=${it.size}")
            industryView.setList(it)
        })
        filterViewModel.regionFilters.observe(this, Observer {
            LogUtil.i("observe-regionFilters=${it.size}")
            regionView.setList(it)
        })
        filterViewModel.hallFilters.observe(this, Observer {
            LogUtil.i("observe-hallFilters=${it.size}")
            hallView.setList(it)
        })
        filterViewModel.zoneFilters.observe(this, Observer {
            LogUtil.i("observe-zoneFilters=${it.size}")
            zoneView.setList(it)
        })
    }

    private fun itemClick() {
        val navController = NavHostFragment.findNavController(this)
        binding.tvFilterAll.setOnClickListener {
            hideInput()
            navController.navigate(
                ExhibitorFilterFragmentDirections.actionMenuExhibitorsToExhibitorListFragment(
                    EXHIBITOR_ALL, null
                )
            )
        }
        binding.filterViewApplication.setOnClickListener {
            hideInput()
            navController.navigate(
                ExhibitorFilterFragmentDirections.actionMenuExhibitorsToFilterApplicationFragment(
                    APPLICATION_EXHIBITOR
                )
            )
        }
        binding.filterViewIndustry.setOnClickListener {
            hideInput()
            navController.navigate(R.id.filterIndustryFragment)
        }
        binding.filterViewRegion.setOnClickListener {
            hideInput()
            navController.navigate(R.id.filterRegionFragment)
        }
        binding.filterViewZone.setOnClickListener {
            hideInput()
            navController.navigate(R.id.filterZoneFragment)
        }
        binding.filterViewHall.setOnClickListener {
            hideInput()
            navController.navigate(R.id.filterHallFragment)
        }
    }

    private fun showD3() {
        d3Height()
        generateD3()
    }

    private fun d3Height() {
        ivD3 = binding.ivD3
        val adHeight = getScreenWidth() * IMG_HEIGHT / IMG_WIDTH
        val params = ConstraintLayout.LayoutParams(getScreenWidth(), adHeight)
        params.bottomToBottom = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        ivD3.layoutParams = params
    }

    private fun generateD3() {
        val adHelper = ADHelper.getInstance(requireActivity().application)
        val d3 = adHelper.getCurrentD3()

        val options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)
        Glide.with(this).load("${adHelper.baseUrl}${d3.imageName()}").apply(options).into(ivD3)
        LogUtil.i("d3 url = ${adHelper.baseUrl}${d3.imageName()}")

        ivD3.setOnClickListener {
            findNavController().navigate(ExhibitorFilterFragmentDirections.actionToExhibitorDetailFragment(d3.pageID))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.i("onDestroy~~~")
        clearAll()
        filterViewModel.onClear()
    }

}
