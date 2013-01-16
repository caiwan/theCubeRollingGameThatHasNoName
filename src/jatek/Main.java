package jatek;
public class Main {
    public static void main(String args[]){
        System.setProperty("org.lwjgl.librarypath",System.getProperty("user.dir") + "/natives/");
        
        ControlClass vezerlo = new ControlClass();
        vezerlo.start();
    }
}