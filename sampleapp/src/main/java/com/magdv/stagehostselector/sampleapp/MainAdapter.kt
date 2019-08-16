package com.magdv.stagehostselector.sampleapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.magdv.stagehostselector.sampleapp.network.HttpLog
import kotlinx.android.synthetic.main.item_http_log.view.*
import java.text.SimpleDateFormat
import java.util.Locale

class MainAdapter : ListAdapter<HttpLog, MainAdapter.ViewHolder>(MainItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_http_log, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(httpLog: HttpLog) {
            view.urlTextView.text = httpLog.url
            view.timeTextView.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(httpLog.time)
        }
    }
}