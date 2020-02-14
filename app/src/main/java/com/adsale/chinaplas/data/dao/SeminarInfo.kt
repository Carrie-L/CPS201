package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Carrie on 2020/2/14.
 */
@Entity
class SeminarInfo {
    @PrimaryKey
     var ID: Int=0
     var EventID: Int? = null
     var CompanyID: String? = null
     var Booth: String? = null
     var Date: String? = null
     var Time: String? = null
     var Hall: String? = null
     var RoomNo: String? = null
     var PresentCompany: String? = null
     var Topic: String? = null
     var Speaker: String? = null
     var langID: Int? = null
     var OrderFull: Int? = null
     var OrderMob: Int? = null
     var InApplications: String? = null
}