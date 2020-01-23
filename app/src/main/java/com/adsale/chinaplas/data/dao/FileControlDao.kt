package com.adsale.chinaplas.data.dao

import androidx.room.*
import com.adsale.chinaplas.data.entity.CountryJson

/**
 * Created by Carrie on 2019/12/27.
 */

@Dao
interface FileControlDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<FileControl>)

    @Query("select Max(updatedAt) from FileControl")
    fun getLastUpdateTime(): String

    @Query("select * from FileControl")
    fun getFileControls(): List<FileControl>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateFileControlItemTime(entity: FileControl)

}