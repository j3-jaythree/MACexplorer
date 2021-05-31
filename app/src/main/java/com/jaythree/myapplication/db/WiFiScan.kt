package com.jaythree.myapplication.db

import androidx.room.*

@Entity(
    tableName = "wifiscan",
    indices = [Index(value = ["bssid"], unique = false)])
data class WiFiScan(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long = 0,
    @ColumnInfo(name = "bssid") var bssid: String,
    @ColumnInfo(name = "lat") var lat: Double,
    @ColumnInfo(name = "lon") var lon: Double,
    @ColumnInfo(name = "intensity") var intensity: Int,
    @ColumnInfo(name = "begin") var begin: Long,
    @ColumnInfo(name = "end") var end: Long,
    @ColumnInfo(name = "flag") var flag: Int){

    constructor(bssid: String, lat: Double, lon: Double, intensity: Int, begin: Long, end: Long) :
            this(0L,bssid,lat, lon,intensity,begin,end, 1)

}