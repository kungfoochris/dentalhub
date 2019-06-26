package com.example.dentalhub.dbhelpers

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.dentalhub.contracts.DBContract
import com.example.dentalhub.models.Patient

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

    fun readAllPatients(): List<Patient>{
        val patients = mutableListOf<Patient>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("select * from " + DBContract.PatientEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_PATIENT_TABLE)
            return ArrayList()
        }
        if (cursor!!.moveToFirst()) {
            while(!cursor.isAfterLast){
                val id = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_ID))
                val firstName = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_FIRST_NAME))
                val middleName = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_MIDDLE_NAME))
                val lastName = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_LAST_NAME))
                val address = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_ADDRESS))
                val fullName = firstName + " " + middleName + " " + lastName
                val gender = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_GENDER))
                val dob = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_DOB))
                val phone = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_PHONE))
                val education = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_EDUCATION))
                val city = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_CITY))
                val state = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_STATE))
                val country = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_COUNTRY))
                val latitutde = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_LATITUDE))
                val longitude = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_LONGITUDE))
                val date = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_DATE))
                val tmpTransit = Patient(id, firstName, middleName, lastName,fullName, address, gender, dob, phone, education, city, state,country, latitutde, longitude, date)
                patients.add(tmpTransit)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return patients.toList()
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
                    DBContract.PatientEntry.COLUMN_MIDDLE_NAME + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_LAST_NAME + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_GENDER + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_PHONE + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_DOB + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_ADDRESS + " $COLUMN_TYPE "+"" +
                    DBContract.PatientEntry.COLUMN_CITY + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_STATE + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_COUNTRY + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_LATITUDE + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_LONGITUDE + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_DATE + " $COLUMN_TYPE," +
                    " )"

        private val SQL_DELETE_PATIENT_TABLE = "DROP TABLE IF EXISTS " + DBContract.PatientEntry.TABLE_NAME

        private val SQL_CREATE_ENCOUNTER_TABLE = "CREATE TABLE "+DBContract.PatientEntry.TABLE_NAME + " ("+
                DBContract.EncounterEntry.COLUMN_ID + " $COLUMN_TYPE PRIMARY KEY,"+
                DBContract.EncounterEntry.COLUMN_DATE + " $COLUMN_TYPE"+
                ")"
        private val SQL_DELETE_ENCOUNTER_TABLE = "DROP TABLE IF EXISTS "+DBContract.EncounterEntry.TABLE_NAME

    }
}