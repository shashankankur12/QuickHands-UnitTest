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
        const val PREFERENCE_NOTIFICATION = "pref_notification"

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

        // REQUEST LUMPERS Enums
        const val REQUEST_LUMPERS_STATUS_PENDING = "pending"
        const val REQUEST_LUMPERS_STATUS_APPROVED = "approved"
        const val REQUEST_LUMPERS_STATUS_REJECTED = "rejected"

        // Attendance
        const val ATTENDANCE_IS_PRESENT = "ATTENDANCE_IS_PRESENT"
        const val ATTENDANCE_MORNING_PUNCH_IN = "ATTENDANCE_MORNING_PUNCH_IN"
        const val ATTENDANCE_EVENING_PUNCH_OUT = "ATTENDANCE_EVENING_PUNCH_OUT"
        const val ATTENDANCE_LUNCH_PUNCH_IN = "ATTENDANCE_LUNCH_PUNCH_IN"
        const val ATTENDANCE_LUNCH_PUNCH_OUT = "ATTENDANCE_LUNCH_PUNCH_OUT"

        const val NOTES_NOT_AVAILABLE = "NA"

        const val REQUEST_CODE_CHANGED = 101

        const val API_PAGE_SIZE = 20

        const val LANGUAGE_ENGLISH_CODE = "en-US"
        const val LANGUAGE_SPANISH_CODE = "es"

    }
}