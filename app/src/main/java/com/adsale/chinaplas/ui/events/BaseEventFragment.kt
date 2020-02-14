package com.adsale.chinaplas.ui.events


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.data.dao.ConcurrentEvent
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.EventRepository
import com.adsale.chinaplas.viewmodels.EventViewModel
import com.adsale.chinaplas.viewmodels.EventViewModelFactory
import com.baidu.speech.utils.LogUtil

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseEventFragment : BaseFragment() {
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var eventViewModel: EventViewModel
    protected lateinit var adapter: EventAdapter
    protected var layoutId: Int = R.layout.item_event_part
    protected lateinit var itemClickListener:OnItemClickListener

    override fun initedView(inflater: LayoutInflater) {
        val view = inflater.inflate(R.layout.layout_recycler_view, baseFrame, true)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
    }

    override fun initView() {
    }

    override fun initedData() {
        val eventRepository =
            EventRepository.getInstance(CpsDatabase.getInstance(requireContext()).eventDao())
        eventViewModel = ViewModelProviders.of(this, EventViewModelFactory(eventRepository))
            .get(EventViewModel::class.java)
        adapter = EventAdapter(listOf(), itemClickListener)
        recyclerView.adapter = adapter
    }

    override fun initData() {
        eventViewModel.events.observe(this, Observer {
            adapter.setList(it)
        })
    }

    override fun back() {
    }

//    private val itemClickListener = OnItemClickListener { entity, pos ->
//        LogUtil.i("item: $pos")
////        findNavController().navigate(Concurr.actionBaseEventFragmentToWebContentFragment("PlasticsRecycling",getString(R.string.title_concurrent_event)))
//    }

    inner class EventAdapter(
        private val list: List<ConcurrentEvent>,
        itemClickListener: OnItemClickListener
    ) : CpsBaseAdapter<ConcurrentEvent>(list, itemClickListener) {
        override fun getLayoutIdForPosition(position: Int): Int {
            return layoutId
        }
    }


}
