package com.hibob.academy.service

import com.hibob.academy.dao.*
import jakarta.ws.rs.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OwnerService @Autowired constructor(
    private val ownerDao: OwnerDao,
    private val petDao: PetDao
) {

    fun getAllOwnersByCompanyId(companyId: Long): List<Owner> {
        return ownerDao.getAllOwnersByCompanyId(companyId)
    }

    fun createNewOwner(ownerCreationRequest: OwnerCreationRequest, companyId: Long) {
        ownerDao.createNewOwner(ownerCreationRequest)
    }

    //jooq task
    fun getOwnerByPetId(petId: Long, companyId: Long): Owner {
        val pet = getPetById(petId, companyId)
        return getOwnerFromDao(pet, companyId)
    }

    private fun getPetById(petId: Long, companyId: Long): Pet {
        return petDao.getAllPetsByCompanyId(companyId).find { it.id == petId }
            ?: throw NotFoundException("Pet with id $petId not found")
    }

    private fun getOwnerFromDao(pet: Pet, companyId: Long): Owner {
        return ownerDao.getOwnerByPetId(pet.id, companyId)
            ?: throw NotFoundException("Owner with pet ${pet.id} not found")
    }
}