package com.saveetha.cineselect.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.saveetha.cineselect.R

class WatchlistAdapter(private val movies: MutableList<MovieDetails>, private val onRemoveClick: (MovieDetails) -> Unit) :
    RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_watchlist_movie, parent, false)
        return WatchlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: WatchlistViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = movies.size

    fun removeItem(movie: MovieDetails) {
        val position = movies.indexOf(movie)
        if (position > -1) {
            movies.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    inner class WatchlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivMoviePoster: ImageView = itemView.findViewById(R.id.ivMoviePoster)
        private val tvMovieTitle: TextView = itemView.findViewById(R.id.tvMovieTitle)
        private val tvMovieYear: TextView = itemView.findViewById(R.id.tvMovieYear)
        private val tvMovieOverview: TextView = itemView.findViewById(R.id.tvMovieOverview)
        private val ivRemove: ImageButton = itemView.findViewById(R.id.ivRemove)

        fun bind(movie: MovieDetails) {
            tvMovieTitle.text = movie.title
            tvMovieYear.text = movie.releaseDate?.split("-")?.firstOrNull() ?: "N/A"
            tvMovieOverview.text = movie.overview

            ivMoviePoster.load(movie.posterPath) {
                crossfade(true)
                placeholder(R.drawable.movie_poster_placeholder)
                error(R.drawable.movie_poster_placeholder)
            }

            ivRemove.setOnClickListener { onRemoveClick(movie) }
        }
    }
}
