package com.adsale.chinaplas.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by Carrie on 2019/12/24.
 */

@Dao
interface HtmlTextDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<HtmlText>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemHtmlText(entity: HtmlText)


    @Query("SELECT * FROM HtmlText WHERE id like:keyword or title_sc like:keyword or title_en like:keyword or title_tc like:keyword or text like:keyword or group_title like:keyword order by groupID")
    fun searchHtmlText(keyword: String): List<HtmlText>


}
