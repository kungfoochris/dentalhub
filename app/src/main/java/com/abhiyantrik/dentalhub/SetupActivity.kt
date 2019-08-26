package com.abhiyantrik.dentalhub

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.abhiyantrik.dentalhub.entities.District_
import com.abhiyantrik.dentalhub.entities.Municipality
import com.abhiyantrik.dentalhub.entities.Municipality_
import com.abhiyantrik.dentalhub.entities.Ward
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.models.District
import io.objectbox.Box
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetupActivity : AppCompatActivity() {

    private val TAG = "SetupActivity"
    private lateinit var tvMessage: TextView

    private lateinit var districtsBox: Box<com.abhiyantrik.dentalhub.entities.District>
    private lateinit var municipalitiesBox: Box<Municipality>
    private lateinit var wardsBox: Box<Ward>
    var allDistricts = listOf<District>()

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        context = this
        initUI()
        loadData()
    }

    private fun loadData() {

        Log.d(TAG, "listAddressess()")
        tvMessage.text = tvMessage.text.toString() + "Loading addresses...\n"
        val panelService = DjangoInterface.create(this)
        val call = panelService.listAddresses()
        call.enqueue(object : Callback<List<District>> {
            override fun onFailure(call: Call<List<District>>, t: Throwable) {
                Log.d(TAG, "onFailure()")
                tvMessage.text = tvMessage.text.toString() + "Failed to load adresses\n"
                Log.d(TAG, t.toString())
            }

            override fun onResponse(call: Call<List<District>>, response: Response<List<District>>) {
                Log.d(TAG, "onResponse()")
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            allDistricts = response.body() as List<District>

                            for(district in allDistricts){
                                if(districtsBox.query().equal(District_.name,district.name).build().count() == 0.toLong()){
                                    val newDistrict = com.abhiyantrik.dentalhub.entities.District()
                                    newDistrict.remote_id = district.id
                                    newDistrict.name = district.name
                                    districtsBox.put(newDistrict)
                                }


                                for(municipality in district.municipalities){
                                    val dbDistrict = districtsBox.query().orderDesc(District_.id).build().findFirst()
                                    if(municipalitiesBox.query().equal(Municipality_.name,municipality.name).build().count() == 0.toLong()) {
                                        val newMunicipality = Municipality()
                                        newMunicipality.remote_id = municipality.id
                                        newMunicipality.name = municipality.name
                                        newMunicipality.district?.target = dbDistrict
                                        municipalitiesBox.put(newMunicipality)
                                        for(ward in municipality.wards){
                                            val dbMunicipality = municipalitiesBox.query().orderDesc(Municipality_.id).build().findFirst()
                                            val newWard = Ward()
                                            newWard.remote_id = ward.id
                                            newWard.ward = ward.ward
                                            newWard.name = dbMunicipality!!.name+"-"+ward.ward.toString()+", "+dbDistrict!!.name
                                            newWard.municipality?.target = dbMunicipality
                                            wardsBox.put(newWard)
                                        }
                                    }
                                }
                            }
                            tvMessage.text = tvMessage.text.toString() + "Loading address complete\n"
                            DentalApp.saveToPreference(context,Constants.PREF_SETUP_COMPLETE,"true")
                            startActivity(Intent(context, LocationSelectorActivity::class.java))
                            finish()
                        }
                    }
                }else{
                    Log.d(TAG,response.code().toString())
                }
            }

        })

    }

    private fun initUI() {
        tvMessage = findViewById(R.id.tvMessage)

        districtsBox = ObjectBox.boxStore.boxFor(com.abhiyantrik.dentalhub.entities.District::class.java)
        municipalitiesBox = ObjectBox.boxStore.boxFor(Municipality::class.java)
        wardsBox= ObjectBox.boxStore.boxFor(Ward::class.java)

    }
}
