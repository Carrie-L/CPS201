package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.getName

/**
 * Created by Carrie on 2019/10/24.
 */
@Entity
 class WebContent {
    var ParentID: String = ""
    @PrimaryKey
    var PageID: String = ""
    var TitleSC: String = ""
    var TitleTC: String = ""
    var TitleEN: String = ""
    var FileName: String? = ""
    var Icon: String? = ""
    var Seq: Int = 1
    var IsHidden: Boolean? = false
    var updatedAt: String = ""

    fun getTitle(): String {
        return getName(TitleTC, TitleEN, TitleSC)
    }

   override fun toString(): String {
      return "WebContent(ParentID='$ParentID', PageID='$PageID', TitleSC='$TitleSC', TitleTC='$TitleTC', TitleEN='$TitleEN', FileName=$FileName, Icon=$Icon, Seq=$Seq, IsHidden=$IsHidden, updatedAt='$updatedAt')"
   }


}