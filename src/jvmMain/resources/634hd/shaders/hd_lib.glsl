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
#define HD_MATERIALS 8192
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
    // world space to the occlusion height map's (u, v, depth) (HD_AO)
    mat4 hdAoMatrix;
    // x: height range of the map's depth, y: strength, z: uv per scene unit
    vec4 hdAoParams;
    // water's tint (sRGB), w: how much of the game's water colour it replaces (HD_WATER)
    vec4 hdWaterTint;
    // x: the debug panel's overlay (0 none, 1 normals, 2 texture ids, 3 lights, 4 shadow map,
    // 5 chunk bounds, 6 occlusion, 7 shadows)
    ivec4 hdDebug;
    // the region's baked occlusion height map (HD_AO): world to (u, v, depth); x top, y height range,
    // z 1 once baked
    mat4 hdAoBakeMatrix;
    vec4 hdAoBake;
};

// 1 for a draw that belongs to the 3D view, 0 for the interface and everything else.
uniform int uHdScene;

// The sun's shadow map (HD_SHADOWS) on texture unit 16; HdShaders.link points every program here.
uniform sampler2DShadow uHdShadowMap;

// The occlusion height maps (HD_AO): moving models around the player on unit 17, the region's baked
// static scene on unit 22. Depth from above, so smaller is higher.
uniform sampler2D uHdAoMap;
uniform sampler2D uHdAoBake;

// The world y (the game's: smaller is higher) of the highest occluder over world ([x], [z]), from both.
float hdOccluderY(vec2 xz) {
    float y = 1.0e9;
    vec2 uv = (hdAoMatrix * vec4(xz.x, 0.0, xz.y, 1.0)).xy;
    if (uv.x > 0.0 && uv.x < 1.0 && uv.y > 0.0 && uv.y < 1.0) {
        float d = texture(uHdAoMap, uv).r;
        if (d < 1.0) y = hdAoParams.w + d * hdAoParams.x;
    }
    if (hdAoBake.z > 0.5) {
        vec2 bake = (hdAoBakeMatrix * vec4(xz.x, 0.0, xz.y, 1.0)).xy;
        float d = texture(uHdAoBake, bake).r;
        if (d < 1.0) y = min(y, hdAoBake.x + d * hdAoBake.y);
    }
    return y;
}

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

// How much of the sun reaches a fragment at eye-space [viewPos] (HD_SHADOWS): 1 in the open, 0 in
// full shadow; 3x3 PCF, fading out towards the edge of the shadow map.
float hdShadow(vec3 viewPos) {
    if (!hdOn(HD_SHADOWS)) return 1.0;
    vec4 p = hdShadowMatrix * vec4(viewPos, 1.0);
    if (p.x <= 0.0 || p.x >= 1.0 || p.y <= 0.0 || p.y >= 1.0 || p.z >= 1.0) return 1.0;
    float texel = 1.0 / float(max(hdFlags.z, 1));
    float sum = 0.0;
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            sum += texture(uHdShadowMap, vec3(p.xy + vec2(x, y) * texel, p.z - 0.0012));
        }
    }
    vec2 edge = min(p.xy, 1.0 - p.xy);
    float fade = clamp(min(edge.x, edge.y) * 12.0, 0.0, 1.0);
    return mix(1.0, sum / 9.0, fade);
}

// [color] darkened where the sun is blocked: by the share of its light the surface would get from the
// sun ([normal] the game's eye-space normal; a zero normal counts as facing it).
vec4 hdApplyShadow(vec4 color, vec3 viewPos, vec3 normal) {
    if (!hdOn(HD_SHADOWS) || hdSunDirection.w < 0.5) return color;
    float lit = hdShadow(viewPos);
    if (lit >= 0.999) return color;
    float normalLength = length(normal);
    float facing = normalLength > 0.0001 ? max(dot(normal / normalLength, hdSunDirection.xyz), 0.0) : 1.0;
    return vec4(color.rgb * (1.0 - 0.42 * facing * (1.0 - lit)), color.a);
}

// How much of the sky a fragment at eye-space [viewPos] sees (HD_AO), from the height map: the
// heights around it at two distances in eight directions (only those in front of a wall-like
// [normal]) and straight above it. 1 in the open.
float hdAmbientOcclusion(vec3 viewPos, vec3 normal) {
    if (!hdOn(HD_AO)) return 1.0;
    vec3 world = (hdViewToWorld * vec4(viewPos, 1.0)).xyz;
    float normalLength = length(normal);
    vec3 n = normalLength > 0.0001 ? mat3(hdViewToWorld) * (normal / normalLength) : vec3(0.0, -1.0, 0.0);
    float up = -n.y;
    vec2 side = n.xz;
    float occlusion = 0.0;
    float weight = 0.0;
    // straight above: a roof, a canopy
    if (up > 0.5) {
        float above = world.y - hdOccluderY(world.xz);
        occlusion += 2.0 * smoothstep(240.0, 600.0, above);
        weight += 2.0;
    }
    // around it; for a wall-like face, each side on its own and the opener one kept (the game's
    // normals do not say reliably which side is out)
    float sideOcclusion[2] = float[2](0.0, 0.0);
    float sideWeight[2] = float[2](0.0, 0.0);
    for (int i = 0; i < 8; i++) {
        float angle = float(i) * 0.785398 + 0.39;
        vec2 dir = vec2(cos(angle), sin(angle));
        float facing = dot(dir, side);
        int which = (up < 0.5 && facing < 0.0) ? 1 : 0;
        if (up < 0.5 && abs(facing) < 0.1) continue;
        for (int r = 0; r < 2; r++) {
            float radius = r == 0 ? 110.0 : 300.0;
            // occluders lower than a tall grass tuft (about 180 units) are left out: grass is not a wall
            float height = world.y - hdOccluderY(world.xz + dir * radius) - 180.0;
            sideOcclusion[which] += clamp(height / (radius * 1.2), 0.0, 1.0);
            sideWeight[which] += 1.0;
        }
    }
    if (up < 0.5 && sideWeight[1] > 0.0 && (sideWeight[0] == 0.0 || sideOcclusion[1] / sideWeight[1] < sideOcclusion[0] / sideWeight[0])) {
        occlusion += sideOcclusion[1];
        weight += sideWeight[1];
    } else {
        occlusion += sideOcclusion[0];
        weight += sideWeight[0];
    }
    float ao = weight > 0.0 ? 1.0 - occlusion / weight : 1.0;
    return mix(1.0, ao, hdAoParams.y);
}

// The height of 634HD's water waves (any units) at world ([x], [z]), and its slope by finite difference.
float hdWaveHeight(vec2 p, float t) {
    return sin(dot(p, vec2(0.0061, 0.0023)) + t * 1.3) * 0.5
        + sin(dot(p, vec2(-0.0029, 0.0071)) + t * 1.7) * 0.35
        + sin(dot(p, vec2(0.0143, -0.0097)) + t * 2.9) * 0.15
        + sin(dot(p, vec2(-0.0231, -0.0187)) + t * 3.7) * 0.08;
}

// Water (HD_WATER, on a water material): the game's water colour, tinted towards the environment's,
// with animated normals, a Fresnel reflection of the sky and the sun's glint (blocked by shadows).
vec4 hdWater(vec4 color, vec3 viewPos) {
    vec3 world = (hdViewToWorld * vec4(viewPos, 1.0)).xyz;
    vec3 camera = hdViewToWorld[3].xyz;
    float t = hdTime.x;
    float e = 8.0;
    float h = hdWaveHeight(world.xz, t);
    float hx = hdWaveHeight(world.xz + vec2(e, 0.0), t) - h;
    float hz = hdWaveHeight(world.xz + vec2(0.0, e), t) - h;
    // world up is -y; the slopes are scaled to a gentle tilt
    vec3 n = normalize(vec3(-hx * 3.5, -1.0, -hz * 3.5));
    vec3 view = normalize(world - camera);
    float cosine = max(dot(-view, n), 0.0);
    float fresnel = 0.03 + 0.97 * pow(1.0 - cosine, 5.0);
    vec3 reflected = reflect(view, n);
    vec3 sky = hdColour(vec4(hdSkyColour(reflected), 1.0)).rgb;
    vec3 tint = hdColour(vec4(hdWaterTint.rgb, 1.0)).rgb;
    vec3 base = mix(color.rgb, tint * max(max(color.r, color.g), color.b) * 1.6, hdWaterTint.w);
    vec3 sunWorld = hdSunDirection.w > 0.5 ? normalize(mat3(hdViewToWorld) * hdSunDirection.xyz) : vec3(0.0, -1.0, 0.0);
    // the waves catch the light: brighter where they face the sun
    base *= 0.8 + 0.4 * max(dot(n, sunWorld), 0.0);
    vec3 result = mix(base, sky, clamp(fresnel * 0.85, 0.0, 0.85));
    if (hdSunDirection.w > 0.5) {
        float glint = pow(max(dot(reflected, sunWorld), 0.0), 90.0) * hdShadow(viewPos);
        result += hdSunColour.rgb * glint * (hdOn(HD_LINEAR) ? 3.0 : 0.8);
    }
    return vec4(result, color.a);
}

// Smooth value noise in 0..1.
float hdNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = fract(sin(dot(i, vec2(127.1, 311.7))) * 43758.5453);
    float b = fract(sin(dot(i + vec2(1.0, 0.0), vec2(127.1, 311.7))) * 43758.5453);
    float c = fract(sin(dot(i + vec2(0.0, 1.0), vec2(127.1, 311.7))) * 43758.5453);
    float d = fract(sin(dot(i + vec2(1.0, 1.0), vec2(127.1, 311.7))) * 43758.5453);
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}


