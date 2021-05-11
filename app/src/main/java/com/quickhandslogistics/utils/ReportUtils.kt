package com.quickhandslogistics.utils

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

object ReportUtils {

    fun showStartDatePicker(startDate: Date?, endDate: Date?, context: Context, isCustome:Boolean, listener: OnDateSetListener) {
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

        if (isCustome)
            endDate?.also { date ->
                picker.datePicker.maxDate = date.time
            } ?: run {
                picker.datePicker.maxDate = System.currentTimeMillis()
            }
        else picker.datePicker.maxDate = System.currentTimeMillis()
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

    fun showTomorrowDatePicker(startDate1: Date?, context: Context, listener: OnDateSetListener) {
        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.DAY_OF_YEAR,1)
        startDate1?.let { date ->
            calendar.time = date
        }
        val picker = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            listener.onDateSet(calendar.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        val startCalendar = Calendar.getInstance()
//        startCalendar.add(Calendar.DAY_OF_YEAR,1)
        picker.datePicker.minDate = startCalendar.timeInMillis

        picker.show()
    }

    interface OnDateSetListener {
        fun onDateSet(selected: Date)
    }
}

