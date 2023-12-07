package com.example.muzikup

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import data.Review

class FeedAdapter(
    private var feedResults: List<Review>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

   inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnClickListener {
       fun bind(feedItem: Review) {
            try{
                itemView.findViewById<TextView>(R.id.feedTitle).text = feedItem.track.toString()
                itemView.findViewById<TextView>(R.id.feedArtist).text = feedItem.artist.toString()
                itemView.findViewById<TextView>(R.id.feedContent).text = feedItem.content.toString()
            } catch (e: Exception){
                Log.e("Firebase", e.toString())
            }
        }

       init {
           itemView.setOnClickListener(this)
       }

       override fun onClick(v: View?) {
           val position = adapterPosition
           if (position != RecyclerView.NO_POSITION) {
               val reviewId = feedResults[position]
               listener.onItemClick(position, reviewId)
           }
       }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, review: Review)
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
