/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jatek.komponensek;

/**
 *
 * @author caiwan
 */
public class Data {
    ////////////////////////////////////////////////////////////////////////////
    // ELERESI UTVONALAK
    ////////////////////////////////////////////////////////////////////////////    
    
    public static final String TEXTURE_ROOT = "/textures/";
    public static final String SFX_ROOT = "sfx/";
    public static final String ZAK_ROOT = "sfx/music/";
    public static final String DATA_ROOT = "./data/";
    
    public static final String CONFIG_FILE = "config.xml";
    public static final String HIGHSCORE_FILE = "highscore.xml";
    public static final String LEVEL_FILE = "levels.xml";
    
    public static final String SOUND_CRSR = "Click.ogg";
    public static final String SOUND_SELECT = "Menu2.ogg";
    public static final String SOUND_MOVE = "Slide.ogg";
    public static final String SOUND_DIE = "GameOver3.ogg";
    public static final String SOUND_WIN = "NewWord.ogg";
    public static final String SOUND_GAME_OVER = "GameOver2.ogg";
    
    public static final String FONTSET1 = "fontset.png";
   
    ////////////////////////////////////////////////////////////////////////////
    // RENDER / KORNYEZET
    ////////////////////////////////////////////////////////////////////////////
    
    public static final int FBO_SIZE = 1024;
    public static final float backgroudColor[]  = {.0f, .1f, .2f};
    public static final float backgroudColor2[] = {.0f, .4f, .6f};
    public static final float textYPadding = .02f;
    public static final float textXPadding = -.4f;
    
    ////////////////////////////////////////////////////////////////////////////
    // SHADEREK
    ////////////////////////////////////////////////////////////////////////////
    
    public static final String vertexShader =
        "//file: basic_v2.vert\n"+
        "#version 120\n"+
        "uniform vec4 lightPosition, diffuse, ambient, ambientGlobal;\n"+
        "varying vec3 normal,lightDir,halfVector,eyeVec;\n"+
        "varying float dist;\n"+
            
        "void main()\n"+
        "{\n"+
                "vec4 ecPos, lcPos, ccPos;\n"+
                "vec3 aux;\n"+

                "normal = normalize(gl_NormalMatrix * gl_Normal);\n"+
                "ccPos = gl_ModelViewMatrix[3];"+
                //
                "ecPos = gl_ModelViewMatrix * gl_Vertex;\n"+
                //"lcPos = gl_ModelViewMatrix * lightPosition;\n"+
                "lcPos = lightPosition;\n"+
                
                "eyeVec = -normalize(ecPos).xyz;"+
                
                "aux = vec3(lcPos-ecPos);\n"+
                "lightDir = normalize(aux);\n"+
                "dist = length(aux);\n"+

                "halfVector = reflect(-lightDir, normal);\n"+
                //"halfVector = -lightDir;\n"+
            
                "gl_Position = ftransform();\n"+
                "gl_FrontColor = gl_Color;\n"+

                "gl_TexCoord[0]  = gl_TextureMatrix[0] * gl_MultiTexCoord0;\n"+
                "gl_TexCoord[1]  = gl_TextureMatrix[1] * gl_MultiTexCoord1;\n"+
                "gl_TexCoord[2]  = gl_TextureMatrix[2] * gl_MultiTexCoord2;\n"+
                "gl_TexCoord[3]  = gl_TextureMatrix[3] * gl_MultiTexCoord3;\n"+
                "gl_TexCoord[4]  = gl_TextureMatrix[4] * gl_MultiTexCoord4;\n"+
                "gl_TexCoord[5]  = gl_TextureMatrix[5] * gl_MultiTexCoord5;\n"+
                "gl_TexCoord[6]  = gl_TextureMatrix[6] * gl_MultiTexCoord6;\n"+
        "}\n";
    
    public static final String fragmentShader = 
        "//file: pointlight_v2.frag\n"+
        "#version 120\n"+
            "#define PI 3.14159265359\n"+
        "varying vec3 normal,lightDir,halfVector,eyeVec;\n"+
        "uniform float shiniess, shiningLevel, correctionCurve;"+
        "uniform vec4 specular, diffuse, ambient, ambientGlobal;\n"+
        "uniform sampler2D tex0;"+

        "vec4 cosKorrekcio(vec4 v, float level){"+
            "return mix(v, .5-.5*cos(v*PI), level);"+
        "}"+
            
        "vec4 expLevelKorrekcio(vec4 v, float level){"+
            "return 1-exp(-v*level);"+
        "}"+
            
        "void main (void)\n"+
        "{\n"+
            "vec4 final_color = ambientGlobal + ambient;\n"+
            
            "vec3 E = normalize(eyeVec);\n"+
            "vec3 N = normalize(normal);\n"+
            "vec3 L = normalize(lightDir);\n"+
            "vec3 R = normalize(halfVector);"+

            "float lambertTerm1 = dot(N,L);\n"+	//normal lambert
            "float lambertTerm = .5*(1.+dot(N,L));\n"+	//half lambert
            "float blinn = dot(R,E);\n"+
            
            "if(lambertTerm > 0.0)\n"+
            "{\n"+
                "final_color += diffuse * lambertTerm; \n"+
                "if ((lambertTerm1 > 0. ) && (blinn>0.0))\n"+
                    "final_color += specular * pow(blinn, shiniess) * shiningLevel;\n"+
                "\n"+
            "}\n"+
            "float alpha = texture2D(tex0, gl_TexCoord[0].st).a;"+
            "final_color = cosKorrekcio(expLevelKorrekcio(final_color * texture2D(tex0, gl_TexCoord[0].st), 1.), correctionCurve);"+
            "gl_FragData[0].rgb = final_color.rgb;\n"+
            //"gl_FragColor.rgb = final_color.rgb;\n"+
            //"gl_FragColor.rgb = (.5*N+.5).rgb;\n"+
            //"gl_FragColor.a = texture2D(tex0, gl_TexCoord[0].st).a;\n"+
            "gl_FragData[0].a = alpha;\n"+

    "}\n";
            
    public static final String hdrVertexShader = ""+
            "#version 120 \n"+
            "void main(){" +
            "gl_Position = ftransform();\n"+
                "gl_FrontColor = gl_Color;\n"+

                "gl_TexCoord[0]  = gl_TextureMatrix[0] * gl_MultiTexCoord0;\n"+
                "gl_TexCoord[1]  = gl_TextureMatrix[1] * gl_MultiTexCoord1;\n"+
                "gl_TexCoord[2]  = gl_TextureMatrix[2] * gl_MultiTexCoord2;\n"+
                "gl_TexCoord[3]  = gl_TextureMatrix[3] * gl_MultiTexCoord3;\n"+
                "gl_TexCoord[4]  = gl_TextureMatrix[4] * gl_MultiTexCoord4;\n"+
                "gl_TexCoord[5]  = gl_TextureMatrix[5] * gl_MultiTexCoord5;\n"+
                "gl_TexCoord[6]  = gl_TextureMatrix[6] * gl_MultiTexCoord6;\n"+
            "}";
    
    public static final String hdrFramgnetShader = ""+
            "#version 120 \n" +
            "uniform sampler2D tex0;" +
            
            "uniform float bloomLevel;"+
            "uniform float exposure;"+
   
            "vec4 hdrExpKorrekcioFelold(vec4 v){"+
                "return -log(1-v);"+
            "}"+
            
            // forras:
            // http://renderingwonders.wordpress.com/2011/01/25/chapter-09-%E2%80%93-advanced-buffers-beyond-the-basics-%E2%80%93-hdr-bloom-effect/
            
            "void main(){" +
                // Fetch from HDR texture & blur textures
                "vec4 baseImage = hdrExpKorrekcioFelold(texture(tex0, gl_TexCoord[0].st));"+

                // Four LoD levels are used from the mipmap
                "vec4 brightPass = textureLod (tex0, gl_TexCoord[0].st, 0);"+
                "vec4 blurColor1 = textureLod (tex0, gl_TexCoord[0].st, 1);"+
                "vec4 blurColor2 = textureLod (tex0, gl_TexCoord[0].st, 2);"+
                "vec4 blurColor3 = textureLod (tex0, gl_TexCoord[0].st, 3);"+
                "vec4 blurColor4 = textureLod (tex0, gl_TexCoord[0].st, 4);"+
                "vec4 blurColor5 = textureLod (tex0, gl_TexCoord[0].st, 5);"+
                "vec4 blurColor6 = textureLod (tex0, gl_TexCoord[0].st, 6);"+
                "vec4 blurColor7 = textureLod (tex0, gl_TexCoord[0].st, 7);"+
                "vec4 blurColor8 = textureLod (tex0, gl_TexCoord[0].st, 8);"+

                "vec4 bloom = brightPass + blurColor1 + blurColor2 + blurColor3 +"+
                    "blurColor4 + blurColor5 + blurColor6 + blurColor7 +"+
                    "blurColor8;"+
                "bloom *= 1./8.;"+
                "bloom = hdrExpKorrekcioFelold(bloom);"+

                "vec4 color = baseImage + bloomLevel * bloom;"+

                // Apply the exposure to this texel
                "gl_FragColor = 1.0 - exp2 (-color * exposure);"+
                "gl_FragColor.a = 1.0;"+
                //"gl_FragColor.rgb = texture2D(tex0, gl_TexCoord[0].st).rgb;" +
                //"gl_FragColor.a = 1.;" +
            "}";
    
    public static final String szovegFramgnetShader = ""+
            "#version 120\n"+
            "uniform sampler2D tex0;"+
            "void main(){"+
                "vec4 sample = texture2D(tex0, gl_TexCoord[0].st);"+
                //"gl_FragColor.rgb = gl_Color.rgb * (sample.b*(.5+.5*sample.g));"+
                "gl_FragColor.rgb = gl_Color.rgb * sample.b;"+
                "gl_FragColor.a = sample.r * gl_Color.a;"+
            "}"+
            "";
    
    public static final String hatterFramgnetShader = ""+
            "#version 120\n"+
            "uniform sampler2D tex0;"+
            "uniform vec3 color0;\n"+
            "uniform vec3 color1;\n"+
            "uniform float xshift;"+
            "uniform float yshift;"+
            "uniform vec2 resolution;\n"+ //= vec2(1000,500);"+
            "uniform float time;\n"+
            
            "\n#define BLADES 6.0\n"+
            "\n#define BIAS 0.1\n"+
            "\n#define SHARPNESS 3.0\n"+

            "float hash( float n ) { return fract(sin(n)*43758.5453); }"+

            "float noise( in vec2 x )"+
            "{"+
                    "vec2 p = floor(x);"+
                    "vec2 f = fract(x);"+
                    "f = f*f*(3.0-2.0*f);"+
                    "float n = p.x + p.y*57.0;"+
                    "float res = mix(mix(hash(n+0.0), hash(n+1.0),f.x), mix(hash(n+57.0), hash(n+58.0),f.x),f.y);"+
                    "return res;"+
            "}"+

            "float cloud(vec2 p) {"+
                    "float f = 0.0;"+
                    "f += 0.50000*noise(p* 1.0*10.0);"+
                    "f += 0.25000*noise(p* 2.0*10.0);"+
                    "f += 0.12500*noise(p* 4.0*10.0);"+
                    "f += 0.06250*noise(p* 8.0*10.0);"+
                    "f += 0.03125*noise(p*16.0*10.0);"+
                    "f += 0.01563*noise(p*32.0*10.0);"+
                    "f *= f;"+
                    "return f;"+
            "}"+
            
            "vec3 cloud(vec2 p, vec3 c0, vec3 c1) {"+
                "return mix(c0, c1, cloud(p));"+
            "}"+
            
            "float plasma(vec2 position, float time){"+
                "float color = 0.0;"+
                "color += sin(position.x*cos(time/10.0)*20.0 )+cos(position.x*cos(time/15.)*10.0 );"+
                "color += sin(position.y*sin(time/ 5.0)*15.0 )+cos(position.x*sin(time/25.)*20.0 );"+
                "color += sin(position.x*sin(time/10.0)*  .2 )+sin(position.y*sin(time/35.)*10.);"+
                "color *= sin(time/10.)*.5;"+
                "return 0.5+.5*sin(color*15.);"+
            "}"+
            
            "const float LAYERS	= 4.0;"+
            "const float SPEED	= 0.005;"+
            "const float SCALE	= 80.0;"+
            "const float DENSITY = 1.5;"+
            "const float BRIGHTNESS = 10.0;"+
            "vec2 ORIGIN = vec2(.5);"+

            "float rand(vec2 co){ return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453); }"+

            "void main( void ) {"+

                    "vec2   pos = (gl_FragCoord.xy/resolution.y) * ((1 - ORIGIN)) + vec2(xshift, yshift);"+
                    "float dist = length(pos) / resolution.y;"+
                    "vec2 coord = vec2(pow(dist, 0.1), atan(pos.x, pos.y) / (3.1415926*2.0));"+

                    // Nebulous cloud
                    //"vec3 color = cloud(pos/resolution, vec3(0,0,0), vec3(1,1,1));"+
                    
                    "float color = plasma(pos, time) * cloud(pos);"+
            
                    "gl_FragColor = vec4(mix(color0, color1, color), 1.0);"+
                //"gl_FragColor = vec4(1, 1, 0, 1.0);"+
            "}";

}
