#version 150

in vec2 texCoord0;

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;
uniform float Intensity;

out vec4 fragColor;

void main() {
    // Sample the current rendered frame
    vec4 color = texture(PrevSampler, texCoord0);
    
    // Invert colors: 1.0 - color
    vec3 inverted = vec3(1.0) - color.rgb;
    
    // Mix between original and inverted based on intensity
    vec3 finalColor = mix(color.rgb, inverted, Intensity);
    
    fragColor = vec4(finalColor, color.a);
}









