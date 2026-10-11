package com.jagex.game.runetek5.toolkit;

import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.client.GameShell;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.client.input.camera.Camera;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ClientState;

public final class AbstractToolkit {
	@OriginalMember(owner = "client!uf", name = "P", descriptor = "[Z")
	public static final boolean[] ACTIVE_RENDER_UNITS = new boolean[8];

	private AbstractToolkit() {
		/* empty */
	}
	@OriginalMember(owner = "client!lk", name = "a", descriptor = "(IIIZBI)V")
	public static void calculateGameArea(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int width, @OriginalArg(3) boolean drawBorders, @OriginalArg(5) int height) {
		if (width < 1) {
			width = 1;
		}
		if (height < 1) {
			height = 1;
		}
		@Pc(21) int heightFactor = height - 334;
		if (heightFactor < 0) {
			heightFactor = 0;
		} else if (heightFactor > 100) {
			heightFactor = 100;
		}
		@Pc(45) int fov = (Camera.viewportFovMax - Camera.viewportFovMin) * heightFactor / 100 + Camera.viewportFovMin;
		if (ClientFeatures.ZOOM) {
			// Not part of the original client: the zoom offset widens the field of view to between a quarter of the
			// narrowest (and no less than 100) and four times the widest, and is held to that range.
			int min = Math.max(Camera.viewportFovMin / 4, 100);
			int max = Camera.viewportFovMax * 4;
			if (min > max) {
				int swap = min;
				min = max;
				max = swap;
			}
			int zoomed = Math.min(Math.max(fov + ClientFeatures.zoomOffset, min), max);
			ClientFeatures.zoomOffset = zoomed - fov;
			fov = zoomed;
		}
		if (Camera.fovScaleMin > fov) {
			fov = Camera.fovScaleMin;
		} else if (Camera.fovScaleMax < fov) {
			fov = Camera.fovScaleMax;
		}
		@Pc(71) int aspectFov = fov * 512 * height / (width * 334);
		@Pc(112) int fittedSize;
		@Pc(119) int borderSize;
		@Pc(83) short aspectLimit;
		if (aspectFov < Camera.aspectFovMin) {
			aspectLimit = Camera.aspectFovMin;
			fov = aspectLimit * width * 334 / (height * 512);
			if (fov > Camera.fovScaleMax) {
				fov = Camera.fovScaleMax;
				fittedSize = fov * height * 512 / (aspectLimit * 334);
				borderSize = (width - fittedSize) / 2;
				if (drawBorders) {
					ClientState.activeToolkit.la();
					ClientState.activeToolkit.fillRect(borderSize, x, y, height, -16777216);
					ClientState.activeToolkit.fillRect(borderSize, x + width - borderSize, y, height, -16777216);
				}
				x += borderSize;
				width -= borderSize * 2;
			}
		} else if (Camera.aspectFovMax < aspectFov) {
			aspectLimit = Camera.aspectFovMax;
			fov = width * aspectLimit * 334 / (height * 512);
			if (Camera.fovScaleMin > fov) {
				fov = Camera.fovScaleMin;
				fittedSize = aspectLimit * width * 334 / (fov * 512);
				borderSize = (height - fittedSize) / 2;
				if (drawBorders) {
					ClientState.activeToolkit.la();
					ClientState.activeToolkit.fillRect(width, x, y, borderSize, -16777216);
					ClientState.activeToolkit.fillRect(width, x, height + y - borderSize, borderSize, -16777216);
				}
				y += borderSize;
				height -= borderSize * 2;
			}
		}
		Camera.viewportScaledFov = height * fov / 334;
		Camera.gameAreaY = y;
		Camera.gameAreaHeight = (short) height;
		GameShell.gameAreaWidth = (short) width;
		Camera.gameAreaX = x;
	}
}
