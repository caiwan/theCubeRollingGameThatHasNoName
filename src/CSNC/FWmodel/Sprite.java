/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CSNC.FWmodel;

import CSNC.FWcore.Core;
import CSNC.FWrender.*;
import CSNC.FWrender.Render.*;
import org.lwjgl.opengl.GL11;

/**
 * 2D-s spriteot rajzol ki a kepernyore. Sajnos meglehetosen lassu.
 * @author caiwan
 */
public class Sprite {    
    public static enum VerticalAlignmentE{
        VA_TOP,
        VA_MIDDLE,
        VA_BOTTOM
    }
    
    public static enum HorizontalAlignemntE{
        HA_LEFT,
        HA_CETER,
        HA_RIGHT
    };
    
    protected static void push2Dmode(){
		// psh

		Render.mat_Push(MatrixModeE.MM_MODELVIEW);
		Render.mat_Push(MatrixModeE.MM_PROJECTION);

		//Render.pushClientState();
		Render.disableAllState();

		Render.switchDepthMask(false);
                Render.switchDepthTest(false);

		Render.switchTextue2D(true);

		//glEnable (GL_BLEND); 
                Render.switchBlend(true);
		//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//glBlendFuncSeparate (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA); 

		// 2D mode
		Camera.ortho();
	}

    protected static void pop2Dmode(){
		// pop
		Render.switchDepthMask(true);
                Render.switchDepthTest(true);
		//Render.popClientState(); 
		//glDisable (GL_BLEND); 
		Render.switchBlend(false);
		
		Render.mat_Pop(MatrixModeE.MM_PROJECTION);
		Render.mat_Pop(MatrixModeE.MM_MODELVIEW);
		
	}   
    
    protected float sx, sy, px, py, kx, ky, ox, oy, scw, sch, ar, wx, wy, u1, u2, v1, v2, cr, cg, cb, ca;
    protected Texture tex[];
    
    public Sprite(){
        scw = (float)Render.getWidth();
        sch = (float)Render.getHeight();
	ar = Core.CORE_FORCE_RATIO.getAR();
        
        wx = ar;
        wy = 1.f;
        
        u1 = 0f;
        u2 = 1f;
        v1 = 0f;
        v2 = 1f;
        
        this.tex = new Texture[Render.getMaxMTexture()];
        
        cr = cg = cb = ca = sx = sy = 1.f;
    }
    
    public void setPositionMode(boolean isAbsolute){
        if(isAbsolute){
            wx = scw;
            wy=sch;
        } else {
            wx = ar;
            wy = 1.f;
        }
    }
    
    /**
     * Ezzel a fuggvennyel lehet beallitani a kepernyon elhelyezheto descartes 
     * koordinata rendszer origojat vizszintes es fuggoleges iranyban.
     * @param va
     * @param ha 
     */
    public void setPositionAlignmentMode(VerticalAlignmentE va, HorizontalAlignemntE ha){
        kx=0.0f; ky=0.0f;
        
        switch (ha)
            {
		case HA_CETER:
			kx = .5f;
			break;
		case HA_RIGHT:
			kx = 1.f;
			break;
            }

	switch (va)
	{
		case VA_MIDDLE:
			ky = .5f;
			break;
		case VA_BOTTOM:
			ky = 1.f;
			break;
	}
    }
    
    /**
     * Ezzel lehet a spriteon beluli descartes koordinata origojat elhelyezni.
     * @param va
     * @param ha 
     */
    public void setOrignAlignmentMode(VerticalAlignmentE va, HorizontalAlignemntE ha){
        ox = 0; oy = 0;

	switch (ha){
		case HA_CETER:
			ox = .5f;
			break;
		case HA_RIGHT:
			ox = 1.f;
			break;
	}

	switch (va)
	{
		case VA_MIDDLE:
			oy = .5f;
			break;
		case VA_BOTTOM:
			oy = 1.f;
			break;
	}
    }
    
    public void setPosition(float px, float py){
        this.px = px;
        this.py = py;
    }
    
    public void setSize(float sx, float sy){
        this.sx = sx;
        this.sy = sy;
    }
    
    public void setTextureUV(float _v1, float _u1, float _v2, float _u2){
        u1 = _u1; v1 = _v1; u2 = _u2; v2 = _v2;
    }
    
    public void setupFullscreenQuad(int fbosize){
        float w = Render.getWidth(), h = Render.getHeight();
	float ssx = h/(float)fbosize, ssy = w/(float)fbosize;
	setPositionMode(true);
	setSize(w, h);
	setTextureUV(ssx, 0.0f, 0.0f, ssy);
    }
    
    public void setupFullscreenQuad(){
        float w = Render.getWidth(), h = Render.getHeight();
	setPositionMode(true);
	setSize(w, h);
	setTextureUV(1.f - 0.0000015f, 0.f + 0.0000015f, 0.f + 0.0000015f, 1.f - 0.0000015f);
    }
    
    public void setTexture(int l, Texture texptr){
        if (l>=Render.getMaxMTexture()) return;
        this.tex[l] = texptr;
    }
    
    public void setColor(float r, float g, float b, float a){
        cr = r; cg  = g; cb = b; ca = a;
    }
    
    public void setColor(float r, float g, float b){
        cr = r; cg  = g; cb = b;
    }
    
    public void setAlpha(float a){
        ca = a;
    }
    
    public void draw(){
        push2Dmode();
	
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
        
	//glRotatef(0,0,0, 10);
        Render.mat_LoadIdentity(MatrixModeE.MM_MODELVIEW);
	GL11.glScalef(sx, sy, 0);
	GL11.glTranslatef((px/sx - ox), (py/sy - oy), 0);

	//GL11.glColor4f(tint.r, tint.g, tint.b, tint.a);
        GL11.glColor4f(cr, cg, cb, ca);

	//Render.addPolyCount(2); //hopp, ez nem azonos csomag
	//Render.addPolyCount(4);
        
//        float x1 = 0.0f, y1 = 0.0f, x2 = 0.0f, y2 = 0.0f;
//        x1 = px-ox+sx; x2 = px-ox+(2*sx);
//        y1 = py-oy+sy; x2 = py-oy+(2*sy);
//	GL11.glBegin(GL11.GL_QUADS);	// TODO: use VBO instead
//	{
//		GL11.glTexCoord2f(u1, v1); GL11.glVertex2f(x1, y1);
//		GL11.glTexCoord2f(u1, v2); GL11.glVertex2f(x1, y2);
//		GL11.glTexCoord2f(u2, v2); GL11.glVertex2f(x2, y2);
//		GL11.glTexCoord2f(u2, v1); GL11.glVertex2f(x2, y1); 
//	}
//	GL11.glEnd();

	GL11.glBegin(GL11.GL_QUADS);
	{
		GL11.glTexCoord2f(u1, v1); GL11.glVertex2f(0, 0);
		GL11.glTexCoord2f(u1, v2); GL11.glVertex2f(0, 1);
		GL11.glTexCoord2f(u2, v2); GL11.glVertex2f(1, 1);
		GL11.glTexCoord2f(u2, v1); GL11.glVertex2f(1, 0); 
	}
	GL11.glEnd();

	pop2Dmode();

	//FWrender::setTexture(0);
	//for(int i=0; i<FWrender::getMaxMTexture(); i++) if (this->tex[i]) this->tex[i]->unbind();
        for(int i=0; i<Render.getMaxMTexture(); i++) 
            if (this.tex[i] != null) 
                this.tex[i].unbind(); 
    }
    
}
