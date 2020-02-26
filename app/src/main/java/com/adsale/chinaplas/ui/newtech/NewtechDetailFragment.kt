package com.adsale.chinaplas.ui.newtech


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.NewtechRepository
import com.adsale.chinaplas.databinding.FragmentNewtechDetailBinding
import com.adsale.chinaplas.utils.dp2px
import com.adsale.chinaplas.utils.getScreenHeight
import com.adsale.chinaplas.utils.getScreenWidth
import com.adsale.chinaplas.viewmodels.NewtechViewModel
import com.adsale.chinaplas.viewmodels.NewtechViewModelFactory
import com.baidu.speech.utils.LogUtil

/**
 * A simple [Fragment] subclass.
 */
class NewtechDetailFragment : Fragment() {
    private lateinit var binding: FragmentNewtechDetailBinding
    private lateinit var viewModel: NewtechViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentNewtechDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        arguments?.let {
            val rid = NewtechDetailFragmentArgs.fromBundle(it).id
            LogUtil.i("rid=$rid")
            viewModel.getItemInfo(rid)
        }

        viewModel.itemInfo.observe(this, Observer {
            if (it == null) return@Observer
            binding.obj = it
            binding.executePendingBindings()

            LogUtil.i("it===${it.toString()}")

        })

        val height = getScreenHeight() - dp2px(136f)
        val params1 = LinearLayout.LayoutParams(getScreenWidth(), (height * 0.3f).toInt())
        binding.ivProduct.layoutParams = params1

        val params2 = LinearLayout.LayoutParams(getScreenWidth(), (height * 0.4f).toInt())
        binding.midCons.layoutParams = params2

        val params3 = LinearLayout.LayoutParams(getScreenWidth(), (height * 0.3f).toInt())
        binding.bottomCons.layoutParams = params3

    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this,
            NewtechViewModelFactory(NewtechRepository.getInstance(CpsDatabase.getInstance(requireContext()).newtechDao())))
            .get(NewtechViewModel::class.java)
//        binding.model = viewModel
//        binding.lifecycleOwner = this
    }


}
