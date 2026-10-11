package com.jagex.game.runetek5.graphics.model;

import com.jagex.game.runetek5.config.sequence.frameset.AnimBase;
import com.jagex.game.runetek5.config.sequence.frameset.AnimFrame;
import com.jagex.game.runetek5.config.sequence.frameset.FrameSet;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex;
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.features.ClientFeatures;
import com.jagex.game.runetek5.cache.node.lru.Shadow;
import com.jagex.game.runetek5.entity.BoundingBoxPolygon;
import com.jagex.game.runetek5.scene.ground.Ground;
import com.jagex.game.runetek5.core.crypto.GpuModelRetainMasks;

@OriginalClass("client!ka")
public abstract class Model {

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "Z")
	public boolean isLocked = false;

	@OriginalMember(owner = "client!ka", name = "<init>", descriptor = "()V")
	public Model() {
	}

	@OriginalMember(owner = "client!ka", name = "FA", descriptor = "(I)V")
	public abstract void FA(@OriginalArg(0) int angle);

	@OriginalMember(owner = "client!ka", name = "p", descriptor = "(IILclient!s;Lclient!s;III)V")
	public abstract void p(@OriginalArg(0) int contourType, @OriginalArg(1) int contourModifier, @OriginalArg(2) Ground ground, @OriginalArg(3) Ground upperGround, @OriginalArg(4) int x, @OriginalArg(5) int y, @OriginalArg(6) int z);

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(ILclient!raa;IZILclient!gk;I[I[ZLclient!gk;IZ)V")
	private void interpolateFrames(@OriginalArg(0) int rotation, @OriginalArg(1) AnimBase animBase, @OriginalArg(3) boolean secondaryPass, @OriginalArg(4) int length, @OriginalArg(5) AnimFrame animFrame, @OriginalArg(6) int cycle, @OriginalArg(7) int[] matrix, @OriginalArg(8) boolean[] animateLabels, @OriginalArg(9) AnimFrame animFrame2, @OriginalArg(10) int submesh, @OriginalArg(11) boolean lights) {
		if (ClientFeatures.FPS > 0 && animFrame != null && cycle >= 0 && length > 0) {
			// Not part of the original client: with client.fps the blend between the two keyframes
			// moves on between game cycles too, by how far the cycle has run.
			cycle = cycle * ClientFeatures.ANIMATION_STEPS + ClientFeatures.animationStep();
			length = length * ClientFeatures.ANIMATION_STEPS;
		}
		@Pc(14) int currentTransformation;
		if (animFrame == null || cycle == 0) {
			for (currentTransformation = 0; currentTransformation < animFrame2.numTransformations; currentTransformation++) {
				@Pc(21) short baseIndex = animFrame2.baseIndices[currentTransformation];
				if (animateLabels == null || animateLabels[baseIndex] == secondaryPass || animBase.transformationType[baseIndex] == 0) {
					@Pc(42) short label = animFrame2.labels[currentTransformation];
					if (label != -1) {
						this.animateTransform(animBase.labels[label], lights, rotation, 0, 0, submesh & animBase.submeshes[label], 0, matrix, 0);
					}
					this.animateTransform(animBase.labels[baseIndex], lights, rotation, animFrame2.transformationY[currentTransformation], animBase.transformationType[baseIndex], animBase.submeshes[baseIndex] & submesh, animFrame2.transformationX[currentTransformation], matrix, animFrame2.transformationZ[currentTransformation]);
				}
			}
			return;
		}
		currentTransformation = 0;
		@Pc(107) int nextTransformation = 0;
		for (@Pc(109) int i = 0; i < animBase.numTransformation; i++) {
			@Pc(121) boolean hasCurrent = false;
			if (currentTransformation < animFrame2.numTransformations && animFrame2.baseIndices[currentTransformation] == i) {
				hasCurrent = true;
			}
			@Pc(139) boolean hasNext = false;
			if (nextTransformation < animFrame.numTransformations && animFrame.baseIndices[nextTransformation] == i) {
				hasNext = true;
			}
			if (hasCurrent || hasNext) {
				if (animateLabels == null || secondaryPass == animateLabels[i] || animBase.transformationType[i] == 0) {
					@Pc(189) short defaultValue = 0;
					@Pc(194) int transformationType = animBase.transformationType[i];
					if (transformationType == 3 || transformationType == 10) {
						defaultValue = 128;
					}
					@Pc(216) short currentX;
					@Pc(210) short currentY;
					@Pc(214) short currentZ;
					@Pc(212) short currentLabel;
					@Pc(218) byte currentTweening;
					if (hasCurrent) {
						currentZ = animFrame2.transformationZ[currentTransformation];
						currentLabel = animFrame2.labels[currentTransformation];
						currentY = animFrame2.transformationY[currentTransformation];
						currentTweening = animFrame2.tweeningProperties[currentTransformation];
						currentX = animFrame2.transformationX[currentTransformation];
						currentTransformation++;
					} else {
						currentY = defaultValue;
						currentLabel = -1;
						currentZ = defaultValue;
						currentX = defaultValue;
						currentTweening = 0;
					}
					@Pc(254) short nextX;
					@Pc(256) short nextY;
					@Pc(250) short nextZ;
					@Pc(252) short nextLabel;
					@Pc(258) byte nextTweening;
					if (hasNext) {
						nextY = animFrame.transformationY[nextTransformation];
						nextZ = animFrame.transformationZ[nextTransformation];
						nextTweening = animFrame.tweeningProperties[nextTransformation];
						nextX = animFrame.transformationX[nextTransformation];
						nextLabel = animFrame.labels[nextTransformation];
						nextTransformation++;
					} else {
						nextZ = defaultValue;
						nextLabel = -1;
						nextX = defaultValue;
						nextY = defaultValue;
						nextTweening = 0;
					}
					if (currentLabel != -1) {
						this.animateTransform(animBase.labels[currentLabel], lights, rotation, 0, 0, submesh & animBase.submeshes[currentLabel], 0, matrix, 0);
					} else if (nextLabel != -1) {
						this.animateTransform(animBase.labels[nextLabel], lights, rotation, 0, 0, submesh & animBase.submeshes[nextLabel], 0, matrix, 0);
					}
					@Pc(348) int interpolatedX;
					@Pc(352) int interpolatedY;
					@Pc(350) int interpolatedZ;
					if ((currentTweening & 0x2) == 0 && (nextTweening & 0x1) == 0) {
						@Pc(371) int deltaX;
						if (transformationType == 2) {
							deltaX = nextX - currentX & 0x3FFF;
							@Pc(490) int deltaY = nextY - currentY & 0x3FFF;
							@Pc(496) int deltaZ = nextZ - currentZ & 0x3FFF;
							if (deltaX >= 8192) {
								deltaX -= 16384;
							}
							if (deltaY >= 8192) {
								deltaY -= 16384;
							}
							if (deltaZ >= 8192) {
								deltaZ -= 16384;
							}
							interpolatedX = currentX + deltaX * cycle / length & 0x3FFF;
							interpolatedY = deltaY * cycle / length + currentY & 0x3FFF;
							interpolatedZ = currentZ + cycle * deltaZ / length & 0x3FFF;
						} else if (transformationType == 9) {
							deltaX = nextX - currentX & 0x3FFF;
							if (deltaX >= 8192) {
								deltaX -= 16384;
							}
							interpolatedX = deltaX * cycle / length + currentX & 0x3FFF;
							interpolatedZ = 0;
							interpolatedY = 0;
						} else if (transformationType == 7) {
							deltaX = nextX - currentX & 0x3F;
							if (deltaX >= 32) {
								deltaX -= 64;
							}
							interpolatedX = currentX + deltaX * cycle / length & 0x3F;
							interpolatedY = cycle * (nextY - currentY) / length + currentY;
							interpolatedZ = (nextZ - currentZ) * cycle / length + currentZ;
						} else {
							interpolatedX = currentX + cycle * (nextX - currentX) / length;
							interpolatedZ = currentZ + (nextZ - currentZ) * cycle / length;
							interpolatedY = cycle * (nextY - currentY) / length + currentY;
						}
					} else {
						interpolatedX = currentX;
						interpolatedZ = currentZ;
						interpolatedY = currentY;
					}
					this.animateTransform(animBase.labels[i], lights, rotation, interpolatedY, transformationType, submesh & animBase.submeshes[i], interpolatedX, matrix, interpolatedZ);
				} else {
					if (hasNext) {
						nextTransformation++;
					}
					if (hasCurrent) {
						currentTransformation++;
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(BI[IIIILclient!gw;ILclient!gw;ZI)V")
	public final void animateUnrotatedSubmesh(@OriginalArg(1) int cycle, @OriginalArg(2) int[] matrix, @OriginalArg(3) int submesh, @OriginalArg(4) int frameId, @OriginalArg(5) int nextFrameId, @OriginalArg(6) FrameSet animFrameset, @OriginalArg(8) FrameSet animFrameset2, @OriginalArg(9) boolean lights, @OriginalArg(10) int length) {
		if (frameId == -1) {
			return;
		}
		this.lock();
		if (!this.NA()) {
			this.unlock();
			return;
		}
		@Pc(20) AnimFrame animFrame = animFrameset2.frames[frameId];
		@Pc(29) AnimBase animBase = animFrame.base;
		@Pc(31) AnimFrame animFrame2 = null;
		if (animFrameset != null) {
			animFrame2 = animFrameset.frames[nextFrameId];
			if (animFrame2.base != animBase) {
				animFrame2 = null;
			}
		}
		this.interpolateFrames(0, animBase, false, length, animFrame2, cycle, matrix, null, animFrame, submesh, lights);
		this.wa();
		this.unlock();
	}

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "()[Lclient!eca;")
	public abstract EffectiveVertex[] getEffectiveVertices();

	@OriginalMember(owner = "client!ka", name = "C", descriptor = "(I)V")
	public abstract void C(@OriginalArg(0) int ambient);

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(I[IIIIIZ)V")
	public abstract void applyTransformUnmasked(@OriginalArg(0) int type, @OriginalArg(1) int[] labels, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int z, @OriginalArg(5) int rotation, @OriginalArg(6) boolean lights);

	@OriginalMember(owner = "client!ka", name = "wa", descriptor = "()V")
	public abstract void wa();

	@OriginalMember(owner = "client!ka", name = "I", descriptor = "(I[IIIIZI[I)V")
	public abstract void I(@OriginalArg(0) int type, @OriginalArg(1) int[] labels, @OriginalArg(2) int x, @OriginalArg(3) int y, @OriginalArg(4) int z, @OriginalArg(5) boolean lights, @OriginalArg(6) int submesh, @OriginalArg(7) int[] matrix);

	@OriginalMember(owner = "client!ka", name = "G", descriptor = "()I")
	public abstract int G();

	@OriginalMember(owner = "client!ka", name = "NA", descriptor = "()Z")
	public abstract boolean NA();

	@OriginalMember(owner = "client!ka", name = "LA", descriptor = "(I)V")
	public abstract void LA(@OriginalArg(0) int contrast);

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(Lclient!bca;Lclient!iw;II)V")
	public abstract void renderOrtho(@OriginalArg(0) Matrix matrix, @OriginalArg(1) BoundingBoxPolygon boundingBox, @OriginalArg(2) int zoom, @OriginalArg(3) int flags);

	@OriginalMember(owner = "client!ka", name = "ua", descriptor = "()I")
	public abstract int ua();

	@OriginalMember(owner = "client!ka", name = "HA", descriptor = "()I")
	public abstract int HA();

	@OriginalMember(owner = "client!ka", name = "ma", descriptor = "()I")
	public abstract int ma();

	@OriginalMember(owner = "client!ka", name = "F", descriptor = "()Z")
	public abstract boolean F();

	@OriginalMember(owner = "client!ka", name = "O", descriptor = "(III)V")
	public abstract void O(@OriginalArg(0) int scaleX, @OriginalArg(1) int scaleY, @OriginalArg(2) int scaleZ);

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(Lclient!bca;IZ)V")
	public abstract void transform(@OriginalArg(0) Matrix matrix, @OriginalArg(1) int submesh, @OriginalArg(2) boolean relative);

	@OriginalMember(owner = "client!ka", name = "EA", descriptor = "()I")
	public abstract int EA();

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(IILclient!gw;)V")
	public final void animateShadowFrame(@OriginalArg(0) int frameId, @OriginalArg(2) FrameSet animFrameset) {
		if (frameId == -1) {
			return;
		}
		this.lock();
		if (!this.NA()) {
			this.unlock();
			return;
		}
		@Pc(23) AnimFrame animFrame = animFrameset.frames[frameId];
		@Pc(26) AnimBase animBase = animFrame.base;
		for (@Pc(28) int i = 0; i < animFrame.numTransformations; i++) {
			@Pc(35) short baseIndex = animFrame.baseIndices[i];
			if (animBase.shadowed[baseIndex]) {
				if (animFrame.labels[i] != -1) {
					this.P(0, 0, 0, 0);
				}
				this.P(animBase.transformationType[baseIndex], animFrame.transformationX[i], animFrame.transformationY[i], animFrame.transformationZ[i]);
			}
		}
		this.wa();
		this.unlock();
	}

	@OriginalMember(owner = "client!ka", name = "b", descriptor = "()V")
	public abstract void uploadToGpu();

	@OriginalMember(owner = "client!ka", name = "da", descriptor = "()I")
	public abstract int da();

	@OriginalMember(owner = "client!ka", name = "s", descriptor = "(I)V")
	public abstract void s(@OriginalArg(0) int functionMask);

	@OriginalMember(owner = "client!ka", name = "RA", descriptor = "()I")
	public abstract int RA();

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(Lclient!ka;IIIZ)V")
	public abstract void shareLight(@OriginalArg(0) Model model, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int z, @OriginalArg(4) boolean renderTypeAdjustment);

	@OriginalMember(owner = "client!ka", name = "v", descriptor = "()V")
	public abstract void v();

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(BIZ)Lclient!ka;")
	public abstract Model copy(@OriginalArg(0) byte copyType, @OriginalArg(1) int functionMask, @OriginalArg(2) boolean ensureLit);

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(Lclient!bca;Lclient!iw;I)V")
	public abstract void render(@OriginalArg(0) Matrix matrix, @OriginalArg(1) BoundingBoxPolygon boundingBox, @OriginalArg(2) int flags);

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(IILclient!gw;ILclient!gw;ZZII)V")
	public final void animateModelFrame(@OriginalArg(0) int frameId, @OriginalArg(1) int length, @OriginalArg(2) FrameSet animFrameset, @OriginalArg(3) int rotation, @OriginalArg(4) FrameSet animFrameset2, @OriginalArg(6) boolean lights, @OriginalArg(7) int nextFrameId, @OriginalArg(8) int cycle) {
		if (frameId == -1) {
			return;
		}
		this.lock();
		if (!this.NA()) {
			this.unlock();
			return;
		}
		@Pc(20) AnimFrame animFrame = animFrameset2.frames[frameId];
		@Pc(23) AnimBase animBase = animFrame.base;
		@Pc(25) AnimFrame animFrame2 = null;
		if (animFrameset != null) {
			animFrame2 = animFrameset.frames[nextFrameId];
			if (animBase != animFrame2.base) {
				animFrame2 = null;
			}
		}
		this.interpolateFrames(rotation, animBase, false, length, animFrame2, cycle, null, null, animFrame, 65535, lights);
		this.wa();
		this.unlock();
	}

	@OriginalMember(owner = "client!ka", name = "r", descriptor = "()Z")
	public abstract boolean r();

	@OriginalMember(owner = "client!ka", name = "c", descriptor = "()Z")
	public abstract boolean loadedTextures();

	@OriginalMember(owner = "client!ka", name = "fa", descriptor = "()I")
	public abstract int fa();

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(I)V")
	public abstract void a(@OriginalArg(0) int angle);

	@OriginalMember(owner = "client!ka", name = "d", descriptor = "()[Lclient!nn;")
	public abstract EmissiveTriangle[] getEmissiveTriangles();

	@OriginalMember(owner = "client!ka", name = "V", descriptor = "()I")
	public abstract int V();

	@OriginalMember(owner = "client!ka", name = "H", descriptor = "(III)V")
	public abstract void H(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) int z);

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(Lclient!bca;)V")
	public abstract void updateParticlePosition(@OriginalArg(0) Matrix matrix);

	@OriginalMember(owner = "client!ka", name = "e", descriptor = "()V")
	public abstract void unlock();

	@OriginalMember(owner = "client!ka", name = "f", descriptor = "()V")
	public abstract void lock();

	@OriginalMember(owner = "client!ka", name = "k", descriptor = "(I)V")
	public abstract void k(@OriginalArg(0) int angle);

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(IILclient!bca;ZII)Z")
	public abstract boolean pickedOrtho(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) Matrix matrix, @OriginalArg(3) boolean quick, @OriginalArg(4) int sizeShift, @OriginalArg(5) int zoom);

	@OriginalMember(owner = "client!ka", name = "P", descriptor = "(IIII)V")
	public abstract void P(@OriginalArg(0) int type, @OriginalArg(1) int x, @OriginalArg(2) int y, @OriginalArg(3) int z);

	@OriginalMember(owner = "client!ka", name = "WA", descriptor = "()I")
	public abstract int WA();

	@OriginalMember(owner = "client!ka", name = "VA", descriptor = "(I)V")
	public abstract void VA(@OriginalArg(0) int angle);

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(IIII)V")
	public abstract void adjustColours(@OriginalArg(0) int hue, @OriginalArg(1) int saturation, @OriginalArg(2) int lightness, @OriginalArg(3) int opacity);

	@OriginalMember(owner = "client!ka", name = "na", descriptor = "()I")
	public abstract int na();

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(Lclient!gw;IBILclient!gw;IIILclient!gw;ZILclient!gw;I[ZI)V")
	public final void doubleAnimation(@OriginalArg(0) FrameSet animFrameset, @OriginalArg(1) int length, @OriginalArg(3) int secondaryLength, @OriginalArg(4) FrameSet animFrameset2, @OriginalArg(5) int frameId, @OriginalArg(6) int secondaryFrameId, @OriginalArg(7) int cycle, @OriginalArg(8) FrameSet animFrameset3, @OriginalArg(9) boolean lights, @OriginalArg(10) int nextFrameId, @OriginalArg(11) FrameSet animFrameset4, @OriginalArg(12) int secondaryCycle, @OriginalArg(13) boolean[] animateLabels, @OriginalArg(14) int secondaryNextFrameId) {
		if (frameId == -1) {
			return;
		}
		if (animateLabels == null || secondaryFrameId == -1) {
			this.animateModelFrame(frameId, length, animFrameset, 0, animFrameset3, lights, nextFrameId, cycle);
			return;
		}
		this.lock();
		if (!this.NA()) {
			this.unlock();
			return;
		}
		@Pc(43) AnimFrame animFrame = animFrameset3.frames[frameId];
		@Pc(46) AnimBase animBase = animFrame.base;
		@Pc(48) AnimFrame animFrame2 = null;
		if (animFrameset != null) {
			animFrame2 = animFrameset.frames[nextFrameId];
			if (animBase != animFrame2.base) {
				animFrame2 = null;
			}
		}
		this.interpolateFrames(0, animBase, false, length, animFrame2, cycle, null, animateLabels, animFrame, 65535, lights);
		@Pc(80) AnimFrame animFrame3 = animFrameset2.frames[secondaryFrameId];
		@Pc(82) AnimFrame animFrame4 = null;
		if (animFrameset4 != null) {
			animFrame4 = animFrameset4.frames[secondaryNextFrameId];
			if (animFrame4.base != animBase) {
				animFrame4 = null;
			}
		}
		this.applyTransformUnmasked(0, new int[0], 0, 0, 0, 0, lights);
		this.interpolateFrames(0, animFrame3.base, true, secondaryLength, animFrame4, secondaryCycle, null, animateLabels, animFrame3, 65535, lights);
		this.wa();
		this.unlock();
	}

	@OriginalMember(owner = "client!ka", name = "ia", descriptor = "(SS)V")
	public abstract void ia(@OriginalArg(0) short src, @OriginalArg(1) short dst);

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(IIIIIIILclient!s;I)V")
	public final void hillChange(@OriginalArg(0) int sizeX, @OriginalArg(2) int maxRotateZ, @OriginalArg(3) int x, @OriginalArg(4) int sizeZ, @OriginalArg(5) int y, @OriginalArg(6) int maxRotateX, @OriginalArg(7) Ground ground, @OriginalArg(8) int z) {
		@Pc(16) int deltaXSw = -sizeX / 2;
		@Pc(21) int deltaZSw = -sizeZ / 2;
		@Pc(31) int southWest = ground.averageHeight(x + deltaXSw, deltaZSw + z);
		@Pc(35) int deltaXSe = sizeX / 2;
		@Pc(40) int deltaZSe = -sizeZ / 2;
		@Pc(51) int southEast = ground.averageHeight(deltaXSe + x, z - -deltaZSe);
		@Pc(56) int deltaXNw = -sizeX / 2;
		@Pc(60) int deltaZNw = sizeZ / 2;
		@Pc(71) int northWest = ground.averageHeight(x + deltaXNw, z - -deltaZNw);
		@Pc(75) int deltaXNe = sizeX / 2;
		@Pc(85) int deltaZNe = sizeZ / 2;
		@Pc(95) int northEast = ground.averageHeight(x + deltaXNe, deltaZNe + z);
		@Pc(106) int lowestSouth = southWest < southEast ? southWest : southEast;
		@Pc(113) int lowestNorth = northEast <= northWest ? northEast : northWest;
		@Pc(124) int lowestEast = southEast < northEast ? southEast : northEast;
		@Pc(157) int inverse;
		if (sizeZ != 0) {
			@Pc(142) int rotationX = (int) (Math.atan2((double) (lowestSouth - lowestNorth), (double) sizeZ) * 2607.5945876176133D) & 0x3FFF;
			if (rotationX != 0) {
				if (maxRotateX != 0) {
					if (rotationX > 8192) {
						inverse = 16384 - maxRotateX;
						if (inverse > rotationX) {
							rotationX = inverse;
						}
					} else if (rotationX > maxRotateX) {
						rotationX = maxRotateX;
					}
				}
				this.FA(rotationX);
			}
		}
		@Pc(183) int lowestWest = southWest >= northWest ? northWest : southWest;
		if (sizeX != 0) {
			@Pc(202) int rotationZ = (int) (Math.atan2((double) (lowestWest - lowestEast), (double) sizeX) * 2607.5945876176133D) & 0x3FFF;
			if (rotationZ != 0) {
				if (maxRotateZ != 0) {
					if (rotationZ > 8192) {
						inverse = 16384 - maxRotateZ;
						if (inverse > rotationZ) {
							rotationZ = inverse;
						}
					} else if (maxRotateZ < rotationZ) {
						rotationZ = maxRotateZ;
					}
				}
				this.VA(rotationZ);
			}
		}
		@Pc(237) int height = northEast + southWest;
		if (height > northWest + southEast) {
			height = northWest + southEast;
		}
		height = (height >> 1) - y;
		if (height != 0) {
			this.H(0, height, 0);
		}
	}

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "([IZIIIIBI[II)V")
	private void animateTransform(@OriginalArg(0) int[] labels, @OriginalArg(1) boolean lights, @OriginalArg(2) int rotation, @OriginalArg(3) int y, @OriginalArg(4) int transformType, @OriginalArg(5) int submesh, @OriginalArg(7) int x, @OriginalArg(8) int[] matrix, @OriginalArg(9) int z) {
		@Pc(41) int temp;
		if (rotation == 1) {
			if (transformType == 0 || transformType == 1) {
				temp = -x;
				x = z;
				z = temp;
			} else if (transformType == 3) {
				temp = x;
				x = z;
				z = temp;
			} else if (transformType == 2) {
				temp = x;
				x = -z & 0x3FFF;
				z = temp & 0x3FFF;
			}
		} else if (rotation == 2) {
			if (transformType == 0 || transformType == 1) {
				x = -x;
				z = -z;
			} else if (transformType == 2) {
				z = -z & 0x3FFF;
				x = -x & 0x3FFF;
			}
		} else if (rotation == 3) {
			if (transformType == 0 || transformType == 1) {
				temp = x;
				x = -z;
				z = temp;
			} else if (transformType == 3) {
				temp = x;
				x = z;
				z = temp;
			} else if (transformType == 2) {
				temp = x;
				x = z & 0x3FFF;
				z = -temp & 0x3FFF;
			}
		}
		if (submesh == 65535) {
			this.applyTransformUnmasked(transformType, labels, x, y, z, rotation, lights);
		} else {
			this.I(transformType, labels, x, y, z, lights, submesh, matrix);
		}
	}

	@OriginalMember(owner = "client!ka", name = "a", descriptor = "(IILclient!bca;ZI)Z")
	public abstract boolean picked(@OriginalArg(0) int x, @OriginalArg(1) int y, @OriginalArg(2) Matrix matrix, @OriginalArg(3) boolean quick, @OriginalArg(4) int sizeShift);

	@OriginalMember(owner = "client!ka", name = "aa", descriptor = "(SS)V")
	public abstract void aa(@OriginalArg(0) short src, @OriginalArg(1) short dst);

	@OriginalMember(owner = "client!ka", name = "ba", descriptor = "(Lclient!r;)Lclient!r;")
	public abstract Shadow ba(@OriginalArg(0) Shadow shadow);
	@OriginalMember(owner = "client!ca", name = "a", descriptor = "(IIIII[FIFIIF[F)V")
	public static void mapCylindricalUv(@OriginalArg(0) int centerZ, @OriginalArg(1) int vertexZ, @OriginalArg(2) int direction, @OriginalArg(4) int vertexX, @OriginalArg(5) float[] uv, @OriginalArg(6) int vertexY, @OriginalArg(7) float speedOffset, @OriginalArg(8) int centerY, @OriginalArg(9) int centerX, @OriginalArg(10) float uScale, @OriginalArg(11) float[] matrix) {
		@Pc(5) int dy = vertexY - centerY;
		@Pc(9) int dz = vertexZ - centerZ;
		@Pc(17) int dx = vertexX - centerX;
		@Pc(38) float tx = matrix[2] * (float) dz + matrix[1] * (float) dy + (float) dx * matrix[0];
		@Pc(59) float ty = matrix[5] * (float) dz + matrix[3] * (float) dx + (float) dy * matrix[4];
		@Pc(80) float tz = (float) dx * matrix[6] + matrix[7] * (float) dy + (float) dz * matrix[8];
		@Pc(91) float u = (float) Math.atan2((double) tx, (double) tz) / 6.2831855F + 0.5F;
		if (uScale != 1.0F) {
			u *= uScale;
		}
		@Pc(105) float v = ty + speedOffset + 0.5F;
		@Pc(110) float tmp;
		if (direction == 1) {
			tmp = u;
			u = -v;
			v = tmp;
		} else if (direction == 2) {
			u = -u;
			v = -v;
		} else if (direction == 3) {
			tmp = u;
			u = v;
			v = -tmp;
		}
		uv[1] = v;
		uv[0] = u;
	}
	@OriginalMember(owner = "client!qb", name = "a", descriptor = "(IIFI[FIIIII[F)V")
	public static void mapSphericalUv(@OriginalArg(0) int centerZ, @OriginalArg(1) int direction, @OriginalArg(2) float speedOffset, @OriginalArg(3) int vertexX, @OriginalArg(4) float[] uv, @OriginalArg(5) int vertexZ, @OriginalArg(6) int centerX, @OriginalArg(7) int vertexY, @OriginalArg(8) int centerY, @OriginalArg(10) float[] matrix) {
		@Pc(5) int dz = vertexZ - centerZ;
		@Pc(13) int dx = vertexX - centerX;
		@Pc(17) int dy = vertexY - centerY;
		@Pc(38) float tx = (float) dz * matrix[2] + matrix[1] * (float) dy + matrix[0] * (float) dx;
		@Pc(59) float ty = (float) dz * matrix[5] + (float) dx * matrix[3] + (float) dy * matrix[4];
		@Pc(80) float tz = matrix[6] * (float) dx + (float) dy * matrix[7] + (float) dz * matrix[8];
		@Pc(95) float dist = (float) Math.sqrt((double) (tx * tx + ty * ty + tz * tz));
		@Pc(106) float u = (float) Math.atan2((double) tx, (double) tz) / 6.2831855F + 0.5F;
		@Pc(124) float v = speedOffset + (float) Math.asin((double) (ty / dist)) / 3.1415927F + 0.5F;
		@Pc(139) float tmp;
		if (direction == 1) {
			tmp = u;
			u = -v;
			v = tmp;
		} else if (direction == 2) {
			v = -v;
			u = -u;
		} else if (direction == 3) {
			tmp = u;
			u = v;
			v = -tmp;
		}
		uv[0] = u;
		uv[1] = v;
	}
	@OriginalMember(owner = "client!dia", name = "a", descriptor = "(FZFF)I")
	public static int getCubeFace(@OriginalArg(0) float x, @OriginalArg(2) float y, @OriginalArg(3) float z) {
		@Pc(12) float absZ = z < 0.0F ? -z : z;
		@Pc(21) float absX = x < 0.0F ? -x : x;
		@Pc(35) float absY = y < 0.0F ? -y : y;
		if (absZ < absX && absX > absY) {
			return x > 0.0F ? 0 : 1;
		} else if (absY > absZ && absX < absY) {
			return y > 0.0F ? 2 : 3;
		} else if (z > 0.0F) {
			return 4;
		} else {
			return 5;
		}
	}

	@OriginalMember(owner = "client!ts", name = "b", descriptor = "(III)Z")
	public static boolean retainFaceAlpha(@OriginalArg(1) int preferenceFlags, @OriginalArg(2) int functionMask) {
		return (functionMask & 0x100) != 0;
	}

	@OriginalMember(owner = "client!gt", name = "a", descriptor = "(IZI)Z")
	public static boolean retainFaceMaterials(@OriginalArg(0) int functionMask, @OriginalArg(2) int preferenceFlags) {
		return (functionMask & 0x800) != 0 | GpuModelRetainMasks.altersTextures(preferenceFlags, functionMask) || GpuModelRetainMasks.requiresColorBufferRebuild(functionMask, preferenceFlags);
	}
	@OriginalMember(owner = "client!uh", name = "a", descriptor = "(FF[FIIZIIIIF[FII)V")
	public static void mapCubeUv(@OriginalArg(0) float vTrans, @OriginalArg(1) float speedOffset, @OriginalArg(2) float[] matrix, @OriginalArg(3) int vertexZ, @OriginalArg(4) int centerZ, @OriginalArg(6) int direction, @OriginalArg(7) int centerX, @OriginalArg(8) int vertexX, @OriginalArg(9) int vertexY, @OriginalArg(10) float uTrans, @OriginalArg(11) float[] uv, @OriginalArg(12) int centerY, @OriginalArg(13) int cubeFace) {
		@Pc(9) int vX = vertexX - centerX;
		@Pc(13) int vZ = vertexZ - centerZ;
		@Pc(17) int vY = vertexY - centerY;
		@Pc(38) float tx = matrix[2] * (float) vZ + matrix[1] * (float) vY + matrix[0] * (float) vX;
		@Pc(59) float ty = (float) vX * matrix[3] + (float) vY * matrix[4] + (float) vZ * matrix[5];
		@Pc(80) float tz = matrix[8] * (float) vZ + matrix[6] * (float) vX + (float) vY * matrix[7];
		@Pc(102) float u;
		@Pc(96) float v;
		if (cubeFace == 0) {
			u = speedOffset + tx + 0.5F;
			v = vTrans + 0.5F - tz;
		} else if (cubeFace == 1) {
			v = tz + vTrans + 0.5F;
			u = speedOffset + tx + 0.5F;
		} else if (cubeFace == 2) {
			u = speedOffset + 0.5F - tx;
			v = uTrans + 0.5F - ty;
		} else if (cubeFace == 3) {
			v = uTrans + 0.5F - ty;
			u = tx + speedOffset + 0.5F;
		} else if (cubeFace == 4) {
			u = tz + vTrans + 0.5F;
			v = uTrans + 0.5F - ty;
		} else {
			u = vTrans + 0.5F - tz;
			v = uTrans + 0.5F - ty;
		}
		@Pc(197) float tmp;
		if (direction == 1) {
			tmp = u;
			u = -v;
			v = tmp;
		} else if (direction == 2) {
			v = -v;
			u = -u;
		} else if (direction == 3) {
			tmp = u;
			u = v;
			v = -tmp;
		}
		uv[1] = v;
		uv[0] = u;
	}
}
