package com.tvapp.pdfloadandshare

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {
        val BASE_URLL = "https://www.google.com"

        fun getRetroInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URLL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}