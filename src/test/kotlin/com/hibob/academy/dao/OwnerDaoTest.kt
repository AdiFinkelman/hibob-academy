package com.hibob.academy.dao

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

@BobDbTest
class OwnerDaoTest @Autowired constructor(private val sql: DSLContext) {

    private val ownerDao = OwnerDao(sql)
    val ownerTable = OwnerTable.instance
    val petTable = PetTable.instance
    val petDao = PetDao(sql)
    private val companyId = 1L
    val owner1 = OwnerCreationRequest("Adi Finkelman", "Adi", "Finkelman", companyId, "2")
    val owner2 = OwnerCreationRequest("Dolev Finkelman", "Dolev", "Finkelman", companyId, "3")

    @Test
    fun `create owner test`() {
        val expectedResult = listOf(owner1, owner2).map { it.name }
        ownerDao.createNewOwner(owner1)
        ownerDao.createNewOwner(owner2)
        assertEquals(expectedResult, ownerDao.getAllOwnersByCompanyId(companyId).map { it.name })
    }

    @Test
    fun `create owner null`() {
        val expectedResult = emptyList<Owner>()
        assertEquals(expectedResult, ownerDao.getAllOwnersByCompanyId(companyId))
    }

    @Test
    fun `create owner without first name and last name`() {
        val owner3 = OwnerCreationRequest("Ori Finkelman", null, null, companyId, "1")
        ownerDao.createNewOwner(owner3)
        assertEquals("Ori", ownerDao.getAllOwnersByCompanyId(companyId)[0].firstName)
        assertEquals("Finkelman", ownerDao.getAllOwnersByCompanyId(companyId)[0].lastName)
    }

    @Test
    fun `create duplicate owner`() {
        ownerDao.createNewOwner(owner1)
        ownerDao.createNewOwner(owner1)
        val expectedResult = listOf(owner1).map { it.name }
        assertEquals(expectedResult, ownerDao.getAllOwnersByCompanyId(companyId).map { it.name })
    }

    @Test
    fun `create multiple owner with same companyId and different employeeId`() {
        ownerDao.createNewOwner(owner1)
        val owner = OwnerCreationRequest("Ori Finkelman", "Ori", "Finkelman", companyId, "1")
        ownerDao.createNewOwner(owner)
        val expectedResult = listOf(owner1, owner).map { it.name }
        assertEquals(expectedResult, ownerDao.getAllOwnersByCompanyId(companyId).map { it.name })
    }

    @Test
    fun `create multiple owner with same employeeId and same companyId`() {
        ownerDao.createNewOwner(owner1)
        val owner = OwnerCreationRequest("Ori Finkelman", "Ori", "Finkelman", companyId, "2")
        ownerDao.createNewOwner(owner)
        val expectedResult = listOf(owner1).map { it.name }
        assertEquals(expectedResult, ownerDao.getAllOwnersByCompanyId(companyId).map { it.name })
    }

    @Test
    fun `get owner by pet id`() {
        ownerDao.createNewOwner(owner1)
        val ownerId = ownerDao.getAllOwnersByCompanyId(companyId).get(0).id
        val petCreationRequest = PetCreationRequest("Rockey", PetType.DOG, 1L, LocalDate.now(), ownerId)
        petDao.createNewPet(petCreationRequest)
        val petId = petDao.getAllPetsByCompanyId(companyId).get(0).id

        val fetchedOwner = ownerDao.getOwnerByPetId(petId, companyId)
        val expectedOwner = Owner(ownerId, owner1.name, owner1.firstName, owner1.lastName, owner1.companyId, owner1.employeeId)

        assertEquals(expectedOwner, fetchedOwner)
    }

    @Test
    fun `get null owner by pet id when owner id null`() {
        val petCreationRequest = PetCreationRequest("Rockey", PetType.DOG, companyId, LocalDate.now(), null)
        petDao.createNewPet(petCreationRequest)
        val petId = petDao.getAllPetsByCompanyId(companyId)[0].id

        assertNull(ownerDao.getOwnerByPetId(petId, companyId))
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(ownerTable)
            .where(ownerTable.companyId.eq(companyId))
            .execute()

        sql.deleteFrom(petTable)
            .where(petTable.companyId.eq(companyId))
            .execute()
    }
}

