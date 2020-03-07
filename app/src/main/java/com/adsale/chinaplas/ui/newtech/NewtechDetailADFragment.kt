package com.adsale.chinaplas.ui.newtech


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.NewtechRepository
import com.adsale.chinaplas.data.entity.D7
import com.adsale.chinaplas.databinding.FragmentNewtechDetailAdBinding
import com.adsale.chinaplas.helper.ADHelper
import com.adsale.chinaplas.utils.dp2px
import com.adsale.chinaplas.utils.getScreenHeight
import com.adsale.chinaplas.utils.getScreenWidth
import com.adsale.chinaplas.viewmodels.NewtechViewModel
import com.adsale.chinaplas.viewmodels.NewtechViewModelFactory
import com.baidu.speech.utils.LogUtil
import com.bumptech.glide.Glide

/**
 * 新技术产品 - 广告 - 详情
 */
class NewtechDetailADFragment : Fragment() {
    private lateinit var binding: FragmentNewtechDetailAdBinding
    private lateinit var viewModel: NewtechViewModel
    private lateinit var videoView: VideoView
    //    private lateinit var ivCover: ImageView
    private lateinit var adHelper: ADHelper
    private var d7: D7? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewtechDetailAdBinding.inflate(inflater)
//        ivCover = binding.ivVideoCover
        videoView = binding.videoView
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()

        adHelper = ADHelper.getInstance()
        arguments?.let {
            val rid = NewtechDetailFragmentArgs.fromBundle(it).id
            LogUtil.i("rid=$rid")
            d7 = adHelper.getD7(rid)
            if (d7 != null) {
                val info = adHelper.getADProductEntity(d7!!)
                binding.obj = info
                binding.executePendingBindings()
                LogUtil.i("d7===${d7.toString()}")

                playVideo()
            }
        }

        val height = getScreenHeight() - dp2px(136f)
        val params1 = LinearLayout.LayoutParams(getScreenWidth(), (height * 0.3f).toInt())
        binding.topContent.layoutParams = params1

        val params2 = LinearLayout.LayoutParams(getScreenWidth(), (height * 0.4f).toInt())
        binding.midCons.layoutParams = params2

        val params3 = LinearLayout.LayoutParams(getScreenWidth(), (height * 0.3f).toInt())
        binding.bottomCons.layoutParams = params3


    }

    private fun playVideo() {
        videoView.setMediaController(MediaController(requireContext()))
        videoView.setVideoURI(Uri.parse(d7!!.videoLink))
        videoView.start()

        Glide.with(this).load(Uri.parse(d7!!.LogoImageLink)).into(binding.ivLogo)

//        ivCover.setOnClickListener {
//            findNavController().navigate()
//        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this,
            NewtechViewModelFactory(NewtechRepository.getInstance(CpsDatabase.getInstance(requireContext()).newtechDao())))
            .get(NewtechViewModel::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.stopPlayback()
    }


}
