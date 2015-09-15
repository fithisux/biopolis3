/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolisdata;
import javax.xml.bind.annotation.*;
/**
 *
 * @author FITHIS
 */
@XmlRootElement
public class BiopolisWeightedTag implements Comparable<BiopolisWeightedTag>
{
    public float weight;
    public int tag;
    
    public int compareTo(BiopolisWeightedTag o)
    {
        if(o.weight > this.weight) return -1;
        if(o.weight < this.weight) return 1;
        return 0;
    }
}
