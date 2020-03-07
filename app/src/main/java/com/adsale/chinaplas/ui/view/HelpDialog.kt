package com.adsale.chinaplas.ui.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.adsale.chinaplas.R
import com.adsale.chinaplas.databinding.LayoutHelpBinding
import com.adsale.chinaplas.isTablet
import com.adsale.chinaplas.utils.*
import com.bumptech.glide.Glide

/**
 * Created by Carrie on 2020/3/5.
 */
const val HELP_EXHIBITOR_LIST = 1001
const val HELP_MY_EXHIBITOR = 1002
const val HELP_MY_CHINAPLAS = 1003
const val HELP_SCHEDULE_LIST = 1004
const val HELP_SCHEDULE_EDIT = 1005
const val HELP_EVENT_LIST = 1006
const val HELP_EVENT_DTL = 1007
const val HELP_NEW_TECH = 1008
//const val HELP_=100


class HelpDialog(private val type: Int) : DialogFragment() {
    private lateinit var binding: LayoutHelpBinding
    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.transparentBgDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.let {
            it.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            /* Make us non-modal, so that others can receive touch events.
            说人话就是 取消默认的 对话框外部区域点击取消对话框事件，而是换成响应事件，不能理解则注释下面两端代码运行看效果。 */
            it.window!!.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            // ...but notify us that it happened.
            it.window!!.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)

            LogUtil.i("init RegProductDialog~~")
            binding = LayoutHelpBinding.inflate(LayoutInflater.from(requireActivity()), container, true)
            mView = binding.root
        }
        return mView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isTablet) {
            setPadWindow()
        } else {
            setWindow()
        }

        binding.btnHelpPageClose.setOnClickListener {
            dismiss()
        }

        val image = when (type) {
            HELP_EVENT_DTL -> getEventDtlImage()
            HELP_EVENT_LIST -> getEventListImage()
            HELP_MY_EXHIBITOR -> getMyExhibitor()
            else -> 0
        }
        Glide.with(requireContext()).load(image).into(binding.imageHelp)
    }

    private fun getEventDtlImage(): Int {
        return when (getCurrLanguage()) {
            LANG_EN -> R.drawable.help_event_dtl_en
            LANG_SC -> R.drawable.help_event_dtl_sc
            else -> R.drawable.help_event_dtl_tc
        }
    }

    private fun getEventListImage(): Int {
        return when (getCurrLanguage()) {
            LANG_EN -> R.drawable.help_eventlist_en
            LANG_SC -> R.drawable.help_eventlist_sc
            else -> R.drawable.help_eventlist_tc
        }
    }

    private fun getMyExhibitor(): Int {
        return if (isTablet) {
            when (getCurrLanguage()) {
                LANG_EN -> R.drawable.help_myexhibitor_en
                LANG_SC -> R.drawable.help_myexhibitor_sc
                else -> R.drawable.help_myexhibitor_tc
            }
        } else {
            when (getCurrLanguage()) {
                LANG_EN -> R.drawable.help_myexhibitor_en
                LANG_SC -> R.drawable.help_myexhibitor_sc
                else -> R.drawable.help_myexhibitor_tc
            }
        }
    }


    private fun setWindow() {
        val window = dialog!!.window
        window!!.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val wl = window.attributes
        wl.x = 0
        wl.y = 0
        wl.height = getDisplayHeight()
        wl.width = getScreenWidth()
        wl.gravity = Gravity.TOP
        window.attributes = wl
        mView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun setPadWindow() {
        val window = dialog!!.window!!
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT) // 加了这个就闪现一个顶部黑条, 不加7.1平板帮助页就无法全屏显示
        mView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    companion object {
        @Volatile
        private var instance: HelpDialog? = null

        fun getInstance(type: Int) =
            instance ?: synchronized(this) {
                instance ?: HelpDialog(type).also { instance = it }
            }
    }


}