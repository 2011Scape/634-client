#version 330 core
// Not part of the original client: 634HD (toolkit 6). What every 634HD scene shader shares: the
// per-frame uniform block (HdFrameUniforms.kt writes it, std140) and the feature functions. A feature
// is on when its bit is set in hdFlags.x and the draw is part of the 3D view (uHdScene); with every
// bit clear the shaders compute exactly what the game draws without 634HD's features.

#define HD_LINEAR 1
#define HD_PIXEL_LIGHTING 2
#define HD_SHADOWS 4
#define HD_LIGHTS 8
#define HD_FOG 16
#define HD_FADE 32
#define HD_SKY 64
#define HD_AO 128
#define HD_TERRAIN 256
#define HD_WATER 512
#define HD_FOLIAGE 1024
#define HD_SOFT_PARTICLES 2048
#define HD_MAX_LIGHTS 64

layout(std140) uniform HdFrame {
    // x: feature bits, y: tonemap (0 off, 1 ACES, 2 AgX), z: shadow map size, w: frame number
    ivec4 hdFlags;
    // x: seconds, y: wind strength, z: fade band start (eye distance), w: fade band length
    vec4 hdTime;
    // the sun: eye-space direction towards it (xyz), w: 1 when there is one
    vec4 hdSunDirection;
    vec4 hdSunColour;
    vec4 hdAmbientColour;
    vec4 hdFogColour;
    // x: fog start, y: fog end (eye distance), z: height fog density, w: height fog top (world y)
    vec4 hdFogParams;
    vec4 hdSkyTop;
    vec4 hdSkyHorizon;
    // eye space to the shadow map's texture space (with depth in z)
    mat4 hdShadowMatrix;
    // eye space to world space
    mat4 hdViewToWorld;
    // x, y, width, height of the 3D view in the target; z, w: 1 / width, 1 / height
    vec4 hdViewport;
    // x: point lights this frame
    ivec4 hdLightInfo;
    // eye-space position, w: radius
    vec4 hdLightPosition[HD_MAX_LIGHTS];
    // colour times intensity, w: unused
    vec4 hdLightColour[HD_MAX_LIGHTS];
};

// 1 for a draw that belongs to the 3D view, 0 for the interface and everything else.
uniform int uHdScene;

bool hdOn(int feature) {
    return uHdScene != 0 && (hdFlags.x & feature) != 0;
}

vec3 hdToLinear(vec3 c) {
    return pow(max(c, vec3(0.0)), vec3(2.2));
}

vec3 hdToSrgb(vec3 c) {
    return pow(max(c, vec3(0.0)), vec3(1.0 / 2.2));
}

// A colour as the game gives it (sRGB), in the space the scene is lit and blended in.
vec4 hdColour(vec4 c) {
    return hdOn(HD_LINEAR) ? vec4(hdToLinear(c.rgb), c.a) : c;
}

// Sun and ambient light on a surface with eye-space [normal] (not normalised: the game's normals
// carry their own scale, as fixed-function lighting used them), for per-pixel lighting.
vec3 hdSunLight(vec3 normal) {
    vec3 light = hdAmbientColour.rgb;
    if (hdSunDirection.w > 0.5) {
        light += hdSunColour.rgb * max(dot(normal, hdSunDirection.xyz), 0.0);
    }
    return light;
}
