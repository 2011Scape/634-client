#version 330 core
// Not part of the original client: 634HD (toolkit 6). A full-screen triangle for the post passes
// (HdPost): no vertex buffer, the vertex id makes the corners.

out vec2 vUv;

void main() {
    vec2 corner = vec2(float((gl_VertexID << 1) & 2), float(gl_VertexID & 2));
    vUv = corner;
    // at the far plane: the sky pass draws only where the depth buffer is still clear
    gl_Position = vec4(corner * 2.0 - 1.0, 1.0, 1.0);
}
