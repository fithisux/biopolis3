/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolisdata.topological;
import java.util.*;
/**
 *
 * @author Manolis
 */
public class BiopolisNode 
{
    public int coreid;
    public List<BiopolisNode> children;
    public List<BiopolisNode> parents;
    public int count;
    
    public BiopolisNode(int id)
    {
        this.coreid=id;
        this.children=new ArrayList<BiopolisNode>();
        this.parents=new ArrayList<BiopolisNode>();
    }  
}
