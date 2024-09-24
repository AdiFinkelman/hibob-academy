package com.hibob.project.dao

import org.jooq.DSLContext
import org.jooq.RecordMapper
import org.jooq.Record
import org.springframework.stereotype.Component

@Component
class EmployeeDao (private val sql: DSLContext) {

    private val employeeTable = EmployeesTable.instance

    private val employeeMapper = RecordMapper<Record, LoginEmployeeRequest>
    { record ->
        LoginEmployeeRequest(
            id = record[employeeTable.id],
            companyId = record[employeeTable.companyId],
            role = enumValueOf<Role>(record[employeeTable.role])
        )
    }

    fun getEmployee(id: Long, companyId: Long): LoginEmployeeRequest? {
        return sql.selectFrom(employeeTable)
            .where(employeeTable.id.eq(id))
            .and(employeeTable.companyId.eq(companyId))
            .fetchOne(employeeMapper)
    }
}