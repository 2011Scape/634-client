package com.jagex.game.runetek5.graphics.hd.gl

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/** Not part of the original client: the shader include expansion, fed files with either line ending. */
class HdShadersTest {

	private val lf: Map<String, String> = mapOf(
		"a.vert" to "#version 330 core\n#include \"lib.glsl\"\nvoid main() {}\n",
		"lib.glsl" to "#version 330 core\n#include \"lib.glsl\"\nfloat f();\n",
	)

	private fun expand(files: Map<String, String>): String {
		return HdShaders.expand("a.vert", HashSet()) { name -> files[name] ?: error("no " + name) }
	}

	@Test
	fun anIncludeLosesItsVersionLine() {
		assertEquals("#version 330 core\nfloat f();\n\n\nvoid main() {}\n\n", expand(lf))
	}

	@Test
	fun crlfFilesExpandLikeLfOnes() {
		val crlf: Map<String, String> = lf.mapValues { it.value.replace("\n", "\r\n") }
		val text: String = expand(crlf)
		assertEquals(expand(lf), text)
		assertFalse(text.contains('\r'))
		assertEquals(1, text.split("#version").size - 1, "one version directive, first")
	}
}
