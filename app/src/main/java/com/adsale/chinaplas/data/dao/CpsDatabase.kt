package com.adsale.chinaplas.data.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.adsale.chinaplas.data.entity.CountryJson
import com.adsale.chinaplas.utils.DATABASE_NAME

/**
 * Created by Carrie on 2019/10/21.
 * Visitor::class
 */
@Database(
    entities = [MainIcon::class, CountryJson::class, RegOptionData::class, WebContent::class, FileControl::class, HtmlText::class, Exhibitor::class,
        CompanyApplication::class, CompanyProduct::class, ExhApplication::class, ExhIndustry::class, ExhibitorZone::class, Zone::class, Country::class, Hall::class, ExhibitorHistory::class,
        ConcurrentEvent::class, SeminarInfo::class, SeminarSpeaker::class, EventApplication::class,
        NewtechProductImage::class, NewtechProductInfo::class, NewtechCategoryID::class, NewtechCategorySub::class, NewtechProductsID::class, NewtechArea::class
        , ScheduleInfo::class

    ],
    version = 10,
    exportSchema = false
)
abstract class CpsDatabase : RoomDatabase() {
    abstract fun mainIconDao(): MainIconDao
    abstract fun countryDao(): CountryJsonDao
    abstract fun regOptionDao(): RegOptionDataDao
    abstract fun webContentDao(): WebContentDao
    abstract fun htmlTextDao(): HtmlTextDao
    abstract fun fileControlDao(): FileControlDao
    abstract fun exhibitorDao(): ExhibitorDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun industryDao(): IndustryDao
    abstract fun regionDao(): RegionDao
    abstract fun hallDao(): HallDao
    abstract fun zoneDao(): ZoneDao
    abstract fun eventDao(): EventDao
    abstract fun seminarDao(): SeminarDao
    abstract fun eventApplicationDao(): EventApplicationDao
    abstract fun newtechDao(): NewtechDao
    abstract fun scheduleDao(): ScheduleInfoDao

    companion object {
        @Volatile
        private var instance: CpsDatabase? = null

        fun getInstance(context: Context): CpsDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context)
            }
        }

        private fun buildDatabase(context: Context): CpsDatabase {
            return Room.databaseBuilder(context, CpsDatabase::class.java, DATABASE_NAME)
                //                .addCallback(object : RoomDatabase.Callback() {
                //                    override fun onCreate(db: SupportSQLiteDatabase) {
                //                        super.onCreate(db)
                ////                        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                ////                        WorkManager.getInstance(context).enqueue(request)
                //                    }
                //                })
                .fallbackToDestructiveMigration()
                .build()
        }
    }

}