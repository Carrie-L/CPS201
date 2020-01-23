package com.adsale.chinaplas.base


import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.databinding.FragmentBaseBinding
import com.adsale.chinaplas.utils.BACK_CUSTOM
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseFragment : Fragment() {
    protected lateinit var baseBinding: FragmentBaseBinding
    protected lateinit var baseFrame: FrameLayout
    protected lateinit var mainViewModel: MainViewModel
    protected var isViewInited = false
    protected var lastView: View? = null

    /*  如果在 initData中设置了为true， 在跳转到其他Fragment时，需要把 isBackCustom 设为 false. 否则其他页有可能无法返回。*/
    protected var isBackCustom = false
    protected var isChangeRightIcon = false
    protected var title: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (lastView == null) {
            baseBinding = FragmentBaseBinding.inflate(inflater)
            baseFrame = baseBinding.baseFrameLayout
            initedView(inflater)
            lastView = baseBinding.root
        }
        initView()
        return lastView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!isViewInited) {
            mainViewModel = ViewModelProviders.of(
                requireActivity(), MainViewModelFactory(requireActivity().application,
                    MainIconRepository.getInstance(CpsDatabase.getInstance(requireContext()).mainIconDao())
                )
            ).get(MainViewModel::class.java)
            initedData()
            mainViewModel.isChangeRightIcon.value = isChangeRightIcon
            if (!TextUtils.isEmpty(title)) {
                mainViewModel.title.value = title
            }
        }

        initData()

        if (isBackCustom) {
            mainViewModel.backRoad.value = BACK_CUSTOM
            mainViewModel.backCustom.observe(this, Observer {
                if (it) {
                    LogUtil.i("back()")
                    back()
                    resetBackDefault()
                }
            })
        }

    }

    protected fun resetBackDefault() {
        LogUtil.i("resetBackDefault")
        mainViewModel.resetBackDefault()
    }

    protected abstract fun initedView(inflater: LayoutInflater)

    protected abstract fun initView()

    /**
     * 设置 [isChangeRightIcon]、[title]
     * 不设置为默认
     */
    protected abstract fun initedData()

    /**
     * 设置 [isBackCustom]
     * 不设置为默认
     */
    protected abstract fun initData()

    /**
     * 要运行back()，还需要设置
     * @param isBackCustom =true
     */
    protected abstract fun back()


}
