package lang

/* Not part of void-client's shims: java.lang errors the client throws or catches (milestone 3). */

expect open class ThreadDeath : Error {
    constructor()
}

expect open class OutOfMemoryError : Error {
    constructor()
    constructor(message: String?)
}
