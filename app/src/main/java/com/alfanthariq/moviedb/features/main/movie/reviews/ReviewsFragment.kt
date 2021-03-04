package com.alfanthariq.moviedb.features.main.movie.reviews


import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.alfanthariq.moviedb.R
import com.alfanthariq.moviedb.data.local.model.Reviews
import com.alfanthariq.moviedb.data.local.model.ReviewsItem
import com.alfanthariq.moviedb.features.base.BaseFragment
import com.alfanthariq.moviedb.features.main.adapter.ReviewsAdapter
import com.alfanthariq.moviedb.utils.EndlessRecyclerViewScrollListener
import com.alfanthariq.moviedb.utils.TOAST
import kotlinx.android.synthetic.main.fragment_reviews.*

class ReviewsFragment : BaseFragment<ReviewsContract.View, ReviewsPresenter>(),
    ReviewsContract.View  {

    private var movieId = ""
    private var reviews = ArrayList<ReviewsItem?>()
    lateinit var reviewAdapter : ReviewsAdapter
    private var dataPage = 1
    private var positionIndex: Int = 0
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    private var totalPage = 0

    override var mPresenter: ReviewsPresenter
        get() = ReviewsPresenter(this, context)
        set(value) {}

    override fun layoutId(): Int = R.layout.fragment_reviews

    override fun setToolbar(mToolbar: Toolbar?, title: String?, setDisplayHomeAsUpEnabled: Boolean) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)

        val b = arguments
        if (b != null) {
            movieId = b.getString("movie_id", "")
        }
    }

    override fun afterGetReviews(review: Reviews) {
        hideLoadingDialog()
        reviewAdapter.removeLoading()
        totalPage = review.totalPages!!
        if (dataPage == 1) {
            reviews.clear()
        }
        reviews.addAll(review.results!!)
        reviewAdapter.notifyDataSetChanged()
    }

    override fun failedRequest(err_msg: String) {
        hideLoadingDialog()
        TOAST(err_msg)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init(){
        setupRecycler()
        btnClose.setOnClickListener {
            dismiss()
        }

        showLoadingDialog("Loading data ...")
        mPresenter.getReviews(movieId, dataPage.toString())
    }

    fun setupRecycler(){
        reviewAdapter = ReviewsAdapter(reviews, context){

        }

        val gridLayout : LinearLayoutManager
        recReviews.apply {
            gridLayout = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            layoutManager = gridLayout
            adapter = reviewAdapter
        }

        scrollListener = object : EndlessRecyclerViewScrollListener(gridLayout) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                if (dataPage < totalPage) {
                    reviewAdapter.addLoading()
                    dataPage += 1
                    mPresenter.getReviews(movieId, dataPage.toString())
                }
            }
        }

        recReviews.addOnScrollListener(scrollListener!!)
    }

    override fun onPause() {
        super.onPause()

        positionIndex = (recReviews.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }

    override fun onResume() {
        super.onResume()
        recReviews.scrollToPosition(positionIndex)
    }
}
