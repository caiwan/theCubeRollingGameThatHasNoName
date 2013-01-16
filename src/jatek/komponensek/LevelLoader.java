/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jatek.komponensek;

import CSNC.FWcore.XMLToolkit;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.File;
import java.io.FileNotFoundException;
import org.w3c.dom.Attr;

/**
 *
 * @author caiwan
 */
public class LevelLoader {
///////////////////////////////////////////////////////////////////////////
    
    private static final String XML_TOKEN_levels = "levels";
    private static final String XML_TOKEN_level  = "level";
    
    private static final String XML_TOKEN_id = "id";
    private static final String XML_TOKEN_score = "score";
    private static final String XML_TOKEN_comment = "comment"; 
    
///////////////////////////////////////////////////////////////////////////    
    private String path;
    private String rawdata[];
    private int score[];
    private String comment[];
    
    private int num_levels;
    
    public LevelLoader(String path){
        this.path = path;
    }
    
    public void parse() throws Exception {
        File fp = new File(path);
        if (!fp.exists()) throw new FileNotFoundException();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fp);
        doc.getDocumentElement().normalize();

        if (!XML_TOKEN_levels.equals(doc.getDocumentElement().getNodeName())) throw new Exception();

        Node node, child;
        NodeList nodeList, children;
        Element elem;
        Attr attr;

        nodeList = doc.getElementsByTagName(XML_TOKEN_level);
        int len;
        num_levels = len = nodeList.getLength();

        // alloc 
        rawdata = new String[len];
        score = new int[len];
        comment = new String[len];

        for(int i=0; i<len; i++){
            node = nodeList.item(i);
            elem = (Element) node;

            attr = elem.getAttributeNode(XML_TOKEN_score);
            score[i] = XMLToolkit.AttribToInt(attr);

            attr = elem.getAttributeNode(XML_TOKEN_comment);
            comment[i] = (attr == null)?"":attr.getValue();

            /*
            child = elem.getFirstChild();
            if (child instanceof CharacterData) {
                CharacterData cd = (CharacterData) child;
                rawdata[i] = cd.getData();
            } else {
                throw new Exception();
            }
            */

            children = node.getChildNodes();
            for(int j=0; j<children.getLength(); j++){
                child = children.item(j);

                rawdata[i] = "";
                if (child instanceof CharacterData) {
                    CharacterData cd = (CharacterData) child;
                    String kk = cd.getNodeName();
                    if (cd != null)
                        rawdata[i] += cd.getData()+"\n";
                }
            }

        }
    }
    
    public String[] getArray(int l){
        if (l>=this.num_levels) return null;
        String str = rawdata[l];
        String lines[] = str.split("\n");
        
        return lines;
    }
 
    public int getMaxScore(int l){
        if (l>=this.num_levels) return -1;
        return score[l];
    }
}
