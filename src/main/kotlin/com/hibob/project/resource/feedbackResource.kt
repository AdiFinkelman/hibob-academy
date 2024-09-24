package com.hibob.project.resource

import com.hibob.project.dao.*
import com.hibob.project.service.AuthenticationService
import com.hibob.project.service.FeedbackService
import jakarta.ws.rs.*
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
@Path("/api/system/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class FeedbackResource(
    private val feedbackService: FeedbackService,
    private val authService: AuthenticationService
) {
    @GET
    @Path("/{companyId}")
    fun getAllFeedbacks(@PathParam("companyId") companyId: Long) = feedbackService.getAllFeedbacks(companyId)

    @POST
    fun feedbackSubmission(@Context requestContext: ContainerRequestContext, feedbackCreationRequest: FeedbackCreationRequest): Response {
        val authenticatedEmployee = authenticateEmployee(requestContext)
        feedbackService.feedbackSubmission(feedbackCreationRequest, authenticatedEmployee)

        return Response.ok(feedbackCreationRequest).build()
    }

    private fun authenticateEmployee(requestContext: ContainerRequestContext): LoginEmployeeResponse {
        val employeeId = requestContext.getProperty("employeeId") as Long
        val companyId = requestContext.getProperty("companyId") as Long
        val role = requestContext.getProperty("role") as Role

        return LoginEmployeeResponse(
            id = employeeId,
            companyId = companyId,
            role = role
        )
    }
}