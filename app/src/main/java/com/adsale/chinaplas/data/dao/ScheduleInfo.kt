package com.adsale.chinaplas.data.dao

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.DATE_1
import com.adsale.chinaplas.utils.LogUtil
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Carrie on 2020/3/3.
 */
//@Entity(primaryKeys = ["startDate", "id", "startTime", "type"])
@Entity
data class ScheduleInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = 0,
    var title: String? = "",
    var note: String? = "",
    var location: String? = "",
    var pageID: String,
    var startDate: String? = DATE_1.toString(),
    var startTime: String? = "09:00",
    var type: Int = 1,
    var hour: Int? = 0,
    var minute: Int? = 0
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readValue(Long::class.java.classLoader) as Long?,
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString()!!,
        source.readString(),
        source.readString(),
        source.readInt(),
        source.readValue(Int::class.java.classLoader) as Int?,
        source.readValue(Int::class.java.classLoader) as Int?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeValue(id)
        writeString(title)
        writeString(note)
        writeString(location)
        writeString(pageID)
        writeString(startDate)
        writeString(startTime)
        writeInt(type)
        writeValue(hour)
        writeValue(minute)
    }

    override fun toString(): String {
        return "ScheduleInfo(id=$id, title=$title, note=$note, location=$location, pageID='$pageID', startDate=$startDate, startTime=$startTime, type=$type, hour=$hour, minute=$minute)"
    }

    fun getEndTime(): String {
        val format = SimpleDateFormat("HH:mm", Locale.CHINA)
        val date = format.parse(startTime!!)
        date!!.hours = date!!.hours + hour!!
        date.minutes = date.minutes + minute!!

        val entTime = format.format(date)
        LogUtil.i("after: $entTime")

        return entTime
    }


    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ScheduleInfo> = object : Parcelable.Creator<ScheduleInfo> {
            override fun createFromParcel(source: Parcel): ScheduleInfo = ScheduleInfo(source)
            override fun newArray(size: Int): Array<ScheduleInfo?> = arrayOfNulls(size)
        }
    }
}
