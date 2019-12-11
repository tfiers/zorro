package net.tomasfiers.zorro.sync

import android.text.format.DateUtils.MINUTE_IN_MILLIS
import android.text.format.DateUtils.WEEK_IN_MILLIS
import android.text.format.DateUtils.getRelativeDateTimeString
import net.tomasfiers.zorro.data.DataRepo
import java.util.Timer
import kotlin.concurrent.timerTask


fun DataRepo.startUpdatingLastSyncText() {
    lastSyncTextUpdateTimer = Timer()
    // Question: does this job keep running when the application is destroyed? I suppose not because
    // when the linux application process is killed, its subthreads/subprocesses are probably killed
    // too.
    lastSyncTextUpdateTimer.schedule(
        timerTask { updateLastSyncText() },
        0,
        MINUTE_IN_MILLIS
    )
}

fun DataRepo.stopUpdatingLastSyncText() = lastSyncTextUpdateTimer.cancel()

fun DataRepo.updateLastSyncText() {
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
        // we cannot directly use `lastSyncText.value = x`.
        lastSyncText.postValue(newSyncStatus)
    }
}
