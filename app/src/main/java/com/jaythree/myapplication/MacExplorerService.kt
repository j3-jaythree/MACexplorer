package com.jaythree.myapplication

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.jaythree.myapplication.db.BluetoothScan
import com.jaythree.myapplication.db.DataBase
import com.jaythree.myapplication.db.WiFiScan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class MacExplorerService : Service() {

    //a lock so we can avoid getting affected by Doze Mode
    private var powerLock: PowerManager.WakeLock? = null

    private var wifiManager :WifiManager? = null
    private var locationManager : LocationManager? = null

    //Bluetooth adapter
    private var btAdapter: BluetoothAdapter? = null

    //Broadcast receiver for BT
    private var btReceiver: BroadcastReceiver? = null


    //Stuff for location changing over time
    private lateinit var serviceHandler : Handler

    private class LocationListener(provider: String) : android.location.LocationListener {
        var lastLocation: Location
        override fun onLocationChanged(location: Location) {
            Log.d(TAG, "onLocationChanged: ${location.latitude} ${location.longitude} " +
                    "${location.time}")
            lastLocation.set(location)
        }

        init {
            Log.d(TAG, "LocationListener $provider")
            lastLocation = Location(provider)
        }
    }

    private var locationListener = LocationListener(LocationManager.GPS_PROVIDER)



    //This will be called when we do startService() on the main activity

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created!")
        createNotificationChannel()
        val builder = NotificationCompat.Builder(this, "MACexplorer channel")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("MAC explorer")
                .setContentText("Running...")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notif = builder.build()
        startForeground(5, notif)

    }

    //This also will be called when we do startService() on the main activity

    //@RequiresApi(Build.VERSION_CODES.P)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Starting the service as foreground")
        Toast.makeText(this, "Scanning started", Toast.LENGTH_SHORT).show()
        //From the docs how to acquire a lock to keep the device awake and prevent doze mode
        powerLock =
                (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MACExplorer::powerlock").apply {
                        acquire()
                    }
                }
        Log.d(TAG, "WakeLock acquired")

        //Wifi manager init
        wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        Log.d(TAG, "WiFi Manager started")
        locationManager = this.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        Log.d(TAG, "Location Manager started")
        btAdapter = BluetoothAdapter.getDefaultAdapter()
        try {
            locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 1000 * 1L, 0F,
                    locationListener)
        } catch (ex: SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } /*catch (ex: IllegalArgumentException) {
            Log.d(TAG, "network provider does not exist, " + ex.message)
        }*/

        //*****************************************************************************
        /*val locationRequest = createLocationRequest()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                location = locationResult.lastLocation
            }
        }
        //val builder = LocationSettingsRequest.Builder()
          //      .addLocationRequest(locationRequest)
        val fusedLocationClient = FusedLocationProviderClient(applicationContext)
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper())
        } catch (e: SecurityException){
            Log.d(TAG, "Error with location permission")
        }*/



        //*****************************************************************************


        //START OF OUR SCANS LOOP
        //Dispatchers.IO states that the thread is for in/out operations
        GlobalScope.launch(Dispatchers.IO){
            while(isRunning()){
                Log.d(TAG, isRunning().toString())

                GlobalScope.launch(Dispatchers.IO) {
                    //locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER, null, )
                    //Looper.prepare()

                    //var day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    //var month = Calendar.getInstance().get(Calendar.MONTH)
                    //var year = Calendar.getInstance().get(Calendar.YEAR)
                    //var hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    //var minute = Calendar.getInstance().get(Calendar.MINUTE)
                    var time = 0L
                    var lat: Double
                    var lon: Double
                    val wifiScanDAO = DataBase.getDatabase(applicationContext).wifiscanDAO()
                    val bluetoothScanDAO = DataBase.getDatabase(applicationContext).bluetoothscanDAO()
                    Log.d(TAG, "Scanning now")
                    if (ActivityCompat.checkSelfPermission(applicationContext,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                    applicationContext,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        //lon = locationListener.lastLocation.longitude
                        //lat = locationListener.lastLocation.latitude
                        lon = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!.longitude
                        lat = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!.latitude
                        time = System.currentTimeMillis()
                            Log.d(TAG, "Getting last location known $lon $lat")
                    } else {
                        lon = locationListener.lastLocation.longitude
                        lat = locationListener.lastLocation.latitude
                        time = locationListener.lastLocation.time
                        Log.d(TAG, "Getting last location $lon $lat")
                        if(lon == 0.0 && lat == 0.0) {
                            lon = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!.longitude
                            lat = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!.latitude
                            time = System.currentTimeMillis()
                            Log.d(TAG, "Getting last location known $lon $lat")
                        }
                    }

                    wifiManager?.let {
                        var scanList = it.scanResults as ArrayList<ScanResult>
                        for (scan in scanList) {
                            if(scan.level <= -67){
                                var actives = wifiScanDAO.active(scan.BSSID)
                                if(actives.isNotEmpty()){
                                    wifiScanDAO.updateTime(time, scan.BSSID)
                                    Log.d(TAG, "Updated: ${scan.BSSID} into ${actives[0].id}")
                                } else {
                                    var wifiScan = WiFiScan(bssid = scan.BSSID, lat = lat, lon = lon,
                                            intensity = scan.level, begin = time, end = time)
                                    Log.d(TAG, wifiScan.toString())
                                    var r = wifiScanDAO.insert(wifiScan)
                                    Log.d(TAG, "Scan: ${scan.BSSID} into $r")
                                }
                                /*
                                var res = wifiScanDAO.exists(scan.BSSID, lat, lon)
                                if(res.isNotEmpty()){
                                    wifiScanDAO.updateTime(time, scan.BSSID, lat, lon)
                                    Log.d(TAG, "Updated: ${scan.BSSID} into ${res[0].id}")
                                } else {
                                    var wiFiScan = WiFiScan(bssid = scan.BSSID, lat = lat, lon = lon,
                                            intensity = scan.level, begin = time, end = time)
                                    var r = wifiScanDAO.insert(wiFiScan)
                                    Log.d(TAG, "Scan: ${scan.BSSID} into $r")
                                }*/
                            } else {
                                if(wifiScanDAO.active(scan.BSSID).isNotEmpty()){
                                    wifiScanDAO.setInactive(scan.BSSID)
                                }
                            }
                        }

                    }
                    btReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context, intent: Intent) {
                            val action = intent.action
                            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED == action) {
                                Log.d(TAG, "BT discovery started")
                            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                                Log.d(TAG, "BT discovery finished")
                            } else if (BluetoothDevice.ACTION_FOUND == action) {
                                //bluetooth device found
                                val device = intent.getParcelableExtra<Parcelable>(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice?
                                Log.d(TAG, device.toString())
                                val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
                                Log.d(TAG, rssi.toString())
                                GlobalScope.launch(Dispatchers.IO) {
                                    if(rssi <= -82) {
                                        var actives = bluetoothScanDAO.active(device!!.address)
                                        if (actives.isNotEmpty()){
                                            bluetoothScanDAO.updateTime(time, device.address)
                                            Log.d(TAG, "Updatedbt: ${device.address} into ${actives[0].id}")
                                        } else {
                                            var btScan = BluetoothScan(mac = device.address, lat = lat, lon = lon,
                                                    intensity = rssi, begin = time, end = time)
                                            var r = bluetoothScanDAO.insert(btScan)
                                            Log.d(TAG, "BTScan: ${device.address} into $r")
                                        }
                                    } else {
                                        if(bluetoothScanDAO.active(device!!.address).isNotEmpty()){
                                            bluetoothScanDAO.setInactive(device.address)
                                        }
                                    }
                                    /*var actives = bluetoothScanDAO.exists(device!!.address, lat, lon)
                                    if (res.isNotEmpty()) {
                                        bluetoothScanDAO.updateTime(time, device.address, lat, lon)
                                        Log.d(TAG, "Updatedbt: ${device.address} into ${res[0].id}")
                                    } else {
                                        var btScan = BluetoothScan(mac = device.address, lat = lat, lon = lon,
                                                intensity = rssi, begin = time, end = time)
                                        var r = bluetoothScanDAO.insert(btScan)
                                        Log.d(TAG, "BTScan: ${device.address} into $r")
                                    }*/
                                }
                            }
                        }
                    }
                    val filter = IntentFilter()

                    filter.addAction(BluetoothDevice.ACTION_FOUND)
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)

                    registerReceiver(btReceiver, filter)
                    if(btAdapter?.isDiscovering == false && btAdapter?.isEnabled == true) {
                        btAdapter?.startDiscovery()
                    }


                }
                delay(1000 * 15)
            }
        }
        return START_STICKY
    }

    //This will be called when we do stopService() on the main activity

    override fun onDestroy() {
        super.onDestroy()
        locationManager!!.removeUpdates(locationListener)
        unregisterReceiver(btReceiver);
        stopForeground(true)
        Log.d(TAG, "Service stopped")
        powerLock?.let { if (it.isHeld) { it.release() } }
        Toast.makeText(this, "Scanning stopped", Toast.LENGTH_SHORT).show()
    }


    //It's mandatory to implement the onBind function

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //TAG constant for logs
    companion object {
        private const val TAG = "MACexplorer";
    }

    //Function for creating a channel for the notifications

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("MACexplorer channel", name, importance).apply {
                description = descriptionText
                this.enableLights(true)
                this.lightColor = Color.BLUE
                this.enableVibration(true)
                this.vibrationPattern = longArrayOf(100, 200, 300, 200, 100)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        return locationRequest
    }

}

