package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.config.item.ItemType
import com.jagex.game.runetek5.config.TypeLists
import com.jagex.game.runetek5.plugin.api.ChatLineView
import com.jagex.game.runetek5.plugin.api.ChatType
import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginConfig
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.SettingsSchema
import com.jagex.game.runetek5.plugin.api.Skill
import com.jagex.game.runetek5.plugin.api.events.ChatMessage
import com.jagex.game.runetek5.plugin.api.events.ClientTick
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.SettingChanged
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: RuneLite's Chat Commands. A chat line that is one of its commands, from
 * anyone (public chat, private messages either way, the clan chat), is answered in place, the line's text
 * becoming the answer:
 * - `!lvl <skill>` (`!total` for the overall): "Level Attack: 99 Experience: 13,034,431 Rank: 1,234", from the
 *   sender's hiscores on the game's website;
 * - `!cmb`: the sender's combat level and the levels it is made of, from the same;
 * - `!price <item>`: "Price of Rune scimitar: GE average 15,000 HA value 15,360", the exchange's guide price;
 * - `!qp`: your quest points, for your own line only (another player's are not sent to the client).
 *
 * The hiscores come in on their own thread (HiscoreLookup): a line asked about a name not yet looked up is
 * answered when they come, by changing its text where it stands (ClientApi.setChatLine, API 39), or left as
 * it was if the website does not know the name. 634's hiscores have no kill counts, clue scrolls or
 * minigames, so RuneLite's `!kc`, `!pb`, `!clues` and the rest are not here. A built-in plugin
 * (BuiltinPlugins, docs/PLUGINS.md).
 */
object ChatCommands : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("chatcommands", "Chat Commands", "1", PluginApi.VERSION, "Answers !lvl, !total, !cmb, !price and !qp in the chat.", "Pixel")

	/** RuneLite's ChatColorType.HIGHLIGHT on the chat box: the figures in red. */
	const val HIGHLIGHT: String = "<col=ff0000>"
	const val NORMAL: String = "</col>"

	/** Void's quest points varp (quest.varps.toml). */
	const val QUEST_POINTS_VARP: Int = 101

	/** How long a line waits for its sender's hiscores before it is left as it was. */
	const val PENDING_MILLIS: Long = 30_000L

	/** RuneLite's SkillAbbreviations, for 634's skills: what `!lvl` takes besides a skill's name. */
	@JvmField
	val ABBREVIATIONS: Map<String, Int> = mapOf(
		"att" to Skill.ATTACK, "atk" to Skill.ATTACK, "attk" to Skill.ATTACK,
		"def" to Skill.DEFENCE, "defense" to Skill.DEFENCE, "defen" to Skill.DEFENCE,
		"str" to Skill.STRENGTH, "stren" to Skill.STRENGTH,
		"hp" to Skill.CONSTITUTION, "hitpoints" to Skill.CONSTITUTION, "cons" to Skill.CONSTITUTION, "lp" to Skill.CONSTITUTION,
		"range" to Skill.RANGED, "ranging" to Skill.RANGED, "rng" to Skill.RANGED,
		"pray" to Skill.PRAYER,
		"mage" to Skill.MAGIC, "magiccc" to Skill.MAGIC,
		"cook" to Skill.COOKING,
		"wc" to Skill.WOODCUTTING, "wood" to Skill.WOODCUTTING, "woodcut" to Skill.WOODCUTTING,
		"fletch" to Skill.FLETCHING,
		"fish" to Skill.FISHING,
		"fm" to Skill.FIREMAKING, "fire" to Skill.FIREMAKING,
		"craft" to Skill.CRAFTING,
		"smith" to Skill.SMITHING,
		"mine" to Skill.MINING,
		"herb" to Skill.HERBLORE,
		"agi" to Skill.AGILITY, "agil" to Skill.AGILITY,
		"thief" to Skill.THIEVING, "thiev" to Skill.THIEVING,
		"slay" to Skill.SLAYER,
		"farm" to Skill.FARMING,
		"rc" to Skill.RUNECRAFTING, "rune" to Skill.RUNECRAFTING, "runecraft" to Skill.RUNECRAFTING,
		"hunt" to Skill.HUNTER,
		"con" to Skill.CONSTRUCTION, "constr" to Skill.CONSTRUCTION,
		"summ" to Skill.SUMMONING, "summon" to Skill.SUMMONING,
		"dung" to Skill.DUNGEONEERING, "dg" to Skill.DUNGEONEERING, "dungeon" to Skill.DUNGEONEERING,
	)

	/** `!lvl`'s overall: what RuneLite takes for it. */
	const val OVERALL: Int = -2

	@JvmField var enabled: Boolean = true
	@JvmField var lvl: Boolean = true
	@JvmField var cmb: Boolean = true
	@JvmField var price: Boolean = true
	@JvmField var qp: Boolean = true

	/** A line answered once its sender's hiscores are in: its id, the sender, the command and when it came. */
	class Pending(@JvmField val lineId: Int, @JvmField val sender: String, @JvmField val text: String, @JvmField val since: Long)

	@JvmField val pending: ArrayList<Pending> = ArrayList()

	/** The answers given, for the smoke test: the line's text after. */
	@JvmField val answered: ArrayList<String> = ArrayList()

	private var context: PluginContext? = null

	private val chatListener: EventListener<ChatMessage> = EventListener { event -> context?.client()?.let { chat(it, event) } }

	private val tickListener: EventListener<ClientTick> = EventListener { _ -> context?.client()?.let { tick(it) } }

	private val settingListener: EventListener<SettingChanged> = EventListener { event ->
		if (event.pluginId == DESCRIPTOR.id) {
			context?.config()?.let { readSettings(it) }
		}
	}

	override fun start(context: PluginContext) {
		this.context = context
		val s: SettingsSchema = context.settings()
		s.toggle("enabled", "Chat commands", true)
		// RuneLite's ChatCommandsConfig, those 634 can answer
		s.toggle("lvl", "Level command (!lvl, !total)", true)
		s.toggle("cmb", "Combat command (!cmb)", true)
		s.toggle("price", "Price command (!price)", true)
		s.toggle("qp", "Quest points command (!qp)", true)
		readSettings(context.config())
		context.events().subscribe(Events.SETTING_CHANGED, settingListener)
		context.events().subscribe(Events.CHAT_MESSAGE, chatListener)
		context.events().subscribe(Events.CLIENT_TICK, tickListener)
	}

	override fun stop() {
		context = null
		pending.clear()
	}

	@JvmStatic
	fun readSettings(config: PluginConfig) {
		enabled = config.getBoolean("enabled", true)
		lvl = config.getBoolean("lvl", true)
		cmb = config.getBoolean("cmb", true)
		price = config.getBoolean("price", true)
		qp = config.getBoolean("qp", true)
	}

	/** The chat types a command is read from: public, a moderator's, private either way, the clan chat. */
	@JvmStatic
	fun isCommandType(type: Int): Boolean = type == ChatType.PUBLIC || type == ChatType.PUBLIC_MOD || type == ChatType.PRIVATE_FROM ||
		type == ChatType.PRIVATE_TO || type == ChatType.PRIVATE_FROM_MOD || type == ChatType.FRIENDS_CHAT

	@JvmStatic
	fun chat(client: ClientApi, event: ChatMessage) {
		val text: String = OpponentInfo.removeTags(event.message ?: return).trim()
		if (!enabled || !text.startsWith("!") || !isCommandType(event.type)) {
			return
		}
		// a private message sent is the local player's, its name the one it went to
		val sender: String = (if (event.type == ChatType.PRIVATE_TO) client.localPlayer()?.name() else event.name?.let { OpponentInfo.removeTags(it) }) ?: return
		val answer: String? = answerNow(client, sender, text)
		if (answer != null) {
			event.message = answer
			answered.add(answer)
			return
		}
		if (needsHiscores(text)) {
			// asks the website now; the line is answered when they come
			HiscoreLookup.lookup(sender)
			pending.add(Pending(nextLineId(client), sender, text, lang.currentTimeMillis()))
		}
	}

	/** The answer to [text] from [sender] that needs nothing to come in, or null (not a command, or waiting on hiscores). */
	@JvmStatic
	fun answerNow(client: ClientApi, sender: String, text: String): String? {
		val lower: String = text.lowercase()
		if (price && lower.startsWith("!price ")) {
			return priceAnswer(client, text.substring(7).trim())
		}
		if (qp && lower == "!qp") {
			val own: String? = client.localPlayer()?.name()
			return if (own != null && own.equals(sender, ignoreCase = true)) questPointsAnswer(client.varp(QUEST_POINTS_VARP)) else null
		}
		if (needsHiscores(text)) {
			return HiscoreLookup.lookup(sender)?.let { hiscoreAnswer(it, text) }
		}
		return null
	}

	/** Whether [text] is a command the sender's hiscores answer: `!lvl <skill>`, `!total`, `!cmb`. */
	@JvmStatic
	fun needsHiscores(text: String): Boolean {
		val lower: String = text.lowercase()
		return (lvl && (lower.startsWith("!lvl ") || lower == "!total")) || (cmb && lower == "!cmb")
	}

	/** The answer to a hiscores command [text] from [result], or null when it asks for no skill 634 has. */
	@JvmStatic
	fun hiscoreAnswer(result: HiscoreLookup.Result, text: String): String? {
		val lower: String = text.lowercase()
		if (lower == "!cmb") {
			return combatAnswer(result)
		}
		val skill: Int = if (lower == "!total") OVERALL else skillOf(text.substring(5).trim()) ?: return null
		return levelAnswer(result, skill)
	}

	/** A skill named [search]: its name, RuneLite's abbreviations, "total"/"overall" for [OVERALL]; null for none. */
	@JvmStatic
	fun skillOf(search: String): Int? {
		val s: String = search.lowercase()
		if (s == "total" || s == "overall") {
			return OVERALL
		}
		val named: Int = Skill.NAMES.indexOfFirst { it.equals(s, ignoreCase = true) }
		return if (named >= 0) named else ABBREVIATIONS[s]
	}

	/** RuneLite's playerSkillLookup answer: "Level <skill>: <level> Experience: <xp> Rank: <rank>". */
	@JvmStatic
	fun levelAnswer(result: HiscoreLookup.Result, skill: Int): String? {
		val name: String = if (skill == OVERALL) "Overall" else Skill.name(skill)
		val level: Int = if (skill == OVERALL) result.overallLevel else result.levels[skill]
		val xp: Long = if (skill == OVERALL) result.overallXp else result.xp[skill]
		val rank: Int = if (skill == OVERALL) result.overallRank else result.ranks[skill]
		if (level < 0) {
			return null
		}
		val b: StringBuilder = StringBuilder()
		b.append("Level ").append(HIGHLIGHT).append(name).append(": ").append(level).append(NORMAL)
		if (xp >= 0) {
			b.append(" Experience: ").append(HIGHLIGHT).append(Examine.formatNumber(xp)).append(NORMAL)
		}
		if (rank > 0) {
			b.append(" Rank: ").append(HIGHLIGHT).append(Examine.formatNumber(rank.toLong())).append(NORMAL)
		}
		return b.toString()
	}

	/** RuneLite's combatLevelLookup answer, with 634's Summoning: "Combat Level: 138 A: 99 S: 99 D: 99 H: 99 R: 99 P: 99 M: 99 Su: 99". */
	@JvmStatic
	fun combatAnswer(result: HiscoreLookup.Result): String? {
		val skills: IntArray = intArrayOf(Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.CONSTITUTION, Skill.RANGED, Skill.PRAYER, Skill.MAGIC, Skill.SUMMONING)
		// an unranked skill is at its least: Constitution 10, the rest 1
		val l: IntArray = IntArray(skills.size) { i ->
			val level: Int = result.levels[skills[i]]
			if (level > 0) level else if (skills[i] == Skill.CONSTITUTION) 10 else 1
		}
		val levels: IntArray = intArrayOf(l[0], l[1], l[2], l[3], l[4], l[6], l[5], l[7])
		val combat: Int = CombatLevel.level(levels, true)
		val labels: Array<String> = arrayOf("A", "S", "D", "H", "R", "P", "M", "Su")
		val b: StringBuilder = StringBuilder("Combat Level: ").append(HIGHLIGHT).append(combat).append(NORMAL)
		for (i in labels.indices) {
			b.append(" ").append(labels[i]).append(": ").append(HIGHLIGHT).append(l[i]).append(NORMAL)
		}
		return b.toString()
	}

	/** RuneLite's itemPriceLookup answer: "Price of <item>: GE average <price> HA value <value>", null for no item. */
	@JvmStatic
	fun priceAnswer(client: ClientApi, search: String): String? {
		val id: Int = findItem(search) { client.itemPrice(it) } ?: return null
		val name: String = client.itemName(id) ?: return null
		val ge: Int = client.itemPrice(id)
		val ha: Int = client.itemHaPrice(id)
		val b: StringBuilder = StringBuilder("Price of ").append(HIGHLIGHT).append(name).append(NORMAL)
		b.append(": GE average ").append(HIGHLIGHT).append(Examine.formatNumber(ge.toLong())).append(NORMAL)
		if (ha > 0) {
			b.append(" HA value ").append(HIGHLIGHT).append(Examine.formatNumber(ha.toLong())).append(NORMAL)
		}
		return b.toString()
	}

	@JvmStatic
	fun questPointsAnswer(points: Int): String = "Quest points: " + HIGHLIGHT + points + NORMAL

	/** The tradeable items' lower-case names, by id (noted, lent and placeholder items left out), read once. */
	private var names: Array<String?>? = null

	/**
	 * RuneLite's item search for `!price`: the tradeable items with a guide price ([price]) whose name holds
	 * [search]; the one named exactly so, else the first by id. Null for none.
	 */
	@JvmStatic
	fun findItem(search: String, price: (Int) -> Int): Int? = bestMatch(itemNames(), search, price)

	@JvmStatic
	fun bestMatch(names: Array<String?>, search: String, price: (Int) -> Int): Int? {
		val s: String = search.lowercase()
		if (s.isEmpty()) {
			return null
		}
		val found: List<Int> = names.indices.filter { names[it]?.contains(s) == true && price(it) > 0 }
		return found.firstOrNull { names[it] == s } ?: found.firstOrNull()
	}

	private fun itemNames(): Array<String?> {
		names?.let { return it }
		val list = TypeLists.OBJ ?: return arrayOfNulls(0)
		val out: Array<String?> = arrayOfNulls(list.num)
		for (i in 0 until list.num) {
			val type: ItemType = list.list(i)
			if (type.stockmarket && type.certTemplateId == -1 && type.lendTemplateId == -1 && type.dummyitem == 0) {
				out[i] = type.name?.lowercase()
			}
		}
		names = out
		return out
	}

	/** The id this line will have: the newest line's plus one, as the client numbers its lines (ChatFilter's). */
	private fun nextLineId(client: ClientApi): Int {
		val newest: ChatLineView? = client.chatLine(0)
		return (newest?.id() ?: -1) + 1
	}

	/** Each client cycle: the waiting lines whose sender's hiscores are in, answered where they stand. */
	private fun tick(client: ClientApi) {
		if (pending.isEmpty()) {
			return
		}
		val now: Long = lang.currentTimeMillis()
		// a line is done once its hiscores are in, the website does not know the name, or it has waited too long
		pending.removeAll { p -> answerLate(client, p) || now - p.since > PENDING_MILLIS || HiscoreLookup.notFound(p.sender) }
	}

	/** Answers [p] in place if its sender's hiscores are in; whether they were. */
	private fun answerLate(client: ClientApi, p: Pending): Boolean {
		val result: HiscoreLookup.Result = HiscoreLookup.lookup(p.sender) ?: return false
		val answer: String? = hiscoreAnswer(result, p.text)
		if (answer != null && client.setChatLine(p.lineId, answer)) {
			answered.add(answer)
		}
		return true
	}
}
