package com.jagex.game.runetek5.network.newsitem;

import com.jagex.game.runetek5.core.constants.Ports;
import com.jagex.game.runetek5.core.datastruct.StringList;
import com.jagex.game.runetek5.network.newsitem.NewsItem;
import com.jagex.game.runetek5.sign.SignedResource;
import com.jagex.game.runetek5.sign.SignedResourceStatus;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;
import com.jagex.game.runetek5.client.ServerEnvironment;
import com.jagex.game.runetek5.StringListFactory;
import com.jagex.game.runetek5.ModeWhere;
import com.jagex.game.runetek5.network.WorldConnection;
import com.jagex.game.runetek5.client.Game;
import com.jagex.game.runetek5.client.GameShell;

@OriginalClass("client!eaa")
public final class NewsItemFetcher implements Runnable {

	@OriginalMember(owner = "client!eaa", name = "d", descriptor = "Lclient!of;")
	private SignedResource aClass240_6;

	@OriginalMember(owner = "client!eaa", name = "g", descriptor = "[Lclient!wc;")
	private NewsItem[] newsItems;

	@OriginalMember(owner = "client!eaa", name = "h", descriptor = "Z")
	private volatile boolean fetched;

	@OriginalMember(owner = "client!eaa", name = "i", descriptor = "Ljava/lang/Thread;")
	private Thread thread;

	@OriginalMember(owner = "client!eaa", name = "run", descriptor = "()V")
	@Override
	public void run() {
		try {
			@Pc(16) BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((DataInputStream) this.aClass240_6.result));
			@Pc(19) String string = bufferedReader.readLine();
			@Pc(22) StringList stringList = StringListFactory.create();
			while (string != null) {
				stringList.add(string);
				string = bufferedReader.readLine();
			}
			@Pc(37) String[] strings = stringList.toArray();
			if (strings.length % 3 != 0) {
				return;
			}
			this.newsItems = new NewsItem[strings.length / 3];
			for (@Pc(55) int i = 0; i < strings.length; i += 3) {
				this.newsItems[i / 3] = new NewsItem(strings[i], strings[i + 1], strings[i + 2]);
			}
		} catch (@Pc(86) IOException ex) {
		}
		this.fetched = true;
	}

	@OriginalMember(owner = "client!eaa", name = "a", descriptor = "(ZI)Lclient!wc;")
	public NewsItem get(@OriginalArg(1) int id) {
		return this.newsItems == null || id < 0 || this.newsItems.length <= id ? null : this.newsItems[id];
	}

	@OriginalMember(owner = "client!eaa", name = "b", descriptor = "(I)Z")
	public boolean fetch() {
		if (this.fetched) {
			return true;
		}
		if (this.aClass240_6 == null) {
			try {
				@Pc(22) int port = ServerEnvironment.LIVE == ModeWhere.WTRC ? Ports.HTTP : WorldConnection.aClass255_3.worldId + Ports.HTTP_OFFSET;
				this.aClass240_6 = GameShell.signLink.openStream(new URL("http://" + WorldConnection.aClass255_3.host + ":" + port + "/news.ws?game=" + Game.CURRENT_GAME.id));
			} catch (@Pc(49) MalformedURLException ex) {
				return true;
			}
		}
		if (this.aClass240_6 == null || this.aClass240_6.status == SignedResourceStatus.ERROR) {
			return true;
		} else if (this.aClass240_6.status == SignedResourceStatus.SUCCESS) {
			if (this.thread == null) {
				this.thread = new Thread(this);
				this.thread.start();
			}
			return this.fetched;
		} else {
			return false;
		}
	}
}
