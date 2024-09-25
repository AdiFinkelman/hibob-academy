package com.hibob.project.dao

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime

@BobDbTest
class FeedbackDaoTest @Autowired constructor(private val sql: DSLContext) {
    private val feedbackDao = FeedbackDao(sql)
    private val feedbackTable = FeedbackConfigurationTable.instance
    private val companyIdTest = 1234L
    private val userTest = LoginEmployeeResponse(1, companyIdTest, Role.EMPLOYEE)

    @Test
    fun `submit feedback successfully`() {
        val feedbackCreation = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), false, StatusType.UNREVIEWED)
        val feedbackId = feedbackDao.feedbackSubmission(feedbackCreation, userTest)
        val feedback = extractToFeedbackConfiguration(feedbackCreation, feedbackId, userTest.id, userTest.companyId)
        assertEquals(listOf(feedback), feedbackDao.getAllFeedbacks(companyIdTest))
    }

    @Test
    fun `submit multiple feedbacks and get all feedbacks successfully`() {
        val feedbackCreation1 = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), false, StatusType.UNREVIEWED)
        val feedbackCreation2 = FeedbackCreationRequest("Regards", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), false, StatusType.UNREVIEWED)
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
    fun `get all feedbacks by date filter`() {
        val feedbackCreation1 = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), false, StatusType.UNREVIEWED)
        val feedbackCreation2 = FeedbackCreationRequest("Regards", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), true, StatusType.UNREVIEWED)
        val feedbackCreation3 = FeedbackCreationRequest("Thanks", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), true, StatusType.UNREVIEWED)
        val feedBackId1 = feedbackDao.feedbackSubmission(feedbackCreation1, userTest)
        val feedBackId2 = feedbackDao.feedbackSubmission(feedbackCreation2, userTest)
        val feedBackId3 = feedbackDao.feedbackSubmission(feedbackCreation3, userTest)
        val feedback1 = extractToFeedbackConfiguration(feedbackCreation1, feedBackId1, userTest.id, userTest.companyId)
        val feedback2 = extractToFeedbackConfiguration(feedbackCreation2, feedBackId2, userTest.id, userTest.companyId)
        val feedback3 = extractToFeedbackConfiguration(feedbackCreation3, feedBackId3, userTest.id, userTest.companyId)
        val filterOption = FilterOption(LocalDate.now(), null, null)
        assertEquals(listOf(feedback1, feedback2, feedback3).sortedBy { it.id }, feedbackDao.getFeedbacksByFilter(companyIdTest, filterOption).sortedBy { it.id })
    }

    @Test
    fun `get all feedbacks by anonymity status filter`() {
        val feedbackCreation1 = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), false, StatusType.UNREVIEWED)
        val feedbackCreation2 = FeedbackCreationRequest("Regards", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), true, StatusType.UNREVIEWED)
        val feedbackCreation3 = FeedbackCreationRequest("Thanks", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), true, StatusType.UNREVIEWED)
        val feedBackId1 = feedbackDao.feedbackSubmission(feedbackCreation1, userTest)
        val feedBackId2 = feedbackDao.feedbackSubmission(feedbackCreation2, userTest)
        val feedBackId3 = feedbackDao.feedbackSubmission(feedbackCreation3, userTest)
        val feedback1 = extractToFeedbackConfiguration(feedbackCreation1, feedBackId1, userTest.id, userTest.companyId)
        val feedback2 = extractToFeedbackConfiguration(feedbackCreation2, feedBackId2, userTest.id, userTest.companyId)
        val feedback3 = extractToFeedbackConfiguration(feedbackCreation3, feedBackId3, userTest.id, userTest.companyId)
        val filterOption1 = FilterOption(null, null, true)
        val filterOption2 = FilterOption(null, null, false)
        assertEquals(listOf(feedback2, feedback3).sortedBy { it.id }, feedbackDao.getFeedbacksByFilter(companyIdTest, filterOption1).sortedBy { it.id })
        assertEquals(listOf(feedback1), feedbackDao.getFeedbacksByFilter(companyIdTest, filterOption2))
    }

    @Test
    fun `get all feedbacks by department filter`() {
        val department1 = "Sales"
        val department2 = "Paleontology"
        // Create employees
        val employee1 = Employee(1, "Adi", "Finkelman", userTest.role, userTest.companyId, department1)
        val employee2 = Employee(2, "Dolev", "Finkelman", userTest.role, userTest.companyId, department2)
        val loginEmployee1 = LoginEmployeeResponse(employee1.id, employee1.companyId, employee1.role)
        val loginEmployee2 = LoginEmployeeResponse(employee2.id, employee2.companyId, employee2.role)
        // Create feedbacks
        val feedbackCreation1 = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), false, StatusType.UNREVIEWED)
        val feedbackCreation2 = FeedbackCreationRequest("Regards", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), true, StatusType.UNREVIEWED)
        val feedbackCreation3 = FeedbackCreationRequest("Thanks", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), true, StatusType.UNREVIEWED)
        val feedBackId1 = feedbackDao.feedbackSubmission(feedbackCreation1, loginEmployee1)
        val feedBackId2 = feedbackDao.feedbackSubmission(feedbackCreation2, loginEmployee2)
        val feedBackId3 = feedbackDao.feedbackSubmission(feedbackCreation3, loginEmployee1)
        val feedback1 = extractToFeedbackConfiguration(feedbackCreation1, feedBackId1, employee1.id, employee1.companyId)
        val feedback2 = extractToFeedbackConfiguration(feedbackCreation2, feedBackId2, employee2.id, employee2.companyId)
        val feedback3 = extractToFeedbackConfiguration(feedbackCreation3, feedBackId3, employee1.id, employee1.companyId)
        // Create filters
        val filterOption1 = FilterOption(null, department1, null)
        val filterOption2 = FilterOption(null, department2, null)
        assertEquals(listOf(feedback1, feedback3), feedbackDao.getFeedbacksByFilter(companyIdTest, filterOption1))
        assertEquals(listOf(feedback2), feedbackDao.getFeedbacksByFilter(companyIdTest, filterOption2))
    }

    @Test
    fun `get all feedbacks by multiple filters`() {
        val department1 = "Sales"
        val department2 = "Paleontology"
        // Create employees
        val employee1 = Employee(1, "Adi", "Finkelman", userTest.role, userTest.companyId, department1)
        val employee2 = Employee(2, "Dolev", "Finkelman", userTest.role, userTest.companyId, department2)
        val loginEmployee1 = LoginEmployeeResponse(employee1.id, employee1.companyId, employee1.role)
        val loginEmployee2 = LoginEmployeeResponse(employee2.id, employee2.companyId, employee2.role)
        // Create feedbacks
        val feedbackCreation1 = FeedbackCreationRequest("Complaint", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), false, StatusType.UNREVIEWED)
        val feedbackCreation2 = FeedbackCreationRequest("Regards", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), true, StatusType.UNREVIEWED)
        val feedbackCreation3 = FeedbackCreationRequest("Thanks", Timestamp.valueOf(LocalDateTime.now().withSecond(0).withNano(0)), true, StatusType.UNREVIEWED)
        val feedBackId1 = feedbackDao.feedbackSubmission(feedbackCreation1, loginEmployee1)
        val feedBackId2 = feedbackDao.feedbackSubmission(feedbackCreation2, loginEmployee2)
        val feedBackId3 = feedbackDao.feedbackSubmission(feedbackCreation3, loginEmployee1)
        val feedback1 = extractToFeedbackConfiguration(feedbackCreation1, feedBackId1, employee1.id, employee1.companyId)
        val feedback2 = extractToFeedbackConfiguration(feedbackCreation2, feedBackId2, employee2.id, employee2.companyId)
        val feedback3 = extractToFeedbackConfiguration(feedbackCreation3, feedBackId3, employee1.id, employee1.companyId)
        // Create filters
        val filterOption1 = FilterOption(LocalDate.now(), department1, true)
        val filterOption2 = FilterOption(LocalDate.now(), department2, true)
        val filterOption3 = FilterOption(LocalDate.now(), department1, false)
        val filterOption4 = FilterOption(LocalDate.now(), null, true)

        assertEquals(listOf(feedback3), feedbackDao.getFeedbacksByFilter(companyIdTest, filterOption1))
        assertEquals(listOf(feedback2), feedbackDao.getFeedbacksByFilter(companyIdTest, filterOption2))
        assertEquals(listOf(feedback1), feedbackDao.getFeedbacksByFilter(companyIdTest, filterOption3))
        assertEquals(listOf(feedback2, feedback3).sortedBy { it.id }, feedbackDao.getFeedbacksByFilter(companyIdTest, filterOption4).sortedBy { it.id })
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