package com.adsale.chinaplas.data.dao

import androidx.databinding.ObservableBoolean
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.LANG_EN
import com.adsale.chinaplas.utils.LANG_TC
import com.adsale.chinaplas.utils.getCurrLanguage
import com.adsale.chinaplas.utils.getName

/**
 * Created by Carrie on 2020/1/7.
 */
@Entity
class ExhApplication constructor() {
    @PrimaryKey
    var IndustryID: String = ""
    var ApplicationEng: String? = ""
    var ApplicationTC: String? = ""
    var ApplicationSC: String? = ""
    var TCStroke: String? = ""
    var SCPY: String? = ""

    @Ignore
    var isSelected = ObservableBoolean(false)

    fun getApplicationName(): String {
        return when (getCurrLanguage()) {
            LANG_EN -> ApplicationEng!!
            LANG_TC -> ApplicationTC!!
            else -> ApplicationSC!!
        }
    }


    constructor(IndustryID: String,
                ApplicationEng: String?,
                ApplicationTC: String?,
                ApplicationSC: String?,
                TCStroke: String?,
                SCPY: String?) : this() {
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