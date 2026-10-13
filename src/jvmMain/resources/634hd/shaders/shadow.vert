#version 330 core
// Not part of the original client: 634HD (toolkit 6). A model drawn the emulated way (players, NPCs,
// animated locs) into the sun's shadow map (HD_SHADOWS), replayed from its recorded draw with
// light-space matrix = eye-to-light times its model-view.

layout(location = 0) in vec3 aPosition;

uniform mat4 uMatrix;

void main() {
    gl_Position = uMatrix * vec4(aPosition, 1.0);
}
