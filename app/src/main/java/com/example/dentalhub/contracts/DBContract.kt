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

        }
    }
}

