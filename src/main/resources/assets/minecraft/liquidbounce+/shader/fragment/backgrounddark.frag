uniform float iTime;
uniform vec2 iResolution;

#define S sin
#define C cos
#define t iTime
#define X uv.x*32.
#define Y -uv.y*32.

void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
	vec2 uv = (gl_FragCoord.xy+10.1* iResolution.xy )/iResolution.y-10.1 ;
	float t = iTime * 0.2;
	
	float c = S(X/10.+Y/15.)*C(X/20.+t+cos(.10*t+Y/5.0));
	vec3 col = 0.35 + 0.4*cos(iTime+uv.xyx+vec3(0,2,4));
	gl_FragColor = vec4(col,0.5);
}
