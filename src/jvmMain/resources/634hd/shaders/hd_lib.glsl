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
#define HD_ENV_LIGHT 16384
#define HD_CAUSTICS 32768
#define HD_GRASS 65536
#define HD_LIGHTING_MODEL 131072
#define HD_REFLECTIONS 262144
#define HD_MAX_ACTORS 8
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
    // the environment's tints of the sun's (light 0) and the ambient light (HD_ENV_LIGHT)
    vec4 hdEnvSun;
    vec4 hdEnvAmbient;
    // the area's water (HD_WATER): x waves' height, y speed, z glint, w wave size (1 each by default)
    vec4 hdWaterParams;
    // the characters grass bends away from (HD_GRASS): x how many; their world x, z
    vec4 hdActorInfo;
    vec4 hdActors[HD_MAX_ACTORS];
    // day and night (HdSettings.dayNight): the sun's or moon's eye-space direction, w 1 when it
    // replaces the game's light 0 (light 1, the game's opposite fill, follows it)
    vec4 hdDayNight;
    // the moon's world direction (xyz) and the night: w 0 by day, 1 at night (stars, the moon's disc)
    vec4 hdMoon;
    // the composite's exposure before the tonemap's curve (x; the curve is hdFlags.y: 1 ACES, 2 AgX)
    vec4 hdTonemap;
    // the loaded scene's size in world units (x, z): the fade near its edge (HD_FADE)
    vec4 hdSceneSize;
};

// The inverse of the composite's tonemap (post_composite.frag): the linear value that, after the
// exposure, the curve and the sRGB encode, shows as the sRGB [display] colour. Water draws (ff.frag)
// use it to show as the game shows them: the curve adds contrast to a surface made for plain sRGB
// (its net's ridges went white, its blue deep). Clamped at 0.76 (a white ridge shows at about 0.87),
// so the ridges feed the bloom about as the game's do, not as the bright values the inverse gives.
vec3 hdAcesInverse(vec3 y) {
    // Narkowicz's fit solved for x: (2.51 - 2.43y) x^2 + (0.03 - 0.59y) x - 0.14y = 0
    vec3 a = 2.51 - 2.43 * y;
    vec3 b = 0.03 - 0.59 * y;
    vec3 c = -0.14 * y;
    return (-b + sqrt(max(b * b - 4.0 * a * c, vec3(0.0)))) / (2.0 * a);
}

float hdAgxSigmoid(float x) {
    float x2 = x * x;
    float x4 = x2 * x2;
    return 15.5 * x4 * x2 - 40.14 * x4 * x + 31.96 * x4 - 6.868 * x2 * x + 0.4298 * x2 + 0.1191 * x - 0.00232;
}

vec3 hdAgxInverse(vec3 display) {
    // the composite's steps backwards: the saturation (grey kept), the outset matrix, the sigmoid (by
    // bisection), the log2 encoding, the inset matrix
    const mat3 inset = mat3(0.842479, 0.0784336, 0.0792237, 0.0423303, 0.878468, 0.0791661, 0.0423756, 0.0784336, 0.879142);
    const mat3 outset = mat3(1.19688, -0.0980209, -0.0990297, -0.0528969, 1.15190, -0.0989612, -0.0529716, -0.0980435, 1.15107);
    vec3 c = pow(max(display, vec3(0.0)), vec3(2.2));
    float grey = dot(c, vec3(0.2126, 0.7152, 0.0722));
    c = grey + (c - grey) / 1.12;
    c = pow(max(c, vec3(0.0)), vec3(1.0 / 2.2));
    c = inset * c;
    vec3 encoded;
    for (int i = 0; i < 3; i++) {
        float lo = 0.0;
        float hi = 1.0;
        for (int k = 0; k < 16; k++) {
            float mid = 0.5 * (lo + hi);
            if (hdAgxSigmoid(mid) < c[i]) lo = mid; else hi = mid;
        }
        encoded[i] = 0.5 * (lo + hi);
    }
    vec3 x = exp2(encoded * 16.5 - 12.47393);
    return outset * x;
}

vec3 hdTonemapInverse(vec3 display) {
    vec3 target = clamp(display, vec3(0.0), vec3(0.999));
    vec3 x = hdFlags.y == 2 ? hdAgxInverse(target) : hdAcesInverse(pow(target, vec3(2.2)));
    return min(x, vec3(0.76)) / max(hdTonemap.x, 0.01);
}

// 1 for a draw that belongs to the 3D view, 0 for the interface and everything else.
uniform int uHdScene;

// The sun's shadow map (HD_SHADOWS) on texture unit 16; HdShaders.link points every program here.
uniform sampler2DShadow uHdShadowMap;

// The occlusion height maps (HD_AO): moving models around the player on unit 17, the region's baked
// static scene on unit 22. Depth from above, so smaller is higher.
uniform sampler2D uHdAoMap;
uniform sampler2D uHdAoBake;

// The ground's baked occlusion (HD_AO), on unit 25: one texel per quarter tile of the ground drawing
// (HdShadows.bindGroundOcclusion), from the static scene only.
uniform sampler2D uHdGroundAo;

// The world y (the game's: smaller is higher) of the highest occluder over world ([x], [z]): from the
// moving models' map when [moving], and from the baked static scene when [baked].
float hdOccluderY(vec2 xz, bool baked, bool moving) {
    float y = 1.0e9;
    vec2 uv = (hdAoMatrix * vec4(xz.x, 0.0, xz.y, 1.0)).xy;
    if (moving && uv.x > 0.0 && uv.x < 1.0 && uv.y > 0.0 && uv.y < 1.0) {
        float d = texture(uHdAoMap, uv).r;
        if (d < 1.0) y = hdAoParams.w + d * hdAoParams.x;
    }
    if (baked && hdAoBake.z > 0.5) {
        vec2 bake = (hdAoBakeMatrix * vec4(xz.x, 0.0, xz.y, 1.0)).xy;
        float d = texture(uHdAoBake, bake).r;
        if (d < 1.0) y = min(y, hdAoBake.x + d * hdAoBake.y);
    }
    return y;
}

bool hdOn(int feature) {
    return uHdScene != 0 && (hdFlags.x & feature) != 0;
}

// Light [index]'s eye-space direction [dir]: the time of day's sun or moon instead of the game's (light
// 0, and light 1 opposite it) with day and night on; unchanged without.
vec3 hdLightDirection(vec3 dir, int index) {
    if (uHdScene != 0 && hdDayNight.w > 0.5 && index < 2) {
        return index == 0 ? hdDayNight.xyz : -hdDayNight.xyz;
    }
    return dir;
}

// The game's ambient light, and light [index]'s diffuse, as the environment tints them (HD_ENV_LIGHT:
// the sun is light 0); unchanged without it.
// For an unlit draw (water): how much the area's and the time of day's tints (HD_ENV_LIGHT) change
// the light on a face lit by the game's [ambient] and its sun's [diffuse] (at a typical angle).
vec3 hdUnlitLight(vec3 ambient, vec3 diffuse) {
    vec3 day = ambient + diffuse * 0.7;
    return (ambient * hdEnvAmbient.rgb + diffuse * 0.7 * hdEnvSun.rgb) / max(day, vec3(0.05));
}

// The sun's (or moon's) strength under the area's and the time of day's tint, 1 without them.
float hdSunStrength() {
    return hdOn(HD_ENV_LIGHT) ? clamp(dot(hdEnvSun.rgb, vec3(0.3333)), 0.0, 1.0) : 1.0;
}

vec4 hdEnvAmbientLight(vec4 ambient) {
    return hdOn(HD_ENV_LIGHT) ? vec4(ambient.rgb * hdEnvAmbient.rgb, ambient.a) : ambient;
}

vec4 hdEnvDiffuse(vec4 diffuse, int index) {
    return (index == 0 && hdOn(HD_ENV_LIGHT)) ? vec4(diffuse.rgb * hdEnvSun.rgb, diffuse.a) : diffuse;
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

// [rgb] scaled by a light factor [f] (a ratio of light, a shadow's darkening): with tonemapping the
// game's light multiplies the sRGB colour (hdApplyLight), so a factor on top does the same, which on
// the linear colour is f^2.2; in linear light the factor came out weaker than the game's own shading.
vec3 hdScaleLight(vec3 rgb, vec3 f) {
    return hdOn(HD_LINEAR) ? rgb * pow(max(f, vec3(0.0)), vec3(2.2)) : rgb * f;
}

vec3 hdScaleLight(vec3 rgb, float f) {
    return hdScaleLight(rgb, vec3(f));
}

// The ceiling of a lit colour in linear colour (HD_LINEAR), on the sRGB colour x light product that
// fixed-function lighting clamped at 1 per channel (so any strong enough light made a face white).
// Here the product is kept over 1 for the tonemapper, but no further than this, per channel as the
// game's clamp was: a strong light still makes a face white (2 is 4.6 in linear colour, white under
// the tonemap), with the excess the bloom glows from, and daylight (at most about 2) is untouched.
// Without it the game's point lights, which attenuate as 1 / (d / radius)^2 with no constant term
// (HdToolkit copyLights), ran to thousands on a lantern's own flame, which sits at its light: it
// reached the half-float ceiling and bloomed into a blob the size of Edgeville bank.
const float HD_LIT_MAX = 2.0;

// The game's light [lit] on the sRGB colour [srgb], in linear colour (HD_LINEAR).
vec3 hdLitColour(vec3 srgb, vec3 lit) {
    return hdToLinear(min(srgb * lit, vec3(HD_LIT_MAX)));
}

// The game's light on [base] (in the scene's space). With tonemapping (HD_LINEAR) the light multiplies
// the sRGB colour, as the game's does, before it is made linear: multiplying the linear colour by the
// same factor flattened the shading (a lit face only lit^(1/2.2) brighter), which with the tonemap's
// curve read as plastic. Not clamped at 1 there, so light over 1 reaches the tonemap (up to HD_LIT_MAX).
vec4 hdApplyLight(vec4 base, vec4 lit) {
    return hdOn(HD_LINEAR) ? vec4(hdLitColour(hdToSrgb(base.rgb), lit.rgb), base.a) : vec4(clamp(base.rgb * lit.rgb, 0.0, 1.0), base.a);
}

// The sky's colour (sRGB) in world direction [dir] (the game's axes: y points down).
vec3 hdSkyColour(vec3 dir) {
    float up = clamp(-dir.y / max(length(dir), 0.0001), 0.0, 1.0);
    return mix(hdSkyHorizon.rgb, hdSkyTop.rgb, pow(up, 0.4));
}

// [color] (in the scene's space) fogged by [amount] of the sRGB [fogRgb]. The mix is made in sRGB as
// the game makes it: in linear light (HD_LINEAR) a half-fogged dark surface would come out much
// brighter, thick fog (Draynor's sea) turning pale.
vec3 hdFogMix(vec3 color, vec3 fogRgb, float amount) {
    if (!hdOn(HD_LINEAR)) return mix(color, fogRgb, amount);
    return hdToLinear(mix(hdToSrgb(color), fogRgb, amount));
}

// Fog over a fragment at eye-space [viewPos] ([depth]: its eye depth). [fogColour], [start] and [end]
// are the draw's fixed-function fog, which is all there is unless HD_FOG (the environment's colour
// and distances, a smooth curve, height fog) or HD_SKY (the colour of the sky behind the fragment).
vec4 hdFog(vec4 color, vec3 viewPos, float depth, vec4 fogColour, float start, float end) {
    if (!hdOn(HD_FOG) && !hdOn(HD_SKY) && !hdOn(HD_FADE)) {
        float fogFactor = clamp((end - depth) / (end - start), 0.0, 1.0);
        return vec4(hdFogMix(color.rgb, fogColour.rgb, 1.0 - fogFactor), color.a);
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
    // the fade (HD_FADE): over the last band before the draw distance, into the fog or sky colour,
    // so the edge of the world and what pops in there melt into the background
    if (hdOn(HD_FADE) && hdTime.w > 0.0) {
        // (the water over the same band and into the same sky as everything else: faded to a colour of
        // its own, the far sea stood pale against the fogged shore)
        float fade = smoothstep(hdTime.z, hdTime.z + hdTime.w, dist);
        // and near the loaded scene's edge, whatever the distance, into the sky: at a long draw distance
        // the view reaches past it, and the sea (and the seabed's wall at its edge) stopped against
        // the sky along a jagged line
        float edge = 0.0;
        if (hdSceneSize.x > 0.0) {
            vec3 at = (hdViewToWorld * vec4(viewPos, 1.0)).xyz;
            float inside = min(min(at.x, at.z), min(hdSceneSize.x - at.x, hdSceneSize.y - at.z));
            edge = 1.0 - smoothstep(0.0, 4096.0, inside);
        }
        amount = max(amount, max(fade, edge));
    }
    return vec4(hdFogMix(color.rgb, fogRgb, amount), color.a);
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
    return vec4(hdScaleLight(color.rgb, 1.0 - 0.42 * facing * (1.0 - lit)), color.a);
}

// How much of the sky world point [world] sees, from the height maps (the baked static scene only
// when [baked]): the heights around it in eight directions (only those in front of a wall-like
// [normal], eye space) and straight above it. 1 in the open.
float hdSkyVisibility(vec3 world, vec3 normal, bool baked) {
    float normalLength = length(normal);
    vec3 n = normalLength > 0.0001 ? mat3(hdViewToWorld) * (normal / normalLength) : vec3(0.0, -1.0, 0.0);
    float up = -n.y;
    vec2 side = n.xz;
    float occlusion = 0.0;
    float weight = 0.0;
    // straight above: a canopy, a porch (the static scene only: a passing character is no roof)
    if (up > 0.5) {
        float above = world.y - hdOccluderY(world.xz, baked, false);
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
        // one distance, about a third of a tile (the static scene's vertices are baked more finely)
        float radius = 180.0;
        // occluders lower than a tall grass tuft (about 180 units) are left out: grass is not a wall
        float height = world.y - hdOccluderY(world.xz + dir * radius, baked, true) - 180.0;
        sideOcclusion[which] += clamp(height / (radius * 1.2), 0.0, 1.0);
        sideWeight[which] += 1.0;
    }
    if (up < 0.5 && sideWeight[1] > 0.0 && (sideWeight[0] == 0.0 || sideOcclusion[1] / sideWeight[1] < sideOcclusion[0] / sideWeight[0])) {
        occlusion += sideOcclusion[1];
        weight += sideWeight[1];
    } else {
        occlusion += sideOcclusion[0];
        weight += sideWeight[0];
    }
    return weight > 0.0 ? 1.0 - occlusion / weight : 1.0;
}

// How much of the sky a fragment at eye-space [viewPos] sees (HD_AO), with the strength applied.
float hdAmbientOcclusion(vec3 viewPos, vec3 normal) {
    if (!hdOn(HD_AO)) return 1.0;
    vec3 world = (hdViewToWorld * vec4(viewPos, 1.0)).xyz;
    return mix(1.0, hdSkyVisibility(world, normal, true), hdAoParams.y);
}

// The same for a ground fragment whose baked occlusion is bound (uHdGround 2): the static scene's part
// read from uHdGroundAo, the moving models' measured per pixel only where their map covers. A ground
// whose texture is not ready yet (uHdGround 3, just after a region load) gets none (ff.frag): it fades in
// with the rest of the scene's occlusion (hdAoParams.y) once everything is baked.
float hdGroundOcclusion(vec3 viewPos, vec3 normal) {
    if (!hdOn(HD_AO)) return 1.0;
    vec3 world = (hdViewToWorld * vec4(viewPos, 1.0)).xyz;
    float ao = texture(uHdGroundAo, world.xz / (vec2(textureSize(uHdGroundAo, 0)) * 128.0)).r;
    vec2 uv = (hdAoMatrix * vec4(world.x, 0.0, world.z, 1.0)).xy;
    if (uv.x > -0.02 && uv.x < 1.02 && uv.y > -0.02 && uv.y < 1.02) {
        ao = min(ao, hdSkyVisibility(world, normal, false));
    }
    return mix(1.0, ao, hdAoParams.y);
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


