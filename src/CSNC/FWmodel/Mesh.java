/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CSNC.FWmodel;

import CSNC.FWmath.VMath;
import CSNC.FWrender.*;
import CSNC.FWrender.VertexArray.*;
import java.nio.IntBuffer;

/**
 *
 * @author caiwan
 */
public class Mesh extends VertexArray{
    private IntBuffer indices;
    private int indices_len;
    
    private float modelView[];
    
    public Mesh(){
        super.bindAll();
        this.modelView = new float[16];
        VMath.identityM(modelView);
    }
    
    
    public void setIndicesPointer(IntBuffer ptr, int len){
        this.indices = ptr;
        this.indices_len = len;
    }
    
    public void bindAll(){
        super.bindAll();
    }
    
    public void draw(){
        Render.mat_Push(Render.MatrixModeE.MM_MODELVIEW);
        Render.mat_Multiply(Render.MatrixModeE.MM_MODELVIEW, modelView);
        
        super.drawByIndex(indices, indices_len);
        
        Render.mat_Pop(Render.MatrixModeE.MM_MODELVIEW);
    }
    
    public void pushMatrix(){
        Render.mat_Push(Render.MatrixModeE.MM_MODELVIEW);
        Render.mat_Multiply(Render.MatrixModeE.MM_MODELVIEW, modelView);
    } 
    
    public void popMatrix(){
        Render.mat_Pop(Render.MatrixModeE.MM_MODELVIEW);
    }
    
    public void unbindAll(){
        super.unbindAll();
    }
    
    public void identity(){
        VMath.identityM(modelView);
    }
    
    public void translate(float x, float y, float z){
        VMath.translateM(modelView, x, y, z, modelView);
    }
    
    public void rotate3d(float x, float y, float z, float alpha){
        VMath.rotate3D(modelView, x, y, z, alpha, modelView);
    }
    
    public void rotate2d(float alpha){
        VMath.rotate2D(modelView, alpha, modelView);
    }
}
