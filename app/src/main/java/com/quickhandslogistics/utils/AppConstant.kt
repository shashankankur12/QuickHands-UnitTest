package com.quickhandslogistics.utils

interface AppConstant {

    companion  object {
        val EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$"
        const val PREF_LANGUAGE_SELECT = "pref_language_select"
        const val APP_SHARED_PREFERENCE = "app_shared_prefrences"
        const val CHECKED = "checked"
        const val LANGUAGE = "language"
        const val ESPANOL = "es"
        const val ENGLISH = "en-us"
        val PREF_TOKEN_ID = "pref_token_id"
        val PREF_IS_LOGIN = "pref_is_login"
        val PREF_IS_ACTIVE = "pref_is_active"
        val PREF_IS_EMAIL_VERFD = "pref_is_email_vrfd"
        val PREF_IS_PHONE_VERFD = "pref_is_phone_vrfd"
        val PREF_EMAIL = "pref_email"
        val PREF_USERFIRSTNAME = "pref_userfirstname"
        val PREF_USERLASTNAME = "pref_userlastname"
        val PREF_USERPHONE = "pref_userphone"
        val PREF_ROLE = "pref_user_role"
        val PREF_USERID = "pref_user_id"
        val PREF_USERIMAGE = "pref_userimage"

    }
}