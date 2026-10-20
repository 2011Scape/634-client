package com.jagex.game.runetek5.plugin.api

/** Receives the locations on a tile from [ClientApi.locsAt]. */
fun interface LocVisitor {
	/**
	 * One location: its type [id] ([ClientApi.locType]), what it is ([kind], a [LocKind]), its
	 * [rotation] 0 to 3, and its [shape] 0 to 22 (10 is a plain object; the rest are the wall and
	 * roof shapes).
	 */
	fun visit(id: Int, kind: Int, rotation: Int, shape: Int)
}
