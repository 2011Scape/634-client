package com.jagex.game.runetek5.preference

import classFieldsAvailable
import com.jagex.game.runetek5.preference.AnimateBackgroundOption
import com.jagex.game.runetek5.preference.impl.AntiAliasingPreference
import com.jagex.game.runetek5.preference.BloomOption
import com.jagex.game.runetek5.preference.impl.BrightnessOption
import com.jagex.game.runetek5.preference.impl.MapSizePreference
import com.jagex.game.runetek5.preference.MonoStereoPreference
import com.jagex.game.runetek5.preference.StartupSafeModePreference
import com.jagex.game.runetek5.preference.impl.FlickeringEffectPreference
import com.jagex.game.runetek5.preference.CharacterShadowPreference
import com.jagex.game.runetek5.preference.CustomCursorsPreference
import com.jagex.game.runetek5.preference.CpuUsageOption
import com.jagex.game.runetek5.EnvironmentContext
import com.jagex.game.runetek5.preference.impl.FogOption
import com.jagex.game.runetek5.preference.GraphicsQuality
import com.jagex.game.runetek5.preference.impl.GroundBlendingOption
import com.jagex.game.runetek5.preference.GroundDecorOption
import com.jagex.game.runetek5.preference.impl.HardShadowsOption
import com.jagex.game.runetek5.preference.impl.IdleAnimationsOption
import com.jagex.game.runetek5.preference.LoadingSequenceOption
import com.jagex.game.runetek5.preference.impl.MaxScreenSizeOption
import com.jagex.game.runetek5.preference.OrthographicOption
import com.jagex.game.runetek5.preference.impl.ParticlesOption
import com.jagex.game.runetek5.preference.RemoveRoofsOption
import com.jagex.game.runetek5.preference.impl.LightDetailPreference
import com.jagex.game.runetek5.preference.impl.ScreenSizeOption
import com.jagex.game.runetek5.preference.SmallTexturesOption
import com.jagex.game.runetek5.preference.TextureQuality
import com.jagex.game.runetek5.preference.VolumeOption
import com.jagex.game.runetek5.preference.WaterDetailOption
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.cache.node.Node
import com.jagex.game.runetek5.cache.JFS32
import com.jagex.game.runetek5.buffer.Packet
import com.jagex.game.runetek5.preference.Preference
import com.jagex.game.runetek5.preference.impl.ToolkitOption
import com.jagex.game.runetek5.graphics.ToolkitType
import com.jagex.game.runetek5.sign.SignedResource
import com.jagex.game.runetek5.sign.SignedResourceStatus
import com.jagex.game.runetek5.sign.SignLinkEnvironment
import lang.reflect.Field
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.systeminfo.SystemInfo
import com.jagex.game.runetek5.client.GameShell
import io.IOException
import com.jagex.game.runetek5.core.util.TimeUtils
import com.jagex.game.runetek5.client.ClientState
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import lang.IllegalAccessException
import util.Locale
import lang.jClass
import util.lowercase

@OriginalClass("client!dh")
class GamePreferences : Node {

	@OriginalMember(owner = "client!dh", name = "o", descriptor = "Lclient!fca;")
	@JvmField var voiceOverVolume: VolumeOption? = null

	@OriginalMember(owner = "client!dh", name = "p", descriptor = "Lclient!vca;")
	@JvmField var antiAliasing: AntiAliasingPreference? = null

	@OriginalMember(owner = "client!dh", name = "t", descriptor = "Lclient!kba;")
	@JvmField var groundDecor: GroundDecorOption? = null

	@OriginalMember(owner = "client!dh", name = "v", descriptor = "Lclient!jaa;")
	@JvmField var orthographic: OrthographicOption? = null

	@OriginalMember(owner = "client!dh", name = "x", descriptor = "Lclient!pr;")
	@JvmField var particles: ParticlesOption? = null

	@OriginalMember(owner = "client!dh", name = "z", descriptor = "Lclient!kia;")
	@JvmField var buildArea: MapSizePreference? = null

	@OriginalMember(owner = "client!dh", name = "A", descriptor = "Lclient!mfa;")
	@JvmField var fixedScreen: ScreenSizeOption? = null

	@OriginalMember(owner = "client!dh", name = "B", descriptor = "Lclient!gga;")
	@JvmField var smallTextures: SmallTexturesOption? = null

	@OriginalMember(owner = "client!dh", name = "C", descriptor = "Lclient!fv;")
	@JvmField var graphics: GraphicsQuality? = null

	@OriginalMember(owner = "client!dh", name = "D", descriptor = "Lclient!fs;")
	@JvmField var lightDetail: LightDetailPreference? = null

	@OriginalMember(owner = "client!dh", name = "E", descriptor = "Lclient!bl;")
	@JvmField var maxScreenSize: MaxScreenSizeOption? = null

	@OriginalMember(owner = "client!dh", name = "F", descriptor = "Lclient!od;")
	@JvmField var brightness: BrightnessOption? = null

	@OriginalMember(owner = "client!dh", name = "H", descriptor = "Lclient!fca;")
	@JvmField var ambientSoundVolume: VolumeOption? = null

	@OriginalMember(owner = "client!dh", name = "I", descriptor = "Lclient!dba;")
	@JvmField var textures: TextureQuality? = null

	@OriginalMember(owner = "client!dh", name = "J", descriptor = "Lclient!ph;")
	@JvmField var idleAnimations: IdleAnimationsOption? = null

	@OriginalMember(owner = "client!dh", name = "L", descriptor = "Lclient!mca;")
	@JvmField var hardShadows: HardShadowsOption? = null

	@OriginalMember(owner = "client!dh", name = "N", descriptor = "Lclient!kfa;")
	@JvmField var monoStereo: MonoStereoPreference? = null

	@OriginalMember(owner = "client!dh", name = "Q", descriptor = "Lclient!av;")
	@JvmField var bloom: BloomOption? = null

	@OriginalMember(owner = "client!dh", name = "S", descriptor = "Lclient!fca;")
	@JvmField var musicVolume: VolumeOption? = null

	@OriginalMember(owner = "client!dh", name = "T", descriptor = "Lclient!aj;")
	@JvmField var flickeringEffects: FlickeringEffectPreference? = null

	@OriginalMember(owner = "client!dh", name = "U", descriptor = "Lclient!sba;")
	@JvmField var fog: FogOption? = null

	@OriginalMember(owner = "client!dh", name = "V", descriptor = "Lclient!vca;")
	@JvmField var antiAliasingPreview: AntiAliasingPreference? = null

	@OriginalMember(owner = "client!dh", name = "X", descriptor = "Lclient!pp;")
	@JvmField var removeRoofs: RemoveRoofsOption? = null

	@OriginalMember(owner = "client!dh", name = "Y", descriptor = "Lclient!eea;")
	@JvmField var toolkitDefault: ToolkitOption? = null

	@OriginalMember(owner = "client!dh", name = "ab", descriptor = "Lclient!wca;")
	@JvmField var customCursor: CustomCursorsPreference? = null

	@OriginalMember(owner = "client!dh", name = "bb", descriptor = "Lclient!mfa;")
	@JvmField var fullScreen: ScreenSizeOption? = null

	@OriginalMember(owner = "client!dh", name = "cb", descriptor = "Lclient!fo;")
	@JvmField var animateBackground: AnimateBackgroundOption? = null

	@OriginalMember(owner = "client!dh", name = "db", descriptor = "Lclient!mm;")
	@JvmField var groundBlending: GroundBlendingOption? = null

	@OriginalMember(owner = "client!dh", name = "eb", descriptor = "Lclient!tt;")
	@JvmField var gametipStartStagePreference: LoadingSequenceOption? = null

	@OriginalMember(owner = "client!dh", name = "gb", descriptor = "Lclient!tl;")
	@JvmField var water: WaterDetailOption? = null

	@OriginalMember(owner = "client!dh", name = "hb", descriptor = "Lclient!fca;")
	@JvmField var ingameMusicVolume: VolumeOption? = null

	@OriginalMember(owner = "client!dh", name = "ib", descriptor = "Lclient!fo;")
	@JvmField var animateBackgroundOverride: AnimateBackgroundOption? = null

	@OriginalMember(owner = "client!dh", name = "kb", descriptor = "Lclient!ti;")
	@JvmField var cpu: CpuUsageOption? = null

	@OriginalMember(owner = "client!dh", name = "lb", descriptor = "Lclient!pp;")
	@JvmField var lb: RemoveRoofsOption? = null

	@OriginalMember(owner = "client!dh", name = "mb", descriptor = "Lclient!pb;")
	@JvmField var startupSafeMode: StartupSafeModePreference? = null

	@OriginalMember(owner = "client!dh", name = "rb", descriptor = "Lclient!vu;")
	@JvmField var characterShadows: CharacterShadowPreference? = null

	@OriginalMember(owner = "client!dh", name = "tb", descriptor = "Lclient!fca;")
	@JvmField var soundEffectVolume: VolumeOption? = null

	@OriginalMember(owner = "client!dh", name = "qb", descriptor = "Lclient!sj;")
	private val game: Game?

	@OriginalMember(owner = "client!dh", name = "u", descriptor = "Lclient!rc;")
	private val processorSpecs: EnvironmentContext

	@OriginalMember(owner = "client!dh", name = "sb", descriptor = "Lclient!eea;")
	@JvmField var toolkit: ToolkitOption? = null

	@OriginalMember(owner = "client!dh", name = "<init>", descriptor = "(Lclient!sj;I)V")
	constructor(@OriginalArg(0) modeGame: Game?, @OriginalArg(1) value: Int) {
		this.game = modeGame
		this.processorSpecs = EnvironmentContext(GameShell.signLink!!.signed, GameShell.maxMemory, GameShell.availableProcessors, SystemInfo.OS_ARCH!!.lowercase(util.defaultLocale()).indexOf("arm") != -1)
		this.toolkit = ToolkitOption(value, this)
		this.setDefaults(true)
	}

	@OriginalMember(owner = "client!dh", name = "<init>", descriptor = "(Lclient!cea;Lclient!sj;I)V")
	constructor(@OriginalArg(0) packet: Packet?, @OriginalArg(1) modeGame: Game?, @OriginalArg(2) value: Int) {
		this.game = modeGame
		this.processorSpecs = EnvironmentContext(GameShell.signLink!!.signed, GameShell.maxMemory, GameShell.availableProcessors, SystemInfo.OS_ARCH!!.indexOf("arm") != -1)
		this.toolkit = ToolkitOption(value, this)
		this.decode(packet)
	}

	@OriginalMember(owner = "client!dh", name = "a", descriptor = "(ILclient!cea;I)V")
	private fun decodeDeprecatedVersion(@OriginalArg(1) packet: Packet?, @OriginalArg(2) version: Int) {
		this.brightness = BrightnessOption(packet!!.readUByte(), this)
		this.animateBackground = AnimateBackgroundOption(packet!!.readUByte(), this)
		this.removeRoofs = RemoveRoofsOption(packet!!.readUByte() + 1, this)
		this.groundDecor = GroundDecorOption(packet!!.readUByte(), this)
		this.smallTextures = SmallTexturesOption(packet!!.readUByte(), this)
		this.idleAnimations = IdleAnimationsOption(packet!!.readUByte(), this)
		this.flickeringEffects = FlickeringEffectPreference(packet!!.readUByte(), this)
		packet!!.readUByte()
		this.characterShadows = CharacterShadowPreference(packet!!.readUByte(), this)
		@Pc(95) var shadowValue: Int = packet!!.readUByte()
		@Pc(97) var shadowValueV17: Int = 0
		if (version >= 17) {
			shadowValueV17 = packet!!.readUByte()
		}
		this.hardShadows = HardShadowsOption(if (shadowValueV17 < shadowValue) shadowValue else shadowValueV17, this)
		@Pc(126) var highDetailShadows: Boolean = true
		@Pc(142) var highDetailLight: Boolean
		if (version >= 2) {
			highDetailLight = packet!!.readUByte() == 1
			if (version >= 17) {
				highDetailShadows = packet!!.readUByte() == 1
			}
		} else {
			highDetailLight = packet!!.readUByte() == 1
			packet!!.readUByte()
		}
		this.lightDetail = LightDetailPreference(if (highDetailLight or highDetailShadows) 1 else 0, this)
		this.water = WaterDetailOption(packet!!.readUByte(), this)
		this.fog = FogOption(packet!!.readUByte(), this)
		this.antiAliasing = AntiAliasingPreference(packet!!.readUByte(), this)
		this.monoStereo = MonoStereoPreference(packet!!.readUByte(), this)
		this.soundEffectVolume = VolumeOption(packet!!.readUByte(), this)
		if (version >= 20) {
			this.voiceOverVolume = VolumeOption(packet!!.readUByte(), this)
		} else {
			this.voiceOverVolume = VolumeOption(this.soundEffectVolume!!.getValue(), this)
		}
		this.ingameMusicVolume = VolumeOption(packet!!.readUByte(), this)
		this.ambientSoundVolume = VolumeOption(packet!!.readUByte(), this)
		if (version >= 21) {
			this.musicVolume = VolumeOption(packet!!.readUByte(), this)
		} else {
			this.musicVolume = VolumeOption(this.ingameMusicVolume!!.getValue(), this)
		}
		if (version >= 1) {
			packet!!.readUShort()
			packet!!.readUShort()
		}
		if (version >= 3 && version < 6) {
			packet!!.readUByte()
		}
		if (version >= 4) {
			this.particles = ParticlesOption(packet!!.readUByte(), this)
		}
		packet!!.readInt()
		if (version >= 6) {
			this.fixedScreen = ScreenSizeOption(packet!!.readUByte(), this)
		}
		if (version >= 7) {
			this.startupSafeMode = StartupSafeModePreference(packet!!.readUByte(), this)
		}
		if (version >= 8) {
			packet!!.readUByte()
		}
		if (version >= 9) {
			this.buildArea = MapSizePreference(packet!!.readUByte(), this)
		}
		if (version >= 10) {
			this.bloom = BloomOption(packet!!.readUByte(), this)
		}
		if (version >= 11) {
			this.customCursor = CustomCursorsPreference(packet!!.readUByte(), this)
		}
		if (version >= 12) {
			this.idleAnimations = IdleAnimationsOption(packet!!.readUByte(), this)
		}
		if (version >= 13) {
			this.groundBlending = GroundBlendingOption(packet!!.readUByte(), this)
		}
		if (version >= 14) {
			this.toolkitDefault = ToolkitOption(packet!!.readUByte(), this)
		}
		if (version >= 15) {
			this.cpu = CpuUsageOption(packet!!.readUByte(), this)
		}
		if (version >= 16) {
			this.textures = TextureQuality(packet!!.readUByte(), this)
		}
		if (version >= 18) {
			this.graphics = GraphicsQuality(packet!!.readUByte(), this)
		}
		if (version >= 19) {
			this.maxScreenSize = MaxScreenSizeOption(packet!!.readUByte(), this)
		}
		if (version >= 22) {
			this.gametipStartStagePreference = LoadingSequenceOption(packet!!.readUByte(), this)
		}
	}

	@OriginalMember(owner = "client!dh", name = "a", descriptor = "(I)Lclient!sj;")
	fun getGame(): Game? {
		return this.game
	}

	@OriginalMember(owner = "client!dh", name = "a", descriptor = "(ZI)V")
	private fun setDefaults(@OriginalArg(0) override: Boolean) {
		if (override || this.antiAliasing == null) {
			this.antiAliasing = AntiAliasingPreference(this)
		}
		if (override || this.antiAliasingPreview == null) {
			this.antiAliasingPreview = AntiAliasingPreference(this.antiAliasing!!.getValue(), this)
		}
		if (override || this.bloom == null) {
			this.bloom = BloomOption(this)
		}
		if (override || this.brightness == null) {
			this.brightness = BrightnessOption(this)
		}
		if (override || this.buildArea == null) {
			this.buildArea = MapSizePreference(this)
		}
		if (override || this.flickeringEffects == null) {
			this.flickeringEffects = FlickeringEffectPreference(this)
		}
		if (override || this.fog == null) {
			this.fog = FogOption(this)
		}
		if (override || this.groundBlending == null) {
			this.groundBlending = GroundBlendingOption(this)
		}
		if (override || this.groundDecor == null) {
			this.groundDecor = GroundDecorOption(this)
		}
		if (override || this.idleAnimations == null) {
			this.idleAnimations = IdleAnimationsOption(this)
		}
		if (override || this.lightDetail == null) {
			this.lightDetail = LightDetailPreference(this)
		}
		if (override || this.hardShadows == null) {
			this.hardShadows = HardShadowsOption(this)
		}
		if (override || this.orthographic == null) {
			this.orthographic = OrthographicOption(this)
		}
		if (override || this.particles == null) {
			this.particles = ParticlesOption(this)
		}
		if (override || this.removeRoofs == null) {
			this.removeRoofs = RemoveRoofsOption(this)
		}
		if (override || this.lb == null) {
			this.lb = RemoveRoofsOption(this.removeRoofs!!.getValue(), this)
		}
		if (override || this.maxScreenSize == null) {
			this.maxScreenSize = MaxScreenSizeOption(this)
		}
		if (override || this.characterShadows == null) {
			this.characterShadows = CharacterShadowPreference(this)
		}
		if (override || this.smallTextures == null) {
			this.smallTextures = SmallTexturesOption(this)
		}
		if (override || this.textures == null) {
			this.textures = TextureQuality(this)
		}
		if (override || this.toolkitDefault == null) {
			this.toolkitDefault = ToolkitOption(this)
		}
		if (override || this.toolkit == null) {
			this.toolkit = ToolkitOption(this.toolkitDefault!!.getValue(), this)
		}
		if (override || this.animateBackground == null) {
			this.animateBackground = AnimateBackgroundOption(this)
		}
		if (override || this.animateBackgroundOverride == null) {
			this.animateBackgroundOverride = AnimateBackgroundOption(this.animateBackground!!.getValue(), this)
		}
		if (override || this.water == null) {
			this.water = WaterDetailOption(this)
		}
		if (override || this.fixedScreen == null) {
			this.fixedScreen = ScreenSizeOption(this)
		}
		if (override || this.fullScreen == null) {
			this.fullScreen = ScreenSizeOption(this.fixedScreen!!.getValue(), this)
		}
		if (override || this.customCursor == null) {
			this.customCursor = CustomCursorsPreference(this)
		}
		if (override || this.graphics == null) {
			this.graphics = GraphicsQuality(this)
		}
		if (override || this.cpu == null) {
			this.cpu = CpuUsageOption(this)
		}
		if (override || this.gametipStartStagePreference == null) {
			this.gametipStartStagePreference = LoadingSequenceOption(this)
		}
		if (override || this.startupSafeMode == null) {
			this.startupSafeMode = StartupSafeModePreference(this)
		}
		if (override || this.soundEffectVolume == null) {
			this.soundEffectVolume = VolumeOption(this)
		}
		if (override || this.ambientSoundVolume == null) {
			this.ambientSoundVolume = VolumeOption(this)
		}
		if (override || this.voiceOverVolume == null) {
			this.voiceOverVolume = VolumeOption(this)
		}
		if (override || this.ingameMusicVolume == null) {
			this.ingameMusicVolume = VolumeOption(this)
		}
		if (override || this.musicVolume == null) {
			this.musicVolume = VolumeOption(this)
		}
		if (override || this.monoStereo == null) {
			this.monoStereo = MonoStereoPreference(this)
		}
	}

	@OriginalMember(owner = "client!dh", name = "a", descriptor = "(ILclient!cea;)V")
	private fun decode(@OriginalArg(1) packet: Packet?) {
		if (packet == null || packet.buffer == null) {
			this.setDefaults(true)
		} else {
			@Pc(20) var version: Int = packet!!.readUByte()
			if (version < 23) {
				try {
					this.decodeDeprecatedVersion(packet, version)
				} catch (@Pc(417) ex: Exception) {
					this.setDefaults(true)
				}
				this.setDefaults(false)
			} else if (version > 24) {
				this.setDefaults(true)
			} else {
				this.antiAliasing = AntiAliasingPreference(packet!!.readUByte(), this)
				this.antiAliasingPreview = AntiAliasingPreference(this.antiAliasing!!.getValue(), this)
				this.bloom = BloomOption(packet!!.readUByte(), this)
				this.brightness = BrightnessOption(packet!!.readUByte(), this)
				this.buildArea = MapSizePreference(packet!!.readUByte(), this)
				this.flickeringEffects = FlickeringEffectPreference(packet!!.readUByte(), this)
				this.fog = FogOption(packet!!.readUByte(), this)
				this.groundBlending = GroundBlendingOption(packet!!.readUByte(), this)
				this.groundDecor = GroundDecorOption(packet!!.readUByte(), this)
				this.idleAnimations = IdleAnimationsOption(packet!!.readUByte(), this)
				this.lightDetail = LightDetailPreference(packet!!.readUByte(), this)
				this.hardShadows = HardShadowsOption(packet!!.readUByte(), this)
				if (version >= 24) {
					this.orthographic = OrthographicOption(packet!!.readUByte(), this)
				}
				this.particles = ParticlesOption(packet!!.readUByte(), this)
				this.removeRoofs = RemoveRoofsOption(packet!!.readUByte(), this)
				this.lb = RemoveRoofsOption(this.removeRoofs!!.getValue(), this)
				this.maxScreenSize = MaxScreenSizeOption(packet!!.readUByte(), this)
				this.characterShadows = CharacterShadowPreference(packet!!.readUByte(), this)
				this.smallTextures = SmallTexturesOption(packet!!.readUByte(), this)
				this.textures = TextureQuality(packet!!.readUByte(), this)
				this.toolkitDefault = ToolkitOption(packet!!.readUByte(), this)
				this.toolkit = ToolkitOption(this.toolkitDefault!!.getValue(), this)
				this.animateBackground = AnimateBackgroundOption(packet!!.readUByte(), this)
				this.animateBackgroundOverride = AnimateBackgroundOption(this.animateBackground!!.getValue(), this)
				this.water = WaterDetailOption(packet!!.readUByte(), this)
				this.fixedScreen = ScreenSizeOption(packet!!.readUByte(), this)
				this.fullScreen = ScreenSizeOption(this.fixedScreen!!.getValue(), this)
				this.customCursor = CustomCursorsPreference(packet!!.readUByte(), this)
				this.graphics = GraphicsQuality(packet!!.readUByte(), this)
				this.cpu = CpuUsageOption(packet!!.readUByte(), this)
				this.gametipStartStagePreference = LoadingSequenceOption(packet!!.readUByte(), this)
				this.startupSafeMode = StartupSafeModePreference(packet!!.readUByte(), this)
				this.soundEffectVolume = VolumeOption(packet!!.readUByte(), this)
				this.ambientSoundVolume = VolumeOption(packet!!.readUByte(), this)
				this.voiceOverVolume = VolumeOption(packet!!.readUByte(), this)
				this.ingameMusicVolume = VolumeOption(packet!!.readUByte(), this)
				this.musicVolume = VolumeOption(packet!!.readUByte(), this)
				this.monoStereo = MonoStereoPreference(packet!!.readUByte(), this)
				this.setDefaults(false)
			}
		}
		this.ensureValid()
	}

	@OriginalMember(owner = "client!dh", name = "b", descriptor = "(I)Z")
	fun isLowMemoryJava(): Boolean {
		return this.toolkit!!.isActive() && this.toolkit!!.getValue() == ToolkitType.JAVA && this.processorSpecs.getMaxMemory() < 96
	}

	@OriginalMember(owner = "client!dh", name = "a", descriptor = "(B)V")
	private fun ensureValid() {
		try {
			@Pc(9) val fields: Array<Field> = this.jClass.getDeclaredFields()
			@Pc(19) var i: Int = 0
			while (i < fields.size) {
				@Pc(25) val field: Field = fields[i]
				if (Preference::class.jClass.isAssignableFrom(field.getType())) {
					@Pc(42) val option: Preference? = field.get(this) as Preference?
					option!!.validate()
				}
				i++
			}
		} catch (@Pc(56) ex: IllegalAccessException) {
		}
	}

	@OriginalMember(owner = "client!dh", name = "d", descriptor = "(I)Lclient!cea;")
	fun encode(): Packet {
		@Pc(11) val packet: Packet = Packet(GamePreferences.getEncodedSize())
		packet.writeByte(24)
		packet.writeByte(this.antiAliasing!!.getValue())
		packet.writeByte(this.bloom!!.getValue())
		packet.writeByte(this.brightness!!.getValue())
		packet.writeByte(this.buildArea!!.getValue())
		packet.writeByte(this.flickeringEffects!!.getValue())
		packet.writeByte(this.fog!!.getValue())
		packet.writeByte(this.groundBlending!!.getValue())
		packet.writeByte(this.groundDecor!!.getValue())
		packet.writeByte(this.idleAnimations!!.getValue())
		packet.writeByte(this.lightDetail!!.getValue())
		packet.writeByte(this.hardShadows!!.getValue())
		packet.writeByte(this.orthographic!!.getValue())
		packet.writeByte(this.particles!!.getValue())
		packet.writeByte(this.removeRoofs!!.getValue())
		packet.writeByte(this.maxScreenSize!!.getValue())
		packet.writeByte(this.characterShadows!!.getValue())
		packet.writeByte(this.smallTextures!!.getValue())
		packet.writeByte(this.textures!!.getValue())
		packet.writeByte(this.toolkitDefault!!.getValue())
		packet.writeByte(this.animateBackground!!.getValue())
		packet.writeByte(this.water!!.getValue())
		packet.writeByte(this.fixedScreen!!.getValue())
		packet.writeByte(this.customCursor!!.getValue())
		packet.writeByte(this.graphics!!.getValue())
		packet.writeByte(this.cpu!!.getValue())
		packet.writeByte(this.gametipStartStagePreference!!.getValue())
		packet.writeByte(this.startupSafeMode!!.getValue())
		packet.writeByte(this.soundEffectVolume!!.getValue())
		packet.writeByte(this.ambientSoundVolume!!.getValue())
		packet.writeByte(this.voiceOverVolume!!.getValue())
		packet.writeByte(this.ingameMusicVolume!!.getValue())
		packet.writeByte(this.musicVolume!!.getValue())
		packet.writeByte(this.monoStereo!!.getValue())
		return packet
	}

	@OriginalMember(owner = "client!dh", name = "b", descriptor = "(B)Lclient!rc;")
	fun getProcessorSpecs(): EnvironmentContext {
		return this.processorSpecs
	}

	@OriginalMember(owner = "client!dh", name = "a", descriptor = "(BLclient!su;I)V")
	fun setValue(@OriginalArg(1) option: Preference?, @OriginalArg(2) value: Int) {
		option!!.setSafeValue(value)
		this.ensureValid()
	}

	companion object {
		@OriginalMember(owner = "client!le", name = "a", descriptor = "(I)V")
		@JvmStatic
		fun savePreferences() {
			@Pc(7) var fileOnDisk: JFS32? = null
			try {
				@Pc(13) val signedResource: SignedResource = GameShell.signLink!!.openCacheFile("")
				while (signedResource.status == SignedResourceStatus.IDLE) {
					TimeUtils.sleep(1L)
				}
				if (signedResource.status == SignedResourceStatus.SUCCESS) {
					fileOnDisk = signedResource.result as JFS32?
					@Pc(35) val packet: Packet = GamePreferences.uid!!.encode()
					fileOnDisk!!.write(0, packet.currentPosition, packet.buffer)
				}
			} catch (@Pc(45) ex: Exception) {
			}
			try {
				if (fileOnDisk != null) {
					fileOnDisk.close()
				}
			} catch (@Pc(57) ex2: Exception) {
			}
		}

		@OriginalMember(owner = "client!qba", name = "a", descriptor = "(I)Lclient!dh;")
		@JvmStatic
		fun loadPreferences(): GamePreferences {
			@Pc(7) var fileOnDisk: JFS32? = null
			@Pc(13) var clientOptions: GamePreferences = GamePreferences(Game.CURRENT_GAME, 0)
			try {
				@Pc(19) val signedResource: SignedResource = GameShell.signLink!!.openCacheFile("")
				while (signedResource.status == SignedResourceStatus.IDLE) {
					TimeUtils.sleep(1L)
				}
				if (signedResource.status == SignedResourceStatus.SUCCESS) {
					fileOnDisk = signedResource.result as JFS32?
					@Pc(43) val data: ByteArray = ByteArray(fileOnDisk!!.length().toInt())
					@Pc(58) var bytesRead: Int
					@Pc(45) var i: Int = 0
					while (i < data.size) {
						bytesRead = fileOnDisk.read(data, i, data.size - i)
						if (bytesRead == -1) {
							throw IOException("EOF")
						}
						i += bytesRead
					}
					clientOptions = GamePreferences(Packet(data), Game.CURRENT_GAME, 0)
				}
			} catch (@Pc(87) ex: Exception) {
			}
			try {
				if (fileOnDisk != null) {
					fileOnDisk.close()
				}
			} catch (@Pc(99) ex2: Exception) {
			}
			return clientOptions
		}

		@OriginalMember(owner = "client!mr", name = "c", descriptor = "(I)I")
		@JvmStatic
		fun getEncodedSize(): Int {
			// Not part of the original client: without reflection (the browser, PlatformLoop.kt) the fields
			// cannot be counted, and a 1-byte buffer made every save throw; there the size is a bound on
			// what encode writes (a version byte and one byte a preference).
			if (!classFieldsAvailable) {
				return 64
			}
			@Pc(7) var count: Int = 0
			@Pc(25) val fields: Array<Field> = GamePreferences::class.jClass.getDeclaredFields()
			@Pc(29) var i: Int = 0
			while (i < fields.size) {
				@Pc(35) val field: Field = fields[i]
				if (Preference::class.jClass.isAssignableFrom(field.getType())) {
					count++
				}
				i++
			}
			return count + 1
		}

		@OriginalMember(owner = "client!wj", name = "a", descriptor = "(ZB)Z")
		@JvmStatic
		fun setBloom(@OriginalArg(0) enabled: Boolean): Boolean {
			@Suppress("NAME_SHADOWING") var enabled: Boolean = enabled
			@Pc(6) val currentlyEnabled: Boolean = ClientState.activeToolkit!!.bloom()
			if (currentlyEnabled == enabled) {
				return true
			}
			if (!enabled) {
				ClientState.activeToolkit!!.stopBloom()
			} else if (!ClientState.activeToolkit!!.hasBloom()) {
				enabled = false
			}
			if (currentlyEnabled == enabled) {
				return false
			} else {
				GamePreferences.uid!!.setValue(GamePreferences.uid!!.bloom, if (enabled) 1 else 0)
				GamePreferences.savePreferences()
				return true
			}
		}

		@OriginalMember(owner = "client!sr", name = "a", descriptor = "(I)V")
		@JvmStatic
		fun applyDefaultCpuUsage() {
			if (GameShell.availableProcessors > 1) {
				GamePreferences.uid!!.setValue(GamePreferences.uid!!.cpu, 4)
			} else {
				GamePreferences.uid!!.setValue(GamePreferences.uid!!.cpu, 2)
			}
		}

		@OriginalMember(owner = "client!r", name = "z", descriptor = "Z")
		@JvmField var preferencesSavedOnServer: Boolean = true

		@OriginalMember(owner = "client!cj", name = "b", descriptor = "Lclient!dh;")
		@JvmField var uid: GamePreferences? = null
	}
}
