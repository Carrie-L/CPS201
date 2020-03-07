package com.adsale.chinaplas.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

/**
 * Created by Carrie on 2019/12/24.
 */

@Dao
interface ExhibitorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExhibitorAll(list: List<Exhibitor>)

    @Update
    fun updateExhibitorItem(entity: Exhibitor)

//    @Update
//    fun updateExhibitorItemLive(entity: MutableLiveData<Exhibitor>)

    @Query("select * from EXHIBITOR")
    fun getAllExhibitors(): MutableList<Exhibitor>

    @Query("DELETE FROM EXHIBITOR")
    fun deleteExhibitorAll()

    /* exhibitor list 按拼音排序 */
    @Query("select * from EXHIBITOR order by PYSimp,SeqSC")
    fun getAllExhibitorsSC(): MutableList<Exhibitor>

    @Query("select * from EXHIBITOR  order by CAST(StrokeTrad AS INT) ASC,CAST(SeqTC AS INT) ASC")
    fun getAllExhibitorsTC(): MutableList<Exhibitor>

    @Query("select * from EXHIBITOR order by StrokeEng,SeqEN")
    fun getAllExhibitorsEN(): MutableList<Exhibitor>

//    @Query("select CompanyID,CompanyNameCN,CompanyNameTW,CompanyNameEN,BoothNo,HallNo,seqHall,IsFavourite,PhotoFileName,PYSimp from EXHIBITOR order by PYSimp,SeqSC")
//    fun getAllExhibitorsSC(): LiveData<List<Exhibitor>>
//
//    @Query("select CompanyID,CompanyNameCN,CompanyNameTW,CompanyNameEN,BoothNo,HallNo,seqHall,IsFavourite,PhotoFileName,StrokeTrad from EXHIBITOR  order by CAST(StrokeTrad AS INT) ASC,CAST(SeqTC AS INT) ASC")
//    fun getAllExhibitorsTC(): LiveData<List<Exhibitor>>
//
//    @Query("select CompanyID,CompanyNameCN,CompanyNameTW,CompanyNameEN,BoothNo,HallNo,seqHall,IsFavourite,PhotoFileName,StrokeEng from EXHIBITOR order by StrokeEng,SeqEN")
//    fun getAllExhibitorsEN(): LiveData<List<Exhibitor>>


    /* =Search= */
    @Query("select CompanyID,CompanyNameCN,CompanyNameTW,CompanyNameEN,BoothNo,HallNo,seqHall,IsFavourite,PhotoFileName,PYSimp from EXHIBITOR WHERE CompanyNameCN LIKE :text OR CompanyNameTW LIKE :text OR CompanyNameEN LIKE :text OR BoothNo LIKE :text OR DescE LIKE :text OR DescS LIKE :text OR DescT LIKE :text order by PYSimp,SeqSC")
    fun querySearchSC(text: String): MutableList<Exhibitor>

    @Query("select CompanyID,CompanyNameCN,CompanyNameTW,CompanyNameEN,BoothNo,HallNo,seqHall,IsFavourite,PhotoFileName,StrokeTrad from EXHIBITOR  WHERE CompanyNameCN LIKE :text OR CompanyNameTW LIKE :text OR CompanyNameEN LIKE :text OR BoothNo LIKE :text OR DescE LIKE :text OR DescS LIKE :text OR DescT LIKE :text order by CAST(StrokeTrad AS INT) ASC,CAST(SeqTC AS INT) ASC")
    fun querySearchTC(text: String): MutableList<Exhibitor>

    @Query("select CompanyID,CompanyNameCN,CompanyNameTW,CompanyNameEN,BoothNo,HallNo,seqHall,IsFavourite,PhotoFileName,StrokeEng from EXHIBITOR WHERE CompanyNameCN LIKE :text OR CompanyNameTW LIKE :text OR CompanyNameEN LIKE :text OR BoothNo LIKE :text OR DescE LIKE :text OR DescS LIKE :text OR DescT LIKE :text order by StrokeEng,SeqEN")
    fun querySearchEN(text: String): MutableList<Exhibitor>


    @RawQuery
    fun filterExhibitors(query: SupportSQLiteQuery): MutableList<Exhibitor>

    @Query("select * from EXHIBITOR where CompanyID=:companyID")
    fun getCompanyItem(companyID: String): Exhibitor

    @Query("select * from EXHIBITOR where CompanyID=:companyID")
    fun getCompanyItemLive(companyID: String): LiveData<Exhibitor>


    /*  --- industry in exhibitor */
    @Query("select * from EXHIBITOR WHERE CompanyID in (select CompanyID FROM CompanyProduct where CatalogProductSubID=:id ) order by PYSimp,SeqSC")
    fun getIndustryInExhibitorsSC(id: String): MutableList<Exhibitor>

    @Query("select * from EXHIBITOR WHERE CompanyID in (select CompanyID FROM CompanyProduct where CatalogProductSubID=:id ) order by CAST(StrokeTrad AS INT) ASC,CAST(SeqTC AS INT) ASC")
    fun getIndustryInExhibitorsTC(id: String): MutableList<Exhibitor>

    @Query("select * from EXHIBITOR  WHERE CompanyID in (select CompanyID FROM CompanyProduct where CatalogProductSubID=:id ) order by StrokeEng,SeqEN")
    fun getIndustryInExhibitorsEN(id: String): MutableList<Exhibitor>

    /*  application in exhibitor */
    @Query("select * from EXHIBITOR WHERE CompanyID in (select CompanyID FROM CompanyApplication where IndustryID=:id ) order by PYSimp,SeqSC")
    fun getApplicationInExhibitorsSC(id: String): MutableList<Exhibitor>

    @Query("select * from EXHIBITOR WHERE CompanyID in (select CompanyID FROM CompanyApplication where IndustryID=:id ) order by CAST(StrokeTrad AS INT) ASC,CAST(SeqTC AS INT) ASC")
    fun getApplicationInExhibitorsTC(id: String): MutableList<Exhibitor>

    @Query("select * from EXHIBITOR  WHERE CompanyID in (select CompanyID FROM CompanyApplication where IndustryID=:id ) order by StrokeEng,SeqEN")
    fun getApplicationInExhibitorsEN(id: String): MutableList<Exhibitor>

    /*  my exhibitor */
    @Query("select * from EXHIBITOR where IsFavourite=1  order by PYSimp,SeqSC")
    fun getMyExhibitorsSC(): LiveData<List<Exhibitor>>

    @Query("select * from EXHIBITOR where IsFavourite=1 order by CAST(StrokeTrad AS INT) ASC,CAST(SeqTC AS INT) ASC")
    fun getMyExhibitorsTC(): LiveData<List<Exhibitor>>

    @Query("select  *  from EXHIBITOR where IsFavourite=1 order by StrokeEng,SeqEN")
    fun getMyExhibitorsEN(): LiveData<List<Exhibitor>>


    /*  ==========  全局搜索  =============  */
//    @Query(select * )
//    fun searchExhibitor():List<Exhibitor>

    /*  ==========  HistoryExhibitor  =============  */
    @Insert
    fun insertToHistory(entity: ExhibitorHistory)

    @Query("select * from ExhibitorHistory order by Time desc")
    fun getAllHistories(): List<ExhibitorHistory>

    @Query("select *,count(CompanyID) as count from ExhibitorHistory  where time like :date group by CompanyID order by time DESC")
    fun getRecentHistories(date: String): MutableList<ExhibitorHistory>

//    @Query("select *,count(CompanyID) as frequency from ExhibitorHistory  where time < :date group by CompanyID order by time DESC")
//    fun getPastHistories(date: String): List<ExhibitorHistory>

}
