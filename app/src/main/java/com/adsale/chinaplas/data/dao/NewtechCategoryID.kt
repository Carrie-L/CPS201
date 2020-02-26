package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class NewtechCategoryID {
    var Category: String = ""
    var RID: String = ""
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    fun parser(strings: Array<String>) {
        this.Category = strings[0]
        this.RID = strings[1]
    }

}