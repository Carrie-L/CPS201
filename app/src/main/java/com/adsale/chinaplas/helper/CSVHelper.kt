package com.adsale.chinaplas.helper

import android.text.TextUtils
import com.adsale.chinaplas.data.dao.*
import com.adsale.chinaplas.rootDir
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.utils.LogUtil.i
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created by Carrie on 2019/10/21.
 */

/**
 * 读取exhibitors.csv，且存入数据库表EXHIBITOR
 * <p> 解析两个csv，将数据合并起来，一起插入数据库
 * <p>
 * exhibitor.csv
 * <font color="#f97798">exhibitorDes.csv</font>
 */
class CSVHelper private constructor(private val exhibitorDao: ExhibitorDao,
                                    private val applicationDao: ApplicationDao,
                                    private val industryDao: IndustryDao,
                                    private val regionDao: RegionDao,
                                    private val hallDao: HallDao,
                                    private val zoneDao: ZoneDao,
                                    private val seminarDao: SeminarDao,
                                    private val newtechDao: NewtechDao
) {

    companion object {
        @Volatile
        private var instance: CSVHelper? = null

        fun getInstance(exhibitorDao: ExhibitorDao,
                        applicationDao: ApplicationDao,
                        industryDao: IndustryDao,
                        regionDao: RegionDao,
                        hallDao: HallDao,
                        zoneDao: ZoneDao,
                        seminarDao: SeminarDao,
                        newtechDao: NewtechDao
        ) =
            instance ?: synchronized(this) {
                instance ?: CSVHelper(exhibitorDao,
                    applicationDao,
                    industryDao,
                    regionDao,
                    hallDao,
                    zoneDao, seminarDao,
                    newtechDao
                ).also { instance = it }
            }
    }

    private var isExhibitorParseSuccess = true
    suspend fun processExhibitorCsv(): Boolean {
        val startTime = System.currentTimeMillis()
        i("processExhibitorCsv......")
        withContext(Dispatchers.IO) {
            readExhApplicationCSV("${rootDir}ExhibitorData/Application.csv")
            readCompanyApplicationCSV("${rootDir}ExhibitorData/CompanyApplication.csv")
            readCompanyProductCSV("${rootDir}ExhibitorData/CompanyProduct.csv")
            readExhIndustryCSV("${rootDir}ExhibitorData/Product.csv")
            readExhibitorCSV("${rootDir}ExhibitorData/exhibitors.csv",
                "${rootDir}ExhibitorData/ExhibitorDescription.csv")
            readExhibitorZoneCSV("${rootDir}ExhibitorData/ExhibitorZone.csv")
            readZoneCSV("${rootDir}ExhibitorData/Zone.csv")
            readCountryCSV("${rootDir}ExhibitorData/Country.csv")
            readHallCSV("${rootDir}ExhibitorData/Hall.csv")
            val endTime = System.currentTimeMillis()
            i("processExhibitorCsv 导入完成：" + (endTime - startTime) + "ms")
        }
        return isExhibitorParseSuccess
    }

    fun readExhibitorCSV(exhibitorPath: String, descPath: String) {
        if (!File(exhibitorPath).exists()) {
            return
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<Exhibitor>()
        var entity: Exhibitor
        val reader: CSVReader
        val streamExhibitor = getFileInputStream(exhibitorPath)
        try {
            reader = CSVReader(InputStreamReader(streamExhibitor, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = Exhibitor()
                entity.parseExhibitor(line)
                entity = processExhibitorSeq(entity)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e.message!!)
        } finally {
            streamExhibitor.close()
        }
        val endTime = System.currentTimeMillis()
        i("readExhibitorCSV 花费时间: ${(endTime - startTime)} ms , ${entities.size}")

        if (File(descPath).exists()) {
            readExhibitorDescCSV(descPath, entities)
        } else {
            exhibitorDao.deleteExhibitorAll()
            exhibitorDao.insertExhibitorAll(entities)
        }
    }

    private fun processExhibitorSeq(entity: Exhibitor): Exhibitor {
        if (TextUtils.isEmpty(entity.StrokeEng)) {
            entity.StrokeEng = getFirstChar(entity.CompanyNameEN!!.trim())
        }
        if (Character.isDigit(entity.StrokeEng!!.trim()[0])) {
            entity.StrokeEng = "#"
        }
        //去掉空格
        entity.CompanyNameCN = entity.CompanyNameCN!!.trim()
        entity.CompanyNameEN = entity.CompanyNameEN!!.trim()
        entity.CompanyNameTW = entity.CompanyNameTW!!.trim()

        if (entity.StrokeEng!!.contains("#")) {
            entity.StrokeEng = "ZZZ#"
        }
        if (entity.StrokeTrad!!.contains("#")) {
            entity.StrokeTrad = "999#"
        }
        if (entity.PYSimp!!.contains("#")) {
            entity.PYSimp = "ZZZ#"
        }
        return entity
    }

    /**
     * 读取ExhibitorDescription.csv，且存入数据库表EXHIBITOR
     */
    private fun readExhibitorDescCSV(descPath: String, list: MutableList<Exhibitor>) {
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<Exhibitor>()
        var entity: Exhibitor
        val reader: CSVReader
        val streamDesc = getFileInputStream(descPath)
        try {
            reader = CSVReader(InputStreamReader(streamDesc, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = Exhibitor()
                entity.parseDescription(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
        } finally {
            streamDesc?.close()
        }
        val endTime = System.currentTimeMillis()
        i("readExhibitorDescCSV 花费时间: ${(endTime - startTime)} ms ")

        combineExhibitorList(list, entities)
    }

    private fun combineExhibitorList(list: MutableList<Exhibitor>, descs: List<Exhibitor>) {
        val startTime = System.currentTimeMillis()
        val size1 = list.size
        val size2 = descs.size
        var entity1: Exhibitor
        var entity2: Exhibitor
        for (i in 0 until size1) {
            entity1 = list[i]
            for (j in 0 until size2) {
                entity2 = descs[j]
                if (entity1.CompanyID == entity2.CompanyID) {
                    entity1.DescE = entity2.DescE
                    entity1.DescS = entity2.DescS
                    entity1.DescT = entity2.DescT
                    list[i] = entity1
                }
            }
        }
        val endTime = System.currentTimeMillis()
        i("combineExhibitorList 花费时间: ${(endTime - startTime)} ms ")

        exhibitorDao.deleteExhibitorAll()
        exhibitorDao.insertExhibitorAll(list)

    }


    private fun readExhApplicationCSV(absPath: String) {
        if (!File(absPath).exists()) {
            return
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<ExhApplication>()
        var entity: ExhApplication
        val reader: CSVReader
        val ins = getFileInputStream(absPath)
        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = ExhApplication()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
            isExhibitorParseSuccess = false
        } finally {
            ins?.close()
        }
        val endTime = System.currentTimeMillis()

        applicationDao.deleteApplicationAll()
        applicationDao.insertApplicationAll(entities)

        i("readExhApplicationCSV 花费时间: ${(endTime - startTime)} ms , ${entities.size} ")
    }

    private fun readCompanyApplicationCSV(absPath: String) {
        if (!File(absPath).exists()) {
            return
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<CompanyApplication>()
        var entity: CompanyApplication
        val reader: CSVReader
        val ins = getFileInputStream(absPath)
        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = CompanyApplication()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
            isExhibitorParseSuccess = false
        } finally {
            ins?.close()
        }
        val endTime = System.currentTimeMillis()

        applicationDao.deleteCompanyApplicationAll()
        applicationDao.insertCompanyApplicationAll(entities)

        i("readCompanyApplicationCSV 花费时间: ${(endTime - startTime)} ms , ${entities.size} ")
    }

    private fun readCompanyProductCSV(absPath: String) {
        if (!File(absPath).exists()) {
            return
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<CompanyProduct>()
        var entity: CompanyProduct
        val reader: CSVReader
        val ins = getFileInputStream(absPath)
        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = CompanyProduct()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
            isExhibitorParseSuccess = false
        } finally {
            ins?.close()
        }
        val endTime = System.currentTimeMillis()

        industryDao.deleteCompanyProductAll()
        industryDao.insertCompanyProductAll(entities)

        i("readCompanyApplicationCSV 花费时间: ${(endTime - startTime)} ms  , ${entities.size}")
    }

    private fun readExhIndustryCSV(absPath: String) {
        if (!File(absPath).exists()) {
            return
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<ExhIndustry>()
        var entity: ExhIndustry
        val reader: CSVReader
        val ins = getFileInputStream(absPath)
        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = ExhIndustry()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
            isExhibitorParseSuccess = false
        } finally {
            ins?.close()
        }
        val endTime = System.currentTimeMillis()

        industryDao.deleteIndustryAll()
        industryDao.insertIndustryAll(entities)

        i("readExhIndustryCSV 花费时间: ${(endTime - startTime)} ms , ${entities.size} ")
    }

    private fun readExhibitorZoneCSV(absPath: String) {
        if (!File(absPath).exists()) {
            return
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<ExhibitorZone>()
        var entity: ExhibitorZone
        val reader: CSVReader
        val ins = getFileInputStream(absPath)
        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = ExhibitorZone()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
            isExhibitorParseSuccess = false
        } finally {
            ins?.close()
        }
        val endTime = System.currentTimeMillis()

        zoneDao.deleteExhibitorZoneAll()
        zoneDao.insertExhibitorZoneAll(entities)

        i("readExhibitorZoneCSV 花费时间: ${(endTime - startTime)} ms  , ${entities.size}")
    }

    private fun readZoneCSV(absPath: String) {
        if (!File(absPath).exists()) {
            return
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<Zone>()
        var entity: Zone
        val reader: CSVReader
        val ins = getFileInputStream(absPath)
        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = Zone()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
            isExhibitorParseSuccess = false
        } finally {
            ins?.close()
        }
        val endTime = System.currentTimeMillis()

        zoneDao.deleteZoneAll()
        zoneDao.insertZoneAll(entities)

        i("readZoneCSV 花费时间: ${(endTime - startTime)} ms , ${entities.size} ")
    }

    private fun readHallCSV(absPath: String) {
        if (!File(absPath).exists()) {
            return
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<Hall>()
        var entity: Hall
        val reader: CSVReader
        val ins = getFileInputStream(absPath)
        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = Hall()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
            isExhibitorParseSuccess = false
        } finally {
            ins?.close()
        }
        val endTime = System.currentTimeMillis()

        hallDao.deleteHallAll()
        hallDao.insertHallAll(entities)

        i("readZoneCSV 花费时间: ${(endTime - startTime)} ms , ${entities.size} ")
    }

    private fun readCountryCSV(absPath: String) {
        if (!File(absPath).exists()) {
            return
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<Country>()
        var entity: Country
        val reader: CSVReader
        val ins = getFileInputStream(absPath)
        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = Country()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
            isExhibitorParseSuccess = false
        } finally {
            ins?.close()
        }
        val endTime = System.currentTimeMillis()

        regionDao.deleteCountryAll()
        regionDao.insertCountryAll(entities)

        i("readCountryCSV 花费时间: ${(endTime - startTime)} ms , ${entities.size} ")
    }


    fun readMainIconCsv(ins: InputStream): MutableList<MainIcon> {
        val list = ArrayList<MainIcon>()
        var entity: MainIcon? = null
        val reader: CSVReader
        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var record: Array<String>?
            reader.readNext() // skip Header
            record = reader.readNext()
            while (record != null) {
                entity = MainIcon(record[1],
                    record[2],
                    record[0],
                    record[5],
                    record[7],
                    record[8],
                    record[3].toInt(),
                    record[6].toInt(),
                    record[4].toBoolean(),
                    "")
                list.add(entity)
                record = reader.readNext()
            }
        } catch (e: IOException) {
            LogUtil.e("readMainIconCsv1: ${e.message}")
        } finally {
            ins?.close()
        }
        LogUtil.i("readMainIconCsv: ${list.size}")
        return list
    }

    suspend fun parseSeminarCsv() {
        withContext(Dispatchers.IO) {
            readSeminarInfoCSV("TechnicalSeminar/SeminarInfo.csv")
            readSeminarSpeakCSV("TechnicalSeminar/SeminarSpeaker.csv")
        }
    }

    suspend fun getSeminars(): List<SeminarInfo> {
        return withContext(Dispatchers.IO) {
            seminarDao.getSeminarAmList(getLangCode(), "%22%")
        }
    }


    /* 技术交流会 */
    private fun readSeminarInfoCSV(absPath: String) {
        LogUtil.i("readSeminarInfoCSV: $absPath")
        val ins: InputStream
        if (!File("${rootDir}$absPath").exists()) {
            ins = getAssetInputStream(absPath)
        } else {
            ins = getFileInputStream("${rootDir}$absPath")
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<SeminarInfo>()
        var entity: SeminarInfo
        val reader: CSVReader
//        val ins = getFileInputStream(absPath)
        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = SeminarInfo()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
        } finally {
            ins?.close()
        }
        val endTime = System.currentTimeMillis()

        seminarDao.deleteSeminarInfoAll()
        seminarDao.insertSeminarInfoAll(entities)

        i("readSeminarInfoCSV 花费时间: ${(endTime - startTime)} ms , ${entities.size} ,, ${entities.toString()}")
    }

    private fun readSeminarSpeakCSV(absPath: String) {
        val ins: InputStream
        if (!File("${rootDir}$absPath").exists()) {
            ins = getAssetInputStream(absPath)
        } else {
            ins = getFileInputStream("${rootDir}$absPath")
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<SeminarSpeaker>()
        var entity: SeminarSpeaker
        val reader: CSVReader

        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = SeminarSpeaker()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
            isExhibitorParseSuccess = false
        } finally {
            ins?.close()
        }
        val endTime = System.currentTimeMillis()

        seminarDao.deleteSeminarSpeakerAll()
        seminarDao.insertSeminarSpeakAll(entities)

        i("readSeminarSpeakCSV 花费时间: ${(endTime - startTime)} ms , ${entities.size} ")
    }


    /*  ￥￥￥￥￥￥￥￥￥♥♥♥  新技术产品  ♥♥♥￥￥￥￥￥￥￥￥￥￥￥￥￥￥ */
    suspend fun parseNewTechCsv() {
        withContext(Dispatchers.IO) {
            readCategoryIDCSV("NewTech/CategoryID.csv")
            readCategorySubCSV("NewTech/CategorySub.csv")
            readProductInfoCSV("NewTech/NewProductInfo.csv")
            readProductIDCSV("NewTech/ProductID.csv")
            readProductImageCSV("NewTech/ProductImage.csv")
            readNewtechAreaCSV("NewTech/NewProductAndArea.csv")
        }
    }


    private fun readCategoryIDCSV(absPath: String) {
        val ins: InputStream
        if (!File("${rootDir}$absPath").exists()) {
            ins = getAssetInputStream(absPath)
        } else {
            ins = getFileInputStream("${rootDir}$absPath")
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<NewtechCategoryID>()
        var entity: NewtechCategoryID
        val reader: CSVReader

        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = NewtechCategoryID()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
        } finally {
            ins.close()
        }
        val endTime = System.currentTimeMillis()

        newtechDao.deleteCategoryIDAll()
        newtechDao.insertCategoryIDAll(entities)

        i("readCategoryIDCSV 花费时间: ${(endTime - startTime)} ms , ${entities.size} ")
    }

    private fun readCategorySubCSV(absPath: String) {
        val ins: InputStream
        if (!File("${rootDir}$absPath").exists()) {
            ins = getAssetInputStream(absPath)
        } else {
            ins = getFileInputStream("${rootDir}$absPath")
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<NewtechCategorySub>()
        var entity: NewtechCategorySub
        val reader: CSVReader

        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = NewtechCategorySub()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
        } finally {
            ins.close()
        }
        val endTime = System.currentTimeMillis()

        newtechDao.deleteCategorySubAll()
        newtechDao.insertCategorySubAll(entities)

        i("readCategorySubCSV 花费时间: ${(endTime - startTime)} ms , ${entities.size} ")
    }

    private fun readProductInfoCSV(absPath: String) {
        val ins: InputStream
        if (!File("${rootDir}$absPath").exists()) {
            ins = getAssetInputStream(absPath)
        } else {
            ins = getFileInputStream("${rootDir}$absPath")
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<NewtechProductInfo>()
        var entity: NewtechProductInfo
        val reader: CSVReader

        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = NewtechProductInfo()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
        } finally {
            ins.close()
        }
        val endTime = System.currentTimeMillis()

        newtechDao.deleteProductInfoAll()
        newtechDao.insertProductInfoAll(entities)

        i("readProductInfoCSV 花费时间: ${(endTime - startTime)} ms , ${entities.size} ")
    }

    private fun readProductIDCSV(absPath: String) {
        val ins: InputStream
        if (!File("${rootDir}$absPath").exists()) {
            ins = getAssetInputStream(absPath)
        } else {
            ins = getFileInputStream("${rootDir}$absPath")
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<NewtechProductsID>()
        var entity: NewtechProductsID
        val reader: CSVReader

        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = NewtechProductsID()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
        } finally {
            ins.close()
        }
        val endTime = System.currentTimeMillis()

        newtechDao.deleteProductsIDAll()
        newtechDao.insertProductsIDAll(entities)

        i("NewtechProductsID 花费时间: ${(endTime - startTime)} ms , ${entities.size} ")
    }

    private fun readProductImageCSV(absPath: String) {
        val ins: InputStream
        if (!File("${rootDir}$absPath").exists()) {
            ins = getAssetInputStream(absPath)
        } else {
            ins = getFileInputStream("${rootDir}$absPath")
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<NewtechProductImage>()
        var entity: NewtechProductImage
        val reader: CSVReader

        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = NewtechProductImage()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
        } finally {
            ins.close()
        }
        val endTime = System.currentTimeMillis()

        newtechDao.deleteProductImageDAll()
        newtechDao.insertProductImageAll(entities)

        i("readProductImageCSV 花费时间: ${(endTime - startTime)} ms , ${entities.size} ")
    }

    private fun readNewtechAreaCSV(absPath: String) {
        val ins: InputStream
        if (!File("${rootDir}$absPath").exists()) {
            ins = getAssetInputStream(absPath)
        } else {
            ins = getFileInputStream("${rootDir}$absPath")
        }
        val startTime = System.currentTimeMillis()
        val entities = ArrayList<NewtechArea>()
        var entity: NewtechArea
        val reader: CSVReader

        try {
            reader = CSVReader(InputStreamReader(ins, "UTF8"))
            var line = reader.readNext()
            while (line != null) {
                line = reader.readNext()
                if (line == null) {
                    break
                }
                entity = NewtechArea()
                entity.parser(line)
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e)
        } finally {
            ins.close()
        }
        val endTime = System.currentTimeMillis()

        newtechDao.deleteAreaAll()
        newtechDao.insertAreaAll(entities)

        i("readNewtechAreaCSV 花费时间: ${(endTime - startTime)} ms , ${entities.size} ")
    }

}
