package com.example.team29v2.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface APIService {
    // Det er kun hentet badesteder fra badetessen for aa redusere vente tid.
    // + badesteder fra yr.no har feil data. For eksempel noen badesteder har feilt lat og lon som
    // leder til at de finnes ikke i Norge, men i the arabian sea?
    @GET("get?incobs=true&parameters=temperature&time=latest&latestmaxage=P1D&latestlimit=1&sources=badetassen.no")
    suspend fun fetchAlleBader(): Response<Base>


    @Headers(
        "Content-Type: application/x-www-form-urlencoded",
        "User-Agent: GRUPPE-29"
    )
    @GET("compact")
    suspend fun fetchWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String
    ): Response<ForecastDto>

}