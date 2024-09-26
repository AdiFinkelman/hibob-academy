package com.hibob.project.service

import com.hibob.project.dao.*
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.NotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.sql.Timestamp
import java.time.LocalDateTime

class FeedbackServiceTest {
    private val feedbackDao = mock<FeedbackDao>()
    private val feedbackService = FeedbackService(feedbackDao)
    val companyIdTest = 1234L

    @Test
    fun `get all feedbacks where feedback list is empty`() {
        assertEquals(emptyList<FeedbackCreationRequest>(), feedbackService.getAllFeedbacks(companyIdTest))
    }

    @Test
    fun `get all feedbacks successfully`() {
        val feedbackList = listOf(FeedbackConfiguration(1, 1, companyIdTest, "Service Test", Timestamp.valueOf(LocalDateTime.now()), false, StatusType.UNREVIEWED))
        whenever(feedbackDao.getAllFeedbacks(companyIdTest)).thenReturn(feedbackList)
        val feedbacks = feedbackService.getAllFeedbacks(companyIdTest)
        assertEquals(feedbacks, feedbackList)
    }

    @Test
    fun `feedback submission with string length smaller than 2`() {
        val employee = LoginEmployeeResponse(1, companyIdTest, Role.EMPLOYEE)
        val feedbackCreationRequest = FeedbackCreationRequest("a", Timestamp.valueOf(LocalDateTime.now()), false, StatusType.UNREVIEWED)
        val expectedMessage = assertThrows<IllegalArgumentException> { feedbackService.feedbackSubmission(feedbackCreationRequest, employee) }
        assertEquals("Text must be at least 2 characters", expectedMessage.message)
    }

    @Test
    fun `feedback submission with string length greater than 100`() {
        val employee = LoginEmployeeResponse(1, companyIdTest, Role.EMPLOYEE)
        val feedbackCreationRequest = FeedbackCreationRequest("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", Timestamp.valueOf(LocalDateTime.now()), false, StatusType.UNREVIEWED)
        val expectedMessage = assertThrows<IllegalArgumentException> { feedbackService.feedbackSubmission(feedbackCreationRequest, employee) }
        assertEquals("Text must not exceed 100 characters", expectedMessage.message)
    }

    @Test
    fun `feedback submission successfully`() {
        val employee = LoginEmployeeResponse(1, companyIdTest, Role.EMPLOYEE)
        val feedbackCreationRequest = FeedbackCreationRequest("Service Test", Timestamp.valueOf(LocalDateTime.now()), false, StatusType.UNREVIEWED)
        feedbackService.feedbackSubmission(feedbackCreationRequest, employee)
        verify(feedbackDao).feedbackSubmission(feedbackCreationRequest, employee)
    }

    @Test
    fun `mark feedback status`() {
        val feedbackId = 1L
        val status = StatusType.REVIEWED
        val feedbackConfiguration = FeedbackConfiguration(feedbackId, 1, companyIdTest, "Service test", Timestamp.valueOf(LocalDateTime.now()), false, StatusType.UNREVIEWED)
        whenever(feedbackDao.getAllFeedbacks(companyIdTest)).thenReturn(listOf(feedbackConfiguration))
        feedbackService.markFeedbackStatus(feedbackId, status, feedbackConfiguration.companyId)
        verify(feedbackDao).markFeedbackStatus(feedbackConfiguration, status)
    }

    @Test
    fun `mark feedback status when feedback is anonymous`() {
        val feedbackConfiguration = FeedbackConfiguration(1, 1, companyIdTest, "Service test", Timestamp.valueOf(LocalDateTime.now()), true, StatusType.UNREVIEWED)
        val status = StatusType.REVIEWED
        whenever(feedbackDao.getAllFeedbacks(companyIdTest)).thenReturn(listOf(feedbackConfiguration))
        val expectedMessage = assertThrows<BadRequestException> { feedbackService.markFeedbackStatus(feedbackConfiguration.id, status, feedbackConfiguration.companyId) }
        assertEquals("Feedback marked as anonymous", expectedMessage.message)
    }

    @Test
    fun `mark feedback status when feedback is not exist`() {
        val feedbackId = 123L
        val status = StatusType.REVIEWED
        val expectedMessage = assertThrows<NotFoundException> { feedbackService.markFeedbackStatus(feedbackId, status, companyIdTest) }
        assertEquals("Feedback not found", expectedMessage.message)
    }

    @Test
    fun `view feedback status successfully`() {
        val feedbackConfiguration = FeedbackConfiguration(1, 1, companyIdTest, "Service test", Timestamp.valueOf(LocalDateTime.now()), false, StatusType.UNREVIEWED)
        whenever(feedbackDao.getAllFeedbacks(companyIdTest)).thenReturn(listOf(feedbackConfiguration))
        whenever(feedbackDao.checkStatus(feedbackConfiguration)).thenReturn(StatusResponse(StatusType.UNREVIEWED))
        feedbackService.checkFeedbackStatus(feedbackConfiguration.id, feedbackConfiguration.companyId)
        assertEquals(StatusResponse(StatusType.UNREVIEWED), feedbackDao.checkStatus(feedbackConfiguration))
    }

    @Test
    fun `view feedback status when feedback is anonymous`() {
        val feedbackConfiguration = FeedbackConfiguration(1, 1, companyIdTest, "Service test", Timestamp.valueOf(LocalDateTime.now()), true, StatusType.UNREVIEWED)
        whenever(feedbackDao.getAllFeedbacks(companyIdTest)).thenReturn(listOf(feedbackConfiguration))
        val expectedMessage = assertThrows<BadRequestException> { feedbackService.checkFeedbackStatus(feedbackConfiguration.id, feedbackConfiguration.companyId) }
        assertEquals("Feedback marked as anonymous", expectedMessage.message)
    }

    @Test
    fun `view feedback status when feedback is not exist`() {
        val feedbackId = 123L
        val expectedMessage = assertThrows<NotFoundException> { feedbackService.checkFeedbackStatus(feedbackId, companyIdTest) }
        assertEquals("Feedback not found", expectedMessage.message)
    }
}