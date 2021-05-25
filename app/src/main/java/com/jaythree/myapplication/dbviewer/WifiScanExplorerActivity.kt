package com.jaythree.myapplication.dbviewer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.jaythree.myapplication.R
import com.jaythree.myapplication.db.DataBase
import com.jaythree.myapplication.db.WiFiScan
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_scan_explorer)
        recyclerView = findViewById(R.id.wifi_recyclerview)
        recyclerView?.layoutManager = LinearLayoutManager(applicationContext)
        dbselector = findViewById(R.id.dbselector)
        val dbstradapter = ArrayAdapter(applicationContext,
                android.R.layout.simple_list_item_1, resources.getStringArray(R.array.db_select_options))
        dbstradapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        dbselector?.adapter = dbstradapter
        dbstr = dbselector?.selectedItem.toString()

        filterbtn = findViewById(R.id.filterbtn)
        resetbtn = findViewById(R.id.resetbtn)

        idtxt = findViewById(R.id.idtxt)
        lattxt = findViewById(R.id.latitudtxt)
        lontxt = findViewById(R.id.longitudtxt)
        filterbtn?.setOnClickListener {
            Log.d("MACexplorer",dbselector?.selectedItem.toString())
            scan()
        }
        resetbtn?.setOnClickListener {
            Log.d("MACexplorer","Reseting filters")
            idtxt?.setText("ID")
            lattxt?.setText("Latitud")
            lontxt?.setText("Longitud")
            Log.d("MACexplorer", idtxt?.text.toString())
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
                        && lontxt?.text.toString() == "Longitud"){
                    wifilist = DataBase.getDatabase(applicationContext).wifiscanDAO().selectAll()
                }
                //At least ID
                else if(idtxt?.text.toString() != "ID"){
                    query = "SELECT * FROM wifiscan WHERE bssid=\"${idtxt?.text}\""
                    if(lattxt?.text.toString() != "Latitud")
                        query += "AND lat=${lattxt?.text} "
                    if(lontxt?.text.toString() != "Longitud")
                        query += "AND lon=${lontxt?.text}"
                    wifilist = DataBase.getDatabase(applicationContext).
                    wifiscanDAO().executeQuery(SimpleSQLiteQuery(query))
                }
                //At least lat
                else if(lattxt?.text.toString() != "Latitud"){
                    query = "SELECT * FROM wifiscan WHERE lat=${lattxt?.text} "
                    if(lontxt?.text.toString() != "Longitud")
                        query += "AND lon=${lontxt?.text}"
                    wifilist = DataBase.getDatabase(applicationContext).
                    wifiscanDAO().executeQuery(SimpleSQLiteQuery(query))
                }
                else {
                    query = "SELECT * FROM wifiscan WHERE lon=${lontxt?.text}"
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
                        && lontxt?.text.toString() == "Longitud"){
                    bluetoothlist = DataBase.getDatabase(applicationContext).bluetoothscanDAO().selectAll()
                }
                //At least ID
                else if(idtxt?.text.toString() != "ID"){
                    query = "SELECT * FROM bluetoothscan WHERE mac=\"${idtxt?.text}\""
                    if(lattxt?.text.toString() != "Latitud")
                        query += "AND lat=${lattxt?.text} "
                    if(lontxt?.text.toString() != "Longitud")
                        query += "AND lon=${lontxt?.text}"
                    bluetoothlist = DataBase.getDatabase(applicationContext).
                    bluetoothscanDAO().executeQuery(SimpleSQLiteQuery(query))
                }
                //At least lat
                else if(lattxt?.text.toString() != "Latitud"){
                    query = "SELECT * FROM bluetoothscan WHERE lat=${lattxt?.text} "
                    if(lontxt?.text.toString() != "Longitud")
                        query += "AND lon=${lontxt?.text}"
                    bluetoothlist = DataBase.getDatabase(applicationContext).
                    bluetoothscanDAO().executeQuery(SimpleSQLiteQuery(query))
                }
                else {
                    query = "SELECT * FROM bluetoothscan WHERE lon=${lontxt?.text}"
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