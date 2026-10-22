package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.plugin.api.Graphics
import com.jagex.game.runetek5.plugin.api.Image
import com.jagex.game.runetek5.plugin.api.InfoBox
import com.jagex.game.runetek5.plugin.api.InfoBoxFilter
import com.jagex.game.runetek5.plugin.api.OverlayMenuEntry
import com.jagex.game.runetek5.plugin.api.OverlayPanel
import com.jagex.game.runetek5.plugin.api.PanelChild
import com.jagex.game.runetek5.plugin.api.PanelComponent
import com.jagex.game.runetek5.plugin.api.PanelOverlay
import com.jagex.game.runetek5.plugin.api.Size
import com.jagex.game.runetek5.plugin.api.Task
import com.jagex.game.runetek5.plugin.api.Viewport
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's InfoBoxManager. Every plugin's infoboxes, in groups
 * ("layers"), each a panel of its own ([Layer], RuneLite's InfoBoxOverlay) at the top left of the game
 * view unless the player moved it: the default group, and one per box the player detached (with the other
 * boxes of its name). A box's group and a group's direction are kept in the layout's file under
 * RuneLite's keys (`infoboxlayer.<name>`, `infoboxoverlay.orient_<group>`).
 */
@Suppress("TooGenericExceptionCaught") // a box that throws disables its plugin and the game goes on
object InfoBoxes {

	const val DEFAULT_LAYER: String = "InfoBoxOverlay"

	private const val LAYER_KEY: String = "infoboxlayer."
	private const val ORIENTATION_KEY: String = "infoboxoverlay.orient_"
	private const val HORIZONTAL: String = "HORIZONTAL"
	private const val VERTICAL: String = "VERTICAL"

	private const val DETACH: String = "Detach"
	private const val FLIP: String = "Flip"
	private const val DELETE: String = "Delete"

	/** A box, whose it is and the group it is in. */
	class Box(@JvmField val box: InfoBox, @JvmField val owner: PluginRegistration?, @JvmField var layer: String)

	@JvmField
	val layers: LinkedHashMap<String, Layer> = LinkedHashMap()

	@JvmStatic
	fun add(owner: PluginRegistration?, box: InfoBox) {
		for (layer in layers.values) {
			for (b in layer.boxes) {
				if (b.box === box) {
					return
				}
			}
		}
		val name: String = OverlayLayout.config().get(LAYER_KEY + box.name, "").ifEmpty { DEFAULT_LAYER }
		val layer: Layer = layer(name)
		val entry: Box = Box(box, owner, name)
		// RuneLite's findInsertionIndex: by priority, then by plugin, after the boxes that tie
		var at: Int = layer.boxes.size
		for (i in layer.boxes.indices) {
			if (compare(entry, layer.boxes[i]) < 0) {
				at = i
				break
			}
		}
		layer.boxes.add(at, entry)
	}

	private fun compare(a: Box, b: Box): Int {
		val byPriority: Int = a.box.priority.ordinal - b.box.priority.ordinal
		if (byPriority != 0) {
			return byPriority
		}
		return pluginName(a).compareTo(pluginName(b))
	}

	private fun pluginName(box: Box): String = box.owner?.descriptor?.name ?: ""

	@JvmStatic
	fun remove(box: InfoBox) {
		for (layer in layers.values) {
			layer.boxes.removeAll { it.box === box }
		}
	}

	@JvmStatic
	fun removeIf(owner: PluginRegistration?, filter: InfoBoxFilter) {
		for (layer in layers.values) {
			layer.boxes.removeAll { it.owner === owner && filter.test(it.box) }
		}
	}

	/** Every box of [owner]'s, when it stops. */
	@JvmStatic
	fun removeAll(owner: PluginRegistration) {
		for (layer in layers.values) {
			layer.boxes.removeAll { it.owner === owner }
		}
	}

	/** RuneLite's cull, each frame: the boxes finished with go. */
	@JvmStatic
	fun cull() {
		for (layer in layers.values) {
			var i: Int = 0
			while (i < layer.boxes.size) {
				val b: Box = layer.boxes[i]
				var gone: Boolean = false
				try {
					gone = b.box.cull()
				} catch (error: Throwable) {
					PluginRuntime.fail(b.owner, error)
				}
				if (gone) {
					layer.boxes.removeAt(i)
				} else {
					i++
				}
			}
		}
	}

	/** The group [name], made (and given its panel) when it is new. */
	private fun layer(name: String): Layer {
		val existing: Layer? = layers[name]
		if (existing != null) {
			return existing
		}
		val stored: String = OverlayLayout.config().get(ORIENTATION_KEY + name, "")
		val vertical: Boolean = stored == VERTICAL
		val made: Layer = Layer(name, vertical)
		layers[name] = made
		OverlayLayout.add(null, made)
		return made
	}

	private fun removeLayer(layer: Layer) {
		OverlayLayout.config().remove(ORIENTATION_KEY + layer.name)
		OverlayLayout.remove(layer)
		layers.remove(layer.name)
	}

	private fun setLayer(box: InfoBox, layer: String) {
		if (layer == DEFAULT_LAYER) {
			OverlayLayout.config().remove(LAYER_KEY + box.name)
		} else {
			OverlayLayout.config().set(LAYER_KEY + box.name, layer)
		}
	}

	/** RuneLite's splitInfobox: [box] and the others of its name into a group of their own. */
	private fun detach(box: Box) {
		val old: Layer = layers[box.layer] ?: return
		val moving: List<Box> = old.boxes.filter { it.box.name == box.box.name }
		old.boxes.removeAll(moving)
		if (old.boxes.isEmpty() && old.name != DEFAULT_LAYER) {
			removeLayer(old)
		}
		val name: String = box.box.name + "_" + lang.currentTimeMillis()
		val made: Layer = layer(name)
		for (b in moving) {
			setLayer(b.box, name)
			b.layer = name
			made.boxes.add(b)
		}
		OverlayLayout.config().save()
	}

	/** RuneLite's mergeInfoBoxes: [source]'s boxes into [dest], and [source] gone. */
	@JvmStatic
	fun merge(source: Layer, dest: Layer) {
		for (b in source.boxes) {
			setLayer(b.box, dest.name)
			b.layer = dest.name
		}
		dest.boxes.addAll(source.boxes)
		source.boxes.clear()
		removeLayer(source)
		OverlayLayout.config().save()
	}

	private fun flip(box: Box) {
		val layer: Layer = layers[box.layer] ?: return
		layer.vertical = !layer.vertical
		OverlayLayout.config().set(ORIENTATION_KEY + layer.name, if (layer.vertical) VERTICAL else HORIZONTAL)
		OverlayLayout.config().save()
	}

	private fun delete(box: Box) {
		val source: Layer = layers[box.layer] ?: return
		val dest: Layer = layer(DEFAULT_LAYER)
		if (source !== dest) {
			merge(source, dest)
		}
	}

	/** RuneLite's infobox scaling: shrunk (never grown) to fit [size] square, its shape kept; width and height in [out]. */
	@JvmStatic
	fun fit(width: Int, height: Int, size: Int, out: IntArray) {
		val limit: Int = maxOf(2, size)
		if (limit < width || limit < height) {
			val scale: Double = minOf(limit.toDouble() / width, limit.toDouble() / height)
			out[0] = (width * scale).toInt()
			out[1] = (height * scale).toInt()
		} else {
			out[0] = width
			out[1] = height
		}
	}

	/** RuneLite's InfoBoxOverlay: one group's boxes, wrapping after four, across or down. */
	class Layer(name: String, @JvmField var vertical: Boolean) : OverlayPanel(name) {
		@JvmField
		val boxes: ArrayList<Box> = ArrayList()

		/** The boxes drawn last frame and where (x, y from the panel's corner), for the hover and the smoke test. */
		@JvmField
		val drawn: ArrayList<Cell> = ArrayList()

		private var hoveredBox: Box? = null

		private val menu: ArrayList<OverlayMenuEntry> = ArrayList()

		init {
			dragTargetable = true
			clearChildren = false
			panelComponent.wrap = true
			panelComponent.background = 0
			panelComponent.borderLeft = 0
			panelComponent.borderTop = 0
			panelComponent.borderRight = 0
			panelComponent.borderBottom = 0
			panelComponent.gapX = GAP
			panelComponent.gapY = GAP
		}

		override fun render(g: Graphics, view: Viewport): Size? {
			val menuOpen: Boolean = MiniMenu.isOpen
			if (!menuOpen) {
				hoveredBox = null
			}
			drawn.clear()
			if (boxes.isEmpty()) {
				return null
			}
			val size: Int = OverlayLayout.infoBoxSize
			panelComponent.preferredWidth = DEFAULT_WRAP_COUNT * (size + GAP)
			panelComponent.preferredHeight = DEFAULT_WRAP_COUNT * (size + GAP)
			panelComponent.orientation = if (vertical) PanelComponent.VERTICAL else PanelComponent.HORIZONTAL
			for (b in boxes) {
				var shown: Boolean = false
				try {
					shown = b.box.render()
				} catch (error: Throwable) {
					PluginRuntime.fail(b.owner, error)
				}
				if (shown) {
					val cell: Cell = Cell(b, size)
					drawn.add(cell)
					panelComponent.children.add(cell)
				}
			}
			val drawnSize: Size? = super.render(g, view)
			val mouseX: Int = OverlayLayout.mouseX() - boundsX
			val mouseY: Int = OverlayLayout.mouseY() - boundsY
			for (cell in drawn) {
				if (mouseX >= cell.x && mouseY >= cell.y && mouseX < cell.x + cell.size && mouseY < cell.y + cell.size) {
					val tooltip: String? = cell.tooltip
					if (!tooltip.isNullOrEmpty()) {
						Tooltips.add(tooltip)
					}
					if (!menuOpen) {
						hoveredBox = cell.box
					}
					break
				}
			}
			panelComponent.children.clear()
			return drawnSize
		}

		/** The hovered box's entries, then Detach, Flip and (in a group of its own) Delete, as RuneLite's InfoBoxManager adds them. */
		override fun menuEntries(): List<OverlayMenuEntry> {
			menu.clear()
			val b: Box = hoveredBox ?: return menu
			try {
				menu.addAll(b.box.menuEntries())
			} catch (error: Throwable) {
				PluginRuntime.fail(b.owner, error)
			}
			menu.add(OverlayMenuEntry(DETACH, "InfoBox", Task { detach(b) }))
			menu.add(OverlayMenuEntry(FLIP, "InfoBox Group", Task { flip(b) }))
			if (b.layer != DEFAULT_LAYER) {
				menu.add(OverlayMenuEntry(DELETE, "InfoBox Group", Task { delete(b) }))
			}
			return menu
		}

		override fun onDrag(source: PanelOverlay): Boolean {
			if (source !is Layer) {
				return false
			}
			merge(source, this)
			return true
		}
	}

	/** RuneLite's InfoBoxComponent: the box's background, its picture centred, its text along the bottom. */
	class Cell(@JvmField val box: Box, @JvmField val size: Int) : PanelChild {
		@JvmField var x: Int = 0
		@JvmField var y: Int = 0
		@JvmField var text: String? = null
		@JvmField var tooltip: String? = null

		private val fitted: IntArray = IntArray(2)

		override fun render(g: Graphics, x: Int, y: Int, preferredWidth: Int, preferredHeight: Int, out: Size) {
			val infoBox: InfoBox = box.box
			val image: Image? = infoBox.image
			val spriteId: Int = infoBox.spriteId
			var imageWidth: Int = 0
			var imageHeight: Int = 0
			if (image != null) {
				imageWidth = image.width()
				imageHeight = image.height()
			} else if (spriteId >= 0) {
				imageWidth = g.spriteWidth(spriteId)
				imageHeight = g.spriteHeight(spriteId)
			}
			if (imageWidth <= 0 || imageHeight <= 0) {
				out.set(0, 0)
				return
			}
			this.x = x
			this.y = y
			var text: String? = null
			var colour: Int = WHITE
			try {
				text = infoBox.text()
				colour = infoBox.textColour()
				tooltip = infoBox.tooltip()
			} catch (error: Throwable) {
				PluginRuntime.fail(box.owner, error)
			}
			this.text = text
			g.font(if (size < DEFAULT_SIZE) com.jagex.game.runetek5.plugin.api.Font.SMALL else OverlayLayout.infoboxFont)
			PanelComponent.background(g, x, y, size, size, OverlayLayout.background)
			fit(imageWidth, imageHeight, size, fitted)
			val ix: Int = x + (size - fitted[0]) / 2
			val iy: Int = y + (size - fitted[1]) / 2
			if (image != null) {
				if (fitted[0] == imageWidth && fitted[1] == imageHeight) {
					g.image(image, ix, iy)
				} else {
					g.imageScaled(image, ix, iy, fitted[0], fitted[1])
				}
			} else if (fitted[0] == imageWidth && fitted[1] == imageHeight) {
				g.sprite(spriteId, ix, iy)
			} else {
				g.spriteScaled(spriteId, ix, iy, fitted[0], fitted[1])
			}
			if (!text.isNullOrEmpty()) {
				val tx: Int = x + (size - g.textWidth(text)) / 2
				val ty: Int = y + size - SEPARATOR
				if (OverlayLayout.infoBoxTextOutline) {
					outlined(g, tx, ty, text, colour)
				} else {
					g.text(tx, ty, text, colour, PanelComponent.SHADOW)
				}
			}
			out.set(size, size)
		}
	}

	/** RuneLite's TextComponent outline: the text in black a pixel each way, then in its colour. */
	@JvmStatic
	fun outlined(g: Graphics, x: Int, y: Int, text: String, colour: Int) {
		g.text(x, y + 1, text, BLACK, 0)
		g.text(x, y - 1, text, BLACK, 0)
		g.text(x + 1, y, text, BLACK, 0)
		g.text(x - 1, y, text, BLACK, 0)
		g.text(x, y, text, colour, 0)
	}

	private const val GAP: Int = 1
	private const val DEFAULT_WRAP_COUNT: Int = 4
	private const val DEFAULT_SIZE: Int = 32
	private const val SEPARATOR: Int = 3
	private const val WHITE: Int = -0x1
	private const val BLACK: Int = -0x1000000
}
