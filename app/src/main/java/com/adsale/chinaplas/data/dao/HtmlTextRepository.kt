package com.adsale.chinaplas.data.dao

/**
 * Created by Carrie on 2020/3/6.
 */
class HtmlTextRepository private constructor(private val htmlTextDao: HtmlTextDao) {

    fun insertAll(list: List<HtmlText>) = htmlTextDao.insertAll(list)

    fun insertItemHtmlText(entity: HtmlText) = htmlTextDao.insertItemHtmlText(entity)

    fun searchHtmlText(keyword: String): List<HtmlText> = htmlTextDao.searchHtmlText(keyword)


    companion object {
        @Volatile
        private var instance: HtmlTextRepository? = null

        fun getInstance(htmlTextDao: HtmlTextDao) =
            instance ?: synchronized(this) {
                instance ?: HtmlTextRepository(htmlTextDao).also { instance = it }
            }
    }

}