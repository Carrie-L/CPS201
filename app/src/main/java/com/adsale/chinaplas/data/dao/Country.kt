package com.adsale.chinaplas.data.dao

import androidx.databinding.ObservableBoolean
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.*

/**
 * Created by Carrie on 2020/1/7.
 */
@Entity
class Country constructor() {
    @PrimaryKey
    var CountryID: String = ""
    var CountryEng: String? = ""
    var CountryTC: String? = ""
    var CountrySC: String? = ""
    var SortTC: String? = ""
    var SortSC: String? = ""
    var SortEN: String? = ""

    @Ignore
    var isSelected = ObservableBoolean(false)
    @Ignore
    var isTypeLabel = ObservableBoolean(false)

    fun getName(): String {
        return when (getCurrLanguage()) {
            LANG_SC -> CountrySC!!
            LANG_EN -> CountryEng!!
            else -> CountryTC!!
        }
    }

    fun getSort(): String {
        return when (getCurrLanguage()) {
            LANG_SC -> SortSC!!
            LANG_EN -> SortEN!!
            else -> SortTC+ TRAD_STROKE
        }
    }


    constructor(
        CountryID: String,
        CountryEng: String?,
        CountryTC: String?,
        CountrySC: String?,
        SortTC: String?,
        SortSC: String?,
        SortEN: String?
    ) : this() {
        this.CountryID = CountryID
        this.CountryEng = CountryEng
        this.CountryTC = CountryTC
        this.CountrySC = CountrySC
        this.SortTC = SortTC
        this.SortSC = SortSC
        this.SortEN = SortEN
    }

    fun parser(strings: Array<String>) {
        CountryID = strings[0]
        CountryEng = strings[1]
        CountryTC = strings[2]
        CountrySC = strings[3]
        SortTC = strings[4]
        SortSC = strings[5]
//        SortEN = getFirstChar(CountryEng!!)
        SortEN = strings[6]
    }

    override fun toString(): String {
        return "Country(CountryID='$CountryID', CountryEng=$CountryEng, CountryTC=$CountryTC, CountrySC=$CountrySC, SortTC=$SortTC, SortSC=$SortSC, SortEN=$SortEN, isSelected=$isSelected)"
    }

//    constructor()

}