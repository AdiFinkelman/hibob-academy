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
    fun `create pet and get all pets`() {
        petDao.createNewPet(pet)
        assertEquals(1 ,petDao.getAllPets().size)
    }

    @Test
    fun `get all pets without type by type`() {
        petDao.createNewPet(pet)
        assertEquals(1, petDao.getAllPetsByType(PetType.CAT).size)
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table)
            .where(table.companyId.eq(companyId))
            .execute()
    }
}