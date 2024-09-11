package com.hibob.academy.resource

import com.hibob.academy.filters.AuthenticationFilter
import com.hibob.academy.service.SessionService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody

@Controller
@Path("/adi/usersession")
class UserApi(private val service: SessionService) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    fun addUser(@RequestBody user: User): Response {
        val token = service.createJwtToken(user)
        val cookie = NewCookie.Builder(AuthenticationFilter.AUTH)
            .value(token)
            .build()

        return Response.ok().cookie(cookie)
            .build()
    }

    @GET
    @Path("/test")
    fun doSomething(): String {
        return "{}"
    }
}

data class User
    (
    val email: String, val username: String, val isAdmin: Boolean,
)


