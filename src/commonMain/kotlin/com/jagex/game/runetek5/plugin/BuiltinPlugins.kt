package com.jagex.game.runetek5.plugin

import com.jagex.game.runetek5.client.features.Agility
import com.jagex.game.runetek5.client.features.AnimationSmoothing
import com.jagex.game.runetek5.client.features.AntiDrag
import com.jagex.game.runetek5.client.features.AttackStyles
import com.jagex.game.runetek5.client.features.BankOrganizer
import com.jagex.game.runetek5.client.features.BarrowsBrothers
import com.jagex.game.runetek5.client.features.Boosts
import com.jagex.game.runetek5.client.features.Cannon
import com.jagex.game.runetek5.client.features.ChatFilter
import com.jagex.game.runetek5.client.features.ChatHistory
import com.jagex.game.runetek5.client.features.ChatNotifications
import com.jagex.game.runetek5.client.features.CombatLevel
import com.jagex.game.runetek5.client.features.Cooking
import com.jagex.game.runetek5.client.features.CustomCursor
import com.jagex.game.runetek5.client.features.DeathIndicator
import com.jagex.game.runetek5.client.features.DiscordPresence
import com.jagex.game.runetek5.client.features.Emojis
import com.jagex.game.runetek5.client.features.EntityHider
import com.jagex.game.runetek5.client.features.Examine
import com.jagex.game.runetek5.client.features.Fishing
import com.jagex.game.runetek5.client.features.FriendNotes
import com.jagex.game.runetek5.client.features.GrandExchange
import com.jagex.game.runetek5.client.features.GroundItems
import com.jagex.game.runetek5.client.features.GroundTiles
import com.jagex.game.runetek5.client.features.Hiscore
import com.jagex.game.runetek5.client.features.Hunter
import com.jagex.game.runetek5.client.features.IdleNotifier
import com.jagex.game.runetek5.client.features.Implings
import com.jagex.game.runetek5.client.features.InteractHighlight
import com.jagex.game.runetek5.client.features.InventoryGrid
import com.jagex.game.runetek5.client.features.InventoryTags
import com.jagex.game.runetek5.client.features.InventoryValue
import com.jagex.game.runetek5.client.features.ItemCharges
import com.jagex.game.runetek5.client.features.ItemIdentification
import com.jagex.game.runetek5.client.features.ItemStats
import com.jagex.game.runetek5.client.features.KeyRemapping
import com.jagex.game.runetek5.client.features.LootTracker
import com.jagex.game.runetek5.client.features.MenuEntrySwapper
import com.jagex.game.runetek5.client.features.Metronome
import com.jagex.game.runetek5.client.features.Mining
import com.jagex.game.runetek5.client.features.MouseTooltips
import com.jagex.game.runetek5.client.features.NpcAggression
import com.jagex.game.runetek5.client.features.NpcIndicators
import com.jagex.game.runetek5.client.features.ObjectIndicators
import com.jagex.game.runetek5.client.features.OpponentInfo
import com.jagex.game.runetek5.client.features.OverlaySettings
import com.jagex.game.runetek5.client.features.PlayerIndicators
import com.jagex.game.runetek5.client.features.Poison
import com.jagex.game.runetek5.client.features.Prayer
import com.jagex.game.runetek5.client.features.RegenMeter
import com.jagex.game.runetek5.client.features.ReportButton
import com.jagex.game.runetek5.client.features.RunEnergy
import com.jagex.game.runetek5.client.features.ScreenMarkers
import com.jagex.game.runetek5.client.features.Screenshot
import com.jagex.game.runetek5.client.features.Slayer
import com.jagex.game.runetek5.client.features.Smelting
import com.jagex.game.runetek5.client.features.SpecialCounter
import com.jagex.game.runetek5.client.features.StatusBars
import com.jagex.game.runetek5.client.features.ShortestPath
import com.jagex.game.runetek5.client.features.TileIndicators
import com.jagex.game.runetek5.client.features.TimeTracking
import com.jagex.game.runetek5.client.features.TimersAndBuffs
import com.jagex.game.runetek5.client.features.VirtualLevels
import com.jagex.game.runetek5.client.features.Woodcutting
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
			builtIn(MouseTooltips.DESCRIPTOR, MouseTooltips),
			builtIn(KeyRemapping.DESCRIPTOR, KeyRemapping),
			builtIn(CustomCursor.DESCRIPTOR, CustomCursor),
			builtIn(ItemStats.DESCRIPTOR, ItemStats),
			builtIn(Woodcutting.DESCRIPTOR, Woodcutting),
			builtIn(Mining.DESCRIPTOR, Mining),
			builtIn(ReportButton.DESCRIPTOR, ReportButton),
			builtIn(PlayerIndicators.DESCRIPTOR, PlayerIndicators),
			builtIn(SpecialCounter.DESCRIPTOR, SpecialCounter),
			builtIn(RegenMeter.DESCRIPTOR, RegenMeter),
			builtIn(RunEnergy.DESCRIPTOR, RunEnergy),
			builtIn(Poison.DESCRIPTOR, Poison),
			builtIn(Fishing.DESCRIPTOR, Fishing),
			builtIn(Agility.DESCRIPTOR, Agility),
			builtIn(Slayer.DESCRIPTOR, Slayer),
			builtIn(ItemCharges.DESCRIPTOR, ItemCharges),
			builtIn(ItemIdentification.DESCRIPTOR, ItemIdentification),
			builtIn(ChatNotifications.DESCRIPTOR, ChatNotifications),
			builtIn(ChatFilter.DESCRIPTOR, ChatFilter),
			builtIn(ChatHistory.DESCRIPTOR, ChatHistory),
			builtIn(CombatLevel.DESCRIPTOR, CombatLevel),
			builtIn(VirtualLevels.DESCRIPTOR, VirtualLevels),
			builtIn(Metronome.DESCRIPTOR, Metronome),
			builtIn(DeathIndicator.DESCRIPTOR, DeathIndicator),
			builtIn(NpcAggression.DESCRIPTOR, NpcAggression),
			builtIn(Hunter.DESCRIPTOR, Hunter),
			builtIn(Implings.DESCRIPTOR, Implings),
			builtIn(Cooking.DESCRIPTOR, Cooking),
			builtIn(Smelting.DESCRIPTOR, Smelting),
			builtIn(Prayer.DESCRIPTOR, Prayer),
			builtIn(InventoryTags.DESCRIPTOR, InventoryTags),
			builtIn(InventoryGrid.DESCRIPTOR, InventoryGrid),
			builtIn(Examine.DESCRIPTOR, Examine),
			builtIn(EntityHider.DESCRIPTOR, EntityHider),
			builtIn(AnimationSmoothing.DESCRIPTOR, AnimationSmoothing),
			builtIn(Screenshot.DESCRIPTOR, Screenshot),
			builtIn(GrandExchange.DESCRIPTOR, GrandExchange),
			builtIn(TimeTracking.DESCRIPTOR, TimeTracking),
			builtIn(ScreenMarkers.DESCRIPTOR, ScreenMarkers),
			builtIn(Hiscore.DESCRIPTOR, Hiscore),
			builtIn(FriendNotes.DESCRIPTOR, FriendNotes),
			builtIn(InteractHighlight.DESCRIPTOR, InteractHighlight),
			builtIn(ShortestPath.DESCRIPTOR, ShortestPath),
		)
	}

	/** The registration of a built-in, with its icon from the client's own resources (`/plugins/<id>.png`) when there is one. */
	private fun builtIn(descriptor: PluginDescriptor, plugin: Plugin): PluginRegistration {
		val registration: PluginRegistration = PluginRegistration(descriptor, plugin, true, "built in")
		registration.icon = PluginIcon.decode(lang.readResource("/plugins/" + descriptor.id + ".png"))
		return registration
	}
}
