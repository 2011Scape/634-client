#version 330 core

// Not part of the original client: 634HD. The reflection of an emulated draw (reflect_dynamic.vert):
// its colour with its texture, the transparent parts left out as the scene's alpha test leaves
// them, fogged. sRGB, as the game's colours are.

in vec4 vColor;
in vec2 vTexCoord;
in float vFog;

uniform int uTextured;
uniform sampler2D uTexture;
uniform vec4 uFogColor;

out vec4 fragColor;

void main() {
    vec4 color = vColor;
    if (uTextured != 0) {
        color *= texture(uTexture, vTexCoord);
    }
    if (color.a < 0.5) {
        discard;
    }
    fragColor = vec4(mix(color.rgb, uFogColor.rgb, vFog), 1.0);
}
