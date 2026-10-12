package com.jagex.game.runetek5

import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.login.LoginManager
import com.jagex.game.runetek5.entity.BoundingBoxPolygon
import com.jagex.game.runetek5.entity.EntityNodeList
import com.jagex.game.runetek5.entity.PickableEntity
import com.jagex.game.runetek5.graphics.FlipException
import com.jagex.game.runetek5.graphics.Matrix
import com.jagex.game.runetek5.graphics.OffscreenSurface
import com.jagex.game.runetek5.graphics.Toolkit
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer
import com.jagex.game.runetek5.js5.Js5Compression
import com.jagex.game.runetek5.preference.GamePreferences
import com.jagex.game.runetek5.scene.Scene
import com.jagex.game.runetek5.statics.SceneProjectionCenterY
import com.jagex.game.runetek5.utils.Utils
import awt.Canvas
import awt.Dimension
import awt.Rectangle
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import awt.height
import awt.width

object OrthoMode {

	@OriginalMember(owner = "client!nfa", name = "c", descriptor = "I")
	@JvmField
	var drawY: Int = 0

	@OriginalMember(owner = "client!sg", name = "eb", descriptor = "I")
	@JvmField
	var worldListSize: Int = 0

	@OriginalMember(owner = "client!nr", name = "g", descriptor = "I")
	@JvmField
	var orthoZoom: Int = 7000

	@OriginalMember(owner = "client!lk", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun applyOrthographicMode() {
		OrthoMode.resetOrtho()
		@Pc(12) val mode: Int = GamePreferences.uid!!.orthographic!!.getValue()
		if (mode == 2) {
			OrthoMode.initOrthoBuffer(ClientState.activeToolkit, GameShell.screenCanvasHeight, GameShell.screenCanvasWidth)
		} else if (mode == 3) {
			OrthoMode.initOrthoTiles(ClientState.activeToolkit, GameShell.screenCanvasWidth, Js5Compression.maxSize, OrthoMode.orthoTileSizeX, GameShell.screenCanvasHeight)
		}
		if (GamePreferences.uid!!.orthographic!!.isEnabled()) {
			OrthoMode.resizeOrthoCanvas(GameShell.gameCanvas)
		}
		if (ClientState.activeToolkit != null) {
			MapRegion.calculateViewRange()
		}
		OrthoMode.orthographicProjection = GamePreferences.uid!!.orthographic!!.getValue() != 0
		OrthoMode.orthoEnabled = GamePreferences.uid!!.orthographic!!.isEnabled()
	}

	@OriginalMember(owner = "client!rga", name = "N", descriptor = "Z")
	@JvmField
	var orthoEnabled: Boolean = false

	@OriginalMember(owner = "client!iv", name = "N", descriptor = "Lclient!bca;")
	@JvmField
	var aClass30_9: Matrix? = null

	@OriginalMember(owner = "client!hv", name = "d", descriptor = "I")
	@JvmField
	var orthoCameraY: Int = 0

	@OriginalMember(owner = "client!av", name = "t", descriptor = "I")
	@JvmField
	var orthoExtraTilesX: Int = 0

	@OriginalMember(owner = "client!kha", name = "k", descriptor = "I")
	@JvmField
	var orthoScreenViewportY: Int = 0

	@OriginalMember(owner = "client!ah", name = "f", descriptor = "I")
	@JvmField
	var orthoTileHeight: Int = 0

	@OriginalMember(owner = "client!es", name = "m", descriptor = "D")
	@JvmField
	var orthoMatrix02: Double = 0.0

	@OriginalMember(owner = "client!lr", name = "H", descriptor = "D")
	@JvmField
	var orthoMatrix12: Double = 0.0

	@OriginalMember(owner = "client!naa", name = "M", descriptor = "D")
	@JvmField
	var orthoMatrix10: Double = 0.0

	@OriginalMember(owner = "client!co", name = "O", descriptor = "D")
	@JvmField
	var orthoScreenCameraZ: Double = 0.0

	@OriginalMember(owner = "client!hj", name = "a", descriptor = "(IIII)V")
	@JvmStatic
	fun setOrthoRotation(@OriginalArg(1) rotateY: Int, @OriginalArg(2) rotateZ: Int, @OriginalArg(3) rotateX: Int) {
		if (OrthoMode.orthoCameraRotateX == rotateX && rotateY == OrthoMode.orthoCameraRotateY && rotateZ == OrthoMode.orthoCameraRotateZ) {
			return
		}
		OrthoMode.orthoCameraRotateX = rotateX
		Scene.staticSceneDirty = true
		OrthoMode.orthoCameraRotateZ = rotateZ
		OrthoMode.orthoCameraRotateY = rotateY
		@Pc(35) val radiansX: Double = -((rotateX * 2).toDouble() * 3.141592653589793) / 16384.0
		@Pc(45) val radiansY: Double = -((rotateY * 2).toDouble() * 3.141592653589793) / 16384.0
		@Pc(48) val cosY: Double = kotlin.math.cos(radiansY)
		@Pc(51) val sinY: Double = kotlin.math.sin(radiansY)
		@Pc(54) val cosX: Double = kotlin.math.cos(radiansX)
		@Pc(57) val sinX: Double = kotlin.math.sin(radiansX)
		OrthoMode.orthoRotationM00 = cosY
		OrthoMode.orthoMatrix21 = sinX
		OrthoMode.orthoRotationM11 = cosX
		OrthoMode.orthoMatrix22 = cosY * cosX
		OrthoMode.orthoMatrix12 = sinX * -cosY
		OrthoMode.orthoMatrix02 = sinY
		OrthoMode.orthoRotationM01 = 0.0
		OrthoMode.orthoMatrix20 = cosX * -sinY
		OrthoMode.orthoMatrix10 = sinY * sinX
	}

	@OriginalMember(owner = "client!ea", name = "a", descriptor = "(IIIII)V")
	@JvmStatic
	fun markOrthoTilesDirty(@OriginalArg(1) minX: Int, @OriginalArg(2) maxX: Int, @OriginalArg(3) minY: Int, @OriginalArg(4) maxY: Int) {
		if (OrthoMode.orthoRenderMode != 1) {
			return
		}
		@Pc(15) var minCellX: Int = minX / OrthoMode.orthoTileWidth
		@Pc(19) var maxCellX: Int = maxX / OrthoMode.orthoTileWidth
		@Pc(23) var minCellY: Int = minY / OrthoMode.orthoTileHeight
		@Pc(27) var maxCellY: Int = maxY / OrthoMode.orthoTileHeight
		if (minCellX >= OrthoMode.orthoTilesX || maxCellX < 0 || OrthoMode.orthoTilesY <= minCellY || maxCellY < 0) {
			return
		}
		if (maxCellX >= OrthoMode.orthoTilesX) {
			maxCellX = OrthoMode.orthoTilesX - 1
		}
		if (minCellY < 0) {
			minCellY = 0
		}
		if (OrthoMode.orthoTilesY <= maxCellY) {
			maxCellY = OrthoMode.orthoTilesY - 1
		}
		if (minCellX < 0) {
			minCellX = 0
		}
		@Pc(79) var i: Int = minCellY
		while (i <= maxCellY) {
			@Pc(90) val rowOffset: Int = Utils.floorMod(i + OrthoMode.orthoTileOffsetY, OrthoMode.orthoTilesY) * OrthoMode.orthoTilesX
			@Pc(92) var j: Int = minCellX
			while (j <= maxCellX) {
				@Pc(103) val cellIndex: Int = rowOffset + Utils.floorMod(OrthoMode.orthoTileOffsetX + j, OrthoMode.orthoTilesX)
				OrthoMode.orthoTileStamps!![cellIndex] = OrthoMode.orthoTileStamp
				j++
			}
			i++
		}
	}

	@OriginalMember(owner = "client!hg", name = "r", descriptor = "Lclient!is;")
	@JvmField
	var logo: OffscreenSurface? = null

	@OriginalMember(owner = "client!iia", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun drawDirtyOrthoTiles() {
		@Pc(3) var i: Int = 0
		while (i < OrthoMode.orthoTilesY) {
			@Pc(14) val rowOffset: Int = Utils.floorMod(OrthoMode.orthoTileOffsetY + i, OrthoMode.orthoTilesY) * OrthoMode.orthoTilesX
			@Pc(16) var j: Int = 0
			while (j < OrthoMode.orthoTilesX) {
				@Pc(28) val cellIndex: Int = Utils.floorMod(j + OrthoMode.orthoTileOffsetX, OrthoMode.orthoTilesX) + rowOffset
				if (OrthoMode.orthoTileStamp == OrthoMode.orthoTileStamps!![cellIndex]) {
					OrthoMode.anInterface14Array1!![cellIndex]!!.copyToSurface(0, 0, OrthoMode.orthoTileWidth, OrthoMode.orthoTileHeight, j * OrthoMode.orthoTileWidth, OrthoMode.orthoTileHeight * i)
				}
				j++
			}
			i++
		}
	}

	@OriginalMember(owner = "client!il", name = "u", descriptor = "D")
	@JvmField
	var orthoMatrix21: Double = 0.0

	@OriginalMember(owner = "client!au", name = "a", descriptor = "(ZI)I")
	@JvmStatic
	fun getOrthoPickOffsetY(@OriginalArg(0) firstPass: Boolean): Int {
		@Pc(10) val mode: Int = OrthoMode.orthoRenderMode
		if (mode == 0) {
			return if (firstPass) 0 else OrthoMode.orthoDrawY
		} else if (mode == 1) {
			return OrthoMode.orthoDrawY
		} else if (mode == 2) {
			return 0
		} else {
			return 0
		}
	}

	@OriginalMember(owner = "client!jd", name = "G", descriptor = "I")
	@JvmField
	var orthoCameraX: Int = 0

	@OriginalMember(owner = "client!jea", name = "a", descriptor = "(IIBII)V")
	@JvmStatic
	fun expandDirtyArea(@OriginalArg(0) maxY: Int, @OriginalArg(1) minX: Int, @OriginalArg(3) maxX: Int, @OriginalArg(4) minY: Int) {
		@Pc(5) val mediumPos: Int = Packet.mediumPos
		if (mediumPos == 0) {
			return
		}
		if (mediumPos == 1) {
			OrthoMode.dirtyAreaMaxY = maxY
			OrthoMode.dirtyAreaMinX = minX
			LoginManager.dirtyAreaMinY = minY
			Packet.mediumPos = 2
			OrthoMode.dirtyAreaMaxX = maxX
		} else if (mediumPos == 2) {
			if (OrthoMode.dirtyAreaMaxX < maxX) {
				OrthoMode.dirtyAreaMaxX = maxX
			}
			if (minX < OrthoMode.dirtyAreaMinX) {
				OrthoMode.dirtyAreaMinX = minX
			}
			if (minY < LoginManager.dirtyAreaMinY) {
				LoginManager.dirtyAreaMinY = minY
			}
			if (maxY > OrthoMode.dirtyAreaMaxY) {
				OrthoMode.dirtyAreaMaxY = maxY
			}
		}
	}

	@OriginalMember(owner = "client!kfa", name = "q", descriptor = "I")
	@JvmField
	var orthoSavedViewportY: Int = 0

	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun restoreOrthoCamera() {
		OrthoMode.sceneToolkit!!.setCamera(OrthoMode.aClass30_12)
		OrthoMode.sceneToolkit!!.DA(OrthoMode.orthoSavedViewportX, OrthoMode.orthoSavedViewportY, OrthoMode.orthoSavedViewportWidth, OrthoMode.orthoSavedViewportHeight)
	}

	@OriginalMember(owner = "client!la", name = "q", descriptor = "I")
	@JvmField
	var orthoTileSizeX: Int = 100

	@OriginalMember(owner = "client!le", name = "z", descriptor = "D")
	@JvmField
	var orthoCachedCameraZ: Double = 0.0

	@OriginalMember(owner = "client!mfa", name = "a", descriptor = "(III)V")
	@JvmStatic
	fun updateOrthoSurfaceSize(@OriginalArg(1) height: Int, @OriginalArg(2) width: Int) {
		OrthoMode.orthoViewWidth = width
		OrthoMode.orthoViewHeight = height
		if (OrthoMode.orthoRenderMode == 0) {
			OrthoMode.orthoSurfaceHeight = OrthoMode.orthoBorderY * 2 + OrthoMode.orthoViewHeight
			OrthoMode.orthoSurfaceWidth = OrthoMode.orthoBorderX * 2 + OrthoMode.orthoViewWidth
		} else if (OrthoMode.orthoRenderMode == 1) {
			OrthoMode.orthoTilesX = OrthoMode.orthoExtraTilesX + OrthoMode.orthoViewWidth / OrthoMode.orthoTileWidth + 2
			OrthoMode.orthoTilesY = OrthoMode.orthoExtraTilesY + OrthoMode.orthoViewHeight / OrthoMode.orthoTileHeight + 2
			OrthoMode.orthoSurfaceWidth = OrthoMode.orthoTileWidth * OrthoMode.orthoTilesX
			OrthoMode.orthoSurfaceHeight = OrthoMode.orthoTileHeight * OrthoMode.orthoTilesY
			OrthoMode.orthoBorderX = (OrthoMode.orthoSurfaceWidth - OrthoMode.orthoViewWidth) shr 1
			OrthoMode.orthoBorderY = (OrthoMode.orthoSurfaceHeight - OrthoMode.orthoViewHeight) shr 1
		} else if (OrthoMode.orthoRenderMode == 2) {
			OrthoMode.orthoSurfaceWidth = OrthoMode.orthoViewWidth
			OrthoMode.orthoSurfaceHeight = OrthoMode.orthoViewHeight
		}
	}

	@OriginalMember(owner = "client!qba", name = "F", descriptor = "I")
	@JvmField
	var orthoSavedViewportWidth: Int = 0

	@OriginalMember(owner = "client!qr", name = "h", descriptor = "I")
	@JvmField
	var orthoScreenViewportWidth: Int = 0

	@OriginalMember(owner = "client!tm", name = "h", descriptor = "I")
	@JvmField
	var orthoTilesY: Int = 0

	@OriginalMember(owner = "client!uo", name = "jb", descriptor = "I")
	@JvmField
	var orthoTileStamp: Int = 1

	@OriginalMember(owner = "client!dba", name = "z", descriptor = "I")
	@JvmField
	var orthoTileOffsetX: Int = 0

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(B)I")
	@JvmStatic
	fun getOrthoOffsetX(): Int {
		return if (OrthoMode.orthoRenderMode == 1) OrthoMode.orthoDrawX else 0
	}

	@OriginalMember(owner = "client!eu", name = "q", descriptor = "I")
	@JvmField
	var orthoScrollY: Int = 0

	@OriginalMember(owner = "client!eu", name = "H", descriptor = "I")
	@JvmField
	var dirtyAreaMaxY: Int = 0

	@OriginalMember(owner = "client!hh", name = "a", descriptor = "(ILclient!ha;IBIIII)V")
	@JvmStatic
	fun initOrthoTiles(@OriginalArg(1) toolkit: Toolkit?, @OriginalArg(2) nameIconsId: Int, @OriginalArg(4) tileHeight: Int, @OriginalArg(5) tileWidth: Int, @OriginalArg(6) height: Int) {
		OrthoMode.sceneToolkit = toolkit
		OrthoMode.aClass30_9 = OrthoMode.sceneToolkit!!.createMatrix()
		OrthoMode.aClass30_8 = OrthoMode.sceneToolkit!!.createMatrix()
		OrthoMode.aClass30_12 = OrthoMode.sceneToolkit!!.createMatrix()
		OrthoMode.orthoExtraTilesY = 2
		OrthoMode.orthoTileOffsetX = 0
		OrthoMode.orthoTileHeight = tileHeight
		OrthoMode.orthoTileOffsetY = 0
		OrthoMode.orthoTileWidth = tileWidth
		OrthoMode.logo = null
		OrthoMode.orthoRenderMode = 1
		OrthoMode.orthoExtraTilesX = 2
		OrthoMode.resizeOrthoSurfaces(height, nameIconsId)
	}

	@OriginalMember(owner = "client!jl", name = "l", descriptor = "I")
	@JvmField
	var orthoTileWidth: Int = 0

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(BLjava/awt/Canvas;)V")
	@JvmStatic
	fun addOrthoCanvas(@OriginalArg(1) canvas: Canvas?) {
		@Pc(6) val dimension: Dimension = canvas!!.getSize()
		OrthoMode.resizeOrthoSurfaces(dimension.height, dimension.width)
		if (OrthoMode.orthoRenderMode == 1) {
			OrthoMode.sceneToolkit!!.addCanvas(canvas, OrthoMode.orthoSurfaceWidth, OrthoMode.orthoSurfaceHeight)
		} else {
			OrthoMode.sceneToolkit!!.addCanvas(canvas, OrthoMode.orthoViewWidth, OrthoMode.orthoViewHeight)
		}
	}

	@OriginalMember(owner = "client!r", name = "A", descriptor = "Lclient!bca;")
	@JvmField
	var aClass30_12: Matrix? = null

	@OriginalMember(owner = "client!r", name = "a", descriptor = "(ZD)V")
	@JvmStatic
	fun translateOrthoCameraZ(@OriginalArg(1) depthOffset: Double) {
		OrthoMode.aClass30_8!!.apply(OrthoMode.aClass30_9)
		OrthoMode.aClass30_8!!.translate(0, 0, depthOffset.toInt())
		OrthoMode.sceneToolkit!!.setCamera(OrthoMode.aClass30_8)
	}

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(ILjava/awt/Canvas;)V")
	@JvmStatic
	fun resizeOrthoCanvas(@OriginalArg(1) canvas: Canvas?) {
		@Pc(10) val dimension: Dimension = canvas!!.getSize()
		OrthoMode.resizeOrthoSurfaces(dimension.height, dimension.width)
		if (OrthoMode.orthoRenderMode == 1) {
			OrthoMode.sceneToolkit!!.resizeCanvas(canvas, OrthoMode.orthoSurfaceWidth, OrthoMode.orthoSurfaceHeight)
		} else {
			OrthoMode.sceneToolkit!!.resizeCanvas(canvas, OrthoMode.orthoViewWidth, OrthoMode.orthoViewHeight)
		}
	}

	@OriginalMember(owner = "client!jq", name = "R", descriptor = "I")
	@JvmField
	var orthoDrawY: Int = 0

	@OriginalMember(owner = "client!ai", name = "a", descriptor = "(I)I")
	@JvmStatic
	fun getOrthoSurfaceWidth(): Int {
		return if (OrthoMode.orthoRenderMode == 1) OrthoMode.orthoSurfaceWidth else OrthoMode.orthoViewWidth
	}

	@OriginalMember(owner = "client!gn", name = "m", descriptor = "I")
	@JvmField
	var orthoCachedViewportWidth: Int = 0

	@OriginalMember(owner = "client!vk", name = "b", descriptor = "I")
	@JvmField
	var orthoTileOffsetY: Int = 0

	@OriginalMember(owner = "client!nn", name = "g", descriptor = "I")
	@JvmField
	var orthoScreenViewportHeight: Int = 0

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(I)V")
	@JvmStatic
	@Throws(FlipException::class)
	fun flipOrtho() {
		if (OrthoMode.orthoRenderMode == 1) {
			OrthoMode.sceneToolkit!!.flip(OrthoMode.orthoDrawX, OrthoMode.orthoDrawY)
		} else {
			OrthoMode.sceneToolkit!!.flip(0, 0)
		}
	}

	@OriginalMember(owner = "client!tha", name = "r", descriptor = "I")
	@JvmField
	var orthoSavedViewportX: Int = 0

	@OriginalMember(owner = "client!ci", name = "b", descriptor = "I")
	@JvmField
	var dirtyAreaMaxX: Int = 0

	@OriginalMember(owner = "client!ci", name = "d", descriptor = "I")
	@JvmField
	var dirtyAreaMinX: Int = 0

	@OriginalMember(owner = "client!jp", name = "p", descriptor = "D")
	@JvmField
	var orthoMatrix20: Double = 0.0

	@OriginalMember(owner = "client!kh", name = "t", descriptor = "I")
	@JvmField
	var orthoCameraRotateX: Int = -1

	@OriginalMember(owner = "client!kk", name = "m", descriptor = "I")
	@JvmField
	var orthoExtraTilesY: Int = 0

	@OriginalMember(owner = "client!lm", name = "D", descriptor = "D")
	@JvmField
	var orthoScrollZ: Double = 0.0

	@OriginalMember(owner = "client!iha", name = "B", descriptor = "D")
	@JvmField
	var orthoMatrix22: Double = 0.0

	@OriginalMember(owner = "client!wb", name = "b", descriptor = "(B)I")
	@JvmStatic
	fun getOrthoSurfaceHeight(): Int {
		return if (OrthoMode.orthoRenderMode == 1) OrthoMode.orthoSurfaceHeight else OrthoMode.orthoViewHeight
	}

	@OriginalMember(owner = "client!ia", name = "f", descriptor = "I")
	@JvmField
	var orthoTilesX: Int = 0

	@OriginalMember(owner = "client!qga", name = "a", descriptor = "(Z)V")
	@JvmStatic
	fun disableOrthoMode() {
		OrthoMode.resetOrtho()
		OrthoMode.orthoEnabled = false
	}

	@OriginalMember(owner = "client!cn", name = "c", descriptor = "D")
	@JvmField
	var orthoRotationM01: Double = 0.0

	@OriginalMember(owner = "client!mn", name = "a", descriptor = "(ZZ)V")
	@JvmStatic
	fun applyOrthoViewport(@OriginalArg(1) dynamic: Boolean) {
		OrthoMode.aClass30_12!!.apply(OrthoMode.sceneToolkit!!.camera())
		@Pc(10) val projection: IntArray? = OrthoMode.sceneToolkit!!.Y()
		OrthoMode.orthoSavedViewportWidth = projection!![2]
		OrthoMode.orthoSavedViewportX = projection[0]
		OrthoMode.orthoSavedViewportHeight = projection[3]
		OrthoMode.orthoSavedViewportY = projection[1]
		if (dynamic) {
			OrthoMode.sceneToolkit!!.DA(OrthoMode.orthoScreenViewportX, OrthoMode.orthoScreenViewportY, OrthoMode.orthoScreenViewportWidth, OrthoMode.orthoScreenViewportHeight)
			OrthoMode.translateOrthoCameraZ(OrthoMode.orthoScreenCameraZ)
		} else {
			OrthoMode.sceneToolkit!!.DA(OrthoMode.drawY, SceneProjectionCenterY.projectionCenterY, OrthoMode.orthoCachedViewportWidth, OrthoMode.orthoCachedViewportHeight)
			OrthoMode.translateOrthoCameraZ(OrthoMode.orthoCachedCameraZ)
		}
	}

	@OriginalMember(owner = "client!ct", name = "F", descriptor = "I")
	@JvmField
	var orthoBorderY: Int = 0

	@OriginalMember(owner = "client!re", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun drawAllOrthoTiles() {
		OrthoMode.orthoTileOffsetY = 0
		OrthoMode.orthoTileOffsetX = 0
		@Pc(19) var i: Int = 0
		while (i < OrthoMode.orthoTilesY) {
			@Pc(25) val rowOffset: Int = OrthoMode.orthoTilesX * i
			@Pc(27) var j: Int = 0
			while (j < OrthoMode.orthoTilesX) {
				@Pc(34) val index: Int = rowOffset + j
				OrthoMode.anInterface14Array1!![index]!!.copyFromSurface(j * OrthoMode.orthoTileWidth, i * OrthoMode.orthoTileHeight, OrthoMode.orthoTileWidth, OrthoMode.orthoTileHeight, true)
				j++
			}
			i++
		}
	}

	@OriginalMember(owner = "client!cea", name = "Gb", descriptor = "I")
	@JvmField
	var orthoRenderMode: Int = 0

	@OriginalMember(owner = "client!sc", name = "d", descriptor = "I")
	@JvmField
	var orthoCachedViewportHeight: Int = 0

	@OriginalMember(owner = "client!lda", name = "a", descriptor = "(BII)V")
	@JvmStatic
	fun resizeOrthoSurfaces(@OriginalArg(1) height: Int, @OriginalArg(2) width: Int) {
		if (OrthoMode.sceneToolkit == null) {
			return
		}
		@Pc(17) val previousColumnCount: Int = OrthoMode.orthoTilesX
		@Pc(19) val previousRowCount: Int = OrthoMode.orthoTilesY
		OrthoMode.updateOrthoSurfaceSize(height, width)
		if (OrthoMode.orthoRenderMode == 0) {
			OrthoMode.logo = null
			OrthoMode.logo = OrthoMode.sceneToolkit!!.createOffscreenSurface(OrthoMode.sceneToolkit!!.method7268(OrthoMode.orthoSurfaceWidth, OrthoMode.orthoSurfaceHeight), OrthoMode.sceneToolkit!!.createDepthBuffer(OrthoMode.orthoSurfaceWidth, OrthoMode.orthoSurfaceHeight))
		} else if (OrthoMode.orthoRenderMode == 1 && (OrthoMode.anInterface14Array1 == null || OrthoMode.orthoTilesX != previousColumnCount || previousRowCount != OrthoMode.orthoTilesY)) {
			OrthoMode.anInterface14Array1 = arrayOfNulls<OffscreenSurface>(OrthoMode.orthoTilesY * OrthoMode.orthoTilesX)
			@Pc(64) var i: Int = 0
			while (i < OrthoMode.anInterface14Array1!!.size) {
				OrthoMode.anInterface14Array1!![i] = OrthoMode.sceneToolkit!!.createOffscreenSurface(OrthoMode.sceneToolkit!!.method7268(OrthoMode.orthoTileWidth, OrthoMode.orthoTileHeight), OrthoMode.sceneToolkit!!.createDepthBuffer(OrthoMode.orthoTileWidth, OrthoMode.orthoTileHeight))
				i++
			}
			OrthoMode.orthoTileStamps = IntArray(OrthoMode.orthoTilesX * OrthoMode.orthoTilesY)
			OrthoMode.orthoTileStamp = 1
		}
		Scene.staticSceneDirty = true
	}

	@OriginalMember(owner = "client!sj", name = "a", descriptor = "([II[[[BII[IIIZII[I[IB[IIZI)V")
	@JvmStatic
	fun drawOrthoScene(@OriginalArg(0) roofMinZ: IntArray?, @OriginalArg(2) settingBits: Array<Array<ByteArray?>?>?, @OriginalArg(3) playerTileZ: Int, @OriginalArg(4) lowestRenderedPlane: Int, @OriginalArg(5) visibleBelowDrawheight: IntArray?, @OriginalArg(6) cameraX: Int, @OriginalArg(7) cameraZ: Int, @OriginalArg(8) disableFlickeringEffects: Boolean, @OriginalArg(9) cameraY: Int, @OriginalArg(10) playerTileX: Int, @OriginalArg(11) roofMinX: IntArray?, @OriginalArg(12) roofMaxX: IntArray?, @OriginalArg(13) roofPreference: Byte, @OriginalArg(14) roofMaxZ: IntArray?, @OriginalArg(15) cycle: Int, @OriginalArg(17) orthoZoom: Int) {
		if (OrthoMode.orthoRenderMode == -1) {
			return
		}
		@Pc(12) val projection: IntArray? = OrthoMode.sceneToolkit!!.Y()
		@Pc(16) val projectionCenterX: Int = projection!![0]
		@Pc(20) val projectionCenterY: Int = projection[1]
		@Pc(24) val projectionScaleX: Int = projection[2]
		@Pc(28) val projectionScaleY: Int = projection[3]
		@Pc(30) var scaleX: Int = projectionScaleX
		@Pc(32) var scaleY: Int = projectionScaleY
		if (OrthoMode.orthoRenderMode == 1) {
			scaleX = (projectionScaleX.toDouble() * OrthoMode.orthoViewHeight.toDouble() / OrthoMode.orthoSurfaceHeight.toDouble()).toInt()
			scaleY = (OrthoMode.orthoViewHeight.toDouble() * projectionScaleY.toDouble() / OrthoMode.orthoSurfaceHeight.toDouble()).toInt()
		}
		if (!Scene.staticSceneDirty) {
			if (OrthoMode.orthoRenderMode == 1) {
				OrthoMode.drawDirtyOrthoTiles()
			}
			@Pc(67) val cameraDeltaX: Int = cameraX - OrthoMode.orthoCameraX
			@Pc(72) val cameraDeltaY: Int = cameraY - OrthoMode.orthoCameraY
			@Pc(77) val cameraDeltaZ: Int = cameraZ - OrthoMode.orthoCameraZ
			@Pc(99) val projectedX: Int = ((OrthoMode.orthoRotationM00 * cameraDeltaX.toDouble() + cameraDeltaY.toDouble() * OrthoMode.orthoRotationM01 + cameraDeltaZ.toDouble() * OrthoMode.orthoMatrix02) * scaleX.toDouble() / orthoZoom.toDouble()).toInt()
			@Pc(121) val projectedY: Int = ((cameraDeltaZ.toDouble() * OrthoMode.orthoMatrix12 + cameraDeltaX.toDouble() * OrthoMode.orthoMatrix10 + cameraDeltaY.toDouble() * OrthoMode.orthoRotationM11) * scaleY.toDouble() / orthoZoom.toDouble()).toInt()
			@Pc(136) val depthDelta: Double = cameraDeltaZ.toDouble() * OrthoMode.orthoMatrix22 + OrthoMode.orthoMatrix21 * cameraDeltaY.toDouble() + cameraDeltaX.toDouble() * OrthoMode.orthoMatrix20
			@Pc(142) val viewX: Int = projectedX + OrthoMode.orthoBorderX - OrthoMode.orthoScrollX
			@Pc(149) val viewY: Int = OrthoMode.orthoBorderY + projectedY - OrthoMode.orthoScrollY
			@Pc(153) val viewRight: Int = OrthoMode.orthoViewWidth + viewX
			@Pc(157) val viewBottom: Int = OrthoMode.orthoViewHeight + viewY
			if (viewX >= 0 && viewY >= 0 && viewRight <= OrthoMode.orthoSurfaceWidth && OrthoMode.orthoSurfaceHeight >= viewBottom || OrthoMode.orthoRenderMode == 2) {
				OrthoMode.orthoDrawX = viewX
				OrthoMode.orthoDrawY = viewY
				if (OrthoMode.orthoRenderMode == 2) {
					OrthoMode.orthoScrollZ = -depthDelta
				}
			} else if (viewRight > 0 && viewBottom > 0 && viewX < OrthoMode.orthoSurfaceWidth && OrthoMode.orthoSurfaceHeight > viewY) {
				@Pc(206) val offsetX: Int = viewX - OrthoMode.orthoBorderX
				@Pc(210) val offsetY: Int = viewY - OrthoMode.orthoBorderY
				@Pc(212) var shiftX: Int = 0
				@Pc(214) var shiftY: Int = 0
				@Pc(216) var tileShiftX: Int = 0
				@Pc(218) var tileShiftY: Int = 0
				@Pc(220) var depthShift: Double = 0.0
				if (OrthoMode.orthoRenderMode == 0) {
					depthShift = depthDelta + OrthoMode.orthoScrollZ
					shiftX = offsetX
					shiftY = offsetY
				} else if (OrthoMode.orthoRenderMode == 1) {
					tileShiftX = offsetX / OrthoMode.orthoTileWidth
					tileShiftY = offsetY / OrthoMode.orthoTileHeight
					shiftX = tileShiftX * OrthoMode.orthoTileWidth
					shiftY = tileShiftY * OrthoMode.orthoTileHeight
					depthShift = (depthDelta + OrthoMode.orthoScrollZ) * (offsetY * shiftY + shiftX * offsetX).toDouble() / (offsetY * offsetY + offsetX * offsetX).toDouble()
				}
				depthShift = -depthShift
				@Pc(283) var dirtyRowStart: Int = 0
				@Pc(285) var dirtyRowCount: Int = 0
				@Pc(287) var dirtyColumnStart: Int = 0
				@Pc(289) var partialRowStart: Int = 0
				@Pc(291) var dirtyColumnCount: Int = 0
				@Pc(298) val clearX: Int
				@Pc(303) val clearWidth: Int
				@Pc(317) val stripX: Int
				@Pc(315) val stripWidth: Int
				if (shiftX >= 0) {
					clearX = 0
					clearWidth = OrthoMode.orthoSurfaceWidth - shiftX
					if (OrthoMode.orthoRenderMode == 1) {
						dirtyColumnCount = tileShiftX
						dirtyColumnStart = OrthoMode.orthoTilesX - tileShiftX
					}
					stripWidth = shiftX
					stripX = clearWidth
				} else {
					clearWidth = OrthoMode.orthoSurfaceWidth + shiftX
					stripX = 0
					clearX = -shiftX
					stripWidth = clearX
					if (OrthoMode.orthoRenderMode == 1) {
						dirtyColumnCount = -tileShiftX
						dirtyColumnStart = 0
					}
				}
				@Pc(340) var partialRowCount: Int = 0
				@Pc(352) val clearY: Int
				@Pc(350) val clearHeight: Int
				@Pc(354) val bandY: Int
				@Pc(356) val bandHeight: Int
				@Pc(358) val verticalStripY: Int
				if (shiftY >= 0) {
					clearHeight = OrthoMode.orthoSurfaceHeight - shiftY
					clearY = 0
					bandY = clearHeight
					bandHeight = shiftY
					verticalStripY = 0
					if (OrthoMode.orthoRenderMode == 1) {
						partialRowStart = 0
						dirtyRowCount = tileShiftY
						dirtyRowStart = OrthoMode.orthoTilesY - tileShiftY
						partialRowCount = dirtyRowStart
					}
				} else {
					clearY = -shiftY
					bandY = 0
					clearHeight = OrthoMode.orthoSurfaceHeight + shiftY
					bandHeight = clearY
					verticalStripY = clearY
					if (OrthoMode.orthoRenderMode == 1) {
						dirtyRowCount = -tileShiftY
						dirtyRowStart = 0
						partialRowCount = tileShiftY + OrthoMode.orthoTilesY
						partialRowStart = dirtyRowCount
					}
				}
				@Pc(410) val linkedList: EntityNodeList = PickList.aClass182_3.entities
				@Pc(424) var j: Int
				@Pc(415) var pickableEntity: PickableEntity? = linkedList.getFirst() as PickableEntity?
				while (pickableEntity != null) {
					@Pc(420) val pickingCylinders: Array<BoundingBoxPolygon?>? = pickableEntity.boundingBoxPolygons
					@Pc(422) var offscreen: Boolean = true
					j = 0
					while (j < pickingCylinders!!.size) {
						@Pc(430) val pickingCylinder: BoundingBoxPolygon? = pickingCylinders[j]
						@Pc(433) val left: Int = pickingCylinder!!.left
						@Pc(436) val bottom: Int = pickingCylinder.bottom
						@Pc(439) val right: Int = pickingCylinder.right
						@Pc(442) val top: Int = pickingCylinder.top
						@Pc(445) val size: Int = pickingCylinder.size
						@Pc(452) val newTop: Int = top - shiftY
						pickingCylinder.top = newTop
						@Pc(460) val newLeft: Int = left - shiftX
						pickingCylinder.left = newLeft
						@Pc(468) val newRight: Int = right - shiftX
						pickingCylinder.right = newRight
						@Pc(476) val newBottom: Int = bottom - shiftY
						pickingCylinder.bottom = newBottom
						if (offscreen) {
							@Pc(489) val minX: Int = (if (newRight > newLeft) newLeft else newRight) - size
							if (OrthoMode.orthoSurfaceWidth >= minX) {
								@Pc(510) val minY: Int = (if (newBottom < newTop) newBottom else newTop) - size
								if (OrthoMode.orthoSurfaceHeight >= minY) {
									@Pc(527) val maxX: Int = (if (newLeft < newRight) newRight else newLeft) + size
									if (maxX >= 0) {
										@Pc(541) val maxY: Int = (if (newTop > newBottom) newTop else newBottom) + size
										if (maxY >= 0) {
											offscreen = false
										}
									}
								}
							}
						}
						j++
					}
					if (offscreen) {
						pickableEntity.unlinkNode()
						PickableEntity.recycle(pickableEntity)
					}
					pickableEntity = linkedList.getNext() as PickableEntity?
				}
				if (OrthoMode.orthoRenderMode == 0) {
					OrthoMode.sceneToolkit!!.swapSurface(OrthoMode.logo)
				}
				OrthoMode.sceneToolkit!!.F(-shiftX, -shiftY)
				OrthoMode.sceneToolkit!!.b(clearX, clearY, clearWidth, clearHeight, depthShift)
				OrthoMode.translateOrthoCameraZ(OrthoMode.orthoScrollZ + depthShift)
				OrthoMode.orthoCachedCameraZ = depthShift + OrthoMode.orthoScrollZ
				if (OrthoMode.orthoRenderMode == 1) {
					SceneProjectionCenterY.projectionCenterY = projectionCenterY - OrthoMode.orthoScrollY - shiftY
					OrthoMode.orthoCachedViewportHeight = scaleY
					OrthoMode.drawY = projectionCenterX - OrthoMode.orthoScrollX - shiftX
					OrthoMode.orthoCachedViewportWidth = scaleX
					OrthoMode.sceneToolkit!!.DA(OrthoMode.drawY, SceneProjectionCenterY.projectionCenterY, OrthoMode.orthoCachedViewportWidth, OrthoMode.orthoCachedViewportHeight)
				} else {
					OrthoMode.orthoCachedViewportWidth = scaleX
					SceneProjectionCenterY.projectionCenterY = projectionCenterY + OrthoMode.orthoBorderY - OrthoMode.orthoScrollY - shiftY
					OrthoMode.drawY = projectionCenterX + OrthoMode.orthoBorderX - OrthoMode.orthoScrollX - shiftX
					OrthoMode.orthoCachedViewportHeight = scaleY
					OrthoMode.sceneToolkit!!.DA(OrthoMode.drawY, SceneProjectionCenterY.projectionCenterY, OrthoMode.orthoCachedViewportWidth, OrthoMode.orthoCachedViewportHeight)
				}
				PickList.setActive(PickList.aClass182_3)
				if (bandHeight > 0) {
					OrthoMode.sceneToolkit!!.KA(0, bandY, OrthoMode.orthoSurfaceWidth, bandHeight + bandY)
					OrthoMode.sceneToolkit!!.ya()
					OrthoMode.sceneToolkit!!.GA(InterfaceComponentRenderer.deferredDragOffsetY)
					SceneDrawer.drawScene(cycle, cameraX, cameraY, cameraZ, settingBits, visibleBelowDrawheight, roofMinX, roofMaxX, roofMaxZ, roofMinZ, lowestRenderedPlane, roofPreference, playerTileX, playerTileZ, disableFlickeringEffects, orthoZoom, 1, false)
				}
				if (stripWidth > 0) {
					OrthoMode.sceneToolkit!!.KA(stripX, verticalStripY, stripX + stripWidth, verticalStripY + clearHeight)
					OrthoMode.sceneToolkit!!.ya()
					OrthoMode.sceneToolkit!!.GA(InterfaceComponentRenderer.deferredDragOffsetY)
					SceneDrawer.drawScene(cycle, cameraX, cameraY, cameraZ, settingBits, visibleBelowDrawheight, roofMinX, roofMaxX, roofMaxZ, roofMinZ, lowestRenderedPlane, roofPreference, playerTileX, playerTileZ, disableFlickeringEffects, orthoZoom, 1, false)
				}
				OrthoMode.sceneToolkit!!.la()
				PickList.resetActive()
				if (OrthoMode.orthoRenderMode == 0) {
					OrthoMode.sceneToolkit!!.restoreSurface()
				}
				OrthoMode.orthoScrollY += shiftY
				OrthoMode.orthoScrollX += shiftX
				OrthoMode.orthoScrollZ += depthShift
				OrthoMode.orthoDrawY = projectedY + OrthoMode.orthoBorderY - OrthoMode.orthoScrollY
				OrthoMode.orthoDrawX = OrthoMode.orthoBorderX + projectedX - OrthoMode.orthoScrollX
				if (OrthoMode.orthoRenderMode == 1) {
					OrthoMode.orthoTileOffsetX += tileShiftX
					OrthoMode.orthoTileOffsetY += tileShiftY
					@Pc(787) var i: Int = 0
					while (i < OrthoMode.orthoTilesY) {
						@Pc(799) val rowOffset: Int = Utils.floorMod(i + OrthoMode.orthoTileOffsetY, OrthoMode.orthoTilesY) * OrthoMode.orthoTilesX
						j = 0
						while (j < OrthoMode.orthoTilesX) {
							@Pc(812) val surfaceIndex: Int = Utils.floorMod(j + OrthoMode.orthoTileOffsetX, OrthoMode.orthoTilesX) + rowOffset
							@Pc(854) val dirty: Boolean = i >= dirtyRowStart && dirtyRowCount + dirtyRowStart > i || i >= partialRowStart && partialRowStart + partialRowCount > i && j >= dirtyColumnStart && j < dirtyColumnStart + dirtyColumnCount
							OrthoMode.anInterface14Array1!![surfaceIndex]!!.copyFromSurface(OrthoMode.orthoTileWidth * j, i * OrthoMode.orthoTileHeight, OrthoMode.orthoTileWidth, OrthoMode.orthoTileHeight, dirty)
							j++
						}
						i++
					}
				}
			} else {
				Scene.staticSceneDirty = true
			}
		}
		if (Scene.staticSceneDirty) {
			OrthoMode.orthoCameraZ = cameraZ
			OrthoMode.orthoCameraY = cameraY
			OrthoMode.orthoDrawY = OrthoMode.orthoBorderY
			OrthoMode.orthoScrollX = 0
			OrthoMode.orthoDrawX = OrthoMode.orthoBorderX
			OrthoMode.orthoCameraX = cameraX
			OrthoMode.orthoScrollY = 0
			OrthoMode.orthoScrollZ = 0.0
			if (OrthoMode.orthoRenderMode == 0) {
				OrthoMode.sceneToolkit!!.swapSurface(OrthoMode.logo)
			}
			OrthoMode.sceneToolkit!!.la()
			OrthoMode.sceneToolkit!!.ya()
			OrthoMode.sceneToolkit!!.GA(InterfaceComponentRenderer.deferredDragOffsetY)
			OrthoMode.aClass30_9!!.createCamera(OrthoMode.orthoCameraX, OrthoMode.orthoCameraY, OrthoMode.orthoCameraZ, OrthoMode.orthoCameraRotateX, OrthoMode.orthoCameraRotateY, OrthoMode.orthoCameraRotateZ)
			OrthoMode.sceneToolkit!!.setCamera(OrthoMode.aClass30_9)
			if (OrthoMode.orthoRenderMode == 1) {
				SceneProjectionCenterY.projectionCenterY = projectionCenterY
				OrthoMode.drawY = projectionCenterX
				OrthoMode.orthoCachedViewportHeight = scaleY
				OrthoMode.orthoCachedViewportWidth = scaleX
				OrthoMode.sceneToolkit!!.DA(OrthoMode.drawY, SceneProjectionCenterY.projectionCenterY, OrthoMode.orthoCachedViewportWidth, OrthoMode.orthoCachedViewportHeight)
			} else {
				OrthoMode.drawY = projectionCenterX + OrthoMode.orthoBorderX
				SceneProjectionCenterY.projectionCenterY = OrthoMode.orthoBorderY + projectionCenterY
				OrthoMode.orthoCachedViewportWidth = scaleX
				OrthoMode.orthoCachedViewportHeight = scaleY
				OrthoMode.sceneToolkit!!.DA(OrthoMode.drawY, SceneProjectionCenterY.projectionCenterY, OrthoMode.orthoCachedViewportWidth, OrthoMode.orthoCachedViewportHeight)
			}
			OrthoMode.orthoCachedCameraZ = 0.0
			PickList.aClass182_3.clear()
			PickList.setActive(PickList.aClass182_3)
			SceneDrawer.drawScene(cycle, cameraX, cameraY, cameraZ, settingBits, visibleBelowDrawheight, roofMinX, roofMaxX, roofMaxZ, roofMinZ, lowestRenderedPlane, roofPreference, playerTileX, playerTileZ, disableFlickeringEffects, orthoZoom, 1, false)
			PickList.resetActive()
			Scene.staticSceneDirty = false
			if (OrthoMode.orthoRenderMode == 0) {
				OrthoMode.sceneToolkit!!.restoreSurface()
			}
			if (OrthoMode.orthoRenderMode == 1) {
				OrthoMode.drawAllOrthoTiles()
			}
		}
		if (OrthoMode.orthoRenderMode == 0) {
			OrthoMode.logo!!.copyToSurface(OrthoMode.orthoDrawX, OrthoMode.orthoDrawY, OrthoMode.orthoViewWidth, OrthoMode.orthoViewHeight, 0, 0)
		}
		OrthoMode.orthoTileStamp++
		OrthoMode.translateOrthoCameraZ(OrthoMode.orthoScrollZ)
		OrthoMode.orthoScreenCameraZ = OrthoMode.orthoScrollZ
		if (OrthoMode.orthoRenderMode == 0 || OrthoMode.orthoRenderMode == 2) {
			if (OrthoMode.orthoRenderMode == 2) {
				OrthoMode.sceneToolkit!!.GA(InterfaceComponentRenderer.deferredDragOffsetY)
				OrthoMode.sceneToolkit!!.ya()
			}
			OrthoMode.orthoScreenViewportWidth = scaleX
			OrthoMode.orthoScreenViewportHeight = scaleY
			OrthoMode.orthoScreenViewportX = projectionCenterX + OrthoMode.orthoBorderX - OrthoMode.orthoDrawX - OrthoMode.orthoScrollX
			OrthoMode.orthoScreenViewportY = projectionCenterY + OrthoMode.orthoBorderY - OrthoMode.orthoDrawY - OrthoMode.orthoScrollY
			OrthoMode.sceneToolkit!!.DA(OrthoMode.orthoScreenViewportX, OrthoMode.orthoScreenViewportY, OrthoMode.orthoScreenViewportWidth, OrthoMode.orthoScreenViewportHeight)
		} else if (OrthoMode.orthoRenderMode == 1) {
			OrthoMode.orthoScreenViewportWidth = scaleX
			OrthoMode.orthoScreenViewportX = projectionCenterX - OrthoMode.orthoScrollX
			OrthoMode.orthoScreenViewportY = projectionCenterY - OrthoMode.orthoScrollY
			OrthoMode.orthoScreenViewportHeight = scaleY
			OrthoMode.sceneToolkit!!.DA(OrthoMode.orthoScreenViewportX, OrthoMode.orthoScreenViewportY, OrthoMode.orthoScreenViewportWidth, OrthoMode.orthoScreenViewportHeight)
			OrthoMode.sceneToolkit!!.KA(OrthoMode.orthoDrawX, OrthoMode.orthoDrawY, OrthoMode.orthoDrawX + OrthoMode.orthoViewWidth, OrthoMode.orthoViewHeight + OrthoMode.orthoDrawY)
		}
		SceneDrawer.drawScene(cycle, cameraX, cameraY, cameraZ, settingBits, visibleBelowDrawheight, roofMinX, roofMaxX, roofMaxZ, roofMinZ, lowestRenderedPlane, roofPreference, playerTileX, playerTileZ, disableFlickeringEffects, orthoZoom, if (OrthoMode.orthoRenderMode == 2) 0 else 2, OrthoMode.orthoRenderMode == 1)
		OrthoMode.sceneToolkit!!.la()
		OrthoMode.sceneToolkit!!.DA(projectionCenterX, projectionCenterY, projectionScaleX, projectionScaleY)
	}

	@OriginalMember(owner = "client!lga", name = "q", descriptor = "I")
	@JvmField
	var orthoCameraZ: Int = 0

	@OriginalMember(owner = "client!ro", name = "q", descriptor = "I")
	@JvmField
	var orthoBorderX: Int = 0

	@OriginalMember(owner = "client!eaa", name = "n", descriptor = "I")
	@JvmField
	var orthoDrawX: Int = 0

	@OriginalMember(owner = "client!wc", name = "k", descriptor = "D")
	@JvmField
	var orthoRotationM11: Double = 0.0

	@OriginalMember(owner = "client!wc", name = "j", descriptor = "I")
	@JvmField
	var orthoCameraRotateY: Int = -1

	@OriginalMember(owner = "client!lw", name = "c", descriptor = "I")
	@JvmField
	var orthoScreenViewportX: Int = 0

	@OriginalMember(owner = "client!gf", name = "g", descriptor = "I")
	@JvmField
	var lastOrthoZoom: Int = -1

	@OriginalMember(owner = "client!hfa", name = "a", descriptor = "(Z)I")
	@JvmStatic
	fun getOrthoDrawOffsetY(): Int {
		return if (OrthoMode.orthoRenderMode == 1) OrthoMode.orthoDrawY else 0
	}

	@OriginalMember(owner = "client!ng", name = "a", descriptor = "(IZ)I")
	@JvmStatic
	fun getOrthoPickOffsetX(@OriginalArg(1) firstPass: Boolean): Int {
		@Pc(5) val mode: Int = OrthoMode.orthoRenderMode
		if (mode == 0) {
			return if (firstPass) 0 else OrthoMode.orthoDrawX
		} else if (mode == 1) {
			return OrthoMode.orthoDrawX
		} else if (mode == 2) {
			return 0
		} else {
			return 0
		}
	}

	@OriginalMember(owner = "client!ng", name = "a", descriptor = "(Lclient!ha;IBIII)V")
	@JvmStatic
	fun initOrthoBuffer(@OriginalArg(0) toolkit: Toolkit?, @OriginalArg(3) canvasHeight: Int, @OriginalArg(5) canvasWidth: Int) {
		OrthoMode.sceneToolkit = toolkit
		OrthoMode.aClass30_9 = OrthoMode.sceneToolkit!!.createMatrix()
		OrthoMode.aClass30_8 = OrthoMode.sceneToolkit!!.createMatrix()
		OrthoMode.aClass30_12 = OrthoMode.sceneToolkit!!.createMatrix()
		OrthoMode.orthoBorderY = 100
		OrthoMode.orthoRenderMode = 0
		OrthoMode.orthoBorderX = 100
		OrthoMode.orthoTileStamps = null
		OrthoMode.anInterface14Array1 = null
		OrthoMode.resizeOrthoSurfaces(canvasHeight, canvasWidth)
		OrthoMode.orthoCameraRotateZ = -1
		OrthoMode.orthoCameraRotateY = -1
		OrthoMode.orthoCameraRotateX = -1
	}

	@OriginalMember(owner = "client!tfa", name = "c", descriptor = "D")
	@JvmField
	var orthoRotationM00: Double = 0.0

	@OriginalMember(owner = "client!gi", name = "a", descriptor = "(B)V")
	@JvmStatic
	fun resetOrtho() {
		OrthoMode.orthoCameraRotateY = -1
		OrthoMode.logo = null
		OrthoMode.aClass30_12 = null
		OrthoMode.orthoTileStamps = null
		OrthoMode.anInterface14Array1 = null
		OrthoMode.sceneToolkit = null
		OrthoMode.aClass30_8 = null
		OrthoMode.orthoCameraRotateZ = -1
		OrthoMode.aClass30_9 = null
		OrthoMode.orthoRenderMode = -1
		OrthoMode.orthoCameraRotateX = -1
		PickList.aClass182_3.clear()
	}

	@OriginalMember(owner = "client!ht", name = "h", descriptor = "I")
	@JvmField
	var orthoSavedViewportHeight: Int = 0

	@OriginalMember(owner = "client!ht", name = "j", descriptor = "I")
	@JvmField
	var orthoCameraRotateZ: Int = -1

	@OriginalMember(owner = "client!ik", name = "d", descriptor = "Lclient!bca;")
	@JvmField
	var aClass30_8: Matrix? = null

	@OriginalMember(owner = "client!lia", name = "b", descriptor = "[Lclient!is;")
	@JvmField
	var anInterface14Array1: Array<OffscreenSurface?>? = null

	@OriginalMember(owner = "client!oc", name = "a", descriptor = "(II[Ljava/awt/Rectangle;)V")
	@JvmStatic
	@Throws(FlipException::class)
	fun orthoFlipDirtyRect(@OriginalArg(0) count: Int, @OriginalArg(2) rectangles: Array<Rectangle?>?) {
		if (OrthoMode.orthoRenderMode == 1) {
			OrthoMode.sceneToolkit!!.flipDirtyRect(rectangles, count, OrthoMode.orthoDrawX, OrthoMode.orthoDrawY)
		} else {
			OrthoMode.sceneToolkit!!.flipDirtyRect(rectangles, count, 0, 0)
		}
	}

	@OriginalMember(owner = "client!dea", name = "b", descriptor = "I")
	@JvmField
	var orthoScrollX: Int = 0

	@OriginalMember(owner = "client!nr", name = "e", descriptor = "[I")
	@JvmField
	var orthoTileStamps: IntArray? = null

	@OriginalMember(owner = "client!hj", name = "f", descriptor = "Lclient!ha;")
	@JvmField
	var sceneToolkit: Toolkit? = null

	@OriginalMember(owner = "client!wr", name = "s", descriptor = "Z")
	@JvmField
	var orthographicProjection: Boolean = false

	@OriginalMember(owner = "client!rga", name = "w", descriptor = "I")
	@JvmField
	var orthoViewWidth: Int = 0

	@OriginalMember(owner = "client!mn", name = "j", descriptor = "I")
	@JvmField
	var orthoViewHeight: Int = 0

	@OriginalMember(owner = "client!bea", name = "B", descriptor = "I")
	@JvmField
	var orthoSurfaceWidth: Int = 0

	@OriginalMember(owner = "client!sba", name = "p", descriptor = "I")
	@JvmField
	var orthoSurfaceHeight: Int = 0
}
