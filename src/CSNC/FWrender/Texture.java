/*
 * Generated with cpp2java.py by slapec^IR
 * (root)/trunk/engine/engine/source/graph/renderer.hpp - Rev 79
 */
package CSNC.FWrender;

import CSNC.FWrender.Render.*;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.*;
import java.util.HashMap;
import java.util.Objects;
import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

public class Texture{
    /**
     * Textura szuresi modok.
     */
    public static enum TextureFilterE {
            TF_NEAREST (GL11.GL_NEAREST),
            TF_LINEAR (GL11.GL_LINEAR),
            TF_NEAREST_MIPMAP_NEAREST (GL11.GL_NEAREST_MIPMAP_NEAREST),
            TF_NEAREST_MIPMAP_LINEAR (GL11.GL_NEAREST_MIPMAP_LINEAR),

            TF_LINEAR_MIPMAP_NEAREST (GL11.GL_LINEAR_MIPMAP_NEAREST),
            TF_LINEAR_MIPMAP_LINEAR (GL11.GL_LINEAR_MIPMAP_LINEAR);

            private int glenum;
            private TextureFilterE(int _glenum) {this.glenum = _glenum;}
            public int resolve(){return this.glenum;}
    };
    
    /**
     * Textura formatum modok.
     */
    public static enum TextureFormatE {
            TFM_AUTO (GL11.GL_FALSE),
            TFM_RGB (GL11.GL_RGB),
            TFM_RGBA (GL11.GL_RGBA),

            TFM_LUMINANCE (GL11.GL_LUMINANCE),
            TFM_LUMINANCE_ALPHA (GL11.GL_LUMINANCE_ALPHA),

            // ezek itt most nem fognak kelleni
            
//            TFM_COLOR_INDEX (GL11.GL_COLOR_INDEX),
//            TFM_RED,
//            TFM_GREEN,
//            TFM_BLUE,
//            TFM_ALPHA,
//
//            TFM_BGR,
//            TFM_BGRA,

            TFM_DEPTH_COMPONENT (GL11.GL_DEPTH_COMPONENT);

            private int glenum;
            private TextureFormatE(int _glenum) {this.glenum = _glenum;}
            public int resolve(){return this.glenum;}
    };

        // static
        private static int emptyTextureID, is_generated_emptyTexture;
        
        /**
         * Egy fix 8x8x3 meretu feher texturat hoz letre. Ez lesz az alapertelmezett
         * textura a 0 helyett, mivel a shaderek nem mukodnek rendesen 0 texturaval. 
         * Helyette van egy alapertelmezett feher.
         */
        public static void generateEmptyTexture(){
            if (is_generated_emptyTexture++ != 0) return;
            Texture tex = new Texture();
            tex.setMagFilter(TextureFilterE.TF_NEAREST);
            tex.setMinFilter(TextureFilterE.TF_NEAREST);
            
            // ez a legkisebb hiba nelkul letrehozhato textura 
            // azert fix, mert mas erteket kar beallitani, az ugy is a GPU memoria
            // rovasara menne. Kulonben is, egy szinu.
            int s = 8;
            ByteBuffer bmp = BufferUtils.createByteBuffer(s*s*3);
            for (int i=0; i<s*s*3; ++i){
                bmp.put((byte)255);
            }
            
            tex.build(bmp, s, s, 3, DataTypeE.DT_UNSIGNED_BYTE);
            
            Texture.emptyTextureID = tex.getID();
        }
        
        // class
        private void setupTextureMatrix(){
            Render.mat_LoadIdentity(MatrixModeE.MM_TEXTURE);
            
            // ... 
            
            Render.mat_Select(MatrixModeE.MM_MODELVIEW);
        }

	private int sx, sy, ch;	
        private int id, layer, bound_layer;
	private Texture.TextureFilterE magFilter;
	private Texture.TextureFilterE minFilter;
	private boolean generateMipmap;

	private TextureFormatE textureFormat;
	private	DataTypeE      dataFormat;
        
        public Texture(){
            Texture.generateEmptyTexture();
            this.textureFormat = TextureFormatE.TFM_AUTO;
            
            this.magFilter = TextureFilterE.TF_LINEAR;
            this.minFilter = TextureFilterE.TF_LINEAR;
            
            this.generateMipmap = false;
        }
        
        /**
         * A textura kicsinyitesehez (minify) alkalmazott textura szuro
         * Alapertelmezett: TF_LINEAR
         * @param f texutra szuro tipusa (TF_NEAREST es TF_LINEAR lehet)
         */
        public void setMinFilter(Texture.TextureFilterE f){this.minFilter = f;}
        
        /**
         * A textura nagyitasahoz (magnitude) alkalmazott textura szuro
         * Alapertelmezett: TF_LINEAR
         * @param f texutra szuro tipusa
         */
        public void setMagFilter(Texture.TextureFilterE f){this.magFilter = f;}
        
        /**
         * Textura felepitese soran generaljon-e mipmapot, van sem.
         * Alapertelmezett: false
         * @param t true ha igen.
         */
        public void setIsGenerateMipmap(boolean  t) {this.generateMipmap = t;}
        
        /**
         * Beallitja melyik retegre huzza fel automatikusan a texturat.
         * Alapertelmezett: 0
         * @param l 
         */
        public void setLayer(int l){this.layer = l;}
        //public int getID(){return this.id;}
        
        /**
         * A beallitott retegre felhuzza a texturat.
         */
        public void bind(){bind(this.layer);}
        
        /**
         * Egy megadott retegre (a beallitott reteget figyelmen kivul hagyva) felhuzza az adaott texturat
         * @param level reteg
         */
        public void bind(int level){
            if (level>=Render.getMaxMTexture() || this.id == 0) return;
            
            this.setupTextureMatrix();
            
            ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB+layer);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);
            this.bound_layer = layer;
        }
        
        /**
         * Kikapcsolja a felhuzott texturat.
         */
        public void unbind(){
            ARBMultitexture.glActiveTextureARB(ARBMultitexture.GL_TEXTURE0_ARB+bound_layer);
            //if (bound_layer == 0)
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, Texture.emptyTextureID);
            //else
            //    GL11.glBindTexture(this.GL11.GL_TEXTURE_2D, 0);
        }
        
        /**
         * RAW bitkep adatokbol felepiti a texturat.
         * @param bmp bitkep adataira mutato pointer
         * @param sx bitkep szelessege
         * @param sy bitkep magassaga
         * @param ch bitkep szincsatornainak szama
         * @param dataFormat bitkep komponenseit tartalmazo formatum
         */
        public void build(ByteBuffer bmp, int sx, int sy, int ch, DataTypeE dataFormat){
            this.sx = sx;
            this.sy = sy;
            this.ch = ch;
            
            this.dataFormat = dataFormat;
            
            IntBuffer tmp = Render.itmp;
            GL11.glGenTextures(tmp); 
            tmp.rewind(); this.id = tmp.get(0);
            
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.id);

            if (this.id == 0) return;
            
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, this.magFilter.resolve());
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, this.minFilter.resolve());
            
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
            
            int texFormat = this.textureFormat.resolve();
            
            if (this.textureFormat == Texture.TextureFormatE.TFM_AUTO) 
                switch (this.ch){
                    case 1: texFormat = GL11.GL_LUMINANCE;  break;
                    //case 1: texFormat = GL_LUMINANCE; break;
                    case 2: texFormat = GL11.GL_LUMINANCE_ALPHA; break;		
                    case 3: texFormat = GL11.GL_RGB; break;
                    case 4: texFormat = GL11.GL_RGBA; break;
                    default:texFormat = GL11.GL_RGB;  break;
            }
       
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_GENERATE_MIPMAP, this.generateMipmap?1:0);
            
            if (bmp!=null) bmp.rewind();
            
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 
                            0,
                            texFormat,
                            this.sx, this.sy, 0, 
                            texFormat,
                            this.dataFormat.resolve(), bmp
            );

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        }
        
        // Resource map kezelo -> ne toltse be ugyanazt a texturat tobbszor
        private class tt{
            public TextureFilterE min;
            public TextureFilterE mag;
            public String fullpath;
            public boolean isGenerateMipmap;

            public boolean equals(Object obj) {
                if (obj == null) {
                    return false;
                }
                if (getClass() != obj.getClass()) {
                    return false;
                }
                final tt other = (tt) obj;
                if (this.min != other.min) {
                    return false;
                }
                if (this.mag != other.mag) {
                    return false;
                }
                if (!Objects.equals(this.fullpath, other.fullpath)) {
                    return false;
                }
                if (this.isGenerateMipmap != other.isGenerateMipmap) {
                    return false;
                }
                return true;
            }

            public int hashCode() {
                int hash = 7;
                hash = 17 * hash + (this.min != null ? this.min.hashCode() : 0);
                hash = 17 * hash + (this.mag != null ? this.mag.hashCode() : 0);
                hash = 17 * hash + Objects.hashCode(this.fullpath);
                hash = 17 * hash + (this.isGenerateMipmap ? 1 : 0);
                return hash;
            }

        }
        
        private static HashMap<tt, Integer> resourceMap;
        private static tt restmp;
        
        /**
         * JAR resourceban levo kepet az awt lib segitsegevel betolti, majd texturat epit fel belole
         * @param path resource eleresi utvonala (a JAR csomagon belul)
         * @return 0 ha sikerult.
         */
        public int buildFromResource(String path){
            // resource map csekkolasa
            if (resourceMap == null) resourceMap = new HashMap<>();
            if (restmp == null) restmp = new tt();
            
            restmp.fullpath = path;
            restmp.mag = this.magFilter;
            restmp.min = this.minFilter;
            restmp.isGenerateMipmap = this.generateMipmap;
            
            if (resourceMap.containsKey(restmp)){
                this.id = resourceMap.get(restmp);
                return 0;
            }
            
            try {
                URL addr = this.getClass().getResource(path);
        
                BufferedImage img;
                img = ImageIO.read(addr);

                int ssx = img.getWidth();
                int ssy = img.getHeight();
                int pixel;
                
                // TODO: szukseg eseten meretezze at a kepet
                
                ByteBuffer bmp = BufferUtils.createByteBuffer(ssy*ssx*4); //rgba kep
       
                for (int y = 0; y<ssy; y++)
                    for (int x = 0; x<ssx; x++){
                        pixel = img.getRGB(x, y);           // argb
                        bmp.put((byte)(pixel>>16 & 0xFF));  // r
                        bmp.put((byte)(pixel>>8  & 0xFF));  // g
                        bmp.put((byte)(pixel     & 0xFF));  // b 
                        bmp.put((byte)(pixel>>24 & 0xFF));  // a
                    }
        
                this.build(bmp, ssx, ssy, 4, Render.DataTypeE.DT_UNSIGNED_BYTE);
            }catch (Exception e){
                // TODO: hiba kezeleset valami elegans modon megoldani.
                return 1;
            }
            
            resourceMap.put(restmp, this.id);
            
            return 0;
        }
        
        /**
         * Torli a GPU bufferbol a texturat
         */
        public void destroyTexture(){
            GL11.glDeleteTextures(this.id);
        }
        
        /**
         * GPU-n beluli textura tar ID-je
         * @return ~
         */
        public int getID() {return this.id;}
}