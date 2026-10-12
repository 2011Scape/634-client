package lang

actual open class ArrayIndexOutOfBoundsException : Exception {
    actual constructor(index: Int) : super("Array index out of range: " + index)
    actual constructor() : super()
    actual constructor(message: String?) : super(message)
}
