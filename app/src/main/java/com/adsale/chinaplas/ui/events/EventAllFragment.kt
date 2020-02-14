package com.adsale.chinaplas.ui.events

import android.view.LayoutInflater
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.data.dao.ConcurrentEvent
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.EventDao
import com.adsale.chinaplas.data.dao.EventRepository
import com.adsale.chinaplas.viewmodels.EventViewModel
import com.adsale.chinaplas.viewmodels.EventViewModelFactory
import com.baidu.speech.utils.LogUtil
import kotlinx.coroutines.Deferred
import org.jetbrains.annotations.NotNull

/**
 * Created by Carrie on 2020/2/10.
 */
class EventAllFragment : BaseEventFragment() {
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var eventViewModel: EventViewModel
//    private var adapter: OverallAdapter? = null
//
//    override fun initedView(inflater: LayoutInflater) {
//        val view = inflater.inflate(R.layout.layout_recycler_view, baseFrame, true)
//        recyclerView = view.findViewById(R.id.recycler_view)
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        recyclerView.setHasFixedSize(true)
//        LogUtil.i("EventAllFragment:   initedView")
//    }

    override fun initView() {
    }

    override fun initedData() {
        layoutId = R.layout.item_event_overall
        itemClickListener = OnItemClickListener { entity, pos ->
            NavHostFragment.findNavController(this).navigate(
                EventAllFragmentDirections.actionEventAllFragmentToWebContentFragment(
                    "PlasticsRecycling",
                    getString(R.string.title_concurrent_event)
                )
            )
        }
        super.initedData()
        eventViewModel.getOverallEvents()
    }

//    override fun initedData() {
//        val eventRepository =
//            EventRepository.getInstance(CpsDatabase.getInstance(requireContext()).eventDao())
//        eventViewModel = ViewModelProviders.of(this, EventViewModelFactory(eventRepository))
//            .get(EventViewModel::class.java)
//        adapter = OverallAdapter(listOf(), itemClickListener)
//        recyclerView.adapter = adapter
//        eventViewModel.getOverallEvents()
//    }

    override fun initData() {
//        eventViewModel.events.observe(this, Observer {
//            adapter!!.setList(it)
//        })
//        LogUtil.i("EventAllFragment:   initedView")
    }


    override fun back() {
    }

    companion object {
        @Volatile
        private var instance: EventAllFragment? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: EventAllFragment().also { instance = it }
            }
    }

    inner class OverallAdapter(
        private val list: List<ConcurrentEvent>,
        itemClickListener: OnItemClickListener
    ) : CpsBaseAdapter<ConcurrentEvent>(list, itemClickListener) {
        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.item_event_overall
        }
    }


}