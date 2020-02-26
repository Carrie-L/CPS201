package com.adsale.chinaplas.data.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

/**
 * Created by Carrie on 2020/2/14.
 */
@Dao
interface SeminarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSeminarInfoAll(list: List<SeminarInfo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSeminarSpeakAll(list: List<SeminarSpeaker>)

    @Query("select * from SeminarInfo where langID=:langId order by DATE,TIME,OrderMob")
    fun getSeminarInfoList(langId: String): MutableList<SeminarInfo>

    @Query("select * from SeminarInfo where langID=:langId and Time<'12:00' and Date like:date order by DATE,TIME,OrderMob")
    fun getSeminarAmList(langId: String, date: String): MutableList<SeminarInfo>

    @Query("select * from SeminarInfo where langID=:langId and Time>'12:00' and Date like:date order by DATE,TIME,OrderMob")
    fun getSeminarPmList(langId: String, date: String): MutableList<SeminarInfo>

    // 筛选： select * from SeminarInfo where langID=950 and Time<'12:00' and Date like "%21%" and (InApplications like "%2%" and InApplications like "%3%") order by DATE,TIME,OrderMob
    @RawQuery
    fun getFilterSeminarList(filterSql: SupportSQLiteQuery): MutableList<SeminarInfo>


    @Query("DELETE FROM SeminarInfo")
    fun deleteSeminarInfoAll()

    @Query("DELETE FROM SeminarSpeaker")
    fun deleteSeminarSpeakerAll()

    /*详情页*/
    @Query("select * from SeminarInfo where EventID=:eventId and langID=:langId")
    fun getSpeakInfoItem(eventId: Int, langId: String): SeminarInfo

    @Query("select * from SeminarSpeaker where EventID=:eventId and langID=:langId")
    fun getSeminarSpeakerItem(eventId: Int, langId: String): SeminarSpeaker
}