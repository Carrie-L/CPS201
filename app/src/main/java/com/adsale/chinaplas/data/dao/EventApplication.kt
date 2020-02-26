package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class EventApplication {
    @PrimaryKey
    var IndustryID: String = ""
    /** Not-"" varue.  */
    var ApplicationEng: String = ""
    /** Not-"" varue.  */
    var ApplicationTC: String = ""
    /** Not-"" varue.  */
    var ApplicationSC: String = ""
    var SortCN: String? = ""
    var SortEN: String? = ""
    var IsDelete: Boolean? = false
    var updatedAt: String = ""

//    @Ignore
//    var isSelected = ObservableBoolean(false)
//    @Ignore
//    var industry: String? = ""
//
//    fun getApplicationName(): String {
//        return getName(ApplicationTC, ApplicationEng, ApplicationSC)
//    }

}