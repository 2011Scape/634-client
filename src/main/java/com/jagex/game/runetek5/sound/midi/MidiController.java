package com.jagex.game.runetek5.sound.midi;

public final class MidiController {

	public static final int BANK_SELECT_COARSE = 0;

	public static final int MODULATION_WHEEL_COARSE = 1;

	public static final int MAIN_VOLUME_COARSE = 7;

	public static final int PAN_COARSE = 10;

	public static final int BANK_SELECT_FINE = 32;

	public static final int MODULATION_WHEEL_FINE = 33;

	public static final int MAIN_VOLUME_FINE = 39;

	public static final int PAN_FINE = 42;

	public static final int HOLD = 64;

	public static final int PORTAMENTO_PEDAL = 65;

	public static final int NON_REGISTERED_PARAM_FINE = 99;

	public static final int NON_REGISTERED_PARAM_COARSE = 98;

	public static final int REGISTERED_PARAM_FINE = 101;

	public static final int REGISTERED_PARAM_COARSE = 100;

	public static final int ALL_SOUND_OFF = 120;

	public static final int ALL_CONTROLLERS_OFF = 121;

	public static final int ALL_NOTES_OFF = 123;

	private MidiController() {
		/* empty */
	}
}
