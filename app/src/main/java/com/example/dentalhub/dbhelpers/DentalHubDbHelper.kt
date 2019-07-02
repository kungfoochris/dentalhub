package com.example.dentalhub.dbhelpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.util.Log
import com.example.dentalhub.contracts.DBContract
import com.example.dentalhub.models.Patient
import java.io.File

class DentalHubDBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_PATIENT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db?.execSQL(SQL_DELETE_PATIENT_TABLE)
        db?.execSQL(SQL_DELETE_ENCOUNTER_TABLE)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun addPatient(patient: Patient): Boolean{
        val db = writableDatabase
        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.PatientEntry.COLUMN_ID, patient.id)
        values.put(DBContract.PatientEntry.COLUMN_FIRST_NAME, patient.first_name)
        values.put(DBContract.PatientEntry.COLUMN_MIDDLE_NAME, patient.middle_name)
        values.put(DBContract.PatientEntry.COLUMN_LAST_NAME, patient.last_name)
        values.put(DBContract.PatientEntry.COLUMN_GENDER, patient.gender)
        values.put(DBContract.PatientEntry.COLUMN_PHONE, patient.phone)
        values.put(DBContract.PatientEntry.COLUMN_DOB, patient.dob)
        values.put(DBContract.PatientEntry.COLUMN_STREET_ADDRESS, patient.street_address)
        values.put(DBContract.PatientEntry.COLUMN_WARD, patient.ward)
        values.put(DBContract.PatientEntry.COLUMN_CITY, patient.city)
        values.put(DBContract.PatientEntry.COLUMN_STATE, patient.state)
        values.put(DBContract.PatientEntry.COLUMN_COUNTRY, patient.country)
        values.put(DBContract.PatientEntry.COLUMN_MARITAL_STATUS, patient.marital_status)
        values.put(DBContract.PatientEntry.COLUMN_EDUCATION, patient.education)
        values.put(DBContract.PatientEntry.COLUMN_LATITUDE, patient.latitude)
        values.put(DBContract.PatientEntry.COLUMN_LONGITUDE, patient.longitude)
        values.put(DBContract.PatientEntry.COLUMN_DATE, patient.date)
        db.insert(DBContract.PatientEntry.TABLE_NAME, null, values)
        return true
    }

    fun readAllPatients(): List<Patient>{
        val patients = mutableListOf<Patient>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("select * from " + DBContract.PatientEntry.TABLE_NAME, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_PATIENT_TABLE)
            return listOf()
        }
        if (cursor!!.moveToFirst()) {
            while(!cursor.isAfterLast){
                val id = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_ID))
                val firstName = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_FIRST_NAME))
                val middleName = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_MIDDLE_NAME))
                val lastName = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_LAST_NAME))
                val fullName = "$firstName $middleName $lastName"
                val gender = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_GENDER))
                val dob = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_DOB))
                val phone = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_PHONE))
                val education = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_EDUCATION))
                val maritalStatus = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_MARITAL_STATUS))
                val streetAddress = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_STREET_ADDRESS))
                val ward = cursor.getInt(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_WARD))
                val city = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_CITY))
                val state = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_STATE))
                val country = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_COUNTRY))
                val latitude = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_LATITUDE))
                val longitude = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_LONGITUDE))
                val date = cursor.getString(cursor.getColumnIndex(DBContract.PatientEntry.COLUMN_DATE))
                val tmpTransit = Patient(
                    id,
                    firstName,
                    middleName,
                    lastName,
                    fullName,
                    gender,
                    dob,
                    phone,
                    education,
                    maritalStatus,
                    streetAddress,
                    ward,
                    city,
                    state,
                    country,
                    latitude,
                    longitude,
                    date)
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
        const val TAG = "DentalHubDBHelper"
        const val DATABASE_VERSION = 1
        val DATABASE_NAME = getDatabasePath("DentalHub.db")

        private fun getDatabasePath(s: String): String {
            val isSDPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
            val isSDSupportedDevice = Environment.isExternalStorageRemovable()
            return if(isSDPresent && isSDSupportedDevice){
                Environment.getExternalStorageDirectory().absolutePath + File.separator + s
            }else{
                s
            }
        }

        private val SQL_CREATE_PATIENT_TABLE =
            "CREATE TABLE " + DBContract.PatientEntry.TABLE_NAME + " (" +
                    DBContract.PatientEntry.COLUMN_ID + " $COLUMN_TYPE PRIMARY KEY," +
                    DBContract.PatientEntry.COLUMN_FIRST_NAME + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_MIDDLE_NAME + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_LAST_NAME + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_GENDER + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_PHONE + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_EDUCATION + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_MARITAL_STATUS + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_DOB + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_STREET_ADDRESS + " $COLUMN_TYPE, "+"" +
                    DBContract.PatientEntry.COLUMN_WARD + " INTEGER, "+""+
                    DBContract.PatientEntry.COLUMN_CITY + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_STATE + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_COUNTRY + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_LATITUDE + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_LONGITUDE + " $COLUMN_TYPE," +
                    DBContract.PatientEntry.COLUMN_DATE + " $COLUMN_TYPE" +
                    " )"

        private val SQL_DELETE_PATIENT_TABLE = "DROP TABLE IF EXISTS " + DBContract.PatientEntry.TABLE_NAME

        private val SQL_CREATE_ENCOUNTER_TABLE = "CREATE TABLE "+DBContract.PatientEntry.TABLE_NAME + " ("+
                DBContract.EncounterEntry.COLUMN_ID + " $COLUMN_TYPE PRIMARY KEY,"+
                DBContract.EncounterEntry.COLUMN_ENCOUNTER_TYPE + " $COLUMN_TYPE, "+
                DBContract.EncounterEntry.COLUMN_PATIENT + " INTEGER,"+
                DBContract.EncounterEntry.COLUMN_AUTHOR + " INTEGER,"+
                DBContract.EncounterEntry.COLUMN_DATE + " $COLUMN_TYPE"+
                ")"
        private val SQL_DELETE_ENCOUNTER_TABLE = "DROP TABLE IF EXISTS "+DBContract.EncounterEntry.TABLE_NAME

    }
}