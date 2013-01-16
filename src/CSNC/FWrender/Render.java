/*
 * Generated with cpp2java.py by slapec^IR
 * (root)/trunk/engine/engine/source/graph/renderer.hpp - Rev 79
 */
package CSNC.FWrender;

/**
 * Ezek a függvének 1:1-be átemelve a c++-s kódból.
 * @author caiwan
 */

import java.nio.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

public final class Render {
//=================================================================================================
// Enumerators
//=================================================================================================
    /**
     * Polygonok shading modellje
     */
    public static enum ShadingModelE{
            SH_SMOOTH (GL11.GL_SMOOTH),
            SH_FLAT (GL11.GL_FLAT);

            private int glenum;
            private ShadingModelE(int _glenum){this.glenum = _glenum;}
            public int resolve(){return this.glenum;}
    };

    /**
     * Tesztelesi feltetelek (alpha illetve melyseg teszthez)
     * http://www.opengl.org/sdk/docs/man/xhtml/glDepthFunc.xml
     * http://www.opengl.org/sdk/docs/man/xhtml/glAlphaFunc.xml
     */
    public static enum TestingConditionE{	
            TC_NEVER (GL11.GL_NEVER),
            TC_LESS (GL11.GL_LESS),
            TC_EQUAL (GL11.GL_EQUAL),
            TC_LEQUAL(GL11.GL_LEQUAL),
            TC_GREATER (GL11.GL_GREATER),
            TC_NOTEQUAL (GL11.GL_NOTEQUAL),
            TC_GEQUAL (GL11.GL_GEQUAL),
            TC_ALWAYS (GL11.GL_ALWAYS);

            private int glenum;
            private TestingConditionE(int _glenum) {this.glenum = _glenum;}
            public int resolve(){return this.glenum;}
    };

    /**
     * Mixelesi modok. Milyen osszefugges alapjan szamolja ki az egymasra renderelt
     * retegeket.
     * http://www.opengl.org/sdk/docs/man/xhtml/glBlendFunc.xml
     */
    public static enum BlendingConditionE{
            BC_ZERO (GL11.GL_ZERO),
            BC_ONE (GL11.GL_ONE),
            BC_DST_COLOR (GL11.GL_DST_COLOR),
            BC_ONE_MINUS_DST_COLOR (GL11.GL_ONE_MINUS_DST_ALPHA),
            BC_SRC_ALPHA (GL11.GL_SRC_ALPHA),
            BC_ONE_MINUS_SRC_ALPHA (GL11.GL_ONE_MINUS_SRC_ALPHA),
            BC_DST_ALPHA (GL11.GL_DST_ALPHA),
            BC_ONE_MINUS_DST_ALPHA (GL11.GL_ONE_MINUS_DST_ALPHA),
            BC_SRC_ALPHA_SATURATE (GL11.GL_SRC_ALPHA_SATURATE);

            private int glenum;
            private BlendingConditionE (int _glenum) {this.glenum = _glenum;}
            public int resolve(){return this.glenum;}
    };
    
    /**
     * Matrix uzemmodok. Matrixok jelolhetok ki illetve modosithatok vele.
     */
    public static enum MatrixModeE {
            MM_PROJECTION (GL11.GL_PROJECTION, GL11.GL_PROJECTION_MATRIX),
            MM_MODELVIEW (GL11.GL_MODELVIEW, GL11.GL_MODELVIEW_MATRIX),
            MM_TEXTURE (GL11.GL_TEXTURE, GL11.GL_TEXTURE_MATRIX),;
            //MM_COLOR (GL11.GL_PROJECTION, GL11.GL_PROJECTION_MATRIX),,

            private int glenum, glmatrixmode;
            private MatrixModeE(int _glenum, int _glmatrixmode) {this.glenum = _glenum; this.glmatrixmode = _glmatrixmode;}
            public int resolve(){return this.glenum;}
            public int resolveMatrixMode(){return this.glmatrixmode;}
    };
   
    /**
     * Adat illetve buffer tomb tipusok.
     */
    public static enum DataTypeE {
            DT_UNSIGNED_BYTE (GL11.GL_UNSIGNED_BYTE),
            DT_BYTE (GL11.GL_BYTE),
            DT_SHORT (GL11.GL_SHORT),
            DT_UNSIGNED_SHORT (GL11.GL_UNSIGNED_SHORT),
            DT_INT (GL11.GL_INT),
            DT_UNSIGNED_INT (GL11.GL_UNSIGNED_INT),
            DT_FLOAT (GL11.GL_FLAT),
            DT_DOUBLE (GL11.GL_DOUBLE);

            private int glenum;
            private DataTypeE(int _glenum) {this.glenum = _glenum;}
            public int resolve(){return this.glenum;}

    };
    
//=================================================================================================
// Global variables and functions
//=================================================================================================
    // max render limitaciok
    private static int 
            maxLights, 
            maxMultiTexture, 
            max2DtexSize,
            maxRenderBuffers,
            maxVertexAttribs,
            maxBufferBind;
        
    private static int is_renderer_inited = 0;

    private static int vertexCount = 0;
    private static int polyCount = 0;

    private static int frameCount = 0;
    
    //private static Core.winsettings_t wndSettings;
    private static int renderWidth, renderHeight, render_xoffset, render_yoffset;
    private static boolean is_chopped;
    private static float render_aspectRatio;
    
    /*************************************************************************/
    /* Reslove es egyeb kiegeszito fuggvenyek                                */
    /*************************************************************************/
    
    // ... 
  
    /*************************************************************************/
    /* Renderer fo fuggvenyek                                                */
    /*************************************************************************/
 
    // 3x 4k elemet tartalmazo globalis buffer 
    // adatok atmeneti tarolasahoz
    // ezzel rengeteg memoria foglalast lehet sporolni
    protected static IntBuffer itmp4k = null;
    protected static ByteBuffer btmp4k = null;
    protected static FloatBuffer ftmp4k = null;
 
    protected static IntBuffer itmp = null;
    protected static ByteBuffer btmp = null;
    protected static FloatBuffer ftmp = null;
    
    /**
     * Beallitja a renderert a megadott stuktura alapjan.
     * @param winSettings ablak es a renderelesi felulet parameterit tartamazo struktura.
     * @return 0 ha minden rendben, kulonben a hiba keletkezesenek helyet adja vissza.
     */
    public static int setupRenderer(int render_width, int render_height, boolean is_chopped, int render_x_offset, int render_y_offset, float aspect_ratio){
        // renderer beallitasai
        Render.renderWidth = render_width;
        Render.renderHeight = render_height;
        Render.render_xoffset = render_x_offset;
        Render.render_yoffset = render_y_offset;
        Render.is_chopped = is_chopped;
        Render.render_aspectRatio = aspect_ratio;
        
        if(Render.is_renderer_inited != 0) return -1;
        
        int b = 4096;
        Render.itmp4k = BufferUtils.createIntBuffer(b); BufferUtils.zeroBuffer(itmp4k);
        Render.btmp4k = BufferUtils.createByteBuffer(b); BufferUtils.zeroBuffer(btmp4k);
        Render.ftmp4k = BufferUtils.createFloatBuffer(b); BufferUtils.zeroBuffer(ftmp4k);
        
        Render.itmp = BufferUtils.createIntBuffer(1); BufferUtils.zeroBuffer(itmp);
        Render.btmp = BufferUtils.createByteBuffer(1); BufferUtils.zeroBuffer(btmp);
        Render.ftmp = BufferUtils.createFloatBuffer(1); BufferUtils.zeroBuffer(ftmp);
        
        // bovitmenyek ellenorzese
        ContextCapabilities capabilities = GLContext.getCapabilities();
        if (            
            //Render.capabilities.WGL_EXT_swap_control &&   //fos
            //Render.capabilities.WGL_ARB_multisample &&    //fos

            // VBO, FBO, PBO
            capabilities.GL_ARB_vertex_buffer_object && 

            capabilities.GL_EXT_framebuffer_object &&
            capabilities.GL_EXT_framebuffer_multisample &&
            //Render.capabilities.ARB_draw_buffers &&   //fos

            capabilities.GL_ARB_pixel_buffer_object &&

            // Shader
            capabilities.GL_ARB_shading_language_100 &&

            // Multitexturing
            capabilities.GL_ARB_multitexture
        ){
            Render.maxLights =  GL11.glGetInteger(GL11.GL_MAX_LIGHTS);
            Render.maxMultiTexture = GL11.glGetInteger(GL13.GL_MAX_TEXTURE_UNITS);
            Render.max2DtexSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
            Render.maxVertexAttribs = GL11.glGetInteger(GL20.GL_MAX_VERTEX_ATTRIBS);
            Render.maxBufferBind = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
            Render.maxRenderBuffers = GL11.glGetInteger(GL20.GL_MAX_DRAW_BUFFERS);
            
            // megvan minden extension ami kell
            Render.is_renderer_inited++;
            //URES TEXTURA BEALLITASA   
            return initGL();
        }
        else // nincs
            return 2; // most nem allok neki atirni jora
    }

    /**
     * Kilepes elott lebontja a renderet.
     */
    public static void destroyRenderer(){
        //URES TEXTURA LELOVESE
    }

    /**
     * Fuggoleges kepszinkronizaciot kapcsolja ki/be. Ha lassu a render akkor vagy be
     * vagy pedig ki kell kapcsolni.
     * @param state 0 ha nincs, 1 ha van vsync beallitva.
     */
    //public static void setVsync(int state){
    public static void setVsync(boolean state){
        // ez nem mukodik mert nicns ra rendes ext.
        Display.setVSyncEnabled(state);
    }

    /**
     * A renderer viewportot a render felulet beallitasainak megfeleloen allitja
     * be. Ezt kell hasznalni a framebufferbe valo renderleskor.
     */
    public static void applyView(){
            Render.applyView(
                    Render.renderWidth, 
                    Render.renderHeight, 
                    Render.render_xoffset, 
                    Render.render_yoffset
            );
    }

    /**
     * Egyeni s*s negyzetre allitja a rendering viewportot. Ezt kell hasznalni 
     * egyeb bufferbe (FBO) valo rendereleskor.
     * @param s negyzet oldalanak hossza
     * 
     */
    public static void applyView(int s){
        Render.applyView(s,s,0,0);
    }

    /**
     * Egyeni meretre allitja a viewportot.
     * @param rw szelesseg
     * @param rh magassag
     * @param ox x offszet
     * @param oy y offszet
     */
    public static void applyView(int rw, int rh, int ox, int oy){
        GL11.glViewport(ox, oy, rw, rh);
    }

    /**
     * Renderer felulet szelessege
     * @return szelesseg
     */
    public static int getWidth(){
        return Render.renderWidth;
    }

    /**
     * Renderer felulet magassaga
     * @return 
     */
    public static int getHeight(){
        return Render.renderHeight;
    }

    /**
     * OGL csovezetek uritese (elvileg folosleges)
     */
    public static void flush(){
        GL11.glFinish();
        GL11.glFlush();
        
        Render.vertexCount = 0;
        Render.polyCount = 0;
        Render.frameCount ++;
    }
    /**
     * Egy menetben felhuzhato fenyforrasok szama. (altalaban 16)
     * @return ~
     */
    public static int getMaxLights(){return Render.maxLights;}
    /**
     * Egy menetben felhuzhato texturak szama. (alatalaban 4)
     * @return ~
     */
    public static int getMaxMTexture(){return Render.maxMultiTexture;}
    /**
     * Felhuzhato 2D textura maximalis merete. (legalabb 1024)
     * @return ~
     */
    public static int getMax2DTextureSize(){return Render.max2DtexSize;}
    /**
     * Egy menetben bekapcsolhato kimeneti bufferek (FBO) szama. (altalaban 16)
     * @return ~
     */
    public static int getMaxRenderBuffers(){return Render.maxRenderBuffers;}
    /**
     * Egy menetben felhasznalhato vertexenkenti parameterke szama.
     * @return ~
     */
    public static int getMaxVertexAttribs(){return Render.maxVertexAttribs;}
    /**
     * Egy menetben bekapcsolhato (vertex) bufferek szama.
     * @return ~
     */
    public static int getMaxBindBuffers(){return Render.maxBufferBind;}
    
    // --------------------------------------------------------------
    // OpenGL state modifier functions
    // --------------------------------------------------------------

    /**
     * Beallitja a kezdeti ertekeket az allapotgepben.
     * @return 
     */
    public static int initGL(){
            disableAllState();
            setDephtTest(Render.TestingConditionE.TC_LEQUAL, 1.0f);
            setShadingModel(Render.ShadingModelE.SH_SMOOTH);

            GL11.glCullFace(GL11.GL_BACK);
            GL11.glFrontFace(GL11.GL_CCW);
            GL11.glEnable(GL11.GL_CULL_FACE);

            // multisample funkico egyelore nem uzemel.
            //if ((samplePerPixel > 1) && multisampleSucceed){
                //glEnable(GL_MULTISAMPLE_ARB);
            //}
        return 0;
    }
    
    //namespace {
    //#if 1 // ez a megoldas akarmilyen rossznak nez ki, csak ez mukodik
    /**
     * Opengl allapotgpen levo funkciokat fix allapotba kapcsolja (ki/be)
     * @param var opengl funkcio (GLenum)
     * @param e true ha bekapcsolja a funkciot
     * @return valtozott-e az ertek, vagy sem
     */
    private static boolean switchVar(int var, boolean e)
    {
            boolean p = GL11.glIsEnabled(var);

            if ((p && e)||(!p && !e)) {return false;}
            if (p  && !e) GL11.glDisable(var);
            else if (!p &&  e) GL11.glEnable (var);

            //return ((p && e)||(!p && !e));
            return true;
    }
//        #else	// valamiert ez a megoldas nem mukodik.
//	inline int switchVar(GLenum var, bool e)
//	{
//		if (!e) glDisable(var);
//		else  glEnable (var);
//
//		return 1;
//	}
//        #endif

    private static final float whtmaterial[] = {1,1,1,1};
    private static final float blkmaterial[] = {0,0,0,1};

    /**
     * Letilt minden hasznalt erteket az allapotgepben.
     * Ezzel egy fix allapotba kapcsolva.
     * http://www.opengl.org/sdk/docs/man/xhtml/glEnable.xml
     */    
    public static void disableAllState(){
	//http://www.opengl.org/sdk/docs/man/xhtml/glEnable.xml
        // TODO: hianyzo enumok ... 
	boolean result =
		switchVar(GL11.GL_ALPHA_TEST,		false) ||
		switchVar(GL11.GL_AUTO_NORMAL,		false) ||
		switchVar(GL11.GL_BLEND,		false) ||
		switchVar(GL11.GL_COLOR_MATERIAL,	false) ||
		switchVar(ARBImaging.GL_COLOR_TABLE,    false) ||
		switchVar(GL11.GL_CULL_FACE,		false) ||
		switchVar(GL11.GL_DEPTH_TEST,		false) ||
		switchVar(GL11.GL_DITHER,		false) ||
		switchVar(GL11.GL_FOG,			false) ||
		switchVar(GL11.GL_LIGHTING,		false) ||
		switchVar(GL13.GL_MULTISAMPLE,		false) ||
		switchVar(ARBMultisample.GL_MULTISAMPLE_ARB, false) ||
		switchVar(GL11.GL_NORMALIZE,		false) ||
		switchVar(GL11.GL_POLYGON_SMOOTH,	false) ||
		switchVar(GL11.GL_TEXTURE_1D,		false) ||
		switchVar(GL11.GL_TEXTURE_2D,		false) ||
		//switchVar(GL11.GL_TEXTURE_3D,		false) ||
		switchVar(GL13.GL_TEXTURE_CUBE_MAP,	false) ||
		switchVar(GL11.GL_TEXTURE_GEN_R,	false) ||
		switchVar(GL11.GL_TEXTURE_GEN_S,	false) ||
		switchVar(GL11.GL_TEXTURE_GEN_T,	false);
        
        // TODO: hosszu tavon ez szar lesz.
	GL11.glColor4f(1,1,1,1);
        //FloatBuffer tmp = FloatBuffer.allocate(16);
        //FloatBuffer tmp = BufferUtils.createFloatBuffer(16);
        FloatBuffer tmp = Render.ftmp4k;
	tmp.put(Render.whtmaterial); GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE, tmp); tmp.rewind();
	tmp.put(Render.blkmaterial); GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_SPECULAR, tmp); tmp.rewind();
        tmp.put(Render.blkmaterial); GL11.glMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_EMISSION, tmp); tmp.rewind();

	// fenyeknek, texturaknak, es shadereknek automatikusan le kellene kapcsolodniuk

	//unbindShader(); 
	//for(register int i=0; i<getMaxMTexture(); i++) setTexture(0,i);
	//for (register int i=0; i<getMaxLights(); i++) result |= switchVar(GL_LIGHT0+i, false);

	return; //result;

    }
    
    /**
     * Beallitja a meshek arnyalasi modjat Render.shade_model_e enum alapjan. 
     * Ezt akkor erdemes hasznalni, ha nem hasznal a render menet shadereket, kulonben
     * a shader felulbiraja ezt.
     * @param sh megfelelo enum ertek.
     */
    public static void setShadingModel(ShadingModelE sh){
        GL11.glShadeModel(sh.resolve());
    }
    
    /**
     * Depth-test modjat allitja be
     * @param tci tesztelesi feltetel
     * @param d felso hatarertek (1.0f)
     */
    public static void setDephtTest(TestingConditionE tci, float d){
        clearDepth(d);
	GL11.glDepthFunc(tci.resolve());
	GL11.glDepthRange(0.0, d);
	//glDepthRange(1.0, 0.0);	// inverted
	switchVar(GL11.GL_DEPTH_TEST, true);
    }
    
    /**
     * Depth bufferbe valo irast kapcsolja ki/be
     * @param f true ha iras engedelyezve, false ha nem.
     */
    public static void switchDepthMask(boolean f){
        //if (f) GL11.glDepthMask(true); else GL11.glDepthMask(false);
        GL11.glDepthMask(f);
    }
    
    public static void switchDepthTest(boolean f){
        switchVar(GL11.GL_DEPTH_TEST, f);
    }
    /**
     * Egymasra renderelt retegek mixelesi modja.
     * @param sf forras mod
     * @param df cel mod
     */
    public static void setBlend(BlendingConditionE sf, BlendingConditionE df){
        GL11.glBlendFunc(sf.resolve(),df.resolve());
	switchBlend(true);
    }
    
    /**
     * Texturak hasznalatat kapcsolja minden retegre.
     * @param e true ha texturazas be van kapcsolva, false ha nem.
     * @return 
     */
    public static boolean switchTextue2D (boolean  e){
	for (int i=0; i<maxMultiTexture; i++) {
		ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB+i);
		switchVar(GL11.GL_TEXTURE_2D, e);
	}
	return switchVar(GL11.GL_TEXTURE_2D, e);
    }
    
    /**
     * Egy magadott retegben kikapcsolja a fenyforrast
     * @param i reteg (0..maxLights)
     * @param e ki/be kapcsolas
     * @return  false ha a megadott reteg szama ervenytelen (>=maxLights)
     */
    public static boolean switchLight (int i, boolean  e){
        if (i>=maxLights) return false;
	switchVar(GL11.GL_LIGHT0+i, e);
	return true; //fuck you
    }
    /**
     * Fenyek szamitasat kapcsola ki/be.
     * @param e ki/be kapcsolas
     * @return parameter valtozott-e
     */
    public static boolean switchLighting(boolean  e){
        return switchVar(GL11.GL_LIGHTING, e);
    }
    
    /**
     * Retegek egymasba mosasat engedelyezi 
     * @param e ki/be kapcsolas
     * @return parameter valtozott-e
     */ 
    public static boolean switchBlend(boolean  e){
        return switchVar (GL11.GL_BLEND, e);
    }
    
    /**
     * Stackbe menti (push) az OGL allapotgep aktualis allapotat.
     * SAJNOS NEM MUKORDIK MERT A JAVA EGY MINOSEGI PROGRAMOZASI NYELV
     */
    public static void pushClientState(){
        /*
        GL11.glPushAttrib(
		GL11.GL_DEPTH_BUFFER_BIT |
		GL11.GL_COLOR_BUFFER_BIT | 
                GL11.GL_PIXEL_MODE_BIT |
		GL11.GL_ENABLE_BIT |
		GL11.GL_TRANSFORM_BIT | 
		GL11.GL_VIEWPORT_BIT |
		GL11.GL_FOG_BIT |
		GL11.GL_LIGHTING_BIT |
		GL11.GL_POLYGON_BIT |
		GL11.GL_TEXTURE_BIT |
		GL13.GL_MULTISAMPLE_BIT | 
		ARBMultisample.GL_MULTISAMPLE_BIT_ARB 
                //0
	);
         * 
         */
    }
    
    /**
     * Stackbol visszaallitja az OGL allapotgep aktualis allapotat.
     * SAJNOS NEM MUKORDIK MERT A JAVA EGY MINOSEGI PROGRAMOZASI NYELV
     */
    public static void popClientState(){
        /*
        try{
            GL11.glPopClientAttrib();
        }catch (Exception e){
            //;
        }
         * 
         */
    }

    //matrix operations
    private static FloatBuffer matrixBuffer;
    protected static void mat_Select(MatrixModeE mode){
        if(matrixBuffer == null) matrixBuffer = BufferUtils.createFloatBuffer(16);
        GL11.glMatrixMode(mode.resolve());
    } 
    
    /**
     * A megadott matrix regiszter tartalmat egysegmatrixra csereli.
     * @param mm matrix regiszter tipusa.
     */
    public static void mat_LoadIdentity(MatrixModeE mm){
        mat_Select(mm);
	GL11.glLoadIdentity();
    }
    
    /**
     * A megadott matrix regiszter tartalmat a stack tetejere menti. 
     * @param m matrix regiszter tipusa.
     */
    public static void mat_Push(MatrixModeE m){
        mat_Select(m);
	GL11.glPushMatrix();
    }
    
    /**
     * A megadott matrix regiszter tartamat a stack tetejerol visszaallitja.
     * @param m matrix regiszter tipusa.
     */
    public static void mat_Pop(MatrixModeE m){
        mat_Select(m);
	GL11.glPopMatrix();
    }
    
    /**
     * A megadot matrix regiszter tartalmat m[] tombbe irja.
     * @param mm matrix regiszter tipusa.
     * @param m tomb amiba a matrix tartalma kerul. Kotelezoen 16 elemu.
     */
    public static void mat_Get(MatrixModeE mm, float m[]){
        matrixBuffer.rewind();
        GL11.glGetFloat(mm.resolveMatrixMode(), matrixBuffer);
        matrixBuffer.rewind();
        matrixBuffer.get(m);
        
    }
    
     /**
     * A megadot matrix regiszter tartalmat m[] tomb tartalmara csereli
     * @param mm matrix regiszter tipusa.
     * @param m tomb aminek tartalma kerul irasra. Kotelezoen 16 elemu.
     */
    public static void mat_Set(MatrixModeE mm, float m[]){
        matrixBuffer.rewind();
        Render.matrixBuffer.put(m); matrixBuffer.rewind();
        GL11.glLoadMatrix(matrixBuffer);
    }
    
    /**
     * A megadot matrix regiszter tartalmat m[] tomb tartalmaval szorozza.
     * @param mm matrix regiszter tipusa.
     * @param m tomb aminek tartalma kerul irasra. Kotelezoen 16 elemu.
     */
    public static void mat_Multiply(MatrixModeE mm, float m[]){
        matrixBuffer.rewind();
        Render.matrixBuffer.put(m); matrixBuffer.rewind();
        GL11.glMultMatrix(matrixBuffer);
    }

    /**
     * Depth buffer tartalmat torli.
     * @param d Ertek amivel feltolti a buffer tartalmat (1.0f)
     */
    public static void clearDepth(float d){
        GL11.glClearDepth(d);
        //GL11.glClearDepth(1.0f);
    }
    
    /**
     * Letorli a framebuffer tartalmat a megadott RGBA komponensu szinre, kepernyo levagasat figyelembe veve.
     * @param cr voros
     * @param cg zold 
     * @param cb kek 
     * @param ca alpha
     */
    public static void clearScene(float cr, float cg, float cb, float ca){
        if (cr+cg+cb+ca > (2.0f/256.0f)){
            if (Render.is_chopped){
                clearScene();
                pushClientState();
		
                mat_Push(MatrixModeE.MM_PROJECTION);
                mat_LoadIdentity(MatrixModeE.MM_PROJECTION);
		
                mat_Push(MatrixModeE.MM_MODELVIEW);
		mat_LoadIdentity(MatrixModeE.MM_MODELVIEW);

		applyView();
		GL11.glOrtho(0, 1, 1, 0, -1, 1);

                disableAllState();

                GL11.glDepthMask(false);

                //GL11.glEnable(GL11.GL_BLEND);
                //glBlendFuncSeparate (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA); 

                GL11.glColor4f(cr, cg, cb, 1);

                GL11.glBegin(GL11.GL_QUADS);
                {
                        GL11.glVertex2f(0.0f, 1.0f);
                        GL11.glVertex2f(0.0f, 0.0f);
                        GL11.glVertex2f(1.0f, 0.0f);
                        GL11.glVertex2f(1.0f, 1.0f); 
                }
                GL11.glEnd();
                GL11.glDepthMask(true);

                mat_Pop(MatrixModeE.MM_MODELVIEW);
                mat_Pop(MatrixModeE.MM_PROJECTION);

                //int err = glGetError();

               popClientState();

		} else {
                    GL11.glClearColor(cr, cg, cb, 1);
                    GL11.glClear (GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		}
	} else {
		clearScene();
	}
    }
    //public static void clearScene(FWmath::rgbaCol color);

    /**
     * Fekete szinure torli a framebuffer tartalmat.
     */
    public static void clearScene(){
        GL11.glClearColor(0f, 0f, 0f, 1f);
        GL11.glClear (GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
    }

    protected static void addVertexCount(int v){Render.vertexCount += v;}
    protected static void addPolyCount(int v){Render.polyCount += v;}
    
    /**
     * Kirajzolt vertexek szama
     * @return ~
     */
    public static int getVertexCount(){return Render.vertexCount;}
    /**
     * kirajzolt polygonok szama
     * @return ~
     */
    public static int getPolyCount(){return Render.polyCount;}
    
    /**
     * Visszaadja render felulet oldalaranyat
     * @return ~
     */
    public static float getRenderAspectRatio(){return Render.render_aspectRatio; }
}
