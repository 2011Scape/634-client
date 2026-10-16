#version 330 core
#include "hd_lib.glsl"

// Not part of the original client: 634HD. The reflection of an emulated draw (reflect_dynamic.vert):
// its colour with its texture, the transparent parts left out as the scene's alpha test leaves
// them, fogged. sRGB, as the game's colours are.

in vec4 vColor;
in vec2 vTexCoord;
in vec3 vViewPos;
in vec3 vMirroredViewPos;

uniform int uTextured;
uniform sampler2D uTexture;
// the draw's fixed-function fog, for hdFog as ff.frag fogs the view
uniform vec4 uFogColor;
uniform float uFogStart;
uniform float uFogEnd;

out vec4 fragColor;

void main() {
    vec4 color = vColor;
    if (uTextured != 0) {
        color *= texture(uTexture, vTexCoord);
    }
    if (color.a < 0.5) {
        discard;
    }
    // fogged as the view is (hdFog: the environment's fog, the sky's colour, the fade): from the direction
    // the eye sees the object in (the sky's colour there), at the distance the mirrored eye sees it
    vec3 p = vViewPos * (length(vMirroredViewPos) / max(length(vViewPos), 1.0));
    vec3 rgb = hdOn(HD_LINEAR) ? hdToLinear(color.rgb) : color.rgb;
    rgb = hdFog(vec4(rgb, 1.0), p, -vMirroredViewPos.z, uFogColor, uFogStart, uFogEnd).rgb;
    fragColor = vec4(hdOn(HD_LINEAR) ? hdToSrgb(rgb) : rgb, 1.0);
}
