package com.jagex.game.runetek5.entity.pathingentity;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

@OriginalClass("client!ui")
public final class EasedAngle {

	@OriginalMember(owner = "client!ui", name = "b", descriptor = "I")
	private int velocity;

	@OriginalMember(owner = "client!ui", name = "g", descriptor = "I")
	public int rotation;

	@OriginalMember(owner = "client!ui", name = "a", descriptor = "(II)V")
	public void setRotation(@OriginalArg(1) int rotation) {
		this.velocity = 0;
		this.rotation = rotation;
	}

	@OriginalMember(owner = "client!ui", name = "a", descriptor = "(B)I")
	public int getRotation() {
		return this.rotation & 0x3FFF;
	}

	@OriginalMember(owner = "client!ui", name = "a", descriptor = "(IIIB)Z")
	public boolean calculateRotation(@OriginalArg(0) int maxVelocity, @OriginalArg(1) int rotation, @OriginalArg(2) int acceleration) {
		@Pc(8) int velocity = this.velocity;
		if (this.rotation == rotation && this.velocity == 0) {
			return false;
		}
		@Pc(71) boolean shouldAccelerate;
		@Pc(49) int stoppingDistance;
		if (this.velocity == 0) {
			if (this.rotation < rotation && rotation <= acceleration + this.rotation || rotation < this.rotation && rotation >= this.rotation - acceleration) {
				this.rotation = rotation;
				return false;
			}
			shouldAccelerate = true;
		} else {
			@Pc(54) int projectedPosition;
			if (this.velocity > 0 && rotation > this.rotation) {
				stoppingDistance = this.velocity * this.velocity / (acceleration * 2);
				projectedPosition = stoppingDistance + this.rotation;
				if (rotation > projectedPosition && projectedPosition >= this.rotation) {
					shouldAccelerate = true;
				} else {
					shouldAccelerate = false;
				}
			} else if (this.velocity < 0 && this.rotation > rotation) {
				stoppingDistance = this.velocity * this.velocity / (acceleration * 2);
				projectedPosition = this.rotation - stoppingDistance;
				if (projectedPosition > rotation && projectedPosition <= this.rotation) {
					shouldAccelerate = true;
				} else {
					shouldAccelerate = false;
				}
			} else {
				shouldAccelerate = false;
			}
		}
		if (shouldAccelerate) {
			if (rotation <= this.rotation) {
				this.velocity -= acceleration;
				if (maxVelocity != 0 && this.velocity < -maxVelocity) {
					this.velocity = -maxVelocity;
				}
			} else {
				this.velocity += acceleration;
				if (maxVelocity != 0 && this.velocity > maxVelocity) {
					this.velocity = maxVelocity;
				}
			}
			if (velocity != this.velocity) {
				stoppingDistance = this.velocity * this.velocity / (acceleration * 2);
				if (this.rotation < rotation) {
					if (this.rotation + stoppingDistance > rotation) {
						this.velocity = velocity;
					}
				} else if (rotation < this.rotation && rotation > this.rotation - stoppingDistance) {
					this.velocity = velocity;
				}
			}
		} else if (this.velocity <= 0) {
			this.velocity += acceleration;
			if (this.velocity > 0) {
				this.velocity = 0;
			}
		} else {
			this.velocity -= acceleration;
			if (this.velocity < 0) {
				this.velocity = 0;
			}
		}
		this.rotation += this.velocity + velocity >> 1;
		return shouldAccelerate;
	}

	@OriginalMember(owner = "client!ui", name = "b", descriptor = "(B)V")
	public void clamp() {
		this.rotation &= 0x3FFF;
	}
}
