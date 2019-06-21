package com.example.dentalhub

import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.util.Log
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import com.example.dentalhub.models.FormField


class MainActivity : AppCompatActivity() {
    private lateinit var mainLayout: LinearLayout

    private var fields = mutableListOf<FormField>()
    var widgetList = mutableListOf<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        readFormConfiguration();
        setupUI()

    }

    private fun readFormConfiguration() {
        fields.add(FormField("email","Email","email","Enter email address", null))
        fields.add(FormField("password","Password","password","Enter password", null))
        fields.add(FormField("string","Name","name","Enter name", null))
        fields.add(FormField("string","Address","address","Enter address", null))
        fields.add(FormField("number","Age","age","Enter age", null))
        fields.add(FormField("label","Label","label","Some dummy text for label.", null))
        fields.add(FormField("select","Gender","gener","Select gender", listOf("Male", "Female")))

    }

    private fun setupUI() {
        mainLayout = findViewById(R.id.mainActivityLayout)

        for(field in fields){
            when(field.fieldType){
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
                    fieldControl.setPadding(8,8,8,8)
                    widgetList.add(fieldControl)
                    mainLayout.addView(fieldControl)
                }
            }
        }
        var submitButton = Button(this)
        submitButton.text = getString(R.string.submit)
        mainLayout.addView(submitButton)

        submitButton.setOnClickListener {
            for(widget in widgetList){
                if(widget is EditText){
                    Log.d("EditText : ", widget.text.toString())
                }else if(widget is Spinner){
                    Log.d("spiner : ", widget.selectedItem.toString())
                }
            }
        }
    }

    private fun createArrayAdapter(values: List<String>?): SpinnerAdapter? {
        var dataAdapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, values!!.toMutableList())
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return dataAdapter
    }
}
