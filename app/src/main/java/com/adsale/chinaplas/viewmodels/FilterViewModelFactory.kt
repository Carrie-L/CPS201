package com.adsale.chinaplas.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adsale.chinaplas.data.dao.*

/**
 * Created by Carrie on 2019/10/12.
 */
@Suppress("UNCHECKED_CAST")
class FilterViewModelFactory(
    private val applicationDao: ApplicationDao,
    private val industryDao: IndustryDao,
    private val regionDao: RegionDao,
    private val hallDao: HallDao,
    private val zoneDao: ZoneDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilterViewModel::class.java))
            return FilterViewModel(applicationDao, industryDao, regionDao, hallDao, zoneDao) as T
        throw IllegalArgumentException("Unable to construct viewmodel")
    }


}