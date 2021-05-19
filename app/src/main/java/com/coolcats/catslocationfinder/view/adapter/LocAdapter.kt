package com.coolcats.catslocationfinder.view.adapter

import android.graphics.Bitmap
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coolcats.catslocationfinder.R
import com.coolcats.catslocationfinder.model.ResultX
import com.coolcats.catslocationfinder.network.LocNetwork
import com.coolcats.catslocationfinder.util.Utilities.Companion.myLog
import kotlinx.android.synthetic.main.location_list_item.view.*

class LocAdapter : RecyclerView.Adapter<LocAdapter.LocViewHolder>() {

    private var list: List<ResultX> = listOf()

    inner class LocViewHolder(view: View) : RecyclerView.ViewHolder(view)

    fun updateList(list: List<ResultX>) {
        this.list = emptyList()
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.location_list_item, parent, false)
        return LocViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LocViewHolder, position: Int) {
        myLog("${list[position].photos}")
        list[position].let {
            holder.itemView.apply {
                location_name_txt.text = it.name
                location_latlng_txt.text = it.vicinity
            }
        }
    }

    override fun getItemCount(): Int = list.size

}