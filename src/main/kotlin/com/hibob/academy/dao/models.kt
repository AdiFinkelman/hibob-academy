package com.hibob.academy.dao

import java.time.LocalDate

data class Example(val id: Long, val companyId: Long, val data: String)

data class Pet(
    val id: Long,
    val name: String,
    val type: PetType,
    val companyId: Long,
    val arrivalDate: LocalDate,
    val ownerId: Long?
)

data class PetCreationRequest(
    val name: String,
    val type: PetType,
    val companyId: Long,
    val arrivalDate: LocalDate,
    val ownerId: Long?
) {
    private fun extractPetCreationToPet(petCreationRequest: PetCreationRequest, id: Long): Pet {
        val pet = Pet(
            id = id,
            name = petCreationRequest.name,
            type = petCreationRequest.type,
            companyId = petCreationRequest.companyId,
            arrivalDate = petCreationRequest.arrivalDate,
            ownerId = petCreationRequest.ownerId,
        )
        return pet
    }
}

data class Owner(
    val id: Long,
    val name: String,
    val firstName: String?,
    val lastName: String?,
    val companyId: Long,
    val employeeId: String
)

data class OwnerCreationRequest(
    val name: String,
    val firstName: String?,
    val lastName: String?,
    val companyId: Long,
    val employeeId: String
) {
    fun extractOwnerCreationToOwner(ownerCreationRequest: OwnerCreationRequest, id: Long): Owner {
        val owner = Owner(
            id = id,
            name = ownerCreationRequest.name,
            firstName = ownerCreationRequest.firstName,
            lastName = ownerCreationRequest.lastName,
            companyId = ownerCreationRequest.companyId,
            employeeId = ownerCreationRequest.employeeId,
        )
        return owner
    }
}

enum class PetType {
    DOG, CAT, BIRD, MOUSE
}

data class AdoptionRequest(
    val adoptedPetId: Long,
    val companyId: Long,
    val newOwnerId: Long
)