package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.cache.compression.GzipDecompressor
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: Shortest Path's map of the world, built from Void's own collision by
 * a generator kept outside the repo (as RuneLite's plugin ships collision-map.zip). Two bits a tile: whether
 * a step north and a step east are open, every ordinary door and gate counted open. A tile with no map is
 * closed.
 */
class ShortestPathGrid(regions: Array<Array<ByteArray?>?>) {

	/** By region id (x shl 8 or y), by level: 1024 bytes, bit (localY * 64 + localX) * 2 north, + 1 east. */
	private val regions: Array<Array<ByteArray?>?> = regions

	private fun bit(x: Int, y: Int, level: Int, east: Int): Boolean {
		if (x < 0 || y < 0 || level < 0 || level > 3 || x >= 16384 || y >= 16384) {
			return false
		}
		val planes: Array<ByteArray?> = regions[((x shr 6) shl 8) or (y shr 6)] ?: return false
		val bits: ByteArray = planes[level] ?: return false
		val i: Int = (((y and 63) shl 6) or (x and 63)) * 2 + east
		return (bits[i shr 3].toInt() shr (i and 7)) and 1 != 0
	}

	fun canNorth(x: Int, y: Int, level: Int): Boolean = bit(x, y, level, 0)

	fun canEast(x: Int, y: Int, level: Int): Boolean = bit(x, y, level, 1)

	fun canSouth(x: Int, y: Int, level: Int): Boolean = bit(x, y - 1, level, 0)

	fun canWest(x: Int, y: Int, level: Int): Boolean = bit(x - 1, y, level, 1)

	/** RuneLite's rule for a diagonal: both of its sides open on the way, both ways round. */
	fun canStep(x: Int, y: Int, level: Int, dx: Int, dy: Int): Boolean {
		if (dx == 0) {
			return if (dy > 0) canNorth(x, y, level) else canSouth(x, y, level)
		}
		if (dy == 0) {
			return if (dx > 0) canEast(x, y, level) else canWest(x, y, level)
		}
		val horizontal: Boolean = if (dx > 0) canEast(x, y, level) else canWest(x, y, level)
		val vertical: Boolean = if (dy > 0) canNorth(x, y, level) else canSouth(x, y, level)
		if (!horizontal || !vertical) {
			return false
		}
		val thenVertical: Boolean = if (dy > 0) canNorth(x + dx, y, level) else canSouth(x + dx, y, level)
		val thenHorizontal: Boolean = if (dx > 0) canEast(x, y + dy, level) else canWest(x, y + dy, level)
		return thenVertical && thenHorizontal
	}

	/** Whether a step leaves the tile any way: a tile a player can stand on. */
	fun isOpen(x: Int, y: Int, level: Int): Boolean = canNorth(x, y, level) || canEast(x, y, level) || canSouth(x, y, level) || canWest(x, y, level)

	companion object {
		/** Decodes the generator's collision.gz (see ShortestPathDump): "SPCM", version 1, then the regions. */
		@JvmStatic
		fun decode(gzip: ByteArray): ShortestPathGrid {
			val data: ByteArray = GzipDecompressor().decompress(gzip)
			require(data.size >= 12 && data[0] == 'S'.code.toByte() && data[1] == 'P'.code.toByte() && data[2] == 'C'.code.toByte() && data[3] == 'M'.code.toByte()) { "not a Shortest Path collision map" }
			val version: Int = int(data, 4)
			require(version == 1) { "collision map version $version" }
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
						planes[level] = data.copyOfRange(p, p + 1024)
						p += 1024
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
) {
	val anywhere: Boolean get() = srcX < 0

	companion object {
		/** The transport table (transports.tsv.gz): type, src, dst, ticks, label, requirements; # starts a comment. */
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
			return ShortestPathTransport(fields[0], src[0], src[1], src[2], dst[0], dst[1], dst[2], fields[3].toIntOrNull() ?: 1, fields[4], requirements)
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
