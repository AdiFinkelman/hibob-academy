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

class PetServiceTest {
    private val petDao = mock<PetDao>()
    private val petService = PetService(petDao)

    @Test
    fun `get all pets by company id`() {
        val companyId = 2L
        val pet1 = Pet(1, "Tom", PetType.CAT, companyId, LocalDate.now(), 1)
        val pet2 = Pet(2, "Garfield", PetType.CAT, companyId, LocalDate.now(), 1)
        val pets = listOf(pet1, pet2)
        whenever(petDao.getAllPetsByCompanyId(companyId)).thenReturn(pets)
        assertEquals(pets, petService.getAllPetsByCompanyId(companyId))
    }

    @Test
    fun `create new pet`() {
        val companyId = 2L
        val petCreationRequest = PetCreationRequest("Luke", PetType.DOG, companyId, LocalDate.now(), 1)
        petService.createNewPet(petCreationRequest)
        verify(petDao).createNewPet(any())
    }

    @Test
    fun `adopt pet null throws exception`() {
        val companyId = 2L
        val adoptionRequest = AdoptionRequest(1, companyId, 1)
        whenever(petDao.getAllPetsByCompanyId(companyId)).thenReturn(emptyList())
        val expectedMessage = assertThrows<NotFoundException> { petService.adoptPet(adoptionRequest) }
        assertEquals("Pet not found", expectedMessage.message)
    }

    @Test
    fun `adopt pet successfully`() {
        val companyId = 2L
        val adoptionRequest = AdoptionRequest(1, companyId, 1)
        val pet1 = Pet(1, "Tom", PetType.CAT, companyId, LocalDate.now(), 1)
        val pet2 = Pet(2, "Garfield", PetType.CAT, companyId, LocalDate.now(), 1)
        val pets = listOf(pet1, pet2)
        whenever(petDao.getAllPetsByCompanyId(companyId)).thenReturn(pets)
        petService.adoptPet(adoptionRequest)
        verify(petDao).adoptPet(any(), any())
    }
}