package CSNC.FWmath;

/**
 * Vektor es matrix muveleteket lehet vegrehajtani.
 * Ez a C-s keretrendszerembol valo, megtoldva a java marhasagaival. Atombiztos
 * meg 486 processzoron is. Tenyleg. Eredetileg ARM processzorra irtam, ha ott
 * jo, akkor JVM-ben is jo lesz.
 * Fixen 3 komponensu vektorokkal es 4x4 matrixokkal mukodik. (nagyobb nem kell)
 * !!!!!! NEM THREADSAFE !!!!!!!
 * !!!!!! SYNCRONIZED KUCSSZOVAL PEDIG >>LASSABB<< LESZ !!!!!!
 * Jah, es ez volt a leghatekonyabb megoldas, mindkozul, sajnos a Java ennyit 
 * tud.
 * @author CAIWAN
 */
public final class VMath {
    // ezek ellenorzik, hogy a vektor az 3 taugu-e
    // illetve a matrix 16 (4x4)
    private static final boolean SAFE_TEST = true;
    
    private static boolean __testV(float ptr[]){
        return ptr != null?ptr.length == 3:false;
    }
    
    private static boolean __testM(float ptr[]){
        return ptr != null?ptr.length == 16:false;
    }
    
    // VEKTOR MUVELETEK
    /**
     * v <- (x, y, z)
     * @param v
     * @param x
     * @param y
     * @param z 
     */
    public static void setV(float[]v, float x, float y, float z){
        if (VMath.SAFE_TEST) if (!__testV(v)) return;
        v[0] = x;
        v[1] = y;
        v[2] = z;
    }
    
    /**
     * dst <- src
     * @param src
     * @param dst 
     */
    public static void copyV(float[]src, float[]dst){
        if (VMath.SAFE_TEST){
            if (!__testV(src)) return;
            if (!__testV(dst)) return;
        }
        dst[0] = src[0];
        dst[1] = src[1];
        dst[2] = src[2];
    }
    
    /**
     * dst <- (a+b)
     * @param a
     * @param b
     * @param dst 
     */
    public static void addV(float[]a, float []b, float []dst){
        if (VMath.SAFE_TEST){
            if (!__testV(dst)) return;
            if (!__testV(a)) return;
            if (!__testV(b)) return;
        }
        
        dst[0] = a[0]+b[0];
        dst[1] = a[1]+b[1];
        dst[2] = a[2]+b[2];
    }
    
    /**
     * dst <- (a-b)
     * @param a
     * @param b
     * @param dst 
     */
    public static void subV(float[]a, float []b, float []dst){
        if (VMath.SAFE_TEST) {
            if (!__testV(dst)) return;
            if (!__testV(a)) return;
            if (!__testV(b)) return;
        }
        
        dst[0] = a[0]-b[0];
        dst[1] = a[1]-b[1];
        dst[2] = a[2]-b[2];
    }
    
    /**
     * dst <- (a*b)
     * @param a
     * @param b
     * @param dst 
     */
    public static void mulV(float[]a, float []b, float []dst){
        if (VMath.SAFE_TEST){
            if (!__testV(dst)) return;
            if (!__testV(a)) return;
            if (!__testV(b)) return;
        }
        
        dst[0] = a[0]*b[0];
        dst[1] = a[1]*b[1];
        dst[2] = a[2]*b[2];
    }
    
    
    /**
     * dst <-(a*(b,b,b))
     * @param a
     * @param b
     * @param dst 
     */
    public static void mulV(float[]a, float b, float []dst){
        if (VMath.SAFE_TEST){
            if (!__testV(dst)) return;
            if (!__testV(a)) return;
        }
        
        dst[0] = a[0]*b;
        dst[1] = a[1]*b;
        dst[2] = a[2]*b;
    }
    
    /**
     * dst <- (a/b)
     * @param a
     * @param b
     * @param dst 
     */
    public static void divV(float[]a, float []b, float []dst){
        if (VMath.SAFE_TEST){
            if (!__testV(dst)) return;
            if (!__testV(a)) return;
            if (!__testV(b) || (b[0]*b[1]*b[2]) == 0.f) return;
        }
        
        dst[0] = a[0]/b[0];
        dst[1] = a[1]/b[1];
        dst[2] = a[2]/b[2];
    }
    
    /**
     * dst <- (a/(b,b,b))
     * @param a
     * @param b
     * @param dst 
     */
    public static void divV(float[]a, float b, float []dst){
        if (VMath.SAFE_TEST){
            if (!__testV(dst)) return;
            if (!__testV(a)) return;
            if (b == 0.f) return;
        }
        dst[0] = a[0]/b;
        dst[1] = a[1]/b;
        dst[2] = a[2]/b;
    }
    /**
     * dst <- (a X b)
     * @param a
     * @param b
     * @param dst 
     */
    public static void crossV(float []a, float []b, float []dst){
        if (VMath.SAFE_TEST){
            if (!__testV(dst)) return;
            if (!__testV(a)) return;
            if (!__testV(b)) return;
        }
        
        // ... 
        float d0 =   (a[1] * b[2]) - (a[2] * b[1]) ;
	float d1 = -((b[2] * a[0]) - (b[0] * a[2]));
	float d2 =   (a[0] * b[1]) - (a[1] * b[0]);
        
        dst[0] = d0; //ha a vagy b == dst ne legyen gond
        dst[1] = d1;
        dst[2] = d2;
    }
    
    /**
     * a es b vektor vektorialis szorzata
     * @param a
     * @param b
     * @return a dot b
     */
    public static float dotV(float []a, float []b){
        if (VMath.SAFE_TEST){
            if (!__testV(a)) return 0f;
            if (!__testV(b)) return 0f;
        }
        return a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
    }
    
    /**
     * Vektor hossza
     * @param src
     * @return 
     */
    public static float lengthV(float []src){
        if (VMath.SAFE_TEST)
            if (!__testV(src)) return 0.0f;
        return (float)Math.sqrt(src[0]*src[0] + src[1]*src[1] + src[2]*src[2]);
    }
    /**
     * dst <- (src / length(src))
     * @param src
     * @param dst 
     */
    public static void normalizeV(float[]src, float[]dst){
        if (VMath.SAFE_TEST){
            if (!__testV(src)) return;
            if (!__testV(dst)) return;
        }
        float vlen = lengthV(src);
        dst[0] = src[0] / vlen;
        dst[1] = src[1] / vlen;
        dst[2] = src[2] / vlen;
    }
    
    //MATRIX MUVELETEK
    
    public static void clearM(float []dst){
        if (VMath.SAFE_TEST){
            if (!__testM(dst)) return;
        }
        
        dst[0 ] = 0.0f; dst[4 ] = 0.0f; dst[8 ] = 0.0f; dst[12] = 0.0f; 
	dst[1 ] = 0.0f; dst[5 ] = 0.0f; dst[9 ] = 0.0f; dst[13] = 0.0f; 
	dst[2 ] = 0.0f; dst[6 ] = 0.0f; dst[10] = 0.0f; dst[14] = 0.0f; 
	dst[3 ] = 0.0f; dst[7 ] = 0.0f; dst[11] = 0.0f; dst[15] = 0.0f; 
    }
    
    /**
     * dst <- I
     * @param dst 
     */
    public static void identityM(float []dst){
        if (VMath.SAFE_TEST){
            if (!__testM(dst)) return;
        }
        
        dst[0 ] = 1.0f; dst[4 ] = 0.0f; dst[8 ] = 0.0f; dst[12] = 0.0f; 
	dst[1 ] = 0.0f; dst[5 ] = 1.0f; dst[9 ] = 0.0f; dst[13] = 0.0f; 
	dst[2 ] = 0.0f; dst[6 ] = 0.0f; dst[10] = 1.0f; dst[14] = 0.0f; 
	dst[3 ] = 0.0f; dst[7 ] = 0.0f; dst[11] = 0.0f; dst[15] = 1.0f; 
    }
    
    private static float mtmp[], tmp[];
    
    /**
     * dst <- src
     * @param src
     * @param dst 
     */
    public static void copyM(float []src, float []dst){
        if (VMath.SAFE_TEST){
            if (!__testM(src)) return;
            if (!__testM(dst)) return;
        }
        // sajnos a tagonkkenti megadas bizonyult a leghtekonyabbnak (szemben a 
        // ciklussal)
        dst[0 ] = src[0 ]; dst[4 ] = src[4 ]; dst[8 ] = src[8 ]; dst[12] = src[12]; 
	dst[1 ] = src[1 ]; dst[5 ] = src[5 ]; dst[9 ] = src[9 ]; dst[13] = src[13]; 
	dst[2 ] = src[2 ]; dst[6 ] = src[6 ]; dst[10] = src[10]; dst[14] = src[14]; 
	dst[3 ] = src[3 ]; dst[7 ] = src[7 ]; dst[11] = src[11]; dst[15] = src[15]; 
    }
    
    /**
     * dst <- (A*B)
     * @param a
     * @param b
     * @param dst 
     */
    public static void multiplyM(float []a, float []b, float []dst){
        if (VMath.SAFE_TEST){
            if (!__testM(a)) return;
            if (!__testM(b)) return;
            if (!__testM(dst)) return;
        }
        
        if (mtmp == null){
            mtmp = new float[16];
        }
        
        if (tmp == null){
            tmp = new float[16];
        }
        
        if (dst != a && dst != b){
            clearM(dst);
            for (int j=0; j<4; j++)
                for (int k=0; k<4; k++)
                    for (int i=0; i<4; i++)
                        dst[j*4+k] += a[4*j+i]*b[4*i+k];
        }
        else
        {
            clearM(mtmp);
            for (int j=0; j<4; j++)
                for (int k=0; k<4; k++)
                    for (int i=0; i<4; i++)
                        mtmp[j*4+k] += a[4*j+i]*b[4*i+k];
            copyM(mtmp, dst);
        }
    }
    
    /**
     * dst <- (src * translate(x,y,z))
     * @param src
     * @param x
     * @param y
     * @param z
     * @param dst 
     */
    public static void translateM(float []src, float x, float y, float z, float []dst){
        if (VMath.SAFE_TEST){
            if (!__testM(src)) return;
            if (!__testM(dst)) return;
        }
        
        if (tmp == null){
            tmp = new float[16];
        }
        
        // ha a forras es cel megegyezik, akkor csak tolja el a 4. oszlopot
        if (src == dst){
            dst[12] = src[12] + x; 
            dst[13] = src[13] + y; 
            dst[14] = src[14] + z; 
        } 
        // ha nem, akkor tmp-n keresztul szorotta fel.
        else {
            identityM(tmp);
            tmp[12] = x;
            tmp[13] = y;
            tmp[14] = z;
            multiplyM(src, tmp, dst);
        }
    }
    /**
     * 
     * @param src
     * @param alpha
     * @param dst 
     */
    public static void rotate2D(float []src, float alpha, float[]dst){
        if (VMath.SAFE_TEST){
            if (!__testM(src)) return;
            if (!__testM(dst)) return;
        }
        
        if (tmp == null){
            tmp = new float[16];
        }
        
        identityM(tmp);
        tmp[0] = (float)Math.cos(alpha);
	tmp[1] = (float)Math.sin(alpha);
	tmp[4] = -tmp[1];
	tmp[5] = tmp[0];
        multiplyM(tmp, src, dst);
    }
    
    /**
     * 
     * @param src
     * @param x
     * @param y
     * @param z
     * @param _alpha
     * @param dst 
     */
    public static void rotate3D(float []src, float x, float y, float z, float _alpha, float []dst){
        if (VMath.SAFE_TEST){
            if (!__testM(src)) return;
            if (!__testM(dst)) return;
        }

	float alpha = (float)Math.PI*(_alpha/180f),
                c = (float)Math.cos(alpha),
                s = (float)Math.sin(alpha);
        
        if (tmp == null){
            tmp = new float[16];
        }
        
	//row 1
	tmp[0 ] = x*x*(1f-c)+c;
	tmp[4 ] = x*y*(1f-c)-z*s;
	tmp[8 ] = x*z*(1f-c)+y*s;
	tmp[12] = 0.0f;
	//row 2
	tmp[1 ] = y*x*(1f-c)+z*s;
	tmp[5 ] = y*y*(1f-c)+c;
	tmp[9 ] = y*z*(1f-c)-x*s;
	tmp[13] = 0.0f;
	//row 3
	tmp[2 ] = z*x*(1f-c)-y*s;
	tmp[6 ] = z*y*(1f-c)+x*s;
	tmp[10] = z*z*(1f-c)+c;
	tmp[14] = 0.0f;
	//row 4
	tmp[3 ] = 0.0f;
	tmp[7 ] = 0.0f;
	tmp[11] = 0.0f;
	tmp[15] = 1.0f;

	multiplyM(tmp, src, dst);
    }       
    
    private static float tmpVec[];
    
    /**
     * dstVec <- srcMat * srcVec
     * @param srcMat
     * @param srcVec 
     * @param dstVec 
     * @param w  
     */
    public static void mulMV(float []srcMat, float []srcVec, float []dstVec, float w){
        if (VMath.SAFE_TEST){
            if (!__testM(srcMat)) return;
            if (!__testV(srcVec)) return;
            if (!__testV(dstVec)) return;
        }
        if(tmpVec == null) tmpVec = new float[3];
        
        // bizonyos esetekben lehet 0-ra tesztelni binarisan floatot.
        // javaban nem
        //if (*((int*)(void*)&w) == 0)
        if (w>0 || w<0){
            tmpVec[0] = srcMat[0]*srcVec[0] + srcMat[4]*srcVec[1] + srcMat[8 ]*srcVec[2] + w * srcMat[12];
            tmpVec[1] = srcMat[1]*srcVec[0] + srcMat[5]*srcVec[1] + srcMat[9 ]*srcVec[2] + w * srcMat[13];
            tmpVec[2] = srcMat[2]*srcVec[0] + srcMat[6]*srcVec[1] + srcMat[10]*srcVec[2] + w * srcMat[14];
        } else {
            tmpVec[0] = srcMat[0]*srcVec[0] + srcMat[4]*srcVec[1] + srcMat[8 ]*srcVec[2];
            tmpVec[1] = srcMat[1]*srcVec[0] + srcMat[5]*srcVec[1] + srcMat[9 ]*srcVec[2];
            tmpVec[2] = srcMat[2]*srcVec[0] + srcMat[6]*srcVec[1] + srcMat[10]*srcVec[2];
        }
        copyV(tmpVec, dstVec);
    }
}
