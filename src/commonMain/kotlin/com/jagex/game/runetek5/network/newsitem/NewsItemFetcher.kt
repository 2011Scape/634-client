package com.jagex.game.runetek5.network.newsitem

import com.jagex.game.runetek5.ModeWhere
import com.jagex.game.runetek5.StringListFactory
import com.jagex.game.runetek5.client.Game
import com.jagex.game.runetek5.client.GameShell
import com.jagex.game.runetek5.client.ServerEnvironment
import com.jagex.game.runetek5.core.constants.Ports
import com.jagex.game.runetek5.core.datastruct.StringList
import com.jagex.game.runetek5.network.WorldConnection
import com.jagex.game.runetek5.sign.SignedResource
import com.jagex.game.runetek5.sign.SignedResourceStatus
import io.BufferedReader
import io.DataInputStream
import io.IOException
import io.InputStreamReader
import net.MalformedURLException
import net.URL
import org.openrs2.deob.annotation.OriginalArg
import org.openrs2.deob.annotation.OriginalClass
import org.openrs2.deob.annotation.OriginalMember
import org.openrs2.deob.annotation.Pc
import kotlin.concurrent.Volatile
import lang.Runnable
import lang.Thread

@OriginalClass("client!eaa")
class NewsItemFetcher : Runnable {

	@OriginalMember(owner = "client!eaa", name = "d", descriptor = "Lclient!of;")
	private var aClass240_6: SignedResource? = null

	@OriginalMember(owner = "client!eaa", name = "g", descriptor = "[Lclient!wc;")
	private var newsItems: Array<NewsItem?>? = null

	@OriginalMember(owner = "client!eaa", name = "h", descriptor = "Z")
	@Volatile
	private var fetched: Boolean = false

	@OriginalMember(owner = "client!eaa", name = "i", descriptor = "Ljava/lang/Thread;")
	private var thread: Thread? = null

	@OriginalMember(owner = "client!eaa", name = "run", descriptor = "()V")
	override fun run() {
		try {
			@Pc(16) val bufferedReader: BufferedReader = BufferedReader(InputStreamReader(this.aClass240_6!!.result as DataInputStream?))
			@Pc(19) var string: String? = bufferedReader.readLine()
			@Pc(22) val stringList: StringList = StringListFactory.create()
			while (string != null) {
				stringList.add(string)
				string = bufferedReader.readLine()
			}
			@Pc(37) val strings: Array<String?> = stringList.toArray()
			if (strings.size % 3 != 0) {
				return
			}
			this.newsItems = arrayOfNulls<NewsItem>(strings.size / 3)
			@Pc(55) var i: Int = 0
			while (i < strings.size) {
				this.newsItems!![i / 3] = NewsItem(strings[i], strings[i + 1], strings[i + 2])
				i += 3
			}
		} catch (@Pc(86) ex: IOException) {
		}
		this.fetched = true
	}

	@OriginalMember(owner = "client!eaa", name = "a", descriptor = "(ZI)Lclient!wc;")
	fun get(@OriginalArg(1) id: Int): NewsItem? {
		return if (this.newsItems == null || id < 0 || this.newsItems!!.size <= id) null else this.newsItems!![id]
	}

	@OriginalMember(owner = "client!eaa", name = "b", descriptor = "(I)Z")
	fun fetch(): Boolean {
		if (this.fetched) {
			return true
		}
		if (this.aClass240_6 == null) {
			try {
				@Pc(22) val port: Int = if (ServerEnvironment.LIVE === ModeWhere.WTRC) Ports.HTTP else WorldConnection.aClass255_3!!.worldId + Ports.HTTP_OFFSET
				this.aClass240_6 = GameShell.signLink!!.openStream(URL("http://" + WorldConnection.aClass255_3!!.host + ":" + port + "/news.ws?game=" + Game.CURRENT_GAME!!.id))
			} catch (@Pc(49) ex: MalformedURLException) {
				return true
			}
		}
		if (this.aClass240_6 == null || this.aClass240_6!!.status == SignedResourceStatus.ERROR) {
			return true
		} else if (this.aClass240_6!!.status == SignedResourceStatus.SUCCESS) {
			if (this.thread == null) {
				this.thread = Thread(this)
				this.thread!!.start()
			}
			return this.fetched
		} else {
			return false
		}
	}
}
