package com.hibob.project.dao

import java.sql.Timestamp

data class FeedbackConfiguration(
    val id: Long,
    val employeeId: Long,
    val companyId: Long,
    val text: String,
    val creationTime: Timestamp,
    val isAnonymous: Boolean,
    val status: StatusType
)

data class FeedbackCreationRequest(
    val text: String,
    val creationTime: Timestamp,
    val isAnonymous: Boolean,
    val status: StatusType
)

data class Comment(
    val id: Long,
    val employeeId: Long,
    val text: String,
    val feedbackId: Long,
    val creationTime: Timestamp,
)

data class Feedback(
    val feedbackConfiguration: FeedbackConfiguration,
    val commentList: List<Comment>
)

enum class StatusType {
    REVIEWED, UNREVIEWED
}

enum class Role {
    ADMIN, HR, EMPLOYEE
}

data class Company(
    val id: Long,
    val name: String
)

data class Employee(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val role: Role,
    val companyId: Long,
    val department: String
)

data class LoginEmployeeRequest(
    val id: Long,
    val companyId: Long
)

data class LoginEmployeeResponse(
    val id: Long,
    val companyId: Long,
    val role: Role
)