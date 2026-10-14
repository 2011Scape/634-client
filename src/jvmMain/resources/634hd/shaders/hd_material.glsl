// Not part of the original client: 634HD (toolkit 6). Phase 5 material maps (HD_MATERIALS), for
// fragment shaders only (hd_lib.glsl first). A texture with maps (materials.json) has a slot in the
// HdMaterialMaps block (HdMaterialMaps.kt) and a layer in three texture arrays: tangent-space normal
// (x, y), packed occlusion/roughness/emissive mask with the height in alpha, and emitted colour. The cache
// texture stays the albedo. The tangent frame comes from the fragment's position and uv derivatives,
// so no vertex data is needed and the ground, models and the retained scene all take maps.

#define HD_MAX_MAPS 256

layout(std140) uniform HdMaterialMaps {
    // x: layer, y: specular strength, z: emissive strength, w: parallax depth
    vec4 hdMapA[HD_MAX_MAPS];
    // x: has normal, y: has orm, z: has emissive, w: unused
    vec4 hdMapB[HD_MAX_MAPS];
};

// texture units 19, 20 and 21 (HdShaders.link)
uniform sampler2DArray uHdNormalMaps;
uniform sampler2DArray uHdOrmMaps;
uniform sampler2DArray uHdEmissiveMaps;

bool hdHasMaps(int slot) {
    return slot >= 0 && hdOn(HD_MATERIALS);
}

// The tangent frame at a fragment: tangent along u, bitangent along v, the face normal (towards the
// viewer) - from screen-space derivatives of the eye-space position and the texture coordinates.
mat3 hdTangentFrame(vec3 viewPos, vec2 uv) {
    vec3 dp1 = dFdx(viewPos);
    vec3 dp2 = dFdy(viewPos);
    vec2 duv1 = dFdx(uv);
    vec2 duv2 = dFdy(uv);
    vec3 n = normalize(cross(dp1, dp2));
    if (dot(n, -viewPos) < 0.0) n = -n;
    vec3 dp2perp = cross(dp2, n);
    vec3 dp1perp = cross(n, dp1);
    vec3 t = dp2perp * duv1.x + dp1perp * duv2.x;
    vec3 b = dp2perp * duv1.y + dp1perp * duv2.y;
    float scale = inversesqrt(max(max(dot(t, t), dot(b, b)), 1e-20));
    return mat3(t * scale, b * scale, n);
}

// [uv] moved by the height map (parallax offset, a few steps), for a material with a parallax depth.
vec2 hdParallaxUv(int slot, vec2 uv, vec3 viewPos) {
    if (!hdHasMaps(slot) || hdMapA[slot].w <= 0.0 || hdMapB[slot].y < 0.5) return uv;
    mat3 tbn = hdTangentFrame(viewPos, uv);
    vec3 view = normalize(-viewPos) * tbn;
    float layer = hdMapA[slot].x;
    float depth = hdMapA[slot].w;
    vec2 step = view.xy / max(view.z, 0.25) * depth / 6.0;
    vec2 p = uv;
    float current = 0.0;
    float height = 1.0 - texture(uHdOrmMaps, vec3(p, layer)).a;
    for (int i = 0; i < 6 && current < height; i++) {
        p -= step;
        current += 1.0 / 6.0;
        height = 1.0 - texture(uHdOrmMaps, vec3(p, layer)).a;
    }
    return p;
}

// [color] with the material's maps: the surface relit by its normal map (the ratio of sun and
// ambient light on the mapped normal to that on the face), occlusion, a specular highlight from the
// roughness, and emission (added, unlit).
vec4 hdApplyMaterial(int slot, vec4 color, vec2 uv, vec3 viewPos) {
    if (!hdHasMaps(slot)) return color;
    float layer = hdMapA[slot].x;
    mat3 tbn = hdTangentFrame(viewPos, uv);
    vec3 face = tbn[2];
    vec3 n = face;
    if (hdMapB[slot].x > 0.5) {
        // x and y from the map (BC5 keeps two channels), z rebuilt
        vec2 xy = texture(uHdNormalMaps, vec3(uv, layer)).xy * 2.0 - 1.0;
        vec3 mapped = vec3(xy, sqrt(max(1.0 - dot(xy, xy), 0.0)));
        n = normalize(tbn * mapped);
    }
    vec4 orm = hdMapB[slot].y > 0.5 ? texture(uHdOrmMaps, vec3(uv, layer)) : vec4(1.0, 0.7, 0.0, 1.0);
    vec3 sun = hdSunDirection.w > 0.5 ? hdSunDirection.xyz : vec3(0.0, 1.0, 0.0);
    float ambient = max(dot(hdAmbientColour.rgb, vec3(0.333)), 0.05);
    float sunLight = max(dot(hdSunColour.rgb, vec3(0.333)), 0.0);
    float flat_ = ambient + sunLight * max(dot(face, sun), 0.0);
    float mappedLight = ambient + sunLight * max(dot(n, sun), 0.0);
    // a bumpy surface turns, on average, a little away from the light: 1.08 keeps the overall
    // brightness where the game had it, so the maps add relief rather than darken
    float relight = clamp(mappedLight / max(flat_, 0.05) * 1.08, 0.45, 1.6);
    vec3 rgb = color.rgb * relight * mix(1.0, orm.r, 0.45);
    // a highlight that narrows as the surface gets smoother
    float roughness = orm.g;
    vec3 view = normalize(-viewPos);
    vec3 halfway = normalize(view + sun);
    float shininess = mix(96.0, 6.0, roughness);
    float highlight = pow(max(dot(n, halfway), 0.0), shininess) * (1.0 - roughness) * hdMapA[slot].y;
    rgb += hdSunColour.rgb * highlight * hdShadow(viewPos);
    if (hdMapB[slot].z > 0.5) {
        rgb += hdColour(texture(uHdEmissiveMaps, vec3(uv, layer))).rgb * orm.b * hdMapA[slot].z;
    }
    return vec4(rgb, color.a);
}

// The debug panel's overlays (hdDebug.x) on a 3D-view fragment. [texture] identifies the texture for
// the id overlay: the 634 texture id, or 4096 plus the retained scene's material slot, -1 for none;
// it is written exactly into red and green so the panel can read the id under the mouse.
vec4 hdDebugOverlay(vec4 color, vec3 viewPos, vec3 normal, int texture) {
    int mode = hdDebug.x;
    if (mode == 0 || uHdScene == 0) return color;
    vec3 world = (hdViewToWorld * vec4(viewPos, 1.0)).xyz;
    if (mode == 1) {
        float l = length(normal);
        vec3 n = l > 0.0001 ? normal / l : vec3(0.0, 0.0, 1.0);
        return vec4(n * 0.5 + 0.5, 1.0);
    }
    if (mode == 2) {
        if (texture < 0) return vec4(0.0, 0.0, 0.0, 1.0);
        return vec4(float(texture & 255) / 255.0, float((texture >> 8) & 255) / 255.0, 1.0, 1.0);
    }
    if (mode == 3) {
        // how many lights reach the fragment: blue one, green two or three, red more
        int count = 0;
        for (int i = 0; i < hdLightInfo.x; i++) {
            if (length(hdLightPosition[i].xyz - viewPos) < hdLightPosition[i].w) count++;
        }
        vec3 heat = count == 0 ? vec3(0.0) : count == 1 ? vec3(0.0, 0.3, 1.0) : count <= 3 ? vec3(0.0, 1.0, 0.2) : vec3(1.0, 0.1, 0.0);
        return vec4(mix(color.rgb * 0.4, heat, count == 0 ? 0.0 : 0.6), 1.0);
    }
    if (mode == 4) {
        vec4 p = hdShadowMatrix * vec4(viewPos, 1.0);
        bool inside = p.x > 0.0 && p.x < 1.0 && p.y > 0.0 && p.y < 1.0;
        vec2 grid = fract(p.xy * 16.0);
        float line = (min(grid.x, grid.y) < 0.03) ? 1.0 : 0.0;
        vec3 tint = inside ? mix(vec3(0.2, 0.9, 0.3), vec3(1.0), line) : vec3(0.9, 0.2, 0.2);
        return vec4(mix(color.rgb, tint, 0.45) * mix(0.5, 1.0, hdShadow(viewPos)), 1.0);
    }
    if (mode == 5) {
        // 8x8-tile chunk edges (the retained scene's), tile edges faintly
        vec2 tile = world.xz / 512.0;
        vec2 chunk = fract(tile / 8.0);
        vec2 width = fwidth(tile / 8.0) * 1.5;
        float edge = (chunk.x < width.x || chunk.y < width.y) ? 1.0 : 0.0;
        vec2 cell = fract(tile);
        vec2 cellWidth = fwidth(tile);
        float tileEdge = (cell.x < cellWidth.x || cell.y < cellWidth.y) ? 0.35 : 0.0;
        return vec4(mix(color.rgb, vec3(1.0, 0.85, 0.0), max(edge, tileEdge)), color.a);
    }
    if (mode == 6) return vec4(vec3(hdAmbientOcclusion(viewPos, normal)), 1.0);
    if (mode == 7) return vec4(vec3(hdShadow(viewPos)), 1.0);
    return color;
}

// The height of 634HD's water waves (any units) at world ([x], [z]): long, slow swells, a tile or
// more from crest to crest, so the surface moves without going grainy.
float hdWaveHeight(vec2 p, float t) {
    return sin(dot(p, vec2(0.0021, 0.0009)) + t * 0.9) * 0.5
        + sin(dot(p, vec2(-0.0012, 0.0026)) + t * 1.1) * 0.35
        + sin(dot(p, vec2(0.0043, -0.0031)) + t * 1.7) * 0.15;
}

// Water (HD_WATER, on a water material): the game's water, kept as the base, gently tinted towards
// the environment's colour, with slow swells that catch the light a little, a Fresnel reflection of
// the sky that only shows at grazing angles, and a soft sun glint that fades out at a distance,
// where it would only sparkle.
vec4 hdWater(vec4 color, vec3 viewPos) {
    vec3 world = (hdViewToWorld * vec4(viewPos, 1.0)).xyz;
    vec3 camera = hdViewToWorld[3].xyz;
    float t = hdTime.x;
    float e = 24.0;
    float h = hdWaveHeight(world.xz, t);
    float hx = hdWaveHeight(world.xz + vec2(e, 0.0), t) - h;
    float hz = hdWaveHeight(world.xz + vec2(0.0, e), t) - h;
    // world up is -y; the slopes give a tilt of a few degrees at most
    vec3 n = normalize(vec3(-hx * 1.2, -1.0, -hz * 1.2));
    vec3 view = normalize(world - camera);
    float cosine = max(dot(-view, n), 0.0);
    float fresnel = 0.02 + 0.98 * pow(1.0 - cosine, 5.0);
    vec3 reflected = reflect(view, n);
    vec3 sky = hdColour(vec4(hdSkyColour(reflected), 1.0)).rgb;
    vec3 tint = hdColour(vec4(hdWaterTint.rgb, 1.0)).rgb;
    vec3 base = mix(color.rgb, tint * max(max(color.r, color.g), color.b) * 1.4, hdWaterTint.w * 0.6);
    vec3 sunWorld = hdSunDirection.w > 0.5 ? normalize(mat3(hdViewToWorld) * hdSunDirection.xyz) : vec3(0.0, -1.0, 0.0);
    base *= 0.94 + 0.12 * max(dot(n, sunWorld), 0.0);
    vec3 result = mix(base, sky, clamp(fresnel * 0.6, 0.0, 0.45));
    if (hdSunDirection.w > 0.5) {
        // a broad, soft glint, gone where a pixel spans more than a few units of water
        float footprint = length(fwidth(world));
        float distanceFade = 1.0 - smoothstep(18.0, 60.0, footprint);
        float glint = pow(max(dot(reflected, sunWorld), 0.0), 48.0) * hdShadow(viewPos) * distanceFade;
        result += hdSunColour.rgb * glint * (hdOn(HD_LINEAR) ? 0.8 : 0.25);
    }
    return vec4(result, color.a);
}
