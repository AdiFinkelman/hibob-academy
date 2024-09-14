package com.hibob.academy.service

import com.hibob.academy.dao.Pet
import com.hibob.academy.dao.PetCreationRequest
import com.hibob.academy.dao.PetDao
import com.hibob.academy.dao.PetType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class PetService @Autowired constructor(private val petDao: PetDao) {

    private val companyId = 2L

    fun getAllPetsByCompanyId(): List<Pet> {
        return petDao.getAllPetsByCompanyId(companyId)
    }

    fun getAllPetsByType(type: PetType): List<Pet> {
        return petDao.getAllPetsByType(type, companyId)
    }

    fun createNewPet(pet: Pet) {
        val petCreationRequest = PetCreationRequest(pet.name, pet.type, pet.companyId, LocalDate.now(), pet.ownerId)
        petDao.createNewPet(petCreationRequest)
    }

    fun adoptPet(pet: Pet, ownerId: Long) {
        petDao.adoptPet(pet, ownerId)
    }
}