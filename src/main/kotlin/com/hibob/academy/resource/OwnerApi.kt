package com.hibob.academy.resource

import com.hibob.academy.dao.Owner
import com.hibob.academy.dao.OwnerCreationRequest
import com.hibob.academy.service.OwnerService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller
import java.util.concurrent.CopyOnWriteArrayList

@Controller
@Path("/api/adi/owners")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class OwnerResource(private val ownerService: OwnerService) {

    private val allOwners: MutableList<Owner> = CopyOnWriteArrayList()

    @GET
    @Path("/{ownerId}")
    fun getOwnerById(@PathParam("ownerId") ownerId: Long): Response {
        val owner = allOwners.find { it.id == ownerId }
        return owner?.let {
            Response.ok(owner).build()
        } ?: throw NotFoundException("Owner not found")
    }

    @GET
    @Path("/company/{companyId}")
    fun getAllOwners(@PathParam("companyId") companyId: Long): Response {
        val owners = ownerService.getAllOwnersByCompanyId(companyId)
        return Response.ok(owners).build()
    }

    //jooq task
    @GET
    @Path("/pet/{petId}/company/{companyId}")
    fun getOwnerByPetId(@PathParam("petId") petId: Long, @PathParam("companyId") companyId: Long): Response {
        val owner = ownerService.getOwnerByPetId(petId, companyId)
        return Response.ok(owner).build()
    }

    @POST
    @Path("/company/{companyId}")
    fun addOwner(@PathParam("companyId") companyId: Long, ownerCreationRequest: OwnerCreationRequest): Response {
        val (firstName, lastName) = extractFirstAndLastName(ownerCreationRequest)
        val newOwner =
            ownerCreationRequest.copy(name = "$firstName $lastName", firstName = firstName, lastName = lastName)
        ownerService.createNewOwner(newOwner, companyId)

        return Response.ok()
            .entity(allOwners)
            .build()
    }

    @PUT
    @Path("/{ownerId}")
    fun updateOwner(@PathParam("ownerId") ownerId: Long, updatedOwner: Owner): Response {
        val index = allOwners.indexOfFirst { it.id == ownerId }
        return if (index != -1) {
            val (firstName, lastName) = updateFirstAndLastName(updatedOwner)

            val ownerToUpdate = updatedOwner.copy(id = ownerId, name = "$firstName $lastName", lastName = lastName)
            allOwners[index] = ownerToUpdate
            Response.ok()
                .entity(ownerToUpdate)
                .build()
        } else {
            throw NotFoundException("Owner not found")
        }
    }

    @DELETE
    @Path("/{ownerId}")
    fun deleteOwner(@PathParam("ownerId") ownerId: Long): Response {
        val removedOwner = allOwners.removeIf { it.id == ownerId }
        return removedOwner.let {
            Response.status(Response.Status.NO_CONTENT).build()
        }
            ?: throw NotFoundException("Owner not found")
    }

    private fun extractFirstAndLastName(ownerCreationRequest: OwnerCreationRequest): Pair<String?, String?> {
        val firstName = ownerCreationRequest.name.split(" ").first()
        val lastName = ownerCreationRequest.name.split(" ").drop(1).joinToString(" ").takeIf { it.isNotBlank() }
            ?: ownerCreationRequest.lastName ?: throw BadRequestException("Last name is required")

        return Pair(firstName, lastName)
    }

    private fun updateFirstAndLastName(updatedOwner: Owner): Pair<String?, String?> {
        val firstName = updatedOwner.name.split(" ").firstOrNull() ?: updatedOwner.firstName
        ?: throw BadRequestException("First name not found")
        val lastName =
            updatedOwner.name.split(" ").drop(1).joinToString(" ").takeIf { it.isNotBlank() } ?: updatedOwner.lastName
            ?: throw BadRequestException("Last name not found")

        return Pair(firstName, lastName)
    }

}