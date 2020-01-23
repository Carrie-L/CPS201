package com.adsale.chinaplas.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by Carrie on 2020/1/9.
 */
@Dao
interface ZoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertZoneAll(list: List<Zone>)

    @Query("DELETE FROM Zone")
    fun deleteZoneAll()

    @Query("select * from Zone order by ThemeZoneCode")
    fun getAllZones(): MutableList<Zone>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExhibitorZoneAll(list: List<ExhibitorZone>)

    @Query("DELETE FROM ExhibitorZone")
    fun deleteExhibitorZoneAll()

}