#version 330 core
// Not part of the original client: 634HD (toolkit 6). The ambient occlusion bake (HdShadows): every
// retained object of the region, placed by the model matrix it was captured with (uModels, 4 texels
// an object), into the region's occlusion height map seen from straight above.

layout(location = 0) in vec3 aPosition;
layout(location = 5) in uint aObject;

uniform samplerBuffer uModels;
uniform mat4 uWorldToClip;

void main() {
    int base = int(aObject) * 4;
    mat4 model = mat4(texelFetch(uModels, base), texelFetch(uModels, base + 1), texelFetch(uModels, base + 2), texelFetch(uModels, base + 3));
    gl_Position = uWorldToClip * (model * vec4(aPosition, 1.0));
}
