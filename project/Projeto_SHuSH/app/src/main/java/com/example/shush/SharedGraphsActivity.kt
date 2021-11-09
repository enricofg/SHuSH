package com.example.shush

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SharedGraphsActivity : AppCompatActivity() {
    //nav menu config
    lateinit var toggle: ActionBarDrawerToggle
    val repository = Firebase.firestore.collection("ruido")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphs_shared)
        var barChart: BarChart = findViewById(R.id.barchart)
        barChart.setNoDataText("Não há informação gravada para ser exibida no gráfico.");
        initGraph(barChart)

        //nav menu init
        var drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var navView: NavigationView = findViewById(R.id.navView)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.miItemDashboard -> {
                    val intent = Intent(this@SharedGraphsActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.miItemMaps -> {
                    val intent = Intent(this@SharedGraphsActivity, MapsActivity::class.java)
                    startActivity(intent)
                }
                R.id.miItemGraphs -> {
                    Toast.makeText(this@SharedGraphsActivity,
                        "A página de gráficos já é a página atual",
                        Toast.LENGTH_SHORT).show()
                }
                R.id.miItemTables -> {
                    val intent = Intent(this@SharedGraphsActivity, LocalTableActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        var localGraphButton: Button = findViewById(R.id.buttonTabelaLocal)
        var sharedGraphButton: Button = findViewById(R.id.buttonTabelaPartilhado)

        localGraphButton.setOnClickListener {
            val intent = Intent(this@SharedGraphsActivity, GraphsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent)
        }
        sharedGraphButton.setOnClickListener {
            Toast.makeText(this@SharedGraphsActivity,
                "O gráfico de valores partilhados já está a ser exibido",
                Toast.LENGTH_SHORT).show()
        }

        val filterGraphbutton: Button = findViewById(R.id.buttonFiltrarDatas)
        val startDate: EditText = findViewById(R.id.editTextDataInicio)
        val endDate: EditText = findViewById(R.id.editTextDataFim)

        startDate.setOnClickListener{datePicker(startDate)}
        endDate.setOnClickListener{datePicker(endDate)}
        filterGraphbutton.setOnClickListener{filterDates(barChart, startDate, endDate)}
    }

    private fun initGraph(barChart: BarChart) {
        var readValues = ArrayList<BarEntry>()
        var dates = ArrayList<String>()
        var listaRuidos = ArrayList<RuidoFB>()
        val sdf = SimpleDateFormat("dd/MM/yyyy")

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
                    var i = 0
                    for (item in listaRuidos){
                        readValues.add(BarEntry(item.value!!.toFloat(), i))
                        val dateFormatted = item.date!!.toDate()
                        dates.add(sdf.format(dateFormatted!!).toString())
                        i++
                    }

                    val mybardataset = MyBarDataSet(readValues, "Valor lido")
                    mybardataset.setColors(intArrayOf(Color.GREEN, Color.YELLOW, Color.RED))
                    val bardataset = ArrayList<BarDataSet>()
                    bardataset.add(mybardataset)

                    barChart.animateY(1000)
                    val data = BarData(dates, bardataset as List<IBarDataSet>?)
                    barChart.setDescription("Valores lidos por data")
                    barChart.data = data
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }



    fun filterDates(barChart: BarChart, startDate: EditText, endDate: EditText) {
        if (startDate.text.isEmpty()) {
            Toast.makeText(this, "Uma data de início não foi inserida", Toast.LENGTH_SHORT).show();
            return;

        }
        if (endDate.text.isEmpty()) {
            Toast.makeText(this, "Uma data de fim não foi inserida", Toast.LENGTH_SHORT).show();
            return;
        }

        var readValues = ArrayList<BarEntry>()
        var dates = ArrayList<String>()
        var listaRuidos = ArrayList<RuidoFB>()
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val sdf_hours = SimpleDateFormat("dd/MM/yyyy HH:mm")


        var parsedStartDate : Date = sdf_hours.parse(startDate.text.toString()+" 0:00")
        var parsedEndDate : Date = sdf_hours.parse(endDate.text.toString()+" 23:59")
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
                    println(docSnapshot)
                    listaRuidos.add(docsData)
                }

                if(listaRuidos.isNotEmpty()){
                    var i = 0
                    for (item in listaRuidos){
                        readValues.add(BarEntry(item.value!!.toFloat(), i))
                        val dateFormatted = item.date!!.toDate()
                        dates.add(sdf.format(dateFormatted!!).toString())
                        i++
                    }

                    val mybardataset = MyBarDataSet(readValues, "Valor lido")
                    mybardataset.setColors(intArrayOf(Color.GREEN, Color.YELLOW, Color.RED))
                    val bardataset = ArrayList<BarDataSet>()
                    bardataset.add(mybardataset)

                    barChart.animateY(1000)
                    val data = BarData(dates, bardataset as List<IBarDataSet>?)
                    barChart.setDescription("Valores lidos por data")
                    barChart.data = data
                } else{
                    Toast.makeText(this@SharedGraphsActivity,
                        "Não foram encontrados resultados para o período selecionado",
                        Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    fun datePicker(text: EditText) {
        val cldr: Calendar = Calendar.getInstance()
        val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
        val month: Int = cldr.get(Calendar.MONTH)
        val year: Int = cldr.get(Calendar.YEAR)
        val  picker = DatePickerDialog(this@SharedGraphsActivity,
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        return;
    }
}
