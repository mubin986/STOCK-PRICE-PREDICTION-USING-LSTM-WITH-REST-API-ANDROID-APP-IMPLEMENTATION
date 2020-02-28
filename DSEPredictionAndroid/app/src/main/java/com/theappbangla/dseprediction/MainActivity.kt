package com.theappbangla.dseprediction

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.theappbangla.dseprediction.model.AvailableStocks
import com.theappbangla.dseprediction.model.Prediction
import com.theappbangla.dseprediction.network.RetrofitClientInstance
import com.theappbangla.dseprediction.network.api.FetchApi
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    val TAG = "TAG_MAIN"

    lateinit var retrofit : Retrofit
    lateinit var fetchApi: FetchApi
    lateinit var availableStocks: AvailableStocks

    var selectedStock: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinner.onItemSelectedListener = this
        retrofit = RetrofitClientInstance.getInstance()
        fetchApi = retrofit.create(FetchApi::class.java)

        init()

    }

    private fun init() {
        tv_loading.visibility = View.GONE
        ll_prediction.visibility = View.GONE
        btnPredict.isEnabled = true
        btnStockData.visibility = View.GONE

        fetchStocks()
    }

    private fun fetchStocks() {

        fetchApi.fetchAvailableStocks().enqueue(object : Callback<AvailableStocks> {
            override fun onFailure(call: Call<AvailableStocks>, t: Throwable) {
                errorUi("Server Not Available")
            }

            override fun onResponse(call: Call<AvailableStocks>, response: Response<AvailableStocks>) {
                availableStocks = response.body()!!
                val adapter: ArrayAdapter<*> =
                    ArrayAdapter<String>(
                        this@MainActivity,
                        android.R.layout.simple_spinner_item,
                        availableStocks.stocks
                    )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter

                Toast.makeText(this@MainActivity, "Available Stocks: " + availableStocks.stocks.size, Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        selectedStock = availableStocks.stocks[p2]
        btnStockData.text = selectedStock + " Data"
        btnStockData.visibility = View.VISIBLE
        //Toast.makeText(this, selectedStock, Toast.LENGTH_SHORT).show()
    }

    fun onClickPredict(view: View) {
        if (selectedStock == null) {
            Toast.makeText(this, "Select a stock first", Toast.LENGTH_SHORT).show()
            return
        }

        updateUi(true)

        fetchApi.predict(selectedStock!!).enqueue(object : Callback<Prediction> {

            override fun onFailure(call: Call<Prediction>, t: Throwable) {
                updateUi(false)
                errorUi("Error fetching data")
                Log.d(TAG, "Error: " +  t.message)

            }

            override fun onResponse(call: Call<Prediction>, response: Response<Prediction>) {
                updateUi(false)
                bindPrediction(response.body())
            }

        })
    }

    private fun bindPrediction(prediction: Prediction?) {
        if (prediction == null)
        {
            errorUi("Prediction is null")
            return
        }
        tv_code.text = prediction.meta.code + " Prediction"
        tv_prediction.text = prediction.prediction + " Tk"
        tv_algo.text = "Algorithm: " + prediction.meta.algo
        tv_epoch.text = "Epochs: " + prediction.meta.epoch
        tv_month.text = "Months: " + prediction.meta.month
        tv_size.text = "Size: " + prediction.meta.size
    }

    private fun updateUi(isLoading: Boolean) {
        ll_prediction.visibility = if (isLoading) View.GONE else View.VISIBLE
        tv_loading.text = "Predicting ..."
        tv_loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnPredict.isEnabled = !isLoading
    }

    private fun errorUi(message: String) {
        tv_loading.text = message
        tv_loading.visibility = View.VISIBLE
        ll_prediction.visibility = View.GONE
    }

    fun onClickReFetchAll(view: View) {
        Toast.makeText(this, "Please wait ...", Toast.LENGTH_SHORT).show()
        init()
    }

    fun onClickOpenChart(view: View) {
        val intent = Intent(this, ChartActivity::class.java)
        intent.putExtra(ChartActivity.EXTRA_TRADE_CODE, selectedStock)
        startActivity(intent)
    }
}
