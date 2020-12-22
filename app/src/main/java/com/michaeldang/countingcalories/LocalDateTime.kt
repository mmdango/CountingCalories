package com.michaeldang.countingcalories

import java.time.LocalDateTime

fun LocalDateTime.roundNearestDay(): LocalDateTime {
    return LocalDateTime.of(this.year, this.month, this.dayOfMonth, 0,0)
}

fun LocalDateTime.roundNextDay(): LocalDateTime {
    val nextDay = this.plusDays(1)
    return LocalDateTime.of(nextDay.year, nextDay.month, nextDay.dayOfMonth, 0,0)
}
