package com.adsale.chinaplas.data.dao

import androidx.databinding.ObservableBoolean
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.LANG_EN
import com.adsale.chinaplas.utils.LANG_SC
import com.adsale.chinaplas.utils.TRAD_STROKE
import com.adsale.chinaplas.utils.getCurrLanguage
import kotlin.reflect.jvm.internal.impl.load.java.Constant

/**
 * Created by Carrie on 2020/1/7.
 */
@Entity
class ExhIndustry constructor() {
    @PrimaryKey
    var CatalogProductSubID: String = ""
    var CatEng: String? = ""
    var CatTC: String? = ""
    var CatSC: String? = ""
    var TCStroke: String? = ""
    var SCPY: String? = ""
    var SortEN: String? = ""

    @Ignore
    var isSelected = ObservableBoolean(false)
    @Ignore
    var isTypeLabel = ObservableBoolean(false)

    fun getName(): String {
        return when (getCurrLanguage()) {
            LANG_SC -> CatSC!!
            LANG_EN -> CatEng!!
            else -> CatTC!!
        }
    }

    fun getSort(): String {
        return when (getCurrLanguage()) {
            LANG_SC -> {
                if (SCPY == "ZZ") {
                    SCPY = "#"
                }
                SCPY!!
            }
            LANG_EN -> {
                if (SortEN!!.contains("#")) {
                    SortEN = "#"
                }
                SortEN!!
            }
            else -> {
                if (TCStroke == "999") {
                    TCStroke = "#"
                }
                TCStroke+ TRAD_STROKE
            }
        }
    }


    constructor(
        CatalogProductSubID: String,
        CatEng: String?,
        CatTC: String?,
        CatSC: String?,
        TCStroke: String?,
        SCPY: String?,
        SortEN: String?
    ) : this() {
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
            TCStroke = "999"
        } else TCStroke = inputStream[4]
        if (inputStream[5] == "#") {
            inputStream[5] = "ZZ"
        }
        SCPY = inputStream[5]
        SortEN = inputStream[6]
        if (SortEN == "#") {
            SortEN = "ZZ#"
        }
    }

    override fun toString(): String {
        return "ExhIndustry(CatalogProductSubID='$CatalogProductSubID', CatEng=$CatEng, CatTC=$CatTC, CatSC=$CatSC, TCStroke=$TCStroke, SCPY=$SCPY, SortEN=$SortEN, isSelected=$isSelected)"
    }
}