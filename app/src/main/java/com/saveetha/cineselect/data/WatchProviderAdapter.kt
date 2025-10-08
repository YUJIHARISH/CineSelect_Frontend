package com.saveetha.cineselect.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.saveetha.cineselect.R

class WatchProviderAdapter(private val providers: List<Provider>) : RecyclerView.Adapter<WatchProviderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_watch_provider, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val provider = providers[position]
        holder.providerName.text = provider.providerName
        holder.providerLogo.load("https://image.tmdb.org/t/p/w500" + provider.logoPath) {
            crossfade(true)
            placeholder(R.drawable.movie_poster_placeholder) // You might want a different placeholder
            error(R.drawable.ic_broken_image)
        }
    }

    override fun getItemCount() = providers.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val providerLogo: ImageView = itemView.findViewById(R.id.ivProviderLogo)
        val providerName: TextView = itemView.findViewById(R.id.tvProviderName)
    }
}