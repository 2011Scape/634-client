package com.jagex.game.runetek5.core.constants

/**
 * What a mini menu entry does when clicked (MiniMenuEntry.clickType). Each id is confirmed by the
 * packet MiniMenuActionExecutor sends for it. An entry the client wants below the others has 2000
 * added to its id; ids from 1000 up sort below the ones under 1000.
 */
object MiniMenuAction {

	/** A heading that cannot be clicked (the "other level" player line). */
	const val LABEL: Int = -1

	const val TGT_LOC: Int = 2

	const val OPLOC1: Int = 3

	const val OPLOC2: Int = 4

	const val OPOBJ5: Int = 5

	/** Use the selected component on another component. */
	const val TGT_BUTTON: Int = 6

	const val OPPLAYER6: Int = 8

	const val OPLOC3: Int = 9

	const val OPOBJ2: Int = 10

	const val OPPLAYER1: Int = 11

	const val FACE_SQUARE: Int = 12

	/** Selects a component to use on something else. */
	const val IF_BUTTONT: Int = 13

	const val TGT_GROUND: Int = 15

	const val PAUSE_BUTTON: Int = 16

	const val OPPLAYER2: Int = 17

	/** A component's options 1 to 5. */
	const val IF_BUTTONX1: Int = 18

	const val WALK: Int = 19

	const val OPNPC2: Int = 20

	const val OPOBJ1: Int = 21

	const val OPOBJ4: Int = 22

	const val OPPLAYER4: Int = 23

	const val OPNPC1: Int = 25

	const val TGT_NPC: Int = 30

	const val OPNPC3: Int = 44

	const val OPPLAYER3: Int = 45

	const val OPNPC4: Int = 46

	const val OPOBJ3: Int = 47

	const val OPPLAYER5: Int = 48

	const val TGT_OBJ: Int = 49

	const val TGT_SELF: Int = 50

	const val TGT_PLAYER: Int = 51

	const val OPPLAYER7: Int = 57

	const val OPPLAYER8: Int = 58

	const val OPLOC4: Int = 59

	const val OPNPC5: Int = 60

	/** Examine a loc. */
	const val OPLOC6: Int = 1001

	const val OP_MAPELEMENT3: Int = 1002

	const val OP_MAPELEMENT4: Int = 1003

	const val CANCEL: Int = 1004

	const val OP_MAPELEMENT5: Int = 1006

	const val OPLOC5: Int = 1007

	/** Examine an npc. */
	const val OPNPC6: Int = 1008

	const val OP_MAPELEMENT1: Int = 1009

	/** Examine an obj. */
	const val OPOBJ6: Int = 1010

	/** A component's options 6 to 10. */
	const val IF_BUTTONX2: Int = 1011

	const val OP_MAPELEMENT2: Int = 1012

	/**
	 * Not part of the original client: the click types the plugins' menu actions get
	 * (PluginRuntime.registerMenuAction), one each, from 1100 up so that they sort below the
	 * original entries and under 2000, the "below" flag.
	 */
	const val PLUGIN_FIRST: Int = 1100

	const val PLUGIN_LAST: Int = 1199
}
