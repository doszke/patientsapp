package com.doszke.patientsapp.model.util

import com.doszke.patientsapp.model.Patient

/**
 * Class used for converting data classes into arrays of Strings. Its main purpose is to prevent leaking data objects into views.
 */
class Converter {

    companion object {

        /**
         * Method used for converting a list of Patient instances into a list of arrays of String objects.
         * @param patients list of Patient instances
         * @return list of arrays of String objects
         */
        fun convertPatients(patients: List<Patient>): Map<Array<String>, List<String>> {
            val output = mutableMapOf<Array<String>, List<String>>()
            patients.forEach {
                val key = it.toStringArray()
                val value = it.clinics.map { it.name + " - " + it.address }.toList() //second 'it' is Clinic instance, first is Patient
                output[key] = value
            }
            return output
        }

    }

}