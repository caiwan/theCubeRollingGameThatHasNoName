/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jatek.komponensek.vezerles;

import java.io.*;

import CSNC.FWcore.*;
import CSNC.FWcore.Core.*;
import CSNC.FWcore.KeyboardUtil.*;
import CSNC.FWmodel.*;
import CSNC.FWmodel.Sprite.*;
import CSNC.FWrender.*;
import CSNC.FWrender.Render.*;
import CSNC.FWrender.Texture.*;
import CSNC.FWrender.VertexArray.*;
import jatek.komponensek.Data;
import org.lwjgl.input.Keyboard;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.*;

import org.w3c.dom.Document;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

/**
 *
 * @author caiwan
 */
public class JatekOptions  implements Scene{
    
    ///////////////////////////////////////////////////////////////////////////
    // XML Tokenek
    ///////////////////////////////////////////////////////////////////////////
    
    private static final String XML_TOKEN_config      = "config";
    private static final String XML_TOKEN_resolution  = "resolution";
    private static final String XML_TOKEN_w           = "width";
    private static final String XML_TOKEN_h           = "height";
    private static final String XML_TOKEN_fullscreen  = "fullscreen";   
    private static final String XML_TOKEN_aspectRatio = "aspectRatio";
    private static final String XML_TOKEN_render      = "render";
    private static final String XML_TOKEN_postprocess = "postprocess";
    
    ///////////////////////////////////////////////////////////////////////////
    // Static
    ///////////////////////////////////////////////////////////////////////////
    
    private static ScreenResoultion screen_res  = ScreenResoultion.SR_640_480;
    private static AspectRatio      screen_ar   = AspectRatio.AR_16_9;
    private static boolean          fullscreen  = false;
    private static boolean          postprocess = true;
    
    /**
     * 
     * @param res
     * @param ar
     * @param isfullscreen
     * @param postprocess 
     */
    
    public static void chageSettings(ScreenResoultion res, AspectRatio ar, boolean isfullscreen, boolean postprocess){
        screen_res  = res;
        screen_ar   = ar;
        fullscreen  = isfullscreen;
        //postprocess = postprocess;
    }
    
    /**
     * 
     * @param path
     * @return 
     */
    public static boolean loadConfigFile(String path){
        try{
            File fp = new File(path);
            if (!fp.exists()) throw new FileNotFoundException();
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fp);
            doc.getDocumentElement().normalize();
            
            if (!XML_TOKEN_config.equals(doc.getDocumentElement().getNodeName())) return false;
            
            NodeList nodeList;
            Element elem;
            
            nodeList = doc.getElementsByTagName(XML_TOKEN_resolution);
            if (nodeList.getLength() != 1) return false;
            
            elem = (Element)nodeList.item(0);
            
            try {
                /*
                Integer w = new Integer(elem.getAttributeNode("width").getValue());
                Integer h = new Integer(elem.getAttributeNode("height").getValue());
                String fscreen = (elem.getAttributeNode("fullscreen").getValue());
                */
                int w = XMLToolkit.AttribToInt(elem.getAttributeNode(XML_TOKEN_w));
                int h = XMLToolkit.AttribToInt(elem.getAttributeNode(XML_TOKEN_h));
                
                /*
                if (fscreen.equals("true"))
                    fullscreen = true;
                else if (fscreen.equals("false"))
                    fullscreen = false;
                else 
                    return false;
                */
                fullscreen = XMLToolkit.AttribToBoolean(elem.getAttributeNode(XML_TOKEN_fullscreen));
                
                ScreenResoultion res_result = null;
                for(ScreenResoultion res : ScreenResoultion.values()){
                    if (res.getH() == h && res.getW() == w){
                        res_result = res; 
                        break;
                    }
                }
                
                if (res_result == null) return false;
                screen_res = res_result;
                
            } catch (Exception e){
                return false;
            }
            
            nodeList = doc.getElementsByTagName(XML_TOKEN_aspectRatio);
            if (nodeList.getLength() != 1) return false;
            
            elem = (Element)nodeList.item(0);
            
            try {
                //Integer w = new Integer(elem.getAttributeNode("width").getValue());
                //Integer h = new Integer(elem.getAttributeNode("height").getValue());
                
                int w = XMLToolkit.AttribToInt(elem.getAttributeNode(XML_TOKEN_w));
                int h = XMLToolkit.AttribToInt(elem.getAttributeNode(XML_TOKEN_h));
                
                AspectRatio as_result = null;
                for(AspectRatio as : AspectRatio.values()){
                    if (as.getH() == h && as.getW() == w){
                        as_result = as; 
                        break;
                    }
                }
                
                if (as_result == null) return false;
                screen_ar = as_result;
                
            } catch (Exception e){
                return false;
            }
            
        } catch (FileNotFoundException e) {
            return false;
        } catch (Exception e){
            //e.printStackTrace();
            return false;
        }
        return true;
    }    
    
    /**
     * 
     * @param path
     * @return 
     */
    public static boolean saveConfigFile(String path){
        try {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
 
		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement(XML_TOKEN_config);
		doc.appendChild(rootElement);
                
                Element elem;
                Attr attr;
                
		// staff elements
		elem = doc.createElement(XML_TOKEN_resolution);
                elem.setAttribute(XML_TOKEN_w,          "".format("%d", screen_res.getW())); // .toString nemjo
                elem.setAttribute(XML_TOKEN_h,          "".format("%d", screen_res.getH()));
                elem.setAttribute(XML_TOKEN_fullscreen, XMLToolkit.BooleanToToken(fullscreen, false));
		rootElement.appendChild(elem);
                
                elem = doc.createElement(XML_TOKEN_aspectRatio);
                elem.setAttribute(XML_TOKEN_w, "".format("%d", screen_ar.getW()));
                elem.setAttribute(XML_TOKEN_h, "".format("%d", screen_ar.getH()));
                rootElement.appendChild(elem);
                
                elem = doc.createElement(XML_TOKEN_render);
                elem.setAttribute(XML_TOKEN_postprocess, XMLToolkit.BooleanToToken(postprocess, false));
                rootElement.appendChild(elem);
                
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(path));
 
		transformer.transform(source, result);
 
		System.out.println("File saved!");
 
	  } catch (ParserConfigurationException pce) {
		pce.printStackTrace();
	  } catch (TransformerException tfe) {
		tfe.printStackTrace();
	  }
        
            return true;
    }
    
    public static ScreenResoultion getScreenRes(){return screen_res;}
    public static AspectRatio getScreenAR(){return screen_ar;}
    public static boolean getScreenFullscreen(){return fullscreen;}
    
    ///////////////////////////////////////////////////////////////////////////
    // Class
    ///////////////////////////////////////////////////////////////////////////
    private static final int KISCICA = 3; //:3
    private static final String OPT_elemek[] = {
        "Screen Resolution",
        "Aspect Ratio",
        "Fullscreen",
        "Apply",
        "Save",
        "Back"
    };
    
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
    private Text menuSzoveg[], valueSzoveg[];
    private Sprite kurzor, hatterSprite;
    private Shader shader, hatterShader;
    
    private float menu_posYHatar[], menu_posY[];
    private float mouse_lastY;
    
    private int aktivElem;
    private boolean action, exit; 
    
    private int res_max, ar_max, res_current, ar_current;
    private String[] res_text, ar_text;
    
    private AspectRatio ar_lookup[];
    private ScreenResoultion res_lookup[];
    
    public JatekOptions() {
        res_max = ScreenResoultion.values().length;
        ar_max  = AspectRatio.values().length;
        
        res_text = new String[res_max];
        ar_text = new String[res_max];
        
        res_lookup = ScreenResoultion.values();
        ar_lookup = AspectRatio.values();
    }
    
    @Override
    public void init() throws Exception {
        this.betukeszlet = new Texture();
        this.betukeszlet.buildFromResource(Data.TEXTURE_ROOT + Data.FONTSET1);
        
        int mlen = JatekOptions.OPT_elemek.length;
        menuSzoveg = new Text[mlen];
        menu_posYHatar = new float[mlen];
        menu_posY = new float[mlen];
        
        valueSzoveg = new Text[KISCICA];
        
        float p = 0f;
        int i=0; 
        for (i= 0; i<mlen; i++){
            menuSzoveg[i] = new Text();
            menuSzoveg[i].setTexture(0,betukeszlet);
            menuSzoveg[i].setSize(textSize, textSize);
            p = textYOffset + (textSize + textYPadding) * i;
            menuSzoveg[i].setPosition(0f, p);
            menuSzoveg[i].setPadding(textXPadding);
            
            if (i<KISCICA){
                menuSzoveg[i].setOrignAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_LEFT);
                menuSzoveg[i].setPositionAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_LEFT);
            }else {
                menuSzoveg[i].setOrignAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_CETER);
                menuSzoveg[i].setPositionAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_CETER);
            }
            menuSzoveg[i].setText(JatekOptions.OPT_elemek[i]);
            
            menu_posY[i] = p - textYPadding/2f;
            menu_posYHatar[i] = textYOffset+(textSize + textYPadding) * i;
        }
        
        for (i=0; i<KISCICA; i++){
            p = textYOffset + (textSize + textYPadding) * i;
            valueSzoveg[i] = new Text();
            valueSzoveg[i].setTexture(0, betukeszlet);
            valueSzoveg[i].setText("LOL MI");
            valueSzoveg[i].setSize(textSize, textSize);
            valueSzoveg[i].setPosition(0, p);
            valueSzoveg[i].setPadding(textXPadding);
            valueSzoveg[i].setOrignAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_RIGHT);
            valueSzoveg[i].setPositionAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_RIGHT);
        }
        
        // #hatter
        hatterShader = new Shader();
        hatterShader.createShader(Data.hdrVertexShader, Data.hatterFramgnetShader);
        
        hatterSprite = new Sprite();
        hatterSprite.setupFullscreenQuad();
        
        shader = new Shader();
        shader.createShader(Data.hdrVertexShader, Data.szovegFramgnetShader);
        
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
        
        this.resetTimer(0.f);
    }   
    
    private int res_index,  ar_index;
    private boolean fullscreen_index;
    
    @Override
    public void mainloop(float time) {
        // eger
        float my = MouseUtil.getMouseY();
        boolean l_presed = MouseUtil.getMouseLPressed();
        boolean r_presed = MouseUtil.getMouseRPressed();
        
        // 
        boolean escPressed = (KeyboardUtil.getKeyStatus(Keyboard.KEY_ESCAPE) == KeyStatusE.KS_down);
        boolean enterPressed = (KeyboardUtil.getKeyStatus(Keyboard.KEY_RETURN) == KeyStatusE.KS_down);
        boolean downPressed  = (KeyboardUtil.getKeyStatus(Keyboard.KEY_DOWN) == KeyStatusE.KS_down);
        boolean upPressed    = (KeyboardUtil.getKeyStatus(Keyboard.KEY_UP) == KeyStatusE.KS_down);
        boolean leftPressed = (KeyboardUtil.getKeyStatus(Keyboard.KEY_LEFT) == KeyStatusE.KS_down);
        boolean rightPressed = (KeyboardUtil.getKeyStatus(Keyboard.KEY_RIGHT) == KeyStatusE.KS_down);
        
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
            if (aktivelem == i){
                menuSzoveg[i].setColor(cszin[0], cszin[1], cszin[2]);
            }
            else{
                menuSzoveg[i].setColor(1, 1, 1);
            }
            menuSzoveg[i].draw();
        }
        
        for (int i=0;i<valueSzoveg.length; i++){
            valueSzoveg[i].draw();
        }
        
        kurzor.setPosition(0, menu_posY[aktivelem]);
        kurzor.draw();
        
        Render.setBlend(BlendingConditionE.BC_ONE, BlendingConditionE.BC_ZERO);
        Render.switchBlend(false);
        
        shader.unbind();
        
        if (escPressed || l_presed || r_presed || enterPressed || leftPressed || rightPressed){    
            this.action = true;
        }
        
        if (aktivelem != aktivElem) selectItemSound.play();
        
        this.aktivElem = aktivelem;
        mouse_lastY = my;
        
        if (this.action){
            // ekson
            switch (this.aktivElem){
                case 0: // res
                    if (l_presed || enterPressed || leftPressed)
                        res_index ++;
                    if (r_presed || rightPressed)
                        res_index --;
                    
                    if (res_index >= res_max) res_index = res_max-1;
                    if (res_index < 0) res_index = 0;
                    
                    valueSzoveg[0].setText(res_text[res_index]);
                    break;
                
                case 1: //ar
                    if (l_presed || enterPressed || leftPressed)
                        ar_index ++;
                    if (r_presed || rightPressed)
                        ar_index --;
                    
                    if (ar_index >= ar_max) ar_index = ar_max-1;
                    if (ar_index < 0) ar_index = 0;
                    
                    valueSzoveg[1].setText(ar_text[ar_index]);
                    break;
                    
                case 2: //fc
                   if (l_presed || r_presed || enterPressed || leftPressed || rightPressed)
                        fullscreen_index = !fullscreen_index;
                    
                    valueSzoveg[2].setText("".format("%s", fullscreen_index));
                    break;
                    
                case 3: //apply
                    changeRes = true;
                    JatekOptions.chageSettings(res_lookup[res_index], ar_lookup[ar_index], fullscreen_index, postprocess);
                    break;
                    
                case 4: //save
                    changeRes = true;
                    JatekOptions.chageSettings(res_lookup[res_index], ar_lookup[ar_index], fullscreen_index, postprocess);
                    JatekOptions.saveConfigFile(Data.DATA_ROOT + Data.CONFIG_FILE);
                    break;
                    
                case 5:
                    exit = true;
                    break;
            }
            
            chooseItemSound.play();
            
            if (escPressed) exit = true;
            
            // torles
            this.action = false;
        }
    }
    
    public boolean isExit(){
        return exit;
    }
    
    public void resetTimer(float time){
        exit = false;
        action = false;
        
        for(int i=0; i<res_max; i++){
            ScreenResoultion sr = ScreenResoultion.values()[i];
            res_text[i] = "".format("%d x %d", sr.getW(), sr.getH());
            if(sr == JatekOptions.screen_res) res_current = i;
        }
        
        for(int i=0; i<ar_max; i++){
            AspectRatio ar = AspectRatio.values()[i];
            ar_text[i] = "".format("%d:%d", ar.getW(), ar.getH());
            if (ar == JatekOptions.screen_ar) ar_current = i;
        }
        
        valueSzoveg[0].setText(res_text[res_current]);
        valueSzoveg[1].setText(ar_text[ar_current]);
        valueSzoveg[2].setText("".format("%s",fullscreen));
        
        res_index = res_current;  
        ar_index = ar_current; 
        fullscreen_index = fullscreen;
        
        aktivElem = 0;
    }
    
    private boolean changeRes;
    public boolean isChangeRes(){
        boolean b = this.changeRes;
        this.changeRes = false;
        return b;
    }
}
