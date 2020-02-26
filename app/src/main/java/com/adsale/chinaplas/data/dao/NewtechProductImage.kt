package com.adsale.chinaplas.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class NewtechProductImage {
    @PrimaryKey
    var RID: String = ""
    var PID: String = ""
    var Image_File: String = ""

    fun parser(strings: Array<String>) {
        this.RID = strings[0]
        this.PID = strings[1]
        this.Image_File = strings[2]
        if (Image_File.contains("JPG")) {
            Image_File = Image_File.replace("JPG", "jpg")
        } else if (Image_File.contains("PNG")) {
            Image_File = Image_File.replace("PNG", "png")
        }
    }

}