package com.adsale.chinaplas.ui.tools


import android.app.Activity
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.adsale.chinaplas.R
import com.adsale.chinaplas.databinding.FragmentLoginSmBinding
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.viewmodels.MyChinaplasViewModel


/**
 * A simple [Fragment] subclass.
 */
class LoginSMSFragment constructor(val viewModel: MyChinaplasViewModel) : Fragment() {
    private lateinit var binding: FragmentLoginSmBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        LogUtil.i("onCreateView--")
        binding = FragmentLoginSmBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogUtil.i("onActivityCreated--")
        binding.tvLoginHint2.text = Html.fromHtml(getString(R.string.login_hint_2))

        binding.etLoginCode.setOnEditorActionListener { _, actionId, _ ->
            LogUtil.i("actionId=$actionId")
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onSmsSubmit()
                true
            }
            false
        }

        viewModel.submitStatus.observe(this, Observer {
            if (viewModel.barClick.value == 2) {
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
                    SMS_CODE_EMPTY -> {
                        alertDialogSingleConfirm(context!!, R.string.toast_code_empty)
                        binding.etLoginCode.requestFocus()
                    }
                    SMS_CODE_SEND_FAIL_0 -> {  // 发送失败，请稍候再试
                        alertDialogSingleConfirm(context!!, R.string.sms_response_0)
                        binding.etLoginCode.requestFocus()
                    }
                    SMS_CODE_SEND_FAIL -> {  // 发送失败，请稍候再试
                        alertDialogSingleConfirm(context!!, R.string.sms_response_1)
                        binding.etLoginCode.requestFocus()
                    }
                    SMS_CODE_PHONE_INVALID -> { // 手机号码格式不对
                        alertDialogSingleConfirm(context!!, R.string.sms_response_2)
                        binding.etLoginCode.requestFocus()
                    }
                    SMS_CODE_SEND_SUCCESS -> {  // 验证码发送成功
                        alertDialogSingleConfirm(context!!, R.string.sms_response_3)
                        binding.etLoginCode.requestFocus()
                    }
                    SMS_CODE_INCORRECT -> {  // 验证码不正确
                        alertDialogSingleConfirm(context!!, R.string.toast_sms_error)
                        binding.etLoginCode.requestFocus()
                    }
                    LOGIN_FAILED -> {
                        alertDialogSingleConfirm(context!!, R.string.toast_login_failed)
                    }
                    LOGIN_SUCCESS -> {
                        viewModel.toDestination.value = true
                    }

                }
            }
        })


    }

    private fun hideInput() {
        val inputMethodManager: InputMethodManager =
            context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }


}
