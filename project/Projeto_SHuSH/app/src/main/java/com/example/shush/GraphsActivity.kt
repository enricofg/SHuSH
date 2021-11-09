package com.example.shush

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.shush.baseDados.RuidoViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.navigation.NavigationView
import java.util.*
import kotlin.collections.ArrayList

class GraphsActivity : AppCompatActivity() {
    //nav menu config
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mRuidoViewModel: RuidoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphs)
        var barChart: BarChart = findViewById(R.id.barchart)
        barChart.setNoDataText("Não há informação gravada para ser exibida no gráfico.");
        mRuidoViewModel = ViewModelProvider(this).get(RuidoViewModel::class.java)
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
                    val intent = Intent(this@GraphsActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                R.id.miItemMaps -> {
                    val intent = Intent(this@GraphsActivity, MapsActivity::class.java)
                    startActivity(intent)
                }
                R.id.miItemGraphs -> {
                    Toast.makeText(this@GraphsActivity,
                        "A página de gráficos já é a página atual",
                        Toast.LENGTH_SHORT).show()
                }
                R.id.miItemTables -> {
                    val intent = Intent(this@GraphsActivity, LocalTableActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        //page controls
        val localGraphButton: Button = findViewById(R.id.buttonTabelaLocal)
        val sharedGraphButton: Button = findViewById(R.id.buttonTabelaPartilhado)

        localGraphButton.setOnClickListener {
            Toast.makeText(this@GraphsActivity,
                "O gráfico local já está a ser exibido",
                Toast.LENGTH_SHORT).show()
        }
        sharedGraphButton.setOnClickListener {
            val intent = Intent(this@GraphsActivity, SharedGraphsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent)
        }
    }

    private fun initGraph(barChart: BarChart) {
        val readValues = ArrayList<BarEntry>()
        val dates = ArrayList<String>()

        mRuidoViewModel.readAllData.observe(this, Observer { ruidos ->
            if (!ruidos.isEmpty()) {
                var i = 0
                for (ruido in ruidos) {

                    readValues.add(BarEntry(ruido.value!!.toFloat(), i))
                    //var date = ruido.date!!.split(" ")[0]
                    dates.add(ruido.date!!)
                    println("DB:" + ruido.value!!.toFloat())
                    i++
                }

                val mybardataset = MyBarDataSet(readValues, "Valor lido")
                mybardataset.setColors(intArrayOf(Color.GREEN, Color.YELLOW, Color.RED))
                val bardataset = ArrayList<BarDataSet>()
                bardataset.add(mybardataset)

                barChart.animateY(1000)
                val data = BarData(dates, bardataset as List<IBarDataSet>?)
                //bardataset.setColors(ColorTemplate.COLORFUL_COLORS)
                barChart.setDescription("Valores lidos por data")
                barChart.data = data
            }
        })

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
