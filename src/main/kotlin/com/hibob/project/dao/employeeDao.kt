package com.hibob.project.dao

import jakarta.ws.rs.NotFoundException
import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.jooq.Record
import org.springframework.stereotype.Component

@Component
class EmployeeDao(private val sql: DSLContext) {
    private val employeeTable = EmployeesTable.instance
    private val employeeMapper = RecordMapper<Record, Employee>
    { record ->
        Employee(
            id = record[employeeTable.id],
            firstName = record[employeeTable.firstName],
            lastName = record[employeeTable.lastName],
            role = enumValueOf(record[employeeTable.role]),
            companyId = record[employeeTable.companyId],
            department = record[employeeTable.department]
        )
    }

    fun getEmployee(loginEmployeeRequest: LoginEmployeeRequest): Employee {
        return sql.select(
            employeeTable.id,
            employeeTable.firstName,
            employeeTable.lastName,
            employeeTable.role,
            employeeTable.companyId,
            employeeTable.department
        )
            .from(employeeTable)
            .where(employeeTable.id.eq(loginEmployeeRequest.id))
            .and(employeeTable.companyId.eq(loginEmployeeRequest.companyId))
            .fetchOne(employeeMapper) ?: throw NotFoundException("Employee not found")
    }
}