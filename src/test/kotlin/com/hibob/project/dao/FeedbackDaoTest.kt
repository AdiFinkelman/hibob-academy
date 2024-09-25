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
class FeedbackDaoTest @Autowired constructor(private val sql: DSLContext) {
    private val feedbackDao = FeedbackDao(sql)
    private val feedbackTable = FeedbackConfigurationTable.instance
    private val companyIdTest = 1234L
    private val userTest = LoginEmployeeResponse(1, companyIdTest, Role.EMPLOYEE)

    @Test
    fun `submit feedback successfully`() {
        val feedbackCreation = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now().withNano(0)), false, StatusType.UNREVIEWED)
        val feedbackId = feedbackDao.feedbackSubmission(feedbackCreation, userTest)
        val feedback = extractToFeedbackConfiguration(feedbackCreation, feedbackId, userTest.id, userTest.companyId)
        assertEquals(listOf(feedback), feedbackDao.getAllFeedbacks(companyIdTest))
    }

    @Test
    fun `submit multiple feedbacks and get all feedbacks successfully`() {
        val feedbackCreation1 = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now().withNano(0)), false, StatusType.UNREVIEWED)
        val feedbackCreation2 = FeedbackCreationRequest("Regards", Timestamp.valueOf(LocalDateTime.now().withNano(0)), false, StatusType.UNREVIEWED)
        val feedBackId1 = feedbackDao.feedbackSubmission(feedbackCreation1, userTest)
        val feedBackId2 = feedbackDao.feedbackSubmission(feedbackCreation2, userTest)
        val feedback1 = extractToFeedbackConfiguration(feedbackCreation1, feedBackId1, userTest.id, userTest.companyId)
        val feedback2 = extractToFeedbackConfiguration(feedbackCreation2, feedBackId2, userTest.id, userTest.companyId)
        assertEquals(listOf(feedback1, feedback2), feedbackDao.getAllFeedbacks(companyIdTest))
    }

    @Test
    fun `submit anonymous feedback successfully`() {
        val feedbackCreation = FeedbackCreationRequest("Anonymous", Timestamp.valueOf(LocalDateTime.now()), true, StatusType.UNREVIEWED)
        feedbackDao.feedbackSubmission(feedbackCreation, userTest)
        assertTrue(feedbackDao.getAllFeedbacks(companyIdTest)[0].isAnonymous)
    }

    @Test
    fun `get all feedbacks where list is empty`() {
        val feedbacks = emptyList<FeedbackConfiguration>()
        assertEquals(feedbacks, feedbackDao.getAllFeedbacks(companyIdTest))
    }

    @Test
    fun `mark feedback status successfully`() {
        val feedbackCreation = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now().withNano(0)), false, StatusType.UNREVIEWED)
        val feedbackId = feedbackDao.feedbackSubmission(feedbackCreation, userTest)
        val feedback = extractToFeedbackConfiguration(feedbackCreation, feedbackId, userTest.id, userTest.companyId)
        val status = StatusType.REVIEWED
        feedbackDao.markFeedbackStatus(feedback, status)
        assertEquals(StatusType.UNREVIEWED, listOf(feedback)[0].status)
        assertEquals(StatusType.REVIEWED, feedbackDao.getAllFeedbacks(companyIdTest)[0].status)
    }

    @Test
    fun `check status successfully`() {
        val feedbackCreation = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now().withNano(0)), false, StatusType.UNREVIEWED)
        feedbackDao.feedbackSubmission(feedbackCreation, userTest)
        assertEquals(StatusType.UNREVIEWED, feedbackDao.getAllFeedbacks(companyIdTest)[0].status)
    }

    private fun extractToFeedbackConfiguration(feedbackCreationRequest: FeedbackCreationRequest, id: Long, employeeId: Long, companyId: Long): FeedbackConfiguration {
        val feedbackConfiguration = FeedbackConfiguration(
            id = id,
            employeeId = employeeId,
            companyId = companyId,
            text = feedbackCreationRequest.text,
            creationTime = feedbackCreationRequest.creationTime,
            isAnonymous = feedbackCreationRequest.isAnonymous,
            status = feedbackCreationRequest.status
        )

        return feedbackConfiguration
    }

    @AfterEach
    fun cleanup() {
        sql.delete(feedbackTable)
            .where(feedbackTable.companyId.eq(companyIdTest))
            .execute()
    }
}