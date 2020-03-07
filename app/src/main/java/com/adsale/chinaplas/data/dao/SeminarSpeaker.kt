package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Carrie on 2020/2/14.
 */
@Entity
class SeminarSpeaker {
    var EventID: Int = 0
    var CompanyID: String? = ""
    var Seminarsummary: String? = ""
    var SpeakerPhoto: String? = ""
    var SpeakerName: String? = ""
    var SpeakerPosition: String? = ""
    var SpeakerInfo: String? = ""
    var Language: String? = ""
    var FreeParticipation: String? = ""
    var ContactPerson: String? = ""
    var Email: String? = ""
    var Tel: String? = ""
    var LangID: String? = ""
    @PrimaryKey
    var ID: Int = 0



    fun parser(strings: Array<String>) {
        this.EventID = Integer.valueOf(strings[0])
        this.CompanyID = strings[1]
        this.Seminarsummary = strings[2]
        this.SpeakerPhoto = strings[3]
        this.SpeakerName = strings[4]
        this.SpeakerPosition = strings[5]
        this.SpeakerInfo = strings[6]
        this.Language = strings[7]
        this.FreeParticipation = strings[8]
        this.ContactPerson = strings[9]
        this.Email = strings[10]
        this.Tel = strings[11]
        this.LangID = strings[12]
        this.ID = Integer.valueOf(strings[13])
    }

    override fun toString(): String {
        return "SeminarSpeaker(EventID=$EventID, SpeakerName=$SpeakerName, Language=$Language)"
    }
}