package com.jagex.game.runetek5.graphics.light;

import com.jagex.game.runetek5.cache.node.impl.ShortNode;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer;
import com.jagex.game.runetek5.cache.node.PointLight;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.buffer.ArrayBufferGL;
import com.jagex.game.runetek5.buffer.ElementsBufferGL;
import com.jagex.game.runetek5.scene.ground.GlGround;
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!qw")
public final class LightGL extends Node {

	@OriginalMember(owner = "client!qw", name = "C", descriptor = "I")
	private int vertexCount;

	@OriginalMember(owner = "client!qw", name = "q", descriptor = "Lclient!ed;")
	private final GlGround ground;

	@OriginalMember(owner = "client!qw", name = "N", descriptor = "Lclient!qo;")
	private final GlToolkit toolkit;

	@OriginalMember(owner = "client!qw", name = "y", descriptor = "Lclient!ab;")
	private final PointLight pointLight;

	@OriginalMember(owner = "client!qw", name = "w", descriptor = "I")
	private final int minTileX;

	@OriginalMember(owner = "client!qw", name = "E", descriptor = "I")
	private final int maxTileX;

	@OriginalMember(owner = "client!qw", name = "K", descriptor = "I")
	private final int minTileZ;

	@OriginalMember(owner = "client!qw", name = "G", descriptor = "I")
	private final int maxTileZ;

	@OriginalMember(owner = "client!qw", name = "p", descriptor = "[[F")
	private float[][] tileNormalsZ;

	@OriginalMember(owner = "client!qw", name = "L", descriptor = "[[F")
	private float[][] tileNormalsX;

	@OriginalMember(owner = "client!qw", name = "J", descriptor = "[[F")
	private float[][] tileNormalsY;

	@OriginalMember(owner = "client!qw", name = "v", descriptor = "I")
	private int totalIndexCount;

	@OriginalMember(owner = "client!qw", name = "x", descriptor = "Lclient!cea;")
	private Packet indexPacket;

	@OriginalMember(owner = "client!qw", name = "P", descriptor = "Lclient!gh;")
	private GlBitBuffer bitBuffer;

	@OriginalMember(owner = "client!qw", name = "t", descriptor = "Lclient!eq;")
	private IterableHashTable vertexHashTable;

	@OriginalMember(owner = "client!qw", name = "H", descriptor = "Lclient!ni;")
	private final ElementsBufferGL indexBuffer;

	@OriginalMember(owner = "client!qw", name = "O", descriptor = "Lclient!hca;")
	private final ArrayBufferGL vertexBuffer;

	@OriginalMember(owner = "client!qw", name = "o", descriptor = "Lclient!nh;")
	private final OpenGlArrayDrawBuffer positionDrawBuffer;

	@OriginalMember(owner = "client!qw", name = "F", descriptor = "Lclient!nh;")
	private final OpenGlArrayDrawBuffer colorDrawBuffer;

	@OriginalMember(owner = "client!qw", name = "<init>", descriptor = "(Lclient!qo;Lclient!ed;Lclient!ab;[I)V")
	public LightGL(@OriginalArg(0) GlToolkit glToolkit, @OriginalArg(1) GlGround glGround, @OriginalArg(2) PointLight pointLight, @OriginalArg(3) int[] tileMask) {
		this.ground = glGround;
		this.toolkit = glToolkit;
		this.pointLight = pointLight;
		@Pc(23) int lightRadius = this.pointLight.getRadius() - (glGround.tileUnits >> 1);
		this.minTileX = this.pointLight.getX() - lightRadius >> glGround.tileScale;
		this.maxTileX = this.pointLight.getX() + lightRadius >> glGround.tileScale;
		this.minTileZ = this.pointLight.getZ() - lightRadius >> glGround.tileScale;
		this.maxTileZ = this.pointLight.getZ() + lightRadius >> glGround.tileScale;
		@Pc(79) int tileRangeX = this.maxTileX + 1 - this.minTileX;
		@Pc(88) int tileRangeZ = this.maxTileZ + 1 - this.minTileZ;
		this.tileNormalsZ = new float[tileRangeX + 1][tileRangeZ + 1];
		this.tileNormalsX = new float[tileRangeX + 1][tileRangeZ + 1];
		this.tileNormalsY = new float[tileRangeX + 1][tileRangeZ + 1];
		@Pc(125) int local125;
		@Pc(139) int minTileZ;
		@Pc(147) int local147;
		@Pc(175) int local175;
		@Pc(191) int local191;
		for (@Pc(117) int i = 0; i <= tileRangeZ; i++) {
			int tileZ = i + this.minTileZ;
			if (tileZ > 0 && tileZ < this.ground.length - 1) {
				for (minTileZ = 0; minTileZ <= tileRangeX; minTileZ++) {
					int tileX = minTileZ + this.minTileX;
					if (tileX > 0 && tileX < this.ground.width - 1) {
						int heightDiffX = glGround.getHeight(tileZ, tileX + 1) - glGround.getHeight(tileZ, tileX - 1);
						int heightDiffZ = glGround.getHeight(tileZ + 1, tileX) - glGround.getHeight(tileZ - 1, tileX);
						@Pc(207) float invMagnitude = (float) (1.0D / Math.sqrt((double) (heightDiffX * heightDiffX + heightDiffZ * heightDiffZ + 65536)));
						this.tileNormalsX[minTileZ][i] = (float) heightDiffX * invMagnitude;
						this.tileNormalsY[minTileZ][i] = -256.0F * invMagnitude;
						this.tileNormalsZ[minTileZ][i] = invMagnitude * (float) heightDiffZ;
					}
				}
			}
		}
		int maskIndex = 0;
		@Pc(317) int minTileX;
		for (minTileZ = this.minTileZ; minTileZ <= this.maxTileZ; minTileZ++) {
			if (minTileZ >= 0 && minTileZ < glGround.length) {
				for (int tileX = this.minTileX; tileX <= this.maxTileX; tileX++) {
					if (tileX >= 0 && glGround.width > tileX) {
						int tileMaskType = tileMask[maskIndex];
						@Pc(300) int[] tileColours = glGround.tileColours[tileX][minTileZ];
						if (tileColours != null && tileMaskType != 0) {
							if (tileMaskType == 1) {
								minTileX = 0;
								while (tileColours.length > minTileX) {
									if (tileColours[minTileX++] != -1 && tileColours[minTileX++] != -1 && tileColours[minTileX++] != -1) {
										this.totalIndexCount += 3;
									}
								}
							} else {
								this.totalIndexCount += 3;
							}
						}
					}
					maskIndex++;
				}
			} else {
				maskIndex += this.maxTileX - this.minTileX;
			}
		}
		if (this.totalIndexCount > 0) {
			this.indexPacket = new Packet(this.totalIndexCount * 2);
			this.bitBuffer = new GlBitBuffer(this.totalIndexCount * 16);
			this.vertexHashTable = new IterableHashTable(Utils.nextPowerOfTwo(this.totalIndexCount));
			int localTileZ = 0;
			maskIndex = 0;
			for (int tileZ = this.minTileZ; tileZ <= this.maxTileZ; tileZ++) {
				if (tileZ >= 0 && tileZ < glGround.length) {
					int localTileX = 0;
					for (minTileX = this.minTileX; minTileX <= this.maxTileX; minTileX++) {
						if (minTileX >= 0 && glGround.width > minTileX) {
							@Pc(440) int maskType = tileMask[maskIndex];
							@Pc(447) int[] tileColours = glGround.tileColours[minTileX][tileZ];
							if (tileColours != null && maskType != 0) {
								if (maskType == 1) {
									@Pc(605) int[] tileOffsetsX = glGround.tileOffsetsX[minTileX][tileZ];
									@Pc(612) int[] tileOffsetsZ = glGround.tileOffsetsZ[minTileX][tileZ];
									@Pc(614) int index = 0;
									label105: while (true) {
										while (true) {
											if (index >= tileColours.length) {
												break label105;
											}
											if (tileColours[index] == -1 || tileColours[index + 1] == -1 || tileColours[index + 2] == -1) {
												index += 3;
											} else {
												this.addVertex(tileOffsetsZ[index], localTileX, localTileZ, minTileX, tileOffsetsX[index], tileZ);
												index++;
												this.addVertex(tileOffsetsZ[index], localTileX, localTileZ, minTileX, tileOffsetsX[index], tileZ);
												index++;
												this.addVertex(tileOffsetsZ[index], localTileX, localTileZ, minTileX, tileOffsetsX[index], tileZ);
												index++;
											}
										}
									}
								} else if (maskType == 3) {
									this.addVertex(0, localTileX, localTileZ, minTileX, 0, tileZ);
									this.addVertex(0, localTileX, localTileZ, minTileX, glGround.tileUnits, tileZ);
									this.addVertex(glGround.tileUnits, localTileX, localTileZ, minTileX, 0, tileZ);
								} else if (maskType == 2) {
									this.addVertex(0, localTileX, localTileZ, minTileX, glGround.tileUnits, tileZ);
									this.addVertex(glGround.tileUnits, localTileX, localTileZ, minTileX, glGround.tileUnits, tileZ);
									this.addVertex(0, localTileX, localTileZ, minTileX, 0, tileZ);
								} else if (maskType == 5) {
									this.addVertex(glGround.tileUnits, localTileX, localTileZ, minTileX, glGround.tileUnits, tileZ);
									this.addVertex(glGround.tileUnits, localTileX, localTileZ, minTileX, 0, tileZ);
									this.addVertex(0, localTileX, localTileZ, minTileX, glGround.tileUnits, tileZ);
								} else if (maskType == 4) {
									this.addVertex(glGround.tileUnits, localTileX, localTileZ, minTileX, 0, tileZ);
									this.addVertex(0, localTileX, localTileZ, minTileX, 0, tileZ);
									this.addVertex(glGround.tileUnits, localTileX, localTileZ, minTileX, glGround.tileUnits, tileZ);
								}
							}
						}
						localTileX++;
						maskIndex++;
					}
				} else {
					maskIndex += this.maxTileX - this.minTileX;
				}
				localTileZ++;
			}
			this.indexBuffer = this.toolkit.createElementsArrayBuffer(this.indexPacket.currentPosition, this.indexPacket.buffer, false);
			this.vertexBuffer = this.toolkit.createArrayBuffer(false, 16, this.bitBuffer.buffer, this.bitBuffer.currentPosition);
			this.positionDrawBuffer = new OpenGlArrayDrawBuffer(this.vertexBuffer, 5126, 3, 0);
			this.colorDrawBuffer = new OpenGlArrayDrawBuffer(this.vertexBuffer, 5121, 4, 12);
		} else {
			this.vertexBuffer = null;
			this.indexBuffer = null;
			this.positionDrawBuffer = null;
			this.colorDrawBuffer = null;
		}
		this.bitBuffer = null;
		this.tileNormalsX = this.tileNormalsY = this.tileNormalsZ = null;
		this.indexPacket = null;
		this.vertexHashTable = null;
	}

	@OriginalMember(owner = "client!qw", name = "a", descriptor = "(IIIIIBI)V")
	private void addVertex(@OriginalArg(0) int tileUnits, @OriginalArg(1) int localTileX, @OriginalArg(2) int localTileZ, @OriginalArg(3) int tileX, @OriginalArg(4) int offsetX, @OriginalArg(6) int tileZ) {
		@Pc(7) long hashKey = -1L;
		@Pc(16) int sceneX = offsetX + (tileX << this.ground.tileScale);
		@Pc(24) int sceneZ = tileUnits + (tileZ << this.ground.tileScale);
		@Pc(31) int sceneY = this.ground.averageHeight(sceneX, sceneZ);
		if ((offsetX & 0x7F) == 0 || (tileUnits & 0x7F) == 0) {
			hashKey = (long) sceneZ << 16 & 0xFFFFL << 16 | (long) sceneX & 0xFFFFL;
			@Pc(62) Node node = this.vertexHashTable.get(hashKey);
			if (node != null) {
				this.writeIndex(((ShortNode) node).value);
				return;
			}
		}
		@Pc(80) short vertexIndex = (short) this.vertexCount++;
		if (hashKey != -1L) {
			this.vertexHashTable.put(hashKey, new ShortNode(vertexIndex));
		}
		@Pc(138) float normalX;
		@Pc(129) float normalY;
		@Pc(120) float normalZ;
		@Pc(165) float local165;
		@Pc(173) float local173;
		@Pc(180) float local180;
		@Pc(187) float local187;
		@Pc(194) float local194;
		@Pc(203) float local203;
		@Pc(212) float local212;
		@Pc(270) float local270;
		if (offsetX == 0 && tileUnits == 0) {
			normalX = this.tileNormalsX[localTileX][localTileZ];
			normalZ = this.tileNormalsZ[localTileX][localTileZ];
			normalY = this.tileNormalsY[localTileX][localTileZ];
		} else if (offsetX == this.ground.tileUnits && tileUnits == 0) {
			normalZ = this.tileNormalsZ[localTileX + 1][localTileZ];
			normalY = this.tileNormalsY[localTileX + 1][localTileZ];
			normalX = this.tileNormalsX[localTileX + 1][localTileZ];
		} else if (offsetX == this.ground.tileUnits && this.ground.tileUnits == tileUnits) {
			normalX = this.tileNormalsX[localTileX + 1][localTileZ + 1];
			normalZ = this.tileNormalsZ[localTileX + 1][localTileZ + 1];
			normalY = this.tileNormalsY[localTileX + 1][localTileZ + 1];
		} else if (offsetX == 0 && this.ground.tileUnits == tileUnits) {
			normalX = this.tileNormalsX[localTileX][localTileZ + 1];
			normalZ = this.tileNormalsZ[localTileX][localTileZ + 1];
			normalY = this.tileNormalsY[localTileX][localTileZ + 1];
		} else {
			float fracX = (float) offsetX / (float) this.ground.tileUnits;
			float fracZ = (float) tileUnits / (float) this.ground.tileUnits;
			float normalX00 = this.tileNormalsX[localTileX][localTileZ];
			float normalY00 = this.tileNormalsY[localTileX][localTileZ];
			float normalZ00 = this.tileNormalsZ[localTileX][localTileZ];
			float normalX10 = this.tileNormalsX[localTileX + 1][localTileZ];
			float normalY10 = this.tileNormalsY[localTileX + 1][localTileZ];
			@Pc(229) float rightNormalY = normalY10 + (this.tileNormalsY[localTileX + 1][localTileZ + 1] - normalY10) * fracX;
			@Pc(246) float rightNormalX = normalX10 + fracX * (this.tileNormalsX[localTileX + 1][localTileZ + 1] - normalX10);
			@Pc(261) float leftNormalZ = normalZ00 + (this.tileNormalsZ[localTileX][localTileZ + 1] - normalZ00) * fracX;
			float normalZ10 = this.tileNormalsZ[localTileX + 1][localTileZ];
			@Pc(285) float leftNormalY = normalY00 + fracX * (this.tileNormalsY[localTileX][localTileZ + 1] - normalY00);
			@Pc(301) float leftNormalX = normalX00 + fracX * (this.tileNormalsX[localTileX][localTileZ + 1] - normalX00);
			normalX = leftNormalX + (rightNormalX - leftNormalX) * fracZ;
			normalY = leftNormalY + fracZ * (rightNormalY - leftNormalY);
			@Pc(336) float rightNormalZ = normalZ10 + fracX * (this.tileNormalsZ[localTileX + 1][localTileZ + 1] - normalZ10);
			normalZ = leftNormalZ + fracZ * (rightNormalZ - leftNormalZ);
		}
		float dirX = this.pointLight.getX() - sceneX;
		float dirY = this.pointLight.getY() - sceneY;
		float dirZ = this.pointLight.getZ() - sceneZ;
		float distance = (float) Math.sqrt((double) (dirY * dirY + dirX * dirX + dirZ * dirZ));
		float invDistance = 1.0F / distance;
		dirZ *= invDistance;
		dirX *= invDistance;
		dirY *= invDistance;
		float distanceRatio = distance / (float) this.pointLight.getRadius();
		float attenuation = 1.0F - distanceRatio * distanceRatio;
		if (attenuation < 0.0F) {
			attenuation = 0.0F;
		}
		float diffuse = dirZ * normalZ + dirY * normalY + normalX * dirX;
		if (diffuse < 0.0F) {
			diffuse = 0.0F;
		}
		@Pc(534) float lightFactor = attenuation * 2.0F * diffuse;
		if (lightFactor > 1.0F) {
			lightFactor = 1.0F;
		}
		@Pc(553) int range = this.pointLight.getColour();
		@Pc(563) int red = (int) ((float) (range >> 16 & 0xFF) * lightFactor);
		if (red > 255) {
			red = 255;
		}
		@Pc(580) int green = (int) ((float) (range >> 8 & 0xFF) * lightFactor);
		if (green > 255) {
			green = 255;
		}
		@Pc(595) int blue = (int) (lightFactor * (float) (range & 0xFF));
		if (this.toolkit.bigEndianess) {
			this.bitBuffer.writeFloatBigEndian((float) sceneX);
			this.bitBuffer.writeFloatBigEndian((float) sceneY);
			this.bitBuffer.writeFloatBigEndian((float) sceneZ);
		} else {
			this.bitBuffer.writeFloatLittleEndian((float) sceneX);
			this.bitBuffer.writeFloatLittleEndian((float) sceneY);
			this.bitBuffer.writeFloatLittleEndian((float) sceneZ);
		}
		if (blue > 255) {
			blue = 255;
		}
		this.bitBuffer.writeByte(red);
		this.bitBuffer.writeByte(green);
		this.bitBuffer.writeByte(blue);
		this.bitBuffer.writeByte(255);
		this.writeIndex(vertexIndex);
	}

	@OriginalMember(owner = "client!qw", name = "a", descriptor = "(BS)V")
	private void writeIndex(@OriginalArg(1) short index) {
		if (this.toolkit.bigEndianess) {
			this.indexPacket.writeShort(index);
		} else {
			this.indexPacket.writeShortLE(index);
		}
	}

	@OriginalMember(owner = "client!qw", name = "a", descriptor = "([[ZIIII)V")
	public void drawLightMesh(@OriginalArg(0) boolean[][] visibility, @OriginalArg(2) int x, @OriginalArg(3) int z, @OriginalArg(4) int radius) {
		if (this.indexBuffer == null || (radius + x < this.minTileX || (x - radius > this.maxTileX || (z + radius < this.minTileZ || this.maxTileZ < z - radius)))) {
			return;
		}
		for (@Pc(50) int minTileZ = this.minTileZ; minTileZ <= this.maxTileZ; minTileZ++) {
			for (@Pc(55) int minTileX = this.minTileX; minTileX <= this.maxTileX; minTileX++) {
				@Pc(61) int relativeX = minTileX - x;
				@Pc(66) int relativeZ = minTileZ - z;
				if (relativeX > -radius && radius > relativeX && -radius < relativeZ && relativeZ < radius && visibility[relativeX + radius][radius + relativeZ]) {
					this.toolkit.setTextureEnvColour((int) (this.pointLight.getIntensity() * 255.0F) << 24);
					this.toolkit.toggleDrawingClientstates(this.positionDrawBuffer, this.colorDrawBuffer, null, null);
					this.toolkit.drawElements(this.totalIndexCount, this.indexBuffer, 0);
					return;
				}
			}
		}
	}
}
