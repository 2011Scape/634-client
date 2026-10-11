package com.jagex.game.runetek5.client.input.camera;

import com.jagex.game.runetek5.NPCList;
import com.jagex.game.runetek5.ClipComponentsDebug;
import com.jagex.game.runetek5.camera.CameraMode;
import com.jagex.game.runetek5.camera.Shake;
import com.jagex.game.runetek5.math.ColourUtils;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.MaterialManager;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.MapRegion;
import com.jagex.game.runetek5.OrthoMode;
import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.graphics.hint.HintArrow;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.scene.SettingsBits;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.ByteArrayPool;

public final class Camera {

	@OriginalMember(owner = "client!sea", name = "w", descriptor = "I")
	public static int componentBackgroundCount = 0;

	@OriginalMember(owner = "client!fs", name = "a", descriptor = "(IIZIZII)V")
	public static void moveCamera(@OriginalArg(0) int constantSpeed, @OriginalArg(1) int localX, @OriginalArg(2) boolean calculatePosition, @OriginalArg(3) int moveZ, @OriginalArg(5) int variableSpeed, @OriginalArg(6) int localY) {
		Camera.moveToHeightOffset = moveZ;
		Camera.moveToLocalY = localY;
		Camera.moveToConstantSpeed = constantSpeed;
		Camera.moveToLocalX = localX;
		Camera.moveToVariableSpeed = variableSpeed;
		if (calculatePosition && Camera.moveToVariableSpeed >= 100) {
			Camera.cameraPositionAbsoluteY = Camera.moveToLocalY * 512 + 256;
			Camera.cameraPositionAbsoluteX = Camera.moveToLocalX * 512 + 256;
			Camera.cameraPositionAbsoluteZ = Scene.tileHeightForAbsolutePosition(Camera.cameraPositionAbsoluteX, PlayerUpdate.currentLevel, Camera.cameraPositionAbsoluteY) - Camera.moveToHeightOffset;
		}
		Camera.cameraCalculationType = CameraMode.MODE_FOLLOWPLAYER;
		Camera.roofRemovalTargetY = -1;
		Camera.roofRemovalTargetX = -1;
	}

	@OriginalMember(owner = "client!oca", name = "I", descriptor = "[I")
	public static final int[] WALKING_STEPS_Y = new int[50];

	@OriginalMember(owner = "client!db", name = "a", descriptor = "(IIIIII)V")
	public static void lookAt(@OriginalArg(0) int localY, @OriginalArg(1) int localX, @OriginalArg(2) int heightOffset, @OriginalArg(3) int variableSpeed, @OriginalArg(4) int constantSpeed) {
		Camera.lookAtVariableSpeed = variableSpeed;
		Camera.lookAtHeightOffset = heightOffset;
		Camera.lookAtLocalX = localX;
		Camera.lookAtConstantSpeed = constantSpeed;
		Camera.lookAtLocalY = localY;
		if (Camera.lookAtVariableSpeed >= 100) {
			@Pc(30) int targetAbsX = Camera.lookAtLocalX * 512 + 256;
			@Pc(36) int targetAbsY = Camera.lookAtLocalY * 512 + 256;
			@Pc(45) int targetHeight = Scene.tileHeightForAbsolutePosition(targetAbsX, PlayerUpdate.currentLevel, targetAbsY) - Camera.lookAtHeightOffset;
			@Pc(49) int deltaX = targetAbsX - Camera.cameraPositionAbsoluteX;
			@Pc(54) int deltaHeight = targetHeight - Camera.cameraPositionAbsoluteZ;
			@Pc(58) int deltaY = targetAbsY - Camera.cameraPositionAbsoluteY;
			@Pc(69) int horizontalDistance = (int) Math.sqrt((double) (deltaX * deltaX + deltaY * deltaY));
			Camera.cameraPitch = (int) (Math.atan2((double) deltaHeight, (double) horizontalDistance) * 2607.5945876176133D) & 0x3FFF;
			Camera.cameraYaw = (int) (-2607.5945876176133D * Math.atan2((double) deltaX, (double) deltaY)) & 0x3FFF;
			if (Camera.cameraPitch < 1024) {
				Camera.cameraPitch = 1024;
			}
			Camera.cameraRoll = 0;
			if (Camera.cameraPitch > 3072) {
				Camera.cameraPitch = 3072;
			}
		}
		Camera.cameraCalculationType = CameraMode.MODE_FOLLOWPLAYER;
		Camera.roofRemovalTargetY = -1;
		Camera.roofRemovalTargetX = -1;
	}

	@OriginalMember(owner = "client!cb", name = "b", descriptor = "(II)V")
	public static void updateSmoothTransitionCamera(@OriginalArg(0) int areaHeight) {
		@Pc(7) int cyclesSinceSmooth = ClientState.clientCycle - Camera.lastSmoothTransitionCycle;
		if (cyclesSinceSmooth >= 100) {
			Camera.cameraCalculationType = CameraMode.MODE_DEFAULT;
			Camera.roofRemovalTargetY = -1;
			Camera.roofRemovalTargetX = -CameraMode.MODE_DEFAULT;
			return;
		}
		@Pc(22) int yaw = (int) Camera.camAngleX;
		if (NPCList.newSize >> 8 > yaw) {
			yaw = NPCList.newSize >> 8;
		}
		if (Camera.CAM_SHAKING[4] && Shake.CAM_SHAKE_AMPLITUDE[4] + 128 > yaw) {
			yaw = Shake.CAM_SHAKE_AMPLITUDE[4] + 128;
		}
		@Pc(59) int roll = (int) Camera.camAngleY + Camera.cameraRandomisationA & 0x3FFF;
		Camera.setCameraPosition(yaw, Scene.tileHeightForAbsolutePosition(PlayerUpdate.localPlayer.x, PlayerUpdate.currentLevel, PlayerUpdate.localPlayer.z) - 200, roll, Camera.cameraBaseZ, areaHeight, Camera.cameraBaseX, (yaw >> 3) * 3 + 600 << 2);
		@Pc(110) float progress = 1.0F - (float) ((100 - cyclesSinceSmooth) * (100 - cyclesSinceSmooth) * (100 - cyclesSinceSmooth)) / 1000000.0F;
		Camera.cameraPositionAbsoluteX = (int) ((float) Camera.transitionStartX + progress * (float) (Camera.cameraPositionAbsoluteX - Camera.transitionStartX));
		Camera.cameraPositionAbsoluteZ = (int) ((float) (Camera.cameraPositionAbsoluteZ - Camera.transitionStartHeight) * progress + (float) Camera.transitionStartHeight);
		Camera.cameraPitch = (int) ((float) Camera.transitionStartPitch + (float) (Camera.cameraPitch - Camera.transitionStartPitch) * progress);
		Camera.cameraPositionAbsoluteY = (int) ((float) Camera.transitionStartY + progress * (float) (Camera.cameraPositionAbsoluteY - Camera.transitionStartY));
		@Pc(161) int rollDelta = Camera.cameraYaw - Camera.transitionStartRoll;
		if (rollDelta > 8192) {
			rollDelta -= 16384;
		} else if (rollDelta < -8192) {
			rollDelta += 16384;
		}
		Camera.cameraYaw = (int) ((float) rollDelta * progress + (float) Camera.transitionStartRoll);
		Camera.cameraYaw &= 0x3FFF;
	}

	@OriginalMember(owner = "client!bb", name = "I", descriptor = "I")
	public static int chatMessageHashIndex = 0;

	@OriginalMember(owner = "client!nm", name = "a", descriptor = "(I)V")
	public static void reset() {
		for (@Pc(3) int i = 0; i < Shake.DIRECTIONS; i++) {
			Camera.CAM_SHAKING[i] = false;
		}
		Camera.roofRemovalTargetY = -1;
		Camera.roofRemovalTargetX = -1;
		Camera.cameraCalculationType = CameraMode.MODE_DEFAULT;
		Camera.cameraPositionPathIndex = -1;
		Camera.lookAtVariableSpeed = 0;
		ColourUtils.cameraLookAtPathIndex = -1;
		Camera.lookAtConstantSpeed = 0;
	}
	@OriginalMember(owner = "client!fk", name = "x", descriptor = "I")
	public static int worldMapViewMinX;

	@OriginalMember(owner = "client!kfa", name = "v", descriptor = "I")
	public static int fps = 0;

	@OriginalMember(owner = "client!u", name = "j", descriptor = "I")
	public static int transitionStartRoll;

	@OriginalMember(owner = "client!f", name = "f", descriptor = "I")
	public static int transitionStartY;

	@OriginalMember(owner = "client!fea", name = "ab", descriptor = "I")
	public static int transitionStartX;

	@OriginalMember(owner = "client!gda", name = "z", descriptor = "I")
	public static int cameraTargetAbsZ = 0;

	@OriginalMember(owner = "client!uba", name = "w", descriptor = "I")
	public static int sceneLength;
	@OriginalMember(owner = "client!ci", name = "a", descriptor = "F")
	public static float camAngleY = 0.0F;

	@OriginalMember(owner = "client!vh", name = "g", descriptor = "I")
	public static int transitionStartPitch;

	@OriginalMember(owner = "client!er", name = "r", descriptor = "I")
	public static int cameraPositionAbsoluteX;

	@OriginalMember(owner = "client!aq", name = "l", descriptor = "I")
	public static int cameraBaseX;
	@OriginalMember(owner = "client!ufa", name = "d", descriptor = "I")
	public static int cameraRandomisationV = 0;

	@OriginalMember(owner = "client!rp", name = "Y", descriptor = "I")
	public static int cameraCalculationType;
	@OriginalMember(owner = "client!fv", name = "a", descriptor = "(B)V")
	public static void startSmoothTransition() {
		for (@Pc(7) int i = 0; i < 5; i++) {
			Camera.CAM_SHAKING[i] = false;
		}
		Camera.transitionStartY = Camera.cameraPositionAbsoluteY;
		Camera.transitionStartPitch = Camera.cameraPitch;
		Camera.transitionStartHeight = Camera.cameraPositionAbsoluteZ;
		Camera.lastSmoothTransitionCycle = ClientState.clientCycle;
		Camera.roofRemovalTargetY = -1;
		Camera.roofRemovalTargetX = -1;
		Camera.transitionStartX = Camera.cameraPositionAbsoluteX;
		Camera.lookAtVariableSpeed = 0;
		Camera.transitionStartRoll = Camera.cameraYaw;
		Camera.lookAtConstantSpeed = 0;
		Camera.cameraCalculationType = CameraMode.MODE_SMOOTH_RESET;
		Camera.cameraPositionPathIndex = -1;
		ColourUtils.cameraLookAtPathIndex = -1;
	}
	@OriginalMember(owner = "client!vg", name = "a", descriptor = "(IJ)V")
	public static void updateFollowPlayerCamera(@OriginalArg(1) long multiplier) {
		@Pc(10) int x = Camera.cameraRandomisationX + PlayerUpdate.localPlayer.x;
		@Pc(15) int z = Camera.cameraRandomisationV + PlayerUpdate.localPlayer.z;
		if (Camera.cameraBaseX - x < -2000 || Camera.cameraBaseX - x > 2000 || Camera.cameraBaseZ - z < -2000 || Camera.cameraBaseZ - z > 2000) {
			Camera.cameraBaseZ = z;
			Camera.cameraBaseX = x;
		}
		@Pc(57) int offsetFromCamera;
		@Pc(65) int offset;
		if (x != Camera.cameraBaseX) {
			offsetFromCamera = x - Camera.cameraBaseX;
			offset = (int) ((long) offsetFromCamera * multiplier / 320L);
			if (offsetFromCamera <= 0) {
				if (offset == 0) {
					offset = -1;
				} else if (offsetFromCamera > offset) {
					offset = offsetFromCamera;
				}
			} else if (offset == 0) {
				offset = 1;
			} else if (offsetFromCamera < offset) {
				offset = offsetFromCamera;
			}
			Camera.cameraBaseX += offset;
		}
		Camera.camAngleY += Camera.cameraYawVelocity * (float) multiplier / 6.0F;
		if (Camera.cameraBaseZ != z) {
			offsetFromCamera = z - Camera.cameraBaseZ;
			offset = (int) ((long) offsetFromCamera * multiplier / 320L);
			if (offsetFromCamera > 0) {
				if (offset == 0) {
					offset = 1;
				} else if (offsetFromCamera < offset) {
					offset = offsetFromCamera;
				}
			} else if (offset == 0) {
				offset = -1;
			} else if (offsetFromCamera > offset) {
				offset = offsetFromCamera;
			}
			Camera.cameraBaseZ += offset;
		}
		Camera.camAngleX += Camera.cameraPitchVelocity * (float) multiplier / 6.0F;
		Camera.processCamera();
	}

	@OriginalMember(owner = "client!qq", name = "g", descriptor = "I")
	public static int cameraBaseZ;

	@OriginalMember(owner = "client!wd", name = "M", descriptor = "I")
	public static int cameraPositionAbsoluteY;

	@OriginalMember(owner = "client!vn", name = "K", descriptor = "I")
	public static int moveToConstantSpeed;

	@OriginalMember(owner = "client!ve", name = "s", descriptor = "I")
	public static int gameAreaHeight = 0;

	@OriginalMember(owner = "client!bm", name = "b", descriptor = "(B)V")
	public static void calculateCameraPosition() {
		@Pc(9) int targetX = Camera.moveToLocalX * 512 + 256;
		@Pc(15) int targetY = Camera.moveToLocalY * 512 + 256;
		@Pc(24) int targetZ = Scene.tileHeightForAbsolutePosition(targetX, PlayerUpdate.currentLevel, targetY) - Camera.moveToHeightOffset;
		if (Camera.moveToVariableSpeed >= 100) {
			Camera.cameraPositionAbsoluteY = Camera.moveToLocalY * 512 + 256;
			Camera.cameraPositionAbsoluteX = Camera.moveToLocalX * 512 + 256;
			Camera.cameraPositionAbsoluteZ = Scene.tileHeightForAbsolutePosition(Camera.cameraPositionAbsoluteX, PlayerUpdate.currentLevel, Camera.cameraPositionAbsoluteY) - Camera.moveToHeightOffset;
		} else {
			if (Camera.cameraPositionAbsoluteX < targetX) {
				Camera.cameraPositionAbsoluteX += Camera.moveToConstantSpeed + Camera.moveToVariableSpeed * (targetX - Camera.cameraPositionAbsoluteX) / 1000;
				if (Camera.cameraPositionAbsoluteX > targetX) {
					Camera.cameraPositionAbsoluteX = targetX;
				}
			}
			if (targetX < Camera.cameraPositionAbsoluteX) {
				Camera.cameraPositionAbsoluteX -= Camera.moveToConstantSpeed + Camera.moveToVariableSpeed * (Camera.cameraPositionAbsoluteX - targetX) / 1000;
				if (targetX > Camera.cameraPositionAbsoluteX) {
					Camera.cameraPositionAbsoluteX = targetX;
				}
			}
			if (targetZ > Camera.cameraPositionAbsoluteZ) {
				Camera.cameraPositionAbsoluteZ += (targetZ - Camera.cameraPositionAbsoluteZ) * Camera.moveToVariableSpeed / 1000 + Camera.moveToConstantSpeed;
				if (targetZ < Camera.cameraPositionAbsoluteZ) {
					Camera.cameraPositionAbsoluteZ = targetZ;
				}
			}
			if (targetY > Camera.cameraPositionAbsoluteY) {
				Camera.cameraPositionAbsoluteY += Camera.moveToConstantSpeed + (targetY - Camera.cameraPositionAbsoluteY) * Camera.moveToVariableSpeed / 1000;
				if (Camera.cameraPositionAbsoluteY > targetY) {
					Camera.cameraPositionAbsoluteY = targetY;
				}
			}
			if (Camera.cameraPositionAbsoluteZ > targetZ) {
				Camera.cameraPositionAbsoluteZ -= (Camera.cameraPositionAbsoluteZ - targetZ) * Camera.moveToVariableSpeed / 1000 + Camera.moveToConstantSpeed;
				if (Camera.cameraPositionAbsoluteZ < targetZ) {
					Camera.cameraPositionAbsoluteZ = targetZ;
				}
			}
			if (Camera.cameraPositionAbsoluteY > targetY) {
				Camera.cameraPositionAbsoluteY -= Camera.moveToConstantSpeed + (Camera.cameraPositionAbsoluteY - targetY) * Camera.moveToVariableSpeed / 1000;
				if (targetY > Camera.cameraPositionAbsoluteY) {
					Camera.cameraPositionAbsoluteY = targetY;
				}
			}
		}
		targetY = Camera.lookAtLocalY * 512 + 256;
		targetX = Camera.lookAtLocalX * 512 + 256;
		targetZ = Scene.tileHeightForAbsolutePosition(targetX, PlayerUpdate.currentLevel, targetY) - Camera.lookAtHeightOffset;
		@Pc(229) int deltaX = targetX - Camera.cameraPositionAbsoluteX;
		@Pc(234) int deltaZ = targetZ - Camera.cameraPositionAbsoluteZ;
		@Pc(239) int deltaY = targetY - Camera.cameraPositionAbsoluteY;
		@Pc(251) int horizontalDistance = (int) Math.sqrt((double) (deltaX * deltaX + deltaY * deltaY));
		@Pc(262) int targetPitch = (int) (Math.atan2((double) deltaZ, (double) horizontalDistance) * 2607.5945876176133D) & 0x3FFF;
		if (targetPitch < 1024) {
			targetPitch = 1024;
		}
		@Pc(283) int targetYaw = (int) (-2607.5945876176133D * Math.atan2((double) deltaX, (double) deltaY)) & 0x3FFF;
		if (targetPitch > 3072) {
			targetPitch = 3072;
		}
		if (Camera.cameraPitch < targetPitch) {
			Camera.cameraPitch += Camera.lookAtVariableSpeed * (targetPitch - Camera.cameraPitch >> 3) / 1000 + Camera.lookAtConstantSpeed << 3;
			if (Camera.cameraPitch > targetPitch) {
				Camera.cameraPitch = targetPitch;
			}
		}
		if (targetPitch < Camera.cameraPitch) {
			Camera.cameraPitch -= (Camera.cameraPitch - targetPitch >> 3) * Camera.lookAtVariableSpeed / 1000 + Camera.lookAtConstantSpeed << 3;
			if (targetPitch > Camera.cameraPitch) {
				Camera.cameraPitch = targetPitch;
			}
		}
		@Pc(358) int yawDelta = targetYaw - Camera.cameraYaw;
		if (yawDelta > 8192) {
			yawDelta -= 16384;
		}
		if (yawDelta < -8192) {
			yawDelta += 16384;
		}
		yawDelta >>= 0x3;
		if (yawDelta > 0) {
			Camera.cameraYaw += Camera.lookAtConstantSpeed + Camera.lookAtVariableSpeed * yawDelta / 1000 << 3;
			Camera.cameraYaw &= 0x3FFF;
		}
		if (yawDelta < 0) {
			Camera.cameraYaw -= Camera.lookAtConstantSpeed + -yawDelta * Camera.lookAtVariableSpeed / 1000 << 3;
			Camera.cameraYaw &= 0x3FFF;
		}
		@Pc(415) int remainingYawDelta = targetYaw - Camera.cameraYaw;
		if (remainingYawDelta > 8192) {
			remainingYawDelta -= 16384;
		}
		if (remainingYawDelta < -8192) {
			remainingYawDelta += 16384;
		}
		Camera.cameraRoll = 0;
		if (remainingYawDelta < 0 && yawDelta > 0 || remainingYawDelta > 0 && yawDelta < 0) {
			Camera.cameraYaw = targetYaw;
		}
	}
	@OriginalMember(owner = "client!ks", name = "j", descriptor = "F")
	public static float camAngleX = 1024.0F;
	@OriginalMember(owner = "client!nd", name = "r", descriptor = "I")
	public static int cameraSplineProgress = 0;

	@OriginalMember(owner = "client!wr", name = "d", descriptor = "I")
	public static int cameraPositionAbsoluteZ;

	@OriginalMember(owner = "client!qb", name = "b", descriptor = "[Z")
	public static final boolean[] CAM_SHAKING = new boolean[5];

	@OriginalMember(owner = "client!ui", name = "j", descriptor = "I")
	public static int cameraLookAtKeyframe = 0;
	@OriginalMember(owner = "client!wp", name = "c", descriptor = "I")
	public static int cameraStepA = 1;

	@OriginalMember(owner = "client!rfa", name = "c", descriptor = "F")
	public static float cameraYawVelocity = 0.0F;

	@OriginalMember(owner = "client!dia", name = "f", descriptor = "I")
	public static int cameraSplineSpeedEnd = 0;

	@OriginalMember(owner = "client!qt", name = "q", descriptor = "I")
	public static int cameraSplineSpeedStart = 0;

	@OriginalMember(owner = "client!tq", name = "r", descriptor = "I")
	public static int transitionStartHeight;

	@OriginalMember(owner = "client!vb", name = "c", descriptor = "I")
	public static int moveToLocalY;

	@OriginalMember(owner = "client!cp", name = "a", descriptor = "S")
	public static short cameraZoomMax = 320;

	@OriginalMember(owner = "client!bn", name = "c", descriptor = "S")
	public static short viewportFovMax = 205;

	@OriginalMember(owner = "client!hfa", name = "a", descriptor = "(I)V")
	public static void applyCameraYawLeft() {
		if (!Camera.yawKeyHeld) {
			Camera.yawKeyHeld = true;
			Camera.cameraYawVelocity += (-Camera.cameraYawVelocity - 24.0F) / 2.0F;
			Camera.sendCameraAngle = true;
		}
	}

	@OriginalMember(owner = "client!mr", name = "b", descriptor = "S")
	public static short viewportFovMin = 256;

	@OriginalMember(owner = "client!vv", name = "C", descriptor = "[[[I")
	public static final int[][][] cameraSplinePaths = new int[2][][];

	@OriginalMember(owner = "client!vea", name = "d", descriptor = "I")
	public static int lookAtLocalY;

	@OriginalMember(owner = "client!qt", name = "n", descriptor = "I")
	public static int cameraRandomisationA = 0;

	@OriginalMember(owner = "client!ng", name = "d", descriptor = "I")
	public static int lookAtHeightOffset;

	@OriginalMember(owner = "client!lga", name = "a", descriptor = "(B)V")
	public static void calculateCutsceneCameraPosition() {
		if (Camera.cameraPositionPathIndex == -1 || ColourUtils.cameraLookAtPathIndex == -1) {
			return;
		}
		@Pc(23) int speed = (Camera.cameraSplineProgress * (Camera.cameraSplineSpeedEnd - Camera.cameraSplineSpeedStart) >> 16) + Camera.cameraSplineSpeedStart;
		Camera.cameraSplineProgress += speed;
		if (Camera.cameraSplineProgress < 65535) {
			Camera.cameraSplineFinished = false;
			Camera.cutsceneCameraPreviouslyCompleted = false;
		} else {
			if (Camera.cutsceneCameraPreviouslyCompleted) {
				Camera.cameraSplineFinished = false;
			} else {
				Camera.cameraSplineFinished = true;
			}
			Camera.cameraSplineProgress = 65535;
			Camera.cutsceneCameraPreviouslyCompleted = true;
		}
		@Pc(55) float t = (float) Camera.cameraSplineProgress / 65535.0F;
		@Pc(58) float[] position = new float[3];
		@Pc(62) int keyframeOffset = Camera.cameraPositionKeyframe * 2;
		@Pc(88) int local88;
		@Pc(121) int local121;
		@Pc(133) int local133;
		@Pc(143) int local143;
		@Pc(161) int local161;
		for (@Pc(64) int i = 0; i < 3; i++) {
			@Pc(76) int scaledStart = Camera.cameraSplinePaths[Camera.cameraPositionPathIndex][keyframeOffset][i] * 3;
			int scaledNext = Camera.cameraSplinePaths[Camera.cameraPositionPathIndex][keyframeOffset + 1][i] * 3;
			int scaledEndTangent = (Camera.cameraSplinePaths[Camera.cameraPositionPathIndex][keyframeOffset + 2][i] + Camera.cameraSplinePaths[Camera.cameraPositionPathIndex][keyframeOffset + 2][i] - Camera.cameraSplinePaths[Camera.cameraPositionPathIndex][keyframeOffset - -3][i]) * 3;
			@Pc(129) int c0 = Camera.cameraSplinePaths[Camera.cameraPositionPathIndex][keyframeOffset][i];
			int c1 = scaledNext - scaledStart;
			int c2 = scaledStart + scaledEndTangent - scaledNext * 2;
			int c3 = Camera.cameraSplinePaths[Camera.cameraPositionPathIndex][keyframeOffset + 2][i] + scaledNext - c0 - scaledEndTangent;
			position[i] = (float) c0 + t * ((float) c1 + t * (t * (float) c3 + (float) c2));
		}
		Camera.cameraPositionAbsoluteX = (int) position[0] - MapRegion.sceneBaseX * 512;
		Camera.cameraPositionAbsoluteZ = (int) position[1] * -1;
		Camera.cameraPositionAbsoluteY = (int) position[2] - MapRegion.sceneBaseY * 512;
		@Pc(214) float[] lookAt = new float[3];
		int lookAtKeyframeOffset = Camera.cameraLookAtKeyframe * 2;
		for (int axis = 0; axis < 3; axis++) {
			int scaledStart = Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex][lookAtKeyframeOffset][axis] * 3;
			int scaledNext = Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex][lookAtKeyframeOffset + 1][axis] * 3;
			int scaledEndTangent = (Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex][lookAtKeyframeOffset + 2][axis] + Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex][lookAtKeyframeOffset + 2][axis] - Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex][lookAtKeyframeOffset + 3][axis]) * 3;
			@Pc(293) int c0 = Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex][lookAtKeyframeOffset][axis];
			@Pc(298) int c1 = scaledNext - scaledStart;
			@Pc(307) int c2 = scaledStart + scaledEndTangent - scaledNext * 2;
			@Pc(325) int c3 = scaledNext + Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex][lookAtKeyframeOffset + 2][axis] - scaledEndTangent - c0;
			lookAt[axis] = t * ((float) c1 + t * ((float) c3 * t + (float) c2)) + (float) c0;
		}
		@Pc(360) float deltaX = lookAt[0] - position[0];
		@Pc(371) float deltaY = -1.0F * (lookAt[1] - position[1]);
		@Pc(380) float deltaZ = lookAt[2] - position[2];
		@Pc(390) double horizontalDistance = Math.sqrt((double) (deltaX * deltaX + deltaZ * deltaZ));
		Camera.cameraPitch = (int) (Math.atan2((double) deltaY, horizontalDistance) * 2607.5945876176133D) & 0x3FFF;
		Camera.cameraYaw = (int) (-Math.atan2((double) deltaX, (double) deltaZ) * 2607.5945876176133D) & 0x3FFF;
		Camera.cameraRoll = Camera.cameraSplinePaths[Camera.cameraPositionPathIndex][keyframeOffset][3] + ((Camera.cameraSplinePaths[Camera.cameraPositionPathIndex][keyframeOffset + 2][3] - Camera.cameraSplinePaths[Camera.cameraPositionPathIndex][keyframeOffset][3]) * Camera.cameraSplineProgress >> 16);
	}

	@OriginalMember(owner = "client!lm", name = "B", descriptor = "I")
	public static int moveToHeightOffset;

	@OriginalMember(owner = "client!vb", name = "e", descriptor = "[I")
	public static final int[] CAM_SHAKE_JITTER = new int[5];

	@OriginalMember(owner = "client!gl", name = "l", descriptor = "I")
	public static int cameraYaw;

	@OriginalMember(owner = "client!gs", name = "a", descriptor = "I")
	public static int roofRemovalTargetX;

	@OriginalMember(owner = "client!fu", name = "A", descriptor = "Z")
	public static boolean sendCameraAngle = true;
	@OriginalMember(owner = "client!aha", name = "a", descriptor = "(I)V")
	public static void applyCameraPitchDown() {
		if (!Camera.pitchKeyHeld) {
			Camera.pitchKeyHeld = true;
			Camera.sendCameraAngle = true;
			Camera.cameraPitchVelocity += (-12.0F - Camera.cameraPitchVelocity) / 2.0F;
		}
	}

	@OriginalMember(owner = "client!gh", name = "Ob", descriptor = "Z")
	public static boolean orthoCameraLock = true;

	@OriginalMember(owner = "client!qia", name = "H", descriptor = "S")
	public static short fovScaleMin = 1;

	@OriginalMember(owner = "client!lba", name = "a", descriptor = "(BJ)V")
	public static void updateOrbitTargetCamera(@OriginalArg(1) long elapsedMillis) {
		@Pc(7) int cameraTargetAbsX = Camera.cameraTargetAbsX;
		@Pc(18) int delta;
		@Pc(26) int step;
		if (Camera.cameraBaseX != cameraTargetAbsX) {
			delta = cameraTargetAbsX - Camera.cameraBaseX;
			step = (int) ((long) delta * elapsedMillis / 320L);
			if (delta <= 0) {
				if (step == 0) {
					step = -1;
				} else if (delta > step) {
					step = delta;
				}
			} else if (step == 0) {
				step = 1;
			} else if (step > delta) {
				step = delta;
			}
			Camera.cameraBaseX += step;
		}
		@Pc(72) int targetAbsZ = Camera.cameraTargetAbsZ;
		Camera.camAngleX += Camera.cameraPitchVelocity * (float) elapsedMillis / 40.0F * 8.0F;
		Camera.camAngleY += Camera.cameraYawVelocity * (float) elapsedMillis / 40.0F * 8.0F;
		if (Camera.cameraBaseZ != targetAbsZ) {
			delta = targetAbsZ - Camera.cameraBaseZ;
			step = (int) (elapsedMillis * (long) delta / 320L);
			if (delta <= 0) {
				if (step == 0) {
					step = -1;
				} else if (step < delta) {
					step = delta;
				}
			} else if (step == 0) {
				step = 1;
			} else if (delta < step) {
				step = delta;
			}
			Camera.cameraBaseZ += step;
		}
		Camera.processCamera();
	}

	@OriginalMember(owner = "client!wb", name = "p", descriptor = "I")
	public static int pickingCameraPitch;

	@OriginalMember(owner = "client!el", name = "g", descriptor = "I")
	public static int lookAtLocalX;

	@OriginalMember(owner = "client!gba", name = "r", descriptor = "I")
	public static int cameraTargetAbsX = 0;

	@OriginalMember(owner = "client!rq", name = "V", descriptor = "I")
	public static int cameraPositionKeyframe = 0;

	@OriginalMember(owner = "client!ri", name = "u", descriptor = "I")
	public static int moveToLocalX;

	@OriginalMember(owner = "client!gh", name = "Tb", descriptor = "I")
	public static int lastSmoothTransitionCycle = 0;

	@OriginalMember(owner = "client!mc", name = "g", descriptor = "I")
	public static int viewportScaledFov = 0;

	@OriginalMember(owner = "client!q", name = "y", descriptor = "I")
	public static int moveToVariableSpeed;

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "(B)V")
	public static void applyCameraPitchUp() {
		if (!Camera.pitchKeyHeld) {
			Camera.sendCameraAngle = true;
			Camera.cameraPitchVelocity += (12.0F - Camera.cameraPitchVelocity) / 2.0F;
			Camera.pitchKeyHeld = true;
		}
	}

	@OriginalMember(owner = "client!ai", name = "w", descriptor = "I")
	public static int cameraPositionPathIndex = -1;
	@OriginalMember(owner = "client!pa", name = "b", descriptor = "Z")
	public static boolean yawKeyHeld = false;

	@OriginalMember(owner = "client!av", name = "B", descriptor = "I")
	public static int lookAtVariableSpeed;

	@OriginalMember(owner = "client!ah", name = "b", descriptor = "I")
	public static int cameraStepV = 2;

	@OriginalMember(owner = "client!gga", name = "e", descriptor = "(I)V")
	public static void processCamera() {
		@Pc(7) short minAngleX = 1024;
		@Pc(9) short maxAngleX = 3072;
		if (OrthoMode.orthographicProjection) {
			maxAngleX = 4096;
			if (Camera.orthoCameraLock) {
				minAngleX = 2048;
			}
		}
		if (Camera.camAngleX < (float) minAngleX) {
			Camera.camAngleX = minAngleX;
		}
		if (Camera.camAngleX > (float) maxAngleX) {
			Camera.camAngleX = maxAngleX;
		}
		while (Camera.camAngleY >= 16384.0F) {
			Camera.camAngleY -= 16384.0F;
		}
		while (Camera.camAngleY < 0.0F) {
			Camera.camAngleY += 16384.0F;
		}
		@Pc(57) int cameraTileX = Camera.cameraBaseX >> 9;
		@Pc(61) int cameraTileZ = Camera.cameraBaseZ >> 9;
		@Pc(67) int cameraFloorHeight = Scene.tileHeightForAbsolutePosition(Camera.cameraBaseX, PlayerUpdate.currentLevel, Camera.cameraBaseZ);
		@Pc(69) int maxHeightDiff = 0;
		@Pc(91) int local91;
		if (cameraTileX > 3 && cameraTileZ > 3 && MapRegion.sceneSizeX - 4 > cameraTileX && MapRegion.sceneSizeY - 4 > cameraTileZ) {
			for (int tileX = cameraTileX - 4; tileX <= cameraTileX + 4; tileX++) {
				for (@Pc(97) int i = cameraTileZ - 4; i <= cameraTileZ + 4; i++) {
					@Pc(101) int level = PlayerUpdate.currentLevel;
					if (level < 3 && SettingsBits.isBridge(i, tileX)) {
						level++;
					}
					@Pc(114) int cameraHeightOffset = 0;
					if (MapRegion.aClass322_Sub1_1.cameraHeights != null && MapRegion.aClass322_Sub1_1.cameraHeights[level] != null) {
						cameraHeightOffset = (MapRegion.aClass322_Sub1_1.cameraHeights[level][tileX][i] & 0xFF) * 8 << 2;
					}
					if (Scene.aClass65Array1 != null && Scene.aClass65Array1[level] != null) {
						@Pc(156) int heightDiff = cameraFloorHeight + cameraHeightOffset - Scene.aClass65Array1[level].getHeight(i, tileX);
						if (maxHeightDiff < heightDiff) {
							maxHeightDiff = heightDiff;
						}
					}
				}
			}
		}
		int targetPitchLimit = (maxHeightDiff >> 2) * 1536;
		if (targetPitchLimit > 786432) {
			targetPitchLimit = 786432;
		}
		if (targetPitchLimit < 262144) {
			targetPitchLimit = 262144;
		}
		if (targetPitchLimit > NPCList.newSize) {
			NPCList.newSize += (targetPitchLimit - NPCList.newSize) / 24;
		} else if (NPCList.newSize > targetPitchLimit) {
			NPCList.newSize += (targetPitchLimit - NPCList.newSize) / 80;
		}
	}

	@OriginalMember(owner = "client!og", name = "o", descriptor = "I")
	public static int cameraAngleSendCooldown = 0;

	@OriginalMember(owner = "client!jo", name = "a", descriptor = "(IIIIIII)V")
	public static void storePickingCamera(@OriginalArg(0) int rotateX, @OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(4) int rotateY, @OriginalArg(5) int x, @OriginalArg(6) int rotateZ) {
		Camera.pickingCameraRoll = rotateZ;
		Camera.pickingCameraX = x;
		Camera.pickingCameraZ = z;
		Camera.pickingCameraPitch = rotateX;
		Camera.pickingCameraYaw = rotateY;
		Camera.pickingCameraY = y;
	}

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "(IIIIIIII)V")
	public static void setCameraPosition(@OriginalArg(0) int rotateX, @OriginalArg(1) int z, @OriginalArg(2) int rotateY, @OriginalArg(3) int y, @OriginalArg(4) int areaHeight, @OriginalArg(6) int x, @OriginalArg(7) int distance) {
		@Pc(7) int areaHeightDelta = areaHeight - 334;
		if (areaHeightDelta < 0) {
			areaHeightDelta = 0;
		} else if (areaHeightDelta > 100) {
			areaHeightDelta = 100;
		}
		@Pc(31) int zoom = areaHeightDelta * (Camera.cameraZoomMax - Camera.cameraZoomMin) / 100 + Camera.cameraZoomMin;
		@Pc(37) int scaledDistance = distance * zoom >> 8;
		HintArrow.scaledOrthoZoom = OrthoMode.orthoZoom * zoom >> 8;
		@Pc(49) int rotationX = 16384 - rotateX & 0x3FFF;
		@Pc(55) int rotationZ = 16384 - rotateY & 0x3FFF;
		@Pc(62) int deltaX = 0;
		@Pc(64) int deltaZ = 0;
		@Pc(66) int deltaY = scaledDistance;
		if (rotationX != 0) {
			deltaZ = -scaledDistance * TextureCombineSource.SINE[rotationX] >> 14;
			deltaY = TextureCombineSource.COSINE[rotationX] * scaledDistance >> 14;
		}
		if (rotationZ != 0) {
			deltaX = TextureCombineSource.SINE[rotationZ] * deltaY >> 14;
			deltaY = deltaY * TextureCombineSource.COSINE[rotationZ] >> 14;
		}
		Camera.cameraYaw = rotateY;
		Camera.cameraRoll = 0;
		Camera.cameraPositionAbsoluteY = y - deltaY;
		Camera.cameraPositionAbsoluteZ = z - deltaZ;
		Camera.cameraPositionAbsoluteX = x - deltaX;
		Camera.cameraPitch = rotateX;
	}

	@OriginalMember(owner = "client!or", name = "v", descriptor = "I")
	public static int lookAtConstantSpeed;

	@OriginalMember(owner = "client!pl", name = "c", descriptor = "S")
	public static short aspectFovMax = 32767;

	@OriginalMember(owner = "client!mk", name = "u", descriptor = "F")
	public static float cameraPitchVelocity = 0.0F;

	@OriginalMember(owner = "client!pw", name = "eb", descriptor = "I")
	public static int roofRemovalTargetY;

	@OriginalMember(owner = "client!dha", name = "B", descriptor = "I")
	public static int pickingCameraZ;

	@OriginalMember(owner = "client!gb", name = "u", descriptor = "Z")
	public static boolean pitchKeyHeld = false;

	@OriginalMember(owner = "client!iia", name = "j", descriptor = "I")
	public static int pickingCameraY;

	@OriginalMember(owner = "client!aba", name = "a", descriptor = "(IIII)V")
	public static void forceCameraAngle(@OriginalArg(0) int angleX, @OriginalArg(1) int angleY) {
		@Pc(3) int scaledAngleX = angleX << 3;
		@Pc(11) int scaledAngleY = angleY << 3;
		if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWPLAYER) {
			Camera.cameraYaw = scaledAngleY;
			Camera.cameraRoll = 0;
			Camera.cameraPitch = scaledAngleX;
		}
		Camera.camAngleY = scaledAngleY;
		Camera.camAngleX = scaledAngleX;
		Camera.processCamera();
		Camera.sendCameraAngle = true;
	}

	@OriginalMember(owner = "client!pk", name = "A", descriptor = "S")
	public static short cameraZoomMin = 256;

	@OriginalMember(owner = "client!hu", name = "d", descriptor = "I")
	public static int cameraRandomisationCycle = 0;

	@OriginalMember(owner = "client!eg", name = "g", descriptor = "Z")
	public static boolean cameraSplineFinished = false;

	@OriginalMember(owner = "client!ea", name = "f", descriptor = "I")
	public static int cameraRoll;

	@OriginalMember(owner = "client!kl", name = "o", descriptor = "S")
	public static short aspectFovMin = 1;

	@OriginalMember(owner = "client!iq", name = "e", descriptor = "S")
	public static short fovScaleMax = 32767;
	@OriginalMember(owner = "client!em", name = "g", descriptor = "(I)V")
	public static void applyCameraYawRight() {
		if (!Camera.yawKeyHeld) {
			Camera.yawKeyHeld = true;
			Camera.cameraYawVelocity += (24.0F - Camera.cameraYawVelocity) / 2.0F;
			Camera.sendCameraAngle = true;
		}
	}
	@OriginalMember(owner = "client!tw", name = "x", descriptor = "I")
	public static int pickingCameraX;

	@OriginalMember(owner = "client!un", name = "K", descriptor = "I")
	public static int cameraStepH = 2;

	@OriginalMember(owner = "client!hh", name = "m", descriptor = "I")
	public static int projectionScaleX;

	@OriginalMember(owner = "client!hw", name = "a", descriptor = "(IIBII)V")
	public static void setProjection(@OriginalArg(0) int scaleX, @OriginalArg(1) int scaleY, @OriginalArg(3) int centerX, @OriginalArg(4) int centerY) {
		Camera.pickingProjectionCenterX = centerX;
		ByteArrayPool.projectionScaleY = scaleY;
		Camera.projectionScaleX = scaleX;
		Camera.pickingProjectionCenterY = centerY;
	}

	@OriginalMember(owner = "client!hq", name = "i", descriptor = "Z")
	public static boolean cutsceneCameraPreviouslyCompleted = false;

	@OriginalMember(owner = "client!so", name = "l", descriptor = "I")
	public static int gameAreaY = 0;

	@OriginalMember(owner = "client!wc", name = "n", descriptor = "I")
	public static int gameAreaX = 0;

	@OriginalMember(owner = "client!dh", name = "R", descriptor = "I")
	public static int pickingCameraRoll;

	@OriginalMember(owner = "client!dt", name = "b", descriptor = "I")
	public static int pickingProjectionCenterX;

	@OriginalMember(owner = "client!hia", name = "d", descriptor = "I")
	public static int cameraRandomisationX = 0;

	@OriginalMember(owner = "client!rfa", name = "d", descriptor = "I")
	public static int pickingProjectionCenterY;

	@OriginalMember(owner = "client!vea", name = "g", descriptor = "I")
	public static int pickingCameraYaw;
	@OriginalMember(owner = "client!jk", name = "K", descriptor = "I")
	public static int cameraPitch;
}
