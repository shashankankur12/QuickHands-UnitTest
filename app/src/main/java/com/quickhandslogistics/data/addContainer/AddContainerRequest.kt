package com.quickhandslogistics.data.addContainer

class AddContainerRequest(val scheduleForMonth:String, val outbounds:ArrayList<ContainerDetails>, val live:ArrayList<ContainerDetails>, val drop:ArrayList<ContainerDetails>, val buildingName:String, val shift:String, val scheduleNote:String="", val scheduleForWeek: Boolean? = false, val scheduleForMonths:Boolean?=false) {
}