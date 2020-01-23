package com.adsale.chinaplas.ui.drawer

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.adsale.chinaplas.R
import com.adsale.chinaplas.databinding.FragmentSubscribeBinding
import com.adsale.chinaplas.ui.webcontent.VisitorTipFragmentDirections
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.viewmodels.SubscribeViewModel

class SubscribeFragment : Fragment() {

    private lateinit var viewModel: SubscribeViewModel
    private lateinit var binding: FragmentSubscribeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProviders.of(this).get(SubscribeViewModel::class.java)
        binding = FragmentSubscribeBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var progressDialog: Dialog? = null
        viewModel.checkStatus.observe(this, Observer {
            if (viewModel.checkStatus.value != -1) {
                when (viewModel.checkStatus.value!!) {
                    EMAIL_EMPTY -> {
                        alertDialogSingleConfirm(context!!, R.string.subscribe_msg003)
                        binding.etEmail.requestFocus()
                    }
                    EMAIL_INVALID -> {
                        alertDialogSingleConfirm(context!!, R.string.subscribe_msg004)
                        binding.etEmail.requestFocus()
                    }
                    COMPANY_EMPTY -> {
                        alertDialogSingleConfirm(context!!, R.string.subscribe_msg013)
                        binding.etCompany.requestFocus()
                    }
                    NAME_EMPTY -> {
                        alertDialogSingleConfirm(context!!, R.string.subscribe_msg001)
                        binding.etName.requestFocus()
                    }
                    NAME_INVALID -> {  // 发送失败，请稍候再试
                        alertDialogSingleConfirm(context!!, R.string.subscribe_msg002)
                        binding.etName.requestFocus()
                    }
                    98 -> { // 提交中
                        hideInput()
                        progressDialog = alertDialogProgress(context!!, getString(R.string.subscribe_msg))
                    }
                    100 -> {  // 訂閱成功
                        if (progressDialog != null && progressDialog!!.isShowing) {
                            progressDialog!!.dismiss()
                        }
                        alertDialogSingleConfirm(context!!, R.string.thx_dy)
                        viewModel.reset()
                    }
                    99 -> { // 訂閱失敗
                        if (progressDialog != null && progressDialog!!.isShowing) {
                            progressDialog!!.dismiss()
                        }
                        alertDialogSingleConfirm(context!!, R.string.reg_toast_submit_fail)
                    }
                    101 -> {  // 隱私政策
                        this.findNavController()
                            .navigate(SubscribeFragmentDirections.actionNavSubscribeToWebContentFragment("Privacy",
                                getString(R.string.title_privacy)))
                    }
                }
                viewModel.checkStatus.value = -1
            } else {

            }
        })

    }

    private fun hideInput() {
        val inputMethodManager: InputMethodManager =
            context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}