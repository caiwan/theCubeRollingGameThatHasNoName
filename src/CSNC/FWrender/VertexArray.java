/*
 * Generated with cpp2java.py by slapec^IR
 * (root)/trunk/engine/engine/source/graph/renderer.hpp - Rev 79
 */
package CSNC.FWrender;

import java.nio.*;
import org.lwjgl.opengl.*;

/**
 * Vertex array es vertex buffer object manager osztaly.
 * Hihetetlen, de ez a nagyon bonyolult kod latvanyosan begyorsitja a rendert.
 * @author CAIWAN
 */
class VBO{
    public enum VBO_BUFFER_TYPE {
        VERTEX_ARRAY(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB),
        INDEX_ARRAY (ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB);

        private int glenum;
        private VBO_BUFFER_TYPE(int _glenum){this.glenum = _glenum;}
        public int resolve(){return this.glenum;}
    }
    //private: inline void* objPtr(unsigned int idx) {return (void*)(((char*)0)+idx);}; //ezzel majd kell kezdeni valamit

    private int bufferID;
    private int type;

    public VBO(VBO_BUFFER_TYPE t){
        this.type = t.resolve();
        this.bufferID = 0;
    }
    
    void build(FloatBuffer data, int data_len, int elem_len){
        if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
            IntBuffer buffer = Render.itmp;
            buffer.rewind();
            ARBVertexBufferObject.glGenBuffersARB(buffer);
        
            this.bufferID = buffer.get(0);

            ARBVertexBufferObject.glBindBufferARB(this.type, this.bufferID);
            ARBVertexBufferObject.glBufferDataARB(this.type, data, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);

        } else {
            this.bufferID = 0;
        }
    }
    public void bind(){
        if (this.bufferID > 0){
            ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, this.bufferID);
        }
    }
    public void unbind(){
        if (this.bufferID > 0){
            ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, 0);
        }
    }
}

public class VertexArray {
            public enum VBODrawModeE {
		VD_TRIANGLES (GL11.GL_TRIANGLES),
                /*
		VD_TRIANGLE_STRIP	= 1, 
		VD_TRIANGLE_FAN		= 2, 
		*/
		VD_POINTS(GL11.GL_POINTS),
		/*
		VD_LINE_STRIP		= 4, 
		VD_LINE_LOOP		= 5, 
		*/
                VD_LINES(GL11.GL_LINES),
		/*
		VD_QUAD_STRIP		= 7, 
		*/
                VD_QUADS(GL11.GL_QUADS);
		/*
		VD_POLYGON			= 9
                */      
        private int glenum;
        private VBODrawModeE(int _glenum){this.glenum = _glenum;}
        public int resolve(){return this.glenum;}
    };
    	private class vertex_array_attrib_t{
            public int varib_id;
            public String varib_name; //char *varib_name;
            public Render.DataTypeE datatype;
	};

	private class vertex_array_element_t{
            FloatBuffer dataptr;
            public int element_length;
            public int array_length;
            public VBO vbo_ptr;
            vertex_array_attrib_t attribs;
	};
        private vertex_array_element_t vertex;
        private vertex_array_element_t normal;
        private vertex_array_element_t texture;
        private vertex_array_element_t attrib[];

        private int maxAttr;

        private VBODrawModeE draw_typ;
        
        private void createArrayElement(vertex_array_element_t element, FloatBuffer data, int elem_len, int data_len, boolean isUseVBO){
            data.rewind();
            element.dataptr = data;
            element.element_length = elem_len;
            element.array_length = data_len;
            
            if (isUseVBO){
                element.vbo_ptr = new VBO(VBO.VBO_BUFFER_TYPE.VERTEX_ARRAY);
                element.vbo_ptr.build(data, data_len, elem_len);
            }
        }
        
        public VertexArray(){
            vertex = new vertex_array_element_t();
            normal = new vertex_array_element_t();
            texture = new vertex_array_element_t();
            
            this.maxAttr = Render.getMaxBindBuffers();
            if (this.maxAttr > 0){ 
                this.attrib = new vertex_array_element_t[this.maxAttr];
                for (int i=0; i<this.maxAttr; i++) this.attrib[i] = new vertex_array_element_t();
            }
            
            draw_typ = VBODrawModeE.VD_TRIANGLES;
        }
        //public ~Vertex_array();

        public void setVertexPointer(FloatBuffer data, int elem_len, int data_len, boolean isUseVBO){
            this.createArrayElement(vertex, data, elem_len, data_len, isUseVBO);
        }
        public void setNormalPointer(FloatBuffer data, int elem_len, int data_len, boolean isUseVBO){
            this.createArrayElement(normal, data, elem_len, data_len, isUseVBO);
        }
        public void setTexturePointer(FloatBuffer data, int elem_len, int data_len, boolean isUseVBO){
            this.createArrayElement(texture, data, elem_len, data_len, isUseVBO);
        }
        public void setAttribPointer(int n, String name, FloatBuffer data, int elem_len,  int data_len, boolean isUseVBO){
            if (n>=this.maxAttr) return;
            this.createArrayElement(attrib[n], data, elem_len, data_len, isUseVBO);
            attrib[n].attribs.varib_name = name;
        }

        public void setDrawType(VBODrawModeE typ) {this.draw_typ = typ;}

        public void bindAll(){
            if (vertex.dataptr != null) GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY); else return;
            if (normal.dataptr != null) GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            if (texture.dataptr != null) GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            
            if (this.vertex.dataptr != null) {
                if (this.vertex.vbo_ptr != null){
                    this.vertex.vbo_ptr.bind();
                    GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
		}else{
                    GL11.glVertexPointer(3, 0, this.vertex.dataptr);
		}
            } else return; //throw(FW_ERROR_VBO_ARRAY_NOT_BUILT);

            if (this.normal.dataptr != null){
		if (this.normal.vbo_ptr != null){
			this.normal.vbo_ptr.bind();
			GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);
		}else{
			GL11.glNormalPointer(0, this.normal.dataptr);
		}	
            }

            if (this.texture.dataptr != null){
		if (this.texture.vbo_ptr != null){
			this.texture.vbo_ptr.bind();
			GL11.glTexCoordPointer( 2, GL11.GL_FLOAT, 0, 0);
		}else{
			GL11.glTexCoordPointer( 2, 0, this.texture.dataptr);
		}	
            }
        }
        
        public void drawByIndex(IntBuffer mesh_d, int mesh_s){
            int drawmode = this.draw_typ.resolve();
            //int indextyp = GL11.GL_UNSIGNED_INT; //ez most fix32 bit lesz
	
            if (mesh_d != null){ //ha van index akkor az indexek szerint rajzol
		mesh_d.rewind();
                GL11.glDrawElements(drawmode, mesh_d);
            }
            else 
		GL11.glDrawArrays(drawmode, 0, mesh_s);

            Render.addPolyCount(mesh_s);
            Render.addVertexCount(this.vertex.array_length);

        }
        
        public void unbindAll(){
            // ha van VBO kapcsolja ki
            if (this.vertex.vbo_ptr != null) this.vertex.vbo_ptr.unbind();
            if (this.normal.vbo_ptr != null) this.normal.vbo_ptr.unbind();
            if (this.texture.vbo_ptr != null) this.texture.vbo_ptr.unbind();
            
            // kikapcsolja a client statet
            if (vertex.dataptr != null) GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY); else return; 
            if (normal.dataptr != null) GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
            if (texture.dataptr != null) GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        }
}


