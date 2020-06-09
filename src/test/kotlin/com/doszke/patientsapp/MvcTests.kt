package com.doszke.patientsapp

import com.doszke.patientsapp.model.Clinic
import com.doszke.patientsapp.model.Patient
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

@SpringBootTest
@AutoConfigureMockMvc
class MvcTests {

    @Autowired
    private lateinit var mockMvc: MockMvc


    @Test
    fun testIndex(){
        mockMvc.perform(get("/"))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("Dodaj pacjenta")))
    }

    @Test
    fun testShowPatients(){
        mockMvc.perform(get("/showpatients"))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("Zapisz do pliku .json")))

    }

    @Test
    fun addClinic(){
        val path = "/addclinic"
        mockMvc.perform(get(path).requestAttr("newClinic", Clinic()))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("Dodaj klinikę")))

    }

    @Test
    fun addPatient(){
        mockMvc.perform(get("/addpatient").sessionAttr("newPatient", Patient()))
                .andExpect(status().isOk)
                .andExpect(view().name("addpatient"))
    }

    @Test
    fun selectClinic(){
        mockMvc.perform(get("/addpatient/clinics").sessionAttr("newPatient", Patient()))
                .andExpect(status().isMethodNotAllowed)//GET method not supported for this mapping, but the template itself exists
        //site is served by returning 'clinics' on GET /addpatient mapping
    }

}