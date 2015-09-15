/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package biopolisdata.topological;

import biopolisdata.topological.exceptions.BiopolisModelExtensionException;
import biopolisdata.*;
import biopolisdata.topological.exceptions.BiopolisModelStructureException;
import java.util.*;
/**
 *
 * @author vanag
 */
public class BiopolisSortedModel 
{
    public BiopolisModelVertex [] sortedvertices;
    public BiopolisNode[] treerep;
    public HashSet<String> adjacency;
    public BiopolisDiff asDiff()
    {
        BiopolisDiff diff=new BiopolisDiff();
        diff.sortedvertices=this.sortedvertices;
        diff.adjacency=this.adjacency;
        return diff;
    }
    public BiopolisDiff getDiff(BiopolisSortedModel other) throws BiopolisModelExtensionException
    {
        BiopolisDiff diff=new BiopolisDiff();
        
        if(this.sortedvertices.length > other.sortedvertices.length) throw new BiopolisModelExtensionException("Less nodes");
        for(int i=0;i<this.sortedvertices.length;i++)
        {
            if(!this.sortedvertices[i].equals(other.sortedvertices[i])) throw new BiopolisModelExtensionException("Renamed nodes");            
        }
        
        for(String s : this.adjacency)
        {
            if(!other.adjacency.contains(s))
            {
                throw new BiopolisModelExtensionException("Deleted link");
            }
        }
        
        diff.sortedvertices=new BiopolisModelVertex[other.sortedvertices.length-this.sortedvertices.length];
        
        for(int i=this.sortedvertices.length;i<other.sortedvertices.length;i++)
        {
            diff.sortedvertices[i-this.sortedvertices.length]=other.sortedvertices[i];
        }
        
        diff.adjacency=new HashSet<String>();
        
        for(String s : other.adjacency)
        {
            if(!this.adjacency.contains(s))
            {
                diff.adjacency.add(s);
            }
        }
        return diff;
    }
    
    public BiopolisSortedModel(BiopolisModel tree)  throws BiopolisModelStructureException
    {
        this.adjacency=new HashSet<String>();
        this.sortedvertices=this.sortVertices(tree);
        this.treerep=new BiopolisNode[this.sortedvertices.length]; 
        for(int i=0;i<this.sortedvertices.length;i++)
        {
            this.treerep[i]=new BiopolisNode(i);           
        }
        this.sortEdges(tree);
        
        //we find if there are cycles
        int [] found=new int[this.sortedvertices.length];        
        for(int i=0;i<this.sortedvertices.length;i++)
        {
            found[i]=this.treerep[i].parents.size();
        }
        
        int events=0;
        do
        {
            events=0;
            //find roots
            for(int i=0;i<found.length;i++)
            {
                if(found[i]==0) //we found a root
                {
                    for(BiopolisNode bn : this.treerep[i].children)
                    {
                        found[bn.coreid]--;
                    }
                    found[i]=-1;
                    events++;
                }
            }
        }while(events>0);
        for(int b : found)
        {
            if(b != -1) throw new BiopolisModelStructureException("cycle deteceted");
        }
    }
    
    private BiopolisModelVertex [] sortVertices(BiopolisModel tree) throws BiopolisModelStructureException
    {
        int max=0;
        int min=0;        
        BiopolisModelVertex[] test=new BiopolisModelVertex[tree.vertices.length];
        boolean firstTime=true;
        for(BiopolisModelVertex v : tree.vertices)
        {
            if((v.coreid < 0) || (v.coreid >= tree.vertices.length)) 
            {
                throw new  BiopolisModelStructureException("illegal index in model");
            }
            if(firstTime)
            {
                max=min=v.coreid;firstTime=false;
            }
            else
            {
                if(v.coreid > max) max=v.coreid;
                if(v.coreid < min) min=v.coreid;                    
            }
            if(test[v.coreid]!=null)
            {
                throw new BiopolisModelStructureException("duplicate index "+v.coreid+" in model"); 
            }
            else
            {
                test[v.coreid]=v;
            }
        }
        
        //check contiguity, maximum checking is not necessary
        if(min != 0) throw new BiopolisModelStructureException("non zero min index in model");
        for(BiopolisModelVertex  v : test)
        {
            if(v==null) throw new BiopolisModelStructureException("index gap in model");
        } 
        
        //check if duplicate names exist      
        for(BiopolisModelVertex v1 : tree.vertices)
        {
            for(BiopolisModelVertex v2 : tree.vertices)
            {
                if(v1.title.equalsIgnoreCase(v2.title) && (v1.coreid != v2.coreid))
                {
                    throw new BiopolisModelStructureException("duplicate names in model");
                }
            }
        }
        
        return test;
    }

    private void sortEdges(BiopolisModel tree) throws BiopolisModelStructureException
    {
        for(BiopolisModelLink e : tree.links)
        {
            if( (e.from < 0) || (e.from >= tree.vertices.length))
            {
                throw new BiopolisModelStructureException("illegal edge from "+e.from+" in model");
            }
            if( (e.to < 0) || (e.to >= tree.vertices.length))
            {
                throw new BiopolisModelStructureException("illegal edge to "+e.to+" in model");
            }
            if( e.from == e.to)
            {
                throw new BiopolisModelStructureException("cyclic edge with from "+e.from+" in model");
            }
                
            String s=e.from+";"+e.to;
            if(this.adjacency.contains(s))
            {
                throw new BiopolisModelStructureException("duplicate edge in model");
            }
            else
            {
                this.treerep[e.from].children.add(this.treerep[e.to]);
                this.treerep[e.to].parents.add(this.treerep[e.from]);
                this.adjacency.add(s);
            }
        }
    }
    
}
