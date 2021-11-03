package com.gribanskij.predictor.data.source.ml

import android.content.Context
import android.content.res.AssetFileDescriptor
import com.gribanskij.predictor.data.StockModel
import kotlinx.coroutines.CoroutineDispatcher
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject


class MlPredictor @Inject constructor(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) {

    var isInit: Boolean = false
    private lateinit var tflite: Interpreter
    private var tfliteoptions: Interpreter.Options = Interpreter.Options()
    private lateinit var tflitemodel: MappedByteBuffer
    private lateinit var mStock: StockModel


    fun init(stock: StockModel) {

        try {
            if (!isInit) {
                mStock = stock
                tflitemodel = loadModelfile(stock.MODEL_NAME)
                tfliteoptions.setNumThreads(1)
                tflite = Interpreter(tflitemodel, tfliteoptions)
                isInit = true
            }
        } catch (e: Throwable) {
            isInit = false
            e.printStackTrace()
        }

    }



    private fun loadModelfile(modelFileName: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = context.assets.openFd(modelFileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset: Long = fileDescriptor.startOffset
        val declaredLength: Long = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


    fun doInference(input: FloatArray): Float {
        val scaleInput = scaledInputData(input)
        val outputVal: ByteBuffer = ByteBuffer.allocateDirect(4)
        outputVal.order(ByteOrder.nativeOrder())
        tflite.run(scaleInput, outputVal)
        outputVal.rewind()
        return scaleOutput(outputVal.float)
    }

    //Входные значений стоимости привести к масштабу 0-1
    private fun scaledInputData(input: FloatArray): FloatArray {
        return input.map { (it - mStock.MODEL_MIN_VALUE) / (mStock.MODEL_MAX_VALUE - mStock.MODEL_MIN_VALUE) }
            .toFloatArray()

    }

    private fun scaleOutput(input: Float): Float {
        return input * (mStock.MODEL_MAX_VALUE - mStock.MODEL_MIN_VALUE) + mStock.MODEL_MIN_VALUE
    }
}