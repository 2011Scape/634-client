/*
 * Not part of the original client: 634HD (toolkit 6), ported from the browser's jaggl emulator
 * (src/jsMain/kotlin/jaggl/FixedFunctionShader.js.kt). The GLSL is in /634hd/shaders/ff.vert and ff.frag.
 */
package com.jagex.game.runetek5.graphics.hd.gl

const val ATTRIB_POSITION = 0
const val ATTRIB_COLOR = 1
const val ATTRIB_TEXCOORD0 = 2
const val ATTRIB_NORMAL = 3
const val ATTRIB_TEXCOORD1 = 4
const val ATTRIB_COUNT = 5

/** Exposes the fixed-function fragment stage to the transpiled ARB vertex programs. */
internal val FF_FRAGMENT_SOURCE: String get() = HdShaders.source("ff.frag")

class FixedFunctionShader(private val gl: WebGL2RenderingContext) {
    val program: WebGLProgram

    val uModelView: WebGLUniformLocation?
    val uModelViewProjection: WebGLUniformLocation?
    val uLightingEnabled: WebGLUniformLocation?
    val uGlobalAmbient: WebGLUniformLocation?
    val uLightEnabled: Array<WebGLUniformLocation?>
    val uLightAmbient: Array<WebGLUniformLocation?>
    val uLightDiffuse: Array<WebGLUniformLocation?>
    val uLightPosition: Array<WebGLUniformLocation?>
    val uLightAttenuation: Array<WebGLUniformLocation?>
    val uTextureMatrixU: Array<WebGLUniformLocation?>
    val uTexGenMode: Array<WebGLUniformLocation?>
    val uTexGenObjPlane: Array<WebGLUniformLocation?>
    val uTexGenEyePlane: Array<WebGLUniformLocation?>
    // Fragment-stage locations (shared layout with the transpiled ARB programs).
    val frag: FfFragmentUniformLocations

    init {
        val prog = HdShaders.program("ff")
        program = prog

        uModelView = gl.getUniformLocation(program, "uModelView")
        uModelViewProjection = gl.getUniformLocation(program, "uModelViewProjection")
        uTextureMatrixU = perUnit("uTextureMatrixU")
        uTexGenMode = perUnit("uTexGenMode")
        uTexGenObjPlane = Array(12) { gl.getUniformLocation(program, "uTexGenObjPlane[$it]") }
        uTexGenEyePlane = Array(12) { gl.getUniformLocation(program, "uTexGenEyePlane[$it]") }
        uLightingEnabled = gl.getUniformLocation(program, "uLightingEnabled")
        uGlobalAmbient = gl.getUniformLocation(program, "uGlobalAmbient")
        uLightEnabled = perLight("uLightEnabled")
        uLightAmbient = perLight("uLightAmbient")
        uLightDiffuse = perLight("uLightDiffuse")
        uLightPosition = perLight("uLightPosition")
        uLightAttenuation = perLight("uLightAttenuation")
        frag = FfFragmentUniformLocations(gl, prog)

        // Sampler unit assignment: 2D samplers on logical units 0..2, 3D samplers on the
        // reserved units 3..5, cube samplers on the reserved units 6..7.
        gl.useProgram(program)
        for (unit in 0 until 3) {
            gl.uniform1i(frag.texture2D[unit], unit)
            gl.uniform1i(frag.texture3D[unit], FF_TEXTURE_3D_SAMPLER_UNIT + unit)
        }
        gl.uniform1i(frag.textureCube[1], CUBE_SAMPLER_UNIT + 1)
        gl.uniform1i(frag.textureCube[2], CUBE_SAMPLER_UNIT + 2)
        gl.useProgram(null)
    }

    private fun perLight(name: String): Array<WebGLUniformLocation?> = Array(MAX_LIGHTS) {
        gl.getUniformLocation(program, "$name[$it]")
    }

    private fun perUnit(name: String): Array<WebGLUniformLocation?> = Array(3) {
        gl.getUniformLocation(program, "$name[$it]")
    }

}

/**
 * Fragment-stage uniform locations for the fixed-function fragment source. Used both by the
 * fixed-function program itself and by transpiled ARB vertex programs, which pair the same
 * fragment stage with a generated vertex stage.
 */
class FfFragmentUniformLocations(gl: WebGL2RenderingContext, program: WebGLProgram) {
    val useTexture: Array<WebGLUniformLocation?> = Array(3) { gl.getUniformLocation(program, "uUseTexture[$it]") }
    val cubeMap: Array<WebGLUniformLocation?> = Array(3) { gl.getUniformLocation(program, "uCubeMap[$it]") }
    val is3D: Array<WebGLUniformLocation?> = Array(3) { gl.getUniformLocation(program, "uIs3D[$it]") }
    val texture2D: Array<WebGLUniformLocation?> = Array(3) { gl.getUniformLocation(program, "uTexture$it") }
    val texture3D: Array<WebGLUniformLocation?> = Array(3) { gl.getUniformLocation(program, "uTexture3D$it") }
    val textureCube: Array<WebGLUniformLocation?> = arrayOf(
        null,
        gl.getUniformLocation(program, "uTextureCube1"),
        gl.getUniformLocation(program, "uTextureCube2"),
    )
    val alphaTestEnabled: WebGLUniformLocation? = gl.getUniformLocation(program, "uAlphaTestEnabled")
    val alphaFunc: WebGLUniformLocation? = gl.getUniformLocation(program, "uAlphaFunc")
    val alphaRef: WebGLUniformLocation? = gl.getUniformLocation(program, "uAlphaRef")
    val fogEnabled: WebGLUniformLocation? = gl.getUniformLocation(program, "uFogEnabled")
    val fogColor: WebGLUniformLocation? = gl.getUniformLocation(program, "uFogColor")
    val fogStart: WebGLUniformLocation? = gl.getUniformLocation(program, "uFogStart")
    val fogEnd: WebGLUniformLocation? = gl.getUniformLocation(program, "uFogEnd")
    val combineRgb: Array<WebGLUniformLocation?> = Array(3) { gl.getUniformLocation(program, "uCombineRgb[$it]") }
    val combineAlpha: Array<WebGLUniformLocation?> = Array(3) { gl.getUniformLocation(program, "uCombineAlpha[$it]") }
    val srcRgb: Array<WebGLUniformLocation?> = Array(3) { gl.getUniformLocation(program, "uSrcRgb[$it]") }
    val opRgb: Array<WebGLUniformLocation?> = Array(3) { gl.getUniformLocation(program, "uOpRgb[$it]") }
    val srcAlpha: Array<WebGLUniformLocation?> = Array(3) { gl.getUniformLocation(program, "uSrcAlpha[$it]") }
    val opAlpha: Array<WebGLUniformLocation?> = Array(3) { gl.getUniformLocation(program, "uOpAlpha[$it]") }
    val texEnvColor: Array<WebGLUniformLocation?> = Array(3) { gl.getUniformLocation(program, "uTexEnvColor[$it]") }
    val envScale: Array<WebGLUniformLocation?> = Array(3) { gl.getUniformLocation(program, "uEnvScale[$it]") }
    val logicalScale: WebGLUniformLocation? = gl.getUniformLocation(program, "uLogicalScale")
    // 634HD: whether the draw belongs to the 3D view (hd_lib.glsl)
    val hdScene: WebGLUniformLocation? = gl.getUniformLocation(program, "uHdScene")
    // 634HD: whether the material is water (HD_WATER)
    val hdWater: WebGLUniformLocation? = gl.getUniformLocation(program, "uHdWater")
    // 634HD: whether the ground is being drawn (HD_TERRAIN)
    val hdGround: WebGLUniformLocation? = gl.getUniformLocation(program, "uHdGround")
    // 634HD: soft particles - on, and the projection's depth terms (HD_SOFT_PARTICLES)
    val hdSoft: WebGLUniformLocation? = gl.getUniformLocation(program, "uHdSoft")
    // 634HD: the slot of the texture's material maps (HD_MATERIALS)
    val hdMapSlot: WebGLUniformLocation? = gl.getUniformLocation(program, "uHdMapSlot")
    // 634HD: the texture id, for the debug panel's texture overlay
    val hdTexture: WebGLUniformLocation? = gl.getUniformLocation(program, "uHdTexture")
    // 634HD: the scaled interface drawn smoothly (HdSettings.smoothInterface)
    val hdSmooth: WebGLUniformLocation? = gl.getUniformLocation(program, "uHdSmooth")
    val hdDepthParams: WebGLUniformLocation? = gl.getUniformLocation(program, "uHdDepthParams")

    // Last values uploaded into *this* program's uniforms (each linked program has its own
    // storage), so uploadFfFragmentUniforms only sends the uniforms that actually differ.
    // Toggling texturing between models used to re-send ~11 uniforms per draw; now it's one.
    // Sentinels (MIN_VALUE / NaN) never match, forcing the first upload.
    internal val shadowInt = IntArray(FF_SHADOW_INTS) { Int.MIN_VALUE }
    internal val shadowFloat = FloatArray(FF_SHADOW_FLOATS) { Float.NaN }
}

/**
 * Uploads the fragment-side fixed-function state (alpha test, fog, texenv/combine, sampler
 * rebindings for the reserved cube/3D units). [force] re-uploads the global state instead of
 * waiting for the dirty flags.
 *
 * [program] identifies which compiled WebGLProgram this upload targets. The fixed-function
 * fragment source is shared (verbatim) by the fixed-function program and every transpiled ARB
 * vertex program, but each is a distinct linked program with its own uniform storage — a value
 * uploaded into one is invisible to the others. [GlState.texEnvDirty]/[GlState.ffpStateDirty]
 * are cleared globally by whichever program draws first after a state change, so without this
 * check every *other* program that draws afterwards would keep stale (default-zero) uniforms:
 * textures reporting as disabled, fog/alpha-test off, texenv colours black. Detecting a program
 * switch and forcing a full re-upload in that case fixes that (surfaces as, e.g., HD water
 * losing its texture/colour or flashing white depending on draw order / camera angle).
 */
fun uploadFfFragmentUniforms(gl: WebGL2RenderingContext, state: GlState, L: FfFragmentUniformLocations, program: WebGLProgram, force: Boolean) {
    val programChanged = state.lastFragUniformProgram !== program
    state.lastFragUniformProgram = program
    val forceGlobal = force || programChanged
    if (forceGlobal) {
        state.ffpStateDirty = false
        setInt(gl, L, L.alphaTestEnabled, SI_ALPHA_TEST, if (state.alphaTestEnabled) 1 else 0)
        setInt(gl, L, L.alphaFunc, SI_ALPHA_FUNC, state.alphaFunc)
        setFloat(gl, L, L.alphaRef, SF_ALPHA_REF, state.alphaRef)
        setInt(gl, L, L.fogEnabled, SI_FOG_ENABLED, if (state.fogEnabled) 1 else 0)
        setFloat4(gl, L, L.fogColor, SF_FOG_COLOR, state.fogColor)
        setFloat(gl, L, L.fogStart, SF_FOG_START, state.fogStart)
        setFloat(gl, L, L.fogEnd, SF_FOG_END, state.fogEnd)
    }
    setFloat(gl, L, L.logicalScale, SF_LOGICAL_SCALE, state.logicalSampleScale)
    setInt(gl, L, L.hdScene, SI_HD_SCENE, state.hdScene)
    setInt(gl, L, L.hdWater, SI_HD_WATER, state.hdWater)
    setInt(gl, L, L.hdGround, SI_HD_GROUND, state.hdGround)
    setInt(gl, L, L.hdSoft, SI_HD_SOFT, state.hdSoft)
    setInt(gl, L, L.hdMapSlot, SI_HD_MAP_SLOT, state.hdMapSlot)
    setInt(gl, L, L.hdTexture, SI_HD_TEXTURE, state.hdTexture)
    setInt(gl, L, L.hdSmooth, SI_HD_SMOOTH, if (com.jagex.game.runetek5.client.features.HdSettings.smoothInterface) 1 else 0)
    if (state.hdSoft != 0) setFloat4(gl, L, L.hdDepthParams, SF_HD_DEPTH, state.hdDepthParams)
    for (unit in 0 until 3) {
        val cube = state.textureTarget[unit] == GL_TEXTURE_CUBE_MAP
        val is3d = state.textureTarget[unit] == GL_TEXTURE_3D
        if (state.texEnvDirty[unit] || programChanged) {
            state.texEnvDirty[unit] = false
            val i = unit * SI_UNIT_STRIDE
            setInt(gl, L, L.useTexture[unit], i + SI_USE_TEXTURE, if (state.texturingEnabled[unit]) 1 else 0)
            setInt(gl, L, L.is3D[unit], i + SI_IS_3D, if (state.texturingEnabled[unit] && is3d) 1 else 0)
            setInt(gl, L, L.cubeMap[unit], i + SI_CUBE_MAP, if (cube) 1 else 0)
            if (state.texturingEnabled[unit]) uploadFfCombine(gl, L, state, unit)
        }
        // Copy the logical-unit binding onto the reserved sampler unit (2D samplers read
        // their logical unit directly, cube/3D samplers read the reserved ones).
        if (unit > 0 && state.texturingEnabled[unit] && cube) {
            state.bindSampler(GL_TEXTURE_CUBE_MAP, CUBE_SAMPLER_UNIT + unit, state.boundTextureCubeMap[unit])
        }
        if (state.texturingEnabled[unit] && is3d) {
            state.bindSampler(GL_TEXTURE_3D, FF_TEXTURE_3D_SAMPLER_UNIT + unit, state.boundTexture3D[unit])
        }
    }
}

private fun uploadFfCombine(gl: WebGL2RenderingContext, L: FfFragmentUniformLocations, state: GlState, unit: Int) {
    val i = unit * SI_UNIT_STRIDE
    setInt(gl, L, L.combineRgb[unit], i + SI_COMBINE_RGB, state.combineRgb[unit])
    setInt(gl, L, L.combineAlpha[unit], i + SI_COMBINE_ALPHA, state.combineAlpha[unit])
    setInt3(gl, L, L.srcRgb[unit], i + SI_SRC_RGB, state.source0Rgb[unit], state.source1Rgb[unit], state.source2Rgb[unit])
    setInt3(gl, L, L.opRgb[unit], i + SI_OP_RGB, state.operand0Rgb[unit], state.operand1Rgb[unit], state.operand2Rgb[unit])
    setInt3(gl, L, L.srcAlpha[unit], i + SI_SRC_ALPHA, state.source0Alpha[unit], state.source1Alpha[unit], state.source2Alpha[unit])
    setInt3(gl, L, L.opAlpha[unit], i + SI_OP_ALPHA, state.operand0Alpha[unit], state.operand1Alpha[unit], state.operand2Alpha[unit])
    val f = unit * SF_UNIT_STRIDE
    setFloat4(gl, L, L.texEnvColor[unit], f + SF_TEX_ENV_COLOR, state.textureEnvColor[unit])
    val sh = L.shadowFloat
    val rgbScale = state.rgbScale[unit]
    val alphaScale = state.alphaScale[unit]
    if (sh[f + SF_ENV_SCALE] != rgbScale || sh[f + SF_ENV_SCALE + 1] != alphaScale) {
        sh[f + SF_ENV_SCALE] = rgbScale
        sh[f + SF_ENV_SCALE + 1] = alphaScale
        gl.uniform2f(L.envScale[unit], rgbScale, alphaScale)
    }
}

private fun setInt(gl: WebGL2RenderingContext, L: FfFragmentUniformLocations, loc: WebGLUniformLocation?, slot: Int, v: Int) {
    if (L.shadowInt[slot] == v) return
    L.shadowInt[slot] = v
    gl.uniform1i(loc, v)
}

private fun setInt3(gl: WebGL2RenderingContext, L: FfFragmentUniformLocations, loc: WebGLUniformLocation?, slot: Int, a: Int, b: Int, c: Int) {
    val sh = L.shadowInt
    if (sh[slot] == a && sh[slot + 1] == b && sh[slot + 2] == c) return
    sh[slot] = a; sh[slot + 1] = b; sh[slot + 2] = c
    gl.uniform3i(loc, a, b, c)
}

private fun setFloat(gl: WebGL2RenderingContext, L: FfFragmentUniformLocations, loc: WebGLUniformLocation?, slot: Int, v: Float) {
    if (L.shadowFloat[slot] == v) return
    L.shadowFloat[slot] = v
    gl.uniform1f(loc, v)
}

private fun setFloat4(gl: WebGL2RenderingContext, L: FfFragmentUniformLocations, loc: WebGLUniformLocation?, slot: Int, v: FloatArray) {
    val sh = L.shadowFloat
    if (sh[slot] == v[0] && sh[slot + 1] == v[1] && sh[slot + 2] == v[2] && sh[slot + 3] == v[3]) return
    sh[slot] = v[0]; sh[slot + 1] = v[1]; sh[slot + 2] = v[2]; sh[slot + 3] = v[3]
    gl.uniform4fv(loc, v)
}

// Shadow-slot layout for FfFragmentUniformLocations.shadowInt / shadowFloat.
private const val SI_USE_TEXTURE = 0
private const val SI_IS_3D = 1
private const val SI_CUBE_MAP = 2
private const val SI_COMBINE_RGB = 3
private const val SI_COMBINE_ALPHA = 4
private const val SI_SRC_RGB = 5
private const val SI_OP_RGB = 8
private const val SI_SRC_ALPHA = 11
private const val SI_OP_ALPHA = 14
private const val SI_UNIT_STRIDE = 17
private const val SI_ALPHA_TEST = 3 * SI_UNIT_STRIDE
private const val SI_ALPHA_FUNC = SI_ALPHA_TEST + 1
private const val SI_FOG_ENABLED = SI_ALPHA_TEST + 2
private const val SI_HD_SCENE = SI_ALPHA_TEST + 3
private const val SI_HD_WATER = SI_ALPHA_TEST + 4
private const val SI_HD_GROUND = SI_ALPHA_TEST + 5
private const val SI_HD_SOFT = SI_ALPHA_TEST + 6
private const val SI_HD_MAP_SLOT = SI_ALPHA_TEST + 7
private const val SI_HD_TEXTURE = SI_ALPHA_TEST + 8
private const val SI_HD_SMOOTH = SI_ALPHA_TEST + 9
internal const val FF_SHADOW_INTS = SI_ALPHA_TEST + 10

private const val SF_TEX_ENV_COLOR = 0
private const val SF_ENV_SCALE = 4
private const val SF_UNIT_STRIDE = 6
private const val SF_ALPHA_REF = 3 * SF_UNIT_STRIDE
private const val SF_FOG_COLOR = SF_ALPHA_REF + 1
private const val SF_FOG_START = SF_ALPHA_REF + 5
private const val SF_FOG_END = SF_ALPHA_REF + 6
private const val SF_LOGICAL_SCALE = SF_ALPHA_REF + 7
private const val SF_HD_DEPTH = SF_ALPHA_REF + 8
internal const val FF_SHADOW_FLOATS = SF_ALPHA_REF + 12
