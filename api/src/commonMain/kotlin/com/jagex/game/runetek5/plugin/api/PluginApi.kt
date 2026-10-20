package com.jagex.game.runetek5.plugin.api

/**
 * The version of this API. A plugin's jar names the version it was built against
 * (`Plugin-Api-Version` in its manifest); the client refuses a plugin built against a newer one.
 *
 * The number goes up with every release that adds to the API, so that a plugin using the new parts
 * is refused by an older client with a clear message instead of failing inside. Nothing is ever
 * removed or changed in a way that breaks a plugin built against a lower version: a plugin built
 * against version N runs on every client whose API is version N or higher.
 *
 * - 1: the first release (client 2.13.0).
 * - 2: actor state (animation, graphic, interaction, facing, overhead text, hit splats, icons),
 *   item, npc and location definitions, the locations and movement flags of a tile, and the
 *   events from [events.Events.NPC_SPAWNED] on.
 */
object PluginApi {
	const val VERSION: Int = 2
}
