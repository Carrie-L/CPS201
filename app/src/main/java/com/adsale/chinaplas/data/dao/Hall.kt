package com.adsale.chinaplas.data.dao

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.adsale.chinaplas.utils.LogUtil

/**
 * Created by Carrie on 2020/1/9.
 */
@Entity
class Hall constructor() {
    @PrimaryKey
    var HallID: String = ""
    var HallEng: String? = ""
    var HallTC: String? = ""
    var HallSC: String? = ""
    var SEQ: String? = ""
    var count: Int? = 0

    @Ignore
    var isSelected = ObservableBoolean(false)

    fun getName(): String {
        return com.adsale.chinaplas.utils.getName(
            HallTC!!+"H",
            HallEng!!+"H",
            HallSC!!+"H"
        )
    }

    fun parser(strings: Array<String>) {
        HallID = strings[0]
        HallEng = strings[1]
        HallTC = strings[2]
        HallSC = strings[3]
        SEQ = strings[4]
    }

    override fun toString(): String {
        return "Hall(HallID='$HallID', HallEng=$HallEng, HallTC=$HallTC, HallSC=$HallSC, SEQ=$SEQ, count=${count})"
    }


}