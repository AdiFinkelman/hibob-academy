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
class AuthenticationFilter : ContainerRequestFilter {
    override fun filter(requestContext: ContainerRequestContext) {

        if (requestContext.uriInfo.path == "TO BE IMPLEMENT") return

    }

//    fun verify(cookie: String?): Jws<Claims>? {
//        val sessionService = SessionService()
//        cookie?.let {
//        try {
//            Jwts.parser().setSigningKey(sessionService.secretKey).parseClaimsJws(cookie)
//        } catch (e: Exception) {
//            null
//        }
//    }
}