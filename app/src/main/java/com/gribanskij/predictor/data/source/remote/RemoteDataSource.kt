package com.gribanskij.predictor.data.source.remote

import com.gribanskij.predictor.data.StockModel
import com.gribanskij.predictor.data.source.DataSource
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedInputStream
import java.net.URL
import java.util.*
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


class RemoteDataSource @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) : DataSource {


    override suspend fun getStockData(
        stock: StockModel,
        sDate: String,
        eDate: String
    ): List<Stock> =
        withContext(ioDispatcher) {

            return@withContext try {

                val response = mutableListOf<Stock>()

                val fullUrl = "${stock.URL}from=${sDate}&till=$eDate"
                val mUrl = URL(fullUrl)
                val input = BufferedInputStream(mUrl.openStream())
                val out = StringBuilder()
                val s = Scanner(input).useDelimiter("\\n")
                while (s.hasNext()) {
                    val raw = s.next()
                    out.append(raw)
                }

                val jObject = JSONObject(out.toString())
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

                    response.add(
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
                response
            } catch (ex: Exception) {
                emptyList()
            }
        }

    override fun observeStockData(
        stock: StockModel,
        sDate: String,
        eDate: String
    ): Flow<List<Stock>>  = emptyFlow()

}