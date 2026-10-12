package io

expect open class FileOutputStream : OutputStream {
    constructor(name: String)
    constructor(file: File?)
    constructor(name: String, append: Boolean)

    override fun write(b: Int)
    override fun close()
    override fun flush()
}
