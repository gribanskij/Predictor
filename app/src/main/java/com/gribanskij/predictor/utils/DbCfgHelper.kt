package com.gribanskij.predictor.utils


import java.text.SimpleDateFormat
import java.util.*


private const val SQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
private const val SQLL_DATE_FORMAT_ONLY = "yyyy-MM-dd"
private val sysDateFormat = SimpleDateFormat(SQL_DATE_FORMAT, Locale.ENGLISH)

private val dateFormatOnly = SimpleDateFormat(SQLL_DATE_FORMAT_ONLY, Locale.ENGLISH)

@Synchronized
fun dateFormatOnly(date: Date): String = dateFormatOnly.format(date)

@Synchronized
fun getSysDate(date: Date): String = sysDateFormat.format(date)

@Synchronized
fun getTimeInMs(tradeDate: String): Long? = dateFormatOnly.parse(tradeDate)?.time