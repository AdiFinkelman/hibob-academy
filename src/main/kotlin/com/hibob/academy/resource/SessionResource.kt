package com.hibob.academy.resource

import com.hibob.academy.dao.Example
import com.hibob.academy.service.ExampleService
import com.hibob.academy.service.SessionService
import io.jsonwebtoken.Jwts
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import java.util.*

@Controller
@Path("/adi/usersession")
class UserApi(private val service: SessionService) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/login")
    fun addUser(@RequestBody user: User): Response {
        val token = service.createJwtToken(user)
        return Response.ok(token)
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


