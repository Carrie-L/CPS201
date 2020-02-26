package com.adsale.chinaplas.ui.events


import android.text.Html
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.SeminarRepository
import com.adsale.chinaplas.databinding.FragmentSeminarDetailBinding
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.getItemSeminarEventID
import com.adsale.chinaplas.viewmodels.SeminarViewModel
import com.adsale.chinaplas.viewmodels.SeminarViewModelFactory

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
        binding.lifecycleOwner = this

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
        })
    }

    override fun back() {
    }


}
