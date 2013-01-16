/*
 * Generated with cpp2java.py by slapec^IR
 * (root)/trunk/engine/engine/source/graph/renderer.hpp - Rev 79
 */
package CSNC.FWrender;


import CSNC.FWmath.VMath;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Project;

//================================================================================================
// Camera class 
//=================================================================================================

public class Camera {
    // mivel nincs #define ezert par dolog itt es most:
    public static final float DEFAULT_ZNEAR = 0.7f;
    public static final float DEFAULT_ZFAR =  1000.0f;
    public static final float DEFAULT_FOV = 45.f;
    private static int camera_count;
    
    private int cameraNumber;
   
    //private float mat[];
    private float modelviewMatrix[];
    private float projectionMatrix[];

    private class clippingPlane_t{
        public float nearPlane, farPlane;
    };
    private clippingPlane_t clippingPlane;
    private float fov;

    private float normal[], view[], rotationEye[], eye[], c_eye[], center[], c_center[], up[], c_up[];

    public static void ortho(){
        ortho(0f,1f,0f,1f);
    }
    
    public static void ortho(float l, float r, float t, float b) {
        Render.mat_LoadIdentity(Render.MatrixModeE.MM_PROJECTION);
	GL11.glOrtho(l, r, b, t, -1, 1); 
        Render.mat_LoadIdentity(Render.MatrixModeE.MM_MODELVIEW);
        Render.mat_LoadIdentity(Render.MatrixModeE.MM_TEXTURE);
    }
    
    public Camera(){
        this.cameraNumber = ++camera_count;

        this.clippingPlane = new clippingPlane_t(); //khurwa;
        this.clippingPlane.farPlane = Camera.DEFAULT_ZFAR;
        this.clippingPlane.nearPlane = Camera.DEFAULT_ZNEAR;
        
        this.fov = Camera.DEFAULT_FOV;
        
        //this.mat = new float [16];
        this.modelviewMatrix = new float [16];
        this.projectionMatrix = new float [16];

        this.normal = new float [3];
        this.view = new float [3];
        this.rotationEye = new float [3];
        this.eye = new float [3];
        this.c_eye = new float [3];
        this.center = new float [3];
        this.c_center = new float [3];
        this.up = new float [3];
        this.c_up = new float [3];
        
        VMath.setV(up, 0, 0, 1);    // up vektor = Z a default Y helyett
        // a tobbi vektor erteke remelhetoleg nulla.
    }
    
    public void setFOV(float _fov){this.fov = _fov;}
    public void setClippingPlanes(float _nearPlane, float _farPlane){this.clippingPlane.nearPlane = _nearPlane; this.clippingPlane.farPlane = _farPlane;}

    public void setEyeRotation(float phi) { VMath.setV(this.rotationEye, phi, 0f, 0f); }

    public void setEye(float eX, float eY, float eZ){VMath.setV(this.eye, eX, eY, eZ);}
    public void setUp(float uX, float uY, float uZ){VMath.setV(this.up, uX, uY, uZ);}
    public void setCenter(float cX, float cY, float cZ){VMath.setV(this.center, cX, cY, cZ);}

    public float getFOV(){return this.fov;}

    public float[] getEye(){return this.eye.clone();}
    public float[] getUp(){return this.up.clone();}
    public float[] getCenter(){return this.center.clone();}

    public int getCameraNumber(){return this.cameraNumber;}

    // get matices
    public float[] getCameraMatrix(){return modelviewMatrix.clone();}
    public float[] getProjectionMatrix(){return projectionMatrix.clone();}
    
    // calc and apply matrices
    public void projectScene(){
    	Render.mat_LoadIdentity(Render.MatrixModeE.MM_PROJECTION);
	//glMatrixMode(GL_PROJECTION);
	//glLoadIdentity();

	GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
	//perspective(fov, forceRatio, ZNEAR, ZFAR);

	Project.gluPerspective(this.fov, 
		Render.getRenderAspectRatio(),
		this.clippingPlane.nearPlane, this.clippingPlane.farPlane
	);

	Render.mat_Get(Render.MatrixModeE.MM_PROJECTION, this.projectionMatrix);
	Render.mat_LoadIdentity(Render.MatrixModeE.MM_TEXTURE);
	Render.mat_LoadIdentity(Render.MatrixModeE.MM_MODELVIEW);
    }
    
    public void lookAtScene(){
        lookAtScene_old();
    }
    
    public void lookAtScene_old(){
        VMath.copyV(eye, c_eye);
        VMath.copyV(center, c_center);
        VMath.copyV(up, c_up);
        
        // itt kiszamoljuk az iranyvektorokat
        // ... 
        
        // normal es nezeti irany meghatarozasa
        VMath.subV(c_eye, c_center, view);  // view = normalize(c_eye - c_center);
        VMath.normalizeV(view, view);
        
        VMath.copyV(view, normal);          // normal = norlalize(crossPriduct(view, up))
        VMath.crossV(normal, c_up, normal);
        VMath.normalizeV(normal, normal);
        
        // ide jon az up vector elforgatasa
        
        Render.mat_LoadIdentity(Render.MatrixModeE.MM_MODELVIEW);
        GLU.gluLookAt(c_eye[0], c_eye[1], c_eye[2], c_center[0],  c_center[1], c_center[2], c_up[0], c_up[1], c_up[2]);
        
        Render.mat_Get(Render.MatrixModeE.MM_MODELVIEW, this.modelviewMatrix);
    }
}