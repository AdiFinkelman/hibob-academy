package com.hibob.project.resource

import com.hibob.academy.filters.AuthenticationFilter
import com.hibob.project.dao.LoginEmployeeRequest
import com.hibob.project.services.AuthenticationService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody

@Controller
@Path("/api/system")
class UserApi(private val service: AuthenticationService) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    fun userAuthenticate(@RequestBody loginEmployeeRequest: LoginEmployeeRequest): Response {
        val token = service.createJwtToken(loginEmployeeRequest)
        val cookie = NewCookie.Builder(AuthenticationFilter.AUTH)
            .value(token)
            .path("/api")
            .build()

        return Response.ok().cookie(cookie)
            .build()
    }
}

data class User
    (
    val email: String, val username: String, val isAdmin: Boolean,
)


