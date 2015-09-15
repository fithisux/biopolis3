/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolisdata.queries;

import java.util.List;
import javax.xml.bind.annotation.*;
/**
 *
 * @author vanag
 */
@XmlRootElement
public class BiopolisPackageQuery 
{
    public BiopolisModelQuery model_query;
    public BiopolisSpatialQuery spatial_query;
    public BiopolisTermQuery term_query;     
}
