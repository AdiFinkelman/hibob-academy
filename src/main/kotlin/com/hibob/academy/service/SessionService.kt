package com.hibob.academy.service

import com.hibob.academy.resource.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

const val ONE_DAY_MILLIS = 60 * 60 * 24

@Component
class SessionService {

    companion object val secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    val now = Date.from(Instant.now())

    fun createJwtToken(user: User): String {

        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .claim("email", user.email)
            .claim("username", user.username)
            .claim("isAdmin", user.isAdmin)
            .setIssuedAt(now)
            .setExpiration(Date(System.currentTimeMillis() + ONE_DAY_MILLIS))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }
}