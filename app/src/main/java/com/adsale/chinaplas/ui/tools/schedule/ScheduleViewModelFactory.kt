package com.adsale.chinaplas.ui.tools.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adsale.chinaplas.data.dao.ScheduleRepository

/**
 * Created by Carrie on 2019/10/12.
 */
@Suppress("UNCHECKED_CAST")
class ScheduleViewModelFactory(
    private val scheduleRepository: ScheduleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java))
            return ScheduleViewModel(scheduleRepository) as T
        throw IllegalArgumentException("Unable to construct viewmodel")
    }


}