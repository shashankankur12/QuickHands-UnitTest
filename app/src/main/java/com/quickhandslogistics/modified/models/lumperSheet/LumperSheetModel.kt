package com.quickhandslogistics.modified.models.lumperSheet

import com.quickhandslogistics.modified.contracts.lumperSheet.LumperSheetContract
import com.quickhandslogistics.modified.data.lumperSheet.LumperModel
import com.quickhandslogistics.modified.data.lumperSheet.StatusModel
import io.bloco.faker.Faker

class LumperSheetModel : LumperSheetContract.Model {
    val lumperList: ArrayList<LumperModel> = ArrayList()
    val lumperStatusList: ArrayList<StatusModel> = ArrayList()


    override fun fetchLumperSheetList(onFinishedListener: LumperSheetContract.Model.OnFinishedListener) {
        val faker = Faker()

        for (i in 1..20) {
            if (i % 2 == 0) {
                lumperList.add(
                    LumperModel(
                        faker.name.firstName(),
                        faker.name.lastName(),
                        "In Progress"
                    )
                )
            } else {
                lumperList.add(
                    LumperModel(
                        faker.name.firstName(),
                        faker.name.lastName(),
                        "Complete"
                    )
                )
            }

        }

        onFinishedListener.onSuccess(lumperList)
    }
}