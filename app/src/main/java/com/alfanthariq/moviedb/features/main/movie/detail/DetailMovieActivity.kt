package com.alfanthariq.moviedb.features.main.movie.detail

import android.graphics.PorterDuff
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import com.alfanthariq.moviedb.R
import com.alfanthariq.moviedb.data.local.model.MovieDetail
import com.alfanthariq.moviedb.data.local.model.Trailers
import com.alfanthariq.moviedb.features.base.BaseActivity
import com.alfanthariq.moviedb.features.common.ErrorView
import com.alfanthariq.moviedb.utils.DateOperationUtil
import com.alfanthariq.moviedb.utils.visible
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_detail_movie.*
import org.jetbrains.anko.toast
import android.webkit.WebView
import android.webkit.WebViewClient
import android.annotation.SuppressLint
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.moviedb.data.local.model.Reviews
import com.alfanthariq.moviedb.data.local.model.ReviewsItem
import com.alfanthariq.moviedb.features.main.adapter.ReviewsSmallAdapter
import com.alfanthariq.moviedb.features.main.movie.reviews.ReviewsFragment
import com.alfanthariq.moviedb.utils.gone

class DetailMovieActivity : BaseActivity<DetailMovieContract.View,
        DetailMoviePresenter>(),
    DetailMovieContract.View, ErrorView.ErrorListener {

    private var movieId : String? = null
    lateinit var reviewAdapter : ReviewsSmallAdapter
    private var reviews = ArrayList<ReviewsItem?>()

    override var mPresenter: DetailMoviePresenter
        get() = DetailMoviePresenter(this)
        set(value) {}

    override fun layoutId(): Int = R.layout.activity_detail_movie

    override fun getTagClass(): String = javaClass.simpleName

    override fun nightMode(): Int = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    override fun afterGetDetailMovies(movies: MovieDetail) {
        hideLoadingDialog()
        title = "${movies.title} (${DateOperationUtil.dateStrFormat("yyyy-mm-dd", "yyyy", movies.releaseDate!!)})"

        Glide.with(this).load("http://image.tmdb.org/t/p/w185${movies.posterPath}").into(imgPoster)
        println("URL : http://image.tmdb.org/t/p/w185${movies.posterPath}")
        txtJudul.text = movies.title
        txtYear.text = DateOperationUtil.dateStrFormat("yyyy-mm-dd", "yyyy", movies.releaseDate!!)
        val genres = ArrayList<String>()
        movies.genres?.forEach {
            genres.add(it?.name!!)
        }
        txtGenre.text = TextUtils.join(" / ", genres)
        txtOverview.text = movies.overview
        txtStatus.text = movies.status

        val countries = ArrayList<String>()
        movies.productionCountries?.forEach {
            countries.add(it?.iso31661!!.toLowerCase().toFlagEmoji())
        }
        txtCountry.text = "Country : \n${TextUtils.join("  ", countries)}"

        val lang = ArrayList<String>()
        movies.spokenLanguages?.forEach {
            lang.add(it?.name!!)
        }
        txtLanguages.text = "Spoken language(s) : \n${TextUtils.join(", ", lang)}"

        txtStatus.visible()
        txtRating.text = movies.voteAverage.toString()
        containerBox.visible()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun afterGetTrailers(trailer: Trailers) {
        if (trailer.results!!.isNotEmpty()) {
            if (trailer.results[0]?.site?.toLowerCase() == "youtube") {
                youtube_web_view.setBackgroundColor(Color.TRANSPARENT)
                youtube_web_view.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        return false
                    }
                }

                val webSettings = youtube_web_view.settings
                webSettings.javaScriptEnabled = true
                webSettings.loadWithOverviewMode = true
                webSettings.useWideViewPort = true

                youtube_web_view.loadUrl("https://www.youtube.com/embed/${trailer.results[0]?.key}")
                youtube_web_view.visible()
                txtEmptyTrailer.gone()
            } else {
                youtube_web_view.gone()
                txtEmptyTrailer.visible()
                txtEmptyTrailer.text = "Not available"
            }
        } else {
            youtube_web_view.gone()
            txtEmptyTrailer.visible()
            txtEmptyTrailer.text = "Not available"
        }
    }

    override fun afterGetReviews(review: Reviews) {
        reviews.clear()
        reviews.addAll(review.results!!.toMutableList())
        reviewAdapter.notifyDataSetChanged()
        toggleEmpty()
    }

    fun String.toFlagEmoji(): String {
        // 1. It first checks if the string consists of only 2 characters: ISO 3166-1 alpha-2 two-letter country codes (https://en.wikipedia.org/wiki/Regional_Indicator_Symbol).
        if (this.length != 2) {
            return this
        }

        val countryCodeCaps = this.toUpperCase() // upper case is important because we are calculating offset
        val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

        // 2. It then checks if both characters are alphabet
        if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
            return this
        }

        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }

    override fun failedRequest(err_msg: String) {
        hideLoadingDialog()
        toast(err_msg)
    }

    override fun onReloadData() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra("movie_id")) movieId = intent.getStringExtra("movie_id")

        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun init(){
        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_back_white)
        toolbar.navigationIcon!!.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP)
        title = "Movie details"

        showLoadingDialog("Getting detail data ...")
        if (movieId != null) {
            mPresenter.getDetailMovie(movieId!!)
            mPresenter.getTrailers(movieId!!)
            mPresenter.getReviews(movieId!!)
        }

        txtSeeReviews.setOnClickListener {
            val reviewDialog = ReviewsFragment()
            val ft = supportFragmentManager.beginTransaction()
            val b = Bundle()
            b.putString("movie_id", movieId)
            reviewDialog.arguments = b
            reviewDialog.show(ft, "DialogFragment")
        }

        setupRecycler()
    }

    fun setupRecycler(){
        reviewAdapter = ReviewsSmallAdapter(reviews, this)

        recReviews.apply {
            val gridLayout = LinearLayoutManager(this@DetailMovieActivity, RecyclerView.HORIZONTAL, false)
            layoutManager = gridLayout
            adapter = reviewAdapter
        }

        toggleEmpty()
    }

    fun toggleEmpty(){
        if (reviews.isEmpty()) {
            txtEmptyReview.visible()
            txtSeeReviews.gone()
        } else {
            txtEmptyReview.gone()
            txtSeeReviews.visible()
        }
    }
}
