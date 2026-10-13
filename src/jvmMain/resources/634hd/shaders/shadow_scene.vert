#version 330 core
// Not part of the original client: 634HD (toolkit 6). The retained scene into the sun's shadow map
// (HD_SHADOWS): scene.vert's object table, with the eye-to-light matrix after each object's
// model-view. Objects the client did not draw this frame are left out.

layout(location = 0) in vec3 aPosition;
layout(location = 5) in uint aObject;

uniform isamplerBuffer uObjectSlots;
uniform samplerBuffer uObjects;
uniform mat4 uViewToLight;

const int OBJECT_TEXELS = 17;

void main() {
    int slot = texelFetch(uObjectSlots, int(aObject)).r;
    if (slot < 0) {
        gl_Position = vec4(2.0, 2.0, 2.0, 1.0);
        return;
    }
    int base = slot * OBJECT_TEXELS;
    mat4 mv = mat4(texelFetch(uObjects, base + 4), texelFetch(uObjects, base + 5), texelFetch(uObjects, base + 6), texelFetch(uObjects, base + 7));
    gl_Position = uViewToLight * (mv * vec4(aPosition, 1.0));
}
