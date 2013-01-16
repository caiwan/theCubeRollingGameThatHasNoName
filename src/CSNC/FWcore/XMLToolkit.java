package CSNC.FWcore;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.File;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

/**
 *
 * @author caiwan
 */
public class XMLToolkit {
    private static final String XML_TOKEN_true = "true";
    private static final String XML_TOKEN_false = "false";
    
    private static final String XML_TOKEN_enable = "enable";
    private static final String XML_TOKEN_disable = "disable";
    
    private static final String _exString = "XML Parse error" ;
    
    // attrib -> boolean
    public static boolean AttribToBoolean(Attr attr) throws Exception{
        if (attr == null) throw new Exception(_exString);
        String attrString = attr.getValue();
        if (attrString.equals(XML_TOKEN_true) || attrString.equals(XML_TOKEN_enable))
            return true;
        else if (attrString.equals(XML_TOKEN_false) || attrString.equals(XML_TOKEN_disable))
            return false;
        else 
            throw new Exception(_exString);
    }
    
    // attrib -> integer
    public static int AttribToInt(Attr attr) throws Exception{
        if (attr == null) throw new Exception(_exString);
        String attrString = attr.getValue();
        try {
            Integer val = new Integer(attrString);
            return val.intValue();
        } catch (NumberFormatException e){
            throw new Exception(_exString);
        }
    }
    
    public static String BooleanToToken(boolean b, boolean sw){
        if (sw)
            if (b) return XML_TOKEN_true;
            else   return XML_TOKEN_false;
        else 
            if (b) return XML_TOKEN_enable;
            else   return XML_TOKEN_disable;
    }   
}
