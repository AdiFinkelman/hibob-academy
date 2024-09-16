package com.hibob.academy.dao


import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OwnerDao @Autowired constructor(private val sql: DSLContext) {

    private val ownerTable = OwnerTable.instance
    private val petTable = PetTable.instance

    private val ownerMapper = RecordMapper<Record, Owner>
    { record ->
        val (firstName, lastName) = splitNameToFirstAndLastName(record[ownerTable.name])

        Owner(
            id = record[ownerTable.id],
            name = record[ownerTable.name],
            firstName = firstName,
            lastName = lastName,
            companyId = record[ownerTable.companyId],
            employeeId = record[ownerTable.employeeId]
        )
    }

    fun getAllOwnersByCompanyId(companyId: Long): List<Owner> =
        sql.select(ownerTable.id, ownerTable.name, ownerTable.employeeId, ownerTable.companyId)
            .from(ownerTable)
            .where(ownerTable.companyId.eq(companyId))
            .fetch(ownerMapper)

    fun createNewOwner(owner: OwnerCreationRequest): Long {
        return sql.insertInto(ownerTable)
            .set(ownerTable.name, owner.name)
            .set(ownerTable.companyId, owner.companyId)
            .set(ownerTable.employeeId, owner.employeeId)
            .onConflict(ownerTable.companyId, ownerTable.employeeId)
            .doNothing()
            .returning(ownerTable.id)
            .fetchOne()!![ownerTable.id] ?: -1
    }

    fun getOwnerByPetId(petId: Long, companyId: Long): Owner? =
        sql.select(ownerTable.id, ownerTable.name, ownerTable.employeeId, ownerTable.companyId)
            .from(ownerTable).leftJoin(petTable).on(ownerTable.id.eq(petTable.ownerId))
            .where(petTable.id.eq(petId))
            .and(petTable.companyId.eq(companyId))
            .fetchOne(ownerMapper)

    private fun splitNameToFirstAndLastName(fullName: String): Pair<String, String> {
        val parts = fullName.trim().split("\\s+".toRegex())

        return when {
            parts.size == 1 -> Pair(parts[0], "")
            parts.size >= 2 -> Pair(parts.first(), parts.last())
            else -> Pair("", "")
        }
    }
}