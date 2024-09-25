package com.hibob.project.service

import com.hibob.project.dao.*
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.NotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.sql.Timestamp
import java.time.LocalDateTime

class CommentServiceTest {
    private val commentDao = mock<CommentDao>()
    private val feedbackDao = mock<FeedbackDao>()
    private val commentService = CommentService(commentDao, feedbackDao)

    @Test
    fun `get all comments where comment list is empty`() {
        val feedbackId = 123L
        val comments = emptyList<Comment>()
        assertEquals(comments, commentService.getAllComments(feedbackId))
    }

    @Test
    fun `get all comments successfully`() {
        val feedbackId = 123L
        val commentsList = listOf(Comment(1, 1, "Service test", feedbackId, Timestamp.valueOf(LocalDateTime.now())))
        whenever(commentService.getAllComments(feedbackId)).thenReturn(commentsList)
        assertEquals(commentsList, commentService.getAllComments(feedbackId))
    }

    @Test
    fun `respond to feedback where feedback is not exist and throws exception`() {
        val feedbackId = 123L
        val companyId = 123L
        val commentCreationRequest = CommentCreationRequest(1, "text", feedbackId, Timestamp.valueOf(LocalDateTime.now()))
        whenever(feedbackDao.getAllFeedbacks(companyId)).thenReturn(listOf(FeedbackConfiguration(1, 1, 1, "Service test", Timestamp.valueOf(LocalDateTime.now()), false, StatusType.UNREVIEWED)))
        val expectedMessage = assertThrows<NotFoundException> { commentService.respondToFeedback(commentCreationRequest, feedbackId, companyId) }
        assertEquals("Feedback is not exist", expectedMessage.message)
    }

    @Test
    fun `respond to feedback where feedback is anonymous and throws exception`() {
        val feedbackId = 1L
        val companyId = 123L
        val commentCreationRequest = CommentCreationRequest(1, "text", feedbackId, Timestamp.valueOf(LocalDateTime.now()))
        whenever(feedbackDao.getAllFeedbacks(companyId)).thenReturn(listOf(FeedbackConfiguration(1, 1, companyId, "Service test", Timestamp.valueOf(LocalDateTime.now()), true, StatusType.UNREVIEWED)))
        val expectedMessage = assertThrows<BadRequestException> { commentService.respondToFeedback(commentCreationRequest, feedbackId, companyId) }
        assertEquals("Feedback is anonymous", expectedMessage.message)
    }

    @Test
    fun `respond to feedback successfully`() {
        val feedbackId = 1L
        val companyId = 123L
        val commentCreationRequest = CommentCreationRequest(1, "text", feedbackId, Timestamp.valueOf(LocalDateTime.now()))
        whenever(feedbackDao.getAllFeedbacks(companyId)).thenReturn(listOf(FeedbackConfiguration(1, 1, companyId, "Service test", Timestamp.valueOf(LocalDateTime.now()), false, StatusType.UNREVIEWED)))
        commentService.respondToFeedback(commentCreationRequest, feedbackId, companyId)
        verify(commentDao).respondToFeedback(commentCreationRequest, feedbackId)
    }
}