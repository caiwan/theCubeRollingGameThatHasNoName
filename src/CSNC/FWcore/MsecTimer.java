package CSNC.FWcore;

import java.util.Date;

/**
 * Ket hivas kozt eltelt idot adja vissza
 * @author caiwan
 */
public class MsecTimer {
    private long lDateTime;
    
    public MsecTimer(){
        this.lDateTime = 0;
    }
    
    /**
     * Ket fuggveny hivas kozti idot kiszamolja.
     * @return delta ido
     */
    public double getDeltaTimems(){
         if (lDateTime == 0) lDateTime = new Date().getTime();
         long ujtime = new Date().getTime();
         long delta = ujtime - lDateTime;
         this.lDateTime = ujtime;
         
         return (double) delta;
    }
    
    public double getDeltaTime(){      
         return getDeltaTimems() / 1000f;
    }
}
