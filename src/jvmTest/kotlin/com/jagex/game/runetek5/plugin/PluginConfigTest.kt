package com.jagex.game.runetek5.plugin

import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Not part of the original client: a plugin's settings file and the on/off file, in a folder of the test's own. */
class PluginConfigTest {

	private fun <T> inTempHome(block: () -> T): T {
		val home: java.io.File = Files.createTempDirectory("634-plugins").toFile()
		PluginPaths.homeOverride = home.path
		PluginSettings.reset()
		try {
			return block()
		} finally {
			PluginPaths.homeOverride = null
			PluginSettings.reset()
			home.deleteRecursively()
		}
	}

	@Test
	fun settingsRoundTripThroughTheFile() {
		inTempHome {
			val config: PluginConfigImpl = PluginConfigImpl("example")
			assertEquals("fallback", config.get("missing", "fallback"))
			config.set("name", "value with spaces")
			config.setInt("count", 42)
			config.setBoolean("on", true)
			assertTrue(config.save())
			val again: PluginConfigImpl = PluginConfigImpl("example")
			assertEquals(3, again.size())
			assertEquals("value with spaces", again.get("name", ""))
			assertEquals(42, again.getInt("count", 0))
			assertTrue(again.getBoolean("on", false))
			assertEquals(7, again.getInt("name", 7), "not a number: the default")
			again.remove("count")
			assertTrue(again.save())
			assertEquals(2, PluginConfigImpl("example").size())
		}
	}

	@Test
	fun awkwardTextRoundTripsThroughTheFile() {
		inTempHome {
			val config: PluginConfigImpl = PluginConfigImpl("awkward")
			config.set("a=b#c", " two\nlines\tand a tab\\ ")
			config.set("#not a comment", "=")
			config.set("spaced key", "plain")
			config.set("empty", "")
			assertTrue(config.save())
			val again: PluginConfigImpl = PluginConfigImpl("awkward")
			assertEquals(4, again.size())
			assertEquals(" two\nlines\tand a tab\\ ", again.get("a=b#c", "?"))
			assertEquals("=", again.get("#not a comment", "?"))
			assertEquals("plain", again.get("spaced key", "?"))
			assertEquals("", again.get("empty", "?"))
			val text: String = java.io.File(PluginPaths.homeOverride, ".634-client/plugin-config/awkward.properties").readText()
			assertTrue(text.contains("a\\=b\\#c=\\ two\\nlines\\tand a tab\\\\ \n"), text)
			assertEquals(5, text.lines().filter { it.isNotEmpty() }.size, "one line a setting, plus the header")
		}
	}

	@Test
	fun aPluginTurnedOffStaysOff() {
		inTempHome {
			assertTrue(PluginSettings.isEnabled("example"))
			assertTrue(PluginSettings.setEnabled("example", false))
			assertFalse(PluginSettings.isEnabled("example"))
			PluginSettings.reset()
			assertFalse(PluginSettings.isEnabled("example"), "read back from the file")
			assertTrue(PluginSettings.isEnabled("other"))
			assertTrue(PluginSettings.setEnabled("example", true))
			PluginSettings.reset()
			assertTrue(PluginSettings.isEnabled("example"))
		}
	}

	@Test
	fun nothingIsWrittenWithoutAHome() {
		PluginPaths.homeOverride = null
		val property: String? = System.getProperty("client.test")
		System.setProperty("client.test", "unit")
		try {
			assertFalse(PluginConfigImpl("example").save())
			assertFalse(PluginSettings.setEnabled("example", false))
		} finally {
			if (property == null) {
				System.clearProperty("client.test")
			} else {
				System.setProperty("client.test", property)
			}
			PluginSettings.reset()
		}
	}
}
