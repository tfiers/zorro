package net.tomasfiers.zoro.sync

import android.text.format.DateUtils.MINUTE_IN_MILLIS
import android.text.format.DateUtils.WEEK_IN_MILLIS
import android.text.format.DateUtils.getRelativeDateTimeString
import net.tomasfiers.zoro.data.DataRepo
import java.util.Timer
import kotlin.concurrent.timerTask



fun DataRepo.startUpdatingLastSyncText() {
    lastSyncTextUpdateTimer = Timer()
    lastSyncTextUpdateTimer.schedule(
        timerTask { setLastSyncText() },
        0,
        MINUTE_IN_MILLIS
    )
}

fun DataRepo.stopUpdatingLastSyncText() = lastSyncTextUpdateTimer.cancel()

fun DataRepo.setLastSyncText() {
    val lastSyncTime = lastSyncTime
    if (lastSyncTime != null) {
        val newSyncStatus = "Last sync: " + getRelativeDateTimeString(
            application.applicationContext,
            lastSyncTime.toEpochMilli(),
            MINUTE_IN_MILLIS,
            WEEK_IN_MILLIS,
            0
        ).toString()
            .replace(Regex("^0 minutes ago"), "just now")
        // We are not on the main thread (but rather a background thread created by the timer), so
        // we cannot directly use `syncStatus = x`.
        syncStatus.postValue(newSyncStatus)
    }
}
