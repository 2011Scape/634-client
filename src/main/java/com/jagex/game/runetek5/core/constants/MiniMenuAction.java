package com.jagex.game.runetek5.core.constants;

/**
 * What a mini menu entry does when clicked (MiniMenuEntry.clickType). Each id is confirmed by the
 * packet MiniMenuActionExecutor sends for it. An entry the client wants below the others has 2000
 * added to its id; ids from 1000 up sort below the ones under 1000.
 */
public final class MiniMenuAction {

	/** A heading that cannot be clicked (the "other level" player line). */
	public static final int LABEL = -1;

	public static final int TGT_LOC = 2;

	public static final int OPLOC1 = 3;

	public static final int OPLOC2 = 4;

	public static final int OPOBJ5 = 5;

	/** Use the selected component on another component. */
	public static final int TGT_BUTTON = 6;

	public static final int OPPLAYER6 = 8;

	public static final int OPLOC3 = 9;

	public static final int OPOBJ2 = 10;

	public static final int OPPLAYER1 = 11;

	public static final int FACE_SQUARE = 12;

	/** Selects a component to use on something else. */
	public static final int IF_BUTTONT = 13;

	public static final int TGT_GROUND = 15;

	public static final int PAUSE_BUTTON = 16;

	public static final int OPPLAYER2 = 17;

	/** A component's options 1 to 5. */
	public static final int IF_BUTTONX1 = 18;

	public static final int WALK = 19;

	public static final int OPNPC2 = 20;

	public static final int OPOBJ1 = 21;

	public static final int OPOBJ4 = 22;

	public static final int OPPLAYER4 = 23;

	public static final int OPNPC1 = 25;

	public static final int TGT_NPC = 30;

	public static final int OPNPC3 = 44;

	public static final int OPPLAYER3 = 45;

	public static final int OPNPC4 = 46;

	public static final int OPOBJ3 = 47;

	public static final int OPPLAYER5 = 48;

	public static final int TGT_OBJ = 49;

	public static final int TGT_SELF = 50;

	public static final int TGT_PLAYER = 51;

	public static final int OPPLAYER7 = 57;

	public static final int OPPLAYER8 = 58;

	public static final int OPLOC4 = 59;

	public static final int OPNPC5 = 60;

	/** Examine a loc. */
	public static final int OPLOC6 = 1001;

	public static final int OP_MAPELEMENT3 = 1002;

	public static final int OP_MAPELEMENT4 = 1003;

	public static final int CANCEL = 1004;

	public static final int OP_MAPELEMENT5 = 1006;

	public static final int OPLOC5 = 1007;

	/** Examine an npc. */
	public static final int OPNPC6 = 1008;

	public static final int OP_MAPELEMENT1 = 1009;

	/** Examine an obj. */
	public static final int OPOBJ6 = 1010;

	/** A component's options 6 to 10. */
	public static final int IF_BUTTONX2 = 1011;

	public static final int OP_MAPELEMENT2 = 1012;

	/**
	 * Not part of the original client: marks or unmarks a tile (TileMarkers). An id the original
	 * menu never uses, from 1000 up so that it sorts below the walk entry.
	 */
	public static final int TILE_MARKER = 1013;

	private MiniMenuAction() {
		/* empty */
	}
}
