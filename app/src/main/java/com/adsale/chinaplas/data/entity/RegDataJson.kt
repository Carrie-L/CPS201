package com.adsale.chinaplas.data.entity

/**
 * Created by Carrie on 2019/11/1.
 */
class RegDataJson(var JobTitleList: List<RegProperty1>,var JobFuctionList: List<RegProperty1>,var ProductList: List<RegProperty1>, var Api_CountryCity: List<CountryJson>) {
    override fun toString(): String {
        return "RegDataJson(Api_CountryCity=$Api_CountryCity)"
    }

     class RegProperty1(var Name: String, var DetailCode: String){
         var Code:String=""
     }


}