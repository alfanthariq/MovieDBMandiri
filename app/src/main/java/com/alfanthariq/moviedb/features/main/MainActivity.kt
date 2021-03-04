package com.alfanthariq.moviedb.features.main

import alfanthariq.com.signatureapp.util.PreferencesHelper
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.moviedb.R
import com.alfanthariq.moviedb.data.local.model.Genres
import com.alfanthariq.moviedb.data.local.model.Movies
import com.alfanthariq.moviedb.data.local.model.MoviesItem
import com.alfanthariq.moviedb.features.base.BaseActivity
import com.alfanthariq.moviedb.features.common.ErrorView
import com.alfanthariq.moviedb.features.main.adapter.MovieAdapter
import com.alfanthariq.moviedb.features.main.movie.detail.DetailMovieActivity
import com.alfanthariq.moviedb.utils.*
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton
import org.jetbrains.anko.toast
import com.google.android.material.appbar.AppBarLayout

class MainActivity : BaseActivity<MainContract.View,
        MainPresenter>(),
    MainContract.View, ErrorView.ErrorListener {

    private var doubleBackToExitPressedOnce = false
    lateinit var pref_setting : SharedPreferences
    lateinit var pref_profile : SharedPreferences
    private var listenSelect = true
    private var selectedGenres = ""
    lateinit var movieAdapter : MovieAdapter
    private var movieData : MutableList<MoviesItem?> = mutableListOf()
    private var dataPage = 1
    private var positionIndex: Int = 0
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    private var totalPage = 0
    private lateinit var slideUp: SlideUp
    lateinit var txtBadge : TextView
    lateinit var gridLayout : LinearLayoutManager

    override fun nightMode(): Int = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    override var mPresenter: MainPresenter
        get() = MainPresenter(this)
        set(value) {}

    override fun layoutId(): Int = R.layout.activity_main

    override fun getTagClass(): String = javaClass.simpleName

    override fun onReloadData() {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)

        val menuItem = menu.findItem(R.id.action_filter)
        val actionView = menuItem.actionView
        txtBadge = actionView.findViewById(R.id.badge)

        actionView.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }
//        val drawable = menu.getItem(0).icon
//        if(drawable != null) {
//            drawable.mutate()
//            drawable.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP)
//        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                slideUp.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun afterGetGenres(genres: Genres) {
        genresGroup.removeAllViews()
        genresGroup.selectableAmount = genres.genres!!.size
        val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics).toInt()
        val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()
        val layParam = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        genres.genres.forEach { item ->
            val btn = ThemedButton(this)
            btn.id = ViewCompat.generateViewId()
            btn.text = item?.name!!
            btn.tag = item.id
            btn.selectedBgColor = ContextCompat.getColor(this, R.color.selectedToggle)
            btn.textColor = ContextCompat.getColor(this, R.color.iconTextColorRevert)
            btn.bgColor = ContextCompat.getColor(this, R.color.bgToggle)

            val dimenFontSize = resources.getDimension(R.dimen.toggleFontSize) / resources.displayMetrics.density

            btn.tvText.setPadding(padding)
            btn.tvText.textSize = dimenFontSize
            btn.tvSelectedText.setPadding(padding)
            btn.tvSelectedText.textSize = dimenFontSize
            println("Dimen : $dimenFontSize")

            layParam.setMargins(margin, margin, margin, margin)
            genresGroup.addView(btn, layParam)
        }
        mPresenter.getMovies(selectedGenres, dataPage.toString())
    }

    override fun afterGetMovies(movies: Movies) {
        movieAdapter.removeLoading()
        movieAdapter.setIsLoading(false)
        totalPage = movies.totalPages!!
        if (dataPage == 1) {
            movieData.clear()
        }
        movieData.addAll(movies.results!!)
        movieAdapter.notifyDataSetChanged()
        toggleEmpty()
        hideLoadingDialog()
    }

    override fun failedRequest(err_msg: String) {
        hideLoadingDialog()
        toggleEmpty()
        toast(err_msg)
    }

    override fun onBackPressed() {
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Press BACK again to exit.", Toast.LENGTH_SHORT).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        } else {
            super.onBackPressed()
            return
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
    }

    fun init(){
        pref_setting = PreferencesHelper.getSettingPref(this)
        pref_profile = PreferencesHelper.getProfilePref(this)
        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        setupRecycler()

        btnApply.setOnClickListener {
            val selButton = genresGroup.selectedButtons
            val ids = ArrayList<Int>()
            selButton.forEach {
                ids.add(it.tag as Int)
            }
            selectedGenres = TextUtils.join(",", ids)
            if (selButton.isNotEmpty()) txtBadge.visible() else txtBadge.gone()
            txtBadge.text = selButton.size.toString()
            slideUp.hide()
            showLoadingDialog("Getting data ...")
            scrollListener?.resetState()
            dataPage = 1
            mPresenter.getMovies(selectedGenres, dataPage.toString())
        }

        txtClearSelection.setOnClickListener {
            listenSelect = false

            for (btn : ThemedButton in ArrayList<ThemedButton>(genresGroup.selectedButtons)) {
                genresGroup.selectButtonWithAnimation(btn)
            }

            listenSelect = true
        }

        dim.setOnClickListener { if (slideUp.isVisible) slideUp.hide() }

        slideUp = SlideUpBuilder(contentGenres)
            .withListeners(object : SlideUp.Listener.Events {
                override fun onSlide(percent: Float) {
                    dim.visibility = View.VISIBLE
                    dim.alpha = 0.7f - percent / 100
                }

                override fun onVisibilityChanged(visibility: Int) {
                    if (visibility == View.GONE) {
                        dim.alpha = 0f
                        dim.visibility = View.GONE
                        dim.isClickable = false
                    } else {
                        dim.visibility = View.VISIBLE
                        dim.isClickable = true
                    }
                }
            })
            .withStartGravity(Gravity.BOTTOM)
            .withLoggingEnabled(true)
            .withGesturesEnabled(true)
            .withStartState(SlideUp.State.HIDDEN)
            .build()

        appBar.addOnOffsetChangedListener(object : AppBarStateChangedListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                if (state == State.COLLAPSED) fab_up.show() else fab_up.hide()
            }
        })

        fab_up.setOnClickListener {
            appBar.setExpanded(true, true)
            gridLayout.smoothScrollToPosition(rec_movies, null, 0)
        }

        showLoadingDialog("Loading ...")
        mPresenter.getGenres()
    }

    fun toggleEmpty(){
        if (movieData.isEmpty()) empty_view.visible() else empty_view.gone()
    }

    fun setupRecycler(){
        movieAdapter = MovieAdapter(movieData as ArrayList<MoviesItem?>, this, {
            println("Movies : $it")
            val map = HashMap<String, String>()
            map["movie_id"] = it.id.toString()
            AppRoute.open(this, DetailMovieActivity::class.java, map)
        }, { searchCount ->

        })

        rec_movies.apply {
            gridLayout = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            layoutManager = gridLayout
            adapter = movieAdapter
        }

        scrollListener = object : EndlessRecyclerViewScrollListener(gridLayout) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                if (dataPage < totalPage) {
                    movieAdapter.addLoading()
                    movieAdapter.setIsLoading(true)
                    dataPage += 1
                    mPresenter.getMovies(selectedGenres, dataPage.toString())
                }
            }
        }

        rec_movies.addOnScrollListener(scrollListener!!)

        toggleEmpty()
    }

    override fun onPause() {
        super.onPause()

        positionIndex = (rec_movies.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    override fun onResume() {
        super.onResume()
        toggleEmpty()
        rec_movies.scrollToPosition(positionIndex)
    }
}
