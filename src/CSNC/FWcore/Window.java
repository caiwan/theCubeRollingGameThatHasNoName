/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CSNC.FWcore;

import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

/**
 *
 * @author caiwan
 */
public class Window {
    private int width = 0;
    private int height = 0;
    
    private boolean isFullscreen;
    private boolean isCreated;
    private boolean exitMessage; //ez amugy is el van fedve.
    
    public Window(int _width, int _height, boolean isFullscreen)  {
        this.exitMessage = false;
        this.width = _width;
        this.height = _height;
        this.isFullscreen = isFullscreen;
    }
    
    public void setDisplayMode(int width, int height, boolean fullscreen) {
        if ((Display.getDisplayMode().getWidth() == width) && 
            (Display.getDisplayMode().getHeight() == height) && 
            (Display.isFullscreen() == fullscreen)
             && isCreated) {
                return;
        }

        try {
            DisplayMode targetDisplayMode = null;

            if (fullscreen) {
                DisplayMode[] modes = Display.getAvailableDisplayModes();
                int freq = 0;

                for (int i=0;i<modes.length;i++) {
                    DisplayMode current = modes[i];

                    if ((current.getWidth() == width) && (current.getHeight() == height)) {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                            if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                                targetDisplayMode = current;
                                freq = targetDisplayMode.getFrequency();
                            }
                        }

                        if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
                            (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                                targetDisplayMode = current;
                                break;
                        }
                    }
                }
            } else {
                targetDisplayMode = new DisplayMode(width,height);
            }

            if (targetDisplayMode == null) {
                System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
                return;
            }

            Display.setDisplayMode(targetDisplayMode);
            Display.setFullscreen(fullscreen);

        } catch (LWJGLException e) {
            System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
        }
    }
    
    public void create() throws LWJGLException{
        isCreated = false;
        Display.setDisplayMode(new DisplayMode(width,height));
        Display.create();
        this.setDisplayMode(width, height, isFullscreen);
        isCreated = true;
        
        Display.makeCurrent();
        
        Keyboard.create();
    }
    
    public void destroy(){
        Display.destroy();
        Keyboard.destroy();
    }
   
    // Mivel itt nincs callback proc, ezert mashogy kell megoldani
    public void processKeyboard() {
        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            exitMessage = true;
        }
    }
    
    public void update(){
        Display.update();
        exitMessage = Display.isCloseRequested();
    }
    
    public int getWidth(){
        return Display.getWidth();
    }
    
    public int getHeight(){
        return Display.getHeight();
    }
    
    public boolean getExitMessage() {return this.exitMessage;}
}

