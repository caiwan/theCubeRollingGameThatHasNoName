package jatek.komponensek.vezerles;

import CSNC.BuiltInResources.*;
import CSNC.FWcore.KeyboardUtil;
import CSNC.FWcore.KeyboardUtil.*;
import CSNC.FWcore.MouseUtil;
import CSNC.FWcore.Sound;
import CSNC.FWmath.VMath;
import CSNC.FWmodel.*;
import CSNC.FWmodel.Sprite.*;
import CSNC.FWrender.*;
import CSNC.FWrender.Render.*;
import CSNC.FWrender.Texture.*;
import CSNC.FWrender.VertexArray.*;
import jatek.komponensek.Data;
import jatek.komponensek.LevelLoader;
import java.nio.*;
import java.util.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;

/**
 *
 * @author caiwan
 */
public class JatekInGame implements Scene{    
    private static final int MAX_LEVEL_SIZE = 128;
    private static final int MAX_LIVES = 5;
    
    private static final float []LIGHT_ambient        = {.1f, .1f, .1f, 1f};
    private static final float []LIGHT_diffuse        = {.2f, .2f, .3f, 1f};
    private static final float []LIGT_specular        = {.3f, .3f, .1f, 1f};
    private static final float MATERIAL_shiniessLevel = 50.f; 
    private static final float MATERIAL_shiniess      = 25.f;
    
    private static final float PRE_colorCorrection  = 0.45f;
    private static final float POST_hdrExposure     = 4.f;
    private static final float POST_hdrBloomLevel   = 0.7f;

    // ... 
    private static final float FADE_TIME  = 2.5f;
    private static final float STEP_SPEED = .25f;
    private static final float FALL_GRAVITY = 1.25f;
    private static final float FALL_FADE_TIME = 5f;

    private static final float diffuseColor[][] = {
        {.9f, .9f, .9f, 1f}, //[0]  top    =  feher
        {.9f, .9f, .2f, 1f}, //[1]  bottom =  sarga
        {.9f, .4f, .2f, 1f}, //[2]  back   =  narancs
        {.7f, .1f, .2f, 1f}, //[3]  front  =  piros
        {.1f, .1f, 1.f, 1f}, //[4]  right  =  kek
        {.1f, .7f, .2f, 1f}, //[5]  left   =  zold
        {1.f, .2f, 1.f, 1f}  //[6]  joker  =  lila
    }; 

    private static final float defaultPlaneOrientation[][] = {
        { 0,  0,  1},  //top              [0]
        { 0,  0, -1},  //bottom           [1]
        { 0,  1,  0},  //back  = Y Uunit  [2]
        { 0, -1,  0},  //front            [3]
        { 1,  0,  0},  //right = X unit   [4]
        {-1,  0,  0}   //left             [5]
    };
 
    private static final float TEXT_SIZE = 0.05f;
    
    ///////////////////////////////////////////////
    
    private List<Level> palyak;
    private Level aktualisPalyaPtr;
    private Player jatekos;
    
    private int palyaIndex;
    private int elet, pontok, lepes, osszeg_szam;
    
    private boolean gameOver, exit;
    
    private Camera camera;
    private Texture betukeszlet;
    private Sprite fbo_sprite;
    private FBO fbo;
    private Shader shader, POST_hdrKorrekcio, szovegShader;
    private Text overlaySzoveg, pontokSzoveg, eletekSzoveg, lepesSzoveg;
    
    private Sound stepSound, dieSound, gameOverSound, nextLevelSound;
    
    public JatekInGame(){
        this.palyak = new ArrayList<>();
    }
    
    public void init() throws Exception /*throws Exception*/{
        int res = 0;
        this.camera = new Camera();
        /*
        this.textura = new Texture();
        this.textura.setIsGenerateMipmap(true);
        this.textura.setMinFilter(TextureFilterE.TF_LINEAR_MIPMAP_LINEAR);
        res = this.textura.buildFromResource(Data.TEXTURE_ROOT + "ir_duck.png");
        // if (res != 0) 
            //throw new Exception("Nem sikerult a texturamat betolteni :(");
        */
        
        this.shader = new Shader();

        //if (this.shader == null)
        //    System.out.println("HALALFEJES HIBA");
        
       res = this.shader.createShader(Data.vertexShader, Data.fragmentShader);
       if (res != 0)
           throw new Exception("Nem sikerult a shaderemet betolteni :(");
       
       this.fbo = new FBO(2, Data.FBO_SIZE, true);
       this.fbo_sprite = new Sprite();
       this.fbo_sprite.setTexture(0, this.fbo.getColorTexture(0));
       //this.fbo_sprite.setTexture(0, this.textura);
       //this.fbo_sprite.setupFullscreenQuad(Data.FBO_SIZE);
       this.fbo_sprite.setupFullscreenQuad();
       
       this.POST_hdrKorrekcio = new Shader();
       this.POST_hdrKorrekcio.createShader(Data.hdrVertexShader, Data.hdrFramgnetShader);
       
       this.betukeszlet = new Texture();
       /*
       this.betukeszlet.setMinFilter(TextureFilterE.TF_NEAREST);
       this.betukeszlet.setMagFilter(TextureFilterE.TF_NEAREST);
       */
       
       this.betukeszlet.buildFromResource(Data.TEXTURE_ROOT + "fontset.png");
       
       this.overlaySzoveg = new Text();
       this.overlaySzoveg.setTexture(0,betukeszlet);
       this.overlaySzoveg.setSize(2f*TEXT_SIZE, 2f*TEXT_SIZE);
       this.overlaySzoveg.setPadding(Data.textXPadding);
       this.overlaySzoveg.setOrignAlignmentMode(VerticalAlignmentE.VA_MIDDLE, Sprite.HorizontalAlignemntE.HA_CETER);
       this.overlaySzoveg.setPositionAlignmentMode(VerticalAlignmentE.VA_MIDDLE, Sprite.HorizontalAlignemntE.HA_CETER);
       
       this.lepesSzoveg = new Text();
       this.lepesSzoveg.setTexture(0,betukeszlet);
       this.lepesSzoveg.setSize(TEXT_SIZE, TEXT_SIZE);
       this.lepesSzoveg.setPadding(Data.textXPadding);
       this.lepesSzoveg.setOrignAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_CETER);
       this.lepesSzoveg.setPositionAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_CETER);
       
       this.eletekSzoveg = new Text();
       this.eletekSzoveg.setTexture(0,betukeszlet);
       this.eletekSzoveg.setSize(TEXT_SIZE, TEXT_SIZE);
       this.eletekSzoveg.setPadding(Data.textXPadding);
       /*
       this.eletek.setOrignAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_LEFT);
       this.eletek.setPositionAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_LEFT);
       */
       
       this.pontokSzoveg = new Text();
       this.pontokSzoveg.setTexture(0,betukeszlet);
       this.pontokSzoveg.setSize(TEXT_SIZE, TEXT_SIZE);
       this.pontokSzoveg.setPadding(Data.textXPadding);
       this.pontokSzoveg.setOrignAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_RIGHT);
       this.pontokSzoveg.setPositionAlignmentMode(VerticalAlignmentE.VA_TOP, Sprite.HorizontalAlignemntE.HA_RIGHT);
       
       this.szovegShader = new Shader();
       this.szovegShader.createShader(Data.hdrVertexShader, Data.szovegFramgnetShader);
       
       try {
            this.stepSound = new Sound();
            this.stepSound.loadFromResource(false, "OGG", Data.SFX_ROOT + Data.SOUND_MOVE);
       
            this.dieSound = new Sound();
            this.dieSound.loadFromResource(false, "OGG", Data.SFX_ROOT + Data.SOUND_DIE);
       
            this.gameOverSound = new Sound();
            this.gameOverSound.loadFromResource(false, "OGG", Data.SFX_ROOT + Data.SOUND_GAME_OVER);
            
            this.nextLevelSound = new Sound();
            this.nextLevelSound.loadFromResource(false, "OGG", Data.SFX_ROOT + Data.SOUND_WIN);
            
            this.loadLevels();
       } catch (Exception e) {
            throw e;
       }
    }
    
    private static final float CAM_ALPHA_SCALE = 360;
    private static final float CAM_BETA_SCALE  = 75;
    
    private static final float CAM_ALPHA_SHIFT = 180;
    private static final float CAM_BETA_SHIFT  = CAM_BETA_SCALE/2 + 5;
    
    private static final float CAM_DIST_SCALE  = 30;
    private static final float CAM_DIST_SHIFT  = 5 + CAM_DIST_SCALE;
    
    private static final float SEG_D_AREA = 0.01f;
    
    private float cam_szogAlpha, cam_szogBeta, cam_tavolasg;
    private float mx0, my0, time0, szovegTime0;
    private boolean mldown, mrdown;
    
    private int escPressed;
    
    private int utolso_irany, utolso_status; 
    
    public void mainloop(float time) /*throws Exception*/{
        //camera.setFOV(Camera.DEFAULT_FOV + 15f*(float)Math.sin(time*.001));
        
        // kijbord
        boolean up_pressed    = KeyboardUtil.getKeyStatus(Keyboard.KEY_UP)    == KeyStatusE.KS_down;
        boolean down_pressed  = KeyboardUtil.getKeyStatus(Keyboard.KEY_DOWN)  == KeyStatusE.KS_down;
        boolean left_pressed  = KeyboardUtil.getKeyStatus(Keyboard.KEY_LEFT)  == KeyStatusE.KS_down;
        boolean right_pressed = KeyboardUtil.getKeyStatus(Keyboard.KEY_RIGHT) == KeyStatusE.KS_down;
        
        boolean W_pressed = KeyboardUtil.getKeyStatus(Keyboard.KEY_W) == KeyStatusE.KS_down;
        boolean S_pressed = KeyboardUtil.getKeyStatus(Keyboard.KEY_S) == KeyStatusE.KS_down;
        boolean A_pressed = KeyboardUtil.getKeyStatus(Keyboard.KEY_A) == KeyStatusE.KS_down;
        boolean D_pressed = KeyboardUtil.getKeyStatus(Keyboard.KEY_D) == KeyStatusE.KS_down;
        
        if ( KeyboardUtil.getKeyStatus(Keyboard.KEY_ESCAPE) == KeyStatusE.KS_down) escPressed++;
        if (escPressed>1) exit = true;
        
        // maus
        float mx = MouseUtil.getMouseX();
        float my = MouseUtil.getMouseY();
        
        if (MouseUtil.getMouseLPressed()){
            mldown = true;
            escPressed = 0;
        }
        
        if (mldown && MouseUtil.getMouseLDown()){
            cam_szogAlpha += mx - mx0;
            cam_szogBeta  += my - my0;
        } else {
            mldown = false;
        }
        
        if (MouseUtil.getMouseRPressed()){
            mrdown = true;
            escPressed = 0;
        }
        
        if (mrdown && MouseUtil.getMouseRDown()){
            cam_szogAlpha += mx - mx0;
            cam_tavolasg  += my - my0;    
        } else {
            mrdown = false;
        }
        
        //if (cam_szogAlpha > .5f) cam_szogAlpha = -.5f + cam_szogAlpha;
        if (cam_szogBeta  > .5f) cam_szogBeta  = .5f;
        if (cam_tavolasg  > .5f)  cam_tavolasg = .5f;
            
        //if (cam_szogAlpha < -.5f) cam_szogAlpha-=  .5f - cam_szogAlpha;
        if (cam_szogBeta  < -.5f) cam_szogBeta  = -.5f;
        if (cam_tavolasg  < -.5f)  cam_tavolasg = -.5f;
        
        my0 = my;
        mx0 = mx;
        
        // kamera beallitas - szogek
        float r = CAM_DIST_SHIFT + cam_tavolasg * CAM_DIST_SCALE;
        float angle  = ((CAM_ALPHA_SHIFT + CAM_ALPHA_SCALE * cam_szogAlpha) / 180f) * (float)Math.PI; // vizszintes
        float angle2 = ((CAM_BETA_SHIFT  + CAM_BETA_SCALE  * cam_szogBeta ) / 180f) * (float)Math.PI; // fuggoleges
        float ca2 = (float)Math.cos(angle2);
        
        // karakter mozgatasa
        int irany = utolso_irany; // ide jon a kamera orientacio kooigalasa
        int p = 0;
                
        float k = angle / (2*(float)Math.PI); k -= (float)Math.floor(k);
        
        if (k>.375+SEG_D_AREA && k<.625-SEG_D_AREA) irany = 0;
        if (k>.625+SEG_D_AREA && k<.875-SEG_D_AREA) irany = 1;
        if (k>.875+SEG_D_AREA || k<.125-SEG_D_AREA) irany = 2; // itt korbe er a kor
        if (k>.125+SEG_D_AREA && k<.375-SEG_D_AREA) irany = 3;
        
        utolso_irany = irany;
        
        //p = irany;
        
        boolean action = !jatekos.isActive();
        if      (up_pressed    || W_pressed) irany += 0;
        else if (down_pressed  || S_pressed) irany += 2;
        else if (left_pressed  || A_pressed) irany += 3;
        else if (right_pressed || D_pressed) irany += 1;
        else action = false;
        
        irany = irany%4; // ne csorduljon tul
        
        if (action){
            switch (irany){
                case 0: jatekos.stepNorth(); break;
                case 2: jatekos.stepSouth(); break;
                case 3: jatekos.stepWest(); break;
                case 1: jatekos.stepEast(); break;
            }
            this.lepes ++;
            escPressed = 0;
        }
        
        int gamestatus = this.aktualisPalyaPtr.testXY(
                this.jatekos.getGridX(),
                this.jatekos.getGridY(),
                this.jatekos.getBottomPlane()
            );

        
        if (gamestatus == 0){
            if (!jatekos.kill())
            {
                this.elet --;
                
                if (this.osszeg_szam - this.lepes > 0)
                    this.osszeg_szam = (this.osszeg_szam - this.lepes) / 2;
                else 
                    this.osszeg_szam = (int)((float)(this.osszeg_szam - this.lepes) * 1.5f);
                
                this.lepes = 0;
                
                if (elet == 0){
                    gameOver = true;
                    exit = true;
                    
                }else{ 
                    jatekos.reset();
                    jatekos.setStartPosGrid(aktualisPalyaPtr.getStartX(), aktualisPalyaPtr.getStartY());
                    
                }
            } else {
                if (action) this.lepes--;
                szovegTime0 = time; //- (STEP_SPEED * FALL_FADE_TIME);
            }
            
            if (elet == 0)
                this.overlaySzoveg.setText("GAME OVER");
            else
                this.overlaySzoveg.setText("LOOSER");
            
        } else if (gamestatus == 2){
            this.pontok += osszeg_szam - lepes;
            szovegTime0 = time;
            this.nextLevel();
        }
        
        if (action){
            if (gamestatus == 0)
                if (gameOver)
                        gameOverSound.play();
                    else
                        dieSound.play();
                else if (gamestatus == 1)
                     stepSound.play();
                else if (gamestatus == 2)
                    nextLevelSound.play();
        }
        
        // kamera baellitas - set
        float cx = jatekos.getPosX();
        float cy = jatekos.getPosY();
        float cz = jatekos.getPosZ();
        
        camera.setCenter(cx, cy, cz);
        camera.setEye(
                r*(float)Math.sin(angle) * ca2 + cx, 
                r*(float)Math.cos(angle) * ca2 + cy, 
                r*(float)Math.sin(angle2)      + cz );
        
        // Scene rendering -> FBO
        
        fbo.bind();
       
        Render.applyView(Data.FBO_SIZE);
        
        camera.projectScene();
        camera.lookAtScene();
        
        Render.clearScene();
        
        Render.switchBlend(true);
        Render.switchTextue2D(true);
        
        this.shader.bind();
        //this.textura.bind();
        
        this.shader.seti ("tex0", 0);
        this.shader.setf ("shiniess",        JatekInGame.MATERIAL_shiniess);
        this.shader.setf ("shiningLevel",    JatekInGame.MATERIAL_shiniessLevel); 
        this.shader.setf ("correctionCurve", JatekInGame.PRE_colorCorrection);
        this.shader.setf ("ambientGlobal",   0f,  0f,  0f,  1f);
        this.shader.setfv("ambient",         JatekInGame.LIGHT_ambient, 4);
        this.shader.setfv("diffuse",         JatekInGame.LIGHT_diffuse, 4);
        this.shader.setfv("specular",        JatekInGame.LIGT_specular, 4);
        this.shader.setf ("lightPosition",   0f, 2*r, 0f,  1f); // ennek valamiert nem ugy szamolja a poziciojat ahogy kellene
        
        aktualisPalyaPtr.draw(this.shader);
        jatekos.draw(time, this.shader);
        
        this.shader.unbind();
        
        fbo.unbind();
        
        // Blending Framebuffer <- FBO
        
        Render.clearScene();
        Render.applyView();
        
        this.POST_hdrKorrekcio.bind();
        this.POST_hdrKorrekcio.setf("exposure",   JatekInGame.POST_hdrExposure);
        this.POST_hdrKorrekcio.setf("bloomLevel", JatekInGame.POST_hdrBloomLevel);
        this.fbo_sprite.draw();
        this.POST_hdrKorrekcio.unbind();
        
        // szovegek kiirasa
        
        this.szovegShader.bind();
        
        Render.setBlend(BlendingConditionE.BC_SRC_ALPHA, BlendingConditionE.BC_ONE_MINUS_SRC_ALPHA);
        
        // szoveg
        float tt0 = (time-szovegTime0) / FADE_TIME;
        if (tt0<1.) {
            this.overlaySzoveg.setAlpha(1f-tt0);
            this.overlaySzoveg.draw();
        }
        
        this.pontokSzoveg.setText("".format("Score: %07d", pontok));
        this.pontokSzoveg.draw();
        
        this.eletekSzoveg.setText("".format("Lives: %d", elet));
        //this.eletekSzoveg.setText("".format("%.3f %d", k, p));
        this.eletekSzoveg.draw();
        
        this.lepesSzoveg.setText("".format("Steps: %d / %d", lepes, osszeg_szam - lepes));
        this.lepesSzoveg.draw();
        
        Render.setBlend(BlendingConditionE.BC_ONE, BlendingConditionE.BC_ZERO);
        Render.switchBlend(false);
        
        this.szovegShader.unbind();
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    private void loadLevels(){ 
        LevelLoader ll = new LevelLoader(Data.DATA_ROOT+Data.LEVEL_FILE);
        
        try {
            ll.parse();
            
            String[] data;
            int i=0, pontok;
            
            do {
                data = ll.getArray(i);
                pontok = ll.getMaxScore(i);
                if (data != null){
                    Level palya = new Level(data, pontok);
                    palya.calculate();
                    this.palyak.add(palya);
                }
                i++;
            } while (data != null);
            
        } catch(Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e);
            e.printStackTrace();
        }
        
        // reset
        resetGame();
    }
    
    public void resetGame(){
        //if (this.palyak.isEmpty()) loadLevels();
        escPressed = 0;
        exit = false;
        gameOver = false;
        
        elet = MAX_LIVES; 
        pontok = 0;
        
        palyaIndex = -1;
        
        if (this.jatekos == null) this.jatekos = new Player();
        
        nextLevel();
    }
    
    private void nextLevel(){
        lepes = 0; 
        osszeg_szam = 0;
        
        palyaIndex++;
        if (palyaIndex >= palyak.size()) return; // gatulalok, vegigtoltad a jatszast
        
        aktualisPalyaPtr = palyak.get(palyaIndex);
        jatekos.reset();
        jatekos.setStartPosGrid(aktualisPalyaPtr.getStartX(), aktualisPalyaPtr.getStartY());
        osszeg_szam = aktualisPalyaPtr.getGoalScore();
        
        // hajajajjj
        this.overlaySzoveg.setText("".format("level %s", this.palyaIndex + 1));
    }
    
    //// PALYAT KEZELO OSZTALY
    private class Level{
        private static final byte FLOOR  = '*';
        private static final byte SPACE1 = ' ';
        private static final byte SPACE2 = '.';
        
        private static final byte PLAYER_E  = 'A';  // facing egtajak szerint
        private static final byte PLAYER_Ny = 'B';  // oramutato jarasaval 
        private static final byte PLAYER_D  = 'C';  // ellentetes iranyba
        private static final byte PLAYER_K  = 'D';
        
        private static final byte GOAL1 = '1';
        private static final byte GOAL2 = '2';
        private static final byte GOAL3 = '3';
        private static final byte GOAL4 = '4';
        private static final byte GOAL5 = '5';
        private static final byte GOAL6 = '6';
        private static final byte JOKER = 'X';
        
        /*
        private byte levelbitmap[][];
        private int levelbitmapLenX[];
        private int levelbitmapLenY;
        */
        
        private ByteBuffer map;
        private int maxX, maxY;
                
        
        private String[] rawBmpPtr;
        
        private int maxScore, startX, startY, goalBrick;
        
        private FloatBuffer vertices;
        private FloatBuffer normals;
        private FloatBuffer textureCoords;
        
        private IntBuffer indices;
        private int indicesLen;
        
        private IntBuffer goal_indices;
        private int goal_indicesLen;
        
        private Mesh model;
        
        public Level(String[] bitmap, int maxScore){
            // hihetetlen, de ezzel a """RONDA""" megoldassal a leg-
            // gyorsabb az egesz. Sajnos.
            
            //this.levelbitmap = new byte[MAX_LEVEL_SIZE][MAX_LEVEL_SIZE];
            //this.levelbitmapLenX = new int[MAX_LEVEL_SIZE];
            
            this.rawBmpPtr = bitmap;
            this.maxScore = maxScore;
            
            this.goalBrick = this.startX = this.startY = -1;
        }
        
        public int getGoalScore() {return this.maxScore;}
        
        public int calculate() throws Exception{
            this.model = new Mesh();
            
            maxX = 0; maxY = (this.rawBmpPtr.length<MAX_LEVEL_SIZE)?this.rawBmpPtr.length:MAX_LEVEL_SIZE;
            
            for(int i=0; i<maxY; i++)
                if (maxX<this.rawBmpPtr[i].length()) maxX = this.rawBmpPtr[i].length();
           
            maxX = (maxX<MAX_LEVEL_SIZE)?maxX:MAX_LEVEL_SIZE;
            
            
            this.map = BufferUtils.createByteBuffer(maxX*maxY);
            BufferUtils.zeroBuffer(map);
            
            // palya betoltese
            byte sor[], szegmens, ertek;
            int lapok = 0;
            for (int y = 0; y<maxY; y++){
                sor = this.rawBmpPtr[y].getBytes();
                for (int x = 0; x<maxX; x++){
                    if (x>=sor.length)
                        break;
                    
                    szegmens = sor[x];
                    ertek = 0;
                    if (szegmens != SPACE1 && szegmens != SPACE2) {
                        /*if (szegmens == FLOOR)*/
                        ertek = 1;
                        if (goalBrick == -1)
                            if      (szegmens == GOAL1) goalBrick = ertek = 2;
                            else if (szegmens == GOAL2) goalBrick = ertek = 3;
                            else if (szegmens == GOAL3) goalBrick = ertek = 4;
                            else if (szegmens == GOAL4) goalBrick = ertek = 5;
                            else if (szegmens == GOAL5) goalBrick = ertek = 6;
                            else if (szegmens == GOAL6) goalBrick = ertek = 7;
                            else if (szegmens == JOKER) goalBrick = ertek = 8;
                        
                        lapok++;
                        
                        // ezt vegul kiveszem.
                        if ((szegmens == PLAYER_E ||
                            szegmens == PLAYER_Ny ||
                            szegmens == PLAYER_D  ||
                            szegmens == PLAYER_K) && 
                            (this.startX == -1 && this.startY == -1) //csak az elso markot vegye figyelembe
                        ){
                            this.startX = x;
                            this.startY = y;
                            // + facing!
                        }
                    } 
                    map.put(y*maxX + x, ertek);
                }   
            }
            
            map.rewind();
            
            if (startX == -1) throw new Exception("Hibas palya.");
            if (goalBrick == -1) throw new Exception("Hibas palya.");
            
            // caulk
            // ... 
            
            vertices = BufferUtils.createFloatBuffer(4*3*lapok +1 );
            normals = BufferUtils.createFloatBuffer(4*3*lapok + 1);
            textureCoords = BufferUtils.createFloatBuffer(4*2*lapok + 1 );
            indices = BufferUtils.createIntBuffer(6*lapok + 1 );
            goal_indices = BufferUtils.createIntBuffer(6*1 + 1 );
            
            //int lap =0;
            int j=0;
            for (int y=0; y<maxY; y++){
                for (int x=0; x<maxX; x++){
                    //map.rewind();
                    szegmens = map.get(y*maxX + x);
                    if (szegmens != 0){
                        for (int i=0; i<4; ++i){
                            vertices.put(CubeData.cube_vertices[3*i+0] + 2f*x); 
                            vertices.put(CubeData.cube_vertices[3*i+1] + 2f*y);
                            vertices.put(CubeData.cube_vertices[3*i+2] + 2f*0);
                        }
                        
                        for (int i=0; i<4*3; ++i)   // normalok beszurasa
                            normals.put(CubeData.cube_normals[0*4+i]); 
                        
                        for (int i=0; i<4*2; ++i)   // textura koordinatak beszurasa
                            textureCoords.put(CubeData.cube_normals[0*4+i]); 
                        
                        if (szegmens>1){
                            goal_indices.put(4*j+0); goal_indices.put(4*j+1); goal_indices.put(4*j+2);
                            goal_indices.put(4*j+2); goal_indices.put(4*j+3); goal_indices.put(4*j+0);
                        }
                        else {
                            indices.put(4*j+0); indices.put(4*j+1); indices.put(4*j+2);
                            indices.put(4*j+2); indices.put(4*j+3); indices.put(4*j+0);
                        }
                        j++;
                    }
                }
            }
            
            this.indicesLen = 6*j;
            this.goal_indicesLen = 6;
            
            this.model.setVertexPointer(this.vertices, 3, lapok*2, true);
            this.model.setNormalPointer(this.normals, 3, lapok*2, true);
            this.model.setTexturePointer(this.textureCoords, 2, lapok*2, true);
            //this.model.setVertexPointer(this.normals, 3, lapok*2, true);
            this.model.setIndicesPointer(indices, indicesLen);
            
            return 0;
        }
        
        public int getStartX(){return this.startX;}
        public int getStartY(){return this.startY;}
        
        public void draw(Shader shader){
            this.model.bindAll();
            this.model.pushMatrix();
            
            this.model.drawByIndex(indices, indicesLen);
            
            shader.setfv("diffuse",  diffuseColor[goalBrick-2], 4);
            shader.setfv("specular", diffuseColor[goalBrick-2], 4);
            this.model.drawByIndex(goal_indices, goal_indicesLen);
            
            this.model.popMatrix();
            
            this.model.unbindAll();
        }
        
        /**
         * 
         * @param x
         * @param y
         * @param plane 
         * @return 
         */
        
        public int testXY(int x, int y, int plane){
            if (x<0 || y<0) return 0;                               // ha negativ koordinata
            if (x>=maxX || y>=maxY ) return 0;  // ha nagyobb mint a palya max merete
            
            int p = this.map.get(maxX * y + x);
            if (p != 0)
            {
                if (p == plane+2 || p == 8) return 2; // you're winner
                return 1; // nem esik le
            }
            return 0;
        }
    }
    
///////////////////////////////////////////////////////////////////////////////    
    
    private class Player{    
        private int []colorIndices;
        
        private IntBuffer []indices;
        
        private float planeOrientation[][];
        private float pos[], pos0[], orientMat[], tmpVec[], rotX, rotY, rotX0, rotY0;
        private int posX, posY, bottomPlane;
        private Mesh kocka;
        
        public Player(){
            pos  = new float[3];
            pos0 = new float[3];
            orientMat = new float[16];
            tmpVec = new float[3];
            
            active = false;
            
            VMath.identityM(orientMat);
            
            boolean usevbo = true;
            this.kocka = new Mesh();
            //this.kocka.setDrawType(VertexArray.VBODrawModeE.VD_TRIANGLES);
            this.kocka.setVertexPointer(CubeData.cube_vertices_buffer, 3, CubeData.cube_vertex_length, usevbo);
            this.kocka.setNormalPointer(CubeData.cube_normal_buffer, 3, CubeData.cube_vertex_length, usevbo);
            this.kocka.setTexturePointer(CubeData.cube_texture_buffer, 2, CubeData.cube_vertex_length, usevbo);
            this.kocka.setIndicesPointer(CubeData.cube_indices_buffer, CubeData.cube_indices_length);
            
            indices = new IntBuffer[6];
            for(int i=0; i<6; i++){
                indices[i] = BufferUtils.createIntBuffer(6);
                for(int j=0; j<6; j++){
                    indices[i].put(CubeData.cube_indices[6*i + j]);
                }
                indices[i].rewind();
            }
            
            planeOrientation = new float[6][3];
            colorIndices = new int[6];
            for(int i=0; i<6; i++){
                VMath.copyV(defaultPlaneOrientation[i], planeOrientation[i]);
                colorIndices[i] = i;
            }

            posX = -1; posY = -1;
        }
        
        public void setStartPosGrid(int x, int y){
            this.posX = x;
            this.posY = y;
            VMath.setV(pos, 2f*x, 2f*y, 2f);
            VMath.copyV(pos, pos0);
            
            die = false;
        }
        
        public float getPosX(){return pos[0];}
        public float getPosY(){return pos[1];}
        public float getPosZ(){return pos[2];} //utallak
        
        public int getGridX(){return posX;}
        public int getGridY(){return posY;}
        public int getBottomPlane() {return bottomPlane;}
        
        private boolean active, die;
        private float time0;
        
        public void reset(){
            for(int i=0; i<6; i++){
                VMath.copyV(defaultPlaneOrientation[i], planeOrientation[i]);
                colorIndices[i] = i;
            }

            posX = -1; posY = -1;
        }
        
        private void move(int x, int y){
            if (active) return;
            
            this.posX += x;
            this.posY += y;
            
            this.rotX = 0;
            this.rotY = 0;
            
            this.rotX0 = 90*y;
            this.rotY0 = -90*x;
            
            VMath.identityM(orientMat);
            VMath.rotate3D(orientMat, 1, 0, 0, -rotX0, orientMat);
            VMath.rotate3D(orientMat, 0, 1, 0, -rotY0, orientMat);
            
            /*
            VMath.setV(frontDir, orientMat[0], orientMat[1], orientMat[2]);
            VMath.setV(sideDir,  orientMat[4], orientMat[5], orientMat[6]);
            */
            
            //VMath.mulMV(orientMat, frontDir, frontDir, 0);
            //VMath.mulMV(orientMat, sideDir, sideDir, 0);
            
            //VMath.setV(tmpVec, 0, 0, 0);
            //VMath.crossV(frontDir, sideDir, tmpVec);
            
            int i=0;
            int top=-1, bottom=-1, front=-1, back=-1, left=-1, right=-1;
            //for(float [] v : planeOrientation){
            for(i=0; i<planeOrientation.length; i++){
                VMath.mulMV(orientMat, planeOrientation[i], planeOrientation[i], 0);
                VMath.copyV(planeOrientation[i], tmpVec);
                if (tmpVec[0]> 0.0000015f) right  = i;
                if (tmpVec[0]<-0.0000015f) left = i;
                
                if (tmpVec[1]> 0.0000015f) back = i;
                if (tmpVec[1]<-0.0000015f) front = i;
                
                if (tmpVec[2]> 0.0000015f) top = i;
                if (tmpVec[2]<-0.0000015f) bottom = i;
            }
            
            colorIndices[0] = top;
            colorIndices[1] = bottom;
            colorIndices[2] = back;
            colorIndices[3] = front;
            colorIndices[4] = right;
            colorIndices[5] = left;
            
            bottomPlane = bottom;
            
            this.active = true;
        }
        
        public void stepNorth() {move( 0, 1);}
        public void stepSouth() {move( 0,-1);}
        public void stepWest()  {move(-1, 0);}
        public void stepEast()  {move( 1, 0);}
        
        public boolean kill() {
            if (active || die) return active;
            die = true; 
            active = true; 
            return true;
        }
        
        public void draw(float time, Shader shader){
            float rx = this.rotX;
            float ry = this.rotY;
            
            if (!active){
                time0 = time;
            } else {
                float dt = time-time0;
                float t = dt/STEP_SPEED;
                
                pos[0] = t*(2f*posX) + (1f-t)*pos0[0];
                pos[1] = t*(2f*posY) + (1f-t)*pos0[1];
                pos[2] = 2f + .5f*(float)Math.sqrt(1-(2*t-1)*(2*t-1));
                
                rx = t*(rotX) + (1f-t)*(rotX0);
                ry = t*(rotY) + (1f-t)*(rotY0);
                
                if (t>1f){
                    VMath.setV(pos, 2f*posX, 2f*posY, 2f);
                    VMath.copyV(pos, pos0);
                    
                    if (die){
                        rx = t*(rotX) + (1f-t)*(rotX0);
                        ry = t*(rotY) + (1f-t)*(rotY0);
                        pos[2] = 2f - FALL_GRAVITY * t * t;
                        
                        if (t>FALL_FADE_TIME) {
                            active = false;
                        }
                    } else {
                        active = false;
                    }
                }
            }
            
            this.kocka.bindAll();
            
            this.kocka.identity();
            
            this.kocka.rotate3d(1, 0, 0, rx);
            this.kocka.rotate3d(0, 1, 0, ry);
            
            this.kocka.translate(pos[0], pos[1], pos[2]);
            
            this.kocka.pushMatrix();
            for(int i=0; i<6; i++){
                shader.setfv("diffuse", diffuseColor[colorIndices[i]], 4);
                shader.setf ("shiniess", 100f);
                shader.setf ("shiningLevel", .7f);
                this.kocka.drawByIndex(this.indices[i], 6);
            }
            this.kocka.popMatrix();
            this.kocka.unbindAll();
        }

        private boolean isActive() {
            return this.active;
        }
    }
    
    public void resetTimer(float time) {
        this.szovegTime0 = time;
        this.time0 = time;
    }
    
    public boolean isExit(){
        return exit;
    }
    
    public int getScore(){
        return pontok;
    }
    
}
