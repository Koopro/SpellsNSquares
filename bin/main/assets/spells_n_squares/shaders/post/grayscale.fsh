#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform GrayscaleConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Convert to grayscale using luminance formula
    float gray = dot(InTexel.rgb, vec3(0.299, 0.587, 0.114));
    
    // Mix between original and grayscale based on intensity
    vec3 finalColor = mix(InTexel.rgb, vec3(gray), Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}







