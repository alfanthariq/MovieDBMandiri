package com.alfanthariq.moviedb.features.main.movie.detail

import alfanthariq.com.signatureapp.util.PreferencesHelper
import com.alfanthariq.moviedb.data.remote.ApiClient
import com.alfanthariq.moviedb.data.remote.ApiService
import com.alfanthariq.moviedb.features.base.BasePresenterImpl
import com.alfanthariq.moviedb.utils.NetworkUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class DetailMoviePresenter (var view: DetailMovieContract.View) :
    BasePresenterImpl<DetailMovieContract.View>(), DetailMovieContract.Presenter {

    val context = view.getContext()
    val pref_profile = PreferencesHelper.getProfilePref(context)

    override fun getDetailMovie(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val token = pref_profile.getString("token", "")
            val service = ApiClient.getClient(NetworkUtil.useAPI, token).create(ApiService::class.java)
            val request = service.getDetailMovie(id, NetworkUtil.apiKey)
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()

                    if (response != null) {
                        view.afterGetDetailMovies(response)
                    } else {
                        view.failedRequest("Failed to get detail")
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    view.failedRequest("Failed to get detail (${e.message()})")
                } catch (e: Throwable) {
                    println(e.message)
                    view.failedRequest("Failed to get detail (${e.message})")
                }
            }
        }
    }

    override fun getTrailers(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val token = pref_profile.getString("token", "")
            val service = ApiClient.getClient(NetworkUtil.useAPI, token).create(ApiService::class.java)
            val request = service.getTrailers(id, NetworkUtil.apiKey)
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()

                    if (response != null) {
                        view.afterGetTrailers(response)
                    } else {
                        view.failedRequest("Failed to get trailers")
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    view.failedRequest("Failed to get trailers (${e.message()})")
                } catch (e: Throwable) {
                    println(e.message)
                    view.failedRequest("Failed to get trailers (${e.message})")
                }
            }
        }
    }

    override fun getReviews(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val token = pref_profile.getString("token", "")
            val service = ApiClient.getClient(NetworkUtil.useAPI, token).create(ApiService::class.java)
            val request = service.getReviews(id, NetworkUtil.apiKey, "1")
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()

                    if (response != null) {
                        view.afterGetReviews(response)
                    } else {
                        view.failedRequest("Failed to get reviews")
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    view.failedRequest("Failed to get reviews (${e.message()})")
                } catch (e: Throwable) {
                    println(e.message)
                    view.failedRequest("Failed to get reviews (${e.message})")
                }
            }
        }
    }
}