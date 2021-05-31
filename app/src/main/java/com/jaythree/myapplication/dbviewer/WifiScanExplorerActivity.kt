package com.jaythree.myapplication.dbviewer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.sqlite.db.SimpleSQLiteQuery
import com.jaythree.myapplication.R
import com.jaythree.myapplication.db.DataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class WifiScanExplorerActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var dbselector: Spinner? = null
    private var dbstr: String? = null
    private var filterbtn: Button? = null
    private var resetbtn: Button? = null
    private var idtxt: EditText? = null
    private var lattxt: EditText? = null
    private var lontxt: EditText? = null
    private var btnfechaini: Button? = null
    private var btnhoraini:Button? = null
    private var btnfechafin: Button? = null
    private var btnhorafin:Button? = null
    private var fechaini: Long = 0L
    private var fechafin: Long = 0L
    private var horaini: Long = 0L
    private var horafin: Long = 0L

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_scan_explorer)
        btnfechaini=findViewById(R.id.btn_fechainicio);
        btnhoraini=findViewById(R.id.btn_horainicio);
        btnfechafin=findViewById(R.id.btn_fechafin);
        btnhorafin=findViewById(R.id.btn_horafin);

        btnfechaini?.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            var ano = c.get(Calendar.YEAR)
            var mes = c.get(Calendar.MONTH)
            var dia = c.get(Calendar.DAY_OF_MONTH)


            val datePickerDialog = DatePickerDialog(this,
                    DatePickerDialog.OnDateSetListener(){ datePicker: DatePicker, ano: Int, mes: Int, dia: Int ->
                        btnfechaini?.text = dia.toString() + "-" + (mes + 1) + "-" + ano
                        c.clear()
                        c.set(ano,mes,dia)
                        fechaini =c.timeInMillis
                        Log.d("MACexplorer", "Fechaini: ${fechaini}")
                    }
                    , ano, mes, dia)
            datePickerDialog.show()

        }

        btnfechafin?.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            var ano = c.get(Calendar.YEAR)
            var mes = c.get(Calendar.MONTH)
            var dia = c.get(Calendar.DAY_OF_MONTH)


            val datePickerDialog = DatePickerDialog(this,
                    DatePickerDialog.OnDateSetListener(){ datePicker: DatePicker, ano: Int, mes: Int, dia: Int ->
                        btnfechafin?.text = dia.toString() + "-" + (mes + 1) + "-" + ano
                        c.clear()
                        c.set(ano,mes,dia)
                        fechafin =c.timeInMillis
                        Log.d("MACexplorer", "Fechaini: ${fechafin}")
                    }
                    , ano, mes, dia)
            datePickerDialog.show()

        }

        btnhoraini?.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            var hora = c.get(Calendar.HOUR_OF_DAY)
            var min = c.get(Calendar.MINUTE)



            val timePickerDialog = TimePickerDialog(this,
                    TimePickerDialog.OnTimeSetListener(){ timePicker: TimePicker, hora: Int, min: Int ->
                        btnhoraini?.text = hora.toString()+":"+min
                        horaini =(((hora*60)+min)*60*1000).toLong()
                        Log.d("MACexplorer", "Fechaini: ${horaini}")
                    }
                    , hora, min, true)
            timePickerDialog.show()

        }

        btnhorafin?.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            var hora = c.get(Calendar.HOUR_OF_DAY)
            var min = c.get(Calendar.MINUTE)



            val timePickerDialog = TimePickerDialog(this,
                    TimePickerDialog.OnTimeSetListener(){ timePicker: TimePicker, hora: Int, min: Int ->
                        btnhorafin?.text = hora.toString()+":"+min
                        horafin =(((hora*60)+min)*60*1000).toLong()
                        Log.d("MACexplorer", "Fechafin: ${horaini}")
                    }
                    , hora, min, true)
            timePickerDialog.show()

        }

        recyclerView = findViewById(R.id.wifi_recyclerview)
        recyclerView?.layoutManager = LinearLayoutManager(applicationContext)
        dbselector = findViewById(R.id.dbselector)
        val dbstradapter = ArrayAdapter(
                applicationContext,
                android.R.layout.simple_list_item_1, resources.getStringArray(R.array.db_select_options)
        )
        dbstradapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        dbselector?.adapter = dbstradapter
        dbstr = dbselector?.selectedItem.toString()

        filterbtn = findViewById(R.id.filterbtn)
        resetbtn = findViewById(R.id.resetbtn)

        idtxt = findViewById(R.id.idtxt)
        lattxt = findViewById(R.id.latitudtxt)
        lontxt = findViewById(R.id.longitudtxt)
        filterbtn?.setOnClickListener {
            Log.d("MACexplorer", dbselector?.selectedItem.toString())
            scan()
        }
        resetbtn?.setOnClickListener {
            Log.d("MACexplorer", "Reseting filters")
            idtxt?.setText("ID")
            lattxt?.setText("Latitud")
            lontxt?.setText("Longitud")
            btnhorafin?.setText("hora fin")
            btnfechafin?.setText("fecha fin")
            btnfechaini?.setText("fecha inicio")
            btnhoraini?.setText("hora fin")
            horafin = 0
            horaini = 0
            fechaini = 0
            fechafin = 0
            scan()
        }


        //showScans()
        //scan()

    }





    private fun scan(){
        GlobalScope.launch(Dispatchers.IO){
            var query = ""

            if (dbselector?.selectedItem.toString() == "wifiscan") {
                var wifilist = DataBase.getDatabase(applicationContext).wifiscanDAO().selectAll()
                //Default
                if(idtxt?.text.toString() == "ID" && lattxt?.text.toString() == "Latitud"
                        && lontxt?.text.toString() == "Longitud" && fechaini == 0L && fechafin == 0L){
                    wifilist = DataBase.getDatabase(applicationContext).wifiscanDAO().selectAll()
                }
                //At least ID
                else if(idtxt?.text.toString() != "ID"){
                    query = "SELECT * FROM wifiscan WHERE bssid=\"${idtxt?.text}\""
                    if(lattxt?.text.toString() != "Latitud")
                        query += "AND lat=${lattxt?.text} "
                    if(lontxt?.text.toString() != "Longitud")
                        query += "AND lon=${lontxt?.text} "
                    if(fechaini!=0L)
                        query += "AND begin>=${fechaini+horaini} "
                    if (fechafin!=0L)
                        query += "AND end<=${fechafin+horafin}"
                    wifilist = DataBase.getDatabase(applicationContext).
                    wifiscanDAO().executeQuery(SimpleSQLiteQuery(query))
                }
                //At least lat
                else if(lattxt?.text.toString() != "Latitud"){
                    query = "SELECT * FROM wifiscan WHERE lat=${lattxt?.text} "
                    if(lontxt?.text.toString() != "Longitud")
                        query += "AND lon=${lontxt?.text}"
                    if(fechaini!=0L)
                        query += "AND begin>=${fechaini+horaini} "
                    if (fechafin!=0L)
                        query += "AND end<=${fechafin+horafin}"
                    wifilist = DataBase.getDatabase(applicationContext).
                    wifiscanDAO().executeQuery(SimpleSQLiteQuery(query))
                }
                //At least lon
                else if(lontxt?.text.toString() != "Longitud"){
                    query = "SELECT * FROM wifiscan WHERE lon=${lontxt?.text} "
                    if(fechaini!=0L)
                        query += "AND begin>=${fechaini+horaini} "
                    if (fechafin!=0L)
                        query += "AND end<=${fechafin+horafin}"
                    wifilist = DataBase.getDatabase(applicationContext).
                    wifiscanDAO().executeQuery(SimpleSQLiteQuery(query))
                }
                //At least init date
                else if(fechaini != 0L){
                    query = "SELECT * FROM wifiscan WHERE begin>=${fechaini+horaini} "
                    if (fechafin!=0L)
                        query += "AND end<=${fechafin+horafin}"
                    wifilist = DataBase.getDatabase(applicationContext).
                    wifiscanDAO().executeQuery(SimpleSQLiteQuery(query))
                }
                else{
                    query = "SELECT * FROM wifiscan WHERE end<=${fechafin+horafin}"
                    wifilist = DataBase.getDatabase(applicationContext).
                    wifiscanDAO().executeQuery(SimpleSQLiteQuery(query))
                }

                var wifiadapter = WifiScanAdapter(applicationContext, wifilist)
                runOnUiThread {
                    recyclerView?.adapter = wifiadapter
                }
            } else {
                var bluetoothlist = DataBase.getDatabase(applicationContext).bluetoothscanDAO().selectAll()
                //Default
                if(idtxt?.text.toString() == "ID" && lattxt?.text.toString() == "Latitud"
                        && lontxt?.text.toString() == "Longitud" && fechaini == 0L && fechafin == 0L){
                    bluetoothlist = DataBase.getDatabase(applicationContext).bluetoothscanDAO().selectAll()
                }
                //At least ID
                else if(idtxt?.text.toString() != "ID"){
                    query = "SELECT * FROM bluetoothscan WHERE mac=\"${idtxt?.text}\""
                    if(lattxt?.text.toString() != "Latitud")
                        query += "AND lat=${lattxt?.text} "
                    if(lontxt?.text.toString() != "Longitud")
                        query += "AND lon=${lontxt?.text}"
                    if(fechaini!=0L)
                        query += "AND begin>=${fechaini+horaini} "
                    if (fechafin!=0L)
                        query += "AND end<=${fechafin+horafin}"
                    bluetoothlist = DataBase.getDatabase(applicationContext).
                    bluetoothscanDAO().executeQuery(SimpleSQLiteQuery(query))
                }
                //At least lat
                else if(lattxt?.text.toString() != "Latitud"){
                    query = "SELECT * FROM bluetoothscan WHERE lat=${lattxt?.text} "
                    if(lontxt?.text.toString() != "Longitud")
                        query += "AND lon=${lontxt?.text}"
                    if(fechaini!=0L)
                        query += "AND begin>=${fechaini+horaini} "
                    if (fechafin!=0L)
                        query += "AND end<=${fechafin+horafin}"
                    bluetoothlist = DataBase.getDatabase(applicationContext).
                    bluetoothscanDAO().executeQuery(SimpleSQLiteQuery(query))
                }
                //At least lon
                else if(lontxt?.text.toString() != "Longitud"){
                    query = "SELECT * FROM bluetoothscan WHERE lon=${lontxt?.text} "
                    if(fechaini!=0L)
                        query += "AND begin>=${fechaini+horaini} "
                    if (fechafin!=0L)
                        query += "AND end<=${fechafin+horafin}"
                    bluetoothlist = DataBase.getDatabase(applicationContext).
                    bluetoothscanDAO().executeQuery(SimpleSQLiteQuery(query))
                }
                //At least init date
                else if(fechaini != 0L){
                    query = "SELECT * FROM bluetoothscan WHERE begin>=${fechaini+horaini} "
                    if (fechafin!=0L)
                        query += "AND end<=${fechafin+horafin}"
                    bluetoothlist = DataBase.getDatabase(applicationContext).
                    bluetoothscanDAO().executeQuery(SimpleSQLiteQuery(query))
                }
                else{
                    query = "SELECT * FROM bluetoothscan WHERE end<=${fechafin+horafin}"
                    bluetoothlist = DataBase.getDatabase(applicationContext).
                    bluetoothscanDAO().executeQuery(SimpleSQLiteQuery(query))
                }

                var bluetoothadapter = BluetoothScanAdapter(applicationContext, bluetoothlist)
                runOnUiThread {
                    recyclerView?.adapter = bluetoothadapter
                }
            }

        }
    }

}