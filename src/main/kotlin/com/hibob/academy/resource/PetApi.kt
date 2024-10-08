package com.hibob.academy.resource

import com.hibob.academy.dao.AdoptionRequest
import com.hibob.academy.dao.Pet
import com.hibob.academy.dao.PetCreationRequest
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

    private val allPets: MutableList<Pet> = CopyOnWriteArrayList()

    @GET
    @Path("/{companyId}")
    fun getAllPets(@PathParam("companyId") companyId: Long): Response {
        val pets = petService.getAllPetsByCompanyId(companyId)
        return Response.ok(pets).build()
    }

    @POST
    fun addPet(petCreationRequest: PetCreationRequest): Response {
        val petToAdd = petService.createNewPet(petCreationRequest)

        return Response.ok()
            .entity(petToAdd)
            .build()
    }

    //jooq task
    @PUT
    @Path("/adopt")
    fun adoptPet(adoptionRequest: AdoptionRequest): Response {
        petService.adoptPet(adoptionRequest)
        return Response.ok()
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

