package com.jagex.game.runetek5;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.js5.Js5Compression;
import com.jagex.game.runetek5.preference.GamePreferences;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.entity.BoundingBoxPolygon;
import com.jagex.game.runetek5.entity.EntityNodeList;
import com.jagex.game.runetek5.entity.PickableEntity;
import com.jagex.game.runetek5.graphics.FlipException;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.OffscreenSurface;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceComponentRenderer;
import com.jagex.game.runetek5.scene.Scene;
import com.jagex.game.runetek5.statics.SceneProjectionCenterY;
import com.jagex.game.runetek5.utils.Utils;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Rectangle;
import org.openrs2.deob.annotation.OriginalArg;

public final class OrthoMode {

	@OriginalMember(owner = "client!nfa", name = "c", descriptor = "I")
	public static int drawY;

	@OriginalMember(owner = "client!sg", name = "eb", descriptor = "I")
	public static int worldListSize;
	@OriginalMember(owner = "client!nr", name = "g", descriptor = "I")
	public static int orthoZoom = 7000;
	@OriginalMember(owner = "client!lk", name = "a", descriptor = "(I)V")
	public static void applyOrthographicMode() {
		OrthoMode.resetOrtho();
		@Pc(12) int mode = GamePreferences.uid.orthographic.getValue();
		if (mode == 2) {
			OrthoMode.initOrthoBuffer(ClientState.activeToolkit, GameShell.screenCanvasHeight, GameShell.screenCanvasWidth);
		} else if (mode == 3) {
			OrthoMode.initOrthoTiles(ClientState.activeToolkit, GameShell.screenCanvasWidth, Js5Compression.maxSize, OrthoMode.orthoTileSizeX, GameShell.screenCanvasHeight);
		}
		if (GamePreferences.uid.orthographic.isEnabled()) {
			OrthoMode.resizeOrthoCanvas(GameShell.gameCanvas);
		}
		if (ClientState.activeToolkit != null) {
			MapRegion.calculateViewRange();
		}
		OrthoMode.orthographicProjection = GamePreferences.uid.orthographic.getValue() != 0;
		OrthoMode.orthoEnabled = GamePreferences.uid.orthographic.isEnabled();
	}

	@OriginalMember(owner = "client!rga", name = "N", descriptor = "Z")
	public static boolean orthoEnabled = false;
	@OriginalMember(owner = "client!iv", name = "N", descriptor = "Lclient!bca;")
	public static Matrix aClass30_9;

	@OriginalMember(owner = "client!hv", name = "d", descriptor = "I")
	public static int orthoCameraY;

	@OriginalMember(owner = "client!av", name = "t", descriptor = "I")
	public static int orthoExtraTilesX;

	@OriginalMember(owner = "client!kha", name = "k", descriptor = "I")
	public static int orthoScreenViewportY;

	@OriginalMember(owner = "client!ah", name = "f", descriptor = "I")
	public static int orthoTileHeight;

	@OriginalMember(owner = "client!es", name = "m", descriptor = "D")
	public static double orthoMatrix02;

	@OriginalMember(owner = "client!lr", name = "H", descriptor = "D")
	public static double orthoMatrix12;

	@OriginalMember(owner = "client!naa", name = "M", descriptor = "D")
	public static double orthoMatrix10;

	@OriginalMember(owner = "client!co", name = "O", descriptor = "D")
	public static double orthoScreenCameraZ;

	@OriginalMember(owner = "client!hj", name = "a", descriptor = "(IIII)V")
	public static void setOrthoRotation(@OriginalArg(1) int rotateY, @OriginalArg(2) int rotateZ, @OriginalArg(3) int rotateX) {
		if (OrthoMode.orthoCameraRotateX == rotateX && rotateY == OrthoMode.orthoCameraRotateY && rotateZ == OrthoMode.orthoCameraRotateZ) {
			return;
		}
		OrthoMode.orthoCameraRotateX = rotateX;
		Scene.staticSceneDirty = true;
		OrthoMode.orthoCameraRotateZ = rotateZ;
		OrthoMode.orthoCameraRotateY = rotateY;
		@Pc(35) double radiansX = -((double) (rotateX * 2) * 3.141592653589793D) / 16384.0D;
		@Pc(45) double radiansY = -((double) (rotateY * 2) * 3.141592653589793D) / 16384.0D;
		@Pc(48) double cosY = Math.cos(radiansY);
		@Pc(51) double sinY = Math.sin(radiansY);
		@Pc(54) double cosX = Math.cos(radiansX);
		@Pc(57) double sinX = Math.sin(radiansX);
		OrthoMode.orthoRotationM00 = cosY;
		OrthoMode.orthoMatrix21 = sinX;
		OrthoMode.orthoRotationM11 = cosX;
		OrthoMode.orthoMatrix22 = cosY * cosX;
		OrthoMode.orthoMatrix12 = sinX * -cosY;
		OrthoMode.orthoMatrix02 = sinY;
		OrthoMode.orthoRotationM01 = 0.0D;
		OrthoMode.orthoMatrix20 = cosX * -sinY;
		OrthoMode.orthoMatrix10 = sinY * sinX;
	}

	@OriginalMember(owner = "client!ea", name = "a", descriptor = "(IIIII)V")
	public static void markOrthoTilesDirty(@OriginalArg(1) int minX, @OriginalArg(2) int maxX, @OriginalArg(3) int minY, @OriginalArg(4) int maxY) {
		if (OrthoMode.orthoRenderMode != 1) {
			return;
		}
		@Pc(15) int minCellX = minX / OrthoMode.orthoTileWidth;
		@Pc(19) int maxCellX = maxX / OrthoMode.orthoTileWidth;
		@Pc(23) int minCellY = minY / OrthoMode.orthoTileHeight;
		@Pc(27) int maxCellY = maxY / OrthoMode.orthoTileHeight;
		if (minCellX >= OrthoMode.orthoTilesX || maxCellX < 0 || OrthoMode.orthoTilesY <= minCellY || maxCellY < 0) {
			return;
		}
		if (maxCellX >= OrthoMode.orthoTilesX) {
			maxCellX = OrthoMode.orthoTilesX - 1;
		}
		if (minCellY < 0) {
			minCellY = 0;
		}
		if (OrthoMode.orthoTilesY <= maxCellY) {
			maxCellY = OrthoMode.orthoTilesY - 1;
		}
		if (minCellX < 0) {
			minCellX = 0;
		}
		for (@Pc(79) int i = minCellY; i <= maxCellY; i++) {
			@Pc(90) int rowOffset = Utils.floorMod(i + OrthoMode.orthoTileOffsetY, OrthoMode.orthoTilesY) * OrthoMode.orthoTilesX;
			for (@Pc(92) int j = minCellX; j <= maxCellX; j++) {
				@Pc(103) int cellIndex = rowOffset + Utils.floorMod(OrthoMode.orthoTileOffsetX + j, OrthoMode.orthoTilesX);
				OrthoMode.orthoTileStamps[cellIndex] = OrthoMode.orthoTileStamp;
			}
		}
	}

	@OriginalMember(owner = "client!hg", name = "r", descriptor = "Lclient!is;")
	public static OffscreenSurface logo;

	@OriginalMember(owner = "client!iia", name = "b", descriptor = "(I)V")
	public static void drawDirtyOrthoTiles() {
		for (@Pc(3) int i = 0; i < OrthoMode.orthoTilesY; i++) {
			@Pc(14) int rowOffset = Utils.floorMod(OrthoMode.orthoTileOffsetY + i, OrthoMode.orthoTilesY) * OrthoMode.orthoTilesX;
			for (@Pc(16) int j = 0; j < OrthoMode.orthoTilesX; j++) {
				@Pc(28) int cellIndex = Utils.floorMod(j + OrthoMode.orthoTileOffsetX, OrthoMode.orthoTilesX) + rowOffset;
				if (OrthoMode.orthoTileStamp == OrthoMode.orthoTileStamps[cellIndex]) {
					OrthoMode.anInterface14Array1[cellIndex].copyToSurface(0, 0, OrthoMode.orthoTileWidth, OrthoMode.orthoTileHeight, j * OrthoMode.orthoTileWidth, OrthoMode.orthoTileHeight * i);
				}
			}
		}
	}

	@OriginalMember(owner = "client!il", name = "u", descriptor = "D")
	public static double orthoMatrix21;

	@OriginalMember(owner = "client!au", name = "a", descriptor = "(ZI)I")
	public static int getOrthoPickOffsetY(@OriginalArg(0) boolean firstPass) {
		@Pc(10) int mode = OrthoMode.orthoRenderMode;
		if (mode == 0) {
			return firstPass ? 0 : OrthoMode.orthoDrawY;
		} else if (mode == 1) {
			return OrthoMode.orthoDrawY;
		} else if (mode == 2) {
			return 0;
		} else {
			return 0;
		}
	}

	@OriginalMember(owner = "client!jd", name = "G", descriptor = "I")
	public static int orthoCameraX;

	@OriginalMember(owner = "client!jea", name = "a", descriptor = "(IIBII)V")
	public static void expandDirtyArea(@OriginalArg(0) int maxY, @OriginalArg(1) int minX, @OriginalArg(3) int maxX, @OriginalArg(4) int minY) {
		@Pc(5) int mediumPos = Packet.mediumPos;
		if (mediumPos == 0) {
			return;
		}
		if (mediumPos == 1) {
			OrthoMode.dirtyAreaMaxY = maxY;
			OrthoMode.dirtyAreaMinX = minX;
			LoginManager.dirtyAreaMinY = minY;
			Packet.mediumPos = 2;
			OrthoMode.dirtyAreaMaxX = maxX;
		} else if (mediumPos == 2) {
			if (OrthoMode.dirtyAreaMaxX < maxX) {
				OrthoMode.dirtyAreaMaxX = maxX;
			}
			if (minX < OrthoMode.dirtyAreaMinX) {
				OrthoMode.dirtyAreaMinX = minX;
			}
			if (minY < LoginManager.dirtyAreaMinY) {
				LoginManager.dirtyAreaMinY = minY;
			}
			if (maxY > OrthoMode.dirtyAreaMaxY) {
				OrthoMode.dirtyAreaMaxY = maxY;
			}
		}
	}

	@OriginalMember(owner = "client!kfa", name = "q", descriptor = "I")
	public static int orthoSavedViewportY;

	@OriginalMember(owner = "client!bba", name = "a", descriptor = "(B)V")
	public static void restoreOrthoCamera() {
		OrthoMode.sceneToolkit.setCamera(OrthoMode.aClass30_12);
		OrthoMode.sceneToolkit.DA(OrthoMode.orthoSavedViewportX, OrthoMode.orthoSavedViewportY, OrthoMode.orthoSavedViewportWidth, OrthoMode.orthoSavedViewportHeight);
	}

	@OriginalMember(owner = "client!la", name = "q", descriptor = "I")
	public static int orthoTileSizeX = 100;

	@OriginalMember(owner = "client!le", name = "z", descriptor = "D")
	public static double orthoCachedCameraZ;

	@OriginalMember(owner = "client!mfa", name = "a", descriptor = "(III)V")
	public static void updateOrthoSurfaceSize(@OriginalArg(1) int height, @OriginalArg(2) int width) {
		OrthoMode.orthoViewWidth = width;
		OrthoMode.orthoViewHeight = height;
		if (OrthoMode.orthoRenderMode == 0) {
			OrthoMode.orthoSurfaceHeight = OrthoMode.orthoBorderY * 2 + OrthoMode.orthoViewHeight;
			OrthoMode.orthoSurfaceWidth = OrthoMode.orthoBorderX * 2 + OrthoMode.orthoViewWidth;
		} else if (OrthoMode.orthoRenderMode == 1) {
			OrthoMode.orthoTilesX = OrthoMode.orthoExtraTilesX + OrthoMode.orthoViewWidth / OrthoMode.orthoTileWidth + 2;
			OrthoMode.orthoTilesY = OrthoMode.orthoExtraTilesY + OrthoMode.orthoViewHeight / OrthoMode.orthoTileHeight + 2;
			OrthoMode.orthoSurfaceWidth = OrthoMode.orthoTileWidth * OrthoMode.orthoTilesX;
			OrthoMode.orthoSurfaceHeight = OrthoMode.orthoTileHeight * OrthoMode.orthoTilesY;
			OrthoMode.orthoBorderX = OrthoMode.orthoSurfaceWidth - OrthoMode.orthoViewWidth >> 1;
			OrthoMode.orthoBorderY = OrthoMode.orthoSurfaceHeight - OrthoMode.orthoViewHeight >> 1;
		} else if (OrthoMode.orthoRenderMode == 2) {
			OrthoMode.orthoSurfaceWidth = OrthoMode.orthoViewWidth;
			OrthoMode.orthoSurfaceHeight = OrthoMode.orthoViewHeight;
		}
	}

	@OriginalMember(owner = "client!qba", name = "F", descriptor = "I")
	public static int orthoSavedViewportWidth;

	@OriginalMember(owner = "client!qr", name = "h", descriptor = "I")
	public static int orthoScreenViewportWidth;

	@OriginalMember(owner = "client!tm", name = "h", descriptor = "I")
	public static int orthoTilesY;

	@OriginalMember(owner = "client!uo", name = "jb", descriptor = "I")
	public static int orthoTileStamp = 1;

	@OriginalMember(owner = "client!dba", name = "z", descriptor = "I")
	public static int orthoTileOffsetX;

	@OriginalMember(owner = "client!qm", name = "a", descriptor = "(B)I")
	public static int getOrthoOffsetX() {
		return OrthoMode.orthoRenderMode == 1 ? OrthoMode.orthoDrawX : 0;
	}

	@OriginalMember(owner = "client!eu", name = "q", descriptor = "I")
	public static int orthoScrollY;

	@OriginalMember(owner = "client!eu", name = "H", descriptor = "I")
	public static int dirtyAreaMaxY;

	@OriginalMember(owner = "client!hh", name = "a", descriptor = "(ILclient!ha;IBIIII)V")
	public static void initOrthoTiles(@OriginalArg(1) Toolkit toolkit, @OriginalArg(2) int nameIconsId, @OriginalArg(4) int tileHeight, @OriginalArg(5) int tileWidth, @OriginalArg(6) int height) {
		OrthoMode.sceneToolkit = toolkit;
		OrthoMode.aClass30_9 = OrthoMode.sceneToolkit.createMatrix();
		OrthoMode.aClass30_8 = OrthoMode.sceneToolkit.createMatrix();
		OrthoMode.aClass30_12 = OrthoMode.sceneToolkit.createMatrix();
		OrthoMode.orthoExtraTilesY = 2;
		OrthoMode.orthoTileOffsetX = 0;
		OrthoMode.orthoTileHeight = tileHeight;
		OrthoMode.orthoTileOffsetY = 0;
		OrthoMode.orthoTileWidth = tileWidth;
		OrthoMode.logo = null;
		OrthoMode.orthoRenderMode = 1;
		OrthoMode.orthoExtraTilesX = 2;
		OrthoMode.resizeOrthoSurfaces(height, nameIconsId);
	}

	@OriginalMember(owner = "client!jl", name = "l", descriptor = "I")
	public static int orthoTileWidth;

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(BLjava/awt/Canvas;)V")
	public static void addOrthoCanvas(@OriginalArg(1) Canvas canvas) {
		@Pc(6) Dimension dimension = canvas.getSize();
		OrthoMode.resizeOrthoSurfaces(dimension.height, dimension.width);
		if (OrthoMode.orthoRenderMode == 1) {
			OrthoMode.sceneToolkit.addCanvas(canvas, OrthoMode.orthoSurfaceWidth, OrthoMode.orthoSurfaceHeight);
		} else {
			OrthoMode.sceneToolkit.addCanvas(canvas, OrthoMode.orthoViewWidth, OrthoMode.orthoViewHeight);
		}
	}

	@OriginalMember(owner = "client!r", name = "A", descriptor = "Lclient!bca;")
	public static Matrix aClass30_12;

	@OriginalMember(owner = "client!r", name = "a", descriptor = "(ZD)V")
	public static void translateOrthoCameraZ(@OriginalArg(1) double depthOffset) {
		OrthoMode.aClass30_8.apply(OrthoMode.aClass30_9);
		OrthoMode.aClass30_8.translate(0, 0, (int) depthOffset);
		OrthoMode.sceneToolkit.setCamera(OrthoMode.aClass30_8);
	}

	@OriginalMember(owner = "client!s", name = "a", descriptor = "(ILjava/awt/Canvas;)V")
	public static void resizeOrthoCanvas(@OriginalArg(1) Canvas canvas) {
		@Pc(10) Dimension dimension = canvas.getSize();
		OrthoMode.resizeOrthoSurfaces(dimension.height, dimension.width);
		if (OrthoMode.orthoRenderMode == 1) {
			OrthoMode.sceneToolkit.resizeCanvas(canvas, OrthoMode.orthoSurfaceWidth, OrthoMode.orthoSurfaceHeight);
		} else {
			OrthoMode.sceneToolkit.resizeCanvas(canvas, OrthoMode.orthoViewWidth, OrthoMode.orthoViewHeight);
		}
	}

	@OriginalMember(owner = "client!jq", name = "R", descriptor = "I")
	public static int orthoDrawY;

	@OriginalMember(owner = "client!ai", name = "a", descriptor = "(I)I")
	public static int getOrthoSurfaceWidth() {
		return OrthoMode.orthoRenderMode == 1 ? OrthoMode.orthoSurfaceWidth : OrthoMode.orthoViewWidth;
	}

	@OriginalMember(owner = "client!gn", name = "m", descriptor = "I")
	public static int orthoCachedViewportWidth;

	@OriginalMember(owner = "client!vk", name = "b", descriptor = "I")
	public static int orthoTileOffsetY;

	@OriginalMember(owner = "client!nn", name = "g", descriptor = "I")
	public static int orthoScreenViewportHeight;

	@OriginalMember(owner = "client!in", name = "b", descriptor = "(I)V")
	public static void flipOrtho() throws FlipException {
		if (OrthoMode.orthoRenderMode == 1) {
			OrthoMode.sceneToolkit.flip(OrthoMode.orthoDrawX, OrthoMode.orthoDrawY);
		} else {
			OrthoMode.sceneToolkit.flip(0, 0);
		}
	}

	@OriginalMember(owner = "client!tha", name = "r", descriptor = "I")
	public static int orthoSavedViewportX;

	@OriginalMember(owner = "client!ci", name = "b", descriptor = "I")
	public static int dirtyAreaMaxX;

	@OriginalMember(owner = "client!ci", name = "d", descriptor = "I")
	public static int dirtyAreaMinX;

	@OriginalMember(owner = "client!jp", name = "p", descriptor = "D")
	public static double orthoMatrix20;

	@OriginalMember(owner = "client!kh", name = "t", descriptor = "I")
	public static int orthoCameraRotateX = -1;

	@OriginalMember(owner = "client!kk", name = "m", descriptor = "I")
	public static int orthoExtraTilesY;

	@OriginalMember(owner = "client!lm", name = "D", descriptor = "D")
	public static double orthoScrollZ;

	@OriginalMember(owner = "client!iha", name = "B", descriptor = "D")
	public static double orthoMatrix22;

	@OriginalMember(owner = "client!wb", name = "b", descriptor = "(B)I")
	public static int getOrthoSurfaceHeight() {
		return OrthoMode.orthoRenderMode == 1 ? OrthoMode.orthoSurfaceHeight : OrthoMode.orthoViewHeight;
	}

	@OriginalMember(owner = "client!ia", name = "f", descriptor = "I")
	public static int orthoTilesX;

	@OriginalMember(owner = "client!qga", name = "a", descriptor = "(Z)V")
	public static void disableOrthoMode() {
		OrthoMode.resetOrtho();
		OrthoMode.orthoEnabled = false;
	}

	@OriginalMember(owner = "client!cn", name = "c", descriptor = "D")
	public static double orthoRotationM01;

	@OriginalMember(owner = "client!mn", name = "a", descriptor = "(ZZ)V")
	public static void applyOrthoViewport(@OriginalArg(1) boolean dynamic) {
		OrthoMode.aClass30_12.apply(OrthoMode.sceneToolkit.camera());
		@Pc(10) int[] projection = OrthoMode.sceneToolkit.Y();
		OrthoMode.orthoSavedViewportWidth = projection[2];
		OrthoMode.orthoSavedViewportX = projection[0];
		OrthoMode.orthoSavedViewportHeight = projection[3];
		OrthoMode.orthoSavedViewportY = projection[1];
		if (dynamic) {
			OrthoMode.sceneToolkit.DA(OrthoMode.orthoScreenViewportX, OrthoMode.orthoScreenViewportY, OrthoMode.orthoScreenViewportWidth, OrthoMode.orthoScreenViewportHeight);
			OrthoMode.translateOrthoCameraZ(OrthoMode.orthoScreenCameraZ);
		} else {
			OrthoMode.sceneToolkit.DA(OrthoMode.drawY, SceneProjectionCenterY.projectionCenterY, OrthoMode.orthoCachedViewportWidth, OrthoMode.orthoCachedViewportHeight);
			OrthoMode.translateOrthoCameraZ(OrthoMode.orthoCachedCameraZ);
		}
	}

	@OriginalMember(owner = "client!ct", name = "F", descriptor = "I")
	public static int orthoBorderY;

	@OriginalMember(owner = "client!re", name = "b", descriptor = "(I)V")
	public static void drawAllOrthoTiles() {
		OrthoMode.orthoTileOffsetY = 0;
		OrthoMode.orthoTileOffsetX = 0;
		for (@Pc(19) int i = 0; i < OrthoMode.orthoTilesY; i++) {
			@Pc(25) int rowOffset = OrthoMode.orthoTilesX * i;
			for (@Pc(27) int j = 0; j < OrthoMode.orthoTilesX; j++) {
				@Pc(34) int index = rowOffset + j;
				OrthoMode.anInterface14Array1[index].copyFromSurface(j * OrthoMode.orthoTileWidth, i * OrthoMode.orthoTileHeight, OrthoMode.orthoTileWidth, OrthoMode.orthoTileHeight, true);
			}
		}
	}

	@OriginalMember(owner = "client!cea", name = "Gb", descriptor = "I")
	public static int orthoRenderMode;

	@OriginalMember(owner = "client!sc", name = "d", descriptor = "I")
	public static int orthoCachedViewportHeight;

	@OriginalMember(owner = "client!lda", name = "a", descriptor = "(BII)V")
	public static void resizeOrthoSurfaces(@OriginalArg(1) int height, @OriginalArg(2) int width) {
		if (OrthoMode.sceneToolkit == null) {
			return;
		}
		@Pc(17) int previousColumnCount = OrthoMode.orthoTilesX;
		@Pc(19) int previousRowCount = OrthoMode.orthoTilesY;
		OrthoMode.updateOrthoSurfaceSize(height, width);
		if (OrthoMode.orthoRenderMode == 0) {
			OrthoMode.logo = null;
			OrthoMode.logo = OrthoMode.sceneToolkit.createOffscreenSurface(OrthoMode.sceneToolkit.method7268(OrthoMode.orthoSurfaceWidth, OrthoMode.orthoSurfaceHeight), OrthoMode.sceneToolkit.createDepthBuffer(OrthoMode.orthoSurfaceWidth, OrthoMode.orthoSurfaceHeight));
		} else if (OrthoMode.orthoRenderMode == 1 && (OrthoMode.anInterface14Array1 == null || OrthoMode.orthoTilesX != previousColumnCount || previousRowCount != OrthoMode.orthoTilesY)) {
			OrthoMode.anInterface14Array1 = new OffscreenSurface[OrthoMode.orthoTilesY * OrthoMode.orthoTilesX];
			for (@Pc(64) int i = 0; i < OrthoMode.anInterface14Array1.length; i++) {
				OrthoMode.anInterface14Array1[i] = OrthoMode.sceneToolkit.createOffscreenSurface(OrthoMode.sceneToolkit.method7268(OrthoMode.orthoTileWidth, OrthoMode.orthoTileHeight), OrthoMode.sceneToolkit.createDepthBuffer(OrthoMode.orthoTileWidth, OrthoMode.orthoTileHeight));
			}
			OrthoMode.orthoTileStamps = new int[OrthoMode.orthoTilesX * OrthoMode.orthoTilesY];
			OrthoMode.orthoTileStamp = 1;
		}
		Scene.staticSceneDirty = true;
	}

	@OriginalMember(owner = "client!sj", name = "a", descriptor = "([II[[[BII[IIIZII[I[IB[IIZI)V")
	public static void drawOrthoScene(@OriginalArg(0) int[] roofMinZ, @OriginalArg(2) byte[][][] settingBits, @OriginalArg(3) int playerTileZ, @OriginalArg(4) int lowestRenderedPlane, @OriginalArg(5) int[] visibleBelowDrawheight, @OriginalArg(6) int cameraX, @OriginalArg(7) int cameraZ, @OriginalArg(8) boolean disableFlickeringEffects, @OriginalArg(9) int cameraY, @OriginalArg(10) int playerTileX, @OriginalArg(11) int[] roofMinX, @OriginalArg(12) int[] roofMaxX, @OriginalArg(13) byte roofPreference, @OriginalArg(14) int[] roofMaxZ, @OriginalArg(15) int cycle, @OriginalArg(17) int orthoZoom) {
		if (OrthoMode.orthoRenderMode == -1) {
			return;
		}
		@Pc(12) int[] projection = OrthoMode.sceneToolkit.Y();
		@Pc(16) int projectionCenterX = projection[0];
		@Pc(20) int projectionCenterY = projection[1];
		@Pc(24) int projectionScaleX = projection[2];
		@Pc(28) int projectionScaleY = projection[3];
		@Pc(30) int scaleX = projectionScaleX;
		@Pc(32) int scaleY = projectionScaleY;
		if (OrthoMode.orthoRenderMode == 1) {
			scaleX = (int) ((double) projectionScaleX * (double) OrthoMode.orthoViewHeight / (double) OrthoMode.orthoSurfaceHeight);
			scaleY = (int) ((double) OrthoMode.orthoViewHeight * (double) projectionScaleY / (double) OrthoMode.orthoSurfaceHeight);
		}
		if (!Scene.staticSceneDirty) {
			if (OrthoMode.orthoRenderMode == 1) {
				OrthoMode.drawDirtyOrthoTiles();
			}
			@Pc(67) int cameraDeltaX = cameraX - OrthoMode.orthoCameraX;
			@Pc(72) int cameraDeltaY = cameraY - OrthoMode.orthoCameraY;
			@Pc(77) int cameraDeltaZ = cameraZ - OrthoMode.orthoCameraZ;
			@Pc(99) int projectedX = (int) ((OrthoMode.orthoRotationM00 * (double) cameraDeltaX + (double) cameraDeltaY * OrthoMode.orthoRotationM01 + (double) cameraDeltaZ * OrthoMode.orthoMatrix02) * (double) scaleX / (double) orthoZoom);
			@Pc(121) int projectedY = (int) (((double) cameraDeltaZ * OrthoMode.orthoMatrix12 + (double) cameraDeltaX * OrthoMode.orthoMatrix10 + (double) cameraDeltaY * OrthoMode.orthoRotationM11) * (double) scaleY / (double) orthoZoom);
			@Pc(136) double depthDelta = (double) cameraDeltaZ * OrthoMode.orthoMatrix22 + OrthoMode.orthoMatrix21 * (double) cameraDeltaY + (double) cameraDeltaX * OrthoMode.orthoMatrix20;
			@Pc(142) int viewX = projectedX + OrthoMode.orthoBorderX - OrthoMode.orthoScrollX;
			@Pc(149) int viewY = OrthoMode.orthoBorderY + projectedY - OrthoMode.orthoScrollY;
			@Pc(153) int viewRight = OrthoMode.orthoViewWidth + viewX;
			@Pc(157) int viewBottom = OrthoMode.orthoViewHeight + viewY;
			if (viewX >= 0 && viewY >= 0 && viewRight <= OrthoMode.orthoSurfaceWidth && OrthoMode.orthoSurfaceHeight >= viewBottom || OrthoMode.orthoRenderMode == 2) {
				OrthoMode.orthoDrawX = viewX;
				OrthoMode.orthoDrawY = viewY;
				if (OrthoMode.orthoRenderMode == 2) {
					OrthoMode.orthoScrollZ = -depthDelta;
				}
			} else if (viewRight > 0 && viewBottom > 0 && viewX < OrthoMode.orthoSurfaceWidth && OrthoMode.orthoSurfaceHeight > viewY) {
				@Pc(206) int offsetX = viewX - OrthoMode.orthoBorderX;
				@Pc(210) int offsetY = viewY - OrthoMode.orthoBorderY;
				@Pc(212) int shiftX = 0;
				@Pc(214) int shiftY = 0;
				@Pc(216) int tileShiftX = 0;
				@Pc(218) int tileShiftY = 0;
				@Pc(220) double depthShift = 0.0D;
				if (OrthoMode.orthoRenderMode == 0) {
					depthShift = depthDelta + OrthoMode.orthoScrollZ;
					shiftX = offsetX;
					shiftY = offsetY;
				} else if (OrthoMode.orthoRenderMode == 1) {
					tileShiftX = offsetX / OrthoMode.orthoTileWidth;
					tileShiftY = offsetY / OrthoMode.orthoTileHeight;
					shiftX = tileShiftX * OrthoMode.orthoTileWidth;
					shiftY = tileShiftY * OrthoMode.orthoTileHeight;
					depthShift = (depthDelta + OrthoMode.orthoScrollZ) * (double) (offsetY * shiftY + shiftX * offsetX) / (double) (offsetY * offsetY + offsetX * offsetX);
				}
				depthShift = -depthShift;
				@Pc(283) int dirtyRowStart = 0;
				@Pc(285) int dirtyRowCount = 0;
				@Pc(287) int dirtyColumnStart = 0;
				@Pc(289) int partialRowStart = 0;
				@Pc(291) int dirtyColumnCount = 0;
				@Pc(298) int clearX;
				@Pc(303) int clearWidth;
				@Pc(317) int stripX;
				@Pc(315) int stripWidth;
				if (shiftX >= 0) {
					clearX = 0;
					clearWidth = OrthoMode.orthoSurfaceWidth - shiftX;
					if (OrthoMode.orthoRenderMode == 1) {
						dirtyColumnCount = tileShiftX;
						dirtyColumnStart = OrthoMode.orthoTilesX - tileShiftX;
					}
					stripWidth = shiftX;
					stripX = clearWidth;
				} else {
					clearWidth = OrthoMode.orthoSurfaceWidth + shiftX;
					stripX = 0;
					clearX = -shiftX;
					stripWidth = clearX;
					if (OrthoMode.orthoRenderMode == 1) {
						dirtyColumnCount = -tileShiftX;
						dirtyColumnStart = 0;
					}
				}
				@Pc(340) int partialRowCount = 0;
				@Pc(352) int clearY;
				@Pc(350) int clearHeight;
				@Pc(354) int bandY;
				@Pc(356) int bandHeight;
				@Pc(358) int verticalStripY;
				if (shiftY >= 0) {
					clearHeight = OrthoMode.orthoSurfaceHeight - shiftY;
					clearY = 0;
					bandY = clearHeight;
					bandHeight = shiftY;
					verticalStripY = 0;
					if (OrthoMode.orthoRenderMode == 1) {
						partialRowStart = 0;
						dirtyRowCount = tileShiftY;
						dirtyRowStart = OrthoMode.orthoTilesY - tileShiftY;
						partialRowCount = dirtyRowStart;
					}
				} else {
					clearY = -shiftY;
					bandY = 0;
					clearHeight = OrthoMode.orthoSurfaceHeight + shiftY;
					bandHeight = clearY;
					verticalStripY = clearY;
					if (OrthoMode.orthoRenderMode == 1) {
						dirtyRowCount = -tileShiftY;
						dirtyRowStart = 0;
						partialRowCount = tileShiftY + OrthoMode.orthoTilesY;
						partialRowStart = dirtyRowCount;
					}
				}
				@Pc(410) EntityNodeList linkedList = PickList.aClass182_3.entities;
				@Pc(424) int j;
				for (@Pc(415) PickableEntity pickableEntity = (PickableEntity) linkedList.getFirst(); pickableEntity != null; pickableEntity = (PickableEntity) linkedList.getNext()) {
					@Pc(420) BoundingBoxPolygon[] pickingCylinders = pickableEntity.boundingBoxPolygons;
					@Pc(422) boolean offscreen = true;
					for (j = 0; j < pickingCylinders.length; j++) {
						@Pc(430) BoundingBoxPolygon pickingCylinder = pickingCylinders[j];
						@Pc(433) int left = pickingCylinder.left;
						@Pc(436) int bottom = pickingCylinder.bottom;
						@Pc(439) int right = pickingCylinder.right;
						@Pc(442) int top = pickingCylinder.top;
						@Pc(445) int size = pickingCylinder.size;
						@Pc(452) int newTop;
						pickingCylinder.top = newTop = top - shiftY;
						@Pc(460) int newLeft;
						pickingCylinder.left = newLeft = left - shiftX;
						@Pc(468) int newRight;
						pickingCylinder.right = newRight = right - shiftX;
						@Pc(476) int newBottom;
						pickingCylinder.bottom = newBottom = bottom - shiftY;
						if (offscreen) {
							@Pc(489) int minX = (newRight > newLeft ? newLeft : newRight) - size;
							if (OrthoMode.orthoSurfaceWidth >= minX) {
								@Pc(510) int minY = (newBottom < newTop ? newBottom : newTop) - size;
								if (OrthoMode.orthoSurfaceHeight >= minY) {
									@Pc(527) int maxX = (newLeft < newRight ? newRight : newLeft) + size;
									if (maxX >= 0) {
										@Pc(541) int maxY = (newTop > newBottom ? newTop : newBottom) + size;
										if (maxY >= 0) {
											offscreen = false;
										}
									}
								}
							}
						}
					}
					if (offscreen) {
						pickableEntity.unlinkNode();
						PickableEntity.recycle(pickableEntity);
					}
				}
				if (OrthoMode.orthoRenderMode == 0) {
					OrthoMode.sceneToolkit.swapSurface(OrthoMode.logo);
				}
				OrthoMode.sceneToolkit.F(-shiftX, -shiftY);
				OrthoMode.sceneToolkit.b(clearX, clearY, clearWidth, clearHeight, depthShift);
				OrthoMode.translateOrthoCameraZ(OrthoMode.orthoScrollZ + depthShift);
				OrthoMode.orthoCachedCameraZ = depthShift + OrthoMode.orthoScrollZ;
				if (OrthoMode.orthoRenderMode == 1) {
					SceneProjectionCenterY.projectionCenterY = projectionCenterY - OrthoMode.orthoScrollY - shiftY;
					OrthoMode.orthoCachedViewportHeight = scaleY;
					OrthoMode.drawY = projectionCenterX - OrthoMode.orthoScrollX - shiftX;
					OrthoMode.orthoCachedViewportWidth = scaleX;
					OrthoMode.sceneToolkit.DA(OrthoMode.drawY, SceneProjectionCenterY.projectionCenterY, OrthoMode.orthoCachedViewportWidth, OrthoMode.orthoCachedViewportHeight);
				} else {
					OrthoMode.orthoCachedViewportWidth = scaleX;
					SceneProjectionCenterY.projectionCenterY = projectionCenterY + OrthoMode.orthoBorderY - OrthoMode.orthoScrollY - shiftY;
					OrthoMode.drawY = projectionCenterX + OrthoMode.orthoBorderX - OrthoMode.orthoScrollX - shiftX;
					OrthoMode.orthoCachedViewportHeight = scaleY;
					OrthoMode.sceneToolkit.DA(OrthoMode.drawY, SceneProjectionCenterY.projectionCenterY, OrthoMode.orthoCachedViewportWidth, OrthoMode.orthoCachedViewportHeight);
				}
				PickList.setActive(PickList.aClass182_3);
				if (bandHeight > 0) {
					OrthoMode.sceneToolkit.KA(0, bandY, OrthoMode.orthoSurfaceWidth, bandHeight + bandY);
					OrthoMode.sceneToolkit.ya();
					OrthoMode.sceneToolkit.GA(InterfaceComponentRenderer.deferredDragOffsetY);
					SceneDrawer.drawScene(cycle, cameraX, cameraY, cameraZ, settingBits, visibleBelowDrawheight, roofMinX, roofMaxX, roofMaxZ, roofMinZ, lowestRenderedPlane, roofPreference, playerTileX, playerTileZ, disableFlickeringEffects, orthoZoom, 1, false);
				}
				if (stripWidth > 0) {
					OrthoMode.sceneToolkit.KA(stripX, verticalStripY, stripX + stripWidth, verticalStripY + clearHeight);
					OrthoMode.sceneToolkit.ya();
					OrthoMode.sceneToolkit.GA(InterfaceComponentRenderer.deferredDragOffsetY);
					SceneDrawer.drawScene(cycle, cameraX, cameraY, cameraZ, settingBits, visibleBelowDrawheight, roofMinX, roofMaxX, roofMaxZ, roofMinZ, lowestRenderedPlane, roofPreference, playerTileX, playerTileZ, disableFlickeringEffects, orthoZoom, 1, false);
				}
				OrthoMode.sceneToolkit.la();
				PickList.resetActive();
				if (OrthoMode.orthoRenderMode == 0) {
					OrthoMode.sceneToolkit.restoreSurface();
				}
				OrthoMode.orthoScrollY += shiftY;
				OrthoMode.orthoScrollX += shiftX;
				OrthoMode.orthoScrollZ += depthShift;
				OrthoMode.orthoDrawY = projectedY + OrthoMode.orthoBorderY - OrthoMode.orthoScrollY;
				OrthoMode.orthoDrawX = OrthoMode.orthoBorderX + projectedX - OrthoMode.orthoScrollX;
				if (OrthoMode.orthoRenderMode == 1) {
					OrthoMode.orthoTileOffsetX += tileShiftX;
					OrthoMode.orthoTileOffsetY += tileShiftY;
					for (@Pc(787) int i = 0; i < OrthoMode.orthoTilesY; i++) {
						@Pc(799) int rowOffset = Utils.floorMod(i + OrthoMode.orthoTileOffsetY, OrthoMode.orthoTilesY) * OrthoMode.orthoTilesX;
						for (j = 0; j < OrthoMode.orthoTilesX; j++) {
							@Pc(812) int surfaceIndex = Utils.floorMod(j + OrthoMode.orthoTileOffsetX, OrthoMode.orthoTilesX) + rowOffset;
							@Pc(854) boolean dirty = i >= dirtyRowStart && dirtyRowCount + dirtyRowStart > i || i >= partialRowStart && partialRowStart + partialRowCount > i && j >= dirtyColumnStart && j < dirtyColumnStart + dirtyColumnCount;
							OrthoMode.anInterface14Array1[surfaceIndex].copyFromSurface(OrthoMode.orthoTileWidth * j, i * OrthoMode.orthoTileHeight, OrthoMode.orthoTileWidth, OrthoMode.orthoTileHeight, dirty);
						}
					}
				}
			} else {
				Scene.staticSceneDirty = true;
			}
		}
		if (Scene.staticSceneDirty) {
			OrthoMode.orthoCameraZ = cameraZ;
			OrthoMode.orthoCameraY = cameraY;
			OrthoMode.orthoDrawY = OrthoMode.orthoBorderY;
			OrthoMode.orthoScrollX = 0;
			OrthoMode.orthoDrawX = OrthoMode.orthoBorderX;
			OrthoMode.orthoCameraX = cameraX;
			OrthoMode.orthoScrollY = 0;
			OrthoMode.orthoScrollZ = 0.0D;
			if (OrthoMode.orthoRenderMode == 0) {
				OrthoMode.sceneToolkit.swapSurface(OrthoMode.logo);
			}
			OrthoMode.sceneToolkit.la();
			OrthoMode.sceneToolkit.ya();
			OrthoMode.sceneToolkit.GA(InterfaceComponentRenderer.deferredDragOffsetY);
			OrthoMode.aClass30_9.createCamera(OrthoMode.orthoCameraX, OrthoMode.orthoCameraY, OrthoMode.orthoCameraZ, OrthoMode.orthoCameraRotateX, OrthoMode.orthoCameraRotateY, OrthoMode.orthoCameraRotateZ);
			OrthoMode.sceneToolkit.setCamera(OrthoMode.aClass30_9);
			if (OrthoMode.orthoRenderMode == 1) {
				SceneProjectionCenterY.projectionCenterY = projectionCenterY;
				OrthoMode.drawY = projectionCenterX;
				OrthoMode.orthoCachedViewportHeight = scaleY;
				OrthoMode.orthoCachedViewportWidth = scaleX;
				OrthoMode.sceneToolkit.DA(OrthoMode.drawY, SceneProjectionCenterY.projectionCenterY, OrthoMode.orthoCachedViewportWidth, OrthoMode.orthoCachedViewportHeight);
			} else {
				OrthoMode.drawY = projectionCenterX + OrthoMode.orthoBorderX;
				SceneProjectionCenterY.projectionCenterY = OrthoMode.orthoBorderY + projectionCenterY;
				OrthoMode.orthoCachedViewportWidth = scaleX;
				OrthoMode.orthoCachedViewportHeight = scaleY;
				OrthoMode.sceneToolkit.DA(OrthoMode.drawY, SceneProjectionCenterY.projectionCenterY, OrthoMode.orthoCachedViewportWidth, OrthoMode.orthoCachedViewportHeight);
			}
			OrthoMode.orthoCachedCameraZ = 0.0D;
			PickList.aClass182_3.clear();
			PickList.setActive(PickList.aClass182_3);
			SceneDrawer.drawScene(cycle, cameraX, cameraY, cameraZ, settingBits, visibleBelowDrawheight, roofMinX, roofMaxX, roofMaxZ, roofMinZ, lowestRenderedPlane, roofPreference, playerTileX, playerTileZ, disableFlickeringEffects, orthoZoom, 1, false);
			PickList.resetActive();
			Scene.staticSceneDirty = false;
			if (OrthoMode.orthoRenderMode == 0) {
				OrthoMode.sceneToolkit.restoreSurface();
			}
			if (OrthoMode.orthoRenderMode == 1) {
				OrthoMode.drawAllOrthoTiles();
			}
		}
		if (OrthoMode.orthoRenderMode == 0) {
			OrthoMode.logo.copyToSurface(OrthoMode.orthoDrawX, OrthoMode.orthoDrawY, OrthoMode.orthoViewWidth, OrthoMode.orthoViewHeight, 0, 0);
		}
		OrthoMode.orthoTileStamp++;
		OrthoMode.translateOrthoCameraZ(OrthoMode.orthoScrollZ);
		OrthoMode.orthoScreenCameraZ = OrthoMode.orthoScrollZ;
		if (OrthoMode.orthoRenderMode == 0 || OrthoMode.orthoRenderMode == 2) {
			if (OrthoMode.orthoRenderMode == 2) {
				OrthoMode.sceneToolkit.GA(InterfaceComponentRenderer.deferredDragOffsetY);
				OrthoMode.sceneToolkit.ya();
			}
			OrthoMode.orthoScreenViewportWidth = scaleX;
			OrthoMode.orthoScreenViewportHeight = scaleY;
			OrthoMode.orthoScreenViewportX = projectionCenterX + OrthoMode.orthoBorderX - OrthoMode.orthoDrawX - OrthoMode.orthoScrollX;
			OrthoMode.orthoScreenViewportY = projectionCenterY + OrthoMode.orthoBorderY - OrthoMode.orthoDrawY - OrthoMode.orthoScrollY;
			OrthoMode.sceneToolkit.DA(OrthoMode.orthoScreenViewportX, OrthoMode.orthoScreenViewportY, OrthoMode.orthoScreenViewportWidth, OrthoMode.orthoScreenViewportHeight);
		} else if (OrthoMode.orthoRenderMode == 1) {
			OrthoMode.orthoScreenViewportWidth = scaleX;
			OrthoMode.orthoScreenViewportX = projectionCenterX - OrthoMode.orthoScrollX;
			OrthoMode.orthoScreenViewportY = projectionCenterY - OrthoMode.orthoScrollY;
			OrthoMode.orthoScreenViewportHeight = scaleY;
			OrthoMode.sceneToolkit.DA(OrthoMode.orthoScreenViewportX, OrthoMode.orthoScreenViewportY, OrthoMode.orthoScreenViewportWidth, OrthoMode.orthoScreenViewportHeight);
			OrthoMode.sceneToolkit.KA(OrthoMode.orthoDrawX, OrthoMode.orthoDrawY, OrthoMode.orthoDrawX + OrthoMode.orthoViewWidth, OrthoMode.orthoViewHeight + OrthoMode.orthoDrawY);
		}
		SceneDrawer.drawScene(cycle, cameraX, cameraY, cameraZ, settingBits, visibleBelowDrawheight, roofMinX, roofMaxX, roofMaxZ, roofMinZ, lowestRenderedPlane, roofPreference, playerTileX, playerTileZ, disableFlickeringEffects, orthoZoom, OrthoMode.orthoRenderMode == 2 ? 0 : 2, OrthoMode.orthoRenderMode == 1);
		OrthoMode.sceneToolkit.la();
		OrthoMode.sceneToolkit.DA(projectionCenterX, projectionCenterY, projectionScaleX, projectionScaleY);
	}

	@OriginalMember(owner = "client!lga", name = "q", descriptor = "I")
	public static int orthoCameraZ;

	@OriginalMember(owner = "client!ro", name = "q", descriptor = "I")
	public static int orthoBorderX;

	@OriginalMember(owner = "client!eaa", name = "n", descriptor = "I")
	public static int orthoDrawX;

	@OriginalMember(owner = "client!wc", name = "k", descriptor = "D")
	public static double orthoRotationM11;

	@OriginalMember(owner = "client!wc", name = "j", descriptor = "I")
	public static int orthoCameraRotateY = -1;

	@OriginalMember(owner = "client!lw", name = "c", descriptor = "I")
	public static int orthoScreenViewportX;

	@OriginalMember(owner = "client!gf", name = "g", descriptor = "I")
	public static int lastOrthoZoom = -1;

	@OriginalMember(owner = "client!hfa", name = "a", descriptor = "(Z)I")
	public static int getOrthoDrawOffsetY() {
		return OrthoMode.orthoRenderMode == 1 ? OrthoMode.orthoDrawY : 0;
	}

	@OriginalMember(owner = "client!ng", name = "a", descriptor = "(IZ)I")
	public static int getOrthoPickOffsetX(@OriginalArg(1) boolean firstPass) {
		@Pc(5) int mode = OrthoMode.orthoRenderMode;
		if (mode == 0) {
			return firstPass ? 0 : OrthoMode.orthoDrawX;
		} else if (mode == 1) {
			return OrthoMode.orthoDrawX;
		} else if (mode == 2) {
			return 0;
		} else {
			return 0;
		}
	}

	@OriginalMember(owner = "client!ng", name = "a", descriptor = "(Lclient!ha;IBIII)V")
	public static void initOrthoBuffer(@OriginalArg(0) Toolkit toolkit, @OriginalArg(3) int canvasHeight, @OriginalArg(5) int canvasWidth) {
		OrthoMode.sceneToolkit = toolkit;
		OrthoMode.aClass30_9 = OrthoMode.sceneToolkit.createMatrix();
		OrthoMode.aClass30_8 = OrthoMode.sceneToolkit.createMatrix();
		OrthoMode.aClass30_12 = OrthoMode.sceneToolkit.createMatrix();
		OrthoMode.orthoBorderY = 100;
		OrthoMode.orthoRenderMode = 0;
		OrthoMode.orthoBorderX = 100;
		OrthoMode.orthoTileStamps = null;
		OrthoMode.anInterface14Array1 = null;
		OrthoMode.resizeOrthoSurfaces(canvasHeight, canvasWidth);
		OrthoMode.orthoCameraRotateZ = -1;
		OrthoMode.orthoCameraRotateY = -1;
		OrthoMode.orthoCameraRotateX = -1;
	}

	@OriginalMember(owner = "client!tfa", name = "c", descriptor = "D")
	public static double orthoRotationM00;

	@OriginalMember(owner = "client!gi", name = "a", descriptor = "(B)V")
	public static void resetOrtho() {
		OrthoMode.orthoCameraRotateY = -1;
		OrthoMode.logo = null;
		OrthoMode.aClass30_12 = null;
		OrthoMode.orthoTileStamps = null;
		OrthoMode.anInterface14Array1 = null;
		OrthoMode.sceneToolkit = null;
		OrthoMode.aClass30_8 = null;
		OrthoMode.orthoCameraRotateZ = -1;
		OrthoMode.aClass30_9 = null;
		OrthoMode.orthoRenderMode = -1;
		OrthoMode.orthoCameraRotateX = -1;
		PickList.aClass182_3.clear();
	}

	@OriginalMember(owner = "client!ht", name = "h", descriptor = "I")
	public static int orthoSavedViewportHeight;

	@OriginalMember(owner = "client!ht", name = "j", descriptor = "I")
	public static int orthoCameraRotateZ = -1;

	@OriginalMember(owner = "client!ik", name = "d", descriptor = "Lclient!bca;")
	public static Matrix aClass30_8;

	@OriginalMember(owner = "client!lia", name = "b", descriptor = "[Lclient!is;")
	public static OffscreenSurface[] anInterface14Array1;

	@OriginalMember(owner = "client!oc", name = "a", descriptor = "(II[Ljava/awt/Rectangle;)V")
	public static void orthoFlipDirtyRect(@OriginalArg(0) int count, @OriginalArg(2) Rectangle[] rectangles) throws FlipException {
		if (OrthoMode.orthoRenderMode == 1) {
			OrthoMode.sceneToolkit.flipDirtyRect(rectangles, count, OrthoMode.orthoDrawX, OrthoMode.orthoDrawY);
		} else {
			OrthoMode.sceneToolkit.flipDirtyRect(rectangles, count, 0, 0);
		}
	}

	@OriginalMember(owner = "client!dea", name = "b", descriptor = "I")
	public static int orthoScrollX;

	@OriginalMember(owner = "client!nr", name = "e", descriptor = "[I")
	public static int[] orthoTileStamps;

	@OriginalMember(owner = "client!hj", name = "f", descriptor = "Lclient!ha;")
	public static Toolkit sceneToolkit;
	@OriginalMember(owner = "client!wr", name = "s", descriptor = "Z")
	public static boolean orthographicProjection = false;
	@OriginalMember(owner = "client!rga", name = "w", descriptor = "I")
	public static int orthoViewWidth;

	@OriginalMember(owner = "client!mn", name = "j", descriptor = "I")
	public static int orthoViewHeight;

	@OriginalMember(owner = "client!bea", name = "B", descriptor = "I")
	public static int orthoSurfaceWidth;

	@OriginalMember(owner = "client!sba", name = "p", descriptor = "I")
	public static int orthoSurfaceHeight;
}
