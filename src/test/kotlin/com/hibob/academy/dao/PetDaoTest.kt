package com.hibob.academy.dao

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import java.sql.Date
import java.time.LocalDate
import kotlin.random.Random

@BobDbTest
class PetDaoTest @Autowired constructor(private val sql: DSLContext) {

    private val petDao = PetDao(sql)
    val table = PetTable.instance
    private val companyId = 1L
    val pet = Pet(1, "Tom", PetType.CAT.toString(), companyId, Date.valueOf(LocalDate.now()), 1L )

    @Test
    fun `create pet table and get all pets`() {
        petDao.createNewPet(pet)
        assertEquals("Tom" ,petDao.getAllPets().get(0).name)
        assertEquals(PetType.CAT.toString(), petDao.getAllPets().get(0).type)
        assertEquals(companyId, petDao.getAllPets().get(0).companyId)
    }

    @Test
    fun `get pets by type`() {
        petDao.createNewPet(pet)
        assertEquals("Tom", petDao.getAllPetsByType(PetType.CAT).get(0).name)
        assertEquals(PetType.CAT.toString(), petDao.getAllPetsByType(PetType.CAT).get(0).type)
        assertEquals(companyId, petDao.getAllPetsByType(PetType.CAT).get(0).companyId)
    }

    @Test
    fun `get pets by owner`() {
        petDao.createNewPet(pet)
        assertEquals("Tom", petDao.getPetsByOwner(1L).get(0).name)
        assertEquals(PetType.CAT.toString(), petDao.getAllPets().get(0).type)
        assertEquals(companyId, petDao.getAllPets().get(0).companyId)
    }

    @Test
    fun `count pet by type`() {
        petDao.createNewPet(pet)
        val pet1 = Pet(2, "Garfield", PetType.CAT.toString(), companyId, Date.valueOf(LocalDate.now()), 1L)
        petDao.createNewPet(pet1)
        assertEquals(mapOf("CAT" to 2), petDao.countPetsByType())
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table)
            .where(table.companyId.eq(companyId))
            .execute()
    }
}