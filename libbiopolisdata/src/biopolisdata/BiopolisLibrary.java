/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package biopolisdata;

import biopolisdata.queries.BiopolisPackageQuery;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
/**
 *
 * @author vanag
 */
@XmlRootElement
public class BiopolisLibrary extends BiopolisDescriptor
{
    @NotNull    
    public BiopolisPackageQuery query;
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
        map.put("query",query);
        return map;
    }
    
    

    
}
