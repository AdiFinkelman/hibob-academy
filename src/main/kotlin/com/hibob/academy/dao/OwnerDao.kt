package com.hibob.academy.dao


import jakarta.inject.Inject
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper

class OwnerDao @Inject constructor(private val sql: DSLContext) {

    private val owner = OwnerTable.instance
    private val pet = PetTable.instance

    private val ownerMapper = RecordMapper<Record, Owner>
    { record ->
        val (firstName, lastName) = splitNameToFirstAndLastName(record[owner.name])

        Owner (
            id = record[owner.id].toLong(),
            name = record[owner.name],
            firstName = firstName,
            lastName = lastName,
            companyId = record[owner.companyId].toLong(),
            employeeId = record[owner.employeeId]
        )
    }

    fun getAllOwnersByCompanyId(companyId: Long): List<Owner> =
        sql.select(owner.id, owner.name, owner.employeeId, owner.companyId)
            .from(owner)
            .where(owner.companyId.eq(companyId))
            .fetch(ownerMapper)

    fun createNewOwner(ownerData: Owner) {
        sql.insertInto(owner)
            .set(owner.name, ownerData.name)
            .set(owner.companyId, ownerData.companyId)
            .set(owner.employeeId, ownerData.employeeId)
            .onConflict(owner.companyId, owner.employeeId)
            .doNothing()
            .execute()
    }

    fun splitNameToFirstAndLastName(fullName: String): Pair<String, String> {
        val parts = fullName.trim().split("\\s+".toRegex())

        return when {
            parts.size == 1 -> Pair(parts[0], "")
            parts.size >= 2 -> Pair(parts.first(), parts.last())
            else -> Pair("", "")
        }
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