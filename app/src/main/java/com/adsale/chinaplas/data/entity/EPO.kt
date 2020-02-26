package com.adsale.chinaplas.data.entity

import com.adsale.chinaplas.isTablet
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class EPO(
    val D1: D1,
    val D2: D2,
    val D3: D3,
    val D4: D3,
    val D5: D3,
    val D6: List<D6>,
    val D7: List<D7>,
    val base: Base
) {
    override fun toString(): String {
        return "EPO(D1=$D1, D2=$D2, D3=$D3, base=$base)"
    }
}

@JsonClass(generateAdapter = true)
class D1(
    val image_cn: String,
    val image_en: String,
    val company_id_cn: String,
    val company_id_en: String
)

@JsonClass(generateAdapter = true)
class D2(
    val cn: List<Property>,
    val en: List<Property>
) {
    override fun toString(): String {
        return "D2(cn=$cn, en=$en)"
    }
}


@JsonClass(generateAdapter = true)
class D3(val cn: List<Property>,
         val en: List<Property>
)

@JsonClass(generateAdapter = true)
class D4(val cn: List<Property>,
         val en: List<Property>
)

@JsonClass(generateAdapter = true)
class D5(val cn: List<Property>,
         val en: List<Property>
)

@JsonClass(generateAdapter = true)
data class D6(
    val about_en: String,
    val about_sc: String,
    val about_tc: String,
    val companyID: String,
    val logo: String,
    val products: List<String>,
    val videoCover: String,
    val videoLink: String,
    val website: String
)

@JsonClass(generateAdapter = true)
data class D7(
    val RID: String,
    val BoothNo: String,
    val CompanyID: String,
    val CompanyName_EN: String,
    val CompanyName_SC: String,
    val CompanyName_TC: String,
    val Description_EN: String,
    val Description_SC: String,
    val Description_TC: String,
    val FirstPageImage: String,
    val ImageLinks: List<String>,
    val LogoImageLink: String,
    val ProductName_EN: String,
    val ProductName_SC: String,
    val ProductName_TC: String,
    val videoLink: String
)

@JsonClass(generateAdapter = true)
class D8(
)

@JsonClass(generateAdapter = true)
class Base(
    val baseUrl: String,
    val endTime: String,
    val startTime: String,
    val isOpenCN: List<Int>,
    val isOpenEN: List<Int>
)


@JsonClass(generateAdapter = true)
class Property(
    val function: Int,
    val image: String,
    val pageID: String
) {
    override fun toString(): String {
        return "En(function=$function, image='$image',  pageID='$pageID')"
    }

    fun imageName(): String {
        if (isTablet) {
            return image.replace(".jpg", "_pad.jpg")
        }
        return image
    }
}
