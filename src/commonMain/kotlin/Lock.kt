import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import withLock

/*
 * Not part of the original client: withLock(lock) { ... } for common code. The contract sits
 * here, in common code, so that a variable assigned in the block counts as initialised after it;
 * an expect function cannot carry one. On the JVM both functions inline to the same
 * monitorenter/monitorexit as before.
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> withLock(lock: Any, block: () -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return platformLock(lock, block)
}

/* The platform's monitor: synchronized on the JVM. */
expect inline fun <T> platformLock(lock: Any, block: () -> T): T

/* Object.wait() on the lock's monitor; the caller holds it through withLock. */
expect fun lockWait(lock: Any?)

/* Object.notify() on the lock's monitor; the caller holds it through withLock. */
expect fun lockNotify(lock: Any?)

/* Object.notifyAll() on the lock's monitor; the caller holds it through withLock. */
expect fun lockNotifyAll(lock: Any?)
