package com.example.shush

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.MediaRecorder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.shush.baseDados.RuidoViewModel
import com.example.shush.baseDeDados.RuidoBD
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Math.log10
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {

    //private var db: AppDataBase? = null

    //nav menu config
    lateinit var toggle: ActionBarDrawerToggle

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val REQUEST_PERMISSION_RECORD = 1
    var mRecorder: MediaRecorder? = null
    private var timer: TimerTask? = null
    var valorMaximoAtual = 0 // guarda o valor maximo lido
    var valorMinimoAtual = 0 // guarda o valor minimo lido
    val valores = mutableListOf<Double>()
    var medianaCalculada = 0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var ultimoRuidoLido: Ruido? = null
    var ultimoRuidoFBLido: RuidoFB? = null
    var ultimoRuidoBDLido: RuidoBD? = null
    var isRecording: Boolean = false;
    var start: Long? = 0
    private lateinit var mRuidoViewModel: RuidoViewModel

    private var gcd: Geocoder? = null
    var addresses = mutableListOf<Address>()

    //local database

    private val soundCollectionRef = Firebase.firestore.collection("ruido") //adicionar referencia do q é guardado

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //db = Room.databaseBuilder(applicationContext, AppDataBase::class.java, "database-name").build()
        mRuidoViewModel = ViewModelProvider(this).get(RuidoViewModel::class.java)
        start = System.currentTimeMillis()
        gcd = Geocoder(baseContext, Locale.getDefault())

        //nav menu init
        var drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var navView: NavigationView = findViewById(R.id.navView)

        var localView: TextView = findViewById(R.id.medicaoAtual)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.miItemDashboard -> {
                    /*val intent = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(intent)*/
                    Toast.makeText(
                        this@MainActivity,
                        "O dashboard já é a página atual",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.miItemMaps -> {
                    val intent = Intent(this@MainActivity, MapsActivity::class.java)
                    startActivity(intent)
                }
                R.id.miItemGraphs -> {
                    val intent = Intent(this@MainActivity, GraphsActivity::class.java)
                    startActivity(intent)
                }
                R.id.miItemTables -> {
                    val intent = Intent(this@MainActivity, LocalTableActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        requestPermission(Manifest.permission.RECORD_AUDIO)
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        //requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this) //para localizacao
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        var valorLido: TextView =
            findViewById(R.id.medicaoAtual) //associar o campo da vista a variável
        var valorLidoMax: TextView = findViewById(R.id.valorMaximo)
        var valorLidoMin: TextView = findViewById(R.id.valorMinimo)
        var valorLidoMediana: TextView = findViewById(R.id.valorMediana)
        var labelNivel: TextView = findViewById(R.id.Nivel)

        var playButton: Button = findViewById(R.id.buttonPlay)
        var playButtonGrayed: Button = findViewById(R.id.buttonPlayDisabled)
        var pauseButton: Button = findViewById(R.id.buttonPause)
        var pauseButtonGrayed: Button = findViewById(R.id.buttonPauseDisabled)
        var stopButton: Button = findViewById(R.id.buttonStop)
        var stopButtonGrayed: Button = findViewById(R.id.buttonStopDisabled)
        val shareButton: ImageButton = findViewById(R.id.shareButton)

        markButtonDisable(playButtonGrayed)
        markButtonDisable(pauseButton)
        markButtonDisable(stopButton)

        shareButton.setOnClickListener{
            getLocation()

            if(isRecording==false){
                chamarAlertaMedicaoDesligada()
            } else if((longitude==0.0&&latitude==0.0 || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))){
                chamarAlertaSemAcessoALocalizacao()
            } else if(isOnline(this)==false){
                chamarAlertaSemAcessoAInternet()
            } else{
                if(ultimoRuidoLido != null && isRecording==true && isOnline(this)==true){
                    ultimoRuidoBDLido!!.longitude = longitude
                    ultimoRuidoBDLido!!.latitude = latitude
                    ultimoRuidoLido!!.longitude = longitude
                    ultimoRuidoLido!!.latitude = latitude

                    pause()
                    if(timer!=null){
                        timer!!.cancel()
                    }

                    markButtonEnable(pauseButtonGrayed)
                    markButtonEnable(playButton)
                    markButtonEnable(stopButton)
                    markButtonDisable(pauseButton)
                    markButtonDisable(playButtonGrayed)
                    markButtonDisable(stopButtonGrayed)

                    chamarAlertEnviarDados()
                }
            }
        }


        playButton.setOnClickListener {
            try{
                start = System.currentTimeMillis()
                start()
                timer = Timer("tempo", false).schedule(25, 500) {
                    var valor = 20 * log10(mRecorder!!.maxAmplitude.toDouble())
                    if(valor<0){
                        valor = 0.0;
                    }
                    getLocation()
                    val id = UUID.randomUUID().toString()
                    val c =  Calendar.getInstance()
                    val sDFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
                    val date = sDFormat.format(c.time)
                    var end = System.currentTimeMillis() - start!!
                    var time = (end/ 1000)/60
                    var localizacao:String = ""

                    try {
                        addresses = gcd!!.getFromLocation(latitude, longitude, 1)//ir buscar o nome da localizacao consoante a coordenada
                        if (addresses.size > 0) {
                            localizacao = addresses[0].locality
                        }
                        ultimoRuidoBDLido = RuidoBD(
                            id,
                            valor.toInt(),
                            date,
                            longitude,
                            latitude,
                            valorMaximoAtual,
                            valorMinimoAtual,
                            medianaCalculada,
                            end.toString(),
                            localizacao
                        )
                        ultimoRuidoLido = Ruido(
                            id,
                            valor.toInt(),
                            date,
                            longitude,
                            latitude,
                            localizacao
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    atualizarValor(
                        valor.toInt(),
                        valorLido,
                        valorLidoMax,
                        valorLidoMin,
                        valorLidoMediana,
                        labelNivel
                    )
                }

                isRecording=true;
                markButtonEnable(playButtonGrayed)
                markButtonEnable(pauseButton)
                markButtonEnable(stopButton)
                markButtonDisable(playButton)
                markButtonDisable(pauseButtonGrayed)
                markButtonDisable(stopButtonGrayed)
            } catch (e: Exception){
                chamarAlertaSemAcessoAoMicro()
            }
        }

        pauseButton.setOnClickListener{
            pause()
            if(timer!=null){
                timer!!.cancel()
            }

            isRecording=true;
            markButtonEnable(pauseButtonGrayed)
            markButtonEnable(playButton)
            markButtonEnable(stopButton)
            markButtonDisable(pauseButton)
            markButtonDisable(playButtonGrayed)
            markButtonDisable(stopButtonGrayed)
        }

        stopButton.setOnClickListener{
            reset(valorLido, valorLidoMax, valorLidoMin, valorLidoMediana, labelNivel)
            if(timer!=null){
                timer!!.cancel()
            }

            isRecording=false;

            if(longitude==0.0&&latitude==0.0){
                getLocation()
            chamarAlertaSemAcessoALocalizacao()

                markButtonEnable(stopButtonGrayed)
                markButtonEnable(pauseButtonGrayed)
                markButtonEnable(playButton)
                markButtonDisable(stopButton)
                markButtonDisable(pauseButton)
                markButtonDisable(playButtonGrayed)
        } else {
                getLocation()
                chamarAlertGuardarLocalmente()

                markButtonEnable(stopButtonGrayed)
                markButtonEnable(pauseButtonGrayed)
                markButtonEnable(playButton)
                markButtonDisable(stopButton)
                markButtonDisable(pauseButton)
                markButtonDisable(playButtonGrayed)
            }
        }
    }

    fun requestPermission(permission: String): Boolean {
        val isGranted = ContextCompat.checkSelfPermission(
            baseContext!!,
            permission
        ) == PackageManager.PERMISSION_GRANTED
        if (!isGranted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                REQUEST_PERMISSION_RECORD
            )
        }
        return isGranted
    }

    fun start() {
        mRecorder = MediaRecorder()
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mRecorder!!.setOutputFile("/dev/null")
        mRecorder!!.prepare()
        mRecorder!!.start()
    }

    fun pause() {
        if (mRecorder != null) {
            mRecorder!!.stop()
            mRecorder!!.release()
            mRecorder = null
        }
    }

    fun reset(
        textViewValor: TextView,
        textViewValorMax: TextView,
        textViewValorMin: TextView,
        textViewValorMediana: TextView,
        labelNivel: TextView,
    ){
        textViewValor.text="-"
        textViewValorMax.text="0"
        textViewValorMin.text="0"
        textViewValorMediana.text="0"
        labelNivel.text="Nível"
        textViewValor.setBackgroundResource(0);
        ultimoRuidoLido?.latitude=0.0
        ultimoRuidoLido?.longitude=0.0

    }

    fun getValue(): String {
        println("${mRecorder!!.maxAmplitude} has run.")
        return mRecorder!!.maxAmplitude.toString()
    }

    private fun atualizarValor(
        valorLido: Int,
        textViewValor: TextView,
        textViewValorMax: TextView,
        textViewValorMin: TextView,
        textViewValorMediana: TextView,
        labelNivel: TextView,
    ) {
        if (valorMinimoAtual == 0 && valorLido>=0) { // Inicializa o valor mínimo
            valorMinimoAtual = valorLido
        }//
        runOnUiThread(java.lang.Runnable {
            if (valorLido >= 0) {
                textViewValor.text = valorLido.toString()
            }
            if (valorLido < 65) {
                textViewValor.setBackgroundResource(R.drawable.circle_shape_verde);
                labelNivel.text = "Baixo"
            } else if (valorLido in 65..85) {
                textViewValor.setBackgroundResource(R.drawable.circle_shape_amarelo);
                labelNivel.text = "Médio"
            } else {
                textViewValor.setBackgroundResource(R.drawable.circle_shape_vermelho);
                labelNivel.text = "Alto "
            }

            if (valorLido >= 0) {
                valorMaximoAtual =
                    Math.max(valorMaximoAtual, valorLido)  // Descobre o valor maximo, e atualiza.
                textViewValorMax.text =
                    valorMaximoAtual.toString()        // Atribui o valor maximo à variavel visivel
                /// valor minimo ------------------------- //

                valorMinimoAtual =
                    Math.min(valorMinimoAtual, valorLido)  // Descobre o valor minimo e atualiza
                if (valorMaximoAtual >= 0) {
                    textViewValorMin.text =
                        valorMinimoAtual.toString()       // Atribui o valor minimo à variavel visivel
                }

                valores.add(valorLido.toDouble())        // adiciona os valores lido à lista p depois calcular mediana
                medianaCalculada = med(valores).toInt() // calcula a mediana
                textViewValorMediana.text = medianaCalculada.toString()
            }
        })
    }

    fun med(list: List<Double>) = list.sorted().let {
        (it[it.size / 2] + it[(it.size - 1) / 2]) / 2
    } // Funçao para calcular a mediana :)

    fun markButtonDisable(button: Button) {
        button?.isVisible = false
        button?.isEnabled = false
        button.isClickable = false
    }

    fun markButtonEnable(button: Button) {
        button?.isVisible = true
        button?.isEnabled = true
        button.isClickable = true
    }

    fun getLocation(){
        val request = LocationRequest()
        request.interval = 1000
        request.fastestInterval = 500
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val permissionLocation = ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val permissionRecorder = ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.RECORD_AUDIO
        )

        //verificar permissões antes de partilhar localização
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            chamarAlertaSemAcessoALocalizacao()
            return;
        }

        if (permissionRecorder != PackageManager.PERMISSION_GRANTED){
            chamarAlertaSemAcessoAoMicro()
            return;
        }

        var handler = Handler(Looper.getMainLooper())

        handler.post {
            fusedLocationClient.requestLocationUpdates(request, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location: Location? = locationResult.lastLocation
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
            }, null)
        }
    }

    private fun saveRuido(ruido: Ruido) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
            val time = sdf.parse(ruido.date)

            ultimoRuidoFBLido = RuidoFB(
                ruido.id,
                ruido.value,
                Timestamp(time),
                ruido.longitude,
                ruido.latitude
            )

            soundCollectionRef.add(ultimoRuidoFBLido!!).await()
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity, "Compartilhado com sucesso", Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun chamarAlertEnviarDados() {
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Confirmação") // O Titulo da notificação
        val message = "Pretende enviar os seguintes dados?\nRuído atual: "+ultimoRuidoLido!!.value+
                "\nData: "+ultimoRuidoLido!!.date+"\nLocalização: "+ultimoRuidoLido!!.latitude+";"+ultimoRuidoLido!!.longitude
        alertDialog.setMessage(message) // a mensagem ou alerta

        alertDialog.setPositiveButton("Sim") { _, _ ->
            //Toast.makeText(this, "Sim", Toast.LENGTH_LONG).show()
            saveRuido(ultimoRuidoLido!!)
        }

        alertDialog.setNegativeButton("Não") { _, _ ->
            //Aqui sera executado a instrução a sua escolha
            Toast.makeText(this, "Compartilhamento cancelado pelo utilizador", Toast.LENGTH_LONG).show()

        }
        alertDialog.show()
    }

    /*fun chamarAlertShare() {
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Confirmação") // O Titulo da notificação
        alertDialog.setMessage("Pretende guardar os dados localmente ou compartilhá-los?") // a mensagem ou alerta

        alertDialog.setPositiveButton("Compartilhar") { _, _ ->
            //Toast.makeText(this, "Sim", Toast.LENGTH_LONG).show()
            chamarAlertEnviarDados()
        }

        alertDialog.setNegativeButton("Localmente") { _, _ ->
            //Aqui sera executado a instrução a sua escolha
            chamarAlertGuardarLocalmente()

        }
        alertDialog.show().
    }*/

    fun chamarAlertGuardarLocalmente() {
        var alertDialog = AlertDialog.Builder(this)
        val message = "Deseja guardar os seguintes dados no seu dispositivo?\nRuído atual: "+ultimoRuidoBDLido!!.value+
                "\nData Início: "+ultimoRuidoBDLido!!.date+"\nLocalização: "+ultimoRuidoBDLido!!.latitude+
                ";"+ultimoRuidoBDLido!!.longitude+
                "\nMaximo: " +ultimoRuidoBDLido!!.maximo+"\nMínimo: "+ultimoRuidoBDLido!!.minimo+
                "\nMediana: "+ultimoRuidoBDLido!!.mediana+"\nTempo decorrido: "+ ultimoRuidoBDLido!!.tDecorrido+"ms"
        alertDialog.setTitle("Confirmação") // O Titulo da notificação
        alertDialog.setMessage(message) // a mensagem ou alerta

        alertDialog.setPositiveButton("Sim") { _, _ ->
            //Toast.makeText(this, "Sim", Toast.LENGTH_LONG).show()
            saveDataToLocal()
            Toast.makeText(this, "Dados guardados com sucesso", Toast.LENGTH_LONG).show()

        }

        alertDialog.setNegativeButton("Não") { _, _ ->
            //Aqui sera executado a instrução a sua escolha
            Toast.makeText(this, "Compartilhamento cancelado pelo utilizador", Toast.LENGTH_LONG).show()

        }
        alertDialog.show()
    }

    private fun saveDataToLocal() = CoroutineScope(Dispatchers.IO).launch {
        try {
            //db!!.ruidoDao().insertAll(ultimoRuidoBDLido!!)
                mRuidoViewModel.addRuido(ultimoRuidoBDLido!!)
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity, "Guardado com sucesso", Toast.LENGTH_LONG).show()
            }
        }catch (e: Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun chamarAlertaSemAcessoAoMicro(){
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Microfone não disponível")
        val message = "A medição de som só pode ser realizada se permitir o acesso ao microfone"
        alertDialog.setMessage(message)

        alertDialog.setPositiveButton("OK") { _, _ ->
            requestPermission(Manifest.permission.RECORD_AUDIO)
        }

        alertDialog.show()
    }

    fun chamarAlertaSemAcessoALocalizacao(){
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Localização não disponível")
        val message = "A partilha de dados da medição só pode ser realizada com os serviços de localização ativados"
        alertDialog.setMessage(message)

        alertDialog.setPositiveButton("OK") { _, _ ->
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        alertDialog.show()
    }

    fun chamarAlertaSemAcessoAInternet(){
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Dispositivo não conectado à rede")
        val message = "A partilha de dados da medição só pode ser realizada com conexão ativa à internet"
        alertDialog.setMessage(message)

        alertDialog.setPositiveButton("OK") { _, _ ->
        }

        alertDialog.show()
    }

    fun chamarAlertaMedicaoDesligada(){
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("A medição de som não está a correr")
        val message = "A partilha de dados da medição só pode ser realizada com a medição de som ativada"
        alertDialog.setMessage(message)

        alertDialog.setPositiveButton("OK") { _, _ ->
        }

        alertDialog.show()
    }

    //source: https://stackoverflow.com/questions/51141970/check-internet-connectivity-android-in-kotlin
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    } else {
                        TODO("VERSION.SDK_INT < M")
                    }
                } else {
                    TODO("VERSION.SDK_INT < LOLLIPOP")
                }
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        }
        return false
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