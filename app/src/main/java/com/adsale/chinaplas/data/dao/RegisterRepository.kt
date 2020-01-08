package com.adsale.chinaplas.data.dao

import androidx.lifecycle.LiveData
import com.adsale.chinaplas.data.entity.CountryJson
import com.adsale.chinaplas.utils.LANG_EN
import com.adsale.chinaplas.utils.LANG_TC
import com.adsale.chinaplas.utils.getCurrLanguage

/**
 * Created by Carrie on 2019/11/1.
 */
class RegisterRepository private constructor(
    private val countryDao: CountryJsonDao,
    private val regOptionDataDao: RegOptionDataDao
) {
    fun insertCountryAll(list: List<CountryJson>) = countryDao.insertAll(list)

    fun insertOptionDataAll(list: List<RegOptionData>) = regOptionDataDao.insertAll(list)

    fun insertItemOptionData(entity: RegOptionData) = regOptionDataDao.insertItem(entity)

    fun updateItemOptionData(entity: RegOptionData) = regOptionDataDao.updateItem(entity)

    //    fun updateItemOptionDataEN(entity: RegOptionData) = regOptionDataDao.updateItemEN(entity.DetailCode, entity.NameEN, entity.OrderEN)
    //    fun updateItemOptionDataSC(entity: RegOptionData) = regOptionDataDao.updateItemSC(entity.DetailCode, entity.NameSC, entity.OrderSC)

    fun getTitles(): List<RegOptionData> = regOptionDataDao.getTitles()
    fun getFunctions(): List<RegOptionData> = regOptionDataDao.getFunctions()
    fun getProductsSC(): List<RegOptionData> = regOptionDataDao.getProductsSC()
    fun getProductsTC(): List<RegOptionData> = regOptionDataDao.getProductsTC()
    fun getProductsEN(): List<RegOptionData> = regOptionDataDao.getProductsEN()
    fun getTitle(text: String): RegOptionData = regOptionDataDao.getTitle(text)
    fun getFunction(text: String): RegOptionData = regOptionDataDao.getFunction(text)

    fun getProducts(): MutableList<RegOptionData> {
        return when (getCurrLanguage()) {
            LANG_TC -> regOptionDataDao.getProductsTC()
            LANG_EN -> regOptionDataDao.getProductsEN()
            else -> regOptionDataDao.getProductsSC()
        }
    }

    fun insertRegOptionDataAll(list: List<RegOptionData>) = regOptionDataDao.insertAll(list)
    fun clearRegOptionData() = regOptionDataDao.clearAll()

    fun getCountries(): MutableList<CountryJson> =
        when (getCurrLanguage()) {
            LANG_TC -> countryDao.getCountriesTC()
            LANG_EN -> countryDao.getCountriesEN()
            else -> countryDao.getCountriesSC() as MutableList<CountryJson>
        }

    fun getProvinces(): MutableList<CountryJson> =
        when (getCurrLanguage()) {
            LANG_TC -> countryDao.getProvincesTC() as MutableList<CountryJson>
            LANG_EN -> countryDao.getProvincesEN() as MutableList<CountryJson>
            else -> countryDao.getProvincesSC() as MutableList<CountryJson>
        }

    fun getCities(province: String): List<CountryJson> =
        when (getCurrLanguage()) {
            LANG_TC -> countryDao.getCitiesTC(province)
            LANG_EN -> countryDao.getCitiesEN(province)
            else -> countryDao.getCitiesSC(province)
        }

    /*  获取单个地区的名字 */
    fun getSingleRegionName(code: String, level: Int): String =
        when (getCurrLanguage()) {
            LANG_TC -> countryDao.getSingleRegionNameTC(code, level)
            LANG_EN -> countryDao.getSingleRegionNameEN(code, level)
            else -> countryDao.getSingleRegionNameCN(code, level)
        }

    fun getRegionProvinceCityEntity(regionCode: String, provinceCode: String, cityCode: String)
            : List<CountryJson> {
        return countryDao.getRegionProvinceCityEntity(regionCode,provinceCode,cityCode)
    }

//    fun getProvinceName(code: String): String =
//        when (currLanguage) {
//            0 -> countryDao.getSingleProvinceNameTC(code)
//            1 -> countryDao.getSingleProvinceNameEN(code)
//            else -> countryDao.getSingleProviniceNameCN(code)
//        }
//
//    fun getCityName(code: String): String =
//        when (currLanguage) {
//            0 -> countryDao.getSingleCityNameTC(code)
//            1 -> countryDao.getSingleCityNameEN(code)
//            else -> countryDao.getSingleCityNameCN(code)
//        }

    fun clearAll() = countryDao.clearAll()

    fun getCountryLastUpdateTime() = countryDao.getCountryLastUpdateTime()

    fun getRegOptionLastUpdateTime() = regOptionDataDao.getLastUpdateTime()


    companion object {
        @Volatile
        private var instance: RegisterRepository? = null

        fun getInstance(countryDao: CountryJsonDao, regOptionDataDao: RegOptionDataDao) =
            instance ?: synchronized(this) {
                instance ?: RegisterRepository(countryDao, regOptionDataDao).also { instance = it }
            }
    }

}