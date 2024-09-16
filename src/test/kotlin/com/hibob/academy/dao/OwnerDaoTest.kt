package com.hibob.academy.dao

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

@BobDbTest
class OwnerDaoTest @Autowired constructor(private val sql: DSLContext) {

    private val ownerDao = OwnerDao(sql)
    val ownerTable = OwnerTable.instance
    val petTable = PetTable.instance
    val petDao = PetDao(sql)
    private val companyId = 1L
    val ownerCreationRequest1 = OwnerCreationRequest("Adi Finkelman", "Adi", "Finkelman", companyId, "2")
    val ownerCreationRequest2 = OwnerCreationRequest("Dolev Finkelman", "Dolev", "Finkelman", companyId, "3")

    @Test
    fun `create owner test`() {
        val id1 = ownerDao.createNewOwner(ownerCreationRequest1)
        val id2 = ownerDao.createNewOwner(ownerCreationRequest2)
        val owner1 = ownerCreationRequest1.extractOwnerCreationToOwner(ownerCreationRequest1, id1)
        val owner2 = ownerCreationRequest2.extractOwnerCreationToOwner(ownerCreationRequest2, id2)
        val expectedResult = listOf(owner1, owner2)
        assertEquals(expectedResult, ownerDao.getAllOwnersByCompanyId(companyId))
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
    fun `create duplicate owner and throws exception`() {
        ownerDao.createNewOwner(ownerCreationRequest1)
        assertThrows<NullPointerException> { ownerDao.createNewOwner(ownerCreationRequest1) }
    }

    @Test
    fun `create multiple owner with same companyId and different employeeId`() {
        val id1 = ownerDao.createNewOwner(ownerCreationRequest1)
        val owner1 = ownerCreationRequest1.extractOwnerCreationToOwner(ownerCreationRequest1, id1)
        val ownerCreationRequest3 = OwnerCreationRequest("Ori Finkelman", "Ori", "Finkelman", companyId, "1")
        val id3 = ownerDao.createNewOwner(ownerCreationRequest3)
        val owner3 = ownerCreationRequest3.extractOwnerCreationToOwner(ownerCreationRequest3, id3)
        val expectedResult = listOf(owner1, owner3).sortedBy { it.id }
        assertEquals(expectedResult, ownerDao.getAllOwnersByCompanyId(companyId).sortedBy { it.id })
    }

    @Test
    fun `create multiple owner with same employeeId and same companyId and throws exception`() {
        ownerDao.createNewOwner(ownerCreationRequest1)
        val ownerCreationRequest3 = OwnerCreationRequest("Ori Finkelman", "Ori", "Finkelman", companyId, "2")
        assertThrows<NullPointerException>{ownerDao.createNewOwner(ownerCreationRequest3)}
    }

    @Test
    fun `get owner by pet id`() {
        val ownerId = ownerDao.createNewOwner(ownerCreationRequest1)
        val petCreationRequest = PetCreationRequest("Rockey", PetType.DOG, 1L, LocalDate.now(), ownerId)
        val petId = petDao.createNewPet(petCreationRequest)

        val fetchedOwner = ownerDao.getOwnerByPetId(petId, companyId)
        val expectedOwner = ownerCreationRequest1.extractOwnerCreationToOwner(ownerCreationRequest1, ownerId)
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
        deleteOwnerTable(ownerTable, companyId)
        deletePetTable(petTable, companyId)
    }

    private fun deletePetTable(table: PetTable, companyId: Long) {
        sql.deleteFrom(table)
            .where(table.companyId.eq(companyId))
            .execute()
    }

    private fun deleteOwnerTable(table: OwnerTable, companyId: Long) {
        sql.deleteFrom(table)
            .where(table.companyId.eq(companyId))
            .execute()
    }
}

