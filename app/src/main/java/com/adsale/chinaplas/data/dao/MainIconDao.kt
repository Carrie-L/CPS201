package com.adsale.chinaplas.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by Carrie on 2019/10/21.
 */
@Dao
interface MainIconDao {

    @Query("select * from MainIcon where MenuSeq>0")
    fun getAllMainIcons(): List<MainIcon>

    @Query("select * from MainIcon where DrawerSeq>0")
    fun getDrawerMainIcons(): List<MainIcon>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<MainIcon>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItemIcon(entity: MainIcon)

    @Query("select Max(updatedAt) from MainIcon")
    fun getLastUpdateTime(): String

}