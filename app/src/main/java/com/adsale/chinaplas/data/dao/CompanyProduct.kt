package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Carrie on 2020/1/7.
 */
@Entity
class CompanyProduct constructor(){
    @PrimaryKey(autoGenerate = true)
    var id = 1
    var CompanyID: String? = ""
    var CatalogProductSubID: String? = ""

    constructor(id: Int, CompanyID: String?, CatalogProductSubID: String?):this() {
        this.id = id
        this.CompanyID = CompanyID
        this.CatalogProductSubID = CatalogProductSubID
    }

    fun parser(inputStream: Array<String>) {
        CompanyID = inputStream[0]
        CatalogProductSubID = inputStream[1]
    }
}