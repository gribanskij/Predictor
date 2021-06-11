package com.gribanskij.predictor.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gribanskij.predictor.data.source.local.entities.Stock
import com.gribanskij.predictor.data.source.local.entities.StockNoID
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDAO {

    @Query(
        "SELECT " +
                "STOCK.name                         AS name, " +
                "STOCK.stockId                      AS stockId, " +
                "STOCK.tradeDate                    AS tradeDate, " +
                "STOCK.priceClose                   AS priceClose, " +
                "STOCK.priceOpen                    AS priceOpen, " +
                "STOCK.priceLow                     AS priceLow, " +
                "STOCK.priceHigh                    AS priceHigh " +

                "FROM STOCK " +
                "WHERE STOCK.stockId =:stockId AND STOCK.tradeDate < :date ORDER by STOCK.tradeDate LIMIT 10 "
    )
    fun getStockBeforeDate(stockId: String, date: String): Flow<List<StockNoID>>

    @Insert(entity = Stock::class)
    suspend fun saveStock(stock: StockNoID)

}