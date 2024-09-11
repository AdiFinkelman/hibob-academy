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
import java.time.LocalDateTime
import kotlin.random.Random

@BobDbTest
class PetDaoTest @Autowired constructor(private val sql: DSLContext) {

    private val petDao = PetDao(sql)
    val table = PetTable.instance
    private val companyId = 1L
    val owner = Owner(1, "Adi", null, null, 1, "1")
    val pet = Pet(1, "Tom", PetType.CAT.toString(), companyId, LocalDate.now(), owner.id.toLong() )

    @Test
    fun `create pet and get all pets`() {
        petDao.createNewPet(pet)
        assertEquals("Tom" ,petDao.getAllPetsByCompanyId(companyId).get(0).name)
        assertEquals(companyId, petDao.getAllPetsByCompanyId(companyId).get(0).companyId)
        assertEquals(PetType.CAT.toString(), petDao.getAllPetsByCompanyId(companyId).get(0).type)
    }

    @Test
    fun `get all pets without type by type`() {
        petDao.createNewPet(pet)
        assertEquals("Tom" ,petDao.getAllPetsByType(PetType.CAT).get(0).name)
        assertEquals(companyId, petDao.getAllPetsByType(PetType.CAT).get(0).companyId)
        assertEquals(PetType.CAT.toString(), petDao.getAllPetsByType(PetType.CAT).get(0).type)
    }

    @Test
    fun `update pet`() {
        petDao.createNewPet(pet)
        val newPet = Pet(2, "Garfield", PetType.CAT.toString(), companyId, LocalDate.now(), 1L )
        petDao.updatePet(newPet, 1L)
        assertEquals("Garfield", petDao.getAllPetsByCompanyId(companyId).get(0).name)
        assertEquals(PetType.CAT.toString(), petDao.getAllPetsByCompanyId(companyId).get(0).type)
    }

//    @Test
//    fun `get owner by pet id`() {
//        petDao.createNewPet(pet)
//        assertEquals("Adi", petDao.getOwnerByPetId(pet)?.name ?: "NO NAME")
//    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table)
            .where(table.companyId.eq(companyId))
            .execute()
    }
}