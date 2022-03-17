#version 150

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 BlurDir;
uniform float Radius;

void main() {
    vec3 blurred = vec3(.0);
    float totalAlpha = 0.0;
    float roughCalc = Radius * 2.0 + 1.0;
    for (float r = -Radius; r <= Radius; r += 1.0) {
        vec4 mainTexture = texture(DiffuseSampler, texCoord + oneTexel * r * BlurDir);
        blurred += mainTexture.rgb;
        totalAlpha += mainTexture.a;
    }   
    gl_FragColor = vec4(blurred, totalAlpha / roughCalc);
} // i think i fixed the shader, big thanks to https://stackoverflow.com/questions/35476142/gaussian-blur-handle-with-alpha-transparency