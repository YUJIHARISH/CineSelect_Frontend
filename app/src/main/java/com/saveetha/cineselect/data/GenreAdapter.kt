package com.saveetha.cineselect.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.saveetha.cineselect.R

class GenreAdapter(
    private var genres: MutableList<Genre>,
    private val onGenreClicked: (Genre) -> Unit
) : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    var selectedGenres = setOf<Genre>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val chip = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_genre_chip, parent, false) as Chip
        return GenreViewHolder(chip)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genres[position]
        holder.bind(genre, selectedGenres.contains(genre))
    }

    override fun getItemCount(): Int = genres.size

    fun updateData(newGenres: List<Genre>) {
        genres.clear()
        genres.addAll(newGenres)
        notifyDataSetChanged()
    }

    inner class GenreViewHolder(private val chip: Chip) : RecyclerView.ViewHolder(chip) {
        fun bind(genre: Genre, isSelected: Boolean) {
            chip.text = genre.name
            chip.isChecked = isSelected
            chip.setOnClickListener { onGenreClicked(genre) }
        }
    }
}
