package jatek.engineteszt;

import CSNC.BuiltInResources.*;
import CSNC.FWmodel.*;
import CSNC.FWmodel.Sprite.*;
import CSNC.FWrender.*;
import CSNC.FWrender.Render.*;
import CSNC.FWrender.Texture.*;
import CSNC.FWrender.VertexArray.*;

/**
 *
 * @author caiwan
 */
public class Teszt {
    private static final int FBO_SIZE = 2048;
    private static final String RESOURCE_ROOT = "/textures/";
    
    private static final String vertexShader =
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
                "ecPos = gl_ModelViewMatrix * gl_Vertex;\n"+
                "lcPos = gl_ModelViewMatrix * lightPosition;\n"+
                
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
    
    private static final String fragmentShader = 
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

            //"float lambertTerm = dot(N,L);\n"+	//normal lambert
            "float lambertTerm = .5*(1.+dot(N,L));\n"+	//half lambert
            "float blinn = dot(R,E);\n"+
            
            "if(lambertTerm > 0.0)\n"+
            "{\n"+
                "final_color += diffuse * lambertTerm; \n"+
                "if (blinn>0.0)\n"+
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
            
    private static final String hdrVertexShader = ""+
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
    
    private static final String hdrFramgnetShader = ""+
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
    private static final String szovegFramgnetShader = ""+
            "#version 120\n"+
            "uniform sampler2D tex0;"+
            "void main(){"+
                "vec4 sample = texture2D(tex0, gl_TexCoord[0].st);"+
                "gl_FragColor.rgb = vec3(.4,0,0) * sample.r + vec3(1,1,1) * sample.b;"+
                "gl_FragColor.a = sample.r;"+
            "}"+
            "";
    
    private Camera camera;
    private VertexArray kocka;
    private Texture textura, betukeszlet;
    private Sprite fbo_sprite;
    private FBO fbo;
    private Shader shader, hdrKorrekcio, szovegShader;
    private Text probaSzoveg;
    
    public Teszt(){}
    
    public void init() throws Exception{
        this.camera = new Camera();
        
        boolean usevbo = true;
        
        this.kocka = new VertexArray();
        this.kocka.setDrawType(VertexArray.VBODrawModeE.VD_TRIANGLES);
        this.kocka.setVertexPointer(CubeData.cube_vertices_buffer, 3, CubeData.cube_vertex_length, usevbo);
        this.kocka.setNormalPointer(CubeData.cube_normal_buffer, 3, CubeData.cube_vertex_length, usevbo);
        this.kocka.setTexturePointer(CubeData.cube_texture_buffer, 2, CubeData.cube_vertex_length, usevbo);
        
        this.textura = new Texture();
        this.textura.setIsGenerateMipmap(true);
        this.textura.setMinFilter(TextureFilterE.TF_LINEAR_MIPMAP_LINEAR);
        if (this.textura.buildFromResource(RESOURCE_ROOT + "ir_duck.png") != 0) 
            throw new Exception("Nem sikerult a texturamat betolteni :(");
        
       this.shader = new Shader();
       int res = this.shader.createShader(Teszt.vertexShader, Teszt.fragmentShader);
       if (res != 0)
           throw new Exception("Nem sikerult a shaderemet betolteni :(");
       
       this.fbo = new FBO(2, FBO_SIZE, true);
       this.fbo_sprite = new Sprite();
       this.fbo_sprite.setTexture(0, this.fbo.getColorTexture(0));
       //this.fbo_sprite.setTexture(0, this.textura);
       this.fbo_sprite.setupFullscreenQuad(FBO_SIZE);
       
       this.hdrKorrekcio = new Shader();
       this.hdrKorrekcio.createShader(hdrVertexShader, hdrFramgnetShader);
       
       this.betukeszlet = new Texture();
       this.betukeszlet.setMinFilter(TextureFilterE.TF_NEAREST);
       this.betukeszlet.setMagFilter(TextureFilterE.TF_NEAREST);
       
       this.betukeszlet.buildFromResource(Teszt.RESOURCE_ROOT + "fontset.png");
       
       this.probaSzoveg = new Text();
       this.probaSzoveg.setTexture(0,betukeszlet);
       this.probaSzoveg.setSize(.1f, .1f);
       this.probaSzoveg.setOrignAlignmentMode(VerticalAlignmentE.VA_MIDDLE, Sprite.HorizontalAlignemntE.HA_CETER);
       this.probaSzoveg.setPositionAlignmentMode(VerticalAlignmentE.VA_MIDDLE, Sprite.HorizontalAlignemntE.HA_CETER);
       
       this.szovegShader = new Shader();
       this.szovegShader.createShader(hdrVertexShader, szovegFramgnetShader);
    }
    
    public void mainloop(float time) throws Exception{
        //camera.setFOV(Camera.DEFAULT_FOV + 15f*(float)Math.sin(time*.001));
        float r = 2.5f;
        float s = 5.f;
        camera.setCenter(0f, 0f, 0f);
        camera.setEye(r*(float)Math.sin(time/1000), r*(float)Math.cos(time/1000), s*(float)Math.cos(time/1000));
        
        fbo.bind();
       
        //Render.applyView();
        
        camera.projectScene();
        camera.lookAtScene();
        
        Render.clearScene(.0f, .1f, .2f, 1.f);
        
        Render.switchBlend(true);
        Render.switchTextue2D(true);
        
        this.shader.bind();
        this.textura.bind();
        this.kocka.bindAll();
        
        this.shader.seti("tex0", 0);
        this.shader.setf("shiniess",      10.f);
        this.shader.setf("shiningLevel",  50.f); 
        this.shader.setf("correctionCurve", 0.f);
        this.shader.setf("ambientGlobal", 0f, 0f, 0f, 1f);
        this.shader.setf("ambient",       .2f, .2f, .2f, 1f);
        this.shader.setf("diffuse",       .2f,  .2f,  .4f, 1f);
        this.shader.setf("specular",      .4f,  .4f,  .1f, 1f);
        this.shader.setf("lightPosition", 0f, 0f, 5f, 1f);
        
        this.kocka.drawByIndex(CubeData.cube_indices_buffer, CubeData.cube_indices_length);
        
        this.kocka.unbindAll();
        this.textura.unbind();
        this.shader.unbind();
        
        fbo.unbind();
        
        Render.clearScene(.0f, .1f, .2f, 1.f);
        
        this.hdrKorrekcio.bind();
        
        this.hdrKorrekcio.setf("exposure", 3.f);
        this.hdrKorrekcio.setf("bloomLevel", 0.7f);
        
        this.fbo_sprite.draw();
        
        this.hdrKorrekcio.unbind();
        
        this.szovegShader.bind();
        
        Render.setBlend(BlendingConditionE.BC_SRC_ALPHA, BlendingConditionE.BC_ONE_MINUS_SRC_ALPHA);
        
        this.probaSzoveg.setText("Hello world!");
        this.probaSzoveg.draw();
        
        Render.setBlend(BlendingConditionE.BC_ONE, BlendingConditionE.BC_ZERO);
        Render.switchBlend(false);
        
        this.szovegShader.unbind();
    }
}
