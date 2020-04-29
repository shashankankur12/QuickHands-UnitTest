package com.quickhandslogistics.utils

interface AppConstant {

    companion object {
        //Shared Preference Keys
        const val PREFERENCE_NAME = "QuickHands_Pref"
        const val PREFERENCE_LEAD_PROFILE = "pref_lead_profile"
        const val PREFERENCE_AUTH_TOKEN = "pref_auth_token"
        const val PREFERENCE_EMPLOYEE_ID = "pref_emp_id"
        const val PREFERENCE_LANGUAGE = "pref_language"
        const val PREFERENCE_BUILDING_ID = "pref_building_id"
        const val PREFERENCE_REGISTRATION_TOKEN = "pref_registration_token"

        //Notification Data Keys
        const val NOTIFICATION_KEY_TITLE = "NotificationTitle"
        const val NOTIFICATION_KEY_CONTENT = "NotificationDescription"
        const val NOTIFICATION_KEY_TYPE = "NotificationType"
        const val NOTIFICATION_KEY_SCHEDULE_IDENTITY = "ScheduleIdentity"
        const val NOTIFICATION_KEY_SCHEDULE_FROM_DATE = "ScheduledFromDate"

        //Notification Types
        const val NOTIFICATION_TYPE_SCHEDULE_CREATE = "ScheduleCreate"

        // API Enums
        const val WORK_ITEM_STATUS_ON_HOLD = "ON-HOLD"
        const val WORK_ITEM_STATUS_IN_PROGRESS = "IN-PROGRESS"
        const val WORK_ITEM_STATUS_COMPLETED = "COMPLETED"
        const val WORK_ITEM_STATUS_CANCELLED = "CANCELLED"
        const val WORK_ITEM_STATUS_SCHEDULED = "SCHEDULED"

        const val NOTES_NOT_AVAILABLE = "NA"

        const val EDIT_DIALOG = "edit_dialog"

        const val REQUEST_CODE_CHANGED = 101
    }
}