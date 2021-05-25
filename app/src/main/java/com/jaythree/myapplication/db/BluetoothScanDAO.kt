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
            "SET end = :time " +
            "WHERE mac = :mac " +
            "AND lat = :lat " +
            "AND lon = :lon")
    suspend fun updateTime(time: Int, mac: String, lat: Double, lon: Double)

    @Query("SELECT * FROM bluetoothscan " +
            "WHERE mac = :mac " +
            "AND lat = :lat " +
            "AND lon = :lon ")
    suspend fun exists(mac: String, lat: Double, lon: Double): List<BluetoothScan>

    @RawQuery
    suspend fun executeQuery(query: SupportSQLiteQuery): List<BluetoothScan>


}