package com.alfanthariq.moviedb.features.main.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.moviedb.R
import com.alfanthariq.moviedb.data.local.model.MoviesItem
import com.alfanthariq.moviedb.utils.DateOperationUtil
import com.bumptech.glide.Glide
import java.util.*
import kotlin.collections.ArrayList

class MovieAdapter (val items : ArrayList<MoviesItem?>,
                    val context : Context,
                    val callback : (MoviesItem) -> Unit,
                    val callbackFilter : (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private val VIEW_TYPE_LOADING = 1
    private val VIEW_TYPE_ITEM = 0
    private var isLoading = false

    var itemsFiltered : ArrayList<MoviesItem?> = ArrayList()

    init {
        itemsFiltered = items
    }

    fun setIsLoading(value:Boolean) {
        isLoading = value
    }

    fun addLoading() {
        val lastIdx = items.lastIndex
        if (items[lastIdx] != null) {
            items.add(null)
            itemsFiltered = items
            notifyItemInserted(lastIdx+1)
        }
    }

    fun removeLoading() {
        if (items.isNotEmpty()) {
            val lastIdx = items.lastIndex
            if (items[lastIdx] == null) {
                items.removeAt(lastIdx)
                itemsFiltered = items
                notifyItemRemoved(lastIdx)
                //notifyDataSetChanged()
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                itemsFiltered = if (charSearch.isEmpty()) {
                    items
                } else {
                    val resultList = ArrayList<MoviesItem?>()
                    for (row in items) {
                        if (row?.title?.toLowerCase(Locale.ROOT)!!.contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = itemsFiltered
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                itemsFiltered = results?.values as ArrayList<MoviesItem?>
                callbackFilter(itemsFiltered.size)
                notifyDataSetChanged()
            }
        }
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt_judul = itemView.findViewById<TextView>(R.id.txtJudul)
        val txt_release = itemView.findViewById<TextView>(R.id.txtReleaseDate)
        val txt_overview = itemView.findViewById<TextView>(R.id.txtOverview)
        val container = itemView.findViewById<LinearLayout>(R.id.container)
        val img_poster = itemView.findViewById<ImageView>(R.id.imgPoster)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        lateinit var v : View
        vh = if (viewType == VIEW_TYPE_ITEM) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
            MovieViewHolder(v)
        } else {
            v = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            LoadingHolder(v)
        }
        return vh
    }

    override fun getItemCount(): Int = itemsFiltered.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) {
            val data = itemsFiltered[position]
            holder.txt_judul.text = data?.title
            holder.txt_overview.text = data?.overview
            holder.txt_release.text = "Release date : ${DateOperationUtil.dateStrFormat("yyyy-MM-dd", "dd/MM/yyyy", data?.releaseDate!!)}"

            Glide.with(context)
                .load("http://image.tmdb.org/t/p/w92${data?.posterPath}")
                .into(holder.img_poster)

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
        var viewType = if (itemsFiltered[position] == null) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }

        return viewType
    }
}