package com.jaythree.myapplication.dbviewer

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.jaythree.myapplication.R
import com.jaythree.myapplication.db.BluetoothScan
import com.jaythree.myapplication.db.WiFiScan
import java.util.*

class BluetoothScanAdapter(private val ctx: Context, private val scanlist: List<BluetoothScan>) : RecyclerView.Adapter<BluetoothScanAdapter.BluetoothScanHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothScanHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.scan_view, parent, false)
        return BluetoothScanHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: BluetoothScanHolder, position: Int) {
        val sdf = SimpleDateFormat("dd/MMM/yyyy-HH:mm:ss")
        val scan: BluetoothScan = scanlist[position]
        holder.ssid.text = scan.mac
        holder.level.text = scan.intensity.toString()
        var pos = "Lat: ${scan.lat}, Lon: ${scan.lon}"
        holder.position.text = pos
        //var ini = "${scan.begin/60}:${scan.begin%60}-${scan.day}-$month-${scan.year}"
        holder.horaini.text = sdf.format(Date(scan.begin))
        //var fin = "${scan.end/60}:${scan.end%60}-${scan.day}-$month-${scan.year}"
        holder.horafin.text = sdf.format(Date(scan.end))
    }

    override fun getItemCount(): Int {
        return scanlist.size
    }

    /*init {
        this.ctx = ctx
        this.scanlist = scanlist
    }*/

    class BluetoothScanHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ssid: TextView = itemView.findViewById(R.id.ssid)
        var level: TextView = itemView.findViewById(R.id.level)
        var position:TextView = itemView.findViewById(R.id.position)
        var horaini: TextView = itemView.findViewById(R.id.horaini)
        var horafin: TextView = itemView.findViewById(R.id.horafin)

        /*init {
            ssid = itemView.findViewById(R.id.ssid)
            level = itemView.findViewById(R.id.level)
            horaini = itemView.findViewById(R.id.horaini)
            horafin = itemView.findViewById(R.id.horafin)
        }*/

    }
}