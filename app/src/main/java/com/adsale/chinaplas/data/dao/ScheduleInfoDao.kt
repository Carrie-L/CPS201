package com.adsale.chinaplas.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Created by Carrie on 2020/3/3.
 */
@Dao
interface ScheduleInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: ScheduleInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun update(entity: ScheduleInfo)

    @Query("select * from ScheduleInfo order by startDate")
    fun getAllSchedules(): LiveData<List<ScheduleInfo>>
//    fun getAllSchedules(): List<ScheduleInfo>

    //    @Query("select * from ScheduleInfo where startDate=:date and startTime=:startTime and id=:id and type=:type")
    @Query("select * from ScheduleInfo where id=:id and type=:type")
    fun getScheduleItem(id: Long, type: Int): ScheduleInfo

    @Delete
    fun deleteItem(entity: ScheduleInfo)

}