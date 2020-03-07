package com.adsale.chinaplas.ui.tools.schedule


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.adapters.ScheduleAdapter
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.ScheduleInfo
import com.adsale.chinaplas.data.dao.ScheduleRepository
import com.adsale.chinaplas.databinding.FragmentScheduleBinding
import com.adsale.chinaplas.utils.DATE_1
import com.adsale.chinaplas.utils.DATE_2
import com.adsale.chinaplas.utils.DATE_3
import com.adsale.chinaplas.utils.DATE_4
import com.baidu.speech.utils.LogUtil

/**
 * A simple [Fragment] subclass.
 */
class ScheduleFragment : Fragment() {
    private lateinit var binding: FragmentScheduleBinding
    private lateinit var rvPager: RecyclerView
    private lateinit var scheduleViewModel: ScheduleViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentScheduleBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        scheduleViewModel = ViewModelProviders.of(this,
            ScheduleViewModelFactory(ScheduleRepository.getInstance(CpsDatabase.getInstance(requireContext()).scheduleDao())))
            .get(ScheduleViewModel::class.java)
        binding.model = scheduleViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()


        initPager()

    }

    private fun initPager() {
        rvPager = binding.rvSchedulePager
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rvPager.layoutManager = layoutManager
        rvPager.setHasFixedSize(true)

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rvPager)

        val adapter = ScheduleAdapter(listOf(), OnItemClickListener { entity, pos ->
            findNavController().navigate(ScheduleFragmentDirections.actionToScheduleEditFragment(false,
                entity as ScheduleInfo))
        })
        rvPager.adapter = adapter

        val list: MutableList<List<ScheduleInfo>> = mutableListOf()
        val itemList1: MutableList<ScheduleInfo> = mutableListOf()
        val itemList2: MutableList<ScheduleInfo> = mutableListOf()
        val itemList3: MutableList<ScheduleInfo> = mutableListOf()
        val itemList4: MutableList<ScheduleInfo> = mutableListOf()

        scheduleViewModel.schedules.observe(this, Observer {
            LogUtil.i("schedules = ${it.size},, ${it.toString()}")

            if (it.isNotEmpty()) {
                list.clear()
                itemList1.clear()
                itemList2.clear()
                itemList3.clear()
                itemList4.clear()

                for ((i, entity) in it.withIndex()) {
                    when {
                        entity.startDate!!.contains("$DATE_1") -> {
                            LogUtil.i(" contains $DATE_1")
                            itemList1.add(entity)
                        }
                        entity.startDate!!.contains("$DATE_2") -> {
                            LogUtil.i(" contains $DATE_2")
                            itemList2.add(entity)
                        }
                        entity.startDate!!.contains("$DATE_3") -> {
                            LogUtil.i(" contains $DATE_3")
                            itemList3.add(entity)
                        }
                        else -> {
                            LogUtil.i(" contains $DATE_4")
                            itemList4.add(entity)
                        }
                    }
                }
                list.add(0, itemList1)
                list.add(1, itemList2)
                list.add(2, itemList3)
                list.add(3, itemList4)
                LogUtil.i("list = ${list.size},, ${list.toString()}")
            } else {
                list.add(0, listOf())
                list.add(1, listOf())
                list.add(2, listOf())
                list.add(3, listOf())
            }
            adapter.setList(list)
        })


        rvPager.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            private var state = 0

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (state == SCROLL_STATE_DRAGGING) {
                    val position = snapHelper.findTargetSnapPosition(layoutManager, dx, dy)
                    LogUtil.i("~~~~~~~~~~state==1, pos=$position")
                    scheduleViewModel.barClick.value = position
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                state = newState
//                val currentPos = layoutManager.getPosition(snapHelper.findSnapView(layoutManager)!!)
//                //  滑动停止时，获取当前position
//                if (newState == SCROLL_STATE_IDLE) {
////                    scheduleViewModel.barClick.value = currentPos
//                    LogUtil.i("onScrollStateChanged currentPos= $currentPos")
//                }
//                LogUtil.i("onScrollStateChanged currentPos= $currentPos")
            }
        })

        scheduleViewModel.barClick.observe(this, Observer {
            rvPager.smoothScrollToPosition(it)
        })


        LogUtil.i("======  initPager() =======")

    }

    override fun onResume() {
        super.onResume()
        LogUtil.i("======  onResume() =======${scheduleViewModel.barClick.value}")
        rvPager.scrollToPosition(scheduleViewModel.barClick.value!!)
    }

}
