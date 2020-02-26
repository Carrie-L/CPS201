package com.adsale.chinaplas.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by Carrie on 2020/1/9.
 */
@Dao
interface ApplicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertApplicationAll(list: List<ExhApplication>)

    @Query("DELETE FROM ExhApplication")
    fun deleteApplicationAll()

    @Query("select * from ExhApplication order by SCPY")
    fun getAllApplicationSC(): MutableList<ExhApplication>

    @Query("select * from ExhApplication order by TCStroke")
    fun getAllApplicationTC(): MutableList<ExhApplication>

    @Query("select * from ExhApplication order by ApplicationEng")
    fun getAllApplicationEN(): MutableList<ExhApplication>

    @Query("select A.* from ExhApplication A, CompanyApplication CA where A.IndustryID = CA.IndustryID AND CA.CompanyID =:companyID ORDER BY  SCPY")
    fun getApplicationSC(companyID: String): MutableList<ExhApplication>

    @Query("select A.* from ExhApplication A, CompanyApplication CA where A.IndustryID = CA.IndustryID AND CA.CompanyID = :companyID ORDER BY  TCStroke")
    fun getApplicationTC(companyID: String): MutableList<ExhApplication>

    @Query("select A.* from ExhApplication A, CompanyApplication CA where A.IndustryID = CA.IndustryID AND CA.CompanyID =:companyID ORDER BY  ApplicationEng")
    fun getApplicationEN(companyID: String): MutableList<ExhApplication>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompanyApplicationAll(list: List<CompanyApplication>)

    @Query("DELETE FROM CompanyApplication")
    fun deleteCompanyApplicationAll()

    /*   ------Event Application------   */
    @Query("select Max(updatedAt) from EventApplication")
    fun getEVLastUpdateTime(): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEVAll(list: List<EventApplication>)

    @Query("select * from EventApplication order by SortCN")
    fun getEVCNList(): List<EventApplication>

    @Query("select * from EventApplication order by SortEN")
    fun getEVENList(): List<EventApplication>

}