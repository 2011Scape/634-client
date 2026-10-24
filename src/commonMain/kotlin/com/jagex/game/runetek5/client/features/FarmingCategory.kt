package com.jagex.game.runetek5.client.features

/** Not part of the original client: TimeTracking's kinds of farming patch, each with its infobox's item icon. */
enum class FarmingCategory(val title: String, val itemId: Int) {
	HERB("Herbs", 249),
	ALLOTMENT("Allotments", 1942),
	FLOWER("Flowers", 6010),
	HOPS("Hops", 5994),
	BUSH("Bushes", 1951),
	TREE("Trees", 1521),
	FRUIT_TREE("Fruit trees", 1955),
	SPIRIT_TREE("Spirit trees", 5317),
	CACTUS("Cactus", 6016),
	MUSHROOM("Mushrooms", 6004),
	BELLADONNA("Belladonna", 2398),
	CALQUAT("Calquat", 5980),
	SPECIAL("Special", 12134),
}
