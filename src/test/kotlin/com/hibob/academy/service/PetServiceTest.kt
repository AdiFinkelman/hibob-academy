package com.hibob.academy.service

import com.hibob.academy.dao.*
import jakarta.ws.rs.BadRequestException
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

    @Test
    fun `adopt multiple pets with empty list`() {
        val companyId = 2L
        val newOwnerId = 1L
        val multiAdoptionRequest = MultiAdoptionRequest(emptyList(), companyId, newOwnerId)
        whenever(petDao.getAllPetsByCompanyId(multiAdoptionRequest.companyId)).thenReturn(emptyList())
        assertEquals(emptyList<PetCreationRequest>(), multiAdoptionRequest.petsToAdopt)
    }

    @Test
    fun `adopt multiple pets successfully`() {
        val companyId = 2L
        val newOwnerId = 1L
        val adoptionRequest1 = AdoptionCreationRequest(1)
        val adoptionRequest2 = AdoptionCreationRequest(2)
        val adoptionRequests = listOf(adoptionRequest1, adoptionRequest2)
        val multiAdoptionRequest = MultiAdoptionRequest(adoptionRequests, companyId, newOwnerId)
        val pet1 = Pet(1, "Tom", PetType.CAT, companyId, LocalDate.now(), 1)
        val pet2 = Pet(2, "Garfield", PetType.CAT, companyId, LocalDate.now(), 1)
        whenever(petDao.getAllPetsByCompanyId(multiAdoptionRequest.companyId)).thenReturn(listOf(pet1, pet2))
        petService.adoptMultiplePets(multiAdoptionRequest)
        verify(petDao).adoptMultiplePets(listOf(pet1, pet2), newOwnerId)
    }

    @Test
    fun `create multiple pets when pets empty and throws exception`() {
        val petCreationRequests = emptyList<PetCreationRequest>()
        val expectedMessage = assertThrows<BadRequestException> { petService.createMultiplePets(petCreationRequests) }
        assertEquals("The pets list is empty", expectedMessage.message)
    }

    @Test
    fun `create multiple pets successfully`() {
        val companyId = 2L
        val petCreation1 = PetCreationRequest("Tom", PetType.CAT, companyId, LocalDate.now(), 1)
        val petCreation2 = PetCreationRequest("Garfield", PetType.CAT, companyId, LocalDate.now(), 1)
        val petCreationRequests = listOf(petCreation1, petCreation2)
        petService.createMultiplePets(petCreationRequests)
        verify(petDao).createMultiplePets(petCreationRequests)
    }
}