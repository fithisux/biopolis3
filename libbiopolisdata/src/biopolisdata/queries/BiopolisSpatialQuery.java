/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolisdata.queries;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import biopolisdata.BiopolisModelVertex;
import java.util.List;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.*;
/**
 *
 * @author vanag
 */
@XmlRootElement
public class BiopolisSpatialQuery
{
    public double radius;
    public double latitude;
    public double longitude;
    
    public boolean equals(BiopolisSpatialQuery other)
    {
        //we do not check hidden
        if(this.radius!= other.radius) return false;
        if(this.latitude!= other.latitude) return false;
        if(this.longitude!= other.longitude) return false;
        return true;        
    }
}
