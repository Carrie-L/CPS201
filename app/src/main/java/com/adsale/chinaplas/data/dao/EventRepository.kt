package com.adsale.chinaplas.data.dao

/**
 * Created by Carrie on 2020/2/11.
 */
class EventRepository private constructor(private val eventDao: EventDao) {

    fun getLastUpdateTime() = eventDao.getLastUpdateTime()

    fun insertAll(list: List<ConcurrentEvent>) = eventDao.insertAll(list)

    fun getOverallList(): List<ConcurrentEvent> = eventDao.getEventList()

    fun getDateEventList(date:String): List<ConcurrentEvent> = eventDao.getDateEventList(date)


    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(eventDao: EventDao) =
            instance ?: synchronized(this) {
                instance ?: EventRepository(eventDao).also { instance = it }
            }
    }


}