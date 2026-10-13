#version 330 core
// Not part of the original client: 634HD. Bloom upsample: a 9-tap tent filter, added to the level
// above (additive blending).

in vec2 vUv;
uniform sampler2D uSource;
uniform vec2 uTexel;
uniform float uRadius;
out vec4 fragColor;

void main() {
    vec2 d = uTexel * uRadius;
    vec3 colour = texture(uSource, vUv + vec2(-d.x, -d.y)).rgb
                + texture(uSource, vUv + vec2(0.0, -d.y)).rgb * 2.0
                + texture(uSource, vUv + vec2(d.x, -d.y)).rgb
                + texture(uSource, vUv + vec2(-d.x, 0.0)).rgb * 2.0
                + texture(uSource, vUv).rgb * 4.0
                + texture(uSource, vUv + vec2(d.x, 0.0)).rgb * 2.0
                + texture(uSource, vUv + vec2(-d.x, d.y)).rgb
                + texture(uSource, vUv + vec2(0.0, d.y)).rgb * 2.0
                + texture(uSource, vUv + vec2(d.x, d.y)).rgb;
    fragColor = vec4(colour / 16.0, 1.0);
}
