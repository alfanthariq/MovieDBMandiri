package com.alfanthariq.moviedb.features.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.moviedb.R
import com.alfanthariq.moviedb.data.local.model.ReviewsItem
import com.bumptech.glide.Glide

class ReviewsSmallAdapter (val items : ArrayList<ReviewsItem?>,
                           val context : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class RevViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt_author = itemView.findViewById<TextView>(R.id.txtAuthor)
        val txt_rating = itemView.findViewById<TextView>(R.id.txtRating)
        val txt_content = itemView.findViewById<TextView>(R.id.txtContent)
        val img_profile = itemView.findViewById<ImageView>(R.id.imgProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_review_small, parent, false)
        vh = RevViewHolder(v)
        return vh
    }

    override fun getItemCount(): Int {
        return if (items.size > 5) 5 else items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RevViewHolder) {
            val data = items[position]
            holder.txt_author.text = "Reviewed by ${data?.author}"
            holder.txt_rating.text = "${data?.authorDetails?.rating.toString()}/10"
            holder.txt_content.text = data?.content

            Glide.with(context)
                .load("http://image.tmdb.org/t/p/w92${data?.authorDetails?.avatarPath}")
                .placeholder(R.drawable.user)
                .into(holder.img_profile)
        }
    }
}