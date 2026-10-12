package com.jagex.game.runetek5

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc

object PolygonFiller {

	@OriginalMember(owner = "client!df", name = "a", descriptor = "I")
	@JvmField
	var nextEdgeIndex: Int = 0

	@OriginalMember(owner = "client!df", name = "b", descriptor = "I")
	@JvmField
	var scanlineY: Int = 0

	@OriginalMember(owner = "client!df", name = "c", descriptor = "I")
	@JvmField
	var activeEdgeStart: Int = 0

	@OriginalMember(owner = "client!df", name = "d", descriptor = "I")
	@JvmField
	var edgeBufferSize: Int = 0

	@OriginalMember(owner = "client!df", name = "e", descriptor = "I")
	@JvmField
	var scanlineStartX: Int = 0

	@OriginalMember(owner = "client!df", name = "g", descriptor = "I")
	@JvmField
	var scanlineEndX: Int = 0

	@OriginalMember(owner = "client!df", name = "h", descriptor = "I")
	@JvmField
	var scanEdgeIndex: Int = 0

	@OriginalMember(owner = "client!df", name = "f", descriptor = "[I")
	@JvmField
	var edgeBuffer: IntArray? = null

	@OriginalMember(owner = "client!df", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun initializeScanline(@OriginalArg(0) topY: Int) {
		if (edgeBufferSize < 0) {
			scanEdgeIndex = 0
			nextEdgeIndex = 0
			activeEdgeStart = 0
			scanlineY = 2147483646
			return
		}
		PolygonFiller.sortEdgesByY(0, edgeBufferSize)
		@Pc(17) var y: Int = edgeBuffer!![1]
		if (y < topY) {
			y = topY
		}
		@Pc(26) var edge: Int = 0
		while (edge < edgeBufferSize) {
			@Pc(33) val startY: Int = edgeBuffer!![edge + 1]
			if (y < startY) {
				break
			}
			@Pc(40) val x1: Int = edgeBuffer!![edge]
			@Pc(46) val x2: Int = edgeBuffer!![edge + 2]
			@Pc(52) val y2: Int = edgeBuffer!![edge + 3]
			@Pc(62) val step: Int = ((x2 - x1) shl 16) / (y2 - startY)
			@Pc(68) val x: Int = (x1 shl 16) + 32768
			edgeBuffer!![edge] = x + (y - startY) * step
			edgeBuffer!![edge + 2] = step
			edge += 4
		}
		activeEdgeStart = 0
		nextEdgeIndex = edge
		scanEdgeIndex = edge
		scanlineY = y - 1
	}

	@OriginalMember(owner = "client!df", name = "a", descriptor = "([III)V")
	@JvmStatic
	fun addEdges(@OriginalArg(0) vertices: IntArray?, @OriginalArg(2) count: Int) {
		@Pc(5) val required: Int = edgeBufferSize + (count shl 1)
		@Pc(16) var index: Int
		if (edgeBuffer == null || edgeBuffer!!.size < required) {
			@Pc(14) val grown: IntArray = IntArray(required)
			index = 0
			while (index < edgeBufferSize) {
				grown[index] = edgeBuffer!![index]
				index++
			}
			edgeBuffer = grown
		}
		@Pc(33) val length: Int = count
		@Pc(37) var previous: Int = length - 2
		index = 0
		while (index < length) {
			@Pc(46) val previousY: Int = vertices!![previous + 1]
			@Pc(52) val y: Int = vertices[index + 1]
			if (previousY < y) {
				edgeBuffer!![edgeBufferSize++] = vertices[previous]
				edgeBuffer!![edgeBufferSize++] = previousY
				edgeBuffer!![edgeBufferSize++] = vertices[index]
				edgeBuffer!![edgeBufferSize++] = y
			} else if (y < previousY) {
				edgeBuffer!![edgeBufferSize++] = vertices[index]
				edgeBuffer!![edgeBufferSize++] = y
				edgeBuffer!![edgeBufferSize++] = vertices[previous]
				edgeBuffer!![edgeBufferSize++] = previousY
			}
			previous = index
			index += 2
		}
	}

	@OriginalMember(owner = "client!df", name = "b", descriptor = "()V")
	@JvmStatic
	fun resetEdgeBuffer() {
		edgeBufferSize = 0
	}

	@OriginalMember(owner = "client!df", name = "b", descriptor = "(I)Z")
	@JvmStatic
	fun advanceScanline(@OriginalArg(0) maxY: Int): Boolean {
		@Pc(1) var end: Int = nextEdgeIndex
		@Pc(3) var cursor: Int = scanEdgeIndex
		@Pc(5) var y: Int = scanlineY
		while (cursor >= end) {
			y++
			scanlineY = y
			if (y >= maxY) {
				return false
			}
			@Pc(16) var start: Int = activeEdgeStart
			@Pc(23) var local23: Int
			@Pc(30) var local30: Int
			while (end < edgeBufferSize) {
				val y1: Int = edgeBuffer!![end + 1]
				if (y < y1) {
					break
				}
				val x1: Int = edgeBuffer!![end]
				@Pc(36) val x2: Int = edgeBuffer!![end + 2]
				@Pc(42) val y2: Int = edgeBuffer!![end + 3]
				@Pc(52) val step: Int = ((x2 - x1) shl 16) / (y2 - y1)
				@Pc(58) val x: Int = (x1 shl 16) + 32768
				edgeBuffer!![end] = x
				edgeBuffer!![end + 2] = step
				end += 4
			}
			var edgeIndex: Int = start
			while (edgeIndex < end) {
				val edgeEndY: Int = edgeBuffer!![edgeIndex + 3]
				if (y >= edgeEndY) {
					edgeBuffer!![edgeIndex] = edgeBuffer!![start]
					edgeBuffer!![edgeIndex + 1] = edgeBuffer!![start + 1]
					edgeBuffer!![edgeIndex + 2] = edgeBuffer!![start + 2]
					edgeBuffer!![edgeIndex + 3] = edgeBuffer!![start + 3]
					start += 4
				}
				edgeIndex += 4
			}
			if (start == edgeBufferSize) {
				edgeBufferSize = 0
				return false
			}
			PolygonFiller.sortActiveEdgesByX(start, end)
			activeEdgeStart = start
			nextEdgeIndex = end
			cursor = start
		}
		scanlineStartX = edgeBuffer!![cursor] shr 16
		scanlineEndX = edgeBuffer!![cursor + 4] shr 16
		edgeBuffer!![cursor] += edgeBuffer!![cursor + 2]
		edgeBuffer!![cursor + 4] += edgeBuffer!![cursor + 6]
		cursor += 8
		scanEdgeIndex = cursor
		return true
	}

	@OriginalMember(owner = "client!df", name = "a", descriptor = "(II)V")
	@JvmStatic
	fun sortEdgesByY(@OriginalArg(0) from: Int, @OriginalArg(1) to: Int) {
		if (to <= from + 4) {
			return
		}
		@Pc(7) var store: Int = from
		@Pc(11) val pivotX1: Int = PolygonFiller.edgeBuffer!![from]
		@Pc(17) val pivotY1: Int = PolygonFiller.edgeBuffer!![from + 1]
		@Pc(23) val pivotX2: Int = PolygonFiller.edgeBuffer!![from + 2]
		@Pc(29) val pivotY2: Int = PolygonFiller.edgeBuffer!![from + 3]
		@Pc(33) var edge: Int = from + 4
		while (edge < to) {
			@Pc(40) val startY: Int = PolygonFiller.edgeBuffer!![edge + 1]
			if (startY < pivotY1) {
				PolygonFiller.edgeBuffer!![store] = PolygonFiller.edgeBuffer!![edge]
				PolygonFiller.edgeBuffer!![store + 1] = startY
				PolygonFiller.edgeBuffer!![store + 2] = PolygonFiller.edgeBuffer!![edge + 2]
				PolygonFiller.edgeBuffer!![store + 3] = PolygonFiller.edgeBuffer!![edge + 3]
				store += 4
				PolygonFiller.edgeBuffer!![edge] = PolygonFiller.edgeBuffer!![store]
				PolygonFiller.edgeBuffer!![edge + 1] = PolygonFiller.edgeBuffer!![store + 1]
				PolygonFiller.edgeBuffer!![edge + 2] = PolygonFiller.edgeBuffer!![store + 2]
				PolygonFiller.edgeBuffer!![edge + 3] = PolygonFiller.edgeBuffer!![store + 3]
			}
			edge += 4
		}
		PolygonFiller.edgeBuffer!![store] = pivotX1
		PolygonFiller.edgeBuffer!![store + 1] = pivotY1
		PolygonFiller.edgeBuffer!![store + 2] = pivotX2
		PolygonFiller.edgeBuffer!![store + 3] = pivotY2
		sortEdgesByY(from, store)
		sortEdgesByY(store + 4, to)
	}

	@OriginalMember(owner = "client!df", name = "b", descriptor = "(II)V")
	@JvmStatic
	fun sortActiveEdgesByX(@OriginalArg(0) from: Int, @OriginalArg(1) to: Int) {
		var to: Int = to
		while (true) {
			if (to >= from + 8) {
				@Pc(2) var sorted: Boolean = true
				@Pc(6) var edge: Int = from + 4
				while (edge < to) {
					@Pc(13) var left: Int = PolygonFiller.edgeBuffer!![edge - 4]
					@Pc(17) val right: Int = PolygonFiller.edgeBuffer!![edge]
					if (left > right) {
						sorted = false
						PolygonFiller.edgeBuffer!![edge - 4] = right
						PolygonFiller.edgeBuffer!![edge] = left
						left = PolygonFiller.edgeBuffer!![edge - 2]
						PolygonFiller.edgeBuffer!![edge - 2] = PolygonFiller.edgeBuffer!![edge + 2]
						PolygonFiller.edgeBuffer!![edge + 2] = left
						left = PolygonFiller.edgeBuffer!![edge - 1]
						PolygonFiller.edgeBuffer!![edge - 1] = PolygonFiller.edgeBuffer!![edge + 3]
						PolygonFiller.edgeBuffer!![edge + 3] = left
					}
					edge += 4
				}
				if (!sorted) {
					to -= 4
					continue
				}
			}
			return
		}
	}
}
