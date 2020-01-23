package com.adsale.chinaplas.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by Carrie on 2020/1/9.
 */
@Dao
interface RegionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCountryAll(list: List<Country>)

    @Query("DELETE FROM Country")
    fun deleteCountryAll()

    //    @Query("select CountryID,CountrySC,SortSC from Country order by SortSC")
    @Query("SELECT C.CountryID,C.CountrySC,C.SortSC FROM Country C, EXHIBITOR E WHERE E.CountryID = C.CountryID GROUP BY C.CountryID ORDER BY SortSC")
    fun getAllCountriesSC(): MutableList<Country>

    //    @Query("select C.CountryID,C.CountryTC,C.SortTC from Country order by CAST(SortTC AS INT)")
    @Query("SELECT C.CountryID,C.CountryTC,C.SortTC FROM Country C, EXHIBITOR E WHERE E.CountryID = C.CountryID GROUP BY C.CountryID ORDER BY CAST(SortTC AS INT)")
    fun getAllCountriesTC(): MutableList<Country>

    //    @Query("select CountryID,CountryEng,SortEN from Country order by SortEN")
    @Query("select C.CountryID,C.CountryEng,C.SortEN FROM Country C, EXHIBITOR E WHERE E.CountryID = C.CountryID GROUP BY C.CountryID order by SortEN")
    fun getAllCountriesEN(): MutableList<Country>
}