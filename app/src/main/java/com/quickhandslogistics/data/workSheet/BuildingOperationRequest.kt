package com.quickhandslogistics.data.workSheet

import com.google.gson.annotations.SerializedName
import java.util.HashMap

data class BuildingOperationRequest(@SerializedName("parameters") val parameter: HashMap<String, String>)