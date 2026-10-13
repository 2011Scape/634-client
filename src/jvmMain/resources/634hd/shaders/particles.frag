#version 330 core
// Not part of the original client: 634HD (toolkit 6). A GPU particle: a soft round glow, added.

#include "hd_lib.glsl"

in vec2 vCorner;
in vec4 vColour;
out vec4 fragColor;

void main() {
    float d = dot(vCorner, vCorner);
    if (d >= 1.0) discard;
    float glow = (1.0 - d) * (1.0 - d);
    vec3 colour = hdColour(vec4(vColour.rgb, 1.0)).rgb * (hdOn(HD_LINEAR) ? 2.5 : 1.0);
    fragColor = vec4(colour * glow * vColour.a, 1.0);
}
