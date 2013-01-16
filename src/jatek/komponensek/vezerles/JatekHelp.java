/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jatek.komponensek.vezerles;

import CSNC.BuiltInResources.*;
import CSNC.FWcore.*;
import CSNC.FWcore.KeyboardUtil.KeyStatusE;
import CSNC.FWmodel.*;
import CSNC.FWmodel.Sprite.*;
import CSNC.FWrender.*;
import CSNC.FWrender.Render.*;
import CSNC.FWrender.Texture.*;
import CSNC.FWrender.VertexArray.*;
import jatek.komponensek.Data;

import org.lwjgl.input.Keyboard;

/**
 *
 * @author caiwan
 */
public class JatekHelp  implements Scene{
    
    private static final String pages[]={
        "instr00.png",
        "instr01.png"
    };
    
    private static final float SCOLL_SPEED = .5f;
    
    //////////////////////////////////////////////////////////////////////////
    private Sound selectItemSound, chooseItemSound;
    
    private Texture pageTex[];
    private Sprite pageSprite;
    
    private Sprite hatterSprite;
    private Shader hatterShader;
    
    private boolean exit;
    
    @Override
    public void init() throws Exception {
        pageTex = new Texture[JatekHelp.pages.length];
        for(int i=0; i<pageTex.length; i++){
            pageTex[i] = new Texture();
            pageTex[i].buildFromResource(Data.TEXTURE_ROOT + pages[i]);
        }
        
        pageSprite = new Sprite();
        pageSprite.setOrignAlignmentMode(VerticalAlignmentE.VA_MIDDLE, HorizontalAlignemntE.HA_CETER);
        pageSprite.setPositionAlignmentMode(VerticalAlignmentE.VA_MIDDLE, HorizontalAlignemntE.HA_CETER);
        pageSprite.setSize(.85f, .85f);
        
        // #hatter
        hatterShader = new Shader();
        hatterShader.createShader(Data.hdrVertexShader, Data.hatterFramgnetShader);
        
        hatterSprite = new Sprite();
        hatterSprite.setupFullscreenQuad();
        
        try {
            // hangok
            selectItemSound = new Sound();
            selectItemSound.loadFromResource(false, "OGG", Data.SFX_ROOT + Data.SOUND_MOVE);
        
            chooseItemSound = new Sound();
            chooseItemSound.loadFromResource(false, "OGG", Data.SFX_ROOT + Data.SOUND_SELECT);
        } catch (Exception e){
            throw e;
        }
    }
    
    private int last_page, curr_page;
    private boolean anim;
    private float time0;
    
    private float smooth(float t){
        return t * t * (3 - 2 * t);
    }
    
    @Override
    public void mainloop(float time) {
        exit = false;
        
        boolean l_Pressed   = (KeyboardUtil.getKeyStatus(Keyboard.KEY_LEFT)   == KeyStatusE.KS_down);
        boolean r_Pressed   = (KeyboardUtil.getKeyStatus(Keyboard.KEY_RIGHT)  == KeyStatusE.KS_down);
        boolean esc_pressed = (KeyboardUtil.getKeyStatus(Keyboard.KEY_ESCAPE) == KeyStatusE.KS_down);
        
        if (esc_pressed) {exit = true; chooseItemSound.play();}
        
        if (!anim){
            if (r_Pressed){
                anim = true;
                curr_page++;
                if (curr_page >= pageTex.length-1) curr_page = pageTex.length-1;
                time0 = time;
                selectItemSound.play();
            }

            if (l_Pressed){
                anim = true;
                curr_page --;
                if (curr_page <= 0) curr_page = 0;
                time0 = time;
                selectItemSound.play();
            }
        }
        
        float t = 0.f, st, tt = last_page;
        
        if (anim){
            t = (time-time0) / SCOLL_SPEED;
            st = smooth(t);
            tt = curr_page * st + last_page * (1-st);
            if (t>1.f){
                last_page = curr_page;
                anim = false;
            }
        }
        
        //Render.disableAllState();
        Render.clearScene();
        
        Render.setBlend(BlendingConditionE.BC_SRC_ALPHA, BlendingConditionE.BC_ONE_MINUS_SRC_ALPHA);
        
        hatterShader.bind();
        
        hatterShader.setfv("color0", Data.backgroudColor, 3);
        hatterShader.setfv("color1", Data.backgroudColor2, 3);
        
        //hatterShader.setf("resolution", 1000, 1000*Render.getRenderAspectRatio());
        hatterShader.setf("resolution", Render.getWidth(), Render.getHeight());
        hatterShader.setf("time", time);
        hatterShader.setf("yshift", -(time/50f));
        hatterShader.setf("xshift", tt / 16f);
        
        hatterSprite.draw();
        hatterShader.unbind();
        
        for (int i=0; i<pageTex.length; i++){
            float yshift = i - tt;
            if (yshift<(Render.getRenderAspectRatio()+1) || yshift > -1f){
                pageSprite.setTexture(0, pageTex[i]);
                pageSprite.setPosition(yshift, 0);
                pageSprite.draw();
            }
        }
        
        Render.setBlend(BlendingConditionE.BC_ONE, BlendingConditionE.BC_ZERO);
        Render.switchBlend(false);
    }
    
    public void resetTimer(float time) {
        curr_page = 0;
        last_page = -1;
        anim = true;
        exit = false;
        time0 = time;
    }
    
    public boolean isExit(){
        return exit;
    }
    
}
