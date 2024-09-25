package com.hibob.project.resource

import com.hibob.project.dao.*
import com.hibob.project.service.FeedbackService
import com.hibob.project.utils.AuthenticationUtil
import com.hibob.project.utils.RoleValidationUtil
import jakarta.ws.rs.*
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller

@Controller
@Path("/api/system/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class FeedbackResource(private val feedbackService: FeedbackService) {

    @GET
    fun getAllFeedbacks(@Context requestContext: ContainerRequestContext): Response {
        val authenticatedEmployee = AuthenticationUtil.extractAuthenticatedEmployee(requestContext)
        val allowedRoles = setOf(Role.HR, Role.ADMIN)

        if (RoleValidationUtil.isRoleValid(authenticatedEmployee.role, allowedRoles)) {
            val feedbacks = feedbackService.getAllFeedbacks(authenticatedEmployee.companyId)

            return Response.ok(feedbacks).build()
        }

        return Response.status(Response.Status.UNAUTHORIZED)
            .entity("Employee doesnt have permission")
            .build()
    }

    @POST
    fun feedbackSubmission(@Context requestContext: ContainerRequestContext, feedbackCreationRequest: FeedbackCreationRequest): Response {
        val authenticatedEmployee = AuthenticationUtil.extractAuthenticatedEmployee(requestContext)
        feedbackService.feedbackSubmission(feedbackCreationRequest, authenticatedEmployee)

        return Response.ok(feedbackCreationRequest).build()
    }

    @PUT
    @Path("/status/{feedbackId}")
    fun markFeedbackStatue(@Context requestContext: ContainerRequestContext, @PathParam("feedbackId") feedbackId: Long, statusResponse: StatusResponse): Response {
        val authenticatedEmployee = AuthenticationUtil.extractAuthenticatedEmployee(requestContext)
        feedbackService.markFeedbackStatus(feedbackId, statusResponse.status, authenticatedEmployee.companyId)

        return Response.ok().build()
    }

    @GET
    @Path("/status/{feedbackId}")
    fun checkFeedbackStatus(@Context requestContext: ContainerRequestContext, @PathParam("feedbackId") feedbackId: Long,): Response {
        val authenticatedEmployee = AuthenticationUtil.extractAuthenticatedEmployee(requestContext)
        val statusResponse = feedbackService.checkFeedbackStatus(feedbackId, authenticatedEmployee.companyId)

        return Response.ok(statusResponse.status).build()
    }
}