package com.jagex.game.runetek5.client.features;

import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.OverlayManager;
import com.jagex.game.runetek5.SceneDrawer;
import com.jagex.game.runetek5.config.npc.NPCType;
import com.jagex.game.runetek5.entity.NPCEntity;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.rendering.Renderers;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;

/**
 * Not part of the original client: each npc on screen shows its name, and its combat level when it
 * has one, above its head. It picks npcs and their place the way the overhead text does
 * (OverlayManager.drawEntities): the npcs updated this cycle, the type a transforming npc shows
 * now, the npcs on the player's level or drawn this frame, and the point at their overhead height.
 * On only with {@code client.npcLabels=true}; the console command {@code npclabels} turns it on and
 * off while playing.
 */
public final class NpcLabels {

	public static boolean enabled = Boolean.getBoolean("client.npcLabels");

	private static final int COLOUR = 0xFF00FFFF;

	private static final int SHADOW = 0xFF000000;

	/** How far above the overhead point the label sits, clear of overhead chat. */
	private static final int RAISE = 14;

	/** How many labels the last frame drew, for the smoke test. */
	public static int drawnLastFrame = 0;

	/** The npc types and screen points the last frame labelled, for the smoke test. */
	public static final int[] lastTypes = new int[NPCList.NPC_ARRAY.length];

	public static final int[] lastX = new int[NPCList.NPC_ARRAY.length];

	public static final int[] lastY = new int[NPCList.NPC_ARRAY.length];

	private NpcLabels() {
		/* empty */
	}

	/** The label an npc type shows: its name, and its combat level when it has one. */
	public static String label(NPCType type) {
		return type.combatLevel > 0 ? type.name + " (level-" + type.combatLevel + ")" : type.name;
	}

	/** Labels the npcs in the game view the scene was just drawn into. */
	public static void draw(int clipLeft, int clipTop, int clipWidth, int height) {
		drawnLastFrame = 0;
		if (!enabled || Renderers.FONT_RENDERER == null) {
			return;
		}
		for (int i = 0; i < NPCList.NPC_UPDATE_INDEX && drawnLastFrame < lastTypes.length; i++) {
			NPCEntityNode node = (NPCEntityNode) NPCList.NPC_MAP.get((long) NPCList.NPC_UPDATE_INDICES[i]);
			if (node == null) {
				continue;
			}
			NPCEntity npc = node.npc;
			NPCType type = npc.npcType;
			if (type != null && type.alternativeIds != null) {
				type = type.getMultiNpc(PlayerVarDomain.playerVarProvider);
			}
			if (type == null || type.name == null || type.name.equals("null")) {
				continue;
			}
			if (npc.worldPriority < 0 || SceneDrawer.timesSceneRendered != npc.drawCycle && npc.level != PlayerUpdate.localPlayer.level) {
				continue;
			}
			OverlayManager.projectEntityToScreen(npc.getOverheadHeight(), clipWidth >> 1, height >> 1, npc);
			if (OverlayManager.projectedScreenPosition[0] < 0) {
				continue;
			}
			int x = clipLeft + OverlayManager.projectedScreenPosition[0];
			int y = clipTop + OverlayManager.projectedScreenPosition[1] - RAISE;
			Renderers.FONT_RENDERER.renderCentered(x, COLOUR, label(type), SHADOW, y);
			lastTypes[drawnLastFrame] = type.id;
			lastX[drawnLastFrame] = x;
			lastY[drawnLastFrame] = y;
			drawnLastFrame++;
		}
	}
}
