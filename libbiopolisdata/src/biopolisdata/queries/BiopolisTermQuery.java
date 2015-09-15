/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolisdata.queries;
import javax.xml.bind.annotation.*;
/**
 *
 * @author vanag
 */
@XmlRootElement
public class BiopolisTermQuery
{
    public String[] c_keywords;    
    public String[] t_keywords;   
}
