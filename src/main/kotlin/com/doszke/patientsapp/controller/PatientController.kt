package com.doszke.patientsapp.controller

import com.doszke.patientsapp.model.Clinic
import com.doszke.patientsapp.model.MultipleClinicFormModel
import com.doszke.patientsapp.model.Patient
import com.doszke.patientsapp.service.PersistenceService
import org.springframework.lang.Nullable
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.web.servlet.view.RedirectView
import java.io.FileInputStream
import javax.servlet.http.HttpServletResponse
import javax.transaction.Transactional


/**
 * Main application controller. It has one session attribute: newPatient: Patient, which is an object used for data fetch from form.
 */
@SessionAttributes("newPatient")
@Controller
class PatientController(private val persistenceService: PersistenceService) {

    /**
     * Index GET mapping method.
     * @return 'index' template
     */
    @GetMapping("/")
    fun index(): String{
        return "index"
    }

    /**
     * Addpatient GET mapping method.
     * @param model model map instance
     * @return 'addpatient' template
     */
    @GetMapping("/addpatient")
    fun loadBasicAddPatient(model: ModelMap) : String {
        return this.loadAddPatient(null, model)
    }

    /**
     * Addpatient GET mapping method.
     * @param model model map instance
     * @param msg message sent to the front
     * @return 'addpatient' template
     */
    @GetMapping("/addpatient?msg={msg}")
    fun loadAddPatient(@Nullable @PathVariable("msg") msg: String?, model: ModelMap): String {
        model["msg"] = msg
        if (model.keys.contains("newPatient")) {
            return "addpatient"
        }
        model["newPatient"] = Patient()
        return "addpatient"
    }

    /**
     * Addclinic GET mapping method.
     * @param model model map instance
     * @return 'addclinic' template
     */
    @GetMapping("/addclinic")
    fun loadBasicAddClinic(model: ModelMap): String {
        return loadAddClinic(null, model)
    }

    /**
     * Addclinic GET mapping method.
     * @param model model map instance
     * @param msg message sent to the front
     * @return 'addclinic' template
     */
    @GetMapping("/addclinic?msg={msg}")
    fun loadAddClinic(@PathVariable("msg") msg: String?, model: ModelMap): String{
        model["newClinic"] = Clinic()
        return "addclinic"
    }

    /**
     * Addpatient POST mapping method. It fetches data from the first step of creating a new instance of Patient and
     * redirects the user to the second step of creation. It will indirect in case of any error.
     * @param newPatient session attribute storing information about new patient
     * @param model model map instance
     * @return 'addpatient/clinics' template on success, 'addpatient' template on failure.
     */
    @PostMapping("/addpatient")
    fun formAddPatientFirstStep(@ModelAttribute("newPatient") newPatient: Patient, model: ModelMap): ModelAndView{
        //newPatient is a session attribute - it is stored between requests, allowing to create this object
        //in multiple steps involving multiple forms
        if (newPatient.pesel == "" || newPatient.name == "" || newPatient.surname == "") {
            model["msg"] = "Pola oznaczone gwiazdką muszą zostać wypełnione! "
            return ModelAndView("addpatient")
        }
        val clinicData = persistenceService.getAllClinics()
        model["clinics"] = clinicData
        val checkList = MultipleClinicFormModel(mutableListOf())
        model["checkList"] = checkList //this object will allow multiple clinics to be selected in the form
        return ModelAndView("/addpatient/clinics")
    }

    /**
     * Addclinic POST mapping method. It is used for creation of Clinic instances. It redirects to the same page if
     * new instance has been serialized, or does not redirect in case of any error.
     * @param newClinic object containing passed by the user data about new clinic
     * @param model model map instance
     * @param attrs redirect attributes
     * @return ModelAndView object
     */
    @Transactional
    @PostMapping("/addclinic")
    fun formAddClinic(@ModelAttribute("newClinic") newClinic: Clinic, model: ModelMap, attrs: RedirectAttributes): ModelAndView {
        if (newClinic.name == "" || newClinic.address == "") {
            model["msg"] = "Pola oznaczone gwiazdką muszą zostać wypełnione!"
            return ModelAndView("/addclinic")
        }
        persistenceService.saveClinic(newClinic)
        attrs.addAttribute("msg", "Dodano klinikę")
        return ModelAndView(RedirectView("/addclinic")) //will refresh
    }

    /**
     * Addclinics GET mapping method.
     * @param model model map instance
     * @return 'addclinics' template
     */
    @GetMapping("/addclinics")
    fun loadAddClinics(model: ModelMap) : String{
        model["newClinic"] = Clinic()
        return "/addclinics"
    }

    /**
     * Addclinics POST mapping method. It is used for creation of Clinic instances. It redirects to the 'addpateint/clinics' page if
     * new instance has been serialized, or does not redirect in case of any error.
     * @param newClinic object containing passed by the user data about new clinic
     * @param model model map instance
     * @return ModelAndView object
     */
    @Transactional
    @PostMapping("/addclinics")
    fun formAddClinicFromPateints(@ModelAttribute("newClinic") newClinic: Clinic, model: ModelMap): String {
        if (newClinic.name == "" || newClinic.address == "") {
            model["msg"] = "Pola oznaczone gwiazdką muszą zostać wypełnione!"
            return "/addclinic"
        }
        persistenceService.saveClinic(newClinic)
        val clinicData = persistenceService.getAllClinics()
        model["clinics"] = clinicData
        val checkList = MultipleClinicFormModel(mutableListOf())
        model["checkList"] = checkList //this object will allow multiple clinics to be selected in the form
        return "/addpatient/clinics" //will refresh
    }

    /**
     * Addpatient/clinics POST mapping method. It persists rhe created patient to the database and redirects to 'addpatient' template.
     * @param checkList wrapper object containing a list of selections
     * @param newPatient session attribute storing information about new patient
     * @return a RedirectView instance, which redirects to 'addpatient' template
     */
    @Transactional
    @PostMapping("/addpatient/clinics")
    fun persistPatient(
            @ModelAttribute("checkList") checkList: MultipleClinicFormModel,
            @ModelAttribute("newPatient") newPatient: Patient, //this is a session attribute - stored between requests
            model: ModelMap,
            attrs: RedirectAttributes
    ) : RedirectView {
        val ids = checkList.list.filterNotNull().map { it.toLong() }.toTypedArray()
        persistenceService.savePatient(newPatient, ids)
        model["newPatient"] = Patient()
        attrs.addAttribute("msg", "Dodano pacjenta")
        return RedirectView("/addpatient")
    }

    /**
     * Showpatients GET mapping method. It prepares data from the database to be used by the view.
     * @param model model map instance
     * @return 'showpatients' template
     */
    @GetMapping("/showpatients")
    fun showPatients(model: ModelMap): String {
        model["data"] = persistenceService.getAllPatients()
        return "showpatients"
    }

    /**
     * Download GET mapping method. It allows the user to download current state of the database serialized to a JSON file.
     * @param response HttpServletResponse instance for current handling
     */
    @GetMapping("/download.json")
    fun download(response: HttpServletResponse): Unit {
        val iss = FileInputStream(persistenceService.dropPatientsToJson())
        FileCopyUtils.copy(iss, response.outputStream)
        iss.close()
    }

}