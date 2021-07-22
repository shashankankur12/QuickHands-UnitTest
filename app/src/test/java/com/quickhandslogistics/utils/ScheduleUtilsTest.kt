package com.quickhandslogistics.utils

import android.content.Context
import android.content.res.Resources
import com.google.common.truth.Truth.assertThat
import com.quickhandslogistics.R
import com.quickhandslogistics.controls.Quintuple
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import com.quickhandslogistics.data.dashboard.LeadProfileData
import com.quickhandslogistics.data.dashboard.ShiftDetail
import com.quickhandslogistics.data.lumpers.EmployeeData
import com.quickhandslogistics.data.schedule.WorkItemDetail
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class ScheduleUtilsTest {
    @Mock
    lateinit var resources: Resources
    @Mock
    lateinit var context: Context
    @Mock
    lateinit var sharedPref: SharedPref

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }
    @Test
    fun getWorkItemTypeDisplayName_success_validString() {
        `when`(resources.getString(R.string.live_loads)).thenReturn("Live Loads")
        val result = ScheduleUtils.getWorkItemTypeDisplayName(SCHEDULE_TYPE_LIVE, resources)
        assertThat(result).isEqualTo(resources.getString(R.string.live_loads))
    }

    @Test
    fun getWorkItemTypeDisplayName_pass_outBound() {
        `when`(resources.getString(R.string.out_bounds)).thenReturn("Outbounds")
        val result = ScheduleUtils.getWorkItemTypeDisplayName(SCHEDULE_TYPE_OUTBOUND, resources)
        assertThat(result).isEqualTo(resources.getString(R.string.out_bounds))
    }

    @Test
    fun getWorkItemTypeDisplayName_pass_drop() {
        `when`(resources.getString(R.string.drops)).thenReturn("Drops")
        val result = ScheduleUtils.getWorkItemTypeDisplayName(SCHEDULE_TYPE_DROP, resources)
        assertThat(result).isEqualTo(resources.getString(R.string.drops))
    }

    @Test
    fun getWorkItemTypeDisplayName_fail_empty() {
        val result = ScheduleUtils.getWorkItemTypeDisplayName(SCHEDULE_TYPE_EMPTY, resources)
        assertThat(result).isEqualTo(SCHEDULE_TYPE_EMPTY)
    }

    @Test
    fun getWorkItemTypeCounts_pass_validList(){
        val result = ScheduleUtils.getWorkItemTypeCounts(getWorkScheduleList())
        assertThat(result).isEqualTo((Triple(3,2,5)))
    }

    @Test
    fun getWorkItemTypeCounts_pass_emptyList(){
        val result = ScheduleUtils.getWorkItemTypeCounts(ArrayList())
        assertThat(result).isEqualTo((Triple(0,0,0)))
    }

    @Test
    fun getAllWorkItemTypeCounts_pass_ValidList(){
        val result = ScheduleUtils.getAllWorkItemTypeCounts(getWorkScheduleList())
        assertThat(result).isEqualTo((Quintuple(3,1,4,2,10)))
    }

    @Test
    fun getAllWorkItemTypeCounts_pass_emptyList(){
        val result = ScheduleUtils.getAllWorkItemTypeCounts(ArrayList())
        assertThat(result).isEqualTo((Quintuple(0,0,0,0,0)))
    }
    
    @Test
    fun sortEmployeesList_pass_validEmployeeList(){
        val result = ScheduleUtils.sortEmployeesList(getEmployeeList(), true)
        assertThat(result[0].firstName).isEqualTo("Alok")
        assertThat(result[1].firstName).isEqualTo("Namit")
        assertThat(result[2].firstName).isEqualTo("Nick")
        assertThat(result[3].firstName).isEqualTo("Shashank")
        assertThat(result[4].firstName).isEqualTo(null)
    }

    @Test
    fun sortEmployeesList_pass_EmptyList(){
        val result = ScheduleUtils.sortEmployeesList(ArrayList())
        assertThat(result).isEmpty()
    }

    @Test
    fun getBuildingParametersList_pass_validResponse(){
        `when`(sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java)).thenReturn(getLeadProfile())
        val result = ScheduleUtils.getBuildingParametersList(sharedPref)
        assertThat(result).isEqualTo(getBuildingParameter())
    }

    @Test
    fun getBuildingParametersList_fail_invalidResponse(){
        `when`(sharedPref.getClassObject(AppConstant.PREFERENCE_LEAD_PROFILE, LeadProfileData::class.java)).thenReturn(LeadProfileData())
        val result = ScheduleUtils.getBuildingParametersList(sharedPref)
        assertThat(result).isEmpty()
    }

    @Test
    fun getOldShiftDetailString_pass_validResponse(){
        val result = ScheduleUtils.getOldShiftDetailString(getLeadProfile())
        assertThat(result).isEqualTo("${SHIFT.capitalize()} (${ScheduleUtils.convertMillisecondsToTimeString(
            START_TIME)} - ${ScheduleUtils.convertMillisecondsToTimeString(END_TIME)})")
    }

    @Test
    fun getOldShiftDetailString_fail_invalidResponse(){
        val result = ScheduleUtils.getOldShiftDetailString(LeadProfileData())
        assertThat(result).isEqualTo("${""} (${""} - ${""})")
    }

    private fun getLeadProfile(): LeadProfileData {
        val leadProfileData = LeadProfileData()
        val shiftDetail = ShiftDetail()
        val buildingDetailDataList: ArrayList<BuildingDetailData> = ArrayList()
        val buildingDetailData = BuildingDetailData()
        leadProfileData.shift = SHIFT
        shiftDetail.startTime= START_TIME
        shiftDetail.endTime= END_TIME
        buildingDetailData.parameters = getBuildingParameter()
        buildingDetailData.morningShift=shiftDetail
        buildingDetailDataList.add(buildingDetailData)
        leadProfileData.buildingDetailData = buildingDetailDataList
        return leadProfileData
    }

    private fun getBuildingParameter(): ArrayList<String> {
        val buildingParameter:ArrayList<String> = ArrayList()
        buildingParameter.add(PARAMETER1)
        buildingParameter.add(PARAMETER2)
        buildingParameter.add(PARAMETER3)
        buildingParameter.add(PARAMETER4)
        return buildingParameter
    }

    private fun getEmployeeList(): ArrayList<EmployeeData>{
        val list: ArrayList<EmployeeData> = ArrayList()
        val employeeData1 = EmployeeData()
        employeeData1.firstName="Shashank"
        employeeData1.lastName="Gautam"
        val employeeData2 = EmployeeData()
        employeeData2.firstName= "Namit"
        val employeeData3 = EmployeeData()
        employeeData3.firstName= "Nick"
        employeeData3.lastName= "Fury"
        val employeeData4 = EmployeeData()
        val employeeData5 = EmployeeData()
        employeeData5.firstName= "Alok"
        employeeData5.lastName= "Dixit"

        list.add(employeeData1)
        list.add(employeeData2)
        list.add(employeeData3)
        list.add(employeeData4)
        list.add(employeeData5)
        return list
    }

    private fun getWorkScheduleList(): ArrayList<WorkItemDetail> {
        val list: ArrayList<WorkItemDetail> = ArrayList()
        val liveLoad1 = WorkItemDetail()
        liveLoad1.type = SCHEDULE_TYPE_LIVE
        liveLoad1.type = SCHEDULE_TYPE_LIVE
        var liveLoad2 = WorkItemDetail()
        liveLoad2.type = SCHEDULE_TYPE_LIVE
        var liveLoad3 = WorkItemDetail()
        liveLoad3.type = SCHEDULE_TYPE_LIVE
        var drop1 = WorkItemDetail()
        drop1.type = SCHEDULE_TYPE_DROP
        var drop2 = WorkItemDetail()
        drop2.type = SCHEDULE_TYPE_DROP
        drop2.origin = AppConstant.SCHEDULE_CONTAINER_ORIGIN_RESUME
        var outbound1 = WorkItemDetail()
        outbound1.type = SCHEDULE_TYPE_OUTBOUND
        var outbound2 = WorkItemDetail()
        outbound2.type = SCHEDULE_TYPE_OUTBOUND
        var outbound3 = WorkItemDetail()
        outbound3.type = SCHEDULE_TYPE_OUTBOUND
        var outbound4 = WorkItemDetail()
        outbound4.type = SCHEDULE_TYPE_OUTBOUND
        var outbound5 = WorkItemDetail()
        outbound5.type = SCHEDULE_TYPE_OUTBOUND
        outbound5.origin = AppConstant.SCHEDULE_CONTAINER_ORIGIN_RESUME

        list.add(liveLoad1)
        list.add(liveLoad2)
        list.add(liveLoad3)
        list.add(drop1)
        list.add(drop2)
        list.add(outbound1)
        list.add(outbound2)
        list.add(outbound3)
        list.add(outbound4)
        list.add(outbound5)

        return list
    }

    companion object {
        const val SCHEDULE_TYPE_LIVE = "LIVE"
        const val SCHEDULE_TYPE_OUTBOUND = "OUTBOUND"
        const val SCHEDULE_TYPE_DROP = "DROP"
        const val SCHEDULE_TYPE_EMPTY = ""
        const val SHIFT = "day"
        const val PARAMETER1 = "paramater1"
        const val PARAMETER2 = "paramater2"
        const val PARAMETER3 = "paramater3"
        const val PARAMETER4 = "paramater4"
        const val START_TIME: Long = 1626769145
        const val END_TIME: Long = 1626855545

    }
}