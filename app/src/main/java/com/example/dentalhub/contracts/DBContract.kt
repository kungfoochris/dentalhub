package com.example.dentalhub.contracts

import android.provider.BaseColumns

object DBContract{

    class PatientEntry: BaseColumns {
        companion object {
            var TABLE_NAME = "patients"
            var COLUMN_ID = "id"
            var COLUMN_FIRST_NAME = "first_name"
            var COLUMN_MIDDLE_NAME = "middle_name"
            var COLUMN_LAST_NAME = "last_name"
            var COLUMN_GENDER = "gender"
            var COLUMN_DOB = "dob"
            var COLUMN_PHONE = "phone"
            var COLUMN_EDUCATION = "education"
            var COLUMN_MARITAL_STATUS = "marital_status"
            var COLUMN_STREET_ADDRESS = "address"
            var COLUMN_WARD = "ward"
            var COLUMN_CITY = "city"
            var COLUMN_STATE = "state"
            var COLUMN_COUNTRY = "country"
            var COLUMN_LATITUDE = "latitude"
            var COLUMN_LONGITUDE = "longitude"
            var COLUMN_DATE = "date"
        }
    }

    class EncounterEntry: BaseColumns{
        companion object {
            var TABLE_NAME = "encounters"
            var COLUMN_ID = "id"
            var COLUMN_DATE = "date"
            var COLUMN_ENCOUNTER_TYPE = "encounter_type"
            var COLUMN_AUTHOR = "author_id"
            var COLUMN_PATIENT = "patient_id"

        }
    }

    class HistoryEntry: BaseColumns{
        companion object{
            var TABLE_NAME = "histories"
            var COLUMN_BLOOD_DISORDER = "blood_disorder"
            var COLUMN_DIABETES = "diabetes"
            var COLUMN_LIVER_PROBLEM = "liver_problem"
            var COLUMN_RHEUMATIC_FEVER = "rheumatic_fever"
            var COLUMN_SEIZURES_OR_EPILEPSY = "seizures_or_epilepsy"
            var COLUMN_HEPATITIS_B_OR_C = "hepatitis_b_or_c"
            var COLUMN_HIV = "hiv"
            var COLUMN_OTHER = "other"
            var COLUMN_NO_UNDERLYING_MEDICAL_RECORD = "no_underlying_medical_record"
            var COLUMN_MEDICATIONS = "medications"
            var COLUMN_NOT_TAKING_ANY_MEDICATIONS = "not_taking_any_medications"
            var COLUMN_ALLERGIES = "allergies"
        }
    }

    class ScreeningEntry: BaseColumns{
        companion object{
            var TABLE_NAME = "screenings"

            var COLUMN_CARRIES_RISK = "carries_risk"
            var COLUMN_DECAYED_PRIMARY_TEETH = "decayed_primary_teeth"
            var COLUMN_DECAYED_PERMANENT_TEETH = "decayed_permanent_teeth"
            var COLUMN_CAVITY_PERMANENT_TEETH = "cavity_permanent_teeth"
            var COLUMN_CAVITY_PERMANENT_ANTERIOR = "permanent_anterior"
            var COLUMN_ACTIVE_INFECTION = "active_infection"
            var COLUMN_NEED_ART_FILLING = "need_art_filling"
            var COLUMN_NEED_SEALANT = "need_sealant"
            var COLUMN_NEED_SDF = "need_sdf"
            var COLUMN_NEED_EXTRACTION = "need_extraction"
        }
    }
    class Treatment: BaseColumns{
        companion object{
            var TABLE_NAME = "treatments"
        }
    }
    class ReferralEntry: BaseColumns{
        companion object{
            var TABLE_NAME = "referrals"
        }
    }
}

