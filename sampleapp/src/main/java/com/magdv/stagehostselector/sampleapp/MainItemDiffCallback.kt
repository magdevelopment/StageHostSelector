package com.magdv.stagehostselector.sampleapp

import android.support.v7.util.DiffUtil
import com.magdv.stagehostselector.sampleapp.network.HttpLog

class MainItemDiffCallback : DiffUtil.ItemCallback<HttpLog>() {
    override fun areItemsTheSame(oldItem: HttpLog, newItem: HttpLog): Boolean {
        return newItem.time == oldItem.time
    }

    override fun areContentsTheSame(oldItem: HttpLog, newItem: HttpLog): Boolean {
        return true
    }
}