package com.adsale.chinaplas.data.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import java.lang.Exception

/**
 * Created by Carrie on 2019/12/24.
 */

@Dao
interface ExhibitorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExhibitorAll(list: List<Exhibitor>)

    @Update
    fun updateExhibitorItem(entity:Exhibitor)

    @Query("select * from EXHIBITOR")
    fun getAllExhibitors(): MutableList<Exhibitor>

    @Query("DELETE FROM EXHIBITOR")
    fun deleteExhibitorAll()

    /* exhibitor list 按拼音排序 */
    @Query("select CompanyID,CompanyNameCN,BoothNo,seqHall,IsFavourite,PhotoFileName,PYSimp from EXHIBITOR order by PYSimp,SeqSC")
    fun getAllExhibitorsSC(): MutableList<Exhibitor>

    @Query("select CompanyID,CompanyNameTW,BoothNo,seqHall,IsFavourite,PhotoFileName,StrokeTrad from EXHIBITOR  order by CAST(StrokeTrad AS INT) ASC,CAST(SeqTC AS INT) ASC")
    fun getAllExhibitorsTC(): MutableList<Exhibitor>

    @Query("select CompanyID,CompanyNameEN,BoothNo,seqHall,IsFavourite,PhotoFileName,StrokeEng from EXHIBITOR order by StrokeEng,SeqEN")
    fun getAllExhibitorsEN(): MutableList<Exhibitor>


    /*  ------------ ApplicationDao -----------   */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertApplicationAll(list: List<ExhApplication>)

    @Query("DELETE FROM ExhApplication")
    fun deleteApplicationAll()

    /*  ------------ CompanyApplication -----------   */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompanyApplicationAll(list: List<CompanyApplication>)

    @Query("DELETE FROM CompanyApplication")
    fun deleteCompanyApplicationAll()

    /*  ------------ CompanyProduct -----------   */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompanyProductAll(list: List<CompanyProduct>)

    @Query("DELETE FROM CompanyProduct")
    fun deleteCompanyProductAll()

    /*  ------------ Industry -----------   */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIndustryAll(list: List<ExhIndustry>)

    @Query("DELETE FROM ExhIndustry")
    fun deleteIndustryAll()

    /*  ------------ ExhibitorZone -----------   */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExhibitorZoneAll(list: List<ExhibitorZone>)

    @Query("DELETE FROM ExhibitorZone")
    fun deleteExhibitorZoneAll()

    /*  ------------ Zone -----------   */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertZoneAll(list: List<Zone>)

    @Query("DELETE FROM Zone")
    fun deleteZoneAll()

    /*  ------------ Country -----------   */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCountryAll(list: List<Country>)

    @Query("DELETE FROM Country")
    fun deleteCountryAll()


}
