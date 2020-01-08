package com.adsale.chinaplas.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.webkit.WebView
import androidx.databinding.ObservableBoolean
import com.adsale.chinaplas.mSPReg
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Carrie on 2019/12/6.
 */
class CaptureScreen(val context: Context, private val webView: WebView) {

    init {

    }

    fun captureScreen():Boolean {
        val bitmap = getWebViewBitmap(webView)
        val imgWidth = bitmap!!.width
        val imgHeight = bitmap.height
        mSPReg.edit().putInt("imgWidth", imgWidth).putInt("imgHeight", imgHeight).apply()
        val imgFile = File(context.filesDir.absolutePath + "confirm.jpg")
        val os: FileOutputStream?
        try {
            os = FileOutputStream(imgFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, os)
            os.flush()
            os.close()
            return true
        }catch (e: Exception) {
            LogUtil.e("captureScreen exception: \n"+e.message)
        }
        webView.clearHistory()
       return false
    }

    /**
     * 获取 WebView 视图截图 * @param context * @param view * @return
     */
    private fun getWebViewBitmap(view: WebView?): Bitmap? {
        if (null == view) return null
        view.scrollTo(0, 0)
        view.buildDrawingCache(true)
        view.isDrawingCacheEnabled = true
        view.isVerticalScrollBarEnabled = false
        var b = getViewBitmapWithoutBottom(view)
        // 可见高度
        var vh = view.height
        // 容器内容实际高度
        val th = (view.contentHeight * view.scale).toInt()
        var temp: Bitmap?
        if (th > vh) {
            val w = getScreenWidth()
            val absVh = vh - view.paddingTop - view.paddingBottom
            do {
                val restHeight = th - vh
                if (restHeight <= absVh) {
                    view.scrollBy(0, restHeight)
                    vh += restHeight
                    temp = getViewBitmap(view)
                } else {
                    view.scrollBy(0, absVh)
                    vh += absVh
                    temp = getViewBitmapWithoutBottom(view)
                }
                b = mergeBitmap(vh, w, temp, 0f, view.scrollY.toFloat(), b, 0f, 0f)
            } while (vh < th)
        }
        // 回滚到顶部
        view.scrollTo(0, 0)
        view.isVerticalScrollBarEnabled = true
        view.isDrawingCacheEnabled = false
        view.destroyDrawingCache()
        return b
    }

    private fun getViewBitmap(v: View?): Bitmap? {
        if (null == v) {
            return null
        }
        v.isDrawingCacheEnabled = true
        v.buildDrawingCache()
        v.measure(View.MeasureSpec.makeMeasureSpec(v.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(v.height, View.MeasureSpec.EXACTLY))
        v.layout(v.x.toInt(),
            v.y.toInt(),
            v.x.toInt() + v.measuredWidth,
            v.y.toInt() + v.measuredHeight)
        val b = Bitmap.createBitmap(v.drawingCache, 0, 0, v.measuredWidth, v.measuredHeight)
        v.isDrawingCacheEnabled = false
        v.destroyDrawingCache()
        return b
    }

    private fun getViewBitmapWithoutBottom(v: View?): Bitmap? {
        if (null == v) {
            return null
        }
        v.isDrawingCacheEnabled = true
        v.buildDrawingCache()
        v.measure(View.MeasureSpec.makeMeasureSpec(v.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(v.height, View.MeasureSpec.EXACTLY))
        v.layout(v.x.toInt(),
            v.y.toInt(),
            v.x.toInt() + v.measuredWidth,
            v.y.toInt() + v.measuredHeight)
        val bp = Bitmap.createBitmap(v.drawingCache,
            0,
            0,
            v.measuredWidth,
            v.measuredHeight - v.paddingBottom)
        v.isDrawingCacheEnabled = false
        v.destroyDrawingCache()
        return bp
    }

    /**
     * 拼接图片 * @param newImageH * @param newImageW * @param background * @param backX * @param backY * @param foreground * @param foreX * @param foreY * @return
     */
    private fun mergeBitmap(newImageH: Int,
                            newImageW: Int,
                            background: Bitmap?,
                            backX: Float,
                            backY: Float,
                            foreground: Bitmap?,
                            foreX: Float,
                            foreY: Float): Bitmap? {
        if (null == background || null == foreground) {
            return null
        }
        val bitmap = Bitmap.createBitmap(newImageW, newImageH, Bitmap.Config.RGB_565)
        val cv = Canvas(bitmap)
        cv.drawBitmap(background, backX, backY, null)
        cv.drawBitmap(foreground, foreX, foreY, null)
        cv.save()
        //        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore()
        return bitmap
    }

}