#version 330 core
#include "hd_lib.glsl"
// Not part of the original client: 634HD (toolkit 6). The sky (HD_SKY): a gradient from the
// environment's horizon colour to its top colour, seen through the environment's fog near the horizon
// (hdFoggedSkyColour), behind the 3D view (drawn at the far plane where nothing else was), matching
// the colour hdFog fades distant fragments to.

in vec2 vUv;
out vec4 fragColor;

uniform mat4 uInverseProjection;
// 634HD reflections: the colour left sRGB (the reflection texture holds the game's colours)
uniform int uSrgbOut;

void main() {
    vec4 eye = uInverseProjection * vec4(vUv * 2.0 - 1.0, 1.0, 1.0);
    vec3 dir = mat3(hdViewToWorld) * (eye.xyz / eye.w);
    vec3 colour = hdFoggedSkyColour(dir);
    float night = hdMoon.w;
    if (night > 0.0) {
        vec3 d = normalize(dir);
        float up = -d.y;
        // (the camera looks down from high up: the sky beyond the scene's edge is below the horizon)
        if (up > -0.3) {
            // stars: a sparse field of points on a grid over the sky, each its own brightness and twinkle
            vec2 cell = vec2(atan(d.x, d.z) * 120.0, asin(clamp(up, -1.0, 1.0)) * 120.0);
            vec2 id = floor(cell);
            vec2 f = fract(cell) - 0.5;
            float h = fract(sin(dot(id, vec2(127.1, 311.7))) * 43758.5453);
            float star = h > 0.978 ? smoothstep(0.18, 0.0, length(f)) * (0.5 + 0.5 * fract(h * 97.0)) : 0.0;
            star *= 0.75 + 0.25 * sin(hdTime.x * (2.0 + h * 5.0) + h * 40.0);
            // down to the horizon: the game's camera only ever sees a band of sky just above it
            colour += vec3(star) * night * smoothstep(-0.3, -0.2, up);
        }
        // the moon: a soft disc with a little glow
        float m = dot(d, normalize(hdMoon.xyz));
        colour += vec3(0.85, 0.88, 0.95) * (smoothstep(0.99985, 0.99992, m) + 0.15 * pow(max(m, 0.0), 400.0)) * night;
    }
    fragColor = vec4(uSrgbOut != 0 ? colour : hdColour(vec4(colour, 1.0)).rgb, 1.0);
}
