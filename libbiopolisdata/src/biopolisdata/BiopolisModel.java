package biopolisdata;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.xml.bind.annotation.*;
import java.util.*;
import javax.validation.constraints.*;
/**
 *
 * @author vanag
 */
@XmlRootElement
public class BiopolisModel 
{
    @NotNull
    public BiopolisModelVertex [] vertices;
    @NotNull
    public BiopolisModelLink [] links;      
}
