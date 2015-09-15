package biopolisdata;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.*;
import javax.validation.constraints.*;
/**
 *
 * @author vanag
 */
@XmlRootElement
public class BiopolisUser extends BiopolisDescriptor{
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
