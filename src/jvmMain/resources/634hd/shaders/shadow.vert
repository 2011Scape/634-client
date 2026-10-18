#version 330 core
// Not part of the original client: 634HD (toolkit 6). A model drawn the emulated way (players, NPCs,
// animated locs) into the sun's shadow map (HD_SHADOWS), replayed from its recorded draw with
// light-space matrix = eye-to-light times its model-view. Its colour's alpha, texture coordinate and
// the way its normal points go on to shadow_dynamic.frag, which leaves out what the eye never sees.

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTexCoord;
layout(location = 3) in vec3 aNormal;

uniform mat4 uMatrix;
uniform mat4 uModelWorld;

out float vAlpha;
out vec2 vTexCoord;
// the normal's world y (down is positive): a face that looks at the ground is one the eye never sees
out float vNormalDown;

void main() {
    gl_Position = uMatrix * vec4(aPosition, 1.0);
    vAlpha = aColor.a;
    vTexCoord = aTexCoord;
    vec3 n = mat3(uModelWorld) * aNormal;
    float len = length(n);
    vNormalDown = len > 0.0 ? n.y / len : 0.0;
}
