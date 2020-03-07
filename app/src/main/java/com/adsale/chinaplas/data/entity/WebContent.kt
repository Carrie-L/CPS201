package com.adsale.chinaplas.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class WebContentEntity {
    var ParentID: String = ""
    var PageID: String = ""
    var TitleSC: String = ""
    var TitleTC: String = ""
    var TitleEN: String = ""
    var FileName: String? = ""
    var Icon: String? = ""
    var Seq: Int = 1
    var IsHidden: Boolean? = false
    var updatedAt: String = ""


}