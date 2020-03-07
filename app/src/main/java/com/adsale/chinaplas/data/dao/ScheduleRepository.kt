package com.adsale.chinaplas.data.dao

/**
 * Created by Carrie on 2020/3/3.
 */
class ScheduleRepository private constructor(private val scheduleInfoDao: ScheduleInfoDao) {


    fun getAllSchedules() = scheduleInfoDao.getAllSchedules()

    fun insert(entity: ScheduleInfo) = scheduleInfoDao.insert(entity)
    fun update(entity: ScheduleInfo) = scheduleInfoDao.update(entity)
    fun getScheduleItem(id: Long, type: Int) = scheduleInfoDao.getScheduleItem(id, type)
    fun deleteItem(entity: ScheduleInfo) = scheduleInfoDao.deleteItem(entity)

    companion object {
        @Volatile
        private var instance: ScheduleRepository? = null

        fun getInstance(scheduleInfoDao: ScheduleInfoDao) =
            instance ?: synchronized(this) {
                instance ?: ScheduleRepository(scheduleInfoDao).also { instance = it }
            }
    }


}