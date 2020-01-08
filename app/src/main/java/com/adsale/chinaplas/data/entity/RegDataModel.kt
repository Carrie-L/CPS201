package com.adsale.chinaplas.data.entity

import com.squareup.moshi.JsonClass

/**
 * Created by Carrie on 2019/12/9.
 */
@JsonClass(generateAdapter = true)
class RegKey {
    var Context: String? = ""
}

@JsonClass(generateAdapter = true)
class RegPayStatus(val Status: String, val Name: String?, val RefId: Int)