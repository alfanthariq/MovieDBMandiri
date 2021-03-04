package com.alfanthariq.moviedb.features.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.moviedb.R
import com.alfanthariq.moviedb.data.local.model.ReviewsItem
import com.alfanthariq.moviedb.utils.DateOperationUtil
import com.bumptech.glide.Glide

class ReviewsAdapter (val items : ArrayList<ReviewsItem?>,
                      val context : Context,
                      val callback : (ReviewsItem) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_LOADING = 1
    private val VIEW_TYPE_ITEM = 0

    fun addLoading() {
        val lastIdx = items.lastIndex
        if (items[lastIdx] != null) {
            items.add(null)
            notifyItemInserted(lastIdx+1)
        }
    }

    fun removeLoading() {
        if (items.isNotEmpty()) {
            val lastIdx = items.lastIndex
            if (items[lastIdx] == null) {
                items.removeAt(lastIdx)
                notifyItemRemoved(lastIdx)
            }
        }
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt_author = itemView.findViewById<TextView>(R.id.txtAuthor)
        val txt_rating = itemView.findViewById<TextView>(R.id.txtRating)
        val txt_content = itemView.findViewById<TextView>(R.id.txtContent)
        val img_profile = itemView.findViewById<ImageView>(R.id.imgProfile)
        val txt_tgl = itemView.findViewById<TextView>(R.id.txtTgl)
        val container = itemView.findViewById<LinearLayout>(R.id.container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        lateinit var v : View
        vh = if (viewType == VIEW_TYPE_ITEM) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
            ReviewViewHolder(v)
        } else {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            LoadingHolder(v)
        }
        return vh
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ReviewViewHolder) {
            val data = items[position]
            holder.txt_author.text = "Reviewed by ${data?.author}"
            holder.txt_rating.text = "${data?.authorDetails?.rating.toString()}/10"
            holder.txt_content.text = data?.content?.trim()
            holder.txt_tgl.text = DateOperationUtil.dateStrFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd/MM/yyyy HH:mm:ss", data?.createdAt!!)

            Glide.with(context)
                .load("http://image.tmdb.org/t/p/w92${data.authorDetails?.avatarPath}")
                .placeholder(R.drawable.user)
                .into(holder.img_profile)

            if (position % 2 == 0) {
                holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.bgColor1))
            } else {
                holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.bgColor2))
            }

            holder.container.setOnClickListener {
                callback(data)
            }
        }
    }

    inner class LoadingHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun getItemViewType(position: Int): Int {
        var viewType = if (items[position] == null) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }

        return viewType
    }
}