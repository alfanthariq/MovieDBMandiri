package com.alfanthariq.moviedb.features.main

import alfanthariq.com.signatureapp.util.PreferencesHelper
import com.alfanthariq.moviedb.data.local.AppDatabase
import com.alfanthariq.moviedb.data.remote.ApiClient
import com.alfanthariq.moviedb.data.remote.ApiService
import com.alfanthariq.moviedb.features.base.BasePresenterImpl
import com.alfanthariq.moviedb.utils.NetworkUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class MainPresenter (var view: MainContract.View) :
    BasePresenterImpl<MainContract.View>(), MainContract.Presenter {

    val context = view.getContext()
    val pref_setting = PreferencesHelper.getSettingPref(context)
    val pref_profile = PreferencesHelper.getProfilePref(context)
    val db: AppDatabase by lazy {
        AppDatabase.getInstance(context)!!
    }

    override fun getGenres() {
        CoroutineScope(Dispatchers.IO).launch {
            val token = pref_profile.getString("token", "")
            val service = ApiClient.getClient(NetworkUtil.useAPI, token).create(ApiService::class.java)
            val request = service.getGenres(NetworkUtil.apiKey)
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()

                    if (response != null) {
                        view.afterGetGenres(response)
                    } else {
                        view.failedRequest("Failed to get genres")
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    view.failedRequest("Failed to get genres (${e.message()})")
                } catch (e: Throwable) {
                    println(e.message)
                    view.failedRequest("Failed to get genres (${e.message})")
                }
            }
        }
    }

    override fun getMovies(genres: String, page : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val token = pref_profile.getString("token", "")
            val service = ApiClient.getClient(NetworkUtil.useAPI, token).create(ApiService::class.java)
            val request = if (genres.isEmpty()) service.getAllMovie(NetworkUtil.apiKey, page) else
                service.getGenreMovie(NetworkUtil.apiKey, genres, page)
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()

                    if (response != null) {
                        view.afterGetMovies(response)
                    } else {
                        view.failedRequest("Failed to get movies")
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    view.failedRequest("Failed to get movies (${e.message()})")
                } catch (e: Throwable) {
                    println(e.message)
                    view.failedRequest("Failed to get movies (${e.message})")
                }
            }
        }
    }
}