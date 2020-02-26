package com.adsale.chinaplas.ui.events


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.SeminarAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.SeminarInfo
import com.adsale.chinaplas.data.dao.SeminarRepository
import com.adsale.chinaplas.databinding.FragmentBaseSeminarBinding
import com.adsale.chinaplas.ui.exhibitors.APPLICATION_SEMINAR
import com.adsale.chinaplas.utils.setItemSeminarEventID
import com.adsale.chinaplas.utils.setSPSeminarFilter
import com.adsale.chinaplas.viewmodels.*
import com.baidu.speech.utils.LogUtil

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseSeminarFragment : Fragment() {
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var seminarViewModel: SeminarViewModel
    protected lateinit var adapter: SeminarAdapter
    protected lateinit var binding: FragmentBaseSeminarBinding
    protected var CURRENT_DATE_INDEX: Int = 1  // 1:第一天，2 第二天, 3 第三天
    private lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBaseSeminarBinding.inflate(inflater)
        recyclerView = binding.seminarRecyclerViewBase
        mView = binding.root
        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        recyclerView.setHasFixedSize(true)
        adapter = SeminarAdapter(listOf(), itemClickListener)
        recyclerView.adapter = adapter
    }

    private fun initData() {
        seminarViewModel = ViewModelProviders.of(this, SeminarViewModelFactory(
            SeminarRepository.getInstance(CpsDatabase.getInstance(requireContext()).seminarDao())
        )).get(SeminarViewModel::class.java)
        binding.model = seminarViewModel
        binding.lifecycleOwner = this

        init()

        seminarViewModel.currentDateSelect.value = CURRENT_DATE_INDEX
        seminarViewModel.getSeminarTimeList(true)

        seminarViewModel.seminarList.observe(this, Observer {
            adapter.setList(it)
        })

        seminarViewModel.btnClick.observe(this, Observer {
            when (it) {
                SEMINAR_FILTER -> {
                    val currentDes = findNavController().currentDestination!!
                    if (currentDes.id == R.id.eventSeminarFragment) {
                        Navigation.findNavController(mView)
                            .navigate(EventSeminarFragmentDirections.actionEventSeminarFragmentToFilterApplicationFragment(
                                APPLICATION_SEMINAR))
                    } else {
                        LogUtil.i("!   currentDes=${currentDes.label}")
                    }
                }
                SEMINAR_MAP -> ""
                SEMINAR_RESET -> {
                    setSPSeminarFilter("")
                    seminarViewModel.getSeminarTimeList(true)
                }
            }
        })
    }

//    override fun onResume() {
//        super.onResume()
//        LogUtil.i("onResume")
//
//        val filters = getSPSeminarFilter()
//        if (filters.isNotEmpty()) {
//            LogUtil.i("getSPSeminarFilter=$filters")
//
//            seminarViewModel.getOverallEvents()
//        }
//
//    }

    abstract fun init()

    private val itemClickListener = OnItemClickListener { entity, pos ->
        entity as SeminarInfo
        setItemSeminarEventID(entity.EventID!!)
        findNavController().navigate(R.id.seminarDetailFragment)
    }


}
