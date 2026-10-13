/*
 * Not part of the original client: 634HD (toolkit 6), copied from EffectManagerGL (the Java OpenGL toolkit,
 * toolkit 1) and moved onto HdGl, the GL 3.3 core emulator. The original stays as it was.
 */
package com.jagex.game.runetek5.graphics.hd

import kotlin.jvm.JvmField

class HdEffectManager {

	private var param2: Int = 0

	private var activeEffect: Int = 0

	private var param1: Int = 0

	private val openglToolkit: HdToolkit?

	private val textureContainer: HdTextureContainer

	private val materialEffects: Array<HdMaterialEffect?>

	@JvmField
	val waterEffect: HdStillWaterEffect

	constructor(glToolkit: HdToolkit?) {
		this.openglToolkit = glToolkit
		this.textureContainer = HdTextureContainer(glToolkit)
		this.materialEffects = arrayOfNulls<HdMaterialEffect>(10)
		this.materialEffects[1] = HdCubeMapSpecularEffect(glToolkit)
		this.materialEffects[2] = HdLappingWaterEffect(glToolkit, this.textureContainer)
		this.materialEffects[4] = HdRipplingWaterEffect(glToolkit, this.textureContainer)
		this.materialEffects[5] = HdWaterfallEffect(glToolkit, this.textureContainer)
		this.materialEffects[6] = HdUnlitEffect(glToolkit)
		this.materialEffects[7] = HdReflectingTextureEffect(glToolkit)
		this.waterEffect = HdStillWaterEffect(glToolkit)
		this.materialEffects[3] = this.waterEffect
		this.materialEffects[8] = HdReflectiveRipplingWaterEffect(glToolkit, this.textureContainer)
		this.materialEffects[9] = HdBreakingWaterEffect(glToolkit, this.textureContainer)
		if (!this.materialEffects[8]!!.isSupported()) {
			this.materialEffects[8] = this.materialEffects[4]
		}
		if (!this.materialEffects[9]!!.isSupported()) {
			this.materialEffects[9] = this.materialEffects[8]
		}
	}

	fun loadEffect(effectParam1: Int, hasLight: Boolean, effect: Int, effectParam2: Int, allowStillWater: Boolean) {
		var effect: Int = effect
		var effectParam2: Int = effectParam2
		val useStillWater: Boolean = allowStillWater and this.openglToolkit!!.stillwaterEffectLoaded()
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

	fun bindTexture(combineMode: Int, texture: HdTexture?): Boolean {
		if (this.activeEffect == 0) {
			return false
		} else {
			this.materialEffects[Int.MAX_VALUE and this.activeEffect]!!.bindTexture(texture, combineMode)
			return true
		}
	}

	fun isStillWaterSupported(): Boolean {
		return this.materialEffects[3]!!.isSupported()
	}
}
