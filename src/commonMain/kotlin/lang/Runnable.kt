package lang

/** java.lang.Runnable; on the JVM, the JDK's own (so a lambda or a Thread target is one). */
expect fun interface Runnable {
    fun run()
}
