package com.adsale.chinaplas.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.ui.view.RecyclerViewScrollTo
import com.adsale.chinaplas.ui.view.SideBarView
import com.adsale.chinaplas.utils.BACK_DEFAULT
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.viewmodels.FilterViewModel
import com.adsale.chinaplas.viewmodels.FilterViewModelFactory
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory

/**
 * Created by Carrie on 2020/1/9.
 */
abstract class BaseFilterFragment : Fragment() {
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var viewModel: FilterViewModel
    protected lateinit var mainViewModel: MainViewModel
    protected lateinit var sideBarView: SideBarView
    protected lateinit var rvScrollTo: RecyclerViewScrollTo
    private lateinit var layoutManager: LinearLayoutManager
    protected var showSideBar = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_recycler_view2, container, false)
        sideBarView = view.findViewById(R.id.filter_side_bar)
        recyclerView = view.findViewById(R.id.recycler_view)
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayout.VERTICAL)
        )
        LogUtil.i("this BaseFilterFragment=${this}")
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainViewModel = ViewModelProviders.of(
            requireActivity(), MainViewModelFactory(
                requireActivity().application,
                MainIconRepository.getInstance(CpsDatabase.getInstance(requireContext()).mainIconDao())
            )
        ).get(MainViewModel::class.java)
        mainViewModel.isChangeRightIcon.value = true
        mainViewModel.backRoad.value = BACK_DEFAULT

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

        initData()

        if (showSideBar) {
            sideBarView.visibility = View.VISIBLE
            rvScrollTo = RecyclerViewScrollTo(layoutManager,recyclerView)
        }
    }

    protected abstract fun initData()

    protected fun initSideBar(type:Int) {
        rvScrollTo = RecyclerViewScrollTo(layoutManager, recyclerView)
        sideBarView.setAdapter(type,rvScrollTo)
    }




}