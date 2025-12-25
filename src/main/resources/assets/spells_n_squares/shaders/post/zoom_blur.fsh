#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform ZoomBlurConfig {
    float Intensity;
    float CenterX;
    float CenterY;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Blur center point
    vec2 center = vec2(CenterX, CenterY);
    
    // Calculate direction from center
    vec2 dir = texCoord - center;
    float dist = length(dir);
    
    // Apply radial blur based on distance from center
    float blurAmount = Intensity * dist * 0.1;
    
    // Sample multiple points along the radial direction
    vec3 blurred = vec3(0.0);
    float sampleCount = 0.0;
    
    for (float i = 0.0; i <= 5.0; i += 1.0) {
        vec2 sampleCoord = texCoord + dir * blurAmount * (i / 5.0);
        sampleCoord = clamp(sampleCoord, vec2(0.0), vec2(1.0));
        blurred += texture(InSampler, sampleCoord).rgb;
        sampleCount += 1.0;
    }
    
    blurred /= sampleCount;
    
    // Mix between original and blurred based on intensity
    vec3 finalColor = mix(InTexel.rgb, blurred, Intensity);
    
    fragColor = vec4(finalColor, InTexel.a);
}


