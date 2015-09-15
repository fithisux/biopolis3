package biopolisdata;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;
import javax.xml.bind.annotation.*;
import  com.google.gson.Gson;
import com.google.gson.reflect.*;
/**
 *
 * @author vanag
 */
@XmlRootElement
public class WSResult<T> {
    public String extype;
    public String message;
    @XmlAnyElement
    public T[] result;
    public boolean status;
    
    public WSResult(String desc,String message) {
        this.extype=desc;
        this.message=message;
        this.status=false;
        this.result=null;
    }
    
     public WSResult(List<T> x) {
        this.extype=null;
        this.message=null;
        this.status=true;
        if(x.size()>0)
        {
            this.result=(T[]) new Object[x.size()]; 
            for(int i=0;i<x.size();i++)
            {
                this.result[i]=x.get(i);
            }
        }
    }
     
     public WSResult(T x) {
        this.extype=null;
        this.message=null;
        this.status=true;
        this.result=(T[]) new Object[1]; 
        this.result[0]=x;        
    }
}

   