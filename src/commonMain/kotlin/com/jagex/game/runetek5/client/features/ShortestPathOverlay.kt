package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Viewport
import com.jagex.game.runetek5.plugin.api.Widget
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: Shortest Path's drawing, RuneLite's PathTileOverlay, PathMinimapOverlay,
 * PathMapOverlay and its highlight overlays. On the game view: the path in the chosen style, the
 * transports' tiles with what to click, the tile counter, the teleport pulse, the unreachable text and
 * the bank pickup hint; over the interfaces: the minimap's dots, the world map's line, and the spell, the
 * backpack's items and the bank's items the next step needs. The debug views draw every transport and the
 * map's walls near the player, and a panel of the search's numbers.
 */
object ShortestPathOverlay {

	private const val BLACK: Int = 0xFF000000.toInt()

	/** The backpack's slots and the bank's items, as Void's 634 interfaces lay them out. */
	private const val INVENTORY_INTERFACE: Int = 149
	private const val BANK_INTERFACE: Int = 762
	private const val BANK_ITEMS: Int = 93

	/** The debug collision view reaches this many tiles round the player. */
	private const val COLLISION_RADIUS: Int = 12

	private val xs: IntArray = IntArray(4)
	private val ys: IntArray = IntArray(4)
	private val point: IntArray = IntArray(2)
	private val other: IntArray = IntArray(2)
	private val previous: IntArray = IntArray(2)
	private val area: IntArray = IntArray(4)

	/** The path on the game view. */
	fun drawScene(g: Graphics, view: Viewport, client: ClientApi) {
		val plugin = ShortestPath
		plugin.drawnCounts[0] = 0
		if (!plugin.enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val player: Player = client.localPlayer() ?: return
		val scene = Scene(client, player.level())
		if (plugin.drawCollisionMap) {
			drawCollision(g, view, client, scene, player)
		}
		if (plugin.drawTransports) {
			drawAllTransports(g, view, client, scene)
		}
		val finder: ShortestPathFinder? = plugin.shown
		if (finder != null) {
			drawPath(g, view, client, scene, finder)
			drawPlayerTexts(g, view, client, scene, player, finder)
		}
		if (plugin.drawDebugPanel) {
			drawDebugPanel(g, view)
		}
	}

	/** The loaded scene: where its corner is, how big it is, the level drawn. */
	private class Scene(client: ClientApi, @JvmField val level: Int) {
		@JvmField val baseX: Int = client.sceneBaseX()
		@JvmField val baseY: Int = client.sceneBaseY()
		@JvmField val size: Int = client.sceneSize()

		fun contains(node: Int): Boolean {
			val x: Int = ShortestPathFinder.x(node) - baseX
			val y: Int = ShortestPathFinder.y(node) - baseY
			return ShortestPathFinder.level(node) == level && x >= 0 && y >= 0 && x < size && y < size
		}

		fun x(node: Int): Int = ShortestPathFinder.x(node) - baseX

		fun y(node: Int): Int = ShortestPathFinder.y(node) - baseY
	}

	/**
	 * RuneLite's PathTileOverlay: each tile filled in the path's colour at half its alpha, no border (lines 4
	 * pixels wide in the line styles), the transports' tiles the same, with what to click written above them
	 * in white once the path is found and reaches its target.
	 */
	private fun drawPath(g: Graphics, view: Viewport, client: ClientApi, scene: Scene, finder: ShortestPathFinder) {
		val plugin = ShortestPath
		val path: IntArray = finder.path
		val colour: Int = halfAlpha(ShortestPath.colour())
		val style: String = plugin.pathStyle
		val lines: Boolean = style == "Lines" || style == "Arrow line" || style == "Tracer"
		val labels: Boolean = plugin.showTransportInfo && finder === plugin.finder && !ShortestPath.unreachable(finder)
		var pulsed = false
		for (i in path.indices) {
			val node: Int = path[i]
			// the tile a transport leaves from is the one before the transport's own
			val leaving: ShortestPathTransport? = if (i + 1 < path.size) finder.pathTransports[i + 1] else null
			if (!pulsed && leaving != null && plugin.showTeleportPulse && isTeleport(leaving)) {
				// RuneLite pulses only the first teleport of the path: the next "teleport now"
				pulsed = true
				if (scene.contains(node)) {
					pulse(g, view, client, scene.x(node), scene.y(node), scene.level)
				}
			}
			if (!scene.contains(node)) {
				continue
			}
			val sx: Int = scene.x(node)
			val sy: Int = scene.y(node)
			if (plugin.drawTiles) {
				val filled: Boolean = style == "Tiles" || style == "Turn markers" && isTurn(path, finder, i)
				if (filled && client.tilePolygon(sx, sy, scene.level, 1, xs, ys)) {
					for (c in 0 until 4) {
						xs[c] += view.x
						ys[c] += view.y
					}
					g.fillPolygon(xs, ys, 4, colour)
				} else if (style == "Sprite markers" && client.projectTile(sx, sy, scene.level, 0, point)) {
					g.fillCircle(view.x + point[0], view.y + point[1], 3, colour)
				}
				if (lines && i + 1 < path.size && leaving == null && scene.contains(path[i + 1])) {
					segment(g, view, client, scene, path[i], path[i + 1], colour, style == "Arrow line" && (i % 5 == 4 || i + 2 == path.size))
				}
				plugin.drawnCounts[0]++
			}
			if (leaving != null && labels && g.fontReady() && client.projectTile(sx, sy, scene.level, 0, point)) {
				label(g, view.x + point[0], view.y + point[1], leaving.hint.ifEmpty { leaving.label }, 0)
			}
			counter(g, view, client, scene, i, path.size)
		}
		if (style == "Tracer" && path.isNotEmpty()) {
			// a dot running along the path, a tile every 4 client cycles
			val node: Int = path[(client.clientCycle() / 4) % path.size]
			if (scene.contains(node) && client.projectTile(scene.x(node), scene.y(node), scene.level, 0, point)) {
				g.fillCircle(view.x + point[0], view.y + point[1], 4, ShortestPath.colour())
			}
		}
	}

	/** RuneLite's drawLabel: white text with a black shadow, its bottom a line above the point, [offset] higher again. */
	private fun label(g: Graphics, x: Int, y: Int, text: String, offset: Int): Int {
		g.textCentered(x, y - g.fontHeight() - offset, text, ShortestPath.colourText, BLACK)
		return g.fontHeight() + LABEL_GAP
	}

	private const val LABEL_GAP: Int = 2

	/** A colour at half its alpha, as RuneLite fills the path's tiles. */
	private fun halfAlpha(argb: Int): Int = (((argb ushr 24) / 2) shl 24) or (argb and 0xFFFFFF)

	/** A teleport (a spell, an item, a wizard, a portal): what RuneLite's pulse marks. */
	private fun isTeleport(transport: ShortestPathTransport): Boolean = transport.anywhere || transport.type.startsWith("TELEPORT") || transport.type == "PORTAL"

	private val ringX: IntArray = IntArray(4)
	private val ringY: IntArray = IntArray(4)

	/**
	 * RuneLite's teleport pulse: two tile-shaped rings growing from the tile to 3.6 times its size and fading
	 * from alpha 170, half a period apart, a period every 1.4 seconds.
	 */
	private fun pulse(g: Graphics, view: Viewport, client: ClientApi, sx: Int, sy: Int, level: Int) {
		if (!client.tilePolygon(sx, sy, level, 1, xs, ys)) {
			return
		}
		val cx: Double = xs.average()
		val cy: Double = ys.average()
		val now: Long = lang.currentTimeMillis()
		for (r in 0 until 2) {
			val phase: Double = ((now + r * PULSE_PERIOD / 2) % PULSE_PERIOD).toDouble() / PULSE_PERIOD
			val scale: Double = 1.0 + phase * 2.6
			val alpha: Int = (170 * (1.0 - phase)).toInt()
			if (alpha <= 0) {
				continue
			}
			for (v in 0 until 4) {
				ringX[v] = view.x + (cx + (xs[v] - cx) * scale).toInt()
				ringY[v] = view.y + (cy + (ys[v] - cy) * scale).toInt()
			}
			val argb: Int = (alpha shl 24) or (ShortestPath.colourTeleportPulse and 0xFFFFFF)
			for (v in 0 until 4) {
				val w: Int = (v + 1) % 4
				g.thickLine(ringX[v], ringY[v], ringX[w], ringY[w], 2, argb)
			}
		}
	}

	private const val PULSE_PERIOD: Long = 1400L

	/** Whether the path turns on tile [i] (or starts, ends or takes a transport there). */
	private fun isTurn(path: IntArray, finder: ShortestPathFinder, i: Int): Boolean {
		if (i == 0 || i == path.size - 1 || finder.pathTransports[i] != null) {
			return true
		}
		val dx1: Int = ShortestPathFinder.x(path[i]) - ShortestPathFinder.x(path[i - 1])
		val dy1: Int = ShortestPathFinder.y(path[i]) - ShortestPathFinder.y(path[i - 1])
		val dx2: Int = ShortestPathFinder.x(path[i + 1]) - ShortestPathFinder.x(path[i])
		val dy2: Int = ShortestPathFinder.y(path[i + 1]) - ShortestPathFinder.y(path[i])
		return dx1 != dx2 || dy1 != dy2
	}

	/** A line between two tiles' middles, with an arrowhead at the second when [arrow]. */
	private fun segment(g: Graphics, view: Viewport, client: ClientApi, scene: Scene, a: Int, b: Int, colour: Int, arrow: Boolean) {
		if (!client.projectTile(scene.x(a), scene.y(a), scene.level, 0, point) || !client.projectTile(scene.x(b), scene.y(b), scene.level, 0, other)) {
			return
		}
		val x1: Int = view.x + point[0]
		val y1: Int = view.y + point[1]
		val x2: Int = view.x + other[0]
		val y2: Int = view.y + other[1]
		g.thickLine(x1, y1, x2, y2, 4, colour)
		if (arrow) {
			val angle: Double = atan2((y2 - y1).toDouble(), (x2 - x1).toDouble())
			for (side in intArrayOf(-1, 1)) {
				val a2: Double = angle + PI + side * 0.5
				g.thickLine(x2, y2, x2 + (cos(a2) * 10).toInt(), y2 + (sin(a2) * 10).toInt(), 4, colour)
			}
		}
	}

	/** RuneLite's tile counter: the tiles travelled to this one, or the tiles left from it, every few tiles. */
	private fun counter(g: Graphics, view: Viewport, client: ClientApi, scene: Scene, i: Int, size: Int) {
		val plugin = ShortestPath
		if (plugin.showTileCounter == "Disabled" || i % plugin.tileCounterStep != 0 || !g.fontReady()) {
			return
		}
		val node: Int = ShortestPath.shown?.path?.get(i) ?: return
		if (!client.projectTile(scene.x(node), scene.y(node), scene.level, 0, point)) {
			return
		}
		val n: Int = if (plugin.showTileCounter == "Travelled") i else size - 1 - i
		g.textCentered(view.x + point[0], view.y + point[1] + g.fontHeight() / 2, n.toString(), plugin.colourText, BLACK)
	}

	/** The index of the path's tile nearest the player: where the player is along it. */
	@JvmStatic
	fun progress(client: ClientApi, finder: ShortestPathFinder): Int {
		val here: Int = ShortestPath.origin(client)
		var best = 0
		var bestDistance: Int = Int.MAX_VALUE
		for (i in finder.path.indices) {
			val d: Int = ShortestPathFinder.distance(here, finder.path[i])
			if (d < bestDistance) {
				bestDistance = d
				best = i
			}
		}
		return best
	}

	/** The next transport from where the player is on the path, with its index; null when the rest is on foot. */
	@JvmStatic
	fun nextTransport(client: ClientApi, finder: ShortestPathFinder): Pair<Int, ShortestPathTransport>? {
		val from: Int = progress(client, finder)
		for (i in from + 1 until finder.pathTransports.size) {
			val transport: ShortestPathTransport? = finder.pathTransports[i]
			if (transport != null) {
				return i to transport
			}
		}
		return null
	}

	/** Over the player: the teleport pulse, the unreachable text and the bank pickup hint. */
	private fun drawPlayerTexts(g: Graphics, view: Viewport, client: ClientApi, scene: Scene, player: Player, finder: ShortestPathFinder) {
		val plugin = ShortestPath
		if (!g.fontReady()) {
			return
		}
		if (plugin.showUnreachableText && ShortestPath.unreachable(finder) && client.projectTile(player.serverTileX(), player.serverTileY(), scene.level, 0, point)) {
			label(g, view.x + point[0], view.y + point[1], plugin.unreachableText, 0)
		}
		// RuneLite's bank pickup: what to take out, written over the bank's tile
		val bank: Int = finder.pathTransports.indexOfFirst { it === ShortestPathFinder.BANK }
		if (bank >= 0 && scene.contains(finder.path[bank]) && client.projectTile(scene.x(finder.path[bank]), scene.y(finder.path[bank]), scene.level, 0, point)) {
			val names: List<String> = pickups(client, finder).mapNotNull { client.item(it)?.name() }
			if (names.isNotEmpty()) {
				label(g, view.x + point[0], view.y + point[1], "Pick up: " + names.joinToString(", "), 0)
			}
		}
	}

	/** The items to take out of the bank: those the transports after the bank visit need and the player lacks. */
	@JvmStatic
	fun pickups(client: ClientApi, finder: ShortestPathFinder): List<Int> {
		val bank: Int = finder.pathTransports.indexOfFirst { it === ShortestPathFinder.BANK }
		if (bank < 0) {
			return emptyList()
		}
		val carried: Map<Int, Int> = ShortestPathPlayer.read(client).items
		val wanted = LinkedHashSet<Int>()
		for (i in bank + 1 until finder.pathTransports.size) {
			val transport: ShortestPathTransport = finder.pathTransports[i] ?: continue
			for (group in transport.requirements["items"].orEmpty().split(',')) {
				val ids: List<Int> = group.substringBefore('x').split('|').mapNotNull { it.toIntOrNull() }
				if (ids.none { (carried[it] ?: 0) > 0 }) {
					ids.firstOrNull { (ShortestPath.bank[it] ?: 0) > 0 }?.let { wanted.add(it) }
				}
			}
		}
		return wanted.toList()
	}

	/** RuneLite's debug view: every transport leaving from a tile of the scene, its tile filled in the transports' colour. */
	private fun drawAllTransports(g: Graphics, view: Viewport, client: ClientApi, scene: Scene) {
		for (transport in ShortestPath.transports) {
			if (transport.anywhere || transport.type == "DEST" || transport.type == "STAFF") {
				continue
			}
			val node: Int = ShortestPathFinder.pack(transport.srcX, transport.srcY, transport.srcLevel)
			if (scene.contains(node) && client.tilePolygon(scene.x(node), scene.y(node), scene.level, 1, xs, ys)) {
				for (c in 0 until 4) {
					xs[c] += view.x
					ys[c] += view.y
				}
				g.fillPolygon(xs, ys, 4, ShortestPath.colourTransports)
			}
		}
	}

	/** The map's closed sides near the player: a line on each tile edge a step cannot cross. */
	private fun drawCollision(g: Graphics, view: Viewport, client: ClientApi, scene: Scene, player: Player) {
		val grid: ShortestPathGrid = ShortestPath.grid ?: return
		val colour: Int = ShortestPath.colourCollisionMap
		for (dx in -COLLISION_RADIUS..COLLISION_RADIUS) {
			for (dy in -COLLISION_RADIUS..COLLISION_RADIUS) {
				val sx: Int = player.serverTileX() + dx
				val sy: Int = player.serverTileY() + dy
				val wx: Int = scene.baseX + sx
				val wy: Int = scene.baseY + sy
				if (sx < 0 || sy < 0 || sx >= scene.size || sy >= scene.size || !client.tilePolygon(sx, sy, scene.level, 1, xs, ys)) {
					continue
				}
				// corners: south-west, north-west, north-east, south-east
				if (!grid.canNorth(wx, wy, scene.level)) {
					g.line(view.x + xs[1], view.y + ys[1], view.x + xs[2], view.y + ys[2], colour)
				}
				if (!grid.canEast(wx, wy, scene.level)) {
					g.line(view.x + xs[2], view.y + ys[2], view.x + xs[3], view.y + ys[3], colour)
				}
			}
		}
	}

	/** RuneLite's debug panel: the search's numbers. */
	private fun drawDebugPanel(g: Graphics, view: Viewport) {
		if (!g.fontReady()) {
			return
		}
		val plugin = ShortestPath
		val finder: ShortestPathFinder? = plugin.finder
		val lines = listOf(
			"Shortest Path",
			"Targets: " + plugin.targets.size + (if (plugin.start != -1) ", own start" else ""),
			"Visited: " + (finder?.visitedCount ?: 0),
			"Path: " + (plugin.shown?.path?.size ?: 0) + " tiles",
			"Done: " + (finder?.done ?: false) + ", reached: " + (finder?.reached ?: false),
			"Cycles: " + plugin.searchCycles + " (" + plugin.searchCycles * 20 + " ms)",
			"Transports: " + plugin.transports.size + ", bank items: " + plugin.bank.size,
		)
		val height: Int = g.fontHeight()
		g.fillRect(view.x + 4, view.y + 4, 190, lines.size * height + 6, 0x90000000.toInt())
		for ((i, text) in lines.withIndex()) {
			g.text(view.x + 8, view.y + 4 + (i + 1) * height, text, plugin.colourText, BLACK)
		}
	}

	/** Over the interfaces: the minimap's dots, the world map's line and targets, and the highlights. */
	fun drawInterfaces(g: Graphics, client: ClientApi) {
		val plugin = ShortestPath
		plugin.drawnCounts[1] = 0
		plugin.drawnCounts[2] = 0
		if (client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val finder: ShortestPathFinder = plugin.shown ?: return
		if (plugin.drawMinimap) {
			drawMinimap(g, client, finder)
		}
		if (plugin.drawMap) {
			drawWorldMap(g, client, finder)
		}
		drawHighlights(g, client, finder)
	}

	/** RuneLite's PathMinimapOverlay: each tile a filled square a tile across, turned with the camera; targets not reached in the calculating colour. */
	private fun drawMinimap(g: Graphics, client: ClientApi, finder: ShortestPathFinder) {
		val player: Player = client.localPlayer() ?: return
		if (!client.minimapArea(area)) {
			return
		}
		val scene = Scene(client, player.level())
		g.clip(area[0], area[1], area[2], area[3])
		val colour: Int = ShortestPath.colour()
		for (node in finder.path) {
			if (scene.contains(node) && minimapSquare(g, client, scene, node, colour)) {
				ShortestPath.drawnCounts[1]++
			}
		}
		val end: Int = finder.path.lastOrNull() ?: -1
		for (target in ShortestPath.targets) {
			if (target != end && scene.contains(target)) {
				minimapSquare(g, client, scene, target, ShortestPath.colourCalculating)
			}
		}
		g.unclip()
	}

	/** A tile on the minimap: its centre and the steps to the tiles east and north give the square's sides. */
	private fun minimapSquare(g: Graphics, client: ClientApi, scene: Scene, node: Int, colour: Int): Boolean {
		val sx: Int = scene.x(node)
		val sy: Int = scene.y(node)
		if (!client.projectMinimap(sx, sy, point) || !client.projectMinimap(sx + 1, sy, other) || !client.projectMinimap(sx, sy + 1, previous)) {
			return false
		}
		val ex: Int = other[0] - point[0]
		val ey: Int = other[1] - point[1]
		val nx: Int = previous[0] - point[0]
		val ny: Int = previous[1] - point[1]
		// the corners at half a tile each way from the centre
		xs[0] = point[0] - (ex + nx) / 2
		ys[0] = point[1] - (ey + ny) / 2
		xs[1] = xs[0] + ex
		ys[1] = ys[0] + ey
		xs[2] = xs[1] + nx
		ys[2] = ys[1] + ny
		xs[3] = xs[0] + nx
		ys[3] = ys[0] + ny
		g.fillPolygon(xs, ys, 4, colour)
		return true
	}

	/**
	 * RuneLite's PathMapOverlay: each tile a filled square a tile across, a transport's jump a dashed line,
	 * and targets not reached in the calculating colour.
	 */
	private fun drawWorldMap(g: Graphics, client: ClientApi, finder: ShortestPathFinder) {
		if (!client.worldMapArea(area)) {
			return
		}
		g.clip(area[0], area[1], area[2], area[3])
		val path: IntArray = finder.path
		val colour: Int = ShortestPath.colour()
		for (i in path.indices) {
			val node: Int = path[i]
			if (i > 0 && ShortestPathFinder.distance(path[i - 1], node) > 1 && mapPoint(client, path[i - 1], previous) && mapPoint(client, node, point)) {
				dashed(g, previous[0], previous[1], point[0], point[1], colour)
				ShortestPath.drawnCounts[2]++
			}
			if (mapSquare(g, client, node, colour)) {
				ShortestPath.drawnCounts[2]++
			}
		}
		val end: Int = path.lastOrNull() ?: -1
		for (target in ShortestPath.targets) {
			if (target != end) {
				mapSquare(g, client, target, ShortestPath.colourCalculating)
			}
		}
		g.unclip()
	}

	private fun mapPoint(client: ClientApi, node: Int, out: IntArray): Boolean =
		client.projectWorldMap(ShortestPathFinder.x(node), ShortestPathFinder.y(node), ShortestPathFinder.level(node), out)

	/** A tile on the world map, as wide as the map draws a tile (at least a pixel). */
	private fun mapSquare(g: Graphics, client: ClientApi, node: Int, colour: Int): Boolean {
		if (!mapPoint(client, node, point)) {
			return false
		}
		val corner: Int = ShortestPathFinder.pack(ShortestPathFinder.x(node) + 1, ShortestPathFinder.y(node) - 1, ShortestPathFinder.level(node))
		val size: Int = if (mapPoint(client, corner, other)) maxOf(1, other[0] - point[0]) else 2
		g.fillRect(point[0] - size / 2, point[1] - size / 2, size, size, colour)
		return true
	}

	/** RuneLite's jump stroke: a one-pixel line, nine on and nine off. */
	private fun dashed(g: Graphics, x1: Int, y1: Int, x2: Int, y2: Int, colour: Int) {
		val dx: Double = (x2 - x1).toDouble()
		val dy: Double = (y2 - y1).toDouble()
		val length: Double = kotlin.math.sqrt(dx * dx + dy * dy)
		if (length < 1.0) {
			return
		}
		var t = 0.0
		while (t < length) {
			val t2: Double = minOf(length, t + 9.0)
			g.line(x1 + (dx * t / length).toInt(), y1 + (dy * t / length).toInt(), x1 + (dx * t2 / length).toInt(), y1 + (dy * t2 / length).toInt(), colour)
			t += 18.0
		}
	}

	/** RuneLite's highlight overlays: the spell to cast, the backpack's item to use, the bank's items to take. */
	private fun drawHighlights(g: Graphics, client: ClientApi, finder: ShortestPathFinder) {
		val plugin = ShortestPath
		val next: ShortestPathTransport? = nextTransport(client, finder)?.second
		if (next != null && plugin.highlightSpellbookSpells) {
			val widget: List<Int> = next.requirements["widget"].orEmpty().split(':').mapNotNull { it.toIntOrNull() }
			if (widget.size == 2) {
				client.widget(widget[0], widget[1])?.let { box(g, it, plugin.colourTransports) }
			}
		}
		if (next != null && next.type == "TELEPORT_ITEM" && plugin.highlightInventoryItems) {
			val ids: List<Int> = ShortestPathRules.itemIds(next)
			highlightItems(g, client.widget(INVENTORY_INTERFACE, 0), ids, plugin.colourTransports)
		}
		if (plugin.highlightBankPickupItems && finder.pathTransports.any { it === ShortestPathFinder.BANK }) {
			highlightItems(g, client.widget(BANK_INTERFACE, BANK_ITEMS), pickups(client, finder), plugin.colourBankPickup)
		}
	}

	private fun highlightItems(g: Graphics, container: Widget?, ids: List<Int>, colour: Int) {
		if (container == null || ids.isEmpty() || !container.isDrawnThisFrame()) {
			return
		}
		for (i in 0 until container.childCount()) {
			val child: Widget = container.child(i) ?: continue
			if (child.itemId() in ids && !child.isHidden()) {
				box(g, child, colour)
			}
		}
	}

	private fun box(g: Graphics, widget: Widget, colour: Int) {
		if (widget.isHidden() || !widget.isDrawnThisFrame() || abs(widget.width()) == 0) {
			return
		}
		g.drawRect(widget.screenX(), widget.screenY(), widget.width(), widget.height(), colour)
		g.drawRect(widget.screenX() + 1, widget.screenY() + 1, widget.width() - 2, widget.height() - 2, colour)
	}
}
