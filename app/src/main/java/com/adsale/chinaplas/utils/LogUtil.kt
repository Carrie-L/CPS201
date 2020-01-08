package com.adsale.chinaplas.utils

import android.R
import android.os.Build
import android.util.Log
import com.adsale.chinaplas.BuildConfig
import java.util.regex.Pattern
import android.R.attr.tag


/**
 * Created by Carrie on 2019/10/12.
 */

object LogUtil {
    private val MAX_LOG_LENGTH = 4000
    private val MAX_TAG_LENGTH = 23
    private val CALL_STACK_INDEX = 5
    private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

    private val prefix = "CPS:"

    private fun createStackElementTag(element: StackTraceElement): String {
        var tag = element.className
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        tag = tag.substring(tag.lastIndexOf('.') + 1)
        // Tag length limit was removed in API 24.
        return if (tag.length <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tag
        } else tag.substring(0, MAX_TAG_LENGTH)
    }

    fun getTag(): String {
        val stackTrace = Throwable().stackTrace
        //        var i=0
        //        for (element: StackTraceElement in stackTrace) {
        //            Log.i("LogUtil","$i+,,,cps:$element.className\n+${element.fileName}")
        //            i++
        //        }
        if (stackTrace.size <= 5) {
            throw IllegalStateException(
                "Synthetic stacktrace didn't have enough elements: are you using proguard?"
            )
        }
        //        return createStackElementTag(stackTrace[1])
        return prefix + stackTrace[2].fileName
    }

    fun i(msg: String) {
//        if (BuildConfig.DEBUG) {
        Log.i(getTag(), msg)
    }

    fun iL(msg: String) {
        if (BuildConfig.DEBUG) {
            val tag: String = getTag()
            var message: String = msg
            val max_str_length = 2001 - tag.length
            //大于4000时
            while (message.length > max_str_length) {
                Log.i(tag, message.substring(0, max_str_length))
                message = message.substring(max_str_length)
            }
            Log.i(tag, message)
        }

    }

    fun e(msg: String) {
//        if (BuildConfig.DEBUG)
        Log.e(getTag(), msg)
    }

    fun e(e: Exception) {
//        if (BuildConfig.DEBUG)
        Log.e(getTag(), e.message + "\n" + e.printStackTrace())
    }


}
