#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec2 UV1;
in vec2 UV2;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec4 ColorModulator;

out vec4 vColor;
out vec2 vUV;

void main() {
    vColor = Color * ColorModulator;
    vUV = UV0;
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}









