#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform MatrixConfig {
    float Intensity;
    float Time;
};

out vec4 fragColor;

// Random function for matrix rain
float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Convert to green monochrome (matrix style)
    float gray = dot(InTexel.rgb, vec3(0.299, 0.587, 0.114));
    vec3 green = vec3(0.0, gray * 1.2, gray * 0.3);
    
    // Matrix rain effect
    float rainSpeed = Time * 0.5;
    float rainY = mod(texCoord.y + rainSpeed, 1.0);
    float rainValue = random(vec2(floor(texCoord.x * 50.0), floor(rainY * 100.0)));
    
    // Create falling characters effect
    float charBrightness = step(0.7, rainValue);
    green += vec3(0.0, charBrightness * 0.3, charBrightness * 0.1);
    
    // Digital glitch overlay
    float glitch = random(vec2(Time * 10.0, texCoord.y * 100.0));
    if (glitch > 0.98) {
        green = vec3(0.0, 1.0, 0.3);
    }
    
    // Mix between original and matrix effect
    vec3 finalColor = mix(InTexel.rgb, green, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}


