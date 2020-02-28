package com.theappbangla.dseprediction

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.theappbangla.dseprediction.model.Stock
import com.theappbangla.dseprediction.network.RetrofitClientInstance
import com.theappbangla.dseprediction.network.api.FetchApi
import kotlinx.android.synthetic.main.activity_chart.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ChartActivity : AppCompatActivity() {

    companion object {
        val EXTRA_TRADE_CODE = "EXTRA_TRADE_CODE"
    }

    lateinit var retrofit: Retrofit
    lateinit var fetchApi: FetchApi
    lateinit var tradeCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        retrofit = RetrofitClientInstance.getInstance()
        fetchApi = retrofit.create(FetchApi::class.java)

        tradeCode = intent.getStringExtra(EXTRA_TRADE_CODE)!!

        updateUi(true)

        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)

        fetchApi.fetchStockData(tradeCode, 6).enqueue(object : Callback<List<Stock>> {
            override fun onFailure(call: Call<List<Stock>>, t: Throwable) {
                updateUi(false)
                Toast.makeText(this@ChartActivity, "Error fetching stock data", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<Stock>>, response: Response<List<Stock>>) {
                val stocks = response.body()
                if(stocks == null) {
                    Toast.makeText(this@ChartActivity, "No stock data", Toast.LENGTH_SHORT).show()
                    return
                }

                val entries = ArrayList<Entry>()
                val xAxisValues = ArrayList<String>()
                var count = 0
                for (s in stocks) {
                    entries.add(Entry(count.toFloat(), s.price))
                    xAxisValues.add(s.date!!)
                    count++
                }

                updateChartUi(entries, xAxisValues)

                updateUi(false)
            }
        })

    }

    private fun updateChartUi(entries: ArrayList<Entry>, xAxisValues: ArrayList<String>) {

        val lineDataSet = LineDataSet(entries, "Test Data")
        lineDataSet.fillAlpha = 110
        lineDataSet.color = Color.LTGRAY
        lineDataSet.lineWidth = 1.5f
        lineDataSet.setCircleColor(Color.GRAY)
        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.label = tradeCode

        val iLineDataSet = ArrayList<ILineDataSet>()
        iLineDataSet.add(lineDataSet)

        val lineData = LineData(iLineDataSet)

        lineChart.data = lineData
        val description = Description()
        description.text = "Total Days: "  + entries.size
        lineChart.description = description

        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = DateXAxisFormatter(xAxisValues)
    }

    private fun updateUi(isLoading: Boolean) {
        progress_loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        lineChart.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    class DateXAxisFormatter(var values: ArrayList<String>) : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return values[value.toInt()]
        }

    }
}
