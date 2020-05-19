package com.doszke.patientsapp.repository

import com.doszke.patientsapp.model.Patient
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * CRUD repository for Patient data class, allowing to do basic CRUD operations on the database with the Patient entity.
 */
@Repository
interface PatientRepository: CrudRepository<Patient, Long>

