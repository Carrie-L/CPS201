package com.adsale.chinaplas.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.*
import com.adsale.chinaplas.data.entity.ExhibitorFilter
import com.adsale.chinaplas.ui.exhibitors.TYPE_NEW_TECH_AREA
import com.adsale.chinaplas.ui.exhibitors.TYPE_NEW_TEC_APPLICATIONS
import com.adsale.chinaplas.ui.exhibitors.TYPE_NEW_TEC_PRODUCT
import com.adsale.chinaplas.ui.exhibitors.TYPE_NEW_TEC_THEMATIC
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.utils.LogUtil.i
import kotlinx.coroutines.*
import java.util.*


/**
 * Created by Carrie on 2020/1/8.
 */
class FilterViewModel(
    private val applicationDao: ApplicationDao,
    private val industryDao: IndustryDao,
    private val regionDao: RegionDao,
    private val hallDao: HallDao,
    private val zoneDao: ZoneDao) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private var _applications = MutableLiveData<MutableList<ExhApplication>>()
    val applications: MutableLiveData<MutableList<ExhApplication>>
        get() = _applications

    var industries = MutableLiveData<MutableList<ExhIndustry>>()
    var regions = MutableLiveData<MutableList<Country>>()
    var halls = MutableLiveData<MutableList<Hall>>()
    var zones = MutableLiveData<MutableList<Zone>>()

    /* 新技术 */
    var themes = MutableLiveData<MutableList<ExhApplication>>()
    var newTechs = MutableLiveData<MutableList<ExhApplication>>()

    var appFilters = MutableLiveData<MutableList<ExhibitorFilter>>()
    var industryFilters = MutableLiveData<MutableList<ExhibitorFilter>>()
    var regionFilters = MutableLiveData<MutableList<ExhibitorFilter>>()
    var hallFilters = MutableLiveData<MutableList<ExhibitorFilter>>()
    var zoneFilters = MutableLiveData<MutableList<ExhibitorFilter>>()

    /* 新技术产品 */
    var newApplFilters = MutableLiveData<MutableList<ExhibitorFilter>>()
    var newProdFilters = MutableLiveData<MutableList<ExhibitorFilter>>()
    var themeFilters = MutableLiveData<MutableList<ExhibitorFilter>>()
    var newFilters = MutableLiveData<MutableList<ExhibitorFilter>>()

    var allFilters = mutableListOf<ExhibitorFilter>()


    private var _filterStartNavigate = MutableLiveData(false)
    val filterStartNavigate: MutableLiveData<Boolean>
        get() = _filterStartNavigate

    private var _clearClicked = MutableLiveData(false)
    val clearClicked: MutableLiveData<Boolean>
        get() = _clearClicked

    init {
        i("FilterViewModel init~ (#^.^#)")
        appFilters.value = mutableListOf()
        industryFilters.value = mutableListOf()
        regionFilters.value = mutableListOf()
        hallFilters.value = mutableListOf()
        zoneFilters.value = mutableListOf()
        themeFilters.value = mutableListOf()
        newFilters.value = mutableListOf()
        newApplFilters.value = mutableListOf()
        newProdFilters.value = mutableListOf()
        allFilters = mutableListOf()
    }

    fun getAllApplications() {
        uiScope.launch {
            _applications.value = getApplicationsFromDB()
        }
    }

    fun getAllIndustries() {
        uiScope.launch {
            i("language0 = ${getCurrLanguage()}")
            industries.value = getIndustryFromDB()
        }
    }

    fun getAllRegions() {
        uiScope.launch {
            regions.value = getRegionFromDB()
        }
    }

    fun getAllHalls() {
        uiScope.launch {
            halls.value = getHallFromDB()
        }
    }

    fun getAllZones() {
        uiScope.launch {
            zones.value = getZonesFromDB()
        }
    }

    private suspend fun getApplicationsFromDB(): MutableList<ExhApplication> {
        return withContext(Dispatchers.IO) {
            when (getCurrLanguage()) {
                LANG_SC -> applicationDao.getAllApplicationSC()
                LANG_TC -> applicationDao.getAllApplicationTC()
                else -> applicationDao.getAllApplicationEN()
            }
        }
    }

    private suspend fun getIndustryFromDB(): MutableList<ExhIndustry> {
        return withContext(Dispatchers.IO) {
            when (getCurrLanguage()) {
                LANG_SC -> industryDao.getAllIndustriesSC()
                LANG_TC -> industryDao.getAllIndustriesTC()
                else -> industryDao.getAllIndustriesEN()
            }
        }
    }

    private suspend fun getRegionFromDB(): MutableList<Country> {
        return withContext(Dispatchers.IO) {
            when (getCurrLanguage()) {
                LANG_SC -> regionDao.getAllCountriesSC()
                LANG_TC -> regionDao.getAllCountriesTC()
                else -> regionDao.getAllCountriesEN()
            }
        }
    }

    private suspend fun getHallFromDB(): MutableList<Hall> {
        return withContext(Dispatchers.IO) {
            hallDao.getAllHallsWithCount()
        }
    }

    private suspend fun getZonesFromDB(): MutableList<Zone> {
        return withContext(Dispatchers.IO) {
            zoneDao.getAllZones()
        }
    }

    /*===========  其他类型的列表  =================*/
    val tempApplicationList: MutableList<ExhApplication> = mutableListOf()
    var entity: ExhApplication = ExhApplication()

    /* 同期活动 应用行业 筛选 */
    fun getEventApplications() {
        uiScope.launch {
            val eventApplications = getEventApplicationsFromDB()
            for (app in eventApplications) {
                entity =
                    ExhApplication(app.IndustryID, app.ApplicationEng, app.ApplicationTC, app.ApplicationSC, null, null)
                tempApplicationList.add(entity)
            }
            _applications.value = tempApplicationList
        }
    }

    private suspend fun getEventApplicationsFromDB(): List<EventApplication> {
        return withContext(Dispatchers.IO) {
            when (getCurrLanguage()) {
                LANG_EN -> applicationDao.getEVENList()
                else -> applicationDao.getEVCNList()
            }
        }
    }


    /*  新技术产品 - 筛选 */
    private var newtechRepository: NewtechRepository? = null
    private var newtechType = ""
    fun setNewtechType(type: String) {
        newtechType = type
    }

    fun setNewtechRepository(repository: NewtechRepository) {
        newtechRepository = repository
    }

    fun getNewtechList(type: String) {
        uiScope.launch {
            _applications.value = getNewtechApplicationsFromDB(type)
        }
    }

    private suspend fun getNewtechApplicationsFromDB(type: String): MutableList<ExhApplication> {
        tempApplicationList.clear()
        return withContext(Dispatchers.IO) {
            if (newtechRepository == null) {
                throw NullPointerException("")
            }
            val subList = newtechRepository!!.getFilterSubList(type)
            for (newtech in subList) {
                entity =
                    ExhApplication(newtech.CategoryId,
                        newtech.SubNameEn,
                        newtech.SubNameTc,
                        newtech.SubNameSc,
                        null,
                        null)
                tempApplicationList.add(entity)
            }
            tempApplicationList
        }
    }

    /*============================*/

    val appItemListener = OnItemClickListener { entity, _ ->
        entity as ExhApplication
        if (entity.isSelected.get()) {
            i("移出：" + entity.getApplicationName())
            entity.isSelected.set(false)
            // 新技术产品
            if (newtechType.isNotEmpty()) {
                when (newtechType) {
                    TYPE_NEW_TEC_PRODUCT -> removeInFilters(newProdFilters, entity.IndustryID)
                    TYPE_NEW_TEC_APPLICATIONS -> removeInFilters(newApplFilters, entity.IndustryID)
                    TYPE_NEW_TEC_THEMATIC -> removeInFilters(themeFilters, entity.IndustryID)
                    TYPE_NEW_TECH_AREA -> removeInFilters(newFilters, entity.IndustryID)
                }
            } else {
                removeInFilters(appFilters, entity.IndustryID)
            }
        } else {
            i("添加：" + entity.getApplicationName())
            entity.isSelected.set(true)

            val filter = ExhibitorFilter(0, entity.IndustryID, entity.getApplicationName())

            // 新技术产品
            if (newtechType.isNotEmpty()) {
                when (newtechType) {
                    TYPE_NEW_TEC_PRODUCT -> {
                        filter.index = FILTER_INDEX_NEWTECH_PRO
                        newProdFilters.value!!.add(filter)
                    }
                    TYPE_NEW_TEC_APPLICATIONS -> {
                        filter.index = FILTER_INDEX_NEWTECH_APPL
                        newApplFilters.value!!.add(filter)
                    }
                    TYPE_NEW_TEC_THEMATIC -> {
                        filter.index = FILTER_INDEX_NEWTECH_THEME
                        themeFilters.value!!.add(filter)
                    }
                    TYPE_NEW_TECH_AREA -> {
                        filter.index = FILTER_INDEX_NEWTECH_NEW
                        newFilters.value!!.add(filter)
                    }
                }
            } else {
                filter.index = FILTER_INDEX_APPLICATION
//                val filter = ExhibitorFilter(FILTER_INDEX_APPLICATION, entity.IndustryID, entity.getApplicationName())
                appFilters.value!!.add(filter)
            }
        }
    }

    private fun removeInFilters(filters: MutableLiveData<MutableList<ExhibitorFilter>>, id: String) {
        val size: Int = filters.value!!.size
        if (size > 0) {
            for (i in 0 until size) {
                if (id == filters.value!![i].id) {
                    filters.value!!.removeAt(i)
                    break
                }
            }
        }
        i("filters=${filters.value!!.size}")
    }

    val zoneItemListener = OnItemClickListener { entity, _ ->
        entity as Zone
        if (entity.isSelected.get()) {
            i("移出：" + entity.getName())
            entity.isSelected.set(false)
            removeInFilters(zoneFilters, entity.ThemeZoneCode)
        } else {
            i("添加：" + entity.getName())
            entity.isSelected.set(true)
            val filter = ExhibitorFilter(FILTER_INDEX_ZONE, entity.ThemeZoneCode, entity.getName())
            zoneFilters.value!!.add(filter)
        }
    }

    val regionItemListener = OnItemClickListener { entity, _ ->
        entity as Country
        if (entity.isSelected.get()) {
            i("移出：" + entity.getName())
            entity.isSelected.set(false)
            removeInFilters(regionFilters, entity.CountryID)
        } else {
            i("添加：" + entity.getName())
            entity.isSelected.set(true)
            val filter = ExhibitorFilter(FILTER_INDEX_REGION, entity.CountryID, entity.getName())
            regionFilters.value!!.add(filter)
        }
    }

    val industryItemListener = OnItemClickListener { entity, _ ->
        entity as ExhIndustry
        if (entity.isSelected.get()) {
            i("移出：" + entity.getName())
            entity.isSelected.set(false)
            removeInFilters(industryFilters, entity.CatalogProductSubID)
        } else {
            i("添加：" + entity.getName())
            entity.isSelected.set(true)
            val filter = ExhibitorFilter(FILTER_INDEX_INDUSTRY, entity.CatalogProductSubID, entity.getName())
            industryFilters.value!!.add(filter)
        }
    }

    val hallItemListener = OnItemClickListener { entity, _ ->
        entity as Hall
        if (entity.isSelected.get()) {
            i("移出：" + entity.getName())
            entity.isSelected.set(false)
            removeInFilters(hallFilters, entity.HallID)
            i("hallFilters=${hallFilters.value!!.size}")
        } else {
            i("添加：" + entity.getName())
            entity.isSelected.set(true)
            val filter = ExhibitorFilter(FILTER_INDEX_HALL, entity.HallID, entity.getName())
            hallFilters.value!!.add(filter)
        }
    }

    fun appFilterClear() {
        appFilters.value?.clear()
    }

    fun zoneFilterClear() {
        zoneFilters.value?.clear()
    }

    fun industryFilterClear() {
        industryFilters.value?.clear()
    }

    fun regionFilterClear() {
        regionFilters.value?.clear()
    }

    fun hallFilterClear() {
        hallFilters.value?.clear()
    }

    /* 新技术产品 */
    fun newTechClear() {
        if (newApplFilters.value == null) {
            LogUtil.i("newApplFilters.value==null")
        }
        newProdFilters.value?.clear()
        newApplFilters.value?.clear()
        themeFilters.value?.clear()
        newFilters.value?.clear()
    }

    fun newTechClear1() {
        newProdFilters.value?.clear()
    }

    fun newTechClear2() {
        newApplFilters.value?.clear()
    }

    fun newTechClear3() {
        themeFilters.value?.clear()
    }

    fun newTechClear4() {
        newFilters.value?.clear()
    }

    fun finishClear() {
        _clearClicked.value = false
    }

    fun onClear() {
        appFilterClear()
        zoneFilterClear()
        industryFilterClear()
        regionFilterClear()
        hallFilterClear()

        newTechClear()

        _clearClicked.value = true
        i("clearAllFilter")
    }

    fun onFilter() {
        allFilters.clear()

        if (!isNewTechFilter()) {
            allFilters.addAll(appFilters.value!!)
            allFilters.addAll(industryFilters.value!!)
            allFilters.addAll(regionFilters.value!!)
            allFilters.addAll(hallFilters.value!!)
            allFilters.addAll(zoneFilters.value!!)
        } else {
            allFilters.addAll(newApplFilters.value!!)
            allFilters.addAll(newProdFilters.value!!)
            allFilters.addAll(themeFilters.value!!)
            allFilters.addAll(newFilters.value!!)
        }

        LogUtil.i("allFilters=${allFilters.size},,${allFilters.toString()}")

        _filterStartNavigate.value = true


    }

    fun finishFilterNavigate() {
        _filterStartNavigate.value = false
    }

    /**
     * select E.* from EXHIBITOR E where 1  and CompanyID IN ( select CompanyID from CompanyApplication where IndustryID=9 intersect  select CompanyID from CompanyApplication where IndustryID=3) and CompanyID IN ( select CompanyID from CompanyProduct where CatalogProductSubID = 164 intersect  select CompanyID from CompanyProduct where CatalogProductSubID = 161 intersect  select CompanyID from CompanyProduct where CatalogProductSubID = 78) and (E.DescE LIKE '%123%' OR E.DescS LIKE '%123%' OR E.DescT LIKE '%123%' OR E.CompanyNameCN LIKE '%123%' OR E.CompanyNameEN LIKE '%123%' OR E.CompanyNameTW LIKE '%123%' OR E.BoothNo LIKE '%123%') ORDER BY StrokeEng,SeqEN
     */
    fun filterSql(): String? {
        val size = allFilters.size
        val halls = ArrayList<String?>()
        val countries = ArrayList<String?>()
        val industriesStr = ArrayList<String>()
        val appStr = ArrayList<String>()
        val zoneStr = ArrayList<String>()
        val newTecStr = ArrayList<String>()
        var filter: ExhibitorFilter
        var keyword: String? = ""
        var index: Int
        var sql: StringBuilder = java.lang.StringBuilder("")
        sql.append("select E.* from EXHIBITOR E where 1 ")
        for (i in 0 until size) {
            filter = allFilters[i]
            index = filter.index
            if (index == FILTER_INDEX_HALL) {
                if (halls.size == 0) {
                    sql = sql.append(" and HallNo ").append(" IN (%1\$s) ")
                    halls.add("'" + filter.id + "'")
                }
            } else if (index == FILTER_INDEX_REGION) {
                if (countries.size == 0) {
                    sql = sql.append(" and E.CountryID")
                        .append(" in (%2\$s) ")
                }
                countries.add(filter.id)
            } else if (index == FILTER_INDEX_INDUSTRY) {
                if (industriesStr.size == 0) {
                    sql = sql.append(" and CompanyID IN (%3\$s)")
                }
                industriesStr.add(" select CompanyID from CompanyProduct where CatalogProductSubID = '" + filter.id + "'")
            } else if (index == FILTER_INDEX_APPLICATION) {
                if (appStr.size == 0) {
                    sql = sql.append(" and CompanyID IN (%4\$s)")
                }
                appStr.add(" select CompanyID from CompanyApplication where IndustryID=" + filter.id)
            } else if (index == FILTER_INDEX_ZONE) {
                if (zoneStr.size == 0) {
                    sql = sql.append(" and CompanyID IN (%5\$s)")
                }
                zoneStr.add(" select CompanyId from ExhibitorZone where ThemeZoneCode='" + filter.id.toString() + "'")
            } else if (index == FILTER_INDEX_NEW_TECH) { // new tec
                if (newTecStr.size == 0) {
                    sql = sql.append(" and CompanyID IN (%6\$s)")
                }
                newTecStr.add(" SELECT COMPANY_ID FROM NEW_PRODUCT_INFO WHERE RID IN (select RID from NEW_CATEGORY_ID) ") //  where CATEGORY='C'
            } else if (index == FILTER_INDEX_KEYWORD) { // keyword
                keyword = filter.filter
                sql = sql.append(" and (carriecps)")
            }
        }
        sql.append(orderByStroke())
        i(">>>> exhibitor filter sbsql=$sql")
        var sqlStr =
            String.format(sql.toString(), halls.toString().replace("[", "").replace("]", ""),
                countries.toString().replace("[", "").replace("]", ""),
                industriesStr.toString().replace(",".toRegex(), " intersect").replace("[", "").replace("]", ""),
                appStr.toString().replace(",".toRegex(), " intersect").replace("[", "").replace("]", ""),
                zoneStr.toString().replace(",".toRegex(), " intersect").replace("[", "").replace("]", ""),
                newTecStr.toString().replace(",".toRegex(), " intersect").replace("[", "").replace("]", ""))
        sqlStr = sqlStr.replace("carriecps".toRegex(),
            "E.DescE LIKE '%$keyword%' OR E.DescS LIKE '%$keyword%' OR E.DescT LIKE '%$keyword%'" +
                    " OR E.CompanyNameCN LIKE '%$keyword%' OR E.CompanyNameEN LIKE '%$keyword%' OR E.CompanyNameTW LIKE '%$keyword%'" +
                    " OR E.BoothNo LIKE '%$keyword%'")
        i(">>>> sqlStr=$sqlStr")
        return sqlStr
    }

    /**
     * EXHIBITOR 表：按Stroke排序 ： 简 PYSIMP 英 STROKE_ENG 繁 STROKE_TRAD
     */
    private fun orderByStroke(): String? {
        return getName(" order by CAST(StrokeTrad AS INT) ASC,CAST(SeqTC AS INT) ASC",
            " ORDER BY StrokeEng,SeqEN", " ORDER BY PYSimp,SeqSC")
    }


    fun newtechFilterSql(): String {
        val size = allFilters.size
        val industriesStr = ArrayList<String>()
        val appStr = ArrayList<String>()
        val areasStr = ArrayList<String>()
        var filter: ExhibitorFilter
        var index: Int
        //        String sql = "select RID from NEW_PRODUCT_INFO WHERE ";
//        var sql = "select * from NewtechProductInfo WHERE %1\$s AND %2\$s AND %3\$s "
        var sql =
            "select N.*,I.Image_File AS imageName from NewtechProductInfo N, NewtechProductImage I WHERE N.RID=I.RID and N.Product_Name_EN IS NOT NULL and N.Product_Name_EN<>'' AND %1\$s AND %2\$s AND %3\$s  order by N.RID"

        for (i in 0 until size) {
            filter = allFilters[i]
            index = filter.index
//            if (newtechType == TYPE_NEW_TEC_PRODUCT || newtechType == TYPE_NEW_TEC_THEMATIC) { // 1 产品； 6：首发技术
            if (index == FILTER_INDEX_NEWTECH_PRO || index == FILTER_INDEX_NEWTECH_THEME) { // 1 产品； 6：首发技术
                if (industriesStr.size == 0) {
                    industriesStr.add(" select RID from NewtechCategoryID where Category = '" + filter.id + "' ")
                } else {
                    industriesStr.add("  or Category = '" + filter.id + "' ")
                }
//            } else if (newtechType == TYPE_NEW_TEC_APPLICATIONS) { // 应用
            } else if (index == FILTER_INDEX_NEWTECH_APPL) { // 应用
                appStr.add(" SELECT RID FROM NewtechProductsID WHERE SPOT = '" + filter.id!! + "' ")
//            } else if (newtechType == TYPE_NEW_TECH_AREA) { // 新品技術 範圍
            } else if (index == FILTER_INDEX_NEWTECH_NEW) { // 新品技術 範圍
                if (areasStr.size == 0) {
                    areasStr.add(" select RID from NewtechArea where Area = '" + filter.id + "' ")
                } else {
                    areasStr.add("  and Area = '" + filter.id + "' ")
                }
            }
        }
        sql = String.format(sql,
            if (industriesStr.size > 0) "N.RID IN (" + replaceListStr(industriesStr) + ")" else 1,
            if (appStr.size > 0) "N.RID IN (" + appStr.toString().replace(",".toRegex(), " intersect").replace("[",
                "").replace("]", "") + ")" else 1,
            if (areasStr.size > 0) "N.RID IN (" + replaceListStr(areasStr) + ")" else 1)

        LogUtil.i(">>>> sql=$sql")
        return sql
    }

    private fun replaceListStr(strings: ArrayList<String>): String {
        return strings.toString().replace(",".toRegex(), "").replace("[", "").replace("]", "")
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()

    }


    private fun isNewTechFilter(): Boolean {
        return newtechType.isNotEmpty() &&
                (newtechType == TYPE_NEW_TEC_PRODUCT || newtechType == TYPE_NEW_TEC_APPLICATIONS
                        || newtechType == TYPE_NEW_TEC_THEMATIC || newtechType == TYPE_NEW_TECH_AREA)
    }


}