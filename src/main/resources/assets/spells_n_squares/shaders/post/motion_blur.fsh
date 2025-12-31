#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform MotionBlurConfig {
    float Intensity;
    float DirectionX;
    float DirectionY;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Motion blur direction
    vec2 direction = normalize(vec2(DirectionX, DirectionY));
    float blurAmount = Intensity * 0.05;
    
    // Sample along motion direction
    vec3 blurred = vec3(0.0);
    float sampleCount = 0.0;
    
    for (float i = -5.0; i <= 5.0; i += 1.0) {
        vec2 offset = direction * blurAmount * i;
        vec3 sample = texture(InSampler, clamp(texCoord + offset, vec2(0.0), vec2(1.0))).rgb;
        blurred += sample;
        sampleCount += 1.0;
    }
    
    blurred /= sampleCount;
    
    // Mix between original and motion blurred
    vec3 finalColor = mix(InTexel.rgb, blurred, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}







