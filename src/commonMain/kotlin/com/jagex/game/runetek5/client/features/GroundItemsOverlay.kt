package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.GameState
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Player
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: Ground Items' drawing, RuneLite's GroundItemsOverlay over this
 * client's API: the label over each item (stacked 15 pixels a label on a busy tile), the boxes and the
 * hover backdrop while the hotkey is held, the despawn timer as a pie or a count, the tile outline, and
 * the lootbeams. RuneLite's lootbeam is a model the client adds to the scene; this API draws in 2D, so
 * the beam is drawn over the scene: a column of the item's colour fading upwards from the pile.
 */
object GroundItemsOverlay {

	/** RuneLite's MAX_DISTANCE, 2500 of its local units, in tiles. */
	private const val MAX_DISTANCE_TILES: Double = 2500.0 / 128.0

	/** RuneLite's OFFSET_Z (20 of its units; this client's scene has 512 a tile, not 128). */
	private const val OFFSET_Z: Int = 80

	private const val STRING_GAP: Int = 15

	private const val RECTANGLE_SIZE: Int = 8

	private const val TIMER_DIAMETER: Int = 10

	private const val BLACK: Int = -0x1000000

	private const val WHITE: Int = -0x1

	private const val NO_SHADOW: Int = -1

	private const val RED: Int = 0xFFFF0000.toInt()

	private const val GREEN: Int = 0xFF00FF00.toInt()

	/** RuneLite's BackgroundComponent fill (ColorScheme.DARKER_GRAY_COLOR at 156 of 255). */
	private const val BACKGROUND: Int = 0x9C1E1E1E.toInt()

	/** RuneLite's renderPolygon fill under a tile's outline. */
	private const val TILE_FILL: Int = 0x32000000

	/** How tall a lootbeam stands, in scene units (512 a tile). */
	private const val BEAM_HEIGHT: Int = 1400

	private const val BEAM_SEGMENTS: Int = 12

	private val point: IntArray = IntArray(2)

	private val point2: IntArray = IntArray(2)

	private val xs: IntArray = IntArray(40)

	private val ys: IntArray = IntArray(40)

	/** Labels drawn this frame on each tile, for their stacking (RuneLite's offsetMap). */
	private val offsets: HashMap<Long, Int> = HashMap()

	/** Items drawn last frame, for the smoke test; the last label's text. */
	@JvmField
	var drawnLastFrame: Int = 0

	@JvmField
	var lastLabel: String = ""

	@JvmField
	var beamsLastFrame: Int = 0

	@JvmStatic
	fun draw(g: Graphics, view: Viewport) {
		drawnLastFrame = 0
		beamsLastFrame = 0
		val plugin: GroundItems = GroundItems
		val client: ClientApi = plugin.context()?.client() ?: return
		if (!plugin.enabled || client.gameState() !== GameState.LOGGED_IN) {
			return
		}
		val player: Player = client.localPlayer() ?: return
		drawBeams(g, view, client, player)
		val hotkey: Boolean = plugin.hotKeyPressed
		val dontShowOverlay: Boolean = (plugin.itemHighlightMode == GroundItems.HIGHLIGHT_MENU || plugin.itemHighlightMode == GroundItems.HIGHLIGHT_NONE || plugin.hideAll) && !hotkey
		if (dontShowOverlay && !plugin.highlightTiles) {
			return
		}
		offsets.clear()
		val mouseX: Int = client.mouseX()
		val mouseY: Int = client.mouseY()
		var list: List<GroundItems.Item> = plugin.items
		var top: GroundItems.Item? = null
		if (hotkey) {
			// the offsets are fixed first, so that the item under the mouse can be drawn last (on top) without moving
			val copy: ArrayList<GroundItems.Item> = ArrayList(plugin.items)
			for (item in copy) {
				item.offset = nextOffset(item)
				if (top == null && underMouse(item, mouseX, mouseY)) {
					top = item
				}
			}
			if (top != null) {
				copy.remove(top)
				copy.add(top)
			}
			list = copy
		}
		plugin.textBoxItem = null
		plugin.hiddenBoxItem = null
		plugin.highlightBoxItem = null
		offsets.clear()
		val baseX: Int = client.sceneBaseX()
		val baseY: Int = client.sceneBaseY()
		val playerX: Int = baseX + player.tileX()
		val playerY: Int = baseY + player.tileY()
		val now: Int = client.clientCycle()
		val instance: Boolean = client.isInInstance()
		for (item in list) {
			if (item.level == player.level() && !tooFar(item, playerX, playerY) && GroundItems.shouldDisplay(item.ownership)) {
				drawItem(g, view, client, item, top, dontShowOverlay, mouseX, mouseY, now, instance)
			}
		}
	}

	/** One item: judged, then (unless hidden) its tile and its label. */
	private fun drawItem(g: Graphics, view: Viewport, client: ClientApi, item: GroundItems.Item, top: GroundItems.Item?, dontShowOverlay: Boolean, mouseX: Int, mouseY: Int, now: Int, instance: Boolean) {
		val plugin: GroundItems = GroundItems
		GroundItems.judge(item)
		if (!item.highlighted && !plugin.hotKeyPressed && (item.hidden || plugin.showHighlightedOnly)) {
			return
		}
		if (plugin.highlightTiles) {
			drawTile(g, view, client, item)
		}
		if (!dontShowOverlay) {
			drawLabel(g, view, client, item, top, mouseX, mouseY, now, instance)
		}
	}

	private fun nextOffset(item: GroundItems.Item): Int {
		val tile: Long = LootAttribution.tileKey(item.worldX, item.worldY, item.level)
		val offset: Int = offsets[tile]?.let { it + 1 } ?: 0
		offsets[tile] = offset
		return offset
	}

	private fun underMouse(item: GroundItems.Item, x: Int, y: Int): Boolean {
		val plugin: GroundItems = GroundItems
		return (item === plugin.textBoxItem && inside(plugin.textBox, x, y)) || (item === plugin.hiddenBoxItem && inside(plugin.hiddenBox, x, y)) || (item === plugin.highlightBoxItem && inside(plugin.highlightBox, x, y))
	}

	private fun inside(box: IntArray, x: Int, y: Int): Boolean {
		return x >= box[0] && y >= box[1] && x <= box[0] + box[2] && y <= box[1] + box[3]
	}

	private fun tooFar(item: GroundItems.Item, playerX: Int, playerY: Int): Boolean {
		val dx: Double = (item.worldX - playerX).toDouble()
		val dy: Double = (item.worldY - playerY).toDouble()
		return dx * dx + dy * dy > MAX_DISTANCE_TILES * MAX_DISTANCE_TILES
	}

	/** The item's label, as RuneLite builds it: name, quantity, then its prices by the display mode (none for coins). */
	@JvmStatic
	fun label(item: GroundItems.Item): String {
		val plugin: GroundItems = GroundItems
		val text: StringBuilder = StringBuilder(item.name)
		if (item.quantity > 1) {
			text.append(" (").append(GroundItemRules.quantityToStackSize(item.quantity.toLong())).append(')')
		}
		if (item.itemId != GroundItems.COINS) {
			val mode: Int = plugin.priceDisplayMode
			if (mode == GroundItems.PRICE_BOTH) {
				if (item.geTotal() > 0) {
					text.append(" (GE: ").append(GroundItemRules.quantityToStackSize(item.geTotal())).append(" gp)")
				}
				if (item.haTotal() > 0) {
					text.append(" (HA: ").append(GroundItemRules.quantityToStackSize(item.haTotal())).append(" gp)")
				}
			} else if (mode != GroundItems.PRICE_OFF) {
				val price: Long = if (mode == GroundItems.PRICE_GE) item.geTotal() else item.haTotal()
				if (price > 0) {
					text.append(" (").append(GroundItemRules.quantityToStackSize(price)).append(" gp)")
				}
			}
		}
		return text.toString()
	}

	private fun drawLabel(g: Graphics, view: Viewport, client: ClientApi, item: GroundItems.Item, top: GroundItems.Item?, mouseX: Int, mouseY: Int, now: Int, instance: Boolean) {
		val plugin: GroundItems = GroundItems
		val text: String = label(item)
		val sceneX: Int = item.worldX - client.sceneBaseX()
		val sceneY: Int = item.worldY - client.sceneBaseY()
		if (!client.projectTile(sceneX, sceneY, item.level, item.height + OFFSET_Z, point)) {
			return
		}
		val width: Int = g.textWidth(text)
		val offset: Int = if (plugin.hotKeyPressed) item.offset else nextOffset(item)
		val textX: Int = view.x + point[0] - width / 2
		val textY: Int = view.y + point[1] - STRING_GAP * offset
		if (plugin.hotKeyPressed) {
			drawBoxes(g, item, item === top, width, textX, textY, mouseX, mouseY)
		}
		if (plugin.groundItemTimers == GroundItems.TIMER_PIE || plugin.hotKeyPressed) {
			drawPie(g, item, textX, textY, now, instance)
		} else if (plugin.groundItemTimers == GroundItems.TIMER_SECONDS || plugin.groundItemTimers == GroundItems.TIMER_TICKS) {
			val despawn: Int = GroundItems.despawnCycle(item, instance)
			val colour: Int? = GroundItems.timerColour(item, now, instance)
			if (despawn >= 0 && colour != null) {
				val millis: Long = (despawn - now).toLong() * 20L
				drawText(g, textX + width, textY, timerText(millis, plugin.groundItemTimers == GroundItems.TIMER_SECONDS), colour)
			}
		}
		drawText(g, textX, textY, text, item.colour)
		drawnLastFrame++
		lastLabel = text
	}

	/** RuneLite's " - %.1f" seconds or " - %d" ticks of 600 ms. */
	@JvmStatic
	fun timerText(millis: Long, seconds: Boolean): String {
		if (!seconds) {
			return " - " + (millis / 600L)
		}
		val tenths: Long = (millis + 50L) / 100L
		return " - " + (tenths / 10L) + "." + (tenths % 10L)
	}

	/** Text with RuneLite's TextComponent shadow (one pixel down and right) or, with Text outline, a black outline. */
	private fun drawText(g: Graphics, x: Int, y: Int, text: String, argb: Int) {
		if (GroundItems.textOutline) {
			g.text(x + 1, y, text, BLACK, NO_SHADOW)
			g.text(x - 1, y, text, BLACK, NO_SHADOW)
			g.text(x, y + 1, text, BLACK, NO_SHADOW)
			g.text(x, y - 1, text, BLACK, NO_SHADOW)
			g.text(x, y, text, argb, NO_SHADOW)
		} else {
			g.text(x, y, text, argb, BLACK)
		}
	}

	/** The hover backdrop and the "−" and "+" boxes after the label, and which of them the mouse is on (for the click). */
	private fun drawBoxes(g: Graphics, item: GroundItems.Item, isTop: Boolean, width: Int, textX: Int, textY: Int, mouseX: Int, mouseY: Int) {
		val plugin: GroundItems = GroundItems
		val height: Int = g.fontHeight()
		val bx: Int = textX - 2
		val by: Int = textY - height - 2
		val bw: Int = width + 4
		val bh: Int = height + 4
		val hx: Int = bx + bw + 2
		val hy: Int = textY - (RECTANGLE_SIZE + height) / 2
		val lx: Int = hx + RECTANGLE_SIZE + 2
		val inText: Boolean = mouseX >= bx && mouseX <= bx + bw && mouseY >= by && mouseY <= by + bh
		val inHidden: Boolean = mouseX >= hx && mouseX <= hx + RECTANGLE_SIZE && mouseY >= hy && mouseY <= hy + RECTANGLE_SIZE
		val inHighlight: Boolean = mouseX >= lx && mouseX <= lx + RECTANGLE_SIZE && mouseY >= hy && mouseY <= hy + RECTANGLE_SIZE
		if (inText) {
			set(plugin.textBox, bx, by, bw, bh)
			plugin.textBoxItem = item
		} else if (inHidden) {
			set(plugin.hiddenBox, hx, hy, RECTANGLE_SIZE, RECTANGLE_SIZE)
			plugin.hiddenBoxItem = item
		} else if (inHighlight) {
			set(plugin.highlightBox, lx, hy, RECTANGLE_SIZE, RECTANGLE_SIZE)
			plugin.highlightBoxItem = item
		}
		if (isTop && (inText || inHidden || inHighlight)) {
			g.fillRect(bx, by, bw, bh, BACKGROUND)
			g.drawRect(bx, by, bw, bh, BLACK)
		}
		box(g, hx, hy, if (isTop && inHidden) RED else item.colour, item.hidden, true)
		box(g, lx, hy, if (isTop && inHighlight) GREEN else item.colour, item.highlighted, false)
	}

	private fun set(box: IntArray, x: Int, y: Int, w: Int, h: Int) {
		box[0] = x
		box[1] = y
		box[2] = w
		box[3] = h
	}

	/** RuneLite's drawRectangle: a shadowed square, filled when the item is on that list, with a minus, and a plus for the highlight box. */
	private fun box(g: Graphics, x: Int, y: Int, argb: Int, inList: Boolean, hiddenBox: Boolean) {
		g.drawRect(x + 1, y + 1, RECTANGLE_SIZE + 1, RECTANGLE_SIZE + 1, BLACK)
		g.drawRect(x, y, RECTANGLE_SIZE + 1, RECTANGLE_SIZE + 1, argb)
		if (inList) {
			g.fillRect(x, y, RECTANGLE_SIZE + 1, RECTANGLE_SIZE + 1, argb)
		}
		g.line(x + 2, y + RECTANGLE_SIZE / 2, x + RECTANGLE_SIZE - 2, y + RECTANGLE_SIZE / 2, WHITE)
		if (!hiddenBox) {
			g.line(x + RECTANGLE_SIZE / 2, y + 2, x + RECTANGLE_SIZE / 2, y + RECTANGLE_SIZE - 2, WHITE)
		}
	}

	/** RuneLite's timer pie: left of the label, draining as the item nears its despawn, green while private, yellow once public. */
	private fun drawPie(g: Graphics, item: GroundItems.Item, textX: Int, textY: Int, now: Int, instance: Boolean) {
		val despawn: Int = GroundItems.despawnCycle(item, instance)
		val colour: Int = GroundItems.timerColour(item, now, instance) ?: return
		if (despawn <= item.spawnCycle) {
			return
		}
		val percent: Float = (now - item.spawnCycle).toFloat() / (despawn - item.spawnCycle).toFloat()
		val progress: Float = 1f - percent
		val cx: Int = textX - TIMER_DIAMETER
		val cy: Int = textY - TIMER_DIAMETER / 2
		val radius: Float = TIMER_DIAMETER / 2f
		val steps: Int = maxOf(2, (progress * 32f).toInt())
		xs[0] = cx
		ys[0] = cy
		for (i in 0..steps) {
			val angle: Double = 2.0 * kotlin.math.PI * progress * i / steps
			xs[i + 1] = cx + kotlin.math.round(radius * kotlin.math.sin(angle)).toInt()
			ys[i + 1] = cy - kotlin.math.round(radius * kotlin.math.cos(angle)).toInt()
		}
		g.fillPolygon(xs, ys, steps + 2, colour)
		g.polygon(xs, ys, steps + 2, colour)
	}

	/** RuneLite's renderPolygon on the tile the pile is on, at its height: a two-pixel outline in the item's colour over a faint shadow. */
	private fun drawTile(g: Graphics, view: Viewport, client: ClientApi, item: GroundItems.Item) {
		val sceneX: Int = (item.worldX - client.sceneBaseX()) * 512
		val sceneY: Int = (item.worldY - client.sceneBaseY()) * 512
		val cornersX: IntArray = intArrayOf(sceneX, sceneX + 512, sceneX + 512, sceneX)
		val cornersY: IntArray = intArrayOf(sceneY, sceneY, sceneY + 512, sceneY + 512)
		for (i in 0 until 4) {
			if (!client.projectPoint(cornersX[i], cornersY[i], item.level, item.height, point)) {
				return
			}
			xs[i] = view.x + point[0]
			ys[i] = view.y + point[1]
		}
		g.fillPolygon(xs, ys, 4, TILE_FILL)
		for (i in 0 until 4) {
			val j: Int = (i + 1) % 4
			g.thickLine(xs[i], ys[i], xs[j], ys[j], 2, item.colour)
		}
	}

	// --- lootbeams ---

	/** RuneLite's handleLootbeam for every tile with items: the colour of its beam, or null for none. */
	@JvmStatic
	fun beamColour(tileItems: List<GroundItems.Item>): Int? {
		val plugin: GroundItems = GroundItems
		if (!plugin.showLootbeamForHighlighted && plugin.showLootbeamTier == 0) {
			return null
		}
		var highestPrice: Long = -1L
		var highest: GroundItems.Item? = null
		for (item in tileItems) {
			if (!GroundItems.shouldDisplay(item.ownership)) {
				continue
			}
			val verdict: Int = GroundItems.hiddenOrHighlighted(item)
			if (plugin.showLootbeamForHighlighted && verdict == GroundItems.VERDICT_HIGHLIGHTED) {
				return GroundItems.itemColour(item.itemId) ?: plugin.highlightedColour
			}
			if (verdict != GroundItems.VERDICT_HIDDEN) {
				val price: Long = GroundItems.valueByMode(item.geTotal(), item.haTotal())
				if (price > highestPrice) {
					highestPrice = price
					highest = item
				}
			}
		}
		if (highest == null || plugin.showLootbeamTier == 0) {
			return null
		}
		for (i in plugin.tierPrices.indices) {
			if (highestPrice > plugin.tierPrices[i] && highestPrice > GroundItems.tierPrice(plugin.showLootbeamTier)) {
				return GroundItems.itemColour(highest.itemId) ?: plugin.tierColours[i]
			}
		}
		return null
	}

	private fun drawBeams(g: Graphics, view: Viewport, client: ClientApi, player: Player) {
		val plugin: GroundItems = GroundItems
		if (!plugin.showLootbeamForHighlighted && plugin.showLootbeamTier == 0) {
			return
		}
		val tiles: LinkedHashMap<Long, ArrayList<GroundItems.Item>> = LinkedHashMap()
		for (item in plugin.items) {
			if (item.level == player.level()) {
				tiles.getOrPut(LootAttribution.tileKey(item.worldX, item.worldY, item.level)) { ArrayList() }.add(item)
			}
		}
		for (tileItems in tiles.values) {
			val colour: Int = beamColour(tileItems) ?: continue
			val first: GroundItems.Item = tileItems[0]
			drawBeam(g, view, client, first, colour)
		}
	}

	/** A beam on the pile's tile: a column of [argb] from the pile up, fading as it rises; Light is a soft glow, Modern a narrower beam with a bright core. */
	private fun drawBeam(g: Graphics, view: Viewport, client: ClientApi, item: GroundItems.Item, argb: Int) {
		val sceneX: Int = (item.worldX - client.sceneBaseX()) * 512 + 256
		val sceneY: Int = (item.worldY - client.sceneBaseY()) * 512 + 256
		if (!client.projectPoint(sceneX, sceneY, item.level, item.height, point)) {
			return
		}
		val bottomX: Int = view.x + point[0]
		val bottomY: Int = view.y + point[1]
		// the beam's width on the screen: an eighth of a tile each side of the pile, as far as it lies
		if (!client.projectPoint(sceneX + 64, sceneY, item.level, item.height, point2)) {
			return
		}
		var half: Int = kotlin.math.abs(view.x + point2[0] - bottomX)
		if (client.projectPoint(sceneX, sceneY + 64, item.level, item.height, point2)) {
			half = maxOf(half, kotlin.math.abs(view.x + point2[0] - bottomX))
		}
		half = maxOf(2, half)
		if (!client.projectPoint(sceneX, sceneY, item.level, item.height + BEAM_HEIGHT, point)) {
			return
		}
		val topX: Int = view.x + point[0]
		val topY: Int = view.y + point[1]
		val rgb: Int = argb and 0xFFFFFF
		if (GroundItems.lootbeamStyle == GroundItems.BEAM_LIGHT) {
			column(g, bottomX, bottomY, topX, topY, half * 3 / 2, half, rgb, 110)
			column(g, bottomX, bottomY, topX, topY, maxOf(1, half / 3), maxOf(1, half / 4), lighter(rgb), 200)
		} else {
			column(g, bottomX, bottomY, topX, topY, half, half * 2 / 3, rgb, 170)
			column(g, bottomX, bottomY, topX, topY, maxOf(1, half / 3), maxOf(1, half / 5), lighter(rgb), 230)
		}
		beamsLastFrame++
	}

	/** A tapering column from the bottom ([bottomHalf] wide each side) to the top ([topHalf]), its alpha falling from [alpha] to nothing. */
	private fun column(g: Graphics, bx: Int, by: Int, tx: Int, ty: Int, bottomHalf: Int, topHalf: Int, rgb: Int, alpha: Int) {
		for (s in 0 until BEAM_SEGMENTS) {
			val f0: Float = s.toFloat() / BEAM_SEGMENTS
			val f1: Float = (s + 1).toFloat() / BEAM_SEGMENTS
			val x0: Int = bx + ((tx - bx) * f0).toInt()
			val y0: Int = by + ((ty - by) * f0).toInt()
			val x1: Int = bx + ((tx - bx) * f1).toInt()
			val y1: Int = by + ((ty - by) * f1).toInt()
			val h0: Int = bottomHalf + ((topHalf - bottomHalf) * f0).toInt()
			val h1: Int = bottomHalf + ((topHalf - bottomHalf) * f1).toInt()
			xs[0] = x0 - h0
			ys[0] = y0
			xs[1] = x0 + h0
			ys[1] = y0
			xs[2] = x1 + h1
			ys[2] = y1
			xs[3] = x1 - h1
			ys[3] = y1
			val a: Int = (alpha * (1f - f0)).toInt().coerceIn(0, 255)
			g.fillPolygon(xs, ys, 4, (a shl 24) or rgb)
		}
	}

	private fun lighter(rgb: Int): Int {
		val r: Int = minOf(255, ((rgb shr 16) and 0xFF) + 96)
		val gg: Int = minOf(255, ((rgb shr 8) and 0xFF) + 96)
		val b: Int = minOf(255, (rgb and 0xFF) + 96)
		return (r shl 16) or (gg shl 8) or b
	}
}
