package com.adsale.chinaplas.ui.register


import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.adsale.chinaplas.*
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.data.dao.RegisterRepository
import com.adsale.chinaplas.databinding.DialogDownloadProgressBinding
import com.adsale.chinaplas.databinding.FragmentRegisterWebsiteBinding
import com.adsale.chinaplas.network.CONFIRM_URL
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.utils.LogUtil.i
import com.adsale.chinaplas.viewmodels.*
import com.pingplusplus.android.Pingpp
import com.tencent.bugly.Bugly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

/**
 * 功能：
 * 1. 加载确认信
 * 2. 下载确认信
 * 3. 打开确认信pdf
 *
 *
 */
class RegisterWebsiteFragment : BaseFragment() {
    private lateinit var binding: FragmentRegisterWebsiteBinding
    private lateinit var webview: WebView
    private lateinit var settings: WebSettings
    private lateinit var progressBar: ProgressBar
    private var url: String = ""
    private lateinit var viewModel: RegisterViewModel
    private lateinit var confirmViewModel: RegisterConfirmViewModel
    private var charge: String = ""
    private var lang: String = getLangStr()
    private var failCount = 0

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private var loadingDialog: Dialog? = null
    /*  下载完pdf后，打开pdf */
    private var isOpenPDF = false

    private val testPDFUrl =
        "https://prereg.adsaleonline.com/Prereg/PreregPDF/SubmitAction/Preregland?encryptKey=4e1RVd%2bgH8CRpCPeuticjHIPzzWnp3h0HjwC7MJ0U77%2fHsARcMM0fCCii%2bonkke3eZsaJJQi8F6%2bfFg8SeeQkg%3d%3d"

    override fun initedView(inflater: LayoutInflater) {
        binding = FragmentRegisterWebsiteBinding.inflate(inflater,baseFrame,true)
        webview = binding.webviewRegPay
        progressBar = binding.progressBar
        settings = webview.settings
    }

    override fun initView() {
    }

    override fun initedData() {
    }

    override fun initData() {
        isBackCustom = true
        val regRepo = RegisterRepository.getInstance(cpsDatabase.countryDao(), cpsDatabase.regOptionDao())
        viewModel = ViewModelProviders.of(requireActivity(), RegViewModelFactory(regRepo))
            .get(RegisterViewModel::class.java)
        confirmViewModel = ViewModelProviders.of(this).get(RegisterConfirmViewModel::class.java)

        arguments?.let {
            url = RegisterWebsiteFragmentArgs.fromBundle(it).url ?: ""
            confirmViewModel.isRegister = RegisterWebsiteFragmentArgs.fromBundle(it).isRegister
        }

        if (TextUtils.isEmpty(url)) {
            return
        }
        i("---url--- = $url")

        showDownloadDialog()

        if (url.contains("guid=")) {
            setGuid(url.substringAfterLast("=", "="))
            url += "&device=mobileapp"
        } else if (url.contains("PreregPDF")) {
            confirmViewModel.isDownStatus.value = DOWN_START
            isOpenPDF = true
        }

        webviewSetting()
        setWebViewClient()
        if (url.contains("pdfjs")) {
            settingPDF()  // 打开PDF
        }
        webview.loadUrl(url)

        binding.btnLogoutConfirm.setOnClickListener {
            alertDialogConfirmTwo(context!!,
                R.string.logout_confirm_letter,
                DialogInterface.OnClickListener { dialog, which ->
                    // todo 刪除pdf。 重新跳轉預登記頁. 取消登录
                    putLogin(false)
                    paySuccess(false)
                    deletePDF()
                    NavHostFragment.findNavController(this).navigate(R.id.registerPreFragment)
                })

        }
    }

    private fun deletePDF() {
        val file = File(confirmViewModel.confirmPath())
        if (file.exists()) {
            val siDelete = file.delete()
            i("siDelete=$siDelete, confirmPdfPath=${confirmViewModel.confirmPath()}")
        }
    }


    private fun webviewSetting() {
        settings.javaScriptEnabled = true
        webview.settings.domStorageEnabled = true
    }

    /**
     * 打开PDF
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun settingPDF() {
        settings.javaScriptEnabled = true
        settings.allowFileAccess = true
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true
        binding.btnLogoutConfirm.visibility = View.VISIBLE
    }

    private fun setWebViewClient() {
        webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                progressBar.visibility = View.VISIBLE
                super.onPageStarted(view, url, favicon)
                i("onPageStarted: url=$url")
                if (url != null && url.contains("PayAPPjump")) {
                    getJSValue(view, url)
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                super.shouldOverrideUrlLoading(view, request)
                i("shouldOverrideUrlLoading: url=${request.url}")
                return false
            }

            override fun onPageFinished(view: WebView, url: String?) {
                progressBar.visibility = View.GONE
                super.onPageFinished(view, url)
                i("onPageFinished: url=$url")
                if (url != null) {
                    if (isConfirmUrl(url)) {
                        loadingDialog?.dismiss()
                        paySuccess(true)
                        downloadConfirmPdf()
                    }
//                    else if (url.contains("PreregPDF")) {  // 直接根据确认信pdf链接下载
//                        confirmViewModel.isDownStatus.value = DOWN_START
//                    }
                }
            }
        }
    }

    private fun downloadConfirmPdf() {
        confirmViewModel.startDownload(getGuid())
    }

    private fun getJSValue(view: WebView, url: String) {
        view.evaluateJavascript("document.getElementById('p_payMethod').value+','" +
                "+document.getElementById('g_guid').value+','" +
                "+document.getElementById('p_lang').value+','" +
                "+document.getElementById('FECodeAPP').value+','" +
                "+document.getElementById('FECodeAPP_CODE').value;"
        ) {
            i("value=$it")
            if (it != "null") {
                val value = it.replace("\"", "").split(",")
                // todo ping++ 检测是否有微信 支付宝 客户端
                if (value.isNotEmpty()) {
                    setGuid(value[1])
                    lang = getLangStr(value[2])
                    uiScope.launch {
                        charge = confirmViewModel.apiGetCharge(value[0], value[1], value[2], value[3], value[4])
                        invokePingPay(charge)
                    }
                }
            } else {
                uiScope.launch {
                    val guid = subMiddle(url, "g=", "&")
                    val method = url.substringAfterLast("p=", "")
                    val l = subMiddle(url, "l=", "&")
                    i("guid=$guid, method=$method, l = $l")
                    charge = confirmViewModel.apiGetCharge(method, guid, l, "", "")
                    invokePingPay(charge)
                }
            }

        }
    }

    private fun invokePingPay(charge: String) {
        if (TextUtils.isEmpty(charge))
            return
        Pingpp.createPayment(this, charge)
    }

    /**
     * ping++支付结果返回处理
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT && resultCode == Activity.RESULT_OK) {
            val result = data!!.extras!!.getString("pay_result")
            /* 处理返回值
             * "success" - payment succeed
             * "fail"    - payment failed
             * "cancel"  - user canceld
             * "invalid" - payment plugin not installed
             */
            val errorMsg = data.extras!!.getString("error_msg") // 错误信息
            val extraMsg = data.extras!!.getString("extra_msg") // 错误信息
            i("result=$result, errorMsg=$errorMsg, extraMsg=$extraMsg")
            if (result!!.contains("success")) { /*  */
                // 1. 再次请求是否支付成功
                // 2. 下载确认信pdf
                processPayResponse()
            } else if (result.contains("fail")) { /* 支付失败，重新调起支付 */
                if (failCount < 5) {
                    invokePingPay(charge)
                    failCount++
                } else {
                    failCount = 0
                }
            } else if (result == "cancel") {
                Toast.makeText(context!!, "取消支付", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processPayResponse() {
        loadingDialog = alertDialogProgress(context!!, "加载确认信...")
        uiScope.launch {
            val isSuccess = confirmViewModel.apiPaySuccess()
            i("isPaySuccess=$isSuccess, regGuid=${getGuid()}, cpsGuid=${getMyChinaplasGuid()}")
            if (isSuccess) {
                if (!confirmViewModel.isRegister) {
                    setMyChinaplasIsPay(true)
                } else {
                    paySuccess(true)
                }
                webview.loadUrl(String.format(CONFIRM_URL, lang, getGuid()))
            }
        }
    }

    private fun showDownloadDialog() {
        var dialog: AlertDialog? = null
        confirmViewModel.isDownStatus.observe(this, Observer { status ->
            when (status) {
                DOWN_START -> {
                    i("showDownloadDialog: start download----------")
                    val dialogBinding = DialogDownloadProgressBinding.inflate(LayoutInflater.from(context))
                    dialogBinding.viewModel = confirmViewModel
                    dialogBinding.lifecycleOwner = this
                    dialog = AlertDialog.Builder(context)
                        .setView(dialogBinding.root)
                        .show()
                    dialogBinding.dialogProgress.isIndeterminate = true
                    uiScope.launch {
                        confirmViewModel.downloadPDFAsync()
                    }
                }
                DOWN_FINISH -> {
                    dialog?.dismiss()
                    mSPReg.edit().putBoolean("ComfirmPDFDownload", true).apply()
                    settingPDF()
                    webview.loadUrl(localConfirmPdfPath + confirmViewModel.confirmPath())
                }
            }
        })
    }

    private fun isConfirmUrl(url: String): Boolean {
        return url.contains("PreregSuccess") && url.contains("guid=")
    }

    private fun setProgressClient() {
        i("setProgressClient")
        webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                i("newProgress=$newProgress")
                if (newProgress >= 100) {
                    progressBar.visibility = View.GONE
                } else {
                    if (progressBar.visibility == View.GONE) {
                        progressBar.visibility = View.VISIBLE
                    }
                    progressBar.progress = newProgress
                }
                super.onProgressChanged(view, newProgress)
            }
        }
    }

    override fun back() {
        if (confirmViewModel.isRegister) {
            val registerFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.registerFragment)
            if(registerFragment!=null){
                findNavController().popBackStack(R.id.registerFragment,false)
            }else{
                findNavController().popBackStack(R.id.nav_home, false)
            }
        } else {
            findNavController().popBackStack(R.id.myLoginedFragment, false)
        }
        mainViewModel.resetBackDefault()
    }


}
