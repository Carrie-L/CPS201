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
import com.adsale.chinaplas.utils.getLangCode
import com.adsale.chinaplas.utils.getSPEventFilter
import com.adsale.chinaplas.viewmodels.EventViewModel
import com.adsale.chinaplas.viewmodels.EventViewModelFactory
import com.baidu.speech.utils.LogUtil

/**
 * A simple [Fragment] subclass.
 */
class ConcurrentEventFragment : Fragment() {
    private lateinit var binding: FragmentConcurrentEventBinding
    private lateinit var eventViewModel: EventViewModel
    private lateinit var viewPager: ViewPager
    private lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConcurrentEventBinding.inflate(inflater, container, false)
        viewPager = binding.eventViewPager
        LogUtil.i("onCreateView")
        mView = binding.root
        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogUtil.i("onActivityCreated")
        initedData()

        eventViewModel.barClick.observe(this, Observer {
            viewPager.currentItem = it
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

        viewPager.offscreenPageLimit = 1
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
                eventViewModel.barClick.value = position
            }
        })
    }

    private fun onFilter() {
        binding.ivFilter.setOnClickListener {
            val action = EventSeminarFragmentDirections.actionEventSeminarFragmentToFilterApplicationFragment(
                APPLICATION_EVENT)
            Navigation.findNavController(mView).navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        LogUtil.i("onResume")

        getLangCode()
        val eventFilters = getSPEventFilter()
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

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.i("onDestroy()===================")
    }


}
