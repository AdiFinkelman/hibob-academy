package com.hibob.academy.service

import com.hibob.academy.dao.Owner
import com.hibob.academy.dao.OwnerCreationRequest
import com.hibob.academy.dao.OwnerDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OwnerService @Autowired constructor(private val ownerDao: OwnerDao) {

    private val companyId = 2L

    fun getAllOwnersByCompanyId(): List<Owner> {
        return ownerDao.getAllOwnersByCompanyId(companyId)
    }

    fun createNewOwner(owner: Owner) {
        val ownerCreationRequest =
            OwnerCreationRequest(owner.name, owner.firstName, owner.lastName, owner.companyId, owner.employeeId)
        ownerDao.createNewOwner(ownerCreationRequest)
    }

    fun getOwnerByPetId(petId: Long): Owner? {
        return ownerDao.getOwnerByPetId(petId, companyId)
    }
}