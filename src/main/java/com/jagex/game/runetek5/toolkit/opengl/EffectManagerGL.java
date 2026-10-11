package com.jagex.game.runetek5.toolkit.opengl;

import com.jagex.game.runetek5.toolkit.opengl.effect.LappingWaterEffect;
import com.jagex.game.runetek5.toolkit.opengl.effect.RipplingWaterEffect;
import com.jagex.game.runetek5.toolkit.opengl.effect.WaterfallEffect;
import com.jagex.game.runetek5.toolkit.opengl.effect.CubeMapSpecularEffect;
import com.jagex.game.runetek5.toolkit.opengl.effect.ReflectingTextureEffect;
import com.jagex.game.runetek5.toolkit.opengl.effect.ReflectiveRipplingWaterEffect;
import com.jagex.game.runetek5.toolkit.opengl.effect.BreakingWaterEffect;
import com.jagex.game.runetek5.toolkit.opengl.effect.MaterialEffectGL;
import com.jagex.game.runetek5.toolkit.opengl.effect.UnlitEffect;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.texture.water.TextureContainerGL;
import com.jagex.game.runetek5.toolkit.opengl.effect.StillWaterEffect;
import com.jagex.game.runetek5.graphics.gl.GlTexture;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;

@OriginalClass("client!mea")
public final class EffectManagerGL {

	@OriginalMember(owner = "client!mea", name = "h", descriptor = "I")
	private int param2 = 0;

	@OriginalMember(owner = "client!mea", name = "b", descriptor = "I")
	private int activeEffect = 0;

	@OriginalMember(owner = "client!mea", name = "p", descriptor = "I")
	private int param1 = 0;

	@OriginalMember(owner = "client!mea", name = "e", descriptor = "Lclient!qo;")
	private final GlToolkit openglToolkit;

	@OriginalMember(owner = "client!mea", name = "f", descriptor = "Lclient!lc;")
	private final TextureContainerGL textureContainer;

	@OriginalMember(owner = "client!mea", name = "l", descriptor = "[Lclient!vk;")
	private final MaterialEffectGL[] materialEffects;

	@OriginalMember(owner = "client!mea", name = "j", descriptor = "Lclient!an;")
	public final StillWaterEffect waterEffect;

	@OriginalMember(owner = "client!mea", name = "<init>", descriptor = "(Lclient!qo;)V")
	public EffectManagerGL(@OriginalArg(0) GlToolkit glToolkit) {
		this.openglToolkit = glToolkit;
		this.textureContainer = new TextureContainerGL(glToolkit);
		this.materialEffects = new MaterialEffectGL[10];
		this.materialEffects[1] = new CubeMapSpecularEffect(glToolkit);
		this.materialEffects[2] = new LappingWaterEffect(glToolkit, this.textureContainer);
		this.materialEffects[4] = new RipplingWaterEffect(glToolkit, this.textureContainer);
		this.materialEffects[5] = new WaterfallEffect(glToolkit, this.textureContainer);
		this.materialEffects[6] = new UnlitEffect(glToolkit);
		this.materialEffects[7] = new ReflectingTextureEffect(glToolkit);
		this.materialEffects[3] = this.waterEffect = new StillWaterEffect(glToolkit);
		this.materialEffects[8] = new ReflectiveRipplingWaterEffect(glToolkit, this.textureContainer);
		this.materialEffects[9] = new BreakingWaterEffect(glToolkit, this.textureContainer);
		if (!this.materialEffects[8].isSupported()) {
			this.materialEffects[8] = this.materialEffects[4];
		}
		if (!this.materialEffects[9].isSupported()) {
			this.materialEffects[9] = this.materialEffects[8];
		}
	}

	@OriginalMember(owner = "client!mea", name = "a", descriptor = "(IZIIBZ)V")
	public void loadEffect(@OriginalArg(0) int effectParam1, @OriginalArg(1) boolean hasLight, @OriginalArg(2) int effect, @OriginalArg(3) int effectParam2, @OriginalArg(5) boolean allowStillWater) {
		@Pc(9) boolean useStillWater = allowStillWater & this.openglToolkit.stillwaterEffectLoaded();
		if (!useStillWater && (effect == 4 || effect == 8 || effect == 9)) {
			if (effect == 4) {
				effectParam2 = effectParam1;
			}
			effect = 2;
		}
		if (effect != 0 && hasLight) {
			effect |= Integer.MIN_VALUE;
		}
		if (effect != this.activeEffect) {
			if (this.activeEffect != 0) {
				this.materialEffects[Integer.MAX_VALUE & this.activeEffect].disable();
			}
			if (effect != 0) {
				this.materialEffects[effect & Integer.MAX_VALUE].enable(hasLight);
				this.materialEffects[Integer.MAX_VALUE & effect].applyTextureCombine(hasLight);
				this.materialEffects[effect & Integer.MAX_VALUE].setEffectParams(effectParam1, effectParam2);
			}
			this.activeEffect = effect;
			this.param2 = effectParam2;
			this.param1 = effectParam1;
		} else if (this.activeEffect != 0) {
			this.materialEffects[Integer.MAX_VALUE & this.activeEffect].applyTextureCombine(hasLight);
			if (this.param1 != effectParam1 || this.param2 != effectParam2) {
				this.materialEffects[Integer.MAX_VALUE & this.activeEffect].setEffectParams(effectParam1, effectParam2);
				this.param1 = effectParam1;
				this.param2 = effectParam2;
			}
		}
	}

	@OriginalMember(owner = "client!mea", name = "a", descriptor = "(IBLclient!ud;)Z")
	public boolean bindTexture(@OriginalArg(0) int combineMode, @OriginalArg(2) GlTexture texture) {
		if (this.activeEffect == 0) {
			return false;
		} else {
			this.materialEffects[Integer.MAX_VALUE & this.activeEffect].bindTexture(texture, combineMode);
			return true;
		}
	}

	@OriginalMember(owner = "client!mea", name = "a", descriptor = "(BI)Z")
	public boolean isStillWaterSupported() {
		return this.materialEffects[3].isSupported();
	}
}
