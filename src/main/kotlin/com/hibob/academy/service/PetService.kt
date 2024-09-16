package com.hibob.academy.service

import com.hibob.academy.dao.*
import jakarta.ws.rs.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PetService @Autowired constructor(private val petDao: PetDao) {

    fun getAllPetsByCompanyId(companyId: Long): List<Pet> {
        return petDao.getAllPetsByCompanyId(companyId)
    }

    fun createNewPet(petCreationRequest: PetCreationRequest) {
        petDao.createNewPet(petCreationRequest)
    }

    //jooq task
    fun adoptPet(adoptionRequest: AdoptionRequest) {
        val pet = getAllPetsByCompanyId(adoptionRequest.companyId).find { it.id == adoptionRequest.adoptedPetId } ?: throw NotFoundException("Pet not found")
        petDao.adoptPet(pet, adoptionRequest.newOwnerId)
    }

    fun getPetsByOwner(ownerId: Long, companyId: Long): List<Pet> {
        return petDao.getPetsByOwner(ownerId, companyId)
    }

    fun countPetsByType(): Map<PetType, Int> {
        return petDao.countPetsByType()
    }
}