package com.example.dentalhub.contracts

import android.provider.BaseColumns

object DBContract{

    class PatientEntry: BaseColumns {
        companion object {
            var TABLE_NAME = "patients"
            var COLUMN_ID = "id"
            var COLUMN_FIRST_NAME = "first_name"
            var COLUMN_LAST_NAME = "last_name"
            var COLUMN_ADDRESS = "address"
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

