/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CSNC.BuiltInResources;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

/**
 * Egy 2*2*2 meretu kocka adatait tartalmazza.
 * @author caiwan
 */
public final class CubeData {

    /**
     * csucspontok koordinatai
     */
public static final float cube_vertices[] = {	
		//0 top
		-1.0f, -1.0f,  1.0f,	//0
		 1.0f, -1.0f,  1.0f,	//1
		 1.0f,  1.0f,  1.0f,	//2
		-1.0f,  1.0f,  1.0f,	//3
		//1 bottom
		-1.0f, -1.0f, -1.0f,	//4
		-1.0f,  1.0f, -1.0f,	//5
		 1.0f,  1.0f, -1.0f,	//6
		 1.0f, -1.0f, -1.0f,	//7
		//2 back
		-1.0f,  1.0f, -1.0f,	//8
		-1.0f,  1.0f,  1.0f,	//9
		 1.0f,  1.0f,  1.0f,	//10
		 1.0f,  1.0f, -1.0f,	//11
		//3 front
		-1.0f, -1.0f, -1.0f,	//12
		 1.0f, -1.0f, -1.0f,	//13
		 1.0f, -1.0f,  1.0f,	//14
		-1.0f, -1.0f,  1.0f,	//15
		//4 right
		 1.0f, -1.0f, -1.0f,	//16
		 1.0f,  1.0f, -1.0f,	//17
		 1.0f,  1.0f,  1.0f,	//18
		 1.0f, -1.0f,  1.0f,	//19
		//5 left
		-1.0f, -1.0f, -1.0f,	//20
		-1.0f, -1.0f,  1.0f,	//21
		-1.0f,  1.0f,  1.0f,	//22
		-1.0f,  1.0f, -1.0f	//23
};

/**
 * Csucspontokra vonatkozo normal vektor
 */
public static final float cube_normals[] = {
		
		0.0f, 0.0f, 1.0f,
		0.0f, 0.0f, 1.0f,
		0.0f, 0.0f, 1.0f,
		0.0f, 0.0f, 1.0f,
		
		0.0f, 0.0f,-1.0f,
		0.0f, 0.0f,-1.0f,
		0.0f, 0.0f,-1.0f,
		0.0f, 0.0f,-1.0f,
		
		0.0f, 1.0f, 0.0f,
		0.0f, 1.0f, 0.0f,
		0.0f, 1.0f, 0.0f,
		0.0f, 1.0f, 0.0f,
		
		0.0f,-1.0f, 0.0f,
		0.0f,-1.0f, 0.0f,
		0.0f,-1.0f, 0.0f,
		0.0f,-1.0f, 0.0f,
		
		1.0f, 0.0f, 0.0f,
		1.0f, 0.0f, 0.0f,
		1.0f, 0.0f, 0.0f,
		1.0f, 0.0f, 0.0f,
		
		-1.0f, 0.0f, 0.0f,
		-1.0f, 0.0f, 0.0f,
		-1.0f, 0.0f, 0.0f,
		-1.0f, 0.0f, 0.0f
};

/**
 * Kocka csucspontjaiban levo textura koordinata
 */
public static final float cube_texture_uvs[] = {		
		0.0f, 0.0f, 		
		1.0f, 0.0f, 
		1.0f, 1.0f, 
		0.0f, 1.0f, 
		
		1.0f, 0.0f, 
		1.0f, 1.0f, 
		0.0f, 1.0f, 
		0.0f, 0.0f, 
		
		0.0f, 1.0f, 
		0.0f, 0.0f, 
		1.0f, 0.0f, 
		1.0f, 1.0f, 

		1.0f, 1.0f, 
		0.0f, 1.0f, 
		0.0f, 0.0f, 
		1.0f, 0.0f, 
		
		1.0f, 0.0f, 
		1.0f, 1.0f, 
		0.0f, 1.0f, 
		0.0f, 0.0f, 
		
		0.0f, 0.0f, 		
		1.0f, 0.0f, 
		1.0f, 1.0f, 
		0.0f, 1.0f
};

/**
 * Kockat alkoto haromszogek indexei
 */
public static final int cube_indices[] = {
	0,   1,  2,     2,  3,  0,		// 0
	4,   5,  6,     6,  7,  4,		// 1
        8,   9, 10,    10, 11,  8,		// 2
	12, 13, 14,    14, 15, 12,		// 3
	16, 17, 18,    18, 19, 16,              // 4
        20, 21, 22,    22, 23, 20               // 5
};


public static final int cube_vertex_length = CubeData.cube_vertices.length / 3;
public static final int cube_indices_length = CubeData.cube_indices.length;

public static final FloatBuffer cube_vertices_buffer = BufferUtils.createFloatBuffer(cube_vertex_length*3).put(cube_vertices);
public static final FloatBuffer cube_normal_buffer = BufferUtils.createFloatBuffer(cube_vertex_length*3).put(cube_normals);
public static final FloatBuffer cube_texture_buffer = BufferUtils.createFloatBuffer(cube_vertex_length*2).put(cube_texture_uvs);

public static final IntBuffer cube_indices_buffer = BufferUtils.createIntBuffer(cube_indices_length).put(cube_indices);

}
