package com.adsale.chinaplas.ui.exhibitors


import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.ExhibitorDtlAdapter
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.ExhibitorRepository
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.data.dao.ScheduleInfo
import com.adsale.chinaplas.data.entity.KV
import com.adsale.chinaplas.databinding.FragmentExhibitorDetailBinding
import com.adsale.chinaplas.helper.ADHelper
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.alertDialogConfirmTwo
import com.adsale.chinaplas.utils.isMyChinaplasLogin
import com.adsale.chinaplas.viewmodels.*
import com.google.android.material.appbar.AppBarLayout
import java.util.*

/**
 * 接收[companyID]
 */
class ExhibitorDetailFragment : Fragment() {
    private lateinit var binding: FragmentExhibitorDetailBinding
    private var companyID: String = ""
    private lateinit var dtlViewModel: ExhibitorDtlViewModel
    private lateinit var exhibitorViewModel: ExhibitorViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var pager: ViewPager
    private lateinit var pagerAdapter: ExhibitorDtlAdapter
    private val fragments = mutableListOf<Fragment>()
    private val kvs = mutableListOf<KV>()  /*  K: viewpager position; V: tabIndex */
    private var posIndex = 0
    private lateinit var dtlPin: ConstraintLayout
    private lateinit var adHelper: ADHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LogUtil.i("initedView")
        adHelper = ADHelper.getInstance()
        arguments?.let {
            companyID = ExhibitorDetailFragmentArgs.fromBundle(it).companyID
            if (!TextUtils.isEmpty(companyID)) {
                val d6 = adHelper.getD6(companyID)
//                if(d6==null || !adHelper.isD6Open()){
//                    binding = FragmentExhibitorDetailBinding.inflate(inflater, container, false)
//                }else{
//                    binding = FragmentExhibitorDetailAdBinding.inflate(inflater, container, false)
//                }
                binding = FragmentExhibitorDetailBinding.inflate(inflater, container, false)
            }
        }

        pager = binding.viewPagerExhibitorDtl
        dtlPin = binding.dtlPin
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            companyID = ExhibitorDetailFragmentArgs.fromBundle(it).companyID
            if (TextUtils.isEmpty(companyID)) {
                return
            }
        }
        mainViewModel = ViewModelProviders.of(
            requireActivity(), MainViewModelFactory(
                requireActivity().application,
                MainIconRepository.getInstance(CpsDatabase.getInstance(requireContext()).mainIconDao())
            )
        ).get(MainViewModel::class.java)
        initedData()
        initData()
    }

    fun initedData() {
        LogUtil.i("initedData")

        val exhibitorRepository =
            ExhibitorRepository.getInstance(
                CpsDatabase.getInstance(requireContext()).exhibitorDao(),
                CpsDatabase.getInstance(requireContext()).industryDao(),
                CpsDatabase.getInstance(requireContext()).applicationDao()
            )
        dtlViewModel = ViewModelProviders.of(
            this,
            ExhibitorDtlViewModelFactory(exhibitorRepository, companyID)
        )
            .get(ExhibitorDtlViewModel::class.java)
        binding.viewModel = dtlViewModel
        binding.lifecycleOwner = this

        exhibitorViewModel = ViewModelProviders.of(
            this,
            ExhibitorViewModelFactory(requireActivity().application, exhibitorRepository)
        )
            .get(ExhibitorViewModel::class.java)

        getDataFromDB()
        setTitle()
        setDtlAdapter()

    }

    fun initData() {
        dtlViewModel.itemNavigation.observe(this, Observer { kv ->
            if (kv != null) {
                NavHostFragment.findNavController(this)
                    .navigate(
                        ExhibitorDetailFragmentDirections.actionExhibitorDetailFragmentToExhibitorListFragment(
                            kv.K.toString(),
                            kv.V.toString()
                        )
                    )
                dtlViewModel.finishItemNavigation()
            }
        })
        binding.tvTell.setOnClickListener {
            callIntent()
        }
        binding.tvEmail.setOnClickListener {
            emailIntent()
        }
        binding.tvWebsite.setOnClickListener {
            webIntent()
        }
        binding.tvAddExhibitor.setOnClickListener {
            addExhibitor()
        }
        binding.tvPinStar.setOnClickListener {
            addExhibitor()
        }
    }

    private fun getDataFromDB() {
        dtlViewModel.getDataFinish.observe(this, Observer { finish ->
            if (finish) {
                if (dtlViewModel.company.value != null) {
                    binding.obj = dtlViewModel.company.value
                    binding.executePendingBindings()

                    addSchedule()

                    if (!TextUtils.isEmpty(dtlViewModel.company.value!!.getDescription())) descVisible()
                }
                if (dtlViewModel.industries.value?.isNotEmpty()!!) industryVisible()
                if (dtlViewModel.applications.value?.isNotEmpty()!!) appVisible()
                dtlViewModel.isAD.value?.let {
                    if (it) {
                        aboutVisible()
                    }
                }

                if (fragments.size > 0) {
                    pager.offscreenPageLimit = 1
                    val adapter = ExhibitorDtlAdapter(childFragmentManager, fragments)
                    pagerAdapter = adapter
                    pager.adapter = pagerAdapter
                    dtlViewModel.tabIndex.value = kvs[0].V as Int  // 设置第1个按钮被选中
                } else {
                    forbiddenScroll()
                }

                dtlViewModel.insertToHistory()
            }
        })
    }

    private fun setTitle() {
        var o = 0  // 让收缩时候设置title只执行一次
        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, offset ->
            LogUtil.i("offset=$offset")
//            if (offset > -888 && dtlPin.visibility == 0) {
//                dtlPin.visibility = View.GONE
//            } else if (offset <= -888 && dtlPin.visibility == 8) {
//                dtlPin.visibility = View.VISIBLE
//            }

            if (offset >= -85) {  // 展开
                o = 0
                mainViewModel.title.value = getString(R.string.title_company_info)
                dtlPin.visibility = View.GONE
            } else if (o == 0) { // 收缩
                mainViewModel.title.value = dtlViewModel.company.value?.getCompanyName()
                o++
                dtlPin.visibility = View.VISIBLE
            }
        })
    }

    private fun setDtlAdapter() {
        val size = fragments.size
        dtlViewModel.tabIndex.observe(this, Observer { index ->
            if (size == 4) {
                pager.currentItem = index
            } else {
                for (kv in kvs) {
                    if (index == kv.V) {
                        LogUtil.i("tabIndex.observe: kv.K=${kv.K}, ${kv.V}")
                        pager.currentItem = kv.K as Int
                        break
                    }
                }
            }
        })

        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (fragments.size == 4) dtlViewModel.tabIndex.value = position
                else if (fragments.size > 1) {
                    for (kv in kvs) {
                        if (position == kv.K) {
                            LogUtil.i("onPageSelected: kv.K=${kv.K}, ${kv.V}")
                            dtlViewModel.tabIndex.value = kv.V as Int
                            break
                        }
                    }
                }
            }
        })
    }

    private fun aboutVisible() {
        dtlViewModel.aboutVisible.value = true
        fragments.add(DtlAboutFragment(dtlViewModel))
        kvs.add(posIndex, KV(0, 0))
        posIndex++
    }

    private fun descVisible() {
        dtlViewModel.descVisible.value = true
        fragments.add(DtlInfoFragment(dtlViewModel))
        kvs.add(posIndex, KV(posIndex, 1))
        posIndex++
    }

    private fun industryVisible() {
        dtlViewModel.industryVisible.value = true
        fragments.add(DtlIndustryFragment(dtlViewModel))
        kvs.add(posIndex, KV(posIndex, 2))
        posIndex++
    }

    private fun appVisible() {
        dtlViewModel.appVisible.value = true
        fragments.add(DtlApplicationFragment(dtlViewModel))
        kvs.add(posIndex, KV(posIndex, 3))
        posIndex++
    }

    private fun addExhibitor() {
        if (isMyChinaplasLogin()) {
            dtlViewModel.addMyExhibitor()
        } else {
            alertDialogConfirmTwo(requireContext(), R.string.login_first_add_exhibitor,
                DialogInterface.OnClickListener { dialog, which ->
                    findNavController().navigate(R.id.myChinaplasLoginFragment)
                })
        }
    }

    /**
     * 没有tabs时，禁止折叠
     */
    private fun forbiddenScroll() {
        val barChild = binding.appbar.getChildAt(0)
        val params = barChild.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
        barChild.layoutParams = params
    }

    private fun callIntent() {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:${dtlViewModel.company.value!!.Tel}")
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.exception_toast_phone),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun emailIntent() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            val url = dtlViewModel.company.value!!.Email
            data = Uri.parse("mailto:url")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(url))
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.exception_toast_email),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun webIntent() {
        var url = dtlViewModel.company.value!!.Website
        if (!url!!.toLowerCase(Locale.US).startsWith("http"))
            url = "http://$url"
        LogUtil.i("url=$url")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "No Client!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun addSchedule() {
        binding.fabAddSchedule.setOnClickListener {
            val scheduleInfo =
                ScheduleInfo(null,
                    dtlViewModel.company.value!!.getCompanyName(),
                    "",
                    dtlViewModel.company.value!!.BoothNo,
                    companyID,
                    "",
                    "",
                    1,
                    0,
                    null)
            findNavController().navigate(ExhibitorDetailFragmentDirections.actionToScheduleEditFragment(true,
                scheduleInfo))
        }
    }


}
