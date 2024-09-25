package com.hibob.project.resource

import com.hibob.project.dao.*
import com.hibob.project.service.CommentService
import com.hibob.project.utils.AuthenticationUtil
import com.hibob.project.utils.RoleValidationUtil
import jakarta.ws.rs.*
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller

@Controller
@Path("/api/system/comment")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CommentResource(private val commentService: CommentService) {

    @GET
    @Path("/{feedbackId}")
    fun getAllComments(@PathParam("feedbackId") feedbackId: Long): Response {
        val comments = commentService.getAllComments(feedbackId)

        return Response.ok(comments).build()
    }

    @POST
    fun respondToFeedback(@Context requestContext: ContainerRequestContext, commentCreationRequest: CommentCreationRequest): Response {
        val authenticatedEmployee = AuthenticationUtil.extractAuthenticatedEmployee(requestContext)
        val allowedRoles = setOf(Role.HR)

        if (RoleValidationUtil.isRoleValid(authenticatedEmployee.role, allowedRoles)) {
            commentService.respondToFeedback(commentCreationRequest, commentCreationRequest.feedbackId, authenticatedEmployee.companyId)

            return Response.ok(commentCreationRequest).build()
        }

        return Response.status(Response.Status.UNAUTHORIZED)
            .entity("Employee doesnt have permission to respond")
            .build()
    }
}