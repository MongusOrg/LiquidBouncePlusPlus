#version 150

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 BlurDir;
uniform float Radius;

void main() {
    vec3 blurred = vec3(.0);
    float roughCalc = Radius * 2.0 + 1.0;
    float totalAlpha = 0.0;
    float totalWeight = 0.0;
    for (float r = -Radius; r <= Radius; r += 1.0) {
        vec4 mainTexture = texture(DiffuseSampler, texCoord + oneTexel * r * BlurDir);
        blurred += mainTexture.rgb;
        totalAlpha += mainTexture.a;
        totalWeight += 1.0;
    }   
    gl_FragColor = vec4(blurred.r / totalWeight, blurred.g / totalWeight, blurred.b / totalWeight, totalAlpha / roughCalc);
}