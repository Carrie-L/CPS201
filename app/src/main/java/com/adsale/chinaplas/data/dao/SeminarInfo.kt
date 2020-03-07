package com.adsale.chinaplas.data.dao

import android.text.TextUtils
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Created by Carrie on 2020/2/14.
 */
@Entity
class SeminarInfo {
    @PrimaryKey
    var ID: Int = 0
    var EventID: Int? = 0
    var CompanyID: String? = ""
    var Booth: String? = ""
    var Date: String? = ""
    var Time: String? = ""
    var Hall: String? = ""
    var RoomNo: String? = ""
    var PresentCompany: String? = ""
    var Topic: String? = ""
    var Speaker: String? = ""
    var langID: Int? = 0
    var OrderFull: Int? = 0
    var OrderMob: Int? = 0
    var InApplications: String? = ""

    fun getTopicR(): String {
        Topic?.let {
            if (Topic!!.contains("<br")) {
                Topic = Topic!!.replace("<br />", "\n")
            }
            return Topic!!
        }
        return ""
    }

    fun getSpeakerLanguage() {

    }

    fun isNoIntentCompany(): Boolean {
        if (TextUtils.isEmpty(Booth)) {
            return true
        }
        return false
    }

    @Ignore
    var isAder = false
    @Ignore
    var logoImage = ""

    fun parser(strings: Array<String>) {
        this.ID = Integer.valueOf(strings[0])
        this.EventID = Integer.valueOf(strings[1])
        this.CompanyID = strings[2]
        this.Booth = strings[3]
        this.Date = strings[4]
        this.Time = strings[5]
        this.Hall = strings[6]
        this.RoomNo = strings[7]
        this.PresentCompany = strings[8]
        this.Topic = strings[9]
        this.Speaker = strings[10]
        this.langID = Integer.valueOf(strings[11])
        this.OrderFull = Integer.valueOf(strings[12])
        this.OrderMob = Integer.valueOf(strings[13])
        this.InApplications = strings[14]

//         todo 之后删除，仅为了测试数据
//        if(Date.contains("22"))


    }

    override fun toString(): String {
        return "SeminarInfo(ID=$ID, EventID=$EventID, CompanyID=$CompanyID, Booth=$Booth, Date=$Date, Time=$Time, Hall=$Hall, RoomNo=$RoomNo, PresentCompany=$PresentCompany, Topic=$Topic, Speaker=$Speaker, langID=$langID, OrderFull=$OrderFull, OrderMob=$OrderMob, InApplications=$InApplications, isAder=$isAder)"
    }
}