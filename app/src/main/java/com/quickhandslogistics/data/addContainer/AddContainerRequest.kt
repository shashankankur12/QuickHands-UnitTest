package com.quickhandslogistics.data.addContainer

class AddContainerRequest(val outbounds:ArrayList<ContainerDetails>, val live:ArrayList<ContainerDetails>, val drop:ArrayList<ContainerDetails>, val scheduleNote:String? = null) {
}