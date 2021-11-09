package com.example.shush

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.shush.baseDados.RuidoViewModel
import com.example.shush.baseDeDados.RuidoBD
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class LocalTableActivity: AppCompatActivity()  {
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mRuidoViewModel: RuidoViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tables)

        //nav menu init
        var drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mRuidoViewModel = ViewModelProvider(this).get(RuidoViewModel::class.java)

        var tableLayout: TableLayout = findViewById(R.id.tableLayout)
        init(tableLayout)

        var navView: NavigationView = findViewById(R.id.navView)
        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.miItemDashboard -> {
                    val intent = Intent(this@LocalTableActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.miItemMaps -> {
                    val intent = Intent(this@LocalTableActivity, MapsActivity::class.java)
                    startActivity(intent)
                }
                R.id.miItemGraphs -> {
                    val intent = Intent(this@LocalTableActivity, GraphsActivity::class.java)
                    startActivity(intent)
                }
                R.id.miItemTables -> {
                    Toast.makeText(this@LocalTableActivity,
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
            Toast.makeText(this@LocalTableActivity,
                "A tabela local já está a ser exibida",
                Toast.LENGTH_SHORT).show()
        }
        sharedTableButton.setOnClickListener {
            val intent = Intent(this@LocalTableActivity, SharedTableActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent)
        }

        val spinner: Spinner = findViewById(R.id.spinner)
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
                Toast.makeText(this@LocalTableActivity,
                    "Selecione uma opção",
                    Toast.LENGTH_SHORT).show()
            }
        }

        startDate.setOnClickListener{datePicker(startDate)}
        endDate.setOnClickListener{datePicker(endDate)}
        filterTableByDateButton.setOnClickListener{filterDates(tableLayout, startDate, endDate)}
        filterTableByKeywordButton.setOnClickListener{filterKeywords(tableLayout, keyword)}
    }

    fun init(tableLayout: TableLayout){
        var textInfoDados: TextView = findViewById(R.id.textViewInfoDados)

        mRuidoViewModel.readAllData.observe(this, { ruidos ->
            resetTable(tableLayout)
            if (ruidos.isNotEmpty()) {
                buildTable(tableLayout, ruidos)
                textInfoDados.text = ""
            } else{
                Toast.makeText(this@LocalTableActivity,
                    "Não foram encontrados medições armazenadas no telemóvel",
                    Toast.LENGTH_SHORT).show()
                textInfoDados.text = "Não há informação para ser exibida na tabela."
                tableLayout.setBackgroundResource(R.drawable.background)
            }
        })
    }

    private fun filterKeywords(tableLayout: TableLayout, keyword: EditText) {
        if (keyword.text.isEmpty()) {
            Toast.makeText(this, "Não foi inserido texto livre para iniciar a pesquisa", Toast.LENGTH_SHORT).show();
            return;
        }
        var textInfoDados: TextView = findViewById(R.id.textViewInfoDados)

        mRuidoViewModel.readAllDataByKeyword(keyword.text.toString())?.observe(this, { ruidos ->
            resetTable(tableLayout)
            if (ruidos.isNotEmpty()) {
                buildTable(tableLayout, ruidos)
                textInfoDados.text = ""
            } else{
                Toast.makeText(this@LocalTableActivity,
                    "Não foram encontrados resultados para a palavra chave inserida",
                    Toast.LENGTH_SHORT).show()
                textInfoDados.text = "Não há informação para ser exibida na tabela."
                tableLayout.setBackgroundResource(R.drawable.background)
            }
        })

        //println("RESULT IS"+mRuidoViewModel.readAllDataByKeyword(keyword = keyword.text.toString()))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterDates(tableLayout: TableLayout, startDate: EditText, endDate: EditText) {
        if(startDate.text.toString().isEmpty()){
            Toast.makeText(this, "Uma data de início não foi inserida", Toast.LENGTH_SHORT).show();
            return;
        }
        if(endDate.text.toString().isEmpty()){
            Toast.makeText(this, "Uma data de fim não foi inserida", Toast.LENGTH_SHORT).show();
            return;
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        var textInfoDados: TextView = findViewById(R.id.textViewInfoDados)
        var parsedStartDate : Date = sdf.parse(startDate.text.toString() + " 0:00")
        var parsedEndDate : Date = sdf.parse(endDate.text.toString() + " 23:59")

        if (parsedEndDate.after(parsedStartDate)) {
            resetTable(tableLayout)
            var ruidosArray = ArrayList<RuidoBD>()
            mRuidoViewModel.readAllData.observe(this, { ruidos ->
                if (ruidos.isNotEmpty()) {
                    for (ruido in ruidos) {
                        var ruidoDate : Date = sdf.parse(ruido.date!!)
                        if(ruidoDate.after(parsedStartDate) && parsedEndDate.after(ruidoDate)){
                            ruidosArray.add(ruido)
                        }
                    }
                    if (ruidosArray.isNotEmpty()) {
                        buildTable(tableLayout, ruidosArray)
                        textInfoDados.text = ""
                    } else{
                        Toast.makeText(this@LocalTableActivity,
                            "Não foram encontrados resultados para a palavra chave inserida",
                            Toast.LENGTH_SHORT).show()
                        textInfoDados.text = "Não há informação para ser exibida na tabela."
                        tableLayout.setBackgroundResource(R.drawable.background)
                    }
                }
            })
        } else {
            Toast.makeText(this@LocalTableActivity,
                "A data início deve ser anterior à data final",
                Toast.LENGTH_LONG).show()
        }
    }

    fun datePicker(text: EditText) {
        val cldr: Calendar = Calendar.getInstance()
        val day: Int = cldr.get(Calendar.DAY_OF_MONTH)
        val month: Int = cldr.get(Calendar.MONTH)
        val year: Int = cldr.get(Calendar.YEAR)
        val  picker = DatePickerDialog(this@LocalTableActivity,
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

    fun buildTable(tableLayout: TableLayout, list:List<RuidoBD>){
        for (item in list) {
            val tbrow = TableRow(this)
            val t1v = TextView(this)
            formatCell(t1v, item.date!!)
            t1v.setPadding(10,0,10,0)
            tbrow.addView(t1v)
            val t2v = TextView(this)
            formatCell(t2v, item.value!!.toString())
            tbrow.addView(t2v)
            val t3v = TextView(this)
            formatCell(t3v, item.maximo!!.toString())
            tbrow.addView(t3v)
            val t4v = TextView(this)
            formatCell(t4v, item.minimo!!.toString())
            tbrow.addView(t4v)
            val t5v = TextView(this)
            formatCell(t5v, item.mediana!!.toString())
            tbrow.addView(t5v)
            val t6v = TextView(this)
            formatCell(t6v, item.tDecorrido!!.toString())
            tbrow.addView(t6v)
            val t7v = TextView(this)
            formatCell(t7v, "%.4f".format(item.longitude!!))
            tbrow.addView(t7v)
            val t8v = TextView(this)
            formatCell(t8v, "%.4f".format(item.latitude!!))
            tbrow.addView(t8v)
            tableLayout.addView(tbrow)
        }
        var scrollView: HorizontalScrollView = findViewById(R.id.scrollViewHorizontal)
        scrollView.isScrollbarFadingEnabled = false
    }

    fun resetTable(tableLayout: TableLayout){
        val count: Int = tableLayout.getChildCount()
        for (i in 1 until count) {
            val child: View = tableLayout.getChildAt(i)
            if (child is TableRow) (child as ViewGroup).removeAllViews()
        }
        var scrollView: HorizontalScrollView = findViewById(R.id.scrollViewHorizontal)
        scrollView.isScrollbarFadingEnabled = true
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

    fun formatCell(textView: TextView, text:String){
        textView.text = text
        textView.setTextColor(Color.BLACK)
        textView.gravity = Gravity.CENTER
        textView.setBackgroundResource(R.drawable.background)
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