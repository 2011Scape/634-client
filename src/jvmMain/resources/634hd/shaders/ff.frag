#version 330 core
// Not part of the original client: 634HD (toolkit 6). The fixed-function pipeline as GLSL, ported
// from the browser's jaggl emulator (src/jsMain/kotlin/jaggl/FixedFunctionShader.js.kt).
#include "hd_lib.glsl"
#include "hd_material.glsl"
precision mediump float;
precision highp sampler3D;

in vec4 vColor;
#ifndef ARB_VERTEX
in vec4 vBase;
in vec3 vEyeNormal;
in vec3 vViewPos;
// the fixed-function lights, for per-pixel lighting (the vertex stage has the same uniforms)
uniform bool uLightingEnabled;
uniform vec4 uGlobalAmbient;
uniform bool uLightEnabled[8];
uniform vec4 uLightDiffuse[8];
uniform vec4 uLightPosition[8];
uniform vec3 uLightAttenuation[8];
#endif
#ifndef ARB_VERTEX
#define FF_VIEW_POS vViewPos
#define FF_NORMAL vEyeNormal
#else
// transpiled programs pass the eye position (ArbVertexProgramTranspiler), but no normal
in vec3 vArbViewPos;
#define FF_VIEW_POS vArbViewPos
#define FF_NORMAL vec3(0.0)
#endif
// 1 while the material is water (HD_WATER)
uniform int uHdWater;
// 1 while the ground is drawn (HD_TERRAIN)
uniform int uHdGround;
// 1 while the game's underwater pass draws (the riverbed): caustics there (HD_CAUSTICS)
uniform int uHdUnderwater;
// the slot of the texture's 634HD maps (HD_MATERIALS), -1 without
uniform int uHdMapSlot;
// the 634 texture id of unit 0 (the debug panel's texture overlay), -1 for none
uniform int uHdTexture;
// unit 0's coordinate, after parallax (main sets it before the texture stages)
vec2 gUv0;
// soft particles (HD_SOFT_PARTICLES): on, the scene's depth so far, the projection's depth terms
uniform int uHdSoft;
uniform sampler2D uHdSceneDepth;
uniform vec4 uHdDepthParams;
// the primary colour: the vertex colour, or the per-pixel lit one
vec4 gPrimary;
in vec3 vTexCoord0;
in vec3 vTexCoord1;
in vec3 vTexCoord2;
out vec4 fragColor;

uniform bool uUseTexture[3];
uniform bool uCubeMap[3];
uniform int uIs3D[3];
uniform sampler2D uTexture0;
uniform sampler2D uTexture1;
uniform sampler2D uTexture2;
uniform sampler3D uTexture3D0;
uniform sampler3D uTexture3D1;
uniform sampler3D uTexture3D2;
uniform samplerCube uTextureCube1;
uniform samplerCube uTextureCube2;
uniform bool uAlphaTestEnabled;
uniform int uAlphaFunc;
uniform float uAlphaRef;
uniform bool uFogEnabled;
uniform vec4 uFogColor;
uniform float uFogStart;
uniform float uFogEnd;

uniform int uCombineRgb[3];
uniform int uCombineAlpha[3];
uniform ivec3 uSrcRgb[3];
uniform ivec3 uOpRgb[3];
uniform ivec3 uSrcAlpha[3];
uniform ivec3 uOpAlpha[3];
uniform vec4 uTexEnvColor[3];
uniform vec2 uEnvScale[3];
// Interface scale N for 2D drawing to the window, 0 otherwise: see logicalCentre().
uniform highp float uLogicalScale;

vec4 combineSource(int src, vec4 texColor, vec4 previous, vec4 constant) {
    if (src == 34166) return constant;
    if (src == 5890) return texColor;
    if (src == 34167) return gPrimary;
    return previous;
}

vec3 applyOperand(int op, vec4 c) {
    if (op == 769) return vec3(1.0) - c.rgb;
    if (op == 770) return vec3(c.a);
    if (op == 771) return vec3(1.0 - c.a);
    return c.rgb;
}

float applyOperandAlpha(int op, vec4 c) {
    if (op == 771) return 1.0 - c.a;
    return c.a;
}

vec4 combineStage(int stage, vec4 texColor, vec4 previous) {
    vec4 constant = uTexEnvColor[stage];
    ivec3 srcRgb = uSrcRgb[stage];
    ivec3 opRgb = uOpRgb[stage];
    ivec3 srcAlpha = uSrcAlpha[stage];
    ivec3 opAlpha = uOpAlpha[stage];

    vec3 arg0 = applyOperand(opRgb.x, combineSource(srcRgb.x, texColor, previous, constant));
    vec3 arg1 = applyOperand(opRgb.y, combineSource(srcRgb.y, texColor, previous, constant));
    int combineRgb = uCombineRgb[stage];
    vec3 rgb;
    if (combineRgb == 7681) rgb = arg0;
    else if (combineRgb == 260) rgb = arg0 + arg1;
    else if (combineRgb == 34023) rgb = arg0 - arg1;
    else if (combineRgb == 34164) rgb = arg0 + arg1 - vec3(0.5);
    else if (combineRgb == 34165) {
        vec3 arg2 = applyOperand(opRgb.z, combineSource(srcRgb.z, texColor, previous, constant));
        rgb = arg0 * arg2 + arg1 * (vec3(1.0) - arg2);
    } else rgb = arg0 * arg1;

    float a0 = applyOperandAlpha(opAlpha.x, combineSource(srcAlpha.x, texColor, previous, constant));
    float a1 = applyOperandAlpha(opAlpha.y, combineSource(srcAlpha.y, texColor, previous, constant));
    int combineAlpha = uCombineAlpha[stage];
    float alpha;
    if (combineAlpha == 7681) alpha = a0;
    else if (combineAlpha == 260) alpha = a0 + a1;
    else if (combineAlpha == 34023) alpha = a0 - a1;
    else if (combineAlpha == 34164) alpha = a0 + a1 - 0.5;
    else if (combineAlpha == 34165) {
        float a2 = applyOperandAlpha(opAlpha.z, combineSource(srcAlpha.z, texColor, previous, constant));
        alpha = a0 * a2 + a1 * (1.0 - a2);
    } else alpha = a0 * a1;

    vec2 scale = uEnvScale[stage];
    return vec4(clamp(rgb * scale.x, 0.0, 1.0), clamp(alpha * scale.y, 0.0, 1.0));
}

bool alphaTestPasses(float a) {
    if (uAlphaFunc == 512) return false;
    if (uAlphaFunc == 513) return a < uAlphaRef;
    if (uAlphaFunc == 514) return a == uAlphaRef;
    if (uAlphaFunc == 515) return a <= uAlphaRef;
    if (uAlphaFunc == 516) return a > uAlphaRef;
    if (uAlphaFunc == 517) return a != uAlphaRef;
    if (uAlphaFunc == 518) return a >= uAlphaRef;
    return true;
}

// The client's 3D textures (animated water normal/detail maps) are GL_LUMINANCE_ALPHA. A core
// context has no such format: HdGl stores them as RG8 with a (R,R,R,G) swizzle, so sampling gives
// the (L,L,L,A) the client expects.
vec4 sampleLuminanceAlpha3D(sampler3D s, vec3 coord) {
    return texture(s, coord);
}

// At an interface scale N every logical pixel is N x N physical ones. At 1x a fragment samples
// at its pixel centre, which for 1:1 2D drawing is a texel centre, so LINEAR-filtered masks
// (the minimap's and compass's, on units 1/2) come out all-or-nothing. Sampled at physical
// centres, N x N fragments land between texel centres and edge pixels get partial coverage,
// which shimmers against the frame as the rotating minimap under them moves. Re-evaluating the
// (affine, in 2D) texcoord at the logical pixel's centre samples exactly where 1x does.
// highp: window coordinates past 2048 don't fit mediump at half-pixel precision.
// 1 to draw the scaled interface smoothly (HdSettings.smoothInterface): see sharpBilinear()
uniform int uHdSmooth;

// The interface at a whole-number scale, smoothed: bilinear between texels with smoothstep weights,
// so the steps between pixels soften while flat areas and texel centres keep their colour.
vec4 sharpBilinear(sampler2D tex, vec2 uv) {
    ivec2 size = textureSize(tex, 0);
    vec2 p = uv * vec2(size) - 0.5;
    vec2 i = floor(p);
    vec2 f = smoothstep(0.0, 1.0, p - i);
    ivec2 a = clamp(ivec2(i), ivec2(0), size - 1);
    ivec2 b = clamp(ivec2(i) + 1, ivec2(0), size - 1);
    vec4 c00 = texelFetch(tex, a, 0);
    vec4 c10 = texelFetch(tex, ivec2(b.x, a.y), 0);
    vec4 c01 = texelFetch(tex, ivec2(a.x, b.y), 0);
    vec4 c11 = texelFetch(tex, b, 0);
    return mix(mix(c00, c10, f.x), mix(c01, c11, f.x), f.y);
}

highp vec2 logicalCentre(highp vec2 coord) {
    if (uLogicalScale <= 1.0) return coord;
    highp vec2 centre = (floor(gl_FragCoord.xy / uLogicalScale) + 0.5) * uLogicalScale;
    highp vec2 offset = centre - gl_FragCoord.xy;
    return coord + dFdx(coord) * offset.x + dFdy(coord) * offset.y;
}

// The ground's texture (HD_TERRAIN) at [uv] for a fragment at eye-space [viewPos]. Kept close to
// the game's look: the texture at its own scale and angle throughout.
// - Repetition: a second sample of the same texture, shifted by a fraction of a tile, shows through a
//   soft, large noise mask, so the tile does not repeat in a grid.
// - A faint, broad brightness variation.
// - Steep slopes only (over about 55 degrees): projected from the side at the ground's own texture
//   scale (worked out from the uv and position derivatives), instead of stretched down the slope.
// In the texture's own (sRGB) colour.
vec4 hdTerrainTexture(sampler2D tex, vec2 uv, vec3 viewPos) {
    vec3 world = (hdViewToWorld * vec4(viewPos, 1.0)).xyz;
    vec3 dx = dFdx(world);
    vec3 dy = dFdy(world);
    vec3 n = normalize(cross(dx, dy));
    vec4 base = texture(tex, uv);
    float mask = smoothstep(0.45, 0.75, hdNoise(world.xz / 3072.0 + vec2(5.3, 1.7)));
    if (mask > 0.0) {
        base = mix(base, texture(tex, uv + vec2(0.37, 0.61)), mask * 0.45);
    }
    float slope = 1.0 - abs(n.y);
    float steep = smoothstep(0.42, 0.6, slope);
    if (steep > 0.0) {
        // uv per world unit along the ground, as the game maps it on the flat
        float scale = length(dFdx(uv)) / max(length(dx.xz), 0.0001);
        vec2 side = abs(n.x) > abs(n.z) ? world.zy : world.xy;
        base = mix(base, texture(tex, side * scale), steep);
    }
    base.rgb *= 0.96 + 0.08 * hdNoise(world.xz / 6144.0 + vec2(17.0, 3.0));
    return base;
}

vec4 sampleUnit(int unit) {
    // 3D textures (the animated water normal map) are sampled with the texcoord's full vec3;
    // the animated slice coordinate r arrives through the per-unit texture matrix.
    if (unit == 0) {
        if (uIs3D[0] != 0) return sampleLuminanceAlpha3D(uTexture3D0, vTexCoord0);
        // 634HD: the ground's texture blended (HD_TERRAIN); the base texture in linear colour too (tonemap)
        if (uHdGround != 0 && hdOn(HD_TERRAIN)) return hdColour(hdTerrainTexture(uTexture0, gUv0, FF_VIEW_POS));
        if (uHdSmooth != 0 && uLogicalScale > 1.0 && uHdScene == 0) return sharpBilinear(uTexture0, gUv0);
        return hdColour(texture(uTexture0, gUv0));
    }
    if (unit == 1) {
        if (uCubeMap[1]) return texture(uTextureCube1, vTexCoord1);
        if (uIs3D[1] != 0) return sampleLuminanceAlpha3D(uTexture3D1, vTexCoord1);
        return texture(uTexture1, logicalCentre(vTexCoord1.xy));
    }
    if (uCubeMap[2]) return texture(uTextureCube2, vTexCoord2);
    if (uIs3D[2] != 0) return sampleLuminanceAlpha3D(uTexture3D2, vTexCoord2);
    return texture(uTexture2, logicalCentre(vTexCoord2.xy));
}

void main() {
    gPrimary = vColor;
#ifndef ARB_VERTEX
    if (hdOn(HD_PIXEL_LIGHTING) && uLightingEnabled) {
        vec4 lit = hdEnvAmbientLight(uGlobalAmbient);
        for (int i = 0; i < 8; i++) {
            if (!uLightEnabled[i]) continue;
            if (uLightPosition[i].w == 0.0) {
                lit += hdEnvDiffuse(uLightDiffuse[i], i) * max(dot(vEyeNormal, normalize(uLightPosition[i].xyz)), 0.0);
            } else {
                lit += hdPointLight(vEyeNormal, vViewPos, uLightPosition[i].xyz, uLightAttenuation[i], uLightDiffuse[i].rgb);
            }
        }
        gPrimary = hdApplyLight(vBase, lit);
    }
    gPrimary = hdAddPointLights(gPrimary, vBase, vViewPos, vEyeNormal);
#endif
    vec4 color = gPrimary;
    gUv0 = vTexCoord0.xy;
    bool mapped = uUseTexture[0] && uIs3D[0] == 0 && hdHasMaps(uHdMapSlot);
    if (mapped) gUv0 = hdParallaxUv(uHdMapSlot, gUv0, FF_VIEW_POS);
    for (int i = 0; i < 3; i++) {
        if (uUseTexture[i]) color = combineStage(i, sampleUnit(i), color);
    }
    if (uAlphaTestEnabled && !alphaTestPasses(color.a)) {
        discard;
    }
    if (uHdWater != 0 && hdOn(HD_WATER)) {
        color = hdWater(color, FF_VIEW_POS);
    }
    if (uHdSoft != 0 && hdOn(HD_SOFT_PARTICLES)) {
        // fade out over the last 96 units before the geometry behind: no hard line where it cuts in
        float sceneZ = texture(uHdSceneDepth, gl_FragCoord.xy * uHdDepthParams.zw).r * 2.0 - 1.0;
        float fragmentZ = gl_FragCoord.z * 2.0 - 1.0;
        float sceneEye = uHdDepthParams.y / (sceneZ + uHdDepthParams.x);
        float fragmentEye = uHdDepthParams.y / (fragmentZ + uHdDepthParams.x);
        color.a *= clamp((sceneEye - fragmentEye) / 96.0, 0.0, 1.0);
    }
    if (mapped) color = hdApplyMaterial(uHdMapSlot, color, gUv0, FF_VIEW_POS);
    if (uHdUnderwater != 0 && hdOn(HD_CAUSTICS)) {
        vec3 world = (hdViewToWorld * vec4(FF_VIEW_POS, 1.0)).xyz;
        float light = hdCaustics(world.xz, hdTime.x * hdWaterParams.y);
        color.rgb += color.rgb * light * 0.55 * (hdSunDirection.w > 0.5 ? 1.0 : 0.4);
    }
    color = hdApplyShadow(color, FF_VIEW_POS, FF_NORMAL);
    hdAppliedOcclusion = uHdGround == 3 ? 1.0 : uHdGround == 2 ? hdGroundOcclusion(FF_VIEW_POS, FF_NORMAL) : hdAmbientOcclusion(FF_VIEW_POS, FF_NORMAL);
    color.rgb *= hdAppliedOcclusion;
    float depth = gl_FragCoord.z / gl_FragCoord.w;
    if (uFogEnabled) {
        color = hdFog(color, FF_VIEW_POS, depth, uFogColor, uFogStart, uFogEnd);
    }
    fragColor = hdDebugOverlay(color, FF_VIEW_POS, FF_NORMAL, uUseTexture[0] ? uHdTexture : -1);
}
