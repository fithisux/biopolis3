package biopolisdata.queries;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import biopolisdata.BiopolisPackage;
import java.util.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
/**
 *
 * @author vanag
 */
@XmlRootElement
public class BiopolisSegmentQuery {

    @NotNull
    @Size(min=1)
    public String nodetype;   
    public long from;  
    @NotNull
    @Size(min=1)
    public String biopolisid;
}
