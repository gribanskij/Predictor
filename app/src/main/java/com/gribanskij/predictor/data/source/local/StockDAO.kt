package com.gribanskij.predictor.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gribanskij.predictor.data.source.local.entities.Stock
import com.gribanskij.predictor.data.source.local.entities.StockNoID

@Dao
interface StockDAO {

    @Query(
        "SELECT * FROM STOCK WHERE STOCK.stockId =:stockId"
    )
    suspend fun getStockById(stockId: String): List<Stock>

    @Query(
        "SELECT * FROM STOCK WHERE STOCK.stockId =:stockId AND STOCK.tradeDate < :date ORDER by STOCK.tradeDate LIMIT 10 "
    )
    suspend fun getStockBeforeDate(stockId: String, date: String): List<Stock>

    @Insert(entity = Stock::class)
    suspend fun insertStock(stock: StockNoID)

}