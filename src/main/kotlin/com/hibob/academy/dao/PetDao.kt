package com.hibob.academy.dao

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.jooq.impl.DSL

class PetDao @Inject constructor(private val sql: DSLContext) {

    private val pet = PetTable.instance
    private val companyId = 1L

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

    fun getAllPetsByType(type: PetType): List<Pet> =
        sql.select(pet.id, pet.name, pet.type, pet.companyId, pet.dateOfArrival, pet.ownerId)
            .from(pet)
            .where(pet.type.eq(type.toString()))
            .fetch(petMapper)

    fun getAllPets(): List<Pet> =
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

    fun getPetsByOwner(ownerId: Long): List<Pet> =
        sql.select(pet.id, pet.name, pet.type, pet.companyId, pet.dateOfArrival, pet.ownerId)
            .from(pet)
            .where(pet.ownerId.eq(ownerId))
            .fetch(petMapper)

    fun countPetsByType(): Map<String, Int> {
        return sql.select(pet.type, DSL.count())
            .from(pet)
            .groupBy(pet.type)
            .fetch()
            .associate { record ->
                record[pet.type] to record.get(DSL.count())
            }
    }
}