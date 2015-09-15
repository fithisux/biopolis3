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
public class BiopolisGallery extends BiopolisDescriptor
{    
    @NotNull
    @Size(min=1)
    public String title;
    @NotNull
    @Size(min=1)
    public String comments;

    public String getTitle() {
        return title;
    }

    public String getComments() {
        return comments;
    }
    public Map<String,Object> getMap()
    {
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("title",title);
        map.put("comments",comments);
        return map;
    }
    
    
    
    
}
