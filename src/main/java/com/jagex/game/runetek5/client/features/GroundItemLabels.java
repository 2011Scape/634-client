package com.jagex.game.runetek5.client.features;

import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.OverlayManager;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.cache.node.ObjStack;
import com.jagex.game.runetek5.cache.node.ObjStackEntry;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.config.item.ItemType;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.rendering.Renderers;

/**
 * Not part of the original client: each pile of items on the ground in the scene shows the names
 * of its items, and their counts when more than one, stacked above the tile, most valuable at the
 * bottom, as the client orders a pile (ObjStack.addGroundItem). On only with
 * {@code client.groundItemLabels=true}; the console command {@code grounditems} turns it on and off
 * while playing.
 */
public final class GroundItemLabels {

	public static boolean enabled = Boolean.getBoolean("client.groundItemLabels");

	/** The most items listed for one pile; more show as "and N more". */
	private static final int MAX_LINES = 4;

	private static final int LINE_HEIGHT = 12;

	private static final int COLOUR = 0xFFFF9040;

	private static final int SHADOW = 0xFF000000;

	private static final int TILE_SIZE = 512;

	/** How many piles the last frame labelled, for the smoke test. */
	public static int drawnLastFrame = 0;

	/** The bottom line of each pile the last frame labelled, and where, for the smoke test. */
	public static final String[] lastTexts = new String[64];

	public static final int[] lastX = new int[64];

	public static final int[] lastY = new int[64];

	private GroundItemLabels() {
		/* empty */
	}

	/** The line an item on the ground shows: its name, and its count when more than one. */
	public static String line(ObjStackEntry entry) {
		ItemType type = TypeLists.OBJ.list(entry.id);
		return entry.count > 1 ? type.name + " (" + entry.count + ")" : type.name;
	}

	/** Labels the piles on the local player's level in the game view the scene was just drawn into. */
	public static void draw(int areaX, int areaY) {
		drawnLastFrame = 0;
		if (!enabled || Renderers.FONT_RENDERER == null) {
			return;
		}
		int level = PlayerUpdate.localPlayer.level;
		for (Node node = ObjStack.GROUND_ITEMS.first(); node != null; node = ObjStack.GROUND_ITEMS.next()) {
			// the key packs level << 28 | absolute y << 14 | absolute x (ObjStack.addGroundItem)
			long key = node.key;
			if ((int) (key >>> 28 & 0x3) != level) {
				continue;
			}
			int sceneX = (int) (key & 0x3FFF) - MapRegion.sceneBaseX;
			int sceneY = (int) (key >> 14 & 0x3FFF) - MapRegion.sceneBaseY;
			if (sceneX < 0 || sceneY < 0 || sceneX >= MapRegion.sceneSizeX || sceneY >= MapRegion.sceneSizeY) {
				continue;
			}
			OverlayManager.projectToScreenWithOffset(level, 0, 0, sceneX * TILE_SIZE + TILE_SIZE / 2, 0, sceneY * TILE_SIZE + TILE_SIZE / 2, 0);
			if (OverlayManager.projectedScreenPosition[0] < 0) {
				continue;
			}
			int x = areaX + OverlayManager.projectedScreenPosition[0];
			int y = areaY + OverlayManager.projectedScreenPosition[1];
			ObjStack pile = (ObjStack) node;
			int lines = 0;
			int more = 0;
			String bottom = null;
			for (ObjStackEntry entry = (ObjStackEntry) pile.groundItems.first(); entry != null; entry = (ObjStackEntry) pile.groundItems.next()) {
				if (lines == MAX_LINES) {
					more++;
					continue;
				}
				String text = line(entry);
				if (bottom == null) {
					bottom = text;
				}
				Renderers.FONT_RENDERER.renderCentered(x, COLOUR, text, SHADOW, y - lines * LINE_HEIGHT);
				lines++;
			}
			if (more > 0) {
				Renderers.FONT_RENDERER.renderCentered(x, COLOUR, "and " + more + " more", SHADOW, y - lines * LINE_HEIGHT);
			}
			if (bottom != null && drawnLastFrame < lastTexts.length) {
				lastTexts[drawnLastFrame] = bottom;
				lastX[drawnLastFrame] = x;
				lastY[drawnLastFrame] = y;
				drawnLastFrame++;
			}
		}
	}
}
