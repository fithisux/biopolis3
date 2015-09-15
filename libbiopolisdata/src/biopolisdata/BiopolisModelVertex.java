package biopolisdata;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import biopolisdata.queries.BiopolisSpatialQuery;
import java.util.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
/**
 *
 * @author vanag
 */
@XmlRootElement
public class BiopolisModelVertex extends BiopolisDescriptor
{

    public int coreid;
    public BiopolisSpatialQuery top;
    @NotNull
    @Size(min=1)
    public String title;
    @NotNull
    @Size(min=1)
    public String comments;

    public Map<String,Object> getMap()
    {
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("title",title);
        map.put("comments",comments);
        map.put("coreid",coreid);
        if(top!=null)
        {
            map.put("latitude",top.latitude);
            map.put("longitude",top.longitude);
            map.put("radius",top.radius);
        }
        return map;
    }

    public String getTitle() {
        return title;
    }

    public String getComments() {
        return comments;
    }
    

    
    
    public boolean equals(BiopolisModelVertex other)
    {
        //we do not check hidden
        if(this.coreid!= other.coreid) return false;
        if(!this.comments.equalsIgnoreCase(other.comments)) return false;
        if(!this.title.equalsIgnoreCase(other.title)) return false;
        if((this.top == null) && (other.top!=null)) return false;
        if((this.top != null) && (other.top==null)) return false;
        return this.top.equals(other.top);        
    }
}
