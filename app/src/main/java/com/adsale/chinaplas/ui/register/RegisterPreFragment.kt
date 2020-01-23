package com.adsale.chinaplas.ui.register

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.adsale.chinaplas.R
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.databinding.FragmentRegisterPreBinding
import com.adsale.chinaplas.network.CpsApi
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.viewmodels.RegisterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by Carrie on 2019/12/3.
 */
class RegisterPreFragment : BaseFragment() {
    private lateinit var binding: FragmentRegisterPreBinding
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)


    override fun initedView(inflater: LayoutInflater) {
        binding = FragmentRegisterPreBinding.inflate(inflater, baseFrame, true)
        binding.tvPhone.text = Html.fromHtml(getString(R.string.reg_pre_phone))
        binding.tvEmail.text = Html.fromHtml(getString(R.string.reg_pre_email))
        binding.tv1.text = Html.fromHtml(getString(R.string.reg_pre_1))
        binding.tv4.text = Html.fromHtml(getString(R.string.reg_pre_4))
        binding.tv4.setOnClickListener {
            emailIntent()
        }
    }

    override fun initView() {
    }

    override fun initedData() {
    }

    override fun initData() {
        isBackCustom = true

        binding.tvNext.setOnClickListener {
            val phoneNo = binding.etPhone.text.toString()
            val email = binding.etEmail.text.toString()

            if (TextUtils.isEmpty(phoneNo)) {
                alertDialogSingleConfirm(requireContext(), R.string.reg_hint_input_phone_no)
                binding.etPhone.requestFocus()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(email)) {
                alertDialogSingleConfirm(requireContext(), R.string.reg_hint_input_email)
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }
            if (!checkEmail(email)) {
                alertDialogSingleConfirm(requireContext(), R.string.reg_toast_valid_email)
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            hideInput(requireContext(), binding.root.windowToken)

            uiScope.launch {
                if (netAccountExists(phoneNo, email)) {  // 存在
                    alertDialogTwoButton(requireContext(), R.string.reg_pre_hint_login,
                        R.string.reg_text_login_mychinaplas, R.string.back,
                        DialogInterface.OnClickListener { dialog, _ ->
                            LogUtil.i("phoneNo=$phoneNo, email=$email")
                            setMyChinaplasPhone(phoneNo)
                            setMyChinaplasEmail(email)
                            resetBackDefault()
                            findNavController().navigate(R.id.myChinaplasLoginFragment)
                            dialog.dismiss()
                        })
                } else {
                    resetBackDefault()
                    setTellData(6, phoneNo)
                    setEmail(0, email)
                    val action =
                        RegisterPreFragmentDirections.actionRegisterPreFragmentToRegisterFragment(
                            phoneNo, email
                        )
                    NavHostFragment.findNavController(requireParentFragment())
                        .navigate(action)
                }
            }
        }
    }

    override fun back() {
        findNavController().popBackStack(R.id.nav_home, false)
    }

    /**
     * 查询账户是否存在
     * 1: 存在，登录 MyChinaplas
     * 0: 不存在，进行下一步
     */
    private suspend fun netAccountExists(phoneNo: String, email: String): Boolean {
        val result = CpsApi.regService.PreregAccountExists(phoneNo, email)
            .await()
            .string()
        LogUtil.i("PreregAccountExists: result=$result")
        return result == "1"
    }

    private fun emailIntent() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            type = "text/plain"
            val url = "prereg-payment@adsale.com.hk"
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


}