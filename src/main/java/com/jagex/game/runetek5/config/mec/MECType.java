package com.jagex.game.runetek5.config.mec;

import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.cache.node.StringNode;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.vardomain.VarDomain;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.SpriteDataProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.utils.Utils;

@OriginalClass("client!ii")
public final class MECType {

	@OriginalMember(owner = "client!ii", name = "c", descriptor = "[B")
	public byte[] colorPointers;

	@OriginalMember(owner = "client!ii", name = "d", descriptor = "Ljava/lang/String;")
	public String minimenuName;

	@OriginalMember(owner = "client!ii", name = "e", descriptor = "I")
	public int labelOffsetX;

	@OriginalMember(owner = "client!ii", name = "g", descriptor = "I")
	public int labelOffsetY;

	@OriginalMember(owner = "client!ii", name = "k", descriptor = "[I")
	public int[] colors;

	@OriginalMember(owner = "client!ii", name = "s", descriptor = "I")
	public int defaultTextColor;

	@OriginalMember(owner = "client!ii", name = "t", descriptor = "I")
	public int mectypeId;

	@OriginalMember(owner = "client!ii", name = "u", descriptor = "I")
	public int lineColor;

	@OriginalMember(owner = "client!ii", name = "x", descriptor = "I")
	private int primaryVariableMinValue;

	@OriginalMember(owner = "client!ii", name = "y", descriptor = "I")
	private int secondaryVariableMaxValue;

	@OriginalMember(owner = "client!ii", name = "z", descriptor = "I")
	private int secondaryVariableMinValue;

	@OriginalMember(owner = "client!ii", name = "A", descriptor = "Lclient!eq;")
	private IterableHashTable parameters;

	@OriginalMember(owner = "client!ii", name = "D", descriptor = "[I")
	public int[] offsets;

	@OriginalMember(owner = "client!ii", name = "E", descriptor = "I")
	private int primaryVariableMaxValue;

	@OriginalMember(owner = "client!ii", name = "F", descriptor = "Lclient!on;")
	public MECTypeList initialType;

	@OriginalMember(owner = "client!ii", name = "J", descriptor = "Ljava/lang/String;")
	public String areaName;

	@OriginalMember(owner = "client!ii", name = "O", descriptor = "I")
	public int backgroundColor;

	@OriginalMember(owner = "client!ii", name = "Q", descriptor = "I")
	public int outlineColor;

	@OriginalMember(owner = "client!ii", name = "i", descriptor = "I")
	public int maxOffsetY = Integer.MIN_VALUE;

	@OriginalMember(owner = "client!ii", name = "l", descriptor = "I")
	private int secondaryVarpbit = -1;

	@OriginalMember(owner = "client!ii", name = "n", descriptor = "I")
	public int dashLineLength = -1;

	@OriginalMember(owner = "client!ii", name = "m", descriptor = "Z")
	public boolean displayedOnWorldmap = true;

	@OriginalMember(owner = "client!ii", name = "q", descriptor = "I")
	public int defaultIconArchive = -1;

	@OriginalMember(owner = "client!ii", name = "v", descriptor = "I")
	private int secondaryVarp = -1;

	@OriginalMember(owner = "client!ii", name = "I", descriptor = "I")
	public int categoryId = -1;

	@OriginalMember(owner = "client!ii", name = "L", descriptor = "Z")
	public boolean hasRandomisedMinimapPosition = true;

	@OriginalMember(owner = "client!ii", name = "f", descriptor = "I")
	public int mouseOverTextColor = -1;

	@OriginalMember(owner = "client!ii", name = "o", descriptor = "I")
	public int baseTextzoom = 0;

	@OriginalMember(owner = "client!ii", name = "p", descriptor = "[Ljava/lang/String;")
	public final String[] minimenuActions = new String[5];

	@OriginalMember(owner = "client!ii", name = "K", descriptor = "I")
	private int spriteId = -1;

	@OriginalMember(owner = "client!ii", name = "H", descriptor = "I")
	public int maxOffsetX = Integer.MIN_VALUE;

	@OriginalMember(owner = "client!ii", name = "w", descriptor = "I")
	public int dashLineSpacing = -1;

	@OriginalMember(owner = "client!ii", name = "P", descriptor = "I")
	public int minOffsetX = Integer.MAX_VALUE;

	@OriginalMember(owner = "client!ii", name = "C", descriptor = "I")
	private int primaryVarpbit = -1;

	@OriginalMember(owner = "client!ii", name = "S", descriptor = "I")
	private int primaryVarp = -1;

	@OriginalMember(owner = "client!ii", name = "V", descriptor = "Z")
	public boolean visible = true;

	@OriginalMember(owner = "client!ii", name = "h", descriptor = "Z")
	public boolean displayedOnMinimap = false;

	@OriginalMember(owner = "client!ii", name = "N", descriptor = "I")
	public int dashLineOffset = -1;

	@OriginalMember(owner = "client!ii", name = "T", descriptor = "I")
	public int minOffsetY = Integer.MAX_VALUE;

	@OriginalMember(owner = "client!ii", name = "R", descriptor = "I")
	public int mouseOverIconArchive = -1;

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(I)V")
	public void postDecode() {
		if (this.offsets == null) {
			return;
		}
		for (@Pc(16) int i = 0; i < this.offsets.length; i += 2) {
			if (this.minOffsetX > this.offsets[i]) {
				this.minOffsetX = this.offsets[i];
			} else if (this.offsets[i] > this.maxOffsetX) {
				this.maxOffsetX = this.offsets[i];
			}
			if (this.offsets[i + 1] < this.minOffsetY) {
				this.minOffsetY = this.offsets[i + 1];
			} else if (this.offsets[i + 1] > this.maxOffsetY) {
				this.maxOffsetY = this.offsets[i + 1];
			}
		}
	}

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(Lclient!dd;I)Z")
	public boolean shouldDraw(@OriginalArg(0) VarDomain varDomain) {
		@Pc(22) int primaryValue;
		if (this.primaryVarp == -1) {
			if (this.primaryVarpbit == -1) {
				return true;
			}
			primaryValue = varDomain.getVarBitValue(this.primaryVarpbit);
		} else {
			primaryValue = varDomain.getVarValueInt(this.primaryVarp);
		}
		if (primaryValue < this.primaryVariableMinValue || this.primaryVariableMaxValue < primaryValue) {
			return false;
		}
		@Pc(67) int secondaryValue;
		if (this.secondaryVarp == -1) {
			if (this.secondaryVarpbit == -1) {
				return true;
			}
			secondaryValue = varDomain.getVarBitValue(this.secondaryVarpbit);
		} else {
			secondaryValue = varDomain.getVarValueInt(this.secondaryVarp);
		}
		return this.secondaryVariableMinValue <= secondaryValue && this.secondaryVariableMaxValue >= secondaryValue;
	}

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(ZLclient!ha;B)Lclient!mi;")
	public NativeSprite getIcon(@OriginalArg(0) boolean mouseOver, @OriginalArg(1) Toolkit toolkit) {
		@Pc(11) int iconId = mouseOver ? this.mouseOverIconArchive : this.defaultIconArchive;
		@Pc(23) int cacheKey = iconId | toolkit.index << 29;
		@Pc(32) NativeSprite sprite = (NativeSprite) this.initialType.spriteCache.get((long) cacheKey);
		if (sprite != null) {
			return sprite;
		} else if (this.initialType.spriteResource.isFileReady(iconId)) {
			@Pc(52) SpriteType indexedImage = SpriteDataProvider.listSingle(this.initialType.spriteResource, iconId, 0);
			if (indexedImage != null) {
				sprite = toolkit.generateSprite(indexedImage, true);
				this.initialType.spriteCache.put(sprite, (long) cacheKey);
			}
			return sprite;
		} else {
			return null;
		}
	}

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(Lclient!ha;Z)Lclient!mi;")
	public NativeSprite getSprite(@OriginalArg(0) Toolkit toolkit) {
		@Pc(28) NativeSprite sprite = (NativeSprite) this.initialType.spriteCache.get((long) (this.spriteId | 0x20000 | toolkit.index << 29));
		if (sprite != null) {
			return sprite;
		}
		this.initialType.spriteResource.isFileReady(this.spriteId);
		@Pc(48) SpriteType indexedImage = SpriteDataProvider.listSingle(this.initialType.spriteResource, this.spriteId, 0);
		if (indexedImage != null) {
			sprite = toolkit.generateSprite(indexedImage, true);
			this.initialType.spriteCache.put(sprite, (long) (toolkit.index << 29 | this.spriteId | 0x20000));
		}
		return sprite;
	}

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(Ljava/lang/String;IB)Ljava/lang/String;")
	public String getStringParam(@OriginalArg(0) String string, @OriginalArg(1) int paramKey) {
		if (this.parameters == null) {
			return string;
		} else {
			@Pc(24) StringNode stringNode = (StringNode) this.parameters.get((long) paramKey);
			return stringNode == null ? string : stringNode.value;
		}
	}

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(ILclient!cea;I)V")
	private void decode(@OriginalArg(0) int opcode, @OriginalArg(1) Packet packet) {
		if (opcode == 1) {
			this.defaultIconArchive = packet.readUShort();
		} else if (opcode == 2) {
			this.mouseOverIconArchive = packet.readUShort();
		} else if (opcode == 3) {
			this.areaName = packet.readString();
		} else if (opcode == 4) {
			this.defaultTextColor = packet.readUMedium();
		} else if (opcode == 5) {
			this.mouseOverTextColor = packet.readUMedium();
		} else if (opcode == 6) {
			this.baseTextzoom = packet.readUByte();
		} else {
			@Pc(70) int local70;
			if (opcode == 7) {
				int displayFlags = packet.readUByte();
				if ((displayFlags & 0x1) == 0) {
					this.displayedOnWorldmap = false;
				}
				if ((displayFlags & 0x2) == 2) {
					this.displayedOnMinimap = true;
				}
			} else if (opcode == 8) {
				this.hasRandomisedMinimapPosition = packet.readUByte() == 1;
			} else if (opcode == 9) {
				this.primaryVarpbit = packet.readUShort();
				if (this.primaryVarpbit == 65535) {
					this.primaryVarpbit = -1;
				}
				this.primaryVarp = packet.readUShort();
				if (this.primaryVarp == 65535) {
					this.primaryVarp = -1;
				}
				this.primaryVariableMinValue = packet.readInt();
				this.primaryVariableMaxValue = packet.readInt();
			} else if (opcode >= 10 && opcode <= 14) {
				this.minimenuActions[opcode - 10] = packet.readString();
			} else {
				@Pc(141) int local141;
				@Pc(171) int local171;
				if (opcode == 15) {
					int pointCount = packet.readUByte();
					this.offsets = new int[pointCount * 2];
					for (int offsetIndex = 0; offsetIndex < pointCount * 2; offsetIndex++) {
						this.offsets[offsetIndex] = packet.readShort();
					}
					this.lineColor = packet.readInt();
					@Pc(165) int colorCount = packet.readUByte();
					this.colors = new int[colorCount];
					for (int colorIndex = 0; colorIndex < this.colors.length; colorIndex++) {
						this.colors[colorIndex] = packet.readInt();
					}
					this.colorPointers = new byte[pointCount];
					for (@Pc(196) int i = 0; i < pointCount; i++) {
						this.colorPointers[i] = packet.readByte();
					}
				} else if (opcode == 16) {
					this.visible = false;
				} else if (opcode == 17) {
					this.minimenuName = packet.readString();
				} else if (opcode == 18) {
					this.spriteId = packet.readUShort();
				} else if (opcode == 19) {
					this.categoryId = packet.readUShort();
				} else if (opcode == 20) {
					this.secondaryVarpbit = packet.readUShort();
					if (this.secondaryVarpbit == 65535) {
						this.secondaryVarpbit = -1;
					}
					this.secondaryVarp = packet.readUShort();
					if (this.secondaryVarp == 65535) {
						this.secondaryVarp = -1;
					}
					this.secondaryVariableMinValue = packet.readInt();
					this.secondaryVariableMaxValue = packet.readInt();
				} else if (opcode == 21) {
					this.outlineColor = packet.readInt();
				} else if (opcode == 22) {
					this.backgroundColor = packet.readInt();
				} else if (opcode == 23) {
					this.dashLineSpacing = packet.readUByte();
					this.dashLineLength = packet.readUByte();
					this.dashLineOffset = packet.readUByte();
				} else if (opcode == 24) {
					this.labelOffsetX = packet.readShort();
					this.labelOffsetY = packet.readShort();
				} else if (opcode == 249) {
					int paramCount = packet.readUByte();
					if (this.parameters == null) {
						int bucketCount = Utils.nextPowerOfTwo(paramCount);
						this.parameters = new IterableHashTable(bucketCount);
					}
					for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
						@Pc(364) boolean isString = packet.readUByte() == 1;
						int paramKey = packet.readUMedium();
						@Pc(377) Node node;
						if (isString) {
							node = new StringNode(packet.readString());
						} else {
							node = new IntNode(packet.readInt());
						}
						this.parameters.put((long) paramKey, node);
					}
				}
			}
		}
	}

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(IIZ)I")
	public int getIntParam(@OriginalArg(0) int defaultValue, @OriginalArg(1) int paramKey) {
		if (this.parameters == null) {
			return defaultValue;
		} else {
			@Pc(21) IntNode intNode = (IntNode) this.parameters.get((long) paramKey);
			return intNode == null ? defaultValue : intNode.value;
		}
	}

	@OriginalMember(owner = "client!ii", name = "a", descriptor = "(Lclient!cea;I)V")
	public void decode(@OriginalArg(0) Packet packet) {
		while (true) {
			@Pc(15) int opcode = packet.readUByte();
			if (opcode == 0) {
				return;
			}
			this.decode(opcode, packet);
		}
	}
}
