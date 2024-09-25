package com.hibob.project.service

import com.hibob.project.dao.*
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FeedbackService @Autowired constructor(private val feedbackDao: FeedbackDao) {
    fun getAllFeedbacks(companyId: Long): List<FeedbackConfiguration> {
        val feedbacks = feedbackDao.getAllFeedbacks(companyId)
        return feedbacks
    }

    fun feedbackSubmission(feedbackCreationRequest: FeedbackCreationRequest, employee: LoginEmployeeResponse) {
        if (validateFeedback(feedbackCreationRequest)) { feedbackDao.feedbackSubmission(feedbackCreationRequest, employee) }
    }

    fun markFeedbackStatus(feedbackId: Long, status: StatusType, companyId: Long) {
        val feedback = feedbackDao.getAllFeedbacks(companyId).find { it.id == feedbackId }
            ?: throw NotFoundException("Feedback not found")

        if (!feedback.isAnonymous) {
            feedbackDao.markFeedbackStatus(feedback, status)
        }
        else throw BadRequestException("Feedback marked as anonymous")
    }

    fun checkFeedbackStatus(feedbackId: Long, companyId: Long): StatusResponse {
        val feedback = feedbackDao.getAllFeedbacks(companyId).find { it.id == feedbackId }
            ?: throw NotFoundException("Feedback not found")

        if (!feedback.isAnonymous) {
            val statusResponse = feedbackDao.checkStatus(feedback)

            return statusResponse
        }
        throw BadRequestException("Feedback marked as anonymous")
    }

    private fun validateFeedback(feedbackCreationRequest: FeedbackCreationRequest): Boolean {
        if (feedbackCreationRequest.text.length < 2)
            throw IllegalArgumentException("Text must be at least 2 characters")
        else if (feedbackCreationRequest.text.length > 100)
            throw IllegalArgumentException("Text must not exceed 100 characters")
        return true
    }
}