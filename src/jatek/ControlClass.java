/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jatek;

import CSNC.FWcore.*;
import CSNC.FWcore.Core.*;
import CSNC.FWmodel.*;
import CSNC.FWmodel.Sprite.*;
import CSNC.FWrender.*;
import CSNC.FWrender.Render.*;        
import CSNC.FWrender.Texture.*;
import jatek.komponensek.Data;
import jatek.komponensek.vezerles.*;
import jatek.komponensek.vezerles.JatekMenu.MenuItem;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author caiwan
 */

public class ControlClass {
    private static ScreenResoultion DEFAULT_screen_res = ScreenResoultion.SR_640_480;
    private static AspectRatio      DEFAULT_screen_ar = AspectRatio.AR_16_9;
    private static boolean          DEFAULT_fullscreen = false;
    
    private List<Scene> screenek; 
    
    private JatekIntro intro;
    private JatekMenu menu;
    private JatekInGame inGame;
    private JatekHelp help;
    private JatekHighScore highscore;
    private JatekOptions options;
    private JatekCredits credits;
    
    private Sound FiRG_RULEZ;
    
    private enum ActiveScreen {
        AS_intro,
        AS_menu,
        AS_ingame,
        AS_highscore,
        AS_instructions,
        AS_info,
        AS_options,
        AS_exit;
    };
    
    public void start(){
        this.screenek = new ArrayList<>();
        this.intro = new JatekIntro();         screenek.add(intro);
        this.menu = new JatekMenu();           screenek.add(menu);
        this.inGame = new JatekInGame();       screenek.add(inGame);
        this.help = new JatekHelp();           screenek.add(help);
        this.highscore = new JatekHighScore(); screenek.add(highscore);
        this.options = new  JatekOptions();    screenek.add(options);
        this.credits = new JatekCredits();     screenek.add(credits);
       
        // this.options ->  beallitasok betoltese, es olvasasa
        String configpath = Data.DATA_ROOT + Data.CONFIG_FILE;
        if (!JatekOptions.loadConfigFile(configpath)){
            JatekOptions.chageSettings(DEFAULT_screen_res, DEFAULT_screen_ar, DEFAULT_fullscreen, DEFAULT_fullscreen);
            JatekOptions.saveConfigFile(configpath);
            if (!JatekOptions.loadConfigFile(configpath))
                return;
        }
        
        Core.setRes(JatekOptions.getScreenRes(), JatekOptions.getScreenAR(), JatekOptions.getScreenFullscreen());
        Core.initFW();
        
        this.initLoader();
        this.drawLoader();
        
        try {
            // itt lesz majd loader bar
            for (Scene elem : screenek){
                this.drawLoader();
                elem.init();
            }
            
        } catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e);
            e.printStackTrace();
            Core.releaseFW(0x2); //nincs exit
            return;
        }
                
        float t=Core.getDeltaTime();
        boolean exitMessage = false;
        ActiveScreen as = ActiveScreen.AS_menu;
        
        do {
            Render.applyView();
            Render.clearScene();
            try {
                
                switch (as){
                    case AS_intro:
                        break;
                        
                    case AS_menu:
                        {
                            this.menu.mainloop(t);
                            if (this.menu.isAction()){
                                MenuItem mi = this.menu.getSelectedItem();
                                //if (mi == MenuItem.MI_exit) as = ActiveScreen.AS_exit;
                                switch (mi){
                                    case MI_new_game:
                                        this.inGame.resetGame();
                                        this.inGame.resetTimer(t);
                                        as = ActiveScreen.AS_ingame;
                                        break;
                                    case MI_instructions:
                                        this.help.resetTimer(t);
                                        as = ActiveScreen.AS_instructions;
                                        break;
                                    case MI_hall_of_fame:
                                        as = ActiveScreen.AS_highscore;
                                        break;
                                    case MI_options:
                                        options.resetTimer(t);
                                        as = ActiveScreen.AS_options;
                                        break;
                                    case MI_about:
                                        this.credits.resetTimer(t);
                                        as = ActiveScreen.AS_info;
                                        break;
                                    case MI_exit:
                                        as = ActiveScreen.AS_exit;
                                        break;
                                }
                            }
                        }   
                        break;
                        
                    case AS_ingame:
                        if (this.inGame.isExit())
                            as = ActiveScreen.AS_menu;                        
                        this.inGame.mainloop(t);
                        break;
                        
                    case AS_highscore:
                        break;
                        
                    case AS_instructions:
                        if (this.help.isExit())
                            as = ActiveScreen.AS_menu;    
                        help.mainloop(t);
                        break;
                        
                    case AS_options:
                        if (this.options.isChangeRes()){
                            Core.setRes(JatekOptions.getScreenRes(), JatekOptions.getScreenAR(), JatekOptions.getScreenFullscreen());
                            Core.setupWindow();
                        }
                        
                        if (this.options.isExit())
                            as = ActiveScreen.AS_menu;
                        options.mainloop(t);
                        break;
                    case AS_info:
                        this.credits.mainloop(t);
                        if (this.credits.isExit())
                            as = ActiveScreen.AS_menu;
                        break;
                        
                    case AS_exit:
                        exitMessage = true;
                        break;
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
                System.out.println(e);
                e.printStackTrace();
                Core.releaseFW(0x3); //nincs exit
                return;
            }
            
            t += Core.getDeltaTime();
            KeyboardUtil.fetchKeys();
            Render.flush();
            
        } while (!Core.peekWndMessage() && !exitMessage);
        
        Render.destroyRenderer();
        
        Core.releaseFW(0);
    }
    
    ///// loading screen
    private Texture loadingTex;
    private Sprite loadSprite;
    
    private void initLoader(){
        this.loadingTex = new Texture();
        this.loadingTex.buildFromResource(Data.TEXTURE_ROOT + "loader.png");
        
        this.loadSprite = new Sprite();
        this.loadSprite.setTexture(0, loadingTex);
        this.loadSprite.setSize(.75f, .75f);
        this.loadSprite.setOrignAlignmentMode(VerticalAlignmentE.VA_MIDDLE, HorizontalAlignemntE.HA_CETER);
        this.loadSprite.setPositionAlignmentMode(VerticalAlignmentE.VA_MIDDLE, HorizontalAlignemntE.HA_CETER);
    }
    
    private void drawLoader(){
        Render.applyView();
        Render.clearScene();
        
        Render.setBlend(BlendingConditionE.BC_SRC_ALPHA, BlendingConditionE.BC_ONE_MINUS_SRC_ALPHA);
        
        this.loadSprite.draw();
        
        Render.setBlend(BlendingConditionE.BC_ONE, BlendingConditionE.BC_ZERO);
        Render.switchBlend(false);
        
        Render.flush();
        Core.peekWndMessage();
    }
}
