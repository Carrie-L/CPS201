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
import com.adsale.chinaplas.helper.ADHelper
import com.adsale.chinaplas.network.SEMINAR_ROOM_MAP
import com.adsale.chinaplas.ui.exhibitors.APPLICATION_SEMINAR
import com.adsale.chinaplas.utils.getSPSeminarFilter
import com.adsale.chinaplas.utils.getSeminarTimeIndex
import com.adsale.chinaplas.utils.setItemSeminarEventID
import com.adsale.chinaplas.utils.setSPSeminarFilter
import com.adsale.chinaplas.viewmodels.*
import com.baidu.speech.utils.LogUtil
import com.bumptech.glide.Glide

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseSeminarFragment : Fragment() {
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var seminarViewModel: SeminarViewModel
    protected lateinit var adapter: SeminarAdapter
    protected lateinit var binding: FragmentBaseSeminarBinding
    protected var CURRENT_DATE_INDEX: Int = 1  //     1:第一天，2 第二天, 3 第三天
    private lateinit var mView: View
    private var lastView: View? = null
    private var isInited = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (lastView == null) {
            LogUtil.i("---lastView == null---")
            binding = FragmentBaseSeminarBinding.inflate(inflater)
            recyclerView = binding.seminarRecyclerViewBase
            mView = binding.root
            lastView = mView
        }
        LogUtil.i("---lastView!= null---")
        return lastView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!isInited) {
            LogUtil.i("---not isInited---")
            initView()
            initedData()
            isInited = true
        }
        initData()
        LogUtil.i("--- isInited---")
    }

    private fun initView() {
        recyclerView.setHasFixedSize(true)
        adapter = SeminarAdapter(listOf(), itemClickListener)
        recyclerView.adapter = adapter
    }

    private fun initedData() {
        seminarViewModel = ViewModelProviders.of(this, SeminarViewModelFactory(
            SeminarRepository.getInstance(CpsDatabase.getInstance(requireContext()).seminarDao())
        )).get(SeminarViewModel::class.java)
        binding.model = seminarViewModel
        binding.lifecycleOwner = this

        init()

        seminarViewModel.currentDateSelect.value = CURRENT_DATE_INDEX
        seminarViewModel.getSeminarList()
    }

    private fun initData() {
        seminarViewModel.seminarList.observe(this, Observer {
            adapter.setList(it)
        })

        seminarViewModel.btnClick.observe(this, Observer {
            when (it) {
                SEMINAR_FILTER -> {
//                    setSPSeminarFilter("")
                    val currentDes = findNavController().currentDestination!!
                    if (currentDes.id == R.id.eventSeminarFragment) {
                        Navigation.findNavController(mView)
                            .navigate(EventSeminarFragmentDirections.actionEventSeminarFragmentToFilterApplicationFragment(
                                APPLICATION_SEMINAR))
                    } else {
                        LogUtil.i("!   currentDes=${currentDes.label}")
                    }
                }
                SEMINAR_MAP -> {
                    findNavController().navigate(EventSeminarFragmentDirections.actionToImageFragment(
                        SEMINAR_ROOM_MAP))
                }
                SEMINAR_RESET -> {
                    setSPSeminarFilter(seminarViewModel.getCurrentDate(), "")
                    seminarViewModel.resetList()
                }
            }
        })

        val adHelper = ADHelper.getInstance()
        val d8List = adHelper.d8List()
        seminarViewModel.currentIsAm.observe(this, Observer {
            binding.ivD8Bottom.visibility = View.GONE
            for (d8 in d8List) {
                if (seminarViewModel.getCurrentDate().contains(d8.date)
                    && (it) == (d8.isAm == 1)
                ) {
                    Glide.with(requireContext()).load(adHelper.baseUrl + d8.getBottomImage()).into(binding.ivD8Bottom)
                    binding.ivD8Bottom.visibility = View.VISIBLE
//                binding.ivD8Bottom.setOnClickListener {
//                    setItemSeminarEventID(d8.companyID)
//                    findNavController().navigate(R.id.seminarDetailFragment)
//                }
                    break
                }
            }
        })


    }

    override fun onResume() {
        super.onResume()

        val index = getSeminarTimeIndex(seminarViewModel.getCurrentDate())
        seminarViewModel.currentIsAm.value = index == 0
        LogUtil.i("getSeminarTimeIndex=$index")

        val filter = getSPSeminarFilter(seminarViewModel.getCurrentDate())
        if (filter.isNotEmpty()) {
            LogUtil.i(" onResume--getFilterSeminars")
            seminarViewModel.getFilterSeminars(filter)
        }

    }

    abstract fun init()

    private val itemClickListener = OnItemClickListener { entity, pos ->
        entity as SeminarInfo
        setItemSeminarEventID(entity.EventID!!)
        findNavController().navigate(R.id.seminarDetailFragment)
    }


}
