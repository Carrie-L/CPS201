package com.adsale.chinaplas.data.dao

import okhttp3.internal.Internal.instance


/**
 * Created by Carrie on 2019/10/21.
 */
class MainIconRepository private constructor(private val mainIconDao: MainIconDao) {

    fun getMainIcons() = mainIconDao.getAllMainIcons()

    fun getDrawerIcons() = mainIconDao.getDrawerMainIcons()

    fun insertAll(list: List<MainIcon>) = mainIconDao.insertAll(list)

    fun insertItemIcon(icon: MainIcon) = mainIconDao.insertItemIcon(icon)

    fun getMainIconLUT() = mainIconDao.getLastUpdateTime()






    companion object {
        @Volatile
        private var instance: MainIconRepository? = null

        fun getInstance(mainIconDao: MainIconDao) =
            instance ?: synchronized(this) {
                instance ?: MainIconRepository(mainIconDao).also { instance = it }
            }
    }


}