package com.gribanskij.predictor.data.source.remote

import com.gribanskij.predictor.data.Result
import com.gribanskij.predictor.data.source.DataSource
import java.util.*


private const val SBER_URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/SBER.cvs?from=2021-04-01&till%20=%2021-04-09"
private const val YAND_URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/YNDX.cvs?from=2021-04-01&till%20=%2021-04-09"
private const val GAZPROM_URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/GAZP.cvs?from=2020-10-27&till%20=%2020-11-02"

class RemoteDataSource : DataSource {

    override suspend fun getData(stockName: String, date: Date): Result<String> {
        TODO("Not yet implemented")
    }

    override suspend fun saveData(stockName: String, date: Date) {
    }

    override suspend fun getPredict(stockName: String, inputData: List<Float>): Result<Float> {
        return Result.Error(Exception("Not implemented"))
    }
}