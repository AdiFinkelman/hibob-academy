package com.hibob.academy.dao

import java.sql.Date
import java.time.LocalDate

data class Example(val id: Long, val companyId: Long, val data: String)

data class Pet(
    val id: Long,
    val name: String,
    val type: String,
    val companyId: Long,
    val arrivalDate: LocalDate,
    val ownerId: Long?
)

data class Owner(
    val id: Long,
    val name: String,
    val firstName: String?,
    val lastName: String?,
    val companyId: Long,
    val employeeId: String
)

enum class PetType() {
    DOG, CAT, BIRD, MOUSE
}

fun getPetType(petType: PetType) =
    when (petType) {
        PetType.DOG -> "Dog"
        PetType.CAT -> "Cat"
        PetType.BIRD -> "Bird"
        PetType.MOUSE -> "Mouse"
    }