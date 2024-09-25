package com.hibob.project.dao

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Timestamp
import java.time.LocalDateTime

@BobDbTest
class CommentDaoTest @Autowired constructor(private val sql: DSLContext) {
    private val commentDao = CommentDao(sql)
    private val feedbackDao = FeedbackDao(sql)
    private val commentTable = CommentTable.instance
    private val userTest = Employee(1, "Adi", "Finkelman", Role.EMPLOYEE, 1234L, "development")

    @Test
    fun `get all comments from feedback successfully`() {
        val feedbackCreation = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now().withNano(0)), false, StatusType.UNREVIEWED)
        val feedbackId = feedbackDao.feedbackSubmission(feedbackCreation, userTest)
        val commentCreation1 = CommentCreationRequest(userTest.id, "Let me help you", feedbackId, Timestamp.valueOf(LocalDateTime.now().withNano(0)))
        val commentCreation2 = CommentCreationRequest(userTest.id, "Hope you feel better", feedbackId, Timestamp.valueOf(LocalDateTime.now().withNano(0)))
        val commentId1 = commentDao.respondToFeedback(commentCreation1, feedbackId)
        val commentId2 = commentDao.respondToFeedback(commentCreation2, feedbackId)
        val comment1 = extractToComment(commentCreation1, commentId1)
        val comment2 = extractToComment(commentCreation2, commentId2)
        assertEquals(listOf(comment1, comment2), commentDao.getAllComments(feedbackId))
    }

    @Test
    fun `get all comments from feedback where comment list is empty`() {
        val feedbackCreation = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now().withNano(0)), false, StatusType.UNREVIEWED)
        val feedbackId = feedbackDao.feedbackSubmission(feedbackCreation, userTest)
        assertEquals(emptyList<Comment>(), commentDao.getAllComments(feedbackId))
    }

    @Test
    fun `create comment when feedback is not exists and throws exception`() {}

    private fun extractToComment(commentCreation: CommentCreationRequest, id: Long): Comment {
        val comment = Comment(
            id = id,
            employeeId = commentCreation.employeeId,
            text = commentCreation.text,
            feedbackId = commentCreation.feedbackId,
            creationTime = commentCreation.creationTime
        )

        return comment
    }

    @AfterEach
    fun cleanup() {
        sql.delete(commentTable)
            .where(commentTable.feedbackId.eq(feedbackIdTest))
            .execute()
    }
}