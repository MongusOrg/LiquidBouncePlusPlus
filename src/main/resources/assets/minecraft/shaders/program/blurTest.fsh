#version 150

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 BlurDir;
uniform vec2 ShadowCoord;
uniform vec2 ShadowWH;
uniform float Radius;

void main() {
    if (texCoord.x / oneTexel.x >= ShadowCoord.x - (Radius * 2) && texCoord.y / oneTexel.y >= ShadowCoord.y - (Radius * 2) && texCoord.x / oneTexel.x <= ShadowWH.x + (Radius * 2) && texCoord.y / oneTexel.y <= ShadowWH.y + (Radius * 2)) {
        vec3 blurred = vec3(.0);
        float totalAlpha = 0.0;
        float roughCalc = Radius * 2.0 + 1.0;
        for (float r = -Radius; r <= Radius; r += 1.0) {
            vec4 mainTexture = texture(DiffuseSampler, texCoord + oneTexel * r * BlurDir);
            blurred += mainTexture.rgb;
            totalAlpha += mainTexture.a;
        }   
        gl_FragColor = vec4(blurred, totalAlpha / roughCalc);
    } else {
        gl_FragColor = vec4(.0);
    }
} // i think i fixed the shader, big thanks to https://stackoverflow.com/questions/35476142/gaussian-blur-handle-with-alpha-transparency