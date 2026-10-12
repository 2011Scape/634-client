package com.jagex.game.runetek5.client.features

import awt.Graphics

/*
 * Not part of the original client: the parts of ClientFeatures that need the platform (milestone 3).
 */

/**
 * Reads the packet size overrides in the properties file `file` into `sizes`, one `<opcode>=<size>`
 * per line; on an error, says so and keeps the sizes read so far.
 */
expect fun readPacketSizes(file: String, sizes: IntArray)

/** `graphics` scaled by `scale`, with nearest-neighbour sampling when the scale is `whole`. */
expect fun scaleGraphics2D(graphics: Graphics?, scale: Double, whole: Boolean): Graphics?
