package com.adsale.chinaplas.data.entity

/**
 * Created by Carrie on 2020/1/13.
 */
data class VisitorData(
    val AggregateResults: Any?,
    val Data: List<Data>?,
    val Errors: Any?,
    val Total: Int
) {
    override fun toString(): String {
        return "VisitorData(AggregateResults=$AggregateResults?, Data=$Data?, Errors=$Errors?, Total=$Total)"
    }
}


data class Data(
    val AccountsFilter: List<AccountsFilter>?,
    val Batch: String?,
    val CellArea: String?,
    val CellCountry: String?,
    val CellNo: String?,
    val Company: String?,
    val ContactPerson: String?,
    val Currency: String?,
    val Email: String?,
    val Guid: String?,
    val MasterVisitorId: Any??,
    val PayDate: String?,
    val Paid: Boolean,
    val PayType: String?,
    val PdfUrl: String?,
    val Ref: String?,
    val SalCode: String?,
    val ShowCode: String?,
    val StatusName: String?,
    val TelArea: String?,
    val TelCountry: String?,
    val TelExt: String?,
    val TelNo: String?,
    val VisitorId: String?,
    val cueData: String
) {
    override fun toString(): String {
        return "Data(CellNo='$CellNo'?, Email='$Email'?, Guid='$Guid'?, PayDate='$PayDate'?, PayType=$PayType, PdfUrl='$PdfUrl', VisitorId=$VisitorId,, Paid='$Paid)"
    }
}

data class AccountsFilter(
    val CompanyId: String?,
    val PayDate: String?,
    val PdfUrl: String?,
    val Reference_No: String?,
    val StatusName: String?,
    val cueData: String
)