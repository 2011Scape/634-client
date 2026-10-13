#version 330 core
// Not part of the original client: 634HD. Bloom downsample: a 13-tap filter to half size.

in vec2 vUv;
uniform sampler2D uSource;
uniform vec2 uTexel;
out vec4 fragColor;

void main() {
    vec3 a = texture(uSource, vUv + uTexel * vec2(-2.0, -2.0)).rgb;
    vec3 b = texture(uSource, vUv + uTexel * vec2(0.0, -2.0)).rgb;
    vec3 c = texture(uSource, vUv + uTexel * vec2(2.0, -2.0)).rgb;
    vec3 d = texture(uSource, vUv + uTexel * vec2(-2.0, 0.0)).rgb;
    vec3 e = texture(uSource, vUv).rgb;
    vec3 f = texture(uSource, vUv + uTexel * vec2(2.0, 0.0)).rgb;
    vec3 g = texture(uSource, vUv + uTexel * vec2(-2.0, 2.0)).rgb;
    vec3 h = texture(uSource, vUv + uTexel * vec2(0.0, 2.0)).rgb;
    vec3 i = texture(uSource, vUv + uTexel * vec2(2.0, 2.0)).rgb;
    vec3 j = texture(uSource, vUv + uTexel * vec2(-1.0, -1.0)).rgb;
    vec3 k = texture(uSource, vUv + uTexel * vec2(1.0, -1.0)).rgb;
    vec3 l = texture(uSource, vUv + uTexel * vec2(-1.0, 1.0)).rgb;
    vec3 m = texture(uSource, vUv + uTexel * vec2(1.0, 1.0)).rgb;
    vec3 colour = e * 0.125 + (a + c + g + i) * 0.03125 + (b + d + f + h) * 0.0625 + (j + k + l + m) * 0.125;
    fragColor = vec4(colour, 1.0);
}
