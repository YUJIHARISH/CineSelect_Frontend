package com.saveetha.cineselect.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.saveetha.cineselect.R

class CastAdapter(private val castMembers: List<CastMember>) :
    RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cast_member, parent, false)
        return CastViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        val castMember = castMembers[position]
        holder.bind(castMember)
    }

    override fun getItemCount(): Int = castMembers.size

    inner class CastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCastImage: ImageView = itemView.findViewById(R.id.ivCastImage)
        private val tvCastName: TextView = itemView.findViewById(R.id.tvCastName)

        fun bind(castMember: CastMember) {
            tvCastName.text = castMember.name
            ivCastImage.load(castMember.profilePath) {
                crossfade(true)
                placeholder(R.drawable.ic_profile)
                error(R.drawable.ic_profile) // Show placeholder on error
            }
        }
    }
}
