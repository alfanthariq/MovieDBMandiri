package com.alfanthariq.moviedb.features.main.movie.reviews

import alfanthariq.com.signatureapp.util.PreferencesHelper
import android.content.Context
import com.alfanthariq.moviedb.data.remote.ApiClient
import com.alfanthariq.moviedb.data.remote.ApiService
import com.alfanthariq.moviedb.features.base.BasePresenterImpl
import com.alfanthariq.moviedb.utils.NetworkUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ReviewsPresenter (var view : ReviewsContract.View, var context: Context) :
    BasePresenterImpl<ReviewsContract.View>(), ReviewsContract.Presenter {

    val pref_profile = PreferencesHelper.getProfilePref(context)

    override fun getReviews(id: String, page : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val token = pref_profile.getString("token", "")
            val service = ApiClient.getClient(NetworkUtil.useAPI, token).create(ApiService::class.java)
            val request = service.getReviews(id, NetworkUtil.apiKey, page)
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