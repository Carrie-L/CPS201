package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Carrie on 2020/2/14.
 */
@Entity
class SeminarApplication {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var CompanyID: String? = null
    var IndustryID: String? = null

    fun parser(inputStream: Array<String>) {
        this.CompanyID = inputStream[0]
        this.IndustryID = inputStream[1]
    }
}