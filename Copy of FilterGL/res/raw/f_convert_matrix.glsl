precision mediump float;
varying vec2 v_texCoord;
uniform sampler2D s_texture;

const mediump mat4 rgbToYuv = mat4( 0.257,  0.439,  -0.148, 0.06, 
                                    0.504, -0.368,  -0.291, 0.5,
                                    0.098, -0.071,   0.439, 0.5, 
                                     0.0,     0.0,     0.0, 1.0);

const mediump mat4 yuvToRgb = mat4( 1.164,  1.164,  1.164,  -0.07884, 
                                    2.018, -0.391,    0.0,  1.153216,
                                      0.0, -0.813,  1.596,  0.53866, 
                                      0.0,    0.0,    0.0,  1.0);

void main()
{
    lowp vec4 textureColor = texture2D(s_texture, v_texCoord);
    lowp vec4 rgbPixel;
    
//    lowp vec4 rgbPixel = yuvToRgb * textureColor;
 
	gl_FragColor = textureColor;
}