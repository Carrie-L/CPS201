package com.adsale.chinaplas.data.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Carrie on 2020/1/8.
 */
class ExhibitorFilter(var index: Int,
                      var id: String?,
                      var filter: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString())



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
        parcel.writeString(id)
        parcel.writeString(filter)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "ExhibitorFilter(index=$index, id=$id, filter=$filter)"
    }

    companion object CREATOR : Parcelable.Creator<ExhibitorFilter> {
        override fun createFromParcel(parcel: Parcel): ExhibitorFilter {
            return ExhibitorFilter(parcel)
        }

        override fun newArray(size: Int): Array<ExhibitorFilter?> {
            return arrayOfNulls(size)
        }
    }
}