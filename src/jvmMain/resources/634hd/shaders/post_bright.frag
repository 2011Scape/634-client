#version 330 core
// Not part of the original client: 634HD. Bloom's first pass: the parts of the view brighter than a
// threshold, at half size (a 4-tap average, soft knee).

in vec2 vUv;
uniform sampler2D uSource;
uniform vec2 uTexel;
uniform float uThreshold;
uniform bool uLinear;
out vec4 fragColor;

vec3 linear(vec3 c) {
    return uLinear ? c : pow(max(c, vec3(0.0)), vec3(2.2));
}

void main() {
    vec3 c = linear(texture(uSource, vUv + uTexel * vec2(-0.5, -0.5)).rgb)
           + linear(texture(uSource, vUv + uTexel * vec2(0.5, -0.5)).rgb)
           + linear(texture(uSource, vUv + uTexel * vec2(-0.5, 0.5)).rgb)
           + linear(texture(uSource, vUv + uTexel * vec2(0.5, 0.5)).rgb);
    c *= 0.25;
    float brightness = max(c.r, max(c.g, c.b));
    float knee = uThreshold * 0.5;
    float soft = clamp(brightness - uThreshold + knee, 0.0, 2.0 * knee);
    soft = soft * soft / (4.0 * knee + 0.0001);
    float contribution = max(soft, brightness - uThreshold) / max(brightness, 0.0001);
    fragColor = vec4(c * contribution, 1.0);
}
