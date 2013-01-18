precision mediump float;
varying vec2 v_texCoord;
uniform sampler2D s_texture;

const mediump float u_fInverseWidth = 0.0020833;

void main()
{
    vec2 v2YTexCoord;
	v2YTexCoord.s = v_texCoord.s;
	v2YTexCoord.t = v_texCoord.t * 2.0 / 3.0;
	float fY = texture2D(s_texture, v2YTexCoord).r;
	
	vec2 v2UTexCoord;
	v2UTexCoord.s = floor(v_texCoord.s / 2.0) * 2.0 + u_fInverseWidth;
	v2UTexCoord.t = v_texCoord.t * 1.0 / 3.0 + 2.0 / 3.0;
	float fU = texture2D(s_texture, v2UTexCoord).r;
	
	vec2 v2VTexCoord;
	v2VTexCoord.s = floor(v_texCoord.s / 2.0) * 2.0;
	v2VTexCoord.t = v_texCoord.t * 1.0 / 3.0 + 2.0 / 3.0;
	float fV = texture2D(s_texture, v2VTexCoord).r;
	
	fY=1.1643*(fY-0.0625);
	fU=fU-0.5;
	fV=fV-0.5;
	
	float r=fY+1.5958*fV;
	float g=fY-0.39173*fU-0.81290*fV;
	float b=fY+2.017*fU;
	
	gl_FragColor = vec4(r,g,b,1.0);
    
	//lowp vec4 textureColor = texture2D(s_texture, v_texCoord);
 
	//gl_FragColor = textureColor;
}