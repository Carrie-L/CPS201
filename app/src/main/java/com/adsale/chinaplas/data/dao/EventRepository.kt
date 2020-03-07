package com.adsale.chinaplas.data.dao

import androidx.sqlite.db.SimpleSQLiteQuery
import com.adsale.chinaplas.utils.LANG_EN
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.getCurrLanguage

/**
 * Created by Carrie on 2020/2/11.
 */
class EventRepository private constructor(private val eventDao: EventDao,
                                          private val eventApplicationDao: EventApplicationDao) {

    fun getLastUpdateTime() = eventDao.getLastUpdateTime()

    fun insertAll(list: List<ConcurrentEvent>) = eventDao.insertAll(list)

    fun getOverallList(): List<ConcurrentEvent> = eventDao.getEventList()

    fun getDateEventList(date: String): List<ConcurrentEvent> = eventDao.getDateEventList(date)

    fun getFilterOverallList(eventFilter: String): List<ConcurrentEvent> = run {
        val query = SimpleSQLiteQuery(filterSql(eventFilter, ""))
        eventDao.getFilterEventList(query)
    }

    fun getFilterDateEventList(eventFilter: String, date: String): List<ConcurrentEvent> = run {
        val query = SimpleSQLiteQuery(filterSql(eventFilter, date))
        eventDao.getFilterDateEventList(query)
    }

    private fun filterSql(eventFilter: String, date: String): String {
        val filters: List<String> = eventFilter.replace("[", "").replace("]", "").split(",")
        LogUtil.i("filters=${filters.size}, $filters")
        val sb = StringBuilder()
        sb.append("select * from ConcurrentEvent ")
        if (filters.size == 1) {
            sb.append("where (InApplicationsStr like \"%${filters[0]}%\"")
        } else {
            sb.append("where (")
            val size = filters.size
            for (i in 0 until size) {
                sb.append("InApplicationsStr like \"%${filters[i]}%\"")
                if (i < size - 1) {
                    sb.append(" and ")
                }
            }
        }
        sb.append(" OR InApplicationsStr=\"ALL\") ")
        if (date.isNotEmpty()) {
            sb.append("and Date like \"%${date}%\"")
        }
        sb.append(" group by EventID order by seq")
        LogUtil.i("filterSql = ${sb.toString()}")
        return sb.toString()
    }


    /* ------------ EventApplicationDao -----------  */
    fun getEVLastUpdateTime() = eventApplicationDao.getLastUpdateTime()

    fun insertEVAll(list: List<EventApplication>) = eventApplicationDao.insertAll(list)

    fun getEVList(): List<EventApplication> =
        when (getCurrLanguage()) {
            LANG_EN -> eventApplicationDao.getENList()
            else -> eventApplicationDao.getCNList()
        }


    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(eventDao: EventDao, eventApplicationDao: EventApplicationDao) =
            instance ?: synchronized(this) {
                instance ?: EventRepository(eventDao, eventApplicationDao).also { instance = it }
            }
    }


}