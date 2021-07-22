package com.quickhandslogistics

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.quickhandslogistics.testUtils.LoginTestUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LoginTest {

    @Test
    fun testWithValidUser(){
        val userId= "FB16ZI"
        val password= "password"
        val result =LoginTestUtils.validateLoginDetails(userId, password)
         assertThat(result).isEqualTo(true)
    }
    @Test
    fun testWithWithOutUserID(){
        val userId= ""
        val password= "password"
        val result =LoginTestUtils.validateLoginDetails(userId, password)
         assertThat(result).isEqualTo(false)
    }
    @Test
    fun testWithWithOutPassword(){
        val userId= "FB17ZI"
        val password= ""
        val result =LoginTestUtils.validateLoginDetails(userId, password)
         assertThat(result).isEqualTo(false)
    }

    @Test
    fun testWithInValidPassword(){
        val userId= "FB17ZI"
        val password= "pass"
        val result =LoginTestUtils.validateLoginDetails(userId, password)
         assertThat(result).isEqualTo(false)
    }
}