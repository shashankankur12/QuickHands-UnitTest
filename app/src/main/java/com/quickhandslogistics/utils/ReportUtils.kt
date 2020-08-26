package com.quickhandslogistics.utils

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

object ReportUtils {

    fun showStartDatePicker(startDate: Date?, endDate: Date?, context: Context, listener: OnDateSetListener) {
        val calendar = Calendar.getInstance()
        startDate?.let { date ->
            calendar.time = date
        }
        val picker = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            listener.onDateSet(calendar.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        val startCalendar = Calendar.getInstance()
//        startCalendar.set(2017, Calendar.JANUARY, 1)
        startCalendar.add(Calendar.MONTH, -1)
        picker.datePicker.minDate = startCalendar.timeInMillis

        endDate?.also { date ->
            picker.datePicker.maxDate = date.time
        } ?: run {
            picker.datePicker.maxDate = System.currentTimeMillis()
        }
        picker.show()
    }

    fun showEndDatePicker(startDate: Date?, endDate: Date?, context: Context, listener: OnDateSetListener) {
        val calendar = Calendar.getInstance()
        endDate?.let { date ->
            calendar.time = date
        }
        val picker = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            listener.onDateSet(calendar.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        startDate?.also { date ->
            picker.datePicker.minDate = date.time
        } ?: run {
            val startCalendar = Calendar.getInstance()
            startCalendar.add(Calendar.MONTH, -1)
            //startCalendar.set(2017, Calendar.JANUARY, 1)
            picker.datePicker.minDate = startCalendar.timeInMillis
        }
        picker.datePicker.maxDate = System.currentTimeMillis()
        picker.show()
    }

    fun showDatePicker(startDate1: Date?,endDate: Date?, context: Context, listener: OnDateSetListener) {
        val calendar = Calendar.getInstance()
        val startDate=Date()
        startDate1?.let { date ->
            calendar.time = date
        }
        val picker = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            listener.onDateSet(calendar.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        val startCalendar = Calendar.getInstance()
//        startCalendar.set(2017, Calendar.JANUARY, 1)
        startCalendar.add(Calendar.DATE, -9)
        picker.datePicker.minDate = startCalendar.timeInMillis
        picker.datePicker.maxDate = startDate!!.time

//        endDate?.also { date ->
//            picker.datePicker.maxDate = date.time
//        } ?: run {
//            picker.datePicker.maxDate = System.currentTimeMillis()
//        }
        picker.show()
    }

    interface OnDateSetListener {
        fun onDateSet(selected: Date)
    }
}

