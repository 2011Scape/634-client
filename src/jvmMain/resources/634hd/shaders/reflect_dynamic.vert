#version 330 core

// Not part of the original client: 634HD. A model drawn the emulated way (players, NPCs, animated
// locs) drawn again into the water's reflection (HdReflections, HdShadows.drawReflected): from its
// recorded draw, placed by its model-to-world matrix, mirrored and projected (uMatrix), lit as the
// fixed-function pipeline lit it (the sun and the anti-sun in eye space, uModelView), fogged by its
// distance from the mirrored eye, and clipped to what lies above the water, less a band of
// HD_REFLECT_SURFACE_BAND over it: a decal lying on the water (a fishing spot's NPC carries a
// quad a few units over its tile, facing down, that the eye never sees) was mirrored in place
// as a dark square. uPlane is the model's own origin where that is higher than the mean plane
// (HdShadows.drawReflected): the band is taken from the ground the model stands on.

#include "hd_lib.glsl"

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTexCoord;
layout(location = 3) in vec3 aNormal;

uniform mat4 uMatrix;
uniform mat4 uModelView;
uniform mat4 uModelWorld;
uniform mat4 uMirroredView;
uniform float uPlane;
uniform int uLit;
uniform vec4 uGlobalAmbient;
uniform vec4 uDirLight[2];
uniform vec4 uDirDiffuse[2];

out vec4 vColor;
out vec2 vTexCoord;
// the eye-space position as the eye sees the object, and as the mirrored eye sees it (the fog)
out vec3 vViewPos;
out vec3 vMirroredViewPos;

void main() {
    vec4 position = vec4(aPosition, 1.0);
    gl_Position = uMatrix * position;
    vec4 world = uModelWorld * position;
    gl_ClipDistance[0] = uPlane - HD_REFLECT_SURFACE_BAND - world.y;
    vTexCoord = aTexCoord;
    if (uLit != 0) {
        vec3 n = normalize(mat3(uModelView) * aNormal);
        vec4 lit = hdEnvAmbientLight(uGlobalAmbient);
        for (int i = 0; i < 2; i++) {
            if (uDirLight[i].w > 0.5) {
                vec3 lightDir = normalize(hdLightDirection(uDirLight[i].xyz, i));
                lit += hdEnvDiffuse(uDirDiffuse[i], i) * max(dot(n, lightDir), 0.0);
            }
        }
        vColor = vec4(clamp(aColor.rgb * lit.rgb, 0.0, 1.0), aColor.a);
    } else {
        vColor = aColor;
    }
    vViewPos = (uModelView * position).xyz;
    vMirroredViewPos = (uMirroredView * world).xyz;
}
