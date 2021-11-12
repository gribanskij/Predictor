package com.gribanskij.predictor.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
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
    fun observeStockData(stockId: String, sDate: String, eDate: String): Flow<List<Stock>>

    @Query(
        "SELECT * " +
                "FROM STOCK " +
                "WHERE STOCK.stockId =:stockId AND STOCK.tradeDate >=:sDate AND STOCK.tradeDate <=:eDate " +
                "ORDER by STOCK.tradeDate "
    )
    suspend fun getStockData(stockId: String, sDate: String, eDate: String): List<Stock>


    @Insert(onConflict = REPLACE)
    fun saveStock(stock: Stock)


}