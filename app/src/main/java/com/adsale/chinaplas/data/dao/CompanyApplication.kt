package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Carrie on 2020/1/7.
 */
@Entity
class CompanyApplication constructor(){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 1
    var CompanyID: String? = ""
    var IndustryID: String? = ""

//    constructor()

    constructor(id: Int, CompanyID: String?, IndustryID: String?) :this(){
        this.id = id
        this.CompanyID = CompanyID
        this.IndustryID = IndustryID
    }

    fun parser(inputStream: Array<String>) {
        CompanyID = inputStream[0]
        IndustryID = inputStream[1]
    }


}