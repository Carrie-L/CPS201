package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.adsale.chinaplas.data.dao.RegisterRepository

/**
 * Created by Carrie on 2019/11/4.
 */
@Suppress("UNCHECKED_CAST")
class RegViewModelFactory(val regRepo:RegisterRepository) :ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(regRepo = regRepo) as T
        }
        if(modelClass.isAssignableFrom(RegPickViewModel::class.java)){
            return RegPickViewModel(regRepo = regRepo) as T
        }
        if(modelClass.isAssignableFrom(RegRegionViewModel::class.java)){
            return RegRegionViewModel(regRepo = regRepo) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}