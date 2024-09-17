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
    val petTable = PetTable.instance
    private val companyId = 1L
    private val companyIdTest = 3L
    val petCreationRequest1 = PetCreationRequest("Tom", PetType.CAT, companyId, LocalDate.now(), null )
    val petCreationRequest2 = PetCreationRequest("Luke", PetType.DOG, companyId, LocalDate.now(), null )

    @Test
    fun `create pet and get all pets`() {
        val id1 = petDao.createNewPet(petCreationRequest1)
        val id2 = petDao.createNewPet(petCreationRequest2)
        val pet1 = petCreationRequest1.extractToPet(id1)
        val pet2 = petCreationRequest2.extractToPet(id2)
        val expectedResult = listOf(pet1, pet2)
        assertEquals(expectedResult , petDao.getAllPetsByCompanyId(companyId))
    }

    @Test
    fun `create pet null`() {
        val expectedResult = emptyList<Pet>()
        assertEquals(expectedResult, petDao.getAllPetsByCompanyId(companyId))
    }

    @Test
    fun `create duplicate pets successfully`() {
        val id1 = petDao.createNewPet(petCreationRequest1)
        val id2 = petDao.createNewPet(petCreationRequest1)
        val pet1 = petCreationRequest1.extractToPet(id1)
        val pet2 = petCreationRequest1.extractToPet(id2)
        val expectedResult = listOf(pet1, pet2)
        assertEquals(expectedResult, petDao.getAllPetsByCompanyId(companyId))
    }

    @Test
    fun `create multiple pet with different companyId`() {
        val id1 = petDao.createNewPet(petCreationRequest1)
        val petCreation3 = PetCreationRequest("Tom", PetType.CAT, companyIdTest, LocalDate.now(), 1L )
        petDao.createNewPet(petCreation3)
        val pet1 = petCreationRequest1.extractToPet(id1)
        val expectedResult = listOf(pet1)
        assertEquals(expectedResult, petDao.getAllPetsByCompanyId(companyId))
    }

    @Test
    fun `get all pets by type`() {
        val petCreationRequest3 = PetCreationRequest("Garfield", PetType.CAT, companyId, LocalDate.now(), 1L )
        val id1 = petDao.createNewPet(petCreationRequest1)
        val pet1 = petCreationRequest1.extractToPet(id1)
        petDao.createNewPet(petCreationRequest2)
        val id3 = petDao.createNewPet(petCreationRequest3)
        val pet3 = petCreationRequest3.extractToPet(id3)
        val expectedResult = listOf(pet1, pet3)
        assertEquals(expectedResult, petDao.getAllPetsByType(PetType.CAT, companyId))
    }

    @Test
    fun `adopt pet`() {
        val ownerId = 2L
        val petCreationRequest = PetCreationRequest("Tom", PetType.CAT, companyId, LocalDate.now(), null )
        val petId = petDao.createNewPet(petCreationRequest)
        val pet = petCreationRequest.extractToPet(petId)
        petDao.adoptPet(pet, ownerId)
        assertEquals(ownerId, petDao.getAllPetsByCompanyId(companyId)[0].ownerId)
    }

    @Test
    fun `adopt pet which already have owner`() {
        val newOwnerId = 2L
        val petCreationRequest = PetCreationRequest("Tom", PetType.CAT, companyId, LocalDate.now(), newOwnerId)
        val petId = petDao.createNewPet(petCreationRequest)
        val pet = petCreationRequest.extractToPet(petId)
        petDao.adoptPet(pet, newOwnerId)
        assertEquals(newOwnerId, petDao.getAllPetsByCompanyId(companyId)[0].ownerId)
    }

    //sql 2 tests
    @Test
    fun `get pets by owner`() {
        val ownerId = 2L
        val id1 = petDao.createNewPet(petCreationRequest1)
        val id2 = petDao.createNewPet(petCreationRequest2)
        val pet1 = petCreationRequest1.extractToPet(id1)
        val pet2 = petCreationRequest2.extractToPet(id2)
        petDao.adoptPet(pet1, ownerId)
        petDao.adoptPet(pet2, ownerId)
        val updatedPet1 = pet1.copy(ownerId = ownerId)
        val updatedPet2 = pet2.copy(ownerId = ownerId)
        val expectedResult = listOf(updatedPet1, updatedPet2)
        assertEquals(expectedResult, petDao.getPetsByOwner(ownerId, companyId))
    }

    @Test
    fun `get pets by owner id when owners id are different`() {
        val ownerId = 2L
        petDao.createNewPet(petCreationRequest1) //insert to db -> instead of id1
        val id2 = petDao.createNewPet(petCreationRequest2)
        val pet2 = petCreationRequest2.extractToPet(id2) //insert to db
        petDao.adoptPet(pet2, ownerId)
        val updatedPet2 = pet2.copy(ownerId = ownerId)
        val expectedResult = listOf(updatedPet2)
        assertEquals(expectedResult, petDao.getPetsByOwner(ownerId, companyId))
    }

    @Test
    fun `count pets by type`() {
        petDao.createNewPet(petCreationRequest1)
        petDao.createNewPet(petCreationRequest2)
        val petCreationRequest3 = PetCreationRequest("Nico", PetType.DOG, companyId, LocalDate.now(), 1L )
        petDao.createNewPet(petCreationRequest3)
        val expectedResult = mapOf(PetType.CAT to 1, PetType.DOG to 2)
        assertEquals(expectedResult, petDao.countPetsByType(companyId))
    }

    @Test
    fun `count pets by type for empty list`() {
        val expectedResult = emptyMap<String, Int>()
        assertEquals(expectedResult, petDao.countPetsByType(companyId))
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        deletePetTable(petTable, companyId)
        deletePetTable(petTable, companyIdTest)
    }

    private fun deletePetTable(table: PetTable, companyId: Long) {
        sql.deleteFrom(table)
            .where(table.companyId.eq(companyId))
            .execute()
    }
}