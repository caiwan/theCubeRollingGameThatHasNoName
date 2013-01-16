/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CSNC.FWmodel;

import CSNC.FWrender.*;
import CSNC.FWrender.Render.MatrixModeE;
import CSNC.FWrender.VertexArray.*;
import CSNC.FWrender.Texture.*;

import java.nio.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/**
 * NEHE tipusu dupla fontkeszlet kirajzolasara alkamas osztaly.
 * Az eredeti motor tobbfelekeppen tud betuket rajzolni, ez most nem.
 * http://nehe.gamedev.net/tutorial/2d_texture_font/18002/
 * 
 * @author caiwan
 */

public class Text extends Sprite{
    private static VertexArray nehe_vertexArray;
    private static FloatBuffer nehe_vertexBuffer;
    private static FloatBuffer nehe_texcoordBuffer;
    private static IntBuffer nehe_indicesBuffer[];
    
    private static boolean neheCreated = false;
    
    /**
     * A Nehe-fele fontterkep egy texturan ket fontkeszletet tartalmaz, 
     * mindket fontkeszletbol csak a 96 nyomtathato ASCII karakter
     * van a texturan.
     * Osszesen 256 karakter van, 16*16 matrixban.
     */
    private static void crateNeheFontMap(){
        if (!neheCreated){
            Text.nehe_vertexBuffer = BufferUtils.createFloatBuffer(4*3*256);
            Text.nehe_texcoordBuffer = BufferUtils.createFloatBuffer(4*2*256);
            Text.nehe_indicesBuffer = new IntBuffer[256]; 
            
            float step = 1.f/16f;
            float cx, cy, ccx, ccy;
            for (int y=0; y<16; y++)
                for (int x=0; x<16; x++){
                    nehe_vertexBuffer.put(0f); nehe_vertexBuffer.put(0f); nehe_vertexBuffer.put(0f);
                    nehe_vertexBuffer.put(0f); nehe_vertexBuffer.put(1f); nehe_vertexBuffer.put(0f);
                    nehe_vertexBuffer.put(1f); nehe_vertexBuffer.put(1f); nehe_vertexBuffer.put(0f);
                    nehe_vertexBuffer.put(1f); nehe_vertexBuffer.put(0f); nehe_vertexBuffer.put(0f);
                    
                    cx = (float)x*step;
                    cy = (float)y*step;
                    
                    nehe_texcoordBuffer.put(cx);      nehe_texcoordBuffer.put(cy);
                    nehe_texcoordBuffer.put(cx);      nehe_texcoordBuffer.put(cy+step);
                    nehe_texcoordBuffer.put(cx+step); nehe_texcoordBuffer.put(cy+step);
                    nehe_texcoordBuffer.put(cx+step); nehe_texcoordBuffer.put(cy);       
            }
            
            nehe_vertexBuffer.rewind();
            nehe_texcoordBuffer.rewind();           
            
            for (int i=0; i<256; i++){
                nehe_indicesBuffer[i] = BufferUtils.createIntBuffer(2*3);
                nehe_indicesBuffer[i].put(4*i + 0);
                nehe_indicesBuffer[i].put(4*i + 1);
                nehe_indicesBuffer[i].put(4*i + 2);
                nehe_indicesBuffer[i].put(4*i + 2);
                nehe_indicesBuffer[i].put(4*i + 3);
                nehe_indicesBuffer[i].put(4*i + 0);
                nehe_indicesBuffer[i].rewind();
            }
            
            Text.nehe_vertexArray = new VertexArray();
            Text.nehe_vertexArray.setVertexPointer(nehe_vertexBuffer, 3, 2*256, true);
            Text.nehe_vertexArray.setTexturePointer(nehe_texcoordBuffer, 2, 2*256, true);
            
            neheCreated = true;
        }
    }
    
    //private Texture tex;
    //private float sx, sy, px, py;
    
    private String text;
    private float padding;
    
    public Text(){
        super();
        Text.crateNeheFontMap();
    }
    
    /*
    public void setTexture(Texture tptr){
        this.tex = tptr;
    }
    */
    
    public void setText(String txtptr){
        this.text = txtptr;
    }
    
    public void setPadding(float p){
        padding = p;
    }
    
    @Override
    public void draw(){
        if (this.text == null) return;
        if (this.text.length() == 0) return;
        
        push2Dmode();
	
        byte[] buf = text.getBytes();
        byte chr;
        int len = text.length();
        
	Render.switchTextue2D(true);
        
	//FWrender::setTexture(texID);
	for(int i=0; i<Render.getMaxMTexture(); i++) 
		if (this.tex[i] != null) 
			this.tex[i].bind(i); 
		//else 
		//	FWrender::setTexture(0, i);

	Camera.ortho(
		(-kx) * wx,	(1.f-kx) * wx,
		(-ky) * wy,	(1.f-ky) * wy
	);
        
        float ppad = (1f+padding);
        float kkx = (ppad*sx*(float)len) * kx;
        //float kky = ((float)len) * ky;
        
        GL11.glColor4f(cr, cg, cb, ca);
        
        Render.mat_LoadIdentity(MatrixModeE.MM_MODELVIEW);
	GL11.glScalef(sx, sy, 0);
	GL11.glTranslatef(((px-kkx)/sx - ox), ((py)/sy - oy), 0);
        
        Text.nehe_vertexArray.bindAll();
        for (int i=0; i<len; i++){
            chr = buf[i];
            if(chr < 0) chr = (byte)(256 - chr);
            
            Text.nehe_vertexArray.drawByIndex(Text.nehe_indicesBuffer[chr], 6);
            GL11.glTranslatef(1f*ppad, 0f, 0f);
        }
        
        Text.nehe_vertexArray.unbindAll();
        
        pop2Dmode();
        
        for(int i=0; i<Render.getMaxMTexture(); i++) 
            if (this.tex[i] != null) 
                this.tex[i].unbind(); 
    }
    /*
    public void draw(String text, int charset){
        byte[] buf = text.getBytes();
        byte chr;
        int len = text.length();
        
        Sprite.push2Dmode();
        
        Render.mat_LoadIdentity(Render.matrix_mode_e.MM_MODELVIEW);
        GL11.glScalef(.1f, .1f, .1f);
        GL11.glTranslatef(.0f,.5f, 0f);
        
        this.tex.bind();
        Text.nehe_vertexArray.bindAll();
        for (int i=0; i<len; i++){
            chr = buf[i];
            Text.nehe_vertexArray.drawByIndex(Text.nehe_indicesBuffer[chr], 6);
            GL11.glTranslatef(1f,0, 0f);
        }
        
        Text.nehe_vertexArray.unbindAll();
        this.tex.unbind();
        
        Sprite.pop2Dmode();
    }
    */
}
