package com.adsale.chinaplas.helper

import android.text.TextUtils
import com.adsale.chinaplas.data.dao.*
import com.adsale.chinaplas.rootDir
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.LogUtil.i
import com.adsale.chinaplas.utils.getFileInputStream
import com.adsale.chinaplas.utils.getFirstChar
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
class CSVHelper private constructor(private val exhibitorDao: ExhibitorDao) {

    companion object {
        @Volatile
        private var instance: CSVHelper? = null

        fun getInstance(exhibitorDao: ExhibitorDao) =
            instance ?: synchronized(this) {
                instance ?: CSVHelper(exhibitorDao).also { instance = it }
            }
    }

    suspend fun processExhibitorCsv() {
        val startTime = System.currentTimeMillis()
        i("processExhibitorCsv......")
        withContext(Dispatchers.IO) {
            readExhApplicationCSV("${rootDir}ExhibitorData/ExhApplication.csv")
            readCompanyApplicationCSV("${rootDir}ExhibitorData/CompanyApplication.csv")
            readCompanyProductCSV("${rootDir}ExhibitorData/CompanyProduct.csv")
            readExhIndustryCSV("${rootDir}ExhibitorData/Product.csv")
            readExhibitorCSV("${rootDir}ExhibitorData/exhibitors.csv",
                "${rootDir}ExhibitorData/ExhibitorDescription.csv")
            readExhibitorZoneCSV("${rootDir}ExhibitorData/ExhibitorZone.csv")
            readZoneCSV("${rootDir}ExhibitorData/Zone.csv")
            readCountryCSV("${rootDir}ExhibitorData/Country.csv")
            val endTime = System.currentTimeMillis()
            i("processExhibitorCsv 导入完成：" + (endTime - startTime) + "ms")
        }
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
                entity = Exhibitor()
                entity.parseExhibitor(line)
                entity = processExhibitorSeq(entity)
                LogUtil.i("entity=$entity")
                entities.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtil.e(e.message!!)
        } finally {
            streamExhibitor.close()
        }
        val endTime = System.currentTimeMillis()
        i("readExhibitorCSV 花费时间: ${(endTime - startTime)} ms ")

        if (File(descPath).exists()) {
            readExhibitorDescCSV(descPath, entities)
        }else{
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
                entity = ExhApplication()
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

        exhibitorDao.deleteApplicationAll()
        exhibitorDao.insertApplicationAll(entities)

        i("readExhApplicationCSV 花费时间: ${(endTime - startTime)} ms ")
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
                entity = CompanyApplication()
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

        exhibitorDao.deleteCompanyApplicationAll()
        exhibitorDao.insertCompanyApplicationAll(entities)

        i("readCompanyApplicationCSV 花费时间: ${(endTime - startTime)} ms ")
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
                entity = CompanyProduct()
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

        exhibitorDao.deleteCompanyProductAll()
        exhibitorDao.insertCompanyProductAll(entities)

        i("readCompanyApplicationCSV 花费时间: ${(endTime - startTime)} ms ")
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
                entity = ExhIndustry()
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

        exhibitorDao.deleteIndustryAll()
        exhibitorDao.insertIndustryAll(entities)

        i("readExhIndustryCSV 花费时间: ${(endTime - startTime)} ms ")
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
                entity = ExhibitorZone()
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

        exhibitorDao.deleteExhibitorZoneAll()
        exhibitorDao.insertExhibitorZoneAll(entities)

        i("readExhibitorZoneCSV 花费时间: ${(endTime - startTime)} ms ")
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
                entity = Zone()
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

        exhibitorDao.deleteZoneAll()
        exhibitorDao.insertZoneAll(entities)

        i("readZoneCSV 花费时间: ${(endTime - startTime)} ms ")
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
                entity = Country()
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

        exhibitorDao.deleteCountryAll()
        exhibitorDao.insertCountryAll(entities)

        i("readCountryCSV 花费时间: ${(endTime - startTime)} ms ")
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
}
