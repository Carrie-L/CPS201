package com.adsale.chinaplas.data.dao

/**
 * Created by Carrie on 2019/12/24.
 */
class WebContentRepository private constructor(private val webContentDao: WebContentDao,
                                               private val htmlTextDao: HtmlTextDao,
                                               private val fileControlDao: FileControlDao) {

    fun getLastUpdateTime() = webContentDao.getLastUpdateTime()

    fun insertWebContentAll(list: List<WebContent>) = webContentDao.insertAll(list)

    fun getWebContents(parentID: String) = webContentDao.getWebContents(parentID)

    fun getPageIDs(parentID: String) = webContentDao.getPageIDs(parentID)


    /*-------------------|||   HtmlText   |||---------------------*/
    fun insertHtmlTextAll(list: List<HtmlText>) = htmlTextDao.insertAll(list)

    fun insertItemHtmlText(entity: HtmlText) = htmlTextDao.insertItemHtmlText(entity)

    // todo  查询，读取, PageID 与 WebContent 联动


    /* ------------------------|||  FileControl ||| ----------------------------- */
    fun getFileControlLUT() = fileControlDao.getLastUpdateTime()

    fun getFileControls() = fileControlDao.getFileControls()

    fun insertFileControlAll(list: List<FileControl>) = fileControlDao.insertAll(list)


    companion object {
        @Volatile
        private var instance: WebContentRepository? = null

        fun getInstance(webContentDao: WebContentDao, htmlTextDao: HtmlTextDao, fileControlDao: FileControlDao) =
            instance ?: synchronized(this) {
                instance ?: WebContentRepository(webContentDao, htmlTextDao, fileControlDao).also { instance = it }
            }
    }

}