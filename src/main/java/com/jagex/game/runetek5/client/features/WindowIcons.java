package com.jagex.game.runetek5.client.features;

import java.awt.Frame;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Not part of the original client: the client's windows carry Void's icon (icons/icon-*.png), in
 * every size the desktop may ask for, instead of the JVM's default.
 */
public final class WindowIcons {

	private static final int[] SIZES = { 16, 32, 64, 128, 256 };

	private static List<Image> icons = null;

	private WindowIcons() {
		/* empty */
	}

	/** Gives the frame Void's icon; a size that cannot be read is left out. */
	public static void apply(Frame frame) {
		if (icons == null) {
			icons = new ArrayList<Image>();
			for (int i = 0; i < SIZES.length; i++) {
				URL resource = WindowIcons.class.getResource("/icons/icon-" + SIZES[i] + ".png");
				if (resource == null) {
					continue;
				}
				try {
					icons.add(ImageIO.read(resource));
				} catch (Exception ex) {
					/* leave this size out */
				}
			}
		}
		if (!icons.isEmpty()) {
			frame.setIconImages(icons);
		}
	}
}
