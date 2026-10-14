#version 330 core
// Not part of the original client: 634HD (toolkit 6). GPU particles (HD_PARTICLES): embers over fires
// and torches, sparks over lava, at the lights of lights.json (HdGpuParticles). Nothing is simulated
// on the CPU: each particle's place in its life comes from the time and its seed, here.

#include "hd_lib.glsl"

// per emitter (divisor = particles per emitter): world position, kind (1 fire, 2 lava); strength, and
// the flame's width and height in the loc's model (0: unknown, the old defaults)
layout(location = 0) in vec4 aEmitter;
layout(location = 1) in vec3 aSource;

uniform mat4 uView;
uniform mat4 uProjection;
uniform int uPerEmitter;

out vec2 vCorner;
out vec4 vColour;

float hash(float n) {
    return fract(sin(n * 12.9898 + 78.233) * 43758.5453);
}

void main() {
    int index = gl_InstanceID % uPerEmitter;
    float seed = float(index) * 7.13 + aEmitter.x * 0.0131 + aEmitter.z * 0.0071;
    bool lava = aEmitter.w > 1.5;
    float aStrength = aSource.x;
    // the flame's size: a candle's (about 60 units tall) throws few, small, low embers; a campfire's
    // (about 250) many; unknown flames keep the old look (1)
    float scale = aSource.z > 0.0 ? clamp(aSource.z / 220.0, 0.2, 1.4) : 1.0;
    if (!lava && float(index) >= float(uPerEmitter) * min(1.0, scale * 1.2 + 0.1)) {
        gl_Position = vec4(2.0, 2.0, 2.0, 1.0);
        vCorner = vec2(0.0);
        vColour = vec4(0.0);
        return;
    }
    float life = lava ? 3.2 : (1.4 + hash(seed + 3.0) * 0.8) * mix(0.6, 1.0, min(scale, 1.0));
    float age = fract(hdTime.x / life + hash(seed));
    // the game's y grows downwards: particles rise towards -y, drift with the wind and wander
    float rise = lava ? 260.0 : 420.0 * mix(0.35, 1.0, min(scale, 1.2));
    // they start across the flame's width
    float spread = lava ? 220.0 : (aSource.y > 0.0 ? clamp(aSource.y * 0.7, 6.0, 260.0) : 34.0);
    vec3 start = vec3((hash(seed + 1.0) - 0.5) * spread, 0.0, (hash(seed + 2.0) - 0.5) * spread);
    vec3 position = aEmitter.xyz + start;
    position.y -= age * rise * (0.7 + hash(seed + 4.0) * 0.6);
    float wander = lava ? 14.0 : 14.0 * mix(0.4, 1.0, min(scale, 1.0));
    position.x += sin(hdTime.x * 2.1 + seed * 5.0) * wander * age + age * age * 40.0 * hdTime.y;
    position.z += cos(hdTime.x * 1.7 + seed * 3.0) * wander * age;
    float size = (lava ? 44.0 : 20.0 * mix(0.45, 1.0, min(scale, 1.0))) * (1.0 - age * 0.6) * (0.6 + hash(seed + 5.0) * 0.8);
    vec2 corner = vec2(float(gl_VertexID & 1), float(gl_VertexID >> 1)) * 2.0 - 1.0;
    vec4 eye = uView * vec4(position, 1.0);
    eye.xy += corner * size;
    gl_Position = uProjection * eye;
    vCorner = corner;
    // hot yellow to red, fading in quickly and out slowly; dimmer when the emitter's light is weak
    vec3 hot = lava ? vec3(1.0, 0.55, 0.15) : vec3(1.0, 0.75, 0.3);
    vec3 cool = vec3(0.9, 0.18, 0.04);
    float fade = smoothstep(0.0, 0.08, age) * (1.0 - smoothstep(0.55, 1.0, age));
    vColour = vec4(mix(hot, cool, age), fade * aStrength);
}
