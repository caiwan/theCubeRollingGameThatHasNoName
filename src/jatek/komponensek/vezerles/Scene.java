package jatek.komponensek.vezerles;

/**
 * Absztakcio, mert szeretjuk.
 * @author caiwan
 */
public interface Scene {
    /**
     * 
     */
    public void init() throws Exception;
    
    /**
     * 
     * @param time 
     */
    public void mainloop(float time) throws Exception;
}
