package com.hibob.academy.service

import com.hibob.bootcamp.unittests.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any

import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UserServiceTest {
    private val userDao = mock<UserDao>{}
    private val notificationService = mock<NotificationService>{}
    private val emailVerificationService = mock<EmailVerificationService>{}
    private val userService = UserService(userDao, notificationService, emailVerificationService)

    @Test
    fun `register null user and throws exception`() {
        val user = User(1 ,"Adi Finkelman", "adifi436@gmail.com", "1234", true)
        whenever(userDao.findById(user.id)).thenReturn(user)
        assertThrows<IllegalArgumentException> { userService.registerUser(user) }
    }

    @Test
    fun `register user isn't saved and throws exception`() {
        val user = User(1, "Adi Finkelman", "adifi436@gmail.com", "1234", false)
        whenever(userDao.findById(user.id)).thenReturn(null)
        whenever((userDao).save(any())).thenReturn(false)
        assertThrows<IllegalStateException> { userService.registerUser(user) }
        verify(userDao).save(user)
    }

    @Test
    fun `register user with verification email null throws exception`() {
        val user = User(1, "Adi Finkelman", "adifi436@gmail.com", "1234", false)
        whenever(userDao.findById(user.id)).thenReturn(null)
        whenever((userDao).save(any())).thenReturn(true)
        whenever(emailVerificationService.sendVerificationEmail(any())).thenReturn(false)
        assertThrows<IllegalStateException> { userService.registerUser(user) }
        verify(userDao).save(user)
        verify(emailVerificationService).sendVerificationEmail(user.email)
    }

    @Test
    fun `register user and return true`() {
        val user = User(1, "Adi Finkelman", "adifi436@gmail.com", "1234", false)
        whenever(userDao.findById(user.id)).thenReturn(null)
        whenever((userDao).save(any())).thenReturn(true)
        whenever(emailVerificationService.sendVerificationEmail(any())).thenReturn(true)
        assertTrue(userService.registerUser(user))
        verify(userDao).save(user)
        verify(emailVerificationService).sendVerificationEmail(user.email)
    }

    @Test
    fun `verify user email with user null throws exception`() {
        val user = User(1, "Adi", "adi@gmail.com", "1234", false)
        whenever(userDao.findById(user.id)).thenReturn(null)
        assertThrows<IllegalArgumentException> { userService.verifyUserEmail(user.id, "token") }
    }

    @Test
    fun `verify user email with not verified email throws exception`() {
        val user = User(1, "Adi", "adi@gmail.com", "1234", false)
        whenever(userDao.findById(user.id)).thenReturn(user)
        whenever(emailVerificationService.verifyEmail(any(), any())).thenReturn(false)
        assertThrows<IllegalArgumentException> { userService.verifyUserEmail(user.id, "token") }
        verify(emailVerificationService).verifyEmail(any(), any())
    }

    @Test
    fun `verify user email with verified email not updated false`() {
        val user = User(1, "Adi", "adi@gmail.com", "1234", true)
        whenever(userDao.findById(user.id)).thenReturn(user)
        whenever(emailVerificationService.verifyEmail(any(), any())).thenReturn(true)
        whenever(userDao.update(user)).thenReturn(false)
        assertFalse(userService.verifyUserEmail(user.id, "token"))
        verify(emailVerificationService).verifyEmail(any(), any())
    }

    @Test
    fun `verify user email with verified email updated return true`() {
        val user = User(1, "Adi", "adi@gmail.com", "1234", true)
        whenever(userDao.findById(user.id)).thenReturn(user)
        whenever(emailVerificationService.verifyEmail(any(), any())).thenReturn(true)
        whenever(userDao.update(user)).thenReturn(true)
        assertTrue(userService.verifyUserEmail(user.id, "token"))
        verify(emailVerificationService).verifyEmail(any(), any())
        verify(notificationService).sendEmail(any(), any())
    }
}