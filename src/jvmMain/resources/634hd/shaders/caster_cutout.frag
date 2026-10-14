#version 330 core

// Not part of the original client: 634HD. The cut-out caster's depth: a texel (or a face) less than
// half opaque casts no shadow, as the scene's alpha test lets it through.

in vec2 vTexCoord;
in float vAlpha;
flat in int vMaterial;

uniform samplerBuffer uMaterials;
uniform sampler2DArray uSmallLinear;
uniform sampler2DArray uLargeLinear;

const int FLAG_TEXTURED = 1;
const int FLAG_REPEAT_S = 8;
const int FLAG_REPEAT_T = 16;

void main() {
    float alpha = vAlpha;
    vec4 material = texelFetch(uMaterials, vMaterial * 2);
    int flags = int(material.w);
    if ((flags & FLAG_TEXTURED) != 0) {
        vec4 scroll = texelFetch(uMaterials, vMaterial * 2 + 1);
        vec2 uv = vTexCoord + scroll.xy;
        if ((flags & FLAG_REPEAT_S) == 0) uv.x = clamp(uv.x, 0.0, 1.0);
        if ((flags & FLAG_REPEAT_T) == 0) uv.y = clamp(uv.y, 0.0, 1.0);
        vec3 coord = vec3(uv, material.x);
        float texel = material.y > 0.5 ? textureLod(uLargeLinear, coord, 0.0).a : textureLod(uSmallLinear, coord, 0.0).a;
        alpha *= texel;
    }
    if (alpha < 0.5) {
        discard;
    }
}
