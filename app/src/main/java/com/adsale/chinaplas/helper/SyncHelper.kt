package com.adsale.chinaplas.helper

import android.content.Context
import com.adsale.chinaplas.mSPConfig
import com.adsale.chinaplas.utils.getCurrentTime
import com.adsale.chinaplas.utils.getMemberId
import okhttp3.FormBody
import okhttp3.RequestBody

/**
 * Created by Carrie on 2020/1/17.
 */
class SyncHelper private constructor(private val context: Context) {
    companion object {
        @Volatile
        private var instance: SyncHelper? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: SyncHelper(context).also { instance = it }
            }
    }

    fun getRequestBody(): RequestBody {
        return FormBody.Builder()
            // all add data
            .add("txtMID", getMemberId()).add("txtLogUpdate", getAllUpdateData())
            // all delete data
            .add("txtLogDelete", getAllDeleteData())
            // current time
            .add("txtLUDate", getCurrentTime())
            .add("hidAct", "6872").build()
    }

    /**
     * 每次 [star] 展商时，将 [companyID,currentTime] 存进来
     */
    fun saveStarData(companyId: String) {
        val thisData = "$companyId,${getCurrentTime()}"
        val sb = StringBuilder().append(getAllUpdateData())
        if (sb.isNotEmpty()) {
            sb.append("|")
        }
        sb.append(thisData)
        mSPConfig.edit().putString("SYNC_ADD_DATA", sb.toString()).apply()
    }

    /**
     * 每次 [取消star] 展商时，将 [companyID,currentTime] 存进来
     */
    fun removeStarData(companyId: String) {
        val thisData = "$companyId,${getCurrentTime()}"
        val sb = StringBuilder().append(getAllDeleteData())
        if (sb.isNotEmpty()) {
            sb.append("|")
        }
        sb.append(thisData)
        mSPConfig.edit().putString("SYNC_REMOVE_DATA", sb.toString()).apply()
    }

    fun getAllUpdateData(): String {
        return mSPConfig.getString("SYNC_ADD_DATA", "")!!
    }

    fun getAllDeleteData(): String {
        return mSPConfig.getString("SYNC_REMOVE_DATA", "")!!
    }

}