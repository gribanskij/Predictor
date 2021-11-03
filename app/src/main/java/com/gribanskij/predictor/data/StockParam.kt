package com.gribanskij.predictor.data

class StockParam private constructor() {


    object COLLECTION {
        val stocks =
            mapOf(0 to SBER, 1 to YNDX, 2 to GAZP, 3 to LKOH, 4 to ROSN)
    }


    object SBER : StockModel(
        NAME = "SBER",
        URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/SBER.json?",
        CODE = 0,
        MODEL_INPUT = 6,
        MODEL_NAME = "sber.tflite",
        MODEL_MAX_VALUE = 387.6f,
        MODEL_MIN_VALUE = 53.5f
    )

    object YNDX : StockModel(
        NAME = "YNDX",
        URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/YNDX.json?",
        CODE = 1,
        MODEL_INPUT = 6,
        MODEL_NAME = "yand.tflite",
        MODEL_MAX_VALUE = 5973f,
        MODEL_MIN_VALUE = 694f
    )

    object GAZP : StockModel(
        NAME = "GAZP",
        URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/GAZP.json?",
        CODE = 2,
        MODEL_INPUT = 6,
        MODEL_NAME = "gazp.tflite",
        MODEL_MAX_VALUE = 389.82f,
        MODEL_MIN_VALUE = 115.35f
    )

    object LKOH : StockModel(
        NAME = "LKOH",
        URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/LKOH.json?",
        CODE = 3,
        MODEL_INPUT = 6,
        MODEL_NAME = "lkoh.tflite",
        MODEL_MAX_VALUE = 7441.5f,
        MODEL_MIN_VALUE = 1790.4f
    )

    object ROSN : StockModel(
        NAME = "ROSN",
        URL = "https://iss.moex.com/iss/history/engines/stock/markets/shares/boards/TQBR/securities/ROSN.json?",
        CODE = 4,
        MODEL_INPUT = 6,
        MODEL_NAME = "rosn.tflite",
        MODEL_MAX_VALUE = 655.25f,
        MODEL_MIN_VALUE = 193.2f
    )
}