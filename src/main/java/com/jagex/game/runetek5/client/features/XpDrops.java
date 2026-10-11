package com.jagex.game.runetek5.client.features;

import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.rendering.Renderers;

/**
 * Not part of the original client: when a skill's experience goes up, a "+N Skill" drop rises
 * near the top of the game view, left of the minimap, and fades. The first update of each skill after login only
 * records its experience, so logging in shows no drops. On only with {@code client.xpDrops=true};
 * the console command {@code xpdrops} turns it on and off while playing.
 */
public final class XpDrops {

	public static boolean enabled = Boolean.getBoolean("client.xpDrops");

	/** The skills, in the order the server numbers them. */
	private static final String[] SKILL_NAMES = { "Attack", "Defence", "Strength", "Constitution", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter", "Construction", "Summoning", "Dungeoneering" };

	/** How many client cycles a drop stays up (50 a second). */
	public static final int LIFETIME = 100;

	/** How far a drop rises over its life, in pixels. */
	private static final int RISE = 40;

	private static final int MAX_DROPS = 8;

	private static final int COLOUR = 0xFFFFFFFF;

	private static final int SHADOW = 0xFF000000;

	/** Whether each skill's first update since login has been seen. */
	private static final boolean[] primed = new boolean[SKILL_NAMES.length];

	private static final int[] dropSkill = new int[MAX_DROPS];

	private static final int[] dropAmount = new int[MAX_DROPS];

	private static final int[] dropCycle = new int[MAX_DROPS];

	private static int dropCount = 0;

	/** The drops the last frame drew, for the smoke test. */
	public static int drawnLastFrame = 0;

	/** The text of the newest drop, for the smoke test. */
	public static String lastDropText = null;

	private XpDrops() {
		/* empty */
	}

	/**
	 * Called with each stat update the server sends, before the client stores the new experience.
	 */
	public static void statUpdated(int skillId, int oldXp, int newXp) {
		if (skillId < 0 || skillId >= SKILL_NAMES.length) {
			return;
		}
		if (!primed[skillId]) {
			primed[skillId] = true;
			return;
		}
		if (!enabled || newXp <= oldXp) {
			return;
		}
		if (dropCount == MAX_DROPS) {
			System.arraycopy(dropSkill, 1, dropSkill, 0, MAX_DROPS - 1);
			System.arraycopy(dropAmount, 1, dropAmount, 0, MAX_DROPS - 1);
			System.arraycopy(dropCycle, 1, dropCycle, 0, MAX_DROPS - 1);
			dropCount--;
		}
		dropSkill[dropCount] = skillId;
		dropAmount[dropCount] = newXp - oldXp;
		dropCycle[dropCount] = ClientState.clientCycle;
		dropCount++;
		lastDropText = text(dropCount - 1);
	}

	/** Forgets every skill's experience and the drops on screen, at logout. */
	public static void reset() {
		for (int i = 0; i < primed.length; i++) {
			primed[i] = false;
		}
		dropCount = 0;
		lastDropText = null;
	}

	public static int dropCount() {
		return dropCount;
	}

	private static String text(int index) {
		return "+" + dropAmount[index] + " " + SKILL_NAMES[dropSkill[index]];
	}

	/** Draws the live drops in the game view and lets the expired ones go. */
	public static void draw(int areaX, int areaY, int width) {
		drawnLastFrame = 0;
		int live = 0;
		for (int i = 0; i < dropCount; i++) {
			if (ClientState.clientCycle - dropCycle[i] < LIFETIME) {
				dropSkill[live] = dropSkill[i];
				dropAmount[live] = dropAmount[i];
				dropCycle[live] = dropCycle[i];
				live++;
			}
		}
		dropCount = live;
		if (!enabled || dropCount == 0 || Renderers.FONT_RENDERER == null) {
			return;
		}
		// left of the minimap and the orbs, which the interfaces draw over the game view
		int x = areaX + width - 300;
		for (int i = 0; i < dropCount; i++) {
			int age = ClientState.clientCycle - dropCycle[i];
			// below the debug overlay's lines; newer drops sit lower, so a burst of drops reads as a column
			int y = areaY + 140 + (dropCount - 1 - i) * 14 - age * RISE / LIFETIME;
			int alpha = 255 - age * 255 / LIFETIME;
			Renderers.FONT_RENDERER.renderCentered(x, (alpha << 24) | (COLOUR & 0xFFFFFF), text(i), (alpha << 24) | (SHADOW & 0xFFFFFF), y);
			drawnLastFrame++;
		}
	}
}
