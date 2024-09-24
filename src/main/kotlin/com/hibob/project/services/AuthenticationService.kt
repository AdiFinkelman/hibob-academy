package com.hibob.project.services

import com.hibob.project.dao.LoginEmployeeRequest
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

const val ONE_DAY_MILLIS = 60 * 60 * 24

@Component
class AuthenticationService {

    companion object val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    val now = Date.from(Instant.now())

    fun createJwtToken(user: LoginEmployeeRequest): String {

        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .claim("employeeId", user.id)
            .claim("companyId", user.companyId)
            .setIssuedAt(now)
            .setExpiration(Date(System.currentTimeMillis() + ONE_DAY_MILLIS))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }
}