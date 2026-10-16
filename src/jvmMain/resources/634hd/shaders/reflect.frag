#version 330 core

// Not part of the original client: 634HD. The water's reflection (reflect.vert): the object's colour
// with its texture, the transparent parts of leaves and fences left out as the scene's alpha test
// leaves them, fogged. sRGB, as the game's colours are: the water draws mix it in before the tonemap.

in vec4 vColor;
in vec2 vTexCoord;
in float vFog;
flat in int vMaterial;

uniform samplerBuffer uMaterials;
uniform sampler2DArray uSmallLinear;
uniform sampler2DArray uLargeLinear;
uniform sampler2DArray uFineLinear;
uniform vec4 uFogColor;

const int FLAG_TEXTURED = 1;
const int FLAG_REPEAT_S = 8;
const int FLAG_REPEAT_T = 16;

out vec4 fragColor;

void main() {
    vec4 color = vColor;
    vec4 material = texelFetch(uMaterials, vMaterial * 2);
    int flags = int(material.w);
    if ((flags & FLAG_TEXTURED) != 0) {
        vec2 uv = vTexCoord;
        if ((flags & FLAG_REPEAT_S) == 0) uv.x = clamp(uv.x, 0.0, 1.0);
        if ((flags & FLAG_REPEAT_T) == 0) uv.y = clamp(uv.y, 0.0, 1.0);
        vec3 coord = vec3(uv, material.x);
        vec4 texel = material.y > 1.5 ? texture(uFineLinear, coord) : material.y > 0.5 ? texture(uLargeLinear, coord) : texture(uSmallLinear, coord);
        color *= texel;
    }
    if (color.a < 0.5) {
        discard;
    }
    fragColor = vec4(mix(color.rgb, uFogColor.rgb, vFog), 1.0);
}
