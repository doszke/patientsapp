package com.doszke.patientsapp.repository

import com.doszke.patientsapp.model.Clinic
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * CRUD repository for Clinic data class, allowing to do basic CRUD operations on the database with the Clinic entity.
 */
@Repository
interface ClinicRepository: CrudRepository<Clinic, Long>