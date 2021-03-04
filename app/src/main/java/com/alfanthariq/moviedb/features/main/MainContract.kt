package com.alfanthariq.moviedb.features.main

import com.alfanthariq.moviedb.data.local.model.Genres
import com.alfanthariq.moviedb.data.local.model.Movies
import com.alfanthariq.moviedb.features.base.BaseMvpView
import com.alfanthariq.moviedb.features.base.BasePresenter

object MainContract {
    interface View : BaseMvpView {
        fun afterGetGenres(genres : Genres)
        fun afterGetMovies(movies : Movies)
        fun failedRequest(err_msg : String)
    }

    interface Presenter : BasePresenter<View>{
        fun getGenres()
        fun getMovies(genres : String, page : String)
    }
}