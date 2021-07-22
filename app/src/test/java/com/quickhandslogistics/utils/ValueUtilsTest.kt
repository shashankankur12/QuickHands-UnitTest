package com.quickhandslogistics.utils


import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ValueUtilsTest{
    @Test
    fun getDefaultOrValue_pass_validValue(){
        val result= ValueUtils.getDefaultOrValue(true)
        assertThat(result).isTrue()
    }

    @Test
    fun getDefaultOrValue_fail_nullValue(){
        val bool:Boolean? =null
        val result= ValueUtils.getDefaultOrValue( bool)
        assertThat(result).isFalse()
    }

    @Test
    fun getDefaultOrValue_pass_validIntValue(){
        val value:Int? =33
        val result= ValueUtils.getDefaultOrValue(value)
        assertThat(result).isEqualTo(value)
    }

    @Test
    fun getDefaultOrValue_fail_invalidIntValue(){
        val value:Int? =null
        val result= ValueUtils.getDefaultOrValue(value)
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun getDefaultOrValue_fail_validStringValue(){
        val value:String? ="test"
        val result= ValueUtils.getDefaultOrValue(value)
        assertThat(result).isEqualTo(value)
    }

    @Test
    fun getDefaultOrValue_fail_invalidStringValue(){
        val value:String? = null
        val result= ValueUtils.getDefaultOrValue(value)
        assertThat(result).isEmpty()
    }

    @Test
    fun isNumeric_pass_validString(){
        val value ="77"
        val result= ValueUtils.isNumeric(value)
        assertThat(result).isTrue()
    }

    @Test
    fun isNumeric_fail_invalidString(){
        val value ="nan"
        val result= ValueUtils.isNumeric(value)
        assertThat(result).isFalse()
    }

    @Test
    fun isNumeric_fail_invalidStringTest (){
        val value ="hjhj"
        val result= ValueUtils.isNumeric(value)
        assertThat(result).isFalse()
    }

    @Test
    fun getHoursFromMinutes_pass_validString(){
        val value ="120"
        val result= ValueUtils.getHoursFromMinutes(value)
        assertThat(result).isEqualTo("2")
    }

    @Test
    fun getHoursFromMinutes_fail_invalidString(){
        val value: String? = null
        val result= ValueUtils.getHoursFromMinutes(value)
        assertThat(result).isEqualTo("0")
    }
    @Test
    fun getRemainingMinutes_pass_validString(){
        val value: String? = "150"
        val result= ValueUtils.getRemainingMinutes(value)
        assertThat(result).isEqualTo("30")
    }

    @Test
    fun getRemainingMinutes_fail_invalidString(){
        val value: String? = null
        val result= ValueUtils.getRemainingMinutes(value)
        assertThat(result).isEqualTo("0")
    }
}