package com.jagex.game.runetek5.toolkit.opengl;

import com.jagex.game.runetek5.ParticleList;
import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.graphics.model.particle.ParticleNode;
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer;
import com.jagex.game.runetek5.graphics.model.particle.PointEntity;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.buffer.ArrayBufferGL;
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!sn")
public final class GlParticleRenderer {

	@OriginalMember(owner = "client!sn", name = "k", descriptor = "Lclient!nh;")
	private OpenGlArrayDrawBuffer texCoordPointer;

	@OriginalMember(owner = "client!sn", name = "m", descriptor = "Lclient!hca;")
	private ArrayBufferGL arrayBuffer;

	@OriginalMember(owner = "client!sn", name = "n", descriptor = "Lclient!nh;")
	private OpenGlArrayDrawBuffer colourPointer;

	@OriginalMember(owner = "client!sn", name = "o", descriptor = "Lclient!nh;")
	private OpenGlArrayDrawBuffer vertexPointer;

	@OriginalMember(owner = "client!sn", name = "j", descriptor = "[F")
	private final float[] modelViewMatrix = new float[16];

	@OriginalMember(owner = "client!sn", name = "f", descriptor = "Lclient!gh;")
	private final GlBitBuffer vertices = new GlBitBuffer(786336);

	@OriginalMember(owner = "client!sn", name = "l", descriptor = "I")
	private final int bucketCountBits = Utils.bitLength(1600);

	@OriginalMember(owner = "client!sn", name = "r", descriptor = "[I")
	private final int[] depths = new int[8191];

	@OriginalMember(owner = "client!sn", name = "q", descriptor = "[[Lclient!jc;")
	private final PointEntity[][] buckets = new PointEntity[1600][64];

	@OriginalMember(owner = "client!sn", name = "s", descriptor = "[I")
	private final int[] bucketSizes = new int[1600];

	@OriginalMember(owner = "client!sn", name = "t", descriptor = "I")
	private int overflowBucketCount = 0;

	@OriginalMember(owner = "client!sn", name = "p", descriptor = "[[Lclient!jc;")
	private final PointEntity[][] overflowBuckets = new PointEntity[64][768];

	@OriginalMember(owner = "client!sn", name = "u", descriptor = "[I")
	private final int[] overflowBucketSizes = new int[64];

	@OriginalMember(owner = "client!sn", name = "a", descriptor = "(Lclient!qo;II)V")
	private void setupOrthoRenderState(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int orthoZoom) {
		GlParticleRenderer.savedSunBrightness = glToolkit.sunBrightness;
		glToolkit.setOrthoZoom((float) orthoZoom);
		glToolkit.setupOrthoMatrixRendering();
		OpenGL.glDisable(OpenGLConstants.GL_LIGHT0);
		OpenGL.glDisable(OpenGLConstants.GL_LIGHT1);
		glToolkit.setDepthMask(false);
		OpenGL.glNormal3f(0.0F, -1.0F, 0.0F);
	}

	@OriginalMember(owner = "client!sn", name = "a", descriptor = "(Lclient!qo;B)V")
	private void restoreRenderState(@OriginalArg(0) GlToolkit glToolkit) {
		glToolkit.setDepthMask(true);
		OpenGL.glEnable(OpenGLConstants.GL_LIGHT0);
		OpenGL.glEnable(OpenGLConstants.GL_LIGHT1);
		if (GlParticleRenderer.savedSunBrightness != glToolkit.sunBrightness) {
			glToolkit.xa(GlParticleRenderer.savedSunBrightness);
		}
	}

	@OriginalMember(owner = "client!sn", name = "a", descriptor = "(Lclient!qo;BILclient!mda;)V")
	public void drawParticles(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(2) int orthoZoom, @OriginalArg(3) ParticleList particleList) {
		if (glToolkit.viewMatrix43 == null) {
			return;
		}
		if (orthoZoom >= 0) {
			this.setupOrthoRenderState(glToolkit, orthoZoom);
		} else {
			this.setupSceneRenderState(glToolkit);
		}
		@Pc(26) float zA = glToolkit.viewMatrix43.zA;
		@Pc(30) float zB = glToolkit.viewMatrix43.zB;
		@Pc(34) float zC = glToolkit.viewMatrix43.zC;
		@Pc(38) float zD = glToolkit.viewMatrix43.zD;
		try {
			@Pc(40) int depthIndex = 0;
			@Pc(42) int minDepth = Integer.MAX_VALUE;
			@Pc(44) int maxDepth = 0;
			@Pc(48) ParticleNode node2 = particleList.particles.current;
			@Pc(51) ParticleNode node22;
			@Pc(83) int local83;
			for (node22 = node2.next2; node22 != node2; node22 = node22.next2) {
				@Pc(56) PointEntity particle = (PointEntity) node22;
				int depth = (int) (zD + (float) (particle.y >> 12) * zB + (float) (particle.x >> 12) * zA + (float) (particle.z >> 12) * zC);
				if (maxDepth < depth) {
					maxDepth = depth;
				}
				if (minDepth > depth) {
					minDepth = depth;
				}
				this.depths[depthIndex++] = depth;
			}
			@Pc(109) int bucketCount = maxDepth - minDepth;
			int depthShift;
			if (bucketCount + 2 > 1600) {
				depthShift = Utils.bitLength(bucketCount) + 1 - this.bucketCountBits;
				bucketCount = (bucketCount >> depthShift) + 2;
			} else {
				bucketCount += 2;
				depthShift = 0;
			}
			depthIndex = 0;
			node22 = node2.next2;
			@Pc(144) int materialId = -2;
			@Pc(151) boolean lightIntensityAdjustment = true;
			@Pc(153) boolean startNewBatch = true;
			while (node2 != node22) {
				this.overflowBucketCount = 0;
				for (@Pc(160) int i = 0; i < bucketCount; i++) {
					this.bucketSizes[i] = 0;
				}
				for (@Pc(177) int j = 0; j < 64; j++) {
					this.overflowBucketSizes[j] = 0;
				}
				while (node2 != node22) {
					@Pc(195) PointEntity particle2 = (PointEntity) node22;
					if (startNewBatch) {
						lightIntensityAdjustment = particle2.lightIntensityAdjustment;
						materialId = particle2.materialId;
						startNewBatch = false;
					}
					if (depthIndex > 0 && (particle2.materialId != materialId || particle2.lightIntensityAdjustment != lightIntensityAdjustment)) {
						startNewBatch = true;
						break;
					}
					@Pc(237) int bucketIndex = this.depths[depthIndex++] - minDepth >> depthShift;
					if (bucketIndex < 1600) {
						if (this.bucketSizes[bucketIndex] < 64) {
							this.buckets[bucketIndex][this.bucketSizes[bucketIndex]++] = particle2;
						} else {
							label101: {
								if (this.bucketSizes[bucketIndex] == 64) {
									if (this.overflowBucketCount == 64) {
										break label101;
									}
									this.bucketSizes[bucketIndex] += this.overflowBucketCount++ + 1;
								}
								this.overflowBuckets[this.bucketSizes[bucketIndex] - 64 - 1][this.overflowBucketSizes[this.bucketSizes[bucketIndex] - 64 - 1]++] = particle2;
							}
						}
					}
					node22 = node22.next2;
				}
				if (materialId < 0) {
					glToolkit.renderTexture(-1);
				} else {
					glToolkit.renderTexture(materialId);
				}
				if (lightIntensityAdjustment && GlParticleRenderer.savedSunBrightness != glToolkit.sunBrightness) {
					glToolkit.xa(GlParticleRenderer.savedSunBrightness);
				} else if (glToolkit.sunBrightness != 1.0F) {
					glToolkit.xa(1.0F);
				}
				this.draw(glToolkit, bucketCount);
			}
		} catch (@Pc(374) Exception ex) {
		}
		this.restoreRenderState(glToolkit);
	}

	@OriginalMember(owner = "client!sn", name = "a", descriptor = "(ZLclient!qo;)V")
	private void setupSceneRenderState(@OriginalArg(1) GlToolkit glToolkit) {
		GlParticleRenderer.savedSunBrightness = glToolkit.sunBrightness;
		glToolkit.setupMatrixRendering();
		OpenGL.glDisable(16384);
		OpenGL.glDisable(16385);
		glToolkit.setDepthMask(false);
		OpenGL.glNormal3f(0.0F, -1.0F, 0.0F);
	}

	@OriginalMember(owner = "client!sn", name = "a", descriptor = "(ILclient!qo;)V")
	public void initialize(@OriginalArg(1) GlToolkit glToolkit) {
		this.arrayBuffer = glToolkit.createArrayBuffer(true, 24, null, 196584);
		this.texCoordPointer = new OpenGlArrayDrawBuffer(this.arrayBuffer, OpenGLConstants.GL_FLOAT, 2, 0);
		this.vertexPointer = new OpenGlArrayDrawBuffer(this.arrayBuffer, OpenGLConstants.GL_FLOAT, 3, 8);
		this.colourPointer = new OpenGlArrayDrawBuffer(this.arrayBuffer, OpenGLConstants.GL_UNSIGNED_BYTE, 4, 20);
	}

	@OriginalMember(owner = "client!sn", name = "b", descriptor = "(Lclient!qo;II)V")
	private void draw(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) int bucketCount) {
		OpenGL.glGetFloatv(OpenGLConstants.GL_MODELVIEW_MATRIX, this.modelViewMatrix, 0);
		@Pc(19) float xA = this.modelViewMatrix[0];
		@Pc(24) float xB = this.modelViewMatrix[4];
		@Pc(29) float xC = this.modelViewMatrix[8];
		@Pc(34) float yA = this.modelViewMatrix[1];
		@Pc(39) float yB = this.modelViewMatrix[5];
		@Pc(44) float yC = this.modelViewMatrix[9];
		@Pc(48) float scaleXA = yA + xA;
		@Pc(52) float scaleYA = yB + xB;
		@Pc(56) float scaleZA = yC + xC;
		@Pc(60) float scaleXB = xA - yA;
		@Pc(65) float scaleYB = xB - yB;
		@Pc(70) float scaleZB = xC - yC;
		@Pc(75) float scaleXC = yA - xA;
		@Pc(80) float scaleYC = yB - xB;
		this.vertices.currentPosition = 0;
		@Pc(89) float scaleZC = yC - xC;
		@Pc(96) int bucketIndex;
		@Pc(113) int bucketSize;
		@Pc(119) int index;
		@Pc(128) PointEntity particle;
		@Pc(131) int local131;
		@Pc(136) byte red;
		@Pc(141) byte green;
		@Pc(144) byte local144;
		@Pc(149) byte local149;
		@Pc(155) float x;
		@Pc(161) float y;
		@Pc(167) float local167;
		@Pc(172) int size;
		@Pc(497) float overflowZ;
		@Pc(502) int overflowSize;
		@Pc(442) int overflowBucketIndex;
		@Pc(458) PointEntity particle2;
		@Pc(461) int colorArgb;
		@Pc(474) byte overflowBlue;
		@Pc(479) byte overflowAlpha;
		@Pc(491) float overflowY;
		if (glToolkit.bigEndianess) {
			for (bucketIndex = bucketCount - 1; bucketIndex >= 0; bucketIndex--) {
				bucketSize = this.bucketSizes[bucketIndex] > 64 ? 64 : this.bucketSizes[bucketIndex];
				if (bucketSize > 0) {
					for (index = bucketSize - 1; index >= 0; index--) {
						particle = this.buckets[bucketIndex][index];
						int argb = particle.colorArgb;
						red = (byte) (argb >> 16);
						green = (byte) (argb >> 8);
						byte blue = (byte) argb;
						byte alpha = (byte) (argb >>> 24);
						x = particle.x >> 12;
						y = particle.y >> 12;
						float z = particle.z >> 12;
						size = particle.size >> 12;
						this.vertices.writeFloatBigEndian(0.0F);
						this.vertices.writeFloatBigEndian(0.0F);
						this.vertices.writeFloatBigEndian((float) -size * scaleXA + x);
						this.vertices.writeFloatBigEndian(y + scaleYA * (float) -size);
						this.vertices.writeFloatBigEndian(z + scaleZA * (float) -size);
						this.vertices.writeByte(red);
						this.vertices.writeByte(green);
						this.vertices.writeByte(blue);
						this.vertices.writeByte(alpha);
						this.vertices.writeFloatBigEndian(1.0F);
						this.vertices.writeFloatBigEndian(0.0F);
						this.vertices.writeFloatBigEndian((float) size * scaleXB + x);
						this.vertices.writeFloatBigEndian((float) size * scaleYB + y);
						this.vertices.writeFloatBigEndian(z + (float) size * scaleZB);
						this.vertices.writeByte(red);
						this.vertices.writeByte(green);
						this.vertices.writeByte(blue);
						this.vertices.writeByte(alpha);
						this.vertices.writeFloatBigEndian(1.0F);
						this.vertices.writeFloatBigEndian(1.0F);
						this.vertices.writeFloatBigEndian(x + scaleXA * (float) size);
						this.vertices.writeFloatBigEndian(scaleYA * (float) size + y);
						this.vertices.writeFloatBigEndian(z + (float) size * scaleZA);
						this.vertices.writeByte(red);
						this.vertices.writeByte(green);
						this.vertices.writeByte(blue);
						this.vertices.writeByte(alpha);
						this.vertices.writeFloatBigEndian(0.0F);
						this.vertices.writeFloatBigEndian(1.0F);
						this.vertices.writeFloatBigEndian(scaleXC * (float) size + x);
						this.vertices.writeFloatBigEndian(y + scaleYC * (float) size);
						this.vertices.writeFloatBigEndian(scaleZC * (float) size + z);
						this.vertices.writeByte(red);
						this.vertices.writeByte(green);
						this.vertices.writeByte(blue);
						this.vertices.writeByte(alpha);
					}
					if (this.bucketSizes[bucketIndex] > 64) {
						overflowBucketIndex = this.bucketSizes[bucketIndex] - 64 - 1;
						for (int overflowIndex = this.overflowBucketSizes[overflowBucketIndex] - 1; overflowIndex >= 0; overflowIndex--) {
							particle2 = this.overflowBuckets[overflowBucketIndex][overflowIndex];
							colorArgb = particle2.colorArgb;
							byte overflowRed = (byte) (colorArgb >> 16);
							byte overflowGreen = (byte) (colorArgb >> 8);
							overflowBlue = (byte) colorArgb;
							overflowAlpha = (byte) (colorArgb >>> 24);
							float overflowX = particle2.x >> 12;
							overflowY = particle2.y >> 12;
							overflowZ = particle2.z >> 12;
							overflowSize = particle2.size >> 12;
							this.vertices.writeFloatBigEndian(0.0F);
							this.vertices.writeFloatBigEndian(0.0F);
							this.vertices.writeFloatBigEndian((float) -overflowSize * scaleXA + overflowX);
							this.vertices.writeFloatBigEndian(overflowY + scaleYA * (float) -overflowSize);
							this.vertices.writeFloatBigEndian(overflowZ + scaleZA * (float) -overflowSize);
							this.vertices.writeByte(overflowRed);
							this.vertices.writeByte(overflowGreen);
							this.vertices.writeByte(overflowBlue);
							this.vertices.writeByte(overflowAlpha);
							this.vertices.writeFloatBigEndian(1.0F);
							this.vertices.writeFloatBigEndian(0.0F);
							this.vertices.writeFloatBigEndian((float) overflowSize * scaleXB + overflowX);
							this.vertices.writeFloatBigEndian(overflowY + scaleYB * (float) overflowSize);
							this.vertices.writeFloatBigEndian(overflowZ + (float) overflowSize * scaleZB);
							this.vertices.writeByte(overflowRed);
							this.vertices.writeByte(overflowGreen);
							this.vertices.writeByte(overflowBlue);
							this.vertices.writeByte(overflowAlpha);
							this.vertices.writeFloatBigEndian(1.0F);
							this.vertices.writeFloatBigEndian(1.0F);
							this.vertices.writeFloatBigEndian(scaleXA * (float) overflowSize + overflowX);
							this.vertices.writeFloatBigEndian(scaleYA * (float) overflowSize + overflowY);
							this.vertices.writeFloatBigEndian(overflowZ + (float) overflowSize * scaleZA);
							this.vertices.writeByte(overflowRed);
							this.vertices.writeByte(overflowGreen);
							this.vertices.writeByte(overflowBlue);
							this.vertices.writeByte(overflowAlpha);
							this.vertices.writeFloatBigEndian(0.0F);
							this.vertices.writeFloatBigEndian(1.0F);
							this.vertices.writeFloatBigEndian((float) overflowSize * scaleXC + overflowX);
							this.vertices.writeFloatBigEndian((float) overflowSize * scaleYC + overflowY);
							this.vertices.writeFloatBigEndian((float) overflowSize * scaleZC + overflowZ);
							this.vertices.writeByte(overflowRed);
							this.vertices.writeByte(overflowGreen);
							this.vertices.writeByte(overflowBlue);
							this.vertices.writeByte(overflowAlpha);
						}
					}
				}
			}
		} else {
			for (bucketIndex = bucketCount - 1; bucketIndex >= 0; bucketIndex--) {
				bucketSize = this.bucketSizes[bucketIndex] <= 64 ? this.bucketSizes[bucketIndex] : 64;
				if (bucketSize > 0) {
					for (index = bucketSize - 1; index >= 0; index--) {
						particle = this.buckets[bucketIndex][index];
						int argb = particle.colorArgb;
						red = (byte) (argb >> 16);
						green = (byte) (argb >> 8);
						byte blue = (byte) argb;
						byte alpha = (byte) (argb >>> 24);
						x = particle.x >> 12;
						y = particle.y >> 12;
						float z = particle.z >> 12;
						size = particle.size >> 12;
						this.vertices.writeFloatLittleEndian(0.0F);
						this.vertices.writeFloatLittleEndian(0.0F);
						this.vertices.writeFloatLittleEndian((float) -size * scaleXA + x);
						this.vertices.writeFloatLittleEndian((float) -size * scaleYA + y);
						this.vertices.writeFloatLittleEndian(scaleZA * (float) -size + z);
						this.vertices.writeByte(red);
						this.vertices.writeByte(green);
						this.vertices.writeByte(blue);
						this.vertices.writeByte(alpha);
						this.vertices.writeFloatLittleEndian(1.0F);
						this.vertices.writeFloatLittleEndian(0.0F);
						this.vertices.writeFloatLittleEndian(x + scaleXB * (float) size);
						this.vertices.writeFloatLittleEndian(y + scaleYB * (float) size);
						this.vertices.writeFloatLittleEndian(z + scaleZB * (float) size);
						this.vertices.writeByte(red);
						this.vertices.writeByte(green);
						this.vertices.writeByte(blue);
						this.vertices.writeByte(alpha);
						this.vertices.writeFloatLittleEndian(1.0F);
						this.vertices.writeFloatLittleEndian(1.0F);
						this.vertices.writeFloatLittleEndian((float) size * scaleXA + x);
						this.vertices.writeFloatLittleEndian(y + (float) size * scaleYA);
						this.vertices.writeFloatLittleEndian(z + scaleZA * (float) size);
						this.vertices.writeByte(red);
						this.vertices.writeByte(green);
						this.vertices.writeByte(blue);
						this.vertices.writeByte(alpha);
						this.vertices.writeFloatLittleEndian(0.0F);
						this.vertices.writeFloatLittleEndian(1.0F);
						this.vertices.writeFloatLittleEndian(x + scaleXC * (float) size);
						this.vertices.writeFloatLittleEndian((float) size * scaleYC + y);
						this.vertices.writeFloatLittleEndian((float) size * scaleZC + z);
						this.vertices.writeByte(red);
						this.vertices.writeByte(green);
						this.vertices.writeByte(blue);
						this.vertices.writeByte(alpha);
					}
					if (this.bucketSizes[bucketIndex] > 64) {
						overflowBucketIndex = this.bucketSizes[bucketIndex] - 65;
						for (int overflowIndex = this.overflowBucketSizes[overflowBucketIndex] - 1; overflowIndex >= 0; overflowIndex--) {
							particle2 = this.overflowBuckets[overflowBucketIndex][overflowIndex];
							colorArgb = particle2.colorArgb;
							byte overflowRed = (byte) (colorArgb >> 16);
							byte overflowGreen = (byte) (colorArgb >> 8);
							overflowBlue = (byte) colorArgb;
							overflowAlpha = (byte) (colorArgb >>> 24);
							float overflowX = particle2.x >> 12;
							overflowY = particle2.y >> 12;
							overflowZ = particle2.z >> 12;
							overflowSize = particle2.size >> 12;
							this.vertices.writeFloatLittleEndian(0.0F);
							this.vertices.writeFloatLittleEndian(0.0F);
							this.vertices.writeFloatLittleEndian(overflowX + (float) -overflowSize * scaleXA);
							this.vertices.writeFloatLittleEndian(overflowY + scaleYA * (float) -overflowSize);
							this.vertices.writeFloatLittleEndian(overflowZ + scaleZA * (float) -overflowSize);
							this.vertices.writeByte(overflowRed);
							this.vertices.writeByte(overflowGreen);
							this.vertices.writeByte(overflowBlue);
							this.vertices.writeByte(overflowAlpha);
							this.vertices.writeFloatLittleEndian(1.0F);
							this.vertices.writeFloatLittleEndian(0.0F);
							this.vertices.writeFloatLittleEndian(scaleXB * (float) overflowSize + overflowX);
							this.vertices.writeFloatLittleEndian((float) overflowSize * scaleYB + overflowY);
							this.vertices.writeFloatLittleEndian(scaleZB * (float) overflowSize + overflowZ);
							this.vertices.writeByte(overflowRed);
							this.vertices.writeByte(overflowGreen);
							this.vertices.writeByte(overflowBlue);
							this.vertices.writeByte(overflowAlpha);
							this.vertices.writeFloatLittleEndian(1.0F);
							this.vertices.writeFloatLittleEndian(1.0F);
							this.vertices.writeFloatLittleEndian((float) overflowSize * scaleXA + overflowX);
							this.vertices.writeFloatLittleEndian((float) overflowSize * scaleYA + overflowY);
							this.vertices.writeFloatLittleEndian((float) overflowSize * scaleZA + overflowZ);
							this.vertices.writeByte(overflowRed);
							this.vertices.writeByte(overflowGreen);
							this.vertices.writeByte(overflowBlue);
							this.vertices.writeByte(overflowAlpha);
							this.vertices.writeFloatLittleEndian(0.0F);
							this.vertices.writeFloatLittleEndian(1.0F);
							this.vertices.writeFloatLittleEndian((float) overflowSize * scaleXC + overflowX);
							this.vertices.writeFloatLittleEndian((float) overflowSize * scaleYC + overflowY);
							this.vertices.writeFloatLittleEndian((float) overflowSize * scaleZC + overflowZ);
							this.vertices.writeByte(overflowRed);
							this.vertices.writeByte(overflowGreen);
							this.vertices.writeByte(overflowBlue);
							this.vertices.writeByte(overflowAlpha);
						}
					}
				}
			}
		}
		if (this.vertices.currentPosition != 0) {
			this.arrayBuffer.putArray(24, this.vertices.currentPosition, this.vertices.buffer);
			glToolkit.toggleDrawingClientstates(this.vertexPointer, this.colourPointer, this.texCoordPointer, null);
			glToolkit.drawArrays(this.vertices.currentPosition / 24);
		}
	}
	@OriginalMember(owner = "client!lea", name = "b", descriptor = "F")
	public static float savedSunBrightness;
}
