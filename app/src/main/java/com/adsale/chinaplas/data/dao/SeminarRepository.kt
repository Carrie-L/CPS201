package com.adsale.chinaplas.data.dao

import androidx.sqlite.db.SimpleSQLiteQuery
import com.baidu.speech.utils.LogUtil

/**
 * Created by Carrie on 2020/2/14.
 */
class SeminarRepository private constructor(private val seminarDao: SeminarDao) {


    fun getSeminarTimeList(langId: String, date: String, isAm: Boolean): MutableList<SeminarInfo> =
        if (isAm) {
            seminarDao.getSeminarAmList(langId, date)
        } else {
            seminarDao.getSeminarPmList(langId, date)
        }

    fun getFilterSeminars(langId: String, date: String, isAm: Boolean, filterStr: String): MutableList<SeminarInfo> =
        run {
            val query = SimpleSQLiteQuery(getFilterSql(langId, date, isAm, filterStr))
            seminarDao.getFilterSeminarList(query)
        }

    private fun getFilterSql(langId: String, date: String, isAm: Boolean, filterStr: String): String {
        // select * from SeminarInfo where langID=950 and Time<'12:00' and Date like "%21%" and (InApplications like "%2%" and InApplications like "%3%") order by DATE,TIME,OrderMob
        val sb = StringBuilder()
        sb.append("select * from SeminarInfo where langID=$langId and ")
        if (isAm) sb.append("Time<'12:00'")
        else sb.append("Time>'12:00'")
        sb.append(" and Date like \"%$date%\" and (")

        val filters = filterStr.replace("[", "").replace("]", "").split(",")
        LogUtil.i("filterStr=$filterStr")
        val size = filters.size
        for (i in 0 until size) {
            sb.append(" InApplications like \"%${filters[i]}%\"")
            if (i < size - 1) {
                sb.append(" and ")
            }
        }
        sb.append(") order by DATE,TIME,OrderMob")
        LogUtil.i("seminar sql= ${sb.toString()}")
        return sb.toString()
    }


    fun getSpeakInfoItem(eventId: Int, langId: String) = seminarDao.getSpeakInfoItem(eventId, langId)
    fun getSeminarSpeakerItem(eventId: Int, langId: String) = seminarDao.getSeminarSpeakerItem(eventId, langId)


    companion object {
        @Volatile
        private var instance: SeminarRepository? = null

        fun getInstance(seminarDao: SeminarDao) =
            instance ?: synchronized(this) {
                instance ?: SeminarRepository(seminarDao).also { instance = it }
            }
    }
}