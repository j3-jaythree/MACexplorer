package com.jaythree.myapplication.dbviewer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaythree.myapplication.R
import com.jaythree.myapplication.db.DataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WifiScanExplorerActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_scan_explorer)
        recyclerView = findViewById(R.id.wifi_recyclerview)
        recyclerView?.layoutManager = LinearLayoutManager(applicationContext)
        //showScans()
        GlobalScope.launch(Dispatchers.IO){
            var scanlist = DataBase.getDatabase(applicationContext).wifiscanDAO().selectAll()
            var wifiadapter = WifiScanAdapter(applicationContext, scanlist)
            runOnUiThread {
                recyclerView?.adapter = wifiadapter
            }

        }
    }

}