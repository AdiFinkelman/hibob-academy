package com.hibob.academy.dao

import com.hibob.academy.utils.BobDbTest
import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired

@BobDbTest
class OwnerDaoTest @Autowired constructor(private val sql: DSLContext) {

    private val ownerDao = OwnerDao(sql)
    val table = OwnerTable.instance
    private val companyId = 1L
    val owner = Owner(1,"Adi Finkelman", "Adi", "Finkelman", companyId, "2")

    @Test
    fun `create owner test`() {
        ownerDao.createNewOwner(owner)
        assertEquals("Adi Finkelman" ,ownerDao.getAllOwnersByCompanyId(companyId)[0].name)
        assertEquals("2", ownerDao.getAllOwnersByCompanyId(companyId)[0].employeeId)
    }

    @Test
    fun `test name split`() {
        ownerDao.createNewOwner(owner)
        ownerDao.splitNameToFirstAndLastName(owner.name)
        assertEquals("Adi", ownerDao.getAllOwnersByCompanyId(companyId)[0].firstName)
        assertEquals("Finkelman", ownerDao.getAllOwnersByCompanyId(companyId)[0].lastName)
    }

    @Test
    fun `test null name split`() {
        val ownerWithNullName = Owner(2, "", null, null, 1L, "3")
        ownerDao.createNewOwner(ownerWithNullName)
        ownerDao.splitNameToFirstAndLastName(ownerWithNullName.name)
        assertEquals("", ownerDao.getAllOwnersByCompanyId(companyId)[0].firstName)
        assertEquals("", ownerDao.getAllOwnersByCompanyId(companyId)[0].lastName)
    }

    @BeforeEach
    @AfterEach
    fun cleanup() {
        sql.deleteFrom(table)
            .where(table.companyId.eq(companyId))
            .execute()
    }
}

