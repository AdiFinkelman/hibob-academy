package com.hibob.academy.filters

import com.hibob.academy.service.SessionService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.Provider
import org.apache.http.auth.AUTH
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

const val LOGIN_PATH = "adi/usersession/login"
const val AUTH = "Authorization"

@Component
@Provider
class AuthenticationFilter(private val sessionService: SessionService) : ContainerRequestFilter {

    override fun filter(requestContext: ContainerRequestContext) {

        if (requestContext.uriInfo.path == LOGIN_PATH) {
            return
        }
        val authCookie = requestContext.cookies[AUTH]?.value?.trim()
        val token = authCookie?.substringAfter("Bearer ")

        if (verify(token) == null) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User cannot access the resource")
                    .build()
            )
        }
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