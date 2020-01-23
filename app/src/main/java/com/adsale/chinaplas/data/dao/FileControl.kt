package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Carrie on 2019/12/27.
 */
@Entity
data class FileControl(
    @PrimaryKey
    val PageID: String,
    val FileName: String?,
    var updatedAt:String)