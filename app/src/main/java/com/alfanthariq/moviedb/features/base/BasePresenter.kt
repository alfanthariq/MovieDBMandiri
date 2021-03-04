package com.alfanthariq.moviedb.features.base

import android.content.Context

interface BasePresenter<in V : BaseMvpView> {

    fun attachView(view: V)

    fun detachView()

    fun getContext(context:Context)
}