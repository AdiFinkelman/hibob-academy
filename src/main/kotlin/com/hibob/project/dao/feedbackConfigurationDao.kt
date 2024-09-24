package com.hibob.project.dao

import com.hibob.bootcamp.Employee
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FeedbackConfigurationDao @Autowired constructor(private val sql: DSLContext) {

    private val feedbackTable = FeedbackConfigurationTable.instance

    private val feedbackConfigurationMapper = RecordMapper<Record, FeedbackConfiguration>
    { record ->
        FeedbackConfiguration(
            id = record[feedbackTable.id],
            employeeId = record[feedbackTable.employeeId],
            companyId = record[feedbackTable.companyId],
            creationTime = record[feedbackTable.creationTime],
            isAnonymous = record[feedbackTable.isAnonymous],
            status = enumValueOf(record[feedbackTable.status])
        )
    }

    fun getFeedbackConfiguration(employee: Employee): FeedbackConfiguration {
        sql.select(feedbackTable.id, feedbackTable.employeeId, feedbackTable.companyId, feedbackTable.creationTime, feedbackTable.isAnonymous, feedbackTable.status)
        .from(feedbackTable)
            .where(feedbackTable.name.eq(employee.name))
    }
}