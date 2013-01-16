/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CSNC.FWcore;

import java.io.IOException;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Ezzel lehet tetszoleges hangot betolteni majd lejatszni.
 * LWJGL / OpenAL libbel csinaja.
 * @author caiwan
 */
public class Sound {
    
    private Audio stream_ptr;
    
    public Sound(){}
    
    public void loadFromResource(boolean isStreaming, String type, String path) throws IOException{
        if (isStreaming)
            stream_ptr = AudioLoader.getStreamingAudio(type, ResourceLoader.getResource(path));
        else
            stream_ptr = AudioLoader.getAudio(type, ResourceLoader.getResourceAsStream(path));
    }
    
    public void play(){
        if(this.stream_ptr == null) return;
        stream_ptr.playAsSoundEffect(1.0f, 1.0f, false);
    }
    
    public void playMusic(){
        if(this.stream_ptr == null) return;
        stream_ptr.playAsMusic(1.0f, 1.0f, true);
    }
    
    public void stop(){
        if(this.stream_ptr == null) return;
        stream_ptr.stop();
    }
}
