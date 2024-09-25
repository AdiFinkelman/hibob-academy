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
    private val feedbackTable = FeedbackConfigurationTable.instance
    private val userTest = Employee(1, "Adi", "Finkelman", Role.EMPLOYEE, 1234L, "development")
    private val feedbackCreation = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now().withNano(0)), false, StatusType.UNREVIEWED)
    private val feedbackIdTest = feedbackDao.feedbackSubmission(feedbackCreation, userTest)


    @Test
    fun `add comments to feedback and get all comments from feedback successfully`() {
        val commentCreation1 = CommentCreationRequest(userTest.id, "Let me help you", feedbackIdTest, Timestamp.valueOf(LocalDateTime.now().withNano(0)))
        val commentCreation2 = CommentCreationRequest(userTest.id, "Hope you feel better", feedbackIdTest, Timestamp.valueOf(LocalDateTime.now().withNano(0)))
        val commentId1 = commentDao.respondToFeedback(commentCreation1, feedbackIdTest)
        val commentId2 = commentDao.respondToFeedback(commentCreation2, feedbackIdTest)
        val comment1 = extractToComment(commentCreation1, commentId1)
        val comment2 = extractToComment(commentCreation2, commentId2)
        assertEquals(listOf(comment1, comment2), commentDao.getAllComments(feedbackIdTest))
    }

    @Test
    fun `get empty comment list from feedback where comment list is empty`() {
        assertEquals(emptyList<Comment>(), commentDao.getAllComments(feedbackIdTest))
    }

    @Test
    fun `get empty comment list from feedback when feedback is not exist`() {
        assertEquals(emptyList<Comment>(), commentDao.getAllComments(1111))
    }

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
        deleteCommentTable(commentTable, feedbackIdTest)
        deleteFeedbackTable(feedbackTable, userTest.companyId)
    }

    private fun deleteCommentTable(table: CommentTable, feedbackId: Long) {
        sql.delete(table)
            .where(table.feedbackId.eq(feedbackId))
            .execute()
    }

    private fun deleteFeedbackTable(table: FeedbackConfigurationTable, companyId: Long) {
        sql.delete(table)
            .where(table.companyId.eq(companyId))
            .execute()
    }
}