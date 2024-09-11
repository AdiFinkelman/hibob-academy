package com.hibob.academy.dao

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper

class PetDao @Inject constructor(private val sql: DSLContext) {

    private val pet = PetTable.instance
    private val owner = OwnerTable.instance

    private val petMapper = RecordMapper<Record, Pet>
    { record ->
        Pet (
            id = record[pet.id],
            name = record[pet.name],
            type = record[pet.type],
            companyId = record[pet.companyId].toLong(),
            arrivalDate = record[pet.dateOfArrival],
            ownerId = record[pet.ownerId].toLong()
        )
    }

    fun getAllPetsFromType(type: PetType): List<Pet> =
        sql.select(pet.name, pet.companyId, pet.dateOfArrival)
            .from(pet)
            .where(pet.type.eq(getPetType(type)))
            .fetch(petMapper)

    fun getAllPetsByCompanyId(companyId: Long): List<Pet> =
        sql.select(pet.id, pet.name, pet.type, pet.companyId, pet.dateOfArrival, pet.ownerId)
            .from(pet)
            .fetch(petMapper)

    fun getAllPetsByType(type: PetType): List<Pet> =
        sql.select(pet.id, pet.name, pet.type, pet.companyId, pet.dateOfArrival, pet.ownerId)
            .from(pet)
            .fetch(petMapper)

    fun createNewPet(petData: Pet) {
        sql.insertInto(pet)
            .set(pet.name, petData.name)
            .set(pet.type, petData.type)
            .set(pet.companyId, petData.companyId)
            .set(pet.dateOfArrival, petData.arrivalDate)
            .set(pet.ownerId, petData.ownerId)
            .execute()
    }

    fun updatePet(petData: Pet, ownerId: Long) {
        sql.update(pet)
            .set(pet.name, petData.name)
            .set(pet.type, petData.type)
            .set(pet.companyId, ownerId)
            .set(pet.dateOfArrival, petData.arrivalDate)
            .where(pet.ownerId.eq(ownerId))
            .execute()
    }

//    Leaved in a comment because there is a problem with the test of this method

//    fun getOwnerByPetId(petData: Pet): Owner? =
//        sql.select(owner.id, owner.name, owner.employeeId, owner.companyId)
//            .from(owner)
//            .where(owner.id.eq(petData.ownerId?.toInt()))
//            .fetchOne { record ->
//                Owner(
//                    id = record[owner.id],
//                    name = record[owner.name],
//                    firstName = null,
//                    lastName = null,
//                    companyId = record[owner.companyId].toLong(),
//                    employeeId = record[owner.employeeId]
//                )
//            }
}