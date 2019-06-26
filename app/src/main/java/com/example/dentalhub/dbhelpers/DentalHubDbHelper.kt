package com.example.dentalhub.dbhelpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.dentalhub.contracts.DBContract

class DentalHubDBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_PATIENT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db?.execSQL(SQL_DELETE_PATIENT_TABLE)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        private const val COLUMN_TYPE = "TEXT"
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "DentalHub.db"

        private val SQL_CREATE_PATIENT_TABLE =
            "CREATE TABLE " + DBContract.PatientEntry.TABLE_NAME + " (" +
                    DBContract.PatientEntry.COLUMN_ID + " $COLUMN_TYPE PRIMARY KEY," +
                    DBContract.PatientEntry.COLUMN_FIRST_NAME + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_LAST_NAME + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_ADDRESS + " $COLUMN_TYPE "+"" +
                    " )"

        private val SQL_DELETE_PATIENT_TABLE = "DROP TABLE IF EXISTS " + DBContract.PatientEntry.TABLE_NAME

        private val SQL_CREATE_ENCOUNTER_TABLE = "CREATE TABLE "+DBContract.PatientEntry.TABLE_NAME + " ("+
                DBContract.EncounterEntry.COLUMN_ID + " $COLUMN_TYPE PRIMARY KEY,"+
                DBContract.EncounterEntry.COLUMN_DATE + " $COLUMN_TYPE"+
                ")"
        private val SQL_DELETE_ENCOUNTER_TABLE = "DROP TABLE IF EXISTS "+DBContract.EncounterEntry.TABLE_NAME

    }
}