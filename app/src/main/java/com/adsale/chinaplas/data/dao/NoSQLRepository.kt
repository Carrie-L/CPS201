package com.adsale.chinaplas.data.dao

import okhttp3.internal.Internal.instance


/**
 * Created by Carrie on 2019/10/21.
 */
class NoSQLRepository private constructor(private val mainIconDao: MainIconDao) {

    fun getMainIcons() = mainIconDao.getAllMainIcons()

    fun getDrawerIcons() = mainIconDao.getDrawerMainIcons()

    fun insertAll(list: List<MainIcon>) = mainIconDao.insertAll(list)

    fun insertItemIcon(icon: MainIcon) = mainIconDao.insertItemIcon(icon)


    /*  NoSQL*/
    fun getNoSqlLastUpdateTime() = mainIconDao.getLastUpdateTime()




    companion object {
        @Volatile
        private var instance: NoSQLRepository? = null

        fun getInstance(mainIconDao: MainIconDao) =
            instance ?: synchronized(this) {
                instance ?: NoSQLRepository(mainIconDao).also { instance = it }
            }
    }


}