package com.jagex.game.runetek5.entity.pathingentity

import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.jvm.JvmField

@OriginalClass("client!ui")
class EasedAngle {

	@OriginalMember(owner = "client!ui", name = "b", descriptor = "I")
	private var velocity: Int = 0

	@OriginalMember(owner = "client!ui", name = "g", descriptor = "I")
	@JvmField
	var rotation: Int = 0

	@OriginalMember(owner = "client!ui", name = "a", descriptor = "(II)V")
	fun setRotation(@OriginalArg(1) rotation: Int) {
		this.velocity = 0
		this.rotation = rotation
	}

	@OriginalMember(owner = "client!ui", name = "a", descriptor = "(B)I")
	fun getRotation(): Int {
		return this.rotation and 0x3FFF
	}

	@OriginalMember(owner = "client!ui", name = "a", descriptor = "(IIIB)Z")
	fun calculateRotation(@OriginalArg(0) maxVelocity: Int, @OriginalArg(1) rotation: Int, @OriginalArg(2) acceleration: Int): Boolean {
		@Pc(8) val velocity: Int = this.velocity
		if (this.rotation == rotation && this.velocity == 0) {
			return false
		}
		@Pc(71) val shouldAccelerate: Boolean
		@Pc(49) var stoppingDistance: Int
		if (this.velocity == 0) {
			if (this.rotation < rotation && rotation <= acceleration + this.rotation || rotation < this.rotation && rotation >= this.rotation - acceleration) {
				this.rotation = rotation
				return false
			}
			shouldAccelerate = true
		} else {
			@Pc(54) var projectedPosition: Int
			if (this.velocity > 0 && rotation > this.rotation) {
				stoppingDistance = this.velocity * this.velocity / (acceleration * 2)
				projectedPosition = stoppingDistance + this.rotation
				if (rotation > projectedPosition && projectedPosition >= this.rotation) {
					shouldAccelerate = true
				} else {
					shouldAccelerate = false
				}
			} else if (this.velocity < 0 && this.rotation > rotation) {
				stoppingDistance = this.velocity * this.velocity / (acceleration * 2)
				projectedPosition = this.rotation - stoppingDistance
				if (projectedPosition > rotation && projectedPosition <= this.rotation) {
					shouldAccelerate = true
				} else {
					shouldAccelerate = false
				}
			} else {
				shouldAccelerate = false
			}
		}
		if (shouldAccelerate) {
			if (rotation <= this.rotation) {
				this.velocity -= acceleration
				if (maxVelocity != 0 && this.velocity < -maxVelocity) {
					this.velocity = -maxVelocity
				}
			} else {
				this.velocity += acceleration
				if (maxVelocity != 0 && this.velocity > maxVelocity) {
					this.velocity = maxVelocity
				}
			}
			if (velocity != this.velocity) {
				stoppingDistance = this.velocity * this.velocity / (acceleration * 2)
				if (this.rotation < rotation) {
					if (this.rotation + stoppingDistance > rotation) {
						this.velocity = velocity
					}
				} else if (rotation < this.rotation && rotation > this.rotation - stoppingDistance) {
					this.velocity = velocity
				}
			}
		} else if (this.velocity <= 0) {
			this.velocity += acceleration
			if (this.velocity > 0) {
				this.velocity = 0
			}
		} else {
			this.velocity -= acceleration
			if (this.velocity < 0) {
				this.velocity = 0
			}
		}
		this.rotation += (this.velocity + velocity) shr 1
		return shouldAccelerate
	}

	@OriginalMember(owner = "client!ui", name = "b", descriptor = "(B)V")
	fun clamp() {
		this.rotation = this.rotation and 0x3FFF
	}
}
