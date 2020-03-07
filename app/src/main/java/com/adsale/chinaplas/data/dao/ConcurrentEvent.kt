package com.adsale.chinaplas.data.dao

import androidx.databinding.ObservableInt
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.*

/**
 * Created by Carrie on 2020/2/10.
 */
@Entity
data class ConcurrentEvent(
    @PrimaryKey
    val PageID: String,
    // EventID  不同日期的同一个同期活动主题 EventID相同
    val EventID: String?,
    val TitleTC: String?,
    val TitleSC: String?,
    val TitleEN: String?,
    var Date: String?,
    val duration: String?,
    val VenueSC: String?,
    val isFree: Boolean? = false,
    val isPreReg: Boolean? = false,
    val PageFileName: String?,
    val InApplicationsStr: String?,
    val seq: Int?,
    val updatedAt: String,
    val IsDelete: Boolean? = false,
    val VenueTC: String?,
    val VenueEN: String?,
    var location: String??,
    /* 是否需要下载。 needDown = true/1, 要下载； needDown = false/0, 无需下载。 默认为0 */
    var needDown: Boolean? = false
) {
    /* 头部Bar : 0 没有bar， 1 有bar；2 tech */
    @Ignore
    val isTypeLabel: ObservableInt? = ObservableInt(0)


    fun getTitle(): String {
        return getName(TitleTC!!, TitleEN!!, TitleSC!!)
    }

    fun getVenue(): String {
        return getName(VenueTC!!, VenueEN!!, VenueSC!!)
    }

    fun getDateStr(): String {
        val sb = StringBuilder()
        if (Date == "ALL") {
            return sb.append(String.format(EVENT_DATE, DATE_1)).append(",")
                .append(String.format(EVENT_DATE, DATE_2)).append(",")
                .append(String.format(EVENT_DATE, DATE_3)).append(",")
                .append(String.format(EVENT_DATE, DATE_4)).toString()
        } else if (Date!!.contains("$DATE_1")) {
            sb.append(String.format(EVENT_DATE, DATE_1)).append(",")
        } else if (Date!!.contains("$DATE_2")) {
            sb.append(String.format(EVENT_DATE, DATE_2)).append(",")
        } else if (Date!!.contains("$DATE_3")) {
            sb.append(String.format(EVENT_DATE, DATE_3)).append(",")
        } else if (Date!!.contains("$DATE_4")) {
            sb.append(String.format(EVENT_DATE, DATE_4)).append(",")
        }
        return subLastString(sb.toString())
    }

}