package com.jaythree.myapplication.db

import androidx.room.*

@Entity(
    tableName = "bluetoothscan",
    indices = [Index(value = ["mac"], unique = false)])
data class BluetoothScan(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long=0,
    @ColumnInfo(name = "mac") var mac: String,
    @ColumnInfo(name = "lat") var lat: Double,
    @ColumnInfo(name = "lon") var lon: Double,
    @ColumnInfo(name = "intensity") var intensity: Int,
    @ColumnInfo(name = "begin") var begin: Long,
    @ColumnInfo(name = "end") var end: Long,
    @ColumnInfo(name = "flag") var flag: Int){

    constructor(mac: String, lat: Double, lon: Double, intensity: Int, begin: Long, end: Long) :
            this(0L,"",0.0, 0.0,0,0L,0L,0)

}