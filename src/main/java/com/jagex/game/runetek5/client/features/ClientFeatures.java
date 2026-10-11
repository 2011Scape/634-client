package com.jagex.game.runetek5.client.features;

import com.jagex.game.runetek5.core.constants.MiniMenuAction;
import com.jagex.game.runetek5.MaterialManager;
import com.jagex.game.runetek5.client.menu.MiniMenuEntry;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.entity.PathingEntity;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import com.jagex.game.runetek5.statics.OutPlayerIndexCount;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.client.ClientState;

/**
 * Switches for behaviour that is not part of the original client, read once from system properties.
 * Every switch is off by default, so a client started without them behaves as the 2010 jar did.
 * <p>
 * This class is not part of the original client.
 */
public final class ClientFeatures {

	/** Ids and tile coordinates on right-click menus, and a coordinates overlay beside the fps text. */
	public static final boolean DEBUG = Boolean.getBoolean("client.debug");

	/** A mouse-wheel zoom wider than the original field of view allows. */
	public static final boolean ZOOM = Boolean.getBoolean("client.zoom");

	/** How much further than the original the scene is drawn and fogged; 1 is the original distance. */
	public static final double RENDER_DISTANCE = parseDouble(System.getProperty("client.renderDistance"), 1.0D);

	/** Command history, word editing and server autocomplete in the developer console. */
	public static final boolean CONSOLE = Boolean.getBoolean("client.console");

	/**
	 * Holding Shift makes a left click on an inventory item drop it, where the item can be dropped.
	 * The console command {@code shiftdrop} turns it on and off while playing.
	 */
	public static boolean shiftDrop = Boolean.getBoolean("client.shiftDrop");

	/** The interface of the inventory tab. */
	private static final int INVENTORY = 149;


	/**
	 * The server the client talks to: {@code void} (the default) for a Void server, or {@code jagex}
	 * for the protocol the 2010 client spoke. Void has no lobby, sends the area sound packets 5 and
	 * 66 in a longer form, and its scripts use three instructions and more client variables than
	 * the 2010 cache; with {@code jagex} none of that applies.
	 */
	public static final String SERVER = server(System.getProperty("client.server"));

	/** Whether the client talks to a Void server. */
	public static final boolean VOID = "void".equals(SERVER);

	/** The size of each server packet, by opcode, where it differs from the client's own; -3 means none. */
	private static final int[] PACKET_SIZES = packetSizes(System.getProperty("client.packets"));

	/**
	 * Frames drawn per second, or 0 for the client's own pacing of one frame per game tick (50 per
	 * second). The game logic keeps its 50 ticks per second either way, so the frames between two ticks
	 * show the same game state: a higher rate lowers input latency and smooths the camera, but does not
	 * make movement smoother.
	 */
	public static final int FPS = Math.max(0, Integer.getInteger("client.fps", 0));

	/** When the next game tick is due, by System.nanoTime, once client.fps paces the frames. */
	private static long nextTick = 0L;

	/** The game tick interval the frames are paced against, in nanoseconds. */
	private static long tickInterval = 0L;

	/** The camera of the previous and the current game cycle: x, y, z, pitch, yaw; see blendCamera. */
	private static final int[] cameraBefore = new int[5];
	private static final int[] cameraNow = new int[5];
	private static int cameraCycle = Integer.MIN_VALUE;
	private static int cameraMode = Integer.MIN_VALUE;

	/** How many cycles the camera's recorded move spans: more than one when the frames are slower than the cycles. */
	private static int cameraGap = 1;

	/** A move recorded over more cycles than this is a cut; the frames are far behind the game. */
	private static final int MAX_GAP = 10;

	/** A camera move of more than this many units in one cycle is a cut, not a motion, and is not blended. */
	private static final int CAMERA_CUT = 1024;
	private static final int CAMERA_TURN_CUT = 4096;

	/**
	 * How many screen pixels each game pixel takes, for high resolution displays; 1 draws the game at
	 * its own size. The Java renderers scale: safe mode, which draws through Java, and OpenGL and
	 * DirectX, which draw the frame off screen at the game's size and copy it to the window scaled.
	 * The native software renderer reads each frame back and draws it scaled through Java. The native
	 * OpenGL renderer draws through native code only, so with it the game keeps its own size.
	 */
	public static final double SCALE = Math.max(1.0D, parseDouble(System.getProperty("client.scale"), 1.0D));

	/** A host to connect to instead of the one the client works out itself, or null. */
	public static final String ADDRESS = System.getProperty("client.address");

	/** A port to use for both connection attempts instead of the client's own, or 0. */
	public static final int PORT = Integer.getInteger("client.port", 0);

	/** How far one wheel notch zooms, in field of view units. */
	public static final int ZOOM_STEP = 15;

	/** The zoom added to the field of view; kept within bounds by the projection setup. */
	public static int zoomOffset = 0;

	/** Whether the pointer was over the game view while the interfaces were processed this tick. */
	public static boolean overGameScreen = false;

	/** The last wheel movement of this tick, kept apart because the console clears the shared one. */
	public static int wheelDelta = 0;

	private ClientFeatures() {
		/* empty */
	}

	private static String server(String value) {
		if (value == null || value.equalsIgnoreCase("void")) {
			return "void";
		}
		if (value.equalsIgnoreCase("jagex")) {
			return "jagex";
		}
		System.err.println("client.server: unknown server " + value + ", using void");
		return "void";
	}

	/**
	 * Server packet sizes that override the client's: Void's two longer packets, then the entries of
	 * the properties file {@code client.packets} names, one per line as {@code <opcode>=<size>}, where
	 * -1 means a size byte and -2 a size short precede the packet.
	 */
	private static int[] packetSizes(String file) {
		int[] sizes = new int[256];
		for (int i = 0; i < sizes.length; i++) {
			sizes[i] = -3;
		}
		if ("void".equals(SERVER)) {
			sizes[5] = 8;
			sizes[66] = 8;
		}
		if (file != null) {
			java.util.Properties properties = new java.util.Properties();
			try {
				java.io.Reader reader = new java.io.FileReader(file);
				try {
					properties.load(reader);
				} finally {
					reader.close();
				}
				java.util.Iterator iterator = properties.keySet().iterator();
				while (iterator.hasNext()) {
					String key = (String) iterator.next();
					int opcode = Integer.parseInt(key.trim());
					int size = Integer.parseInt(properties.getProperty(key).trim());
					if (opcode < 0 || opcode >= sizes.length || size < -2) {
						throw new IllegalArgumentException(key + "=" + size);
					}
					sizes[opcode] = size;
				}
			} catch (Exception ex) {
				System.err.println("client.packets: cannot use " + file + ": " + ex);
			}
		}
		return sizes;
	}

	/**
	 * How many game ticks of {@code interval} nanoseconds are due now, at most ten, as the client's own
	 * timer counts them; used when client.fps paces the frames instead of the ticks.
	 */
	public static int ticksDue(long interval) {
		long now = System.nanoTime();
		tickInterval = interval;
		if (nextTick == 0L) {
			nextTick = now;
		}
		int count = 0;
		while (count < 10 && now >= nextTick) {
			count++;
			nextTick += interval;
		}
		if (nextTick < now) {
			nextTick = now;
		}
		return count;
	}

	/** How far the current game cycle has run, 0 at its start and 1 at the next tick; 1 without client.fps. */
	public static float cycleFraction() {
		if (FPS <= 0 || nextTick == 0L || tickInterval <= 0L) {
			return 1.0F;
		}
		float fraction = 1.0F - (float) (nextTick - System.nanoTime()) / (float) tickInterval;
		return fraction < 0.0F ? 0.0F : fraction > 1.0F ? 1.0F : fraction;
	}

	/**
	 * With client.fps, moves the camera to where it is between the previous game cycle and this one,
	 * by how far the cycle has run, so that a frame drawn between two ticks shows the camera on its
	 * way rather than where the last tick left it. Called once the frame's camera is worked out; the
	 * first frame of each cycle records that camera. A cut (a mode change, a jump of more than two
	 * tiles, a turn of more than a quarter) is shown at once, not slid into.
	 */
	public static void blendCamera(int cycle, int mode) {
		if (FPS <= 0) {
			return;
		}
		int[] now = { Camera.cameraPositionAbsoluteX, Camera.cameraPositionAbsoluteZ, Camera.cameraPositionAbsoluteY, Camera.cameraPitch, Camera.cameraYaw };
		if (cycle != cameraCycle) {
			int gap = cycle - cameraCycle;
			boolean cut = cameraCycle == Integer.MIN_VALUE || gap <= 0 || gap > MAX_GAP || mode != cameraMode
				|| Math.abs(now[0] - cameraNow[0]) > CAMERA_CUT * gap || Math.abs(now[1] - cameraNow[1]) > CAMERA_CUT * gap || Math.abs(now[2] - cameraNow[2]) > CAMERA_CUT * gap
				|| Math.abs(angleDelta(cameraNow[3], now[3])) > CAMERA_TURN_CUT * gap || Math.abs(angleDelta(cameraNow[4], now[4])) > CAMERA_TURN_CUT * gap;
			System.arraycopy(cut ? now : cameraNow, 0, cameraBefore, 0, 5);
			System.arraycopy(now, 0, cameraNow, 0, 5);
			cameraGap = cut ? 1 : gap;
			cameraCycle = cycle;
			cameraMode = mode;
		}
		float fraction = gapFraction(cameraGap);
		Camera.cameraPositionAbsoluteX = lerp(cameraBefore[0], cameraNow[0], fraction);
		Camera.cameraPositionAbsoluteZ = lerp(cameraBefore[1], cameraNow[1], fraction);
		Camera.cameraPositionAbsoluteY = lerp(cameraBefore[2], cameraNow[2], fraction);
		Camera.cameraPitch = lerpAngle(cameraBefore[3], cameraNow[3], fraction);
		Camera.cameraYaw = lerpAngle(cameraBefore[4], cameraNow[4], fraction);
	}

	/** An entity move of more than this many units in one cycle is a teleport, not a walk, and is not blended. */
	private static final int ENTITY_CUT = 1024;

	/**
	 * How far along a move recorded over the given number of cycles the frame is: the cycles before
	 * this one have passed in full, and this one by how far it has run. When the frames are slower
	 * than the cycles, a recorded move spans several cycles, and the frame shows it evenly.
	 */
	private static float gapFraction(int gap) {
		return gap <= 1 ? cycleFraction() : ((float) (gap - 1) + cycleFraction()) / (float) gap;
	}

	private static int entityCycle = Integer.MIN_VALUE;

	/**
	 * With client.fps, moves every player and NPC to where it is between the previous game cycle and
	 * this one, by how far the cycle has run, for the frame about to be drawn; restoreEntities puts them
	 * back. The first frame of a cycle records where the cycle left each entity. An entity that is
	 * new, changed level, or moved more than two tiles in a cycle is shown where it is.
	 */
	public static void blendEntities(int cycle) {
		if (FPS <= 0) {
			return;
		}
		boolean newCycle = cycle != entityCycle;
		entityCycle = cycle;
		int players = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES;
		int[] playerIndices = PlayerUpdate.OUT_PLAYER_INDICES;
		int npcs = NPCList.NPC_UPDATE_INDEX;
		int[] npcIds = NPCList.NPC_UPDATE_INDICES;
		for (int i = 0; i < players + npcs; i++) {
			PathingEntity entity;
			if (i < players) {
				entity = PlayerUpdate.players[playerIndices[i]];
			} else {
				NPCEntityNode node = (NPCEntityNode) NPCList.NPC_MAP.get((long) npcIds[i - players]);
				entity = node == null ? null : node.npc;
			}
			if (entity == null || entity.blended) {
				continue;
			}
			int x = entity.x;
			int z = entity.z;
			int yaw = entity.yaw.rotation;
			if (newCycle) {
				int gap = cycle - entity.blendCycle;
				boolean cut = gap <= 0 || gap > MAX_GAP || entity.cycleLevel != entity.level
					|| Math.abs(x - entity.cycleX) > ENTITY_CUT * gap || Math.abs(z - entity.cycleZ) > ENTITY_CUT * gap;
				entity.previousX = cut ? x : entity.cycleX;
				entity.previousZ = cut ? z : entity.cycleZ;
				entity.previousYaw = cut ? yaw : entity.cycleYaw;
				entity.cycleX = x;
				entity.cycleZ = z;
				entity.cycleYaw = yaw;
				entity.blendGap = cut ? 1 : gap;
				entity.blendCycle = cycle;
				entity.cycleLevel = entity.level;
			} else if (entity.blendCycle != cycle) {
				continue; // appeared during the cycle: shown where it is until the next cycle records it
			}
			float fraction = gapFraction(entity.blendGap);
			entity.realX = x;
			entity.realZ = z;
			entity.realYaw = yaw;
			entity.x = lerp(entity.previousX, entity.cycleX, fraction);
			entity.z = lerp(entity.previousZ, entity.cycleZ, fraction);
			entity.yaw.rotation = lerpAngle(entity.previousYaw, entity.cycleYaw, fraction);
			entity.blended = true;
		}
	}

	/** Puts every entity back where this cycle left it, once the frame is drawn. */
	public static void restoreEntities() {
		if (FPS <= 0) {
			return;
		}
		int players = OutPlayerIndexCount.NUM_OUT_PLAYER_INDICES;
		int[] playerIndices = PlayerUpdate.OUT_PLAYER_INDICES;
		int npcs = NPCList.NPC_UPDATE_INDEX;
		int[] npcIds = NPCList.NPC_UPDATE_INDICES;
		for (int i = 0; i < players + npcs; i++) {
			PathingEntity entity;
			if (i < players) {
				entity = PlayerUpdate.players[playerIndices[i]];
			} else {
				NPCEntityNode node = (NPCEntityNode) NPCList.NPC_MAP.get((long) npcIds[i - players]);
				entity = node == null ? null : node.npc;
			}
			if (entity != null && entity.blended) {
				entity.x = entity.realX;
				entity.z = entity.realZ;
				entity.yaw.rotation = entity.realYaw;
				entity.blended = false;
			}
		}
	}

	/** How many steps a game cycle is split into for the animations, with client.fps. */
	public static final int ANIMATION_STEPS = 16;

	/**
	 * With client.fps, how far the current game cycle has run, in steps out of ANIMATION_STEPS; 0
	 * otherwise. An animation blends between its keyframes by cycles elapsed over the keyframe's
	 * length, so a frame drawn between two cycles adds this step to the cycles elapsed, in finer
	 * units, and the animation moves on between cycles too.
	 */
	public static int animationStep() {
		if (FPS <= 0) {
			return 0;
		}
		int step = (int) (cycleFraction() * (float) ANIMATION_STEPS);
		return step < 0 ? 0 : step >= ANIMATION_STEPS ? ANIMATION_STEPS - 1 : step;
	}

	/** Puts the camera back where this cycle left it, once the frame is drawn. */
	public static void restoreCamera() {
		if (FPS <= 0 || cameraCycle == Integer.MIN_VALUE) {
			return;
		}
		Camera.cameraPositionAbsoluteX = cameraNow[0];
		Camera.cameraPositionAbsoluteZ = cameraNow[1];
		Camera.cameraPositionAbsoluteY = cameraNow[2];
		Camera.cameraPitch = cameraNow[3];
		Camera.cameraYaw = cameraNow[4];
	}

	private static int lerp(int from, int to, float fraction) {
		return from + Math.round((float) (to - from) * fraction);
	}

	/** The signed shortest turn from one angle to another, in the client's 16384 units per turn. */
	private static int angleDelta(int from, int to) {
		int delta = to - from & 0x3FFF;
		return delta > 8192 ? delta - 16384 : delta;
	}

	private static int lerpAngle(int from, int to, float fraction) {
		return from + Math.round((float) angleDelta(from, to) * fraction) & 0x3FFF;
	}

	/**
	 * The OpenGL anti-aliasing pipeline: with any of client.msaa, client.smaa or client.renderScale
	 * set, the OpenGL renderer draws the 3D view into its own framebuffer, multisampled by MSAA
	 * samples (0, 2, 4 or 8; -1 follows the game's own anti-aliasing setting), and copies the result
	 * into the frame, so the interface is never multisampled. The window's own surface then has no
	 * samples. The settings may change while the client runs (the console commands msaa, smaa and
	 * renderscale); the renderer rebuilds its framebuffer on the next frame.
	 */
	public static final boolean AA_PIPELINE = System.getProperty("client.msaa") != null || Boolean.getBoolean("client.smaa") || System.getProperty("client.renderScale") != null;
	public static int msaa = parseSamples(System.getProperty("client.msaa"));
	public static boolean smaa = Boolean.getBoolean("client.smaa");
	public static double renderScale = parseRenderScale(System.getProperty("client.renderScale"));
	/** Alpha-to-coverage for cut-out textures while the 3D view is multisampled; client.a2c=false turns it off. */
	public static boolean a2c = !"false".equals(System.getProperty("client.a2c"));
	/** Set by the OpenGL renderer when the driver has no framebuffer objects; the pipeline then stays off. */
	public static boolean aaPipelineUnavailable = false;

	/** The samples the 3D view is drawn with: client.msaa, or the game's own setting when it is -1. */
	public static int sceneSamples(int settingSamples) {
		return msaa < 0 ? settingSamples : msaa;
	}

	private static int parseSamples(String value) {
		if (value == null) {
			return -1;
		}
		try {
			int samples = Integer.parseInt(value.trim());
			if (samples == 0 || samples == 2 || samples == 4 || samples == 8) {
				return samples;
			}
		} catch (NumberFormatException ex) {
		}
		System.err.println("client.msaa: " + value + " is not 0, 2, 4 or 8; following the game's setting");
		return -1;
	}

	private static double parseRenderScale(String value) {
		double scale = parseDouble(value, 1.0D);
		if (scale == 1.0D || scale == 1.5D || scale == 2.0D) {
			return scale;
		}
		System.err.println("client.renderScale: " + value + " is not 1.0, 1.5 or 2.0; drawing at 1.0");
		return 1.0D;
	}

	/**
	 * The console commands msaa <0|2|4|8>, smaa and renderscale <1|1.5|2>: they change the pipeline's
	 * settings and drop the renderer's framebuffers, which are made again on the next frame. Returns
	 * the line to print, or null when the command is not one of them. The pipeline itself has to be
	 * on from the start, since the window's samples are chosen when the renderer is built.
	 */
	public static String antialiasingCommand(String command, Toolkit toolkit) {
		String[] words = command.trim().split(" +");
		String name = words[0].toLowerCase();
		if (!name.equals("msaa") && !name.equals("smaa") && !name.equals("renderscale")) {
			return null;
		}
		if (!AA_PIPELINE) {
			return "Start the client with --msaa, --smaa or --render-scale to use " + name + ".";
		}
		if (name.equals("smaa")) {
			smaa = !smaa;
		} else if (words.length < 2) {
			return name.equals("msaa") ? "MSAA: " + msaa + " samples (msaa 0, 2, 4 or 8; -1 follows the game's setting)." : "Render scale: " + renderScale + " (renderscale 1, 1.5 or 2).";
		} else if (name.equals("msaa")) {
			int samples = parseSamples(words[1]);
			if (samples < 0 && !words[1].equals("-1")) {
				return "msaa takes 0, 2, 4 or 8.";
			}
			msaa = samples;
		} else {
			double scale = parseDouble(words[1], 0.0D);
			if (scale != 1.0D && scale != 1.5D && scale != 2.0D) {
				return "renderscale takes 1, 1.5 or 2.";
			}
			renderScale = scale;
		}
		if (toolkit instanceof GlToolkit) {
			((GlToolkit) toolkit).dropSceneTarget();
		}
		return "MSAA " + msaa + ", SMAA " + (smaa ? "on" : "off") + ", render scale " + renderScale + ".";
	}

	/** Starts the 3D view of a frame in the renderer's own framebuffer, when the pipeline is on and the renderer is OpenGL. */
	public static void beginScene(Toolkit toolkit, int x, int y, int width, int height) {
		if (AA_PIPELINE && toolkit instanceof GlToolkit) {
			((GlToolkit) toolkit).beginScene(x, y, width, height);
		}
	}

	/** Ends the 3D view: the renderer resolves its framebuffer and draws it into the frame. */
	public static void endScene(Toolkit toolkit) {
		if (AA_PIPELINE && toolkit instanceof GlToolkit) {
			((GlToolkit) toolkit).endScene();
		}
	}

	/** Set by a renderer that cannot scale after all (no framebuffer support), with a message printed once. */
	public static boolean scaleUnavailable = false;

	/** Whether the window is scaled now: client.scale is above 1 and a renderer that can scale (or none yet) draws. */
	public static boolean scaled() {
		if (SCALE <= 1.0D || scaleUnavailable) {
			return false;
		}
		Object toolkit = ClientState.activeToolkit;
		if (toolkit == null) {
			return true;
		}
		String name = toolkit.getClass().getName();
		return name.equals("JavaToolkit") || name.equals("oa") || name.equals("com.jagex.game.runetek5.graphics.gl.GlToolkit") || name.equals("com.jagex.game.runetek5.toolkit.D3DToolkit");
	}

	/** Whether the scale is a whole number, so that a game pixel maps to a block of screen pixels. */
	public static boolean wholeScale() {
		return SCALE == Math.floor(SCALE);
	}

	/** A game size in screen pixels. */
	public static int scale(int value) {
		return scaled() ? (int) Math.round((double) value * SCALE) : value;
	}

	/** A screen size in game pixels. */
	public static int unscale(int value) {
		return scaled() ? (int) ((double) value / SCALE) : value;
	}

	/** The graphics of the scaled window, drawing game pixels at the scale; unchanged when not scaled. */
	public static java.awt.Graphics scaleGraphics(java.awt.Graphics graphics) {
		if (!scaled() || !(graphics instanceof java.awt.Graphics2D)) {
			return graphics;
		}
		java.awt.Graphics2D scaled = (java.awt.Graphics2D) graphics;
		boolean whole = SCALE == Math.floor(SCALE);
		scaled.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, whole
			? java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
			: java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		scaled.scale(SCALE, SCALE);
		return scaled;
	}

	/** A mouse event of the scaled window, at the game pixel it points at. */
	public static java.awt.event.MouseEvent unscaleEvent(java.awt.event.MouseEvent event) {
		if (!scaled()) {
			return event;
		}
		int x = unscale(event.getX());
		int y = unscale(event.getY());
		if (event instanceof java.awt.event.MouseWheelEvent) {
			java.awt.event.MouseWheelEvent wheel = (java.awt.event.MouseWheelEvent) event;
			return new java.awt.event.MouseWheelEvent(wheel.getComponent(), wheel.getID(), wheel.getWhen(), wheel.getModifiersEx(), x, y,
				wheel.getXOnScreen(), wheel.getYOnScreen(), wheel.getClickCount(), wheel.isPopupTrigger(), wheel.getScrollType(),
				wheel.getScrollAmount(), wheel.getWheelRotation(), wheel.getPreciseWheelRotation());
		}
		return new java.awt.event.MouseEvent(event.getComponent(), event.getID(), event.getWhen(), event.getModifiersEx(), x, y,
			event.getXOnScreen(), event.getYOnScreen(), event.getClickCount(), event.isPopupTrigger(), event.getButton());
	}

	/** The size of server packet {@code opcode}: an override if one is given, else the client's own. */
	public static int serverPacketSize(int opcode, int size) {
		return opcode >= 0 && opcode < PACKET_SIZES.length && PACKET_SIZES[opcode] != -3 ? PACKET_SIZES[opcode] : size;
	}

	private static double parseDouble(String value, double fallback) {
		if (value == null) {
			return fallback;
		}
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException ex) {
			return fallback;
		}
	}

	/**
	 * The inventory item's Drop entry among the menu entries, or null when the pointer is not over an
	 * item that can be dropped.
	 */
	public static MiniMenuEntry shiftDropEntry(Deque entries) {
		String drop = LocalizedText.aClass351_10.get(ClientState.language);
		for (Node node = entries.sentinel.previous; node != null && node != entries.sentinel; node = node.previous) {
			if (!(node instanceof MiniMenuEntry)) {
				continue;
			}
			MiniMenuEntry entry = (MiniMenuEntry) node;
			if ((entry.clickType == MiniMenuAction.IF_BUTTONX1 || entry.clickType == MiniMenuAction.IF_BUTTONX2) && entry.param1 >>> 16 == INVENTORY && entry.opBase != null && drop.equalsIgnoreCase(entry.opBase.replaceAll("<[^>]*>", "").trim())) {
				return entry;
			}
		}
		return null;
	}

	/** The id suffix for a menu entry, or an empty string when debug info is off. */
	public static String debugSuffix(int id) {
		return DEBUG ? " <col=00ff00>(</col>" + id + "<col=00ff00>)</col>" : "";
	}

	/** The id and component suffix for an interface menu entry, or an empty string. */
	public static String debugSuffix(int id, int component) {
		return DEBUG ? " <col=00ff00>(</col>" + id + " - " + component + "<col=00ff00>)</col>" : "";
	}

	/** The id and tile suffix for a world menu entry, or an empty string. */
	public static String debugSuffix(int id, int x, int y, int level) {
		return DEBUG ? " <col=00ff00>(</col>" + id + " - " + x + ", " + y + ", " + level + "<col=00ff00>)</col>" : "";
	}
}
