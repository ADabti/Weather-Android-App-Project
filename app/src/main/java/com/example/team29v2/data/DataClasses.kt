package com.example.team29v2.data

data class Base(val data: Data?)

data class Body(val value: String?)

data class Data(val tstype: String?, val tseries: List<Tseries>?)

data class Tseries(val header: Header?, val observations: List<Observations>?, var forecastDto: ForecastDto)

data class Header(val id: Id?, val extra: Extra?)

data class Id(val buoyid: String?, val parameter: String?, val source: String?)

data class Extra(val name: String?, val pos: Pos?)

data class Pos(val lat: String?, val lon: String?)

data class Observations(val time: String?, val body: Body?)



data class ForecastDto(
    val properties: Properties,
    val timeseries: List<Timeseries>,
    val data: FData,
    val next_6_hours: Next6hours?,
    val instant: Instant?,
    val details: Details?,
    val summary: Summary?,
    val precipitation_amount: Precipitation?
)

data class Properties(val timeseries: List <Timeseries>)
data class Timeseries(val time: String, val data: FData)
data class FData(val instant: Instant?, val next_6_hours: Next6hours?)
data class Instant(val details: Details)

data class Next6hours(val summary: Summary?, val details: Precipitation)
data class Summary(val symbol_code: String?)

data class Details(var air_temperature: Number, var wind_speed: Number)

data class Precipitation(val precipitation_amount: Number?)

