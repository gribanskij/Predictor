package com.gribanskij.predictor.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gribanskij.predictor.data.source.local.entities.Stock
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDAO {

    @Query(
        "SELECT * " +
                "FROM STOCK " +
                "WHERE STOCK.stockId =:stockId AND STOCK.tradeDate >=:sDate AND STOCK.tradeDate <=:eDate " +
                "ORDER by STOCK.tradeDate "
    )
    fun getStockBeforeDate(stockId: String, sDate: String, eDate: String): Flow<List<Stock>>

    @Insert(entity = Stock::class)
    suspend fun saveStock(stock: Stock)

}