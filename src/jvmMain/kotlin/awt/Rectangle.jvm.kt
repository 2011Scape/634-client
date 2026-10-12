package awt

actual typealias Rectangle = java.awt.Rectangle

actual var Rectangle.left: Int
    get() = x
    set(value) { x = value }

actual var Rectangle.top: Int
    get() = y
    set(value) { y = value }

actual var Rectangle.w: Int
    get() = width
    set(value) { width = value }

actual var Rectangle.h: Int
    get() = height
    set(value) { height = value }
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
actual var Rectangle.x: Int
    get() = this.x
    set(value) {
        this.x = value
    }

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
actual var Rectangle.y: Int
    get() = this.y
    set(value) {
        this.y = value
    }

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
actual var Rectangle.width: Int
    get() = this.width
    set(value) {
        this.width = value
    }

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
actual var Rectangle.height: Int
    get() = this.height
    set(value) {
        this.height = value
    }
