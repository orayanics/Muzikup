package com.example.muzikup

import android.content.ClipData.Item
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.muzikup.Track

class SearchAdapter(
    private var searchResults: List<Track>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnClickListener {
        val trackName: TextView = itemView.findViewById(R.id.textViewTrackName)
        val artistName: TextView = itemView.findViewById(R.id.textViewArtistName)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position, trackName.text.toString(), artistName.text.toString())
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, track: String, artist : String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return SearchViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val currentTrack = searchResults[position]
        holder.trackName.text = currentTrack.name.uppercase()
        holder.artistName.text = currentTrack.artist.uppercase()
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    fun updateData(newSearchResults: List<Track>) {
        searchResults = newSearchResults
        notifyDataSetChanged()
        Log.d("AdapterUpdate", "Data updated. New count: ${searchResults.size}")
    }
}
