package com.theappbangla.dseprediction.model

class AvailableStocks {
    var stocks: ArrayList<String> = ArrayList()
    var total: Int = -1

    override fun toString(): String {
        return "Stocks(stocks=$stocks, total=$total)"
    }

}