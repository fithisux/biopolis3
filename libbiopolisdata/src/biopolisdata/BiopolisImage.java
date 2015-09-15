package biopolisdata;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
/**
 *
 * @author vanag
 */
@XmlRootElement
public class BiopolisImage {

    @NotNull    
    public BiopolisPackage metadata;
    @NotNull
    @Size(min=1)
    public String data;
}
