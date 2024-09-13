package com.hibob.academy.dao

import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PetDao @Autowired constructor(private val sql: DSLContext) {

    private val petTable = PetTable.instance

    private val petMapper = RecordMapper<Record, Pet>
    { record ->
        Pet (
            id = record[petTable.id],
            name = record[petTable.name],
            type = enumValueOf<PetType>(record[petTable.type]),
            companyId = record[petTable.companyId],
            arrivalDate = record[petTable.dateOfArrival],
            ownerId = record[petTable.ownerId]
        )
    }

    fun getAllPetsByCompanyId(companyId: Long): List<Pet> =
        sql.select(petTable.id, petTable.name, petTable.type, petTable.companyId, petTable.dateOfArrival, petTable.ownerId)
            .from(petTable)
            .where(petTable.companyId.eq(companyId))
            .fetch(petMapper)

    fun getAllPetsByType(type: PetType, companyId: Long): List<Pet> =
        sql.select(petTable.id, petTable.name, petTable.type, petTable.companyId, petTable.dateOfArrival, petTable.ownerId)
            .from(petTable)
            .where(petTable.type.eq(type.toString()))
            .and(petTable.companyId.eq(companyId))
            .fetch(petMapper)

    fun createNewPet(pet: PetCreationRequest): Long {
        return sql.insertInto(petTable)
            .set(petTable.name, pet.name)
            .set(petTable.type, pet.type.toString())
            .set(petTable.companyId, pet.companyId)
            .set(petTable.dateOfArrival, pet.arrivalDate)
            .set(petTable.ownerId, pet.ownerId)
            .returning(petTable.id)
            .fetchOne()!![petTable.id]
    }

    fun adoptPet(pet: Pet, ownerId: Long) {
        sql.update(petTable)
            .set(petTable.ownerId, ownerId)
            .where(petTable.id.eq(pet.id))
            .and(petTable.companyId.eq(pet.companyId))
            .execute()
    }

    //sql 2
    fun getPetsByOwner(ownerId: Long): List<Pet> =
        sql.select(petTable.id, petTable.name, petTable.type, petTable.companyId, petTable.dateOfArrival, petTable.ownerId)
            .from(petTable)
            .where(petTable.ownerId.eq(ownerId))
            .fetch(petMapper)

    fun countPetsByType(): Map<String, Int> {
        return sql.select(petTable.type, DSL.count())
            .from(petTable)
            .groupBy(petTable.type)
            .fetch()
            .associate { record ->
                record[petTable.type] to record.get(DSL.count())
            }
    }
}