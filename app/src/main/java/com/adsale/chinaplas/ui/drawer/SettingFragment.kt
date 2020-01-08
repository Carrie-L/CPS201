package com.adsale.chinaplas.ui.drawer

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.databinding.FragmentSettingBinding
import com.adsale.chinaplas.ui.home.MainActivity
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory
import com.adsale.chinaplas.viewmodels.SettingViewModel
import com.tencent.bugly.Bugly.applicationContext
import java.util.*

class SettingFragment : Fragment() {

    private lateinit var settingViewModel: SettingViewModel
    private lateinit var binding: FragmentSettingBinding

    val mainViewModel: MainViewModel by lazy {
        val activity = requireNotNull(this) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProviders.of(
            requireActivity(), MainViewModelFactory(
                requireActivity().application,
                MainIconRepository.getInstance(CpsDatabase.getInstance(applicationContext).mainIconDao())
            )
        )
            .get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater)
        settingViewModel = ViewModelProviders.of(this)[SettingViewModel::class.java]
        binding.viewModel = settingViewModel
        binding.executePendingBindings()

//        val root = inflater.inflate(R.layout.fragment_setting, container, false)
//        val textView: TextView = root.findViewById(R.id.text_tools)
//        toolsViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
//        LogUtil.i("onCreateView")


        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.i("onCreate")

//        activity?.run {
//            LogUtil.i("onCreate mainViewModel")
//            mainViewModel = ViewModelProviders.of(requireParentFragment())[MainViewModel::class.java]
//        }


    }

    private fun alertLanguage() {
        val builder = AlertDialog.Builder(context!!)
        val languages = arrayOf("English", "繁體中文", "简体中文")
        builder.setItems(languages) { _, i ->
            val language = if (i == 0) LANG_EN else if (i == 1) LANG_TC else LANG_SC
            setCurrLanguage(language)
//            mainViewModel.language.value = language
            settingViewModel.langClick.value = false
            switchLanguage(context!!, language)
        }.create().show()
    }


    /**
     * @param mContext
     * @param language 0:ZhTw; 1:en;2:ZhCn;
     */
    private fun switchLanguage(mContext: Context, language: Int) {
        LogUtil.i("switchLanguage=$language")

        setCurrLanguage(language)
        val resources = mContext.resources
        val config = resources.configuration
        val dm = resources.displayMetrics
        val locale: Locale = getLocale(language)!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            mContext.createConfigurationContext(config)
        } else {
            config.setLocale(locale)
        }
        Locale.setDefault(locale)
        resources.updateConfiguration(config, dm)

        requireActivity().recreate()
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onStart() {
        super.onStart()
        LogUtil.i("onStart")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogUtil.i("onActivityCreated")

        settingViewModel.langClick.observe(this, Observer { isClick ->
            LogUtil.i("isClick=$isClick")

            if (isClick) {
                alertLanguage()
            }

        })
    }

    override fun onResume() {
        super.onResume()
        LogUtil.i("onResume")
    }

    override fun onStop() {
        super.onStop()
        LogUtil.i("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.i("onDestroy")
    }
}