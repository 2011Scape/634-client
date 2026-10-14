#version 330 core
// Not part of the original client: 634HD (toolkit 6). The 3D view's last pass (HdPost): bloom added,
// tonemapped from linear colour (ACES or AgX) to sRGB, colour graded through a look-up table, and
// dithered against banding. With every switch off it copies the view unchanged.

in vec2 vUv;
uniform sampler2D uScene;
uniform sampler2D uBloom;
uniform sampler2D uLut;
// the area's previous grading table while the new one fades in (uLutMix 0 to 1)
uniform sampler2D uLutPrevious;
uniform float uLutMix;
uniform int uTonemap;
uniform bool uBloomOn;
uniform float uBloomStrength;
uniform bool uLutOn;
uniform float uLutSize;
uniform bool uDither;
uniform float uExposure;
uniform int uFrame;
out vec4 fragColor;

vec3 aces(vec3 x) {
    // Narkowicz's fit of the ACES filmic curve
    return clamp((x * (2.51 * x + 0.03)) / (x * (2.43 * x + 0.59) + 0.14), 0.0, 1.0);
}

vec3 agx(vec3 x) {
    // a minimal AgX: the inset matrix, a log2 encoding over [-12.47, 4.03] and a sigmoid fit
    const mat3 inset = mat3(0.842479, 0.0784336, 0.0792237, 0.0423303, 0.878468, 0.0791661, 0.0423756, 0.0784336, 0.879142);
    const mat3 outset = mat3(1.19688, -0.0980209, -0.0990297, -0.0528969, 1.15190, -0.0989612, -0.0529716, -0.0980435, 1.15107);
    x = inset * max(x, vec3(1e-10));
    x = clamp((log2(x) + 12.47393) / 16.5, 0.0, 1.0);
    vec3 x2 = x * x;
    vec3 x4 = x2 * x2;
    x = 15.5 * x4 * x2 - 40.14 * x4 * x + 31.96 * x4 - 6.868 * x2 * x + 0.4298 * x2 + 0.1191 * x - 0.00232;
    x = outset * x;
    // AgX's output is display-ready; the pipeline adds the sRGB curve below, so undo it here
    return pow(clamp(x, 0.0, 1.0), vec3(2.2));
}

vec3 gradeWith(sampler2D lut, vec3 c) {
    // a size^3 cube laid out as size slices side by side, blue picking the slice
    float size = uLutSize;
    float blue = c.b * (size - 1.0);
    float slice = floor(blue);
    float next = min(slice + 1.0, size - 1.0);
    vec2 uv = vec2((c.r * (size - 1.0) + 0.5) / (size * size), (c.g * (size - 1.0) + 0.5) / size);
    vec3 a = texture(lut, uv + vec2(slice / size, 0.0)).rgb;
    vec3 b = texture(lut, uv + vec2(next / size, 0.0)).rgb;
    return mix(a, b, blue - slice);
}

vec3 grade(vec3 c) {
    vec3 graded = gradeWith(uLut, c);
    return uLutMix < 1.0 ? mix(gradeWith(uLutPrevious, c), graded, uLutMix) : graded;
}

float bayer(vec2 p) {
    // 4x4 ordered dither, -0.5 to 0.5 of a step
    int x = int(mod(p.x, 4.0));
    int y = int(mod(p.y, 4.0));
    int index = x + y * 4;
    const float m[16] = float[16](0.0, 8.0, 2.0, 10.0, 12.0, 4.0, 14.0, 6.0, 3.0, 11.0, 1.0, 9.0, 15.0, 7.0, 13.0, 5.0);
    return (m[index] + 0.5) / 16.0 - 0.5;
}

void main() {
    vec4 scene = texture(uScene, vUv);
    vec3 c = scene.rgb;
    if (uBloomOn) {
        vec3 bloom = texture(uBloom, vUv).rgb * uBloomStrength;
        // without tonemapping the view is sRGB: add the bloom in linear and come back
        c = uTonemap != 0 ? c + bloom : pow(pow(max(c, vec3(0.0)), vec3(2.2)) + bloom, vec3(1.0 / 2.2));
    }
    if (uTonemap != 0) {
        c *= uExposure;
        c = uTonemap == 2 ? agx(c) : aces(c);
        if (uTonemap == 2) {
            // AgX desaturates by design; a little saturation back keeps the game's colours
            float grey = dot(c, vec3(0.2126, 0.7152, 0.0722));
            c = max(mix(vec3(grey), c, 1.12), vec3(0.0));
        }
        c = pow(c, vec3(1.0 / 2.2));
    }
    if (uLutOn) {
        c = grade(clamp(c, 0.0, 1.0));
    }
    if (uDither && uTonemap != 0) {
        c += bayer(gl_FragCoord.xy) / 255.0;
    }
    fragColor = vec4(clamp(c, 0.0, 1.0), 1.0);
}
