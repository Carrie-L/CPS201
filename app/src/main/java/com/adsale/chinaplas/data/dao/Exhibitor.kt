package com.adsale.chinaplas.data.dao

import android.text.TextUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.*

/**
 * Created by Carrie on 2020/1/3.
 */
@Entity(tableName = "EXHIBITOR")
class Exhibitor constructor() {
    @PrimaryKey
    var CompanyID: String = ""
    var CompanyNameEN: String? = ""
    var CompanyNameTW: String? = ""
    var CompanyNameCN: String? = ""
    var AddressE: String? = ""
    var AddressT: String? = ""
    var AddressS: String? = ""
    var Tel: String? = ""
    var Fax: String? = ""
    var Email: String? = ""
    var Website: String? = ""
    var CountryID: String? = ""
    var BoothNo: String? = ""
    var StrokeEng: String? = ""
    var StrokeTrad: String? = ""
    var StrokeSimp: String? = ""
    var PYSimp: String? = ""
    var ImgFolder: String? = ""
    var DescE: String? = ""
    var DescS: String? = ""
    var DescT: String? = ""
    var PhotoFileName: String? = ""
    var NewTechUpdateDate: String? = ""
    var SeqEN: String? = ""
    var SeqTC: String? = ""
    var SeqSC: String? = ""
    var HallNo: String? = ""
    var IsFavourite: Int? = 0
        set(value) {
            isStared.set(IsFavourite == 1)
            field = value
        }
        get() {
            isStared.set(field == 1)
            return field
        }
    var Note: String? = ""
    var Rate: Int? = 0
    var seqHall: Double? = 1.0    // HallNo的排序
    var descUpdatedAt: String? = ""

    @Ignore
    var isD3Banner = ObservableBoolean(false)
    @Ignore
    var isTypeLabel = ObservableInt()
    @Ignore
    var isStared = ObservableBoolean(false)
//    @Ignore
//    var sort:String=""


    fun getDescription(): String {
        return getName(DescT!!, DescE!!, DescS!!)
    }

    fun getAddress(): String {
        return getName(AddressT!!, AddressE!!, AddressS!!)
    }


    /**
     * 在初始化数据时，把最后的 # 或 TBC 或 N/A 前面加上 999 或 ZZZ ，目的是为了排序的时候让它们在最后，省却还需另外排序的麻烦。
     * 因此在这一 getSort() 步骤，要将 999# 还原成 #.
     *
     * @return sort or hall_no
     */
    fun getSort(): String {
        var sort: String = ""
        /* AZ */
        if (getCurrLanguage() == LANG_SC) {
            sort = PYSimp!!
            if (PYSimp!!.contains("#")) {
                sort = "#"
            }
            return sort!!
        } else if (getCurrLanguage() == LANG_EN) {
            sort = StrokeEng!!
            if (StrokeEng!!.contains("#")) {
                sort = "#"
            }
            return sort!!
        } else {
            sort = StrokeTrad!!
            if (StrokeTrad!!.contains("#")) {
                sort = "#"
            }
            return "${sort}${TRAD_STROKE}"
        }

//        if (checkStrokeEngNull() && StrokeEng!!.contains("#")) {
//            StrokeEng = "#"
//        } else if (checkPYSimpNull() && PYSimp!!.contains("#")) {
//            PYSimp = "#"
//        } else if (checkStrokeTradNull() && StrokeTrad!!.contains("#")) {
//            StrokeTrad = "#$TRAD_STROKE"
//        } else if (checkStrokeEngNull() && StrokeEng!!.contains("TBC")) {
//            StrokeEng = "TBC"
//        } else if (checkPYSimpNull() && PYSimp!!.contains("TBC")) {
//            PYSimp = "TBC"
//        } else if (checkStrokeTradNull() && StrokeTrad!!.contains("TBC")) {
//            StrokeTrad = "TBC"
//        } else if (checkStrokeEngNull() && StrokeEng!!.contains("N/A")) {
//            StrokeEng = "N/A"
//        } else if (checkPYSimpNull() && PYSimp!!.contains("N/A")) {
//            PYSimp = "N/A"
//        } else if (checkStrokeTradNull() && StrokeTrad!!.contains("N/A")) {
//            StrokeTrad = "N/A"
//        }
//        return getName(StrokeTrad!!, StrokeEng!!, PYSimp!!)
    }

    private fun checkStrokeEngNull(): Boolean {
        return getCurrLanguage() == 1 && !TextUtils.isEmpty(StrokeEng)
    }

    private fun checkPYSimpNull(): Boolean {
        return getCurrLanguage() == 2 && !TextUtils.isEmpty(PYSimp)
    }

    private fun checkStrokeTradNull(): Boolean {
        return getCurrLanguage() == 0 && !TextUtils.isEmpty(StrokeTrad)
    }


    fun setSort(sort: String) {
        if (getCurrLanguage() == LANG_EN) {
            StrokeEng = sort
        } else if (getCurrLanguage() == LANG_TC) {
            StrokeTrad = sort
        } else {
            PYSimp = sort
        }
    }

    @Ignore
    var company: String? = ""

    fun getCompanyName(): String {
        if (!TextUtils.isEmpty(company)) {
            return company!!
        } else
            return when (getCurrLanguage()) {
                LANG_SC -> CompanyNameCN ?: ""
                LANG_EN -> CompanyNameEN ?: ""
                else -> CompanyNameTW ?: ""
            }
    }

    //    CompanyID,CompanyNameCN,BoothNo,seqHall,IsFavourite,PhotoFileName
    constructor (CompanyID: String,
                 company: String?,
                 BoothNo: String?,
                 PhotoFileName: String?,
                 IsFavourite: Int?,
                 seqHall: Double?,
                 sort: String) : this() {
        this.CompanyID = CompanyID
        this.company = company
        this.BoothNo = BoothNo
        this.PhotoFileName = PhotoFileName
        this.IsFavourite = IsFavourite ?: 0
        this.seqHall = seqHall
        setSort(sort)
        LogUtil.i("exhibitor part1 = $CompanyID")
    }

    constructor(CompanyID: String,
                CompanyNameEN: String?,
                CompanyNameTW: String?,
                CompanyNameCN: String?,
                AddressE: String?,
                AddressT: String?,
                AddressS: String?,
                Tel: String?,
                Fax: String?,
                Email: String?,
                Website: String?,
                CountryID: String?,
                BoothNo: String?,
                StrokeEng: String?,
                StrokeTrad: String?,
                StrokeSimp: String?,
                PYSimp: String?,
                ImgFolder: String?,
                DescE: String?,
                DescS: String?,
                DescT: String?,
                PhotoFileName: String?,
                NewTechUpdateDate: String?,
                SeqEN: String?,
                SeqTC: String?,
                SeqSC: String?,
                HallNo: String?,
                IsFavourite: Int?,
                Note: String?,
                Rate: Int?,
                seqHall: Double?,
                descUpdatedAt: String?,
                company: String?) : this() {
        this.CompanyID = CompanyID
        this.CompanyNameEN = CompanyNameEN
        this.CompanyNameTW = CompanyNameTW
        this.CompanyNameCN = CompanyNameCN
        this.AddressE = AddressE
        this.AddressT = AddressT
        this.AddressS = AddressS
        this.Tel = Tel
        this.Fax = Fax
        this.Email = Email
        this.Website = Website
        this.CountryID = CountryID
        this.BoothNo = BoothNo
        this.StrokeEng = StrokeEng
        this.StrokeTrad = StrokeTrad
        this.StrokeSimp = StrokeSimp
        this.PYSimp = PYSimp
        this.ImgFolder = ImgFolder
        this.DescE = DescE
        this.DescS = DescS
        this.DescT = DescT
        this.PhotoFileName = PhotoFileName
        this.NewTechUpdateDate = NewTechUpdateDate
        this.SeqEN = SeqEN
        this.SeqTC = SeqTC
        this.SeqSC = SeqSC
        this.HallNo = HallNo
        this.IsFavourite = IsFavourite
        this.Note = Note
        this.Rate = Rate
        this.seqHall = seqHall
        this.descUpdatedAt = descUpdatedAt
        this.company = company
        LogUtil.i("exhibitor part2 = $CompanyID")
    }

    constructor(CompanyID: String,
                CompanyNameEN: String?,
                CompanyNameTW: String?,
                CompanyNameCN: String?,
                AddressE: String?,
                AddressT: String?,
                AddressS: String?,
                Tel: String?,
                Fax: String?,
                Email: String?,
                Website: String?,
                CountryID: String?,
                BoothNo: String?,
                StrokeEng: String?,
                StrokeTrad: String?,
                StrokeSimp: String?,
                PYSimp: String?,
                ImgFolder: String?,
                DescE: String?,
                DescS: String?,
                DescT: String?,
                PhotoFileName: String?,
                SeqEN: String?,
                SeqTC: String?,
                SeqSC: String?,
                HallNo: String?,
                company: String?) : this() {
        this.CompanyID = CompanyID
        this.CompanyNameEN = CompanyNameEN
        this.CompanyNameTW = CompanyNameTW
        this.CompanyNameCN = CompanyNameCN
        this.AddressE = AddressE
        this.AddressT = AddressT
        this.AddressS = AddressS
        this.Tel = Tel
        this.Fax = Fax
        this.Email = Email
        this.Website = Website
        this.CountryID = CountryID
        this.BoothNo = BoothNo
        this.StrokeEng = StrokeEng
        this.StrokeTrad = StrokeTrad
        this.StrokeSimp = StrokeSimp
        this.PYSimp = PYSimp
        this.ImgFolder = ImgFolder
        this.DescE = DescE
        this.DescS = DescS
        this.DescT = DescT
        this.PhotoFileName = PhotoFileName
        this.SeqEN = SeqEN
        this.SeqTC = SeqTC
        this.SeqSC = SeqSC
        this.HallNo = HallNo
        this.company = company
        LogUtil.i("exhibitor part3 = $CompanyID")
    }


    fun parseExhibitor(csv: Array<String>) {
        CompanyID = csv[0]
        CompanyNameEN = csv[1]
        CompanyNameTW = csv[2]
        CompanyNameCN = csv[3]
        AddressE = csv[4]
        AddressT = csv[5]
        AddressS = csv[6]
        Tel = csv[7]
        Fax = csv[8]
        Email = csv[9]
        Website = csv[10]
        CountryID = csv[11]
        BoothNo = csv[12]
        StrokeEng = csv[13]
        StrokeTrad = csv[14]
        StrokeSimp = csv[15]
        PYSimp = csv[16]
        ImgFolder = csv[17]
        DescE = csv[18]
        DescS = csv[19]
        DescT = csv[20]
        PhotoFileName = csv[21]
        NewTechUpdateDate = csv[22]
        SeqEN = csv[23]
        SeqTC = csv[24]
        SeqSC = csv[25]
        HallNo = csv[26]

        if (HallNo == "1.1") {
            seqHall = 1.0
        } else if (HallNo == "1.2") {
            seqHall = 2.0
        } else if (HallNo == "2.1") {
            seqHall = 3.0
        } else if (HallNo == "3") {
            seqHall = 3.1
        } else if (HallNo == "4.1") {
            seqHall = 4.0
        } else if (HallNo == "5.1") {
            seqHall = 5.0
        } else if (HallNo == "5.2") {
            seqHall = 5.1
        } else if (HallNo == "6.1") {
            seqHall = 6.0
        } else if (HallNo == "6.1/1.1") {
            seqHall = 7.0
        } else if (HallNo == "6.2") {
            seqHall = 7.1
        } else if (HallNo == "7.1") {
            seqHall = 8.0
        } else if (HallNo == "7.2") {
            seqHall = 9.0
        } else if (HallNo == "8.1") {
            seqHall = 10.0
        } else if (HallNo == "8.2") {
            seqHall = 11.0
        } else if (HallNo == "NH") {
            seqHall = 12.0
        } else if (HallNo == "TBC") {
            seqHall = 13.0
        }


    }

    //CompanyID|DescE|DescS|DescT
    fun parseDescription(csv: Array<String>) {
        CompanyID = csv[0]
        DescE = csv[1]
        DescS = csv[2]
        DescT = csv[3]
    }

    override fun toString(): String {
        return "Exhibitor(CompanyID='$CompanyID', HallNo=${HallNo}, Sort=${getSort()}, IsFavourite=$IsFavourite, CompanyNameTW=$CompanyNameTW, CompanyNameCN=$CompanyNameCN, BoothNo=$BoothNo)"
    }

}
    