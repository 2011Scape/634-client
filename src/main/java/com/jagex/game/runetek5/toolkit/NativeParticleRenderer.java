package com.jagex.game.runetek5.toolkit;

import com.jagex.game.runetek5.VertexAttribute;
import com.jagex.game.runetek5.NativeMatrix;
import com.jagex.game.runetek5.ParticleList;
import com.jagex.game.runetek5.graphics.model.particle.ParticleNode;
import jaclib.memory.Buffer;
import jaclib.memory.Stream;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.graphics.model.particle.PointEntity;
import com.jagex.game.runetek5.toolkit.AbstractNativeToolkit;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationComponent;
import com.jagex.game.runetek5.graphics.GpuIndexBuffer;
import com.jagex.game.runetek5.toolkit.companion.NativeStream;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclaration;
import com.jagex.game.runetek5.toolkit.vertexdeclaration.VertexDeclarationAction;
import com.jagex.game.runetek5.PrimitiveType;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!ve")
public final class NativeParticleRenderer {

	@OriginalMember(owner = "client!ve", name = "c", descriptor = "[I")
	private final int[] overflowBucketSizes = new int[64];

	@OriginalMember(owner = "client!ve", name = "b", descriptor = "I")
	private int overflowBucketCount = 0;

	@OriginalMember(owner = "client!ve", name = "h", descriptor = "[[Lclient!jc;")
	private final PointEntity[][] buckets = new PointEntity[1600][64];

	@OriginalMember(owner = "client!ve", name = "g", descriptor = "[I")
	private final int[] depths = new int[8191];

	@OriginalMember(owner = "client!ve", name = "r", descriptor = "[[Lclient!jc;")
	private final PointEntity[][] pointEntities = new PointEntity[64][768];

	@OriginalMember(owner = "client!ve", name = "l", descriptor = "[I")
	private final int[] bucketSizes = new int[1600];

	@OriginalMember(owner = "client!ve", name = "m", descriptor = "Lclient!np;")
	private final VertexDeclaration vertexDeclaration;

	@OriginalMember(owner = "client!ve", name = "n", descriptor = "Lclient!tc;")
	private final NativeStream dynamicVertexStream;

	@OriginalMember(owner = "client!ve", name = "f", descriptor = "Lclient!tc;")
	private final NativeStream staticNormalStream;

	@OriginalMember(owner = "client!ve", name = "k", descriptor = "Lclient!uq;")
	private final GpuIndexBuffer indexBuffer;

	@OriginalMember(owner = "client!ve", name = "<init>", descriptor = "(Lclient!wga;)V")
	public NativeParticleRenderer(@OriginalArg(0) AbstractNativeToolkit nativeToolkit) {
		this.vertexDeclaration = nativeToolkit.createVertexDeclaration(new VertexDeclarationComponent[] { new VertexDeclarationComponent(new VertexAttribute[] { VertexDeclarationAction.POSITION, VertexDeclarationAction.COLOR, VertexDeclarationAction.TEXCOORD }), new VertexDeclarationComponent(VertexDeclarationAction.NORMAL_POINTER_ACTION) });
		this.dynamicVertexStream = nativeToolkit.createStream(true);
		this.staticNormalStream = nativeToolkit.createStream(false);
		this.staticNormalStream.create(393168, 12);
		this.indexBuffer = nativeToolkit.createIndexBuffer(false);
		this.indexBuffer.allocate(49146);
		@Pc(98) Buffer buffer = this.indexBuffer.lock();
		@Pc(115) int local115;
		if (buffer != null) {
			@Pc(105) Stream stream = nativeToolkit.wrapBuffer(buffer);
			@Pc(109) int quad;
			if (Stream.c()) {
				for (quad = 0; quad < 8191; quad++) {
					int baseVertex = quad * 4;
					stream.d(baseVertex);
					stream.d(baseVertex + 1);
					stream.d(baseVertex + 2);
					stream.d(baseVertex + 2);
					stream.d(baseVertex + 3);
					stream.d(baseVertex);
				}
			} else {
				for (quad = 0; quad < 8191; quad++) {
					int baseVertex = quad * 4;
					stream.a(baseVertex);
					stream.a(baseVertex + 1);
					stream.a(baseVertex + 2);
					stream.a(baseVertex + 2);
					stream.a(baseVertex + 3);
					stream.a(baseVertex);
				}
			}
			stream.a();
			this.indexBuffer.unlock();
		}
		@Pc(198) Buffer buffer2 = this.staticNormalStream.lock();
		if (buffer2 != null) {
			@Pc(205) Stream stream2 = nativeToolkit.wrapBuffer(buffer2);
			if (Stream.c()) {
				for (int quadIndex = 0; quadIndex < 8191; quadIndex++) {
					stream2.a(0.0F);
					stream2.a(-1.0F);
					stream2.a(0.0F);
					stream2.a(0.0F);
					stream2.a(-1.0F);
					stream2.a(0.0F);
					stream2.a(0.0F);
					stream2.a(-1.0F);
					stream2.a(0.0F);
					stream2.a(0.0F);
					stream2.a(-1.0F);
					stream2.a(0.0F);
				}
			} else {
				for (int quadIndex = 0; quadIndex < 8191; quadIndex++) {
					stream2.b(0.0F);
					stream2.b(-1.0F);
					stream2.b(0.0F);
					stream2.b(0.0F);
					stream2.b(-1.0F);
					stream2.b(0.0F);
					stream2.b(0.0F);
					stream2.b(-1.0F);
					stream2.b(0.0F);
					stream2.b(0.0F);
					stream2.b(-1.0F);
					stream2.b(0.0F);
				}
			}
			stream2.a();
			this.staticNormalStream.unlock();
		}
	}

	@OriginalMember(owner = "client!ve", name = "a", descriptor = "(Lclient!wga;II)V")
	private void setupOrthoRenderState(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(2) int orthoZoom) {
		NativeParticleRenderer.intensity = nativeToolkit.intensity;
		nativeToolkit.setOrthoZoom((float) orthoZoom);
		nativeToolkit.setupOrthoSceneRenderState();
		nativeToolkit.setSunEnabled(false);
		nativeToolkit.setDepthMaskEnabled(false);
		nativeToolkit.resetModelMatrix();
	}

	@OriginalMember(owner = "client!ve", name = "a", descriptor = "(ILclient!wga;)V")
	private void restoreRenderState(@OriginalArg(1) AbstractNativeToolkit nativeToolkit) {
		nativeToolkit.setDepthMaskEnabled(true);
		nativeToolkit.setSunEnabled(true);
		if (NativeParticleRenderer.intensity != nativeToolkit.intensity) {
			nativeToolkit.xa(NativeParticleRenderer.intensity);
		}
	}

	@OriginalMember(owner = "client!ve", name = "a", descriptor = "(BLclient!wga;)V")
	public void resetVertexStream(@OriginalArg(1) AbstractNativeToolkit nativeToolkit) {
		this.dynamicVertexStream.create(786336, 24);
	}

	@OriginalMember(owner = "client!ve", name = "a", descriptor = "(Lclient!wga;Lclient!mda;II)V")
	public void render(@OriginalArg(0) AbstractNativeToolkit nativeToolkit, @OriginalArg(1) ParticleList particleList, @OriginalArg(2) int orthoZoom) {
		if (nativeToolkit.viewMatrix43 == null) {
			return;
		}
		if (orthoZoom >= 0) {
			this.setupOrthoRenderState(nativeToolkit, orthoZoom);
		} else {
			this.setupSceneRenderState(nativeToolkit);
		}
		@Pc(29) float zA = nativeToolkit.viewMatrix43.zA;
		@Pc(33) float zB = nativeToolkit.viewMatrix43.zB;
		@Pc(37) float zC = nativeToolkit.viewMatrix43.zC;
		@Pc(41) float zD = nativeToolkit.viewMatrix43.zD;
		try {
			@Pc(43) int depthIndex = 0;
			@Pc(45) int minDepth = Integer.MAX_VALUE;
			@Pc(47) int maxDepth = 0;
			@Pc(51) ParticleNode node2 = particleList.particles.current;
			@Pc(54) ParticleNode node22;
			@Pc(86) int local86;
			for (node22 = node2.next2; node22 != node2; node22 = node22.next2) {
				@Pc(59) PointEntity particle = (PointEntity) node22;
				int depth = (int) ((float) (particle.x >> 12) * zA + zB * (float) (particle.y >> 12) + (float) (particle.z >> 12) * zC + zD);
				if (depth > maxDepth) {
					maxDepth = depth;
				}
				this.depths[depthIndex++] = depth;
				if (minDepth > depth) {
					minDepth = depth;
				}
			}
			@Pc(117) int bucketCount = maxDepth - minDepth;
			int depthShift;
			if (bucketCount + 2 > 1600) {
				depthShift = Utils.bitLength(bucketCount) + 1 - NativeParticleRenderer.DEPTH_BUCKET_SHIFT_BITS;
				bucketCount = (bucketCount >> depthShift) + 2;
			} else {
				depthShift = 0;
				bucketCount += 2;
			}
			node22 = node2.next2;
			depthIndex = 0;
			@Pc(151) int materialId = -2;
			@Pc(153) boolean lightIntensityAdjustment = true;
			@Pc(155) boolean startNewBatch = true;
			while (node22 != node2) {
				this.overflowBucketCount = 0;
				for (@Pc(162) int i = 0; i < bucketCount; i++) {
					this.bucketSizes[i] = 0;
				}
				for (@Pc(179) int j = 0; j < 64; j++) {
					this.overflowBucketSizes[j] = 0;
				}
				while (node22 != node2) {
					@Pc(195) PointEntity particle2 = (PointEntity) node22;
					if (startNewBatch) {
						lightIntensityAdjustment = particle2.lightIntensityAdjustment;
						materialId = particle2.materialId;
						startNewBatch = false;
					}
					if (depthIndex > 0 && (materialId != particle2.materialId || lightIntensityAdjustment != particle2.lightIntensityAdjustment)) {
						startNewBatch = true;
						break;
					}
					@Pc(238) int bucketIndex = this.depths[depthIndex++] - minDepth >> depthShift;
					if (bucketIndex < 1600) {
						if (this.bucketSizes[bucketIndex] < 64) {
							this.buckets[bucketIndex][this.bucketSizes[bucketIndex]++] = particle2;
						} else {
							label107: {
								if (this.bucketSizes[bucketIndex] == 64) {
									if (this.overflowBucketCount == 64) {
										break label107;
									}
									this.bucketSizes[bucketIndex] += this.overflowBucketCount++ + 1;
								}
								@Pc(305) PointEntity[] particles = this.pointEntities[this.bucketSizes[bucketIndex] - 65];
								@Pc(313) int overflowBucketIndex = this.bucketSizes[bucketIndex] - 65;
								@Pc(315) int overflowSlot = this.overflowBucketSizes[this.bucketSizes[bucketIndex] - 65];
								this.overflowBucketSizes[overflowBucketIndex] = this.overflowBucketSizes[this.bucketSizes[bucketIndex] - 65] + 1;
								particles[overflowSlot] = particle2;
							}
						}
					}
					node22 = node22.next2;
				}
				nativeToolkit.setActiveMaterial(false, false, materialId >= 0 ? materialId : -1);
				if (lightIntensityAdjustment && NativeParticleRenderer.intensity != nativeToolkit.intensity) {
					nativeToolkit.xa(NativeParticleRenderer.intensity);
				} else if (nativeToolkit.intensity != 1.0F) {
					nativeToolkit.xa(1.0F);
				}
				this.renderBatch(nativeToolkit, bucketCount);
			}
		} catch (@Pc(367) Exception ex) {
		}
		this.restoreRenderState(nativeToolkit);
	}

	@OriginalMember(owner = "client!ve", name = "a", descriptor = "(ZLclient!wga;I)V")
	private void renderBatch(@OriginalArg(1) AbstractNativeToolkit nativeToolkit, @OriginalArg(2) int bucketCount) {
		@Pc(15) int particleCount = 0;
		@Pc(19) NativeMatrix viewMatrix = nativeToolkit.calibrateViewPosition();
		@Pc(22) float xA = viewMatrix.xA;
		@Pc(25) float xB = viewMatrix.xB;
		@Pc(28) float xC = viewMatrix.xC;
		@Pc(31) float yA = viewMatrix.yA;
		@Pc(34) float yB = viewMatrix.yB;
		@Pc(37) float yC = viewMatrix.yC;
		@Pc(41) float scaleXA = yA + xA;
		@Pc(45) float scaleYA = xB + yB;
		@Pc(49) float scaleZA = xC + yC;
		@Pc(53) float scaleXB = xA - yA;
		@Pc(57) float scaleYB = xB - yB;
		@Pc(62) float scaleZB = xC - yC;
		@Pc(66) float scaleXC = yA - xA;
		@Pc(71) float scaleYC = yB - xB;
		@Pc(76) float scaleZC = yC - xC;
		@Pc(82) Buffer buffer = this.dynamicVertexStream.lock();
		if (buffer == null) {
			return;
		}
		@Pc(90) Stream stream = nativeToolkit.wrapBuffer(buffer);
		@Pc(96) int bucketIndex;
		@Pc(111) int bucketSize;
		@Pc(120) int index;
		@Pc(129) PointEntity particle;
		@Pc(132) int local132;
		@Pc(137) byte red;
		@Pc(142) byte green;
		@Pc(145) byte local145;
		@Pc(150) byte local150;
		@Pc(156) float x;
		@Pc(162) float y;
		@Pc(168) float local168;
		@Pc(173) int size;
		@Pc(444) float overflowZ;
		@Pc(449) int overflowSize;
		@Pc(389) int overflowBucketIndex;
		@Pc(405) PointEntity particle2;
		@Pc(408) int colorArgb;
		@Pc(421) byte overflowBlue;
		@Pc(426) byte overflowAlpha;
		@Pc(438) float overflowY;
		if (Stream.c()) {
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
						stream.a(scaleXA * (float) -size + x);
						stream.a(y + scaleYA * (float) -size);
						stream.a(z + (float) -size * scaleZA);
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red, green, blue, alpha);
						} else {
							stream.a(red, green, blue, alpha);
						}
						stream.a(0.0F);
						stream.a(0.0F);
						stream.a(scaleXB * (float) size + x);
						stream.a((float) size * scaleYB + y);
						stream.a(z + scaleZB * (float) size);
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red, green, blue, alpha);
						} else {
							stream.a(red, green, blue, alpha);
						}
						stream.a(1.0F);
						stream.a(0.0F);
						stream.a(x + scaleXA * (float) size);
						stream.a(y + scaleYA * (float) size);
						stream.a(scaleZA * (float) size + z);
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red, green, blue, alpha);
						} else {
							stream.a(red, green, blue, alpha);
						}
						stream.a(1.0F);
						stream.a(1.0F);
						stream.a(x + scaleXC * (float) size);
						stream.a(y + scaleYC * (float) size);
						stream.a(z + scaleZC * (float) size);
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red, green, blue, alpha);
						} else {
							stream.a(red, green, blue, alpha);
						}
						stream.a(0.0F);
						particleCount++;
						stream.a(1.0F);
					}
					if (this.bucketSizes[bucketIndex] > 64) {
						overflowBucketIndex = this.bucketSizes[bucketIndex] - 65;
						for (int overflowIndex = this.overflowBucketSizes[overflowBucketIndex] - 1; overflowIndex >= 0; overflowIndex--) {
							particle2 = this.pointEntities[overflowBucketIndex][overflowIndex];
							colorArgb = particle2.colorArgb;
							byte overflowRed = (byte) (colorArgb >> 16);
							byte overflowGreen = (byte) (colorArgb >> 8);
							overflowBlue = (byte) colorArgb;
							overflowAlpha = (byte) (colorArgb >>> 24);
							float overflowX = particle2.x >> 12;
							overflowY = particle2.y >> 12;
							overflowZ = particle2.z >> 12;
							overflowSize = particle2.size >> 12;
							stream.a(scaleXA * (float) -overflowSize + overflowX);
							stream.a((float) -overflowSize * scaleYA + overflowY);
							stream.a(overflowZ + scaleZA * (float) -overflowSize);
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							} else {
								stream.a(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							}
							stream.a(0.0F);
							stream.a(0.0F);
							stream.a(scaleXB * (float) overflowSize + overflowX);
							stream.a((float) overflowSize * scaleYB + overflowY);
							stream.a((float) overflowSize * scaleZB + overflowZ);
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							} else {
								stream.a(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							}
							stream.a(1.0F);
							stream.a(0.0F);
							stream.a(overflowX + (float) overflowSize * scaleXA);
							stream.a((float) overflowSize * scaleYA + overflowY);
							stream.a((float) overflowSize * scaleZA + overflowZ);
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							} else {
								stream.a(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							}
							stream.a(1.0F);
							stream.a(1.0F);
							stream.a((float) overflowSize * scaleXC + overflowX);
							stream.a((float) overflowSize * scaleYC + overflowY);
							stream.a(overflowZ + (float) overflowSize * scaleZC);
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							} else {
								stream.a(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							}
							stream.a(0.0F);
							particleCount++;
							stream.a(1.0F);
						}
					}
				}
			}
		} else {
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
						stream.b((float) -size * scaleXA + x);
						stream.b(y + (float) -size * scaleYA);
						stream.b((float) -size * scaleZA + z);
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red, green, blue, alpha);
						} else {
							stream.a(red, green, blue, alpha);
						}
						stream.b(0.0F);
						stream.b(0.0F);
						stream.b(x + (float) size * scaleXB);
						stream.b(y + (float) size * scaleYB);
						stream.b(z + (float) size * scaleZB);
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red, green, blue, alpha);
						} else {
							stream.a(red, green, blue, alpha);
						}
						stream.b(1.0F);
						stream.b(0.0F);
						stream.b(x + scaleXA * (float) size);
						stream.b(scaleYA * (float) size + y);
						stream.b(scaleZA * (float) size + z);
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red, green, blue, alpha);
						} else {
							stream.a(red, green, blue, alpha);
						}
						stream.b(1.0F);
						stream.b(1.0F);
						stream.b(scaleXC * (float) size + x);
						stream.b((float) size * scaleYC + y);
						stream.b(z + scaleZC * (float) size);
						if (nativeToolkit.endiannessType == 0) {
							stream.b(red, green, blue, alpha);
						} else {
							stream.a(red, green, blue, alpha);
						}
						stream.b(0.0F);
						particleCount++;
						stream.b(1.0F);
					}
					if (this.bucketSizes[bucketIndex] > 64) {
						overflowBucketIndex = this.bucketSizes[bucketIndex] - 64 - 1;
						for (int overflowIndex = this.overflowBucketSizes[overflowBucketIndex] - 1; overflowIndex >= 0; overflowIndex--) {
							particle2 = this.pointEntities[overflowBucketIndex][overflowIndex];
							colorArgb = particle2.colorArgb;
							byte overflowRed = (byte) (colorArgb >> 16);
							byte overflowGreen = (byte) (colorArgb >> 8);
							overflowBlue = (byte) colorArgb;
							overflowAlpha = (byte) (colorArgb >>> 24);
							float overflowX = particle2.x >> 12;
							overflowY = particle2.y >> 12;
							overflowZ = particle2.z >> 12;
							overflowSize = particle2.size >> 12;
							stream.b(overflowX + (float) -overflowSize * scaleXA);
							stream.b((float) -overflowSize * scaleYA + overflowY);
							stream.b(overflowZ + scaleZA * (float) -overflowSize);
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							} else {
								stream.a(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							}
							stream.b(0.0F);
							stream.b(0.0F);
							stream.b(overflowX + (float) overflowSize * scaleXB);
							stream.b(scaleYB * (float) overflowSize + overflowY);
							stream.b(scaleZB * (float) overflowSize + overflowZ);
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							} else {
								stream.a(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							}
							stream.b(1.0F);
							stream.b(0.0F);
							stream.b(overflowX + (float) overflowSize * scaleXA);
							stream.b((float) overflowSize * scaleYA + overflowY);
							stream.b(scaleZA * (float) overflowSize + overflowZ);
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							} else {
								stream.a(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							}
							stream.b(1.0F);
							stream.b(1.0F);
							stream.b(overflowX + scaleXC * (float) overflowSize);
							stream.b(overflowY + (float) overflowSize * scaleYC);
							stream.b((float) overflowSize * scaleZC + overflowZ);
							if (nativeToolkit.endiannessType == 0) {
								stream.b(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							} else {
								stream.a(overflowRed, overflowGreen, overflowBlue, overflowAlpha);
							}
							stream.b(0.0F);
							stream.b(1.0F);
							particleCount++;
						}
					}
				}
			}
		}
		stream.a();
		if (this.dynamicVertexStream.unlock()) {
			nativeToolkit.setStreamSource(this.dynamicVertexStream, 0);
			nativeToolkit.setStreamSource(this.staticNormalStream, 1);
			nativeToolkit.setVertexDeclaration(this.vertexDeclaration);
			nativeToolkit.drawIndexedPrimitive(PrimitiveType.aClass134_6, particleCount * 2, this.indexBuffer, 0, 0, particleCount * 4);
		}
	}

	@OriginalMember(owner = "client!ve", name = "b", descriptor = "(BLclient!wga;)V")
	private void setupSceneRenderState(@OriginalArg(1) AbstractNativeToolkit nativeToolkit) {
		NativeParticleRenderer.intensity = nativeToolkit.intensity;
		nativeToolkit.setupSceneRenderState();
		nativeToolkit.setSunEnabled(false);
		nativeToolkit.setDepthMaskEnabled(false);
		nativeToolkit.resetModelMatrix();
	}

	@OriginalMember(owner = "client!ve", name = "b", descriptor = "(I)V")
	public void release() {
		this.dynamicVertexStream.deallocate();
	}
	@OriginalMember(owner = "client!pha", name = "i", descriptor = "I")
	public static final int DEPTH_BUCKET_SHIFT_BITS = Utils.bitLength(1600);

	@OriginalMember(owner = "client!rga", name = "M", descriptor = "F")
	public static float intensity;
}
