package com.jagex.game.runetek5.config

import com.jagex.game.runetek5.config.bas.BasTypeList
import com.jagex.game.runetek5.config.cursor.CursorTypeList
import com.jagex.game.runetek5.config.enumtype.EnumTypeList
import com.jagex.game.runetek5.config.flu.FluTypeList
import com.jagex.game.runetek5.config.varp.VarbitTypeList
import com.jagex.game.runetek5.config.mec.MECTypeList
import com.jagex.game.runetek5.config.quickchat.QuickchatMessageTypeList
import com.jagex.game.runetek5.config.sequence.spot.SpotAnimationTypeList
import com.jagex.game.runetek5.js5.Js5ResourceProvider
import com.jagex.game.runetek5.config.quest.QuestTypeList
import com.jagex.game.runetek5.config.sun.SunTypeList
import com.jagex.game.runetek5.config.skyboxtype.SkyBoxTypeList
import com.jagex.game.runetek5.config.vartype.player.VarPlayerTypeListClient
import org.openrs2.deob.annotation.OriginalMember
import com.jagex.game.runetek5.config.sequence.SeqTypeList
import com.jagex.game.runetek5.config.loctype.LocTypeList
import com.jagex.game.runetek5.config.npctype.NPCTypeList
import com.jagex.game.runetek5.config.objtype.ObjTypeList
import com.jagex.game.runetek5.config.light.LightTypeList
import com.jagex.game.runetek5.config.param.ParamTypeList
import com.jagex.game.runetek5.config.quickchat.QuickchatCategoryTypeList
import com.jagex.game.runetek5.config.invtype.InvTypeList
import com.jagex.game.runetek5.config.vartype.VarcstrTypeList
import com.jagex.game.runetek5.config.flo.FloTypeList
import com.jagex.game.runetek5.config.idktype.IDKTypeList
import com.jagex.game.runetek5.config.mapsprite.MapSpriteTypeList
import com.jagex.game.runetek5.interaction.chat.ChatBox
import com.jagex.game.runetek5.statics.CS2State
import com.jagex.game.runetek5.config.varc.VarcTypeList
import com.jagex.game.runetek5.graphics.hint.HintArrow
import com.jagex.game.runetek5.config.hitsplat.HitSplatTypeList
import com.jagex.game.runetek5.config.rsinterface.Component
import com.jagex.game.runetek5.config.struct.StructTypeList
import com.jagex.game.runetek5.OverlayManager
import com.jagex.game.runetek5.config.fonttype.FontType
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance
import com.jagex.game.runetek5.client.ClientState
import com.jagex.game.runetek5.Cursor
import com.jagex.game.runetek5.EnvironmentSamplerCacheReset
import com.jagex.game.runetek5.FriendChat
import com.jagex.game.runetek5.ModeWhere
import com.jagex.game.runetek5.cache.node.lru.ClientScript
import com.jagex.game.runetek5.client.ServerEnvironment
import com.jagex.game.runetek5.config.particle.ParticleProducerType
import com.jagex.game.runetek5.config.rsinterface.itemcontainer.ItemContainer
import com.jagex.game.runetek5.entity.PlayerEntity
import com.jagex.game.runetek5.rendering.Renderers
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

object TypeLists {
	@OriginalMember(owner = "client!su", name = "n", descriptor = "Lclient!pg;")
	@JvmField
	var CURSOR: CursorTypeList? = null

	@OriginalMember(owner = "client!naa", name = "ab", descriptor = "Lclient!qga;")
	@JvmField
	var QUEST: QuestTypeList? = null

	@OriginalMember(owner = "client!kia", name = "u", descriptor = "Lclient!aha;")
	@JvmField
	var QUICK_CHAT_MESSAGE: QuickchatMessageTypeList? = null

	@OriginalMember(owner = "client!gda", name = "A", descriptor = "Lclient!iba;")
	@JvmField
	var ENUM: EnumTypeList? = null

	@OriginalMember(owner = "client!ur", name = "d", descriptor = "Lclient!rv;")
	@JvmField
	var VAR: VarPlayerTypeListClient? = null

	@OriginalMember(owner = "client!hw", name = "j", descriptor = "Lclient!lca;")
	@JvmField
	var SKYBOX: SkyBoxTypeList? = null

	@OriginalMember(owner = "client!fg", name = "e", descriptor = "Lclient!qe;")
	@JvmField
	var FLU: FluTypeList? = null

	@OriginalMember(owner = "client!qha", name = "J", descriptor = "Lclient!cn;")
	@JvmField
	var SPOT_ANIM: SpotAnimationTypeList? = null

	@OriginalMember(owner = "client!naa", name = "V", descriptor = "Lclient!on;")
	@JvmField
	var MEC: MECTypeList? = null

	@OriginalMember(owner = "client!paa", name = "T", descriptor = "Lclient!uga;")
	@JvmField
	var BAS: BasTypeList? = null

	@OriginalMember(owner = "client!nh", name = "d", descriptor = "Lclient!hq;")
	@JvmField
	var SUN: SunTypeList? = null

	@OriginalMember(owner = "client!sia", name = "f", descriptor = "Lclient!qs;")
	@JvmField
	var VARBIT: VarbitTypeList? = null

	@OriginalMember(owner = "client!qj", name = "i", descriptor = "Lclient!vda;")
	@JvmField
	var NPC: NPCTypeList? = null

	@OriginalMember(owner = "client!gt", name = "t", descriptor = "Lclient!lfa;")
	@JvmField
	var SEQ: SeqTypeList? = null

	@OriginalMember(owner = "client!aw", name = "O", descriptor = "Lclient!uha;")
	@JvmField
	var LOC: LocTypeList? = null

	@OriginalMember(owner = "client!wn", name = "d", descriptor = "Lclient!ub;")
	@JvmField
	var OBJ: ObjTypeList? = null
	@OriginalMember(owner = "client!ab", name = "C", descriptor = "Lclient!qf;")
	@JvmField
	var LIGHT: LightTypeList? = null
	@OriginalMember(owner = "client!iaa", name = "z", descriptor = "Lclient!dc;")
	@JvmField
	var PARAMS: ParamTypeList? = null
	@OriginalMember(owner = "client!rca", name = "b", descriptor = "Lclient!sga;")
	@JvmField
	var QUICK_CHAT_CATEGORY: QuickchatCategoryTypeList? = null
	@OriginalMember(owner = "client!ep", name = "l", descriptor = "Lclient!ut;")
	@JvmField
	var OP_MAPELEMENT2: InvTypeList? = null

	@OriginalMember(owner = "client!li", name = "e", descriptor = "Lclient!lba;")
	@JvmField
	var aClass188_1: VarcstrTypeList? = null
	@OriginalMember(owner = "client!qi", name = "b", descriptor = "(I)V")
	@JvmStatic
	fun cleanCaches() {
		TypeLists.aClass49_3!!.cacheClean()
		TypeLists.FLU!!.cacheClean()
		TypeLists.IDK!!.cacheClean()
		TypeLists.LOC!!.cacheClean()
		TypeLists.NPC!!.cacheClean()
		TypeLists.OBJ!!.cacheClean()
		TypeLists.SEQ!!.cacheClean()
		TypeLists.SPOT_ANIM!!.cacheClean()
		TypeLists.VARBIT!!.cacheClean()
		TypeLists.VAR!!.cacheClean()
		TypeLists.BAS!!.cacheClean()
		TypeLists.MEC!!.cacheClean()
		TypeLists.renderer!!.cacheClean()
		TypeLists.PARAMS!!.cacheClean()
		TypeLists.QUEST!!.cacheClean()
		TypeLists.SKYBOX!!.cacheClean()
		TypeLists.SUN!!.cacheClean()
		TypeLists.LIGHT!!.cacheClean()
		TypeLists.CURSOR!!.cacheClean()
		TypeLists.STRUCT!!.cacheClean()
		TypeLists.aClass307_2!!.cacheClean()
		PlayerAppearance.processCacheCycle()
		Component.cleanCaches()
		FontType.cacheClean()
		PlayerEntity.cleanPlayerHintModelCache()
		HintArrow.cacheClean()
		OverlayManager.aClass169_22.clean(5)
		OverlayManager.aClass169_17.clean(5)
		OverlayManager.aClass169_7.clean(5)
		ChatBox.CHAT_ICONS.clean(5)
		CS2State.aClass169_33.clean(5)
	}

	@OriginalMember(owner = "client!ak", name = "b", descriptor = "Lclient!oda;")
	@JvmField
	var renderer: MapSpriteTypeList? = null

	@OriginalMember(owner = "client!tga", name = "P", descriptor = "Lclient!oha;")
	@JvmField
	var IDK: IDKTypeList? = null

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(I)V")
	@JvmStatic
	fun removeSoftReferences() {
		TypeLists.aClass49_3!!.cacheRemoveSoftReferences()
		TypeLists.FLU!!.cacheRemoveSoftReferences()
		TypeLists.IDK!!.cacheRemoveSoftReferences()
		TypeLists.LOC!!.cacheRemoveSoftReferences()
		TypeLists.NPC!!.cacheRemoveSoftReferences()
		TypeLists.OBJ!!.cacheRemoveSoftReferences()
		TypeLists.SEQ!!.cacheRemoveSoftReferences()
		TypeLists.SPOT_ANIM!!.cacheRemoveSoftReferences()
		TypeLists.VARBIT!!.cacheRemoveSoftReferences()
		TypeLists.VAR!!.cacheRemoveSoftReferences()
		TypeLists.BAS!!.cacheRemoveSoftReferences()
		TypeLists.renderer!!.cacheRemoveSoftReferences()
		TypeLists.MEC!!.cacheRemoveSoftReferences()
		TypeLists.QUEST!!.cacheRemoveSoftReferences()
		TypeLists.PARAMS!!.cacheRemoveSoftReferences()
		TypeLists.SKYBOX!!.cacheRemoveSoftReferences()
		TypeLists.SUN!!.cacheRemoveSoftReferences()
		TypeLists.LIGHT!!.cacheRemoveSoftReferences()
		TypeLists.CURSOR!!.cacheRemoveSoftReferences()
		TypeLists.STRUCT!!.cacheRemoveSoftReferences()
		TypeLists.aClass307_2!!.cacheRemoveSoftReferences()
		PlayerAppearance.removePlayerAppearanceSoftReferences()
		Component.removeCacheSoftReferences()
		FontType.removeFontTypeSoftReferences()
		PlayerEntity.removePlayerHintModelSoftRefs()
		HintArrow.cacheRemoveSoftReferences()
		OverlayManager.aClass169_22.removeSoftReferences()
		OverlayManager.aClass169_17.removeSoftReferences()
		OverlayManager.aClass169_7.removeSoftReferences()
		ChatBox.CHAT_ICONS.removeSoftReferences()
		CS2State.aClass169_33.removeSoftReferences()
	}

	@OriginalMember(owner = "client!sia", name = "d", descriptor = "Lclient!cb;")
	@JvmField
	var aClass49_3: FloTypeList? = null
	@OriginalMember(owner = "client!pp", name = "B", descriptor = "Lclient!uea;")
	@JvmField
	var aClass338_1: VarcTypeList? = null
	@OriginalMember(owner = "client!kk", name = "k", descriptor = "Lclient!sb;")
	@JvmField
	var aClass307_2: HitSplatTypeList? = null

	@OriginalMember(owner = "client!nv", name = "a", descriptor = "Lclient!kb;")
	@JvmField
	var STRUCT: StructTypeList? = null
	@OriginalMember(owner = "client!ql", name = "m", descriptor = "[S")
	@JvmField
	var recolorPalette: ShortArray? = null

	@OriginalMember(owner = "client!cn", name = "k", descriptor = "Z")
	@JvmField
	var cacheCleaningEnabled: Boolean = false

	@OriginalMember(owner = "client!jg", name = "b", descriptor = "(B)V")
	@JvmStatic
	fun resetAllCaches() {
		TypeLists.aClass49_3!!.cacheReset()
		TypeLists.FLU!!.cacheReset()
		TypeLists.IDK!!.cacheReset()
		TypeLists.LOC!!.cacheReset()
		TypeLists.NPC!!.cacheReset()
		TypeLists.OBJ!!.cacheReset()
		TypeLists.SEQ!!.cacheReset()
		TypeLists.SPOT_ANIM!!.cacheReset()
		TypeLists.VARBIT!!.cacheReset()
		TypeLists.VAR!!.cacheReset()
		TypeLists.BAS!!.cacheReset()
		TypeLists.renderer!!.cacheReset()
		TypeLists.MEC!!.cacheReset()
		TypeLists.QUEST!!.cacheReset()
		TypeLists.PARAMS!!.cacheReset()
		TypeLists.SKYBOX!!.cacheReset()
		TypeLists.SUN!!.cacheReset()
		TypeLists.LIGHT!!.cacheReset()
		TypeLists.CURSOR!!.cacheReset()
		TypeLists.STRUCT!!.cacheReset()
		TypeLists.aClass307_2!!.cacheReset()
		PlayerAppearance.resetCache()
		Component.resetCaches()
		FontType.resetFontTypeCache()
		ItemContainer.resetItemContainerModelCache()
		if (ModeWhere.WTRC !== ServerEnvironment.LIVE) {
			@Pc(81) var i: Int = 0
			while (i < PlayerAppearance.TEST_ENVIRONMENT_APPEARANCE_OVERRIDE_DATA!!.size) {
				PlayerAppearance.TEST_ENVIRONMENT_APPEARANCE_OVERRIDE_DATA!![i] = null
				i++
			}
			Cursor.interaction = 0
		}
		EnvironmentSamplerCacheReset.reset()
		PlayerEntity.clearPlayerHintModels()
		HintArrow.cacheReset()
		ParticleProducerType.clearCache()
		FriendChat.clearParticleCache()
		CS2State.aClass169_33.reset()
		ClientState.activeToolkit!!.cacheReset()
		ClientScript.clearCache()
		Renderers.freeGpuItemRenderer()
		Resource.FRAME_SET!!.discardUnpacked()
		Resource.FRAME_BASE!!.discardUnpacked()
		Resource.CONFIG!!.discardUnpacked()
		Resource.INTERFACE!!.discardUnpacked()
		Resource.SOUND_EFFECT!!.discardUnpacked()
		Resource.MAPS!!.discardUnpacked()
		Resource.MUSIC!!.discardUnpacked()
		Resource.MODEL!!.discardUnpacked()
		Resource.SPRITES!!.discardUnpacked()
		Resource.TEXTURE!!.discardUnpacked()
		Resource.HUFFMAN!!.discardUnpacked()
		Resource.JINGLE!!.discardUnpacked()
		Resource.CS2!!.discardUnpacked()
		Resource.FONT_METRICS!!.discardUnpacked()
		Resource.MIDI_INSTRUMENT!!.discardUnpacked()
		Resource.SOUND_MIDI!!.discardUnpacked()
		Resource.LOC!!.discardUnpacked()
		Resource.ENUM!!.discardUnpacked()
		Resource.NPC!!.discardUnpacked()
		Resource.OBJ!!.discardUnpacked()
		Resource.SEQ!!.discardUnpacked()
		Resource.GFX!!.discardUnpacked()
		Resource.VARBIT!!.discardUnpacked()
		Resource.WORLD_MAP!!.discardUnpacked()
		Resource.QC_MESSAGES!!.discardUnpacked()
		Resource.QC_MENU!!.discardUnpacked()
		Resource.MATERIAL!!.discardUnpacked()
		Resource.PARTICLE!!.discardUnpacked()
		Resource.DEFAULTS!!.discardUnpacked()
		Resource.BILLBOARD!!.discardUnpacked()
		Resource.NATIVE_LIBRARY!!.discardUnpacked()
		Resource.SHADER!!.discardUnpacked()
		Resource.VORBIS!!.discardUnpacked()
		OverlayManager.aClass169_22.reset()
		OverlayManager.aClass169_17.reset()
		OverlayManager.aClass169_7.reset()
		ChatBox.CHAT_ICONS.reset()
	}
}

