package com.adsale.chinaplas.ui.events


import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adsale.chinaplas.R
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.EventRepository
import com.adsale.chinaplas.databinding.FragmentEventSeminarBinding
import com.adsale.chinaplas.utils.setSPEventFilter
import com.adsale.chinaplas.utils.setSPSeminarFilter
import com.adsale.chinaplas.viewmodels.EventViewModel
import com.adsale.chinaplas.viewmodels.EventViewModelFactory
import com.baidu.speech.utils.LogUtil

/**
 * A simple [Fragment] subclass.
 */
class EventSeminarFragment : BaseFragment() {
    //    private lateinit var viewPager: ViewPager
    private lateinit var eventViewModel: EventViewModel
    private lateinit var contentFrame: FrameLayout
    private lateinit var eventFragment: ConcurrentEventFragment
    private lateinit var seminarFragment: SeminarFragment
    private var isAddedEvent = false
    private var isAddedSeminar = false

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val binding = FragmentEventSeminarBinding.inflate(inflater)
//        contentFrame = binding.eventFrameContent
//        return binding.root
//    }

    override fun initedView(inflater: LayoutInflater) {
        val binding = FragmentEventSeminarBinding.inflate(inflater, baseFrame, true)
//        viewPager = binding.viewPagerEvent
        contentFrame = binding.eventFrameContent
        val eventRepository =
            EventRepository.getInstance(CpsDatabase.getInstance(requireContext()).eventDao(),
                CpsDatabase.getInstance(requireContext()).eventApplicationDao())
        eventViewModel = ViewModelProviders.of(this, EventViewModelFactory(eventRepository))
            .get(EventViewModel::class.java)

        binding.viewModel = eventViewModel
        binding.lifecycleOwner = this
    }

    override fun initView() {

    }

    private fun initContentFrame() {
        LogUtil.i("initContentFrame~~~")
        eventFragment = ConcurrentEventFragment()
        seminarFragment = SeminarFragment()

        val fm = fragmentManager!!

        eventViewModel.tabClickIndex.observe(this, Observer {
            LogUtil.i("tabClickIndex observe=$it")
            when (it) {
                1 -> {
                    val tr = fm.beginTransaction()
                    tr.replace(R.id.event_frame_content, eventFragment).commitAllowingStateLoss()
                }
                2 -> {
                    val tr = fm.beginTransaction()
                    tr.replace(R.id.event_frame_content, seminarFragment).commitAllowingStateLoss()
                }
            }
        })

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
    }

    override fun initData() {
        initContentFrame()
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

    override fun onDetach() {
        super.onDetach()
        isAddedEvent = false
        isAddedSeminar = false
        setSPEventFilter("")
        setSPSeminarFilter("")
        LogUtil.i("onDetach")
    }

}
