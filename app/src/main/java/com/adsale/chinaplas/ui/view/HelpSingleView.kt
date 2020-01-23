//package com.adsale.chinaplas.ui.view
//
//import android.content.Context
//import android.os.Bundle
//import android.view.*
//import android.widget.ImageView
//import androidx.fragment.app.DialogFragment
//import com.adsale.chinaplas.R
//import com.adsale.chinaplas.databinding.ViewHelpSingleBinding
//import com.adsale.chinaplas.utils.LANG_SC
//import com.adsale.chinaplas.utils.getCurrLanguage
//import com.adsale.chinaplas.utils.isTablet
//import com.bumptech.glide.Glide
//
///**
// * Created by Carrie on 2020/1/20.
// */
//const val HELP_PAGE_MAIN: Int = 0
//const val HELP_PAGE_EXHIBITOR_DTL = 1
//const val HELP_PAGE_EVENT_DTL = 2
//const val HELP_PAGE_FLOOR_OVERALL = 3
//const val HELP_PAGE_FLOOR_DTL = 4
//const val HELP_PAGE_MY_EXHIBITOR = 5
//const val HELP_PAGE_SCANNER = 6
//const val HELP_PAGE_SCHEDULE = 7
//const val HELP_PAGE_SCHEDULE_DTL = 8
//const val HELP_PAGE_EVENT_LIST = 9
//const val HELP_PAGE_EXHIBITOR_LIST = 10
//const val HELP_PAGE_NEW_TEC_LIST = 11
//const val HELP_PAGE_NAMECARD_LIST = 12
//const val HELP_PAGE_MY_NAMECARD = 13
//
//const val HELP_PAGE = "HELP_PAGE_"
//
//class HelpSingleView : DialogFragment() {
//    private lateinit var binding: ViewHelpSingleBinding
//    private lateinit var ivHelp: ImageView
//    private var mPageType: String = ""
//
//    private fun init(context: Context) {
//        val inflater = LayoutInflater.from(context)
//        binding = ViewHelpSingleBinding.inflate(inflater, this, true)
//
//        binding.btnHelpPageClose.setOnClickListener {
//            visibility = View.GONE
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
//        binding = ViewHelpSingleBinding.inflate(inflater, container, false)
//        initView()
//
//        when(getCurrLanguage()){
//            LANG_SC ->
//        }
//        if (language == 0) {
//            getImageIdsTC()
//        } else if (language == 1) {
//            getImageIdsEN()
//        } else {
//            getImageIdsSC()
//        }
//        return binding.root
//
//
//        return super.onCreateView(inflater, container, savedInstanceState)
//    }
//
//
//    private fun setPadWindow() {
//        window = dialog!!.window
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
//        window.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.MATCH_PARENT
//        ) // 加了这个就闪现一个顶部黑条, 不加7.1平板帮助页就无法全屏显示
//        mView.systemUiVisibility =
//            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        if (isTablet()) {
//            setPadWindow()
//        } else {
//            setPhoneWindow()
//        }
//    }
//
//
//    private fun setPhoneWindow() {
//        window = dialog!!.window
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
//        val wl: WindowManager.LayoutParams = window.getAttributes()
//        wl.x = 0
//        wl.y = 0
//        wl.height = AppUtil.getScreenHeight()
//        wl.width = AppUtil.getScreenWidth()
//        wl.gravity = Gravity.TOP
//        window.setAttributes(wl)
//        mView.systemUiVisibility =
//            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//    }
//
//    fun getHelpPage(type: String) {
//        mPageType = type
//        when (getCurrLanguage()) {
//            LANG_SC -> Glide.with(context)
//        }
//    }
//
//    private fun getImageIdsSC(): Int {
//        if (isTablet()) {
//            getImageIdsSCPad()
//            return
//        }
//        when (mPageType) {
//            HELP_PAGE_EXHIBITOR_DTL -> {
////                mLogHelper.logHelp(LogHelper.HELP_EXHIBITOR_INFO)
////                mLogHelper.setBaiDuLog(mView.getContext(), LogHelper.EVENT_ID_HELP)
//                R.drawable.help_exhibitordtl_0_sc
//            }
//            HELP_PAGE_EVENT_DTL -> {
//                R.drawable.help_event_0_sc
////                mLogHelper.logHelp(LogHelper.HELP_EVENT_INFO)
////                mLogHelper.setBaiDuLog(mView.getContext(), LogHelper.EVENT_ID_HELP)
//            }
//            HELP_PAGE_EVENT_LIST -> {
//                R.drawable.help_eventlist_0_sc
////                mLogHelper.logHelp(LogHelper.HELP_EVENT_LIST)
////                mLogHelper.setBaiDuLog(mView.getContext(), LogHelper.EVENT_ID_HELP)
//            }
//            HELP_PAGE_FLOOR_DTL -> {
//                R.drawable.help_floordtl_0_sc
////                mLogHelper.logHelp(LogHelper.HELP_FLOOR_PLAN)
////                mLogHelper.setBaiDuLog(mView.getContext(), LogHelper.EVENT_ID_HELP)
//            }
//            HELP_PAGE_MY_EXHIBITOR -> {
//                R.drawable.help_myexhibitor_0_sc
////                mLogHelper.logHelp(LogHelper.HELP_MY_CHINAPLAS)
////                mLogHelper.setBaiDuLog(mView.getContext(), LogHelper.EVENT_ID_HELP)
//            }
//            HELP_PAGE_SCANNER -> {
//                R.drawable.help_scanner_0_sc
////                mLogHelper.logHelp(LogHelper.HELP_SCANNER)
////                mLogHelper.setBaiDuLog(mView.getContext(), LogHelper.EVENT_ID_HELP)
//            }
//            HELP_PAGE_SCHEDULE -> {
//                R.drawable.help_schedule_0_sc
////                mLogHelper.logHelp(LogHelper.HELP_SCHEDULE_LIST)
////                mLogHelper.setBaiDuLog(mView.getContext(), LogHelper.EVENT_ID_HELP)
//            }
//            HELP_PAGE_SCHEDULE_DTL -> {
//                R.drawable.help_schedule_edit_0_sc
////                mLogHelper.logHelp(LogHelper.HELP_SCHEDULE_INFO)
////                mLogHelper.setBaiDuLog(mView.getContext(), LogHelper.EVENT_ID_HELP)
//            }
//            HELP_PAGE_EXHIBITOR_LIST -> {
//                R.drawable.help_exhibitorlist_0_sc
////                mLogHelper.logHelp("ExhibitorList")
////                mLogHelper.setBaiDuLog(mView.getContext(), LogHelper.EVENT_ID_HELP)
//            }
//            HELP_PAGE_NAMECARD_LIST -> {
//                R.drawable.help_namecardlist_0_sc
////                mLogHelper.logHelp("NameCardList")
////                mLogHelper.setBaiDuLog(mView.getContext(), LogHelper.EVENT_ID_HELP)
//            }
//            HELP_PAGE_MY_NAMECARD -> {
//                R.drawable.help_mynamecard_0_sc
////                mLogHelper.logHelp("MyNameCard")
////                mLogHelper.setBaiDuLog(mView.getContext(), LogHelper.EVENT_ID_HELP)
//            }
//            HELP_PAGE_NEW_TEC_LIST -> {
//                R.drawable.help_newtec_0_sc
////                mLogHelper.logHelp("NewTechList")
////                mLogHelper.setBaiDuLog(mView.getContext(), LogHelper.EVENT_ID_HELP)
//            }
//        }
//    }
//
//    private fun getImageIdsSCPad(): Int {
//        return when (mPageType) {
//            HELP_PAGE_EXHIBITOR_DTL ->
//                R.drawable.help_exhibitordtl_0_sc_pad
//            else -> 0
//        }
//    }
//
//
//}