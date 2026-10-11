package com.jagex.game.runetek5.client.features;

import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.OverlayManager;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;

/**
 * Not part of the original client: tiles the player marks from the mini menu ("Mark tile"), each
 * drawn with an outline after the scene until it is unmarked. A marker is kept in world
 * coordinates, so it stays on its tile when the map region around the player is rebuilt. On only
 * with {@code client.tileMarkers=true}; the console command {@code tilemarkers} turns it on and off
 * while playing.
 */
public final class TileMarkers {

	public static boolean enabled = Boolean.getBoolean("client.tileMarkers");

	/** The most tiles that can be marked at once. */
	public static final int MAX_MARKERS = 64;

	/** The outline colour, opaque yellow. */
	private static final int COLOUR = 0xFFFFFF00;

	/** The size of a tile in world units. */
	private static final int TILE_SIZE = 512;

	/** The marked tiles, packed by {@link #pack}. */
	private static final int[] markers = new int[MAX_MARKERS];

	private static int markerCount = 0;

	/** How many markers the last frame drew, for the smoke test. */
	public static int drawnLastFrame = 0;

	/** The screen corners of the last marker drawn, x then y for each, for the smoke test. */
	public static final int[] lastCorners = new int[8];

	private TileMarkers() {
		/* empty */
	}

	/** A world tile packed into an int: level in the top 2 bits, then x and y in 15 bits each. */
	private static int pack(int level, int worldX, int worldY) {
		return level << 30 | worldX << 15 | worldY;
	}

	public static int markerCount() {
		return markerCount;
	}

	/** Whether the scene tile is marked on the local player's level. */
	public static boolean isMarked(int sceneX, int sceneY) {
		return indexOf(pack(PlayerUpdate.localPlayer.level, MapRegion.sceneBaseX + sceneX, MapRegion.sceneBaseY + sceneY)) != -1;
	}

	/** Marks the scene tile on the local player's level, or unmarks it when it is marked. */
	public static void toggle(int sceneX, int sceneY) {
		int packed = pack(PlayerUpdate.localPlayer.level, MapRegion.sceneBaseX + sceneX, MapRegion.sceneBaseY + sceneY);
		int index = indexOf(packed);
		if (index != -1) {
			markers[index] = markers[--markerCount];
		} else if (markerCount < MAX_MARKERS) {
			markers[markerCount++] = packed;
		}
	}

	private static int indexOf(int packed) {
		for (int i = 0; i < markerCount; i++) {
			if (markers[i] == packed) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Outlines every marked tile on the local player's level that is in the scene, in the viewport
	 * the scene was just drawn into.
	 */
	public static void draw(int areaX, int areaY) {
		drawnLastFrame = 0;
		if (!enabled || markerCount == 0) {
			return;
		}
		int level = PlayerUpdate.localPlayer.level;
		int[] screenX = new int[4];
		int[] screenY = new int[4];
		for (int i = 0; i < markerCount; i++) {
			int packed = markers[i];
			if (packed >>> 30 != level) {
				continue;
			}
			int sceneX = (packed >> 15 & 0x7FFF) - MapRegion.sceneBaseX;
			int sceneY = (packed & 0x7FFF) - MapRegion.sceneBaseY;
			boolean visible = true;
			for (int corner = 0; corner < 4 && visible; corner++) {
				// corners in order around the tile: (0,0), (1,0), (1,1), (0,1)
				int x = (sceneX + (corner == 1 || corner == 2 ? 1 : 0)) * TILE_SIZE;
				int z = (sceneY + (corner >= 2 ? 1 : 0)) * TILE_SIZE;
				OverlayManager.projectToScreenWithOffset(level, 0, 0, x, 0, z, 0);
				if (OverlayManager.projectedScreenPosition[0] < 0) {
					visible = false;
				}
				screenX[corner] = OverlayManager.projectedScreenPosition[0] + areaX;
				screenY[corner] = OverlayManager.projectedScreenPosition[1] + areaY;
			}
			if (!visible) {
				continue;
			}
			for (int corner = 0; corner < 4; corner++) {
				lastCorners[corner * 2] = screenX[corner];
				lastCorners[corner * 2 + 1] = screenY[corner];
				int next = (corner + 1) % 4;
				ClientState.activeToolkit.line(screenX[corner], screenY[corner], screenX[next], screenY[next], COLOUR, 1);
			}
			drawnLastFrame++;
		}
	}
}
