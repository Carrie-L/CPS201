package com.adsale.chinaplas.viewmodels

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.RegisterRepository
import com.adsale.chinaplas.data.entity.CountryJson
import com.adsale.chinaplas.utils.*
import kotlinx.coroutines.*

interface RegionSelectCallback {
    fun onResult(
        combineText: String,   // 国家-省份-城市的组合文字
        countryEntity: CountryJson?,
        provinceEntity: CountryJson?,
        cityEntity: CountryJson?,
        tellRegionCode: String   // 国家/地区号
    )
}


/**
 * Created by Carrie on 2019/12/4.
 */
class RegRegionViewModel(val regRepo: RegisterRepository) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)


    // -- 国家地区 --
    val selectTitle =
        if (getCurrLanguage() == LANG_TC) SELECT_TITLE_TC else if (getCurrLanguage() == LANG_EN) SELECT_TITLE_EN else SELECT_TITLE_SC

    var list = MutableLiveData<MutableList<CountryJson>>()
    private var provinceList = mutableListOf<CountryJson>()
    private var cityList = mutableListOf<CountryJson>()
    private var countryList = mutableListOf<CountryJson>()
    var refreshList = true

    var provinceVisible = MutableLiveData<Int>(View.GONE)
    var cityVisible = MutableLiveData<Int>(View.GONE)
    private var selectedProvince: String = ""   // Province Code

    private var combineText: String = ""
    private var countryEntity: CountryJson? = null
    private var provinceEntity: CountryJson? = null
    private var cityEntity: CountryJson? = null
    private var telCode = ""  // 区号

    var indexClicked = MutableLiveData(0)  // index 3个短横线，哪个被点击，就显示哪个； 0 国家地区，1 省份。2 城市
    private var preClickPos0: Int = 0
    private var preClickPos1: Int = 0
    private var preClickPos2: Int = 0
    var currentPos: Int = 0
    var prePos: Int = 0

    var isChinaSelected = true
    var dialogDismiss = MutableLiveData<Boolean>(false)

    //    var region = MutableLiveData<String>()  // 国家 + 省份 + 城市 ， 在fragment_register中显示
    var regionText = MutableLiveData<String>(selectTitle)  // 显示在对话框页面的文字
    var provinceText = MutableLiveData<String>(selectTitle)
    var cityText = MutableLiveData<String>(selectTitle)
    private var regionDetailCode = MutableLiveData<String>(selectTitle)  // 选中的国家/地区的 DetailCode
    private var provinceDetailCode = MutableLiveData<String>(selectTitle)
    private var cityDetailCode = MutableLiveData<String>(selectTitle)

    private lateinit var selectListener: RegionSelectCallback

    init {

        LogUtil.i("~~~~~~~~~~~RegRegionViewModel init~~~~~~~~~~~~~~~~")

        regionDetailCode.value = getRegion()
        provinceDetailCode.value = getProvince()
        cityDetailCode.value = getCity()
        selectedProvince = getProvince()

        initDialog()

    }

    fun initDialog() {
        selectedProvince = getProvince()
        refreshList = true
        list.value?.clear()
//        if (!TextUtils.isEmpty(selectedProvince)) {
//            getCities()
//        }
        getCountries()
        initRegionCode()
    }

    fun setRegionSelectCallback(listener: RegionSelectCallback) {
        selectListener = listener
    }

    fun setRegionCode(regionDCode: String, provinceDCode: String, cityDCode: String) {
        regionDetailCode.value = regionDCode
        provinceDetailCode.value = provinceDCode
        cityDetailCode.value = cityDCode

        if (!TextUtils.isEmpty(regionDCode) && !TextUtils.isEmpty(provinceDCode)
            && !TextUtils.isEmpty(cityDCode)
            && regionDCode == "CN"
        ) {
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    val list =
                        regRepo.getRegionProvinceCityEntity(regionDCode, provinceDCode, cityDCode)


                }
            }

        }
    }

    private fun getCountries() {
        if (list.value == null || list.value?.size == 0) {
            uiScope.launch {
                list.value = getCountriesFromDB()
                countryList = list.value as MutableList<CountryJson>
                LogUtil.i("getCountries=${list.value?.size}, ${list.toString()}")
                setListChecked(regionDetailCode.value!!, 1)
            }
        } else {
            labelRegionClick()
            setListChecked(regionDetailCode.value!!, 1)
        }
    }

    private fun setListChecked(code: String, level: Int) {
        if (TextUtils.isEmpty(code)) {
            return
        }
        val temps = list.value
        var i = 0
        if (temps != null) {
            for (item in temps) {
                if (item.Code == code && item.Level == "$level") {
                    item.isChecked.set(true)
                    temps[i] = item
                    when (level) {
                        1 -> preClickPos0 = i
                        2 -> preClickPos1 = i
                        3 -> preClickPos2 = i
                    }
                    telCode = item.CountryTelCode
                    break
                }
                i++
            }
        }
        if (level == 1) {
            if (temps != null) {
                countryList = temps
            }
        }
        list.value = temps
    }


    private suspend fun getCountriesFromDB(): MutableList<CountryJson> {
        return withContext(Dispatchers.IO) {
            regRepo.getCountries()
        }
    }

    private fun initRegionCode() {
        uiScope.launch {
            val sb = StringBuilder()
            if (!TextUtils.isEmpty(getRegion())) {
                regionText.value = getSingleRegionFromDB(regionDetailCode.value!!, 1)
                sb.append(regionText.value)
            }
            if (!TextUtils.isEmpty(getProvince())) {
                provinceVisible.value = View.VISIBLE
                provinceText.value = getSingleRegionFromDB(provinceDetailCode.value!!, 2)
                sb.append(" ").append(provinceText.value)
            }
            if (!TextUtils.isEmpty(getCity())) {
                cityVisible.value = View.VISIBLE
                cityText.value = getSingleRegionFromDB(cityDetailCode.value!!, 3)
                sb.append(" ").append(cityText.value)
            }
//            region.value = sb.toString()
        }
    }


    private fun initProvince() {
        if (provinceList.size == 0) {
            return
        }
        provinceText.value = selectTitle
        val item: CountryJson = provinceList[preClickPos1]
        item.isChecked.set(false)
        provinceList[preClickPos1] = item
        preClickPos1 = 0
        setProvince("")
        provinceEntity = null
    }

    private fun initCity() {
        if (cityList.size == 0) {
            return
        }
        cityText.value = selectTitle
        val item: CountryJson = cityList[preClickPos2]
        item.isChecked.set(false)
        cityList[preClickPos2] = item
        preClickPos2 = 0
        setCity("")
        setPostCity("")
        cityEntity = null
    }

    private fun getProvinces() {
        uiScope.launch {
            list.value = getProvincesFromDB()
            provinceList = list.value as MutableList<CountryJson>
            LogUtil.i("getProvinces=${list.value?.size}")
            refreshList = true
            setListChecked(provinceDetailCode.value!!, 2)
        }
    }

    private fun getCities() {
        uiScope.launch {
            list.value = getCitiesFromDB() as MutableList<CountryJson>
            cityList = list.value as MutableList<CountryJson>
            LogUtil.i("getProvinces=${list.value?.size}")
            setListChecked(cityDetailCode.value!!, 3)
        }
    }

    private suspend fun getProvincesFromDB(): MutableList<CountryJson> {
        return withContext(Dispatchers.IO) {
            regRepo.getProvinces()
        }
    }

    private suspend fun getCitiesFromDB(): List<CountryJson> {
        return withContext(Dispatchers.IO) {
            regRepo.getCities(selectedProvince)
        }
    }

    fun labelRegionClick() {
        refreshList = true
        indexClicked.value = 0
        list.value = countryList
    }

    fun labelProvinceClick() {
        setProvinceList()
        if (provinceList.size == 0) {
            getProvinces()
        }
    }

    private fun setProvinceList() {
        refreshList = true
        indexClicked.value = 1
        list.value = provinceList
    }

    fun labelCityClick() {
        refreshList = true
        indexClicked.value = 2
        list.value = cityList
        if (cityList.isEmpty()) {
            getCities()
        }
    }

    private fun clickRegion(item: CountryJson, pos: Int) {
        currentPos = pos
        prePos = preClickPos0
        regionText.value = item.getName()
        regionDetailCode.value = item.Code
        setRegion(item.Country)

        if (item.Code == "CN" && item.AdcCountryId == "44") {
            isChinaSelected = true
            refreshList = true
            if (provinceList.size == 0) {
                getProvinces()
            } else {
                setProvinceList()
            }
            indexClicked.value = 1
            provinceVisible.value = View.VISIBLE
        } else {
            isChinaSelected = false
            indexClicked.value = 0
            refreshList = false
            // 清除省份城市信息
            // 当第一次选择中国-省份-城市后，第二次选择了其他国家，就要将省份城市的储存记录清除掉
            provinceVisible.value = View.GONE
            cityVisible.value = View.GONE
            initProvince()
            initCity()

            combineText = item.getName()
        }

        // switch checked item
        item.isChecked.set(true)
        if (pos != preClickPos0) {
            val preItem = countryList[preClickPos0]
            preItem.isChecked.set(false)
            countryList[pos] = item
            countryList[preClickPos0] = preItem
            if (indexClicked.value == 0) {
                list.value = countryList
            }
        }
        preClickPos0 = pos

        countryEntity = item
        telCode = item.CountryTelCode

        if (!isChinaSelected) {
            selectListener.onResult(
                combineText,
                countryEntity, provinceEntity, cityEntity,
                telCode
            )
            dialogDismiss.value = true
        }
    }

    private fun clickProvince(item: CountryJson, pos: Int) {
        refreshList = true
        currentPos = pos
        prePos = preClickPos1
        provinceText.value = item.getName()
        provinceVisible.value = View.VISIBLE
        cityVisible.value = View.VISIBLE
        selectedProvince = item.Province
        item.isChecked.set(true)
        provinceDetailCode.value = item.Code

        if (pos != preClickPos1) {
            val preItem = provinceList[preClickPos1]
            preItem.isChecked.set(false)
            provinceList[pos] = item
            provinceList[preClickPos1] = preItem
            list.value = provinceList
        }

        indexClicked.value = 2
        if (pos == preClickPos1 && cityList.isNotEmpty()) {
            list.value = cityList
        } else {
            cityText.value = selectTitle
            getCities()
        }
        preClickPos1 = pos
        setProvince(item.Province)
        provinceEntity = item
    }

    private fun clickCity(item: CountryJson, pos: Int) {
        if (cityList.isEmpty()) {
            getCities()
        }


        refreshList = false
        currentPos = pos
        prePos = preClickPos2
        cityText.value = item.getName()
        provinceVisible.value = View.VISIBLE
        cityVisible.value = View.VISIBLE
        indexClicked.value = 2
        cityDetailCode.value = item.Code
        item.isChecked.set(true)
        if (pos != preClickPos2) {
            val preItem = cityList[preClickPos2]
            preItem.isChecked.set(false)
            cityList[pos] = item
            cityList[preClickPos2] = preItem
            list.value = cityList
        }
        preClickPos2 = pos
        setCity(item.Code)
        setPostCity(item.getName())
        combineText = regionText.value + " " + provinceText.value + " " + cityText.value
        cityEntity = item
        selectListener.onResult(
            combineText,
            countryEntity, provinceEntity, cityEntity,
            telCode
        )
        dialogDismiss.value = true
    }

    fun onDialogConfirm() {
        dialogDismiss.value = true

        selectListener.onResult(
            combineText,
            countryEntity,
            provinceEntity,
            cityEntity,
            telCode
        )
    }

    // todo 跳转到特定位置
    val itemClickListener = OnItemClickListener { item, pos ->
        item as CountryJson
        LogUtil.i("clicked: ${item.Code} ${item.Name_Sc}")
        when (indexClicked.value) {
            0 -> clickRegion(item, pos)
            1 -> clickProvince(item, pos)
            2 -> clickCity(item, pos)
        }
    }

    private suspend fun getSingleRegionFromDB(code: String, level: Int): String {
        return withContext(Dispatchers.IO) {
            regRepo.getSingleRegionName(code, level)
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
        list.value?.clear()
        countryList.clear()
        provinceList.clear()
        cityList.clear()
        isChinaSelected = false
        refreshList = true
        dialogDismiss.value = false

    }


}