package com.adsale.chinaplas.data.dao

import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by Carrie on 2019/12/24.
 */

@Dao
interface WebContentDao {

    @Query("select Max(updatedAt) from WebContent")
    fun getLastUpdateTime(): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<WebContent>)

    @Query("select * from WebContent where ParentID=:parentID")
    fun getWebContents(parentID: String): MutableList<WebContent>

    @Query("select PageID from WebContent where ParentID=:parentID")
    fun getPageIDs(parentID: String): List<String>

}
