package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Carrie on 2020/2/14.
 */
@Entity
class SeminarSpeaker {
    @PrimaryKey
     var EventID: Int = 0
     var CompanyID: String? = null
     var Seminarsummary: String? = null
     var SpeakerPhoto: String? = null
     var SpeakerName: String? = null
     var SpeakerPosition: String? = null
     var SpeakerInfo: String? = null
     var Language: String? = null
     var FreeParticipation: String? = null
     var ContactPerson: String? = null
     var Email: String? = null
     var Tel: String? = null
     var LangID: String? = null
     var ID: Int? = null
}