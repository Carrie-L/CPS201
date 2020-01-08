package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Carrie on 2020/1/7.
 */
@Entity
class ExhIndustry constructor(){
    @PrimaryKey
    var CatalogProductSubID: String = ""
    var CatEng: String? = ""
    var CatTC: String? = ""
    var CatSC: String? = ""
    var TCStroke: Int? = 0
    var SCPY: String? = ""
    var SortEN: String? = ""

    constructor(CatalogProductSubID: String,
                CatEng: String?,
                CatTC: String?,
                CatSC: String?,
                TCStroke: Int?,
                SCPY: String?,
                SortEN: String?):this() {
        this.CatalogProductSubID = CatalogProductSubID
        this.CatEng = CatEng
        this.CatTC = CatTC
        this.CatSC = CatSC
        this.TCStroke = TCStroke
        this.SCPY = SCPY
        this.SortEN = SortEN
    }

    fun parser(inputStream: Array<String>) {
        CatalogProductSubID = inputStream[0]
        CatEng = inputStream[1]
        CatTC = inputStream[2]
        CatSC = inputStream[3]
        if (inputStream[4] == "#") {
            TCStroke = 999
        } else TCStroke = Integer.valueOf(inputStream[4])
        if (inputStream[5] == "#") {
            inputStream[5] = "ZZ"
        }
        SCPY = inputStream[5]
    }
}