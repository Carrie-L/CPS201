package com.adsale.chinaplas.ui.events


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.adsale.chinaplas.R
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.EventRepository
import com.adsale.chinaplas.databinding.FragmentEventSeminarBinding
import com.adsale.chinaplas.databinding.FragmentMyChinaplasLoginBinding
import com.adsale.chinaplas.ui.tools.mychinaplas.MyChinaplasLoginFragment
import com.adsale.chinaplas.viewmodels.EventViewModel
import com.adsale.chinaplas.viewmodels.EventViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class EventSeminarFragment : BaseFragment() {
    //    private lateinit var viewPager: ViewPager
    private lateinit var eventViewModel: EventViewModel
    private lateinit var contentFrame: FrameLayout
    private lateinit var eventFragment: ConcurrentEventFragment
    private lateinit var seminarFragment: SeminarFragment

    override fun initedView(inflater: LayoutInflater) {
        val binding = FragmentEventSeminarBinding.inflate(inflater, baseFrame, true)
//        viewPager = binding.viewPagerEvent
        contentFrame = binding.eventFrameContent

        val eventRepository =
            EventRepository.getInstance(CpsDatabase.getInstance(requireContext()).eventDao())
        eventViewModel = ViewModelProviders.of(this, EventViewModelFactory(eventRepository))
            .get(EventViewModel::class.java)

        binding.viewModel = eventViewModel
        binding.lifecycleOwner = this
    }

    override fun initView() {

    }

    private fun initContentFrame() {
        eventFragment = ConcurrentEventFragment.getInstance()
        seminarFragment = SeminarFragment.getInstance()

        eventViewModel.tabClickIndex.observe(this, Observer {
            when (it) {
                1 -> {
                    val fm =  requireActivity().supportFragmentManager
                    val tr = fm.beginTransaction()
                    tr.add(R.id.event_frame_content,eventFragment)
                    tr.commit()
//                    requireActivity().supportFragmentManager.inTransaction {
//                     add(R.id.event_frame_content,fragmentManager)
////                        show(eventFragment)
//                    }
                }
                2 -> {
                    val fm =  requireActivity().supportFragmentManager
                    val tr = fm.beginTransaction()
                    tr.add(R.id.event_frame_content,seminarFragment)
                    tr.commit()
//                    requireActivity().supportFragmentManager.inTransaction {
//                        add(seminarFragment, "seminar")
////                        show(seminarFragment)
//                    }
                }
            }
        })

    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    private fun initViewPager() {
        val fragments = mutableListOf<Fragment>()
        fragments.add(ConcurrentEventFragment.getInstance())
        fragments.add(SeminarFragment.getInstance())

//        viewPager.offscreenPageLimit = 1
//        val adapter = EventSeminarAdapter(childFragmentManager, fragments)
//        viewPager.adapter = adapter
//
//        eventViewModel.tabClickIndex.observe(this, Observer {
//            if (it == 1) {
//                viewPager.currentItem = 0
//            } else if (it == 2) {
//                viewPager.currentItem = 1
//            }
//        })
    }

    override fun initedData() {
        initContentFrame()

    }

    override fun initData() {

    }

    override fun back() {

    }

    inner class EventSeminarAdapter constructor(
        fm: FragmentManager,
        private val fragments: List<Fragment>
    ) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return 2
        }
    }


}
