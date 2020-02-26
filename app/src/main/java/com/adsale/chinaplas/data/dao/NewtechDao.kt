package com.adsale.chinaplas.data.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface NewtechDao {


    @Query("select N.RID,N.CompanyID,N.BoothNo,N.CompanyNameSc,N.Product_Name_SC,N.Rroduct_Description_SC,I.Image_File AS imageName from NewtechProductInfo N, NewtechProductImage I WHERE N.RID=I.RID and N.Product_Name_SC IS NOT NULL and N.Product_Name_SC<>'' order by N.RID")
    fun getAllProductInfoListSC(): MutableList<NewtechProductInfo>

    @Query("select N.RID,N.CompanyID,N.BoothNo,N.CompanyNameTc,N.Product_Name_TC,N.Rroduct_Description_TC,I.Image_File AS imageName from NewtechProductInfo N, NewtechProductImage I WHERE N.RID=I.RID and N.Product_Name_TC IS NOT NULL and N.Product_Name_TC<>'' order by N.RID")
    fun getAllProductInfoListTC(): MutableList<NewtechProductInfo>

    @Query("select N.RID,N.CompanyID,N.BoothNo,N.CompanyNameEn,N.Product_Name_EN,N.Rroduct_Description_EN,I.Image_File AS imageName from NewtechProductInfo N, NewtechProductImage I WHERE N.RID=I.RID and N.Product_Name_EN IS NOT NULL and N.Product_Name_EN<>'' order by N.RID")
    fun getAllProductInfoListEN(): MutableList<NewtechProductInfo>

    /*filter*/
    @RawQuery
    fun getFilterList(query: SupportSQLiteQuery): MutableList<NewtechProductInfo>

    /* ------- Detail ---------- */
    @Query("select N.*,I.Image_File AS imageName from NewtechProductInfo N, NewtechProductImage I WHERE N.RID=:rid AND N.RID=I.RID and N.Product_Name_EN IS NOT NULL and N.Product_Name_EN<>''  order by N.RID")
    fun getItemInfo(rid: String): NewtechProductInfo


    /* ------- CategorySub ---------- */
    @Query("select * from NewtechCategorySub where MainTypeId=:type order by cast(TCSort as Int)")
    fun getFilterProductsTC(type: String): MutableList<NewtechCategorySub>

    @Query("select * from NewtechCategorySub where MainTypeId=:type order by cast(SCSort as Int)")
    fun getFilterProductsSC(type: String): MutableList<NewtechCategorySub>

    @Query("select * from NewtechCategorySub where MainTypeId=:type order by cast(ENSort as Int)")
    fun getFilterProductsEN(type: String): MutableList<NewtechCategorySub>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategoryIDAll(list: List<NewtechCategoryID>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategorySubAll(list: List<NewtechCategorySub>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductsIDAll(list: List<NewtechProductsID>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductInfoAll(list: List<NewtechProductInfo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAreaAll(list: List<NewtechArea>)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductImageAll(list: List<NewtechProductImage>)

    @Query("DELETE FROM NewtechCategoryID")
    fun deleteCategoryIDAll()

    @Query("DELETE FROM NewtechCategorySub")
    fun deleteCategorySubAll()

    @Query("DELETE FROM NewtechProductsID")
    fun deleteProductsIDAll()

    @Query("DELETE FROM NewtechProductInfo")
    fun deleteProductInfoAll()

    @Query("DELETE FROM NewtechProductImage")
    fun deleteProductImageDAll()

    @Query("DELETE FROM NewtechArea")
    fun deleteAreaAll()

}