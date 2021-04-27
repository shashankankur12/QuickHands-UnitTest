package com.quickhandslogistics.data.workSheet

data class SaveNoteWorkItemRequest(val containerIds: ArrayList<String>, val containerType: String, val noteForCustomer: String, val noteForQHL: String)