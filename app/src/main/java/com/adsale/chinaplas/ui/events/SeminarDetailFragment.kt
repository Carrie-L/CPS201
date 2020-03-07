package com.adsale.chinaplas.ui.events


import android.text.Html
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.SeminarInfo
import com.adsale.chinaplas.data.dao.SeminarRepository
import com.adsale.chinaplas.databinding.FragmentSeminarDetailBinding
import com.adsale.chinaplas.helper.ADHelper
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.getItemSeminarEventID
import com.adsale.chinaplas.viewmodels.SeminarViewModel
import com.adsale.chinaplas.viewmodels.SeminarViewModelFactory
import com.bumptech.glide.Glide

/**
 * A simple [Fragment] subclass.
 */
class SeminarDetailFragment : BaseFragment() {
    private lateinit var model: SeminarViewModel
    private lateinit var binding: FragmentSeminarDetailBinding

    override fun initedView(inflater: LayoutInflater) {
        binding = FragmentSeminarDetailBinding.inflate(inflater, baseFrame, true)
    }

    override fun initView() {
    }

    override fun initedData() {

    }

    override fun initData() {
        model = ViewModelProviders.of(this, SeminarViewModelFactory(
            SeminarRepository.getInstance(CpsDatabase.getInstance(requireContext()).seminarDao())
        )).get(SeminarViewModel::class.java)
//        binding.model = seminarViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val eventID = getItemSeminarEventID()
        LogUtil.i("eventID=$eventID")

        model.getDetailInfo(eventID)

        model.speakerEntity.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            binding.speaker = it
            it.Seminarsummary?.let { Seminarsummary ->
                binding.tvSeminarInfo.text = Html.fromHtml(Seminarsummary)
            }
            it.SpeakerName?.let { SpeakerName ->
                binding.tvSeminarInfo.text = Html.fromHtml(SpeakerName)
            }
        })
        model.infoEntity.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            binding.info = it
            it.Topic?.let { Topic ->
                binding.tvSeminarInfo.text = Html.fromHtml(Topic)
            }
            showD8(it)
        })

        binding.tvSeminarCompany.setOnClickListener {
            findNavController().navigate(SeminarDetailFragmentDirections.actionToExhibitorDetailFragment(model.infoEntity.value!!.CompanyID!!))
        }

    }

    private fun showD8(info: SeminarInfo) {
        val adHelper = ADHelper.getInstance()
        val d8List = adHelper.d8List()
        for (d8 in d8List) {
            if (info.Date!!.contains(d8.date)
                && isAM(info.Time!!) == (d8.isAm == 1)) {
                Glide.with(requireContext()).load(adHelper.baseUrl + d8.getTopImage()).into(binding.ivAdBanner)
                LogUtil.i("detail top url=${adHelper.baseUrl + d8.getTopImage()}")
                binding.ivAdBanner.visibility = View.VISIBLE
                break
            }
        }
    }

    private fun isAM(time: String): Boolean {
        return time < "12:00"
    }


    override fun back() {
    }


}
