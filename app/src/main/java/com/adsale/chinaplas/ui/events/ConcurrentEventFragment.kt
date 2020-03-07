package com.adsale.chinaplas.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.EventRepository
import com.adsale.chinaplas.databinding.FragmentConcurrentEventBinding
import com.adsale.chinaplas.ui.exhibitors.APPLICATION_EVENT
import com.adsale.chinaplas.ui.view.HELP_EVENT_LIST
import com.adsale.chinaplas.ui.view.HelpDialog
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.viewmodels.EventViewModel
import com.adsale.chinaplas.viewmodels.EventViewModelFactory
import com.baidu.speech.utils.LogUtil

/**
 * A simple [Fragment] subclass.
 */
class ConcurrentEventFragment : Fragment() {
    private lateinit var binding: FragmentConcurrentEventBinding
    private lateinit var eventViewModel: EventViewModel
    //    private lateinit var recyclerView: RecyclerView
    private lateinit var viewPager: ViewPager
    private lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConcurrentEventBinding.inflate(inflater, container, false)
        viewPager = binding.eventViewPager
//        recyclerView = binding.eventTabRecyclerView
//        val layoutManager = LinearLayoutManager(requireContext())
//        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
//        recyclerView.layoutManager = layoutManager
//        recyclerView.setHasFixedSize(true)
//        val snapHelper = PagerSnapHelper()
//        snapHelper.attachToRecyclerView(recyclerView)
        LogUtil.i("onCreateView")
        mView = binding.root
        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogUtil.i("onActivityCreated")
        initedData()

        eventViewModel.barClick.observe(viewLifecycleOwner, Observer {
            viewPager.currentItem = it
//            recyclerView.smoothScrollToPosition(it)
        })

        onFilter()
    }

    fun initedData() {
        val eventRepository =
            EventRepository.getInstance(CpsDatabase.getInstance(requireContext()).eventDao(),
                CpsDatabase.getInstance(requireContext()).eventApplicationDao())
        eventViewModel = ViewModelProviders.of(this, EventViewModelFactory(eventRepository))
            .get(EventViewModel::class.java)
        binding.model = eventViewModel
        binding.lifecycleOwner = this

        val fragments = arrayListOf<Fragment>()
        val overallFragment = EventAllFragment()
        val oneFragment = EventOneFragment()
        val twoFragment = EventTwoFragment()
        val threeFragment = EventThreeFragment()
        val fourFragment = EventFourFragment()
        fragments.add(overallFragment)
        fragments.add(oneFragment)
        fragments.add(twoFragment)
        fragments.add(threeFragment)
        fragments.add(fourFragment)

        val adapter = EventPagerAdapter(childFragmentManager, fragments)
        viewPager.adapter = adapter
        viewPager.currentItem = 0

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                setEventTabIndex(position)
                eventViewModel.barClick.value = position
            }
        })

        helpPage()
    }

    private fun onFilter() {
        binding.ivFilter.setOnClickListener {
            val barClickIndex = eventViewModel.barClick.value
            LogUtil.i("barClickIndex=$barClickIndex")
            setEventTabIndex(barClickIndex!!)

            val action = EventSeminarFragmentDirections.actionEventSeminarFragmentToFilterApplicationFragment(
                APPLICATION_EVENT)
            Navigation.findNavController(mView).navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        LogUtil.i("onResume")

        val barClickIndex = getEventTabIndex()
        LogUtil.i("onResume: barClickIndex=$barClickIndex")
        eventViewModel.barClick.value = barClickIndex

        getLangCode()
        val eventFilters = getSPEventFilter().replace("[", "").replace("]", "")
        if (eventFilters.isNotEmpty()) {
            LogUtil.i("eventFilters=$eventFilters")
            eventViewModel.getOverallEvents()
        }

    }

    companion object {
        @Volatile
        private var instance: ConcurrentEventFragment? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: ConcurrentEventFragment().also { instance = it }
            }
    }


//    inner class EventTabAdapter(list: List<ConcurrentEvent>) : CpsBaseAdapter<Fragment>(list, null) {
//        override fun getLayoutIdForPosition(position: Int): Int {
//            return R.layout.item_event_wrap_list
//        }
//    }

    class EventPagerAdapter constructor(
        fm: FragmentManager,
        private val fragments: List<Fragment>
    ) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }
    }

    private fun helpPage() {
        binding.ivHelper.setOnClickListener {
            val helpDialog = HelpDialog.getInstance(HELP_EVENT_LIST)
            requireActivity().supportFragmentManager.inTransaction {
                helpDialog.show(fragmentManager!!, "Help")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.i("onDestroy()===================")
    }


}
