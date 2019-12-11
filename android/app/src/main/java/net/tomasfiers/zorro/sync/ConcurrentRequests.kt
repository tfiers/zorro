package net.tomasfiers.zorro.sync

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.tomasfiers.zorro.data.DataRepo
import java.util.concurrent.atomic.AtomicInteger

open class FunctionArgs

suspend fun <T : FunctionArgs> DataRepo.makeConcurrentRequests(
    downloadFunction: suspend DataRepo.(args: T) -> Unit,
    argsList: List<T>
) {
    numCompletedRequests.value = 0
    numRequests.value = argsList.size
    if (numRequests.value ?: 0 > 5) {
        showProgressBar.value = true
    }
    val numCompletedRequestsAtomic = AtomicInteger(0)
    // Wait until all coroutines launched inside this block have completed.
    coroutineScope {
        argsList.forEach { args ->
            launch {
                downloadFunction(args)
                numCompletedRequests.value = numCompletedRequestsAtomic.incrementAndGet()
            }
        }
    }
    showProgressBar.value = false
}
