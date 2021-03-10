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
        const val PREFERENCE_BUILDING_DETAILS = "pref_building_details"
        const val PREFERENCE_REGISTRATION_TOKEN = "pref_registration_token"
        const val PREFERENCE_NOTIFICATION = "pref_notification"

        //Notification Data Keys
        const val NOTIFICATION_KEY_TITLE = "NotificationTitle"
        const val NOTIFICATION_KEY_CONTENT = "NotificationDescription"
        const val NOTIFICATION_KEY_TYPE = "NotificationType"
        const val NOTIFICATION_KEY_SCHEDULE_IDENTITY = "ScheduleIdentity"
        const val NOTIFICATION_KEY_SCHEDULE_FROM_DATE = "ScheduledFromDate"
        const val NOTIFICATION_KEY_DATE = "Date"

        //Notification Types
        const val NOTIFICATION_TYPE_SCHEDULE_CREATE = "schedule-created"
        const val NOTIFICATION_TYPE_SCHEDULE_UPDATE = "schedule-updated"
        const val NOTIFICATION_TYPE_LUMPER_CHANGED = "lumper-assignment-update"
        const val NOTIFICATION_TYPE_LUMPER_REQUEST_APPROVED = "temp-lumpers-req-approved"
        const val NOTIFICATION_TYPE_LUMPER_REQUEST_REJECTED = "temp-lumpers-req-rejected"
        const val NOTIFICATION_TYPE_LEAD_BUILDING_ADDED = "lead-building-added"
        const val NOTIFICATION_TYPE_LEAD_BUILDING_REMOVED = "lead-building-removed"

        // API Enums
        const val WORK_ITEM_STATUS_ON_HOLD = "ON-HOLD"
        const val WORK_ITEM_STATUS_IN_PROGRESS = "IN-PROGRESS"
        const val WORK_ITEM_STATUS_COMPLETED = "COMPLETED"
        const val WORK_ITEM_STATUS_CANCELLED = "CANCELLED"
        const val WORK_ITEM_STATUS_SCHEDULED = "SCHEDULED"
        const val WORK_ITEM_SHOW_MORE = "SHOW MORE STATUS"
        const val WORK_ITEM_SHOW_LESS = "SHOW LESS STATUS"
        const val WORK_ITEM_STATUS_UNFINISHED = "UNFINISHED"
        const val WORK_ITEM_STATUS_NOT_OPEN = "NOT-OPEN"

        // Employee Department Enums
            const val EMPLOYEE_DEPARTMENT_INBOUND = "RECEIVING"
        const val EMPLOYEE_DEPARTMENT_OUTBOUND = "SHIPPING"
        const val EMPLOYEE_DEPARTMENT_BOTH = "OPERATION"

        // Employee Swift Enums
        const val EMPLOYEE_SHIFT_MORNING = "day"
        const val EMPLOYEE_SHIFT_SWING = "swing"
        const val EMPLOYEE_SHIFT_NIGHT = "night"

        // REQUEST LUMPERS Enums
        const val REQUEST_LUMPERS_STATUS_PENDING = "pending"
        const val REQUEST_LUMPERS_STATUS_APPROVED = "approved"
        const val REQUEST_LUMPERS_STATUS_REJECTED = "rejected"
        const val REQUEST_LUMPERS_STATUS_CANCELLED = "cancelled"

        //Worksheet
        const val WORKSHEET_WORK_ITEM_LIVE = "LIVE"
        const val WORKSHEET_WORK_ITEM_OUTBOUND = "OUTBOUND"
        const val WORKSHEET_WORK_ITEM_INBOUND = "DROP"


        // Attendance
        const val ATTENDANCE_IS_PRESENT = "ATTENDANCE_IS_PRESENT"
        const val ATTENDANCE_MORNING_PUNCH_IN = "ATTENDANCE_MORNING_PUNCH_IN"
        const val ATTENDANCE_EVENING_PUNCH_OUT = "ATTENDANCE_EVENING_PUNCH_OUT"
        const val ATTENDANCE_LUNCH_PUNCH_IN = "ATTENDANCE_LUNCH_PUNCH_IN"
        const val ATTENDANCE_LUNCH_PUNCH_OUT = "ATTENDANCE_LUNCH_PUNCH_OUT"

        const val NOTES_NOT_AVAILABLE = "N/A"

        const val REQUEST_CODE_CHANGED = 101

        const val API_PAGE_SIZE = 20

        const val LANGUAGE_ENGLISH_CODE = "en-US"
        const val LANGUAGE_SPANISH_CODE = "es"


        const val VIEW_DETAILS = "VIEW_DETAILS"

        //roles
        const val LEADS = "lead"
        const val DISTRICT_MANAGER = "District Manager"
        const val MANAGER = "manager"
        const val SUPERVISOR = "supervisor"
        const val LUMPER = "lumper"

    }
}