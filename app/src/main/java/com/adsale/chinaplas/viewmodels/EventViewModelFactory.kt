package com.adsale.chinaplas.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adsale.chinaplas.data.dao.EventRepository
import com.adsale.chinaplas.data.dao.ExhibitorRepository
import com.adsale.chinaplas.data.dao.MainIconRepository

/**
 * Created by Carrie on 2019/10/12.
 */
@Suppress("UNCHECKED_CAST")
class EventViewModelFactory(private val eventRepository: EventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java))
            return EventViewModel(eventRepository) as T
        throw IllegalArgumentException("Unable to construct viewmodel")
    }


}