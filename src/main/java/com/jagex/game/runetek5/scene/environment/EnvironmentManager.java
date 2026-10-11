package com.jagex.game.runetek5.scene.environment;

import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.EnvironmentSampler;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.MainLogicManager;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.client.input.camera.Camera;
import com.jagex.game.runetek5.entity.PlayerEntity;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.EnvironmentSamplerCacheReset;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.SunPositionZ;
import com.jagex.game.runetek5.entity.StaticLocation;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.utils.Utils;

public final class EnvironmentManager {
	@OriginalMember(owner = "client!lb", name = "a", descriptor = "[I")
	public static int[] cubeTextureIds;

	private EnvironmentManager() {
		/* empty */
	}
	@OriginalMember(owner = "client!oh", name = "a", descriptor = "(IIII)I")
	public static int rotatedX(@OriginalArg(0) int z, @OriginalArg(1) int x, @OriginalArg(3) int rotation) {
		@Pc(3) int rotationMasked = rotation & 0x3;
		if (rotationMasked == 0) {
			return x;
		} else if (rotationMasked == 1) {
			return z;
		} else if (rotationMasked == 2) {
			return 4095 - x;
		} else {
			return 4095 - z;
		}
	}
	@OriginalMember(owner = "client!uha", name = "a", descriptor = "([BB)V")
	public static void decodeMapDefaults(@OriginalArg(0) byte[] src) {
		@Pc(26) Packet packet = new Packet(src);
		while (true) {
			while (true) {
				while (true) {
					@Pc(30) int opcode = packet.readUByte();
					if (opcode == 0) {
						return;
					}
					if (opcode == 1) {
						@Pc(124) int[] textureIds = EnvironmentManager.cubeTextureIds = new int[6];
						textureIds[0] = packet.readUShort();
						textureIds[1] = packet.readUShort();
						textureIds[2] = packet.readUShort();
						textureIds[3] = packet.readUShort();
						textureIds[4] = packet.readUShort();
						textureIds[5] = packet.readUShort();
					} else {
						@Pc(53) int i;
						@Pc(48) int count;
						if (opcode == 4) {
							count = packet.readUByte();
							PlayerEntity.defaultTitleEnumIds = new int[count];
							for (i = 0; i < count; i++) {
								PlayerEntity.defaultTitleEnumIds[i] = packet.readUShort();
								if (PlayerEntity.defaultTitleEnumIds[i] == 65535) {
									PlayerEntity.defaultTitleEnumIds[i] = -1;
								}
							}
						} else if (opcode == 5) {
							count = packet.readUByte();
							PlayerEntity.femaleTitleEnumIds = new int[count];
							for (i = 0; i < count; i++) {
								PlayerEntity.femaleTitleEnumIds[i] = packet.readUShort();
								if (PlayerEntity.femaleTitleEnumIds[i] == 65535) {
									PlayerEntity.femaleTitleEnumIds[i] = -1;
								}
							}
						}
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!sj", name = "a", descriptor = "(IIIIIII)Lclient!wh;")
	public static EnvironmentSampler getCubeTexture(@OriginalArg(0) int materialPositiveZ, @OriginalArg(1) int materialNegativeY, @OriginalArg(2) int materialPositiveX, @OriginalArg(3) int materialNegativeZ, @OriginalArg(4) int materialNegativeX, @OriginalArg(6) int materialPositiveY) {
		@Pc(33) long key = (long) materialNegativeZ * 76724863L ^ (long) materialPositiveZ * 32147369L ^ (long) materialNegativeY * 986053L ^ (long) materialPositiveX * 67481L ^ (long) materialNegativeX * 97549L ^ (long) materialPositiveY * 475427L;
		@Pc(39) EnvironmentSampler environmentSampler = (EnvironmentSampler) EnvironmentSampler.aClass169_27.get(key);
		if (environmentSampler == null) {
			environmentSampler = EnvironmentManager.toolkit.createCubeTexture(materialPositiveX, materialNegativeX, materialPositiveY, materialNegativeY, materialPositiveZ, materialNegativeZ);
			EnvironmentSampler.aClass169_27.put(environmentSampler, key);
			return environmentSampler;
		} else {
			return environmentSampler;
		}
	}

	@OriginalMember(owner = "client!wga", name = "L", descriptor = "(I)V")
	public static void resetChunkEnvironments() {
		if (EnvironmentManager.aClass41ArrayArray1 == null) {
			return;
		}
		for (@Pc(9) int i = 0; i < EnvironmentManager.aClass41ArrayArray1.length; i++) {
			for (@Pc(13) int j = 0; j < EnvironmentManager.aClass41ArrayArray1[i].length; j++) {
				EnvironmentManager.aClass41ArrayArray1[i][j] = EnvironmentManager.aClass41_2;
			}
		}
	}

	@OriginalMember(owner = "client!td", name = "H", descriptor = "Lclient!wh;")
	public static EnvironmentSampler aClass78_4;

	@OriginalMember(owner = "client!uh", name = "m", descriptor = "[[Lclient!bp;")
	public static Environment[][] aClass41ArrayArray1;

	@OriginalMember(owner = "client!wv", name = "a", descriptor = "(IIII)I")
	public static int rotatedZ(@OriginalArg(1) int rotation, @OriginalArg(2) int z, @OriginalArg(3) int x) {
		@Pc(12) int orientation = rotation & 0x3;
		if (orientation == 0) {
			return z;
		} else if (orientation == 1) {
			return 4095 - x;
		} else if (orientation == 2) {
			return 4095 - z;
		} else {
			return x;
		}
	}
	@OriginalMember(owner = "client!pp", name = "w", descriptor = "Lclient!bp;")
	public static Environment environment;

	@OriginalMember(owner = "client!kq", name = "P", descriptor = "Z")
	public static boolean directEnvironmentTransition = false;

	@OriginalMember(owner = "client!ks", name = "a", descriptor = "(Z)V")
	public static void requestDirectEnvironmentTransition() {
		EnvironmentManager.directEnvironmentTransition = true;
	}
	@OriginalMember(owner = "client!vfa", name = "a", descriptor = "(IIIB)V")
	public static void chunkEnvironmentUpdate(@OriginalArg(0) int fadeMillis, @OriginalArg(1) int zoneX, @OriginalArg(2) int zoneY) {
		@Pc(9) Environment environment = EnvironmentManager.aClass41ArrayArray1[zoneX][zoneY];
		EnvironmentManager.startEnvironmentTransition(fadeMillis, environment == null ? EnvironmentManager.aClass41_2 : environment);
	}

	@OriginalMember(owner = "client!wi", name = "f", descriptor = "(I)V")
	public static void transitionSceneEnvironment() {
		if (LoginManager.lastDisallowResult < 0) {
			return;
		}
		@Pc(13) long now = Utils.currentTimeMillis();
		LoginManager.lastDisallowResult = (int) ((long) LoginManager.lastDisallowResult + EnvironmentManager.lastEnvironmentUpdate - now);
		if (LoginManager.lastDisallowResult > 0) {
			@Pc(33) int previousWeight = (LoginManager.lastDisallowResult << 8) / EnvironmentManager.totalTransitionDuration;
			@Pc(38) int currentWeight = 255 - previousWeight;
			@Pc(43) float previousFactor = (float) previousWeight / 255.0F;
			@Pc(47) float currentFactor = 1.0F - previousFactor;
			EnvironmentManager.currentSunColour = (currentWeight * (EnvironmentManager.environment.sunColour & 0xFF00FF) + (EnvironmentManager.previousSunColour & 0xFF00FF) * previousWeight & 0xFF00FF00) + (previousWeight * (EnvironmentManager.previousSunColour & 0xFF00) + (EnvironmentManager.environment.sunColour & 0xFF00) * currentWeight & 0xFF0000) >>> 8;
			EnvironmentManager.currentWhitePoint = EnvironmentManager.previousWhitePoint + currentFactor * (EnvironmentManager.environment.whitePoint - EnvironmentManager.previousWhitePoint);
			EnvironmentManager.currentBloom = (EnvironmentManager.environment.bloom - EnvironmentManager.previousBloom) * currentFactor + EnvironmentManager.previousBloom;
			EnvironmentManager.fogDepth = previousWeight * EnvironmentManager.previousFogDepth + EnvironmentManager.environment.fogDepth * currentWeight >> 8;
			EnvironmentManager.currentSunAmbient = EnvironmentManager.previousSunAmbient + (EnvironmentManager.environment.sunAmbient - EnvironmentManager.previousSunAmbient) * currentFactor;
			EnvironmentManager.currentSunLight = EnvironmentManager.previousSunLight + currentFactor * (EnvironmentManager.environment.sunLight - EnvironmentManager.previousSunLight);
			EnvironmentManager.fogColour = (previousWeight * (EnvironmentManager.previousFogColour & 0xFF00) + (EnvironmentManager.environment.fogColour & 0xFF00) * currentWeight & 0xFF0000) + (previousWeight * (EnvironmentManager.previousFogColour & 0xFF00FF) + (currentWeight * (EnvironmentManager.environment.fogColour & 0xFF00FF)) & 0xFF00FF00) >>> 8;
			EnvironmentManager.currentSunBacklight = EnvironmentManager.previousSunBacklight + currentFactor * (EnvironmentManager.environment.sunBacklight - EnvironmentManager.previousSunBacklight);
			EnvironmentManager.currentBrightpass = EnvironmentManager.previousBrightpass + (EnvironmentManager.environment.brightpass - EnvironmentManager.previousBrightpass) * currentFactor;
			if (EnvironmentManager.aClass78_2 != EnvironmentManager.environment.sunCubeTexture) {
				EnvironmentManager.aClass78_3 = EnvironmentManager.toolkit.drawQuad(EnvironmentManager.aClass78_2, EnvironmentManager.environment.sunCubeTexture, currentFactor, EnvironmentManager.aClass78_3);
			}
		} else {
			EnvironmentManager.aClass78_3 = EnvironmentManager.environment.sunCubeTexture;
			EnvironmentManager.currentSunLight = EnvironmentManager.environment.sunLight;
			EnvironmentManager.currentBloom = EnvironmentManager.environment.bloom;
			EnvironmentManager.currentWhitePoint = EnvironmentManager.environment.whitePoint;
			EnvironmentManager.fogDepth = EnvironmentManager.environment.fogDepth;
			EnvironmentManager.currentBrightpass = EnvironmentManager.environment.brightpass;
			EnvironmentManager.currentSunBacklight = EnvironmentManager.environment.sunBacklight;
			LoginManager.lastDisallowResult = -1;
			EnvironmentManager.fogColour = EnvironmentManager.environment.fogColour;
			EnvironmentManager.currentSunColour = EnvironmentManager.environment.sunColour;
			EnvironmentManager.currentSunAmbient = EnvironmentManager.environment.sunAmbient;
		}
		EnvironmentManager.lastEnvironmentUpdate = now;
	}

	@OriginalMember(owner = "client!cb", name = "p", descriptor = "I")
	public static int defaultFogColour = 13156520;

	@OriginalMember(owner = "client!cb", name = "k", descriptor = "I")
	public static int defaultSunColour = 16777215;
	@OriginalMember(owner = "client!lj", name = "a", descriptor = "(Lclient!bp;III)V")
	public static void addChunkEnvironment(@OriginalArg(0) Environment environment, @OriginalArg(2) int chunkX, @OriginalArg(3) int chunkZ) {
		EnvironmentManager.aClass41ArrayArray1[chunkX][chunkZ] = environment;
	}
	@OriginalMember(owner = "client!lk", name = "e", descriptor = "(B)V")
	public static void updateAtmosphere() {
		if (MainLogicManager.isLoginScreenStep(ClientState.mainLogicStep) || MainLogicManager.inLobby(ClientState.mainLogicStep)) {
			EnvironmentManager.chunkEnvironmentUpdate(EnvironmentManager.ENVIRONMENT_FADE_MILLIS, Camera.cameraPositionAbsoluteX >> 12, Camera.cameraPositionAbsoluteY >> 12);
		} else {
			@Pc(16) int zoneX = PlayerUpdate.localPlayer.movementBaseX[0] >> 3;
			@Pc(23) int zoneY = PlayerUpdate.localPlayer.movementBaseY[0] >> 3;
			if (zoneX >= 0 && MapRegion.sceneSizeX >> 3 > zoneX && zoneY >= 0 && zoneY < MapRegion.sceneSizeY >> 3) {
				EnvironmentManager.chunkEnvironmentUpdate(EnvironmentManager.ENVIRONMENT_FADE_MILLIS, zoneX, zoneY);
			} else {
				EnvironmentManager.chunkEnvironmentUpdate(0, MapRegion.sceneSizeX >> 4, MapRegion.sceneSizeY >> 4);
			}
		}
		EnvironmentManager.transitionSceneEnvironment();
		EnvironmentManager.applySunSettings();
		EnvironmentManager.applyBloomParams();
		EnvironmentManager.applyFogSettings();
	}

	@OriginalMember(owner = "client!vg", name = "k", descriptor = "I")
	public static int previousFogColour;

	@OriginalMember(owner = "client!ot", name = "h", descriptor = "Lclient!wh;")
	public static EnvironmentSampler aClass78_3;

	@OriginalMember(owner = "client!rg", name = "z", descriptor = "I")
	public static int totalTransitionDuration = 0;

	@OriginalMember(owner = "client!ur", name = "m", descriptor = "F")
	public static float currentBrightpass;

	@OriginalMember(owner = "client!ka", name = "m", descriptor = "F")
	public static float previousSunLight;

	@OriginalMember(owner = "client!raa", name = "x", descriptor = "J")
	public static long lastEnvironmentUpdate = 0L;

	@OriginalMember(owner = "client!tda", name = "r", descriptor = "F")
	public static float currentSunLight;

	@OriginalMember(owner = "client!dba", name = "C", descriptor = "Lclient!wh;")
	public static EnvironmentSampler aClass78_2;

	@OriginalMember(owner = "client!wha", name = "z", descriptor = "F")
	public static float previousWhitePoint;

	@OriginalMember(owner = "client!fe", name = "y", descriptor = "F")
	public static float currentSunBacklight;

	@OriginalMember(owner = "client!mc", name = "a", descriptor = "(IILclient!ha;I)V")
	public static void initialize(@OriginalArg(0) int zoneCountX, @OriginalArg(2) Toolkit toolkit, @OriginalArg(3) int zoneCountZ) {
		EnvironmentManager.toolkit = toolkit;
		EnvironmentManager.aClass41ArrayArray1 = new Environment[zoneCountX][zoneCountZ];
		if (EnvironmentManager.cubeTextureIds != null) {
			EnvironmentManager.aClass78_4 = EnvironmentManager.getCubeTexture(EnvironmentManager.cubeTextureIds[4], EnvironmentManager.cubeTextureIds[3], EnvironmentManager.cubeTextureIds[0], EnvironmentManager.cubeTextureIds[5], EnvironmentManager.cubeTextureIds[1], EnvironmentManager.cubeTextureIds[2]);
		}
		EnvironmentManager.aClass41_2 = new Environment();
		EnvironmentManager.resetChunkEnvironments();
	}

	@OriginalMember(owner = "client!ca", name = "e", descriptor = "F")
	public static float previousSunBacklight;

	@OriginalMember(owner = "client!cw", name = "i", descriptor = "I")
	public static int currentSunColour;

	@OriginalMember(owner = "client!va", name = "sb", descriptor = "Lclient!bp;")
	public static Environment aClass41_2;

	@OriginalMember(owner = "client!tt", name = "r", descriptor = "I")
	public static int previousFogDepth;

	@OriginalMember(owner = "client!eu", name = "J", descriptor = "F")
	public static float currentBloom;
	@OriginalMember(owner = "client!od", name = "y", descriptor = "F")
	public static float currentWhitePoint;

	@OriginalMember(owner = "client!hj", name = "e", descriptor = "I")
	public static int sunPositionX = -50;

	@OriginalMember(owner = "client!pp", name = "a", descriptor = "(B)V")
	public static void reset() {
		EnvironmentSamplerCacheReset.reset();
		EnvironmentManager.aClass41ArrayArray1 = null;
		EnvironmentManager.toolkit = null;
		EnvironmentManager.aClass78_2 = null;
		EnvironmentManager.aClass78_3 = null;
		EnvironmentManager.aClass78_4 = null;
	}

	@OriginalMember(owner = "client!pc", name = "a", descriptor = "(ZILclient!bp;)V")
	public static void startEnvironmentTransition(@OriginalArg(1) int transitionTime, @OriginalArg(2) Environment environment) {
		if (EnvironmentManager.directEnvironmentTransition) {
			EnvironmentManager.directEnvironmentTransition = false;
			transitionTime = 0;
		}
		if (EnvironmentManager.environment != null && EnvironmentManager.environment.isEqual(environment)) {
			return;
		}
		EnvironmentManager.environment = environment;
		EnvironmentManager.lastEnvironmentUpdate = Utils.currentTimeMillis();
		LoginManager.lastDisallowResult = transitionTime;
		EnvironmentManager.totalTransitionDuration = transitionTime;
		if (EnvironmentManager.totalTransitionDuration == 0) {
			EnvironmentManager.transitionSceneEnvironment();
			return;
		}
		EnvironmentManager.previousSunAmbient = EnvironmentManager.currentSunAmbient;
		EnvironmentManager.previousFogColour = EnvironmentManager.fogColour;
		EnvironmentManager.aClass78_2 = EnvironmentManager.aClass78_3;
		EnvironmentManager.previousSunLight = EnvironmentManager.currentSunLight;
		EnvironmentManager.previousBrightpass = EnvironmentManager.currentBrightpass;
		EnvironmentManager.previousBloom = EnvironmentManager.currentBloom;
		EnvironmentManager.previousWhitePoint = EnvironmentManager.currentWhitePoint;
		EnvironmentManager.previousSunBacklight = EnvironmentManager.currentSunBacklight;
		EnvironmentManager.previousSunColour = EnvironmentManager.currentSunColour;
		EnvironmentManager.previousFogDepth = EnvironmentManager.fogDepth;
	}

	@OriginalMember(owner = "client!jb", name = "z", descriptor = "I")
	public static int previousSunColour;

	@OriginalMember(owner = "client!aba", name = "D", descriptor = "F")
	public static float previousSunAmbient;

	@OriginalMember(owner = "client!pa", name = "i", descriptor = "F")
	public static float currentSunAmbient;
	public static final int ENVIRONMENT_FADE_MILLIS = 5000;
	@OriginalMember(owner = "client!jr", name = "o", descriptor = "Lclient!ha;")
	public static Toolkit toolkit;
	@OriginalMember(owner = "client!aq", name = "b", descriptor = "(I)V")
	public static void applyFogSettings() {
		EnvironmentManager.toolkit.L(EnvironmentManager.fogColour, GamePreferences.uid.fog.getValue() == 1 ? EnvironmentManager.fogDepth + 256 << 2 : -1, 0);
	}

	@OriginalMember(owner = "client!vj", name = "b", descriptor = "(B)V")
	public static void applyBloomParams() {
		EnvironmentManager.toolkit.setBloomParams(EnvironmentManager.currentWhitePoint, EnvironmentManager.currentBloom, EnvironmentManager.currentBrightpass);
	}

	@OriginalMember(owner = "client!in", name = "S", descriptor = "F")
	public static float previousBloom;

	@OriginalMember(owner = "client!ts", name = "a", descriptor = "(Z)V")
	public static void applySunSettings() {
		EnvironmentManager.toolkit.xa(((float) GamePreferences.uid.brightness.getValue() * 0.1F + 0.7F) * EnvironmentManager.currentSunAmbient);
		EnvironmentManager.toolkit.ZA(EnvironmentManager.currentSunColour, EnvironmentManager.currentSunLight, EnvironmentManager.currentSunBacklight, (float) (EnvironmentManager.sunPositionX << 2), (float) (StaticLocation.lb << 2), (float) (SunPositionZ.sunPositionZ << 2));
		EnvironmentManager.toolkit.setSunCubeTexture(EnvironmentManager.aClass78_3);
	}

	@OriginalMember(owner = "client!qga", name = "c", descriptor = "I")
	public static int fogColour;

	@OriginalMember(owner = "client!id", name = "a", descriptor = "I")
	public static int fogDepth;

	@OriginalMember(owner = "client!mo", name = "a", descriptor = "(IIZ)V")
	public static void applyEnvironmentSunPosition(@OriginalArg(0) int chunkX, @OriginalArg(1) int chunkZ) {
		@Pc(9) Environment environment = EnvironmentManager.aClass41ArrayArray1[chunkX][chunkZ];
		if (environment != null) {
			SunPositionZ.sunPositionZ = environment.sunPositionZ;
			EnvironmentManager.sunPositionX = environment.sunPositionX;
			StaticLocation.lb = environment.sunPositionY;
		}
		EnvironmentManager.applySunSettings();
	}

	@OriginalMember(owner = "client!te", name = "d", descriptor = "F")
	public static float previousBrightpass;
}
