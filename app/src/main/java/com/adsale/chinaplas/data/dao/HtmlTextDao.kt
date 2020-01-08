package com.adsale.chinaplas.data.dao

import android.text.Html
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
    fun insertItemHtmlText(entity:HtmlText)


}
