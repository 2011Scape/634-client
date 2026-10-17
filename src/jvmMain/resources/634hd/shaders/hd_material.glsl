// Not part of the original client: 634HD (toolkit 6). Phase 5 material maps (HD_MATERIALS), for
// fragment shaders only (hd_lib.glsl first). A texture with maps (materials.json) has a slot in the
// HdMaterialMaps block (HdMaterialMaps.kt) and a layer in three texture arrays: tangent-space normal
// (x, y), packed occlusion/roughness/emissive mask with the height in alpha, and emitted colour. The cache
// texture stays the albedo. The tangent frame comes from the fragment's position and uv derivatives,
// so no vertex data is needed and the ground, models and the retained scene all take maps.

#define HD_MAX_MAPS 448

layout(std140) uniform HdMaterialMaps {
    // x: layer, y: specular strength, z: emissive strength, w: parallax depth
    vec4 hdMapA[HD_MAX_MAPS];
    // x: has normal, y: has orm, z: has emissive, w: the emissive array's layer
    vec4 hdMapB[HD_MAX_MAPS];
};

// texture units 19, 20 and 21 (HdShaders.link)
uniform sampler2DArray uHdNormalMaps;
uniform sampler2DArray uHdOrmMaps;
uniform sampler2DArray uHdEmissiveMaps;

// The flowing textures (117HD's fire cape and lava), a second block: the first sits just under GL's
// least block size (HdMaterialMaps.kt).
layout(std140) uniform HdMaterialFlow {
    // x: the flow map's layer plus one (0: none), y: its strength, z and w: 1 / the seconds the map
    // takes to slide once along u and v (0: it stands still)
    vec4 hdMapC[HD_MAX_MAPS];
    // x and y: extra scroll in uv per second on top of the game's own, z: unlit (1), w: the water type plus
    // one (0: the default type; HdWaterTypes below)
    vec4 hdMapD[HD_MAX_MAPS];
};

// texture unit 31 (HdShaders.link): the flow maps, sRGB (read linearised, as 117HD reads its)
uniform sampler2DArray uHdFlowMaps;

// 634HD: the water's reflection (HdReflections, unit 26): the viewport it is for (z 0: none), and the
// texture (sRGB, alpha its coverage); sampled by the water surface below and by ff.frag's game water
uniform vec4 uHdReflection;
uniform sampler2D uHdReflectionMap;

// 634HD's water surface (HdSettings.water 2): 117HD's water types (rs117/hd/scene/water_types/WaterType.java),
// a third block (HdMaterialMaps.loadWaterTypes); type 0 the defaults, a water material's own at hdMapD.w.
// Its maps are trailing layers of the flow array (117HD's water_normal_map_1, water_foam, water_flow_map).
#define HD_MAX_WATER_TYPES 16
layout(std140) uniform HdWaterTypes {
    // x: the water normal map's layer in the flow array, y: the foam map's, z: the flow map's, w: types loaded
    vec4 hdWaterMaps;
    // the surface's colour (sRGB), w: the least opacity                   (surfaceColor, baseOpacity)
    vec4 hdWaterSurfaceColour[HD_MAX_WATER_TYPES];
    // the shore foam's colour (sRGB), w: 1 with foam                      (foamColor, hasFoam)
    vec4 hdWaterFoamColour[HD_MAX_WATER_TYPES];
    // the seabed's tint (sRGB), w: 1 for a flat, opaque type              (depthColor, flat)
    vec4 hdWaterDepthColour[HD_MAX_WATER_TYPES];
    // x: the glint's strength, y: its exponent, z: the ripples' height, w: the reflection's share
    //                                                                      (specularStrength, specularGloss, normalStrength, fresnelAmount)
    vec4 hdWaterSpec[HD_MAX_WATER_TYPES];
    // x: the animation's period scale (duration)
    vec4 hdWaterTime[HD_MAX_WATER_TYPES];
};

// [rgb] with a highlight [h] added. Highlights are tuned as amounts of the game's (sRGB) colour; with
// tonemapping (HD_LINEAR) the colour is linear, where the same amount is several times brighter (0.2
// linear is about 0.48 sRGB) and lit surfaces took a plastic sheen: there it is added in sRGB.
vec3 hdAddHighlight(vec3 rgb, vec3 h) {
    return hdOn(HD_LINEAR) ? hdToLinear(hdToSrgb(rgb) + h) : rgb + h;
}

bool hdHasMaps(int slot) {
    return slot >= 0 && hdOn(HD_MATERIALS);
}

// Whether the material is drawn as its texture alone: no lighting, shadow or occlusion, the face
// colour ignored (117HD's unlit with overrideBaseColor: its fire cape and lava).
bool hdUnlit(int slot) {
    return hdHasMaps(slot) && hdMapD[slot].z > 0.5;
}

// 117HD's flowing texture: [uv] slid by the material's extra scroll, then moved by the flow map's red
// and green (0..1, linearised: the offset is one-sided, as in 117HD), the map itself sampled at the uv
// slid once per its duration along each axis. The scroll and the slide take the fraction of hdTime.x
// (seconds, wrapping hourly): a repeating texture reads the same, and the uv keeps its precision.
vec2 hdFlowUv(int slot, vec2 uv) {
    if (!hdHasMaps(slot)) return uv;
    uv += fract(hdMapD[slot].xy * hdTime.x);
    if (hdMapC[slot].x > 0.5) {
        vec2 flowUv = uv - fract(vec2(hdTime.x) * hdMapC[slot].zw);
        uv += texture(uHdFlowMaps, vec3(flowUv, hdMapC[slot].x - 1.0)).xy * hdMapC[slot].y;
    }
    return uv;
}

// An unlit material's [color]: only its emission is added (lava keeps its glow; the fire cape has none).
vec4 hdUnlitMaterial(int slot, vec4 color, vec2 uv) {
    if (!hdHasMaps(slot) || hdMapB[slot].z < 0.5) return color;
    float layer = hdMapA[slot].x;
    float mask = hdMapB[slot].y > 0.5 ? texture(uHdOrmMaps, vec3(uv, layer)).b : 0.0;
    color.rgb += hdColour(texture(uHdEmissiveMaps, vec3(uv, hdMapB[slot].w))).rgb * mask * hdMapA[slot].z;
    return color;
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
    vec3 rgb = hdScaleLight(color.rgb, relight * mix(1.0, orm.r, 0.45));
    // a highlight that narrows as the surface gets smoother
    float roughness = orm.g;
    vec3 view = normalize(-viewPos);
    vec3 halfway = normalize(view + sun);
    float shininess = mix(96.0, 6.0, roughness);
    float highlight = pow(max(dot(n, halfway), 0.0), shininess) * (1.0 - roughness) * hdMapA[slot].y;
    rgb = hdAddHighlight(rgb, hdSunColour.rgb * highlight * hdShadow(viewPos));
    if (hdMapB[slot].z > 0.5) {
        rgb += hdColour(texture(uHdEmissiveMaps, vec3(uv, hdMapB[slot].w))).rgb * orm.b * hdMapA[slot].z;
    }
    return vec4(rgb, color.a);
}

// The lighting model (HD_LIGHTING_MODEL): the light a surface with [normal] (eye space, unit) gets, as
// a sum of terms - ambient and a hemisphere of sky light from above and warm bounce light from below
// (both scaled by [occlusion], the occlusion map), and the sun on the normal, scaled by [sun] (1 lit,
// 0 in shadow). In the game's colour units: the ambient and sun colours are the frame's.
vec3 hdModelLight(vec3 normal, vec3 up, float occlusion, float sun) {
    vec3 ambient = max(hdAmbientColour.rgb, vec3(0.05));
    float ambientLevel = dot(ambient, vec3(0.333));
    // sky light: mostly the ambient's own colour, a little of the sky's; bounce: a little warmer
    vec3 skyTint = hdSkyTop.rgb / max(dot(hdSkyTop.rgb, vec3(0.333)), 0.05);
    vec3 sky = mix(ambient, skyTint * ambientLevel, 0.15) * 0.45;
    vec3 bounce = ambient * vec3(1.06, 0.98, 0.88) * 0.25;
    float facingUp = dot(normal, up);
    vec3 fill = ambient * 0.75 + sky * max(facingUp, 0.0) + bounce * max(-facingUp, 0.0);
    vec3 direct = vec3(0.0);
    if (hdSunDirection.w > 0.5) {
        direct = hdSunColour.rgb * max(dot(normal, hdSunDirection.xyz), 0.0) * sun;
    }
    return fill * occlusion + direct;
}

// The point lights' highlights (HD_LIGHTS) on a surface with [normal] at [viewPos]: Phong, [gloss] wide,
// each light's colour times its falloff.
vec3 hdPointHighlights(vec3 viewPos, vec3 normal, float gloss) {
    vec3 sum = vec3(0.0);
    if (!hdOn(HD_LIGHTS)) return sum;
    vec3 view = normalize(-viewPos);
    bool walls = hdOn(HD_LIGHT_GRID);
    vec3 world = walls ? (hdViewToWorld * vec4(viewPos, 1.0)).xyz : vec3(0.0);
    for (int i = 0; i < hdLightInfo.x; i++) {
        vec3 toLight = hdLightPosition[i].xyz - viewPos;
        float dist = length(toLight);
        float radius = hdLightPosition[i].w;
        if (dist >= radius) continue;
        float falloff = hdPointFalloff(dist, radius);
        if (walls) falloff *= hdLightVisibility(i, world);
        vec3 reflected = reflect(-toLight / max(dist, 0.0001), normal);
        sum += hdLightColour[i].rgb * falloff * pow(max(dot(view, reflected), 0.0), gloss);
    }
    return sum;
}

// [color] (the game's colour, lit as the game lights it) relit by the lighting model: the game's light
// is taken as the model's light on the smooth [normal] in full sun, and replaced, per colour channel,
// by the model's light on the material's mapped normal with the shadow, the occlusion map and a
// highlight from the roughness map; emission is added unlit. A zero [normal] keeps the old path.
vec4 hdLightModel(int slot, vec4 color, vec2 uv, vec3 viewPos, vec3 normal) {
    float normalLength = length(normal);
    if (normalLength < 0.0001) return hdApplyShadow(hdApplyMaterial(slot, color, uv, viewPos), viewPos, normal);
    vec3 n0 = normal / normalLength;
    if (dot(n0, -viewPos) < 0.0) n0 = -n0;
    // the world's up (the game's y points down) in eye space
    vec3 up = normalize(-vec3(hdViewToWorld[0].y, hdViewToWorld[1].y, hdViewToWorld[2].y));
    vec3 n = n0;
    float occlusion = 1.0;
    float roughness = 1.0;
    float strength = 0.0;
    bool mapped = hdHasMaps(slot);
    float layer = mapped ? hdMapA[slot].x : 0.0;
    if (mapped) {
        if (hdMapB[slot].x > 0.5) {
            // the tangent frame around the smooth normal, so the relief follows the model's shading
            mat3 frame = hdTangentFrame(viewPos, uv);
            vec3 t = normalize(frame[0] - n0 * dot(frame[0], n0));
            vec3 b = normalize(frame[1] - n0 * dot(frame[1], n0) - t * dot(frame[1], t));
            if (dot(cross(t, b), n0) < 0.0) b = -b;
            vec2 xy = texture(uHdNormalMaps, vec3(uv, layer)).xy * 2.0 - 1.0;
            n = normalize(mat3(t, b, n0) * vec3(xy, sqrt(max(1.0 - dot(xy, xy), 0.0))));
        }
        if (hdMapB[slot].y > 0.5) {
            vec4 orm = texture(uHdOrmMaps, vec3(uv, layer));
            occlusion = mix(1.0, orm.r, 0.45);
            roughness = orm.g;
        }
        strength = hdMapA[slot].y;
    }
    float sun = 1.0;
    if (hdOn(HD_SHADOWS) && hdSunDirection.w > 0.5) sun = hdShadow(viewPos);
    vec3 reference = hdModelLight(n0, up, 1.0, 1.0);
    vec3 light = hdModelLight(n, up, occlusion, sun);
    // per channel: the model's light over its own light on the smooth normal in full sun, so where
    // nothing differs (no relief, no shadow, no occlusion) the game's colour is kept exactly
    // 1.08: shadow and occlusion now take light away where the game's flat light did not; the scene
    // keeps its overall brightness
    vec3 ratio = clamp(light / max(reference, vec3(0.05)) * 1.08, vec3(0.2), vec3(2.2));
    vec3 rgb = hdScaleLight(color.rgb, ratio);
    // point lights: the game added them on the smooth normal (hdAddPointLights); the difference on the
    // mapped normal is added here, so the relief catches torchlight too
    if (hdOn(HD_LIGHTS) && hdLightInfo.x > 0 && mapped) {
        rgb += color.rgb * (hdPointLights(viewPos, n) - hdPointLights(viewPos, n0)) * 0.5;
    }
    float gloss = mix(80.0, 4.0, roughness);
    if (strength > 0.0) {
        vec3 view = normalize(-viewPos);
        if (hdSunDirection.w > 0.5) {
            vec3 reflected = reflect(-hdSunDirection.xyz, n);
            rgb = hdAddHighlight(rgb, hdSunColour.rgb * pow(max(dot(view, reflected), 0.0), gloss) * (1.0 - roughness) * strength * sun);
        }
        rgb = hdAddHighlight(rgb, hdPointHighlights(viewPos, n, gloss) * (1.0 - roughness) * strength * 0.6);
    }
    if (mapped && hdMapB[slot].z > 0.5) {
        vec4 orm = texture(uHdOrmMaps, vec3(uv, layer));
        rgb += hdColour(texture(uHdEmissiveMaps, vec3(uv, hdMapB[slot].w))).rgb * orm.b * hdMapA[slot].z;
    }
    return vec4(rgb, color.a);
}

// The debug panel's overlays (hdDebug.x) on a 3D-view fragment. [texture] identifies the texture for
// the id overlay: the 634 texture id, or 4096 plus the retained scene's material slot, -1 for none;
// it is written exactly into red and green so the panel can read the id under the mouse.
// The occlusion the fragment was given (set by scene.frag and ff.frag before the overlay), for the
// occlusion overlay: baked into the vertex, read from the ground's texture, or measured per pixel.
float hdAppliedOcclusion = 1.0;

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
    if (mode == 6) return vec4(vec3(hdAppliedOcclusion), 1.0);
    if (mode == 7) return vec4(vec3(hdShadow(viewPos)), 1.0);
    if (mode == 8) {
        // the room fill per tile (HdLightGrid) over the dimmed scene, tile edges faintly
        // red where no level's floor is found at or below the fragment (hdLightFill)
        vec3 fill = hdOn(HD_LIGHT_GRID) ? hdLightFill(world) / max(hdGridParams.x, 0.01) : vec3(0.0);
        if (hdOn(HD_LIGHT_GRID) && hdLightFillLevel(world) < 0) fill = vec3(0.4, 0.0, 0.0);
        if (hdDebug.y == 1) {
            // raw: green the layer-0 fill x20, red the alpha's presence, blue the texture's width / 512
            // red: the level picked (0.2 per level, 0.9 none); green: the real fill x20; blue: layer-0 floor y / -1024
            int picked = hdLightFillLevel(world);
            vec3 real = hdLightFill(world);
            return vec4(picked < 0 ? 0.9 : 0.2 * float(picked + 1), (real.r + real.g + real.b) * 2.0, hdLightGridRaw(world, 0).a / -1024.0, 1.0);
        }
        vec2 tile = world.xz / 512.0;
        vec2 cell = fract(tile);
        vec2 cellWidth = fwidth(tile);
        float tileEdge = (cell.x < cellWidth.x || cell.y < cellWidth.y) ? 0.25 : 0.0;
        return vec4(mix(color.rgb * 0.25 + fill, vec3(1.0, 0.85, 0.0), tileEdge), 1.0);
    }
    return color;
}

// Caustics (HD_CAUSTICS): the bright, shifting web the water's surface focuses onto the ground under
// it, at world ([x], [z]): two layers of warped waves, sharpened into lines; 0 to about 1.
float hdCaustics(vec2 p, float t) {
    vec2 q = p * 0.0042;
    float total = 0.0;
    for (int i = 0; i < 2; i++) {
        float s = float(i) * 1.37 + 1.0;
        // each layer turned (about 37 degrees) and warped by its own waves, so no grid shows
        float angle = float(i) * 0.65;
        vec2 r = mat2(cos(angle), -sin(angle), sin(angle), cos(angle)) * q * s;
        vec2 w = r + vec2(sin(r.y * 1.3 + t * 0.7 * s), cos(r.x * 1.1 - t * 0.6 * s)) * 1.1;
        w += vec2(sin(w.y * 2.7 + t * 0.4), cos(w.x * 2.3 - t * 0.5)) * 0.35;
        float a = sin(w.x * 2.1 + t * 0.9) * sin(w.y * 2.4 - t * 0.8);
        total += pow(1.0 - abs(a), 7.0);
    }
    return total * 0.5;
}

// ---- 634HD's water surface (HdSettings.water 2): 117HD's water, recreated ----
// rs117/hd/utils/water.glsl (sampleWater, sampleUnderwater), utils/specular.glsl, the types of
// scene/water_types/WaterType.java (HdWaterTypes above), the reflection's gradient of
// renderer/SkyRenderer.java (updateGlobalUbo). The world's y points down here as there. Sums are made
// in sRGB terms as 117HD makes them, and the result made linear once (hdColour) for the scene.

// The depth (world units, 512 a tile) over which the seabed's tint goes to black and the surface goes
// opaque (117HD's 500 of its 128-unit tiles is about four tiles; the game's beds are real and shallow,
// a river a tile or so, so three), how deep the shore foam reaches, and how opaque deep water gets.
#define HD_WATER_DEPTH_RANGE 1536.0
#define HD_WATER_FOAM_DEPTH 192.0
#define HD_WATER_DEPTH_OPACITY 0.9
// A water model (a pond or pool that is a loc, not ground) has no vertex depth, no foam and no seabed
// pass: its depth colour is laid under the surface instead, and it goes this opaque at the least (the
// oasis over bright sand was a pale, flat sheet without it).
#define HD_WATER_MODEL_OPACITY 0.9
// How far the ripples bend the reflection, in the view's width per unit of the normal's lean.
#define HD_WATER_REFLECTION_BEND 0.3
// How much of what the surface reflects is the scene mirrored in it (HdReflections) where it was drawn;
// the rest is 117HD's own: the sky in the reflected direction, or its gradient. At 1 the water was a
// pane of glass with the trees in it; 117HD's water is the sky's colour, rippling.
#define HD_WATER_MIRROR_SHARE 0.5

int hdWaterType(int slot) {
    return slot >= 0 ? int(hdMapD[slot].w + 0.5) : 0;
}

// 117HD's worldUvs: a repeat every [tiles] tiles (512 units here, 128 there) of the world position
// ([xz] in the scene plus its base, as 117HD adds its sceneBase: the ripples stay put when the scene
// is rebuilt around a new base); the area's size scales it.
vec2 hdWaterWorldUv(vec2 xz, float tiles) {
    return -(xz + hdSceneSize.zw) / (512.0 * tiles * max(hdWaterParams.w, 0.25));
}

// 117HD's animationFrame: the phase of a slide that takes [seconds] (periods that divide the hour, so
// hdTime.x's hourly wrap passes unseen).
float hdWaterFrame(float seconds) {
    return seconds > 0.0 ? fract(hdTime.x / seconds) : 0.0;
}

// The surface's normal (world space) at world [xz] for water [type]: 117HD's three scrolled layers
// of its normal map (3, 5 and 7 tiles a repeat), bent by its flow map ([flow], for the foam too).
// The array is sRGB: the encoded values come back through hdToSrgb, as 117HD's linearToSrgb does.
vec3 hdWaterNormal(vec2 xz, int type, out vec2 flow) {
    float duration = hdWaterTime[type].x / max(hdWaterParams.y, 0.05);
    float strength = hdWaterSpec[type].z * hdWaterParams.x;
    vec2 flowUv = hdWaterWorldUv(xz, 15.0) + hdWaterFrame(50.0 * duration);
    flow = texture(uHdFlowMaps, vec3(flowUv, hdWaterMaps.z)).xy;
    vec2 uv1 = hdWaterWorldUv(xz, 3.0).yx - hdWaterFrame(30.0 * duration) + flow * 0.025;
    vec2 uv2 = hdWaterWorldUv(xz, 5.0) + hdWaterFrame(24.0 * duration) + flow * 0.025;
    vec2 uv3 = hdWaterWorldUv(xz, 7.0) + hdWaterFrame(20.0 * duration) + flow * 0.025;
    float layer = hdWaterMaps.x;
    vec3 n1 = hdToSrgb(texture(uHdFlowMaps, vec3(uv1, layer)).xyz);
    vec3 n2 = hdToSrgb(texture(uHdFlowMaps, vec3(uv2, layer)).xyz);
    vec3 n3 = hdToSrgb(texture(uHdFlowMaps, vec3(uv3, layer)).xyz);
    n1 = -vec3((n1.x * 2.0 - 1.0) * strength, n1.z, (n1.y * 2.0 - 1.0) * strength);
    n2 = -vec3((n2.x * 2.0 - 1.0) * strength, n2.z, (n2.y * 2.0 - 1.0) * strength);
    n3 = -vec3((n3.x * 2.0 - 1.0) * strength, n3.z, (n3.y * 2.0 - 1.0) * strength);
    return normalize(n1 + n2 + n3);
}

// What the surface reflects (sRGB) for a fresnel share [finalFresnel]: 117HD's gradient of the area's
// water colour (light, mid and dark: the colour at 0.8, 0.45 and 0.05, by the fresnel), with the scene
// mirrored in the water (HdReflections, the sky and the trees) mixed over it where it was drawn, bent
// by the normal. (117HD's own sky sampled in the reflected direction gave a pale water here: the
// gradient's sky is the saturated blue of its tint.) Not for a water [model]: the scene is mirrored
// about the ground's water plane, and a pond at its own height got the trees' reflection displaced
// over it (the swamp's willow, a lit patch at night).
// The gradient is the sky's colour by day; it takes the time of day's and the area's change in light
// (hdUnlitLight, as the game's water does), so a pond does not glow at night.
vec3 hdWaterReflection(vec3 viewDir, vec3 n, float finalFresnel, vec2 fragCoord, bool model) {
    vec3 tint = hdWaterTint.rgb / max(max(max(hdWaterTint.r, hdWaterTint.g), hdWaterTint.b), 0.001);
    vec3 own = finalFresnel < 0.5 ? mix(tint * 0.05, tint * 0.45, finalFresnel * 2.0) : mix(tint * 0.45, tint * 0.8, (finalFresnel - 0.5) * 2.0);
    if (hdOn(HD_ENV_LIGHT)) own *= hdUnlitLight(hdAmbientColour.rgb, hdSunColour.rgb);
    if (!model && hdOn(HD_REFLECTIONS) && uHdReflection.z > 0.0) {
        vec2 uv = clamp((fragCoord - uHdReflection.xy) / uHdReflection.zw + n.xz * HD_WATER_REFLECTION_BEND, vec2(0.001), vec2(0.999));
        vec4 mirrored = texture(uHdReflectionMap, uv);
        return mix(own, mirrored.rgb * finalFresnel, mirrored.a * HD_WATER_MIRROR_SHARE);
    }
    return own;
}

// 117HD's water surface for a water draw: [slot] the texture's material slot (-1: the default type),
// [viewPos] eye space, [vertexDepth] the vertex's water depth less one (0 at the shore) on the ground,
// below 0 for a water model (a fixed depth, no foam), [fragCoord] gl_FragCoord.xy. The surface in the
// scene's space with its own alpha (the caller fogs it, and the fog raises the alpha).
vec4 hdWaterSurface(int slot, vec3 viewPos, float vertexDepth, vec2 fragCoord) {
    int type = hdWaterType(slot);
    vec3 world = (hdViewToWorld * vec4(viewPos, 1.0)).xyz;
    vec3 viewDir = normalize(hdViewToWorld[3].xyz - world);
    vec2 flow;
    vec3 n = hdWaterNormal(world.xz, type, flow);
    // the sun (eye space in the frame block) in world space; the ripples' normal in eye space for the point lights
    vec3 sunDir = normalize(mat3(hdViewToWorld) * hdSunDirection.xyz);
    vec3 nEye = normalize(transpose(mat3(hdViewToWorld)) * n);
    bool sunUp = hdSunDirection.w > 0.5;
    float shadow = hdShadow(viewPos);
    vec3 ambient = hdEnvAmbientLight(hdAmbientColour).rgb;
    vec3 sunColour = sunUp ? hdEnvDiffuse(hdSunColour, 0).rgb * shadow : vec3(0.0);
    vec3 lightOut = max(dot(n, sunDir), 0.0) * sunColour;
    float gloss = hdWaterSpec[type].y;
    float specStrength = hdWaterSpec[type].x * hdWaterParams.z;
    vec3 spec = sunUp ? sunColour * pow(clamp(dot(viewDir, reflect(-sunDir, n)), 1e-10, 1.0), gloss) * specStrength * hdSunStrength() : vec3(0.0);
    vec3 pointOut = vec3(0.0);
    vec3 pointSpec = vec3(0.0);
    if (hdOn(HD_LIGHTS) && hdLightInfo.x > 0) {
        pointOut = hdPointLights(viewPos, nEye);
        pointSpec = hdPointHighlights(viewPos, nEye, gloss) * specStrength;
    }
    vec3 skyLight = max(-n.y, 0.0) * hdFogColour.rgb * 0.5;
    float fresnel = 1.0 - clamp(dot(viewDir, n), 0.0, 1.0);
    float finalFresnel = clamp(mix(0.4, 1.0, fresnel * 1.2), 0.0, 1.0);
    bool model = vertexDepth < 0.0;
    vec3 reflection = hdWaterReflection(viewDir, n, finalFresnel, fragCoord, model);
    if (hdDebug.x == 9) {
        // the water probe overlay: by screen third, the type's surface colour (and its opacity as alpha),
        // what the surface reflects, the ripples' normal; a type table that never arrived shows black
        float third = (fragCoord.x - hdViewport.x) * hdViewport.z * 3.0;
        if (third < 1.0) return vec4(hdWaterSurfaceColour[type].rgb, 1.0);
        if (third < 2.0) return vec4(reflection, 1.0);
        return vec4(n * 0.5 + 0.5, 1.0);
    }
    vec3 composite = ambient + lightOut + spec + skyLight + pointOut + pointSpec + reflection * max(specStrength, 0.2);
    // the type's colour, its hue pulled towards the area's tint by the tint's strength at the type's
    // own brightness (pulled towards the tint itself, 117HD's dark swamp water came out a mid green)
    vec3 typeColour = hdWaterSurfaceColour[type].rgb;
    float typeLuma = dot(typeColour, vec3(0.299, 0.587, 0.114));
    float tintLuma = max(dot(hdWaterTint.rgb, vec3(0.299, 0.587, 0.114)), 0.001);
    vec3 surfaceSrgb = mix(typeColour, hdWaterTint.rgb * (typeLuma / tintLuma), hdWaterTint.w);
    vec3 base = mix(surfaceSrgb * composite, reflection, hdWaterSpec[type].w);
    // 117HD darkens its plain water (the types with its WATER's fresnel amount) by a quarter
    if (hdWaterSpec[type].w > 0.84 && hdWaterSpec[type].w < 0.86) base *= 0.75;
    // foam at the shore: where the vertex depth is small (a model has no shore)
    float depth = max(vertexDepth, 0.0);
    float shore = model ? 0.0 : 1.0 - clamp(depth / HD_WATER_FOAM_DEPTH, 0.0, 1.0);
    vec2 foamUv = fract((world.xz + hdSceneSize.zw) / 512.0) + flow * 0.025;
    float foamMask = texture(uHdFlowMaps, vec3(foamUv, hdWaterMaps.y)).r;
    vec3 foamColour = hdWaterFoamColour[type].rgb * foamMask * composite;
    float foamAmount = clamp(pow(max(1.0 - (1.0 - min(shore, 0.8)) / 0.7, 0.0), 3.0), 0.0, 1.0) * hdWaterFoamColour[type].w * foamColour.r;
    base = mix(base, foamColour, foamAmount);
    vec3 specComposite = mix(spec, vec3(0.0), foamAmount);
    finalFresnel = max(finalFresnel, 1.0 - dot(viewDir, vec3(0.0, -1.0, 0.0)));
    base += pointSpec + spec / 3.0;
    float alpha = max(hdWaterSurfaceColour[type].w, max(foamAmount, max(finalFresnel, length(specComposite / 3.0))));
    // deeper water goes opaque (634HD's own: 117HD leaves it to the darkened bed)
    alpha = max(alpha, HD_WATER_DEPTH_OPACITY * smoothstep(0.3, 1.0, clamp(depth / HD_WATER_DEPTH_RANGE, 0.0, 1.0)));
    // the depth colour laid under a flat type or a model takes the time of day's light too
    vec3 under = hdWaterDepthColour[type].rgb * (hdOn(HD_ENV_LIGHT) ? hdUnlitLight(hdAmbientColour.rgb, hdSunColour.rgb) : vec3(1.0));
    if (hdWaterDepthColour[type].w > 0.5) {
        base = mix(under, base, alpha);
        alpha = 1.0;
    } else if (model) {
        base = mix(under, base, alpha);
        alpha = max(alpha, HD_WATER_MODEL_OPACITY);
    }
    return vec4(hdColour(vec4(base, 1.0)).rgb, alpha);
}

// The seabed under 634HD's surface (117HD's sampleUnderwater): [color] (the scene's space) tinted by
// the type's depth colour over the first 30% of the depth range, then towards black. (The seabed's
// draw carries its own texture's slot, not the water's: the default type's depth colour.)
vec3 hdWaterBed(int slot, vec3 color, float vertexDepth) {
    int type = hdWaterType(slot);
    float t = clamp(max(vertexDepth, 0.0) / HD_WATER_DEPTH_RANGE, 0.0, 1.0);
    vec3 depthColour = hdColour(vec4(hdWaterDepthColour[type].rgb, 1.0)).rgb;
    vec3 f = t < 0.3 ? mix(vec3(1.0), depthColour, t / 0.3) : mix(depthColour, vec3(0.0), (t - 0.3) / 0.7);
    return color * f;
}

