package com.hibob.project.service

import com.hibob.project.dao.Employee
import com.hibob.project.dao.EmployeeDao
import com.hibob.project.dao.LoginEmployeeRequest
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

const val ONE_DAY_MILLIS = 60 * 60 * 24

@Component
class AuthenticationService(private val employeeDao: EmployeeDao) {
    companion object

    val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    val now = Date.from(Instant.now())

    fun createJwtToken(employee: Employee?): String {

        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .claim("employeeId", employee?.id)
            .claim("companyId", employee?.companyId)
            .claim("role", employee?.role)
            .setIssuedAt(now)
            .setExpiration(Date(System.currentTimeMillis() + ONE_DAY_MILLIS))
            .signWith(secretKey)
            .compact()
    }

    fun getEmployee(loginEmployeeRequest: LoginEmployeeRequest): Employee? {
        return employeeDao.getEmployee(loginEmployeeRequest)
    }
}