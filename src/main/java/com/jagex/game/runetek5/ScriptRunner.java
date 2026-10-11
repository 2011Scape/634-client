package com.jagex.game.runetek5;

import com.jagex.game.runetek5.network.packet.TcpPacket;
import com.jagex.game.runetek5.LocalisedText;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.tools.LocalizedText;
import com.jagex.game.runetek5.network.BufferedConnectionContext;
import com.jagex.game.runetek5.network.packet.ClientProt;
import com.jagex.game.runetek5.encoding.HuffmanList;
import com.jagex.game.runetek5.client.login.LoginManager;
import com.jagex.game.runetek5.client.ClientState;

public final class ScriptRunner {

	@OriginalMember(owner = "client!hg", name = "v", descriptor = "[I")
	public static int[] threadSplitX;

	@OriginalMember(owner = "client!vg", name = "h", descriptor = "I")
	public static int minimumDrawLevel = 0;

	@OriginalMember(owner = "client!bb", name = "kb", descriptor = "I")
	public static int menuCornerSpriteId;

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "()V")
	public static void profileClear() {
	}

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "(Ljava/lang/String;I)V")
	public static void sendPublicChat(@OriginalArg(0) String string, @OriginalArg(1) int opcode) {
		if (LoginManager.staffModLevel == 0 && (LoginManager.userDetailQuickchat && !LoginManager.verifiedEmailAddress || LoginManager.quickchatOnly)) {
			return;
		}
		@Pc(11) String string2 = string.toLowerCase();
		@Pc(13) byte color = 0;
		if (string2.startsWith(LocalizedText.YELLOW.get(0))) {
			color = 0;
			string = string.substring(LocalizedText.YELLOW.get(0).length());
		} else if (string2.startsWith(LocalizedText.RED.get(0))) {
			color = 1;
			string = string.substring(LocalizedText.RED.get(0).length());
		} else if (string2.startsWith(LocalizedText.GREEN.get(0))) {
			color = 2;
			string = string.substring(LocalizedText.GREEN.get(0).length());
		} else if (string2.startsWith(LocalizedText.CYAN.get(0))) {
			color = 3;
			string = string.substring(LocalizedText.CYAN.get(0).length());
		} else if (string2.startsWith(LocalizedText.PURPLE.get(0))) {
			color = 4;
			string = string.substring(LocalizedText.PURPLE.get(0).length());
		} else if (string2.startsWith(LocalizedText.WHITE.get(0))) {
			color = 5;
			string = string.substring(LocalizedText.WHITE.get(0).length());
		} else if (string2.startsWith(LocalizedText.FLASH1.get(0))) {
			color = 6;
			string = string.substring(LocalizedText.FLASH1.get(0).length());
		} else if (string2.startsWith(LocalizedText.FLASH2.get(0))) {
			color = 7;
			string = string.substring(LocalizedText.FLASH2.get(0).length());
		} else if (string2.startsWith(LocalizedText.FLASH3.get(0))) {
			color = 8;
			string = string.substring(LocalizedText.FLASH3.get(0).length());
		} else if (string2.startsWith(LocalizedText.GLOW1.get(0))) {
			color = 9;
			string = string.substring(LocalizedText.GLOW1.get(0).length());
		} else if (string2.startsWith(LocalisedText.GLOW2.get(0))) {
			color = 10;
			string = string.substring(LocalisedText.GLOW2.get(0).length());
		} else if (string2.startsWith(LocalizedText.GLOW3.get(0))) {
			color = 11;
			string = string.substring(LocalizedText.GLOW3.get(0).length());
		} else if (ClientState.language != 0) {
			if (string2.startsWith(LocalizedText.YELLOW.get(ClientState.language))) {
				color = 0;
				string = string.substring(LocalizedText.YELLOW.get(ClientState.language).length());
			} else if (string2.startsWith(LocalizedText.RED.get(ClientState.language))) {
				color = 1;
				string = string.substring(LocalizedText.RED.get(ClientState.language).length());
			} else if (string2.startsWith(LocalizedText.GREEN.get(ClientState.language))) {
				color = 2;
				string = string.substring(LocalizedText.GREEN.get(ClientState.language).length());
			} else if (string2.startsWith(LocalizedText.CYAN.get(ClientState.language))) {
				color = 3;
				string = string.substring(LocalizedText.CYAN.get(ClientState.language).length());
			} else if (string2.startsWith(LocalizedText.PURPLE.get(ClientState.language))) {
				color = 4;
				string = string.substring(LocalizedText.PURPLE.get(ClientState.language).length());
			} else if (string2.startsWith(LocalizedText.WHITE.get(ClientState.language))) {
				color = 5;
				string = string.substring(LocalizedText.WHITE.get(ClientState.language).length());
			} else if (string2.startsWith(LocalizedText.FLASH1.get(ClientState.language))) {
				color = 6;
				string = string.substring(LocalizedText.FLASH1.get(ClientState.language).length());
			} else if (string2.startsWith(LocalizedText.FLASH2.get(ClientState.language))) {
				color = 7;
				string = string.substring(LocalizedText.FLASH2.get(ClientState.language).length());
			} else if (string2.startsWith(LocalizedText.FLASH3.get(ClientState.language))) {
				color = 8;
				string = string.substring(LocalizedText.FLASH3.get(ClientState.language).length());
			} else if (string2.startsWith(LocalizedText.GLOW1.get(ClientState.language))) {
				color = 9;
				string = string.substring(LocalizedText.GLOW1.get(ClientState.language).length());
			} else if (string2.startsWith(LocalisedText.GLOW2.get(ClientState.language))) {
				color = 10;
				string = string.substring(LocalisedText.GLOW2.get(ClientState.language).length());
			} else if (string2.startsWith(LocalizedText.GLOW3.get(ClientState.language))) {
				color = 11;
				string = string.substring(LocalizedText.GLOW3.get(ClientState.language).length());
			}
		}
		string2 = string.toLowerCase();
		@Pc(451) byte effect = 0;
		if (string2.startsWith(LocalizedText.WAVE.get(0))) {
			effect = 1;
			string = string.substring(LocalizedText.WAVE.get(0).length());
		} else if (string2.startsWith(LocalizedText.WAVE2.get(0))) {
			effect = 2;
			string = string.substring(LocalizedText.WAVE2.get(0).length());
		} else if (string2.startsWith(LocalizedText.SHAKE.get(0))) {
			effect = 3;
			string = string.substring(LocalizedText.SHAKE.get(0).length());
		} else if (string2.startsWith(LocalizedText.SCROLL.get(0))) {
			effect = 4;
			string = string.substring(LocalizedText.SCROLL.get(0).length());
		} else if (string2.startsWith(LocalizedText.SLIDE.get(0))) {
			effect = 5;
			string = string.substring(LocalizedText.SLIDE.get(0).length());
		} else if (ClientState.language != 0) {
			if (string2.startsWith(LocalizedText.WAVE.get(ClientState.language))) {
				effect = 1;
				string = string.substring(LocalizedText.WAVE.get(ClientState.language).length());
			} else if (string2.startsWith(LocalizedText.WAVE2.get(ClientState.language))) {
				effect = 2;
				string = string.substring(LocalizedText.WAVE2.get(ClientState.language).length());
			} else if (string2.startsWith(LocalizedText.SHAKE.get(ClientState.language))) {
				effect = 3;
				string = string.substring(LocalizedText.SHAKE.get(ClientState.language).length());
			} else if (string2.startsWith(LocalizedText.SCROLL.get(ClientState.language))) {
				effect = 4;
				string = string.substring(LocalizedText.SCROLL.get(ClientState.language).length());
			} else if (string2.startsWith(LocalizedText.SLIDE.get(ClientState.language))) {
				effect = 5;
				string = string.substring(LocalizedText.SLIDE.get(ClientState.language).length());
			}
		}
		@Pc(641) TcpPacket packet = TcpPacket.createPacket(ClientProt.MESSAGE_PUBLIC, TcpPacket.isaac);
		packet.buffer.writeByte(0);
		@Pc(650) int currentPosition = packet.buffer.currentPosition;
		packet.buffer.writeByte(color);
		packet.buffer.writeByte(effect);
		HuffmanList.writeChatMessage(packet.buffer, string);
		packet.buffer.writeIndex(packet.buffer.currentPosition - currentPosition);
		BufferedConnectionContext.queuePacket(packet);
	}

	@OriginalMember(owner = "client!kca", name = "a", descriptor = "[I")
	public static final int[] intStack = new int[1000];

	@OriginalMember(owner = "client!kca", name = "g", descriptor = "[Ljava/lang/String;")
	public static String[] stringVars;

	@OriginalMember(owner = "client!kca", name = "m", descriptor = "[[I")
	public static final int[][] arrayVars = new int[5][5000];

	@OriginalMember(owner = "client!kca", name = "p", descriptor = "[I")
	public static int[] intLocals;
}
