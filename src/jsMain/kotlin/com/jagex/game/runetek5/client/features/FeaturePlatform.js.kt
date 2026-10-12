package com.jagex.game.runetek5.client.features

import awt.Graphics

/* Not part of the original client: the browser side of FeaturePlatform (milestone 4). */

/* A page has no file system to read the packet size file from; the sizes stay the client's own. */
actual fun readPacketSizes(file: String, sizes: IntArray) {
	lang.printlnErr("client.packets: cannot read " + file + " in a browser")
}

/* The canvas graphics already draw at the page's scale (awt.UiScale); client.scale is milestone 6. */
actual fun scaleGraphics2D(graphics: Graphics?, scale: Double, whole: Boolean): Graphics? = graphics
