package com.hibob.project.dao

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CommentDao @Autowired constructor(private val sql: DSLContext) {
    private val commentTable = CommentTable.instance

    private val commentMapper = RecordMapper<Record, Comment> { record ->
        Comment(
            id = record[commentTable.id],
            employeeId = record[commentTable.employeeId],
            text = record[commentTable.text],
            feedbackId = record[commentTable.feedbackId],
            creationTime = record[commentTable.creationTime]
        )
    }

    fun getAllComments(feedbackId: Long): List<Comment> =
        sql.select(
            commentTable.id,
            commentTable.employeeId,
            commentTable.text,
            commentTable.feedbackId,
            commentTable.creationTime
        )
            .from(commentTable)
            .where(commentTable.feedbackId.eq(feedbackId))
            .fetch(commentMapper)

    fun respondToFeedback(commentCreation: CommentCreationRequest, feedbackId: Long): Long {
        return sql.insertInto(commentTable)
            .set(commentTable.employeeId, commentCreation.employeeId)
            .set(commentTable.text, commentCreation.text)
            .set(commentTable.feedbackId, feedbackId)
            .set(commentTable.creationTime, commentCreation.creationTime)
            .returning(commentTable.id)
            .fetchOne()!![commentTable.id]
    }

    fun deleteComment(commentId: Long) {
        sql.deleteFrom(commentTable)
            .where(commentTable.id.eq(commentId))
            .execute()
    }
}