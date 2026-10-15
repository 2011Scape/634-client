#version 330 core

// Not part of the original client: 634HD. A retained object drawn into the water's reflection
// (HdReflections, HdSettings.reflections): placed by its captured world matrix, mirrored about the
// water plane in world space and projected (uWorldToClip), lit as scene.vert lights it (the sun and
// the anti-sun in eye space, no point lights), fogged by its distance from the mirrored eye, and
// clipped to what lies above the water.

#include "hd_lib.glsl"

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTexCoord;
layout(location = 3) in vec3 aNormal;
layout(location = 4) in uint aMaterial;
layout(location = 5) in uint aObject;

uniform samplerBuffer uModels;
uniform samplerBuffer uMaterials;
// world to clip through the mirror; world to the eye (lighting); world to the mirrored eye (fog)
uniform mat4 uWorldToClip;
uniform mat4 uView;
uniform mat4 uMirroredView;
// the water plane's height (world y, which grows downwards)
uniform float uPlane;
uniform vec4 uGlobalAmbient;
uniform vec4 uDirLight[2];
uniform vec4 uDirDiffuse[2];

const int FLAG_UNLIT = 32;

out vec4 vColor;
out vec2 vTexCoord;
// the eye-space position as the eye sees the object, and as the mirrored eye sees it (the fog)
out vec3 vViewPos;
out vec3 vMirroredViewPos;
flat out int vMaterial;

void main() {
    int base = int(aObject) * 4;
    mat4 model = mat4(texelFetch(uModels, base), texelFetch(uModels, base + 1), texelFetch(uModels, base + 2), texelFetch(uModels, base + 3));
    vec4 world = model * vec4(aPosition, 1.0);
    gl_Position = uWorldToClip * world;
    gl_ClipDistance[0] = uPlane - world.y;
    vMaterial = int(aMaterial);
    vec4 material = texelFetch(uMaterials, vMaterial * 2);
    vec4 scroll = texelFetch(uMaterials, vMaterial * 2 + 1);
    vTexCoord = aTexCoord + scroll.xy;
    bool unlit = (int(material.w) & FLAG_UNLIT) != 0;
    if (unlit) {
        vColor = aColor;
    } else {
        vec3 n = normalize(mat3(uView * model) * aNormal);
        vec4 lit = hdEnvAmbientLight(uGlobalAmbient);
        for (int i = 0; i < 2; i++) {
            if (uDirLight[i].w > 0.5) {
                vec3 lightDir = normalize(hdLightDirection(uDirLight[i].xyz, i));
                lit += hdEnvDiffuse(uDirDiffuse[i], i) * max(dot(n, lightDir), 0.0);
            }
        }
        vColor = vec4(clamp(aColor.rgb * lit.rgb, 0.0, 1.0), aColor.a);
    }
    vViewPos = (uView * world).xyz;
    vMirroredViewPos = (uMirroredView * world).xyz;
}
