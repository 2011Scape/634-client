package com.jagex.game.runetek5.client.features;

import com.jagex.game.runetek5.ObjStackEntity;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.cache.node.ObjStackEntry;
import com.jagex.game.runetek5.cache.node.ObjStack;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.statics.StatBaseLevels;
import com.jagex.game.runetek5.config.rsinterface.MediaType;
import com.jagex.game.runetek5.entity.NPCEntityNode;
import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.vardomain.PlayerVarDomain;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.core.constants.MiniMenuAction;
import com.jagex.game.runetek5.client.menu.MiniMenuEntry;
import com.jagex.game.runetek5.client.menu.MiniMenu;
import com.jagex.game.runetek5.config.iftype.SubInterfaceType;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.config.rsinterface.ComponentType;
import com.jagex.game.runetek5.config.rsinterface.Component;
import com.jagex.game.runetek5.config.rsinterface.Interface;
import com.jagex.game.runetek5.config.rsinterface.SubInterface;
import com.jagex.game.runetek5.client.window.WindowModeType;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.core.constants.MainLogicStep;
import com.jagex.game.runetek5.entity.PathingEntitySpotAnimation;
import com.jagex.game.runetek5.entity.PlayerEntity;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.client.window.WindowMode;
import com.jagex.game.runetek5.OverlayManager;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.client.ClientState;

/**
 * Not part of the original client. With client.test=smoke the client logs itself in and exits
 * with a verdict, so a change can be checked without a person at the keyboard:
 *
 * - on the login screen it logs in as client.test.user (default smoketest) with
 *   client.test.password (default smoketest1), once;
 * - once in game, with a local player, it waits for client.test.frames frames (default 250) to be
 *   drawn, then reads the last one through the toolkit, saves it to client.test.shot, and checks
 *   it is not blank and, when client.test.reference names a directory holding a reference for this
 *   toolkit (smoke-toolkit<id>.png), that it is close to it. The frame is cut into 32 by 24 blocks.
 *   The mean difference of the blocks' average colours, out of 255, may be at most
 *   client.test.tolerance (default 12); it catches a missing scene or a moved camera. The mean
 *   difference of the blocks' contrast (the spread of brightness inside each) may be at most
 *   client.test.detailTolerance (default 6); it catches lost textures and detail. Two runs of an
 *   unchanged client differ by about 7 and 3;
 *   then it prints "SMOKE PASS ..." and exits 0;
 * - with client.test.command (for example "gfx level_up", which needs an admin account), it sends
 *   that command once the first frame passes, waits for a gfx command's graphic to arrive (up to
 *   150 frames; the server answers on its next tick), then client.test.commandFrames more frames
 *   (default 60), and checks the frame again, against smoke-toolkit<id>-<first word of the command>.png. For
 *   a gfx command, one of the local player's spot animation slots must also have taken the graphic
 *   and advanced its frames;
 * - if that has not happened within client.test.timeout seconds (default 180), or the client
 *   throws, it prints "SMOKE FAIL ..." with the step it reached and exits 1.
 *
 * - with client.test.toolkit (a ToolkitType id: 0 Java, 1 Java OpenGL, 2 native software,
 *   5 native OpenGL), it first switches to that toolkit once in game, through the client's own
 *   switch, and fails if the client does not end up on it; every check then runs on that toolkit,
 *   against its own references. At the end it switches back to the toolkit it started on, a saved
 *   preference, and waits for that to settle before exiting.
 *
 * It runs when the client is started with client.test=smoke against a local Void server.
 */
public final class SmokeTest {

	public static final boolean ENABLED = "smoke".equals(System.getProperty("client.test"));

	private static final String USER = System.getProperty("client.test.user", "smoketest");

	private static final String PASSWORD = System.getProperty("client.test.password", "smoketest1");

	private static final int FRAMES = Integer.getInteger("client.test.frames", 250);

	private static final long TIMEOUT = Integer.getInteger("client.test.timeout", 180) * 1000L;

	private static final String SHOT = System.getProperty("client.test.shot", "build/smoke/frame.png");

	private static final String REFERENCE = System.getProperty("client.test.reference", "");

	private static final int TOLERANCE = Integer.getInteger("client.test.tolerance", 12);

	private static final int DETAIL_TOLERANCE = Integer.getInteger("client.test.detailTolerance", 6);

	private static final int BLOCKS_X = 32;

	private static final int BLOCKS_Y = 24;

	private static final String COMMAND = System.getProperty("client.test.command", "");

	private static final int COMMAND_FRAMES = Integer.getInteger("client.test.commandFrames", 60);

	private static final String CHAT = System.getProperty("client.test.chat", "smoke test chat");

	private static final int CHAT_FRAMES = Integer.getInteger("client.test.chatFrames", 150);

	private static String firstImage = null;

	private static boolean spotModelBuilt = false;

	private static int chatFrame = -1;

	private static String chatSeen = null;

	private static int chatColour = -1;

	private static int chatTicksFirst = -1;

	private static int chatTicksLast = -1;

	private static boolean chatQueued = false;

	private static String lastText = null;

	private static final boolean WINDOW_MODES = !"false".equals(System.getProperty("client.test.windowModes"));

	private static final int WINDOW_FRAMES = 30;

	private static WindowModeType startMode = null;

	private static int windowFrame = -1;

	private static int windowStep = 0;

	private static final boolean SUB_INTERFACES = !"false".equals(System.getProperty("client.test.subInterfaces"));

	private static final int SUB_INTERFACE_FRAMES = 20;

	private static int subInterfaceStep = 0;

	private static int subInterfaceFrame = -1;

	private static long modalParent = -1L;

	private static int overlaysAtStart = 0;

	private static final boolean GROUND_ITEMS = !"false".equals(System.getProperty("client.test.groundItems"));

	private static final int COINS = 995;

	private static final int BRONZE_AXE = 1351;

	private static int groundStep = 0;

	private static int groundFrame = -1;

	private static boolean groundWasEnabled = false;

	private static int groundX = -1;

	private static int groundY = -1;

		private static final boolean NPC_LABELS = !"false".equals(System.getProperty("client.test.npcLabels"));

	/** The Tutorial Island guide, who stands beside the test account. */
	private static final int GUIDE_NPC = 945;

	private static final int NPC_LABEL_WAIT = 200;

	private static int npcLabelStep = 0;

	private static int npcLabelFrame = -1;

	private static boolean npcLabelsWereEnabled = false;

	private static int npcLabelStart = -1;

		private static final boolean XP_DROPS = !"false".equals(System.getProperty("client.test.xpDrops"));

	private static final int XP_WAIT = 300;

	private static int xpStep = 0;

	private static int xpFrame = -1;

	private static boolean xpWasEnabled = false;

	private static int xpDrawn = 0;

	private static int xpDrawnFrame = -1;

		private static final boolean MARKERS = !"false".equals(System.getProperty("client.test.markers"));

	private static final int MARKER_FRAMES = 40;

	private static int markerStep = 0;

	private static int markerFrame = -1;

	private static boolean markersWereEnabled = false;

		private static final boolean MEDIA = !"false".equals(System.getProperty("client.test.media"));

	private static final int MEDIA_FRAMES = 300;

	private static int mediaStep = 0;

	private static int mediaFrame = -1;

	private static String mediaBuilt = "";

		private static final boolean MENU_WALK = !"false".equals(System.getProperty("client.test.menuWalk"));

	private static final int MENU_WALK_FRAMES = 400;

	private static int menuWalkFrame = -1;

	private static int menuWalkStartX = -1;

	private static int menuWalkStartY = -1;

	private static int menuWalkStep = 0;

	private static int menuWalkHomeX = -1;

	private static int menuWalkHomeY = -1;

	private static int menuWalkAwayX = -1;

	private static int menuWalkAwayY = -1;

		private static final int TOOLKIT = Integer.getInteger("client.test.toolkit", -1);

	private static int startToolkit = -1;

	private static boolean toolkitSwitched = false;

	private static String passLine = null;

	private static int restoreFrame = -1;

	private static int commandFrame = -1;

	private static int spotSeen = -1;

	private static int spotSeenFrame = -1;

	private static final int COMMAND_WAIT = 150;

	private static int spotFrameMax = 0;

	private static final long START = System.currentTimeMillis();

	private static boolean loginSent = false;

	private static int highestStep = 0;

	private static int lastStep = -1;

	private static int gameFrames = 0;

	private static int frames = 0;

	private SmokeTest() {
		/* empty */
	}

	/**
	 * Called at the start of every logic tick.
	 */
	public static void tick() {
		if (!ENABLED) {
			return;
		}
		int step = ClientState.mainLogicStep.id();
		if (step != lastStep) {
			lastStep = step;
			highestStep = Math.max(highestStep, step);
			System.out.println("SMOKE step " + step + " at " + elapsed() + "s");
		}
		if (ClientState.mainLogicStep == MainLogicStep.STEP_LOGIN_SCREEN && !loginSent) {
			loginSent = true;
			System.out.println("SMOKE logging in as " + USER);
			LoginManager.lobbyLogin(USER, PASSWORD);
		}
		if (System.currentTimeMillis() - START > TIMEOUT) {
			fail("timed out after " + elapsed() + "s at step " + step + " (highest " + highestStep + "), " + frames + " frames drawn");
		}
	}

	/**
	 * Called after every frame is drawn.
	 */
	public static void frame() {
		if (!ENABLED) {
			return;
		}
		frames++;
		if (restoreFrame >= 0) {
			// switched back to the starting toolkit: let it settle, then report
			if (frames - restoreFrame >= 50) {
				System.out.println(passLine);
				System.out.flush();
				Runtime.getRuntime().halt(0);
			}
			return;
		}
		PlayerEntity player = PlayerUpdate.localPlayer;
		if (ClientState.mainLogicStep != MainLogicStep.STEP_GAME_SCREEN || player == null) {
			return;
		}
		if (TOOLKIT >= 0 && !toolkitSwitched) {
			toolkitSwitched = true;
			startToolkit = GamePreferences.uid.toolkit.getValue();
			if (startToolkit != TOOLKIT) {
				System.out.println("SMOKE switching from toolkit " + startToolkit + " to " + TOOLKIT);
				switchToolkit(TOOLKIT);
				if (GamePreferences.uid.toolkit.getValue() != TOOLKIT) {
					fail("toolkit " + TOOLKIT + " could not start; the client is on toolkit " + GamePreferences.uid.toolkit.getValue());
				}
				System.out.println("SMOKE now on toolkit " + TOOLKIT + " (" + ClientState.activeToolkit.getClass().getSimpleName() + ")");
				gameFrames = 0;
				return;
			}
		}
		if (++gameFrames < FRAMES) {
			return;
		}
		if (firstImage == null) {
			firstImage = checkImage("", SHOT);
			if (COMMAND.length() > 0) {
				System.out.println("SMOKE sending ::" + COMMAND);
				sendCommand(COMMAND);
				commandFrame = gameFrames;
			}
		}
		if (commandFrame >= 0) {
			PathingEntitySpotAnimation[] slots = { player.primarySpotAnimation, player.secondarySpotAnimation };
			for (int i = 0; i < slots.length; i++) {
				if (slots[i].spotAnimationId != -1) {
					if (spotSeen == -1) {
						System.out.println("SMOKE " + (i == 0 ? "primary" : "secondary") + " spot animation slot took graphic " + slots[i].spotAnimationId);
					}
					spotSeen = slots[i].spotAnimationId;
					spotFrameMax = Math.max(spotFrameMax, slots[i].currentFrameId);
				}
			}
			// the server answers on its next tick, up to 600 ms away: count from when the graphic arrives
			if (spotSeen != -1 && spotSeenFrame < 0) {
				spotSeenFrame = gameFrames;
			}
			boolean gfx = COMMAND.split(" ")[0].equals("gfx");
			if (gfx && spotSeen == -1 && gameFrames - commandFrame < COMMAND_WAIT) {
				return;
			}
			if (gameFrames - (spotSeenFrame >= 0 ? spotSeenFrame : commandFrame) < COMMAND_FRAMES) {
				return;
			}
			String word = COMMAND.split(" ")[0];
			if (word.equals("gfx") && (spotSeen == -1 || spotFrameMax == 0)) {
				fail("::" + COMMAND + " did not play: neither spot animation slot " + (spotSeen == -1 ? "took a graphic" : "took graphic " + spotSeen + " but never advanced its frame"));
			}
			if (word.equals("gfx") && !spotModelBuilt) {
				fail("::" + COMMAND + " played in the spot animation slot, but its model was never drawn");
			}
			if (chatFrame < 0) {
				String second = checkImage("-" + word, SHOT.replace(".png", "-" + word + ".png"));
				firstImage += "; after ::" + COMMAND + (spotSeen == -1 ? "" : " (spot animation " + spotSeen + ", frame up to " + spotFrameMax + ", model built)") + ": " + second;
			}
		}
		if (CHAT.length() > 0) {
			if (chatFrame < 0) {
				System.out.println("SMOKE forcing overhead chat \"" + CHAT + "\"");
				sendCommand("chat \"" + CHAT + "\"");
				chatFrame = gameFrames;
				commandFrame = -1;
				return;
			}
			String expected = CHAT;
			if (player.hintDetails.text != null && !player.hintDetails.text.equals(lastText)) {
				lastText = player.hintDetails.text;
				System.out.println("SMOKE overhead chat now \"" + lastText + "\"");
			}
			if (expected.equalsIgnoreCase(player.hintDetails.text)) {
				if (chatSeen == null) {
					System.out.println("SMOKE overhead chat took \"" + player.hintDetails.text + "\", colour " + player.hintDetails.colorType + ", " + player.hintDetails.ticksLeft + " ticks");
					chatTicksFirst = player.hintDetails.ticksLeft;
				}
				chatSeen = player.hintDetails.text;
				chatColour = player.hintDetails.colorType;
				chatTicksLast = player.hintDetails.ticksLeft;
				for (int i = 0; i < OverlayManager.overheadTextCount; i++) {
					if (expected.equalsIgnoreCase(OverlayManager.OVERHEAD_TEXTS[i])) {
						chatQueued = true;
					}
				}
			}
			if (gameFrames - chatFrame < CHAT_FRAMES) {
				return;
			}
			if (chatSeen == null) {
				fail("the overhead chat never took \"" + expected + "\"");
			}
			if (chatTicksLast >= chatTicksFirst) {
				fail("the overhead chat's ticks did not count down (" + chatTicksFirst + " to " + chatTicksLast + ")");
			}
			if (!chatQueued) {
				fail("the overhead chat never reached the overlay's text queue");
			}
			if (!firstImage.contains("; overhead chat")) {
				firstImage += "; overhead chat \"" + chatSeen + "\" colour " + chatColour + ", ticks " + chatTicksFirst + " to " + chatTicksLast + ", drawn";
			}
		}
		if (WINDOW_MODES && !windowModesDone()) {
			return;
		}
		if (SUB_INTERFACES && !subInterfacesDone()) {
			return;
		}
		if (MENU_WALK && !menuWalkDone(player)) {
			return;
		}
		if (MEDIA && !mediaDone(player)) {
			return;
		}
		if (MARKERS && !markersDone(player)) {
			return;
		}
		if (XP_DROPS && !xpDropsDone()) {
			return;
		}
		if (NPC_LABELS && !npcLabelsDone()) {
			return;
		}
		if (GROUND_ITEMS && !groundItemsDone(player)) {
			return;
		}
		passLine = "SMOKE PASS in game as " + USER + " at scene tile " + player.movementBaseX[0] + "," + player.movementBaseY[0] + " after " + elapsed() + "s, " + gameFrames + " frames in game, " + frames + " in all; " + firstImage;
		if (toolkitSwitched && startToolkit != TOOLKIT) {
			System.out.println("SMOKE switching back to toolkit " + startToolkit);
			switchToolkit(startToolkit);
			restoreFrame = frames;
			return;
		}
		System.out.println(passLine);
		System.out.flush();
		Runtime.getRuntime().halt(0);
	}

	/**
	 * Reads the frame just drawn, saves it, and fails the test if it is blank or too far from the
	 * reference. Returns a summary for the pass line.
	 */
	private static String checkImage(String suffix, String shotPath) {
		Toolkit toolkit = ClientState.activeToolkit;
		int[] size = new int[2];
		toolkit.accumulateViewportDimensions(size);
		int width = size[0];
		int height = size[1];
		int[] pixels = toolkit.na(0, 0, width, height);
		BufferedImage shot = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		shot.setRGB(0, 0, width, height, pixels, 0, width);
		int toolkitId = GamePreferences.uid.toolkit.getValue();
		try {
			File file = new File(shotPath);
			if (file.getParentFile() != null) {
				file.getParentFile().mkdirs();
			}
			ImageIO.write(shot, "png", file);
		} catch (Exception ex) {
			fail("cannot save " + SHOT + ": " + ex);
		}
		double[] blocks = blocks(shot);
		double detail = spread(blocks);
		if (detail < 8.0D) {
			fail("the frame is blank: block brightness varies by " + format(detail) + " (toolkit " + toolkitId + ", " + width + "x" + height + ", saved to " + shotPath + ")");
		}
		String summary = "toolkit " + toolkitId + ", " + width + "x" + height + ", detail " + format(detail);
		if (REFERENCE.length() == 0) {
			return summary + ", no reference given";
		}
		// a check may keep several references taken in different runs (smoke-toolkit<id><suffix>.png,
		// then .2.png, .3.png, ...): the guide wanders, and a frame need only be close to one of them
		File reference = null;
		double difference = Double.MAX_VALUE;
		double detailDifference = Double.MAX_VALUE;
		double[] shotContrast = contrast(shot);
		for (int n = 1; ; n++) {
			File candidate = new File(REFERENCE, "smoke-toolkit" + toolkitId + suffix + (n == 1 ? "" : "." + n) + ".png");
			if (!candidate.isFile()) {
				break;
			}
			BufferedImage expected;
			try {
				expected = ImageIO.read(candidate);
			} catch (Exception ex) {
				fail("cannot read " + candidate + ": " + ex);
				return null;
			}
			double colour = difference(blocks, blocks(expected));
			double contrastDifference = difference(shotContrast, contrast(expected));
			if (Math.max(colour / TOLERANCE, contrastDifference / DETAIL_TOLERANCE) < Math.max(difference / TOLERANCE, detailDifference / DETAIL_TOLERANCE)) {
				reference = candidate;
				difference = colour;
				detailDifference = contrastDifference;
			}
		}
		if (reference == null) {
			return summary + ", no reference at " + new File(REFERENCE, "smoke-toolkit" + toolkitId + suffix + ".png");
		}
		if (difference > TOLERANCE || detailDifference > DETAIL_TOLERANCE) {
			fail("the frame differs from its closest reference, " + reference + ": colour by " + format(difference) + " (at most " + TOLERANCE + "), detail by " + format(detailDifference) + " (at most " + DETAIL_TOLERANCE + "); " + summary + ", saved to " + shotPath);
		}
		return summary + ", from the reference " + reference.getName() + ": colour " + format(difference) + ", detail " + format(detailDifference);
	}

	/**
	 * The mean red, green and blue of each of BLOCKS_X by BLOCKS_Y blocks, row by row.
	 */
	private static double[] blocks(BufferedImage image) {
		double[] out = new double[BLOCKS_X * BLOCKS_Y * 3];
		int width = image.getWidth();
		int height = image.getHeight();
		for (int by = 0; by < BLOCKS_Y; by++) {
			for (int bx = 0; bx < BLOCKS_X; bx++) {
				int x0 = bx * width / BLOCKS_X;
				int x1 = (bx + 1) * width / BLOCKS_X;
				int y0 = by * height / BLOCKS_Y;
				int y1 = (by + 1) * height / BLOCKS_Y;
				long red = 0L;
				long green = 0L;
				long blue = 0L;
				int count = 0;
				for (int y = y0; y < y1; y++) {
					for (int x = x0; x < x1; x++) {
						int rgb = image.getRGB(x, y);
						red += rgb >> 16 & 0xFF;
						green += rgb >> 8 & 0xFF;
						blue += rgb & 0xFF;
						count++;
					}
				}
				int at = (by * BLOCKS_X + bx) * 3;
				if (count > 0) {
					out[at] = (double) red / count;
					out[at + 1] = (double) green / count;
					out[at + 2] = (double) blue / count;
				}
			}
		}
		return out;
	}

	/**
	 * The spread of brightness inside each of BLOCKS_X by BLOCKS_Y blocks, row by row: textures and
	 * edges raise it, blur and blank areas lower it.
	 */
	private static double[] contrast(BufferedImage image) {
		double[] out = new double[BLOCKS_X * BLOCKS_Y];
		int width = image.getWidth();
		int height = image.getHeight();
		for (int by = 0; by < BLOCKS_Y; by++) {
			for (int bx = 0; bx < BLOCKS_X; bx++) {
				double sum = 0.0D;
				double squares = 0.0D;
				int count = 0;
				for (int y = by * height / BLOCKS_Y; y < (by + 1) * height / BLOCKS_Y; y++) {
					for (int x = bx * width / BLOCKS_X; x < (bx + 1) * width / BLOCKS_X; x++) {
						int rgb = image.getRGB(x, y);
						double brightness = ((rgb >> 16 & 0xFF) * 299 + (rgb >> 8 & 0xFF) * 587 + (rgb & 0xFF) * 114) / 1000.0D;
						sum += brightness;
						squares += brightness * brightness;
						count++;
					}
				}
				if (count > 0) {
					double mean = sum / count;
					out[by * BLOCKS_X + bx] = Math.sqrt(Math.max(0.0D, squares / count - mean * mean));
				}
			}
		}
		return out;
	}

	/**
	 * The standard deviation of the blocks' brightness: near 0 for a blank frame.
	 */
	private static double spread(double[] blocks) {
		int n = blocks.length / 3;
		double sum = 0.0D;
		double squares = 0.0D;
		for (int i = 0; i < n; i++) {
			double brightness = (blocks[i * 3] + blocks[i * 3 + 1] + blocks[i * 3 + 2]) / 3.0D;
			sum += brightness;
			squares += brightness * brightness;
		}
		double mean = sum / n;
		return Math.sqrt(Math.max(0.0D, squares / n - mean * mean));
	}

	private static double difference(double[] a, double[] b) {
		double total = 0.0D;
		for (int i = 0; i < a.length; i++) {
			total += Math.abs(a[i] - b[i]);
		}
		return total / a.length;
	}

	/**
	 * Runs a console command as typed, through ClientCommandProcessor.executeCommand, which is in the root package
	 * and so out of reach of a direct call from here.
	 */
	private static void sendCommand(String command) {
		try {
			Class.forName("ClientCommandProcessor").getMethod("executeCommand", String.class, boolean.class, boolean.class).invoke(null, command, Boolean.FALSE, Boolean.FALSE);
		} catch (Exception ex) {
			fail("cannot send ::" + command + ": " + ex);
		}
	}

	/**
	 * Switches toolkit through ToolkitSwitch.loadAssets, the client's own switch (in the root package,
	 * so out of reach of a direct call from here).
	 */
	private static void switchToolkit(int toolkit) {
		try {
			Class.forName("ToolkitSwitch").getMethod("loadAssets", boolean.class, int.class).invoke(null, Boolean.FALSE, Integer.valueOf(toolkit));
		} catch (Exception ex) {
			fail("cannot switch to toolkit " + toolkit + ": " + ex);
		}
	}

	private static String format(double value) {
		return String.valueOf(Math.round(value * 10.0D) / 10.0D);
	}

	/**
	 * Steps through the sub-interface check, a frame at a time: the overlays the server opened at login
	 * are there, a modal opened into a free container of the root interface draws, and closing modals
	 * removes it and leaves the overlays. True once it is done.
	 */
	private static boolean subInterfacesDone() {
		if (subInterfaceStep == 0) {
			int overlay = -1;
			overlaysAtStart = 0;
			for (SubInterface sub = (SubInterface) InterfaceManager.OPEN_INTERFACES.first(); sub != null; sub = (SubInterface) InterfaceManager.OPEN_INTERFACES.next()) {
				if (sub.openType == SubInterfaceType.OVERLAY) {
					overlaysAtStart++;
					overlay = sub.interfaceId;
				}
			}
			if (overlaysAtStart == 0) {
				fail("no overlay sub-interface is open after login");
			}
			Component[] root = Interface.interfaces[ClientState.BASE_WINDOW_ID];
			for (int i = 0; root != null && i < root.length && modalParent == -1L; i++) {
				Component c = root[i];
				if (c != null && c.type == ComponentType.CONTAINER && c.parentId != -1 && InterfaceManager.OPEN_INTERFACES.get((long) c.idHash) == null) {
					modalParent = c.idHash;
				}
			}
			if (modalParent == -1L) {
				fail("the root interface " + ClientState.BASE_WINDOW_ID + " has no free container to open a modal into");
			}
			openSubInterface(overlay, (int) modalParent, SubInterfaceType.MODAL);
			subInterfaceFrame = frames;
			subInterfaceStep = 1;
			return false;
		}
		if (frames - subInterfaceFrame < SUB_INTERFACE_FRAMES) {
			return false;
		}
		if (subInterfaceStep == 1) {
			SubInterface modal = (SubInterface) InterfaceManager.OPEN_INTERFACES.get(modalParent);
			if (modal == null || modal.openType != SubInterfaceType.MODAL) {
				fail("the modal opened into " + modalParent + " is " + (modal == null ? "not open" : "open as " + modal.openType));
			}
			closeModals();
			if (InterfaceManager.OPEN_INTERFACES.get(modalParent) != null) {
				fail("closing modals left the modal in " + modalParent + " open");
			}
			subInterfaceFrame = frames;
			subInterfaceStep = 2;
			return false;
		}
		int overlays = 0;
		for (SubInterface sub = (SubInterface) InterfaceManager.OPEN_INTERFACES.first(); sub != null; sub = (SubInterface) InterfaceManager.OPEN_INTERFACES.next()) {
			if (sub.openType == SubInterfaceType.OVERLAY) {
				overlays++;
			}
		}
		if (overlays != overlaysAtStart) {
			fail("closing modals changed the overlays open from " + overlaysAtStart + " to " + overlays);
		}
		firstImage += "; modal opened into " + (modalParent >> 16) + ":" + (modalParent & 0xFFFF) + " and closed, " + overlays + " overlays kept";
		return true;
	}

	/**
	 * Steps through the mini-menu check: adds a "Walk here" entry for the tile two squares east of the
	 * player and runs it the way a click does (MiniMenuActionExecutor.processMenuClick), requires the
	 * player to leave its tile, then walks it back the same way and requires it home again. The
	 * account's position is saved by the server, so the next run starts where this one did. Home is
	 * the starting tile, or the scene tile client.test.menuWalkHome ("x,y") to put a moved account
	 * back. True once it is done.
	 */
	private static boolean menuWalkDone(PlayerEntity player) {
		int x = player.movementBaseX[0];
		int y = player.movementBaseY[0];
		if (menuWalkStep == 0) {
			menuWalkStartX = x;
			menuWalkStartY = y;
			menuWalkHomeX = x;
			menuWalkHomeY = y;
			String home = System.getProperty("client.test.menuWalkHome", "");
			if (home.length() > 0) {
				menuWalkHomeX = Integer.parseInt(home.split(",")[0].trim());
				menuWalkHomeY = Integer.parseInt(home.split(",")[1].trim());
			}
			walkFromMenu(x + 2, y);
			menuWalkStep = 1;
			return false;
		}
		if (menuWalkStep == 1) {
			if (x == menuWalkStartX && y == menuWalkStartY) {
				if (frames - menuWalkFrame >= MENU_WALK_FRAMES) {
					fail("the player never left scene tile " + menuWalkStartX + "," + menuWalkStartY + " after a walk from the mini menu");
				}
				return false;
			}
			menuWalkAwayX = x;
			menuWalkAwayY = y;
			walkFromMenu(menuWalkHomeX, menuWalkHomeY);
			menuWalkStep = 2;
			return false;
		}
		if (x != menuWalkHomeX || y != menuWalkHomeY) {
			if (frames - menuWalkFrame >= MENU_WALK_FRAMES) {
				fail("the player never got back to scene tile " + menuWalkHomeX + "," + menuWalkHomeY + " (it is at " + x + "," + y + ")");
			}
			return false;
		}
		firstImage += "; walked from the mini menu to " + menuWalkAwayX + "," + menuWalkAwayY + " and back to " + x + "," + y;
		return true;
	}

	/**
	 * Steps through the ground item label check: turns the labels on and puts a pile of 1000 coins
	 * and a bronze axe on the tile east of the player the way the server's ground item packet does
	 * (ObjStack.addGroundItem, then ObjStackEntity.refreshObjStack), client side only. It requires a
	 * label whose bottom line is "Coins (1000)" (the pile lists its most valuable item first) inside
	 * the frame, checks that frame against its own reference, and takes the pile away the way the
	 * server's removal does. True once it is done.
	 */
	private static boolean groundItemsDone(PlayerEntity player) {
		if (groundStep == 3) {
			return true;
		}
		int level = player.level;
		if (groundStep == 0) {
			groundWasEnabled = GroundItemLabels.enabled;
			GroundItemLabels.enabled = true;
			groundX = player.movementBaseX[0] + 1;
			groundY = player.movementBaseY[0];
			ObjStack.addGroundItem(new ObjStackEntry(COINS, 1000), level, MapRegion.sceneBaseY + groundY, MapRegion.sceneBaseX + groundX);
			ObjStack.addGroundItem(new ObjStackEntry(BRONZE_AXE, 1), level, MapRegion.sceneBaseY + groundY, MapRegion.sceneBaseX + groundX);
			ObjStackEntity.refreshObjStack(groundY, groundX, level);
			groundFrame = frames;
			groundStep = 1;
			return false;
		}
		int label = -1;
		for (int i = 0; i < GroundItemLabels.drawnLastFrame; i++) {
			if ("Coins (1000)".equals(GroundItemLabels.lastTexts[i])) {
				label = i;
			}
		}
		if (groundStep == 1) {
			if (label == -1) {
				if (frames - groundFrame >= 200) {
					fail("no frame labelled the pile on scene tile " + groundX + "," + groundY + " \"Coins (1000)\" (" + GroundItemLabels.drawnLastFrame + " piles labelled" + (GroundItemLabels.drawnLastFrame > 0 ? ", the first \"" + GroundItemLabels.lastTexts[0] + "\"" : "") + ")");
				}
				return false;
			}
			// the screen shows a frame a little after it is drawn: let the label be on it before the shot
			groundFrame = frames;
			groundStep = 2;
			return false;
		}
		if (frames - groundFrame < 10) {
			return false;
		}
		if (label == -1) {
			groundStep = 1;
			return false;
		}
		int[] size = new int[2];
		ClientState.activeToolkit.accumulateViewportDimensions(size);
		int x = GroundItemLabels.lastX[label];
		int y = GroundItemLabels.lastY[label];
		if (x < 0 || y < 0 || x >= size[0] || y >= size[1]) {
			fail("the pile's label is at " + x + "," + y + ", outside the " + size[0] + "x" + size[1] + " frame");
		}
		String image = checkImage("-grounditems", SHOT.replace(".png", "-grounditems.png"));
		ObjStack pile = (ObjStack) ObjStack.GROUND_ITEMS.get((long) (MapRegion.sceneBaseY + groundY << 14 | level << 28 | MapRegion.sceneBaseX + groundX));
		if (pile != null) {
			pile.unlink();
		}
		ObjStackEntity.refreshObjStack(groundY, groundX, level);
		GroundItemLabels.enabled = groundWasEnabled;
		firstImage += "; ground items labelled \"Coins (1000)\" at " + x + "," + y + ": " + image;
		groundStep = 3;
		return true;
	}

	/**
	 * Steps through the npc label check: turns the labels on and requires, within NPC_LABEL_WAIT
	 * frames, a label for the Tutorial Island guide at a point inside the frame. The frame with the
	 * labels is saved for a look but never compared: the guide walks. True once it is done.
	 */
	private static boolean npcLabelsDone() {
		if (npcLabelStep == 3) {
			return true;
		}
		if (npcLabelStep == 0) {
			npcLabelStart = frames;
			npcLabelsWereEnabled = NpcLabels.enabled;
			NpcLabels.enabled = true;
			npcLabelFrame = frames;
			npcLabelStep = 1;
			return false;
		}
		int guide = -1;
		for (int i = 0; i < NpcLabels.drawnLastFrame; i++) {
			if (NpcLabels.lastTypes[i] == GUIDE_NPC) {
				guide = i;
			}
		}
		if (npcLabelStep == 1) {
			if (guide == -1) {
				if (frames - npcLabelFrame >= NPC_LABEL_WAIT) {
					fail("no frame labelled the guide (npc " + GUIDE_NPC + "); the last frame labelled " + NpcLabels.drawnLastFrame + " npcs");
				}
				return false;
			}
			// the screen shows a frame a little after it is drawn: let the labels be on it before the shot
			npcLabelFrame = frames;
			npcLabelStep = 2;
			return false;
		}
		if (frames - npcLabelFrame < 10) {
			return false;
		}
		if (guide == -1) {
			// the guide walked out of view while the screen caught up: wait for him again
			if (frames - npcLabelStart >= NPC_LABEL_WAIT * 3) {
				fail("the guide's label kept going away");
			}
			npcLabelStep = 1;
			return false;
		}
		int x = NpcLabels.lastX[guide];
		int y = NpcLabels.lastY[guide];
		int[] size = new int[2];
		ClientState.activeToolkit.accumulateViewportDimensions(size);
		if (x < 0 || y < 0 || x >= size[0] || y >= size[1]) {
			fail("the guide's label is at " + x + "," + y + ", outside the " + size[0] + "x" + size[1] + " frame");
		}
		checkImage("-npclabels", SHOT.replace(".png", "-npclabels.png"));
		NpcLabels.enabled = npcLabelsWereEnabled;
		firstImage += "; " + NpcLabels.drawnLastFrame + " npc labels, the guide's \"" + NpcLabels.label(TypeLists.NPC.list(GUIDE_NPC)) + "\" at " + x + "," + y;
		npcLabelStep = 3;
		return true;
	}

	/**
	 * Steps through the experience drop check: logging in must have made no drops. Void sends no
	 * stats at login, so it first sets the test account's Attack to 1 with its admin command
	 * ::level (the update only primes the skill), then to 2, which must make a "+83 Attack" drop
	 * that frames draw, and then back to 1. True once it is done.
	 */
	private static boolean xpDropsDone() {
		int level = StatBaseLevels.statBaseLevels[0];
		if (xpStep == 0) {
			if (XpDrops.dropCount() != 0 || XpDrops.lastDropText != null) {
				fail("logging in made experience drops (" + XpDrops.lastDropText + ")");
			}
			xpWasEnabled = XpDrops.enabled;
			XpDrops.enabled = true;
			sendCommand("level attack 1");
			xpFrame = frames;
			xpStep = 1;
			return false;
		}
		if (xpStep == 1) {
			if (level != 1) {
				if (frames - xpFrame >= XP_WAIT) {
					fail("::level attack 1 did not reach the client (Attack is " + level + ")");
				}
				return false;
			}
			if (XpDrops.lastDropText != null) {
				fail("setting Attack to 1 made the drop \"" + XpDrops.lastDropText + "\"");
			}
			System.out.println("SMOKE raising Attack from 1 to 2");
			sendCommand("level attack 2");
			xpFrame = frames;
			xpStep = 2;
			return false;
		}
		if (xpStep == 2) {
			if (XpDrops.drawnLastFrame > 0) {
				xpDrawn = Math.max(xpDrawn, XpDrops.drawnLastFrame);
			}
			if (XpDrops.lastDropText == null || xpDrawn == 0) {
				if (frames - xpFrame >= XP_WAIT) {
					fail("raising Attack made " + (XpDrops.lastDropText == null ? "no experience drop" : "the drop \"" + XpDrops.lastDropText + "\" but no frame drew it"));
				}
				return false;
			}
			// the screen shows a frame a little after it is drawn: let the drop be on it before the shot
			if (xpDrawnFrame < 0) {
				xpDrawnFrame = frames;
			}
			if (frames - xpDrawnFrame < 10) {
				return false;
			}
			if (!"+83 Attack".equals(XpDrops.lastDropText)) {
				fail("the experience drop reads \"" + XpDrops.lastDropText + "\", not \"+83 Attack\"");
			}
			// saved for a look, never compared: the drop rises and fades, so no two runs catch the same frame
			checkImage("-xpdrop", SHOT.replace(".png", "-xpdrop.png"));
			System.out.println("SMOKE experience drop \"" + XpDrops.lastDropText + "\" drawn; setting Attack back to 1");
			sendCommand("level attack 1");
			xpFrame = frames;
			xpStep = 3;
			return false;
		}
		if (xpStep == 3) {
			if (level != 1) {
				if (frames - xpFrame >= XP_WAIT) {
					fail("Attack did not go back to 1 (it is " + level + ")");
				}
				return false;
			}
			XpDrops.enabled = xpWasEnabled;
			firstImage += "; experience drop \"" + XpDrops.lastDropText + "\" drawn, Attack back to 1";
			xpStep = 4;
		}
		return true;
	}

	/**
	 * Steps through the tile marker check: turns the markers on, marks the tile east of the player
	 * through its mini menu entry, and after MARKER_FRAMES frames requires one marker that the last
	 * frame drew, with the marker's yellow on the frame at the middle of each of its edges. Then it unmarks the tile the same
	 * way, requires no markers, and puts the switch back. True once it is done.
	 */
	private static boolean markersDone(PlayerEntity player) {
		int x = player.movementBaseX[0] + 1;
		int y = player.movementBaseY[0];
		if (markerStep == 0) {
			markersWereEnabled = TileMarkers.enabled;
			TileMarkers.enabled = true;
			runMenuEntry(MiniMenuAction.TILE_MARKER, x, y, "Mark tile");
			if (TileMarkers.markerCount() != 1 || !TileMarkers.isMarked(x, y)) {
				fail("the Mark tile entry did not mark scene tile " + x + "," + y + " (" + TileMarkers.markerCount() + " markers)");
			}
			markerFrame = frames;
			markerStep = 1;
			return false;
		}
		if (frames - markerFrame < MARKER_FRAMES) {
			return false;
		}
		if (markerStep == 1) {
			if (TileMarkers.drawnLastFrame != 1) {
				fail("the marked tile " + x + "," + y + " was not drawn (" + TileMarkers.drawnLastFrame + " markers drawn in the last frame)");
			}
			// saved for a look; the check is the outline's own pixels, not the whole frame, which the
			// walking guide changes from run to run
			checkImage("-tilemarker", SHOT.replace(".png", "-tilemarker.png"));
			String image = markerPixels();
			runMenuEntry(MiniMenuAction.TILE_MARKER, x, y, "Unmark tile");
			if (TileMarkers.markerCount() != 0) {
				fail("the Unmark tile entry left " + TileMarkers.markerCount() + " markers");
			}
			TileMarkers.enabled = markersWereEnabled;
			firstImage += "; tile " + x + "," + y + " marked, drawn and unmarked: " + image;
			markerStep = 2;
		}
		return true;
	}

	/**
	 * Reads the frame around the middle of each edge of the last marker drawn and requires the
	 * marker's yellow within a few pixels of it. Returns a summary for the pass line.
	 */
	private static String markerPixels() {
		int[] size = new int[2];
		ClientState.activeToolkit.accumulateViewportDimensions(size);
		int[] pixels = ClientState.activeToolkit.na(0, 0, size[0], size[1]);
		int found = 0;
		for (int edge = 0; edge < 4; edge++) {
			int next = (edge + 1) % 4;
			int midX = (TileMarkers.lastCorners[edge * 2] + TileMarkers.lastCorners[next * 2]) / 2;
			int midY = (TileMarkers.lastCorners[edge * 2 + 1] + TileMarkers.lastCorners[next * 2 + 1]) / 2;
			boolean yellow = false;
			for (int dy = -3; dy <= 3 && !yellow; dy++) {
				for (int dx = -3; dx <= 3 && !yellow; dx++) {
					int x = midX + dx;
					int y = midY + dy;
					if (x >= 0 && y >= 0 && x < size[0] && y < size[1]) {
						int rgb = pixels[y * size[0] + x];
						yellow = (rgb >> 16 & 0xFF) > 180 && (rgb >> 8 & 0xFF) > 180 && (rgb & 0xFF) < 100;
					}
				}
			}
			if (!yellow) {
				fail("no marker yellow in the frame near " + midX + "," + midY + ", the middle of the marked tile's edge " + edge);
			}
			found++;
		}
		return "yellow on all " + found + " edges";
	}

	/**
	 * Steps through the component media check: a scratch component takes each kind of media
	 * getAnimatedMediaModel builds (a raw model, the local player's head, an item, an npc's body and,
	 * if a nearby npc has one, its head) and must get a model for it within MEDIA_FRAMES frames,
	 * while the cache loads what it needs. True once it is done.
	 */
	private static boolean mediaDone(PlayerEntity player) {
		int[][] cases = mediaCases();
		if (mediaStep >= cases.length) {
			firstImage += "; component media built: " + mediaBuilt.substring(2);
			return true;
		}
		if (mediaFrame < 0) {
			mediaFrame = frames;
		}
		int[] media = cases[mediaStep];
		if (media == null) {
			mediaStep++;
			mediaFrame = -1;
			return false;
		}
		Component component = new Component();
		component.mediaType = MediaType.fromId(media[0]);
		component.primaryMediaId = media[1];
		Model model = component.getAnimatedMediaModel(TypeLists.IDK, TypeLists.SEQ, 2048, ClientState.activeToolkit, -1, 0, TypeLists.OBJ, PlayerVarDomain.playerVarProvider, player.playerModel, null, TypeLists.BAS, TypeLists.NPC, -1);
		if (model != null) {
			mediaBuilt += ", type " + media[0] + " (" + media[1] + ")";
			mediaStep++;
			mediaFrame = -1;
			return false;
		}
		if (frames - mediaFrame >= MEDIA_FRAMES) {
			fail("a component with media type " + media[0] + " and id " + media[1] + " never got a model" + (Component.missingMedia ? " (still loading)" : ""));
		}
		return false;
	}

	/**
	 * The media the check builds, as { type, id }: a null entry is skipped (no nearby npc).
	 */
	private static int[][] mediaCases() {
		int npc = -1;
		for (int i = 0; i < NPCList.npcCount && npc == -1; i++) {
			NPCEntityNode node = NPCList.NPC_ARRAY[i];
			if (node != null && node.npc != null && node.npc.npcType != null) {
				npc = node.npc.npcType.id;
			}
		}
		return new int[][] { { 1, 0 }, { 3, -1 }, { 4, 995 }, npc == -1 ? null : new int[] { 6, npc } };
	}

	/**
	 * Adds a "Walk here" entry for a scene tile to the mini menu and runs it through
	 * MiniMenuActionExecutor.processMenuClick, in the root package.
	 */
	private static void walkFromMenu(int tileX, int tileY) {
		runMenuEntry(MiniMenuAction.WALK, tileX, tileY, MiniMenu.walkHereText);
		System.out.println("SMOKE walking from the mini menu to scene tile " + tileX + "," + tileY);
		menuWalkFrame = frames;
	}

	/**
	 * Adds a tile entry of the given click type to the mini menu and runs it through
	 * MiniMenuActionExecutor.processMenuClick, in the root package, as a click does.
	 */
	private static void runMenuEntry(int clickType, int tileX, int tileY, String op) {
		MiniMenu.addEntry(false, "", tileY, true, tileX, -1, true, clickType, (long) (tileY | tileX << 0), op, 0L, -1);
		MiniMenuEntry found = null;
		for (MiniMenuEntry entry = (MiniMenuEntry) MiniMenu.entryList.first(); entry != null; entry = (MiniMenuEntry) MiniMenu.entryList.next()) {
			if (entry.clickType == clickType && entry.param0 == tileX && entry.param1 == tileY) {
				found = entry;
			}
		}
		if (found == null) {
			fail("the \"" + op + "\" entry added to the mini menu is not in its entry list");
		}
		try {
			Class.forName("MiniMenuActionExecutor").getMethod("processMenuClick", MiniMenuEntry.class, int.class, int.class).invoke(null, found, Integer.valueOf(0), Integer.valueOf(0));
		} catch (Exception ex) {
			fail("cannot run the \"" + op + "\" entry: " + ex);
		}
	}

	/**
	 * Opens a sub-interface through SubInterfaceOpener.openSubInterface (in the root package, so out of
	 * reach of a direct call from here), without its load scripts.
	 */
	private static void openSubInterface(int interfaceId, int parentHash, SubInterfaceType openType) {
		try {
			Class.forName("SubInterfaceOpener").getMethod("openSubInterface", int.class, int.class, SubInterfaceType.class, boolean.class).invoke(null, Integer.valueOf(interfaceId), Integer.valueOf(parentHash), openType, Boolean.TRUE);
		} catch (Exception ex) {
			fail("cannot open interface " + interfaceId + ": " + ex);
		}
	}

	/**
	 * Closes the open modals through ModalInterfaceClose.closeInterface, in the root package.
	 */
	private static void closeModals() {
		try {
			Class.forName("ModalInterfaceClose").getMethod("closeInterface").invoke(null);
		} catch (Exception ex) {
			fail("cannot close the modals: " + ex);
		}
	}

	/**
	 * Steps through the window mode check, a frame at a time. True once it is done.
	 */
	private static boolean windowModesDone() {
		if (windowStep == 0) {
			startMode = WindowMode.getWindowMode();
			if (startMode == WindowModeType.FULLSCREEN) {
				firstImage += "; window modes skipped (full screen)";
				return true;
			}
			WindowModeType other = startMode == WindowModeType.FIXED ? WindowModeType.RESIZABLE : WindowModeType.FIXED;
			System.out.println("SMOKE window mode " + startMode + ", switching to " + other);
			sendCommand(other == WindowModeType.FIXED ? "wm1" : "wm2");
			if (WindowMode.getWindowMode() != other) {
				fail("the window mode is " + WindowMode.getWindowMode() + " after switching to " + other);
			}
			windowFrame = frames;
			windowStep = 1;
			return false;
		}
		if (frames - windowFrame < WINDOW_FRAMES) {
			return false;
		}
		if (windowStep == 1) {
			System.out.println("SMOKE drew " + (frames - windowFrame) + " frames in " + WindowMode.getWindowMode() + ", switching back to " + startMode);
			sendCommand(startMode == WindowModeType.FIXED ? "wm1" : "wm2");
			if (WindowMode.getWindowMode() != startMode) {
				fail("the window mode is " + WindowMode.getWindowMode() + " after switching back to " + startMode);
			}
			windowFrame = frames;
			windowStep = 2;
			return false;
		}
		firstImage += "; window modes " + startMode + " to " + (startMode == WindowModeType.FIXED ? WindowModeType.RESIZABLE : WindowModeType.FIXED) + " and back, drawn in both";
		return true;
	}

	/**
	 * Called by PlayerEntity as it draws, with the models it built: the body, then the primary and
	 * secondary spot animation. Records that the local player drew a spot animation model.
	 */
	public static void drawn(PlayerEntity player, com.jagex.game.runetek5.graphics.model.Model[] models) {
		if (ENABLED && commandFrame >= 0 && player == PlayerUpdate.localPlayer) {
			if (player.primarySpotAnimation.spotAnimationId != -1 && models[1] != null
				|| player.secondarySpotAnimation.spotAnimationId != -1 && models[2] != null) {
				spotModelBuilt = true;
			}
		}
	}

	/**
	 * Called when the client reports an error it would otherwise only log.
	 */
	public static void error(String message, Throwable ex) {
		if (ENABLED) {
			fail("client error: " + message + (ex == null ? "" : " " + ex));
		}
	}

	private static void fail(String why) {
		System.out.println("SMOKE FAIL " + why);
		System.out.flush();
		Runtime.getRuntime().halt(1);
	}

	private static long elapsed() {
		return (System.currentTimeMillis() - START) / 1000L;
	}
}
