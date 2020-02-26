package com.adsale.chinaplas.data.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

/**
 * Created by Carrie on 2020/2/11.
 */
@Dao
interface EventDao {
    @Query("select Max(updatedAt) from ConcurrentEvent")
    fun getLastUpdateTime(): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<ConcurrentEvent>)

    @Query("select * from ConcurrentEvent group by EventID order by seq")
    fun getEventList(): MutableList<ConcurrentEvent>


    @Query("select * from ConcurrentEvent where Date like:date order by seq")
    fun getDateEventList(date: String): MutableList<ConcurrentEvent>


    //    @Query("select * from ConcurrentEvent where InApplicationsStr='ALL' and sql group by EventID order by seq")
    @RawQuery
    fun getFilterEventList(query: SupportSQLiteQuery): MutableList<ConcurrentEvent>

    @RawQuery
    fun getFilterDateEventList(query: SupportSQLiteQuery): MutableList<ConcurrentEvent>

}