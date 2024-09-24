package com.hibob.project.service

import com.hibob.project.dao.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FeedbackService @Autowired constructor(private val feedbackDao: FeedbackDao) {
    fun getAllFeedbacks(companyId: Long): List<FeedbackConfiguration> {
        val feedbacks = feedbackDao.getAllFeedbacks(companyId)
        return feedbacks
    }

    fun feedbackSubmission(feedbackCreationRequest: FeedbackCreationRequest, employee: Employee) {
        if (feedbackCreationRequest.text.length < 2)
            throw IllegalArgumentException("Text must be at least 2 characters")

        else if (feedbackCreationRequest.text.length > 100)
            throw IllegalArgumentException("Text must not exceed 100 characters")

        feedbackDao.feedbackSubmission(feedbackCreationRequest, employee)
    }
}