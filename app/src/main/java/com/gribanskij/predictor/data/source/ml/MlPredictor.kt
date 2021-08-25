package com.gribanskij.predictor.data.source.ml

import android.content.Context
import android.content.res.AssetFileDescriptor
import com.gribanskij.predictor.data.source.remote.*
import kotlinx.coroutines.CoroutineDispatcher
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject


private const val SBER_FILE_NAME = "sber.tflite"
private const val YAND_FILE_NAME = "yand.tflite"
private const val GAZPROM_FILE_NAME = "gazp.tflite"
private const val LUKOIL_FILE_NAME = "lkoh.tflite"
private const val ROSN_FILE_NAME = "rosn.tflite"


class MlPredictor @Inject constructor(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) {

    var isInit: Boolean = false
    private lateinit var tflite: Interpreter
    private var tfliteoptions: Interpreter.Options = Interpreter.Options()
    private lateinit var tflitemodel: MappedByteBuffer


    fun init(stockName: String) {

        try {
            if (!isInit) {
                tflitemodel = loadModelfile(getModelFileName(stockName))
                tfliteoptions.setNumThreads(1)
                tflite = Interpreter(tflitemodel, tfliteoptions)
                isInit = true
            }
        } catch (e: Throwable) {
            isInit = false
            e.printStackTrace()
        }

    }


    private fun getModelFileName(stockName: String): String {
        return when {
            stockName.equals(SBER_NAME, ignoreCase = true) -> SBER_FILE_NAME
            stockName.equals(YAND_NAME, ignoreCase = true) -> YAND_FILE_NAME
            stockName.equals(GAZPROM_NAME, ignoreCase = true) -> GAZPROM_FILE_NAME
            stockName.equals(LUKOIL_NAME, ignoreCase = true) -> LUKOIL_FILE_NAME
            stockName.equals(ROSN_NAME, ignoreCase = true) -> ROSN_FILE_NAME
            else -> SBER_FILE_NAME
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
        val outputVal: ByteBuffer = ByteBuffer.allocateDirect(4)
        outputVal.order(ByteOrder.nativeOrder())
        tflite.run(input, outputVal)
        outputVal.rewind()
        return outputVal.float
    }
}