package com.jagex.game.runetek5.client;

import com.jagex.game.runetek5.LocalisedText;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import com.jagex.game.runetek5.graphics.loginscreen.LoginScreenComponent;
import com.jagex.game.runetek5.tools.LocalizedText;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!jj")
public final class LoadingStage {

	@OriginalMember(owner = "client!jj", name = "a", descriptor = "Lclient!va;")
	public final LocalisedText stalledText;

	@OriginalMember(owner = "client!jj", name = "b", descriptor = "Z")
	public final boolean showPercentage;

	@OriginalMember(owner = "client!jj", name = "d", descriptor = "Lclient!va;")
	public final LocalisedText localisedText2;

	@OriginalMember(owner = "client!jj", name = "e", descriptor = "Z")
	public final boolean hasProgressTracking;

	@OriginalMember(owner = "client!jj", name = "f", descriptor = "I")
	private final int step;

	@OriginalMember(owner = "client!jj", name = "j", descriptor = "I")
	public final int secondaryCompletionDisplay;

	@OriginalMember(owner = "client!jj", name = "n", descriptor = "I")
	public final int primaryCompletionDisplay;

	@OriginalMember(owner = "client!jj", name = "<init>", descriptor = "(ILclient!va;I)V")
	public LoadingStage(@OriginalArg(0) int number, @OriginalArg(1) LocalisedText localisedText, @OriginalArg(2) int completionDisplay) {
		this(number, localisedText, localisedText, completionDisplay, completionDisplay, true, false);
	}

	@OriginalMember(owner = "client!jj", name = "<init>", descriptor = "(ILclient!va;Lclient!va;II)V")
	public LoadingStage(@OriginalArg(0) int number, @OriginalArg(1) LocalisedText localisedText, @OriginalArg(2) LocalisedText localisedText2, @OriginalArg(3) int primaryCompletionDisplay, @OriginalArg(4) int secondaryCompletionDisplay) {
		this(number, localisedText, localisedText2, primaryCompletionDisplay, secondaryCompletionDisplay, true, false);
	}

	@OriginalMember(owner = "client!jj", name = "<init>", descriptor = "(ILclient!va;Lclient!va;IIZZ)V")
	public LoadingStage(@OriginalArg(0) int step, @OriginalArg(1) LocalisedText localisedText, @OriginalArg(2) LocalisedText localisedText2, @OriginalArg(3) int primaryCompletionDisplay, @OriginalArg(4) int secondaryCompletionDisplay, @OriginalArg(5) boolean showPercentage, @OriginalArg(6) boolean hasProgressTracking) {
		this.stalledText = localisedText;
		this.secondaryCompletionDisplay = secondaryCompletionDisplay;
		this.step = step;
		this.primaryCompletionDisplay = primaryCompletionDisplay;
		this.localisedText2 = localisedText2;
		this.hasProgressTracking = hasProgressTracking;
		this.showPercentage = showPercentage;
	}

	@OriginalMember(owner = "client!jj", name = "a", descriptor = "(I)I")
	public int getStep() {
		return this.step;
	}

	@OriginalMember(owner = "client!jj", name = "toString", descriptor = "()Ljava/lang/String;")
	@Override
	public String toString() {
		throw new IllegalStateException();
	}
	@OriginalMember(owner = "client!qca", name = "c", descriptor = "[Lclient!dca;")
	public static LoginScreenComponent[] gametipsToDisplay;

	@OriginalMember(owner = "client!hm", name = "d", descriptor = "[Lclient!jj;")
	public static LoadingStage[] LOADING_STAGES;
	@OriginalMember(owner = "client!nca", name = "H", descriptor = "Ljava/lang/String;")
	public static String LOADING_TEXT;

	@OriginalMember(owner = "client!gf", name = "f", descriptor = "I")
	public static int lastDisplayedGametip = -1;

	@OriginalMember(owner = "client!tr", name = "h", descriptor = "I")
	public static int displayedLoadingPercentage;
	@OriginalMember(owner = "client!et", name = "L", descriptor = "Lclient!jj;")
	public static LoadingStage LOADING_STAGE;
	@OriginalMember(owner = "client!jj", name = "z", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_17 = new LoadingStage(14, LocalizedText.DOWNLOADING_UPDATES, LocalizedText.DOWNLOADING_UPDATES, 94, 95);

	@OriginalMember(owner = "client!jj", name = "F", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_23 = new LoadingStage(20, LocalizedText.DOWNLOADING_UPDATES, 100);

	@OriginalMember(owner = "client!jj", name = "p", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_7 = new LoadingStage(4, LocalizedText.CHECKING_FOR_UPDATES, LocalizedText.CHECKING_FOR_UPDATES, 3, 4);

	@OriginalMember(owner = "client!jj", name = "w", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_14 = new LoadingStage(11, LocalizedText.DOWNLOADING_UPDATES, LocalizedText.DOWNLOADING_UPDATES, 0, 92, true, true);

	@OriginalMember(owner = "client!jj", name = "g", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_3 = new LoadingStage(0, LocalizedText.CHECKING_FOR_UPDATES, LocalizedText.CHECKING_FOR_UPDATES, 0, 1);

	@OriginalMember(owner = "client!jj", name = "u", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_12 = new LoadingStage(9, LocalizedText.CHECKING_FOR_UPDATES, 99);

	@OriginalMember(owner = "client!jj", name = "B", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_19 = new LoadingStage(16, LocalizedText.DOWNLOADING_UPDATES, 97);

	@OriginalMember(owner = "client!jj", name = "C", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_20 = new LoadingStage(17, LocalizedText.DOWNLOADING_UPDATES, 97);

	@OriginalMember(owner = "client!jj", name = "E", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_22 = new LoadingStage(19, LocalizedText.DOWNLOADING_UPDATES, 100);

	@OriginalMember(owner = "client!jj", name = "m", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_5 = new LoadingStage(2, LocalizedText.CHECKING_FOR_UPDATES, LocalizedText.CHECKING_FOR_UPDATES, 2, 3);

	@OriginalMember(owner = "client!jj", name = "r", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_9 = new LoadingStage(6, LocalizedText.CHECKING_FOR_UPDATES, 4);

	@OriginalMember(owner = "client!jj", name = "t", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_11 = new LoadingStage(8, LocalizedText.CHECKING_FOR_UPDATES, LocalizedText.CHECKING_FOR_UPDATES, 5, 98, true, true);

	@OriginalMember(owner = "client!jj", name = "v", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_13 = new LoadingStage(10, LocalizedText.CHECKING_FOR_UPDATES, 100);

	@OriginalMember(owner = "client!jj", name = "A", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_18 = new LoadingStage(15, LocalizedText.DOWNLOADING_UPDATES, LocalizedText.DOWNLOADING_UPDATES, 96, 97);

	@OriginalMember(owner = "client!jj", name = "o", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_6 = new LoadingStage(3, LocalizedText.CHECKING_FOR_UPDATES, 3);

	@OriginalMember(owner = "client!jj", name = "q", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_8 = new LoadingStage(5, LocalizedText.CHECKING_FOR_UPDATES, 4);

	@OriginalMember(owner = "client!jj", name = "c", descriptor = "(I)[Lclient!jj;")
	public static LoadingStage[] getValues() {
		return new LoadingStage[] { aClass167_3, aClass167_4, aClass167_5, aClass167_6, aClass167_7, aClass167_8, aClass167_9, aClass167_10, aClass167_11, aClass167_12, aClass167_13, aClass167_14, aClass167_15, LoadingStage.aClass167_16, aClass167_17, aClass167_18, aClass167_19, aClass167_20, aClass167_21, aClass167_22, aClass167_23 };
	}

	@OriginalMember(owner = "client!jj", name = "x", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_15 = new LoadingStage(12, LocalizedText.DOWNLOADING_UPDATES, LocalizedText.DOWNLOADING_UPDATES, 92, 92);

	@OriginalMember(owner = "client!jj", name = "s", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_10 = new LoadingStage(7, LocalizedText.CHECKING_FOR_UPDATES, LocalizedText.CHECKING_FOR_UPDATES, 4, 5);

	@OriginalMember(owner = "client!jj", name = "l", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_4 = new LoadingStage(1, LocalizedText.CHECKING_FOR_UPDATES, 2);

	@OriginalMember(owner = "client!jj", name = "D", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_21 = new LoadingStage(18, LocalizedText.DOWNLOADING_UPDATES, 100);
	@OriginalMember(owner = "client!vh", name = "e", descriptor = "J")
	public static long loadingStageChangedTime;

	@OriginalMember(owner = "client!gha", name = "b", descriptor = "(I)V")
	public static void initializeTips() {
		if (LoadingStage.gametipsToDisplay == null) {
			return;
		}
		@Pc(9) LoginScreenComponent[] loadingScreens = LoadingStage.gametipsToDisplay;
		for (@Pc(11) int i = 0; i < loadingScreens.length; i++) {
			@Pc(17) LoginScreenComponent loadingScreen = loadingScreens[i];
			loadingScreen.init();
		}
	}
	@OriginalMember(owner = "client!gr", name = "f", descriptor = "[I")
	public static final int[] JS5_ARCHIVE_LOAD_WEIGHTS = new int[] { 4, 4, 1, 2, 6, 4, 2, 44, 2, 2, 2, 2, 2, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1 };

	@OriginalMember(owner = "client!pn", name = "e", descriptor = "J")
	public static long lastGcTime = 0L;

	@OriginalMember(owner = "client!wq", name = "a", descriptor = "Z")
	public static boolean safeModeChosenAtStartup = false;

	@OriginalMember(owner = "client!jj", name = "y", descriptor = "Lclient!jj;")
	public static final LoadingStage aClass167_16 = new LoadingStage(13, LocalizedText.DOWNLOADING_UPDATES, LocalizedText.DOWNLOADING_UPDATES, 92, 93);

	@OriginalMember(owner = "client!jf", name = "r", descriptor = "J")
	public static long gcWaitStartTime = 0L;

	@OriginalMember(owner = "client!cb", name = "g", descriptor = "I")
	public static int js5IndexPercentBaseline = -1;
}
