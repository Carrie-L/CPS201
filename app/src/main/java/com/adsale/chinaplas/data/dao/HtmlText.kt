package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Carrie on 2019/12/25.
 */
@Entity
data class HtmlText(
    @PrimaryKey
    val pageID: String,
    val content: String)