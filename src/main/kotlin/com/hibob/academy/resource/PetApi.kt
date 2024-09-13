package com.hibob.academy.resource

import com.hibob.academy.dao.AdoptionRequest
import com.hibob.academy.dao.Owner
import com.hibob.academy.dao.Pet
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller
import java.time.LocalDate
import java.util.concurrent.CopyOnWriteArrayList

@Controller
@Path("/api/adi/pets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PetsResource(private val ownerResource: OwnerResource) {

    private val allPets: MutableList<Pet> = CopyOnWriteArrayList()
    private val allOwners: MutableList<Owner> = CopyOnWriteArrayList()

    @GET
    @Path("/{petId}")
    fun getPetById(@PathParam("petId") petId: Long): Response {
        val pet = allPets.find { it.id == petId }
        return pet?.let {
            Response.ok()
                .entity(pet)
                .build()
        }
            ?: throw NotFoundException("Pet not found")
    }

    @GET
    fun getAllPets(): Response {
        return Response.ok(allPets).build()
    }

    @GET
    @Path("/{petId}/owner")
    fun getOwnerByPetId(@PathParam("petId") petId: Long): Response {
        val pet = allPets.find { it.id == petId } ?: throw NotFoundException("Pet not found")
        val ownerResponse = ownerResource.getOwnerById(pet.ownerId)
        val owner = ownerResponse.entity as? Owner ?: throw NotFoundException("Owner not found")

        return Response.ok()
            .entity(owner)
            .build()
    }

    @POST
    fun addPet(pet: Pet): Response {
        val newPetId = (allPets.maxOfOrNull { it.id } ?: 0) + 1
        val newPet = pet.copy(id = newPetId, arrivalDate = LocalDate.now())
        allPets.add(newPet)

        return Response.ok()
            .entity(newPet)
            .build()
    }

    @PUT
    @Path("/{petId}")
    fun updatePet(@PathParam("petId") petId: Long, updatedPet: Pet): Response {
        val index = allPets.indexOfFirst { it.id == petId }
        return if (index != -1) {
            val petToUpdate = updatedPet.copy(id = petId)
            allPets[index] = petToUpdate
            Response.ok()
                .entity(petToUpdate)
                .build()
        } else {
            throw NotFoundException("Pet not found")
        }
    }

    @PUT
    @Path("/{petId}/adopt")
    @Consumes(MediaType.APPLICATION_JSON)
    fun adoptPet(@PathParam("petId") petId: Long, request: AdoptionRequest): Response {
        val index = allPets.indexOfFirst { it.id == petId }
        return if (index != -1) {
            val petToUpdate = allPets[index].copy(ownerId = request.ownerId)
            allPets[index] = petToUpdate
            Response.ok()
                .entity(petToUpdate)
                .build()
        } else {
            throw NotFoundException("Pet not found")
        }
    }

    @DELETE
    @Path("/{petId}")
    fun deletePet(@PathParam("petId") petId: Long): Response {
        val removedPet = allPets.removeIf { it.id == petId }
        return removedPet.let {
            Response.ok().build()
        }
            ?: throw NotFoundException("Pet not found")
    }
}