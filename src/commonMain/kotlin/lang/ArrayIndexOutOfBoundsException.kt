package lang


expect open class ArrayIndexOutOfBoundsException : Exception {
    constructor()
    constructor(message: String?)
    constructor(index: Int)
}
