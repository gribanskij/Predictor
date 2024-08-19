package com.gribanskij.predictor.data.source.remote


import com.gribanskij.predictor.data.source.local.entities.Stock
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.net.URL
import java.util.*
import javax.inject.Inject



@ViewModelScoped
class RemoteDataSource @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val parser: Parser
) {


    suspend fun getStockData(
        stockUrl: String, sDate: String, eDate: String
    ): List<Stock> = withContext(ioDispatcher) {

        return@withContext try {

            val response = downLoadData(stockUrl,sDate,eDate)
            parser.parseResponse(response)

        } catch (ex: Exception) {
            emptyList()
        }
    }


    private fun downLoadData(stockUrl: String, sDate: String, eDate: String):String{

        val fullUrl = "${stockUrl}from=${sDate}&till=$eDate"
        val mUrl = URL(fullUrl)
        val input = BufferedInputStream(mUrl.openStream())
        val out = StringBuilder()
        val s = Scanner(input).useDelimiter("\\n")
        while (s.hasNext()) {
            val raw = s.next()
            out.append(raw)
        }
        return out.toString()

    }
}