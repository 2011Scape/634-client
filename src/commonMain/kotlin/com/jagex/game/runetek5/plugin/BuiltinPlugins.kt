package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.AntiDrag
import com.jagex.game.runetek5.client.features.AttackStyles
import com.jagex.game.runetek5.client.features.BankOrganizer
import com.jagex.game.runetek5.client.features.BarrowsBrothers
import com.jagex.game.runetek5.client.features.Boosts
import com.jagex.game.runetek5.client.features.Cannon
import com.jagex.game.runetek5.client.features.DiscordPresence
import com.jagex.game.runetek5.client.features.Emojis
import com.jagex.game.runetek5.client.features.GroundItems
import com.jagex.game.runetek5.client.features.GroundTiles
import com.jagex.game.runetek5.client.features.IdleNotifier
import com.jagex.game.runetek5.client.features.InventoryValue
import com.jagex.game.runetek5.client.features.LootTracker
import com.jagex.game.runetek5.client.features.MenuEntrySwapper
import com.jagex.game.runetek5.client.features.NamePlates
import com.jagex.game.runetek5.client.features.NpcIndicators
import com.jagex.game.runetek5.client.features.ObjectIndicators
import com.jagex.game.runetek5.client.features.OpponentInfo
import com.jagex.game.runetek5.client.features.OverlaySettings
import com.jagex.game.runetek5.client.features.StatusBars
import com.jagex.game.runetek5.client.features.TileIndicators
import com.jagex.game.runetek5.client.features.TimersAndBuffs
import com.jagex.game.runetek5.client.features.XpDrops
import com.jagex.game.runetek5.client.features.XpGlobes
import com.jagex.game.runetek5.client.features.XpTracker
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: the plugins compiled into the client, on the same API as the
 * ones from the plugins folder. They run on every platform, the browser included, and are always
 * on (each has its own switch, as the experience drops have `::xpdrops`).
 */
object BuiltinPlugins {

	@JvmStatic
	fun all(): Array<PluginRegistration> {
		return arrayOf(
			builtIn(XpDrops.DESCRIPTOR, XpDrops),
			builtIn(IdleNotifier.DESCRIPTOR, IdleNotifier),
			builtIn(MenuEntrySwapper.DESCRIPTOR, MenuEntrySwapper),
			builtIn(InventoryValue.DESCRIPTOR, InventoryValue),
			builtIn(NamePlates.DESCRIPTOR, NamePlates),
			builtIn(GroundTiles.DESCRIPTOR, GroundTiles),
			builtIn(NpcIndicators.DESCRIPTOR, NpcIndicators),
			builtIn(DiscordPresence.DESCRIPTOR, DiscordPresence),
			builtIn(GroundItems.DESCRIPTOR, GroundItems),
			builtIn(LootTracker.DESCRIPTOR, LootTracker),
			builtIn(ObjectIndicators.DESCRIPTOR, ObjectIndicators),
			builtIn(XpTracker.DESCRIPTOR, XpTracker),
			builtIn(OpponentInfo.DESCRIPTOR, OpponentInfo),
			builtIn(Boosts.DESCRIPTOR, Boosts),
			builtIn(OverlaySettings.DESCRIPTOR, OverlaySettings),
			builtIn(BankOrganizer.DESCRIPTOR, BankOrganizer),
			builtIn(StatusBars.DESCRIPTOR, StatusBars),
			builtIn(TimersAndBuffs.DESCRIPTOR, TimersAndBuffs),
			builtIn(TileIndicators.DESCRIPTOR, TileIndicators),
			builtIn(XpGlobes.DESCRIPTOR, XpGlobes),
			builtIn(BarrowsBrothers.DESCRIPTOR, BarrowsBrothers),
			builtIn(AttackStyles.DESCRIPTOR, AttackStyles),
			builtIn(Cannon.DESCRIPTOR, Cannon),
			builtIn(AntiDrag.DESCRIPTOR, AntiDrag),
			builtIn(Emojis.DESCRIPTOR, Emojis),
		)
	}

	/** The registration of a built-in, with its icon from the client's own resources (`/plugins/<id>.png`) when there is one. */
	private fun builtIn(descriptor: PluginDescriptor, plugin: Plugin): PluginRegistration {
		val registration: PluginRegistration = PluginRegistration(descriptor, plugin, true, "built in")
		registration.icon = PluginIcon.decode(lang.readResource("/plugins/" + descriptor.id + ".png"))
		return registration
	}
}
