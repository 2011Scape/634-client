#version 330 core
// Not part of the original client: 634HD (toolkit 6). The depth of an emulated draw's caster
// (shadow.vert): what the eye would not see casts no shadow. A fragment less than half opaque, by its
// face's alpha and its texture's (an impling's blended parts); and, when the draw's normals are known,
// a face that looks at the ground: the eye's pass culls back faces and the camera is above the ground,
// so such a face never shows, but the shadow pass draws both sides (the impling's stand of four
// downward squares cast a square the size of a tile).

in float vAlpha;
in vec2 vTexCoord;
in float vNormalDown;

uniform int uTextured;
uniform int uHaveNormal;
uniform sampler2D uTexture;

void main() {
    float alpha = vAlpha;
    if (uTextured != 0) {
        alpha *= texture(uTexture, vTexCoord).a;
    }
    if (alpha < 0.5) {
        discard;
    }
    if (uHaveNormal != 0 && vNormalDown > 0.7) {
        discard;
    }
}
