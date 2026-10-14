#version 330 core
// Not part of the original client: 634HD (toolkit 6). The fixed-function pipeline as GLSL, ported
// from the browser's jaggl emulator (src/jsMain/kotlin/jaggl/FixedFunctionShader.js.kt).
#define MAX_LIGHTS 8
#include "hd_lib.glsl"
layout(location = 0) in vec4 aPosition;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTexCoord0;
layout(location = 3) in vec3 aNormal;
layout(location = 4) in vec3 aTexCoord1;

uniform mat4 uModelView;
// projection * modelview, multiplied once on the CPU (MatrixStack.mvp) - see main().
uniform mat4 uModelViewProjection;
uniform mat4 uTextureMatrixU[3];
// Per unit, per coordinate (s, t, r, q) GL_TEXTURE_GEN_MODE, or 0 where that coordinate's
// texgen is disabled and the vertex attribute passes through.
uniform highp ivec4 uTexGenMode[3];
uniform vec4 uTexGenObjPlane[12];
uniform vec4 uTexGenEyePlane[12];
uniform bool uLightingEnabled;
uniform vec4 uGlobalAmbient;
uniform bool uLightEnabled[MAX_LIGHTS];
uniform vec4 uLightAmbient[MAX_LIGHTS];
uniform vec4 uLightDiffuse[MAX_LIGHTS];
// xyz = eye-space position (or direction when w == 0), w == 1 means positional with attenuation.
uniform vec4 uLightPosition[MAX_LIGHTS];
uniform vec3 uLightAttenuation[MAX_LIGHTS];

out vec4 vColor;
// 634HD per-pixel lighting (HD_PIXEL_LIGHTING): the unlit colour, the eye-space normal and position
out vec4 vBase;
out vec3 vEyeNormal;
out vec3 vViewPos;
out vec3 vTexCoord0;
out vec3 vTexCoord1;
out vec3 vTexCoord2;

// Transpiled ARB_position_invariant vertex programs (water, ground shader, fog pass) must
// reproduce this exact transform bit-for-bit so their geometry doesn't z-fight against
// fixed-function-rendered geometry at the same world position - see the matching invariant
// declaration and comment in ArbVertexProgramTranspiler.buildGlsl().
invariant gl_Position;

void main() {
    // gl_Position deliberately shares no subexpression with viewPos: deriving it from viewPos
    // lets the compiler lower the modelview product differently here than in a transpiled ARB
    // program (which computes its own view position with DP4s), and the rounding difference
    // makes coplanar multi-pass terrain (e.g. SD water over a shore texture) z-fight.
    gl_Position = uModelViewProjection * aPosition;
    vec4 viewPos = uModelView * aPosition;

    vec3 rawEyeNormal = mat3(uModelView) * aNormal;
    vBase = hdColour(aColor);
    vEyeNormal = rawEyeNormal;
    vViewPos = viewPos.xyz;
    float eyeNormalLenSq = dot(rawEyeNormal, rawEyeNormal);
    vec3 eyeNormal = eyeNormalLenSq > 0.0 ? rawEyeNormal * inversesqrt(eyeNormalLenSq) : vec3(0.0);

    if (uLightingEnabled) {
        vec4 lit = hdEnvAmbientLight(uGlobalAmbient);
        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (uLightEnabled[i]) {
                vec3 lightDir;
                float atten = 1.0;
                if (uLightPosition[i].w == 0.0) {
                    lightDir = normalize(uLightPosition[i].xyz);
                } else {
                    vec3 toLight = uLightPosition[i].xyz - viewPos.xyz;
                    float dist = length(toLight);
                    lightDir = toLight / max(dist, 0.0001);
                    vec3 k = uLightAttenuation[i];
                    atten = 1.0 / max(k.x + k.y * dist + k.z * dist * dist, 0.0001);
                }
                float diff = max(dot(rawEyeNormal, lightDir), 0.0);
                lit += (uLightAmbient[i] + hdEnvDiffuse(uLightDiffuse[i], i) * diff) * atten;
            }
        }
        // 634HD: in linear (HDR) colour the lit colour is not clamped; the tonemapper takes it down
        vec4 base = hdColour(aColor);
        vColor = hdOn(HD_LINEAR) ? vec4(base.rgb * lit.rgb, base.a) : vec4(clamp(aColor.rgb * lit.rgb, 0.0, 1.0), aColor.a);
    } else {
        vColor = hdColour(aColor);
    }

    vec3 reflected = reflect(normalize(viewPos.xyz), eyeNormal);
    float sphereM = 2.0 * sqrt(reflected.x * reflected.x + reflected.y * reflected.y +
                               (reflected.z + 1.0) * (reflected.z + 1.0));
    vec4 sphere = vec4(reflected.x / sphereM + 0.5, reflected.y / sphereM + 0.5, 0.0, 1.0);
    for (int i = 0; i < 3; i++) {
        vec4 gen = i == 0 ? vec4(aTexCoord0, 0.0, 1.0) : vec4(aTexCoord1, 1.0);
        ivec4 mode = uTexGenMode[i];
        for (int c = 0; c < 4; c++) {
            int m = mode[c];
            if (m == 0) continue;
            float v;
            if (m == 9217) v = dot(uTexGenObjPlane[i * 4 + c], aPosition);      // GL_OBJECT_LINEAR
            else if (m == 9216) v = dot(uTexGenEyePlane[i * 4 + c], viewPos);   // GL_EYE_LINEAR
            else if (m == 34065) v = c < 3 ? eyeNormal[c] : 1.0;                // GL_NORMAL_MAP
            else if (m == 34066) v = c < 3 ? reflected[c] : 1.0;                // GL_REFLECTION_MAP
            else v = sphere[c];                                                 // GL_SPHERE_MAP
            gen[c] = v;
        }
        vec3 coord = (uTextureMatrixU[i] * gen).xyz;
        if (i == 0) vTexCoord0 = coord; else if (i == 1) vTexCoord1 = coord; else vTexCoord2 = coord;
    }
}
