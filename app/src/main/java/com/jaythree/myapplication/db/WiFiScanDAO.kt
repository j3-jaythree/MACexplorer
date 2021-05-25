package com.jaythree.myapplication.db


import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

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

    @RawQuery
    suspend fun executeQuery(query: SupportSQLiteQuery): List<WiFiScan>

}