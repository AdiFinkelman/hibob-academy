package com.hibob.academy.resource

import com.hibob.academy.dao.*
import com.hibob.academy.service.PetService
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
class PetsResource(private val petService: PetService) {

    private val allPets: MutableList<Pet> = CopyOnWriteArrayList()

    @GET
    @Path("/{petId}")
    fun getPetById(@PathParam("petId") petId: Long): Response {
        val pets = petService.getAllPetsByCompanyId()
        val pet = pets.find { it.id == petId }
        return pet?.let {
            Response.ok().entity(it).build()
        } ?: throw NotFoundException("Pet not found")
    }

    @GET
    fun getAllPets(): Response {
        val pets = petService.getAllPetsByCompanyId()
        return Response.ok(pets).build() ?: throw NotFoundException("Pets not found")
    }

    @GET
    @Path("/type/{type}")
    fun getPetsByType(@PathParam("type") type: String): Response {
        val pets = petService.getAllPetsByType(enumValueOf<PetType>(type))
        return Response.ok(pets).build() ?: throw NotFoundException("Pets not found")
    }

    @POST
    fun addPet(pet: Pet): Response {
        val newPetId = (allPets.maxOfOrNull { it.id } ?: 0) + 1
        val newPet = pet.copy(id = newPetId, arrivalDate = LocalDate.now())
        allPets.add(newPet)
        petService.createNewPet(newPet)

        return Response.ok().entity(newPet).build()
    }

    //Dont need it
//    @PUT
//    @Path("/{petId}")
//    fun updatePet(@PathParam("petId") petId: Long, updatedPet: Pet): Response {
//        val index = allPets.indexOfFirst { it.id == petId }
//        return if (index != -1) {
//            val petToUpdate = updatedPet.copy(id = petId)
//            allPets[index] = petToUpdate
//            petService.updatePet(petId, petToUpdate)
//            Response.ok()
//                .entity(petToUpdate)
//                .build()
//        } else {
//            throw NotFoundException("Pet not found")
//        }
//    }

    @PUT
    @Path("/{petId}/adopt")
    fun adoptPet(@PathParam("petId") petId: Long, request: AdoptionRequest): Response {
        val pet = petService.getAllPetsByCompanyId().find { it.id == petId } ?: throw NotFoundException("Pet not found")
        petService.adoptPet(pet, request.ownerId)
            return Response.ok()
                .entity(pet)
                .build()
                ?: throw NotFoundException("Pet not found")
    }

    //not working with db
    @DELETE
    @Path("/{petId}")
    fun deletePet(@PathParam("petId") petId: Long): Response {
        val removedPet = allPets.removeIf { it.id == petId }
        return removedPet.let {
            Response.ok().build()
        } ?: throw NotFoundException("Pet not found")
    }
}