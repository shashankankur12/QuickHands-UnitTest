package com.quickhandslogistics.utils
interface AppConstant {

    companion  object {
        //Shared Preference Keys
        val PREF_AUTH_TOKEN = "pref_auth_token"
        const val PREFERENCE_EMPLOYEE_ID = "pref_emp_id"
        const val PREFERENCE_IS_ACTIVE = "pref_is_active"
        const val PREFERENCE_USER_EMAIL = "pref_email"
        const val PREFERENCE_USER_FIRST_NAME = "pref_userfirstname"
        const val PREFERENCE_USER_FULL_NAME = "pref_user_name"
        const val PREFERENCE_USER_LAST_NAME = "pref_userlastname"
        const val PREFERENCE_USER_PHONE = "pref_userphone"
        const val PREFERENCE_USER_ROLE = "pref_user_role"

        val EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$"
        const val PREF_LANGUAGE_SELECT = "pref_language_select"
        const val APP_SHARED_PREFERENCE = "app_shared_prefrences"
        const val CHECKED = "checked"
        const val LANGUAGE = "language"
        const val ESPANOL = "es"
        const val ENGLISH = "en-us"
        val PREF_TOKEN_ID = "pref_token_id"
        val PREF_IS_LOGIN = "pref_is_login"
        val PREF_IS_EMAIL_VERFD = "pref_is_email_vrfd"
        val PREF_IS_PHONE_VERFD = "pref_is_phone_vrfd"
        val PREF_USERID = "pref_user_id"
        val PREF_USERIMAGE = "pref_userimage"
        val PREF_KEY_USER_LOGGED_IN = "PREF_KEY_USER_LOGGED_IN"
        val PREF_KEY_USER_SESSION = "PREF_KEY_USER_SESSION"
        val EDIT_DIALOG = "edit_dialog"

    }
}