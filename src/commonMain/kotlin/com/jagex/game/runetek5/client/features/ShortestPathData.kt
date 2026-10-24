package com.jagex.game.runetek5.client.features

import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: Shortest Path's map of the world, built from Void's own collision by
 * a generator kept outside the repo (as RuneLite's plugin ships collision-map.zip), every ordinary door and
 * gate counted open and the guarded ones shut. Since version 3 a byte a tile, a bit for each of the eight
 * steps out of it as the server's own rules have them: Void's walls are not always on both tiles, so a step
 * can be open one way and closed the other, and a wall's corner can close a diagonal its two sides leave
 * open. Version 1, two bits a tile (a step north, a step east, each taken as open both ways), is still read.
 * A tile with no map is closed. The planes stay in the one array they were decoded into, by offset.
 */
class ShortestPathGrid private constructor(
	private val data: ByteArray,
	/** By (region id shl 2 or level): where the plane starts in [data], -1 for none. */
	private val offsets: IntArray,
	/** 4096 (version 3) or 1024 (version 1). */
	private val planeSize: Int,
) {

	/** A grid of separate planes, by region id (x shl 8 or y) then level, all of version 1's or all of version 3's size. */
	constructor(regions: Array<Array<ByteArray?>?>) : this(concat(regions), offsetsOf(regions), sizeOf(regions))

	private fun offset(x: Int, y: Int, level: Int): Int {
		if (x < 0 || y < 0 || level < 0 || level > 3 || x > 0x3FFF || y > 0x3FFF) {
			return -1
		}
		return offsets[(((x shr 6) shl 8) or (y shr 6)) * 4 + level]
	}

	/** Version 1's bit: a step north (0) or east (1) from the tile. */
	private fun pair(x: Int, y: Int, level: Int, east: Int): Boolean {
		val at: Int = offset(x, y, level)
		if (at < 0) {
			return false
		}
		val i: Int = (((y and 63) shl 6) or (x and 63)) * 2 + east
		return (data[at + (i shr 3)].toInt() shr (i and 7)) and 1 != 0
	}

	/** Whether the step (dx, dy) out of tile (x, y) is open. */
	fun canStep(x: Int, y: Int, level: Int, dx: Int, dy: Int): Boolean {
		if (planeSize == TILE_PLANE) {
			val at: Int = offset(x, y, level)
			if (at < 0) {
				return false
			}
			val bit: Int = when {
				dx == 0 -> if (dy > 0) 0 else 2
				dy == 0 -> if (dx > 0) 1 else 3
				dy > 0 -> if (dx > 0) 4 else 5
				else -> if (dx > 0) 6 else 7
			}
			return (data[at + (((y and 63) shl 6) or (x and 63))].toInt() shr bit) and 1 != 0
		}
		if (dx == 0) {
			return if (dy > 0) pair(x, y, level, 0) else pair(x, y - 1, level, 0)
		}
		if (dy == 0) {
			return if (dx > 0) pair(x, y, level, 1) else pair(x - 1, y, level, 1)
		}
		// version 1: RuneLite's rule, both sides of the diagonal open on the way, both ways round
		return canStep(x, y, level, dx, 0) && canStep(x, y, level, 0, dy) && canStep(x + dx, y, level, 0, dy) && canStep(x, y + dy, level, dx, 0)
	}

	fun canNorth(x: Int, y: Int, level: Int): Boolean = canStep(x, y, level, 0, 1)

	fun canEast(x: Int, y: Int, level: Int): Boolean = canStep(x, y, level, 1, 0)

	fun canSouth(x: Int, y: Int, level: Int): Boolean = canStep(x, y, level, 0, -1)

	fun canWest(x: Int, y: Int, level: Int): Boolean = canStep(x, y, level, -1, 0)

	/** Whether a step leaves the tile any way: a tile a player can stand on. */
	fun isOpen(x: Int, y: Int, level: Int): Boolean = canNorth(x, y, level) || canEast(x, y, level) || canSouth(x, y, level) || canWest(x, y, level)

	companion object {
		/** A version 3 plane: a byte a tile. */
		private const val TILE_PLANE: Int = 4096

		/** Decodes the generator's collision.gz (see ShortestPathDump): "SPCM", version 1 or 3, then the regions. */
		@JvmStatic
		fun decode(gzip: ByteArray): ShortestPathGrid {
			val data: ByteArray = ShortestPathGzip.gunzip(gzip)
			require(data.size >= 12 && data[0] == 'S'.code.toByte() && data[1] == 'P'.code.toByte() && data[2] == 'C'.code.toByte() && data[3] == 'M'.code.toByte()) { "not a Shortest Path collision map" }
			val version: Int = int(data, 4)
			require(version == 1 || version == 3) { "collision map version $version" }
			val plane: Int = if (version == 3) TILE_PLANE else 1024
			val count: Int = int(data, 8)
			val offsets = IntArray(65536 * 4) { -1 }
			var p = 12
			for (r in 0 until count) {
				require(p + 3 <= data.size) { "collision map cut short" }
				val id: Int = ((data[p].toInt() and 0xFF) shl 8) or (data[p + 1].toInt() and 0xFF)
				val mask: Int = data[p + 2].toInt() and 0xFF
				p += 3
				for (level in 0 until 4) {
					if (mask and (1 shl level) != 0) {
						require(p + plane <= data.size) { "collision map cut short" }
						offsets[id * 4 + level] = p
						p += plane
					}
				}
			}
			return ShortestPathGrid(data, offsets, plane)
		}

		private fun sizeOf(regions: Array<Array<ByteArray?>?>): Int = regions.firstNotNullOfOrNull { planes -> planes?.firstNotNullOfOrNull { it?.size } } ?: TILE_PLANE

		private fun concat(regions: Array<Array<ByteArray?>?>): ByteArray {
			val parts: List<ByteArray> = regions.flatMap { planes -> planes?.filterNotNull() ?: emptyList() }
			val out = ByteArray(parts.sumOf { it.size })
			var p = 0
			for (part in parts) {
				part.copyInto(out, p)
				p += part.size
			}
			return out
		}

		private fun offsetsOf(regions: Array<Array<ByteArray?>?>): IntArray {
			val offsets = IntArray(65536 * 4) { -1 }
			var p = 0
			for (id in regions.indices) {
				val planes: Array<ByteArray?> = regions[id] ?: continue
				for (level in 0 until minOf(4, planes.size)) {
					val bytes: ByteArray = planes[level] ?: continue
					offsets[id * 4 + level] = p
					p += bytes.size
				}
			}
			return offsets
		}

		private fun int(data: ByteArray, at: Int): Int =
			((data[at].toInt() and 0xFF) shl 24) or ((data[at + 1].toInt() and 0xFF) shl 16) or ((data[at + 2].toInt() and 0xFF) shl 8) or (data[at + 3].toInt() and 0xFF)
	}
}

/**
 * Not part of the original client: gzip for Shortest Path's files with any header (RFC 1952: a name, a
 * comment, extra fields, a header CRC), not only the bare one GzipDecompressor takes, so files written by
 * the gzip tool read as well as Java's.
 */
object ShortestPathGzip {
	@JvmStatic
	fun gunzip(gzip: ByteArray): ByteArray {
		require(gzip.size >= 18 && gzip[0].toInt() == 0x1F && (gzip[1].toInt() and 0xFF) == 0x8B && gzip[2].toInt() == 8) { "not gzip" }
		val flags: Int = gzip[3].toInt() and 0xFF
		var p = 10
		if (flags and 4 != 0) {
			p += 2 + ((gzip[p].toInt() and 0xFF) or ((gzip[p + 1].toInt() and 0xFF) shl 8))
		}
		if (flags and 8 != 0) {
			while (gzip[p].toInt() != 0) p++
			p++
		}
		if (flags and 16 != 0) {
			while (gzip[p].toInt() != 0) p++
			p++
		}
		if (flags and 2 != 0) {
			p += 2
		}
		val n: Int = gzip.size
		val length: Int = (gzip[n - 4].toInt() and 0xFF) or ((gzip[n - 3].toInt() and 0xFF) shl 8) or ((gzip[n - 2].toInt() and 0xFF) shl 16) or ((gzip[n - 1].toInt() and 0xFF) shl 24)
		val out = ByteArray(length)
		val inflater = util.zip.Inflater(true)
		inflater.setInput(gzip, p, n - 8 - p)
		inflater.inflate(out)
		inflater.reset()
		return out
	}
}

/**
 * Not part of the original client: an area of the map as Void draws it, a polygon on a range of levels,
 * with Void's own test (Polygon.pointInPolygon: integer arithmetic, a tile on an edge inside).
 */
class ShortestPathArea(
	private val xs: IntArray,
	private val ys: IntArray,
	private val minLevel: Int,
	private val maxLevel: Int,
) {
	private val minX: Int = xs.minOrNull() ?: 0
	private val maxX: Int = xs.maxOrNull() ?: -1
	private val minY: Int = ys.minOrNull() ?: 0
	private val maxY: Int = ys.maxOrNull() ?: -1

	fun contains(x: Int, y: Int, level: Int): Boolean {
		if (xs.size <= 2 || level < minLevel || level > maxLevel || x < minX || x > maxX || y < minY || y > maxY) {
			return false
		}
		var inside = false
		var j: Int = xs.size - 1
		for (i in xs.indices) {
			val dxl: Int = xs[j] - xs[i]
			val dyl: Int = ys[j] - ys[i]
			if ((ys[i] > y) != (ys[j] > y) && x < dxl * (y - ys[i]) / dyl + xs[i]) {
				inside = !inside
			}
			if ((x - xs[i]) * dyl - (y - ys[i]) * dxl == 0) {
				val onEdge: Boolean = if (kotlin.math.abs(dxl) >= kotlin.math.abs(dyl)) {
					x in xs[if (dxl > 0) i else j]..xs[if (dxl > 0) j else i]
				} else {
					y in ys[if (dyl > 0) i else j]..ys[if (dyl > 0) j else i]
				}
				if (onEdge) {
					return true
				}
			}
			j = i
		}
		return inside
	}

	companion object {
		/** An AREA row's requirements: `x=a,b,...;y=a,b,...;levels=min-max`. */
		@JvmStatic
		fun of(row: ShortestPathTransport): ShortestPathArea? {
			val xs: List<Int> = row.requirements["x"].orEmpty().split(',').mapNotNull { it.toIntOrNull() }
			val ys: List<Int> = row.requirements["y"].orEmpty().split(',').mapNotNull { it.toIntOrNull() }
			if (xs.size != ys.size || xs.size < 3) {
				return null
			}
			val levels: List<Int> = row.requirements["levels"].orEmpty().split('-').mapNotNull { it.toIntOrNull() }
			return ShortestPathArea(xs.toIntArray(), ys.toIntArray(), levels.getOrElse(0) { 0 }, levels.getOrElse(1) { 0 })
		}
	}
}

/** One way across the map that is not a step: stairs, a ladder, a teleport, a ship. */
class ShortestPathTransport(
	@JvmField val type: String,
	/** The tile it starts from, -1 for one that works anywhere (a teleport). */
	@JvmField val srcX: Int,
	@JvmField val srcY: Int,
	@JvmField val srcLevel: Int,
	@JvmField val dstX: Int,
	@JvmField val dstY: Int,
	@JvmField val dstLevel: Int,
	@JvmField val ticks: Int,
	@JvmField val label: String,
	/** The generator's requirement fields, `key=value` split on ';' (magic=25, items=563x1,556x3, equipped=1704|1706). */
	@JvmField val requirements: Map<String, String>,
	/** What to click: the menu option and the dialogue choices ("Rub Amulet of glory > Edgeville"), or "". */
	@JvmField val hint: String = "",
) {
	val anywhere: Boolean get() = srcX < 0

	/** The spell's button to highlight (`widget=<interface>:<component>`), packed interface shl 16 or component; -1 for none. */
	@JvmField
	val widget: Int = requirements["widget"]?.split(':')?.let { parts ->
		val id: Int? = parts.getOrNull(0)?.toIntOrNull()
		val component: Int? = parts.getOrNull(1)?.toIntOrNull()
		if (id == null || component == null) -1 else (id shl 16) or (component and 0xFFFF)
	} ?: -1

	companion object {
		/** The transport table (transports.tsv.gz): type, src, dst, ticks, label, requirements, hint; # starts a comment. */
		@JvmStatic
		fun decode(gzip: ByteArray): List<ShortestPathTransport> = parse(ShortestPathGzip.gunzip(gzip).decodeToString())

		@JvmStatic
		fun parse(text: String): List<ShortestPathTransport> {
			val list = ArrayList<ShortestPathTransport>()
			for (line in text.split('\n')) {
				parseLine(line.trimEnd('\r'))?.let { list.add(it) }
			}
			return list
		}

		/** One row of the table, null for a comment, a blank line or a row too short to be one. */
		private fun parseLine(line: String): ShortestPathTransport? {
			if (line.isEmpty() || line.startsWith("#")) {
				return null
			}
			val fields: List<String> = line.split('\t')
			if (fields.size < 5) {
				return null
			}
			val src: IntArray = tile(fields[1])
			val dst: IntArray = tile(fields[2])
			val requirements = HashMap<String, String>()
			for (part in fields.getOrElse(5) { "" }.split(';')) {
				val eq: Int = part.indexOf('=')
				if (part.isNotEmpty()) {
					requirements[if (eq < 0) part else part.substring(0, eq)] = if (eq < 0) "" else part.substring(eq + 1)
				}
			}
			return ShortestPathTransport(fields[0], src[0], src[1], src[2], dst[0], dst[1], dst[2], fields[3].toIntOrNull() ?: 1, fields[4], requirements, fields.getOrElse(6) { "" })
		}

		private fun tile(field: String): IntArray {
			val parts: List<String> = field.trim().split(' ').filter { it.isNotEmpty() }
			if (parts.size < 3) {
				return intArrayOf(-1, -1, -1)
			}
			return intArrayOf(parts[0].toIntOrNull() ?: -1, parts[1].toIntOrNull() ?: -1, parts[2].toIntOrNull() ?: -1)
		}
	}
}
