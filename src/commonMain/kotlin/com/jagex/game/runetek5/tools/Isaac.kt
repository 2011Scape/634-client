package com.jagex.game.runetek5.tools

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

@OriginalClass("client!ku")
class Isaac {

	@OriginalMember(owner = "client!ku", name = "a", descriptor = "[I")
	private var results: IntArray? = null

	@OriginalMember(owner = "client!ku", name = "c", descriptor = "I")
	private var lastResult: Int = 0

	@OriginalMember(owner = "client!ku", name = "d", descriptor = "I")
	private var count: Int = 0

	@OriginalMember(owner = "client!ku", name = "e", descriptor = "I")
	private var accumulator: Int = 0

	@OriginalMember(owner = "client!ku", name = "f", descriptor = "I")
	private var counter: Int = 0

	@OriginalMember(owner = "client!ku", name = "h", descriptor = "[I")
	private var memory: IntArray? = null

	@OriginalMember(owner = "client!ku", name = "<init>", descriptor = "()V")
	private constructor() {
	}

	@OriginalMember(owner = "client!ku", name = "<init>", descriptor = "([I)V")
	constructor(@OriginalArg(0) seed: IntArray?) {
		this.memory = IntArray(256)
		this.results = IntArray(256)
		@Pc(13) var i: Int = 0
		while (i < seed!!.size) {
			this.results!![i] = seed[i]
			i++
		}
		this.init()
	}

	@OriginalMember(owner = "client!ku", name = "a", descriptor = "(I)V")
	private fun init() {
		@Pc(8) var a: Int = -1640531527
		@Pc(10) var b: Int = -1640531527
		@Pc(12) var c: Int = -1640531527
		@Pc(14) var d: Int = -1640531527
		@Pc(16) var e: Int = -1640531527
		@Pc(18) var f: Int = -1640531527
		@Pc(20) var g: Int = -1640531527
		@Pc(21) var h: Int = -1640531527
		@Pc(23) var i: Int
		i = 0
		while (i < 4) {
			h = h xor (g shl 11)
			e += h
			g += f
			g = g xor (f ushr 2)
			d += g
			f += e
			f = f xor (e shl 8)
			e += d
			c += f
			e = e xor (d ushr 16)
			b += e
			d += c
			d = d xor (c shl 10)
			c += b
			a += d
			c = c xor (b ushr 4)
			h += c
			b += a
			b = b xor (a shl 8)
			g += b
			a += h
			a = a xor (h ushr 9)
			h += g
			f += a
			i++
		}
		i = 0
		while (i < 256) {
			f += this.results!![i + 2]
			a += this.results!![i + 7]
			e += this.results!![i + 3]
			c += this.results!![i + 5]
			b += this.results!![i + 6]
			g += this.results!![i + 1]
			d += this.results!![i + 4]
			h += this.results!![i]
			h = h xor (g shl 11)
			g += f
			e += h
			g = g xor (f ushr 2)
			f += e
			d += g
			f = f xor (e shl 8)
			c += f
			e += d
			e = e xor (d ushr 16)
			b += e
			d += c
			d = d xor (c shl 10)
			a += d
			c += b
			c = c xor (b ushr 4)
			b += a
			h += c
			b = b xor (a shl 8)
			a += h
			g += b
			a = a xor (h ushr 9)
			f += a
			h += g
			this.memory!![i] = h
			this.memory!![i + 1] = g
			this.memory!![i + 2] = f
			this.memory!![i + 3] = e
			this.memory!![i + 4] = d
			this.memory!![i + 5] = c
			this.memory!![i + 6] = b
			this.memory!![i + 7] = a
			i += 8
		}
		i = 0
		while (i < 256) {
			f += this.memory!![i + 2]
			e += this.memory!![i + 3]
			a += this.memory!![i + 7]
			c += this.memory!![i + 5]
			g += this.memory!![i + 1]
			b += this.memory!![i + 6]
			h += this.memory!![i]
			d += this.memory!![i + 4]
			h = h xor (g shl 11)
			e += h
			g += f
			g = g xor (f ushr 2)
			d += g
			f += e
			f = f xor (e shl 8)
			c += f
			e += d
			e = e xor (d ushr 16)
			b += e
			d += c
			d = d xor (c shl 10)
			c += b
			a += d
			c = c xor (b ushr 4)
			b += a
			h += c
			b = b xor (a shl 8)
			g += b
			a += h
			a = a xor (h ushr 9)
			f += a
			h += g
			this.memory!![i] = h
			this.memory!![i + 1] = g
			this.memory!![i + 2] = f
			this.memory!![i + 3] = e
			this.memory!![i + 4] = d
			this.memory!![i + 5] = c
			this.memory!![i + 6] = b
			this.memory!![i + 7] = a
			i += 8
		}
		this.isaac()
		this.count = 256
	}

	@OriginalMember(owner = "client!ku", name = "a", descriptor = "(B)I")
	fun peek(): Int {
		if (this.count == 0) {
			this.isaac()
			this.count = 256
		}
		return this.results!![this.count - 1]
	}

	@OriginalMember(owner = "client!ku", name = "a", descriptor = "(Z)V")
	private fun isaac() {
		this.counter++
		this.lastResult += this.counter
		@Pc(19) var i: Int = 0
		while (i < 256) {
			@Pc(26) val x: Int = this.memory!![i]
			if ((i and 0x2) == 0) {
				if ((i and 0x1) == 0) {
					this.accumulator = this.accumulator xor (this.accumulator shl 13)
				} else {
					this.accumulator = this.accumulator xor (this.accumulator ushr 6)
				}
			} else if ((i and 0x1) == 0) {
				this.accumulator = this.accumulator xor (this.accumulator shl 2)
			} else {
				this.accumulator = this.accumulator xor (this.accumulator ushr 16)
			}
			this.accumulator += this.memory!![(i + 128) and 0xFF]
			@Pc(116) val y: Int = this.memory!![(x shr 2) and 0xFF] + this.accumulator + this.lastResult
			this.memory!![i] = y
			this.lastResult = x + this.memory!![((y shr 8) shr 2) and 0xFF]
			this.results!![i] = this.lastResult
			i++
		}
	}

	@OriginalMember(owner = "client!ku", name = "b", descriptor = "(B)I")
	fun next(): Int {
		if (this.count == 0) {
			this.isaac()
			this.count = 256
		}
		return this.results!![--this.count]
	}
}
