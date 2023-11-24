package com.example.muzikup

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.muzikup.Track

class SearchAdapter(private var searchResults: List<Track>) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackName: TextView = itemView.findViewById(R.id.textViewTrackName)
        val artistName: TextView = itemView.findViewById(R.id.textViewArtistName)
        val albumName: TextView = itemView.findViewById(R.id.textViewAlbumName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return SearchViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val currentTrack = searchResults[position]

        holder.trackName.text = currentTrack.name
        holder.artistName.text = currentTrack.artist
        Log.d("TrackAdapter", "Album Name: ${currentTrack.album?.name}")
        if (currentTrack.album != null && !currentTrack.album.name.isNullOrBlank()) {
            holder.albumName.text = currentTrack.album.name
        } else {
            // Handle the case where the album name is null or empty
            holder.albumName.text = "Unknown Album"
        }


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
