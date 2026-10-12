package awt

actual class Dimension actual constructor(var width: Int, var height: Int)

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
actual var Dimension.width: Int
    get() = this.width
    set(value) {
        this.width = value
    }

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
actual var Dimension.height: Int
    get() = this.height
    set(value) {
        this.height = value
    }
