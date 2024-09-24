package com.hibob.project.service

import com.hibob.project.dao.*
import jakarta.ws.rs.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FeedbackService @Autowired constructor(private val feedbackDao: FeedbackDao) {
    fun getAllFeedbacks(companyId: Long): List<FeedbackConfiguration> {
        val feedbacks = feedbackDao.getAllFeedbacks(companyId)

        if (feedbacks.isEmpty())
            throw NotFoundException("There are no feedbacks for this company")

        return feedbacks
    }

    fun feedbackSubmission(feedbackCreationRequest: FeedbackCreationRequest, employee: Employee) {
        if (feedbackCreationRequest.text.length < 2)
            throw IllegalArgumentException("Title must be at least 2 characters")

        feedbackDao.feedbackSubmission(feedbackCreationRequest, employee)
    }
}