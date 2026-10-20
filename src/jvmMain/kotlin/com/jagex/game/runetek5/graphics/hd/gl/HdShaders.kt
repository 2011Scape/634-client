/*
 * Not part of the original client: 634HD (toolkit 6). GLSL lives in resource files under
 * /634hd/shaders/; this loads, compiles and links them, and on failure throws with the driver's log
 * next to the numbered source lines it points at, so a broken shader names its file and line.
 */
package com.jagex.game.runetek5.graphics.hd.gl

object HdShaders {

    private const val ROOT = "/634hd/shaders/"

    private val cache = HashMap<String, String>()

    /**
     * The text of /634hd/shaders/[name], with its #include "file" lines replaced by those files (once
     * each); [defines] are inserted after the #version line.
     */
    fun source(name: String, defines: String = ""): String {
        val text = cache.getOrPut(name) { expand(name, HashSet()) }
        if (defines.isEmpty()) return text
        val end = text.indexOf('\n')
        return text.substring(0, end + 1) + defines + text.substring(end + 1)
    }

    private fun read(name: String): String {
        val stream = HdShaders::class.java.getResourceAsStream(ROOT + name)
            ?: throw IllegalStateException("634HD: missing shader resource " + ROOT + name)
        return stream.use { String(it.readBytes(), Charsets.UTF_8) }
    }

    /**
     * [name] with its includes pasted in, read through [read] (a file on a Windows checkout with
     * core.autocrlf, or saved by an editor, ends its lines in CRLF: those are made LF first, or the
     * strip of an include's #version line misses and the driver reports a second version directive).
     */
    internal fun expand(name: String, included: HashSet<String>, read: (String) -> String = ::read): String {
        val out = StringBuilder()
        for (line in read(name).replace("\r\n", "\n").split('\n')) {
            val trimmed = line.trim()
            if (trimmed.startsWith("#include")) {
                val file = trimmed.substringAfter('"').substringBefore('"')
                if (included.add(file)) {
                    out.append(expand(file, included, read).replace("#version 330 core\n", "")).append('\n')
                }
                continue
            }
            out.append(line).append('\n')
        }
        return out.toString()
    }

    /** Drops the cached sources, so the next program reads the files again (hot reload). */
    fun reload() {
        cache.clear()
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
        bindFrameBlock(program)
        // the shadow map's sampler (hd_lib.glsl) on its own unit, so it never shares one with a sampler2D
        val shadowMap = org.lwjgl.opengl.GL33C.glGetUniformLocation(program.id, "uHdShadowMap")
        if (shadowMap >= 0) {
            val previous = org.lwjgl.opengl.GL33C.glGetInteger(org.lwjgl.opengl.GL33C.GL_CURRENT_PROGRAM)
            org.lwjgl.opengl.GL33C.glUseProgram(program.id)
            org.lwjgl.opengl.GL33C.glUniform1i(shadowMap, com.jagex.game.runetek5.graphics.hd.scene.HdShadows.UNIT)
            org.lwjgl.opengl.GL33C.glUseProgram(previous)
        }
        val sceneDepth = org.lwjgl.opengl.GL33C.glGetUniformLocation(program.id, "uHdSceneDepth")
        if (sceneDepth >= 0) {
            val previous = org.lwjgl.opengl.GL33C.glGetInteger(org.lwjgl.opengl.GL33C.GL_CURRENT_PROGRAM)
            org.lwjgl.opengl.GL33C.glUseProgram(program.id)
            org.lwjgl.opengl.GL33C.glUniform1i(sceneDepth, com.jagex.game.runetek5.graphics.hd.scene.HdSoftParticles.UNIT)
            org.lwjgl.opengl.GL33C.glUseProgram(previous)
        }
        val maps = org.lwjgl.opengl.GL33C.glGetUniformBlockIndex(program.id, "HdMaterialMaps")
        if (maps != org.lwjgl.opengl.GL33C.GL_INVALID_INDEX) {
            org.lwjgl.opengl.GL33C.glUniformBlockBinding(program.id, maps, com.jagex.game.runetek5.graphics.hd.scene.HdMaterialMaps.BINDING)
        }
        val flow = org.lwjgl.opengl.GL33C.glGetUniformBlockIndex(program.id, "HdMaterialFlow")
        if (flow != org.lwjgl.opengl.GL33C.GL_INVALID_INDEX) {
            org.lwjgl.opengl.GL33C.glUniformBlockBinding(program.id, flow, com.jagex.game.runetek5.graphics.hd.scene.HdMaterialMaps.BINDING_FLOW)
        }
        val water = org.lwjgl.opengl.GL33C.glGetUniformBlockIndex(program.id, "HdWaterTypes")
        if (water != org.lwjgl.opengl.GL33C.GL_INVALID_INDEX) {
            org.lwjgl.opengl.GL33C.glUniformBlockBinding(program.id, water, com.jagex.game.runetek5.graphics.hd.scene.HdMaterialMaps.BINDING_WATER)
        }
        val mapNames = arrayOf("uHdNormalMaps", "uHdOrmMaps", "uHdEmissiveMaps", "uHdFlowMaps")
        val mapUnits = intArrayOf(
            com.jagex.game.runetek5.graphics.hd.scene.HdMaterialMaps.UNIT_NORMAL,
            com.jagex.game.runetek5.graphics.hd.scene.HdMaterialMaps.UNIT_ORM,
            com.jagex.game.runetek5.graphics.hd.scene.HdMaterialMaps.UNIT_EMISSIVE,
            com.jagex.game.runetek5.graphics.hd.scene.HdMaterialMaps.UNIT_FLOW,
        )
        for (i in mapNames.indices) {
            val location = org.lwjgl.opengl.GL33C.glGetUniformLocation(program.id, mapNames[i])
            if (location >= 0) {
                val previous = org.lwjgl.opengl.GL33C.glGetInteger(org.lwjgl.opengl.GL33C.GL_CURRENT_PROGRAM)
                org.lwjgl.opengl.GL33C.glUseProgram(program.id)
                org.lwjgl.opengl.GL33C.glUniform1i(location, mapUnits[i])
                org.lwjgl.opengl.GL33C.glUseProgram(previous)
            }
        }
        val reflection = org.lwjgl.opengl.GL33C.glGetUniformLocation(program.id, "uHdReflectionMap")
        if (reflection >= 0) {
            val previous = org.lwjgl.opengl.GL33C.glGetInteger(org.lwjgl.opengl.GL33C.GL_CURRENT_PROGRAM)
            org.lwjgl.opengl.GL33C.glUseProgram(program.id)
            org.lwjgl.opengl.GL33C.glUniform1i(reflection, com.jagex.game.runetek5.graphics.hd.scene.HdReflections.UNIT)
            org.lwjgl.opengl.GL33C.glUseProgram(previous)
        }
        val groundAo = org.lwjgl.opengl.GL33C.glGetUniformLocation(program.id, "uHdGroundAo")
        if (groundAo >= 0) {
            val previous = org.lwjgl.opengl.GL33C.glGetInteger(org.lwjgl.opengl.GL33C.GL_CURRENT_PROGRAM)
            org.lwjgl.opengl.GL33C.glUseProgram(program.id)
            org.lwjgl.opengl.GL33C.glUniform1i(groundAo, com.jagex.game.runetek5.graphics.hd.scene.HdShadows.GROUND_AO_UNIT)
            org.lwjgl.opengl.GL33C.glUseProgram(previous)
        }
        val aoBake = org.lwjgl.opengl.GL33C.glGetUniformLocation(program.id, "uHdAoBake")
        if (aoBake >= 0) {
            val previous = org.lwjgl.opengl.GL33C.glGetInteger(org.lwjgl.opengl.GL33C.GL_CURRENT_PROGRAM)
            org.lwjgl.opengl.GL33C.glUseProgram(program.id)
            org.lwjgl.opengl.GL33C.glUniform1i(aoBake, com.jagex.game.runetek5.graphics.hd.scene.HdShadows.AO_BAKE_UNIT)
            org.lwjgl.opengl.GL33C.glUseProgram(previous)
        }
        val aoMap = org.lwjgl.opengl.GL33C.glGetUniformLocation(program.id, "uHdAoMap")
        if (aoMap >= 0) {
            val previous = org.lwjgl.opengl.GL33C.glGetInteger(org.lwjgl.opengl.GL33C.GL_CURRENT_PROGRAM)
            org.lwjgl.opengl.GL33C.glUseProgram(program.id)
            org.lwjgl.opengl.GL33C.glUniform1i(aoMap, com.jagex.game.runetek5.graphics.hd.scene.HdShadows.AO_UNIT)
            org.lwjgl.opengl.GL33C.glUseProgram(previous)
        }
        val gridNames = arrayOf("uHdLightGrid", "uHdLightVisibility")
        val gridUnits = intArrayOf(com.jagex.game.runetek5.graphics.hd.scene.HdLightGrid.GRID_UNIT, com.jagex.game.runetek5.graphics.hd.scene.HdLightGrid.VIS_UNIT)
        for (i in gridNames.indices) {
            val location = org.lwjgl.opengl.GL33C.glGetUniformLocation(program.id, gridNames[i])
            if (location >= 0) {
                val previous = org.lwjgl.opengl.GL33C.glGetInteger(org.lwjgl.opengl.GL33C.GL_CURRENT_PROGRAM)
                org.lwjgl.opengl.GL33C.glUseProgram(program.id)
                org.lwjgl.opengl.GL33C.glUniform1i(location, gridUnits[i])
                org.lwjgl.opengl.GL33C.glUseProgram(previous)
            }
        }
        return program
    }

    /** Points the program's HdFrame block, if it has one (hd_lib.glsl), at the frame's uniform buffer. */
    fun bindFrameBlock(program: WebGLProgram) {
        val index = org.lwjgl.opengl.GL33C.glGetUniformBlockIndex(program.id, "HdFrame")
        if (index != org.lwjgl.opengl.GL33C.GL_INVALID_INDEX) {
            org.lwjgl.opengl.GL33C.glUniformBlockBinding(program.id, index, 0)
        }
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
