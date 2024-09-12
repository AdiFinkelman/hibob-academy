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
    val pet1 = Pet(1, "Tom", PetType.CAT, companyId, LocalDate.now(), 1L )
    val pet2 = Pet(2, "Luke", PetType.DOG, companyId, LocalDate.now(), 2L )

    @Test
    fun `create pet and get all pets`() {
        val expectedResult = listOf(pet1, pet2)
        petDao.createNewPet(pet1)
        petDao.createNewPet(pet2)
        assertEquals(expectedResult , petDao.getAllPetsByCompanyId(companyId))
    }

    @Test
    fun `create pet null`() {
        val expectedResult = emptyList<Pet>()
        assertEquals(expectedResult, petDao.getAllPetsByCompanyId(companyId))
    }

    @Test
    fun `create duplicate pet`() {
        petDao.createNewPet(pet1)
        petDao.createNewPet(pet1)
        val expectedResult = listOf(pet1)
        assertEquals(expectedResult, petDao.getAllPetsByCompanyId(companyId))
    }

    @Test
    fun `create multiple pet with different companyId`() {
        petDao.createNewPet(pet1)
        val pet = Pet(1, "Tom", PetType.CAT, 2L, LocalDate.now(), 1L )
        petDao.createNewPet(pet)
        val expectedResult = listOf(pet1)
        assertEquals(expectedResult, petDao.getAllPetsByType(PetType.CAT))
    }

    @Test
    fun `get all pets by type`() {
        val pet3 = Pet(3, "Garfield", PetType.CAT, 2L, LocalDate.now(), 1L )
        petDao.createNewPet(pet1)
        petDao.createNewPet(pet3)
        val expectedResult = listOf(pet1, pet3)
        assertEquals(expectedResult, petDao.getAllPetsByType(PetType.CAT))
    }

    @Test
    fun `adopt pet`() {
        val pet = Pet(1, "Tom", PetType.CAT, companyId, LocalDate.now(), null )
        petDao.createNewPet(pet)
        petDao.adoptPet(pet, 2L)
        assertEquals(2L, petDao.getAllPetsByCompanyId(companyId)[0].ownerId)
    }

    @Test
    fun `adopt pet which already have owner`() {
        val pet = Pet(1, "Tom", PetType.CAT, companyId, LocalDate.now(), 1L )
        petDao.createNewPet(pet)
        petDao.adoptPet(pet, 2L)
        assertEquals(2L, petDao.getAllPetsByCompanyId(companyId)[0].ownerId)
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table)
            .execute()
    }
}