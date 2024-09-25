package com.hibob.project.service

import com.hibob.project.dao.*
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
        val employee = Employee(1, "Adi", "Finkelman", Role.EMPLOYEE, companyIdTest, "Development")
        val feedbackCreationRequest = FeedbackCreationRequest("a", Timestamp.valueOf(LocalDateTime.now()), false, StatusType.UNREVIEWED)
        val expectedMessage = assertThrows<IllegalArgumentException> { feedbackService.feedbackSubmission(feedbackCreationRequest, employee) }
        assertEquals("Text must be at least 2 characters", expectedMessage.message)
    }

    @Test
    fun `feedback submission with string length greater than 100`() {
        val employee = Employee(1, "Adi", "Finkelman", Role.EMPLOYEE, companyIdTest, "Development")
        val feedbackCreationRequest = FeedbackCreationRequest("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", Timestamp.valueOf(LocalDateTime.now()), false, StatusType.UNREVIEWED)
        val expectedMessage = assertThrows<IllegalArgumentException> { feedbackService.feedbackSubmission(feedbackCreationRequest, employee) }
        assertEquals("Text must not exceed 100 characters", expectedMessage.message)
    }

    @Test
    fun `feedback submission successfully`() {
        val employee = Employee(1, "Adi", "Finkelman", Role.EMPLOYEE, companyIdTest, "Development")
        val feedbackCreationRequest = FeedbackCreationRequest("Service Test", Timestamp.valueOf(LocalDateTime.now()), false, StatusType.UNREVIEWED)
        feedbackService.feedbackSubmission(feedbackCreationRequest, employee)
        verify(feedbackDao).feedbackSubmission(feedbackCreationRequest, employee)
    }
}