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
#define HD_MIPMAPS 4096
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

// One point light as fixed-function lighting has it (quadratic attenuation), for per-pixel lighting.
vec4 hdPointLight(vec3 normal, vec3 viewPos, vec3 lightPos, vec3 attenuation, vec3 diffuse) {
    vec3 toLight = lightPos - viewPos;
    float dist = length(toLight);
    vec3 lightDir = toLight / max(dist, 0.0001);
    float atten = 1.0 / max(attenuation.x + attenuation.y * dist + attenuation.z * dist * dist, 0.0001);
    return vec4(diffuse * max(dot(normal, lightDir), 0.0) * atten, 0.0);
}

vec4 hdApplyLight(vec4 base, vec4 lit) {
    return hdOn(HD_LINEAR) ? vec4(base.rgb * lit.rgb, base.a) : vec4(clamp(base.rgb * lit.rgb, 0.0, 1.0), base.a);
}

// The sky's colour (sRGB) in world direction [dir] (the game's axes: y points down).
vec3 hdSkyColour(vec3 dir) {
    float up = clamp(-dir.y / max(length(dir), 0.0001), 0.0, 1.0);
    return mix(hdSkyHorizon.rgb, hdSkyTop.rgb, pow(up, 0.4));
}

// Fog over a fragment at eye-space [viewPos] ([depth]: its eye depth). [fogColour], [start] and [end]
// are the draw's fixed-function fog, which is all there is unless HD_FOG (the environment's colour
// and distances, a smooth curve, height fog) or HD_SKY (the colour of the sky behind the fragment).
vec4 hdFog(vec4 color, vec3 viewPos, float depth, vec4 fogColour, float start, float end) {
    if (!hdOn(HD_FOG) && !hdOn(HD_SKY)) {
        float fogFactor = clamp((end - depth) / (end - start), 0.0, 1.0);
        return vec4(mix(hdColour(fogColour).rgb, color.rgb, fogFactor), color.a);
    }
    vec3 fogRgb = fogColour.rgb;
    float amount;
    float dist = length(viewPos);
    if (hdOn(HD_FOG)) {
        fogRgb = hdFogColour.rgb;
        amount = smoothstep(hdFogParams.x, hdFogParams.y, dist);
    } else {
        amount = 1.0 - clamp((end - depth) / (end - start), 0.0, 1.0);
    }
    if (hdOn(HD_SKY)) {
        fogRgb = hdSkyColour(mat3(hdViewToWorld) * viewPos);
        if (hdFogParams.z > 0.0) {
            // height fog: thicker the lower the fragment lies under the environment's fog top
            float worldY = (hdViewToWorld * vec4(viewPos, 1.0)).y;
            float below = clamp((worldY - hdFogParams.w) / 1536.0, 0.0, 1.0);
            amount = max(amount, hdFogParams.z * below * clamp(dist / 6144.0, 0.0, 1.0));
        }
    }
    return vec4(mix(color.rgb, hdColour(vec4(fogRgb, 1.0)).rgb, amount), color.a);
}

// Whether a fragment at eye distance [dist] is dropped by the fade at the edge of the view (HD_FADE):
// over the band hdTime.z .. hdTime.z + hdTime.w, a growing share of a 4x4 ordered pattern.
bool hdFaded(float dist, vec2 fragCoord) {
    if (!hdOn(HD_FADE) || hdTime.w <= 0.0 || dist <= hdTime.z) return false;
    float share = (dist - hdTime.z) / hdTime.w;
    ivec2 p = ivec2(fragCoord) & 3;
    int index = p.x + p.y * 4;
    int bayer[16] = int[16](0, 8, 2, 10, 12, 4, 14, 6, 3, 11, 1, 9, 15, 7, 13, 5);
    return share > (float(bayer[index]) + 0.5) / 16.0;
}

// The point lights of lights.json (HD_LIGHTS) on a fragment at eye-space [viewPos] with the game's
// eye-space [normal] (any scale; a zero normal, as on the ground's unlit draws, takes light from every
// side): the light to multiply the unlit colour by, in the space the scene is lit in.
vec3 hdPointLights(vec3 viewPos, vec3 normal) {
    vec3 sum = vec3(0.0);
    float normalLength = length(normal);
    vec3 n = normalLength > 0.0001 ? normal / normalLength : vec3(0.0);
    for (int i = 0; i < hdLightInfo.x; i++) {
        vec3 toLight = hdLightPosition[i].xyz - viewPos;
        float dist = length(toLight);
        float radius = hdLightPosition[i].w;
        if (dist >= radius) continue;
        float falloff = 1.0 - dist / radius;
        falloff *= falloff;
        float facing = normalLength > 0.0001 ? max(dot(n, toLight / max(dist, 0.0001)), 0.0) * 0.8 + 0.2 : 1.0;
        sum += hdLightColour[i].rgb * falloff * facing;
    }
    return hdOn(HD_LINEAR) ? sum : sum * 0.8;
}

// [color] with the point lights added on its unlit colour [base] (HD_LIGHTS). The frame clamps the result.
vec4 hdAddPointLights(vec4 color, vec4 base, vec3 viewPos, vec3 normal) {
    if (!hdOn(HD_LIGHTS) || hdLightInfo.x == 0) return color;
    // not clamped: the texture multiplies it next, and a lit stone wall is near white already
    return vec4(color.rgb + base.rgb * hdPointLights(viewPos, normal), color.a);
}
