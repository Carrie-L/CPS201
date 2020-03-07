package com.adsale.chinaplas.ui.tools.schedule

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.adsale.chinaplas.data.dao.ScheduleInfo
import com.adsale.chinaplas.data.dao.ScheduleRepository
import com.adsale.chinaplas.utils.*
import com.adsale.chinaplas.utils.LogUtil.i
import kotlinx.coroutines.*
import java.util.*


/**
 * Created by Carrie on 2020/3/3.
 */
class ScheduleViewModel(private val scheduleRepository: ScheduleRepository) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main) + job

    var schedules = scheduleRepository.getAllSchedules()
    var isNoData = Transformations.map(schedules) {
        it.isEmpty()
    }

    var barClick = MutableLiveData(0)

    fun onTabClick(index: Int) {
        barClick.value = index
    }


    ///////////////////////////////////////////////////////////   编辑/ 添加日程   ///////////////////////////////////////////////////////////
    var title = MutableLiveData<String>()
    var location = MutableLiveData<String>()
    var date = MutableLiveData<String>(SHOW_DATE_1ST)
    var startTime = MutableLiveData<String>("09:00")
    var hour = MutableLiveData("0")
    var minute = MutableLiveData("15")
    var note = MutableLiveData<String>()
    var entity: ScheduleInfo? = null

    var type = 1
    var id = ""
    var isAdd = true

    fun onTimeClick(view: View) {
        val calendar = Calendar.getInstance()
        calendar.time = stringToDate(startTime.value)!!
        val mHour = calendar[Calendar.HOUR_OF_DAY]
        val mMinute = calendar[Calendar.MINUTE]

        val timeDialog = TimePickerDialog(view.context,
            OnTimeSetListener { view, hourOfDay, minute ->
                calendar.timeInMillis = System.currentTimeMillis()
                calendar[Calendar.HOUR_OF_DAY] = hourOfDay
                calendar[Calendar.MINUTE] = minute
                startTime.value = timeToString(calendar.time)
                i("timePicker: " + startTime.value)
            }, mHour, mMinute, true)
        timeDialog.show()
    }

    fun onDateClick(view: View) {
        val currDay: Int = Integer.valueOf(date.value!!.split("-").toTypedArray()[2])
        val calendar = Calendar.getInstance()
        val dateDialog = DatePickerDialog(view.context,
            OnDateSetListener { view, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                date.value = dateToString(calendar.time)
                i(" datePicker: " + date.value)
            }, SHOW_YEAR, SHOW_MONTH, DATE_1)

        //set minDate and maxDate
        val datePicker = dateDialog.datePicker
        calendar[SHOW_YEAR, SHOW_MONTH - 1, DATE_1, 0] = 0 //月份需减1

        val minDate = calendar.timeInMillis
        calendar[SHOW_YEAR, SHOW_MONTH - 1, DATE_4, 23, 59] = 59
        val maxDate = calendar.timeInMillis
        datePicker.minDate = minDate
        datePicker.maxDate = maxDate
        datePicker.updateDate(SHOW_YEAR, SHOW_MONTH - 1, currDay)
        dateDialog.show()
    }

    fun onSave() {
        if (isAdd) {
            entity = ScheduleInfo(null,
                title.value, note.value, location.value, id,
                date.value, startTime.value, type, hour.value!!.toInt(), minute.value!!.toInt())
        } else {
            entity!!.title = title.value
            entity!!.location = location.value
            entity!!.note = note.value
            entity!!.startDate = date.value
            entity!!.startTime = startTime.value
            entity!!.hour = hour.value!!.toInt()
            entity!!.minute = minute.value!!.toInt()
        }
        i("save: schedule = ${entity.toString()}")
        uiScope.launch {
            withContext(Dispatchers.IO) {
                if (isAdd) {
                    i("  添加日程 ")
                    scheduleRepository.insert(entity!!)
                } else {
                    i("  编辑日程 ")
                    scheduleRepository.update(entity!!)
                }

            }
        }
    }

    fun onDelete() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                scheduleRepository.deleteItem(entity!!)
            }
        }
    }

    fun setDetailInfo(entity: ScheduleInfo) {
        this.entity = entity
        title.value = entity.title
        location.value = entity.location
        date.value = entity.startDate
        startTime.value = entity.startTime
        hour.value = entity.hour.toString()
        minute.value = entity.minute.toString()
        note.value = entity.note
    }


}