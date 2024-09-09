package com.hibob.academy.filters

import com.hibob.academy.service.SessionService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import org.apache.tomcat.util.http.parser.Cookie
import org.springframework.stereotype.Component

@Component
class AuthenticationFilter(private val sessionService: SessionService) : ContainerRequestFilter {
    override fun filter(requestContext: ContainerRequestContext) {

        if (requestContext.uriInfo.path == "/adi/usersession") return

    }

    fun verify(token: String?): Jws<Claims>? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(sessionService.secretKey)
                .build()
                .parseClaimsJws(token)
        } catch (e: Exception) {
            null
        }
    }
}