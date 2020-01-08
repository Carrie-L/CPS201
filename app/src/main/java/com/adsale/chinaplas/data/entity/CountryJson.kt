package com.adsale.chinaplas.data.entity

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.*

import java.util.*

/**
 * Created by Carrie on 2019/11/1.
 */
@Entity(tableName = "CountryJson", primaryKeys = ["Code", "AdcCountryId"])
class CountryJson {
    var Code: String = ""
    var Name_Eng: String? = ""
        set(value) {
            value?.let { OrderEN = value.substring(0, 1).toUpperCase(Locale.ENGLISH) }
            field = value
        }
    var Name_Sc: String? = ""
        set(value) {
            value?.let { OrderSC = getFirstChar(value.substring(0, 1)) }
            field = value
        }
    var Name_Tc: String? = ""
        set(value) {
            value?.let { OrderTC = getFirstChar(value.substring(0, 1)) }
            field = value
        }
    var Name_V: String? = ""
    var Country: String = ""
    var Province: String = ""
    var AdcCountryId: String = ""
    var CountryTelCode: String = ""
    var Tel_area: String = ""
    var Level: String = ""
    var DisplayOrder: String = ""
    var updatedAt: String? = ""
    var OrderSC: String? = ""
    var OrderTC: String? = ""
    var OrderEN: String? = ""

    @Transient
    @Ignore
    var isChecked = ObservableField<Boolean>(false)

    override fun toString(): String {
        //        return Name_Eng ?: Name_Sc ?: Name_Tc ?: ""
        return "{\"Code\"='$Code', \"Name_Eng\"='$Name_Eng', \"Name_Sc\"='$Name_Sc', \"Name_Tc\"='$Name_Tc', \"Name_V\"='$Name_V', \"Country\"='$Country', \"Province\"='$Province', \"AdcCountryId\"='$AdcCountryId', \"CountryTelCode\"='$CountryTelCode', \"Tel_area\"='$Tel_area', \"Level\"='$Level', \"DisplayOrder\"='$DisplayOrder'}"
    }

    fun getName(): String {
        return if (getCurrLanguage() == LANG_TC) Name_Tc ?: "" else if (getCurrLanguage() == LANG_EN) Name_Eng
            ?: "" else Name_Sc ?: ""
    }

}
