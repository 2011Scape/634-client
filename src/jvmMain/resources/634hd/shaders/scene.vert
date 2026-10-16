#version 330 core
// Not part of the original client: 634HD (toolkit 6). The retained scene: static geometry stays on
// the GPU and draws in a few calls, but every vertex comes out as the fixed-function pipeline
// (ff.vert) would have lit and placed it for its object this frame. The per-object matrices and
// lights are copied from the emulator's own state as the client draws each object (HdSceneRenderer).

#include "hd_lib.glsl"

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTexCoord;
layout(location = 3) in vec3 aNormal;
layout(location = 4) in uint aMaterial;
layout(location = 5) in uint aObject;
// ambient occlusion baked into the vertex (HdSceneRenderer.bakeVertexOcclusion), 1 for open sky
layout(location = 6) in float aOcclusion;

// Per retained object: its slot in uObjects this frame, or -1 when the client did not draw it.
uniform isamplerBuffer uObjectSlots;
// Per drawn object, OBJECT_TEXELS texels: model-view-projection (4), model-view (4),
// (lit, point light count, 0, 0), then per point light (eye position, quadratic attenuation)
// and (diffuse colour, constant attenuation); the linear term is always 0 (HdToolkit).
uniform samplerBuffer uObjects;
// Per material slot, 2 texels: (layer, array, combine op, flags) and (u offset, v offset, size, 0).
uniform samplerBuffer uMaterials;

uniform vec4 uGlobalAmbient;
// The sun (light 0) and anti-sun (light 1): eye-space direction, w = enabled.
uniform vec4 uDirLight[2];
uniform vec4 uDirDiffuse[2];

const int OBJECT_TEXELS = 17;
const int FLAG_UNLIT = 32;

out vec4 vColor;
out vec2 vTexCoord;
flat out int vMaterial;
// 634HD per-pixel lighting (HD_PIXEL_LIGHTING): unlit colour, eye-space normal and position, the
// object's slot (for its point lights) and whether it is lit at all
out vec4 vBase;
out vec3 vEyeNormal;
out vec3 vViewPos;
flat out int vSlot;
flat out int vLit;
out float vOcclusion;

// As in ff.vert: the same expression for the position keeps retained and emulated draws of the
// same surface on identical depths.
invariant gl_Position;

void main() {
    int slot = texelFetch(uObjectSlots, int(aObject)).r;
    vOcclusion = aOcclusion;
    vMaterial = int(aMaterial);
    if (slot < 0) {
        gl_Position = vec4(2.0, 2.0, 2.0, 1.0);
        vColor = vec4(0.0);
        vTexCoord = vec2(0.0);
        vBase = vec4(0.0);
        vEyeNormal = vec3(0.0);
        vViewPos = vec3(0.0);
        vSlot = 0;
        vLit = 0;
        return;
    }
    int base = slot * OBJECT_TEXELS;
    mat4 mvp = mat4(texelFetch(uObjects, base), texelFetch(uObjects, base + 1), texelFetch(uObjects, base + 2), texelFetch(uObjects, base + 3));
    mat4 mv = mat4(texelFetch(uObjects, base + 4), texelFetch(uObjects, base + 5), texelFetch(uObjects, base + 6), texelFetch(uObjects, base + 7));
    vec4 flags = texelFetch(uObjects, base + 8);
    vec4 position = vec4(aPosition, 1.0);
    if (hdOn(HD_FOLIAGE) && flags.z > 0.0) {
        // wind sway: the higher above the object's base (the game's y grows downwards), the further;
        // a gust travels across the world, so neighbouring plants move together but not in step
        float height = max(-aPosition.y, 0.0);
        vec3 origin = (hdViewToWorld * vec4(mv[3].xyz, 1.0)).xyz;
        float phase = dot(origin.xz, vec2(0.0011, 0.0007));
        float t = hdTime.x * flags.w * 1.6 + phase;
        float sway = sin(t) * 0.7 + sin(t * 2.3 + 1.7) * 0.3;
        position.xz += vec2(0.8, 0.45) * sway * height * 0.08 * flags.z * hdTime.y;
    }
    if (hdOn(HD_GRASS) && flags.z >= 0.7 && hdActorInfo.x > 0.0) {
        // grass and plants (wind strength 0.7 and up) bend away from characters walking through:
        // pushed in world space, more the higher the vertex and the nearer the character
        float height = max(-aPosition.y, 0.0);
        vec3 world = (hdViewToWorld * (mv * position)).xyz;
        vec2 push = vec2(0.0);
        for (int i = 0; i < HD_MAX_ACTORS; i++) {
            if (float(i) >= hdActorInfo.x) break;
            vec2 away = world.xz - hdActors[i].xy;
            float distance = length(away);
            if (distance < 220.0 && distance > 1.0) {
                float near = 1.0 - distance / 220.0;
                push += away / distance * near * near * min(height, 260.0) * 0.55;
            }
        }
        if (push.x != 0.0 || push.y != 0.0) {
            // world offset to the model's own space (the eye's rotation, then the model's)
            vec3 eyeOffset = mat3(inverse(hdViewToWorld)) * vec3(push.x, 0.0, push.y);
            position.xyz += inverse(mat3(mv)) * eyeOffset;
        }
    }
    gl_Position = mvp * position;

    vec4 material = texelFetch(uMaterials, vMaterial * 2);
    vec4 scroll = texelFetch(uMaterials, vMaterial * 2 + 1);
    vTexCoord = aTexCoord + scroll.xy;

    bool unlit = (int(material.w) & FLAG_UNLIT) != 0;
    vec4 eyePos = mv * position;
    vBase = hdColour(aColor);
    vEyeNormal = mat3(mv) * aNormal;
    vViewPos = eyePos.xyz;
    vSlot = slot;
    vLit = (flags.x > 0.5 && !unlit) ? 1 : 0;
    if (flags.x > 0.5 && !unlit) {
        vec4 viewPos = eyePos;
        vec3 rawEyeNormal = vEyeNormal;
        vec4 lit = hdEnvAmbientLight(uGlobalAmbient);
        for (int i = 0; i < 2; i++) {
            if (uDirLight[i].w > 0.5) {
                vec3 lightDir = normalize(hdLightDirection(uDirLight[i].xyz, i));
                float diff = max(dot(rawEyeNormal, lightDir), 0.0);
                lit += vec4(0.0, 0.0, 0.0, 1.0) + hdEnvDiffuse(uDirDiffuse[i], i) * diff;
            }
        }
        int count = int(flags.y);
        for (int i = 0; i < count; i++) {
            vec4 light = texelFetch(uObjects, base + 9 + i * 2);
            vec4 diffuse = texelFetch(uObjects, base + 10 + i * 2);
            vec3 toLight = light.xyz - viewPos.xyz;
            float dist = length(toLight);
            vec3 lightDir = toLight / max(dist, 0.0001);
            float atten = 1.0 / max(diffuse.w + light.w * dist * dist, 0.0001);
            float diff = max(dot(rawEyeNormal, lightDir), 0.0);
            lit += (vec4(0.0, 0.0, 0.0, 1.0) + vec4(diffuse.rgb, 1.0) * diff) * atten;
        }
        vec4 base = hdColour(aColor);
        // with tonemapping the light multiplies the sRGB colour, then made linear, no higher than
        // HD_LIT_MAX (hd_lib.glsl hdLitColour, hdApplyLight)
        vColor = hdOn(HD_LINEAR) ? vec4(hdLitColour(aColor.rgb, lit.rgb), base.a) : vec4(clamp(aColor.rgb * lit.rgb, 0.0, 1.0), aColor.a);
    } else {
        vColor = hdColour(aColor);
    }
}
