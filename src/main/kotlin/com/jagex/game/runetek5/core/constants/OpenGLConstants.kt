package com.jagex.game.runetek5.core.constants

object OpenGLConstants {

	const val GL_POINTS: Int = 0x0

	const val GL_LINES: Int = 0x1

	const val GL_ONE: Int = 0x1

	const val GL_LINE_LOOP: Int = 0x2

	const val GL_TRIANGLES: Int = 0x4

	const val GL_TRIANGLE_FAN: Int = 0x6

	const val GL_QUADS: Int = 0x7

	const val GL_DEPTH_BUFFER_BIT: Int = 0x100

	const val GL_LEQUAL: Int = 0x203

	const val GL_GREATER: Int = 0x204

	const val GL_SRC_COLOR: Int = 0x300

	const val GL_ONE_MINUS_SRC_COLOR: Int = 0x301

	const val GL_SRC_ALPHA: Int = 0x302

	const val GL_ONE_MINUS_SRC_ALPHA: Int = 0x303

	const val GL_DST_COLOR: Int = 0x306

	const val GL_FRONT: Int = 0x404

	const val GL_BACK: Int = 0x405

	const val GL_VIEWPORT_BIT: Int = 0x800

	const val GL_POINT_SMOOTH: Int = 0xB10

	const val GL_POINT_SIZE_RANGE: Int = 0xB12

	const val GL_CULL_FACE: Int = 0xB44

	const val GL_LIGHTING: Int = 0xB50

	const val GL_LIGHT_MODEL_AMBIENT: Int = 0xB53

	const val GL_COLOR_MATERIAL: Int = 0xB57

	const val GL_FOG: Int = 0xB60

	const val GL_FOG_DENSITY: Int = 0xB62

	const val GL_FOG_START: Int = 0xB63

	const val GL_FOG_END: Int = 0xB64

	const val GL_FOG_MODE: Int = 0xB65

	const val GL_FOG_COLOR: Int = 0xB66

	const val GL_DEPTH_TEST: Int = 0xB71

	const val GL_MODELVIEW_MATRIX: Int = 0xBA6

	const val GL_ALPHA_TEST: Int = 0xBC0

	const val GL_BLEND: Int = 0xBE2

	const val GL_SCISSOR_TEST: Int = 0xC11

	const val GL_FOG_HINT: Int = 0xC54

	const val GL_TEXTURE_GEN_S: Int = 0xC60

	const val GL_TEXTURE_GEN_T: Int = 0xC61

	const val GL_TEXTURE_GEN_R: Int = 0xC62

	const val GL_TEXTURE_GEN_Q: Int = 0xC63

	const val GL_UNPACK_ROW_LENGTH: Int = 0xCF2

	const val GL_UNPACK_ALIGNMENT: Int = 0xCF5

	const val GL_RED_SCALE: Int = 0xD14

	const val GL_RED_BIAS: Int = 0xD15

	const val GL_GREEN_SCALE: Int = 0xD18

	const val GL_GREEN_BIAS: Int = 0xD19

	const val GL_BLUE_SCALE: Int = 0xD1A

	const val GL_BLUE_BIAS: Int = 0xD1B

	const val GL_TEXTURE_2D: Int = 0xDE1

	const val GL_FASTEST: Int = 0x1101

	const val GL_AMBIENT: Int = 0x1200

	const val GL_DIFFUSE: Int = 0x1201

	const val GL_POSITION: Int = 0x1203

	const val GL_CONSTANT_ATTENUATION: Int = 0x1207

	const val GL_LINEAR_ATTENUATION: Int = 0x1208

	const val GL_QUADRATIC_ATTENUATION: Int = 0x1209

	const val GL_COMPILE: Int = 0x1300

	const val GL_UNSIGNED_BYTE: Int = 0x1401

	const val GL_FLOAT: Int = 0x1406

	const val GL_AMBIENT_AND_DIFFUSE: Int = 0x1602

	const val GL_MODELVIEW: Int = 0x1700

	const val GL_PROJECTION: Int = 0x1701

	const val GL_TEXTURE: Int = 0x1702

	const val GL_COLOR: Int = 0x1800

	const val GL_ALPHA: Int = 0x1906

	const val GL_RGBA: Int = 0x1908

	const val GL_FILL: Int = 0x1B02

	const val GL_SMOOTH: Int = 0x1D01

	const val GL_VENDOR: Int = 0x1F00

	const val GL_RENDERER: Int = 0x1F01

	const val GL_VERSION: Int = 0x1F02

	const val GL_S: Int = 0x2000

	const val GL_T: Int = 0x2001

	const val GL_R: Int = 0x2002

	const val GL_Q: Int = 0x2003

	const val GL_MODULATE: Int = 0x2100

	const val GL_TEXTURE_ENV_MODE: Int = 0x2200

	const val GL_TEXTURE_ENV_COLOR: Int = 0x2201

	const val GL_TEXTURE_ENV: Int = 0x2300

	const val GL_EYE_LINEAR: Int = 0x2400

	const val GL_OBJECT_LINEAR: Int = 0x2401

	const val GL_TEXTURE_GEN_MODE: Int = 0x2500

	const val GL_OBJECT_PLANE: Int = 0x2501

	const val GL_EYE_PLANE: Int = 0x2502

	const val GL_NEAREST: Int = 0x2600

	const val GL_LINEAR: Int = 0x2601

	const val GL_NEAREST_MIPMAP_NEAREST: Int = 0x2700

	const val GL_LINEAR_MIPMAP_LINEAR: Int = 0x2703

	const val GL_TEXTURE_MAG_FILTER: Int = 0x2800

	const val GL_TEXTURE_MIN_FILTER: Int = 0x2801

	const val GL_TEXTURE_WRAP_S: Int = 0x2802

	const val GL_TEXTURE_WRAP_T: Int = 0x2803

	const val GL_REPEAT: Int = 0x2901

	const val GL_LIGHT0: Int = 0x4000

	const val GL_COLOR_BUFFER_BIT: Int = 0x4000

	const val GL_LIGHT1: Int = 0x4001

	const val GL_LIGHT2: Int = 0x4002

	const val GL_VERTEX_ARRAY: Int = 0x8074

	const val GL_NORMAL_ARRAY: Int = 0x8075

	const val GL_COLOR_ARRAY: Int = 0x8076

	const val GL_TEXTURE_COORD_ARRAY: Int = 0x8078

	const val GL_BGRA: Int = 0x80E1

	const val GL_TEXTURE0: Int = 0x84C0

	const val GL_TEXTURE1: Int = 0x84C1

	const val GL_MAX_TEXTURE_UNITS: Int = 0x84E2

	const val GL_NORMAL_MAP: Int = 0x8511

	const val GL_REFLECTION_MAP: Int = 0x8512

	const val GL_TEXTURE_CUBE_MAP_POSITIVE_X: Int = 0x8515

	const val GL_COMBINE: Int = 0x8570

	const val GL_COMBINE_RGB: Int = 0x8571

	const val GL_COMBINE_ALPHA: Int = 0x8572

	const val GL_RGB_SCALE: Int = 0x8573

	const val GL_CONSTANT: Int = 0x8576

	const val GL_PREVIOUS: Int = 0x8578

	const val GL_SRC0_RGB: Int = 0x8580

	const val GL_SRC2_RGB: Int = 0x8582

	const val GL_SRC0_ALPHA: Int = 0x8588

	const val GL_SRC2_ALPHA: Int = 0x858A

	const val GL_OPERAND0_RGB: Int = 0x8590

	const val GL_OPERAND0_ALPHA: Int = 0x8598

	const val GL_VERTEX_PROGRAM_ARB: Int = 0x8620

	const val GL_PROGRAM_ERROR_POSITION_ARB: Int = 0x864B

	const val GL_FRAGMENT_PROGRAM_ARB: Int = 0x8804

	const val GL_RGBA16F: Int = 0x881A

	const val GL_MAX_TEXTURE_COORDS: Int = 0x8871

	const val GL_MAX_TEXTURE_IMAGE_UNITS: Int = 0x8872

	const val GL_PROGRAM_FORMAT_ASCII_ARB: Int = 0x8875

	const val GL_ARRAY_BUFFER: Int = 0x8892

	const val GL_ELEMENT_ARRAY_BUFFER: Int = 0x8893

	const val GL_COMPILE_STATUS: Int = 0x8B81

	const val GL_INFO_LOG_LENGTH: Int = 0x8B84

	const val GL_READ_FRAMEBUFFER: Int = 0x8CA8

	const val GL_DRAW_FRAMEBUFFER: Int = 0x8CA9

	const val GL_FRAMEBUFFER_COMPLETE: Int = 0x8CD5

	const val GL_FRAMEBUFFER: Int = 0x8D40

	const val GL_RENDERBUFFER: Int = 0x8D41

	// Not part of the original client: used by the OpenGL anti-aliasing pipeline (client.msaa).
	const val GL_SAMPLE_ALPHA_TO_COVERAGE: Int = 0x809E
	const val GL_MAX_SAMPLES: Int = 0x8D57
	const val GL_DEPTH_COMPONENT: Int = 0x1902
}
