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
public class JatekCredits implements Scene {
    private static final String greetings[] = {
        "::::::::::::::::::::::::::::::::::::::::",
        "   .            ___/\\____               ",
        "  / \\/\\     __  \\   __  /___            ",
        "  \\ /  \\   /\\_\\  \\  \\/___  /evolutioners",
        "   <    \\  \\/_/   \\  \\  /  \\            ",
        "    \\    \\         \\  \\ \\   \\/\\         ",
        "     )    \\__       \\  \\ \\  __/         ",
        "    /_______/ndustrial_/  \\/            ",
        "::::::::::::::::::::::::::::::::::::::::",
        "",
        "Proudly presents:","",
        "....:::....","",
        "The cube rollin game",
        "that has no name",
        "",
        "in",
        "2013",
        "",
        "::::::::::::::::::::::::::::::::::::::::",
        "",
        "Credits:",
        "----------------------------------------",
        "Code:",
        "Caiwan",
        "....:::....","",
        "Sound:",
        "Gabriel",
        "....:::....","",
        "Graphics and Design:",
        "Slapec",
        "Caiwan",
        "",
        "ASCII art:",
        "MR.EEH/Nerve",
        "(thank you again :) )",
        "",
        "----------------------------------------",
        "We would send our best wishes",
        "for the following persons and groups:",
        "....:::....",
        "",
    	"Agnes Achs",
        "Astroidea",
	"Bad Sectors",
	"Bluebox Cinematique GMBH", 
	"Brainstorm",
	"CaPaNNa",
	"Conspiracy",
        "Crowngear",
	"Digiatal Dynamite",
        "Exceed",
	"Fresh!Mindworkz",
	"United Force",
	"Umlaut Design",
	"Lungcancer Entertaiment",
	"A Moire Misszio Bemutatja",
        "MU6K",
	"Rebels",
	"Resource",
	"Singular Crew",
	"Suprise!Productions",
        "",
        "----------------------------------------",
        "",
        "(c) Industrial Revolutoners 2009 - 2013",
        "",
        "http://ir.untergrund.net/",
        "",
        "----------------------------------------",
        "",
        "And don't forget to come",
        "Function!",
        "",
    };
    
    private static final float TEXT_Size = .07f;
    private static final float TEXT_YPadding = -.005f;
    private static final float TEXT_XPadding = -.4f;
    private static final float TEXT_XOffset = 1.01f;
    
    private static final float SCROLL_SPEED = 12;
    
    private Texture betukeszlet;
    private Text greetingsSzoveg[];
    private Shader shader;
    
    private Sprite hatterSprite;
    private Shader hatterShader;
    
    private int glen;
    private float time0;
    
    private boolean end;
    
    @Override
    public void init() {
        this.betukeszlet = new Texture();
        //this.betukeszlet.setMinFilter(TextureFilterE.TF_NEAREST);
        //this.betukeszlet.setMagFilter(TextureFilterE.TF_NEAREST);
        
        this.betukeszlet.buildFromResource(Data.TEXTURE_ROOT + Data.FONTSET1);
        
        glen = greetings.length;
        greetingsSzoveg = new Text[glen];
        for (int i= 0; i<glen; i++){
            greetingsSzoveg[i] = new Text();
            greetingsSzoveg[i].setTexture(0,betukeszlet);
            greetingsSzoveg[i].setSize(TEXT_Size, TEXT_Size);
            //greetingsSzoveg[i].setPosition(0f, textYOffset + (textSize + textYPadding) * i);
            greetingsSzoveg[i].setPadding(TEXT_XPadding);
            greetingsSzoveg[i].setOrignAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_CETER);
            greetingsSzoveg[i].setPositionAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_CETER);
            greetingsSzoveg[i].setText(greetings[i]);
        }
        
        shader = new Shader();
        shader.createShader(Data.hdrVertexShader, Data.szovegFramgnetShader);
        
        hatterShader = new Shader();
        hatterShader.createShader(Data.hdrVertexShader, Data.hatterFramgnetShader);
        
        hatterSprite = new Sprite();
        hatterSprite.setupFullscreenQuad();
    }

    @Override
    public void mainloop(float time) {
        end = false;
        float ttime = time - time0;
        
        // eger
        boolean mousePressed = MouseUtil.getMouseLPressed() || MouseUtil.getMouseRPressed();
        // billezet
        boolean returnPressed = KeyboardUtil.getKeyStatus(Keyboard.KEY_RETURN) == KeyStatusE.KS_down;
        boolean escPressed = KeyboardUtil.getKeyStatus(Keyboard.KEY_ESCAPE) == KeyStatusE.KS_down;
        
        Render.clearScene();        
        Render.setBlend(BlendingConditionE.BC_SRC_ALPHA, BlendingConditionE.BC_ONE_MINUS_SRC_ALPHA);
        
        hatterShader.bind();
        
        hatterShader.setfv("color0", Data.backgroudColor, 3);
        hatterShader.setfv("color1", Data.backgroudColor2, 3);
        
        //hatterShader.setf("resolution", 1000, 1000*Render.getRenderAspectRatio());
        hatterShader.setf("resolution", Render.getWidth(), Render.getHeight());
        hatterShader.setf("time", time);
        hatterShader.setf("yshift", -((time)/(20f)));
        
        hatterSprite.draw();
        
        hatterShader.unbind();
        
        shader.bind();
        shader.seti("tex0", 0);
        
        //Render.setBlend(BlendingConditionE.BC_SRC_ALPHA, BlendingConditionE.BC_ONE_MINUS_SRC_ALPHA);
        
        //for(Text elem : menuSzoveg) elem.draw();
        float p = 0;
        for (int i=0; i<glen; i++){
            p = ((TEXT_XOffset - ttime/SCROLL_SPEED) + (TEXT_Size + TEXT_YPadding) * i);
            greetingsSzoveg[i].setPosition(0f, p);
            if (p<1+TEXT_Size && p>0-TEXT_Size) //csak azt rajzolja ki, ami a screenen van
                greetingsSzoveg[i].draw();
        }
        
        Render.setBlend(BlendingConditionE.BC_ONE, BlendingConditionE.BC_ZERO);
        Render.switchBlend(false);
        
        shader.unbind();
        
        if ((p<0-(TEXT_Size*2f)) 
                || mousePressed
                || returnPressed 
                || escPressed
            ) 
            end = true;
    }

    public void resetTimer(float time) {
        time0 = time;
    }
    
    public boolean isExit(){
        return end;
    }
    
}
