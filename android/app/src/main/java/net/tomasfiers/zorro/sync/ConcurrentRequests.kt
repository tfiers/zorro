package net.tomasfiers.zorro.sync

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.tomasfiers.zorro.data.DataRepo
import java.util.concurrent.atomic.AtomicInteger


suspend fun <T> DataRepo.makeConcurrentRequests(
    requestFunction: suspend DataRepo.(args: T) -> Unit,
    argsPerRequest: List<T>
) {
    numRequests.value = argsPerRequest.size
    numCompletedRequests.value = 0
    if (numRequests.value ?: 0 > 5) {
        showProgressBar.value = true
    }
    val numCompletedRequestsAtomic = AtomicInteger(0)
    // Wait until all coroutines launched inside this block have completed.
    coroutineScope {
        argsPerRequest.forEach { args ->
            launch {
                requestFunction(args)
                numCompletedRequests.value = numCompletedRequestsAtomic.incrementAndGet()
            }
        }
    }
    showProgressBar.value = false
}
