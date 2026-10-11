package com.jagex.game.runetek5;

import com.jagex.game.runetek5.DisplayModeDetails;
import com.jagex.game.runetek5.core.util.Arrays;
import com.jagex.game.runetek5.sign.SignLink;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.preference.GamePreferences;
import com.jagex.game.runetek5.client.GameShell;

public final class Fullscreen {

	@OriginalMember(owner = "client!ev", name = "b", descriptor = "(I)[Lclient!b;")
	public static DisplayModeDetails[] getActiveDisplayModes() {
		if (Fullscreen.aClass25Array1 == null) {
			@Pc(17) DisplayModeDetails[] displayModes = SignLink.getDisplayModes(GameShell.signLink);
			@Pc(21) DisplayModeDetails[] filteredModes = new DisplayModeDetails[displayModes.length];
			@Pc(23) int length = 0;
			@Pc(28) int maxScreenSize = GamePreferences.uid.maxScreenSize.getValue();
			@Pc(100) int j;
			@Pc(106) DisplayModeDetails storedMode;
			label70: for (@Pc(30) int i = 0; i < displayModes.length; i++) {
				@Pc(36) DisplayModeDetails mode = displayModes[i];
				if ((mode.bitDepth <= 0 || mode.bitDepth >= 24) && mode.width >= 800 && mode.height >= 600 && (maxScreenSize != 2 || mode.width <= 800 && mode.height <= 600) && (maxScreenSize != 1 || mode.width <= 1024 && mode.height <= 768)) {
					for (j = 0; j < length; j++) {
						storedMode = filteredModes[j];
						if (storedMode.width == mode.width && mode.height == storedMode.height) {
							if (storedMode.bitDepth < mode.bitDepth) {
								filteredModes[j] = mode;
							}
							continue label70;
						}
					}
					filteredModes[length] = mode;
					length++;
				}
			}
			Fullscreen.aClass25Array1 = new DisplayModeDetails[length];
			Arrays.copy(filteredModes, 0, Fullscreen.aClass25Array1, 0, length);
			@Pc(166) int[] areas = new int[Fullscreen.aClass25Array1.length];
			for (j = 0; j < Fullscreen.aClass25Array1.length; j++) {
				storedMode = Fullscreen.aClass25Array1[j];
				areas[j] = storedMode.width * storedMode.height;
			}
			IntKeyedQuicksort.sort(Fullscreen.aClass25Array1, areas);
		}
		return Fullscreen.aClass25Array1;
	}
	@OriginalMember(owner = "client!si", name = "d", descriptor = "[Lclient!b;")
	public static DisplayModeDetails[] aClass25Array1;
}
