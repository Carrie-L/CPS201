package com.adsale.chinaplas.data.dao

import androidx.databinding.ObservableBoolean
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.getName

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

    @Ignore
    var isSelected = ObservableBoolean(false)

    fun getName():String{
        return getName(ThemeZoneDescriptionTC!!,ThemeZoneDescription!!,ThemeZoneDescriptionSC!!)
    }

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
        // 第0列是ShowCode，不需要
        ThemeZoneCode = strings[1]
        ThemeZoneDescription = strings[2]
        ThemeZoneDescriptionTC = strings[3]
        ThemeZoneDescriptionSC = strings[4]
    }

}