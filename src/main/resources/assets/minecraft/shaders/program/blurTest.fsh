#version 150

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 BlurDir;
uniform float Radius;

void main() {
    vec4 blurred = vec4(.0);
    float roughCalc = Radius * 2.0 + 1.0;
    for (float r = -Radius; r <= Radius; r += 1.0) {
        vec4 mainTexture = texture(DiffuseSampler, texCoord + oneTexel * r * BlurDir);
        blurred += mainTexture;
    }   
    gl_FragColor = vec4(blurred / roughCalc);
}