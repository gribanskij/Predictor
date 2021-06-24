package com.gribanskij.predictor.data


import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


//завершения работы ММВБ
const val MMVB_END_TIME = 20


class DateMaker @Inject constructor() {

    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    //список выходных дней в 2021г кроме воскресенья и субботы
    private val listDayOff = listOf(
        "2021-01-01", "2021-01-04", "2021-01-05", "2021-01-06", "2021-01-07",
        "2021-01-08", "2021-02-22", "2021-02-23", "2021-03-08", "2021-05-03",
        "2021-05-10", "2021-06-14", "2021-11-04", "2021-11-05", "2021-12-31"
    )

    //список рабочих суббот в 2021
    private val listWorkDay = listOf("2021-02-20")

    //возвращает список дат - рабочих дней ММВБ.
    @Synchronized
    fun getListDate(dayNum: Int, startDate: Date): List<String> {
        val resultListDate = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate


        val carHour = calendar.get(Calendar.HOUR_OF_DAY)
        if (carHour < MMVB_END_TIME) calendar.add(Calendar.DAY_OF_MONTH, -1)

        do {

            when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> {
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                }
                Calendar.SATURDAY -> {
                    val date = formatter.format(calendar.time)
                    if (listWorkDay.contains(date)) resultListDate.add(date)
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                }
                else -> {
                    val date = formatter.format(calendar.time)
                    if (!listDayOff.contains(date)) resultListDate.add(date)
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                }
            }
        } while (resultListDate.size != dayNum)

        return resultListDate

    }
}