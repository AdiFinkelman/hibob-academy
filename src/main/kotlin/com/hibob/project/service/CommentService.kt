package com.hibob.project.service

import com.hibob.project.dao.*
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CommentService @Autowired constructor(private val commentDao: CommentDao, private val feedbackDao: FeedbackDao) {
    fun getAllComments(feedbackId: Long): List<Comment> {
        val comments = commentDao.getAllComments(feedbackId)
        return comments
    }

    fun respondToFeedback(commentCreation: CommentCreationRequest, feedbackId: Long, companyId: Long) {
        if (validateComment(feedbackId, companyId)) { commentDao.respondToFeedback(commentCreation, feedbackId) }
    }

    fun validateComment(feedbackId: Long, companyId: Long): Boolean {
        val feedbackList = feedbackDao.getAllFeedbacks(companyId)
        val isIdExist = feedbackList.any { it.id == feedbackId }

        if (!isIdExist)
            throw NotFoundException("Feedback is not exist")

        val feedback = feedbackList.first { it.id == feedbackId }
        val isAnonymous = feedback.isAnonymous

        if (isAnonymous)
            throw BadRequestException("Feedback is anonymous")

        return true
    }
}