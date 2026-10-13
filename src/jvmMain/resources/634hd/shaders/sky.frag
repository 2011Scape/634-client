#version 330 core
#include "hd_lib.glsl"
// Not part of the original client: 634HD (toolkit 6). The sky (HD_SKY): a gradient from the
// environment's horizon colour to its top colour, behind the 3D view (drawn at the far plane where
// nothing else was), matching the colour hdFog fades distant fragments to.

in vec2 vUv;
out vec4 fragColor;

uniform mat4 uInverseProjection;

void main() {
    vec4 eye = uInverseProjection * vec4(vUv * 2.0 - 1.0, 1.0, 1.0);
    vec3 dir = mat3(hdViewToWorld) * (eye.xyz / eye.w);
    fragColor = vec4(hdColour(vec4(hdSkyColour(dir), 1.0)).rgb, 1.0);
}
