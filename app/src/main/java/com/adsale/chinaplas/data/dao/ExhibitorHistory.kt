package com.adsale.chinaplas.data.dao

import androidx.databinding.ObservableInt
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.LANG_SC
import com.adsale.chinaplas.utils.LANG_TC
import com.adsale.chinaplas.utils.getCurrLanguage

/**
 * Created by Carrie on 2020/1/20.
 */
@Entity
data class ExhibitorHistory(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = 0L,
    var CompanyID: String = "",
    var CompanyNameEN: String? = "",
    var CompanyNameTW: String? = "",
    var CompanyNameCN: String? = "",
    var BoothNo: String? = "",
    var time: String? = "",
    var count: Int? = 0
) {
    @Transient
    @Ignore
    var frequency = 0

    @Transient
    @Ignore
    var isTypeLabel = ObservableInt(-1)  // -1 不是label.  1 今天， 2 昨天， 3 过去

    fun getCompanyName(): String {
        return when (getCurrLanguage()) {
            LANG_SC -> CompanyNameCN!!
            LANG_TC -> CompanyNameTW!!
            else -> CompanyNameEN!!
        }
    }

    override fun toString(): String {
        return "ExhibitorHistory(id=$id, CompanyID='$CompanyID', CompanyNameCN=$CompanyNameCN, time=$time, count=$count)"
    }


}