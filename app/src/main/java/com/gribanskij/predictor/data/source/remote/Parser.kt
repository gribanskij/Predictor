package com.gribanskij.predictor.data.source.remote

import com.gribanskij.predictor.data.source.local.entities.Stock
import org.json.JSONObject
import javax.inject.Inject


private const val INDEX_TRADEDATE = 1
private const val INDEX_SHORTNAME = 2
private const val INDEX_SECID = 3
private const val INDEX_CLOSE = 11
private const val INDEX_LOW = 7
private const val INDEX_HIGH = 8
private const val INDEX_OPEN = 6

private const val JSON_HISTORY = "history"
private const val JSON_DATA = "data"


class Parser @Inject constructor() {

    fun parseResponse(input: String?): List<Stock> {

        if (input.isNullOrEmpty()) return emptyList()

        val result = mutableListOf<Stock>()

        val jObject = JSONObject(input)
        val jHistory = jObject.getJSONObject(JSON_HISTORY)
        val jData = jHistory.getJSONArray(JSON_DATA)

        for (i in 0 until jData.length()) {

            val tqbr = jData.getJSONArray(i)
            val high = tqbr.getDouble(INDEX_HIGH)
            val low = tqbr.getDouble(INDEX_LOW)
            val open = tqbr.getDouble(INDEX_OPEN)
            val close = tqbr.getDouble(INDEX_CLOSE)
            val tdate = tqbr.getString(INDEX_TRADEDATE)
            val id = tqbr.getString(INDEX_SECID)
            val name = tqbr.getString(INDEX_SHORTNAME)

            result.add(
                Stock(
                    name = name,
                    stockId = id,
                    tradeDate = tdate,
                    priceClose = close.toFloat(),
                    priceHigh = high.toFloat(),
                    priceLow = low.toFloat(),
                    priceOpen = open.toFloat()
                )
            )
        }
        return result
    }
}