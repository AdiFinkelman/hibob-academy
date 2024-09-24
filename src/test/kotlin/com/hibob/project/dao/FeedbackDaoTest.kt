package com.hibob.project.dao

import com.hibob.academy.utils.BobDbTest
import jakarta.ws.rs.NotFoundException
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Timestamp
import java.time.LocalDateTime

@BobDbTest
class FeedbackDaoTest @Autowired constructor(private val sql: DSLContext) {

    private val feedbackDao = FeedbackDao(sql)
    private val feedbackTable = FeedbackConfigurationTable.instance
    private val companyIdTest = 1234L
    private val userTest = Employee(1, "Adi", "Finkelman", Role.EMPLOYEE, companyIdTest, "development")

    @Test
    fun `submit feedback successfully`() {
        val feedbackCreation = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now()), false, StatusType.UNREVIEWED)
        val feedbackId = feedbackDao.feedbackSubmission(feedbackCreation, userTest)
        val feedback = feedbackCreation.extractToFeedbackConfiguration(feedbackId, userTest.id, userTest.companyId)
        assertEquals(listOf(feedback), feedbackDao.getAllFeedbacks(companyIdTest))
    }

    @Test
    fun `submit multiple feedbacks and get all feedback successfully`() {
        val feedbackCreation1 = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now()), false, StatusType.UNREVIEWED)
        val feedbackCreation2 = FeedbackCreationRequest("Regards", Timestamp.valueOf(LocalDateTime.now()), false, StatusType.UNREVIEWED)
        val feedBackId1 = feedbackDao.feedbackSubmission(feedbackCreation1, userTest)
        val feedBackId2 = feedbackDao.feedbackSubmission(feedbackCreation2, userTest)
        val feedback1 = feedbackCreation1.extractToFeedbackConfiguration(feedBackId1, userTest.id, userTest.companyId)
        val feedback2 = feedbackCreation2.extractToFeedbackConfiguration(feedBackId2, userTest.id, userTest.companyId)
        assertEquals(listOf(feedback1, feedback2), feedbackDao.getAllFeedbacks(companyIdTest))
    }

    @Test
    fun `get all feedbacks where list is empty and throws exception`() {
        val expectedMessage = assertThrows<NotFoundException> { feedbackDao.getAllFeedbacks(companyIdTest) }
        assertEquals("List of feedbacks for companyId=$companyIdTest not found", expectedMessage.message)
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(feedbackTable)
            .where(feedbackTable.companyId.eq(companyIdTest))
            .execute()
    }
}