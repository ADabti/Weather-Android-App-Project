package com.example.team29v2.data

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class DataSource : AppCompatActivity() {

    private val bader = "https://havvarsel-frost.met.no/api/v1/obs/badevann/"
    private val weather = "https://api.met.no/weatherapi/locationforecast/2.0/"
    private var baderList: MutableList<Tseries> = mutableListOf()
    private val gson = Gson()

    private val client = OkHttpClient.Builder()
        .readTimeout(100, TimeUnit.SECONDS)
        .build()


    private val client2 = OkHttpClient.Builder()
        .addNetworkInterceptor { chain ->
            chain.proceed(
                chain.request()
                    .newBuilder()
                    .header("d282bfcb-7bf9-481f-9964-aaf1539e503a", "Dalvik/2.1.0")
                    .build()
            )
        }
        .build()


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(bader)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val retrofit2: Retrofit = Retrofit.Builder()
        .baseUrl(weather)
        .client(client2)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    suspend fun fetchBader(): MutableList<Tseries>? {

        Log.d("DataSource | fetchBader():BEGIN", "*****************************")
        val service = retrofit.create(APIService::class.java)
        Log.d("DataSource | fetchBader():1", "*****************************")
        val response = service.fetchAlleBader()
        Log.d("DataSource | fetchBader():2", "*****************************")
        if (response.isSuccessful) {
            Log.d("DataSource | fetchBader():3", "*****************************")
            val elements = response.body()?.data?.tseries
            if (elements != null) {
                for (el in elements) {
                    Log.d("DataSource | fetchBader():4", "*****************************")
                    baderList.add(el)

                }
            }
        }
        Log.d("DataSource | fetchBader():END", "*****************************")
        Log.d("DataSource | fetchBader()", baderList.toString())
        return baderList
    }

    // Under er kode for a fetche weather api som skal kombineres kanskje med havvarsel api.
    // Kan brukes når vi fikser helt havvarsel api
    suspend fun fetchBadeTillegg(): MutableList<Tseries>? {
        Log.d("reponse is suscess", "reponse is suscess")

        baderList = fetchBader()!!

        for (el in baderList) {
            val lat = el.header?.extra?.pos?.lat.toString()
            val lon = el.header?.extra?.pos?.lon.toString()
            val service = retrofit2.create(APIService::class.java)
            val response = service.fetchWeather(lat, lon)
            if (response.isSuccessful) {
                Log.d("reponse is suscess", "reponse is suscess")

                el.forecastDto = response.body()!!
                Log.d(
                    "DataSource | ckeckWeather()",
                    el.forecastDto.toString()
                )
            }

        }
        return baderList
    }
}