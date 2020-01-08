package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Carrie on 2020/1/7.
 */
@Entity
class Country constructor(){
    @PrimaryKey
    var CountryID: String = ""
    var CountryEng: String? = ""
    var CountryTC: String? = ""
    var CountrySC: String? = ""
    var SortTC: String? = ""
    var SortSC: String? = ""
    var SortEN: String? = ""

    constructor(CountryID: String,
                CountryEng: String?,
                CountryTC: String?,
                CountrySC: String?,
                SortTC: String?,
                SortSC: String?,
                SortEN: String?) :this() {
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
        SortEN = strings[6]
    }

//    constructor()

}