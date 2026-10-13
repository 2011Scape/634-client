#version 330 core
// Not part of the original client: 634HD (toolkit 6). FXAA (after Timothy Lottes's FXAA 3.11, the
// console version's edge search reduced to a fixed blur along the edge): an edge is found from the
// luma of the 3x3 neighbourhood and the pixel blended along it.

in vec2 vUv;
uniform sampler2D uSource;
uniform vec2 uTexel;
out vec4 fragColor;

float luma(vec3 c) {
    return dot(c, vec3(0.299, 0.587, 0.114));
}

void main() {
    vec3 rgbM = texture(uSource, vUv).rgb;
    float lumaM = luma(rgbM);
    float lumaNW = luma(texture(uSource, vUv + vec2(-1.0, -1.0) * uTexel).rgb);
    float lumaNE = luma(texture(uSource, vUv + vec2(1.0, -1.0) * uTexel).rgb);
    float lumaSW = luma(texture(uSource, vUv + vec2(-1.0, 1.0) * uTexel).rgb);
    float lumaSE = luma(texture(uSource, vUv + vec2(1.0, 1.0) * uTexel).rgb);
    float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
    float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));
    if (lumaMax - lumaMin < max(0.0312, lumaMax * 0.125)) {
        fragColor = vec4(rgbM, 1.0);
        return;
    }
    vec2 dir = vec2(-((lumaNW + lumaNE) - (lumaSW + lumaSE)), (lumaNW + lumaSW) - (lumaNE + lumaSE));
    float reduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * 0.25 * 0.125, 1.0 / 128.0);
    float scale = 1.0 / (min(abs(dir.x), abs(dir.y)) + reduce);
    dir = clamp(dir * scale, vec2(-8.0), vec2(8.0)) * uTexel;
    vec3 rgbA = 0.5 * (texture(uSource, vUv + dir * (1.0 / 3.0 - 0.5)).rgb + texture(uSource, vUv + dir * (2.0 / 3.0 - 0.5)).rgb);
    vec3 rgbB = rgbA * 0.5 + 0.25 * (texture(uSource, vUv - dir * 0.5).rgb + texture(uSource, vUv + dir * 0.5).rgb);
    float lumaB = luma(rgbB);
    fragColor = vec4((lumaB < lumaMin || lumaB > lumaMax) ? rgbA : rgbB, 1.0);
}
