package com.doszke.patientsapp

import com.doszke.patientsapp.model.Clinic
import com.doszke.patientsapp.model.Patient
import com.doszke.patientsapp.repository.ClinicRepository
import com.doszke.patientsapp.repository.PatientRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import java.lang.Math.abs
import javax.transaction.Transactional

@SpringBootTest
class PatientsappApplicationTests {

    @Autowired
    private lateinit var patientRepository: PatientRepository

    @Autowired
    private lateinit var clinicRepository: ClinicRepository

    @Test
    @Transactional //methods annotated with Transactional are checked like JavaEE EntityManager implementation of db access
    @Rollback // after execution all possible changes are rolled back from the database, either on success or in failure
    fun databaseTest() {
        val createdClinics = listOf( //id is automatically generated and given value is overridden
                Clinic(0, "Clinic a", "Curie-Skłodowskiej, Wrocław", setOf<Patient>()),
                Clinic(0, "Clinic b", "ul. Głośna, Bydgoszcz", setOf<Patient>())
        )
        val beforeClinics = clinicRepository.findAll().toList()
        //save to database
        createdClinics.forEach { clinicRepository.save(it) }

        //retrieve from database
        val clinics = clinicRepository.findAll().toList()
        assert(abs(beforeClinics.size - clinics.size) == createdClinics.size)
        assert(clinics.containsAll(createdClinics)) //one way of checking equality of two collections

        //this is also in the same test, because we need clinics to fully initialize patients
        //and rollback works only for current method, not for class
        //create some patients
        val createdPatients = listOf(
                Patient(0, "3124567", "John", "Doe", setOf(createdClinics[0]), "cardiovascular patient"),
                Patient(0, "66723", "Maria", "Sanders", setOf<Clinic>(), "patient broke her leg 3 years ago"),
                Patient(0, "567", "Adam", "Smith", createdClinics.toSet(), "patient after stroke")
        )

        //save to database
        createdPatients.forEach { patientRepository.save(it) }

        //retrieve from database
        val patients = patientRepository.findAll().toList()
        assert(patients.containsAll(createdPatients))



        //removing in a way that does not violate constraints
        patients.forEach{ patientRepository.delete(it) }

        val removedPatients = patientRepository.findAll().toList()
        assert(!removedPatients.containsAll(createdPatients))

        clinics.forEach{ clinicRepository.delete(it) }
        val removedClinics = clinicRepository.findAll().toList()
        assert(!removedClinics.containsAll(createdClinics))
    }

}
