#version 150

in vec4 vertexColor;
in vec2 texCoord0;

uniform sampler2D Sampler0;
uniform float Intensity;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    
    // Convert to grayscale using luminance weights
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    vec3 grayscale = vec3(gray);
    
    // Mix between original and grayscale based on intensity
    vec3 finalColor = mix(color.rgb, grayscale, Intensity);
    
    fragColor = vec4(finalColor, color.a);
}












