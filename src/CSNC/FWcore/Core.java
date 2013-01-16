package CSNC.FWcore;

/**
 * API mag
 * Itt kap helyet az inicializacio:
 * - init ablak kidobasa
 * - renderer ablak kinyitasa
 * - renderer beallitasa a rendereleshez
 * 
 * MEGJEGYZES: ezt a reszt csak osszeganyolom,
 * ha lesz idom megcsinalom ""rendesen""
 * ha ez a szoveg ittmarad, akkor ezt nem tettem
 * meg meg.
 * 
 * @author caiwan
 */

import CSNC.FWrender.Render;
import javax.swing.JOptionPane;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
//import java.io.*;

public final class Core{
    // core config
    public static final Core.AspectRatio CORE_FORCE_RATIO = Core.AspectRatio.AR_16_9;
    
    // core maderfaker
    public static enum AspectRatio{
        //AR_1_2 (1,2), 
        AR_4_3 (4,3), 
        //AR_5_4 (5,4), 
        AR_16_9 (16,9), 
        AR_16_10 (16,10);

        private int width, height;
        private AspectRatio(int width, int height){
            this.width = width;
            this.height = height;
        }
        public float getAR(){ return (float)this.width / (float)this.height; }
        public int getW(){return this.width;}
        public int getH(){return this.height;}
    }

    public static enum ScreenResoultion{
	SR_640_480 (640, 480),
	SR_800_600  (800, 600),
	SR_1024_768 (1024, 768),
        //SR_1440_720 (1440, 720),
        SR_1366_768 (1366, 768),
	//SR_1280_960 (1280, 960),
        SR_1920_1080 (1920, 1080);
                
        private int width, height;
        private ScreenResoultion(int _width, int _height){
            this.width = _width;
            this.height = _height;
        }
        public int getW(){return this.width;}
        public int getH(){return this.height;}
        public float getAR(){ return (float)this.width / (float)this.height;}
    }
    
    private static Window mainWindow;
    
    private static Core.ScreenResoultion i_res;        // valasztott felbontas (enum)
    private static int	i_width, i_height;             // valasztott nevleges felbontas (x*y)
    private static int w_width, w_height;              // valodi felbontas (ablak meret)

    private static Core.AspectRatio i_ratio;           // valasztott (display) meretarany
    private static float aspectRatio;                  // valasztott nevleges meretarany

    private static Core.AspectRatio force_i_ratio;     // kenyszeritett meretarany (enum) (flag)
    private static float force_aspectRatio;            // kenyszeritett nevleges meretarany (flag)

    private static int r_width, r_height;              // render area merete
    private static int r_x_offset, r_y_offset;         // render area offset (levagas miatt)

    //flags
    private static boolean use_forced_aspect;              // kenyszeritett meretarany
    private static boolean i_fullscreen;                   // fullscreen-e
    private static boolean i_chopped;                 // levagott-e
    
    public static void setRes(Core.ScreenResoultion res, Core.AspectRatio ar, boolean is_Fullscreen){
        Core.i_res = res;
        Core.i_ratio = ar;
        Core.i_fullscreen = is_Fullscreen;
    }
    
    public static void setupWindow(){
        //renderer beallitasa
        Core.i_height = Core.i_res.getH();
        Core.i_width  = Core.i_res.getW();
        
        Core.force_i_ratio = Core.CORE_FORCE_RATIO;
        Core.force_aspectRatio = Core.force_i_ratio.getAR();
        
        Core.use_forced_aspect = true;
        Core.aspectRatio = Core.i_ratio.getAR();
        
        // beallitjuk a renderer meretet
	int width = Core.i_res.getW(), height = Core.i_res.getH();
	float ratio = Core.aspectRatio;
        
        // ha nem teljes kepernyo, akkor az ablak felbontasat megvaltoztatjuk.
        // TODO: csak felso levagast tud.
	if (!Core.i_fullscreen){
		width = (int)Math.ceil((float)height*(Core.force_aspectRatio));
		ratio = Core.force_aspectRatio;
                
                // visszairjuk az uj felbontast
                Core.i_width = width;
                Core.i_height = height;
	}
        
        Core.aspectRatio = ratio;
        
        // main window kinyitasa
        try {
            if (Core.mainWindow == null) {
                mainWindow = new Window(Core.i_width, Core.i_height, Core.i_fullscreen);
                mainWindow.create();
            }
            mainWindow.setDisplayMode(Core.i_width, Core.i_height, Core.i_fullscreen);
        } catch(Exception ex) {
            //LOGGER.log(Level.SEVERE,ex.toString(),ex);
              releaseFW(0x01);
        }// finally {
          
        //}
        
        // ablak valodi merete (mivel elterhet a beallitottol)
        Core.w_width = mainWindow.getWidth();
        Core.w_height = mainWindow.getHeight();
        
	if (Core.i_fullscreen){
		int _height1 = (int)Math.floor((float)Core.w_width*(1.0f/Core.aspectRatio));
		int _height2 = (int)Math.floor((float)Core.w_width*(1.0f/Core.force_aspectRatio));
		int _height = Core.w_height-(_height1-_height2);
		int _h_off = (int)Math.floor((float)(Core.w_height-_height)/2.0);

		if (_h_off > 0) {	
			Core.i_chopped  = true;
			Core.r_width    = Core.w_width;
			Core.r_height   = _height;
			Core.r_y_offset = _h_off;
			Core.r_x_offset = 0;
		} else {
			Core.i_chopped  = true;
			Core.r_width    = Core.w_width;
			Core.r_height   = Core.w_height;
			Core.r_y_offset = 0;
			Core.r_x_offset = 0;
		}
	} else {
            Core.i_chopped  = false;
            Core.r_width    = Core.w_width;
            Core.r_height   = Core.w_height;
            Core.r_y_offset = 0;
            Core.r_x_offset = 0;
        }
        
        int res = Render.setupRenderer(
                Core.r_width, Core.r_height, 
                Core.i_chopped,
                Core.r_x_offset, Core.r_y_offset,
                Core.force_aspectRatio
        );
        
        if (res != 0 && res != -1) {
            Core.releaseFW(0x9002);
        }
    }
    
    /**
     * Ez inicializalja a keretrendszert
     */
 
    public static void initFW(){
        setupWindow();
        /*
        try {
            AL.create();
        } catch (LWJGLException e){
            Core.releaseFW(0x9003);
        }
        */
    }   
    /**
     * Teljes keretrendszer felszamolasa
     */
    
    public static void releaseFW(int err){
        // err. msg.
        if (err != 0) JOptionPane.showMessageDialog(null, "Motor leall. Hibakod:"+err);
        if(mainWindow != null) mainWindow.destroy();
        AL.destroy();
    }
    /**
     * 
     * @return ablak szelessege
     */
    public static int getW(){
        return Core.i_width;
    }
    /**
     * 
     * @return ablak magassage
     */
    public static int getH(){
        return Core.i_height;
    }
    
    public static boolean getIsFullscreen(){
        return Core.i_fullscreen;
    }
    
    public static boolean peekWndMessage(){
        mainWindow.update();
        return mainWindow.getExitMessage();
    }

    private static MsecTimer timer;
    public static float getDeltaTime(){
        if (Core.timer == null)
            Core.timer = new MsecTimer();
        return (float)timer.getDeltaTime();
    }
}

/* mivel ebbol az osztalybol a program futasa soran csak egyetlen peldany lesz
 *ezert itt most athagok nehany szabalyt es elvet */

