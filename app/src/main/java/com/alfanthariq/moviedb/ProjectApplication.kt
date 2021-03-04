package com.alfanthariq.moviedb

import android.app.Application
import android.content.Context
import com.alfanthariq.moviedb.data.local.AppDatabase
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class ProjectApplication : Application() {

    companion object {
        var database: AppDatabase? = null

        fun getDB(context: Context) : AppDatabase?{
            database = AppDatabase.getInstance(context)
            return AppDatabase.getInstance(context)
        }
    }

    override fun onCreate() {
        super.onCreate()
        //getDB(this)

        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/FallingSky.otf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
    }
}