
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jatek.komponensek.vezerles;

import CSNC.BuiltInResources.*;
import CSNC.FWcore.*;
import CSNC.FWcore.KeyboardUtil.*;
import CSNC.FWmodel.*;
import CSNC.FWmodel.Sprite.*;
import CSNC.FWrender.*;
import CSNC.FWrender.Render.*;
import CSNC.FWrender.Texture.*;
import CSNC.FWrender.VertexArray.*;
import jatek.komponensek.Data;
import org.lwjgl.input.Keyboard;

/**
 * Jatek menurendszere
 * @author caiwan
 */

public class JatekMenu implements Scene{
    public enum MenuItem{
        MI_new_game,
        MI_instructions,
        MI_hall_of_fame,
        MI_options,
        MI_about,
        MI_exit;
    };
    
    private static final String menuElemek[] = {
            "New Game",
            "Instructions",
            "Hall of Fame",
            "Options",
            "About",
            "Exit"
    };
    
    private static final MenuItem items[] = {
            MenuItem.MI_new_game,
            MenuItem.MI_instructions,
            MenuItem.MI_hall_of_fame,
            MenuItem.MI_options,
            MenuItem.MI_about,
            MenuItem.MI_exit
    };
   
    private static final String credit = "Industrial Revolutioners 2013";
    
    private static final float textSize = .075f;
    private static final float textYPadding = .02f;
    private static final float textXPadding = -.4f;
    private static final float textYOffset = .2f;
    private static final float credit_textSize = .03f;
    
    /*
    private static final String kurzorFragmentshader = "";
    private static final String postFragmentshader = "";
    */
    
    private static final float cszin[] = {.2f, 5f, 1f};
    
    /////////////////////////////////////////////////////////////////////////
    private Sound selectItemSound, chooseItemSound;
    
    private Texture betukeszlet;
    private Text menuSzoveg[], creditSzoveg;
    private Sprite kurzor, hatterSprite;
    private Shader shader, hatterShader;
    
    private float menu_posYHatar[], menu_posY[];
    private float mouse_lastY;
    
    private int aktivElem;
    private boolean action; 
    
    public JatekMenu() {
    }
    
    @Override
    public void init() throws Exception {
        this.betukeszlet = new Texture();
        this.betukeszlet.buildFromResource(Data.TEXTURE_ROOT + Data.FONTSET1);
        
        int mlen = JatekMenu.menuElemek.length;
        menuSzoveg = new Text[mlen];
        menu_posYHatar = new float[mlen];
        menu_posY = new float[mlen];
        
        float p = 0f;
        for (int i= 0; i<mlen; i++){
            menuSzoveg[i] = new Text();
            menuSzoveg[i].setTexture(0,betukeszlet);
            menuSzoveg[i].setSize(textSize, textSize);
            p = textYOffset + (textSize + textYPadding) * i;
            menuSzoveg[i].setPosition(0f, p);
            menuSzoveg[i].setPadding(textXPadding);
            menuSzoveg[i].setOrignAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_CETER);
            menuSzoveg[i].setPositionAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_CETER);
            menuSzoveg[i].setText(JatekMenu.menuElemek[i]);
            
            menu_posY[i] = p - textYPadding/2f;
            menu_posYHatar[i] = textYOffset+(textSize + textYPadding) * i;
        }
        
        creditSzoveg = new Text();
        creditSzoveg.setSize(credit_textSize,2f*credit_textSize);
        creditSzoveg.setTexture(0, betukeszlet);
        creditSzoveg.setText(credit);
        creditSzoveg.setPadding(textXPadding);
        creditSzoveg.setOrignAlignmentMode(VerticalAlignmentE.VA_BOTTOM, Sprite.HorizontalAlignemntE.HA_LEFT);
        creditSzoveg.setPositionAlignmentMode(VerticalAlignmentE.VA_BOTTOM, Sprite.HorizontalAlignemntE.HA_LEFT);
        
        shader = new Shader();
        shader.createShader(Data.hdrVertexShader, Data.szovegFramgnetShader);
        
        // #hatter
        hatterShader = new Shader();
        hatterShader.createShader(Data.hdrVertexShader, Data.hatterFramgnetShader);
        
        hatterSprite = new Sprite();
        hatterSprite.setupFullscreenQuad();
        
        kurzor = new Sprite();
        kurzor.setSize(Render.getRenderAspectRatio(), textSize + textYPadding);
        kurzor.setOrignAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_LEFT);
        kurzor.setPositionAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_LEFT);
        kurzor.setAlpha(.2f);
        
        try {
            // hangok
            selectItemSound = new Sound();
            selectItemSound.loadFromResource(false, "OGG", Data.SFX_ROOT + Data.SOUND_CRSR);
        
            chooseItemSound = new Sound();
            chooseItemSound.loadFromResource(false, "OGG", Data.SFX_ROOT + Data.SOUND_SELECT);
        } catch (Exception e){
            throw e;
        }
    }   
    
    @Override
    public void mainloop(float time) {
        this.action = false;
        
        // eger
        float my = MouseUtil.getMouseY();
        boolean mpresed = MouseUtil.getMouseLPressed();
        
        // 
        boolean escPressed = (KeyboardUtil.getKeyStatus(Keyboard.KEY_ESCAPE) == KeyStatusE.KS_down);
        boolean enterPressed = (KeyboardUtil.getKeyStatus(Keyboard.KEY_RETURN) == KeyStatusE.KS_down);
        boolean downPressed = (KeyboardUtil.getKeyStatus(Keyboard.KEY_DOWN) == KeyStatusE.KS_down);
        boolean upPressed = (KeyboardUtil.getKeyStatus(Keyboard.KEY_UP) == KeyStatusE.KS_down);
        //System.out.println(my);
        
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
        
        hatterSprite.draw();
        hatterShader.unbind();
        
        shader.bind();
        shader.seti("tex0", 0);
        
        int menulen = menuSzoveg.length;
        int aktivelem = aktivElem % menulen;
        if (Math.abs(mouse_lastY - my) > 0.0000015) //ha az eger elmozdulasa a FLOAT PONTOSSAGI TAROMANYAN BELUL van
        {
            for(int i=0; i<menu_posYHatar.length; i++){
                if (i<menulen-1){
                    if (menu_posYHatar[i]<my && menu_posYHatar[i+1]>my){
                        aktivelem = i;
                        break;
                    }
                } else if (menu_posYHatar[i]<my) aktivelem = i;
                if (my>1f-0.0000015f) aktivelem = 0;
            }
        } else {
            if (upPressed) aktivelem--;
            else if (downPressed) aktivelem++;
            
            if (aktivelem < 0) aktivelem = menulen-1;
            aktivelem = aktivelem % menulen;
        }
        
        for(int i=0; i<menulen; i++){
            if (aktivelem == i) 
                menuSzoveg[i].setColor(cszin[0], cszin[1], cszin[2]);
            else
                menuSzoveg[i].setColor(1, 1, 1);
            
            menuSzoveg[i].draw();
        }
        
        kurzor.setPosition(0, menu_posY[aktivelem]);
        kurzor.draw();
        
        creditSzoveg.draw();
        
        Render.setBlend(BlendingConditionE.BC_ONE, BlendingConditionE.BC_ZERO);
        Render.switchBlend(false);
        
        shader.unbind();
        
        if (mpresed || enterPressed || escPressed){    
            this.action = true;
            if (escPressed) aktivelem = 5;
            else 
                chooseItemSound.play();
        }
        
        if (aktivelem != this.aktivElem)
            selectItemSound.play();
        
        this.aktivElem = aktivelem;
        mouse_lastY = my;
    }
    
    public boolean isAction(){
        return this.action;
    }
    
    public MenuItem getSelectedItem(){
        return JatekMenu.items[this.aktivElem];
    }
  
}
