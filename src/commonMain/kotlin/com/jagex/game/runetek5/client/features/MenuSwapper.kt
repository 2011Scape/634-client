package com.jagex.game.runetek5.client.features

import com.jagex.game.runetek5.plugin.api.ClientApi
import com.jagex.game.runetek5.plugin.api.CommandHandler
import com.jagex.game.runetek5.plugin.api.MenuEntry
import com.jagex.game.runetek5.plugin.api.Plugin
import com.jagex.game.runetek5.plugin.api.PluginApi
import com.jagex.game.runetek5.plugin.api.PluginContext
import com.jagex.game.runetek5.plugin.api.PluginDescriptor
import com.jagex.game.runetek5.plugin.api.events.EventListener
import com.jagex.game.runetek5.plugin.api.events.Events
import com.jagex.game.runetek5.plugin.api.events.MenuBuilt
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Not part of the original client: makes an option the left click of its menu. `::swap bank` puts
 * "Bank" on top wherever it appears (bankers, booths, chests); `::swap talk-to banker` only when the
 * target's name has "banker" in it; a rule also matches an option that begins with it as a word
 * (`::swap mark` for "Mark tile"); `::unswap bank` takes a rule away; `::swaps` lists them. The rules
 * are kept in the plugin's settings.
 *
 * A built-in plugin (BuiltinPlugins, docs/PLUGINS.md) on API 3: each game tick it reads the finished
 * menu at MenuBuilt and promotes the first entry a rule matches, which API 2 could not do.
 */
object MenuSwapper : Plugin {

	@JvmField
	val DESCRIPTOR: PluginDescriptor = PluginDescriptor("menuswapper", "Menu swapper", "1", PluginApi.VERSION, "Makes an option the left click of its menu: ::swap <option> [target], ::unswap <option>, ::swaps.", "634-client")

	private const val MAX_RULES: Int = 64

	private val ruleOptions: ArrayList<String> = ArrayList()

	private val ruleTargets: ArrayList<String> = ArrayList()

	/** How many entries were promoted, for the smoke test. */
	@JvmField
	var swaps: Int = 0

	private var context: PluginContext? = null

	private val menuListener: EventListener<MenuBuilt> = EventListener { _ -> apply() }

	private val swapCommand: CommandHandler = CommandHandler { _, args -> swap(args) }

	private val unswapCommand: CommandHandler = CommandHandler { _, args -> unswap(args) }

	private val listCommand: CommandHandler = CommandHandler { _, _ -> list() }

	override fun start(context: PluginContext) {
		this.context = context
		load(context.config().get("rules", ""))
		context.events().subscribe(Events.MENU_BUILT, menuListener)
		context.registerCommand("swap", swapCommand)
		context.registerCommand("unswap", unswapCommand)
		context.registerCommand("swaps", listCommand)
	}

	override fun stop() {
		context = null
	}

	/** The rules as saved: `option|target;option|target`. */
	private fun load(saved: String) {
		ruleOptions.clear()
		ruleTargets.clear()
		for (rule in saved.split(';')) {
			val bar: Int = rule.indexOf('|')
			val option: String = (if (bar < 0) rule else rule.substring(0, bar)).trim()
			val target: String = if (bar < 0) "" else rule.substring(bar + 1).trim()
			if (option.isNotEmpty() && ruleOptions.size < MAX_RULES) {
				ruleOptions.add(option)
				ruleTargets.add(target)
			}
		}
	}

	private fun save() {
		val context: PluginContext = this.context ?: return
		val out: StringBuilder = StringBuilder()
		for (i in 0 until ruleOptions.size) {
			if (i > 0) {
				out.append(';')
			}
			out.append(ruleOptions[i]).append('|').append(ruleTargets[i])
		}
		context.config().set("rules", out.toString())
		context.config().save()
	}

	private fun swap(args: Array<String>) {
		val context: PluginContext = this.context ?: return
		if (args.isEmpty() || args[0].indexOf(';') >= 0 || args[0].indexOf('|') >= 0) {
			context.client().consolePrint("Usage: swap <option> [target words]: makes that option the left click (swaps lists the rules, unswap <option> drops one)")
			return
		}
		if (ruleOptions.size == MAX_RULES) {
			context.client().consolePrint("No room for another rule (the most is " + MAX_RULES + ")")
			return
		}
		val target: StringBuilder = StringBuilder()
		for (i in 1 until args.size) {
			if (i > 1) {
				target.append(' ')
			}
			target.append(args[i].replace(';', ' ').replace('|', ' '))
		}
		ruleOptions.add(args[0])
		ruleTargets.add(target.toString())
		save()
		context.client().consolePrint("Left click is now \"" + args[0] + "\"" + (if (target.isEmpty()) "" else " on targets with \"" + target + "\"") + ".")
	}

	private fun unswap(args: Array<String>) {
		val context: PluginContext = this.context ?: return
		if (args.isEmpty()) {
			context.client().consolePrint("Usage: unswap <option>")
			return
		}
		var removed: Int = 0
		var i: Int = ruleOptions.size - 1
		while (i >= 0) {
			if (ruleOptions[i].equals(args[0], ignoreCase = true)) {
				ruleOptions.removeAt(i)
				ruleTargets.removeAt(i)
				removed++
			}
			i--
		}
		save()
		context.client().consolePrint(if (removed == 0) "No rule for \"" + args[0] + "\"" else removed.toString() + " rule(s) for \"" + args[0] + "\" dropped.")
	}

	private fun list() {
		val context: PluginContext = this.context ?: return
		if (ruleOptions.isEmpty()) {
			context.client().consolePrint("No swaps. swap <option> [target words] adds one.")
			return
		}
		for (i in 0 until ruleOptions.size) {
			context.client().consolePrint("  " + ruleOptions[i] + (if (ruleTargets[i].isEmpty()) "" else " on \"" + ruleTargets[i] + "\""))
		}
	}

	/** Whether [option] is [rule], or begins with it as a whole word: "mark" matches "Mark tile". */
	@JvmStatic
	fun optionMatches(option: String, rule: String): Boolean {
		if (option.equals(rule, ignoreCase = true)) {
			return true
		}
		return option.length > rule.length && option[rule.length] == ' ' && option.startsWith(rule, ignoreCase = true)
	}

	/** At MenuBuilt: the first entry from the top that a rule matches becomes the top. */
	@JvmStatic
	fun apply() {
		if (ruleOptions.isEmpty()) {
			return
		}
		val client: ClientApi = context?.client() ?: return
		val count: Int = client.menuEntryCount()
		for (i in 0 until count) {
			val entry: MenuEntry = client.menuEntry(i) ?: continue
			val option: String = ClientFeatures.plainText(entry.option())
			val target: String = ClientFeatures.plainText(entry.target())
			for (r in 0 until ruleOptions.size) {
				if (optionMatches(option, ruleOptions[r]) && (ruleTargets[r].isEmpty() || target.contains(ruleTargets[r], ignoreCase = true))) {
					if (i > 0 && client.promoteMenuEntry(i)) {
						swaps++
						if (swaps == 1) {
							context?.log("first swap: \"" + option + "\" on \"" + target + "\" is the left click")
						}
					}
					return
				}
			}
		}
	}
}
