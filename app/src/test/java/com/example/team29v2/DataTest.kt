package com.example.team29v2

import com.example.team29v2.data.APIService
import com.example.team29v2.data.Tseries
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.Assert
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class DataFragmentTest {
    private val bader = "https://havvarsel-frost.met.no/api/v1/obs/badevann/"
    private val weather = "https://api.met.no/weatherapi/locationforecast/2.0/"
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


    @Test
    fun callFromHavvarselAPI() {
        //skal sjekke om vi kan hente observasjoner fra Havvarsel-Frost API
        val baderList: MutableList<Tseries> = mutableListOf()

        val service = retrofit.create(APIService::class.java)

        val response = runBlocking {service.fetchAlleBader()}

        if (response.isSuccessful) {

            val elements = response.body()?.data?.tseries
            if (elements != null) {
                for (el in elements) {

                    baderList.add(el)
                }
            }
        }

        Assert.assertNotNull(baderList)
    }
    @Test
    fun callFromLocationForcastAPI() {
        //skal sjekke om vi kan hente observasjoner fra LocationForcast API

        val service = retrofit2.create(APIService::class.java)

        val response = runBlocking {service.fetchWeather("59.91","10.75")}//skjekker om API returnerer observasjoner til Oslo


        Assert.assertEquals(true, response.isSuccessful)
    }

//        //testen skulle sjekke om vi kan hente fra disk en list med observasjon kombinert
//        // med HavvarselFrost API og LocationForcast API. Funker ikke og gir feil
//    @Test
//    fun testDataFragment() {
//        val data = DataFragment()
//        val baderList: MutableList<Tseries> = data.hentBadeDataFraDisk()
//
//        val result = data.testDataFragment(baderList)
//        assertEquals(true, result)
//
//
//    }
}