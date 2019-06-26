package com.example.dentalhub

import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.dentalhub.models.FormField

class AddPatientDynamicActivity : AppCompatActivity(){


    private lateinit var mainLayout: LinearLayout

    private var fields = mutableListOf<FormField>()
    var widgetList = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient_dynamic)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.add_patient)

        readFormConfiguration();
        setupUI()

    }

    private fun readFormConfiguration() {
        fields.add(FormField("email", "Email", "email", "Enter email address", null))
        fields.add(FormField("password", "Password", "password", "Enter password", null))
        fields.add(FormField("string", "Name", "name", "Enter name", null))
        fields.add(FormField("string", "Address", "address", "Enter address", null))
        fields.add(FormField("number", "Age", "age", "Enter age", null))
        fields.add(FormField("label", "Label", "label", "Some dummy text for label.", null))
        fields.add(FormField("select", "Gender", "gender", "Select gender", listOf("Male", "Female")))
        fields.add(FormField("checkbox", "Accept", "accept", "I accept", null))
        fields.add(FormField("seekbar", "Range", "range", "I accept", listOf("20")))
        fields.add(FormField("calendar", "Select Date", "date", "Select date",null))
        fields.add(FormField("ratingbar", "Rating", "rating", "Rating",listOf("5","1","0")))

    }

    private fun setupUI() {
        mainLayout = findViewById(R.id.mainActivityLayout)

        for (field in fields) {
            when (field.fieldType) {
                "string" -> {
                    var fieldControl = EditText(this)
                    fieldControl.hint = field.fieldHint
                    widgetList.add(fieldControl)
                    mainLayout.addView(fieldControl)
                }
                "number" -> {
                    var fieldControl = EditText(this)
                    fieldControl.hint = field.fieldHint
                    fieldControl.inputType = InputType.TYPE_CLASS_NUMBER
                    widgetList.add(fieldControl)
                    mainLayout.addView(fieldControl)
                }
                "email" -> {
                    var fieldControl = EditText(this)
                    fieldControl.hint = field.fieldHint
                    fieldControl.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    widgetList.add(fieldControl)
                    mainLayout.addView(fieldControl)
                }
                "password" -> {
                    var fieldControl = EditText(this)
                    fieldControl.hint = field.fieldHint
                    fieldControl.transformationMethod = PasswordTransformationMethod.getInstance()
                    widgetList.add(fieldControl)
                    mainLayout.addView(fieldControl)
                }
                "label" -> {
                    var fieldControl = TextView(this)
                    fieldControl.text = field.fieldHint
                    mainLayout.addView(fieldControl)
                }
                "select" -> {
                    var fieldControl = Spinner(this)
                    fieldControl.prompt = field.fieldHint
                    fieldControl.adapter = createArrayAdapter(field.values)
                    fieldControl.setPadding(8, 8, 8, 8)
                    widgetList.add(fieldControl)
                    mainLayout.addView(fieldControl)
                }
                "checkbox" -> {
                    var fieldControl = CheckBox(this)
                    fieldControl.text = field.fieldHint
                    widgetList.add(fieldControl)
                    mainLayout.addView(fieldControl)
                }
                "seekbar" -> {
                    var fieldControl = SeekBar(this)
                    fieldControl.max = Integer.parseInt(field.values!![0])
                    widgetList.add(fieldControl)
                    mainLayout.addView(fieldControl)
                }
                "calendar" -> {
                    var fieldControl = CalendarView(this)
                    widgetList.add(fieldControl)
                    mainLayout.addView(fieldControl)
                }
                "ratingbar" -> {
                    var fieldControl = RatingBar(this)
                    fieldControl.numStars = Integer.parseInt(field.values!![0])
                    fieldControl.stepSize = field.values!![1].toFloat()
                    fieldControl.rating = field.values!![2].toFloat()
                    widgetList.add(fieldControl)
                    mainLayout.addView(fieldControl)
                }
            }
        }
        var submitButton = Button(this)
        submitButton.text = getString(R.string.submit)
        mainLayout.addView(submitButton)

        submitButton.setOnClickListener {
            for (widget in widgetList) {
                if (widget is EditText) {
                    Log.d("EditText : ", widget.text.toString())
                } else if (widget is Spinner) {
                    Log.d("spiner : ", widget.selectedItem.toString())
                } else if (widget is CheckBox) {
                    Log.d("checkbox", widget.isChecked.toString())
                } else if(widget is SeekBar){
                    Log.d("seekbar: ", widget.progress.toString())
                } else if(widget is CalendarView){
                    Log.d("calendar: ", widget.date.toString())
                } else if(widget is RatingBar){
                    Log.d("ratingbar: ", widget.rating.toString())
                }
            }
        }
    }

    private fun createArrayAdapter(values: List<String>?): SpinnerAdapter? {
        var dataAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values!!.toMutableList())
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return dataAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}
