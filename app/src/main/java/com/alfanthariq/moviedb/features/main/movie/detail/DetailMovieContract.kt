package com.alfanthariq.moviedb.features.main.movie.detail

import com.alfanthariq.moviedb.data.local.model.MovieDetail
import com.alfanthariq.moviedb.data.local.model.Reviews
import com.alfanthariq.moviedb.data.local.model.Trailers
import com.alfanthariq.moviedb.features.base.BaseMvpView
import com.alfanthariq.moviedb.features.base.BasePresenter

object DetailMovieContract {
    interface View : BaseMvpView {
        fun afterGetDetailMovies(movies : MovieDetail)
        fun afterGetTrailers(trailer : Trailers)
        fun afterGetReviews(review : Reviews)
        fun failedRequest(err_msg : String)
    }

    interface Presenter : BasePresenter<View> {
        fun getDetailMovie(id : String)
        fun getTrailers(id : String)
        fun getReviews(id : String)
    }
}