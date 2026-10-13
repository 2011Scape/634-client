#version 330 core
// Not part of the original client: 634HD (toolkit 6). The retained scene's fragment stage: texture
// unit 0 combined with the vertex colour as the toolkit's setColourOp sets the combiner (ff.frag's
// combine stage, specialised), then the alpha test and fog. Textures come from two arrays (64 and
// the toolkit's size) with a linear and a nearest sampler each; wrap and mipmapping per material.

#include "hd_lib.glsl"

in vec4 vColor;
in vec2 vTexCoord;
flat in int vMaterial;

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

vec4 sampleMaterial(vec4 material, vec4 extra) {
    int flags = int(material.w);
    float size = extra.z;
    vec2 uv = vTexCoord;
    // CLAMP_TO_EDGE on a sampler that repeats: keep the coordinate inside the edge texels' centres
    float edge = (flags & FLAG_LINEAR) != 0 ? 0.5 / size : 0.0;
    float top = (flags & FLAG_LINEAR) != 0 ? 1.0 - 0.5 / size : 1.0 - 0.25 / size;
    if ((flags & FLAG_REPEAT_S) == 0) uv.x = clamp(uv.x, edge, top);
    if ((flags & FLAG_REPEAT_T) == 0) uv.y = clamp(uv.y, edge, top);
    vec3 coord = vec3(uv, material.x);
    bool large = material.y > 0.5;
    bool linear = (flags & FLAG_LINEAR) != 0;
    if ((flags & FLAG_MIPMAP) != 0) {
        if (large) return linear ? texture(uLargeLinear, coord) : texture(uLargeNearest, coord);
        return linear ? texture(uSmallLinear, coord) : texture(uSmallNearest, coord);
    }
    if (large) return linear ? textureLod(uLargeLinear, coord, 0.0) : textureLod(uLargeNearest, coord, 0.0);
    return linear ? textureLod(uSmallLinear, coord, 0.0) : textureLod(uSmallNearest, coord, 0.0);
}

void main() {
    vec4 material = texelFetch(uMaterials, vMaterial * 2);
    vec4 color = vColor;
    if ((int(material.w) & FLAG_TEXTURED) != 0) {
        vec4 tex = hdColour(sampleMaterial(material, texelFetch(uMaterials, vMaterial * 2 + 1)));
        int op = int(material.z);
        if (op == 1) {
            // REPLACE, REPLACE
            color = tex;
        } else if (op == 2) {
            // INTERPOLATE(texture, previous, previous alpha), REPLACE
            color = vec4(clamp(tex.rgb * vColor.a + vColor.rgb * (1.0 - vColor.a), 0.0, 1.0), tex.a);
        } else if (op == 3) {
            // ADD, MODULATE
            color = vec4(clamp(tex.rgb + vColor.rgb, 0.0, 1.0), tex.a * vColor.a);
        } else if (op == 4) {
            // SUBTRACT, SUBTRACT
            color = clamp(tex - vColor, 0.0, 1.0);
        } else {
            // MODULATE, MODULATE
            color = tex * vColor;
        }
    }
    if (uAlphaTestEnabled && !alphaTestPasses(color.a)) {
        discard;
    }
    if (uFogEnabled) {
        float depth = gl_FragCoord.z / gl_FragCoord.w;
        float fogFactor = clamp((uFogEnd - depth) / (uFogEnd - uFogStart), 0.0, 1.0);
        color = vec4(mix(hdColour(uFogColor).rgb, color.rgb, fogFactor), color.a);
    }
    fragColor = color;
}
