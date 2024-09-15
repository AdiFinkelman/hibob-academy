package com.hibob.academy.service

import com.hibob.academy.dao.*
import jakarta.ws.rs.NotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

class OwnerServiceTest {
    private val ownerDao = mock<OwnerDao>()
    private val petDao = mock<PetDao>()
    private val ownerService = OwnerService(ownerDao, petDao)

    @Test
    fun `get all owners by company id `() {
        val companyId = 2L
        val owner1 = Owner(1, "Adi", null, null, companyId, "1")
        val owner2 = Owner(2, "Dolev", null, null, companyId, "2")
        val owners = listOf(owner1, owner2)
        whenever(ownerDao.getAllOwnersByCompanyId(companyId)).thenReturn(owners)
        assertEquals(owners, ownerService.getAllOwnersByCompanyId(companyId))
    }

    @Test
    fun `create new owner`() {
        val companyId = 2L
        val ownerCreationRequest = OwnerCreationRequest("Adi", null, null, companyId, "1")
        ownerService.createNewOwner(ownerCreationRequest, companyId)
        verify(ownerDao).createNewOwner(any())
    }

    @Test
    fun `get owner by pet id not found and throws exception`() {
        val companyId = 2L
        val pet = Pet(1, "Tom", PetType.CAT, companyId, LocalDate.now(), 1)
        whenever(petDao.getAllPetsByCompanyId(companyId)).thenReturn(emptyList())
        val expectedMessage = assertThrows<NotFoundException> { ownerService.getOwnerByPetId(pet.id, companyId) }
        assertEquals("Pet with id ${pet.id} not found", expectedMessage.message)
        verify(petDao).getAllPetsByCompanyId(companyId)
    }

    @Test
    fun `get owner by pet id with owner not found and throws exception`() {
        val companyId = 2L
        val pet = Pet(1, "Tom", PetType.CAT, companyId, LocalDate.now(), 1)
        whenever(petDao.getAllPetsByCompanyId(companyId)).thenReturn(listOf(pet))
        whenever(ownerDao.getOwnerByPetId(pet.id, companyId)).thenReturn(null)
        val expectedMessage = assertThrows<NotFoundException> { ownerService.getOwnerByPetId(pet.id, companyId) }
        assertEquals("Owner with pet ${pet.id} not found", expectedMessage.message)
        verify(petDao).getAllPetsByCompanyId(companyId)
        verify(ownerDao).getOwnerByPetId(pet.id, companyId)
    }

    @Test
    fun `get owner successfully by pet id`() {
        val companyId = 2L
        val pet = Pet(1, "Tom", PetType.CAT, companyId, LocalDate.now(), 1)
        val owner = Owner(1, "Adi", null, null, companyId, "1")
        whenever(petDao.getAllPetsByCompanyId(companyId)).thenReturn(listOf(pet))
        whenever(ownerDao.getOwnerByPetId(pet.id, companyId)).thenReturn(owner)
        assertEquals(owner, ownerService.getOwnerByPetId(pet.id, companyId))
        verify(petDao).getAllPetsByCompanyId(companyId)
        verify(ownerDao).getOwnerByPetId(pet.id, companyId)
    }
}