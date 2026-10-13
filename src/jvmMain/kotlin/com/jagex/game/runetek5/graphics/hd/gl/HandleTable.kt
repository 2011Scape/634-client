/*
 * Not part of the original client: 634HD (toolkit 6), ported from the browser's jaggl emulator
 * (src/jsMain/kotlin/jaggl/HandleTable.js.kt). The client's GL names map to the emulator's objects;
 * names are handed out from 1 and looked up in a growable array, since binds look them up per draw.
 */
package com.jagex.game.runetek5.graphics.hd.gl

class IntHandleTable<T : Any> {
    private var next = 1
    private var objects = arrayOfNulls<Any>(256)
    private val free = IntArray(4096)
    private var freeCount = 0

    fun allocate(obj: T): Int {
        val id = if (freeCount > 0) free[--freeCount] else next++
        if (id >= objects.size) objects = objects.copyOf(maxOf(objects.size * 2, id + 1))
        objects[id] = obj
        return id
    }

    @Suppress("UNCHECKED_CAST")
    operator fun get(id: Int): T? = if (id > 0 && id < objects.size) objects[id] as T? else null

    fun release(id: Int) {
        if (id <= 0 || id >= objects.size || objects[id] == null) return
        objects[id] = null
        if (freeCount < free.size) free[freeCount++] = id
    }
}

class LongHandleTable<T : Any> {
    private var next = 1L
    private val map = HashMap<Long, T>()

    fun allocate(obj: T): Long {
        val id = next++
        map[id] = obj
        return id
    }

    operator fun get(id: Long): T? = map[id]

    fun release(id: Long) {
        map.remove(id)
    }
}
