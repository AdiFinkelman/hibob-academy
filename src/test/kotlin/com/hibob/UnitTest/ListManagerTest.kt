package com.hibob.UnitTest

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.*

class ListManagerTest {
    private val listManager = ListManager()
    private val person = Person("Adi", 28)
    private val baby = Person("Ori", 1)

    // addPerson
    @Test
    fun `adding a unique person`() {
        assertEquals(true, listManager.addPerson(person))
    }

    @Test
    fun `adding a duplicate person and ensure it throws the expected exception`() {
        assertThrows<IllegalArgumentException> {
            listManager.addPerson(person)
            listManager.addPerson(person)
        }
    }

    @Test
    fun `adding multiple people, checking that the list grows appropriately`() {
        listManager.addPerson(person)
        listManager.addPerson(baby)
        val sizeOfPeople = listManager.getPeopleSortedByAgeAndName().size
        assertEquals(sizeOfPeople, 2)
    }

    // removePerson
    @Test
    fun `removing a person that exists in the list` (){
        listManager.addPerson(person)
        assertEquals(true, listManager.removePerson(person))
    }

    @Test
    fun `trying to remove a person that does not exist, ensuring it returns false` (){
        assertEquals(false, listManager.removePerson(person))
    }

    @Test
    fun `the state of the list after multiple add and remove operations` () {
        listManager.addPerson(person)
        listManager.addPerson(baby)
        listManager.removePerson(person)
        assertEquals(listOf(baby), listManager.getPeopleSortedByAgeAndName())
    }

    // getPeopleSortedByAgeAndName

    @Test
    fun `with an empty list` () {
        assertEquals(emptyList<Person>(), listManager.getPeopleSortedByAgeAndName())
    }

    @Test
    fun `with one person` () {
        listManager.addPerson(person)
        assertEquals(listOf(person), listManager.getPeopleSortedByAgeAndName())
    }

    @Test
    fun `with multiple people to ensure they are sorted first by age, then by name` () {
        listManager.addPerson(person)
        listManager.addPerson(baby)
        assertEquals(listOf(baby, person), listManager.getPeopleSortedByAgeAndName() )
    }

    @Test
    fun `with edge cases like people with the same name but different ages and vice versa` () {
        val personWithSameName = Person("Adi", 27)
        val personWithSameAge = Person("Dolev", 28)
        val list1 = ListManager()
        val list2 = ListManager()

        list1.addPerson(person)
        list1.addPerson(personWithSameName)

        list2.addPerson(person)
        list2.addPerson(personWithSameAge)

        assertEquals(listOf(personWithSameName,  person), list1.getPeopleSortedByAgeAndName())
        assertEquals(listOf(person, personWithSameAge), list2.getPeopleSortedByAgeAndName())
    }

    @Test
    fun `people list is empty` () {
        assertEquals(null, listManager.calculateStatistics())
    }

    @Test
    fun `is people statistic as expected` () {
        val peopleStatistics = PeopleStatistics(14.5, baby, person, mapOf(baby.age to 1, person.age to 1))
        listManager.addPerson(person)
        listManager.addPerson(baby)
        assertEquals(peopleStatistics, listManager.calculateStatistics())
    }

}