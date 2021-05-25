package com.jaythree.myapplication.dbviewer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jaythree.myapplication.R
import com.jaythree.myapplication.db.BluetoothScan
import com.jaythree.myapplication.db.WiFiScan

class BluetoothScanAdapter(private val ctx: Context, private val scanlist: List<BluetoothScan>) : RecyclerView.Adapter<BluetoothScanAdapter.BluetoothScanHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothScanHolder {
        val view: View = LayoutInflater.from(ctx).inflate(R.layout.scan_view, parent, false)
        return BluetoothScanHolder(view)
    }

    override fun onBindViewHolder(holder: BluetoothScanHolder, position: Int) {
        val scan: BluetoothScan = scanlist[position]
        holder.ssid.text = scan.mac
        holder.level.text = scan.intensity.toString()
        var pos = "Lat: ${scan.lat}, Lon: ${scan.lon}"
        holder.position.text = pos
        var month = scan.month+1
        var ini = "${scan.begin/60}:${scan.begin%60}-${scan.day}-$month-${scan.year}"
        holder.horaini.text = ini
        var fin = "${scan.end/60}:${scan.end%60}-${scan.day}-$month-${scan.year}"
        holder.horafin.text = fin
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