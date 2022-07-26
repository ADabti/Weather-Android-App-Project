package com.example.team29v2.ui.favoritter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.team29v2.R
import com.example.team29v2.data.Tseries
import com.example.team29v2.ui.favoritter.ItemMoveCallback.ItemTouchHelperContract
import java.util.*

class RecyclerViewAdapter(private val data: MutableList<Tseries>,
                          private val callbackInterface:CallbackInterface) :
    RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>(),
    ItemTouchHelperContract {

    inner class MyViewHolder(var rowView: View) : RecyclerView.ViewHolder(
        rowView
    ) {
        // Vi droppet vaer-ikon her til slutt.
        // var badeStedsVaerIkon: ImageView = itemView.findViewById(R.id.FavorittBadestedVaerIkon)
        var badeStedsNavn: TextView = itemView.findViewById(R.id.FavorittBadestedNavn)
        var badeStedsTemperatur: TextView = itemView.findViewById(R.id.FavorittBadestedTemp)
        var removeItem : ImageView = itemView.findViewById(R.id.removeItem)
    }

    // Interface for aa lagre data til disk etter oppdatering.
    interface CallbackInterface {
        fun passResultCallback(message: MutableList<Tseries>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.favorittcard, parent, false)
        return MyViewHolder(itemView)
    }

    // Oppdaterer card med navn, temperatur, vaer-ikon, annen informasjon
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.badeStedsNavn.text = data[position].header?.extra?.name.toString()
        holder.badeStedsTemperatur.text = data[position].forecastDto.properties.timeseries[0].data.instant?.details?.air_temperature.toString() + "°"
        val itemToBeRemoved : Tseries = data[position]

        // Naar man klikker paa en favoritt, sett som valgt badested for detaljvisning.
        /*
        holder.badeStedsVaerIkon.setOnClickListener {
            callbackInterface.onFavorittClicked(data[position])
        }
         */

        holder.removeItem.setOnClickListener {
            removeItem(itemToBeRemoved)
        }

        // holder.badeStedsVaerIkon.setImageResource(images[i])
    }

    private fun removeItem(itemToBeRemoved: Tseries) {
        val position : Int = data.indexOf(itemToBeRemoved)
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
        callbackInterface.passResultCallback(data)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(data, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(data, i, i - 1)
            }
        }

        notifyItemMoved(fromPosition, toPosition)

        // Lagrer den nye listen til disk etter flytting.
        callbackInterface.passResultCallback(data)
    }

    // Eventuelle fargefunksjoner:
    override fun onRowSelected(myViewHolder: MyViewHolder?) {
        //
    }

    override fun onRowClear(myViewHolder: MyViewHolder?) {
        //
    }

}