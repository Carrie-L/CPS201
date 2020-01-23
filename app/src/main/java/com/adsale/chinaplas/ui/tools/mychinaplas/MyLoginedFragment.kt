package com.adsale.chinaplas.ui.tools.mychinaplas

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.adsale.chinaplas.R
import com.adsale.chinaplas.databinding.DialogDownloadProgressBinding
import com.adsale.chinaplas.localConfirmPdfPath
import com.adsale.chinaplas.network.PAY_URL
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.viewmodels.DOWN_FINISH
import com.adsale.chinaplas.viewmodels.DOWN_START
import com.adsale.chinaplas.viewmodels.RegisterConfirmViewModel
import java.io.File

/**
 * [我的登记信息]
 * @本地有确认信, [打开PDF]
 * @本地无确认信，[下载PDF]
 * @未支付，[跳转到预登记表单页]
 */
class MyLoginedFragment : Fragment() {

    companion object {
        fun newInstance() = MyLoginedFragment()
    }

    private lateinit var viewModel: RegisterConfirmViewModel
    private lateinit var webView: WebView
    private lateinit var settings: WebSettings
    private var pdfPath: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_web_view, container, false)
        webView = view.findViewById(R.id.web_view)
        settings = webView.settings
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RegisterConfirmViewModel::class.java)

        val filePath = String.format(viewModel.confirmPath, CONFIRM_PDF_CHINAPLAS)
        pdfPath = "${localConfirmPdfPath}$filePath"
        LogUtil.i("pdfPath=$pdfPath")

        if (!getMyChinaplasIsPay()) {
            navigate()
        } else {
            if (File(filePath).exists()) {
                settingPDF()
                webView.loadUrl(pdfPath)
            } else {
                downPDF()
            }
        }
    }

    private fun downPDF() {
        viewModel.isRegister = false
        viewModel.startDownload(getMyChinaplasGuid())
        showDownloadDialog()
    }

    private fun navigate() {
        alertDialogConfirmTwo(context!!, R.string.chinaplas_pay_str1, DialogInterface.OnClickListener { dialog, which ->
            findNavController().navigate(MyLoginedFragmentDirections.actionMyLoginedFragmentToRegisterFragment(
                getMyChinaplasPhone(), getMyChinaplasEmail()))
//            findNavController().navigate(MyLoginedFragmentDirections.actionMyLoginedFragmentToRegisterWebsiteFragment(
//                String.format(PAY_URL, getLangStr(), getMyChinaplasGuid()), false))
        })
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
    }

    private fun showDownloadDialog() {
        var dialog: AlertDialog? = null
        viewModel.isDownStatus.observe(this, Observer { status ->
            when (status) {
                DOWN_START -> {
                    LogUtil.i("showDownloadDialog: start download----------")
                    val dialogBinding = DialogDownloadProgressBinding.inflate(LayoutInflater.from(context))
                    dialogBinding.viewModel = viewModel
                    dialogBinding.lifecycleOwner = this
                    dialog = AlertDialog.Builder(context).setView(dialogBinding.root).show()
                    dialog?.setCancelable(false)
                    dialogBinding.dialogProgress.isIndeterminate = true
                    viewModel.downloadPDF()
                }
                DOWN_FINISH -> {
                    dialog?.dismiss()
                    settingPDF()
                    webView.loadUrl(pdfPath)
                    LogUtil.i("${pdfPath}")
                }
            }
        })
    }


}
