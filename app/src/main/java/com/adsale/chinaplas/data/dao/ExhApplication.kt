package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Carrie on 2020/1/7.
 */
@Entity
class ExhApplication constructor(){
    @PrimaryKey
    var IndustryID: String = ""
    var ApplicationEng: String? = ""
    var ApplicationTC: String? = ""
    var ApplicationSC: String? = ""
    var TCStroke: String? = ""
    var SCPY: String? = ""


    constructor(IndustryID: String,
                ApplicationEng: String?,
                ApplicationTC: String?,
                ApplicationSC: String?,
                TCStroke: String?,
                SCPY: String?):this() {
        this.IndustryID = IndustryID
        this.ApplicationEng = ApplicationEng
        this.ApplicationTC = ApplicationTC
        this.ApplicationSC = ApplicationSC
        this.TCStroke = TCStroke
        this.SCPY = SCPY
    }

    fun parser(inputStream: Array<String>) {
        IndustryID = inputStream[0]
        ApplicationEng = inputStream[1]
        ApplicationTC = inputStream[2]
        ApplicationSC = inputStream[3]
        TCStroke = inputStream[4]
        SCPY = inputStream[5]
    }

}