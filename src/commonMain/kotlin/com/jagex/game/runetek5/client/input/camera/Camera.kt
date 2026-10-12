package com.jagex.game.runetek5.client.input.camera

import com.jagex.game.runetek5.NPCList
import com.jagex.game.runetek5.camera.CameraMode
import com.jagex.game.runetek5.camera.Shake
import com.jagex.game.runetek5.math.ColourUtils
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate
import com.jagex.game.runetek5.MapRegion
import com.jagex.game.runetek5.OrthoMode
import com.jagex.game.runetek5.TextureCombineSource
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.scene.SettingsBits
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.ByteArrayPool
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object Camera {

	@OriginalMember(owner = "client!sea", name = "w", descriptor = "I")
	@JvmField
	var componentBackgroundCount: Int = 0

	@OriginalMember(owner = "client!fs", name = "a", descriptor = "(IIZIZII)V")
	@JvmStatic
	fun moveCamera(@OriginalArg(0) constantSpeed: Int, @OriginalArg(1) localX: Int, @OriginalArg(2) calculatePosition: Boolean, @OriginalArg(3) moveZ: Int, @OriginalArg(5) variableSpeed: Int, @OriginalArg(6) localY: Int) {
		Camera.moveToHeightOffset = moveZ
		Camera.moveToLocalY = localY
		Camera.moveToConstantSpeed = constantSpeed
		Camera.moveToLocalX = localX
		Camera.moveToVariableSpeed = variableSpeed
		if (calculatePosition && Camera.moveToVariableSpeed >= 100) {
			Camera.cameraPositionAbsoluteY = Camera.moveToLocalY * 512 + 256
			Camera.cameraPositionAbsoluteX = Camera.moveToLocalX * 512 + 256
			Camera.cameraPositionAbsoluteZ = Scene.tileHeightForAbsolutePosition(Camera.cameraPositionAbsoluteX, PlayerUpdate.currentLevel, Camera.cameraPositionAbsoluteY) - Camera.moveToHeightOffset
		}
		Camera.cameraCalculationType = CameraMode.MODE_FOLLOWPLAYER
		Camera.roofRemovalTargetY = -1
		Camera.roofRemovalTargetX = -1
	}

	@OriginalMember(owner = "client!oca", name = "I", descriptor = "[I")
	@JvmField
	val WALKING_STEPS_Y: IntArray = IntArray(50)

	@OriginalMember(owner = "client!db", name = "a", descriptor = "(IIIIII)V")
	@JvmStatic
	fun lookAt(@OriginalArg(0) localY: Int, @OriginalArg(1) localX: Int, @OriginalArg(2) heightOffset: Int, @OriginalArg(3) variableSpeed: Int, @OriginalArg(4) constantSpeed: Int) {
		Camera.lookAtVariableSpeed = variableSpeed
		Camera.lookAtHeightOffset = heightOffset
		Camera.lookAtLocalX = localX
		Camera.lookAtConstantSpeed = constantSpeed
		Camera.lookAtLocalY = localY
		if (Camera.lookAtVariableSpeed >= 100) {
			@Pc(30) val targetAbsX: Int = Camera.lookAtLocalX * 512 + 256
			@Pc(36) val targetAbsY: Int = Camera.lookAtLocalY * 512 + 256
			@Pc(45) val targetHeight: Int = Scene.tileHeightForAbsolutePosition(targetAbsX, PlayerUpdate.currentLevel, targetAbsY) - Camera.lookAtHeightOffset
			@Pc(49) val deltaX: Int = targetAbsX - Camera.cameraPositionAbsoluteX
			@Pc(54) val deltaHeight: Int = targetHeight - Camera.cameraPositionAbsoluteZ
			@Pc(58) val deltaY: Int = targetAbsY - Camera.cameraPositionAbsoluteY
			@Pc(69) val horizontalDistance: Int = kotlin.math.sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toInt()
			Camera.cameraPitch = (kotlin.math.atan2(deltaHeight.toDouble(), horizontalDistance.toDouble()) * 2607.5945876176133).toInt() and 0x3FFF
			Camera.cameraYaw = (-2607.5945876176133 * kotlin.math.atan2(deltaX.toDouble(), deltaY.toDouble())).toInt() and 0x3FFF
			if (Camera.cameraPitch < 1024) {
				Camera.cameraPitch = 1024
			}
			Camera.cameraRoll = 0
			if (Camera.cameraPitch > 3072) {
				Camera.cameraPitch = 3072
			}
		}
		Camera.cameraCalculationType = CameraMode.MODE_FOLLOWPLAYER
		Camera.roofRemovalTargetY = -1
		Camera.roofRemovalTargetX = -1
	}

	@OriginalMember(owner = "client!cb", name = "b", descriptor = "(II)V")
	@JvmStatic
	fun updateSmoothTransitionCamera(@OriginalArg(0) areaHeight: Int) {
		@Pc(7) val cyclesSinceSmooth: Int = ClientState.clientCycle - Camera.lastSmoothTransitionCycle
		if (cyclesSinceSmooth >= 100) {
			Camera.cameraCalculationType = CameraMode.MODE_DEFAULT
			Camera.roofRemovalTargetY = -1
			Camera.roofRemovalTargetX = -CameraMode.MODE_DEFAULT
			return
		}
		@Pc(22) var yaw: Int = Camera.camAngleX.toInt()
		if (NPCList.newSize shr 8 > yaw) {
			yaw = NPCList.newSize shr 8
		}
		if (Camera.CAM_SHAKING[4] && Shake.CAM_SHAKE_AMPLITUDE[4] + 128 > yaw) {
			yaw = Shake.CAM_SHAKE_AMPLITUDE[4] + 128
		}
		@Pc(59) val roll: Int = (Camera.camAngleY.toInt() + Camera.cameraRandomisationA) and 0x3FFF
		Camera.setCameraPosition(yaw, Scene.tileHeightForAbsolutePosition(PlayerUpdate.localPlayer!!.x, PlayerUpdate.currentLevel, PlayerUpdate.localPlayer!!.z) - 200, roll, Camera.cameraBaseZ, areaHeight, Camera.cameraBaseX, ((yaw shr 3) * 3 + 600) shl 2)
		@Pc(110) val progress: Float = 1.0f - ((100 - cyclesSinceSmooth) * (100 - cyclesSinceSmooth) * (100 - cyclesSinceSmooth)).toFloat() / 1000000.0f
		Camera.cameraPositionAbsoluteX = (Camera.transitionStartX.toFloat() + progress * (Camera.cameraPositionAbsoluteX - Camera.transitionStartX).toFloat()).toInt()
		Camera.cameraPositionAbsoluteZ = ((Camera.cameraPositionAbsoluteZ - Camera.transitionStartHeight).toFloat() * progress + Camera.transitionStartHeight.toFloat()).toInt()
		Camera.cameraPitch = (Camera.transitionStartPitch.toFloat() + (Camera.cameraPitch - Camera.transitionStartPitch).toFloat() * progress).toInt()
		Camera.cameraPositionAbsoluteY = (Camera.transitionStartY.toFloat() + progress * (Camera.cameraPositionAbsoluteY - Camera.transitionStartY).toFloat()).toInt()
		@Pc(161) var rollDelta: Int = Camera.cameraYaw - Camera.transitionStartRoll
		if (rollDelta > 8192) {
			rollDelta -= 16384
		} else if (rollDelta < -8192) {
			rollDelta += 16384
		}
		Camera.cameraYaw = (rollDelta.toFloat() * progress + Camera.transitionStartRoll.toFloat()).toInt()
		Camera.cameraYaw = Camera.cameraYaw and 0x3FFF
	}

	@OriginalMember(owner = "client!bb", name = "I", descriptor = "I")
	@JvmField
	var chatMessageHashIndex: Int = 0

	@OriginalMember(owner = "client!nm", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun reset() {
		@Pc(3) var i: Int = 0
		while (i < Shake.DIRECTIONS) {
			Camera.CAM_SHAKING[i] = false
			i++
		}
		Camera.roofRemovalTargetY = -1
		Camera.roofRemovalTargetX = -1
		Camera.cameraCalculationType = CameraMode.MODE_DEFAULT
		Camera.cameraPositionPathIndex = -1
		Camera.lookAtVariableSpeed = 0
		ColourUtils.cameraLookAtPathIndex = -1
		Camera.lookAtConstantSpeed = 0
	}
	@OriginalMember(owner = "client!fk", name = "x", descriptor = "I")
	@JvmField
	var worldMapViewMinX: Int = 0

	@OriginalMember(owner = "client!kfa", name = "v", descriptor = "I")
	@JvmField
	var fps: Int = 0

	@OriginalMember(owner = "client!u", name = "j", descriptor = "I")
	@JvmField
	var transitionStartRoll: Int = 0

	@OriginalMember(owner = "client!f", name = "f", descriptor = "I")
	@JvmField
	var transitionStartY: Int = 0

	@OriginalMember(owner = "client!fea", name = "ab", descriptor = "I")
	@JvmField
	var transitionStartX: Int = 0

	@OriginalMember(owner = "client!gda", name = "z", descriptor = "I")
	@JvmField
	var cameraTargetAbsZ: Int = 0

	@OriginalMember(owner = "client!uba", name = "w", descriptor = "I")
	@JvmField
	var sceneLength: Int = 0
	@OriginalMember(owner = "client!ci", name = "a", descriptor = "F")
	@JvmField
	var camAngleY: Float = 0.0f

	@OriginalMember(owner = "client!vh", name = "g", descriptor = "I")
	@JvmField
	var transitionStartPitch: Int = 0

	@OriginalMember(owner = "client!er", name = "r", descriptor = "I")
	@JvmField
	var cameraPositionAbsoluteX: Int = 0

	@OriginalMember(owner = "client!aq", name = "l", descriptor = "I")
	@JvmField
	var cameraBaseX: Int = 0
	@OriginalMember(owner = "client!ufa", name = "d", descriptor = "I")
	@JvmField
	var cameraRandomisationV: Int = 0

	@OriginalMember(owner = "client!rp", name = "Y", descriptor = "I")
	@JvmField
	var cameraCalculationType: Int = 0
	@OriginalMember(owner = "client!fv", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun startSmoothTransition() {
		@Pc(7) var i: Int = 0
		while (i < 5) {
			Camera.CAM_SHAKING[i] = false
			i++
		}
		Camera.transitionStartY = Camera.cameraPositionAbsoluteY
		Camera.transitionStartPitch = Camera.cameraPitch
		Camera.transitionStartHeight = Camera.cameraPositionAbsoluteZ
		Camera.lastSmoothTransitionCycle = ClientState.clientCycle
		Camera.roofRemovalTargetY = -1
		Camera.roofRemovalTargetX = -1
		Camera.transitionStartX = Camera.cameraPositionAbsoluteX
		Camera.lookAtVariableSpeed = 0
		Camera.transitionStartRoll = Camera.cameraYaw
		Camera.lookAtConstantSpeed = 0
		Camera.cameraCalculationType = CameraMode.MODE_SMOOTH_RESET
		Camera.cameraPositionPathIndex = -1
		ColourUtils.cameraLookAtPathIndex = -1
	}
	@OriginalMember(owner = "client!vg", name = "a", descriptor = "(IJ)V")
	@JvmStatic
	fun updateFollowPlayerCamera(@OriginalArg(1) multiplier: Long) {
		@Pc(10) val x: Int = Camera.cameraRandomisationX + PlayerUpdate.localPlayer!!.x
		@Pc(15) val z: Int = Camera.cameraRandomisationV + PlayerUpdate.localPlayer!!.z
		if (Camera.cameraBaseX - x < -2000 || Camera.cameraBaseX - x > 2000 || Camera.cameraBaseZ - z < -2000 || Camera.cameraBaseZ - z > 2000) {
			Camera.cameraBaseZ = z
			Camera.cameraBaseX = x
		}
		@Pc(57) var offsetFromCamera: Int
		@Pc(65) var offset: Int
		if (x != Camera.cameraBaseX) {
			offsetFromCamera = x - Camera.cameraBaseX
			offset = (offsetFromCamera.toLong() * multiplier / 320L).toInt()
			if (offsetFromCamera <= 0) {
				if (offset == 0) {
					offset = -1
				} else if (offsetFromCamera > offset) {
					offset = offsetFromCamera
				}
			} else if (offset == 0) {
				offset = 1
			} else if (offsetFromCamera < offset) {
				offset = offsetFromCamera
			}
			Camera.cameraBaseX += offset
		}
		Camera.camAngleY += Camera.cameraYawVelocity * multiplier.toFloat() / 6.0f
		if (Camera.cameraBaseZ != z) {
			offsetFromCamera = z - Camera.cameraBaseZ
			offset = (offsetFromCamera.toLong() * multiplier / 320L).toInt()
			if (offsetFromCamera > 0) {
				if (offset == 0) {
					offset = 1
				} else if (offsetFromCamera < offset) {
					offset = offsetFromCamera
				}
			} else if (offset == 0) {
				offset = -1
			} else if (offsetFromCamera > offset) {
				offset = offsetFromCamera
			}
			Camera.cameraBaseZ += offset
		}
		Camera.camAngleX += Camera.cameraPitchVelocity * multiplier.toFloat() / 6.0f
		Camera.processCamera()
	}

	@OriginalMember(owner = "client!qq", name = "g", descriptor = "I")
	@JvmField
	var cameraBaseZ: Int = 0

	@OriginalMember(owner = "client!wd", name = "M", descriptor = "I")
	@JvmField
	var cameraPositionAbsoluteY: Int = 0

	@OriginalMember(owner = "client!vn", name = "K", descriptor = "I")
	@JvmField
	var moveToConstantSpeed: Int = 0

	@OriginalMember(owner = "client!ve", name = "s", descriptor = "I")
	@JvmField
	var gameAreaHeight: Int = 0

	@OriginalMember(owner = "client!bm", name = "b", descriptor = "(B)V")
	@JvmStatic
	fun calculateCameraPosition() {
		@Pc(9) var targetX: Int = Camera.moveToLocalX * 512 + 256
		@Pc(15) var targetY: Int = Camera.moveToLocalY * 512 + 256
		@Pc(24) var targetZ: Int = Scene.tileHeightForAbsolutePosition(targetX, PlayerUpdate.currentLevel, targetY) - Camera.moveToHeightOffset
		if (Camera.moveToVariableSpeed >= 100) {
			Camera.cameraPositionAbsoluteY = Camera.moveToLocalY * 512 + 256
			Camera.cameraPositionAbsoluteX = Camera.moveToLocalX * 512 + 256
			Camera.cameraPositionAbsoluteZ = Scene.tileHeightForAbsolutePosition(Camera.cameraPositionAbsoluteX, PlayerUpdate.currentLevel, Camera.cameraPositionAbsoluteY) - Camera.moveToHeightOffset
		} else {
			if (Camera.cameraPositionAbsoluteX < targetX) {
				Camera.cameraPositionAbsoluteX += Camera.moveToConstantSpeed + Camera.moveToVariableSpeed * (targetX - Camera.cameraPositionAbsoluteX) / 1000
				if (Camera.cameraPositionAbsoluteX > targetX) {
					Camera.cameraPositionAbsoluteX = targetX
				}
			}
			if (targetX < Camera.cameraPositionAbsoluteX) {
				Camera.cameraPositionAbsoluteX -= Camera.moveToConstantSpeed + Camera.moveToVariableSpeed * (Camera.cameraPositionAbsoluteX - targetX) / 1000
				if (targetX > Camera.cameraPositionAbsoluteX) {
					Camera.cameraPositionAbsoluteX = targetX
				}
			}
			if (targetZ > Camera.cameraPositionAbsoluteZ) {
				Camera.cameraPositionAbsoluteZ += (targetZ - Camera.cameraPositionAbsoluteZ) * Camera.moveToVariableSpeed / 1000 + Camera.moveToConstantSpeed
				if (targetZ < Camera.cameraPositionAbsoluteZ) {
					Camera.cameraPositionAbsoluteZ = targetZ
				}
			}
			if (targetY > Camera.cameraPositionAbsoluteY) {
				Camera.cameraPositionAbsoluteY += Camera.moveToConstantSpeed + (targetY - Camera.cameraPositionAbsoluteY) * Camera.moveToVariableSpeed / 1000
				if (Camera.cameraPositionAbsoluteY > targetY) {
					Camera.cameraPositionAbsoluteY = targetY
				}
			}
			if (Camera.cameraPositionAbsoluteZ > targetZ) {
				Camera.cameraPositionAbsoluteZ -= (Camera.cameraPositionAbsoluteZ - targetZ) * Camera.moveToVariableSpeed / 1000 + Camera.moveToConstantSpeed
				if (Camera.cameraPositionAbsoluteZ < targetZ) {
					Camera.cameraPositionAbsoluteZ = targetZ
				}
			}
			if (Camera.cameraPositionAbsoluteY > targetY) {
				Camera.cameraPositionAbsoluteY -= Camera.moveToConstantSpeed + (Camera.cameraPositionAbsoluteY - targetY) * Camera.moveToVariableSpeed / 1000
				if (targetY > Camera.cameraPositionAbsoluteY) {
					Camera.cameraPositionAbsoluteY = targetY
				}
			}
		}
		targetY = Camera.lookAtLocalY * 512 + 256
		targetX = Camera.lookAtLocalX * 512 + 256
		targetZ = Scene.tileHeightForAbsolutePosition(targetX, PlayerUpdate.currentLevel, targetY) - Camera.lookAtHeightOffset
		@Pc(229) val deltaX: Int = targetX - Camera.cameraPositionAbsoluteX
		@Pc(234) val deltaZ: Int = targetZ - Camera.cameraPositionAbsoluteZ
		@Pc(239) val deltaY: Int = targetY - Camera.cameraPositionAbsoluteY
		@Pc(251) val horizontalDistance: Int = kotlin.math.sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toInt()
		@Pc(262) var targetPitch: Int = (kotlin.math.atan2(deltaZ.toDouble(), horizontalDistance.toDouble()) * 2607.5945876176133).toInt() and 0x3FFF
		if (targetPitch < 1024) {
			targetPitch = 1024
		}
		@Pc(283) val targetYaw: Int = (-2607.5945876176133 * kotlin.math.atan2(deltaX.toDouble(), deltaY.toDouble())).toInt() and 0x3FFF
		if (targetPitch > 3072) {
			targetPitch = 3072
		}
		if (Camera.cameraPitch < targetPitch) {
			Camera.cameraPitch += (Camera.lookAtVariableSpeed * ((targetPitch - Camera.cameraPitch) shr 3) / 1000 + Camera.lookAtConstantSpeed) shl 3
			if (Camera.cameraPitch > targetPitch) {
				Camera.cameraPitch = targetPitch
			}
		}
		if (targetPitch < Camera.cameraPitch) {
			Camera.cameraPitch -= (((Camera.cameraPitch - targetPitch) shr 3) * Camera.lookAtVariableSpeed / 1000 + Camera.lookAtConstantSpeed) shl 3
			if (targetPitch > Camera.cameraPitch) {
				Camera.cameraPitch = targetPitch
			}
		}
		@Pc(358) var yawDelta: Int = targetYaw - Camera.cameraYaw
		if (yawDelta > 8192) {
			yawDelta -= 16384
		}
		if (yawDelta < -8192) {
			yawDelta += 16384
		}
		yawDelta = yawDelta shr 0x3
		if (yawDelta > 0) {
			Camera.cameraYaw += (Camera.lookAtConstantSpeed + Camera.lookAtVariableSpeed * yawDelta / 1000) shl 3
			Camera.cameraYaw = Camera.cameraYaw and 0x3FFF
		}
		if (yawDelta < 0) {
			Camera.cameraYaw -= (Camera.lookAtConstantSpeed + -yawDelta * Camera.lookAtVariableSpeed / 1000) shl 3
			Camera.cameraYaw = Camera.cameraYaw and 0x3FFF
		}
		@Pc(415) var remainingYawDelta: Int = targetYaw - Camera.cameraYaw
		if (remainingYawDelta > 8192) {
			remainingYawDelta -= 16384
		}
		if (remainingYawDelta < -8192) {
			remainingYawDelta += 16384
		}
		Camera.cameraRoll = 0
		if (remainingYawDelta < 0 && yawDelta > 0 || remainingYawDelta > 0 && yawDelta < 0) {
			Camera.cameraYaw = targetYaw
		}
	}
	@OriginalMember(owner = "client!ks", name = "j", descriptor = "F")
	@JvmField
	var camAngleX: Float = 1024.0f
	@OriginalMember(owner = "client!nd", name = "r", descriptor = "I")
	@JvmField
	var cameraSplineProgress: Int = 0

	@OriginalMember(owner = "client!wr", name = "d", descriptor = "I")
	@JvmField
	var cameraPositionAbsoluteZ: Int = 0

	@OriginalMember(owner = "client!qb", name = "b", descriptor = "[Z")
	@JvmField
	val CAM_SHAKING: BooleanArray = BooleanArray(5)

	@OriginalMember(owner = "client!ui", name = "j", descriptor = "I")
	@JvmField
	var cameraLookAtKeyframe: Int = 0
	@OriginalMember(owner = "client!wp", name = "c", descriptor = "I")
	@JvmField
	var cameraStepA: Int = 1

	@OriginalMember(owner = "client!rfa", name = "c", descriptor = "F")
	@JvmField
	var cameraYawVelocity: Float = 0.0f

	@OriginalMember(owner = "client!dia", name = "f", descriptor = "I")
	@JvmField
	var cameraSplineSpeedEnd: Int = 0

	@OriginalMember(owner = "client!qt", name = "q", descriptor = "I")
	@JvmField
	var cameraSplineSpeedStart: Int = 0

	@OriginalMember(owner = "client!tq", name = "r", descriptor = "I")
	@JvmField
	var transitionStartHeight: Int = 0

	@OriginalMember(owner = "client!vb", name = "c", descriptor = "I")
	@JvmField
	var moveToLocalY: Int = 0

	@OriginalMember(owner = "client!cp", name = "a", descriptor = "S")
	@JvmField
	var cameraZoomMax: Short = 320

	@OriginalMember(owner = "client!bn", name = "c", descriptor = "S")
	@JvmField
	var viewportFovMax: Short = 205

	@OriginalMember(owner = "client!hfa", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun applyCameraYawLeft() {
		if (!Camera.yawKeyHeld) {
			Camera.yawKeyHeld = true
			Camera.cameraYawVelocity += (-Camera.cameraYawVelocity - 24.0f) / 2.0f
			Camera.sendCameraAngle = true
		}
	}

	@OriginalMember(owner = "client!mr", name = "b", descriptor = "S")
	@JvmField
	var viewportFovMin: Short = 256

	@OriginalMember(owner = "client!vv", name = "C", descriptor = "[[[I")
	@JvmField
	val cameraSplinePaths: Array<Array<IntArray?>?> = arrayOfNulls<Array<IntArray?>>(2)

	@OriginalMember(owner = "client!vea", name = "d", descriptor = "I")
	@JvmField
	var lookAtLocalY: Int = 0

	@OriginalMember(owner = "client!qt", name = "n", descriptor = "I")
	@JvmField
	var cameraRandomisationA: Int = 0

	@OriginalMember(owner = "client!ng", name = "d", descriptor = "I")
	@JvmField
	var lookAtHeightOffset: Int = 0

	@OriginalMember(owner = "client!lga", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun calculateCutsceneCameraPosition() {
		if (Camera.cameraPositionPathIndex == -1 || ColourUtils.cameraLookAtPathIndex == -1) {
			return
		}
		@Pc(23) val speed: Int = ((Camera.cameraSplineProgress * (Camera.cameraSplineSpeedEnd - Camera.cameraSplineSpeedStart)) shr 16) + Camera.cameraSplineSpeedStart
		Camera.cameraSplineProgress += speed
		if (Camera.cameraSplineProgress < 65535) {
			Camera.cameraSplineFinished = false
			Camera.cutsceneCameraPreviouslyCompleted = false
		} else {
			if (Camera.cutsceneCameraPreviouslyCompleted) {
				Camera.cameraSplineFinished = false
			} else {
				Camera.cameraSplineFinished = true
			}
			Camera.cameraSplineProgress = 65535
			Camera.cutsceneCameraPreviouslyCompleted = true
		}
		@Pc(55) val t: Float = Camera.cameraSplineProgress.toFloat() / 65535.0f
		@Pc(58) val position: FloatArray = FloatArray(3)
		@Pc(62) val keyframeOffset: Int = Camera.cameraPositionKeyframe * 2
		@Pc(88) var local88: Int
		@Pc(121) var local121: Int
		@Pc(133) var local133: Int
		@Pc(143) var local143: Int
		@Pc(161) var local161: Int
		@Pc(64) var i: Int = 0
		while (i < 3) {
			@Pc(76) val scaledStart: Int = Camera.cameraSplinePaths[Camera.cameraPositionPathIndex]!![keyframeOffset]!![i] * 3
			val scaledNext: Int = Camera.cameraSplinePaths[Camera.cameraPositionPathIndex]!![keyframeOffset + 1]!![i] * 3
			val scaledEndTangent: Int = (Camera.cameraSplinePaths[Camera.cameraPositionPathIndex]!![keyframeOffset + 2]!![i] + Camera.cameraSplinePaths[Camera.cameraPositionPathIndex]!![keyframeOffset + 2]!![i] - Camera.cameraSplinePaths[Camera.cameraPositionPathIndex]!![keyframeOffset - -3]!![i]) * 3
			@Pc(129) val c0: Int = Camera.cameraSplinePaths[Camera.cameraPositionPathIndex]!![keyframeOffset]!![i]
			val c1: Int = scaledNext - scaledStart
			val c2: Int = scaledStart + scaledEndTangent - scaledNext * 2
			val c3: Int = Camera.cameraSplinePaths[Camera.cameraPositionPathIndex]!![keyframeOffset + 2]!![i] + scaledNext - c0 - scaledEndTangent
			position[i] = c0.toFloat() + t * (c1.toFloat() + t * (t * c3.toFloat() + c2.toFloat()))
			i++
		}
		Camera.cameraPositionAbsoluteX = position[0].toInt() - MapRegion.sceneBaseX * 512
		Camera.cameraPositionAbsoluteZ = position[1].toInt() * -1
		Camera.cameraPositionAbsoluteY = position[2].toInt() - MapRegion.sceneBaseY * 512
		@Pc(214) val lookAt: FloatArray = FloatArray(3)
		val lookAtKeyframeOffset: Int = Camera.cameraLookAtKeyframe * 2
		var axis: Int = 0
		while (axis < 3) {
			val scaledStart: Int = Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex]!![lookAtKeyframeOffset]!![axis] * 3
			val scaledNext: Int = Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex]!![lookAtKeyframeOffset + 1]!![axis] * 3
			val scaledEndTangent: Int = (Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex]!![lookAtKeyframeOffset + 2]!![axis] + Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex]!![lookAtKeyframeOffset + 2]!![axis] - Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex]!![lookAtKeyframeOffset + 3]!![axis]) * 3
			@Pc(293) val c0: Int = Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex]!![lookAtKeyframeOffset]!![axis]
			@Pc(298) val c1: Int = scaledNext - scaledStart
			@Pc(307) val c2: Int = scaledStart + scaledEndTangent - scaledNext * 2
			@Pc(325) val c3: Int = scaledNext + Camera.cameraSplinePaths[ColourUtils.cameraLookAtPathIndex]!![lookAtKeyframeOffset + 2]!![axis] - scaledEndTangent - c0
			lookAt[axis] = t * (c1.toFloat() + t * (c3.toFloat() * t + c2.toFloat())) + c0.toFloat()
			axis++
		}
		@Pc(360) val deltaX: Float = lookAt[0] - position[0]
		@Pc(371) val deltaY: Float = -1.0f * (lookAt[1] - position[1])
		@Pc(380) val deltaZ: Float = lookAt[2] - position[2]
		@Pc(390) val horizontalDistance: Double = kotlin.math.sqrt((deltaX * deltaX + deltaZ * deltaZ).toDouble())
		Camera.cameraPitch = (kotlin.math.atan2(deltaY.toDouble(), horizontalDistance) * 2607.5945876176133).toInt() and 0x3FFF
		Camera.cameraYaw = (-kotlin.math.atan2(deltaX.toDouble(), deltaZ.toDouble()) * 2607.5945876176133).toInt() and 0x3FFF
		Camera.cameraRoll = Camera.cameraSplinePaths[Camera.cameraPositionPathIndex]!![keyframeOffset]!![3] + (((Camera.cameraSplinePaths[Camera.cameraPositionPathIndex]!![keyframeOffset + 2]!![3] - Camera.cameraSplinePaths[Camera.cameraPositionPathIndex]!![keyframeOffset]!![3]) * Camera.cameraSplineProgress) shr 16)
	}

	@OriginalMember(owner = "client!lm", name = "B", descriptor = "I")
	@JvmField
	var moveToHeightOffset: Int = 0

	@OriginalMember(owner = "client!vb", name = "e", descriptor = "[I")
	@JvmField
	val CAM_SHAKE_JITTER: IntArray = IntArray(5)

	@OriginalMember(owner = "client!gl", name = "l", descriptor = "I")
	@JvmField
	var cameraYaw: Int = 0

	@OriginalMember(owner = "client!gs", name = "a", descriptor = "I")
	@JvmField
	var roofRemovalTargetX: Int = 0

	@OriginalMember(owner = "client!fu", name = "A", descriptor = "Z")
	@JvmField
	var sendCameraAngle: Boolean = true
	@OriginalMember(owner = "client!aha", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun applyCameraPitchDown() {
		if (!Camera.pitchKeyHeld) {
			Camera.pitchKeyHeld = true
			Camera.sendCameraAngle = true
			Camera.cameraPitchVelocity += (-12.0f - Camera.cameraPitchVelocity) / 2.0f
		}
	}

	@OriginalMember(owner = "client!gh", name = "Ob", descriptor = "Z")
	@JvmField
	var orthoCameraLock: Boolean = true

	@OriginalMember(owner = "client!qia", name = "H", descriptor = "S")
	@JvmField
	var fovScaleMin: Short = 1

	@OriginalMember(owner = "client!lba", name = "a", descriptor = "(BJ)V")
	@JvmStatic
	fun updateOrbitTargetCamera(@OriginalArg(1) elapsedMillis: Long) {
		@Pc(7) val cameraTargetAbsX: Int = Camera.cameraTargetAbsX
		@Pc(18) var delta: Int
		@Pc(26) var step: Int
		if (Camera.cameraBaseX != cameraTargetAbsX) {
			delta = cameraTargetAbsX - Camera.cameraBaseX
			step = (delta.toLong() * elapsedMillis / 320L).toInt()
			if (delta <= 0) {
				if (step == 0) {
					step = -1
				} else if (delta > step) {
					step = delta
				}
			} else if (step == 0) {
				step = 1
			} else if (step > delta) {
				step = delta
			}
			Camera.cameraBaseX += step
		}
		@Pc(72) val targetAbsZ: Int = Camera.cameraTargetAbsZ
		Camera.camAngleX += Camera.cameraPitchVelocity * elapsedMillis.toFloat() / 40.0f * 8.0f
		Camera.camAngleY += Camera.cameraYawVelocity * elapsedMillis.toFloat() / 40.0f * 8.0f
		if (Camera.cameraBaseZ != targetAbsZ) {
			delta = targetAbsZ - Camera.cameraBaseZ
			step = (elapsedMillis * delta.toLong() / 320L).toInt()
			if (delta <= 0) {
				if (step == 0) {
					step = -1
				} else if (step < delta) {
					step = delta
				}
			} else if (step == 0) {
				step = 1
			} else if (delta < step) {
				step = delta
			}
			Camera.cameraBaseZ += step
		}
		Camera.processCamera()
	}

	@OriginalMember(owner = "client!wb", name = "p", descriptor = "I")
	@JvmField
	var pickingCameraPitch: Int = 0

	@OriginalMember(owner = "client!el", name = "g", descriptor = "I")
	@JvmField
	var lookAtLocalX: Int = 0

	@OriginalMember(owner = "client!gba", name = "r", descriptor = "I")
	@JvmField
	var cameraTargetAbsX: Int = 0

	@OriginalMember(owner = "client!rq", name = "V", descriptor = "I")
	@JvmField
	var cameraPositionKeyframe: Int = 0

	@OriginalMember(owner = "client!ri", name = "u", descriptor = "I")
	@JvmField
	var moveToLocalX: Int = 0

	@OriginalMember(owner = "client!gh", name = "Tb", descriptor = "I")
	@JvmField
	var lastSmoothTransitionCycle: Int = 0

	@OriginalMember(owner = "client!mc", name = "g", descriptor = "I")
	@JvmField
	var viewportScaledFov: Int = 0

	@OriginalMember(owner = "client!q", name = "y", descriptor = "I")
	@JvmField
	var moveToVariableSpeed: Int = 0

	@OriginalMember(owner = "client!ia", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun applyCameraPitchUp() {
		if (!Camera.pitchKeyHeld) {
			Camera.sendCameraAngle = true
			Camera.cameraPitchVelocity += (12.0f - Camera.cameraPitchVelocity) / 2.0f
			Camera.pitchKeyHeld = true
		}
	}

	@OriginalMember(owner = "client!ai", name = "w", descriptor = "I")
	@JvmField
	var cameraPositionPathIndex: Int = -1
	@OriginalMember(owner = "client!pa", name = "b", descriptor = "Z")
	@JvmField
	var yawKeyHeld: Boolean = false

	@OriginalMember(owner = "client!av", name = "B", descriptor = "I")
	@JvmField
	var lookAtVariableSpeed: Int = 0

	@OriginalMember(owner = "client!ah", name = "b", descriptor = "I")
	@JvmField
	var cameraStepV: Int = 2

	@OriginalMember(owner = "client!gga", name = "e", descriptor = "(I)V")
	@JvmStatic
	fun processCamera() {
		@Pc(7) var minAngleX: Short = 1024
		@Pc(9) var maxAngleX: Short = 3072
		if (OrthoMode.orthographicProjection) {
			maxAngleX = 4096
			if (Camera.orthoCameraLock) {
				minAngleX = 2048
			}
		}
		if (Camera.camAngleX < minAngleX.toFloat()) {
			Camera.camAngleX = minAngleX.toFloat()
		}
		if (Camera.camAngleX > maxAngleX.toFloat()) {
			Camera.camAngleX = maxAngleX.toFloat()
		}
		while (Camera.camAngleY >= 16384.0f) {
			Camera.camAngleY -= 16384.0f
		}
		while (Camera.camAngleY < 0.0f) {
			Camera.camAngleY += 16384.0f
		}
		@Pc(57) val cameraTileX: Int = Camera.cameraBaseX shr 9
		@Pc(61) val cameraTileZ: Int = Camera.cameraBaseZ shr 9
		@Pc(67) val cameraFloorHeight: Int = Scene.tileHeightForAbsolutePosition(Camera.cameraBaseX, PlayerUpdate.currentLevel, Camera.cameraBaseZ)
		@Pc(69) var maxHeightDiff: Int = 0
		@Pc(91) var local91: Int
		if (cameraTileX > 3 && cameraTileZ > 3 && MapRegion.sceneSizeX - 4 > cameraTileX && MapRegion.sceneSizeY - 4 > cameraTileZ) {
			var tileX: Int = cameraTileX - 4
			while (tileX <= cameraTileX + 4) {
				@Pc(97) var i: Int = cameraTileZ - 4
				while (i <= cameraTileZ + 4) {
					@Pc(101) var level: Int = PlayerUpdate.currentLevel
					if (level < 3 && SettingsBits.isBridge(i, tileX)) {
						level++
					}
					@Pc(114) var cameraHeightOffset: Int = 0
					if (MapRegion.aClass322_Sub1_1!!.cameraHeights != null && MapRegion.aClass322_Sub1_1!!.cameraHeights!![level] != null) {
						cameraHeightOffset = ((MapRegion.aClass322_Sub1_1!!.cameraHeights!![level]!![tileX]!![i].toInt() and 0xFF) * 8) shl 2
					}
					if (Scene.aClass65Array1 != null && Scene.aClass65Array1!![level] != null) {
						@Pc(156) val heightDiff: Int = cameraFloorHeight + cameraHeightOffset - Scene.aClass65Array1!![level]!!.getHeight(i, tileX)
						if (maxHeightDiff < heightDiff) {
							maxHeightDiff = heightDiff
						}
					}
					i++
				}
				tileX++
			}
		}
		var targetPitchLimit: Int = (maxHeightDiff shr 2) * 1536
		if (targetPitchLimit > 786432) {
			targetPitchLimit = 786432
		}
		if (targetPitchLimit < 262144) {
			targetPitchLimit = 262144
		}
		if (targetPitchLimit > NPCList.newSize) {
			NPCList.newSize += (targetPitchLimit - NPCList.newSize) / 24
		} else if (NPCList.newSize > targetPitchLimit) {
			NPCList.newSize += (targetPitchLimit - NPCList.newSize) / 80
		}
	}

	@OriginalMember(owner = "client!og", name = "o", descriptor = "I")
	@JvmField
	var cameraAngleSendCooldown: Int = 0

	@OriginalMember(owner = "client!jo", name = "a", descriptor = "(IIIIIII)V")
	@JvmStatic
	fun storePickingCamera(@OriginalArg(0) rotateX: Int, @OriginalArg(1) y: Int, @OriginalArg(2) z: Int, @OriginalArg(4) rotateY: Int, @OriginalArg(5) x: Int, @OriginalArg(6) rotateZ: Int) {
		Camera.pickingCameraRoll = rotateZ
		Camera.pickingCameraX = x
		Camera.pickingCameraZ = z
		Camera.pickingCameraPitch = rotateX
		Camera.pickingCameraYaw = rotateY
		Camera.pickingCameraY = y
	}

	@OriginalMember(owner = "client!hg", name = "a", descriptor = "(IIIIIIII)V")
	@JvmStatic
	fun setCameraPosition(@OriginalArg(0) rotateX: Int, @OriginalArg(1) z: Int, @OriginalArg(2) rotateY: Int, @OriginalArg(3) y: Int, @OriginalArg(4) areaHeight: Int, @OriginalArg(6) x: Int, @OriginalArg(7) distance: Int) {
		@Pc(7) var areaHeightDelta: Int = areaHeight - 334
		if (areaHeightDelta < 0) {
			areaHeightDelta = 0
		} else if (areaHeightDelta > 100) {
			areaHeightDelta = 100
		}
		@Pc(31) val zoom: Int = areaHeightDelta * (Camera.cameraZoomMax - Camera.cameraZoomMin) / 100 + Camera.cameraZoomMin
		@Pc(37) val scaledDistance: Int = (distance * zoom) shr 8
		HintArrow.scaledOrthoZoom = (OrthoMode.orthoZoom * zoom) shr 8
		@Pc(49) val rotationX: Int = (16384 - rotateX) and 0x3FFF
		@Pc(55) val rotationZ: Int = (16384 - rotateY) and 0x3FFF
		@Pc(62) var deltaX: Int = 0
		@Pc(64) var deltaZ: Int = 0
		@Pc(66) var deltaY: Int = scaledDistance
		if (rotationX != 0) {
			deltaZ = (-scaledDistance * TextureCombineSource.SINE[rotationX]) shr 14
			deltaY = (TextureCombineSource.COSINE[rotationX] * scaledDistance) shr 14
		}
		if (rotationZ != 0) {
			deltaX = (TextureCombineSource.SINE[rotationZ] * deltaY) shr 14
			deltaY = (deltaY * TextureCombineSource.COSINE[rotationZ]) shr 14
		}
		Camera.cameraYaw = rotateY
		Camera.cameraRoll = 0
		Camera.cameraPositionAbsoluteY = y - deltaY
		Camera.cameraPositionAbsoluteZ = z - deltaZ
		Camera.cameraPositionAbsoluteX = x - deltaX
		Camera.cameraPitch = rotateX
	}

	@OriginalMember(owner = "client!or", name = "v", descriptor = "I")
	@JvmField
	var lookAtConstantSpeed: Int = 0

	@OriginalMember(owner = "client!pl", name = "c", descriptor = "S")
	@JvmField
	var aspectFovMax: Short = 32767

	@OriginalMember(owner = "client!mk", name = "u", descriptor = "F")
	@JvmField
	var cameraPitchVelocity: Float = 0.0f

	@OriginalMember(owner = "client!pw", name = "eb", descriptor = "I")
	@JvmField
	var roofRemovalTargetY: Int = 0

	@OriginalMember(owner = "client!dha", name = "B", descriptor = "I")
	@JvmField
	var pickingCameraZ: Int = 0

	@OriginalMember(owner = "client!gb", name = "u", descriptor = "Z")
	@JvmField
	var pitchKeyHeld: Boolean = false

	@OriginalMember(owner = "client!iia", name = "j", descriptor = "I")
	@JvmField
	var pickingCameraY: Int = 0

	@OriginalMember(owner = "client!aba", name = "a", descriptor = "(IIII)V")
	@JvmStatic
	fun forceCameraAngle(@OriginalArg(0) angleX: Int, @OriginalArg(1) angleY: Int) {
		@Pc(3) val scaledAngleX: Int = angleX shl 3
		@Pc(11) val scaledAngleY: Int = angleY shl 3
		if (Camera.cameraCalculationType == CameraMode.MODE_FOLLOWPLAYER) {
			Camera.cameraYaw = scaledAngleY
			Camera.cameraRoll = 0
			Camera.cameraPitch = scaledAngleX
		}
		Camera.camAngleY = scaledAngleY.toFloat()
		Camera.camAngleX = scaledAngleX.toFloat()
		Camera.processCamera()
		Camera.sendCameraAngle = true
	}

	@OriginalMember(owner = "client!pk", name = "A", descriptor = "S")
	@JvmField
	var cameraZoomMin: Short = 256

	@OriginalMember(owner = "client!hu", name = "d", descriptor = "I")
	@JvmField
	var cameraRandomisationCycle: Int = 0

	@OriginalMember(owner = "client!eg", name = "g", descriptor = "Z")
	@JvmField
	var cameraSplineFinished: Boolean = false

	@OriginalMember(owner = "client!ea", name = "f", descriptor = "I")
	@JvmField
	var cameraRoll: Int = 0

	@OriginalMember(owner = "client!kl", name = "o", descriptor = "S")
	@JvmField
	var aspectFovMin: Short = 1

	@OriginalMember(owner = "client!iq", name = "e", descriptor = "S")
	@JvmField
	var fovScaleMax: Short = 32767
	@OriginalMember(owner = "client!em", name = "g", descriptor = "(I)V")
	@JvmStatic
	fun applyCameraYawRight() {
		if (!Camera.yawKeyHeld) {
			Camera.yawKeyHeld = true
			Camera.cameraYawVelocity += (24.0f - Camera.cameraYawVelocity) / 2.0f
			Camera.sendCameraAngle = true
		}
	}
	@OriginalMember(owner = "client!tw", name = "x", descriptor = "I")
	@JvmField
	var pickingCameraX: Int = 0

	@OriginalMember(owner = "client!un", name = "K", descriptor = "I")
	@JvmField
	var cameraStepH: Int = 2

	@OriginalMember(owner = "client!hh", name = "m", descriptor = "I")
	@JvmField
	var projectionScaleX: Int = 0

	@OriginalMember(owner = "client!hw", name = "a", descriptor = "(IIBII)V")
	@JvmStatic
	fun setProjection(@OriginalArg(0) scaleX: Int, @OriginalArg(1) scaleY: Int, @OriginalArg(3) centerX: Int, @OriginalArg(4) centerY: Int) {
		Camera.pickingProjectionCenterX = centerX
		ByteArrayPool.projectionScaleY = scaleY
		Camera.projectionScaleX = scaleX
		Camera.pickingProjectionCenterY = centerY
	}

	@OriginalMember(owner = "client!hq", name = "i", descriptor = "Z")
	@JvmField
	var cutsceneCameraPreviouslyCompleted: Boolean = false

	@OriginalMember(owner = "client!so", name = "l", descriptor = "I")
	@JvmField
	var gameAreaY: Int = 0

	@OriginalMember(owner = "client!wc", name = "n", descriptor = "I")
	@JvmField
	var gameAreaX: Int = 0

	@OriginalMember(owner = "client!dh", name = "R", descriptor = "I")
	@JvmField
	var pickingCameraRoll: Int = 0

	@OriginalMember(owner = "client!dt", name = "b", descriptor = "I")
	@JvmField
	var pickingProjectionCenterX: Int = 0

	@OriginalMember(owner = "client!hia", name = "d", descriptor = "I")
	@JvmField
	var cameraRandomisationX: Int = 0

	@OriginalMember(owner = "client!rfa", name = "d", descriptor = "I")
	@JvmField
	var pickingProjectionCenterY: Int = 0

	@OriginalMember(owner = "client!vea", name = "g", descriptor = "I")
	@JvmField
	var pickingCameraYaw: Int = 0
	@OriginalMember(owner = "client!jk", name = "K", descriptor = "I")
	@JvmField
	var cameraPitch: Int = 0
}
