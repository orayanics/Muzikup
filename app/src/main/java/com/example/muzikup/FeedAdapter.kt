package com.example.muzikup

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import data.Review

class FeedAdapter(
    private var feedResults: List<Review>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {

   inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), OnClickListener {
       private val feedImageView: ImageView = itemView.findViewById(R.id.feedImage)
       private val titleTextView: TextView = itemView.findViewById(R.id.feedTitle)
       private val artistTextView: TextView = itemView.findViewById(R.id.feedArtist)
       private val contentTextView: TextView = itemView.findViewById(R.id.feedContent)
       private val userTextView: TextView = itemView.findViewById(R.id.feedUsername)
       private val likeButton: ImageButton = itemView.findViewById(R.id.btnHeart)
       private val likeCount : TextView = itemView.findViewById(R.id.countLikes)

       fun bind(feedItem: Review) {
           titleTextView.text = feedItem.track.toString()
           artistTextView.text = feedItem.artist.toString()
           contentTextView.text = feedItem.content.toString()
           userTextView.text = feedItem.username.toString()

           Picasso.get().load(feedItem.image ?: "placeholder")
               .placeholder(R.drawable.greenbtn)
               .error(R.drawable.greenbtn)
               .into(feedImageView)

           likeButton.setOnClickListener {
               val position = adapterPosition
               if (position != RecyclerView.NO_POSITION) {
                   val reviewId = feedResults[position]
                   listener.onItemClick(position, reviewId)
               }
           }

           likeCounter(feedItem)
       }


       private fun likeCounter(feedItem: Review) {
           // Get the like count from your Review object or Firebase Database and set it to the TextView
           val count = feedItem.likes // Replace 'likes' with the appropriate field in your Review object
           likeCount.text = count.toString()
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

    fun updateList(newList: List<Review>) {
        feedResults = newList
        notifyDataSetChanged()
    }
}
