package com.doszke.patientsapp.service

import com.doszke.patientsapp.model.Clinic
import com.doszke.patientsapp.model.Patient
import com.doszke.patientsapp.model.util.Converter
import com.doszke.patientsapp.repository.ClinicRepository
import com.doszke.patientsapp.repository.PatientRepository
import com.google.gson.Gson
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter

/**
 * Persistence service, limiting the range of operations passed to the user and combining the logic of Clinic- and PatientRepository.
 *
 * @property clinicRepository Clinic CRUD repository
 * @property patientRepository Patient CRUD repository
 * @constructor primary constructor
 */
@Service
class PersistenceService(
        private val clinicRepository: ClinicRepository,
        private val patientRepository: PatientRepository
) {

    //implementations of repository methods are annotated with @Transactional, no need for it here

    /**
     * Method used for saving Clinic instance into the database.
     * @param clinic Clinic instance to save
     */
    fun saveClinic(clinic: Clinic) {
        clinicRepository.save(clinic)
    }

    /**
     * Method used for retrieving Clinic instances from the database and mapping them into String arrays.
     * @return list of String arrays containing information about clinics
     */
    fun getAllClinics(): List<Array<String>> {
        val clinics = clinicRepository.findAll().toList()
        return clinics.map { it.toStringArray() }
    }

    /**
     * Method used for retrieving one Clinic instance by its primary key.
     * @param id primary key of type Long
     * @return fetched Clinic instance
     */
    private fun getClinicById(id: Long): Clinic{
        return clinicRepository.findById(id).get() //maybe worth orElseThrow() and handle that later
    }

    /**
     * method used for saving new patient into the database.
     * @param patient instance to save
     * @param idsOfClinics array of primary keys of entity Clinic intended to be bound with the new Patient instance
     */
    fun savePatient(patient: Patient, idsOfClinics: Array<Long>) {
        val clinicsById = idsOfClinics.map { this.getClinicById(it) }.toSet()
        patient.clinics = clinicsById
        clinicsById.forEach { it.patients + patient } //adding to set, + operator means set.add(element)
        patientRepository.save(patient)
    }

    /**
     * Method used for retrieving information about patients and bound to them clinics.
     * @return a map, which keys are String arrays containing information about Patients, and values are lists of String objects containing information about clinics.
     */
    fun getAllPatients(): Map<Array<String>, List<String>> { //keys are info about patient, values are lists of info about patients' clinics
        val patients = patientRepository.findAll().toList()
        return Converter.convertPatientsForFront(patients)
    }

    /**
     * Method used for creating a JSON file containing all anonymized patients and corresponding to them clinics.
     * @return JSON file
     */
    fun dropPatientsToJson(): File {
        val patients = patientRepository.findAll().toList()
        val anonymized = Converter.convertPatientsToAnonymized(patients)
        val jsoned = Gson().toJson(anonymized)
        val file = File("download.json")
        val f = FileWriter(file)
        f.write(jsoned)
        f.close()
        return file
    }


}