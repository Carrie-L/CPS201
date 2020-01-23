package com.adsale.chinaplas.ui.drawer

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.databinding.FragmentSettingBinding
import com.adsale.chinaplas.network.CPS_URL
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
        ViewModelProviders.of(
            requireActivity(), MainViewModelFactory(requireActivity().application,
                MainIconRepository.getInstance(CpsDatabase.getInstance(applicationContext).mainIconDao())
            )
        )
            .get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater)
        settingViewModel = ViewModelProviders.of(this)[SettingViewModel::class.java]
        binding.viewModel = settingViewModel
        binding.executePendingBindings()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogUtil.i("onActivityCreated")
        binding.tvLanguage.setOnClickListener {
            alertLanguage()
        }
        binding.tvShare.setOnClickListener {
            onShare()
        }
        binding.tvWebsite.setOnClickListener {
            onLinkWebsite()
        }
        binding.tvReset.setOnClickListener {
            onResetAll()
        }
        binding.tvPrivacy.setOnClickListener {
            onPrivacy()
        }
        binding.tvItems.setOnClickListener {
            onUseItems()
        }
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

    private fun onShare() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_setting_text))
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun onLinkWebsite() {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val content_url =
            Uri.parse(String.format(CPS_URL, getLangStr()))
        intent.data = content_url
        startActivity(intent)
    }

    private fun onResetAll() {
        alertDialogTwoButton(context!!,
            R.string.ask_clear,
            R.string.yes,
            R.string.no,
            DialogInterface.OnClickListener { dialog, which ->
                // todo 清空我的日程表
//                val scheduleRepository: ScheduleRepository = ScheduleRepository.getInstance()
//                scheduleRepository.clearAll()

                // todo 清空我的参展商（本地）| 清空兴趣展商 : 查找Exhibitor表中isFavourite=1的数据，删除
//                val exhibitorRepository: ExhibitorRepository = ExhibitorRepository.getInstance()
//                exhibitorRepository.cancelMyExhibitor()
//                exhibitorRepository.clearInterestedExhibitor()

                // 退出登录
                resetLoginInfo()
                mainViewModel.isLogin.value = false

                // todo 预登记资料
                putLogin(false)
                paySuccess(false)
            })
    }

    private fun onPrivacy() {
        findNavController().navigate(SettingFragmentDirections.actionNavSettingToWebContentFragment(PRIVACY,
            getString(R.string.title_privacy)))
    }

    private fun onUseItems() {
        findNavController().navigate(SettingFragmentDirections.actionNavSettingToWebContentFragment(USE_ITEM,
            getString(R.string.title_user_item)))
    }


}