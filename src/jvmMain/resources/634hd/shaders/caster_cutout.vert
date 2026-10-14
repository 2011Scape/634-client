#version 330 core

// Not part of the original client: 634HD. A retained object drawn into the sun's static shadow map
// with its texture (HdSettings.cutoutShadows): ao_bake.vert, plus what caster_cutout.frag needs to
// leave out the transparent parts of leaves, fences and grilles.

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTexCoord;
layout(location = 4) in uint aMaterial;
layout(location = 5) in uint aObject;

uniform samplerBuffer uModels;
uniform mat4 uWorldToClip;

out vec2 vTexCoord;
out float vAlpha;
flat out int vMaterial;

void main() {
    int base = int(aObject) * 4;
    mat4 model = mat4(texelFetch(uModels, base), texelFetch(uModels, base + 1), texelFetch(uModels, base + 2), texelFetch(uModels, base + 3));
    gl_Position = uWorldToClip * (model * vec4(aPosition, 1.0));
    vTexCoord = aTexCoord;
    vAlpha = aColor.a;
    vMaterial = int(aMaterial);
}
