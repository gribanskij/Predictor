package com.gribanskij.predictor.data


import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject



class DateMaker @Inject constructor() {

    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private val timeZone = TimeZone.getTimeZone("Europe/Moscow")
    private val locale = Locale.getDefault()


    //список выходных дней мос.биржи в 2021-2022 кроме воскресенья и субботы
    private val listDayOff = listOf(
        "2021-01-01", "2021-01-04", "2021-01-05", "2021-01-06", "2021-01-07",
        "2021-01-08", "2021-02-22", "2021-02-23", "2021-03-08", "2021-05-03",
        "2021-05-10", "2021-06-14", "2021-11-04", "2021-11-05", "2021-12-31",
        "2022-01-01", "2022-01-02", "2022-05-02"
    )

    //список рабочих суббот в 2021,2022
    private val listWorkDay = listOf("2021-02-20")

    //возвращает список дат - рабочих дней ММВБ. Количесвто дат определяется dayNum.
    //startDate - дата до котороый нужны рабочие дни ММВБ в заданом количестве, не включая startDate
    @Synchronized
    fun getPrevWorkDate(dayNum: Int, startDate: Long): List<String> {
        val resultListDate = mutableListOf<String>()
        val calendar = Calendar.getInstance(timeZone, locale)
        calendar.time = Date(startDate)

        //Добавляем 7 дней чтобы учесть ошибки когда рабочий день на самом деле выходной.
        //и не хватит данных для последующих расчетов
        //val workDayNum = dayNum + 7


        //val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        //if (currentHour < MMVB_END_TIME) calendar.add(Calendar.DAY_OF_MONTH, -1)

        //начинаем с предыдущего дня отсносительно текущего
        calendar.add(Calendar.DAY_OF_MONTH, -1)

        do {

            when (calendar.get(Calendar.DAY_OF_WEEK)) {
                //100% выходной день, пропускаем.
                Calendar.SUNDAY -> {
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                }
                Calendar.SATURDAY -> {
                    val date = formatter.format(calendar.time)
                    //может суббота рабочая?
                    if (listWorkDay.contains(date)) resultListDate.add(date)
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                }
                else -> {
                    val date = formatter.format(calendar.time)
                    //может выходной день?
                    if (!listDayOff.contains(date)) resultListDate.add(date)
                    calendar.add(Calendar.DAY_OF_MONTH, -1)
                }
            }
        } while (resultListDate.size < dayNum)

        return resultListDate

    }

    //возвращает список дат - рабочих дней ММВБ. Количесвто дат определяется dayNum.
    //startDate - дата c котороый нужны рабочие дни ММВБ в заданом количестве
    @Synchronized
    fun getFutureWorkDate(dayNum: Int, startDate: Long): List<String> {
        val resultListDate = mutableListOf<String>()
        val calendar = Calendar.getInstance(timeZone, locale)
        calendar.time = Date(startDate)

        repeat(dayNum){
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val date = formatter.format(calendar.time)
            resultListDate.add(date)
        }


        //val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        //if (currentHour < MMVB_END_TIME) calendar.add(Calendar.DAY_OF_MONTH, -1)

        //начинаем с следующего дня относительно текущего
        //calendar.add(Calendar.DAY_OF_MONTH, 1)

        /*

        do {

            when (calendar.get(Calendar.DAY_OF_WEEK)) {
                //100% выходной день, пропускаем.
                Calendar.SUNDAY -> {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                Calendar.SATURDAY -> {
                    val date = formatter.format(calendar.time)
                    //может суббота рабочая?
                    if (listWorkDay.contains(date)) resultListDate.add(date)
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
                else -> {
                    val date = formatter.format(calendar.time)
                    //может выходной день?
                    if (!listDayOff.contains(date)) resultListDate.add(date)
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
            }
        } while (resultListDate.size < dayNum)

         */

        return resultListDate

    }

}