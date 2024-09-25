package com.hibob.project.utils

import com.hibob.project.dao.LoginEmployeeResponse
import com.hibob.project.dao.Role
import jakarta.ws.rs.container.ContainerRequestContext

object AuthenticationUtil {
    fun extractAuthenticatedEmployee(requestContext: ContainerRequestContext): LoginEmployeeResponse {
        val employeeId = requestContext.getProperty("employeeId") as Long
        val companyId = requestContext.getProperty("companyId") as Long
        val role = requestContext.getProperty("role") as Role

        return LoginEmployeeResponse(
            id = employeeId,
            companyId = companyId,
            role = role
        )
    }
}