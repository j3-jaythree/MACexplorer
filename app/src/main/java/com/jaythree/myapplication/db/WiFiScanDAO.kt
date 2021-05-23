package com.jaythree.myapplication.db


import android.provider.MediaStore
import androidx.room.*

@Dao
interface WiFiScanDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(wifiscan: WiFiScan): Long

    @Query("SELECT * FROM wifiscan")
    suspend fun selectAll(): List<WiFiScan>

    @Query("UPDATE wifiscan " +
            "SET end = :time " +
            "WHERE bssid = :bssid " +
            "AND lat = :lat " +
            "AND lon = :lon")
    suspend fun updateTime(time: Int, bssid: String, lat: Double, lon: Double)

    @Query("SELECT * FROM wifiscan " +
            "WHERE bssid = :bssid " +
            "AND lat = :lat " +
            "AND lon = :lon ")
    suspend fun exists(bssid: String, lat: Double, lon: Double): List<WiFiScan>

}