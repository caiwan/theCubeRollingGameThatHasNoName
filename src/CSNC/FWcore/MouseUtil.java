package CSNC.FWcore;

import CSNC.FWcore.KeyboardUtil.KeyStatusE;
import org.lwjgl.input.Mouse;

/**
 * http://ninjacave.com/lwjglbasics2
 * 
 * @author caiwan
 */
public final class MouseUtil {
    
    private static KeyStatusE leftMouse, rightMouse;
    
    public static void fetchMouse(){
        //leftMouse = KeyStatusE.KS_null;
        //rightMouse = KeyStatusE.KS_null;
        /*
        if (Mouse.isButtonDown(0)) leftMouse = KeyStatusE.KS_down;
        if (Mouse.isButtonDown(1)) rightMouse = KeyStatusE.KS_down;
        */
        
        // get mouse event == rakas szar, nem mukodik.
        
        while (Mouse.next()){
            if (Mouse.getEventButton() == 0) 
                if (leftMouse != KeyStatusE.KS_down && getMouseLDown())
                    leftMouse = KeyStatusE.KS_down;
                else
                    leftMouse = KeyStatusE.KS_null;
            //else
                //leftMouse = KeyStatusE.KS_up;

            if (Mouse.getEventButton() == 1) 
                if (rightMouse != KeyStatusE.KS_down && getMouseRDown())
                    rightMouse = KeyStatusE.KS_down;
                else
                    rightMouse = KeyStatusE.KS_null;
            //else
                //rightMouse = KeyStatusE.KS_up;
        }
    }
    
    public static float getMouseX() {
        int x = Mouse.getX();
        return (float)x/(float)Core.getW();
    }
    
    public static float getMouseY() {
        int y = Mouse.getY();
        return 1f-(float)y/(float)Core.getH();
    }
    
    public static boolean getMouseLPressed(){
        KeyStatusE ks = leftMouse;
        /*
        if (ks != KeyStatusE.KS_down && getMouseLDown())
            return false;
        */
        leftMouse = KeyStatusE.KS_null;
        
        return ks == KeyStatusE.KS_down;
    }

     public static boolean getMouseRPressed(){
        KeyStatusE ks = rightMouse;
        /*
        if (ks != KeyStatusE.KS_down && getMouseRDown())
            return false;
        */
        rightMouse = KeyStatusE.KS_null;
        
        return ks == KeyStatusE.KS_down;
    }
     
    public static boolean getMouseLDown(){
        return Mouse.isButtonDown(0);
    }

     public static boolean getMouseRDown(){
        return Mouse.isButtonDown(1);
    }
     
    public static float getMouseDX() {
        int x = Mouse.getDX();
        return (float)x/(float)Core.getW();
    }
    
    public static float getMouseDY() {
        int y = Mouse.getDY();
        return 1f-(float)y/(float)Core.getH();
    }
     
     // TODO: hide/show mouse
}
