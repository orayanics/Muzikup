package com.example.muzikup

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import data.Review

class FeedAdapter(
    private var feedResults: List<Review>
) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(feedItem: Review) {
            itemView.findViewById<TextView>(R.id.feedTitle).text = feedItem.track.toString()
            itemView.findViewById<TextView>(R.id.feedArtist).text = feedItem.artist.toString()
            itemView.findViewById<TextView>(R.id.feedContent).text = feedItem.content.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_post, parent, false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val feedItem = feedResults[position]
        holder.bind(feedItem)
    }

    override fun getItemCount(): Int = feedResults.size
}
