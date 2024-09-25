package com.hibob.project.resource

import com.hibob.academy.filters.AuthenticationFilter
import com.hibob.project.dao.LoginEmployeeRequest
import com.hibob.project.service.AuthenticationService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody

@Controller
@Path("/api/system")
class AuthenticationResource(private val service: AuthenticationService) {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    fun userAuthenticate(@RequestBody loginEmployeeRequest: LoginEmployeeRequest): Response {
        val employee = service.getEmployee(loginEmployeeRequest)
        val token = service.createJwtToken(employee)
        val cookie = NewCookie.Builder(AuthenticationFilter.AUTH)
            .value(token)
            .path("/api")
            .build()

        return Response.ok().cookie(cookie)
            .build()
    }
}


