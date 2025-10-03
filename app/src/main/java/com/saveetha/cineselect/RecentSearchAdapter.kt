package com.saveetha.cineselect.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saveetha.cineselect.R

class RecentSearchAdapter(private val searches: List<String>, private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<RecentSearchAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_search, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val searchQuery = searches[position]
        holder.bind(searchQuery)
    }

    override fun getItemCount(): Int = searches.size

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSearchQuery: TextView = itemView.findViewById(R.id.tvSearchQuery)

        fun bind(query: String) {
            tvSearchQuery.text = query
            itemView.setOnClickListener { onItemClick(query) }
        }
    }
}
