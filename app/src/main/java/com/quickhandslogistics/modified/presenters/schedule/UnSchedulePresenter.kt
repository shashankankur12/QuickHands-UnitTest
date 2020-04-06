package com.quickhandslogistics.modified.presenters.schedule

import android.content.res.Resources
import com.quickhandslogistics.R
import com.quickhandslogistics.modified.contracts.schedule.UnScheduleContract
import com.quickhandslogistics.modified.data.schedule.ScheduleListAPIResponse
import com.quickhandslogistics.modified.data.schedule.ScheduleDetail
import com.quickhandslogistics.modified.models.schedule.UnScheduleModel
import com.quickhandslogistics.utils.DateUtils
import com.quickhandslogistics.utils.SharedPref
import java.util.*

class UnSchedulePresenter(
    private var unScheduleView: UnScheduleContract.View?,
    private val resources: Resources,
    sharedPref: SharedPref
) :
    UnScheduleContract.Presenter, UnScheduleContract.Model.OnFinishedListener {

    private val unScheduleModel: UnScheduleModel = UnScheduleModel(sharedPref)

    override fun getUnScheduledWorkItems(showProgressDialog: Boolean) {
        if (showProgressDialog) {
            unScheduleView?.showProgressDialog(resources.getString(R.string.api_loading_message))
        }
        unScheduleModel.fetchUnSchedulesByDate(this)
    }

    override fun onFailure(message: String) {

    }

    override fun onSuccess(
        unScheduleListAPIResponse: ScheduleListAPIResponse
    ) {
        val workItemsList = ArrayList<ScheduleDetail>()
        unScheduleListAPIResponse.data?.scheduleDetailsList?.let {
            workItemsList.addAll(it)
        }

        val iterate = workItemsList.listIterator()
        while (iterate.hasNext()) {
            val oldValue = iterate.next()
            var scheduleTypeNames = ""
            oldValue.scheduleTypes?.liveLoads?.let {
                if (it.isNotEmpty()) {
                    scheduleTypeNames = resources.getString(R.string.string_live_loads)
                }
            }

            oldValue.scheduleTypes?.drops?.let {
                if (it.isNotEmpty()) {
                    if (scheduleTypeNames.isNotEmpty()) {
                        scheduleTypeNames += ", "
                    }
                    scheduleTypeNames += resources.getString(R.string.string_drops)
                }
            }

            oldValue.scheduleTypes?.outbounds?.let {
                if (it.isNotEmpty()) {
                    if (scheduleTypeNames.isNotEmpty()) {
                        scheduleTypeNames += ", "
                    }
                    scheduleTypeNames += resources.getString(R.string.string_out_bonds)
                }
            }
            oldValue.scheduleTypeNames = scheduleTypeNames
            iterate.set(oldValue)
        }

        workItemsList.sortWith(Comparator { workItem1, workItem2 ->
            val dateLong1 = DateUtils.getMillisecondsFromDateString(
                DateUtils.PATTERN_API_REQUEST_PARAMETER,
                workItem1?.startDate
            )
            val dateLong2 = DateUtils.getMillisecondsFromDateString(
                DateUtils.PATTERN_API_REQUEST_PARAMETER,
                workItem2?.startDate
            )
            dateLong1.compareTo(dateLong2)
        })

        unScheduleView?.showUnScheduleData(workItemsList)
        unScheduleView?.hideProgressDialog()
    }
}
