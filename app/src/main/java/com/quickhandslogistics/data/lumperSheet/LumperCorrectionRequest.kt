package com.quickhandslogistics.data.lumperSheet

import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.workSheet.UpdateLumperTimeRequest
import java.util.*

data class LumperCorrectionRequest(
        @SerializedName("lumperId") val lumperId: String?,
        @SerializedName("containerId") val workItemId: String?,
        @SerializedName("workTiming") val workTiming: LumperParameterCorrection,
        @SerializedName("containerParameters") var containerParameters: HashMap<String, String>? = null,
        @SerializedName("notesForQHL") var notesForQHL: String? = null,
        @SerializedName("correctionNote") var correctionNote: String? = null
)
