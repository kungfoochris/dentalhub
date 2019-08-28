package com.abhiyantrik.dentalhub

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhiyantrik.dentalhub.adapters.PatientAdapter
import com.abhiyantrik.dentalhub.entities.Patient
import com.abhiyantrik.dentalhub.entities.Patient_
import com.abhiyantrik.dentalhub.entities.Recall
import com.abhiyantrik.dentalhub.entities.Recall_
import com.abhiyantrik.dentalhub.services.LocationTrackerService
import com.abhiyantrik.dentalhub.services.SyncService
import com.abhiyantrik.dentalhub.utils.DateHelper
import com.abhiyantrik.dentalhub.utils.RecyclerViewItemSeparator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.perf.metrics.AddTrace
import com.hornet.dateconverter.DateConverter
import io.objectbox.Box
import io.objectbox.exception.DbException
import io.objectbox.query.Query
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var loading: ProgressBar
    private lateinit var btnAddPatient: Button
    private lateinit var fabBtnAddPatient: FloatingActionButton
    private lateinit var fabBtnSync: FloatingActionButton

    private lateinit var tvLocation: TextView
    private lateinit var tvName: TextView
    private lateinit var tvActivity: TextView

    private lateinit var context: Context
    private lateinit var patientAdapter: PatientAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var dividerItemDecoration: DividerItemDecoration
    private lateinit var allPatients: List<Patient>

    private lateinit var patientsBox: Box<Patient>
    private lateinit var patientsQuery: Query<Patient>
    private lateinit var recallBox: Box<Recall>
//    private lateinit var recallQuery: Query<Recall>

    private lateinit var allPatientRecall: MutableList<Patient>

    private val TAG = "MainActivity"


    @AddTrace(name = "onCreateMainActivity", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        setContentView(R.layout.activity_main)

        context = this

        startService(Intent(this, LocationTrackerService::class.java))

        setupUI()

        Log.d("Location", DentalApp.geography_name)
        Log.d("Proxy: ", DentalApp.readFromPreference(context, Constants.PREF_PROFILE_FULL_NAME,"Some thing is fishy."))
        Log.d("Activity", DentalApp.activity_name)
        tvName.text = DentalApp.readFromPreference(context, Constants.PREF_PROFILE_FULL_NAME,"")
        tvLocation.text = DentalApp.geography_name
        tvActivity.text = DentalApp.activity_name
    }

    private fun listRecallPatients() {
        println("called once.")
        var c = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = df.format(c)
//
//        Log.d("LocalDate", currentDate.toString())
//        // get all Address objects
//        val builder = patientsBox.query()
//        // ...which are linked from a Recall date "today"
//        builder.link(Patient_.recall).equal(Recall_.date, currentDate.toString())
//        var sesameStreetsWithElmo = builder.build().find()
//        allPatientRecall = sesameStreetsWithElmo
//
//        for (eachDay in 1..10) {
////            val days = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                LocalDate.now().plusDays(eachDay.toLong())
////            } else {
////                // do something here
////            }
//            val days= 1;
//            val builder = patientsBox.query()
//            builder.link(Patient_.recall).equal(Recall_.date, days.toString())
//            sesameStreetsWithElmo = builder.build().find()
//            allPatientRecall.addAll(sesameStreetsWithElmo)
//        }
//
//        for (recall in allPatientRecall) {
//            println("Recall patient name is ${recall.fullName()}")
//        }
        allPatientRecall = mutableListOf()
        val today = DateHelper.getCurrentNepaliDate()
        val todayPatient = patientsBox.query().equal(Patient_.recall_date, today).build().find()


        val rowToday = Patient()
        rowToday.first_name = "Recall Today"
        rowToday.content = "header"
        allPatientRecall.add(rowToday)
        allPatientRecall.addAll(todayPatient)

//        val rowTomorrow = Patient()
//        rowTomorrow.first_name = "Recall Tomorrow" + tomorrow
//        rowTomorrow.content = "header"
//        allPatientRecall.add(rowTomorrow)
//        allPatientRecall.addAll(tomorrowPatient)

        val rowThisWeek = Patient()
        rowThisWeek.first_name = "Recall Next Week"
        rowThisWeek.content = "Header"
        allPatientRecall.add(rowThisWeek)

        val tomorrow = addOneDay(today)
        val thirdDay = addOneDay(tomorrow)
        val fourthDay = addOneDay(thirdDay)
        val fifthDay = addOneDay(fourthDay)
        val sixthDay = addOneDay(fifthDay)
        val seventhDay = addOneDay(sixthDay)
        val eightDay = addOneDay(seventhDay)
        val ninthDay = addOneDay(eightDay)
        val thisWeekPatients = patientsBox.query()
            .equal(Patient_.recall_date, tomorrow).or()
            .equal(Patient_.recall_date, thirdDay).or()
            .equal(Patient_.recall_date, fourthDay).or()
            .equal(Patient_.recall_date, fifthDay).or()
            .equal(Patient_.recall_date, sixthDay).or()
            .equal(Patient_.recall_date, seventhDay).or()
            .equal(Patient_.recall_date, eightDay).or()
            .equal(Patient_.recall_date, ninthDay).build().find()
        allPatientRecall.addAll(thisWeekPatients)

        val rowRecallNextMonth = Patient()
        rowRecallNextMonth.first_name = "Recall Next Month"
        rowRecallNextMonth.content = "header"
        allPatientRecall.add(rowRecallNextMonth)

        setupAdapter(allPatientRecall)

    }

    private fun addOneDay(date: String): String{
        Log.d("Add one day to : ", date)
        var day = date.substring(8,10).toInt()
        var month = date.substring(5,7).toInt()
        var year = date.substring(0,4).toInt()
        if(day>30){
            if(month==12){
                year+=1
            }
            month= (month+1)%12
        }
        day = (day+1)%30
        return DecimalFormat("0000").format(year)+"-"+DecimalFormat("00").format(month)+"-"+DecimalFormat("00").format(day)
    }

    @AddTrace(name = "setupUIMainActivity", enabled = true /* optional */)
    private fun setupUI() {
        loading = findViewById(R.id.loading)
        recyclerView = findViewById(R.id.recyclerView)
        btnAddPatient = findViewById(R.id.btnAddNewPatient)
        fabBtnAddPatient = findViewById(R.id.fabAddPatient)
        fabBtnSync = findViewById(R.id.fabSync)

        tvLocation = findViewById(R.id.tvLocation)
        tvActivity = findViewById(R.id.tvActivity)
        tvName = findViewById(R.id.tvFullName)

        title = getString(R.string.dashboard)

        patientsBox = ObjectBox.boxStore.boxFor(Patient::class.java)
        patientsQuery = patientsBox.query().build()

        recallBox = ObjectBox.boxStore.boxFor(Recall::class.java)

        mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        dividerItemDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL)
        val divider = RecyclerViewItemSeparator(0)
        recyclerView.addItemDecoration(divider)

        btnAddPatient.setOnClickListener {
            addNewPatient()
        }
        fabBtnAddPatient.setOnClickListener {
            addNewPatient()
        }
        fabBtnSync.setOnClickListener {
            Log.d(TAG, "startSync")
            startService(Intent(this, SyncService::class.java))
            //Toast.makeText(context,"Work in progress", Toast.LENGTH_LONG).show()
        }

    }


    @AddTrace(name = "listPatientsMainActivity", enabled = true /* optional */)
    private fun listPatients() {
        listPatientsFromLocalDB()
    }

    @AddTrace(name = "listPatientsFromLocalDBMainActivity", enabled = true /* optional */)
    private fun listPatientsFromLocalDB() {
        Log.d(TAG, "listPatientsFromLocalDB()")
        try{
            allPatients =
                patientsBox.query().equal(Patient_.geography_id, DentalApp.geography_id).orderDesc(Patient_.id).build().find()
            setupAdapter(allPatients)
        }catch(e: DbException){
            Log.d("DBException", e.printStackTrace().toString())
        }

    }

    @AddTrace(name = "setupAdapterMainActivity", enabled = true /* optional */)
    private fun setupAdapter(patientList: List<Patient>) {
        patientAdapter =
            PatientAdapter(context, patientList, object : PatientAdapter.PatientClickListener {
                override fun onDelayPatientClick(patient: Patient) {
                    displayDelayDialog(patient)
                }

                override fun onCallPatientClick(patient: Patient) {
                    val call = Intent(Intent.ACTION_DIAL)
                    call.data = Uri.parse("tel:" + patient.phone)
                    startActivity(call)
                }

                override fun onViewPatientDetailClick(patient: Patient) {
                    val viewPatientIntent = Intent(context, ViewPatientActivity::class.java)
                    viewPatientIntent.putExtra("PATIENT_ID", patient.id)
                    startActivity(viewPatientIntent)
                }

            })
        recyclerView.adapter = patientAdapter
        patientAdapter.notifyDataSetChanged()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        if (DentalApp.activity_name == "Health Post") {
            listRecallPatients()
        } else {
            listPatients()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                startActivity(Intent(context, SearchPatientActivity::class.java))
            }

            R.id.logout -> {
                DentalApp.clearAuthDetails(context)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Goes to intent AddPatientActivity to Add New Patient
    private fun addNewPatient() {
        val addPatientActivityIntent = Intent(this, AddPatientActivity::class.java)
        addPatientActivityIntent.putExtra("ACTION", "new")
        startActivity(addPatientActivityIntent)
    }

    private fun displayDelayDialog(patient: Patient) {
        // delay recall of patient
        val grpName = arrayOf(
            "1 week",
            "2 weeks",
            "3 weeks",
            "1 month",
            "2 months",
            "3 months"
        )
        val delayChooser = androidx.appcompat.app.AlertDialog.Builder(this)
        delayChooser.setTitle(getString(R.string.delay))
        delayChooser.setSingleChoiceItems(
            grpName,
            -1,
            DialogInterface.OnClickListener { dialog, item ->
                loading.visibility = View.VISIBLE
                Log.d("DELAYED: ", patient.fullName() + " by " + grpName[item])
                val tempPatient = patientsBox.query().equal(Patient_.id,patient.id).build().findFirst()!!
                val calendar = Calendar.getInstance()
                try{
                    calendar.time = SimpleDateFormat("yyyy/MM/dd").parse(tempPatient.recall_date)
                }catch (e: ParseException){
                    Log.e("ParseException", e.printStackTrace().toString())
                }
                when(item){
                    0 -> {
                        calendar.add(Calendar.DAY_OF_MONTH,7)
                    }
                    1 -> {
                        calendar.add(Calendar.DAY_OF_MONTH,14)
                    }
                    2 -> {
                        calendar.add(Calendar.DAY_OF_MONTH,21)
                    }
                    3 -> {
                        calendar.add(Calendar.DAY_OF_MONTH,28)

                    }
                    4 -> {
                        calendar.add(Calendar.DAY_OF_MONTH,60)
                    }
                    5->{
                        calendar.add(Calendar.DAY_OF_MONTH,90)
                    }
                }

                val newDate = SimpleDateFormat("yyyy-mm-dd").format(calendar.time)
                tempPatient.recall_date = newDate
                patientsBox.put(tempPatient)
                listPatients()
                dialog.dismiss()// dismiss the alert box after chose option
            })
        val alert = delayChooser.create()
        alert.show()
    }

//    @AddTrace(name = "displaySearchDialogMainActivity", enabled = true /* optional */)
//    private fun displaySearchDialog() {
//        Log.d("TAG", "displaySearchDialog()")
//        val searchDialogView = LayoutInflater.from(this).inflate(R.layout.search_dialog, null)
//        val mBuilder =
//            AlertDialog.Builder(this).setView(searchDialogView).setTitle(getString(R.string.search))
//
//        mBuilder.show()
//    }


}
