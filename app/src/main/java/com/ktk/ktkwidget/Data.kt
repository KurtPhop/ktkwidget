package com.ktk.ktkwidget

data class Data(
    var status: String? = null,
    var rasp: String? = null,
    var rasp2: String? = null,
    var rasp3: String? = null,
    var rasp4: String? = null,
    var kab: String? = null,
    var kab2: String? = null,
    var kab3: String? = null,
    var kab4: String? = null,
    var time1: String? = null,
    var time2: String? = null,
    var time4: String? = null,
    var time3: String? = null
) {
    //Статус
    fun status(): String {
        return status.toString()
    }
    //Пары
    fun raspesanie1(): String {
        val tempPrice1 = rasp.toString()
        return tempPrice1
    }
    fun raspesanie2(): String {
        val tempPrice2 = rasp2.toString()
        return tempPrice2
    }
    fun raspesanie3(): String {
        val tempPrice3 = rasp3.toString()
        return tempPrice3
    }
    fun raspesanie4(): String {
        val tempPrice4 = rasp4.toString()
        return tempPrice4
    }
    //Кабинеты
    fun kab(): String {
        val tempkab1 = kab.toString()
        return tempkab1
    }
    fun kab2(): String {
        val tempkab2 = kab2.toString()
        return tempkab2
    }
    fun kab3(): String {
        val tempkab3 = kab3.toString()
        return tempkab3
    }
    fun kab4(): String {
        val tempkab4 = kab4.toString()
        return tempkab4
    }
    //Время
    fun time1(): String {
        val temptime1 = time1.toString()
        return temptime1
    }
    fun time2(): String {
        val temptime2 = time2.toString()
        return temptime2
    }
    fun time3(): String {
        val temptime3 = time3.toString()
        return temptime3
    }
    fun time4(): String {
        val temptime4 = time4.toString()
        return temptime4
    }
}