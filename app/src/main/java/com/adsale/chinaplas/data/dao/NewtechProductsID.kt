package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class NewtechProductsID {
    var RID: String = "";
    var Spot: String = "";
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0


    fun parser(strings: Array<String>) {
        this.RID = strings[0]
        this.Spot = strings[1]
    }
}