package com.example.shush

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.shush.baseDados.RuidoViewModel
import com.example.shush.baseDeDados.RuidoBD
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    //nav menu config
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mRuidoViewModel: RuidoViewModel
    private val PLACE_PICKER_REQUEST = 3
    private val marks = mutableListOf<Marker>()
    val repository = Firebase.firestore.collection("ruido")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val editTextDate:EditText = findViewById(R.id.editTextDate)
        val editTextDate2:EditText = findViewById(R.id.editTextDate2)
        val editTextLocalizacao:EditText = findViewById(R.id.editTextLocalizacao)
        val filterOptions = resources.getStringArray(R.array.Filter)
        val spinner:Spinner = findViewById(R.id.spinner)
        val btnPartilhado:Button = findViewById(R.id.buttonPartilhado)
        val btnLocal:Button = findViewById(R.id.buttonLocal)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)
        editTextDate2.visibility = View.INVISIBLE
        editTextDate2.isClickable = false

        editTextLocalizacao.visibility = View.INVISIBLE
        editTextLocalizacao.isClickable = false
        spinner.adapter = adapter
        mRuidoViewModel = ViewModelProvider(this).get(RuidoViewModel::class.java)

        //nav menu init
        var drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        var navView: NavigationView = findViewById(R.id.navView)
        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.miItemDashboard -> {
                    val intent = Intent(this@MapsActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.miItemMaps -> {
                    /*val intent = Intent(this@MapsActivity, MapsActivity::class.java)
                    startActivity(intent)*/
                    Toast.makeText(this@MapsActivity,
                        "A página de mapas já é a página atual",
                        Toast.LENGTH_SHORT).show()
                }
                R.id.miItemGraphs -> {
                    val intent = Intent(this@MapsActivity, GraphsActivity::class.java)
                    startActivity(intent)
                }
                R.id.miItemTables -> {
                    val intent = Intent(this@MapsActivity, LocalTableActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long,
            ) {
                when(filterOptions[position]){
                    "Filtro entre datas" -> {
                        editTextDate2.visibility = View.VISIBLE
                        editTextDate2.isClickable = true
                        editTextDate.visibility = View.VISIBLE
                        editTextDate.isClickable = true
                        editTextLocalizacao.visibility = View.INVISIBLE
                        editTextLocalizacao.isClickable = false
                        editTextDate.setText("")
                        editTextDate2.setText("")
                        editTextLocalizacao.setText("")
                    }
                    "Filtro por data" -> {
                        editTextDate.visibility = View.VISIBLE
                        editTextDate.isClickable = true
                        editTextLocalizacao.visibility = View.INVISIBLE
                        editTextLocalizacao.isClickable = false
                        editTextDate2.visibility = View.INVISIBLE
                        editTextDate2.isClickable = false
                        editTextDate.setText("")
                        editTextDate2.setText("")
                        editTextLocalizacao.setText("")
                    }
                    "Filtro por localização" -> {
                        editTextLocalizacao.visibility = View.VISIBLE
                        editTextLocalizacao.isClickable = true
                        editTextDate.visibility = View.INVISIBLE
                        editTextDate.isClickable = false
                        editTextDate2.visibility = View.INVISIBLE
                        editTextDate2.isClickable = false
                        editTextDate.setText("")
                        editTextDate2.setText("")
                        editTextLocalizacao.setText("")
                    }
                    else -> {
                        editTextDate.visibility = View.INVISIBLE
                        editTextDate.isClickable = false
                        editTextDate2.visibility = View.INVISIBLE
                        editTextDate2.isClickable = false
                        editTextLocalizacao.visibility = View.INVISIBLE
                        editTextLocalizacao.isClickable = false
                        editTextDate.setText("")
                        editTextDate2.setText("")
                        editTextLocalizacao.setText("")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(this@MapsActivity,
                    "Por Favor seleciona uma opção",
                    Toast.LENGTH_SHORT).show()
            }


        }

        btnLocal.setOnClickListener{
            var selectedFilterOption = spinner.selectedItem


            when(selectedFilterOption){
                "Filtro entre datas" -> {
                    var data = editTextDate.text.toString()
                    var data2 = editTextDate2.text.toString()
                    if (data.isEmpty() || data2.isEmpty()) {
                        Toast.makeText(this@MapsActivity,
                            "Insira datas para serem efectuadas as pesquisas",
                            Toast.LENGTH_LONG).show()
                    } else {
                        getLocalDataBetweenDates(data, data2)
                    }

                }
                "Filtro por data" -> {
                    var data = editTextDate.text.toString()

                    if (data.isEmpty()) {
                        Toast.makeText(this@MapsActivity,
                            "Insira data para serem efectuadas as pesquisas",
                            Toast.LENGTH_LONG).show()
                    } else {
                        getLocalDataByDate(data)
                    }
                }
                "Filtro por localização" -> {
                    var localizacao = editTextLocalizacao.text.toString()
                    if (localizacao.isEmpty()) {
                        Toast.makeText(this@MapsActivity,
                            "Insira localização para serem efectuadas as pesquisas",
                            Toast.LENGTH_LONG).show()
                    } else {
                        getLocalDataByLocation(localizacao)
                    }
                }
                else -> {
                    getLocalData()
                }
            }
        }

        btnPartilhado.setOnClickListener{
            var selectedFilterOption = spinner.selectedItem


            when(selectedFilterOption){
                "Filtro entre datas" -> {
                    var data = editTextDate.text.toString()
                    var data2 = editTextDate2.text.toString()
                    if (data.isEmpty() || data2.isEmpty()) {
                        Toast.makeText(this@MapsActivity,
                            "Insira datas para serem efectuadas as pesquisas",
                            Toast.LENGTH_LONG).show()
                    } else {
                        getLocalDataBetweenDatesPartilhado(data, data2)
                    }

                }
                "Filtro por data" -> {
                    var data = editTextDate.text.toString()

                    if (data.isEmpty()) {
                        Toast.makeText(this@MapsActivity,
                            "Insira data para serem efectuadas as pesquisas",
                            Toast.LENGTH_LONG).show()
                    } else {
                        getLocalDataByDatePartilhado(data)
                    }
                }
                "Filtro por localização" -> {
                    var localizacao = editTextLocalizacao.text.toString()
                    if (localizacao.isEmpty()) {
                        Toast.makeText(this@MapsActivity,
                            "Insira localização para serem efectuadas as pesquisas",
                            Toast.LENGTH_LONG).show()
                    } else {
                        getLocalDataByLocationPartilhado(localizacao)
                    }
                }
                else -> {
                    getLocalDataPartilhado()
                }
            }
        }

        editTextDate.setOnClickListener{

            val cldr: Calendar = Calendar.getInstance()
            val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            val month: Int = cldr.get(Calendar.MONTH)
            val year: Int = cldr.get(Calendar.YEAR)
            // date picker dialog
            // date picker dialog
            val  picker:DatePickerDialog = DatePickerDialog(this@MapsActivity,
                { view, year, monthOfYear, dayOfMonth ->
                    var aux = monthOfYear + 1
                    val dia = if (dayOfMonth < 10) "0$dayOfMonth" else "" + dayOfMonth
                    val mes = if (aux < 10) "0" + (aux) else "" + aux
                    var message =
                        "$dia/$mes/$year"
                    editTextDate.setText(message)
                },
                year,
                month,
                day)
            picker.show()
        }

        editTextDate2.setOnClickListener{

            val cldr: Calendar = Calendar.getInstance()
            val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
            val month: Int = cldr.get(Calendar.MONTH)
            val year: Int = cldr.get(Calendar.YEAR)
            // date picker dialog
            // date picker dialog
            val  picker:DatePickerDialog = DatePickerDialog(this@MapsActivity,
                { view, year, monthOfYear, dayOfMonth ->
                    var aux = monthOfYear + 1
                    val dia = if (dayOfMonth < 10) "0$dayOfMonth" else "" + dayOfMonth
                    val mes = if (aux < 10) "0" + (aux) else "" + aux

                    var text =
                        "$dia/$mes/$year"
                    editTextDate2.setText(text)
                },
                year,
                month,
                day)
            picker.show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLocalDataBetweenDates(data: String, data2: String) {
        try {
            var dataAux = data.split("/")[2]+ "-"+data.split("/")[1]+ "-"+data.split("/")[0]
            var dataAux2 = data2.split("/")[2]+ "-"+data2.split("/")[1]+ "-"+data2.split("/")[0]
            var date:LocalDate = LocalDate.parse(dataAux)
            var date2:LocalDate = LocalDate.parse(dataAux2)


            if(date.isBefore(date2)){
                //correct
                mMap.clear()
                var ruidosArray = mutableListOf<RuidoBD>()
                var hashMap : HashMap<String, MutableList<Int>>
                        = HashMap<String, MutableList<Int>> ()
                mRuidoViewModel.readAllData.observe(this, Observer { ruidos ->
                    if (ruidos.isNotEmpty()) {
                        for (ruido in ruidos) {
                            var dateAux = ruido.date!!.split(" ")[0]
                            var dataAuxiliar =
                                dateAux.split("/")[2] + "-" + dateAux.split("/")[1] + "-" + dateAux.split(
                                    "/")[0]
                            var ruidoDate: LocalDate = LocalDate.parse(dataAuxiliar)

                            if (ruidoDate.isBefore(date2) && ruidoDate.isAfter(date)) {
                                ruidosArray.add(ruido)
                            } else {
                                if (ruidoDate == date || ruidoDate == date2) {
                                    ruidosArray.add(ruido)
                                }
                            }
                        }
                        if (ruidosArray.size == 0) {
                            Toast.makeText(this@MapsActivity,
                                "Nao existem ruidos para serem mostrados",
                                Toast.LENGTH_LONG).show()
                        }
                        for (ruido in ruidosArray) {
                            var key = ruido.latitude.toString() + ";" + ruido.longitude.toString()
                            if (hashMap.containsKey(key)) {
                                var values = hashMap[key]
                                values!!.add(ruido.value!!)
                                hashMap[key] = values
                            } else {
                                var values = mutableListOf<Int>()
                                values.add(ruido.value!!)
                                hashMap[key] = values
                            }
                        }

                        for ((key, value) in hashMap) {
                            var latitude = key.split(";")[0]
                            var longitude = key.split(";")[1]
                            val mark = LatLng(latitude.toDouble(), longitude.toDouble())
                            var media = 0.0

                            for (v in value) {
                                media += v
                            }
                            media /= value.size
                            var text = ""
                            if (value.size > 1) {
                                text = "Média: " + media + " de " + value.size + " leituras"
                            } else {
                                text = "Ruido: " + media
                            }
                            var marker: Marker = mMap.addMarker(MarkerOptions().position(mark)
                                .title(
                                    text))
                            marks.add(marker)
                        }
                    }
                })
            }else{
                Toast.makeText(this@MapsActivity,
                    "A segunda data devera ser superior a primeira data",
                    Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            println("Error:" + e.message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLocalDataBetweenDatesPartilhado(data: String, data2: String) {
        try {
            var dataAux = data.split("/")[2]+ "-"+data.split("/")[1]+ "-"+data.split("/")[0]
            var dataAux2 = data2.split("/")[2]+ "-"+data2.split("/")[1]+ "-"+data2.split("/")[0]
            var date:LocalDate = LocalDate.parse(dataAux)
            var date2:LocalDate = LocalDate.parse(dataAux2)


            if(date.isBefore(date2)){
                //correct
                mMap.clear()
                var ruidosArray = mutableListOf<RuidoFB>()
                var listaRuidos = ArrayList<RuidoFB>()
                var hashMap : HashMap<String, MutableList<Int>>
                        = HashMap<String, MutableList<Int>> ()

                repository.orderBy("date").get()
                    .addOnSuccessListener { docsSnapshot ->
                        for (docSnapshot in docsSnapshot.documents) {
                            val hashmap = docSnapshot.data
                            hashmap?.put("id", docSnapshot.id)
                            val Data = Gson().toJson(hashmap)
                            val docsData = Gson().fromJson(Data, RuidoFB::class.java)
                            listaRuidos.add(docsData)
                        }

                        if(listaRuidos.isNotEmpty()){
                            for (item in listaRuidos){
                                val dateFormatted = item.date!!.toDate()
                                var ruidoDate:LocalDate = convertToLocalDateViaInstant(dateFormatted)!!
                                if(ruidoDate.isBefore(date2) && ruidoDate.isAfter(date)){
                                    ruidosArray.add(item)
                                }else{
                                    if(ruidoDate == date || ruidoDate == date2){
                                        ruidosArray.add(item)
                                    }
                                }
                            }
                        }

                        if(ruidosArray.size == 0){
                            Toast.makeText(this@MapsActivity, "Nao existem ruidos para serem mostrados", Toast.LENGTH_LONG).show()
                        }

                        for(ruido in ruidosArray){
                            var key = ruido.latitude.toString() + ";" + ruido.longitude.toString()
                            if(hashMap.containsKey(key)){
                                var values = hashMap[key]
                                values!!.add(ruido.value!!)
                                hashMap[key] = values
                            }else{
                                var values = mutableListOf<Int>()
                                values.add(ruido.value!!)
                                hashMap[key] = values
                            }
                        }

                        for((key,value) in hashMap){
                            var latitude = key.split(";")[0]
                            var longitude = key.split(";")[1]
                            val mark = LatLng(latitude.toDouble(), longitude.toDouble())
                            var media = 0.0

                            for(v in value){
                                media += v
                            }
                            media /= value.size
                            var text = ""
                            if(value.size > 1){
                                text = "Média: " + media +" de "+value.size +" leituras"
                            }else {
                                text = "Ruido: " + media
                            }
                            var marker: Marker = mMap.addMarker(MarkerOptions().position(mark)
                                .title(
                                    text))
                            marks.add(marker)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(ContentValues.TAG, "Error getting documents.", exception)
                    }
            }else{
                Toast.makeText(this@MapsActivity,
                    "A segunda data devera ser superior a primeira data",
                    Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            println("Error:" + e.message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToLocalDateViaInstant(dateToConvert: Date): LocalDate? {
        return dateToConvert.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    private fun getLocalDataByDate(data: String) {
        mMap.clear()
        var ruidosArray = mutableListOf<RuidoBD>()
        var hashMap : HashMap<String, MutableList<Int>>
                = HashMap<String, MutableList<Int>> ()
        mRuidoViewModel.readAllData.observe(this, Observer { ruidos ->
            if (ruidos.isNotEmpty()) {
                for (ruido in ruidos) {
                    if (ruido.date!!.split(" ")[0] == data) {
                        ruidosArray.add(ruido)
                    }
                }
                if (ruidosArray.size == 0) {
                    Toast.makeText(this@MapsActivity,
                        "Nao existem ruidos para serem mostrados",
                        Toast.LENGTH_LONG).show()
                }
                for (ruido in ruidosArray) {
                    var key = ruido.latitude.toString() + ";" + ruido.longitude.toString()
                    if (hashMap.containsKey(key)) {
                        var values = hashMap[key]
                        values!!.add(ruido.value!!)
                        hashMap[key] = values
                    } else {
                        var values = mutableListOf<Int>()
                        values.add(ruido.value!!)
                        hashMap[key] = values
                    }
                }

                for ((key, value) in hashMap) {
                    var latitude = key.split(";")[0]
                    var longitude = key.split(";")[1]
                    val mark = LatLng(latitude.toDouble(), longitude.toDouble())
                    var media = 0.0

                    for (v in value) {
                        media += v
                    }
                    media /= value.size
                    var text = ""
                    if (value.size > 1) {
                        text = "Média: " + media + " de " + value.size + " leituras"
                    } else {
                        text = "Ruido: " + media
                    }
                    var marker: Marker = mMap.addMarker(MarkerOptions().position(mark)
                        .title(
                            text))
                    marks.add(marker)
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLocalDataByDatePartilhado(data: String) {
        mMap.clear()
        var listaRuidos = ArrayList<RuidoFB>()
        var ruidosArray = mutableListOf<RuidoFB>()
        var hashMap : HashMap<String, MutableList<Int>>
                = HashMap<String, MutableList<Int>> ()
        var dataAux = data.split("/")[2]+ "-"+data.split("/")[1]+ "-"+data.split("/")[0]
        var date:LocalDate = LocalDate.parse(dataAux)

        repository.orderBy("date").get()
            .addOnSuccessListener { docsSnapshot ->
                for (docSnapshot in docsSnapshot.documents) {
                    val hashmap = docSnapshot.data
                    hashmap?.put("id", docSnapshot.id)
                    val Data = Gson().toJson(hashmap)
                    val docsData = Gson().fromJson(Data, RuidoFB::class.java)
                    listaRuidos.add(docsData)
                }

                if(listaRuidos.isNotEmpty()){
                    for (item in listaRuidos){
                        val dateFormatted = item.date!!.toDate()
                        var ruidoDate:LocalDate = convertToLocalDateViaInstant(dateFormatted)!!
                        if(ruidoDate == date){
                            ruidosArray.add(item)
                        }
                    }
                }

                if(ruidosArray.size == 0){
                    Toast.makeText(this@MapsActivity, "Nao existem ruidos para serem mostrados", Toast.LENGTH_LONG).show()
                }

                for(ruido in ruidosArray){
                    var key = ruido.latitude.toString() + ";" + ruido.longitude.toString()
                    if(hashMap.containsKey(key)){
                        var values = hashMap[key]
                        values!!.add(ruido.value!!)
                        hashMap[key] = values
                    }else{
                        var values = mutableListOf<Int>()
                        values.add(ruido.value!!)
                        hashMap[key] = values
                    }
                }

                for((key,value) in hashMap){
                    var latitude = key.split(";")[0]
                    var longitude = key.split(";")[1]
                    val mark = LatLng(latitude.toDouble(), longitude.toDouble())
                    var media = 0.0

                    for(v in value){
                        media += v
                    }
                    media /= value.size
                    var text = ""
                    if(value.size > 1){
                        text = "Média: " + media +" de "+value.size +" leituras"
                    }else {
                        text = "Ruido: " + media
                    }
                    var marker: Marker = mMap.addMarker(MarkerOptions().position(mark)
                        .title(
                            text))
                    marks.add(marker)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }

    }

    private fun getLocalDataByLocation(localizacao: String){
        mMap.clear()
        var ruidosArray = mutableListOf<RuidoBD>()
        var hashMap : HashMap<String, MutableList<Int>>
                = HashMap<String, MutableList<Int>> ()
        mRuidoViewModel.readAllData.observe(this, Observer { ruidos ->
            if (ruidos.isNotEmpty()) {
                for (ruido in ruidos) {
                    if (ruido.localizacao.toLowerCase() == localizacao.toLowerCase()) {
                        ruidosArray.add(ruido)
                    }
                }

                if (ruidosArray.size == 0) {
                    Toast.makeText(this@MapsActivity,
                        "Nao existem ruidos para serem mostrados",
                        Toast.LENGTH_LONG).show()
                }

                for (ruido in ruidosArray) {
                    var key = ruido.latitude.toString() + ";" + ruido.longitude.toString()
                    if (hashMap.containsKey(key)) {
                        var values = hashMap[key]
                        values!!.add(ruido.value!!)
                        hashMap[key] = values
                    } else {
                        var values = mutableListOf<Int>()
                        values.add(ruido.value!!)
                        hashMap[key] = values
                    }
                }

                for ((key, value) in hashMap) {
                    var latitude = key.split(";")[0]
                    var longitude = key.split(";")[1]
                    val mark = LatLng(latitude.toDouble(), longitude.toDouble())
                    var media = 0.0

                    for (v in value) {
                        media += v
                    }
                    media /= value.size
                    var text = ""
                    if (value.size > 1) {
                        text = "Média: " + media + " de " + value.size + " leituras"
                    } else {
                        text = "Ruido: " + media
                    }
                    var marker: Marker = mMap.addMarker(MarkerOptions().position(mark)
                        .title(
                            text))
                    marks.add(marker)
                }
            }
        })
    }

    private fun getLocalDataByLocationPartilhado(localizacao: String){
        mMap.clear()
        var listaRuidos = ArrayList<RuidoFB>()
        var ruidosArray = mutableListOf<RuidoFB>()
        var hashMap : HashMap<String, MutableList<Int>>
                = HashMap<String, MutableList<Int>> ()
        var gcd: Geocoder? = Geocoder(baseContext,Locale.getDefault())
        var addresses = mutableListOf<Address>()
        var local:String = ""

        repository.orderBy("date").get()
            .addOnSuccessListener { docsSnapshot ->
                for (docSnapshot in docsSnapshot.documents) {
                    val hashmap = docSnapshot.data
                    hashmap?.put("id", docSnapshot.id)
                    val Data = Gson().toJson(hashmap)
                    val docsData = Gson().fromJson(Data, RuidoFB::class.java)
                    listaRuidos.add(docsData)
                }

                if(listaRuidos.isNotEmpty()){
                    for (item in listaRuidos){
                        try {
                            addresses = gcd!!.getFromLocation(item.latitude, item.longitude, 1)//ir buscar o nome da localizacao consoante a coordenada
                            if (addresses.size > 0) {
                                local = addresses[0].locality
                            }

                            if(localizacao.toLowerCase() == local.toLowerCase()){
                                ruidosArray.add(item)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                if(ruidosArray.size == 0){
                    Toast.makeText(this@MapsActivity, "Nao existem ruidos para serem mostrados", Toast.LENGTH_LONG).show()
                }

                for(ruido in ruidosArray){
                    var key = ruido.latitude.toString() + ";" + ruido.longitude.toString()
                    if(hashMap.containsKey(key)){
                        var values = hashMap[key]
                        values!!.add(ruido.value!!)
                        hashMap[key] = values
                    }else{
                        var values = mutableListOf<Int>()
                        values.add(ruido.value!!)
                        hashMap[key] = values
                    }
                }

                for((key,value) in hashMap){
                    var latitude = key.split(";")[0]
                    var longitude = key.split(";")[1]
                    val mark = LatLng(latitude.toDouble(), longitude.toDouble())
                    var media = 0.0

                    for(v in value){
                        media += v
                    }
                    media /= value.size
                    var text = ""
                    if(value.size > 1){
                        text = "Média: " + media +" de "+value.size +" leituras"
                    }else {
                        text = "Ruido: " + media
                    }
                    var marker: Marker = mMap.addMarker(MarkerOptions().position(mark)
                        .title(
                            text))
                    marks.add(marker)
                }

                if(ruidosArray.size == 0){
                    Toast.makeText(this@MapsActivity, "Nao existem ruidos para serem mostrados", Toast.LENGTH_LONG).show()
                }

                for(ruido in ruidosArray){
                    var key = ruido.latitude.toString() + ";" + ruido.longitude.toString()
                    if(hashMap.containsKey(key)){
                        var values = hashMap[key]
                        values!!.add(ruido.value!!)
                        hashMap[key] = values
                    }else{
                        var values = mutableListOf<Int>()
                        values.add(ruido.value!!)
                        hashMap[key] = values
                    }
                }

                for((key,value) in hashMap){
                    var latitude = key.split(";")[0]
                    var longitude = key.split(";")[1]
                    val mark = LatLng(latitude.toDouble(), longitude.toDouble())
                    var media = 0.0

                    for(v in value){
                        media += v
                    }
                    media /= value.size
                    var text = ""
                    if(value.size > 1){
                        text = "Média: " + media +" de "+value.size +" leituras"
                    }else {
                        text = "Ruido: " + media
                    }
                    var marker: Marker = mMap.addMarker(MarkerOptions().position(mark)
                        .title(
                            text))
                    marks.add(marker)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun getLocalData() {
        mMap.clear()
        var hashMap : HashMap<String, MutableList<Int>>
                = HashMap<String, MutableList<Int>> ()
        mRuidoViewModel.readAllData.observe(this, Observer { ruidos ->
            if (ruidos.isNotEmpty()) {
                for (ruido in ruidos) {
                    var key = ruido.latitude.toString() + ";" + ruido.longitude.toString()
                    if (hashMap.containsKey(key)) {
                        var values = hashMap[key]
                        values!!.add(ruido.value!!)
                        hashMap[key] = values
                    } else {
                        var values = mutableListOf<Int>()
                        values.add(ruido.value!!)
                        hashMap[key] = values
                    }
                }

                if (hashMap.size == 0) {
                    Toast.makeText(this@MapsActivity,
                        "Nao existem ruidos para serem mostrados",
                        Toast.LENGTH_LONG).show()
                }

                for ((key, value) in hashMap) {
                    var latitude = key.split(";")[0]
                    var longitude = key.split(";")[1]
                    val mark = LatLng(latitude.toDouble(), longitude.toDouble())
                    var media = 0.0

                    for (v in value) {
                        media += v
                    }
                    media /= value.size
                    var text = ""
                    if (value.size > 1) {
                        text = "Média: " + media + " de " + value.size + " leituras"
                    } else {
                        text = "Ruido: " + media
                    }
                    var marker: Marker = mMap.addMarker(MarkerOptions().position(mark)
                        .title(
                            text))
                    marks.add(marker)
                }
            }
        })
    }

    private fun getLocalDataPartilhado() {
        mMap.clear()
        var hashMap : HashMap<String, MutableList<Int>>
                = HashMap<String, MutableList<Int>> ()
        var listaRuidos = ArrayList<RuidoFB>()
        var ruidosArray = mutableListOf<RuidoFB>()

        repository.orderBy("date").get()
            .addOnSuccessListener { docsSnapshot ->
                for (docSnapshot in docsSnapshot.documents) {
                    val hashmap = docSnapshot.data
                    hashmap?.put("id", docSnapshot.id)
                    val Data = Gson().toJson(hashmap)
                    val docsData = Gson().fromJson(Data, RuidoFB::class.java)
                    listaRuidos.add(docsData)
                }

                if(listaRuidos.isNotEmpty()){
                    for (item in listaRuidos){
                        ruidosArray.add(item)
                    }
                }

                if(ruidosArray.size == 0){
                    Toast.makeText(this@MapsActivity, "Nao existem ruidos para serem mostrados", Toast.LENGTH_LONG).show()
                }

                for(ruido in ruidosArray){
                    var key = ruido.latitude.toString() + ";" + ruido.longitude.toString()
                    if(hashMap.containsKey(key)){
                        var values = hashMap[key]
                        values!!.add(ruido.value!!)
                        hashMap[key] = values
                    }else{
                        var values = mutableListOf<Int>()
                        values.add(ruido.value!!)
                        hashMap[key] = values
                    }
                }

                for((key,value) in hashMap){
                    var latitude = key.split(";")[0]
                    var longitude = key.split(";")[1]
                    val mark = LatLng(latitude.toDouble(), longitude.toDouble())
                    var media = 0.0

                    for(v in value){
                        media += v
                    }
                    media /= value.size
                    var text = ""
                    if(value.size > 1){
                        text = "Média: " + media +" de "+value.size +" leituras"
                    }else {
                        text = "Ruido: " + media
                    }
                    var marker: Marker = mMap.addMarker(MarkerOptions().position(mark)
                        .title(
                            text))
                    marks.add(marker)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        return;
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marker: Marker):Boolean{
        return false;
    }

}