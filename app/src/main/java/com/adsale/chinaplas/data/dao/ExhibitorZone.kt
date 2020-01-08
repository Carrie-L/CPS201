package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Carrie on 2020/1/7.
 */
@Entity
class ExhibitorZone constructor(){
    @PrimaryKey
    var CompanyID: String = ""
    var ThemeZoneCode: String = ""

    constructor(CompanyID: String, ThemeZoneCode: String) :this() {
        this.CompanyID = CompanyID
        this.ThemeZoneCode = ThemeZoneCode
    }

    fun parser(strings: Array<String>) {
        ThemeZoneCode = strings[0]
        CompanyID = strings[1]
    }

}