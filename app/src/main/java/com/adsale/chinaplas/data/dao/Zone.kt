package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Carrie on 2020/1/7.
 */
@Entity
class Zone constructor(){
    @PrimaryKey
    var ThemeZoneCode: String = ""
    var ThemeZoneDescription: String? = ""
    var ThemeZoneDescriptionTC: String? = ""
    var ThemeZoneDescriptionSC: String? = ""

    constructor(ThemeZoneCode: String,
                ThemeZoneDescription: String?,
                ThemeZoneDescriptionTC: String?,
                ThemeZoneDescriptionSC: String?) :this() {
        this.ThemeZoneCode = ThemeZoneCode
        this.ThemeZoneDescription = ThemeZoneDescription
        this.ThemeZoneDescriptionTC = ThemeZoneDescriptionTC
        this.ThemeZoneDescriptionSC = ThemeZoneDescriptionSC
    }

    fun parser(strings: Array<String>) {
        ThemeZoneCode = strings[1]
        ThemeZoneDescription = strings[2]
        ThemeZoneDescriptionTC = strings[3]
        ThemeZoneDescriptionSC = strings[4]
    }

}