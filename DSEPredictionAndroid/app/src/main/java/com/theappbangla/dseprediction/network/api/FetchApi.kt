package com.theappbangla.dseprediction.network.api

import com.theappbangla.dseprediction.model.AvailableStocks
import com.theappbangla.dseprediction.model.Prediction
import com.theappbangla.dseprediction.model.Stock
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FetchApi {
    @GET("/stocks")
    fun fetchAvailableStocks() : Call<AvailableStocks>

    @GET("/predict/{code}")
    fun predict(@Path("code") tradeCode: String) : Call<Prediction>

    @GET("/dummy")
    fun fetchDummyStockData(): Call<List<Stock>>

    @GET("/data/{tradeCode}/{months}")
    fun fetchStockData(@Path("tradeCode") tradeCode: String,
                       @Path("months") months: Int): Call<List<Stock>>

}