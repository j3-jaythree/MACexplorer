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
            "AND flag = 1")
    suspend fun updateTime(time: Long, bssid: String)

    @Query("SELECT * FROM wifiscan " +
            "WHERE bssid = :bssid " +
            "AND lat = :lat " +
            "AND lon = :lon ")
    suspend fun exists(bssid: String, lat: Double, lon: Double): List<WiFiScan>

    @Query("SELECT * FROM wifiscan " +
            "WHERE bssid = :bssid " +
            "AND flag = 1")
    suspend fun active(bssid: String): List<WiFiScan>

    @Query("UPDATE wifiscan " +
            "SET flag = 0 " +
            "WHERE bssid = :bssid " +
            "AND flag = 1 ")
    suspend fun setInactive(bssid: String)

    @RawQuery
    suspend fun executeQuery(query: SupportSQLiteQuery): List<WiFiScan>

}