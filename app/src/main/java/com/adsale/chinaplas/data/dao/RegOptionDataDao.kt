package com.adsale.chinaplas.data.dao

import androidx.room.*

/**
 * Created by Carrie on 2019/11/1.
 */
@Dao
interface RegOptionDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<RegOptionData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(entity: RegOptionData)

    @Update
    fun updateAll(list: List<RegOptionData>)

    @Update
    fun updateItem(entity: RegOptionData)

    @Query("update RegOptionData set NameEN=:name,OrderEN=:order where DetailCode=:detailCode")
    fun updateItemEN(detailCode: String, name: String, order: String)

    @Query("update RegOptionData set NameSC=:name,OrderSC=:order where DetailCode=:detailCode")
    fun updateItemSC(detailCode: String, name: String, order: String)

    @Query("select * from RegOptionData where PartName='JobTitleList'")
    fun getTitles(): List<RegOptionData>

    @Query("select * from RegOptionData where PartName='JobTitleList' and NameSC=:text or NameEN=:text or NameTC=:text ")
    fun getTitle(text:String): RegOptionData

    @Query("select * from RegOptionData where PartName='JobFunctionList'")
    fun getFunctions(): List<RegOptionData>

    @Query("select * from RegOptionData where PartName='JobFuctionList' and NameSC=:text or NameEN=:text or NameTC=:text ")
    fun getFunction(text:String): RegOptionData


    @Query("select * from RegOptionData where PartName='ProductList' Order by OrderSC")
    fun getProductsSC(): MutableList<RegOptionData>

    @Query("select * from RegOptionData where PartName='ProductList' Order by OrderTC")
    fun getProductsTC(): MutableList<RegOptionData>

    @Query("select * from RegOptionData where PartName='ProductList' Order by OrderEN")
    fun getProductsEN(): MutableList<RegOptionData>

    @Query("select Max(updatedAt) from RegOptionData")
    fun getLastUpdateTime(): String

    @Query("delete from RegOptionData")
    fun clearAll()


}