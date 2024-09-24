package com.hibob.academy.filters

import com.hibob.academy.service.SessionService
import io.jsonwebtoken.Jwts
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.Provider
import org.springframework.stereotype.Component


@Component
@Provider
class AuthenticationFilter(private val sessionService: SessionService) : ContainerRequestFilter {

    companion object {
        const val LOGIN_PATH = "api/system/login"
        const val AUTH = "Authorization"
    }

    override fun filter(requestContext: ContainerRequestContext) {

        if (requestContext.uriInfo.path == LOGIN_PATH) {
            return
        }
        val token = requestContext.cookies[AUTH]?.value?.trim()

        verify(token, requestContext)
    }

    fun verify(token: String?, requestContext: ContainerRequestContext) {
        if (token.isNullOrEmpty())
            unauthorizedUser(requestContext)

        try {
            Jwts.parserBuilder()
                .setSigningKey(sessionService.secretKey)
                .build()
        } catch (e: Exception) {
            unauthorizedUser(requestContext)
        }
    }

    private fun unauthorizedUser(requestContext: ContainerRequestContext) {
        requestContext.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                .entity("User cannot access the resource")
                .build()
        )
    }
}