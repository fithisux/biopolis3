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
public class BiopolisPackage  extends BiopolisDescriptor
{
    public double latitude;
    public double longitude;
    @NotNull
    @Size(min=1)
    public Long[] tags;     
    @NotNull
    @Size(min=1)
    public String title;
    @NotNull
    @Size(min=1)
    public String comments;
    public long captureTime;

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
        map.put("latitude",latitude);
        map.put("longitude",longitude);
        String s="";
        if((tags!=null) && (tags.length>0))
        {
            s=tags[0].toString();
            for(int i=1;i<tags.length;i++)
            {
                s+=";"+tags[i].toString();
            }            
        }
        map.put("tags",s);
        map.put("capturetime",captureTime);
        return map;
    }
 
    
    
}
