package com.jagex.game.runetek5.config.rsinterface;

import com.jagex.game.runetek5.Skybox;
import com.jagex.game.runetek5.graphics.sprite.SpriteType;
import com.jagex.game.runetek5.entity.ParticleSystem;
import com.jagex.game.runetek5.config.sequence.SeqType;
import com.jagex.game.runetek5.config.sequence.SeqTypeList;
import com.jagex.game.runetek5.cache.node.IntNode;
import com.jagex.game.runetek5.core.datastruct.key.IterableHashTable;
import com.jagex.game.runetek5.cache.node.Node;
import com.jagex.game.runetek5.cache.node.StringNode;
import com.jagex.game.runetek5.buffer.Packet;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerAppearance;
import com.jagex.game.runetek5.BrightnessTableBuilder;
import com.jagex.game.runetek5.config.bas.BasTypeList;
import com.jagex.game.runetek5.config.idktype.IDKTypeList;
import com.jagex.game.runetek5.config.rsinterface.IFEvents;
import com.jagex.game.runetek5.config.npctype.NPCTypeList;
import com.jagex.game.runetek5.config.item.ItemType;
import com.jagex.game.runetek5.config.objtype.ObjTypeList;
import com.jagex.game.runetek5.config.sun.SunTypeList;
import com.jagex.game.runetek5.config.skyboxtype.SkyBoxTypeList;
import com.jagex.game.runetek5.vardomain.VarDomain;
import com.jagex.game.runetek5.config.rsinterface.ComponentSurface;
import com.jagex.game.runetek5.graphics.font.AbstractFontRenderer;
import com.jagex.game.runetek5.graphics.Matrix;
import com.jagex.game.runetek5.graphics.model.Mesh;
import com.jagex.game.runetek5.graphics.model.Model;
import com.jagex.game.runetek5.graphics.sprite.NativeSprite;
import com.jagex.game.runetek5.graphics.SpriteDataProvider;
import com.jagex.game.runetek5.graphics.Toolkit;
import com.jagex.game.runetek5.graphics.model.particle.EffectiveVertex;
import com.jagex.game.runetek5.graphics.model.particle.EmissiveTriangle;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.cache.node.lru.LRUCache;
import com.jagex.game.runetek5.core.datastruct.key.Deque;
import com.jagex.game.runetek5.InterfaceManager;
import com.jagex.game.runetek5.InterfaceRectangles;
import com.jagex.game.runetek5.cache.node.HashTableIterator;
import com.jagex.game.runetek5.config.Resource;
import com.jagex.game.runetek5.graphics.font.FontManager;
import com.jagex.game.runetek5.graphics.ifcomponent.InterfaceRenderer;
import com.jagex.game.runetek5.cache.node.JavaObjSprite;
import com.jagex.game.runetek5.config.TypeLists;
import com.jagex.game.runetek5.entity.pathingentity.player.PlayerUpdate;
import com.jagex.game.runetek5.js5.Js5ResourceProvider;
import com.jagex.game.runetek5.client.ClientState;

@OriginalClass("client!at")
public final class Component {

	@OriginalMember(owner = "client!at", name = "Uc", descriptor = "[B")
	public static final byte[] normalizeTable = new byte[32896];

	@OriginalMember(owner = "client!at", name = "a", descriptor = "[Ljava/lang/Object;")
	public Object[] onStatTransmit;

	@OriginalMember(owner = "client!at", name = "d", descriptor = "I")
	public int lightPositionX;

	@OriginalMember(owner = "client!at", name = "g", descriptor = "[I")
	public int[] rowStartX;

	@OriginalMember(owner = "client!at", name = "i", descriptor = "[Ljava/lang/Object;")
	public Object[] onTargetLeave;

	@OriginalMember(owner = "client!at", name = "j", descriptor = "[Ljava/lang/Object;")
	public Object[] hookParams;

	@OriginalMember(owner = "client!at", name = "m", descriptor = "[Ljava/lang/Object;")
	public Object[] onMouseLeaveScript;

	@OriginalMember(owner = "client!at", name = "o", descriptor = "[Ljava/lang/Object;")
	public Object[] onKey;

	@OriginalMember(owner = "client!at", name = "p", descriptor = "[I")
	public int[] varps;

	@OriginalMember(owner = "client!at", name = "q", descriptor = "[Ljava/lang/Object;")
	public Object[] onCamFinished;

	@OriginalMember(owner = "client!at", name = "v", descriptor = "[Ljava/lang/Object;")
	public Object[] onDragComplete;

	@OriginalMember(owner = "client!at", name = "w", descriptor = "I")
	public int lightPositionZ;

	@OriginalMember(owner = "client!at", name = "E", descriptor = "[Ljava/lang/Object;")
	public Object[] onSubChange;

	@OriginalMember(owner = "client!at", name = "J", descriptor = "[I")
	public int[] opCursors;

	@OriginalMember(owner = "client!at", name = "K", descriptor = "[I")
	public int[] cycleSteps;

	@OriginalMember(owner = "client!at", name = "L", descriptor = "[Ljava/lang/Object;")
	public Object[] onScrollWheel;

	@OriginalMember(owner = "client!at", name = "O", descriptor = "Lclient!eq;")
	private IterableHashTable clientParams;

	@OriginalMember(owner = "client!at", name = "R", descriptor = "[Ljava/lang/Object;")
	public Object[] onTargetEnter;

	@OriginalMember(owner = "client!at", name = "Y", descriptor = "Ljava/lang/String;")
	public String name;

	@OriginalMember(owner = "client!at", name = "eb", descriptor = "[Ljava/lang/Object;")
	public Object[] onResize;

	@OriginalMember(owner = "client!at", name = "ib", descriptor = "[I")
	public int[] statTransmitFilter;

	@OriginalMember(owner = "client!at", name = "jb", descriptor = "I")
	public int serverTriggers;

	@OriginalMember(owner = "client!at", name = "kb", descriptor = "I")
	public int lightPositionY;

	@OriginalMember(owner = "client!at", name = "lb", descriptor = "[Ljava/lang/Object;")
	public Object[] onFriendTransmit;

	@OriginalMember(owner = "client!at", name = "mb", descriptor = "Z")
	public boolean flipHorizontal;

	@OriginalMember(owner = "client!at", name = "rb", descriptor = "Lclient!uj;")
	public ParticleSystem particleSystem;

	@OriginalMember(owner = "client!at", name = "sb", descriptor = "[Ljava/lang/Object;")
	public Object[] params;

	@OriginalMember(owner = "client!at", name = "tb", descriptor = "[Ljava/lang/Object;")
	public Object[] onRelease;

	@OriginalMember(owner = "client!at", name = "xb", descriptor = "[B")
	public byte[] keyTriggeringAction;

	@OriginalMember(owner = "client!at", name = "Cb", descriptor = "[Ljava/lang/Object;")
	public Object[] mouseLeaveScript;

	@OriginalMember(owner = "client!at", name = "Eb", descriptor = "I")
	public int primaryMediaId;

	@OriginalMember(owner = "client!at", name = "Kb", descriptor = "I")
	public int sunRotationX;

	@OriginalMember(owner = "client!at", name = "Mb", descriptor = "[Ljava/lang/Object;")
	public Object[] onClanTransmit;

	@OriginalMember(owner = "client!at", name = "Ob", descriptor = "[Ljava/lang/Object;")
	public Object[] onClick;

	@OriginalMember(owner = "client!at", name = "Pb", descriptor = "[Ljava/lang/Object;")
	public Object[] onTimer;

	@OriginalMember(owner = "client!at", name = "Wb", descriptor = "[I")
	public int[] mouseLeaveArrayParams;

	@OriginalMember(owner = "client!at", name = "Xb", descriptor = "[I")
	public int[] rowWidth;

	@OriginalMember(owner = "client!at", name = "Zb", descriptor = "I")
	public ComponentType type = ComponentType.CONTAINER;

	@OriginalMember(owner = "client!at", name = "cc", descriptor = "[Ljava/lang/Object;")
	public Object[] onVarpTransmit;

	@OriginalMember(owner = "client!at", name = "jc", descriptor = "Z")
	public boolean hasOrigin;

	@OriginalMember(owner = "client!at", name = "kc", descriptor = "[Ljava/lang/Object;")
	public Object[] onClickRepeat;

	@OriginalMember(owner = "client!at", name = "pc", descriptor = "Z")
	public boolean flipVertical;

	@OriginalMember(owner = "client!at", name = "xc", descriptor = "[Lclient!at;")
	public Component[] slotChildren;

	@OriginalMember(owner = "client!at", name = "Ac", descriptor = "[I")
	public int[] cycles;

	@OriginalMember(owner = "client!at", name = "Cc", descriptor = "[Ljava/lang/Object;")
	public Object[] onMouseMove;

	@OriginalMember(owner = "client!at", name = "Ec", descriptor = "[Ljava/lang/Object;")
	public Object[] onHold;

	@OriginalMember(owner = "client!at", name = "Gc", descriptor = "[Ljava/lang/Object;")
	public Object[] onStockTransmit;

	@OriginalMember(owner = "client!at", name = "Kc", descriptor = "[Ljava/lang/Object;")
	public Object[] onMouseOver;

	@OriginalMember(owner = "client!at", name = "Oc", descriptor = "[Ljava/lang/Object;")
	public Object[] onLoadScript;

	@OriginalMember(owner = "client!at", name = "Pc", descriptor = "Ljava/lang/String;")
	public String resumeText;

	@OriginalMember(owner = "client!at", name = "Rc", descriptor = "[I")
	public int[] keyPressArray;

	@OriginalMember(owner = "client!at", name = "Tc", descriptor = "[Ljava/lang/Object;")
	public Object[] onChatTransmit;

	@OriginalMember(owner = "client!at", name = "Vc", descriptor = "[Ljava/lang/Object;")
	public Object[] onKeyType;

	@OriginalMember(owner = "client!at", name = "Wc", descriptor = "[Ljava/lang/Object;")
	public Object[] onDrag;

	@OriginalMember(owner = "client!at", name = "ed", descriptor = "[I")
	public int[] mouseWheelArray;

	@OriginalMember(owner = "client!at", name = "fd", descriptor = "[B")
	public byte[] keyTriggerParams;

	@OriginalMember(owner = "client!at", name = "gd", descriptor = "[Ljava/lang/String;")
	public String[] options;

	@OriginalMember(owner = "client!at", name = "id", descriptor = "I")
	public int sunRotationY;

	@OriginalMember(owner = "client!at", name = "jd", descriptor = "[Ljava/lang/Object;")
	public Object[] onMiscTransmit;

	@OriginalMember(owner = "client!at", name = "md", descriptor = "[Ljava/lang/Object;")
	public Object[] popupScript;

	@OriginalMember(owner = "client!at", name = "nd", descriptor = "[Ljava/lang/Object;")
	public Object[] onSubClose;

	@OriginalMember(owner = "client!at", name = "c", descriptor = "I")
	public int lineSpacing = 0;

	@OriginalMember(owner = "client!at", name = "h", descriptor = "I")
	public int renderStack = 2;

	@OriginalMember(owner = "client!at", name = "r", descriptor = "I")
	public int originX = 0;

	@OriginalMember(owner = "client!at", name = "z", descriptor = "I")
	public int transparency = 0;

	@OriginalMember(owner = "client!at", name = "F", descriptor = "I")
	public int fontId = -1;

	@OriginalMember(owner = "client!at", name = "y", descriptor = "I")
	public int targetLeaveCursor = -1;

	@OriginalMember(owner = "client!at", name = "D", descriptor = "I")
	public int textVerticalAli = 0;

	@OriginalMember(owner = "client!at", name = "f", descriptor = "Z")
	public boolean hasInteraction = false;

	@OriginalMember(owner = "client!at", name = "db", descriptor = "I")
	public int lastServerTick = -1;

	@OriginalMember(owner = "client!at", name = "l", descriptor = "Z")
	public boolean hasScript = false;

	@OriginalMember(owner = "client!at", name = "t", descriptor = "I")
	public int lineWidth = 1;

	@OriginalMember(owner = "client!at", name = "T", descriptor = "I")
	public int spriteScale = 100;

	@OriginalMember(owner = "client!at", name = "Ib", descriptor = "I")
	public int spritePitch = 0;

	@OriginalMember(owner = "client!at", name = "H", descriptor = "I")
	public int slotId = -1;

	@OriginalMember(owner = "client!at", name = "Bb", descriptor = "I")
	public int y = 0;

	@OriginalMember(owner = "client!at", name = "e", descriptor = "I")
	public int spriteRoll = 0;

	@OriginalMember(owner = "client!at", name = "Ab", descriptor = "I")
	public int color = 0;

	@OriginalMember(owner = "client!at", name = "Hb", descriptor = "I")
	public int spriteId = -1;

	@OriginalMember(owner = "client!at", name = "N", descriptor = "I")
	public int aspectX = 1;

	@OriginalMember(owner = "client!at", name = "pb", descriptor = "I")
	public int lastKeyCycle = 0;

	@OriginalMember(owner = "client!at", name = "b", descriptor = "I")
	public int borderThickness = 0;

	@OriginalMember(owner = "client!at", name = "hb", descriptor = "I")
	public int nextFrameId = 1;

	@OriginalMember(owner = "client!at", name = "Fb", descriptor = "Z")
	public boolean priorityRender = false;

	@OriginalMember(owner = "client!at", name = "yb", descriptor = "I")
	public int scrollX = 0;

	@OriginalMember(owner = "client!at", name = "ab", descriptor = "I")
	public int lastContainerTransmitCycle = 0;

	@OriginalMember(owner = "client!at", name = "bc", descriptor = "Z")
	public boolean preventClickThrough = false;

	@OriginalMember(owner = "client!at", name = "nc", descriptor = "I")
	public int basePositionX = 0;

	@OriginalMember(owner = "client!at", name = "C", descriptor = "I")
	public int animation = -1;

	@OriginalMember(owner = "client!at", name = "Ub", descriptor = "Z")
	public boolean shadow = false;

	@OriginalMember(owner = "client!at", name = "X", descriptor = "Z")
	public boolean wearCol = false;

	@OriginalMember(owner = "client!at", name = "Q", descriptor = "I")
	public int targetOpCursor = -1;

	@OriginalMember(owner = "client!at", name = "qb", descriptor = "I")
	public int basePositionY = 0;

	@OriginalMember(owner = "client!at", name = "gb", descriptor = "I")
	public int dragDeadzone = 0;

	@OriginalMember(owner = "client!at", name = "fb", descriptor = "I")
	public int baseHeight = 0;

	@OriginalMember(owner = "client!at", name = "wc", descriptor = "I")
	public int dragType = Component.DRAG_RENDER_TRANSLUCENT;

	@OriginalMember(owner = "client!at", name = "ec", descriptor = "I")
	public int secondaryMediaId = -1;

	@OriginalMember(owner = "client!at", name = "M", descriptor = "I")
	public int width = 0;

	@OriginalMember(owner = "client!at", name = "ac", descriptor = "I")
	public int aspectY = 1;

	@OriginalMember(owner = "client!at", name = "W", descriptor = "I")
	public int moveOverCursor = -1;

	@OriginalMember(owner = "client!at", name = "sc", descriptor = "Z")
	public boolean isPressed = false;

	@OriginalMember(owner = "client!at", name = "qc", descriptor = "I")
	public int scrollHeight = 0;

	@OriginalMember(owner = "client!at", name = "S", descriptor = "I")
	public int lastStatTransmitCycle = 0;

	@OriginalMember(owner = "client!at", name = "s", descriptor = "Z")
	public boolean usesOrthogonal = false;

	@OriginalMember(owner = "client!at", name = "A", descriptor = "Z")
	public boolean tiling = false;

	@OriginalMember(owner = "client!at", name = "Hc", descriptor = "I")
	public int offsetX = 0;

	@OriginalMember(owner = "client!at", name = "Qb", descriptor = "I")
	public int contentType = 0;

	@OriginalMember(owner = "client!at", name = "Gb", descriptor = "I")
	public int scrollY = 0;

	@OriginalMember(owner = "client!at", name = "zb", descriptor = "Lclient!wb;")
	public IFEvents targetParams = Component.DEFAULT_SETTINGS;

	@OriginalMember(owner = "client!at", name = "lc", descriptor = "I")
	public int offsetY = 0;

	@OriginalMember(owner = "client!at", name = "Lb", descriptor = "I")
	public int cacheReference = -1;

	@OriginalMember(owner = "client!at", name = "zc", descriptor = "I")
	public int x = 0;

	@OriginalMember(owner = "client!at", name = "Vb", descriptor = "I")
	public MediaType mediaType = MediaType.RAW_MODEL;

	@OriginalMember(owner = "client!at", name = "dc", descriptor = "B")
	public byte aspectWidthType = 0;

	@OriginalMember(owner = "client!at", name = "Jc", descriptor = "Z")
	public boolean filled = false;

	@OriginalMember(owner = "client!at", name = "bb", descriptor = "B")
	public byte aspectHeightType = 0;

	@OriginalMember(owner = "client!at", name = "hc", descriptor = "Lclient!at;")
	public Component depthChildOverride = null;

	@OriginalMember(owner = "client!at", name = "Yb", descriptor = "I")
	public int maxTextLines = 0;

	@OriginalMember(owner = "client!at", name = "tc", descriptor = "I")
	public int lastRenderedCycle = -1;

	@OriginalMember(owner = "client!at", name = "cb", descriptor = "I")
	public int lastVarpTransmitCycle = 0;

	@OriginalMember(owner = "client!at", name = "yc", descriptor = "I")
	public int originY = 0;

	@OriginalMember(owner = "client!at", name = "ob", descriptor = "Z")
	public boolean isHooked = false;

	@OriginalMember(owner = "client!at", name = "G", descriptor = "I")
	public int dragDeadTime = 0;

	@OriginalMember(owner = "client!at", name = "bd", descriptor = "I")
	public int angle2d = 0;

	@OriginalMember(owner = "client!at", name = "Db", descriptor = "Ljava/lang/String;")
	public String opBase = "";

	@OriginalMember(owner = "client!at", name = "wb", descriptor = "Z")
	public boolean alpha = false;

	@OriginalMember(owner = "client!at", name = "B", descriptor = "I")
	public int scrollWidth = 0;

	@OriginalMember(owner = "client!at", name = "rc", descriptor = "Ljava/lang/String;")
	public String text = "";

	@OriginalMember(owner = "client!at", name = "Nc", descriptor = "I")
	public int lastMouseWheelCycle = 0;

	@OriginalMember(owner = "client!at", name = "gc", descriptor = "I")
	public int itemStackSize = 0;

	@OriginalMember(owner = "client!at", name = "Mc", descriptor = "Z")
	public boolean hidden = false;

	@OriginalMember(owner = "client!at", name = "Xc", descriptor = "I")
	public int targetOverCursor = -1;

	@OriginalMember(owner = "client!at", name = "dd", descriptor = "I")
	public int idHash = -1;

	@OriginalMember(owner = "client!at", name = "k", descriptor = "B")
	public byte aspectYType = 0;

	@OriginalMember(owner = "client!at", name = "Ic", descriptor = "I")
	public int spriteShadow = 0;

	@OriginalMember(owner = "client!at", name = "hd", descriptor = "I")
	public int parentId = -1;

	@OriginalMember(owner = "client!at", name = "Nb", descriptor = "I")
	public int textHorizontalAli = 0;

	@OriginalMember(owner = "client!at", name = "oc", descriptor = "I")
	public int height = 0;

	@OriginalMember(owner = "client!at", name = "fc", descriptor = "Ljava/lang/String;")
	public String targetVerb = "";

	@OriginalMember(owner = "client!at", name = "vb", descriptor = "Z")
	public boolean lineDirection = false;

	@OriginalMember(owner = "client!at", name = "Zc", descriptor = "I")
	public int aspectHeight = 0;

	@OriginalMember(owner = "client!at", name = "mc", descriptor = "I")
	public int originZ = 0;

	@OriginalMember(owner = "client!at", name = "I", descriptor = "I")
	public int skyboxId = -1;

	@OriginalMember(owner = "client!at", name = "uc", descriptor = "I")
	public int currentFrameId = 0;

	@OriginalMember(owner = "client!at", name = "Lc", descriptor = "I")
	public int containerItemId = -1;

	@OriginalMember(owner = "client!at", name = "U", descriptor = "I")
	public int spriteYaw = 0;

	@OriginalMember(owner = "client!at", name = "Fc", descriptor = "I")
	public int videoFileId = -1;

	@OriginalMember(owner = "client!at", name = "pd", descriptor = "I")
	public int baseWidth = 0;

	@OriginalMember(owner = "client!at", name = "vc", descriptor = "I")
	public int aspectWidth = 0;

	@OriginalMember(owner = "client!at", name = "od", descriptor = "I")
	public int currentFrameDuration = 0;

	@OriginalMember(owner = "client!at", name = "Qc", descriptor = "B")
	public byte aspectXType = 0;

	static {
		@Pc(4) int index = 0;
		for (@Pc(6) int y = 0; y < 256; y++) {
			for (@Pc(9) int x = 0; x <= y; x++) {
				normalizeTable[index++] = (byte) (255.0D / Math.sqrt((double) ((float) (y * y + x * x + 65535) / 65535.0F)));
			}
		}
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(Lclient!ha;B)Lclient!aa;")
	public ComponentSurface getClippingMask(@OriginalArg(0) Toolkit toolkit) {
		@Pc(13) ComponentSurface clippingMask = (ComponentSurface) Component.aClass169_13.get((long) this.idHash);
		if (clippingMask != null) {
			return clippingMask;
		}
		@Pc(23) SpriteType indexedImage = SpriteDataProvider.listSingle(Component.packet, this.spriteId, 0);
		if (indexedImage == null) {
			return null;
		}
		@Pc(36) int sizeX = indexedImage.minX + indexedImage.width + indexedImage.borderX;
		@Pc(45) int sizeY = indexedImage.minY + indexedImage.height + indexedImage.borderY;
		this.rowWidth = new int[sizeY];
		this.rowStartX = new int[sizeY];
		for (@Pc(55) int i = 0; i < indexedImage.height; i++) {
			@Pc(59) int clipLeft = 0;
			for (@Pc(61) int j = 0; j < indexedImage.width; j++) {
				if (indexedImage.pixels[indexedImage.width * i + j] != 0) {
					clipLeft = j;
					break;
				}
			}
			@Pc(88) int clipRight = sizeX;
			for (@Pc(90) int k = clipLeft; k < indexedImage.width; k++) {
				if (indexedImage.pixels[indexedImage.width * i + k] == 0) {
					clipRight = k;
					break;
				}
			}
			this.rowStartX[indexedImage.minY + i] = indexedImage.minX + clipLeft;
			this.rowWidth[i + indexedImage.minY] = clipRight - clipLeft;
		}
		clippingMask = toolkit.createMask(sizeX, sizeY, this.rowStartX, this.rowWidth);
		Component.aClass169_13.put(clippingMask, (long) this.idHash);
		return clippingMask;
	}

	@OriginalMember(owner = "client!at", name = "b", descriptor = "(Lclient!ha;B)Lclient!da;")
	public AbstractFontRenderer getFontRenderer(@OriginalArg(0) Toolkit toolkit) {
		@Pc(10) AbstractFontRenderer font = FontManager.getFont(toolkit, false, this.fontId);
		Component.missingMedia = font == null;
		return font;
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(III)I")
	public int getIntParam(@OriginalArg(0) int defaultValue, @OriginalArg(1) int paramKey) {
		if (this.clientParams == null) {
			return defaultValue;
		} else {
			@Pc(22) IntNode intNode = (IntNode) this.clientParams.get((long) paramKey);
			return intNode == null ? defaultValue : intNode.value;
		}
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(ILjava/lang/String;I)Ljava/lang/String;")
	public String getStringParam(@OriginalArg(0) int paramKey, @OriginalArg(1) String defaultValue) {
		if (this.clientParams == null) {
			return defaultValue;
		} else {
			@Pc(26) StringNode stringNode = (StringNode) this.clientParams.get((long) paramKey);
			return stringNode == null ? defaultValue : stringNode.value;
		}
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(Lclient!oha;Lclient!lfa;ILclient!ha;IILclient!ub;Lclient!dd;ILclient!oo;Lclient!hg;Lclient!uga;Lclient!vda;I)Lclient!ka;")
	public Model getAnimatedMediaModel(@OriginalArg(0) IDKTypeList idkTypeList, @OriginalArg(1) SeqTypeList seqTypeList, @OriginalArg(2) int functionMask, @OriginalArg(3) Toolkit toolkit, @OriginalArg(4) int currentFrameId, @OriginalArg(5) int currentFrameDuration, @OriginalArg(6) ObjTypeList objTypeList, @OriginalArg(7) VarDomain varDomain, @OriginalArg(9) PlayerAppearance playerAppearance, @OriginalArg(10) SeqType seqType, @OriginalArg(11) BasTypeList basTypeList, @OriginalArg(12) NPCTypeList npcTypeList, @OriginalArg(13) int nextFrameId) {
		Component.missingMedia = false;
		if (this.mediaType == MediaType.UNDEFINED) {
			return null;
		} else if (this.mediaType == MediaType.RAW_MODEL && this.primaryMediaId == -1) {
			return null;
		} else {
			@Pc(65) Model model;
			if (this.mediaType == MediaType.RAW_MODEL) {
				if (seqType != null) {
					functionMask = seqType.getFunctionMask(nextFrameId, currentFrameId, true) | 0x800;
				}
				@Pc(59) long uid = (long) (this.primaryMediaId + (this.mediaType.id << 16) + (toolkit.index << 29));
				model = (Model) Component.MODEL_CACHE.get(uid);
				if (model == null || toolkit.compareFunctionMasks(model.ua(), functionMask) != 0) {
					if (model != null) {
						functionMask = toolkit.combineFunctionMasks(functionMask, model.ua());
					}
					@Pc(88) Mesh mesh = Mesh.fromResource(Resource.aClass157_291, this.primaryMediaId);
					if (mesh == null) {
						Component.missingMedia = true;
						return null;
					}
					if (mesh.version < 13) {
						mesh.upscale();
					}
					model = toolkit.createModel(mesh, functionMask, Interface.preferenceMask, 64, 768);
					Component.MODEL_CACHE.put(model, uid);
				}
				if (seqType != null) {
					model = seqType.animateModel(model, nextFrameId, currentFrameDuration, functionMask, currentFrameId);
				}
				model.s(2048);
				return model;
			}
			@Pc(153) Model model2;
			if (this.mediaType == MediaType.NPC_HEAD) {
				model2 = npcTypeList.list(this.primaryMediaId).getAnimatedHeadModel(varDomain, toolkit, currentFrameDuration, nextFrameId, seqType, seqTypeList, currentFrameId);
				if (model2 == null) {
					Component.missingMedia = true;
					return null;
				} else {
					return model2;
				}
			} else if (this.mediaType == MediaType.PLAYER_HEAD) {
				if (playerAppearance == null) {
					return null;
				}
				model2 = playerAppearance.getAnimatedHeadModel(objTypeList, seqTypeList, idkTypeList, varDomain, npcTypeList, currentFrameId, nextFrameId, seqType, currentFrameDuration, toolkit);
				if (model2 == null) {
					Component.missingMedia = true;
					return null;
				} else {
					return model2;
				}
			} else if (this.mediaType == MediaType.ITEM) {
				@Pc(216) ItemType objType = objTypeList.list(this.primaryMediaId);
				@Pc(228) Model model3 = objType.getAnimatedModel(playerAppearance, seqType, toolkit, 2048, currentFrameDuration, 10, currentFrameId, nextFrameId);
				if (model3 == null) {
					Component.missingMedia = true;
					return null;
				} else {
					return model3;
				}
			} else if (this.mediaType == MediaType.NPC_MODEL) {
				model2 = npcTypeList.list(this.primaryMediaId).getBodyModel(0, null, seqTypeList, null, 0, basTypeList, currentFrameId, seqType, varDomain, toolkit, 0, null, nextFrameId, 0, 2048, currentFrameDuration);
				if (model2 == null) {
					Component.missingMedia = true;
					return null;
				} else {
					return model2;
				}
			} else if (this.mediaType != MediaType.PLAYER_HEAD_IGNOREWORN) {
				return null;
			} else if (playerAppearance == null) {
				return null;
			} else {
				@Pc(287) int primaryHead = this.primaryMediaId >>> 16;
				@Pc(292) int secondaryHead = this.primaryMediaId & 0xFFFF;
				@Pc(295) int tertiaryHead = this.secondaryMediaId;
				model = playerAppearance.getPlainAnimatedHeadModel(nextFrameId, tertiaryHead, secondaryHead, seqTypeList, idkTypeList, primaryHead, currentFrameDuration, toolkit, currentFrameId, seqType);
				if (model == null) {
					Component.missingMedia = true;
					return null;
				} else {
					return model;
				}
			}
		}
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(IIB)V")
	public void addCursor(@OriginalArg(0) int index, @OriginalArg(1) int cursor) {
		if (this.opCursors == null || this.opCursors.length <= index) {
			@Pc(22) int[] cursors = new int[index + 1];
			if (this.opCursors != null) {
				for (@Pc(27) int i = 0; i < this.opCursors.length; i++) {
					cursors[i] = this.opCursors[i];
				}
				for (@Pc(50) int length = this.opCursors.length; length < index; length++) {
					cursors[length] = -1;
				}
			}
			this.opCursors = cursors;
		}
		this.opCursors[index] = cursor;
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(Lclient!cea;I)[Ljava/lang/Object;")
	private Object[] decodeScript(@OriginalArg(0) Packet packet) {
		@Pc(14) int argumentCount = packet.readUByte();
		if (argumentCount == 0) {
			return null;
		}
		@Pc(21) Object[] objects = new Object[argumentCount];
		for (@Pc(23) int i = 0; i < argumentCount; i++) {
			@Pc(29) int valueType = packet.readUByte();
			if (valueType == 0) {
				objects[i] = Integer.valueOf(packet.readInt());
			} else if (valueType == 1) {
				objects[i] = packet.readString();
			}
		}
		this.hasScript = true;
		return objects;
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(Lclient!cea;Z)V")
	public void decode(@OriginalArg(0) Packet packet) {
		@Pc(9) int formatVersion = packet.readUByte();
		if (formatVersion == 255) {
			formatVersion = -1;
		}
		int type = packet.readUByte();
		if ((type & 0x80) != 0) {
			type &= 0x7F;
			this.name = packet.readString();
		}
		this.type = ComponentType.fromId(type);
		this.contentType = packet.readUShort();
		this.basePositionX = packet.readShort();
		this.basePositionY = packet.readShort();
		this.baseWidth = packet.readUShort();
		this.baseHeight = packet.readUShort();
		this.aspectWidthType = packet.readByte();
		this.aspectHeightType = packet.readByte();
		this.aspectXType = packet.readByte();
		this.aspectYType = packet.readByte();
		this.parentId = packet.readUShort();
		if (this.parentId == 65535) {
			this.parentId = -1;
		} else {
			this.parentId += this.idHash & 0xFFFF0000;
		}
		@Pc(107) int interactionMask = packet.readUByte();
		if (formatVersion >= 0) {
			this.preventClickThrough = (interactionMask & 0x2) != 0;
		}
		this.hidden = (interactionMask & 0x1) != 0;
		if (this.type == ComponentType.CONTAINER) {
			this.scrollWidth = packet.readUShort();
			this.scrollHeight = packet.readUShort();
			if (formatVersion < 0) {
				this.preventClickThrough = packet.readUByte() == 1;
			}
		}
		@Pc(184) int local184;
		if (this.type == ComponentType.SPRITE) {
			this.spriteId = packet.readInt();
			this.angle2d = packet.readUShort();
			int spriteFlags = packet.readUByte();
			this.tiling = (spriteFlags & 0x1) != 0;
			this.alpha = (spriteFlags & 0x2) != 0;
			this.transparency = packet.readUByte();
			this.borderThickness = packet.readUByte();
			this.spriteShadow = packet.readInt();
			this.flipVertical = packet.readUByte() == 1;
			this.flipHorizontal = packet.readUByte() == 1;
			this.color = packet.readInt();
		}
		if (this.type == ComponentType.MODEL) {
			this.mediaType = MediaType.RAW_MODEL;
			this.primaryMediaId = packet.readUShort();
			if (this.primaryMediaId == 65535) {
				this.primaryMediaId = -1;
			}
			int modelFlags = packet.readUByte();
			this.hasOrigin = (modelFlags & 0x2) == 2;
			@Pc(293) boolean hasTransform = (modelFlags & 0x1) == 1;
			this.usesOrthogonal = (modelFlags & 0x4) == 4;
			this.priorityRender = (modelFlags & 0x8) == 8;
			if (hasTransform) {
				this.originX = packet.readShort();
				this.originY = packet.readShort();
				this.spritePitch = packet.readUShort();
				this.spriteRoll = packet.readUShort();
				this.spriteYaw = packet.readUShort();
				this.spriteScale = packet.readUShort();
			} else if (this.hasOrigin) {
				this.originX = packet.readShort();
				this.originY = packet.readShort();
				this.originZ = packet.readShort();
				this.spritePitch = packet.readUShort();
				this.spriteRoll = packet.readUShort();
				this.spriteYaw = packet.readUShort();
				this.spriteScale = packet.readShort();
			}
			this.animation = packet.readUShort();
			if (this.animation == 65535) {
				this.animation = -1;
			}
			if (this.aspectWidthType != 0) {
				this.aspectWidth = packet.readUShort();
			}
			if (this.aspectHeightType != 0) {
				this.aspectHeight = packet.readUShort();
			}
		}
		if (this.type == ComponentType.TEXT) {
			this.fontId = packet.readUShort();
			if (this.fontId == 65535) {
				this.fontId = -1;
			}
			this.text = packet.readString();
			this.lineSpacing = packet.readUByte();
			this.textHorizontalAli = packet.readUByte();
			this.textVerticalAli = packet.readUByte();
			this.shadow = packet.readUByte() == 1;
			this.color = packet.readInt();
			this.transparency = packet.readUByte();
			if (formatVersion >= 0) {
				this.maxTextLines = packet.readUByte();
			}
		}
		if (this.type == ComponentType.FIGURE) {
			this.color = packet.readInt();
			this.filled = packet.readUByte() == 1;
			this.transparency = packet.readUByte();
		}
		if (this.type == ComponentType.LINE) {
			this.lineWidth = packet.readUByte();
			this.color = packet.readInt();
			this.lineDirection = packet.readUByte() == 1;
		}
		int events = packet.readUMedium();
		@Pc(547) int keyTriggerData = packet.readUByte();
		@Pc(572) int local572;
		if (keyTriggerData != 0) {
			this.keyTriggerParams = new byte[11];
			this.keyTriggeringAction = new byte[11];
			this.cycleSteps = new int[11];
			while (keyTriggerData != 0) {
				int triggerIndex = (keyTriggerData >> 4) - 1;
				keyTriggerData = keyTriggerData << 8 | packet.readUByte();
				keyTriggerData &= 0xFFF;
				if (keyTriggerData == 4095) {
					keyTriggerData = -1;
				}
				@Pc(595) byte triggerAction = packet.readByte();
				if (triggerAction != 0) {
					this.hasInteraction = true;
				}
				@Pc(604) byte triggerParam = packet.readByte();
				this.cycleSteps[triggerIndex] = keyTriggerData;
				this.keyTriggeringAction[triggerIndex] = triggerAction;
				this.keyTriggerParams[triggerIndex] = triggerParam;
				keyTriggerData = packet.readUByte();
			}
		}
		this.opBase = packet.readString();
		int opCounts = packet.readUByte();
		@Pc(641) int optionCount = opCounts & 0xF;
		@Pc(652) int local652;
		if (optionCount > 0) {
			this.options = new String[optionCount];
			for (int optionIndex = 0; optionIndex < optionCount; optionIndex++) {
				this.options[optionIndex] = packet.readString();
			}
		}
		@Pc(669) int cursorCount = opCounts >> 4;
		@Pc(686) int local686;
		if (cursorCount > 0) {
			int cursorIndex = packet.readUByte();
			this.opCursors = new int[cursorIndex + 1];
			for (int cursorSlot = 0; cursorSlot < this.opCursors.length; cursorSlot++) {
				this.opCursors[cursorSlot] = -1;
			}
			this.opCursors[cursorIndex] = packet.readUShort();
		}
		if (cursorCount > 1) {
			int cursorIndex = packet.readUByte();
			this.opCursors[cursorIndex] = packet.readUShort();
		}
		this.resumeText = packet.readString();
		if (this.resumeText.equals("")) {
			this.resumeText = null;
		}
		this.dragDeadzone = packet.readUByte();
		this.dragDeadTime = packet.readUByte();
		this.dragType = packet.readUByte();
		this.targetVerb = packet.readString();
		int targetParam = -1;
		if (IFEvents.getUseOptionFlags(events) != 0) {
			targetParam = packet.readUShort();
			this.targetOverCursor = packet.readUShort();
			if (targetParam == 65535) {
				targetParam = -1;
			}
			if (this.targetOverCursor == 65535) {
				this.targetOverCursor = -1;
			}
			this.targetLeaveCursor = packet.readUShort();
			if (this.targetLeaveCursor == 65535) {
				this.targetLeaveCursor = -1;
			}
		}
		if (formatVersion >= 0) {
			this.moveOverCursor = packet.readUShort();
			if (this.moveOverCursor == 65535) {
				this.moveOverCursor = -1;
			}
		}
		this.targetParams = new IFEvents(events, targetParam);
		if (formatVersion >= 0) {
			int intParamCount = packet.readUByte();
			@Pc(833) int local833;
			@Pc(837) int local837;
			for (@Pc(827) int i = 0; i < intParamCount; i++) {
				int intParamKey = packet.readUMedium();
				int intParamValue = packet.readInt();
				this.clientParams.put((long) intParamKey, new IntNode(intParamValue));
			}
			int stringParamCount = packet.readUByte();
			for (int stringParamIndex = 0; stringParamIndex < stringParamCount; stringParamIndex++) {
				@Pc(867) int paramKey = packet.readUMedium();
				@Pc(871) String string = packet.readJagString();
				this.clientParams.put((long) paramKey, new StringNode(string));
			}
		}
		this.onLoadScript = this.decodeScript(packet);
		this.onMouseOver = this.decodeScript(packet);
		this.onMouseLeaveScript = this.decodeScript(packet);
		this.hookParams = this.decodeScript(packet);
		this.onTargetEnter = this.decodeScript(packet);
		this.onVarpTransmit = this.decodeScript(packet);
		this.mouseLeaveScript = this.decodeScript(packet);
		this.onStatTransmit = this.decodeScript(packet);
		this.onTimer = this.decodeScript(packet);
		this.params = this.decodeScript(packet);
		if (formatVersion >= 0) {
			this.onTargetLeave = this.decodeScript(packet);
		}
		this.popupScript = this.decodeScript(packet);
		this.onClick = this.decodeScript(packet);
		this.onClickRepeat = this.decodeScript(packet);
		this.onRelease = this.decodeScript(packet);
		this.onHold = this.decodeScript(packet);
		this.onDrag = this.decodeScript(packet);
		this.onDragComplete = this.decodeScript(packet);
		this.onMouseMove = this.decodeScript(packet);
		this.onKey = this.decodeScript(packet);
		this.onScrollWheel = this.decodeScript(packet);
		this.varps = this.decodeScript3(packet);
		this.mouseLeaveArrayParams = this.decodeScript3(packet);
		this.statTransmitFilter = this.decodeScript3(packet);
		this.keyPressArray = this.decodeScript3(packet);
		this.mouseWheelArray = this.decodeScript3(packet);
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(Z)V")
	public void clearScriptHooks() {
		this.mouseLeaveArrayParams = null;
		this.statTransmitFilter = null;
		this.onChatTransmit = null;
		this.varps = null;
		this.popupScript = null;
		this.onClick = null;
		this.mouseWheelArray = null;
		this.params = null;
		this.onTimer = null;
		this.onMiscTransmit = null;
		this.onSubChange = null;
		this.mouseLeaveScript = null;
		this.onKeyType = null;
		this.onTargetEnter = null;
		this.onStatTransmit = null;
		this.onClickRepeat = null;
		this.onResize = null;
		this.onMouseOver = null;
		this.onMouseLeaveScript = null;
		this.keyPressArray = null;
		this.onScrollWheel = null;
		this.onCamFinished = null;
		this.onMouseMove = null;
		this.onDrag = null;
		this.onHold = null;
		this.onKey = null;
		this.onLoadScript = null;
		this.onDragComplete = null;
		this.onTargetLeave = null;
		this.onRelease = null;
		this.onSubClose = null;
		this.onVarpTransmit = null;
		this.onStockTransmit = null;
		this.onClanTransmit = null;
		this.onFriendTransmit = null;
		this.hookParams = null;
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(II)V")
	public void removeParam(@OriginalArg(0) int paramKey) {
		if (this.clientParams != null) {
			@Pc(13) Node node = this.clientParams.get((long) paramKey);
			if (node != null) {
				node.unlink();
			}
		}
	}

	@OriginalMember(owner = "client!at", name = "b", descriptor = "(III)V")
	public void setIntParam(@OriginalArg(1) int paramKey, @OriginalArg(2) int paramValue) {
		if (this.clientParams == null) {
			this.clientParams = new IterableHashTable(16);
			this.clientParams.put((long) paramKey, new IntNode(paramValue));
			return;
		}
		@Pc(35) IntNode intNode = (IntNode) this.clientParams.get((long) paramKey);
		if (intNode == null) {
			this.clientParams.put((long) paramKey, new IntNode(paramValue));
		} else {
			intNode.value = paramValue;
		}
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(ILclient!ka;Lclient!ha;ILclient!bca;)V")
	public void cycleParticles(@OriginalArg(1) Model model, @OriginalArg(2) Toolkit toolkit, @OriginalArg(3) int cycle, @OriginalArg(4) Matrix matrix) {
		model.updateParticlePosition(matrix);
		@Pc(17) EmissiveTriangle[] modelParticleEmitters = model.getEmissiveTriangles();
		@Pc(20) EffectiveVertex[] modelParticleEffectors = model.getEffectiveVertices();
		if ((this.particleSystem == null || this.particleSystem.removed) && (modelParticleEmitters != null || modelParticleEffectors != null)) {
			this.particleSystem = ParticleSystem.get(cycle, false);
		}
		if (this.particleSystem != null) {
			this.particleSystem.update(toolkit, (long) cycle, modelParticleEmitters, modelParticleEffectors);
		}
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(IILjava/lang/String;)V")
	public void addOptionName(@OriginalArg(0) int index, @OriginalArg(2) String option) {
		if (this.options == null || index >= this.options.length) {
			@Pc(14) String[] strings = new String[index + 1];
			if (this.options != null) {
				for (@Pc(19) int i = 0; i < this.options.length; i++) {
					strings[i] = this.options[i];
				}
			}
			this.options = strings;
		}
		this.options[index] = option;
	}

	@OriginalMember(owner = "client!at", name = "b", descriptor = "(IILjava/lang/String;)V")
	public void setStringParam(@OriginalArg(1) int paramKey, @OriginalArg(2) String paramValue) {
		if (this.clientParams == null) {
			this.clientParams = new IterableHashTable(16);
			this.clientParams.put((long) paramKey, new StringNode(paramValue));
			return;
		}
		@Pc(36) StringNode stringNode = (StringNode) this.clientParams.get((long) paramKey);
		if (stringNode == null) {
			this.clientParams.put((long) paramKey, new StringNode(paramValue));
		} else {
			stringNode.value = paramValue;
		}
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(ZLclient!cea;)[I")
	private int[] decodeScript3(@OriginalArg(1) Packet packet) {
		@Pc(9) int count = packet.readUByte();
		if (count == 0) {
			return null;
		}
		@Pc(24) int[] values = new int[count];
		for (@Pc(26) int i = 0; i < count; i++) {
			values[i] = packet.readInt();
		}
		return values;
	}

	@OriginalMember(owner = "client!at", name = "c", descriptor = "(Lclient!ha;B)Lclient!mi;")
	public NativeSprite getSprite(@OriginalArg(0) Toolkit toolkit) {
		Component.missingMedia = false;
		@Pc(48) long spriteHash = ((long) this.spriteShadow << 40) + ((this.flipVertical ? 1L : 0L) << 38) + ((long) this.borderThickness << 36) + ((this.alpha ? 1L : 0L) << 35) + (long) this.spriteId + ((this.flipHorizontal ? 1L : 0L) << 39);
		@Pc(54) NativeSprite sprite = (NativeSprite) Component.NATIVE_SPRITE_CACHE.get(spriteHash);
		if (sprite != null) {
			return sprite;
		}
		@Pc(70) SpriteType indexedImage = SpriteDataProvider.listSingle(Component.packet, this.spriteId, 0);
		if (indexedImage == null) {
			Component.missingMedia = true;
			return null;
		}
		if (this.flipVertical) {
			indexedImage.flipVertical();
		}
		if (this.flipHorizontal) {
			indexedImage.flipHorizontal();
		}
		if (this.borderThickness > 0) {
			indexedImage.drawSmallBorder(this.borderThickness);
		} else if (this.spriteShadow != 0) {
			indexedImage.drawSmallBorder(1);
		}
		if (this.borderThickness >= 1) {
			indexedImage.drawThickBorder(1);
		}
		if (this.borderThickness >= 2) {
			indexedImage.drawThickBorder(16777215);
		}
		if (this.spriteShadow != 0) {
			indexedImage.outline(this.spriteShadow | 0xFF000000);
		}
		sprite = toolkit.generateSprite(indexedImage, true);
		Component.NATIVE_SPRITE_CACHE.put(sprite, spriteHash, sprite.getWidth() * sprite.getHeight() * 4);
		return sprite;
	}

	@OriginalMember(owner = "client!at", name = "a", descriptor = "(ZLclient!hq;Lclient!lca;)Lclient!wr;")
	public Skybox calculateLighting(@OriginalArg(1) SunTypeList sunTypeList, @OriginalArg(2) SkyBoxTypeList skyBoxTypeList) {
		if (this.skyboxId == -1) {
			return null;
		}
		@Pc(63) long uid = (long) this.lightPositionZ << 16 & 0xFFFFL << 16 | (long) this.lightPositionY << 32 & 0xFFFFL << 32 | (long) this.lightPositionX << 48 & 0xFFFFL << 48 | (long) this.skyboxId & 0xFFFFL;
		@Pc(69) Skybox lighting = (Skybox) Component.COMPONENT_LIGHTING.get(uid);
		if (lighting == null) {
			lighting = skyBoxTypeList.generateSkybox(this.lightPositionY, this.lightPositionZ, this.lightPositionX, sunTypeList, this.skyboxId);
			Component.COMPONENT_LIGHTING.put(lighting, uid);
		}
		return lighting;
	}
	@OriginalMember(owner = "client!vq", name = "z", descriptor = "Z")
	public static boolean missingMedia = false;
	@OriginalMember(owner = "client!ww", name = "L", descriptor = "Lclient!uh;")
	public static final Deque ITEMS = new Deque();

	@OriginalMember(owner = "client!ew", name = "g", descriptor = "Lclient!jr;")
	public static final LRUCache MODEL_CACHE = new LRUCache(50);

	@OriginalMember(owner = "client!kj", name = "N", descriptor = "Lclient!jr;")
	public static final LRUCache COMPONENT_LIGHTING = new LRUCache(4);

	@OriginalMember(owner = "client!ej", name = "f", descriptor = "Lclient!jr;")
	public static final LRUCache NATIVE_SPRITE_CACHE = new LRUCache(3000000, 200);
	@OriginalMember(owner = "client!ek", name = "h", descriptor = "I")
	public static final int DRAG_RENDER_TRANSLUCENT = 0;
	@OriginalMember(owner = "client!rfa", name = "h", descriptor = "Lclient!wb;")
	public static final IFEvents DEFAULT_SETTINGS = new IFEvents(0, -1);
	@OriginalMember(owner = "client!tr", name = "a", descriptor = "(ILclient!at;)V")
	public static void redrawComponent(@OriginalArg(1) Component component) {
		if (component.lastRenderedCycle == InterfaceRenderer.lastRenderedCycle) {
			InterfaceRectangles.IS_COMPONENT_ON_TOP[component.cacheReference] = true;
		}
	}

	@OriginalMember(owner = "client!fk", name = "a", descriptor = "(ILclient!at;Z)Ljava/lang/String;")
	public static String getOpName(@OriginalArg(0) int opIndex, @OriginalArg(1) Component component) {
		if (!InterfaceManager.getComponentSettings(component).isOpEnabled(opIndex) && component.params == null) {
			return null;
		} else if (component.options == null || component.options.length <= opIndex || component.options[opIndex] == null || component.options[opIndex].trim().length() == 0) {
			return InterfaceManager.qaOpTest ? "Hidden-" + opIndex : null;
		} else {
			return component.options[opIndex];
		}
	}
	@OriginalMember(owner = "client!kda", name = "a", descriptor = "(II)V")
	public static void setPreferenceMask(@OriginalArg(0) int preferenceMask) {
		Interface.preferenceMask = preferenceMask;
		Component.MODEL_CACHE.reset();
	}
	@OriginalMember(owner = "client!dba", name = "A", descriptor = "Lclient!jr;")
	public static final LRUCache aClass169_13 = new LRUCache(8);

	@OriginalMember(owner = "client!jp", name = "a", descriptor = "(II)V")
	public static void cleanCaches() {
		Component.NATIVE_SPRITE_CACHE.clean(50);
		Component.MODEL_CACHE.clean(50);
		Component.aClass169_13.clean(50);
		Component.COMPONENT_LIGHTING.clean(50);
	}
	@OriginalMember(owner = "client!pm", name = "c", descriptor = "(I)V")
	public static void removeCacheSoftReferences() {
		Component.NATIVE_SPRITE_CACHE.removeSoftReferences();
		Component.MODEL_CACHE.removeSoftReferences();
		Component.aClass169_13.removeSoftReferences();
		Component.COMPONENT_LIGHTING.removeSoftReferences();
	}

	@OriginalMember(owner = "client!pea", name = "a", descriptor = "(B)V")
	public static void resetCaches() {
		Component.NATIVE_SPRITE_CACHE.reset();
		Component.MODEL_CACHE.reset();
		Component.aClass169_13.reset();
		Component.COMPONENT_LIGHTING.reset();
	}
	@OriginalMember(owner = "client!cd", name = "l", descriptor = "I")
	public static final int CONTENT_TYPE_MINIMAP = 1338;

	@OriginalMember(owner = "client!tda", name = "b", descriptor = "I")
	public static final int CONTENT_TYPE_LOCAL_PLAYER_MODEL = 328;

	@OriginalMember(owner = "client!bu", name = "o", descriptor = "I")
	public static final int CONTENT_TYPE_WORLDMAP = 1400;

	@OriginalMember(owner = "client!nv", name = "g", descriptor = "I")
	public static final int CONTENT_TYPE_DEBUG_INFORMATION = 1405;

	@OriginalMember(owner = "client!wm", name = "x", descriptor = "I")
	public static final int CONTENT_TYPE_MOUSE_OVER_TEXT = 1406;

	@OriginalMember(owner = "client!mea", name = "a", descriptor = "(BILclient!at;)I")
	public static int getOpCursor(@OriginalArg(1) int op, @OriginalArg(2) Component component) {
		if (!InterfaceManager.getComponentSettings(component).isOpEnabled(op) && component.params == null) {
			return -1;
		} else if (component.opCursors == null || op >= component.opCursors.length) {
			return -1;
		} else {
			return component.opCursors[op];
		}
	}

	@OriginalMember(owner = "client!tr", name = "a", descriptor = "(ZILclient!at;)V")
	public static void layoutChildren(@OriginalArg(0) boolean fireResizeHooks, @OriginalArg(2) Component component) {
		@Pc(20) int width = component.scrollWidth == 0 ? component.width : component.scrollWidth;
		@Pc(29) int height = component.scrollHeight == 0 ? component.height : component.scrollHeight;
		InterfaceManager.updateInterfaceDimensions(component.idHash, width, fireResizeHooks, height, Interface.interfaces[component.idHash >> 16]);
		if (component.slotChildren != null) {
			InterfaceManager.updateInterfaceDimensions(component.idHash, width, fireResizeHooks, height, component.slotChildren);
		}
		@Pc(62) SubInterface subInterface = (SubInterface) InterfaceManager.OPEN_INTERFACES.get((long) component.idHash);
		if (subInterface != null) {
			InterfaceManager.updateInterfaceDimensions(height, subInterface.interfaceId, fireResizeHooks, width);
		}
	}

	@OriginalMember(owner = "client!bm", name = "a", descriptor = "(BIILclient!at;)V")
	public static void updatePosition(@OriginalArg(1) int parentWidth, @OriginalArg(2) int parentHeight, @OriginalArg(3) Component component) {
		if (component.aspectXType == 0) {
			component.x = component.basePositionX;
		} else if (component.aspectXType == 1) {
			component.x = (parentWidth - component.width) / 2 + component.basePositionX;
		} else if (component.aspectXType == 2) {
			component.x = parentWidth - component.basePositionX - component.width;
		} else if (component.aspectXType == 3) {
			component.x = parentWidth * component.basePositionX >> 14;
		} else if (component.aspectXType == 4) {
			component.x = (parentWidth - component.width) / 2 + (parentWidth * component.basePositionX >> 14);
		} else {
			component.x = parentWidth - component.width - (parentWidth * component.basePositionX >> 14);
		}
		if (component.aspectYType == 0) {
			component.y = component.basePositionY;
		} else if (component.aspectYType == 1) {
			component.y = (parentHeight - component.height) / 2 + component.basePositionY;
		} else if (component.aspectYType == 2) {
			component.y = parentHeight - component.height - component.basePositionY;
		} else if (component.aspectYType == 3) {
			component.y = component.basePositionY * parentHeight >> 14;
		} else if (component.aspectYType == 4) {
			component.y = (parentHeight * component.basePositionY >> 14) + (parentHeight - component.height) / 2;
		} else {
			component.y = parentHeight - (parentHeight * component.basePositionY >> 14) - component.height;
		}
		if (!InterfaceManager.qaOpTest || InterfaceManager.getComponentSettings(component).settingsHash == 0 && component.type != ComponentType.CONTAINER) {
			return;
		}
		if (component.y < 0) {
			component.y = 0;
		} else if (component.height + component.y > parentHeight) {
			component.y = parentHeight - component.height;
		}
		if (component.x < 0) {
			component.x = 0;
		} else if (parentWidth < component.x + component.width) {
			component.x = parentWidth - component.width;
			return;
		}
	}

	@OriginalMember(owner = "client!cga", name = "k", descriptor = "I")
	public static final int anInt2217 = 1403;

	@OriginalMember(owner = "client!sr", name = "a", descriptor = "(Lclient!at;I)Lclient!at;")
	public static Component getParentLayer(@OriginalArg(0) Component component) {
		if (component.parentId != -1) {
			return Interface.getComponent(component.parentId);
		}
		@Pc(24) int interfaceId = component.idHash >>> 16;
		@Pc(29) HashTableIterator hashTableIterator = new HashTableIterator(InterfaceManager.OPEN_INTERFACES);
		for (@Pc(34) SubInterface subInterface = (SubInterface) hashTableIterator.first(); subInterface != null; subInterface = (SubInterface) hashTableIterator.next()) {
			if (subInterface.interfaceId == interfaceId) {
				return Interface.getComponent((int) subInterface.key);
			}
		}
		return null;
	}

	@OriginalMember(owner = "client!rw", name = "m", descriptor = "I")
	public static final int WORLD_LIST_VERSION = 1401;
	@OriginalMember(owner = "client!ga", name = "a", descriptor = "(Lclient!ha;ILclient!at;)V")
	public static void queueItemSpriteIfMissing(@OriginalArg(0) Toolkit toolkit, @OriginalArg(2) Component component) {
		@Pc(38) boolean spriteMissing = TypeLists.OBJ.getCachedSprite(component.borderThickness, component.containerItemId, component.itemStackSize, component.spriteShadow | 0xFF000000, component.renderStack, toolkit, component.wearCol ? PlayerUpdate.localPlayer.playerModel : null) == null;
		if (spriteMissing) {
			Component.ITEMS.addLast(new JavaObjSprite(component.containerItemId, component.itemStackSize, component.borderThickness, component.spriteShadow | 0xFF000000, component.renderStack, component.wearCol));
			Component.redrawComponent(component);
		}
	}

	@OriginalMember(owner = "client!mfa", name = "a", descriptor = "(ILclient!at;)Ljava/lang/String;")
	public static String getUseOptionString(@OriginalArg(1) Component component) {
		if (InterfaceManager.getComponentSettings(component).getUseOptionFlags() == 0) {
			return null;
		} else if (component.targetVerb == null || component.targetVerb.trim().length() == 0) {
			return InterfaceManager.qaOpTest ? "Hidden-use" : null;
		} else {
			return component.targetVerb;
		}
	}

	@OriginalMember(owner = "client!od", name = "w", descriptor = "I")
	public static final int CONTENT_TYPE_GAME_WINDOW = 1337;

	@OriginalMember(owner = "client!lo", name = "a", descriptor = "(IBI)Lclient!at;")
	public static Component getComponentWithSlot(@OriginalArg(0) int slotId, @OriginalArg(2) int idHash) {
		@Pc(9) Component component = Interface.getComponent(idHash);
		if (slotId == -1) {
			return component;
		} else if (component == null || component.slotChildren == null || component.slotChildren.length <= slotId) {
			return null;
		} else {
			return component.slotChildren[slotId];
		}
	}

	@OriginalMember(owner = "client!jb", name = "Q", descriptor = "Lclient!in;")
	public static Js5ResourceProvider packet;

	@OriginalMember(owner = "client!pb", name = "q", descriptor = "I")
	public static final int CONTENT_TYPE_COMPASS = 1339;
}
