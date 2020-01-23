package com.adsale.chinaplas.helper

import android.content.Context
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.ExhibitorRepository
import com.adsale.chinaplas.viewmodels.MyExhibitorViewModelFactory

/**
 * Created by Carrie on 2020/1/16.
 */
object InjectionUtils {

    private fun getExhibitorRepository(context: Context): ExhibitorRepository {
        return ExhibitorRepository.getInstance(
            CpsDatabase.getInstance(context.applicationContext).exhibitorDao(),
            CpsDatabase.getInstance(context.applicationContext).industryDao(),
            CpsDatabase.getInstance(context.applicationContext).applicationDao())
    }

    fun provideMyExhibitorViewModelFactory(context: Context): MyExhibitorViewModelFactory {
        val repository = getExhibitorRepository(context)
        return MyExhibitorViewModelFactory(repository)
    }




}