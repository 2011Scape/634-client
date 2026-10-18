#version 330 core
// Not part of the original client: 634HD (toolkit 6). A model drawn the emulated way (players, NPCs,
// animated locs) into the sun's shadow map (HD_SHADOWS), replayed from its recorded draw with
// light-space matrix = eye-to-light times its model-view. Its colour's alpha and texture coordinate
// go on to shadow_dynamic.frag, which leaves out the parts that would not be drawn solid.

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTexCoord;

uniform mat4 uMatrix;

out float vAlpha;
out vec2 vTexCoord;

void main() {
    gl_Position = uMatrix * vec4(aPosition, 1.0);
    vAlpha = aColor.a;
    vTexCoord = aTexCoord;
}
