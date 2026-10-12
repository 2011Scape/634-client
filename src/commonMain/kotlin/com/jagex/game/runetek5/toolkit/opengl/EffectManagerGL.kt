package com.jagex.game.runetek5.toolkit.opengl

import com.jagex.game.runetek5.toolkit.opengl.effect.LappingWaterEffect
import com.jagex.game.runetek5.toolkit.opengl.effect.RipplingWaterEffect
import com.jagex.game.runetek5.toolkit.opengl.effect.WaterfallEffect
import com.jagex.game.runetek5.toolkit.opengl.effect.CubeMapSpecularEffect
import com.jagex.game.runetek5.toolkit.opengl.effect.ReflectingTextureEffect
import com.jagex.game.runetek5.toolkit.opengl.effect.ReflectiveRipplingWaterEffect
import com.jagex.game.runetek5.toolkit.opengl.effect.BreakingWaterEffect
import com.jagex.game.runetek5.toolkit.opengl.effect.MaterialEffectGL
import com.jagex.game.runetek5.toolkit.opengl.effect.UnlitEffect
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import com.jagex.game.runetek5.graphics.texture.water.TextureContainerGL
import com.jagex.game.runetek5.toolkit.opengl.effect.StillWaterEffect
import com.jagex.game.runetek5.graphics.gl.GlTexture
import com.jagex.game.runetek5.graphics.gl.GlToolkit
import kotlin.jvm.JvmField

@OriginalClass("client!mea")
class EffectManagerGL {

	@OriginalMember(owner = "client!mea", name = "h", descriptor = "I")
	private var param2: Int = 0

	@OriginalMember(owner = "client!mea", name = "b", descriptor = "I")
	private var activeEffect: Int = 0

	@OriginalMember(owner = "client!mea", name = "p", descriptor = "I")
	private var param1: Int = 0

	@OriginalMember(owner = "client!mea", name = "e", descriptor = "Lclient!qo;")
	private val openglToolkit: GlToolkit?

	@OriginalMember(owner = "client!mea", name = "f", descriptor = "Lclient!lc;")
	private val textureContainer: TextureContainerGL

	@OriginalMember(owner = "client!mea", name = "l", descriptor = "[Lclient!vk;")
	private val materialEffects: Array<MaterialEffectGL?>

	@OriginalMember(owner = "client!mea", name = "j", descriptor = "Lclient!an;")
	@JvmField
	val waterEffect: StillWaterEffect

	@OriginalMember(owner = "client!mea", name = "<init>", descriptor = "(Lclient!qo;)V")
	constructor(@OriginalArg(0) glToolkit: GlToolkit?) {
		this.openglToolkit = glToolkit
		this.textureContainer = TextureContainerGL(glToolkit)
		this.materialEffects = arrayOfNulls<MaterialEffectGL>(10)
		this.materialEffects[1] = CubeMapSpecularEffect(glToolkit)
		this.materialEffects[2] = LappingWaterEffect(glToolkit, this.textureContainer)
		this.materialEffects[4] = RipplingWaterEffect(glToolkit, this.textureContainer)
		this.materialEffects[5] = WaterfallEffect(glToolkit, this.textureContainer)
		this.materialEffects[6] = UnlitEffect(glToolkit)
		this.materialEffects[7] = ReflectingTextureEffect(glToolkit)
		this.waterEffect = StillWaterEffect(glToolkit)
		this.materialEffects[3] = this.waterEffect
		this.materialEffects[8] = ReflectiveRipplingWaterEffect(glToolkit, this.textureContainer)
		this.materialEffects[9] = BreakingWaterEffect(glToolkit, this.textureContainer)
		if (!this.materialEffects[8]!!.isSupported()) {
			this.materialEffects[8] = this.materialEffects[4]
		}
		if (!this.materialEffects[9]!!.isSupported()) {
			this.materialEffects[9] = this.materialEffects[8]
		}
	}

	@OriginalMember(owner = "client!mea", name = "a", descriptor = "(IZIIBZ)V")
	fun loadEffect(@OriginalArg(0) effectParam1: Int, @OriginalArg(1) hasLight: Boolean, @OriginalArg(2) effect: Int, @OriginalArg(3) effectParam2: Int, @OriginalArg(5) allowStillWater: Boolean) {
		var effect: Int = effect
		var effectParam2: Int = effectParam2
		@Pc(9) val useStillWater: Boolean = allowStillWater and this.openglToolkit!!.stillwaterEffectLoaded()
		if (!useStillWater && (effect == 4 || effect == 8 || effect == 9)) {
			if (effect == 4) {
				effectParam2 = effectParam1
			}
			effect = 2
		}
		if (effect != 0 && hasLight) {
			effect = effect or Int.MIN_VALUE
		}
		if (effect != this.activeEffect) {
			if (this.activeEffect != 0) {
				this.materialEffects[Int.MAX_VALUE and this.activeEffect]!!.disable()
			}
			if (effect != 0) {
				this.materialEffects[effect and Int.MAX_VALUE]!!.enable(hasLight)
				this.materialEffects[Int.MAX_VALUE and effect]!!.applyTextureCombine(hasLight)
				this.materialEffects[effect and Int.MAX_VALUE]!!.setEffectParams(effectParam1, effectParam2)
			}
			this.activeEffect = effect
			this.param2 = effectParam2
			this.param1 = effectParam1
		} else if (this.activeEffect != 0) {
			this.materialEffects[Int.MAX_VALUE and this.activeEffect]!!.applyTextureCombine(hasLight)
			if (this.param1 != effectParam1 || this.param2 != effectParam2) {
				this.materialEffects[Int.MAX_VALUE and this.activeEffect]!!.setEffectParams(effectParam1, effectParam2)
				this.param1 = effectParam1
				this.param2 = effectParam2
			}
		}
	}

	@OriginalMember(owner = "client!mea", name = "a", descriptor = "(IBLclient!ud;)Z")
	fun bindTexture(@OriginalArg(0) combineMode: Int, @OriginalArg(2) texture: GlTexture?): Boolean {
		if (this.activeEffect == 0) {
			return false
		} else {
			this.materialEffects[Int.MAX_VALUE and this.activeEffect]!!.bindTexture(texture, combineMode)
			return true
		}
	}

	@OriginalMember(owner = "client!mea", name = "a", descriptor = "(BI)Z")
	fun isStillWaterSupported(): Boolean {
		return this.materialEffects[3]!!.isSupported()
	}
}
