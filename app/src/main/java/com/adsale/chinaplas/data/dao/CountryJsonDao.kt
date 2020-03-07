package com.adsale.chinaplas.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adsale.chinaplas.data.entity.CountryJson

/**
 * Created by Carrie on 2019/11/1.
 */
@Dao
interface CountryJsonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<CountryJson>)

    @Query("SELECT * from CountryJson where Level='1' order by DisplayOrder desc,OrderTC")
    fun getCountriesTC(): MutableList<CountryJson>

    @Query("SELECT * from CountryJson where Level='1' order by DisplayOrder desc,OrderEN")
    fun getCountriesEN(): MutableList<CountryJson>

    @Query("SELECT * from CountryJson where Level='1' order by DisplayOrder desc,OrderSC")
    fun getCountriesSC(): List<CountryJson>

    @Query("SELECT * from CountryJson where Level='2' ")
    fun getProvincesTC(): List<CountryJson>

    @Query("SELECT * from CountryJson where Level='2' ")
    fun getProvincesEN(): List<CountryJson>

    @Query("SELECT * from CountryJson where Level='2' ")
    fun getProvincesSC(): List<CountryJson>

    @Query("SELECT * from CountryJson where Level='3' and Province=:province ")
    fun getCitiesTC(province: String): List<CountryJson>

    @Query("SELECT * from CountryJson where Level='3' and Province=:province ")
    fun getCitiesEN(province: String): List<CountryJson>

    @Query("SELECT * from CountryJson where Level='3' and Province=:province ")
    fun getCitiesSC(province: String): List<CountryJson>

    @Query("delete from CountryJson")
    fun clearAll()

    @Query("select Max(updatedAt) from CountryJson")
    fun getCountryLastUpdateTime(): String

    /*  获取单个地区的名字 */
    @Query("select Name_Sc from CountryJson where  Code=:code AND Level=1")
    fun getSingleCountryNameCN(code: String): String

    @Query("select Name_Eng from CountryJson where  Code=:code AND Level=1")
    fun getSingleCountryNameEN(code: String): String

    @Query("select Name_Tc from CountryJson where  Code=:code AND Level=1")
    fun getSingleCountryNameTC(code: String): String

    @Query("select Name_Sc from CountryJson where  Code=:code AND Level=2")
    fun getSingleProviniceNameCN(code: String): String

    @Query("select Name_Eng from CountryJson where  Code=:code AND Level=2")
    fun getSingleProvinceNameEN(code: String): String

    @Query("select Name_Tc from CountryJson where  Code=:code AND Level=2")
    fun getSingleProvinceNameTC(code: String): String

    @Query("select Name_Sc from CountryJson where  Code=:code AND Level=:level")
    fun getSingleRegionNameCN(code: String, level: Int): String

    @Query("select Name_Eng from CountryJson where  Code=:code AND Level=:level")
    fun getSingleRegionNameEN(code: String, level: Int): String

    @Query("select Name_Tc from CountryJson where  Code=:code AND Level=:level")
    fun getSingleRegionNameTC(code: String, level: Int): String


    @Query(" select * from CountryJson where (Code=:regionCode and Level=1) or (Code=:provinceCode and Level=2) or (Code=:cityCode and Level=3)")
    fun getRegionProvinceCityEntity(
        regionCode: String,
        provinceCode: String,
        cityCode: String
    ): List<CountryJson>


}