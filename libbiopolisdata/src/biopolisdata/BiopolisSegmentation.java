package biopolisdata;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import biopolisdata.BiopolisPackage;
import java.util.*;
import javax.xml.bind.annotation.*;
/**
 *
 * @author vanag
 */
@XmlRootElement
public class BiopolisSegmentation {

    public String nodetype;   
    public long results; 
    public String biopolisid;
}
