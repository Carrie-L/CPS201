package com.adsale.chinaplas.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract.*
import com.adsale.chinaplas.R
import com.adsale.chinaplas.utils.LogUtil.i
import com.adsale.chinaplas.utils.PermissionUtil
import com.adsale.chinaplas.utils.PermissionUtil.*
import com.adsale.chinaplas.utils.alertDialogSingleConfirm
import java.util.*

/**
 * Created by Carrie on 2020/1/15.
 */
class CalendarHelper private constructor(private val activity: Activity) {
    private val YEAR = 2020
    private val MONTH = 3 // 月份-1
    private val START_DAY = 21
    private val ACCOUNT_NAME = "ChinaPlas@gmail.com"
    private val CALENDAR_DISPLAY_NAME = "ChinaPlas20"

    companion object {
        @Volatile
        private var instance: CalendarHelper? = null

        fun getInstance(activity: Activity) =
            instance ?: synchronized(this) {
                instance ?: CalendarHelper(activity).also { instance = it }
            }
    }

    fun addToCalendar() {
        try {
            if (!checkPermission(activity, PERMISSION_READ_CALENDAR)) {
                requestPermission(activity, PERMISSION_READ_CALENDAR, PMS_CODE_READ_CALENDAR)
                return
            }
            if (!checkPermission(activity, PERMISSION_WRITE_CALENDAR)) {
                i("沒有寫日曆的權限，請求")
                requestPermission(activity, PERMISSION_WRITE_CALENDAR, PMS_CODE_WRITE_CALENDAR)
                return
            }
            var event: ContentValues
            var values: ContentValues
            var calendar: Calendar
            var newEvent: Uri?
            var cr: ContentResolver
            var start01: Long
            var end01: Long
            var id: Long
            val calId = getCalendarId()
            i("calId = $calId")
            if (calId == -1) {
                i("没有日历权限，返回。")
                alertDialogSingleConfirm(activity, R.string.addToCalendar_fail)
                return
            }
            for (i in 0..3) {
                event = ContentValues()
//                event.put(Events.TITLE, activity.getString(R.string.notes))
                event.put(Events.TITLE, activity.getString(R.string.calendar_description))
                event.put(Events.EVENT_LOCATION, activity.getString(R.string.calendar_location))
                event.put(Events.CALENDAR_ID, calId)
                calendar = Calendar.getInstance()
                calendar[YEAR, MONTH, START_DAY + i, 9] = 30
                start01 = calendar.time.time
                calendar[YEAR, MONTH, START_DAY + i, 17] = 0 // 11.30
                end01 = calendar.time.time
                event.put(Events.DTSTART, start01)
                event.put(Events.DTEND, end01)
                event.put(Events.HAS_ALARM, 1)
                event.put(EXTRA_EVENT_ALL_DAY, 0)
                event.put(Events.STATUS, 1)
                event.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
                event.put(Events.GUESTS_CAN_MODIFY, true) //参与者能否修改事件  2016/10/28
                newEvent = activity.contentResolver.insert(Events.CONTENT_URI, event)
                id = newEvent!!.lastPathSegment!!.toLong()
                i("id=$id")
                values = ContentValues()
                values.put(Reminders.EVENT_ID, id)
                values.put(Reminders.MINUTES, 1 * 60 * 24) // 设置为提前一天提醒
                values.put(Reminders.METHOD, Reminders.METHOD_ALERT)
                cr = activity.contentResolver // 为刚才新添加的event添加reminder
                cr.insert(Reminders.CONTENT_URI, values) // 调用这个方法返回值是一个Uri
            }
            alertDialogSingleConfirm(activity, R.string.addToCalendar_success)
        } catch (e: Exception) {
            e.printStackTrace()
            alertDialogSingleConfirm(activity, R.string.addToCalendar_fail)
        }
    }

    private fun getCalendarId(): Int {
        val cr = activity.contentResolver
        return if (checkPermission(activity, PERMISSION_READ_CALENDAR)) {
            val userCursor =
                cr.query(Calendars.CONTENT_URI, null, null, null, null)
            var calId = 0
            if (userCursor != null && userCursor.count > 0) { //先获取用户日历账户，如果没有，则初始化添加账户
                while (userCursor.moveToNext()) {
                    if (userCursor.getString(userCursor.getColumnIndex(Calendars.ACCOUNT_NAME)) == ACCOUNT_NAME) {
                        calId = userCursor.getInt(userCursor.getColumnIndex("_id"))
                        break
                    }
                }
                userCursor.close()
            }
            if (calId == 0) {
                calId = initCalendars2(activity)
            }
            i("calId=$calId")
            calId
        } else {
            requestPermission(activity, PERMISSION_WRITE_CALENDAR, PMS_CODE_WRITE_CALENDAR)
            -1
        }
    }

    /**
     * 添加账户
     *
     * @param context
     */
    @SuppressLint("MissingPermission")
    private fun initCalendars2(context: Context): Int { //插入成功
        val calId = 3
        val timeZone = TimeZone.getDefault()
        val value = ContentValues()
        value.put("_id", calId)
        value.put(Calendars.NAME, "CPS")
        value.put(Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
        value.put(Calendars.ACCOUNT_TYPE, "com.adsale.ChinaPlas") //"com.android.exchange"
        value.put(Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_DISPLAY_NAME)
        value.put(Calendars.VISIBLE, 1)
        value.put(Calendars.CALENDAR_COLOR, -9206951)
        value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER)
        value.put(Calendars.SYNC_EVENTS, 1)
        value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.id)
        value.put(Calendars.OWNER_ACCOUNT, ACCOUNT_NAME)
        value.put(Calendars.CAN_ORGANIZER_RESPOND, 0)
        var calendarUri = Calendars.CONTENT_URI
        calendarUri = calendarUri.buildUpon().appendQueryParameter(CALLER_IS_SYNCADAPTER, "true")
            .appendQueryParameter(Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
            .appendQueryParameter(Calendars.ACCOUNT_TYPE, "com.adsale.ChinaPlas")
            .build()
        context.contentResolver.insert(calendarUri, value)
        return calId
    }


}