package com.jagex.game.runetek5.client.features

import awt.image.BufferedImage
import io.File

/* Not part of the original client: SmokeTest's image files (milestone 3). */

/** Saves `image` as a PNG file; throws when it cannot. */
expect fun writePng(image: BufferedImage, file: File)

/** The image in `file`, or null when it is not one; throws when it cannot read it. */
expect fun readImage(file: File): BufferedImage?
