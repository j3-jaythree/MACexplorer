package com.jaythree.myapplication.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface BluetoothScanDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bluetootscan: BluetoothScan): Long

    @Query("SELECT * FROM bluetoothscan")
    suspend fun selectAll(): List<BluetoothScan>

    @Query("UPDATE bluetoothscan " +
            "SET end = :time, " +
            "intensity = :intensity " +
            "WHERE mac = :mac " +
            "AND flag = 1")
    suspend fun updateTime(time: Long, intensity: Int, mac: String)

    @Query("SELECT * FROM bluetoothscan " +
            "WHERE mac = :mac " +
            "AND lat = :lat " +
            "AND lon = :lon ")
    suspend fun exists(mac: String, lat: Double, lon: Double): List<BluetoothScan>

    @Query("SELECT * FROM bluetoothscan " +
            "WHERE mac = :mac " +
            "AND flag = 1")
    suspend fun active(mac: String): List<BluetoothScan>

    @Query("UPDATE bluetoothscan " +
            "SET flag = 0 " +
            "WHERE mac = :mac " +
            "AND flag = 1")
    suspend fun setInactive(mac: String)

    @RawQuery
    suspend fun executeQuery(query: SupportSQLiteQuery): List<BluetoothScan>


}