import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/*
 * Not part of the original client: the browser client's coroutine scope, the page's lifetime. The
 * client runs as long as its page does and ends with it, so this is the scope its background work
 * (cache writes, the game's threads) belongs to; a failed task does not cancel the others.
 */
val pageScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
