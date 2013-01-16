/*
 * Generated with cpp2java.py by slapec^IR
 * (root)/trunk/engine/engine/source/graph/renderer.hpp - Rev 79
 */
package CSNC.FWrender;

import java.nio.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;


/**
 * Az opengl allapotgepet helyettesito forrasprogram definialasara alkalmas 
 * osztaly. GLSL nyelvi arnyalokat lehet vele be/ki kapcsolni.
 * @author CAIWAN
 */
public class Shader {
    private int vertex_id, fragment_id, program_id;
    private boolean tamogatottAShader;
    
    public Shader(){
        tamogatottAShader = true;
        // itt nem artan ellenorizni, hogy egyaltalan a GPU tamogatja-e a shadereket
    }
    
    private int ShaderInfoLog(int obj, String message, boolean isVerbose, String melyik){
        int status = 1;
        int infologLength = 0;
        if (GL20.glIsShader(obj)){
                status = GL20.glGetShaderi(obj, GL20.GL_COMPILE_STATUS);
                infologLength = GL20.glGetShaderi(obj, GL20.GL_INFO_LOG_LENGTH);
        } else if (GL20.glIsProgram(obj)) {
                status = GL20.glGetProgrami(obj, GL20.GL_LINK_STATUS);
                infologLength = GL20.glGetProgrami(obj, GL20.GL_INFO_LOG_LENGTH);
        } else {
            System.out.printf("%s info (id=%d):\n invalid object", obj, melyik);    
            status = 0; //invalid obj.
        }
        
        //if (infologLength == 0) return status;
        
        if (GL20.glIsShader(obj)){
            //GL20.glGetShaderInfoLog(obj, infologLength, &charsWritten, infoLog);
            message = GL20.glGetShaderInfoLog(obj, infologLength+1);
        }else {
            message = GL20.glGetProgramInfoLog(obj, infologLength+1);
        }
        // ha visszajelzes be van allitva akkor mindenkepp irja ki, hogy van-e 
        // hiba, ha nem, akkor csak akkor, ha van
        if (isVerbose || status != 1){
            System.out.printf("%s info (id= %d):\n%s\n\n", melyik, obj, message);
        }

        return status;
    }
    
    public int createShader(String vertex_shader_source, String fragment_shader_source){
        if (!this.tamogatottAShader) return 1;
        
        this.program_id = GL20.glCreateProgram(); //opengl2 nem is mukodik.
        //this.program_id = ARBShaderObjects.glCreateProgramObjectARB();
        
        String message = "";
        int status = 0;
        
        ByteBuffer buf = BufferUtils.createByteBuffer(vertex_shader_source.length()>fragment_shader_source.length()?vertex_shader_source.length()+1:fragment_shader_source.length()+1);
        
        buf.put(vertex_shader_source.getBytes());
        buf.rewind();
        
        // vertex shader
        this.vertex_id = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
        //this.vertex_id = ARBShaderObjects.glCreateShaderObjectARB(ARBVertexShader.GL_VERTEX_SHADER_ARB);
        //ARBShaderObjects.glShaderSourceARB(vertex_id, buf);
        //ARBShaderObjects.glCompileShaderARB(vertex_id);
        GL20.glShaderSource(vertex_id, buf);
        GL20.glCompileShader(vertex_id);
       
        status = this.ShaderInfoLog(vertex_id, message, true, "Vertex shader");
        if(status != 1){
            // ... 
            this.tamogatottAShader = false;
            return 2;
        }
        
        GL20.glAttachShader(program_id, vertex_id);
        
        BufferUtils.zeroBuffer(buf);
        buf.put(fragment_shader_source.getBytes());
        buf.rewind();
        
        // fragment shader
        this.fragment_id = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
        GL20.glShaderSource(fragment_id, buf);
        GL20.glCompileShader(fragment_id);
        
        status = this.ShaderInfoLog(fragment_id, message, true, "Fragment shader"); 
        if(status!= 1){
            // ... 
            this.tamogatottAShader = false;
            return 3;
        }
        GL20.glAttachShader(program_id, fragment_id);
        
        // link
        GL20.glLinkProgram(program_id);
        status = this.ShaderInfoLog(program_id, message, true, "Shader program") ;
        if(status!= 1){
            // ... 
            this.tamogatottAShader = false;
            return 4;
        }
        
        return 0;
    }
    
    public void bind(){
        if (this.tamogatottAShader)
            GL20.glUseProgram(program_id);
    }
    public void unbind(){
        //if (this.tamogatottAShader)
            GL20.glUseProgram(0);
    }
    
    private int getUniformLoc(String name){
        if (!this.tamogatottAShader) return -2;
	return GL20.glGetUniformLocation(this.program_id, name);
    }
    
    // parameterek
    public void setf(String name, float v0){
        int loc = this.getUniformLoc(name);
        if (loc<0) return; 
        GL20.glUniform1f(loc, v0);
    }
    public void setf(String name, float v0, float v1){
        int loc = this.getUniformLoc(name);
        if (loc<0) return; 
        GL20.glUniform2f(loc, v0, v1);
    }
    public void setf(String name, float v0, float v1, float v2){
        int loc = this.getUniformLoc(name);
        if (loc<0) return; 
        GL20.glUniform3f(loc, v0, v1, v2);
    }
    public void setf(String name, float v0, float v1, float v2, float v3){
        int loc = this.getUniformLoc(name);
        if (loc<0) return; 
        GL20.glUniform4f(loc, v0, v1, v2, v3);
    }
    
    public void setfv(String name, float []vp, int len){
        int loc = this.getUniformLoc(name);
        if (loc<0) return; 
        
        switch(len){
            case 1:
                GL20.glUniform1f(loc, vp[0]);
                break;
            case 2:
                GL20.glUniform2f(loc, vp[0], vp[1]);
                break;
            case 3:
                GL20.glUniform3f(loc, vp[0], vp[1], vp[2]);
                break;
            case 4:
                GL20.glUniform4f(loc, vp[0], vp[1], vp[2], vp[3]);
                break;
        }
    }
    
    public void seti(String name, int i0){
        int loc = this.getUniformLoc(name);
        if (loc<0) return; 
        GL20.glUniform1i(loc, i0);
    }
    /*
    public void setm4x4(String name, float[]matrix){
        int loc = this.getUniformLoc(name);
        if (loc<0) return; 
        //Render.ftmp4k.put(matrix);
        //GL20.glUniformMatrix4(loc, false, null);
    }
    */
    // vertex attribs(?)
}
