package com.adsale.chinaplas.ui.register


import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import com.adsale.chinaplas.R
import com.adsale.chinaplas.cpsDatabase
import com.adsale.chinaplas.data.dao.RegOptionData
import com.adsale.chinaplas.data.dao.RegisterRepository
import com.adsale.chinaplas.data.entity.CountryJson
import com.adsale.chinaplas.data.entity.KV
import com.adsale.chinaplas.databinding.FragmentRegisterFormBinding
import com.adsale.chinaplas.ui.dialog.RegProductDialog
import com.adsale.chinaplas.ui.dialog.RegRegionDialog
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.viewmodels.*
import java.lang.Exception

/**
 * 预登记表单页
 *
 * 1. 如果已经预登记，
 *
 */
class RegisterFragment : Fragment(), RegionSelectCallback {

    //    private lateinit var binding: FragmentRegisterBinding
    private lateinit var binding: FragmentRegisterFormBinding
    private lateinit var regViewModel: RegisterViewModel
    private lateinit var pickViewModel: RegPickViewModel
    private lateinit var regionViewModel: RegRegionViewModel

    private var submitDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN and WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        binding = FragmentRegisterFormBinding.inflate(inflater)
        binding.tvPostHint.text =
            Html.fromHtml(getString(com.adsale.chinaplas.R.string.reg_post_hint))
        binding.cbPrivacy.text = Html.fromHtml(getString(com.adsale.chinaplas.R.string.reg_privacy))
        binding.tvLast.text = Html.fromHtml(getString(com.adsale.chinaplas.R.string.reg_last_hint))
        binding.tvHintEmail.text = Html.fromHtml(getString(R.string.reg_help_email))

        val fragment = requireParentFragment()
        LogUtil.i("fragment= $fragment")

        val regRepo =
            RegisterRepository.getInstance(cpsDatabase.countryDao(), cpsDatabase.regOptionDao())
        regViewModel = ViewModelProviders.of(requireActivity(), RegViewModelFactory(regRepo))
            .get(RegisterViewModel::class.java)
        pickViewModel = ViewModelProviders.of(requireActivity(), RegViewModelFactory(regRepo))
            .get(RegPickViewModel::class.java)
        regionViewModel = ViewModelProviders.of(requireActivity(), RegViewModelFactory(regRepo))
            .get(RegRegionViewModel::class.java)

        binding.viewModel = regViewModel
        binding.pickViewModel = pickViewModel
        binding.lifecycleOwner = requireActivity()

        regionViewModel.setRegionSelectCallback(this)

        binding.etArea.setOnClickListener {
            LogUtil.i("click country pick")
            val dialogFragment = RegRegionDialog()
            requireActivity().supportFragmentManager.inTransaction {
                dialogFragment.show(fragmentManager!!, "Area")
            }
        }

        binding.etService.setOnClickListener {
            LogUtil.i("click service pick")
            val dialogFragment = RegProductDialog()
            requireActivity().supportFragmentManager.inTransaction {
                dialogFragment.show(fragmentManager!!, "Product")
            }
        }
        initView()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()

        try {
            arguments?.let {
                val phoneNo = RegisterFragmentArgs.fromBundle(it).phoneNo
                if (!TextUtils.isEmpty(phoneNo))
                    regViewModel.mobileNo.value = phoneNo
                val email = RegisterFragmentArgs.fromBundle(it).email
                if (!TextUtils.isEmpty(email))
                    regViewModel.email.value = email
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
        }

        pickViewModel.otherService.observe(this, Observer {
            binding.llProductOthers.visibility = View.VISIBLE
            setServiceOthersView(it)
        })
        binding.cbPost.setOnCheckedChangeListener { _, isChecked ->
            regViewModel.postChecked.set(isChecked)
        }
        pickViewModel.serviceLabels.observe(this, Observer {
            val sb = StringBuilder()
            val sbDialogText = StringBuilder()
            LogUtil.i("serviceLabels:: it=${it.toString()}")

            for (item in it) {
                if (sb.isNotEmpty()) {
                    sb.append(",")
                }
                sb.append(item.GroupCode).append("|").append(item.DetailCode)
                // confirm dialog 显示的文字
                if (!item.isOther()) {
                    if (sbDialogText.isNotEmpty()) {
                        sbDialogText.append(",")
                    }
                    sbDialogText.append(item.getName())
                }
                // confirm dialog end+++
            }
            regViewModel.service.value = sb.toString()
        })

        regViewModel.isSMSCodeCorrect.observe(this, Observer {
            if (it == 0) {
                alertDialogSingleConfirm(context!!, R.string.error_verify_code_incorrect)
                binding.mobileVerifyCode.requestFocus()
            }
        })

        regViewModel.isSubmitDataSuccess.observe(this, Observer {
            if (it == 1) {
                if (submitDialog != null && submitDialog!!.isShowing()) {
                    LogUtil.i(" isSubmitDataSuccess   取消dialog  ::$submitDialog")
                    submitDialog!!.dismiss()
                }
                // 成功返回url，跳转Fragment
                NavHostFragment.findNavController(this).navigate(
                    RegisterFragmentDirections.actionRegisterFragmentToRegisterWebsiteFragment2(
                        regViewModel.payUrl!!,true))

//                val webFragment = RegisterWebsiteFragment()
//                requireActivity().supportFragmentManager.inTransaction {
//                    webFragment.
//                }

//               requireActivity().showFragment<RegisterWebsiteFragment>(R.id.nav_host_fragment)

            }
        })
//        var dialog: ProgressDialog? = null
        regViewModel.progressBarVisible.observe(this, Observer { isVisible ->
            LogUtil.i("isVisible=$isVisible")
            if (isVisible) {
                submitDialog = alertDialogProgress(this.requireContext(), getString(R.string.reg_toast_submit))
                LogUtil.i("init dialog = $submitDialog")
            } else {
                if (submitDialog == null) {
                    LogUtil.i("isVisible: dialog==null")
                } else {
                    LogUtil.i("isVisible: dialog!=nul:::: $submitDialog")
                }
                submitDialog?.dismiss()
            }
        })

        regViewModel.btnSubmitClicked.observe(this, Observer { submit ->
            if (submit) {
                if (!checkIsEmpty()) {
                    regViewModel.apiSubmit()
                }
                regViewModel.btnSubmitClicked.value = false
            }
        })
    }

    private fun checkIsEmpty(): Boolean {
//        LogUtil.i("titleText=${regViewModel.titleText.value}")
//        LogUtil.i("titleCode=${regViewModel.titleCode.value}")
//        LogUtil.i("titleOther=${regViewModel.titleOther.value}")
//        LogUtil.i("functionText=${regViewModel.functionText.value}")
//        LogUtil.i("functionOther=${regViewModel.functionOther.value}")

        // 名字
        if (TextUtils.isEmpty(regViewModel.name.value?.trim())) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_name, R.string.back)
            binding.etName.requestFocus()
            binding.regScroll.scrollTo(0, 0)
        } else if (!checkYou(regViewModel.name.value!!)) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_valid_name, R.string.back)
            binding.etName.requestFocus()
            binding.regScroll.scrollTo(0, 0)
        }
        // 公司
        else if (TextUtils.isEmpty(regViewModel.company.value)) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_company, R.string.back)
            binding.etCompany.requestFocus()
            binding.regScroll.scrollTo(0, 0)
        }
        // 职位
        else if (regViewModel.titleText.value == getString(R.string.please_select)) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_title, R.string.back)
            binding.spinnerTitle.requestFocus()
            binding.regScroll.scrollTo(0, 0)
        } else if (binding.etTitleOther.visibility == View.VISIBLE && TextUtils.isEmpty(regViewModel.titleOther.value)) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_title_other, R.string.back)
            binding.etTitleOther.requestFocus()
            binding.regScroll.scrollTo(0, 0)
        }
        // 职能部门
        else if (regViewModel.functionText.value == getString(R.string.please_select)) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_function, R.string.back)
            binding.spinnerFunction.requestFocus()
            binding.regScroll.scrollTo(0, 0)
        } else if (binding.etDepOther.visibility == View.VISIBLE && TextUtils.isEmpty(regViewModel.functionOther.value)) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_function_other, R.string.back)
            binding.etDepOther.requestFocus()
            binding.regScroll.scrollTo(0, 0)
        }
        // 主营产品
        else if (TextUtils.isEmpty(regViewModel.companyProduct.value)) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_product, R.string.back)
            binding.etProduct.requestFocus()
            binding.regScroll.scrollTo(0, (binding.etProduct.y / 1.2).toInt())
        }
        // 国家/地区
        else if (regViewModel.regionText.value == getString(R.string.please_select)) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_region, R.string.back)
            binding.etArea.requestFocus()
            binding.regScroll.scrollTo(0, (binding.etArea.y / 1.2).toInt())
        }
        // 电话
        else if (TextUtils.isEmpty(regViewModel.tellRegionCode.value)) {   // 国家地区号
            alertDialogSingleButton(requireContext(), R.string.reg_toast_mobile_number_region, R.string.back)
            binding.etRegionCode.requestFocus()
            binding.regScroll.scrollTo(0, (binding.etRegionCode.y / 1.2).toInt())
        } else if (TextUtils.isEmpty(regViewModel.tellAreaCode.value)) {  // 电话 - 区号
            alertDialogSingleButton(requireContext(), R.string.reg_toast_tell_area, R.string.back)
            binding.etAreaCode.requestFocus()
            binding.regScroll.scrollTo(0, (binding.etAreaCode.y / 1.2).toInt())
        } else if (TextUtils.isEmpty(regViewModel.tellNo.value)) {  // 电话号码
            alertDialogSingleButton(requireContext(), R.string.reg_toast_tell_number, R.string.back)
            binding.etTell.requestFocus()
            binding.regScroll.scrollTo(0, (binding.etTell.y / 1.2).toInt())
        }
        // 手机号码
        else if (!regViewModel.cnMobileVisible.value!! && TextUtils.isEmpty(regViewModel.mobileAreaCode.value)) {  // 手机号码  - 区号
            alertDialogSingleButton(requireContext(), R.string.reg_toast_mobile_number_area, R.string.back)
            if (binding.layoutMobileNotCn.visibility == View.VISIBLE) {
                binding.etAreaCodeMobile1.requestFocus()
            } else if (binding.layoutMobileCn.visibility == View.VISIBLE) {
                binding.etMobileCn.requestFocus()
            }
            binding.regScroll.scrollTo(0, 2000)
        } else if (TextUtils.isEmpty(regViewModel.mobileNo.value)) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_tell_number, R.string.back)
            binding.etMobile.requestFocus()
            binding.regScroll.scrollTo(0, 2000)
        } else if (regViewModel.cnMobileVisible.value!! && TextUtils.isEmpty(regViewModel.smsCode.value)) {  // 短信验证码
            alertDialogSingleButton(requireContext(), R.string.reg_toast_sms_code, R.string.back)
            binding.mobileVerifyCode.requestFocus()
            binding.regScroll.scrollTo(0, 2000)
        }
        //  邮箱
        else if (regViewModel.email2.value != regViewModel.email.value) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_email2, R.string.back)
            binding.etEmail2.requestFocus()
            binding.regScroll.scrollTo(0, 1500)
        }
        // 密码
        else if (TextUtils.isEmpty(regViewModel.pwd.value)) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_pwd, R.string.back)
            binding.etPwd.requestFocus()
            binding.regScroll.scrollTo(0, (binding.etPwd.y / 1.2).toInt())
        } else if (!checkPwd(regViewModel.pwd.value!!)) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_valid_pwd, R.string.back)
            binding.etPwd.requestFocus()
            binding.regScroll.scrollTo(0, (binding.etPwd.y / 1.2).toInt())
        } else if (regViewModel.pwd2.value != regViewModel.pwd.value) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_pwd2, R.string.back)
            binding.etPwd2.requestFocus()
            binding.regScroll.scrollTo(0, (binding.etPwd2.y / 1.2).toInt())
        }
        // 邮寄
        else if (regViewModel.postChecked.get()!! && TextUtils.isEmpty(regViewModel.postCity.value)) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_address, R.string.back)
            binding.etPostCity.requestFocus()
            binding.regScroll.scrollTo(0, (binding.etPostCity.y / 1.2).toInt())
        }
        // 邮编
        else if (regViewModel.postChecked.get() && regViewModel.cnMobileVisible.value!! && regViewModel.postcode.value != "0" && regViewModel.postcode.value!!.length < 6) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_valid_postcode_cn, R.string.back)
            binding.etPostcode.requestFocus()
        } else if (regViewModel.postChecked.get() && !regViewModel.cnMobileVisible.value!! && regViewModel.postcode.value != "0" && regViewModel.postcode.value!!.length < 3) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_postcode_0, R.string.back)
            binding.etPostcode.requestFocus()
        }

        // 勾选条款
        else if (!binding.cbPrivacy.isChecked) {
            LogUtil.i("binding.cbPrivacy.isChecked=${binding.cbPrivacy.isChecked}")
            alertDialogSingleButton(requireContext(), R.string.reg_toast_agree_privacy, R.string.back)
            binding.cbPrivacy.requestFocus()
        }
        // 公司业务
        else if (TextUtils.isEmpty(regViewModel.service.value)) {
            alertDialogSingleButton(requireContext(), R.string.reg_toast_service, R.string.back)
            binding.etService.requestFocus()
            binding.regScroll.scrollTo(0, 200)
        } else if (otherCollections.size > 0 && serviceOtherIsEmpty()) {
            LogUtil.i("service other empty")
        } else {
            LogUtil.i("binding.cbPrivacy.isChecked2=${binding.cbPrivacy.isChecked}")
            return false
        }
        return true
    }

    private fun serviceOtherIsEmpty(): Boolean {
        for (item in otherCollections) {
            when (item.DetailCode) {
                "2008" -> {
                    if (serviceToastValue(regViewModel.serviceOtherCar.value, item.getName())) {
                        return true
                    }
                }
                "2016" -> {
                    if (serviceToastValue(regViewModel.serviceOtherPackage.value, item.getName())) {
                        return true
                    }
                }
                "2022" -> {
                    if (serviceToastValue(regViewModel.serviceOtherCosme.value, item.getName())) {
                        return true
                    }
                }
                "2032" -> {
                    if (serviceToastValue(regViewModel.serviceOtherEE.value, item.getName())) {
                        return true
                    }
                }
                "2038" -> {
                    if (serviceToastValue(regViewModel.serviceOtherMedical.value, item.getName())) {
                        return true
                    }
                }
                "2045" -> {
                    if (serviceToastValue(regViewModel.serviceOtherBuild.value, item.getName())) {
                        return true
                    }
                }
                "2049" -> {
                    if (serviceToastValue(regViewModel.serviceOtherLED.value, item.getName())) {
                        return true
                    }
                }
                "2066" -> {
                    if (serviceToastValue(regViewModel.serviceOtherText.value, item.getName())) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * true : 空 ，false: 不为空
     */
    private fun serviceToastValue(value: String?, name: String): Boolean {
        if (TextUtils.isEmpty(value)) {
            alertDialogSingleButton(
                requireContext(),
                String.format(getString(R.string.reg_toast_service_other), name),
                R.string.back
            )
            binding.regScroll.scrollTo(0, 300)
            return true
        }
        return false
    }

    private fun initData() {
        initGender()
        initSpinnerTitle()
        initSpinnerFunction()
    }

    private fun initGender() {
        LogUtil.i("gender=${regViewModel.gender.value}")

        if (regViewModel.gender.value == REG_FEMALE) {
            binding.rbFemale.isChecked = true
        } else {
            binding.rbMale.isChecked = true
        }
        binding.rbFemale.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                LogUtil.i("rbFemale isChecked")
                regViewModel.gender.value = REG_FEMALE
            }
        }
        binding.rbMale.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                LogUtil.i("rbMale isChecked")
                regViewModel.gender.value = REG_MALE
            }
        }
        LogUtil.i("gender2=${regViewModel.gender.value}")
    }

    private fun initSpinnerTitle() {
        val titleSpinner = binding.spinnerTitle
        //        var spinnerAdapter: RegSpinnerAdapter? = null
        //        spinnerAdapter = RegSpinnerAdapter(listOf())
        //        titleSpinner.adapter = spinnerAdapter
        //        regViewModel.titles.observe(this, Observer { titles ->
        //            if (!TextUtils.isEmpty(regViewModel.titleCode.value)) {
        //                var i = 0
        //                for (item in titles) {
        //                    if (item.DetailCode == regViewModel.titleCode.value) {
        //                        item.isChecked.set(true)
        //                        titles[i] = item
        //                        regViewModel.titles.value = titles
        //                        break
        //                    }
        //                    i++
        //                }
        //            }
        //            spinnerAdapter.setList(regViewModel.titles.value!!)
        //        })


        val titles = resources.getStringArray(R.array.titles)
        val titleText = getTitleText()
        if (!TextUtils.isEmpty(titleText)) {
            for (i in titles.indices) {
                if (titles[i] == titleText) {
                    titleSpinner.setSelection(i)
                    break
                }
            }
        }
        titleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val text = titles[position]
                LogUtil.i("titleEntity text=$text")
                regViewModel.titleText.value = text
                regViewModel.getTitleEntity(text)

                // 其他
                if (text == "其他" || text == "OTHERS") {
                    binding.etTitleOther.visibility = View.VISIBLE
                } else {
                    binding.etTitleOther.visibility = View.GONE
                    regViewModel.titleOther.value = ""
                    binding.etTitleOther.text = null
                }
            }
        }
        binding.etTitleOther.addTextChangedListener {
            regViewModel.titleOther.value = it.toString()
        }


    }

    private fun initSpinnerFunction() {
        val spinner = binding.spinnerFunction
        val functions = resources.getStringArray(R.array.departments)
        val functionText = getFunctionText()
        if (!TextUtils.isEmpty(functionText)) {
            for (i in functions.indices) {
                if (functions[i] == functionText) {
                    spinner.setSelection(i)
                    break
                }
            }
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val text = functions[position]
                LogUtil.i("titleEntity text=$text")
                regViewModel.functionText.value = text
                regViewModel.getFunctionEntity(text)

                // 其他
                if (text == "其他" || text == "OTHERS") {
                    binding.etDepOther.visibility = View.VISIBLE
                } else {
                    binding.etDepOther.visibility = View.GONE
                    regViewModel.functionOther.value = ""
                    binding.etDepOther.text = null
                }
            }
        }
        binding.etDepOther.addTextChangedListener {
            regViewModel.functionOther.value = it.toString()
        }

    }

    /**
     * 公司业务  “其他”输入框集合
     */
//    private var otherCollections: MutableList<String> = mutableListOf()
    private var otherCollections: MutableList<RegOptionData> = mutableListOf()

    private fun setServiceOthersView(kv: KV) {
        LogUtil.i("hint=${kv.K}")
        val regOptionData = kv.K as RegOptionData
        val editText: EditText?
        if (kv.V as Boolean) { // add
            editText = EditText(context)
            editText.hint = regOptionData.getName()
            editText.textSize = 14f
            editText.setSingleLine()
            binding.llProductOthers.addView(editText)
            otherCollections.add(regOptionData)

            // set sp save value, if has
            val spOther = getServiceOther(regOptionData.DetailCode)
            if (!TextUtils.isEmpty(spOther)) {
                editText.setText(spOther)
            }
            LogUtil.i("edit DetailCode= ${regOptionData.DetailCode}")

            editText.addTextChangedListener { text ->
                regViewModel.setServiceOtherValue(regOptionData.DetailCode, text.toString())
            }
        } else { // sub
            for (i in 0 until otherCollections.size) {
                if (otherCollections[i].DetailCode == (kv.K as RegOptionData).DetailCode) {
                    binding.llProductOthers.removeViewAt(i)
                    otherCollections.removeAt(i)
                    regViewModel.setServiceOtherValue(regOptionData.DetailCode, "")
                    break
                }
            }
        }
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    private fun initView() {
//        binding.etName.error = getString(R.string.reg_help_name)
//        val colors = IntArray(2)
//        colors[0] = resources.getColor(R.color.grey_500)
//        colors[1] = resources.getColor(R.color.grey_500)
//        val states = IntArray(2)
//        states[0] = android.R.attr.state_enabled
//        states[0] = android.R.attr.state_focused
//        binding.inputPwd.error = getString(R.string.reg_help_pwd)
//        binding.inputPwd.setErrorTextColor(ColorStateList(arrayOf(states), colors))
//        binding.inputPwd.isErrorEnabled = true
    }

    override fun onPause() {
        super.onPause()

        regViewModel.saveFiledToSP()

        LogUtil.i("serviceOtherCar=${regViewModel.serviceOtherCar.value.toString()}")
        LogUtil.i("serviceOtherBuild=${regViewModel.serviceOtherBuild.value}")
        LogUtil.i("serviceOtherCosme=${regViewModel.serviceOtherCosme.value}")
        LogUtil.i("serviceOtherEE=${regViewModel.serviceOtherEE.value}")
        LogUtil.i("serviceOtherLED=${regViewModel.serviceOtherLED.value}")
        LogUtil.i("serviceOtherPackage=${regViewModel.serviceOtherPackage.value}")
        LogUtil.i("serviceOtherMedical=${regViewModel.serviceOtherMedical.value}")
        LogUtil.i("serviceOtherText=${regViewModel.serviceOtherText.value}")


    }

    override fun onResult(
        combineText: String,
        countryEntity: CountryJson?,
        provinceEntity: CountryJson?,
        cityEntity: CountryJson?,
        tellRegionCode: String
    ) {
        LogUtil.i(" combineText=$combineText,   countryEntity=$countryEntity,\n provinceEntity=$provinceEntity \n cityEntity=$cityEntity \ntellRegionCode=$tellRegionCode ")

        regViewModel.regionText.value = combineText
        regViewModel.tellRegionCode.value = tellRegionCode
//        regViewModel.dialogRegion.value = countryEntity?.getName()

        countryEntity?.let {
            setRegion(countryEntity.Code)
        }
        provinceEntity?.let {
            setProvince(provinceEntity.Code)
        }
        cityEntity?.let {
            regViewModel.postCity.value = cityEntity.getName()
            setCity(cityEntity.Code)
        }
        setTellData(1, tellRegionCode)
        setRegionCombineText(combineText)

    }


}
