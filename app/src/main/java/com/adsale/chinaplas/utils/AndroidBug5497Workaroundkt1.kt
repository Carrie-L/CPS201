package com.adsale.chinaplas.utils

/**
 * Created by Carrie on 2019/11/18.
 */


import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * Created by Administrator on 2016/3/3.
 */
class AndroidBug5497Workaroundkt1 private constructor(activity: Activity, layoutId: Int) {

    private val mChildOfContent: View
    private var usableHeightPrevious: Int = 0
    private val frameLayoutParams: ConstraintLayout.LayoutParams

    init {
        val content = activity.findViewById<View>(layoutId) as ConstraintLayout
        mChildOfContent = content.getChildAt(0)
        mChildOfContent.viewTreeObserver.addOnGlobalLayoutListener { possiblyResizeChildOfContent() }
        frameLayoutParams = mChildOfContent.layoutParams as ConstraintLayout.LayoutParams
    }

    private fun possiblyResizeChildOfContent() {
        val usableHeightNow = computeUsableHeight()

        LogUtil.i("usableHeightNow=$usableHeightNow")
        LogUtil.i("usableHeightPrevious=$usableHeightPrevious")

        if (usableHeightNow != usableHeightPrevious) {
            val usableHeightSansKeyboard = mChildOfContent.rootView.height
            LogUtil.i("usableHeightSansKeyboard=$usableHeightSansKeyboard")
            val heightDifference = usableHeightSansKeyboard - usableHeightNow
            LogUtil.i("heightDifference=$heightDifference")
            if (heightDifference > usableHeightSansKeyboard / 4) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference - 300
                LogUtil.i(">1 =${frameLayoutParams.height}")
            } else {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightNow
                LogUtil.i("2 =$usableHeightSansKeyboard")
            }
            mChildOfContent.requestLayout()
            usableHeightPrevious = usableHeightNow
        }
    }

    private fun computeUsableHeight(): Int {
        val r = Rect()
        mChildOfContent.getWindowVisibleDisplayFrame(r)

        LogUtil.i("r.bottom =${r.bottom}, r.top=${r.top}")
        return r.bottom - r.top + 100
    }

    companion object {

        // For more information, see https://code.google.com/p/android/issues/detail?id=5497
        // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

        fun assistActivity(activity: Activity, layoutId: Int) {
            AndroidBug5497Workaroundkt1(activity, layoutId)
        }
    }
}
