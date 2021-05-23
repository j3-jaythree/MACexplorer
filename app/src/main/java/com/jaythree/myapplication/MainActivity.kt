package com.jaythree.myapplication


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jaythree.myapplication.dbviewer.WifiScanExplorerActivity
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "MAC explorer"

        //Go to database explorer
        val db_button=findViewById<Button>(R.id.DBexplorer)
        db_button.setOnClickListener {
            val intent = Intent(this, WifiScanExplorerActivity::class.java)
            startActivity(intent)
            //RoomExplorer.show(applicationContext, DataBase::class.java, "scans.db")
        }

        var startButton = findViewById<Button>(R.id.start_button)
        var stopButton = findViewById<Button>(R.id.stop_button)
        startButton.visibility = View.VISIBLE
        startButton.setOnClickListener {
            //check for location permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    100)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        105)
            }
            //check for bluetooth permisions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(baseContext,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                            111)
                }
            }
            //when start is clicked show stop button and hide play button
            startButton.visibility = View.GONE
            stopButton.visibility = View.VISIBLE
            Log.d(Companion.TAG, "TRYING TO START THE FOREGROUND SERVICE")
            startService()

        }

        stopButton.setOnClickListener {
            //when start is clicked show stop button and hide play button
            startButton.visibility = View.VISIBLE
            stopButton.visibility = View.GONE
            Log.d(Companion.TAG, "TRYING TO STOP THE FOREGROUND SERVICE")
            stopService()
        }
    }

    private fun startService() {
        val intent = Intent(this, MacExplorerService::class.java)
        if (!isRunning()) {
            setRunning()
            Log.d(Companion.TAG, "Service starting")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(intent)
            }
        } else {
            Toast.makeText(this, "Scanning was already started", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopService() {
        if (isRunning()) {
            val i = Intent(this, com.jaythree.myapplication.MacExplorerService::class.java)
            stopService(i)
            Log.d(TAG, "Stopping Service")
            setStopped()
        } else {
            Toast.makeText(this, "Scanning wasn't running", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> startService()
                PackageManager.PERMISSION_DENIED -> {Toast.makeText(this, "We need " +
                        "location permission to work", Toast.LENGTH_SHORT).show()
                    quitApp()
                }
            }
        }
        if (requestCode == 105) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> startService()
                PackageManager.PERMISSION_DENIED -> {Toast.makeText(this, "We need " +
                        "location permission to work", Toast.LENGTH_SHORT).show()
                    quitApp()
                }
            }
        }
        if (requestCode == 111) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> startService()
                PackageManager.PERMISSION_DENIED -> {Toast.makeText(this, "We need " +
                        "background location permission to work", Toast.LENGTH_SHORT).show()
                    quitApp()
                }
            }
        }
    }


    private fun quitApp() {
        this@MainActivity.finish()
        exitProcess(0)
    }

    //TAG constant for logs

    companion object {
        private const val TAG = "MACexplorer";
    }
}