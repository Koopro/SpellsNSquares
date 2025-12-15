#version 150

in vec4 vColor;
in vec2 vUV;

uniform float Time;

out vec4 fragColor;

void main() {
    // Centered UV for radial gradient
    vec2 uv = vUV - vec2(0.5);
    float r = length(uv);

    // Subtle wobble using time
    float wobble = 0.05 * sin(Time * 0.8) + 0.04 * sin((uv.x + uv.y + Time) * 2.3);
    float radius = 0.45 + wobble;

    // Smooth falloff
    float edge = smoothstep(radius, radius - 0.2, r);

    // Core stays white, rim uses incoming color
    vec3 core = vec3(1.0);
    vec3 rim = vColor.rgb;
    float mixAmt = clamp(r / max(radius, 0.0001), 0.0, 1.0);
    vec3 color = mix(core, rim, mixAmt);

    float alpha = (1.0 - edge) * vColor.a;
    if (alpha <= 0.01) discard;

    fragColor = vec4(color, alpha);
}

