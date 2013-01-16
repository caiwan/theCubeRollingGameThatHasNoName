/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CSNC.FWcore;

import org.lwjgl.input.Keyboard;

/**
 * http://ninjacave.com/lwjglbasics2
 * 
 * @author caiwan
 */
public class KeyboardUtil {
    public enum KeyStatusE{
        KS_null,
        KS_down,
        KS_up
    };
    
    private static char keyCharsetLUT[];
    private static KeyStatusE keyMap[];
    
    /**
     * Lookup table bill. kod -> karakter
     */
    private static void buildLookupTable(){
        if (keyCharsetLUT == null) keyCharsetLUT = new char[256];
        else return;
        
        char[] p = keyCharsetLUT; //kicsit rovidebb kod... 
        p[Keyboard.KEY_0] = '0';
        p[Keyboard.KEY_1] = '1';
        p[Keyboard.KEY_2] = '2';
        p[Keyboard.KEY_3] = '3';
        p[Keyboard.KEY_4] = '4';
        p[Keyboard.KEY_5] = '5';
        p[Keyboard.KEY_6] = '6';
        p[Keyboard.KEY_7] = '7';
        p[Keyboard.KEY_8] = '8';
        p[Keyboard.KEY_9] = '9';
        p[Keyboard.KEY_A] = 'a';
        p[Keyboard.KEY_B] = 'b';
        p[Keyboard.KEY_C] = 'c';
        p[Keyboard.KEY_D] = 'd';
        p[Keyboard.KEY_E] = 'e';
        p[Keyboard.KEY_F] = 'f';
        p[Keyboard.KEY_G] = 'h';
        p[Keyboard.KEY_H] = 'i';
        p[Keyboard.KEY_J] = 'j';
        p[Keyboard.KEY_K] = 'k';
        p[Keyboard.KEY_L] = 'l';
        p[Keyboard.KEY_M] = 'm';
        p[Keyboard.KEY_N] = 'n';
        p[Keyboard.KEY_O] = 'p';
        p[Keyboard.KEY_Q] = 'q';
        p[Keyboard.KEY_R] = 'r';
        p[Keyboard.KEY_S] = 's';
        p[Keyboard.KEY_T] = 't';
        p[Keyboard.KEY_U] = 'u';
        p[Keyboard.KEY_V] = 'v';
        p[Keyboard.KEY_X] = 'x';
        p[Keyboard.KEY_Y] = 'y';
        p[Keyboard.KEY_Z] = 'z';
        p[Keyboard.KEY_SPACE] = ' ';
        p[Keyboard.KEY_MINUS] = '-';
        p[Keyboard.KEY_ADD] = '+';
        p[Keyboard.KEY_EQUALS] = '=';
        p[Keyboard.KEY_APOSTROPHE] = '\'';
        p[Keyboard.KEY_COMMA] = ',';
        p[Keyboard.KEY_UNDERLINE] = '_';
        p[Keyboard.KEY_NUMPAD0] = '0';
        p[Keyboard.KEY_NUMPAD1] = '1';
        p[Keyboard.KEY_NUMPAD2] = '2';
        p[Keyboard.KEY_NUMPAD3] = '3';
        p[Keyboard.KEY_NUMPAD4] = '4';
        p[Keyboard.KEY_NUMPAD5] = '5';
        p[Keyboard.KEY_NUMPAD6] = '6';
        p[Keyboard.KEY_NUMPAD7] = '7';
        p[Keyboard.KEY_NUMPAD8] = '8';
        p[Keyboard.KEY_NUMPAD9] = '9';
        p[Keyboard.KEY_NUMPADCOMMA] = ',';
        p[Keyboard.KEY_NUMPADEQUALS] = '=';
    }
    
    public static void fetchKeys(){
        if (keyMap == null) keyMap = new KeyStatusE[256];
        
        for (int i=0; i<256; ++i) keyMap[i] = KeyStatusE.KS_null;
        
        int evt;
        while (Keyboard.next()) {
            evt = Keyboard.getEventKey();
            keyMap[evt] = Keyboard.getEventKeyState()?KeyStatusE.KS_down:KeyStatusE.KS_up;
        }
        
        MouseUtil.fetchMouse();
    }
    
    public static KeyStatusE getKeyStatus(int key){
        if (keyMap == null) return null;
            
        int kk = key % 256;
        KeyStatusE ks = keyMap[kk];
        keyMap[kk] = KeyStatusE.KS_null;
        return ks;
    }
    
    public static char getKeyChar(){
        char c = 0;
        if (keyMap == null) return c;
        
        for (int i=0; i<256; ++i) 
            if (getKeyStatus(i) == KeyStatusE.KS_down) {
                c = keyCharsetLUT[i]; 
                break;
            }
        return c;
    }
}
