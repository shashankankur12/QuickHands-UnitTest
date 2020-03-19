package com.quickhandslogistics.utils

import android.text.TextUtils.isEmpty

object StringUtils {

    private val EMAIL_PATTERN =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    private val NAME_PATTERN = "^[A-Za-z0-9\\s]{1,}[\\.]{0,1}[A-Za-z0-9\\s]{0,}$"
    private val NUMBER_PATTERN = "^[0-9]+$"

    fun isValidEmailId(emailId: String): Boolean {
        return emailId.trim { it <= ' ' }.matches(EMAIL_PATTERN.toRegex())
    }

    fun isValidName(name: String): Boolean {
        return name.trim { it <= ' ' }.matches(NAME_PATTERN.toRegex())
    }

    fun isValidNumber(number: String): Boolean {
        return number.trim { it <= ' ' }.matches(NUMBER_PATTERN.toRegex())
    }

    fun capitalizeFirstLetter(str: String): String? {
        if (isEmpty(str)) {
            return str
        }

        val c = str[0]
        return if (!Character.isLetter(c) || Character.isUpperCase(c))
            str
        else
            StringBuilder(str.length)
                .append(Character.toUpperCase(c)).append(str.substring(1)).toString()
    }

    fun isNullOrEmpty(value: String?): Boolean {
        var ret = false
        value?.also {
            if (value.trim().isEmpty()) {
                ret = true
            }
        } ?: run {
            ret = true
        }
        return ret
    }
}

