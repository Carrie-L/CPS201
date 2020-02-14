package com.adsale.chinaplas.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adsale.chinaplas.data.dao.*

/**
 * Created by Carrie on 2019/10/12.
 */
@Suppress("UNCHECKED_CAST")
class LoadingViewModelFactory(val app: Application, private val regRepo: RegisterRepository, private val wcRepository: WebContentRepository,
                              private val exhibitorDao: ExhibitorDao,private val eventRepository: EventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoadingViewModel::class.java))
            return LoadingViewModel(app, regRepo, wcRepository,exhibitorDao,eventRepository) as T
        throw IllegalArgumentException("Unable to construct viewmodel")
    }


}