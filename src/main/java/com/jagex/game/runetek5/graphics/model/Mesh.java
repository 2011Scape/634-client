package com.jagex.game.runetek5.graphics.model;

import com.jagex.game.runetek5.TextureCombineSource;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.graphics.model.normal.MeshBillboard;
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex;
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;

@OriginalClass("client!bh")
public final class Mesh {

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "[I")
	public int[] texturesScaleY;

	@OriginalMember(owner = "client!bh", name = "e", descriptor = "[B")
	public byte[] facesTexture;

	@OriginalMember(owner = "client!bh", name = "g", descriptor = "[S")
	public short[] facesMaterial;

	@OriginalMember(owner = "client!bh", name = "h", descriptor = "[B")
	public byte[] texturesMapping;

	@OriginalMember(owner = "client!bh", name = "i", descriptor = "[I")
	public int[] faceGroups;

	@OriginalMember(owner = "client!bh", name = "j", descriptor = "[S")
	public short[] textureFaceZ;

	@OriginalMember(owner = "client!bh", name = "n", descriptor = "[S")
	public short[] textureFaceX;

	@OriginalMember(owner = "client!bh", name = "q", descriptor = "[Lclient!pd;")
	public MeshBillboard[] billboards;

	@OriginalMember(owner = "client!bh", name = "r", descriptor = "[B")
	public byte[] texturesRotation;

	@OriginalMember(owner = "client!bh", name = "s", descriptor = "[B")
	public byte[] facesAlpha;

	@OriginalMember(owner = "client!bh", name = "t", descriptor = "[S")
	public short[] facesB;

	@OriginalMember(owner = "client!bh", name = "x", descriptor = "[B")
	public byte[] facesLayer;

	@OriginalMember(owner = "client!bh", name = "z", descriptor = "[I")
	public int[] verticesX;

	@OriginalMember(owner = "client!bh", name = "A", descriptor = "[S")
	public short[] verticesSubmeshes;

	@OriginalMember(owner = "client!bh", name = "B", descriptor = "[B")
	public byte[] faceRenderType;

	@OriginalMember(owner = "client!bh", name = "C", descriptor = "[I")
	public int[] texturesScaleZ;

	@OriginalMember(owner = "client!bh", name = "E", descriptor = "[Lclient!nn;")
	public EmissiveTriangle[] particleEmitters;

	@OriginalMember(owner = "client!bh", name = "F", descriptor = "[I")
	public int[] verticesY;

	@OriginalMember(owner = "client!bh", name = "I", descriptor = "[S")
	public short[] textureFaceY;

	@OriginalMember(owner = "client!bh", name = "L", descriptor = "[I")
	public int[] verticesZ;

	@OriginalMember(owner = "client!bh", name = "M", descriptor = "[B")
	public byte[] texturesDirection;

	@OriginalMember(owner = "client!bh", name = "O", descriptor = "[S")
	public short[] facesC;

	@OriginalMember(owner = "client!bh", name = "P", descriptor = "[S")
	public short[] facesSubmeshes;

	@OriginalMember(owner = "client!bh", name = "Q", descriptor = "[I")
	public int[] texturesUtrans;

	@OriginalMember(owner = "client!bh", name = "S", descriptor = "[I")
	public int[] texturesScaleX;

	@OriginalMember(owner = "client!bh", name = "V", descriptor = "[S")
	public short[] facesColor;

	@OriginalMember(owner = "client!bh", name = "W", descriptor = "[S")
	public short[] facesA;

	@OriginalMember(owner = "client!bh", name = "Y", descriptor = "[I")
	public int[] texturesVtrans;

	@OriginalMember(owner = "client!bh", name = "Z", descriptor = "[Lclient!eca;")
	public EffectiveVertex[] particleEffectors;

	@OriginalMember(owner = "client!bh", name = "ab", descriptor = "[I")
	public int[] texturesSpeed;

	@OriginalMember(owner = "client!bh", name = "bb", descriptor = "[I")
	public int[] vertexGroups;

	@OriginalMember(owner = "client!bh", name = "o", descriptor = "I")
	public int version = 12;

	@OriginalMember(owner = "client!bh", name = "u", descriptor = "I")
	public int numVertices = 0;

	@OriginalMember(owner = "client!bh", name = "b", descriptor = "I")
	public int numFaces = 0;

	@OriginalMember(owner = "client!bh", name = "K", descriptor = "B")
	public byte priority = 0;

	@OriginalMember(owner = "client!bh", name = "f", descriptor = "I")
	public int maxDepth = 0;

	@OriginalMember(owner = "client!bh", name = "c", descriptor = "I")
	public int numTextures = 0;

	@OriginalMember(owner = "client!bh", name = "<init>", descriptor = "()V")
	private Mesh() {
	}

	@OriginalMember(owner = "client!bh", name = "<init>", descriptor = "([B)V")
	public Mesh(@OriginalArg(0) byte[] data) {
		if (data[data.length - 1] == -1 && data[data.length - 2] == -1) {
			this.decodeNewFormat(data);
		} else {
			this.decodeOldFormat(data);
		}
	}

	@OriginalMember(owner = "client!bh", name = "<init>", descriptor = "(III)V")
	public Mesh(@OriginalArg(0) int verticeCount, @OriginalArg(1) int faceCount, @OriginalArg(2) int textureCount) {
		this.facesTexture = new byte[faceCount];
		this.facesA = new short[faceCount];
		this.facesColor = new short[faceCount];
		this.vertexGroups = new int[verticeCount];
		this.faceGroups = new int[faceCount];
		this.verticesZ = new int[verticeCount];
		if (textureCount > 0) {
			this.texturesUtrans = new int[textureCount];
			this.texturesSpeed = new int[textureCount];
			this.textureFaceY = new short[textureCount];
			this.texturesScaleZ = new int[textureCount];
			this.textureFaceZ = new short[textureCount];
			this.texturesVtrans = new int[textureCount];
			this.texturesRotation = new byte[textureCount];
			this.texturesScaleX = new int[textureCount];
			this.textureFaceX = new short[textureCount];
			this.texturesMapping = new byte[textureCount];
			this.texturesScaleY = new int[textureCount];
			this.texturesDirection = new byte[textureCount];
		}
		this.facesMaterial = new short[faceCount];
		this.facesLayer = new byte[faceCount];
		this.facesC = new short[faceCount];
		this.facesB = new short[faceCount];
		this.verticesX = new int[verticeCount];
		this.faceRenderType = new byte[faceCount];
		this.verticesY = new int[verticeCount];
		this.facesAlpha = new byte[faceCount];
	}

	@OriginalMember(owner = "client!bh", name = "<init>", descriptor = "([Lclient!bh;I)V")
	public Mesh(@OriginalArg(0) Mesh[] meshes, @OriginalArg(1) int meshCount) {
		this.numTextures = 0;
		this.numFaces = 0;
		this.numVertices = 0;
		@Pc(32) int emitterCount = 0;
		@Pc(34) int effectorCount = 0;
		@Pc(36) int billboardCount = 0;
		@Pc(38) boolean hasRenderType = false;
		@Pc(40) boolean hasPriorities = false;
		@Pc(42) boolean hasAlpha = false;
		@Pc(44) boolean hasFaceTextures = false;
		@Pc(46) boolean hasFaceMaterials = false;
		this.priority = -1;
		@Pc(51) boolean hasFaceGroups = false;
		for (@Pc(53) int i = 0; i < meshCount; i++) {
			@Pc(59) Mesh mesh = meshes[i];
			if (mesh != null) {
				this.numFaces += mesh.numFaces;
				this.numVertices += mesh.numVertices;
				this.numTextures += mesh.numTextures;
				hasRenderType |= mesh.faceRenderType != null;
				if (mesh.billboards != null) {
					billboardCount += mesh.billboards.length;
				}
				if (mesh.particleEmitters != null) {
					emitterCount += mesh.particleEmitters.length;
				}
				if (mesh.particleEffectors != null) {
					effectorCount += mesh.particleEffectors.length;
				}
				hasFaceMaterials |= mesh.facesMaterial != null;
				hasFaceTextures |= mesh.facesTexture != null;
				if (mesh.facesLayer == null) {
					if (this.priority == -1) {
						this.priority = mesh.priority;
					}
					if (mesh.priority != this.priority) {
						hasPriorities = true;
					}
				} else {
					hasPriorities = true;
				}
				hasAlpha |= mesh.facesAlpha != null;
				hasFaceGroups |= mesh.faceGroups != null;
			}
		}
		this.facesB = new short[this.numFaces];
		if (this.numTextures > 0) {
			this.textureFaceX = new short[this.numTextures];
			this.texturesMapping = new byte[this.numTextures];
			this.texturesSpeed = new int[this.numTextures];
			this.texturesScaleZ = new int[this.numTextures];
			this.texturesRotation = new byte[this.numTextures];
			this.texturesUtrans = new int[this.numTextures];
			this.texturesVtrans = new int[this.numTextures];
			this.texturesScaleX = new int[this.numTextures];
			this.texturesDirection = new byte[this.numTextures];
			this.textureFaceY = new short[this.numTextures];
			this.texturesScaleY = new int[this.numTextures];
			this.textureFaceZ = new short[this.numTextures];
		}
		this.facesSubmeshes = new short[this.numFaces];
		if (hasFaceMaterials) {
			this.facesMaterial = new short[this.numFaces];
		}
		this.facesA = new short[this.numFaces];
		if (hasFaceGroups) {
			this.faceGroups = new int[this.numFaces];
		}
		if (hasPriorities) {
			this.facesLayer = new byte[this.numFaces];
		}
		if (hasRenderType) {
			this.faceRenderType = new byte[this.numFaces];
		}
		if (billboardCount > 0) {
			this.billboards = new MeshBillboard[billboardCount];
		}
		if (effectorCount > 0) {
			this.particleEffectors = new EffectiveVertex[effectorCount];
		}
		this.vertexGroups = new int[this.numVertices];
		if (hasFaceTextures) {
			this.facesTexture = new byte[this.numFaces];
		}
		this.verticesZ = new int[this.numVertices];
		this.facesColor = new short[this.numFaces];
		this.verticesX = new int[this.numVertices];
		this.verticesY = new int[this.numVertices];
		if (emitterCount > 0) {
			this.particleEmitters = new EmissiveTriangle[emitterCount];
		}
		this.facesC = new short[this.numFaces];
		if (hasAlpha) {
			this.facesAlpha = new byte[this.numFaces];
		}
		this.verticesSubmeshes = new short[this.numVertices];
		this.numTextures = 0;
		effectorCount = 0;
		billboardCount = 0;
		emitterCount = 0;
		this.numFaces = 0;
		this.numVertices = 0;
		@Pc(616) int local616;
		@Pc(627) int local627;
		for (@Pc(383) int j = 0; j < meshCount; j++) {
			@Pc(390) short meshFlag = (short) (0x1 << j);
			@Pc(394) Mesh mesh2 = meshes[j];
			if (mesh2 != null) {
				@Pc(401) int local401;
				if (mesh2.billboards != null) {
					for (int billboardIndex = 0; billboardIndex < mesh2.billboards.length; billboardIndex++) {
						@Pc(408) MeshBillboard meshBillboard = mesh2.billboards[billboardIndex];
						this.billboards[billboardCount++] = meshBillboard.copyWithFace(meshBillboard.appliesOnFace + this.numFaces);
					}
				}
				for (int face = 0; face < mesh2.numFaces; face++) {
					if (hasRenderType && mesh2.faceRenderType != null) {
						this.faceRenderType[this.numFaces] = mesh2.faceRenderType[face];
					}
					if (hasPriorities) {
						if (mesh2.facesLayer == null) {
							this.facesLayer[this.numFaces] = mesh2.priority;
						} else {
							this.facesLayer[this.numFaces] = mesh2.facesLayer[face];
						}
					}
					if (hasAlpha && mesh2.facesAlpha != null) {
						this.facesAlpha[this.numFaces] = mesh2.facesAlpha[face];
					}
					if (hasFaceMaterials) {
						if (mesh2.facesMaterial == null) {
							this.facesMaterial[this.numFaces] = -1;
						} else {
							this.facesMaterial[this.numFaces] = mesh2.facesMaterial[face];
						}
					}
					if (hasFaceGroups) {
						if (mesh2.faceGroups == null) {
							this.faceGroups[this.numFaces] = -1;
						} else {
							this.faceGroups[this.numFaces] = mesh2.faceGroups[face];
						}
					}
					this.facesA[this.numFaces] = (short) this.connect(mesh2, mesh2.facesA[face], meshFlag);
					this.facesB[this.numFaces] = (short) this.connect(mesh2, mesh2.facesB[face], meshFlag);
					this.facesC[this.numFaces] = (short) this.connect(mesh2, mesh2.facesC[face], meshFlag);
					this.facesSubmeshes[this.numFaces] = meshFlag;
					this.facesColor[this.numFaces] = mesh2.facesColor[face];
					this.numFaces++;
				}
				@Pc(603) int local603;
				if (mesh2.particleEmitters != null) {
					for (int emitterIndex = 0; emitterIndex < mesh2.particleEmitters.length; emitterIndex++) {
						int emitterVertexA = this.connect(mesh2, mesh2.particleEmitters[emitterIndex].faceA, meshFlag);
						int emitterVertexB = this.connect(mesh2, mesh2.particleEmitters[emitterIndex].faceB, meshFlag);
						@Pc(638) int emitterVertexC = this.connect(mesh2, mesh2.particleEmitters[emitterIndex].vertexC, meshFlag);
						this.particleEmitters[emitterCount] = mesh2.particleEmitters[emitterIndex].copyWithFaces(emitterVertexB, emitterVertexC, emitterVertexA);
						emitterCount++;
					}
				}
				if (mesh2.particleEffectors != null) {
					for (int effectorIndex = 0; effectorIndex < mesh2.particleEffectors.length; effectorIndex++) {
						int effectorVertex = this.connect(mesh2, mesh2.particleEffectors[effectorIndex].appliesOnVertex, meshFlag);
						this.particleEffectors[effectorCount] = mesh2.particleEffectors[effectorIndex].copyWithVertex(effectorVertex);
						effectorCount++;
					}
				}
			}
		}
		@Pc(708) int faceIndex = 0;
		this.maxDepth = this.numVertices;
		for (@Pc(714) int k = 0; k < meshCount; k++) {
			@Pc(721) short meshFlag = (short) (0x1 << k);
			@Pc(725) Mesh mesh3 = meshes[k];
			if (mesh3 != null) {
				for (int face = 0; face < mesh3.numFaces; face++) {
					if (hasFaceTextures) {
						this.facesTexture[faceIndex++] = (byte) (mesh3.facesTexture == null || mesh3.facesTexture[face] == -1 ? -1 : mesh3.facesTexture[face] + this.numTextures);
					}
				}
				for (int texture = 0; texture < mesh3.numTextures; texture++) {
					@Pc(778) byte mapping = this.texturesMapping[this.numTextures] = mesh3.texturesMapping[texture];
					if (mapping == 0) {
						this.textureFaceX[this.numTextures] = (short) this.connect(mesh3, mesh3.textureFaceX[texture], meshFlag);
						this.textureFaceY[this.numTextures] = (short) this.connect(mesh3, mesh3.textureFaceY[texture], meshFlag);
						this.textureFaceZ[this.numTextures] = (short) this.connect(mesh3, mesh3.textureFaceZ[texture], meshFlag);
					}
					if (mapping >= 1 && mapping <= 3) {
						this.textureFaceX[this.numTextures] = mesh3.textureFaceX[texture];
						this.textureFaceY[this.numTextures] = mesh3.textureFaceY[texture];
						this.textureFaceZ[this.numTextures] = mesh3.textureFaceZ[texture];
						this.texturesScaleX[this.numTextures] = mesh3.texturesScaleX[texture];
						this.texturesScaleY[this.numTextures] = mesh3.texturesScaleY[texture];
						this.texturesScaleZ[this.numTextures] = mesh3.texturesScaleZ[texture];
						this.texturesRotation[this.numTextures] = mesh3.texturesRotation[texture];
						this.texturesDirection[this.numTextures] = mesh3.texturesDirection[texture];
						this.texturesSpeed[this.numTextures] = mesh3.texturesSpeed[texture];
					}
					if (mapping == 2) {
						this.texturesUtrans[this.numTextures] = mesh3.texturesUtrans[texture];
						this.texturesVtrans[this.numTextures] = mesh3.texturesVtrans[texture];
					}
					this.numTextures++;
				}
			}
		}
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(II)V")
	public void upscale() {
		for (@Pc(7) int i = 0; i < this.numVertices; i++) {
			this.verticesX[i] <<= 0x2;
			this.verticesY[i] <<= 0x2;
			this.verticesZ[i] <<= 0x2;
		}
		if (this.numTextures <= 0 || this.texturesScaleX == null) {
			return;
		}
		for (@Pc(58) int j = 0; j < this.texturesScaleX.length; j++) {
			this.texturesScaleX[j] <<= 0x2;
			this.texturesScaleY[j] <<= 0x2;
			if (this.texturesMapping[j] != 1) {
				this.texturesScaleZ[j] <<= 0x2;
			}
		}
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(B)[[I")
	public int[][] createBillboardGroups() {
		@Pc(8) int[] counts = new int[256];
		@Pc(10) int max = 0;
		for (@Pc(12) int i = 0; i < this.billboards.length; i++) {
			@Pc(20) int group = this.billboards[i].depth;
			if (group >= 0) {
				@Pc(26) int previousCount = counts[group]++;
				if (max < group) {
					max = group;
				}
			}
		}
		@Pc(45) int[][] groups = new int[max + 1][];
		for (@Pc(47) int j = 0; j <= max; j++) {
			groups[j] = new int[counts[j]];
			counts[j] = 0;
		}
		for (@Pc(75) int k = 0; k < this.billboards.length; k++) {
			@Pc(83) int depth = this.billboards[k].depth;
			if (depth >= 0) {
				groups[depth][counts[depth]++] = k;
			}
		}
		return groups;
	}

	@OriginalMember(owner = "client!bh", name = "b", descriptor = "(B)[[I")
	public int[][] createFaceGroups() {
		@Pc(8) int[] counts = new int[256];
		@Pc(10) int max = 0;
		for (@Pc(12) int i = 0; i < this.numFaces; i++) {
			@Pc(19) int group = this.faceGroups[i];
			if (group >= 0) {
				if (max < group) {
					max = group;
				}
				@Pc(33) int previousCount = counts[group]++;
			}
		}
		@Pc(46) int[][] groups = new int[max + 1][];
		for (@Pc(63) int j = 0; j <= max; j++) {
			groups[j] = new int[counts[j]];
			counts[j] = 0;
		}
		for (@Pc(86) int k = 0; k < this.numFaces; k++) {
			@Pc(93) int faceGroup = this.faceGroups[k];
			if (faceGroup >= 0) {
				groups[faceGroup][counts[faceGroup]++] = k;
			}
		}
		return groups;
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(SIS)V")
	public void retexture(@OriginalArg(0) short src, @OriginalArg(2) short dst) {
		if (this.facesMaterial == null) {
			return;
		}
		for (@Pc(11) int i = 0; i < this.numFaces; i++) {
			if (src == this.facesMaterial[i]) {
				this.facesMaterial[i] = dst;
			}
		}
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(SBS)V")
	public void recolor(@OriginalArg(0) short src, @OriginalArg(2) short dst) {
		for (@Pc(7) int i = 0; i < this.numFaces; i++) {
			if (this.facesColor[i] == src) {
				this.facesColor[i] = dst;
			}
		}
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(BIII)V")
	public void translate(@OriginalArg(1) int deltaZ, @OriginalArg(2) int deltaX, @OriginalArg(3) int deltaY) {
		for (@Pc(3) int i = 0; i < this.numVertices; i++) {
			this.verticesX[i] += deltaX;
			this.verticesY[i] += deltaY;
			this.verticesZ[i] += deltaZ;
		}
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(ZI)[[I")
	public int[][] createVertexGroups(@OriginalArg(0) boolean flag) {
		@Pc(8) int[] counts = new int[256];
		@Pc(10) int max = 0;
		@Pc(18) int count = flag ? this.numVertices : this.maxDepth;
		for (@Pc(20) int i = 0; i < count; i++) {
			@Pc(27) int group = this.vertexGroups[i];
			if (group >= 0) {
				@Pc(36) int previousCount = counts[group]++;
				if (max < group) {
					max = group;
				}
			}
		}
		@Pc(53) int[][] groups = new int[max + 1][];
		for (@Pc(55) int j = 0; j <= max; j++) {
			groups[j] = new int[counts[j]];
			counts[j] = 0;
		}
		for (@Pc(78) int k = 0; k < count; k++) {
			@Pc(85) int vertexGroup = this.vertexGroups[k];
			if (vertexGroup >= 0) {
				groups[vertexGroup][counts[vertexGroup]++] = k;
			}
		}
		return groups;
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(IIII)I")
	public int addVertex(@OriginalArg(0) int y, @OriginalArg(1) int z, @OriginalArg(2) int x) {
		for (@Pc(7) int i = 0; i < this.numVertices; i++) {
			if (this.verticesX[i] == x && y == this.verticesY[i] && this.verticesZ[i] == z) {
				return i;
			}
		}
		this.verticesX[this.numVertices] = x;
		this.verticesY[this.numVertices] = y;
		this.verticesZ[this.numVertices] = z;
		this.maxDepth = this.numVertices + 1;
		return this.numVertices++;
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(IBBSBSIBI)I")
	public int addFace(@OriginalArg(0) int faceA, @OriginalArg(1) byte faceRenderType, @OriginalArg(2) byte faceTexture, @OriginalArg(3) short faceMaterial, @OriginalArg(5) short faceColor, @OriginalArg(6) int faceB, @OriginalArg(7) byte faceAlpha, @OriginalArg(8) int faceC) {
		this.facesA[this.numFaces] = (short) faceA;
		this.facesB[this.numFaces] = (short) faceB;
		this.facesC[this.numFaces] = (short) faceC;
		this.faceRenderType[this.numFaces] = faceRenderType;
		this.facesTexture[this.numFaces] = faceTexture;
		this.facesColor[this.numFaces] = faceColor;
		this.facesAlpha[this.numFaces] = faceAlpha;
		this.facesMaterial[this.numFaces] = faceMaterial;
		return this.numFaces++;
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(I[B)V")
	private void decodeOldFormat(@OriginalArg(1) byte[] data) {
		@Pc(7) boolean hasFaceTypes = false;
		@Pc(9) boolean hasMaterials = false;
		@Pc(14) Packet packet = new Packet(data);
		@Pc(19) Packet packet2 = new Packet(data);
		@Pc(24) Packet packet3 = new Packet(data);
		@Pc(29) Packet packet4 = new Packet(data);
		@Pc(34) Packet packet5 = new Packet(data);
		packet.currentPosition = data.length - 18;
		this.numVertices = packet.readUShort();
		this.numFaces = packet.readUShort();
		this.numTextures = packet.readUByte();
		@Pc(61) int hasFaceInfo = packet.readUByte();
		@Pc(65) int modelPriority = packet.readUByte();
		@Pc(69) int hasFaceAlpha = packet.readUByte();
		@Pc(73) int hasFaceGroups = packet.readUByte();
		@Pc(77) int hasVertexGroups = packet.readUByte();
		@Pc(81) int verticesXSize = packet.readUShort();
		@Pc(85) int verticesYSize = packet.readUShort();
		@Pc(89) int verticesZSize = packet.readUShort();
		@Pc(93) int faceIndicesSize = packet.readUShort();
		@Pc(102) int offset = this.numVertices;
		@Pc(104) int faceIndexTypesOffset = offset;
		offset += this.numFaces;
		@Pc(111) int facePriorityOffset = offset;
		if (modelPriority == 255) {
			offset += this.numFaces;
		}
		@Pc(123) int faceGroupsOffset = offset;
		if (hasFaceGroups == 1) {
			offset += this.numFaces;
		}
		@Pc(135) int faceTypeOffset = offset;
		if (hasFaceInfo == 1) {
			offset += this.numFaces;
		}
		@Pc(145) int vertexGroupsOffset = offset;
		if (hasVertexGroups == 1) {
			offset += this.numVertices;
		}
		@Pc(155) int faceAlphaOffset = offset;
		if (hasFaceAlpha == 1) {
			offset += this.numFaces;
		}
		@Pc(165) int faceIndicesDataOffset = offset;
		offset += faceIndicesSize;
		@Pc(171) int faceColorOffset = offset;
		offset += this.numFaces * 2;
		@Pc(180) int textureTriOffset = offset;
		offset += this.numTextures * 6;
		@Pc(189) int verticesXDataOffset = offset;
		offset += verticesXSize;
		@Pc(195) int verticesYDataOffset = offset;
		offset += verticesYSize;
		this.facesColor = new short[this.numFaces];
		if (modelPriority == 255) {
			this.facesLayer = new byte[this.numFaces];
		} else {
			this.priority = (byte) modelPriority;
		}
		if (hasVertexGroups == 1) {
			this.vertexGroups = new int[this.numVertices];
		}
		if (hasFaceAlpha == 1) {
			this.facesAlpha = new byte[this.numFaces];
		}
		this.facesA = new short[this.numFaces];
		if (hasFaceInfo == 1) {
			this.facesMaterial = new short[this.numFaces];
			this.facesTexture = new byte[this.numFaces];
			this.faceRenderType = new byte[this.numFaces];
		}
		this.verticesY = new int[this.numVertices];
		if (hasFaceGroups == 1) {
			this.faceGroups = new int[this.numFaces];
		}
		this.facesC = new short[this.numFaces];
		if (this.numTextures > 0) {
			this.texturesMapping = new byte[this.numTextures];
			this.textureFaceX = new short[this.numTextures];
			this.textureFaceY = new short[this.numTextures];
			this.textureFaceZ = new short[this.numTextures];
		}
		this.facesB = new short[this.numFaces];
		this.verticesZ = new int[this.numVertices];
		this.verticesX = new int[this.numVertices];
		packet.currentPosition = 0;
		packet2.currentPosition = verticesXDataOffset;
		packet3.currentPosition = verticesYDataOffset;
		packet4.currentPosition = offset;
		packet5.currentPosition = vertexGroupsOffset;
		@Pc(342) int lastVertexX = 0;
		@Pc(344) int lastVertexY = 0;
		@Pc(346) int lastVertexZ = 0;
		@Pc(354) int local354;
		@Pc(356) int local356;
		for (@Pc(348) int i = 0; i < this.numVertices; i++) {
			int vertexFlags = packet.readUByte();
			int deltaX = 0;
			if ((vertexFlags & 0x1) != 0) {
				deltaX = packet2.readSmart();
			}
			@Pc(366) int deltaY = 0;
			if ((vertexFlags & 0x2) != 0) {
				deltaY = packet3.readSmart();
			}
			@Pc(379) int deltaZ = 0;
			if ((vertexFlags & 0x4) != 0) {
				deltaZ = packet4.readSmart();
			}
			this.verticesX[i] = deltaX + lastVertexX;
			this.verticesY[i] = lastVertexY + deltaY;
			this.verticesZ[i] = lastVertexZ + deltaZ;
			lastVertexZ = this.verticesZ[i];
			lastVertexY = this.verticesY[i];
			lastVertexX = this.verticesX[i];
			if (hasVertexGroups == 1) {
				this.vertexGroups[i] = packet5.readUByte();
			}
		}
		packet.currentPosition = faceColorOffset;
		packet2.currentPosition = faceTypeOffset;
		packet3.currentPosition = facePriorityOffset;
		packet4.currentPosition = faceAlphaOffset;
		packet5.currentPosition = faceGroupsOffset;
		for (int face = 0; face < this.numFaces; face++) {
			this.facesColor[face] = (short) packet.readUShort();
			if (hasFaceInfo == 1) {
				int faceInfo = packet2.readUByte();
				if ((faceInfo & 0x1) == 1) {
					this.faceRenderType[face] = 1;
					hasFaceTypes = true;
				} else {
					this.faceRenderType[face] = 0;
				}
				if ((faceInfo & 0x2) == 2) {
					this.facesTexture[face] = (byte) (faceInfo >> 2);
					this.facesMaterial[face] = this.facesColor[face];
					this.facesColor[face] = 127;
					if (this.facesMaterial[face] != -1) {
						hasMaterials = true;
					}
				} else {
					this.facesTexture[face] = -1;
					this.facesMaterial[face] = -1;
				}
			}
			if (modelPriority == 255) {
				this.facesLayer[face] = packet3.readByte();
			}
			if (hasFaceAlpha == 1) {
				this.facesAlpha[face] = packet4.readByte();
			}
			if (hasFaceGroups == 1) {
				this.faceGroups[face] = packet5.readUByte();
			}
		}
		packet.currentPosition = faceIndicesDataOffset;
		this.maxDepth = -1;
		packet2.currentPosition = faceIndexTypesOffset;
		@Pc(595) short vertexA = 0;
		@Pc(597) short vertexB = 0;
		@Pc(599) short vertexC = 0;
		@Pc(601) short lastVertex = 0;
		@Pc(609) int local609;
		for (@Pc(603) int j = 0; j < this.numFaces; j++) {
			int faceIndexType = packet2.readUByte();
			if (faceIndexType == 1) {
				vertexA = (short) (lastVertex + packet.readSmart());
				vertexB = (short) (packet.readSmart() + vertexA);
				vertexC = (short) (vertexB + packet.readSmart());
				this.facesA[j] = vertexA;
				lastVertex = vertexC;
				this.facesB[j] = vertexB;
				this.facesC[j] = vertexC;
				if (this.maxDepth < vertexA) {
					this.maxDepth = vertexA;
				}
				if (vertexB > this.maxDepth) {
					this.maxDepth = vertexB;
				}
				if (vertexC > this.maxDepth) {
					this.maxDepth = vertexC;
				}
			}
			if (faceIndexType == 2) {
				vertexB = vertexC;
				vertexC = (short) (packet.readSmart() + lastVertex);
				this.facesA[j] = vertexA;
				lastVertex = vertexC;
				this.facesB[j] = vertexB;
				this.facesC[j] = vertexC;
				if (vertexC > this.maxDepth) {
					this.maxDepth = vertexC;
				}
			}
			if (faceIndexType == 3) {
				vertexA = vertexC;
				vertexC = (short) (packet.readSmart() + lastVertex);
				lastVertex = vertexC;
				this.facesA[j] = vertexA;
				this.facesB[j] = vertexB;
				this.facesC[j] = vertexC;
				if (this.maxDepth < vertexC) {
					this.maxDepth = vertexC;
				}
			}
			if (faceIndexType == 4) {
				@Pc(768) short temp = vertexA;
				vertexA = vertexB;
				vertexC = (short) (packet.readSmart() + lastVertex);
				vertexB = temp;
				lastVertex = vertexC;
				this.facesA[j] = vertexA;
				this.facesB[j] = temp;
				this.facesC[j] = vertexC;
				if (this.maxDepth < vertexC) {
					this.maxDepth = vertexC;
				}
			}
		}
		this.maxDepth++;
		packet.currentPosition = textureTriOffset;
		for (int texture = 0; texture < this.numTextures; texture++) {
			this.texturesMapping[texture] = 0;
			this.textureFaceX[texture] = (short) packet.readUShort();
			this.textureFaceY[texture] = (short) packet.readUShort();
			this.textureFaceZ[texture] = (short) packet.readUShort();
		}
		if (this.facesTexture != null) {
			@Pc(868) boolean usesTextureFaces = false;
			for (@Pc(870) int k = 0; k < this.numFaces; k++) {
				@Pc(879) int textureIndex = this.facesTexture[k] & 0xFF;
				if (textureIndex != 255) {
					if ((this.textureFaceX[textureIndex] & 0xFFFF) == this.facesA[k] && this.facesB[k] == (this.textureFaceY[textureIndex] & 0xFFFF) && (this.textureFaceZ[textureIndex] & 0xFFFF) == this.facesC[k]) {
						this.facesTexture[k] = -1;
					} else {
						usesTextureFaces = true;
					}
				}
			}
			if (!usesTextureFaces) {
				this.facesTexture = null;
			}
		}
		if (!hasFaceTypes) {
			this.faceRenderType = null;
		}
		if (!hasMaterials) {
			this.facesMaterial = null;
		}
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(Lclient!bh;IIS)I")
	private int connect(@OriginalArg(0) Mesh mesh, @OriginalArg(2) int vertex, @OriginalArg(3) short submeshFlag) {
		@Pc(10) int x = mesh.verticesX[vertex];
		@Pc(15) int y = mesh.verticesY[vertex];
		@Pc(20) int z = mesh.verticesZ[vertex];
		for (@Pc(22) int i = 0; i < this.numVertices; i++) {
			if (this.verticesX[i] == x && y == this.verticesY[i] && this.verticesZ[i] == z) {
				this.verticesSubmeshes[i] |= submeshFlag;
				return i;
			}
		}
		this.verticesX[this.numVertices] = x;
		this.verticesY[this.numVertices] = y;
		this.verticesZ[this.numVertices] = z;
		this.verticesSubmeshes[this.numVertices] = submeshFlag;
		this.vertexGroups[this.numVertices] = mesh.vertexGroups == null ? -1 : mesh.vertexGroups[vertex];
		return this.numVertices++;
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "(IBBSSBSSSS)B")
	public byte addSunTexture() {
		if (this.numTextures >= 255) {
			throw new IllegalStateException();
		}
		this.texturesMapping[this.numTextures] = 3;
		this.textureFaceX[this.numTextures] = 0;
		this.textureFaceY[this.numTextures] = 32767;
		this.textureFaceZ[this.numTextures] = 0;
		this.texturesScaleX[this.numTextures] = 1024;
		this.texturesScaleY[this.numTextures] = 1024;
		this.texturesScaleZ[this.numTextures] = 1024;
		this.texturesRotation[this.numTextures] = 0;
		this.texturesDirection[this.numTextures] = 0;
		this.texturesSpeed[this.numTextures] = 0;
		return (byte) this.numTextures++;
	}

	@OriginalMember(owner = "client!bh", name = "a", descriptor = "([BB)V")
	private void decodeNewFormat(@OriginalArg(0) byte[] data) {
		@Pc(10) Packet packet = new Packet(data);
		@Pc(15) Packet packet2 = new Packet(data);
		@Pc(20) Packet packet3 = new Packet(data);
		@Pc(25) Packet packet4 = new Packet(data);
		@Pc(30) Packet packet5 = new Packet(data);
		@Pc(35) Packet packet6 = new Packet(data);
		@Pc(40) Packet packet7 = new Packet(data);
		packet.currentPosition = data.length - 23;
		this.numVertices = packet.readUShort();
		this.numFaces = packet.readUShort();
		this.numTextures = packet.readUByte();
		@Pc(65) int footerFlags = packet.readUByte();
		@Pc(76) boolean hasFaceTypes = (footerFlags & 0x1) == 1;
		@Pc(85) boolean hasParticleEffects = (footerFlags & 0x2) == 2;
		@Pc(96) boolean hasBillboards = (footerFlags & 0x4) == 4;
		@Pc(105) boolean hasVersion = (footerFlags & 0x8) == 8;
		if (hasVersion) {
			packet.currentPosition -= 7;
			this.version = packet.readUByte();
			packet.currentPosition += 6;
		}
		@Pc(128) int modelPriority = packet.readUByte();
		@Pc(132) int hasFaceAlpha = packet.readUByte();
		@Pc(136) int hasFaceGroups = packet.readUByte();
		@Pc(140) int hasFaceTextures = packet.readUByte();
		@Pc(144) int hasVertexGroups = packet.readUByte();
		@Pc(148) int verticesXSize = packet.readUShort();
		@Pc(152) int verticesYSize = packet.readUShort();
		@Pc(156) int verticesZSize = packet.readUShort();
		@Pc(160) int faceIndicesSize = packet.readUShort();
		@Pc(164) int textureIndicesSize = packet.readUShort();
		@Pc(166) int simpleTextureCount = 0;
		@Pc(168) int complexTextureCount = 0;
		@Pc(170) int cubeTextureCount = 0;
		@Pc(186) int local186;
		if (this.numTextures > 0) {
			packet.currentPosition = 0;
			this.texturesMapping = new byte[this.numTextures];
			for (int texture = 0; texture < this.numTextures; texture++) {
				@Pc(197) byte mapping = this.texturesMapping[texture] = packet.readByte();
				if (mapping >= 1 && mapping <= 3) {
					complexTextureCount++;
				}
				if (mapping == 2) {
					cubeTextureCount++;
				}
				if (mapping == 0) {
					simpleTextureCount++;
				}
			}
		}
		int dataOffset = this.numTextures;
		@Pc(227) int vertexFlagsOffset = dataOffset;
		dataOffset += this.numVertices;
		@Pc(234) int faceTypesOffset = dataOffset;
		if (hasFaceTypes) {
			dataOffset += this.numFaces;
		}
		@Pc(243) int faceIndexTypesOffset = dataOffset;
		dataOffset += this.numFaces;
		@Pc(250) int facePriorityOffset = dataOffset;
		if (modelPriority == 255) {
			dataOffset += this.numFaces;
		}
		@Pc(262) int faceGroupsOffset = dataOffset;
		if (hasFaceGroups == 1) {
			dataOffset += this.numFaces;
		}
		@Pc(274) int vertexGroupsOffset = dataOffset;
		if (hasVertexGroups == 1) {
			dataOffset += this.numVertices;
		}
		@Pc(284) int faceAlphaOffset = dataOffset;
		if (hasFaceAlpha == 1) {
			dataOffset += this.numFaces;
		}
		@Pc(300) int faceIndicesDataOffset = dataOffset;
		dataOffset += faceIndicesSize;
		@Pc(306) int faceMaterialOffset = dataOffset;
		if (hasFaceTextures == 1) {
			dataOffset += this.numFaces * 2;
		}
		@Pc(318) int textureIndexDataOffset = dataOffset;
		dataOffset += textureIndicesSize;
		@Pc(324) int faceColorOffset = dataOffset;
		dataOffset += this.numFaces * 2;
		@Pc(333) int verticesXOffset = dataOffset;
		dataOffset += verticesXSize;
		@Pc(339) int verticesYOffset = dataOffset;
		dataOffset += verticesYSize;
		@Pc(345) int verticesZOffset = dataOffset;
		dataOffset += verticesZSize;
		@Pc(351) int simpleTextureOffset = dataOffset;
		dataOffset += simpleTextureCount * 6;
		@Pc(359) int complexTextureVertOffset = dataOffset;
		dataOffset += complexTextureCount * 6;
		@Pc(367) byte textureScaleBytes = 6;
		if (this.version == 14) {
			textureScaleBytes = 7;
		} else if (this.version >= 15) {
			textureScaleBytes = 9;
		}
		@Pc(383) int complexTextureScaleOffset = dataOffset;
		dataOffset += textureScaleBytes * complexTextureCount;
		@Pc(391) int complexTextureRotOffset = dataOffset;
		dataOffset += complexTextureCount;
		@Pc(397) int complexTextureDirOffset = dataOffset;
		dataOffset += complexTextureCount;
		@Pc(403) int complexTextureSpeedOffset = dataOffset;
		dataOffset += cubeTextureCount * 2 + complexTextureCount;
		this.facesB = new short[this.numFaces];
		this.facesColor = new short[this.numFaces];
		if (hasFaceGroups == 1) {
			this.faceGroups = new int[this.numFaces];
		}
		if (this.numTextures > 0) {
			if (cubeTextureCount > 0) {
				this.texturesUtrans = new int[cubeTextureCount];
				this.texturesVtrans = new int[cubeTextureCount];
			}
			this.textureFaceY = new short[this.numTextures];
			this.textureFaceX = new short[this.numTextures];
			if (complexTextureCount > 0) {
				this.texturesSpeed = new int[complexTextureCount];
				this.texturesDirection = new byte[complexTextureCount];
				this.texturesScaleY = new int[complexTextureCount];
				this.texturesRotation = new byte[complexTextureCount];
				this.texturesScaleZ = new int[complexTextureCount];
				this.texturesScaleX = new int[complexTextureCount];
			}
			this.textureFaceZ = new short[this.numTextures];
		}
		if (hasFaceTypes) {
			this.faceRenderType = new byte[this.numFaces];
		}
		if (hasFaceAlpha == 1) {
			this.facesAlpha = new byte[this.numFaces];
		}
		if (modelPriority == 255) {
			this.facesLayer = new byte[this.numFaces];
		} else {
			this.priority = (byte) modelPriority;
		}
		this.facesC = new short[this.numFaces];
		this.verticesY = new int[this.numVertices];
		this.facesA = new short[this.numFaces];
		if (hasFaceTextures == 1 && this.numTextures > 0) {
			this.facesTexture = new byte[this.numFaces];
		}
		this.verticesZ = new int[this.numVertices];
		this.verticesX = new int[this.numVertices];
		if (hasVertexGroups == 1) {
			this.vertexGroups = new int[this.numVertices];
		}
		packet.currentPosition = vertexFlagsOffset;
		if (hasFaceTextures == 1) {
			this.facesMaterial = new short[this.numFaces];
		}
		packet2.currentPosition = verticesXOffset;
		packet3.currentPosition = verticesYOffset;
		packet4.currentPosition = verticesZOffset;
		packet5.currentPosition = vertexGroupsOffset;
		@Pc(601) int lastVertexX = 0;
		@Pc(603) int lastVertexY = 0;
		@Pc(605) int lastVertexZ = 0;
		@Pc(613) int local613;
		for (@Pc(607) int i = 0; i < this.numVertices; i++) {
			int vertexFlags = packet.readUByte();
			@Pc(615) int deltaX = 0;
			if ((vertexFlags & 0x1) != 0) {
				deltaX = packet2.readSmart();
			}
			@Pc(628) int deltaY = 0;
			if ((vertexFlags & 0x2) != 0) {
				deltaY = packet3.readSmart();
			}
			@Pc(641) int deltaZ = 0;
			if ((vertexFlags & 0x4) != 0) {
				deltaZ = packet4.readSmart();
			}
			this.verticesX[i] = deltaX + lastVertexX;
			this.verticesY[i] = deltaY + lastVertexY;
			this.verticesZ[i] = lastVertexZ + deltaZ;
			lastVertexY = this.verticesY[i];
			lastVertexX = this.verticesX[i];
			lastVertexZ = this.verticesZ[i];
			if (hasVertexGroups == 1) {
				this.vertexGroups[i] = packet5.readUByte();
			}
		}
		packet.currentPosition = faceColorOffset;
		packet2.currentPosition = faceTypesOffset;
		packet3.currentPosition = facePriorityOffset;
		packet4.currentPosition = faceAlphaOffset;
		packet5.currentPosition = faceGroupsOffset;
		packet6.currentPosition = faceMaterialOffset;
		packet7.currentPosition = textureIndexDataOffset;
		for (int face = 0; face < this.numFaces; face++) {
			this.facesColor[face] = (short) packet.readUShort();
			if (hasFaceTypes) {
				this.faceRenderType[face] = packet2.readByte();
			}
			if (modelPriority == 255) {
				this.facesLayer[face] = packet3.readByte();
			}
			if (hasFaceAlpha == 1) {
				this.facesAlpha[face] = packet4.readByte();
			}
			if (hasFaceGroups == 1) {
				this.faceGroups[face] = packet5.readUByte();
			}
			if (hasFaceTextures == 1) {
				this.facesMaterial[face] = (short) (packet6.readUShort() - 1);
			}
			if (this.facesTexture != null) {
				if (this.facesMaterial[face] == -1) {
					this.facesTexture[face] = -1;
				} else {
					this.facesTexture[face] = (byte) (packet7.readUByte() - 1);
				}
			}
		}
		packet.currentPosition = faceIndicesDataOffset;
		this.maxDepth = -1;
		packet2.currentPosition = faceIndexTypesOffset;
		@Pc(838) short vertexA = 0;
		@Pc(840) short vertexB = 0;
		@Pc(842) short vertexC = 0;
		@Pc(844) short lastVertex = 0;
		@Pc(852) int local852;
		for (@Pc(846) int j = 0; j < this.numFaces; j++) {
			int faceIndexType = packet2.readUByte();
			if (faceIndexType == 1) {
				vertexA = (short) (packet.readSmart() + lastVertex);
				vertexB = (short) (packet.readSmart() + vertexA);
				vertexC = (short) (vertexB + packet.readSmart());
				lastVertex = vertexC;
				this.facesA[j] = vertexA;
				this.facesB[j] = vertexB;
				this.facesC[j] = vertexC;
				if (this.maxDepth < vertexA) {
					this.maxDepth = vertexA;
				}
				if (this.maxDepth < vertexB) {
					this.maxDepth = vertexB;
				}
				if (vertexC > this.maxDepth) {
					this.maxDepth = vertexC;
				}
			}
			if (faceIndexType == 2) {
				vertexB = vertexC;
				vertexC = (short) (packet.readSmart() + lastVertex);
				this.facesA[j] = vertexA;
				lastVertex = vertexC;
				this.facesB[j] = vertexB;
				this.facesC[j] = vertexC;
				if (this.maxDepth < vertexC) {
					this.maxDepth = vertexC;
				}
			}
			if (faceIndexType == 3) {
				vertexA = vertexC;
				vertexC = (short) (packet.readSmart() + lastVertex);
				lastVertex = vertexC;
				this.facesA[j] = vertexA;
				this.facesB[j] = vertexB;
				this.facesC[j] = vertexC;
				if (this.maxDepth < vertexC) {
					this.maxDepth = vertexC;
				}
			}
			if (faceIndexType == 4) {
				@Pc(1003) short temp = vertexA;
				vertexA = vertexB;
				vertexC = (short) (lastVertex + packet.readSmart());
				vertexB = temp;
				this.facesA[j] = vertexA;
				lastVertex = vertexC;
				this.facesB[j] = temp;
				this.facesC[j] = vertexC;
				if (this.maxDepth < vertexC) {
					this.maxDepth = vertexC;
				}
			}
		}
		packet.currentPosition = simpleTextureOffset;
		this.maxDepth++;
		packet2.currentPosition = complexTextureVertOffset;
		packet3.currentPosition = complexTextureScaleOffset;
		packet4.currentPosition = complexTextureRotOffset;
		packet5.currentPosition = complexTextureDirOffset;
		packet6.currentPosition = complexTextureSpeedOffset;
		@Pc(1082) int local1082;
		for (int texture = 0; texture < this.numTextures; texture++) {
			int mapping = this.texturesMapping[texture] & 0xFF;
			if (mapping == 0) {
				this.textureFaceX[texture] = (short) packet.readUShort();
				this.textureFaceY[texture] = (short) packet.readUShort();
				this.textureFaceZ[texture] = (short) packet.readUShort();
			}
			if (mapping == 1) {
				this.textureFaceX[texture] = (short) packet2.readUShort();
				this.textureFaceY[texture] = (short) packet2.readUShort();
				this.textureFaceZ[texture] = (short) packet2.readUShort();
				if (this.version >= 15) {
					this.texturesScaleX[texture] = packet3.readUMedium();
					this.texturesScaleY[texture] = packet3.readUMedium();
					this.texturesScaleZ[texture] = packet3.readUMedium();
				} else {
					this.texturesScaleX[texture] = packet3.readUShort();
					if (this.version >= 14) {
						this.texturesScaleY[texture] = packet3.readUMedium();
					} else {
						this.texturesScaleY[texture] = packet3.readUShort();
					}
					this.texturesScaleZ[texture] = packet3.readUShort();
				}
				this.texturesRotation[texture] = packet4.readByte();
				this.texturesDirection[texture] = packet5.readByte();
				this.texturesSpeed[texture] = packet6.readByte();
			}
			if (mapping == 2) {
				this.textureFaceX[texture] = (short) packet2.readUShort();
				this.textureFaceY[texture] = (short) packet2.readUShort();
				this.textureFaceZ[texture] = (short) packet2.readUShort();
				if (this.version >= 15) {
					this.texturesScaleX[texture] = packet3.readUMedium();
					this.texturesScaleY[texture] = packet3.readUMedium();
					this.texturesScaleZ[texture] = packet3.readUMedium();
				} else {
					this.texturesScaleX[texture] = packet3.readUShort();
					if (this.version < 14) {
						this.texturesScaleY[texture] = packet3.readUShort();
					} else {
						this.texturesScaleY[texture] = packet3.readUMedium();
					}
					this.texturesScaleZ[texture] = packet3.readUShort();
				}
				this.texturesRotation[texture] = packet4.readByte();
				this.texturesDirection[texture] = packet5.readByte();
				this.texturesSpeed[texture] = packet6.readByte();
				this.texturesUtrans[texture] = packet6.readByte();
				this.texturesVtrans[texture] = packet6.readByte();
			}
			if (mapping == 3) {
				this.textureFaceX[texture] = (short) packet2.readUShort();
				this.textureFaceY[texture] = (short) packet2.readUShort();
				this.textureFaceZ[texture] = (short) packet2.readUShort();
				if (this.version < 15) {
					this.texturesScaleX[texture] = packet3.readUShort();
					if (this.version < 14) {
						this.texturesScaleY[texture] = packet3.readUShort();
					} else {
						this.texturesScaleY[texture] = packet3.readUMedium();
					}
					this.texturesScaleZ[texture] = packet3.readUShort();
				} else {
					this.texturesScaleX[texture] = packet3.readUMedium();
					this.texturesScaleY[texture] = packet3.readUMedium();
					this.texturesScaleZ[texture] = packet3.readUMedium();
				}
				this.texturesRotation[texture] = packet4.readByte();
				this.texturesDirection[texture] = packet5.readByte();
				this.texturesSpeed[texture] = packet6.readByte();
			}
		}
		packet.currentPosition = dataOffset;
		@Pc(1486) int local1486;
		@Pc(1492) int local1492;
		@Pc(1496) int local1496;
		@Pc(1561) int local1561;
		if (hasParticleEffects) {
			int emitterCount = packet.readUByte();
			if (emitterCount > 0) {
				this.particleEmitters = new EmissiveTriangle[emitterCount];
				for (int emitterIndex = 0; emitterIndex < emitterCount; emitterIndex++) {
					int producerId = packet.readUShort();
					int emitterFace = packet.readUShort();
					@Pc(1504) byte priority;
					if (modelPriority == 255) {
						priority = this.facesLayer[emitterFace];
					} else {
						priority = (byte) modelPriority;
					}
					this.particleEmitters[emitterIndex] = new EmissiveTriangle(producerId, this.facesA[emitterFace], this.facesB[emitterFace], this.facesC[emitterFace], priority);
				}
			}
			int effectorCount = packet.readUByte();
			if (effectorCount > 0) {
				this.particleEffectors = new EffectiveVertex[effectorCount];
				for (int effectorIndex = 0; effectorIndex < effectorCount; effectorIndex++) {
					int effectId = packet.readUShort();
					int appliesOnVertex = packet.readUShort();
					this.particleEffectors[effectorIndex] = new EffectiveVertex(effectId, appliesOnVertex);
				}
			}
		}
		if (!hasBillboards) {
			return;
		}
		int billboardCount = packet.readUByte();
		if (billboardCount <= 0) {
			return;
		}
		this.billboards = new MeshBillboard[billboardCount];
		for (int billboardIndex = 0; billboardIndex < billboardCount; billboardIndex++) {
			int billboardId = packet.readUShort();
			int appliesOnFace = packet.readUShort();
			int depth = packet.readUByte();
			@Pc(1609) byte distance = packet.readByte();
			this.billboards[billboardIndex] = new MeshBillboard(billboardId, appliesOnFace, depth, distance);
		}
		return;
	}

	@OriginalMember(owner = "client!bh", name = "b", descriptor = "(IIII)V")
	public void rotateWithoutNormals(@OriginalArg(1) int y, @OriginalArg(2) int z, @OriginalArg(3) int x) {
		@Pc(10) int sin;
		@Pc(14) int cos;
		@Pc(16) int vertex;
		@Pc(34) int rotated;
		if (z != 0) {
			sin = TextureCombineSource.SINE[z];
			cos = TextureCombineSource.COSINE[z];
			for (vertex = 0; vertex < this.numVertices; vertex++) {
				rotated = cos * this.verticesX[vertex] + this.verticesY[vertex] * sin >> 14;
				this.verticesY[vertex] = this.verticesY[vertex] * cos - this.verticesX[vertex] * sin >> 14;
				this.verticesX[vertex] = rotated;
			}
		}
		if (x != 0) {
			sin = TextureCombineSource.SINE[x];
			cos = TextureCombineSource.COSINE[x];
			for (vertex = 0; vertex < this.numVertices; vertex++) {
				rotated = this.verticesY[vertex] * cos - sin * this.verticesZ[vertex] >> 14;
				this.verticesZ[vertex] = this.verticesY[vertex] * sin + cos * this.verticesZ[vertex] >> 14;
				this.verticesY[vertex] = rotated;
			}
		}
		if (y == 0) {
			return;
		}
		sin = TextureCombineSource.SINE[y];
		cos = TextureCombineSource.COSINE[y];
		for (vertex = 0; vertex < this.numVertices; vertex++) {
			rotated = cos * this.verticesX[vertex] + this.verticesZ[vertex] * sin >> 14;
			this.verticesZ[vertex] = cos * this.verticesZ[vertex] - this.verticesX[vertex] * sin >> 14;
			this.verticesX[vertex] = rotated;
		}
	}
	@OriginalMember(owner = "client!wia", name = "a", descriptor = "(ILclient!in;II)Lclient!bh;")
	public static Mesh fromResource(@OriginalArg(1) Js5ResourceProvider resource, @OriginalArg(2) int id) {
		@Pc(14) byte[] data = resource.getFile(id, 0);
		return data == null ? null : new Mesh(data);
	}
}
