/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolisrestclient;
import java.io.*;
import java.net.URI;
import java.util.*;
import java.io.*;
import java.net.URISyntaxException;
import com.google.gson.Gson;
import com.google.gson.reflect.*;
import org.apache.commons.codec.binary.Base64;
import biopolisdata.*;
import biopolisdata.queries.*;
/**
 *
 * @author Manolis
 */
public class BiopolisTester 
{
    public static String readFile(String filename)
    {
        java.nio.file.Path path = java.nio.file.Paths.get(filename);  
        try {
            byte[] data = java.nio.file.Files.readAllBytes(path);
            return new String(data,"UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }        
    }
    public static void main(String[] args)
    {
        BiopolisClient cc =new BiopolisClient();
        String biopolisJSON = BiopolisTester.readFile("c:/Intermediate/biopolis3/model.json");             
        String query2JSON = BiopolisTester.readFile("c:/Intermediate/biopolis3/query2.json");         
        String query3JSON = BiopolisTester.readFile("c:/Intermediate/biopolis3/query3.json");  
        String query4JSON = BiopolisTester.readFile("c:/Intermediate/biopolis3/query4.json");  
        String query5JSON = BiopolisTester.readFile("c:/Intermediate/biopolis3/query5.json");  
        String query6JSON = BiopolisTester.readFile("c:/Intermediate/biopolis3/query6.json");  
        String query7JSON = BiopolisTester.readFile("c:/Intermediate/biopolis3/query7.json");  
        String library = BiopolisTester.readFile("c:/Intermediate/biopolis3/library.json"); 
        String packageA = BiopolisTester.readFile("c:/Intermediate/biopolis3/packageA.json");
        
        java.nio.file.Path path = java.nio.file.Paths.get("c:/Intermediate/biopolis3/flower.jpg");
        String coded="";
        try {
            byte[] data1 = java.nio.file.Files.readAllBytes(path);
            coded= 	Base64.encodeBase64String(data1);
            //System.out.println(coded);
        } catch (IOException ex) {

        }
        
        BiopolisUser user1=new BiopolisUser();
        user1.title="vasilis";
        user1.comments="o vasilis";
        
        BiopolisUser user2=new BiopolisUser();
        user2.title="georgia";
        user2.comments="i georgia";
        
        BiopolisUser user3=new BiopolisUser();
        user3.title="manolis";
        user3.comments="o manolis";
        
        
        BiopolisImage img1=new BiopolisImage();
        img1.metadata=new BiopolisPackage();
        img1.metadata.captureTime=1000;
        img1.metadata.comments="photo1";
        img1.metadata.title="this";
        img1.metadata.latitude=1.0;
        img1.metadata.longitude=2.0;
        img1.metadata.tags=new Long[]{0L,1L};
        img1.data=coded;
        
       
        BiopolisImage img2=new BiopolisImage();
        img2.metadata=new BiopolisPackage();
        img2.metadata.captureTime=2000;
        img2.metadata.comments="photo2";
        img2.metadata.title="wraio";
        img2.metadata.latitude=1.0;
        img2.metadata.longitude=2.0;
        img2.metadata.tags=new Long[]{2L,4L};
        img2.data=coded;
        
        BiopolisImage img3=new BiopolisImage();
        img3.metadata=new BiopolisPackage();
        img3.metadata.captureTime=2000;
        img3.metadata.comments="photo3";
        img3.metadata.title="wraio";
        img3.metadata.latitude=1.0;
        img3.metadata.longitude=2.0;
        img3.metadata.tags=new Long[]{3L,1L};
        img3.data=coded;
        
        String rr;
        
        
        java.lang.reflect.Type listType;

        WSResult<String> r;
        WSResult<BiopolisResult<BiopolisModel>> model;
        WSResult<Integer> id;
        WSResult<BiopolisResult<BiopolisPackage>> pkg;
        
        
        
        
        //get echo       
        rr=cc.getEcho(String.class);
        listType = new TypeToken<WSResult<String>>(){}.getType();
        r=(new Gson()).fromJson(rr, listType);        
        if(r.status) System.out.println(r.result[0]);
        else System.out.println(r.extype+";"+r.message);   
        
        //put a model
        rr=cc.putModel(biopolisJSON,String.class);
        listType = new TypeToken<WSResult<String>>(){}.getType();
        r=(new Gson()).fromJson(rr, listType);        
        if(r.status) System.out.println(r.result[0]);
        else System.out.println(r.extype+";"+r.message);  
        
        //get model
        rr=cc.getModel(String.class);
        listType = new TypeToken<WSResult<BiopolisResult<BiopolisModel>>>(){}.getType();
        model=(new Gson()).fromJson(rr, listType);                
        if(model.status) System.out.println(rr);
        else System.out.println(model.extype+";"+model.message);
        
        
        //create user
        /*
        rr=cc.createUser((new Gson()).toJson(user1), String.class);
        listType = new TypeToken<WSResult<Long>>(){}.getType();
        id=(new Gson()).fromJson(rr, listType); 
        if(id.status) System.out.println(rr);
        else System.out.println(id.extype+";"+id.message);
        rr=cc.createUser((new Gson()).toJson(user2), String.class);
        listType = new TypeToken<WSResult<Long>>(){}.getType();
        id=(new Gson()).fromJson(rr, listType); 
        if(id.status) System.out.println(rr);
        else System.out.println(id.extype+";"+id.message);
        rr=cc.createUser((new Gson()).toJson(user3), String.class);
        listType = new TypeToken<WSResult<Long>>(){}.getType();
        id=(new Gson()).fromJson(rr, listType); 
        if(id.status) System.out.println(rr);
        else System.out.println(id.extype+";"+id.message);
        */
 
        
        
        //create packages
        
        for(int i=0;i<102;i++)
        {            
            if(i % 3 == 0)
            {
                rr=cc.createPackage((new Gson()).toJson(img1), String.class, "23");
            }
            else if(i % 3 == 1)
            {
                rr=cc.createPackage((new Gson()).toJson(img2), String.class, "24");
            }
            else
            {
                rr=cc.createPackage((new Gson()).toJson(img3), String.class, "25");
            }    
            listType = new TypeToken<WSResult<Long>>(){}.getType();
            id=(new Gson()).fromJson(rr, listType); 
            if(id.status) System.out.println(rr);
            else System.out.println(id.extype+";"+id.message);
        }
        
        
        /*
         //create packages        
        rr=cc.createPackage((new Gson()).toJson(img2), String.class, "147");
        listType = new TypeToken<WSResult<Long>>(){}.getType();
        id=(new Gson()).fromJson(rr, listType); 
        if(id.status) System.out.println(rr);
        else System.out.println(id.extype+";"+id.message);
        */
        
        /*
        BiopolisTermQuery tq=new BiopolisTermQuery();
        tq.t_keywords=new String[]{"vasilis"};
        rr=cc.termsearchUsers(tq,String.class);
        System.out.println(rr);
        listType = new TypeToken<WSResult<BiopolisSegmentation>>(){}.getType();
        WSResult<BiopolisSegmentation> a=(new Gson()).fromJson(rr, listType);        
        if(a.status)
        {
            System.out.println(a.result[0].biopolisid);            
            BiopolisSegmentQuery sq=new  BiopolisSegmentQuery();
            sq.biopolisid=a.result[0].biopolisid;
            sq.from=0;
            sq.nodetype=a.result[0].nodetype;
            rr=cc.searchResults(sq, String.class);
            listType = new TypeToken<WSResult<BiopolisResult<Object>>>(){}.getType();
            WSResult<BiopolisResult<Object>> b=(new Gson()).fromJson(rr, listType);
            if(b.status)
            {
                System.out.println(rr);
            }
            else System.out.println(b.extype+";"+b.message);           
        }
        else System.out.println(a.extype+";"+a.message);        
        */
        
        /*
        BiopolisPackageQuery pq=new BiopolisPackageQuery();
        pq.model_query=new BiopolisModelQuery();
        pq.model_query.tags=new Long[]{0L};
        rr=cc.packagesearchPackages(pq,String.class);
        System.out.println(rr);
        */
        
        
        
        cc.close();
    }
    
    
}
