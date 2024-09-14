package com.hibob.academy.resource

import com.hibob.academy.dao.AdoptionRequest
import com.hibob.academy.dao.Pet
import com.hibob.academy.service.PetService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Controller
import java.util.concurrent.CopyOnWriteArrayList

@Controller
@Path("/api/adi/pets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class PetsResource(private val petService: PetService) {

    private val companyId = 2L
    private val allPets: MutableList<Pet> = CopyOnWriteArrayList()

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
        val pets = petService.getAllPetsByCompanyId(companyId)
        return Response.ok(pets).build()
    }

    @POST
    fun addPet(pet: Pet): Response {
        val petToAdd = petService.createNewPet(pet)

        return Response.ok()
            .entity(petToAdd)
            .build()
    }

    //jooq task
    @PUT
    @Path("/adopt")
    fun adoptPet(adoptionRequest: AdoptionRequest): Response {
        val petRequest = petService.adoptPet(adoptionRequest)
        return Response.ok()
            .entity(petRequest)
            .build()
            ?: throw NotFoundException("Pet not found")
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
        }
        else {
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

