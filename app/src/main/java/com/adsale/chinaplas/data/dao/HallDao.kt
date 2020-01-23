package com.adsale.chinaplas.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Created by Carrie on 2020/1/9.
 */
@Dao
interface HallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHallAll(list: List<Hall>)

    @Query("DELETE FROM Hall")
    fun deleteHallAll()

    @Query("select * from Hall order by HallID")
    fun getAllHalls(): MutableList<Hall>

    @Query("select H.*,count(H.HallID) AS count from Hall H,Exhibitor E where H.HallID=E.HallNo GROUP BY H.HallID ORDER BY HallID")
    fun getAllHallsWithCount(): MutableList<Hall>


}