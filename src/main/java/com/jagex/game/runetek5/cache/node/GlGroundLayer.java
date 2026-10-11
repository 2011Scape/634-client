package com.jagex.game.runetek5.cache.node;

import com.jagex.game.runetek5.core.constants.OpenGLConstants;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlBitBuffer;
import com.jagex.game.runetek5.config.material.MaterialType;
import jaclib.memory.Stream;
import jaclib.memory.heap.NativeHeapBuffer;
import jaggl.OpenGL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.buffer.ArrayBufferGL;
import com.jagex.game.runetek5.scene.ground.GlGround;
import com.jagex.game.runetek5.toolkit.opengl.buffer.GlElementsArrayBuffer;
import com.jagex.game.runetek5.toolkit.opengl.buffer.OpenGlArrayDrawBuffer;
import com.jagex.game.runetek5.graphics.gl.GlToolkit;

@OriginalClass("client!jn")
public final class GlGroundLayer extends Node {

	@OriginalMember(owner = "client!jn", name = "D", descriptor = "Lclient!jaclib/memory/heap/NativeHeapBuffer;")
	private NativeHeapBuffer colourHeapBuffer;

	@OriginalMember(owner = "client!jn", name = "I", descriptor = "Lclient!nh;")
	private OpenGlArrayDrawBuffer colourDrawBuffer;

	@OriginalMember(owner = "client!jn", name = "K", descriptor = "Lclient!jaclib/memory/Stream;")
	private Stream colourWriter;

	@OriginalMember(owner = "client!jn", name = "C", descriptor = "Lclient!ed;")
	private final GlGround glGround;

	@OriginalMember(owner = "client!jn", name = "G", descriptor = "[I")
	private final int[] tileTriangleMasks;

	@OriginalMember(owner = "client!jn", name = "o", descriptor = "I")
	public final int waterFogColour;

	@OriginalMember(owner = "client!jn", name = "v", descriptor = "F")
	public final float textureScale;

	@OriginalMember(owner = "client!jn", name = "u", descriptor = "Lclient!qo;")
	private final GlToolkit glToolkit;

	@OriginalMember(owner = "client!jn", name = "F", descriptor = "I")
	public final int materialId;

	@OriginalMember(owner = "client!jn", name = "w", descriptor = "I")
	public final int waterDepth;

	@OriginalMember(owner = "client!jn", name = "p", descriptor = "I")
	public final int waterIntensity;

	@OriginalMember(owner = "client!jn", name = "H", descriptor = "Lclient!wv;")
	private final GlElementsArrayBuffer indexBuffer;

	@OriginalMember(owner = "client!jn", name = "<init>", descriptor = "(Lclient!ed;IIIII)V")
	public GlGroundLayer(@OriginalArg(0) GlGround glGround, @OriginalArg(1) int materialId, @OriginalArg(2) int textureScale, @OriginalArg(3) int waterFogColour, @OriginalArg(4) int waterDepth, @OriginalArg(5) int waterIntensity) {
		this.glGround = glGround;
		this.tileTriangleMasks = new int[this.glGround.width * this.glGround.length];
		this.waterFogColour = waterFogColour;
		this.textureScale = textureScale;
		this.glToolkit = this.glGround.glToolkit;
		this.materialId = materialId;
		this.waterDepth = waterDepth;
		this.waterIntensity = waterIntensity;
		this.indexBuffer = new GlElementsArrayBuffer(this.glToolkit, 5123, null, 1);
	}

	@OriginalMember(owner = "client!jn", name = "a", descriptor = "(IBII)V")
	public void addTriangle(@OriginalArg(0) int tileX, @OriginalArg(2) int tileZ, @OriginalArg(3) int triangle) {
		this.tileTriangleMasks[tileZ * this.glGround.width + tileX] |= 0x1 << triangle;
	}

	@OriginalMember(owner = "client!jn", name = "c", descriptor = "(II)V")
	public void initColours(@OriginalArg(1) int vertexCount) {
		this.colourHeapBuffer = this.glToolkit.nativeHeap.a(vertexCount * 4, true);
		this.colourWriter = new Stream(this.colourHeapBuffer);
	}

	@OriginalMember(owner = "client!jn", name = "a", descriptor = "(II[I)V")
	public void render(@OriginalArg(1) int tileCount, @OriginalArg(2) int[] tileIndices) {
		@Pc(7) int indexCount = 0;
		@Pc(11) GlBitBuffer gpuPacket = this.glToolkit.textureBuffer;
		gpuPacket.currentPosition = 0;
		@Pc(32) short[] vertexIndices;
		@Pc(26) int tileIndex;
		@Pc(37) int mask;
		@Pc(20) int i;
		@Pc(46) int triangle;
		@Pc(48) int pos;
		if (this.glToolkit.bigEndianess) {
			for (i = 0; i < tileCount; i++) {
				tileIndex = tileIndices[i];
				vertexIndices = this.glGround.tileVertexIndices[tileIndex];
				mask = this.tileTriangleMasks[tileIndex];
				if (mask != 0 && vertexIndices != null) {
					triangle = 0;
					pos = 0;
					while (vertexIndices.length > pos) {
						if ((mask & 0x1 << triangle++) == 0) {
							pos += 3;
						} else {
							gpuPacket.writeShort(vertexIndices[pos++] & 0xFFFF);
							indexCount++;
							gpuPacket.writeShort(vertexIndices[pos++] & 0xFFFF);
							indexCount++;
							indexCount++;
							gpuPacket.writeShort(vertexIndices[pos++] & 0xFFFF);
						}
					}
				}
			}
		} else {
			for (i = 0; i < tileCount; i++) {
				tileIndex = tileIndices[i];
				mask = this.tileTriangleMasks[tileIndex];
				vertexIndices = this.glGround.tileVertexIndices[tileIndex];
				if (mask != 0 && vertexIndices != null) {
					triangle = 0;
					pos = 0;
					while (pos < vertexIndices.length) {
						if ((mask & 0x1 << triangle++) == 0) {
							pos += 3;
						} else {
							indexCount++;
							gpuPacket.writeShortLE(vertexIndices[pos++] & 0xFFFF);
							gpuPacket.writeShortLE(vertexIndices[pos++] & 0xFFFF);
							indexCount++;
							indexCount++;
							gpuPacket.writeShortLE(vertexIndices[pos++] & 0xFFFF);
						}
					}
				}
			}
		}
		if (indexCount <= 0) {
			return;
		}
		this.indexBuffer.putElements(gpuPacket.buffer, gpuPacket.currentPosition);
		this.glToolkit.toggleDrawingClientstates(this.glGround.vertexBuffer, this.colourDrawBuffer, this.glGround.textureCoordinatesBuffer, this.glGround.normalsBuffer);
		this.glToolkit.renderTexture((this.glGround.featureFlags & 0x7) != 0, this.materialId, (this.glGround.featureFlags & 0x8) != 0);
		if (this.glToolkit.stillWaterToggled) {
			this.glToolkit.EA(Integer.MAX_VALUE, this.waterFogColour, this.waterDepth, this.waterIntensity);
		}
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE);
		OpenGL.glPushMatrix();
		OpenGL.glScalef(1.0F / this.textureScale, 1.0F / this.textureScale, 1.0F);
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
		this.glToolkit.toggleDrawingClientstates(this.glGround.vertexBuffer, this.colourDrawBuffer, this.glGround.textureCoordinatesBuffer, this.glGround.normalsBuffer);
		this.glToolkit.drawElements(indexCount, this.indexBuffer, 0);
		OpenGL.glMatrixMode(OpenGLConstants.GL_TEXTURE);
		OpenGL.glPopMatrix();
		OpenGL.glMatrixMode(OpenGLConstants.GL_MODELVIEW);
	}

	@OriginalMember(owner = "client!jn", name = "a", descriptor = "(BIIIF)V")
	public void setVertexColour(@OriginalArg(1) int vertex, @OriginalArg(2) int lightness, @OriginalArg(3) int colour, @OriginalArg(4) float brightness) {
		@Pc(23) int local23;
		@Pc(44) int local44;
		if (this.materialId != -1) {
			@Pc(18) MaterialType textureMetrics = this.glToolkit.materialProvider.getMetrics(this.materialId);
			int alpha = textureMetrics.alpha & 0xFF;
			@Pc(66) int local66;
			if (alpha != 0 && textureMetrics.effectType != 4) {
				int lightnessColour;
				if (lightness < 0) {
					lightnessColour = 0;
				} else if (lightness > 127) {
					lightnessColour = 16777215;
				} else {
					lightnessColour = lightness * 131586;
				}
				if (alpha == 256) {
					colour = lightnessColour;
				} else {
					int inverseAlpha = 256 - alpha;
					colour = (inverseAlpha * (colour & 0xFF00FF) + (lightnessColour & 0xFF00FF) * alpha & 0xFF00FF00) + (inverseAlpha * (colour & 0xFF00) + alpha * (lightnessColour & 0xFF00) & 0xFF0000) >> 8;
				}
			}
			int materialBrightness = textureMetrics.brightness & 0xFF;
			if (materialBrightness != 0) {
				materialBrightness += 256;
				@Pc(120) int red = materialBrightness * (colour >> 16 & 0xFF);
				if (red > 65535) {
					red = 65535;
				}
				int green = (colour >> 8 & 0xFF) * materialBrightness;
				if (green > 65535) {
					green = 65535;
				}
				@Pc(148) int blue = (colour & 0xFF) * materialBrightness;
				if (blue > 65535) {
					blue = 65535;
				}
				colour = (green & 0xFF00) + (((red & 0xFA00FF00) << 8) + (blue >> 8));
			}
		}
		this.colourWriter.e(vertex * 4);
		if (brightness != 1.0F) {
			@Pc(190) int red = colour >> 16 & 0xFF;
			int green = colour >> 8 & 0xFF;
			int blue = colour & 0xFF;
			red = (int) ((float) red * brightness);
			if (red < 0) {
				red = 0;
			} else if (red > 255) {
				red = 255;
			}
			green = (int) ((float) green * brightness);
			blue = (int) ((float) blue * brightness);
			if (green < 0) {
				green = 0;
			} else if (green > 255) {
				green = 255;
			}
			if (blue < 0) {
				blue = 0;
			} else if (blue > 255) {
				blue = 255;
			}
			colour = green << 8 | red << 16 | blue;
		}
		this.colourWriter.f((byte) (colour >> 16));
		this.colourWriter.f((byte) (colour >> 8));
		this.colourWriter.f((byte) colour);
	}

	@OriginalMember(owner = "client!jn", name = "d", descriptor = "(II)V")
	public void setVertexOpaque(@OriginalArg(1) int vertex) {
		this.colourWriter.e(vertex * 4 + 3);
		this.colourWriter.f(-1);
	}

	@OriginalMember(owner = "client!jn", name = "e", descriptor = "(II)V")
	public void finishColours(@OriginalArg(1) int vertexCount) {
		this.colourWriter.a();
		@Pc(22) ArrayBufferGL colourArrayBuffer = this.glToolkit.createDataArrayBuffer(this.colourHeapBuffer, 4, vertexCount * 4);
		this.colourDrawBuffer = new OpenGlArrayDrawBuffer(colourArrayBuffer, 5121, 4, 0);
		this.colourWriter = null;
		this.colourHeapBuffer = null;
	}
}
