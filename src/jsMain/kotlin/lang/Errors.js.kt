package lang

actual open class ThreadDeath actual constructor() : Error()

actual open class OutOfMemoryError : Error {
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
}
