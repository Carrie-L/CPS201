package com.adsale.chinaplas.ui.tools.mychinaplas


import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.adsale.chinaplas.R
import com.adsale.chinaplas.databinding.FragmentLoginPasswordBinding
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.viewmodels.MyChinaplasViewModel


/**
 * A simple [Fragment] subclass.
 */
class LoginPasswordFragment constructor(val viewModel: MyChinaplasViewModel) : Fragment() {

    private lateinit var binding: FragmentLoginPasswordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        LogUtil.i("onCreateView--")
        binding = FragmentLoginPasswordBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogUtil.i("onActivityCreated--phone=${getMyChinaplasPhone()}")

        viewModel.phoneNo.value = getMyChinaplasPhone()
        viewModel.email.value = getMyChinaplasEmail()

        binding.etLoginPwd.setOnEditorActionListener { _, actionId, _ ->
            LogUtil.i("actionId=$actionId")
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onPwdSubmit()
                true
            }
            false
        }

        viewModel.submitStatus.observe(this, Observer {
            LogUtil.i("viewModel.barClick.value=${viewModel.barClick.value}")
            if (viewModel.barClick.value == 1) {
                when (viewModel.submitStatus.value!!) {
                    EMAIL_EMPTY -> {
                        alertDialogSingleConfirm(context!!, R.string.reg_hint_input_email)
                        binding.etLoginEmail.requestFocus()
                    }
                    EMAIL_INVALID -> {
                        alertDialogSingleConfirm(context!!, R.string.toast_cps_invalid_email)
                        binding.etLoginEmail.requestFocus()
                    }
                    PHONE_EMPTY -> {
                        alertDialogSingleConfirm(context!!, R.string.reg_hint_input_phone_no)
                        binding.etLoginPhone.requestFocus()
                    }
                    PWD_EMPTY -> {
                        alertDialogSingleConfirm(context!!, R.string.toast_cps_pwd_empty)
                        binding.etLoginPwd.requestFocus()
                    }
                    PWD_INVALID -> {
                        alertDialogSingleConfirm(context!!, R.string.reg_help_pwd)
                        binding.etLoginPwd.requestFocus()
                    }
                    LOGIN_FAILED -> {
                        alertDialogSingleConfirm(context!!, R.string.toast_login_failed)
                    }
                    LOGIN_SUCCESS -> {
                        LogUtil.i("Login password -登录成功")
                        viewModel.toDestination.value = true
                    }
                }
            }
        })


    }


}
