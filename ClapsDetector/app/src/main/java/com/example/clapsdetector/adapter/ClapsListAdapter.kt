package com.example.clapsdetector.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clapsdetector.R
import com.example.clapsdetector.model.Clap
import kotlinx.android.synthetic.main.item_clap_data.view.*
import java.util.*

class ClapsListAdapter(): RecyclerView.Adapter<ClapsListAdapter.ClapsViewHolder>(){

    private var items = LinkedList<Clap>()

    fun addItem(clap: Clap){
        items.add(clap)
        notifyItemInserted(items.size-1)
    }

    fun setItems(claps: Collection<Clap>){
        items.clear()
        items.addAll(claps)
        notifyDataSetChanged()
    }

    fun removeItems(){
        val lastIndex = items.size-1
        items.clear()
        notifyItemRangeRemoved(0, lastIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClapsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_clap_data, parent, false)
        val clapsViewHoder =
            ClapsViewHolder(view)
        return clapsViewHoder
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ClapsViewHolder, position: Int){
        val adapterPosition = holder.adapterPosition
        if (holder.adapterPosition != RecyclerView.NO_POSITION){
            holder.bind(items[adapterPosition])
        }
    }

    class ClapsViewHolder(view: View): RecyclerView.ViewHolder(view){
        var clapTimeTextView = view.clapTime
        var clapIntensityTextView = view.clapIntensity

        fun bind(data: Clap){
            clapTimeTextView.setText(data.time)
            clapIntensityTextView.setText(data.intensity.toString())
        }
    }


}