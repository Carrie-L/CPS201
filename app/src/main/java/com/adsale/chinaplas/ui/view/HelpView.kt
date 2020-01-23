package com.adsale.chinaplas.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.AdViewPagerAdapter
import com.adsale.chinaplas.databinding.ViewHelpBinding
import com.adsale.chinaplas.utils.LANG_EN
import com.adsale.chinaplas.utils.LANG_SC
import com.adsale.chinaplas.utils.dp2px
import com.adsale.chinaplas.utils.getCurrLanguage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.util.*

/**
 * Created by Carrie on 2020/1/15.
 */
class HelpView : ConstraintLayout {
    private lateinit var binding: ViewHelpBinding
    private lateinit var mLlPoint: LinearLayout
    private lateinit var viewPager: ViewPager

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = ViewHelpBinding.inflate(inflater,this,true)
        mLlPoint = binding.vpindicator
        viewPager = binding.helpPager

        generatePage(context)

        binding.btnHelpPageClose.setOnClickListener {
            visibility = View.GONE
        }
    }

    private fun getMenuImages(): Array<Int> {
        return when (getCurrLanguage()) {
            LANG_SC -> arrayOf(R.drawable.help_1_tc, R.drawable.help_2_tc, R.drawable.help_3_tc, R.drawable.help_4_tc)
            LANG_EN -> arrayOf(R.drawable.help_1_en, R.drawable.help_2_en, R.drawable.help_3_en, R.drawable.help_4_en)
            else -> arrayOf(R.drawable.help_1_sc, R.drawable.help_2_sc, R.drawable.help_3_sc, R.drawable.help_4_sc)
        }
    }

    private fun generatePage(context: Context) {
        val helpPages: MutableList<View> = ArrayList()
        val imageIds = getMenuImages()
        val length: Int = imageIds.size
        //        /* 设置缓存策略为 不缓存，因为根据语言切换的不同，图片也不同，如果缓存了，切换语言后仍然会使用上一语言的图片 */
        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA)
        for (i in 0 until length) {
            val imageView = ImageView(context)
            imageView.adjustViewBounds = true
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            Glide.with(context).load(imageIds.get(i)).apply(requestOptions).into(imageView)
            helpPages.add(imageView)
        }
        setPoint(length, context)
        val mPagerAdapter = AdViewPagerAdapter(helpPages)
        viewPager.adapter = mPagerAdapter
        viewPager.addOnPageChangeListener(helpPageChangeListener)
    }

    private fun setPoint(length: Int, context: Context) {
        if (length > 1) { // 几个圆点
            val width: Int = dp2px(8f)
            val indParams = LinearLayout.LayoutParams(width, width)
            indParams.setMargins(width, width, 0, width * 2)
            var iv: ImageView
            mLlPoint.removeAllViews()
            for (i in 0 until length) {
                iv = ImageView(context)
                if (i == 0) {
                    iv.setBackgroundResource(R.drawable.dot_focused)
                } else iv.setBackgroundResource(R.drawable.dot_normal)
                iv.layoutParams = indParams
                mLlPoint.addView(iv)
            }
        }
    }

    private val helpPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrollStateChanged(pos: Int) {}
        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
        // 监听页面改变事件来改变viewIndicator中的指示图片
        override fun onPageSelected(arg0: Int) {
            showDot(arg0)
        }
    }

    private fun showDot(currPos: Int) {
        val len = mLlPoint.childCount
        for (i in 0 until len) mLlPoint.getChildAt(i).setBackgroundResource(R.drawable.dot_normal)
        mLlPoint.getChildAt(currPos).setBackgroundResource(R.drawable.dot_focused)
    }
}