package com.doszke.patientsapp.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

/**
 * Class used for storing data about clinics.
 *
 * @property id id used as primary key in the database
 * @property name name of the clinic
 * @property address address of the clinic
 * @property patients set of Patient instances, mapped many-to-many with Patient entity. JSON-ignored
 *
 * @constructor primary constructor
 */
@Entity(name = "clinic")
data class Clinic(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long,
        var name: String,
        var address: String,
        @JsonIgnore
        @ManyToMany(targetEntity = Patient::class, fetch = FetchType.EAGER)
        var patients: Set<Patient>
) {

    /**
     * No-Args constructor, setting properties to default values.
     */
    constructor() : this(0L, "", "", setOf<Patient>())

    /**
     * Method used for mapping the object into an array of String objects, containing information about the instance.
     * @return String array
     */
    fun toStringArray(): Array<String> {
        return arrayOf(id.toString(), name, address)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Clinic) return false

        if (name != other.name) return false
        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + address.hashCode()
        return result
    }
}