#version 150

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform ThermalConfig {
    float Intensity;
};

out vec4 fragColor;

void main() {
    vec4 InTexel = texture(InSampler, texCoord);
    
    // Calculate brightness (heat)
    float heat = dot(InTexel.rgb, vec3(0.299, 0.587, 0.114));
    
    // Thermal color mapping: cold (dark) -> blue, medium -> green/yellow, hot (bright) -> red
    vec3 thermal;
    if (heat < 0.33) {
        // Cold: blue to cyan
        float t = heat / 0.33;
        thermal = mix(vec3(0.0, 0.0, 0.5), vec3(0.0, 0.5, 0.5), t);
    } else if (heat < 0.66) {
        // Medium: cyan to yellow
        float t = (heat - 0.33) / 0.33;
        thermal = mix(vec3(0.0, 0.5, 0.5), vec3(1.0, 1.0, 0.0), t);
    } else {
        // Hot: yellow to red
        float t = (heat - 0.66) / 0.34;
        thermal = mix(vec3(1.0, 1.0, 0.0), vec3(1.0, 0.0, 0.0), t);
    }
    
    // Mix between original and thermal
    vec3 finalColor = mix(InTexel.rgb, thermal, Intensity);
    
    fragColor = vec4(clamp(finalColor, 0.0, 1.0), InTexel.a);
}







