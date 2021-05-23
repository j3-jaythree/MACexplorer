package com.jaythree.myapplication.db

import androidx.room.*

@Entity(
    tableName = "bluetoothscan",
    indices = [Index(value = ["mac"], unique = true)])
data class BluetoothScan(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long=0,
    @ColumnInfo(name = "mac") var mac: String,
    @ColumnInfo(name = "lat") var lat: Double,
    @ColumnInfo(name = "lon") var lon: Double,
    @ColumnInfo(name = "intensity") var intensity: Int,
    @ColumnInfo(name = "day") var day: Int,
    @ColumnInfo(name = "month") var month: Int,
    @ColumnInfo(name = "year") var year: Int,
    @ColumnInfo(name = "begin") var begin: Int,
    @ColumnInfo(name = "end") var end: Int){

    constructor() : this(0L,"",0.0, 0.0,0,0,0,0,0,0)

}