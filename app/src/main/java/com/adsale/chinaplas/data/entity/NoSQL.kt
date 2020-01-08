package com.adsale.chinaplas.data.entity

import com.squareup.moshi.JsonClass


/**
 * Created by Carrie on 2019/10/24.
 */
/*
 * 接口描述：返回大于 UpdateTime 的所有数据
 *  1) 请求地址
        https://api2.bmob.cn/1/classes/NoSQL
    2) 调用方式：HTTP get
    3) Headers:
        "Content-Type:application/json",
        "X-Bmob-Application-Id:xxx",
        "X-Bmob-REST-API-Key:xxx"
    4) 请求返回结果:
    {
    "results": [
        {
            "ID": "M01",
            "Info": "",
            "PartName": "MAINICON",
            "Remark": "观众预登记",
            "createdAt": "2019-10-23 18:41:24",
            "objectId": "Dy7Fppp2",
            "updatedAt": "2019-10-24 14:13:20"
        },...
    ]}
 */
@JsonClass(generateAdapter = true)
class NoSQLEntity(val ID: String, val Info: String, val PartName: String, val updatedAt: String) { //, val IsDelete:Boolean
    override fun toString(): String {
        return "Results(ID='$ID', Info='$Info', PartName='$PartName',  updatedAt='$updatedAt')"
    }
}

@JsonClass(generateAdapter = true)
class MainIconModel {
    var IconID: String = ""
    var TitleCN: String = ""
    var TitleTW: String = ""
    var TitleEN: String = ""
    var BaiDu_TJ: String = ""
    var Icon: String = ""
    var MenuSeq: Int = 0
    var DrawerSeq: Int = 0
    var IsDelete: Boolean = false
    var updatedAt: String = ""
}

class NoSQL(var results: List<Results>) {

    class Results(val ID: String,
                  val Info: String,
                  val PartName: String,
                  val updatedAt: String) { //, val IsDelete:Boolean
        override fun toString(): String {
            return "Results(ID='$ID', Info='$Info', PartName='$PartName',  updatedAt='$updatedAt')"
        }
    }

    override fun toString(): String {
        return "NoSQL(results=$results)"
    }


}