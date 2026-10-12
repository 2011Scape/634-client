package util.zip

/* Not part of void-client's shims: java.util.zip.Inflater, as far as GzipDecompressor uses it (milestone 3). */
expect open class Inflater {
    constructor(nowrap: Boolean)
    fun setInput(input: ByteArray?, off: Int, len: Int)
    fun inflate(output: ByteArray?): Int
    fun reset()
}
