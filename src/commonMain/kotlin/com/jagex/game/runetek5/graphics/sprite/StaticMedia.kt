package com.jagex.game.runetek5.graphics.sprite

import com.jagex.game.runetek5.plugin.PluginChatIcons
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.FriendChat
import com.jagex.game.runetek5.client.menu.MiniMenu
import com.jagex.game.runetek5.core.util.Arrays
import com.jagex.game.runetek5.graphics.SpriteDataProvider
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.rendering.Renderers
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.font.FontManager
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.MapRegion
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object StaticMedia {
	@OriginalMember(owner = "client!fi", name = "N", descriptor = "I")
	@JvmField
	var nameIconsGroupid: Int = 0

	@OriginalMember(owner = "client!ec", name = "i", descriptor = "[Lclient!mi;")
	@JvmField
	var chatIcons: Array<NativeSprite?>? = null

	@OriginalMember(owner = "client!mv", name = "f", descriptor = "I")
	@JvmField
	var hintHeadiconsGroupid: Int = 0

	@OriginalMember(owner = "client!pq", name = "u", descriptor = "[I")
	@JvmField
	var chatScaleHeights: IntArray? = null

	@OriginalMember(owner = "client!bf", name = "d", descriptor = "I")
	@JvmField
	var mapdotsGroupid: Int = 0

	@OriginalMember(owner = "client!vw", name = "u", descriptor = "I")
	@JvmField
	var hintMapmarkersGroupid: Int = 0

	@OriginalMember(owner = "client!lea", name = "e", descriptor = "I")
	@JvmField
	var mapflagGroupid: Int = 0

	@OriginalMember(owner = "client!aj", name = "x", descriptor = "I")
	@JvmField
	var totalChatIcons: Int = 0

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "I")
	@JvmField
	var headiconsPkGroupid: Int = 0

	@OriginalMember(owner = "client!bf", name = "f", descriptor = "[Lclient!mi;")
	@JvmField
	var nameIconSprites: Array<NativeSprite?>? = null

	@OriginalMember(owner = "client!bd", name = "g", descriptor = "I")
	@JvmField
	var timerbarDefaultGroupid: Int = 0

	@OriginalMember(owner = "client!lo", name = "t", descriptor = "I")
	@JvmField
	var hitmarksGroupid: Int = 0

	@OriginalMember(owner = "client!mia", name = "i", descriptor = "I")
	@JvmField
	var hitbarDefaultGroupid: Int = 0

	@OriginalMember(owner = "client!fh", name = "d", descriptor = "I")
	@JvmField
	var scrollbarGroupid: Int = 0

	@OriginalMember(owner = "client!lba", name = "b", descriptor = "I")
	@JvmField
	var headiconsPrayerGroupid: Int = 0

	@OriginalMember(owner = "client!qga", name = "m", descriptor = "I")
	@JvmField
	var floorShadowsGroupid: Int = 0
	@OriginalMember(owner = "client!rca", name = "c", descriptor = "[Lclient!mi;")
	@JvmField
	var hintMapedgeSprites: Array<NativeSprite?>? = null

	@OriginalMember(owner = "client!tq", name = "t", descriptor = "I")
	@JvmField
	var hintMapedgeGroupid: Int = 0
	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(Lclient!in;Lclient!ha;Z)V")
	@JvmStatic
	fun loadSprites(@OriginalArg(0) spriteResource: Js5ResourceProvider?, @OriginalArg(1) toolkit: Toolkit?) {
		@Pc(10) var indexedImages: Array<SpriteType?>? = SpriteType.decodeGroup(spriteResource, StaticMedia.hitmarksGroupid)
		StaticMedia.hitmarkSprites = arrayOfNulls<NativeSprite>(indexedImages!!.size)
		for (@Pc(16) i in 0 until indexedImages.size) {
			StaticMedia.hitmarkSprites!![i] = toolkit!!.generateSprite(indexedImages[i], true)
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.hitbarDefaultGroupid)
		StaticMedia.hitbarDefaultSprites = arrayOfNulls<NativeSprite>(indexedImages!!.size)
		for (@Pc(43) j in 0 until indexedImages.size) {
			StaticMedia.hitbarDefaultSprites!![j] = toolkit!!.generateSprite(indexedImages[j], true)
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.timerbarDefaultGroupid)
		StaticMedia.timerbarDefaultSprites = arrayOfNulls<NativeSprite>(indexedImages!!.size)
		for (@Pc(74) k in 0 until indexedImages.size) {
			StaticMedia.timerbarDefaultSprites!![k] = toolkit!!.generateSprite(indexedImages[k], true)
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.headiconsPkGroupid)
		StaticMedia.headiconPkSprites = arrayOfNulls<NativeSprite>(indexedImages!!.size)
		for (@Pc(101) m in 0 until indexedImages.size) {
			StaticMedia.headiconPkSprites!![m] = toolkit!!.generateSprite(indexedImages[m], true)
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.headiconsPrayerGroupid)
		StaticMedia.headiconPrayerSprites = arrayOfNulls<NativeSprite>(indexedImages!!.size)
		for (@Pc(128) n in 0 until indexedImages.size) {
			StaticMedia.headiconPrayerSprites!![n] = toolkit!!.generateSprite(indexedImages[n], true)
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.hintHeadiconsGroupid)
		StaticMedia.hintHeadiconSprites = arrayOfNulls<NativeSprite>(indexedImages!!.size)
		for (@Pc(155) p in 0 until indexedImages.size) {
			StaticMedia.hintHeadiconSprites!![p] = toolkit!!.generateSprite(indexedImages[p], true)
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.hintMapmarkersGroupid)
		StaticMedia.hintMapmarkerSprites = arrayOfNulls<NativeSprite>(indexedImages!!.size)
		for (@Pc(186) q in 0 until indexedImages.size) {
			StaticMedia.hintMapmarkerSprites!![q] = toolkit!!.generateSprite(indexedImages[q], true)
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.mapflagGroupid)
		StaticMedia.mapflagSprites = arrayOfNulls<NativeSprite>(indexedImages!!.size)
		for (@Pc(213) r in 0 until indexedImages.size) {
			StaticMedia.mapflagSprites!![r] = toolkit!!.generateSprite(indexedImages[r], true)
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.crossGroupid)
		StaticMedia.crossSprites = arrayOfNulls<NativeSprite>(indexedImages!!.size)
		for (@Pc(240) s in 0 until indexedImages.size) {
			StaticMedia.crossSprites!![s] = toolkit!!.generateSprite(indexedImages[s], true)
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.mapdotsGroupid)
		StaticMedia.mapdotSprites = arrayOfNulls<NativeSprite>(indexedImages!!.size)
		for (@Pc(271) t in 0 until indexedImages.size) {
			StaticMedia.mapdotSprites!![t] = toolkit!!.generateSprite(indexedImages[t], true)
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.scrollbarGroupid)
		StaticMedia.scrollbarSprites = arrayOfNulls<NativeSprite>(indexedImages!!.size)
		for (@Pc(302) u in 0 until indexedImages.size) {
			StaticMedia.scrollbarSprites!![u] = toolkit!!.generateSprite(indexedImages[u], true)
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.nameIconsGroupid)
		StaticMedia.nameIconSprites = arrayOfNulls<NativeSprite>(indexedImages!!.size)
		for (@Pc(329) v in 0 until indexedImages.size) {
			StaticMedia.nameIconSprites!![v] = toolkit!!.generateSprite(indexedImages[v], true)
		}
		PluginChatIcons.loaded() // Not part of the original client: the plugins' chat icons after the game's.
		StaticMedia.compassSprite = toolkit!!.generateSprite(SpriteDataProvider.listSingle(spriteResource, StaticMedia.compassGroupid, 0), true)
		StaticMedia.otherLevelSprite = toolkit.generateSprite(SpriteDataProvider.listSingle(spriteResource, FriendChat.otherLevelGroupId, 0), true)
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.hintMapedgeGroupid)
		StaticMedia.hintMapedgeSprites = arrayOfNulls<NativeSprite>(indexedImages!!.size)
		for (@Pc(372) w in 0 until indexedImages.size) {
			StaticMedia.hintMapedgeSprites!![w] = toolkit.generateSprite(indexedImages[w], true)
		}
	}

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "([Lclient!mi;I)V")
	@JvmStatic
	fun initializeChatIcons(@OriginalArg(0) sprites: Array<NativeSprite?>?) {
		StaticMedia.totalChatIcons = sprites!!.size
		StaticMedia.chatScaleHeights = IntArray(StaticMedia.totalChatIcons + 10)
		StaticMedia.chatIcons = arrayOfNulls<NativeSprite>(StaticMedia.totalChatIcons + 10)
		Arrays.copy(sprites, 0, StaticMedia.chatIcons, 0, StaticMedia.totalChatIcons)
		for (@Pc(26) i in 0 until StaticMedia.totalChatIcons) {
			StaticMedia.chatScaleHeights!![i] = StaticMedia.chatIcons!![i]!!.scaleHeight()
		}
		@Pc(50) var totalChatIcons: Int = StaticMedia.totalChatIcons
		while (totalChatIcons < StaticMedia.chatIcons!!.size) {
			StaticMedia.chatScaleHeights!![totalChatIcons] = 12
			totalChatIcons++
		}
	}

	@OriginalMember(owner = "client!hq", name = "h", descriptor = "[Lclient!mi;")
	@JvmField
	var mapflagSprites: Array<NativeSprite?>? = null

	@OriginalMember(owner = "client!saa", name = "X", descriptor = "[Lclient!mi;")
	@JvmField
	var headiconPkSprites: Array<NativeSprite?>? = null

	@OriginalMember(owner = "client!bb", name = "O", descriptor = "Lclient!mi;")
	@JvmField
	var compassSprite: NativeSprite? = null

	@OriginalMember(owner = "client!ig", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun clearSprites() {
		StaticMedia.hitbarDefaultSprites = null
		StaticMedia.timerbarDefaultSprites = null
		StaticMedia.mapdotSprites = null
		StaticMedia.scrollbarSprites = null
		StaticMedia.compassSprite = null
		StaticMedia.mapflagSprites = null
		StaticMedia.hintHeadiconSprites = null
		StaticMedia.hintMapedgeSprites = null
		StaticMedia.crossSprites = null
		MiniMenu.minimenuHeader = null
		StaticMedia.otherLevelSprite = null
		FontManager.aClass68_4 = null
		Renderers.FONT_RENDERER = null
		StaticMedia.hitmarkSprites = null
		StaticMedia.nameIconSprites = null
		StaticMedia.headiconPrayerSprites = null
		StaticMedia.hintMapmarkerSprites = null
		StaticMedia.headiconPkSprites = null
	}
	@OriginalMember(owner = "client!od", name = "D", descriptor = "I")
	@JvmField
	var crossGroupid: Int = 0

	@OriginalMember(owner = "client!mm", name = "w", descriptor = "[Lclient!mi;")
	@JvmField
	var hintHeadiconSprites: Array<NativeSprite?>? = null
	@OriginalMember(owner = "client!pg", name = "a", descriptor = "(IIIII)V")
	@JvmStatic
	fun drawClickCross(@OriginalArg(0) height: Int, @OriginalArg(1) width: Int, @OriginalArg(2) y: Int, @OriginalArg(4) x: Int) {
		@Pc(5) var clickX: Int = StaticMedia.crossX
		@Pc(12) var clickY: Int = StaticMedia.crossY
		if (OrthoMode.orthoEnabled) {
			clickX += OrthoMode.getOrthoOffsetX()
			clickY += OrthoMode.getOrthoDrawOffsetY()
		}
		@Pc(33) var sprite: NativeSprite?
		if (StaticMedia.crossType == 1) {
			sprite = StaticMedia.crossSprites!![StaticMedia.crossSpritePtr / 100]
			sprite!!.render(clickX - 8, clickY + -8)
			OrthoMode.markOrthoTilesDirty(clickX - 8, clickX - -sprite.scaleWidth() + -8, clickY - 8, sprite.scaleHeight() + -8 + clickY)
		}
		if (StaticMedia.crossType == 2) {
			sprite = StaticMedia.crossSprites!![StaticMedia.crossSpritePtr / 100 + 4]
			sprite!!.render(clickX - 8, clickY + -8)
			OrthoMode.markOrthoTilesDirty(clickX - 8, sprite.scaleWidth() + clickX + -8, clickY - 8, sprite.scaleHeight() + -8 + clickY)
		}
		MapRegion.updateInTutorialIsland()
	}

	@OriginalMember(owner = "client!bu", name = "t", descriptor = "[Lclient!mi;")
	@JvmField
	var hitbarDefaultSprites: Array<NativeSprite?>? = null

	@OriginalMember(owner = "client!dba", name = "B", descriptor = "Lclient!mi;")
	@JvmField
	var otherLevelSprite: NativeSprite? = null

	@OriginalMember(owner = "client!ar", name = "w", descriptor = "[Lclient!mi;")
	@JvmField
	var mapdotSprites: Array<NativeSprite?>? = null

	@OriginalMember(owner = "client!rga", name = "H", descriptor = "[Lclient!mi;")
	@JvmField
	var hintMapmarkerSprites: Array<NativeSprite?>? = null

	@OriginalMember(owner = "client!ta", name = "h", descriptor = "[Lclient!mi;")
	@JvmField
	var scrollbarSprites: Array<NativeSprite?>? = null

	@OriginalMember(owner = "client!pha", name = "d", descriptor = "[Lclient!mi;")
	@JvmField
	var hitmarkSprites: Array<NativeSprite?>? = null

	@OriginalMember(owner = "client!cf", name = "c", descriptor = "Lclient!mi;")
	@JvmField
	var menuRightSideBorderSprite: NativeSprite? = null

	@OriginalMember(owner = "client!ui", name = "o", descriptor = "[Lclient!mi;")
	@JvmField
	var headiconPrayerSprites: Array<NativeSprite?>? = null
	@OriginalMember(owner = "client!ng", name = "g", descriptor = "Lclient!mi;")
	@JvmField
	var menuHeaderLeftCornerSprite: NativeSprite? = null

	@OriginalMember(owner = "client!ch", name = "e", descriptor = "I")
	@JvmField
	var menuSideBorderSpriteId: Int = 0

	@OriginalMember(owner = "client!mr", name = "h", descriptor = "Lclient!mi;")
	@JvmField
	var menuBottomRightCornerSprite: NativeSprite? = null
	@OriginalMember(owner = "client!po", name = "w", descriptor = "Lclient!mi;")
	@JvmField
	var menuBottomBorderSprite: NativeSprite? = null

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(BLclient!in;)V")
	@JvmStatic
	fun loadGroupIds(@OriginalArg(1) spritesResource: Js5ResourceProvider?) {
		StaticMedia.hitmarksGroupid = spritesResource!!.getGroupId("hitmarks")
		StaticMedia.hitbarDefaultGroupid = spritesResource.getGroupId("hitbar_default")
		StaticMedia.timerbarDefaultGroupid = spritesResource.getGroupId("timerbar_default")
		StaticMedia.headiconsPkGroupid = spritesResource.getGroupId("headicons_pk")
		StaticMedia.headiconsPrayerGroupid = spritesResource.getGroupId("headicons_prayer")
		StaticMedia.hintHeadiconsGroupid = spritesResource.getGroupId("hint_headicons")
		StaticMedia.hintMapmarkersGroupid = spritesResource.getGroupId("hint_mapmarkers")
		StaticMedia.mapflagGroupid = spritesResource.getGroupId("mapflag")
		StaticMedia.crossGroupid = spritesResource.getGroupId("cross")
		StaticMedia.mapdotsGroupid = spritesResource.getGroupId("mapdots")
		StaticMedia.scrollbarGroupid = spritesResource.getGroupId("scrollbar")
		StaticMedia.nameIconsGroupid = spritesResource.getGroupId("name_icons")
		StaticMedia.floorShadowsGroupid = spritesResource.getGroupId("floorshadows")
		StaticMedia.compassGroupid = spritesResource.getGroupId("compass")
		FriendChat.otherLevelGroupId = spritesResource.getGroupId("otherlevel")
		StaticMedia.hintMapedgeGroupid = spritesResource.getGroupId("hint_mapedge")
	}
	@OriginalMember(owner = "client!lo", name = "a", descriptor = "(ILclient!in;)I")
	@JvmStatic
	fun countReadyGroups(@OriginalArg(1) resource: Js5ResourceProvider?): Int {
		@Pc(5) var readyCount: Int = 0
		if (resource!!.isFileReady(StaticMedia.hitmarksGroupid)) {
			readyCount++
		}
		if (resource.isFileReady(StaticMedia.hitbarDefaultGroupid)) {
			readyCount++
		}
		if (resource.isFileReady(StaticMedia.timerbarDefaultGroupid)) {
			readyCount++
		}
		if (resource.isFileReady(StaticMedia.headiconsPkGroupid)) {
			readyCount++
		}
		if (resource.isFileReady(StaticMedia.headiconsPrayerGroupid)) {
			readyCount++
		}
		if (resource.isFileReady(StaticMedia.hintHeadiconsGroupid)) {
			readyCount++
		}
		if (resource.isFileReady(StaticMedia.hintMapmarkersGroupid)) {
			readyCount++
		}
		if (resource.isFileReady(StaticMedia.mapflagGroupid)) {
			readyCount++
		}
		if (resource.isFileReady(StaticMedia.crossGroupid)) {
			readyCount++
		}
		if (resource.isFileReady(StaticMedia.mapdotsGroupid)) {
			readyCount++
		}
		if (resource.isFileReady(StaticMedia.scrollbarGroupid)) {
			readyCount++
		}
		if (resource.isFileReady(StaticMedia.nameIconsGroupid)) {
			readyCount++
		}
		if (resource.isFileReady(StaticMedia.floorShadowsGroupid)) {
			readyCount++
		}
		if (resource.isFileReady(StaticMedia.compassGroupid)) {
			readyCount++
		}
		if (resource.isFileReady(FriendChat.otherLevelGroupId)) {
			readyCount++
		}
		if (resource.isFileReady(StaticMedia.hintMapedgeGroupid)) {
			readyCount++
		}
		return readyCount
	}

	@OriginalMember(owner = "client!et", name = "K", descriptor = "Lclient!mi;")
	@JvmField
	var menuBottomLeftCornerSprite: NativeSprite? = null

	@OriginalMember(owner = "client!et", name = "J", descriptor = "Lclient!mi;")
	@JvmField
	var menuLeftSideBorderSprite: NativeSprite? = null

	@OriginalMember(owner = "client!gga", name = "u", descriptor = "[Lclient!mi;")
	@JvmField
	var timerbarDefaultSprites: Array<NativeSprite?>? = null
	@OriginalMember(owner = "client!fd", name = "S", descriptor = "[Lclient!mi;")
	@JvmField
	var crossSprites: Array<NativeSprite?>? = null

	@OriginalMember(owner = "client!vca", name = "o", descriptor = "I")
	@JvmField
	var crossType: Int = 0

	@OriginalMember(owner = "client!wg", name = "b", descriptor = "(I)I")
	@JvmStatic
	fun getStaticMediaGroupCount(): Int {
		return 16
	}

	@OriginalMember(owner = "client!hs", name = "m", descriptor = "I")
	@JvmField
	var crossX: Int = 0

	@OriginalMember(owner = "client!eh", name = "h", descriptor = "I")
	@JvmField
	var crossSpritePtr: Int = 0

	@OriginalMember(owner = "client!jca", name = "b", descriptor = "I")
	@JvmField
	var compassGroupid: Int = 0

	@OriginalMember(owner = "client!lt", name = "f", descriptor = "I")
	@JvmField
	var crossY: Int = 0
}
