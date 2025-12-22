#version 150

in vec2 texCoord0;

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;
uniform float Intensity;

out vec4 fragColor;

void main() {
    // Sample the current rendered frame
    vec4 color = texture(PrevSampler, texCoord0);
    
    // Convert to grayscale using luminance weights
    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
    vec3 grayscale = vec3(gray);
    
    // Mix between original and grayscale based on intensity
    vec3 finalColor = mix(color.rgb, grayscale, Intensity);
    
    fragColor = vec4(finalColor, color.a);
}









