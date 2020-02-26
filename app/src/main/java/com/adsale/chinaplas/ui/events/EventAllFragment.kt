package com.adsale.chinaplas.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.ConcurrentEvent
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.EventRepository
import com.adsale.chinaplas.helper.ADHelper
import com.adsale.chinaplas.helper.D5_GENERATION
import com.adsale.chinaplas.helper.D5_MYCHINAPLAS
import com.adsale.chinaplas.utils.getScreenWidth
import com.adsale.chinaplas.utils.setItemEventID
import com.adsale.chinaplas.viewmodels.EventViewModel
import com.adsale.chinaplas.viewmodels.EventViewModelFactory
import com.baidu.speech.utils.LogUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/**
 * Created by Carrie on 2020/2/10.
 */
class EventAllFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventViewModel: EventViewModel
    private var adapter: OverallAdapter? = null
    private lateinit var ivD5: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_overall, container, false)
        recyclerView = view.findViewById(R.id.rv_event_overall)
        ivD5 = view.findViewById(R.id.iv_d5)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
        showD5()
    }

//    override fun initedData() {
//        LogUtil.i("initedData---")
//        layoutId = R.layout.item_event_overall
//
//
////        itemClickListener = OnItemClickListener { entity, pos ->
////            NavHostFragment.findNavController(this).navigate(
////                EventAllFragmentDirections.actionEventAllFragmentToWebContentFragment(
////                    "PlasticsRecycling",
////                    getString(R.string.title_concurrent_event)
////                )
////            )
////        }
//        super.initedData()
//        eventViewModel.getOverallEvents()
//    }

    fun initData() {
        val eventRepository =
            EventRepository.getInstance(
                CpsDatabase.getInstance(requireContext()).eventDao(),
                CpsDatabase.getInstance(requireContext()).eventApplicationDao()
            )
        eventViewModel = ViewModelProviders.of(this, EventViewModelFactory(eventRepository))
            .get(EventViewModel::class.java)
        adapter = OverallAdapter(listOf(), itemClickListener)
        recyclerView.adapter = adapter
        eventViewModel.getOverallEvents()

        eventViewModel.events.observe(this, Observer {
            adapter!!.setList(it)
        })
        LogUtil.i("EventAllFragment:   initedView")
    }

    private val itemClickListener = OnItemClickListener { entity, pos ->

    }


    companion object {
        @Volatile
        private var instance: EventAllFragment? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: EventAllFragment().also { instance = it }
            }
    }

    private fun showD5() {
        val adHelper = ADHelper.getInstance(requireActivity().application)
        val property = adHelper.d5Property(D5_MYCHINAPLAS)
        if (property.pageID.isEmpty() || !adHelper.isD5Open()) {
            ivD5.visibility = View.GONE
            return
        }
        val params = ConstraintLayout.LayoutParams(getScreenWidth(), adHelper.getADHeight())
        params.bottomToBottom = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
        ivD5.layoutParams = params

        val options = RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE)
        Glide.with(this).load(adHelper.d5ImageUrl(D5_GENERATION)).apply(options).into(ivD5)

        ivD5.setOnClickListener {
            when (property.function) {
                1 -> findNavController().navigate(
                    EventSeminarFragmentDirections.actionToExhibitorDetailFragment(
                        property.pageID
                    )
                )
                2 -> { // 同期活动
                    setItemEventID(property.pageID)
                    findNavController().navigate(R.id.eventDetailFragment)
                }
            }
        }
    }

    inner class OverallAdapter(
        list: List<ConcurrentEvent>,
        itemClickListener: OnItemClickListener
    ) : CpsBaseAdapter<ConcurrentEvent>(list, itemClickListener) {
        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.item_event_overall
        }
    }


}