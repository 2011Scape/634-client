package com.jagex.game.runetek5.graphics.sprite;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.FriendChat;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.graphics.SpriteDataProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.rendering.Renderers;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.font.FontManager;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.MapRegion;

public final class StaticMedia {
	@OriginalMember(owner = "client!fi", name = "N", descriptor = "I")
	public static int nameIconsGroupid;

	@OriginalMember(owner = "client!ec", name = "i", descriptor = "[Lclient!mi;")
	public static NativeSprite[] chatIcons;

	@OriginalMember(owner = "client!mv", name = "f", descriptor = "I")
	public static int hintHeadiconsGroupid;

	@OriginalMember(owner = "client!pq", name = "u", descriptor = "[I")
	public static int[] chatScaleHeights;

	@OriginalMember(owner = "client!bf", name = "d", descriptor = "I")
	public static int mapdotsGroupid;

	@OriginalMember(owner = "client!vw", name = "u", descriptor = "I")
	public static int hintMapmarkersGroupid;

	@OriginalMember(owner = "client!lea", name = "e", descriptor = "I")
	public static int mapflagGroupid;

	@OriginalMember(owner = "client!aj", name = "x", descriptor = "I")
	public static int totalChatIcons;

	@OriginalMember(owner = "client!ha", name = "b", descriptor = "I")
	public static int headiconsPkGroupid;

	@OriginalMember(owner = "client!bf", name = "f", descriptor = "[Lclient!mi;")
	public static NativeSprite[] nameIconSprites;

	@OriginalMember(owner = "client!bd", name = "g", descriptor = "I")
	public static int timerbarDefaultGroupid;

	@OriginalMember(owner = "client!lo", name = "t", descriptor = "I")
	public static int hitmarksGroupid;

	@OriginalMember(owner = "client!mia", name = "i", descriptor = "I")
	public static int hitbarDefaultGroupid;

	@OriginalMember(owner = "client!fh", name = "d", descriptor = "I")
	public static int scrollbarGroupid;

	@OriginalMember(owner = "client!lba", name = "b", descriptor = "I")
	public static int headiconsPrayerGroupid;

	private StaticMedia() {
		/* empty */
	}
	@OriginalMember(owner = "client!qga", name = "m", descriptor = "I")
	public static int floorShadowsGroupid;
	@OriginalMember(owner = "client!rca", name = "c", descriptor = "[Lclient!mi;")
	public static NativeSprite[] hintMapedgeSprites;

	@OriginalMember(owner = "client!tq", name = "t", descriptor = "I")
	public static int hintMapedgeGroupid;
	@OriginalMember(owner = "client!ub", name = "a", descriptor = "(Lclient!in;Lclient!ha;Z)V")
	public static void loadSprites(@OriginalArg(0) Js5ResourceProvider spriteResource, @OriginalArg(1) Toolkit toolkit) {
		@Pc(10) SpriteType[] indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.hitmarksGroupid);
		StaticMedia.hitmarkSprites = new NativeSprite[indexedImages.length];
		for (@Pc(16) int i = 0; i < indexedImages.length; i++) {
			StaticMedia.hitmarkSprites[i] = toolkit.generateSprite(indexedImages[i], true);
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.hitbarDefaultGroupid);
		StaticMedia.hitbarDefaultSprites = new NativeSprite[indexedImages.length];
		for (@Pc(43) int j = 0; j < indexedImages.length; j++) {
			StaticMedia.hitbarDefaultSprites[j] = toolkit.generateSprite(indexedImages[j], true);
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.timerbarDefaultGroupid);
		StaticMedia.timerbarDefaultSprites = new NativeSprite[indexedImages.length];
		for (@Pc(74) int k = 0; k < indexedImages.length; k++) {
			StaticMedia.timerbarDefaultSprites[k] = toolkit.generateSprite(indexedImages[k], true);
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.headiconsPkGroupid);
		StaticMedia.headiconPkSprites = new NativeSprite[indexedImages.length];
		for (@Pc(101) int m = 0; m < indexedImages.length; m++) {
			StaticMedia.headiconPkSprites[m] = toolkit.generateSprite(indexedImages[m], true);
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.headiconsPrayerGroupid);
		StaticMedia.headiconPrayerSprites = new NativeSprite[indexedImages.length];
		for (@Pc(128) int n = 0; n < indexedImages.length; n++) {
			StaticMedia.headiconPrayerSprites[n] = toolkit.generateSprite(indexedImages[n], true);
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.hintHeadiconsGroupid);
		StaticMedia.hintHeadiconSprites = new NativeSprite[indexedImages.length];
		for (@Pc(155) int p = 0; p < indexedImages.length; p++) {
			StaticMedia.hintHeadiconSprites[p] = toolkit.generateSprite(indexedImages[p], true);
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.hintMapmarkersGroupid);
		StaticMedia.hintMapmarkerSprites = new NativeSprite[indexedImages.length];
		for (@Pc(186) int q = 0; q < indexedImages.length; q++) {
			StaticMedia.hintMapmarkerSprites[q] = toolkit.generateSprite(indexedImages[q], true);
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.mapflagGroupid);
		StaticMedia.mapflagSprites = new NativeSprite[indexedImages.length];
		for (@Pc(213) int r = 0; r < indexedImages.length; r++) {
			StaticMedia.mapflagSprites[r] = toolkit.generateSprite(indexedImages[r], true);
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.crossGroupid);
		StaticMedia.crossSprites = new NativeSprite[indexedImages.length];
		for (@Pc(240) int s = 0; s < indexedImages.length; s++) {
			StaticMedia.crossSprites[s] = toolkit.generateSprite(indexedImages[s], true);
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.mapdotsGroupid);
		StaticMedia.mapdotSprites = new NativeSprite[indexedImages.length];
		for (@Pc(271) int t = 0; t < indexedImages.length; t++) {
			StaticMedia.mapdotSprites[t] = toolkit.generateSprite(indexedImages[t], true);
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.scrollbarGroupid);
		StaticMedia.scrollbarSprites = new NativeSprite[indexedImages.length];
		for (@Pc(302) int u = 0; u < indexedImages.length; u++) {
			StaticMedia.scrollbarSprites[u] = toolkit.generateSprite(indexedImages[u], true);
		}
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.nameIconsGroupid);
		StaticMedia.nameIconSprites = new NativeSprite[indexedImages.length];
		for (@Pc(329) int v = 0; v < indexedImages.length; v++) {
			StaticMedia.nameIconSprites[v] = toolkit.generateSprite(indexedImages[v], true);
		}
		StaticMedia.compassSprite = toolkit.generateSprite(SpriteDataProvider.listSingle(spriteResource, StaticMedia.compassGroupid, 0), true);
		StaticMedia.otherLevelSprite = toolkit.generateSprite(SpriteDataProvider.listSingle(spriteResource, FriendChat.otherLevelGroupId, 0), true);
		indexedImages = SpriteType.decodeGroup(spriteResource, StaticMedia.hintMapedgeGroupid);
		StaticMedia.hintMapedgeSprites = new NativeSprite[indexedImages.length];
		for (@Pc(372) int w = 0; w < indexedImages.length; w++) {
			StaticMedia.hintMapedgeSprites[w] = toolkit.generateSprite(indexedImages[w], true);
		}
	}

	@OriginalMember(owner = "client!bca", name = "a", descriptor = "([Lclient!mi;I)V")
	public static void initializeChatIcons(@OriginalArg(0) NativeSprite[] sprites) {
		StaticMedia.totalChatIcons = sprites.length;
		StaticMedia.chatScaleHeights = new int[StaticMedia.totalChatIcons + 10];
		StaticMedia.chatIcons = new NativeSprite[StaticMedia.totalChatIcons + 10];
		Arrays.copy(sprites, 0, StaticMedia.chatIcons, 0, StaticMedia.totalChatIcons);
		for (@Pc(26) int i = 0; i < StaticMedia.totalChatIcons; i++) {
			StaticMedia.chatScaleHeights[i] = StaticMedia.chatIcons[i].scaleHeight();
		}
		for (@Pc(50) int totalChatIcons = StaticMedia.totalChatIcons; totalChatIcons < StaticMedia.chatIcons.length; totalChatIcons++) {
			StaticMedia.chatScaleHeights[totalChatIcons] = 12;
		}
	}

	@OriginalMember(owner = "client!hq", name = "h", descriptor = "[Lclient!mi;")
	public static NativeSprite[] mapflagSprites;

	@OriginalMember(owner = "client!saa", name = "X", descriptor = "[Lclient!mi;")
	public static NativeSprite[] headiconPkSprites;

	@OriginalMember(owner = "client!bb", name = "O", descriptor = "Lclient!mi;")
	public static NativeSprite compassSprite;

	@OriginalMember(owner = "client!ig", name = "a", descriptor = "(I)V")
	public static void clearSprites() {
		StaticMedia.hitbarDefaultSprites = null;
		StaticMedia.timerbarDefaultSprites = null;
		StaticMedia.mapdotSprites = null;
		StaticMedia.scrollbarSprites = null;
		StaticMedia.compassSprite = null;
		StaticMedia.mapflagSprites = null;
		StaticMedia.hintHeadiconSprites = null;
		StaticMedia.hintMapedgeSprites = null;
		StaticMedia.crossSprites = null;
		MiniMenu.minimenuHeader = null;
		StaticMedia.otherLevelSprite = null;
		FontManager.aClass68_4 = null;
		Renderers.FONT_RENDERER = null;
		StaticMedia.hitmarkSprites = null;
		StaticMedia.nameIconSprites = null;
		StaticMedia.headiconPrayerSprites = null;
		StaticMedia.hintMapmarkerSprites = null;
		StaticMedia.headiconPkSprites = null;
	}
	@OriginalMember(owner = "client!od", name = "D", descriptor = "I")
	public static int crossGroupid;

	@OriginalMember(owner = "client!mm", name = "w", descriptor = "[Lclient!mi;")
	public static NativeSprite[] hintHeadiconSprites;
	@OriginalMember(owner = "client!pg", name = "a", descriptor = "(IIIII)V")
	public static void drawClickCross(@OriginalArg(0) int height, @OriginalArg(1) int width, @OriginalArg(2) int y, @OriginalArg(4) int x) {
		@Pc(5) int clickX = StaticMedia.crossX;
		@Pc(12) int clickY = StaticMedia.crossY;
		if (OrthoMode.orthoEnabled) {
			clickX += OrthoMode.getOrthoOffsetX();
			clickY += OrthoMode.getOrthoDrawOffsetY();
		}
		@Pc(33) NativeSprite sprite;
		if (StaticMedia.crossType == 1) {
			sprite = StaticMedia.crossSprites[StaticMedia.crossSpritePtr / 100];
			sprite.render(clickX - 8, clickY + -8);
			OrthoMode.markOrthoTilesDirty(clickX - 8, clickX - -sprite.scaleWidth() + -8, clickY - 8, sprite.scaleHeight() + -8 + clickY);
		}
		if (StaticMedia.crossType == 2) {
			sprite = StaticMedia.crossSprites[StaticMedia.crossSpritePtr / 100 + 4];
			sprite.render(clickX - 8, clickY + -8);
			OrthoMode.markOrthoTilesDirty(clickX - 8, sprite.scaleWidth() + clickX + -8, clickY - 8, sprite.scaleHeight() + -8 + clickY);
		}
		MapRegion.updateInTutorialIsland();
	}

	@OriginalMember(owner = "client!bu", name = "t", descriptor = "[Lclient!mi;")
	public static NativeSprite[] hitbarDefaultSprites;

	@OriginalMember(owner = "client!dba", name = "B", descriptor = "Lclient!mi;")
	public static NativeSprite otherLevelSprite;

	@OriginalMember(owner = "client!ar", name = "w", descriptor = "[Lclient!mi;")
	public static NativeSprite[] mapdotSprites;

	@OriginalMember(owner = "client!rga", name = "H", descriptor = "[Lclient!mi;")
	public static NativeSprite[] hintMapmarkerSprites;

	@OriginalMember(owner = "client!ta", name = "h", descriptor = "[Lclient!mi;")
	public static NativeSprite[] scrollbarSprites;

	@OriginalMember(owner = "client!pha", name = "d", descriptor = "[Lclient!mi;")
	public static NativeSprite[] hitmarkSprites;

	@OriginalMember(owner = "client!cf", name = "c", descriptor = "Lclient!mi;")
	public static NativeSprite menuRightSideBorderSprite;

	@OriginalMember(owner = "client!ui", name = "o", descriptor = "[Lclient!mi;")
	public static NativeSprite[] headiconPrayerSprites;
	@OriginalMember(owner = "client!ng", name = "g", descriptor = "Lclient!mi;")
	public static NativeSprite menuHeaderLeftCornerSprite;

	@OriginalMember(owner = "client!ch", name = "e", descriptor = "I")
	public static int menuSideBorderSpriteId;

	@OriginalMember(owner = "client!mr", name = "h", descriptor = "Lclient!mi;")
	public static NativeSprite menuBottomRightCornerSprite;
	@OriginalMember(owner = "client!po", name = "w", descriptor = "Lclient!mi;")
	public static NativeSprite menuBottomBorderSprite;

	@OriginalMember(owner = "client!gn", name = "a", descriptor = "(BLclient!in;)V")
	public static void loadGroupIds(@OriginalArg(1) Js5ResourceProvider spritesResource) {
		StaticMedia.hitmarksGroupid = spritesResource.getGroupId("hitmarks");
		StaticMedia.hitbarDefaultGroupid = spritesResource.getGroupId("hitbar_default");
		StaticMedia.timerbarDefaultGroupid = spritesResource.getGroupId("timerbar_default");
		StaticMedia.headiconsPkGroupid = spritesResource.getGroupId("headicons_pk");
		StaticMedia.headiconsPrayerGroupid = spritesResource.getGroupId("headicons_prayer");
		StaticMedia.hintHeadiconsGroupid = spritesResource.getGroupId("hint_headicons");
		StaticMedia.hintMapmarkersGroupid = spritesResource.getGroupId("hint_mapmarkers");
		StaticMedia.mapflagGroupid = spritesResource.getGroupId("mapflag");
		StaticMedia.crossGroupid = spritesResource.getGroupId("cross");
		StaticMedia.mapdotsGroupid = spritesResource.getGroupId("mapdots");
		StaticMedia.scrollbarGroupid = spritesResource.getGroupId("scrollbar");
		StaticMedia.nameIconsGroupid = spritesResource.getGroupId("name_icons");
		StaticMedia.floorShadowsGroupid = spritesResource.getGroupId("floorshadows");
		StaticMedia.compassGroupid = spritesResource.getGroupId("compass");
		FriendChat.otherLevelGroupId = spritesResource.getGroupId("otherlevel");
		StaticMedia.hintMapedgeGroupid = spritesResource.getGroupId("hint_mapedge");
	}
	@OriginalMember(owner = "client!lo", name = "a", descriptor = "(ILclient!in;)I")
	public static int countReadyGroups(@OriginalArg(1) Js5ResourceProvider resource) {
		@Pc(5) int readyCount = 0;
		if (resource.isFileReady(StaticMedia.hitmarksGroupid)) {
			readyCount++;
		}
		if (resource.isFileReady(StaticMedia.hitbarDefaultGroupid)) {
			readyCount++;
		}
		if (resource.isFileReady(StaticMedia.timerbarDefaultGroupid)) {
			readyCount++;
		}
		if (resource.isFileReady(StaticMedia.headiconsPkGroupid)) {
			readyCount++;
		}
		if (resource.isFileReady(StaticMedia.headiconsPrayerGroupid)) {
			readyCount++;
		}
		if (resource.isFileReady(StaticMedia.hintHeadiconsGroupid)) {
			readyCount++;
		}
		if (resource.isFileReady(StaticMedia.hintMapmarkersGroupid)) {
			readyCount++;
		}
		if (resource.isFileReady(StaticMedia.mapflagGroupid)) {
			readyCount++;
		}
		if (resource.isFileReady(StaticMedia.crossGroupid)) {
			readyCount++;
		}
		if (resource.isFileReady(StaticMedia.mapdotsGroupid)) {
			readyCount++;
		}
		if (resource.isFileReady(StaticMedia.scrollbarGroupid)) {
			readyCount++;
		}
		if (resource.isFileReady(StaticMedia.nameIconsGroupid)) {
			readyCount++;
		}
		if (resource.isFileReady(StaticMedia.floorShadowsGroupid)) {
			readyCount++;
		}
		if (resource.isFileReady(StaticMedia.compassGroupid)) {
			readyCount++;
		}
		if (resource.isFileReady(FriendChat.otherLevelGroupId)) {
			readyCount++;
		}
		if (resource.isFileReady(StaticMedia.hintMapedgeGroupid)) {
			readyCount++;
		}
		return readyCount;
	}

	@OriginalMember(owner = "client!et", name = "K", descriptor = "Lclient!mi;")
	public static NativeSprite menuBottomLeftCornerSprite;

	@OriginalMember(owner = "client!et", name = "J", descriptor = "Lclient!mi;")
	public static NativeSprite menuLeftSideBorderSprite;

	@OriginalMember(owner = "client!gga", name = "u", descriptor = "[Lclient!mi;")
	public static NativeSprite[] timerbarDefaultSprites;
	@OriginalMember(owner = "client!fd", name = "S", descriptor = "[Lclient!mi;")
	public static NativeSprite[] crossSprites;

	@OriginalMember(owner = "client!vca", name = "o", descriptor = "I")
	public static int crossType = 0;

	@OriginalMember(owner = "client!wg", name = "b", descriptor = "(I)I")
	public static int getStaticMediaGroupCount() {
		return 16;
	}

	@OriginalMember(owner = "client!hs", name = "m", descriptor = "I")
	public static int crossX = 0;

	@OriginalMember(owner = "client!eh", name = "h", descriptor = "I")
	public static int crossSpritePtr = 0;

	@OriginalMember(owner = "client!jca", name = "b", descriptor = "I")
	public static int compassGroupid;

	@OriginalMember(owner = "client!lt", name = "f", descriptor = "I")
	public static int crossY = 0;
}
