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
    val table = OwnerTable.instance
    private val companyId = 1L
    val owner1 = Owner(1L,"Adi Finkelman", "Adi", "Finkelman", companyId, "2")
    val owner2 = Owner(2L,"Dolev Finkelman", "Dolev", "Finkelman", companyId, "3")

    @Test
    fun `create owner test`() {
        val expectedResult = listOf(owner1, owner2)
        ownerDao.createNewOwner(owner1)
        ownerDao.createNewOwner(owner2)
        assertEquals(expectedResult, ownerDao.getAllOwnersByCompanyId(companyId))
    }

    @Test
    fun `create owner null`() {
        val expectedResult = emptyList<Owner>()
        assertEquals(expectedResult, ownerDao.getAllOwnersByCompanyId(companyId))
    }

    @Test
    fun `create owner without first name and last name`() {
        val owner3 = Owner(3, "Ori Finkelman", null, null, companyId, "1")
        ownerDao.createNewOwner(owner3)
        assertEquals("Ori", ownerDao.getAllOwnersByCompanyId(companyId)[0].firstName)
        assertEquals("Finkelman", ownerDao.getAllOwnersByCompanyId(companyId)[0].lastName)
    }

    @Test
    fun `create duplicate owner`() {
        ownerDao.createNewOwner(owner1)
        ownerDao.createNewOwner(owner1)
        val expectedResult = listOf(owner1)
        assertEquals(expectedResult, ownerDao.getAllOwnersByCompanyId(companyId))
    }

    @Test
    fun `create multiple owner with same companyId and different employeeId`() {
        ownerDao.createNewOwner(owner1)
        val owner = Owner(3L, "Ori Finkelman", "Ori", "Finkelman", companyId, "1")
        ownerDao.createNewOwner(owner)
        val expectedResult = listOf(owner1, owner)
        assertEquals(expectedResult, ownerDao.getAllOwnersByCompanyId(companyId))
    }

    @Test
    fun `create multiple owner with same employeeId and same companyId`() {
        ownerDao.createNewOwner(owner1)
        val owner = Owner(3L, "Ori Finkelman", "Ori", "Finkelman", companyId, "2")
        ownerDao.createNewOwner(owner)
        val expectedResult = listOf(owner1)
        assertEquals(expectedResult, ownerDao.getAllOwnersByCompanyId(companyId))
    }

    @Test
    fun `get owner by pet id`() {
        val pet = Pet(3L, "Rockey", PetType.DOG, 1L, LocalDate.now(), owner1.id)

        val petTable = PetTable.instance
        ownerDao.createNewOwner(owner1)
        val petDao = PetDao(sql)
        petDao.createNewPet(pet)

        val fetchedOwner = ownerDao.getOwnerByPetId(pet.id)

        assertNotNull(fetchedOwner)
        assertEquals(owner1.name, fetchedOwner?.name)
        assertEquals(owner1.employeeId, fetchedOwner?.employeeId)
        assertEquals(owner1.companyId, fetchedOwner?.companyId)

        //clean pet table
        sql.deleteFrom(petTable)
            .where(petTable.companyId.eq(companyId))
            .execute()
    }

    @Test
    fun `get null owner by pet id when owner id null`() {
        val pet = Pet(3L, "Rockey", PetType.DOG, 1L, LocalDate.now(), null)

        val petDao = PetDao(sql)
        petDao.createNewPet(pet)

        assertNull(ownerDao.getOwnerByPetId(pet.id))

        //clean pet table
        val petTable = PetTable.instance
        sql.deleteFrom(petTable)
            .execute()
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table)
            .execute()
    }
}

