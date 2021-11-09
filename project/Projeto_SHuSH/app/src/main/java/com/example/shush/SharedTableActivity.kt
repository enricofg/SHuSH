package com.example.shush

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SharedTableActivity: AppCompatActivity()  {
    lateinit var toggle: ActionBarDrawerToggle
    val repository = Firebase.firestore.collection("ruido")
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tables_shared)

        //nav menu init
        var drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var tableLayout: TableLayout = findViewById(R.id.tableLayout)
        init(tableLayout)

        var navView: NavigationView = findViewById(R.id.navView)
        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.miItemDashboard -> {
                    val intent = Intent(this@SharedTableActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.miItemMaps -> {
                    val intent = Intent(this@SharedTableActivity, MapsActivity::class.java)
                    startActivity(intent)
                }
                R.id.miItemGraphs -> {
                    val intent = Intent(this@SharedTableActivity, GraphsActivity::class.java)
                    startActivity(intent)
                }
                R.id.miItemTables -> {
                    Toast.makeText(this@SharedTableActivity,
                        "A página de tabelas já é a página atual",
                        Toast.LENGTH_SHORT).show()
                }
            }
            true
        }

        //page controls
        val localTableButton: Button = findViewById(R.id.buttonTabelaLocal)
        val sharedTableButton: Button = findViewById(R.id.buttonTabelaPartilhado)

        localTableButton.setOnClickListener {
            val intent = Intent(this@SharedTableActivity, LocalTableActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent)
        }
        sharedTableButton.setOnClickListener {
            Toast.makeText(this@SharedTableActivity,
                "A tabela de valores partilhados já está a ser exibida",
                Toast.LENGTH_SHORT).show()
        }

        val spinner:Spinner = findViewById(R.id.spinner)
        val filterOptions = ArrayList<String>()
        filterOptions.add("Sem filtro")
        filterOptions.add("Intervalo de datas")
        filterOptions.add("Texto livre")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)
        spinner.adapter = adapter


        val startDate: EditText = findViewById(R.id.editTextDataInicio)
        val endDate: EditText = findViewById(R.id.editTextDataFim)
        val keyword: EditText = findViewById(R.id.editTextPalavraChave)
        val filterTableByDateButton: Button = findViewById(R.id.buttonFiltrarDatas)
        val filterTableByKeywordButton: Button = findViewById(R.id.buttonFiltrarKeyword)

        disableEditText(startDate)
        disableEditText(endDate)
        disableEditText(keyword)
        disableButton(filterTableByDateButton)
        disableButton(filterTableByKeywordButton)

        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long,
            ) {
                when (filterOptions[position]) {
                    "Intervalo de datas" -> {
                        enableEditText(startDate)
                        enableEditText(endDate)
                        enableButton(filterTableByDateButton)
                        disableEditText(keyword)
                        disableButton(filterTableByKeywordButton)
                    }
                    "Texto livre" -> {
                        enableEditText(keyword)
                        enableButton(filterTableByKeywordButton)
                        disableEditText(startDate)
                        disableEditText(endDate)
                        disableButton(filterTableByDateButton)
                    }
                    else -> {
                        disableEditText(startDate)
                        disableEditText(endDate)
                        disableEditText(keyword)
                        disableButton(filterTableByDateButton)
                        disableButton(filterTableByKeywordButton)
                        init(tableLayout)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(this@SharedTableActivity,
                    "Selecione uma opção",
                    Toast.LENGTH_SHORT).show()
            }
        }

        startDate.setOnClickListener{datePicker(startDate)}
        endDate.setOnClickListener{datePicker(endDate)}
        filterTableByDateButton.setOnClickListener{filterDates(tableLayout, startDate, endDate)}
        filterTableByKeywordButton.setOnClickListener{filterKeywords(tableLayout, keyword)}
    }

    fun init(tableLayout: TableLayout) {
        var textInfoDados: TextView = findViewById(R.id.textViewInfoDados)
        var listaRuidos = ArrayList<RuidoFB>()

        repository.orderBy("date").get()
            .addOnSuccessListener { docsSnapshot ->
                for (docSnapshot in docsSnapshot.documents) {
                    val hashmap = docSnapshot.data
                    hashmap?.put("id", docSnapshot.id)
                    val Data = Gson().toJson(hashmap)
                    val docsData = Gson().fromJson(Data, RuidoFB::class.java)
                    listaRuidos.add(docsData)
                }

                resetTable(tableLayout)

                if(listaRuidos.isNotEmpty()){
                    buildTable(tableLayout, listaRuidos)
                    textInfoDados.text = ""
                } else{
                    Toast.makeText(this@SharedTableActivity,
                        "Não foram encontrados resultados para o período selecionado",
                        Toast.LENGTH_SHORT).show()
                    textInfoDados.text = "Não há informação para ser exibida na tabela."
                    tableLayout.setBackgroundResource(R.drawable.background)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    fun filterDates(tableLayout: TableLayout, startDate: EditText, endDate: EditText) {
        if (startDate.text.isEmpty()) {
            Toast.makeText(this, "Uma data de início não foi inserida", Toast.LENGTH_SHORT).show();
            return;
        }
        if (endDate.text.isEmpty()) {
            Toast.makeText(this, "Uma data de fim não foi inserida", Toast.LENGTH_SHORT).show();
            return;
        }

        var textInfoDados: TextView = findViewById(R.id.textViewInfoDados)
        var listaRuidos = ArrayList<RuidoFB>()
        var parsedStartDate : Date = sdf.parse(startDate.text.toString() + " 0:00")
        var parsedEndDate : Date = sdf.parse(endDate.text.toString() + " 23:59")

        repository
            .whereGreaterThanOrEqualTo("date", Timestamp(parsedStartDate))
            .whereLessThanOrEqualTo("date", Timestamp(parsedEndDate))
            .orderBy("date")
            .get()
            .addOnSuccessListener { docsSnapshot ->
                for (docSnapshot in docsSnapshot.documents) {
                    val hashmap = docSnapshot.data
                    hashmap?.put("id", docSnapshot.id)
                    val Data = Gson().toJson(hashmap)
                    val docsData = Gson().fromJson(Data, RuidoFB::class.java)
                    listaRuidos.add(docsData)
                }

                resetTable(tableLayout)

                if(listaRuidos.isNotEmpty()){
                    buildTable(tableLayout, listaRuidos)

                    var textInfoDados: TextView = findViewById(R.id.textViewInfoDados)
                    textInfoDados.text = ""
                }
                else{
                    Toast.makeText(this@SharedTableActivity,
                        "Não foram encontrados resultados para o período selecionado",
                        Toast.LENGTH_SHORT).show()
                    textInfoDados.text = "Não há informação para ser exibida na tabela."
                    tableLayout.setBackgroundResource(R.drawable.background)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    fun filterKeywords(tableLayout: TableLayout, keyword: EditText) {
        if (keyword.text.isEmpty()) {
            Toast.makeText(this, "Não foi inserido texto livre para iniciar a pesquisa", Toast.LENGTH_SHORT).show();
            return;
        }

        var textInfoDados: TextView = findViewById(R.id.textViewInfoDados)
        var listaRuidos = ArrayList<RuidoFB>()
        var searchTerm = keyword.text.toString()


        repository.orderBy("date").get()
            .addOnSuccessListener { docsSnapshot ->
                for (docSnapshot in docsSnapshot.documents) {
                    val hashmap = docSnapshot.data
                    hashmap?.put("id", docSnapshot.id)
                    val Data = Gson().toJson(hashmap)
                    val docsData = Gson().fromJson(Data, RuidoFB::class.java)
                    listaRuidos.add(docsData)
                }

                resetTable(tableLayout)
                if(listaRuidos.isNotEmpty()){
                    var filteredList = listaRuidos.filter { s -> s.date.toString().contains(searchTerm, ignoreCase = true) || s.value.toString().contains(searchTerm, ignoreCase = true) || s.latitude.toString().contains(searchTerm, ignoreCase = true) || s.longitude.toString().contains(searchTerm, ignoreCase = true)}

                    buildTable(tableLayout, filteredList)

                    if(filteredList.isNotEmpty()){
                        var textInfoDados: TextView = findViewById(R.id.textViewInfoDados)
                        textInfoDados.text = ""
                    } else{
                        textInfoDados.text = "Não há informação para ser exibida na tabela."
                    }
                }
                else{
                    Toast.makeText(this@SharedTableActivity,
                        "Não foram encontrados resultados para o período selecionado",
                        Toast.LENGTH_SHORT).show()
                    textInfoDados.text = "Não há informação para ser exibida na tabela."
                    tableLayout.setBackgroundResource(R.drawable.background)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    fun buildTable(tableLayout: TableLayout, list:List<RuidoFB>){
        for (item in list){
            val dateFormatted = sdf.format(item.date!!.toDate()).toString()

            val tbrow = TableRow(this)
            val t1v = TextView(this)
            formatCell(t1v, dateFormatted!!)
            tbrow.addView(t1v)
            val t2v = TextView(this)
            formatCell(t2v,item.value!!.toString())
            tbrow.addView(t2v)
            val t3v = TextView(this)
            formatCell(t3v, "%.4f".format(item.longitude!!))
            tbrow.addView(t3v)
            val t4v = TextView(this)
            formatCell(t4v, "%.4f".format(item.latitude!!))
            tbrow.addView(t4v)
            tableLayout.addView(tbrow)
        }
    }

    fun formatCell(textView: TextView, text:String){
        textView.text = text
        textView.setTextColor(Color.BLACK)
        textView.gravity = Gravity.CENTER
        textView.setBackgroundResource(R.drawable.background)
    }

    fun datePicker(text: EditText) {
        val cldr: Calendar = Calendar.getInstance()
        val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
        val month: Int = cldr.get(Calendar.MONTH)
        val year: Int = cldr.get(Calendar.YEAR)
        val  picker = DatePickerDialog(this@SharedTableActivity,
            { view, year, monthOfYear, dayOfMonth ->
                var aux = monthOfYear + 1
                val dia = if (dayOfMonth < 10) "0$dayOfMonth" else "" + dayOfMonth
                val mes = if (aux < 10) "0" + (aux) else "" + aux
                var message =
                    "$dia/$mes/$year"
                text.setText(message)
            },
            year,
            month,
            day)
        picker.show()
    }

    fun resetTable(tableLayout: TableLayout){
        val count: Int = tableLayout.getChildCount()
        for (i in 1 until count) {
            val child: View = tableLayout.getChildAt(i)
            if (child is TableRow) (child as ViewGroup).removeAllViews()
        }
    }

    fun enableEditText(editText: EditText){
        editText.visibility = View.VISIBLE
        editText.isClickable = true
    }

    fun disableEditText(editText: EditText){
        editText.visibility = View.INVISIBLE
        editText.isClickable = false
    }

    fun enableButton(button: Button){
        button.visibility = View.VISIBLE
        button.isClickable = true
    }

    fun disableButton(button: Button){
        button.visibility = View.INVISIBLE
        button.isClickable = false
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
}