/*
 * Not part of the original client: 634HD (toolkit 6). GLSL lives in resource files under
 * /634hd/shaders/; this loads, compiles and links them, and on failure throws with the driver's log
 * next to the numbered source lines it points at, so a broken shader names its file and line.
 */
package com.jagex.game.runetek5.graphics.hd.gl

object HdShaders {

    private const val ROOT = "/634hd/shaders/"

    private val cache = HashMap<String, String>()

    /** The text of /634hd/shaders/[name]; [defines] are inserted after the #version line. */
    fun source(name: String, defines: String = ""): String {
        val text = cache.getOrPut(name) {
            val stream = HdShaders::class.java.getResourceAsStream(ROOT + name)
                ?: throw IllegalStateException("634HD: missing shader resource " + ROOT + name)
            stream.use { String(it.readBytes(), Charsets.UTF_8) }
        }
        if (defines.isEmpty()) return text
        val end = text.indexOf('\n')
        return text.substring(0, end + 1) + defines + text.substring(end + 1)
    }

    fun compile(type: Int, source: String, name: String): WebGLShader {
        val shader = CoreGl.createShader(type)
        CoreGl.shaderSource(shader, source)
        CoreGl.compileShader(shader)
        if (!CoreGl.shaderCompiled(shader)) {
            val log = CoreGl.getShaderInfoLog(shader)
            CoreGl.deleteShader(shader)
            throw IllegalStateException("634HD: " + name + " does not compile:\n" + log + "\n" + numbered(source))
        }
        return shader
    }

    fun link(vertex: WebGLShader, fragment: WebGLShader, name: String): WebGLProgram {
        val program = CoreGl.createProgram()
        CoreGl.attachShader(program, vertex)
        CoreGl.attachShader(program, fragment)
        CoreGl.bindFragDataLocation(program, 0, "fragColor")
        CoreGl.linkProgram(program)
        if (!CoreGl.programLinked(program)) {
            val log = CoreGl.getProgramInfoLog(program)
            CoreGl.deleteProgram(program)
            throw IllegalStateException("634HD: " + name + " does not link:\n" + log)
        }
        return program
    }

    /** Compiles and links /634hd/shaders/[base].vert and [base].frag. */
    fun program(base: String, defines: String = ""): WebGLProgram {
        val vertex = compile(CoreGl.VERTEX_SHADER, source("$base.vert", defines), "$base.vert")
        val fragment = compile(CoreGl.FRAGMENT_SHADER, source("$base.frag", defines), "$base.frag")
        val program = link(vertex, fragment, base)
        CoreGl.deleteShader(vertex)
        CoreGl.deleteShader(fragment)
        return program
    }

    private fun numbered(source: String): String {
        val out = StringBuilder()
        var line = 1
        for (text in source.split('\n')) {
            out.append(line.toString().padStart(4)).append("  ").append(text).append('\n')
            line++
        }
        return out.toString()
    }
}
