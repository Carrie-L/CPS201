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

    @Query("select * from SeminarInfo where langID=:langId and Time<'12:00'  order by DATE,TIME,OrderMob")
    fun getSeminarAmListAll(langId: String): MutableList<SeminarInfo>

    @Query("select * from SeminarInfo where langID=:langId and Time>'12:00' order by DATE,TIME,OrderMob")
    fun getSeminarPmListAll(langId: String): MutableList<SeminarInfo>


    // 筛选： select * from SeminarInfo where langID=950 and Time<'12:00' and Date like "%21%" and (InApplications like "%2%" and InApplications like "%3%") order by DATE,TIME,OrderMob
    @RawQuery
    fun getFilterSeminarList(filterSql: SupportSQLiteQuery): MutableList<SeminarInfo>


    @Query("DELETE FROM SeminarInfo")
    fun deleteSeminarInfoAll()

    @Query("DELETE FROM SeminarSpeaker")
    fun deleteSeminarSpeakerAll()

//    CREATE TABLE `SeminarInfo` (Booth` TEXT, `Date` TEXT, `Time` TEXT, `Hall` TEXT, `RoomNo` TEXT, `PresentCompany` TEXT, `Topic` TEXT, `Speaker` TEXT, `langID` INTEGER, `OrderFull` INTEGER, `OrderMob` INTEGER, `InApplications` TEXT, PRIMARY KEY(`ID`))

    @Query("select * from SeminarInfo where Booth like:keyword or Date like:keyword or Time like:keyword or Hall like:keyword or RoomNo like:keyword or PresentCompany like:keyword or Topic like:keyword or Speaker like:keyword  order by DATE,TIME,OrderMob")
    fun searchSeminar(keyword: String): MutableList<SeminarInfo>


    /*详情页*/
    @Query("select * from SeminarInfo where EventID=:eventId and LangID=:langId")
    fun getSpeakInfoItem(eventId: Int, langId: String): SeminarInfo

    @Query("select * from SeminarSpeaker where EventID=:eventId and LangID=:langId")
    fun getSeminarSpeakerItem(eventId: Int, langId: String): SeminarSpeaker

    @Query("select * from SeminarSpeaker")
    fun getSeminars(): List<SeminarSpeaker>
}