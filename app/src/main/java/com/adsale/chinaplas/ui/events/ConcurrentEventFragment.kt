package com.adsale.chinaplas.ui.events

import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.EventRepository
import com.adsale.chinaplas.databinding.FragmentConcurrentEventBinding
import com.adsale.chinaplas.ui.tools.mychinaplas.MyChinaplasLoginFragment
import com.adsale.chinaplas.viewmodels.EventViewModel
import com.adsale.chinaplas.viewmodels.EventViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class ConcurrentEventFragment : BaseFragment() {
    private lateinit var binding: FragmentConcurrentEventBinding
    private lateinit var eventViewModel: EventViewModel
    private lateinit var viewPager: ViewPager

    override fun initedView(inflater: LayoutInflater) {
        binding = FragmentConcurrentEventBinding.inflate(inflater, baseFrame, true)
        viewPager = binding.eventViewPager
    }

    override fun initView() {

    }

    override fun initedData() {
        val eventRepository =
            EventRepository.getInstance(CpsDatabase.getInstance(requireContext()).eventDao())
        eventViewModel = ViewModelProviders.of(this, EventViewModelFactory(eventRepository))
            .get(EventViewModel::class.java)
        binding.model = eventViewModel
        binding.lifecycleOwner = this

        val fragments = arrayListOf<Fragment>()
        val overallFragment = EventAllFragment.getInstance()
        val oneFragment = EventOneFragment.getInstance()
        val twoFragment = EventTwoFragment.getInstance()
        val threeFragment = EventThreeFragment.getInstance()
        val fourFragment = EventFourFragment.getInstance()
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

    override fun initData() {
        eventViewModel.barClick.observe(this, Observer {
            viewPager.currentItem = it
        })
    }

    override fun back() {
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


}
