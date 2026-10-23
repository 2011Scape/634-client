package com.jagex.game.runetek5.plugin

/*
 * Not part of the original client: a plugin's picture saved or copied (ClientApi.saveImage and copyImage,
 * API 33), [pixels] ARGB rows of [width].
 */

/**
 * Saves the picture as a PNG named [name] in [folder]: on the desktop under the player's Pictures,
 * 634-client/[folder]; in a browser as a download. Where it went, or null when it could not be saved.
 */
expect fun saveImagePng(width: Int, height: Int, pixels: IntArray, folder: String, name: String): String?

/** Puts the picture on the system clipboard; false where that cannot be done. */
expect fun copyImageToClipboard(width: Int, height: Int, pixels: IntArray): Boolean
