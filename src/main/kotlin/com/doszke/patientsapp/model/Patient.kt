package com.doszke.patientsapp.model

import javax.persistence.*

/**
 * Class used for storing data about patients.
 *
 * @property id id used as primary key in the database
 * @property pesel pesel number of the patient
 * @property name name of the patient
 * @property surname surname of the patient
 * @property clinics set of Clinic instances, mapped many-to-many with Clinic entity.
 * @property medicalHistory medical history of the patient
 *
 * @constructor primary constructor
 */
@Entity(name = "patient")
data class Patient(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long,
        var pesel: String,
        var name: String,
        var surname: String,
        @ManyToMany(targetEntity = Clinic::class, fetch = FetchType.EAGER)
        var clinics: Set<Clinic>,
        var medicalHistory: String
) {

    /**
     * No-Args constructor, setting fields to default values.
     */
    constructor(): this(0L, "", "", "", setOf<Clinic>(), "")

    /**
     * Method used for mapping the object into an array of String objects, containing information about the instance.
     * @return String array
     */
    fun toStringArray(): Array<String> {
        return arrayOf(id.toString(), pesel, name, surname, medicalHistory)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Patient) return false

        if (pesel != other.pesel) return false
        if (name != other.name) return false
        if (surname != other.surname) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pesel.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + surname.hashCode()
        return result
    }


}
