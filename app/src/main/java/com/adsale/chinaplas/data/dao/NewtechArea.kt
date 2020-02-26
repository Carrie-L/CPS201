package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class NewtechArea {
    @PrimaryKey
    var RID: String = ""
    var Area: String? = ""

    fun parser(strings: Array<String>) {
        this.RID = strings[0]
        this.Area = strings[1]
    }

}