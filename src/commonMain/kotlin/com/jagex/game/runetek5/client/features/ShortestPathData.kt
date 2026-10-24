package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.cache.compression.GzipDecompressor
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: Shortest Path's map of the world, built from Void's own collision by
 * a generator kept outside the repo (as RuneLite's plugin ships collision-map.zip), every ordinary door and
 * gate counted open. Since version 3 a byte a tile, a bit for each of the eight steps out of it as the
 * server's own rules have them: Void's walls are not always on both tiles, so a step can be open one way
 * and closed the other, and a wall's corner can close a diagonal its two sides leave open. Version 1, two
 * bits a tile (a step north, a step east, each taken as open both ways), is still read. A tile with no
 * map is closed.
 */
class ShortestPathGrid(regions: Array<Array<ByteArray?>?>) {

	/**
	 * By region id (x shl 8 or y), by level: 4096 bytes, one a tile at localY * 64 + localX, bits north, east,
	 * south, west, north-east, north-west, south-east, south-west ([STEPS]); or 1024 for version 1, bit
	 * (localY * 64 + localX) * 2 north, + 1 east.
	 */
	private val regions: Array<Array<ByteArray?>?> = regions

	private fun plane(x: Int, y: Int, level: Int): ByteArray? {
		if (x < 0 || y < 0 || level < 0 || level > 3 || x >= 16384 || y >= 16384) {
			return null
		}
		return regions[((x shr 6) shl 8) or (y shr 6)]?.get(level)
	}

	/** Version 1's bit: a step north (0) or east (1) from the tile. */
	private fun pair(x: Int, y: Int, level: Int, east: Int): Boolean {
		val bits: ByteArray = plane(x, y, level) ?: return false
		val i: Int = (((y and 63) shl 6) or (x and 63)) * 2 + east
		return (bits[i shr 3].toInt() shr (i and 7)) and 1 != 0
	}

	/** Whether the step (dx, dy) out of tile (x, y) is open. */
	fun canStep(x: Int, y: Int, level: Int, dx: Int, dy: Int): Boolean {
		val bytes: ByteArray = plane(x, y, level) ?: return false
		if (bytes.size == TILE_PLANE) {
			val bit: Int = when {
				dx == 0 -> if (dy > 0) 0 else 2
				dy == 0 -> if (dx > 0) 1 else 3
				dy > 0 -> if (dx > 0) 4 else 5
				else -> if (dx > 0) 6 else 7
			}
			return (bytes[((y and 63) shl 6) or (x and 63)].toInt() shr bit) and 1 != 0
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
			val data: ByteArray = GzipDecompressor().decompress(gzip)
			require(data.size >= 12 && data[0] == 'S'.code.toByte() && data[1] == 'P'.code.toByte() && data[2] == 'C'.code.toByte() && data[3] == 'M'.code.toByte()) { "not a Shortest Path collision map" }
			val version: Int = int(data, 4)
			require(version == 1 || version == 3) { "collision map version $version" }
			val plane: Int = if (version == 3) TILE_PLANE else 1024
			val count: Int = int(data, 8)
			val regions: Array<Array<ByteArray?>?> = arrayOfNulls(65536)
			var p = 12
			for (r in 0 until count) {
				val id: Int = ((data[p].toInt() and 0xFF) shl 8) or (data[p + 1].toInt() and 0xFF)
				val mask: Int = data[p + 2].toInt() and 0xFF
				p += 3
				val planes: Array<ByteArray?> = arrayOfNulls(4)
				for (level in 0 until 4) {
					if (mask and (1 shl level) != 0) {
						planes[level] = data.copyOfRange(p, p + plane)
						p += plane
					}
				}
				regions[id] = planes
			}
			return ShortestPathGrid(regions)
		}

		private fun int(data: ByteArray, at: Int): Int =
			((data[at].toInt() and 0xFF) shl 24) or ((data[at + 1].toInt() and 0xFF) shl 16) or ((data[at + 2].toInt() and 0xFF) shl 8) or (data[at + 3].toInt() and 0xFF)
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

	companion object {
		/** The transport table (transports.tsv.gz): type, src, dst, ticks, label, requirements, hint; # starts a comment. */
		@JvmStatic
		fun decode(gzip: ByteArray): List<ShortestPathTransport> = parse(GzipDecompressor().decompress(gzip).decodeToString())

		@JvmStatic
		fun parse(text: String): List<ShortestPathTransport> {
			val list = ArrayList<ShortestPathTransport>()
			for (line in text.split('\n')) {
				parseLine(line)?.let { list.add(it) }
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
			val parts: List<String> = field.split(' ')
			if (parts.size < 3) {
				return intArrayOf(-1, -1, -1)
			}
			return intArrayOf(parts[0].toIntOrNull() ?: -1, parts[1].toIntOrNull() ?: -1, parts[2].toIntOrNull() ?: -1)
		}
	}
}
