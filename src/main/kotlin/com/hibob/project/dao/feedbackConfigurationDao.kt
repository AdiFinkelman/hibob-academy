package com.hibob.project.dao

import jakarta.ws.rs.NotFoundException
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FeedbackDao @Autowired constructor(private val sql: DSLContext) {

    private val feedbackTable = FeedbackConfigurationTable.instance

    private val feedbackConfigurationMapper = RecordMapper<Record, FeedbackConfiguration>
    { record ->
        FeedbackConfiguration(
            id = record[feedbackTable.id],
            employeeId = record[feedbackTable.employeeId],
            companyId = record[feedbackTable.companyId],
            title = record[feedbackTable.title],
            creationTime = record[feedbackTable.creationTime],
            isAnonymous = record[feedbackTable.isAnonymous],
            status = enumValueOf(record[feedbackTable.status])
        )
    }

    fun getAllFeedbacks(companyId: Long): List<FeedbackConfiguration> {
        val feedbacks = sql.select(feedbackTable.id, feedbackTable.employeeId, feedbackTable.companyId, feedbackTable.title, feedbackTable.creationTime, feedbackTable.isAnonymous, feedbackTable.status)
            .from(feedbackTable)
            .where(feedbackTable.companyId.eq(companyId))
            .fetch(feedbackConfigurationMapper)

        if (feedbacks.isEmpty()) {
            throw NotFoundException("List of feedbacks for companyId=$companyId not found")
        }

        return feedbacks
    }

    fun feedbackSubmission(feedbackCreationRequest: FeedbackCreationRequest, employee: Employee): Long {
        return sql.insertInto(feedbackTable)
            .set(feedbackTable.employeeId, employee.id)
            .set(feedbackTable.companyId, employee.companyId)
            .set(feedbackTable.title, feedbackCreationRequest.title)
            .set(feedbackTable.creationTime, feedbackCreationRequest.creationTime)
            .set(feedbackTable.isAnonymous, feedbackCreationRequest.isAnonymous)
            .set(feedbackTable.status, feedbackCreationRequest.status.name)
            .returning(feedbackTable.id)
            .fetchOne()!![feedbackTable.id]
    }

    fun deleteFeedbackConfiguration(feedbackConfigurationId: Long) {
        sql.deleteFrom(feedbackTable)
            .where(feedbackTable.id.eq(feedbackConfigurationId))
            .execute()
    }
}