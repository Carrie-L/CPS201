package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class NewtechCategorySub {
    var MainTypeId: String = ""
    var SubNameEn: String = ""
    var SubNameTc: String = ""
    var SubNameSc: String = ""
    @PrimaryKey
    var CategoryId: String = ""
    var OrderId: Int = 0
    var SCSort: String? = ""
    var TCSort: String? = ""
    var ENSort: String? = ""


    fun parser(strings: Array<String>) {
        /*      ShowId|MainTypeId|NameEn|NameTc|NameSc|OrderId|UpdateDate   */
        this.MainTypeId = strings[0]
        this.SubNameEn = strings[1]
        this.SubNameTc = strings[2]
        this.SubNameSc = strings[3]
        this.CategoryId = strings[4]
        this.OrderId = Integer.valueOf(strings[5])
        this.SCSort = strings[6]
        this.TCSort = strings[7]
        this.ENSort = strings[8]
    }

}