/*
 * Generated with cpp2java.py by slapec^IR
 * (root)/trunk/engine/engine/source/graph/renderer.hpp - Rev 79
 */
package CSNC.FWrender;

import CSNC.FWrender.Texture.*;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;

/**
 * Egyszerusitett frame buffer object osztaly.
 * Nem renderel depth es stencil mapot, csak color mapot
 * @author CAIWAN
 */
public class FBO {
    private int fboID, rboID[];
    private Texture[] tex;
    private int size, mapNum;
    private boolean isGenerateMipmap;
    
    private IntBuffer drawBuffersList; 
    
    private void checkFBO(){
        int status = EXTFramebufferObject.glCheckFramebufferStatusEXT( EXTFramebufferObject.GL_FRAMEBUFFER_EXT ); 
        switch (status) {
                case EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT:
                        break;
                case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
                        throw new RuntimeException( "FrameBuffer: " + fboID
                                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception" );
                case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
                        throw new RuntimeException( "FrameBuffer: " + fboID
                                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception" );
                case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
                        throw new RuntimeException( "FrameBuffer: " + fboID
                                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception" );
                case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
                        throw new RuntimeException( "FrameBuffer: " + fboID
                                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception" );
                case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
                        throw new RuntimeException( "FrameBuffer: " + fboID
                                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception" );
                case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
                        throw new RuntimeException( "FrameBuffer: " + fboID
                                        + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception" );
                default:
                        throw new RuntimeException( "Unexpected reply from glCheckFramebufferStatusEXT: " + status );
}
    }
    
    public FBO(int _colorMap, int _size, boolean _isGenerateMipmap){
        int max = Render.getMaxRenderBuffers();
        
        size = _size;
        isGenerateMipmap = _isGenerateMipmap;
        
        mapNum = _colorMap>=max?max:_colorMap;
        tex = new Texture[mapNum]; drawBuffersList = BufferUtils.createIntBuffer(mapNum);
        rboID = new int[mapNum];
        
        fboID = EXTFramebufferObject.glGenFramebuffersEXT();
	EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, fboID);
        
        for (int i=0; i<mapNum; i++){
            tex[i] = new Texture();
            tex[i].setIsGenerateMipmap(isGenerateMipmap);
            if (isGenerateMipmap)
                tex[i].setMinFilter(isGenerateMipmap?TextureFilterE.TF_LINEAR_MIPMAP_LINEAR:TextureFilterE.TF_LINEAR);
            
            tex[i].build(null, size, size, 4, Render.DataTypeE.DT_UNSIGNED_BYTE);
            
            rboID[i] = EXTFramebufferObject.glGenRenderbuffersEXT();
            EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, rboID[i]);
            
            if (i>0) 
                EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT32, size, size);
            if (i>0) 
                EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0);
            
            EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT+i, GL11.GL_TEXTURE_2D, tex[i].getID(), 0);
            if (i>0)
                EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, rboID[i]);
            
            drawBuffersList.put(EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT + i);
        }
        
        drawBuffersList.rewind();
        GL20.glDrawBuffers(drawBuffersList); 
        
        checkFBO();
        
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, 0);
	EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
    }
    
    public void bind(){
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, this.fboID);
    }
    
    public void unbind(){
       EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0); 
       
       	if (this.isGenerateMipmap) for (Texture ptr : this.tex){
		ptr.bind();
		EXTFramebufferObject.glGenerateMipmapEXT(GL11.GL_TEXTURE_2D);
		ptr.unbind();
	}
    }
    public Texture getColorTexture(int l){
        return this.tex[l%mapNum];
    }
}
