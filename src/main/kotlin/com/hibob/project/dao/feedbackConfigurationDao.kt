package com.hibob.project.dao

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FeedbackDao @Autowired constructor(private val sql: DSLContext) {
    private val feedbackTable = FeedbackConfigurationTable.instance

    private val feedbackConfigurationMapper = RecordMapper<Record, FeedbackConfiguration> { record ->
        FeedbackConfiguration(
            id = record[feedbackTable.id],
            employeeId = record[feedbackTable.employeeId],
            companyId = record[feedbackTable.companyId],
            text = record[feedbackTable.text],
            creationTime = record[feedbackTable.creationTime],
            isAnonymous = record[feedbackTable.isAnonymous],
            status = enumValueOf(record[feedbackTable.status])
        )
    }

    fun getAllFeedbacks(companyId: Long): List<FeedbackConfiguration> =
        sql.select(
            feedbackTable.id,
            feedbackTable.employeeId,
            feedbackTable.companyId,
            feedbackTable.text,
            feedbackTable.creationTime,
            feedbackTable.isAnonymous,
            feedbackTable.status
        )
            .from(feedbackTable)
            .where(feedbackTable.companyId.eq(companyId))
            .fetch(feedbackConfigurationMapper)

    fun feedbackSubmission(feedbackCreationRequest: FeedbackCreationRequest, employee: LoginEmployeeResponse): Long {
        return sql.insertInto(feedbackTable)
            .set(feedbackTable.employeeId, employee.id)
            .set(feedbackTable.companyId, employee.companyId)
            .set(feedbackTable.text, feedbackCreationRequest.text)
            .set(feedbackTable.creationTime, feedbackCreationRequest.creationTime)
            .set(feedbackTable.isAnonymous, feedbackCreationRequest.isAnonymous)
            .set(feedbackTable.status, feedbackCreationRequest.status.name)
            .returning(feedbackTable.id)
            .fetchOne()!![feedbackTable.id]
    }

    fun markFeedbackStatus(feedbackConfiguration: FeedbackConfiguration, status: StatusType) {
        sql.update(feedbackTable)
            .set(feedbackTable.status, status.name)
            .where(feedbackTable.id.eq(feedbackConfiguration.id))
            .execute()
    }

    fun checkStatus(feedbackConfiguration: FeedbackConfiguration, status: StatusType): StatusResponse {
        val currentStatus = sql.select(feedbackTable.status)
            .from(feedbackTable)
            .where(feedbackTable.id.eq(feedbackConfiguration.id))
            .fetchOne(feedbackTable.status)

        return StatusResponse(enumValueOf(currentStatus.toString()))
    }

    fun deleteCompanyFeedbacks(feedbackConfigurationId: Long) {
        sql.deleteFrom(feedbackTable)
            .where(feedbackTable.id.eq(feedbackConfigurationId))
            .execute()
    }
}