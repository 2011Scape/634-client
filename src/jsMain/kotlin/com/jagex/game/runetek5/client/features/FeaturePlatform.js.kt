package com.jagex.game.runetek5.client.features

import awt.Graphics

/* Not part of the original client: the browser side of FeaturePlatform (milestone 4). */

/* A page has no file system to read the packet size file from; the sizes stay the client's own. */
actual fun readPacketSizes(file: String, sizes: IntArray) {
	lang.printlnErr("client.packets: cannot read " + file + " in a browser")
}

/* The canvas graphics drawn at client.scale: whole scales nearest-neighbour, others filtered, as on the desktop. */
actual fun scaleGraphics2D(graphics: Graphics?, scale: Double, whole: Boolean): Graphics? =
    (graphics as? awt.CanvasGraphics)?.scaledBy(scale, !whole) ?: graphics
