package com.adsale.chinaplas.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by Carrie on 2020/1/9.
 */
@Dao
interface IndustryDao {
    /*  ------------ Industry -----------   */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIndustryAll(list: List<ExhIndustry>)

    @Query("DELETE FROM ExhIndustry")
    fun deleteIndustryAll()

    @Query("select * from ExhIndustry order by CAST(TCStroke AS INT) ")
    fun getAllIndustriesTC(): MutableList<ExhIndustry>

    @Query("select * from ExhIndustry order by SCPY")
    fun getAllIndustriesSC(): MutableList<ExhIndustry>

    @Query("select * from ExhIndustry order by SortEN")
    fun getAllIndustriesEN(): MutableList<ExhIndustry>

    @Query("select A.* from ExhIndustry A, CompanyProduct CA where A.CatalogProductSubID = CA.CatalogProductSubID AND CA.CompanyID = :companyID ORDER BY SortEN")
    fun getIndustriesEN(companyID: String): MutableList<ExhIndustry>

    @Query("select A.* from ExhIndustry A, CompanyProduct CA where A.CatalogProductSubID = CA.CatalogProductSubID AND CA.CompanyID = :companyID ORDER BY SCPY")
    fun getIndustriesSC(companyID: String): MutableList<ExhIndustry>

    @Query("select A.* from ExhIndustry A, CompanyProduct CA where A.CatalogProductSubID = CA.CatalogProductSubID AND CA.CompanyID = :companyID ORDER BY TCStroke")
    fun getIndustriesTC(companyID: String): MutableList<ExhIndustry>


    /*  ------------ CompanyProduct -----------   */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompanyProductAll(list: List<CompanyProduct>)

    @Query("DELETE FROM CompanyProduct")
    fun deleteCompanyProductAll()
}