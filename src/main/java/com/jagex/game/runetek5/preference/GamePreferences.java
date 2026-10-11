package com.jagex.game.runetek5.preference;

import com.jagex.game.runetek5.preference.AnimateBackgroundOption;
import com.jagex.game.runetek5.preference.impl.AntiAliasingPreference;
import com.jagex.game.runetek5.preference.BloomOption;
import com.jagex.game.runetek5.preference.impl.BrightnessOption;
import com.jagex.game.runetek5.preference.impl.MapSizePreference;
import com.jagex.game.runetek5.preference.MonoStereoPreference;
import com.jagex.game.runetek5.preference.StartupSafeModePreference;
import com.jagex.game.runetek5.preference.impl.FlickeringEffectPreference;
import com.jagex.game.runetek5.preference.CharacterShadowPreference;
import com.jagex.game.runetek5.preference.CustomCursorsPreference;
import com.jagex.game.runetek5.preference.CpuUsageOption;
import com.jagex.game.runetek5.EnvironmentContext;
import com.jagex.game.runetek5.preference.impl.FogOption;
import com.jagex.game.runetek5.preference.GraphicsQuality;
import com.jagex.game.runetek5.preference.impl.GroundBlendingOption;
import com.jagex.game.runetek5.preference.GroundDecorOption;
import com.jagex.game.runetek5.preference.impl.HardShadowsOption;
import com.jagex.game.runetek5.preference.impl.IdleAnimationsOption;
import com.jagex.game.runetek5.preference.LoadingSequenceOption;
import com.jagex.game.runetek5.preference.impl.MaxScreenSizeOption;
import com.jagex.game.runetek5.preference.OrthographicOption;
import com.jagex.game.runetek5.preference.impl.ParticlesOption;
import com.jagex.game.runetek5.preference.RemoveRoofsOption;
import com.jagex.game.runetek5.preference.impl.LightDetailPreference;
import com.jagex.game.runetek5.preference.impl.ScreenSizeOption;
import com.jagex.game.runetek5.preference.SmallTexturesOption;
import com.jagex.game.runetek5.preference.TextureQuality;
import com.jagex.game.runetek5.preference.VolumeOption;
import com.jagex.game.runetek5.preference.WaterDetailOption;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.cache.JFS32;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.preference.Preference;
import com.jagex.game.runetek5.preference.impl.ToolkitOption;
import com.jagex.game.runetek5.graphics.ToolkitType;
import com.jagex.game.runetek5.sign.SignedResource;
import com.jagex.game.runetek5.sign.SignedResourceStatus;
import com.jagex.game.runetek5.sign.SignLinkEnvironment;
import java.lang.reflect.Field;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.systeminfo.SystemInfo;
import com.jagex.game.runetek5.client.GameShell;
import java.io.IOException;
import com.jagex.game.runetek5.core.util.TimeUtils;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!dh")
public final class GamePreferences extends Node {

	@OriginalMember(owner = "client!dh", name = "o", descriptor = "Lclient!fca;")
	public VolumeOption voiceOverVolume;

	@OriginalMember(owner = "client!dh", name = "p", descriptor = "Lclient!vca;")
	public AntiAliasingPreference antiAliasing;

	@OriginalMember(owner = "client!dh", name = "t", descriptor = "Lclient!kba;")
	public GroundDecorOption groundDecor;

	@OriginalMember(owner = "client!dh", name = "v", descriptor = "Lclient!jaa;")
	public OrthographicOption orthographic;

	@OriginalMember(owner = "client!dh", name = "x", descriptor = "Lclient!pr;")
	public ParticlesOption particles;

	@OriginalMember(owner = "client!dh", name = "z", descriptor = "Lclient!kia;")
	public MapSizePreference buildArea;

	@OriginalMember(owner = "client!dh", name = "A", descriptor = "Lclient!mfa;")
	public ScreenSizeOption fixedScreen;

	@OriginalMember(owner = "client!dh", name = "B", descriptor = "Lclient!gga;")
	public SmallTexturesOption smallTextures;

	@OriginalMember(owner = "client!dh", name = "C", descriptor = "Lclient!fv;")
	public GraphicsQuality graphics;

	@OriginalMember(owner = "client!dh", name = "D", descriptor = "Lclient!fs;")
	public LightDetailPreference lightDetail;

	@OriginalMember(owner = "client!dh", name = "E", descriptor = "Lclient!bl;")
	public MaxScreenSizeOption maxScreenSize;

	@OriginalMember(owner = "client!dh", name = "F", descriptor = "Lclient!od;")
	public BrightnessOption brightness;

	@OriginalMember(owner = "client!dh", name = "H", descriptor = "Lclient!fca;")
	public VolumeOption ambientSoundVolume;

	@OriginalMember(owner = "client!dh", name = "I", descriptor = "Lclient!dba;")
	public TextureQuality textures;

	@OriginalMember(owner = "client!dh", name = "J", descriptor = "Lclient!ph;")
	public IdleAnimationsOption idleAnimations;

	@OriginalMember(owner = "client!dh", name = "L", descriptor = "Lclient!mca;")
	public HardShadowsOption hardShadows;

	@OriginalMember(owner = "client!dh", name = "N", descriptor = "Lclient!kfa;")
	public MonoStereoPreference monoStereo;

	@OriginalMember(owner = "client!dh", name = "Q", descriptor = "Lclient!av;")
	public BloomOption bloom;

	@OriginalMember(owner = "client!dh", name = "S", descriptor = "Lclient!fca;")
	public VolumeOption musicVolume;

	@OriginalMember(owner = "client!dh", name = "T", descriptor = "Lclient!aj;")
	public FlickeringEffectPreference flickeringEffects;

	@OriginalMember(owner = "client!dh", name = "U", descriptor = "Lclient!sba;")
	public FogOption fog;

	@OriginalMember(owner = "client!dh", name = "V", descriptor = "Lclient!vca;")
	public AntiAliasingPreference antiAliasingPreview;

	@OriginalMember(owner = "client!dh", name = "X", descriptor = "Lclient!pp;")
	public RemoveRoofsOption removeRoofs;

	@OriginalMember(owner = "client!dh", name = "Y", descriptor = "Lclient!eea;")
	public ToolkitOption toolkitDefault;

	@OriginalMember(owner = "client!dh", name = "ab", descriptor = "Lclient!wca;")
	public CustomCursorsPreference customCursor;

	@OriginalMember(owner = "client!dh", name = "bb", descriptor = "Lclient!mfa;")
	public ScreenSizeOption fullScreen;

	@OriginalMember(owner = "client!dh", name = "cb", descriptor = "Lclient!fo;")
	public AnimateBackgroundOption animateBackground;

	@OriginalMember(owner = "client!dh", name = "db", descriptor = "Lclient!mm;")
	public GroundBlendingOption groundBlending;

	@OriginalMember(owner = "client!dh", name = "eb", descriptor = "Lclient!tt;")
	public LoadingSequenceOption gametipStartStagePreference;

	@OriginalMember(owner = "client!dh", name = "gb", descriptor = "Lclient!tl;")
	public WaterDetailOption water;

	@OriginalMember(owner = "client!dh", name = "hb", descriptor = "Lclient!fca;")
	public VolumeOption ingameMusicVolume;

	@OriginalMember(owner = "client!dh", name = "ib", descriptor = "Lclient!fo;")
	public AnimateBackgroundOption animateBackgroundOverride;

	@OriginalMember(owner = "client!dh", name = "kb", descriptor = "Lclient!ti;")
	public CpuUsageOption cpu;

	@OriginalMember(owner = "client!dh", name = "lb", descriptor = "Lclient!pp;")
	public RemoveRoofsOption lb;

	@OriginalMember(owner = "client!dh", name = "mb", descriptor = "Lclient!pb;")
	public StartupSafeModePreference startupSafeMode;

	@OriginalMember(owner = "client!dh", name = "rb", descriptor = "Lclient!vu;")
	public CharacterShadowPreference characterShadows;

	@OriginalMember(owner = "client!dh", name = "tb", descriptor = "Lclient!fca;")
	public VolumeOption soundEffectVolume;

	@OriginalMember(owner = "client!dh", name = "qb", descriptor = "Lclient!sj;")
	private final Game game;

	@OriginalMember(owner = "client!dh", name = "u", descriptor = "Lclient!rc;")
	private final EnvironmentContext processorSpecs;

	@OriginalMember(owner = "client!dh", name = "sb", descriptor = "Lclient!eea;")
	public ToolkitOption toolkit;

	@OriginalMember(owner = "client!dh", name = "<init>", descriptor = "(Lclient!sj;I)V")
	public GamePreferences(@OriginalArg(0) Game modeGame, @OriginalArg(1) int value) {
		this.game = modeGame;
		this.processorSpecs = new EnvironmentContext(GameShell.signLink.signed, GameShell.maxMemory, GameShell.availableProcessors, SystemInfo.OS_ARCH.toLowerCase().indexOf("arm") != -1);
		this.toolkit = new ToolkitOption(value, this);
		this.setDefaults(true);
	}

	@OriginalMember(owner = "client!dh", name = "<init>", descriptor = "(Lclient!cea;Lclient!sj;I)V")
	public GamePreferences(@OriginalArg(0) Packet packet, @OriginalArg(1) Game modeGame, @OriginalArg(2) int value) {
		this.game = modeGame;
		this.processorSpecs = new EnvironmentContext(GameShell.signLink.signed, GameShell.maxMemory, GameShell.availableProcessors, SystemInfo.OS_ARCH.indexOf("arm") != -1);
		this.toolkit = new ToolkitOption(value, this);
		this.decode(packet);
	}

	@OriginalMember(owner = "client!dh", name = "a", descriptor = "(ILclient!cea;I)V")
	private void decodeDeprecatedVersion(@OriginalArg(1) Packet packet, @OriginalArg(2) int version) {
		this.brightness = new BrightnessOption(packet.readUByte(), this);
		this.animateBackground = new AnimateBackgroundOption(packet.readUByte(), this);
		this.removeRoofs = new RemoveRoofsOption(packet.readUByte() + 1, this);
		this.groundDecor = new GroundDecorOption(packet.readUByte(), this);
		this.smallTextures = new SmallTexturesOption(packet.readUByte(), this);
		this.idleAnimations = new IdleAnimationsOption(packet.readUByte(), this);
		this.flickeringEffects = new FlickeringEffectPreference(packet.readUByte(), this);
		packet.readUByte();
		this.characterShadows = new CharacterShadowPreference(packet.readUByte(), this);
		@Pc(95) int shadowValue = packet.readUByte();
		@Pc(97) int shadowValueV17 = 0;
		if (version >= 17) {
			shadowValueV17 = packet.readUByte();
		}
		this.hardShadows = new HardShadowsOption(shadowValueV17 < shadowValue ? shadowValue : shadowValueV17, this);
		@Pc(126) boolean highDetailShadows = true;
		@Pc(142) boolean highDetailLight;
		if (version >= 2) {
			highDetailLight = packet.readUByte() == 1;
			if (version >= 17) {
				highDetailShadows = packet.readUByte() == 1;
			}
		} else {
			highDetailLight = packet.readUByte() == 1;
			packet.readUByte();
		}
		this.lightDetail = new LightDetailPreference(highDetailLight | highDetailShadows ? 1 : 0, this);
		this.water = new WaterDetailOption(packet.readUByte(), this);
		this.fog = new FogOption(packet.readUByte(), this);
		this.antiAliasing = new AntiAliasingPreference(packet.readUByte(), this);
		this.monoStereo = new MonoStereoPreference(packet.readUByte(), this);
		this.soundEffectVolume = new VolumeOption(packet.readUByte(), this);
		if (version >= 20) {
			this.voiceOverVolume = new VolumeOption(packet.readUByte(), this);
		} else {
			this.voiceOverVolume = new VolumeOption(this.soundEffectVolume.getValue(), this);
		}
		this.ingameMusicVolume = new VolumeOption(packet.readUByte(), this);
		this.ambientSoundVolume = new VolumeOption(packet.readUByte(), this);
		if (version >= 21) {
			this.musicVolume = new VolumeOption(packet.readUByte(), this);
		} else {
			this.musicVolume = new VolumeOption(this.ingameMusicVolume.getValue(), this);
		}
		if (version >= 1) {
			packet.readUShort();
			packet.readUShort();
		}
		if (version >= 3 && version < 6) {
			packet.readUByte();
		}
		if (version >= 4) {
			this.particles = new ParticlesOption(packet.readUByte(), this);
		}
		packet.readInt();
		if (version >= 6) {
			this.fixedScreen = new ScreenSizeOption(packet.readUByte(), this);
		}
		if (version >= 7) {
			this.startupSafeMode = new StartupSafeModePreference(packet.readUByte(), this);
		}
		if (version >= 8) {
			packet.readUByte();
		}
		if (version >= 9) {
			this.buildArea = new MapSizePreference(packet.readUByte(), this);
		}
		if (version >= 10) {
			this.bloom = new BloomOption(packet.readUByte(), this);
		}
		if (version >= 11) {
			this.customCursor = new CustomCursorsPreference(packet.readUByte(), this);
		}
		if (version >= 12) {
			this.idleAnimations = new IdleAnimationsOption(packet.readUByte(), this);
		}
		if (version >= 13) {
			this.groundBlending = new GroundBlendingOption(packet.readUByte(), this);
		}
		if (version >= 14) {
			this.toolkitDefault = new ToolkitOption(packet.readUByte(), this);
		}
		if (version >= 15) {
			this.cpu = new CpuUsageOption(packet.readUByte(), this);
		}
		if (version >= 16) {
			this.textures = new TextureQuality(packet.readUByte(), this);
		}
		if (version >= 18) {
			this.graphics = new GraphicsQuality(packet.readUByte(), this);
		}
		if (version >= 19) {
			this.maxScreenSize = new MaxScreenSizeOption(packet.readUByte(), this);
		}
		if (version >= 22) {
			this.gametipStartStagePreference = new LoadingSequenceOption(packet.readUByte(), this);
		}
	}

	@OriginalMember(owner = "client!dh", name = "a", descriptor = "(I)Lclient!sj;")
	public Game getGame() {
		return this.game;
	}

	@OriginalMember(owner = "client!dh", name = "a", descriptor = "(ZI)V")
	private void setDefaults(@OriginalArg(0) boolean override) {
		if (override || this.antiAliasing == null) {
			this.antiAliasing = new AntiAliasingPreference(this);
		}
		if (override || this.antiAliasingPreview == null) {
			this.antiAliasingPreview = new AntiAliasingPreference(this.antiAliasing.getValue(), this);
		}
		if (override || this.bloom == null) {
			this.bloom = new BloomOption(this);
		}
		if (override || this.brightness == null) {
			this.brightness = new BrightnessOption(this);
		}
		if (override || this.buildArea == null) {
			this.buildArea = new MapSizePreference(this);
		}
		if (override || this.flickeringEffects == null) {
			this.flickeringEffects = new FlickeringEffectPreference(this);
		}
		if (override || this.fog == null) {
			this.fog = new FogOption(this);
		}
		if (override || this.groundBlending == null) {
			this.groundBlending = new GroundBlendingOption(this);
		}
		if (override || this.groundDecor == null) {
			this.groundDecor = new GroundDecorOption(this);
		}
		if (override || this.idleAnimations == null) {
			this.idleAnimations = new IdleAnimationsOption(this);
		}
		if (override || this.lightDetail == null) {
			this.lightDetail = new LightDetailPreference(this);
		}
		if (override || this.hardShadows == null) {
			this.hardShadows = new HardShadowsOption(this);
		}
		if (override || this.orthographic == null) {
			this.orthographic = new OrthographicOption(this);
		}
		if (override || this.particles == null) {
			this.particles = new ParticlesOption(this);
		}
		if (override || this.removeRoofs == null) {
			this.removeRoofs = new RemoveRoofsOption(this);
		}
		if (override || this.lb == null) {
			this.lb = new RemoveRoofsOption(this.removeRoofs.getValue(), this);
		}
		if (override || this.maxScreenSize == null) {
			this.maxScreenSize = new MaxScreenSizeOption(this);
		}
		if (override || this.characterShadows == null) {
			this.characterShadows = new CharacterShadowPreference(this);
		}
		if (override || this.smallTextures == null) {
			this.smallTextures = new SmallTexturesOption(this);
		}
		if (override || this.textures == null) {
			this.textures = new TextureQuality(this);
		}
		if (override || this.toolkitDefault == null) {
			this.toolkitDefault = new ToolkitOption(this);
		}
		if (override || this.toolkit == null) {
			this.toolkit = new ToolkitOption(this.toolkitDefault.getValue(), this);
		}
		if (override || this.animateBackground == null) {
			this.animateBackground = new AnimateBackgroundOption(this);
		}
		if (override || this.animateBackgroundOverride == null) {
			this.animateBackgroundOverride = new AnimateBackgroundOption(this.animateBackground.getValue(), this);
		}
		if (override || this.water == null) {
			this.water = new WaterDetailOption(this);
		}
		if (override || this.fixedScreen == null) {
			this.fixedScreen = new ScreenSizeOption(this);
		}
		if (override || this.fullScreen == null) {
			this.fullScreen = new ScreenSizeOption(this.fixedScreen.getValue(), this);
		}
		if (override || this.customCursor == null) {
			this.customCursor = new CustomCursorsPreference(this);
		}
		if (override || this.graphics == null) {
			this.graphics = new GraphicsQuality(this);
		}
		if (override || this.cpu == null) {
			this.cpu = new CpuUsageOption(this);
		}
		if (override || this.gametipStartStagePreference == null) {
			this.gametipStartStagePreference = new LoadingSequenceOption(this);
		}
		if (override || this.startupSafeMode == null) {
			this.startupSafeMode = new StartupSafeModePreference(this);
		}
		if (override || this.soundEffectVolume == null) {
			this.soundEffectVolume = new VolumeOption(this);
		}
		if (override || this.ambientSoundVolume == null) {
			this.ambientSoundVolume = new VolumeOption(this);
		}
		if (override || this.voiceOverVolume == null) {
			this.voiceOverVolume = new VolumeOption(this);
		}
		if (override || this.ingameMusicVolume == null) {
			this.ingameMusicVolume = new VolumeOption(this);
		}
		if (override || this.musicVolume == null) {
			this.musicVolume = new VolumeOption(this);
		}
		if (override || this.monoStereo == null) {
			this.monoStereo = new MonoStereoPreference(this);
		}
	}

	@OriginalMember(owner = "client!dh", name = "a", descriptor = "(ILclient!cea;)V")
	private void decode(@OriginalArg(1) Packet packet) {
		if (packet == null || packet.buffer == null) {
			this.setDefaults(true);
		} else {
			@Pc(20) int version = packet.readUByte();
			if (version < 23) {
				try {
					this.decodeDeprecatedVersion(packet, version);
				} catch (@Pc(417) Exception ex) {
					this.setDefaults(true);
				}
				this.setDefaults(false);
			} else if (version > 24) {
				this.setDefaults(true);
			} else {
				this.antiAliasing = new AntiAliasingPreference(packet.readUByte(), this);
				this.antiAliasingPreview = new AntiAliasingPreference(this.antiAliasing.getValue(), this);
				this.bloom = new BloomOption(packet.readUByte(), this);
				this.brightness = new BrightnessOption(packet.readUByte(), this);
				this.buildArea = new MapSizePreference(packet.readUByte(), this);
				this.flickeringEffects = new FlickeringEffectPreference(packet.readUByte(), this);
				this.fog = new FogOption(packet.readUByte(), this);
				this.groundBlending = new GroundBlendingOption(packet.readUByte(), this);
				this.groundDecor = new GroundDecorOption(packet.readUByte(), this);
				this.idleAnimations = new IdleAnimationsOption(packet.readUByte(), this);
				this.lightDetail = new LightDetailPreference(packet.readUByte(), this);
				this.hardShadows = new HardShadowsOption(packet.readUByte(), this);
				if (version >= 24) {
					this.orthographic = new OrthographicOption(packet.readUByte(), this);
				}
				this.particles = new ParticlesOption(packet.readUByte(), this);
				this.removeRoofs = new RemoveRoofsOption(packet.readUByte(), this);
				this.lb = new RemoveRoofsOption(this.removeRoofs.getValue(), this);
				this.maxScreenSize = new MaxScreenSizeOption(packet.readUByte(), this);
				this.characterShadows = new CharacterShadowPreference(packet.readUByte(), this);
				this.smallTextures = new SmallTexturesOption(packet.readUByte(), this);
				this.textures = new TextureQuality(packet.readUByte(), this);
				this.toolkitDefault = new ToolkitOption(packet.readUByte(), this);
				this.toolkit = new ToolkitOption(this.toolkitDefault.getValue(), this);
				this.animateBackground = new AnimateBackgroundOption(packet.readUByte(), this);
				this.animateBackgroundOverride = new AnimateBackgroundOption(this.animateBackground.getValue(), this);
				this.water = new WaterDetailOption(packet.readUByte(), this);
				this.fixedScreen = new ScreenSizeOption(packet.readUByte(), this);
				this.fullScreen = new ScreenSizeOption(this.fixedScreen.getValue(), this);
				this.customCursor = new CustomCursorsPreference(packet.readUByte(), this);
				this.graphics = new GraphicsQuality(packet.readUByte(), this);
				this.cpu = new CpuUsageOption(packet.readUByte(), this);
				this.gametipStartStagePreference = new LoadingSequenceOption(packet.readUByte(), this);
				this.startupSafeMode = new StartupSafeModePreference(packet.readUByte(), this);
				this.soundEffectVolume = new VolumeOption(packet.readUByte(), this);
				this.ambientSoundVolume = new VolumeOption(packet.readUByte(), this);
				this.voiceOverVolume = new VolumeOption(packet.readUByte(), this);
				this.ingameMusicVolume = new VolumeOption(packet.readUByte(), this);
				this.musicVolume = new VolumeOption(packet.readUByte(), this);
				this.monoStereo = new MonoStereoPreference(packet.readUByte(), this);
				this.setDefaults(false);
			}
		}
		this.ensureValid();
	}

	@OriginalMember(owner = "client!dh", name = "b", descriptor = "(I)Z")
	public boolean isLowMemoryJava() {
		return this.toolkit.isActive() && this.toolkit.getValue() == ToolkitType.JAVA && this.processorSpecs.getMaxMemory() < 96;
	}

	@OriginalMember(owner = "client!dh", name = "a", descriptor = "(B)V")
	private void ensureValid() {
		try {
			@Pc(9) Field[] fields = this.getClass().getDeclaredFields();
			for (@Pc(19) int i = 0; i < fields.length; i++) {
				@Pc(25) Field field = fields[i];
				if (Preference.class.isAssignableFrom(field.getType())) {
					@Pc(42) Preference option = (Preference) field.get(this);
					option.validate();
				}
			}
		} catch (@Pc(56) IllegalAccessException ex) {
		}
	}

	@OriginalMember(owner = "client!dh", name = "d", descriptor = "(I)Lclient!cea;")
	public Packet encode() {
		@Pc(11) Packet packet = new Packet(GamePreferences.getEncodedSize());
		packet.writeByte(24);
		packet.writeByte(this.antiAliasing.getValue());
		packet.writeByte(this.bloom.getValue());
		packet.writeByte(this.brightness.getValue());
		packet.writeByte(this.buildArea.getValue());
		packet.writeByte(this.flickeringEffects.getValue());
		packet.writeByte(this.fog.getValue());
		packet.writeByte(this.groundBlending.getValue());
		packet.writeByte(this.groundDecor.getValue());
		packet.writeByte(this.idleAnimations.getValue());
		packet.writeByte(this.lightDetail.getValue());
		packet.writeByte(this.hardShadows.getValue());
		packet.writeByte(this.orthographic.getValue());
		packet.writeByte(this.particles.getValue());
		packet.writeByte(this.removeRoofs.getValue());
		packet.writeByte(this.maxScreenSize.getValue());
		packet.writeByte(this.characterShadows.getValue());
		packet.writeByte(this.smallTextures.getValue());
		packet.writeByte(this.textures.getValue());
		packet.writeByte(this.toolkitDefault.getValue());
		packet.writeByte(this.animateBackground.getValue());
		packet.writeByte(this.water.getValue());
		packet.writeByte(this.fixedScreen.getValue());
		packet.writeByte(this.customCursor.getValue());
		packet.writeByte(this.graphics.getValue());
		packet.writeByte(this.cpu.getValue());
		packet.writeByte(this.gametipStartStagePreference.getValue());
		packet.writeByte(this.startupSafeMode.getValue());
		packet.writeByte(this.soundEffectVolume.getValue());
		packet.writeByte(this.ambientSoundVolume.getValue());
		packet.writeByte(this.voiceOverVolume.getValue());
		packet.writeByte(this.ingameMusicVolume.getValue());
		packet.writeByte(this.musicVolume.getValue());
		packet.writeByte(this.monoStereo.getValue());
		return packet;
	}

	@OriginalMember(owner = "client!dh", name = "b", descriptor = "(B)Lclient!rc;")
	public EnvironmentContext getProcessorSpecs() {
		return this.processorSpecs;
	}

	@OriginalMember(owner = "client!dh", name = "a", descriptor = "(BLclient!su;I)V")
	public void setValue(@OriginalArg(1) Preference option, @OriginalArg(2) int value) {
		option.setSafeValue(value);
		this.ensureValid();
	}

	@OriginalMember(owner = "client!le", name = "a", descriptor = "(I)V")
	public static void savePreferences() {
		@Pc(7) JFS32 fileOnDisk = null;
		try {
			@Pc(13) SignedResource signedResource = GameShell.signLink.openCacheFile("");
			while (signedResource.status == SignedResourceStatus.IDLE) {
				TimeUtils.sleep(1L);
			}
			if (signedResource.status == SignedResourceStatus.SUCCESS) {
				fileOnDisk = (JFS32) signedResource.result;
				@Pc(35) Packet packet = GamePreferences.uid.encode();
				fileOnDisk.write(0, packet.currentPosition, packet.buffer);
			}
		} catch (@Pc(45) Exception ex) {
		}
		try {
			if (fileOnDisk != null) {
				fileOnDisk.close();
			}
		} catch (@Pc(57) Exception ex2) {
		}
	}
	@OriginalMember(owner = "client!qba", name = "a", descriptor = "(I)Lclient!dh;")
	public static GamePreferences loadPreferences() {
		@Pc(7) JFS32 fileOnDisk = null;
		@Pc(13) GamePreferences clientOptions = new GamePreferences(Game.CURRENT_GAME, 0);
		try {
			@Pc(19) SignedResource signedResource = GameShell.signLink.openCacheFile("");
			while (signedResource.status == SignedResourceStatus.IDLE) {
				TimeUtils.sleep(1L);
			}
			if (signedResource.status == SignedResourceStatus.SUCCESS) {
				fileOnDisk = (JFS32) signedResource.result;
				@Pc(43) byte[] data = new byte[(int) fileOnDisk.length()];
				@Pc(58) int bytesRead;
				for (@Pc(45) int i = 0; i < data.length; i += bytesRead) {
					bytesRead = fileOnDisk.read(data, i, data.length - i);
					if (bytesRead == -1) {
						throw new IOException("EOF");
					}
				}
				clientOptions = new GamePreferences(new Packet(data), Game.CURRENT_GAME, 0);
			}
		} catch (@Pc(87) Exception ex) {
		}
		try {
			if (fileOnDisk != null) {
				fileOnDisk.close();
			}
		} catch (@Pc(99) Exception ex2) {
		}
		return clientOptions;
	}
	@OriginalMember(owner = "client!mr", name = "c", descriptor = "(I)I")
	public static int getEncodedSize() {
		@Pc(7) int count = 0;
		@Pc(25) Field[] fields = GamePreferences.class.getDeclaredFields();
		for (@Pc(29) int i = 0; i < fields.length; i++) {
			@Pc(35) Field field = fields[i];
			if (Preference.class.isAssignableFrom(field.getType())) {
				count++;
			}
		}
		return count + 1;
	}
	@OriginalMember(owner = "client!wj", name = "a", descriptor = "(ZB)Z")
	public static boolean setBloom(@OriginalArg(0) boolean enabled) {
		@Pc(6) boolean currentlyEnabled = ClientState.activeToolkit.bloom();
		if (currentlyEnabled == enabled) {
			return true;
		}
		if (!enabled) {
			ClientState.activeToolkit.stopBloom();
		} else if (!ClientState.activeToolkit.hasBloom()) {
			enabled = false;
		}
		if (currentlyEnabled == enabled) {
			return false;
		} else {
			GamePreferences.uid.setValue(GamePreferences.uid.bloom, enabled ? 1 : 0);
			GamePreferences.savePreferences();
			return true;
		}
	}

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(I)V")
	public static void applyDefaultCpuUsage() {
		if (GameShell.availableProcessors > 1) {
			GamePreferences.uid.setValue(GamePreferences.uid.cpu, 4);
		} else {
			GamePreferences.uid.setValue(GamePreferences.uid.cpu, 2);
		}
	}

	@OriginalMember(owner = "client!r", name = "z", descriptor = "Z")
	public static boolean preferencesSavedOnServer = true;

	@OriginalMember(owner = "client!cj", name = "b", descriptor = "Lclient!dh;")
	public static GamePreferences uid;
}
