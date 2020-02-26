package com.adsale.chinaplas.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EventApplicationDao {

    @Query("select Max(updatedAt) from EventApplication")
    fun getLastUpdateTime(): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<EventApplication>)

    @Query("select * from EventApplication order by SortCN")
    fun getCNList(): List<EventApplication>

    @Query("select * from EventApplication order by SortEN")
    fun getENList(): List<EventApplication>

}