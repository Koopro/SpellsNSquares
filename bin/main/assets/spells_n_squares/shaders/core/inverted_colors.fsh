#version 150

in vec4 vertexColor;
in vec2 texCoord0;

uniform sampler2D Sampler0;
uniform float Intensity;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    
    // Invert colors: 1.0 - color
    vec3 inverted = vec3(1.0) - color.rgb;
    
    // Mix between original and inverted based on intensity
    vec3 finalColor = mix(color.rgb, inverted, Intensity);
    
    fragColor = vec4(finalColor, color.a);
}












