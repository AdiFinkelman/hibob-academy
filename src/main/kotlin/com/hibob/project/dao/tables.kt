package com.hibob.project.dao

import com.hibob.academy.utils.JooqTable

class FeedbackConfigurationTable(tableName: String = "feedback") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val employeeId = createBigIntField("employee_id")
    val companyId = createBigIntField("company_id")
    val title = createVarcharField("title")
    val creationTime = createTimestampField("creation_time")
    val isAnonymous = createBooleanField("is_anonymous")
    val status = createVarcharField("status")

    companion object {
        val instance = FeedbackConfigurationTable()
    }
}

class CommentTable(tableName: String = "comment") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val employeeId = createBigIntField("employee_id")
    val text = createVarcharField("text")
    val feedbackId = createBigIntField("feedback_id")
    val creationTime = createTimestampField("creation_time")

    companion object {
        val instance = CommentTable()
    }
}

class CompanyTable(tableName: String = "company") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val name = createVarcharField("name")

    companion object {
        val instance = CompanyTable()
    }
}

class EmployeesTable(tableName: String = "employees") : JooqTable(tableName) {
    val id = createBigIntField("id")
    val firstName = createVarcharField("first_name")
    val lastName = createVarcharField("last_name")
    val role = createVarcharField("role")
    val companyId = createVarcharField("company_id")
    val department = createVarcharField("department")

    companion object {
        val instance = EmployeesTable()
    }
}