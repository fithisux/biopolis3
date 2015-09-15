/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolisdata.queries;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
/**
 *
 * @author vanag
 */
@XmlRootElement
public class BiopolisModelQuery
{
    public Long[] tags;
    public Long from;
    public Long to;    
}

