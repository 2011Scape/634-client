#version 330 core
// Not part of the original client: 634HD (toolkit 6). The depth of an emulated draw's caster
// (shadow.vert): a fragment less than half opaque, by its face's alpha and its texture's, casts no
// shadow. An impling's glow and a fishing spot's ripple are blended quads: solid, they cast a square.

in float vAlpha;
in vec2 vTexCoord;

uniform int uTextured;
uniform sampler2D uTexture;

void main() {
    float alpha = vAlpha;
    if (uTextured != 0) {
        alpha *= texture(uTexture, vTexCoord).a;
    }
    if (alpha < 0.5) {
        discard;
    }
}
