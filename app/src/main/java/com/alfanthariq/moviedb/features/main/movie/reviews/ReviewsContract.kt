package com.alfanthariq.moviedb.features.main.movie.reviews

import com.alfanthariq.moviedb.data.local.model.Reviews
import com.alfanthariq.moviedb.features.base.BaseMvpView
import com.alfanthariq.moviedb.features.base.BasePresenter

object ReviewsContract {
    interface View : BaseMvpView {
        fun afterGetReviews(review : Reviews)
        fun failedRequest(err_msg : String)
    }

    interface Presenter : BasePresenter<View> {
        fun getReviews(id : String, page : String)
    }
}