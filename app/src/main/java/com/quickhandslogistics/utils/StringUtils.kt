package com.quickhandslogistics.utils

object StringUtils {
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

