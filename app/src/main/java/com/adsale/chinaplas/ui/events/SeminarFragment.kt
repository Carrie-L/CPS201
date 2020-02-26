package com.adsale.chinaplas.ui.events


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.SeminarRepository
import com.adsale.chinaplas.databinding.FragmentSeminarBinding
import com.adsale.chinaplas.viewmodels.SeminarViewModel
import com.adsale.chinaplas.viewmodels.SeminarViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class SeminarFragment : Fragment() {
    private lateinit var viewPager: ViewPager
    private lateinit var seminarViewModel: SeminarViewModel
    private lateinit var binding: FragmentSeminarBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSeminarBinding.inflate(inflater)
        viewPager = binding.seminarViewPager
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
        initViewPager()

        seminarViewModel.btnClick.observe(this, Observer {
            if (it in 1..3) {
                viewPager.currentItem = it - 1
            } else {

            }
        })
    }

    fun initData() {
        seminarViewModel = ViewModelProviders.of(this, SeminarViewModelFactory(
            SeminarRepository.getInstance(CpsDatabase.getInstance(requireContext()).seminarDao())
        )).get(SeminarViewModel::class.java)
        binding.model = seminarViewModel
        binding.lifecycleOwner = this
    }

    private fun initViewPager() {
        val fragments = arrayListOf<Fragment>()
        fragments.add(SeminarOneFragment())
        fragments.add(SeminarTwoFragment())
        fragments.add(SeminarThreeFragment())

        viewPager.offscreenPageLimit = 1
        val adapter = ConcurrentEventFragment.EventPagerAdapter(childFragmentManager, fragments)
        viewPager.adapter = adapter
        viewPager.currentItem = 0

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                seminarViewModel.btnClick.value = position + 1
            }
        })
    }


    companion object {
        @Volatile
        private var instance: SeminarFragment? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SeminarFragment().also { instance = it }
            }
    }


}
