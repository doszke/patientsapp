package com.doszke.patientsapp.model

/**
 * Wrapper data class used for fetching information about multiple-choice selections form the form.
 *
 * @property list a list of nullable String objects
 * @constructor primary constructor
 */
data class MultipleClinicFormModel(var list: List<String?>)