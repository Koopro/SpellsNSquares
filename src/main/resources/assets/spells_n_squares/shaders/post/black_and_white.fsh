#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform BlackAndWhiteConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Convert to grayscale using luminance formula
    float gray = dot(InTexel.rgb, vec3(0.299, 0.587, 0.114));
    
    // Apply high contrast for true black and white effect
    // This creates a more dramatic black and white look
    gray = pow(gray, 1.2); // Slight gamma adjustment
    gray = smoothstep(0.3, 0.7, gray); // Increase contrast
    
    // Mix between original and black and white based on intensity
    vec3 finalColor = mix(InTexel.rgb, vec3(gray), Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}







