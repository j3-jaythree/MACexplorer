package com.jaythree.myapplication.db

import android.content.Context
import androidx.room.*

@Database(entities = [WiFiScan::class, BluetoothScan::class], version = 1)
abstract class DataBase : RoomDatabase() {

    abstract fun wifiscanDAO(): WiFiScanDAO
    abstract fun bluetoothscanDAO(): BluetoothScanDAO

    companion object {
        private var DB: DataBase? = null
        private const val NAME = "scans.db"

        fun getDatabase(context: Context): DataBase {
            if (DB == null) {
                synchronized(DataBase::class.java) {
                    if (DB == null) {
                        DB = Room.databaseBuilder(context.applicationContext,
                            DataBase::class.java, NAME).build()
                    }
                }
            }
            return DB!!
        }
    }

}