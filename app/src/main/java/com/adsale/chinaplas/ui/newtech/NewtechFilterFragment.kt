package com.adsale.chinaplas.ui.newtech


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.data.entity.KV
import com.adsale.chinaplas.databinding.FragmentNewtechFilterBinding
import com.adsale.chinaplas.network.HIGHLIGHT_URL
import com.adsale.chinaplas.ui.exhibitors.TYPE_NEW_TECH_AREA
import com.adsale.chinaplas.ui.exhibitors.TYPE_NEW_TEC_APPLICATIONS
import com.adsale.chinaplas.ui.exhibitors.TYPE_NEW_TEC_PRODUCT
import com.adsale.chinaplas.ui.exhibitors.TYPE_NEW_TEC_THEMATIC
import com.adsale.chinaplas.ui.view.FilterView
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.viewmodels.FilterViewModel
import com.adsale.chinaplas.viewmodels.FilterViewModelFactory
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory


/**
 * 筛选页
 */
class NewtechFilterFragment : Fragment() {
    private lateinit var binding: FragmentNewtechFilterBinding
    private lateinit var viewModel: FilterViewModel

    private lateinit var productView: FilterView
    private lateinit var applicationView: FilterView
    private lateinit var themeView: FilterView
    private lateinit var newTecView: FilterView

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewtechFilterBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initFilterView()
        mainViewModel.backRoad.value = BACK_DEFAULT
        mainViewModel.backCustom.value = false
    }

    private fun initFilterView() {
        val productKV = KV(FILTER_INDEX_NEWTECH_PRO, getString(R.string.filter_product))
        val appKV = KV(FILTER_INDEX_NEWTECH_APPL, getString(R.string.filter_applications))
        val themeKV = KV(FILTER_INDEX_NEWTECH_THEME, getString(R.string.filter_topic_collection))
        val newKV = KV(FILTER_INDEX_NEWTECH_NEW, getString(R.string.filter_new_tech))

        applicationView = binding.filterViewApplication
        applicationView.setData(viewModel, appKV)
        productView = binding.filterViewProduct
        productView.setData(viewModel, productKV)
        themeView = binding.filterViewTheme
        themeView.setData(viewModel, themeKV)
        newTecView = binding.filterViewNewTech
        newTecView.setData(viewModel, newKV)

        productView.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(NewtechFilterFragmentDirections.actionNewtechFilterFragmentToFilterApplicationFragment(
                    TYPE_NEW_TEC_PRODUCT))
        }
        applicationView.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(NewtechFilterFragmentDirections.actionNewtechFilterFragmentToFilterApplicationFragment(
                    TYPE_NEW_TEC_APPLICATIONS))
        }
        themeView.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(NewtechFilterFragmentDirections.actionNewtechFilterFragmentToFilterApplicationFragment(
                    TYPE_NEW_TEC_THEMATIC))
        }
        newTecView.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(NewtechFilterFragmentDirections.actionNewtechFilterFragmentToFilterApplicationFragment(
                    TYPE_NEW_TECH_AREA))
        }
        binding.tvFilterHighlight.setOnClickListener {
            onLinkWebsite()
        }

        observeFilters()

    }

    private fun observeFilters() {
        viewModel.newApplFilters.observe(this, Observer {
            LogUtil.i("observe-appFilters=${it.size}")
            applicationView.setList(it)
        })
        viewModel.newProdFilters.observe(this, Observer {
            LogUtil.i("observe-industryFilters=${it.size}")
            productView.setList(it)
        })
        viewModel.themeFilters.observe(this, Observer {
            LogUtil.i("observe-themeFilters=${it.size}")
            themeView.setList(it)
        })
        viewModel.newFilters.observe(this, Observer {
            LogUtil.i("observe-newFilters=${it.size}")
            newTecView.setList(it)
        })

        viewModel.filterStartNavigate.observe(this, Observer { navigate ->
            if (navigate) {
                if (viewModel.allFilters.size > 0) {
                    navigateFilter()
                }
                viewModel.finishFilterNavigate()
            }
        })
        viewModel.clearClicked.observe(this, Observer { clear ->
            if (clear) {
                clearAll()
            }
        })

    }

    /**
     * add 所有筛选条件，跳转到展商列表，进行筛选
     */
    private fun navigateFilter() {
        NavHostFragment.findNavController(this).navigate(
            NewtechFilterFragmentDirections.actionNewtechFilterFragmentToNewtechListFragment(viewModel.newtechFilterSql())
        )
        viewModel.onClear()
    }

    private fun clearAll() {
        LogUtil.i("==clearAll==")
        productView.setList(listOf())
        applicationView.setList(listOf())
        themeView.setList(listOf())
        newTecView.setList(listOf())

        viewModel.finishClear()
    }

    private fun initViewModel() {
        mainViewModel = ViewModelProviders.of(
            requireActivity(), MainViewModelFactory(requireActivity().application,
                MainIconRepository.getInstance(CpsDatabase.getInstance(requireContext()).mainIconDao())
            )
        ).get(MainViewModel::class.java)

        val database = CpsDatabase.getInstance(requireContext())
        viewModel = ViewModelProviders.of(
            requireActivity(), FilterViewModelFactory(
                database.applicationDao(),
                database.industryDao(),
                database.regionDao(),
                database.hallDao(),
                database.zoneDao()
            )
        )
            .get(FilterViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
    }

    private fun onLinkWebsite() {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val url = Uri.parse(String.format(HIGHLIGHT_URL, getLangStr()))
        intent.data = url
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.i("onDestroy~~~")
        clearAll()
        viewModel.onClear()
    }


}
