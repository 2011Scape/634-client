#version 330 core
// Not part of the original client: 634HD (toolkit 6). The retained scene's fragment stage: texture
// unit 0 combined with the vertex colour as the toolkit's setColourOp sets the combiner (ff.frag's
// combine stage, specialised), then the alpha test and fog. Textures come from two arrays (64 and
// the toolkit's size) with a linear and a nearest sampler each; wrap and mipmapping per material.

#include "hd_lib.glsl"
#include "hd_material.glsl"

in vec4 vColor;
in vec2 vTexCoord;
flat in int vMaterial;
in vec4 vBase;
in vec3 vEyeNormal;
in vec3 vViewPos;
flat in int vSlot;
flat in int vLit;
in float vOcclusion;

uniform samplerBuffer uObjects;
// alpha to coverage for the cut-outs (HD_FOLIAGE with a multisampled view)
uniform bool uAlphaToCoverage;
uniform vec4 uGlobalAmbient;
uniform vec4 uDirLight[2];
uniform vec4 uDirDiffuse[2];

uniform samplerBuffer uMaterials;
uniform sampler2DArray uSmallLinear;
uniform sampler2DArray uSmallNearest;
uniform sampler2DArray uLargeLinear;
uniform sampler2DArray uLargeNearest;

uniform bool uAlphaTestEnabled;
uniform int uAlphaFunc;
uniform float uAlphaRef;
uniform bool uFogEnabled;
uniform vec4 uFogColor;
uniform float uFogStart;
uniform float uFogEnd;

const int FLAG_TEXTURED = 1;
const int FLAG_MIPMAP = 2;
const int FLAG_LINEAR = 4;
const int FLAG_REPEAT_S = 8;
const int FLAG_REPEAT_T = 16;
const int FLAG_UNLIT = 32;

out vec4 fragColor;

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

vec4 sampleMaterial(vec4 material, vec4 extra, vec2 uv) {
    int flags = int(material.w);
    float size = extra.z;
    // CLAMP_TO_EDGE on a sampler that repeats: keep the coordinate inside the edge texels' centres
    float edge = (flags & FLAG_LINEAR) != 0 ? 0.5 / size : 0.0;
    float top = (flags & FLAG_LINEAR) != 0 ? 1.0 - 0.5 / size : 1.0 - 0.25 / size;
    if ((flags & FLAG_REPEAT_S) == 0) uv.x = clamp(uv.x, edge, top);
    if ((flags & FLAG_REPEAT_T) == 0) uv.y = clamp(uv.y, edge, top);
    vec3 coord = vec3(uv, material.x);
    bool large = material.y > 0.5;
    bool linear = (flags & FLAG_LINEAR) != 0;
    if ((flags & FLAG_MIPMAP) != 0 || hdOn(HD_MIPMAPS)) {
        if (large) return linear ? texture(uLargeLinear, coord) : texture(uLargeNearest, coord);
        return linear ? texture(uSmallLinear, coord) : texture(uSmallNearest, coord);
    }
    if (large) return linear ? textureLod(uLargeLinear, coord, 0.0) : textureLod(uLargeNearest, coord, 0.0);
    return linear ? textureLod(uSmallLinear, coord, 0.0) : textureLod(uSmallNearest, coord, 0.0);
}

// The colour lit per pixel, as scene.vert lights it per vertex (HD_PIXEL_LIGHTING).
vec4 pixelLit() {
    vec4 lit = uGlobalAmbient;
    for (int i = 0; i < 2; i++) {
        if (uDirLight[i].w > 0.5) {
            lit += uDirDiffuse[i] * max(dot(vEyeNormal, normalize(uDirLight[i].xyz)), 0.0);
        }
    }
    int base = vSlot * 17;
    int count = int(texelFetch(uObjects, base + 8).y);
    for (int i = 0; i < count; i++) {
        vec4 light = texelFetch(uObjects, base + 9 + i * 2);
        vec4 diffuse = texelFetch(uObjects, base + 10 + i * 2);
        lit += hdPointLight(vEyeNormal, vViewPos, light.xyz, vec3(diffuse.w, 0.0, light.w), diffuse.rgb);
    }
    return hdApplyLight(vBase, lit);
}

void main() {
    int mapSlot = -1;
    vec2 mapUv = vTexCoord;
    vec4 material = texelFetch(uMaterials, vMaterial * 2);
    vec4 primary = (vLit != 0 && hdOn(HD_PIXEL_LIGHTING)) ? pixelLit() : vColor;
    if ((int(material.w) & FLAG_UNLIT) == 0) {
        primary = hdAddPointLights(primary, vBase, vViewPos, vEyeNormal);
    }
    vec4 color = primary;
    if ((int(material.w) & FLAG_TEXTURED) != 0) {
        vec4 extra = texelFetch(uMaterials, vMaterial * 2 + 1);
        // the texture's 634HD maps (HD_MATERIALS): extra.w is its slot plus one, 0 without
        mapSlot = int(extra.w) - 1;
        mapUv = hdParallaxUv(mapSlot, vTexCoord, vViewPos);
        vec4 tex = hdColour(sampleMaterial(material, extra, mapUv));
        int op = int(material.z);
        if (op == 1) {
            // REPLACE, REPLACE
            color = tex;
        } else if (op == 2) {
            // INTERPOLATE(texture, previous, previous alpha), REPLACE
            color = vec4(clamp(tex.rgb * primary.a + primary.rgb * (1.0 - primary.a), 0.0, 1.0), tex.a);
        } else if (op == 3) {
            // ADD, MODULATE
            color = vec4(clamp(tex.rgb + primary.rgb, 0.0, 1.0), tex.a * primary.a);
        } else if (op == 4) {
            // SUBTRACT, SUBTRACT
            color = clamp(tex - primary, 0.0, 1.0);
        } else {
            // MODULATE, MODULATE
            color = tex * primary;
        }
    }
    if (uAlphaToCoverage && uAlphaTestEnabled && hdOn(HD_FOLIAGE)) {
        // a sharp edge one pixel wide, its coverage from the alpha: no stair-steps, no shimmer
        float a = (color.a - 0.5) / max(fwidth(color.a), 0.0001) + 0.5;
        if (a <= 0.0) {
            discard;
        }
        color.a = clamp(a, 0.0, 1.0);
    } else if (uAlphaTestEnabled && !alphaTestPasses(color.a)) {
        discard;
    }
    color = hdApplyMaterial(mapSlot, color, mapUv, vViewPos);
    color = hdApplyShadow(color, vViewPos, vEyeNormal);
    // the retained scene's occlusion is baked into its vertices once the region is baked; the height
    // maps (moving models, the ground) until then
    color.rgb *= (hdOn(HD_AO) && hdAoBake.z > 0.5) ? mix(1.0, vOcclusion, hdAoParams.y) : hdAmbientOcclusion(vViewPos, vEyeNormal);
    float depth = gl_FragCoord.z / gl_FragCoord.w;
    if (hdFaded(length(vViewPos), gl_FragCoord.xy)) {
        discard;
    }
    if (uFogEnabled) {
        color = hdFog(color, vViewPos, depth, uFogColor, uFogStart, uFogEnd);
    }
    fragColor = hdDebugOverlay(color, vViewPos, vEyeNormal, (int(material.w) & FLAG_TEXTURED) != 0 ? 4096 + vMaterial : -1);
}
