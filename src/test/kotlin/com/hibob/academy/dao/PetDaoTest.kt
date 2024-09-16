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
class PetDaoTest @Autowired constructor(private val sql: DSLContext) {

    private val petDao = PetDao(sql)
    val table = PetTable.instance
    private val companyId = 1L
    val pet1 = PetCreationRequest("Tom", PetType.CAT, companyId, LocalDate.now(), 1L )
    val pet2 = PetCreationRequest("Luke", PetType.DOG, companyId, LocalDate.now(), 2L )

    @Test
    fun `create pet and get all pets`() {
        val expectedResult = listOf(pet1, pet2).map { it.name }
        val id1 = petDao.createNewPet(pet1)
        val id2 = petDao.createNewPet(pet2)
        assertEquals(expectedResult , petDao.getAllPetsByCompanyId(companyId).map { it.name })
    }

    @Test
    fun `create pet null`() {
        val expectedResult = emptyList<Pet>()
        assertEquals(expectedResult, petDao.getAllPetsByCompanyId(companyId))
    }

    @Test
    fun `create multiple pet with different companyId`() {
        petDao.createNewPet(pet1)
        val petTest = PetCreationRequest("Tom", PetType.CAT, 2L, LocalDate.now(), 1L )
        petDao.createNewPet(petTest)
        val expectedResult = listOf(pet1).map { it.name }
        assertEquals(expectedResult, petDao.getAllPetsByType(PetType.CAT, companyId).map { it.name })
    }

    @Test
    fun `get all pets by type`() {
        val petTest = PetCreationRequest("Garfield", PetType.CAT, 2L, LocalDate.now(), 1L )
        petDao.createNewPet(pet1)
        petDao.createNewPet(petTest)
        val expectedResult = listOf(pet1).map { it.name }
        assertEquals(expectedResult, petDao.getAllPetsByType(PetType.CAT, companyId).map { it.name })
    }

    @Test
    fun `adopt pet`() {
        val ownerId = 2L
        val petCreationRequest = PetCreationRequest("Tom", PetType.CAT, companyId, LocalDate.now(), null )
        val pet = Pet(1L, petCreationRequest.name, petCreationRequest.type, petCreationRequest.companyId, petCreationRequest.arrivalDate, petCreationRequest.ownerId)
        petDao.createNewPet(petCreationRequest)
        petDao.adoptPet(pet, ownerId)
        assertEquals(pet.ownerId, petDao.getAllPetsByCompanyId(companyId)[0].ownerId)
    }

    @Test
    fun `adopt pet which already have owner`() {
        val newOwnerId = 2L
        val petCreationRequest = PetCreationRequest("Tom", PetType.CAT, companyId, LocalDate.now(), newOwnerId)
        val pet = Pet(1L, petCreationRequest.name, petCreationRequest.type, petCreationRequest.companyId, petCreationRequest.arrivalDate, petCreationRequest.ownerId)
        petDao.createNewPet(petCreationRequest)
        petDao.adoptPet(pet, newOwnerId)
        assertEquals(newOwnerId, petDao.getAllPetsByCompanyId(companyId)[0].ownerId)
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table)
            .where(table.companyId.eq(companyId))
            .execute()
    }
}