package com.hibob.academy.service

import com.hibob.academy.dao.*
import jakarta.ws.rs.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class PetService @Autowired constructor(private val petDao: PetDao) {

    fun getAllPetsByCompanyId(companyId: Long): List<Pet> {
        return petDao.getAllPetsByCompanyId(companyId)
    }

    fun createNewPet(pet: Pet) {
        val petCreationRequest = PetCreationRequest(pet.name, pet.type, pet.companyId, LocalDate.now(), pet.ownerId)
        petDao.createNewPet(petCreationRequest)
    }

    //jooq task
    fun adoptPet(adoptionRequest: AdoptionRequest) {
        val pet = getAllPetsByCompanyId(adoptionRequest.companyId).find { it.id == adoptionRequest.adoptedPetId } ?: throw NotFoundException("Pet not found")
        return petDao.adoptPet(pet, adoptionRequest.newOwnerId)
    }
}