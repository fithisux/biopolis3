/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolis.headless;

import java.net.UnknownHostException;
import java.util.*;
import biopolisdata.queries.*;
import biopolisdata.*;
import biopolis.exceptions.system.*;
import com.mongodb.*;
import java.sql.ResultSet;
import java.util.Iterator;
import java.sql.*;
import org.bson.types.ObjectId;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author vanag
 */
public class BiopolisSegmentationManagement {

    BiopolisGraphManagement bgr;
    int occurs;
    ObjectId id;
    String biopiolisid;
    DBCollection dbcol;
    int segSZ;

    public BiopolisSegmentationManagement(BiopolisPersistencyLayer somepl) throws BiopolisGeneralException {
        this.dbcol = somepl.getDbcolSearches();
        this.occurs = 0;
        this.segSZ = somepl.getSegSZ();
    }

    public void clean() {
        dbcol.remove(new BasicDBObject());
    }

    public void initialPhase() throws SQLException {
        this.occurs = 0;
        BasicDBObject master = new BasicDBObject();
        master.put("master", "master");
        master.put("biopolisttl", new java.util.Date());
        dbcol.insert(master);
        id = (ObjectId) master.get("_id");
        byte[] bytes = id.toByteArray();
        biopiolisid = Base64.encodeBase64String(bytes);
    }

    public java.util.Date updateTTL() throws BiopolisGeneralException {
        java.util.Date date = new java.util.Date();
        DBCursor cur = dbcol.find(new BasicDBObject("_id", id));
        if (!cur.hasNext()) {
            throw new BiopolisGeneralException("Search trashed");
        }
        BasicDBObject setter = new BasicDBObject("$set", new BasicDBObject("biopolisttl", date));
        dbcol.update(new BasicDBObject("_id", id), setter);
        dbcol.update(new BasicDBObject("biopolishandle", this.biopiolisid), setter);
        return date;
    }

    public void iterationPhase(ResultSet resultSet) throws SQLException, BiopolisGeneralException {
        java.util.Date date = this.updateTTL();
        while (resultSet.next()) {
            long ll = resultSet.getLong(1);
            BasicDBObject place = new BasicDBObject();
            place.put("biopolishandle", this.biopiolisid);
            place.put("biopolisdata", ll);
            place.put("biopolisttl", date);
            dbcol.insert(place);
        }
        this.occurs++;
    }

    /*    
     public void iterationPhase(List<Long> names) throws SQLException,BiopolisGeneralException
     {        
     java.util.Date date=this.updateTTL();
     for(Long name : names)
     {
     BasicDBObject place = new BasicDBObject();          
     place.put("biopolishandle", this.biopiolisid);
     place.put("biopolisdata", name);            
     place.put("biopolisttl",date);
     dbcol.insert(place);
     }        
     this.occurs++;
     }
    
     */
    public void iterationPhase(DBCursor cur) throws SQLException, BiopolisGeneralException {
        java.util.Date date = this.updateTTL();
        while (cur.hasNext()) {
            Long name = (Long) cur.next().get("biopolisdata");
            BasicDBObject place = new BasicDBObject();
            place.put("biopolishandle", this.biopiolisid);
            place.put("biopolisdata", name);
            place.put("biopolisttl", date);
            dbcol.insert(place);
        }
        this.occurs++;
    }

    public BiopolisSegmentation finalPhase(String nodetype) throws SQLException, BiopolisGeneralException {
        java.util.Date date = this.updateTTL();
        List ids = dbcol.distinct("biopolisdata", new BasicDBObject("biopolishandle", this.biopiolisid));
        for (Object id : ids) {
            BasicDBObject aggregate = new BasicDBObject();
            aggregate.put("biopolishandle", this.biopiolisid);
            aggregate.put("biopolisdata", (Long) id);
            long count = dbcol.count(aggregate);
            if (count == this.occurs) {
                BasicDBObject insertspec = new BasicDBObject();
                insertspec.put("biopolisid", this.biopiolisid);
                insertspec.put("biopolisdata", (Long) id);
                insertspec.put("biopolisttl", date);
                dbcol.insert(insertspec);
            }
        }
        long count = dbcol.count(new BasicDBObject("biopolisid", this.biopiolisid));
        //finalize segmentation
        BasicDBObject place = new BasicDBObject();
        place.put("biopolisnodetype", nodetype);
        place.put("biopoliscount", count);
        place.put("biopolisttl", new java.util.Date());
        BasicDBObject setter = new BasicDBObject("$set", place);
        dbcol.update(new BasicDBObject("_id", id), setter);
        BiopolisSegmentation segmentation = new BiopolisSegmentation();
        segmentation.nodetype = nodetype;
        segmentation.results = count;
        segmentation.biopolisid = this.biopiolisid;
        date = new java.util.Date();
        BasicDBObject updater = new BasicDBObject("$set", new BasicDBObject("biopolisttl", date));
        dbcol.update(new BasicDBObject("_id", this.id), updater);
        dbcol.update(new BasicDBObject("biopolisid", this.biopiolisid), updater);
        return segmentation;
    }

    public List<Long> search(BiopolisSegmentQuery seg) throws BiopolisGeneralException {
        byte[] bytes = Base64.decodeBase64(seg.biopolisid);
        ObjectId someid = new ObjectId(bytes);
        DBCursor cur = dbcol.find(new BasicDBObject("_id", someid));
        if (!cur.hasNext()) {
            throw new BiopolisGeneralException("Search not exists with id " + seg.biopolisid);
        }
        BasicDBObject setter = new BasicDBObject("$set", new BasicDBObject("biopolisttl", new java.util.Date()));
        dbcol.update(new BasicDBObject("_id", someid), setter);
        dbcol.update(new BasicDBObject("biopolisid", seg.biopolisid), setter);

        List<Long> resus = new ArrayList<Long>();
        cur = dbcol.find(new BasicDBObject("biopolisid", seg.biopolisid));
        cur = cur.sort(new BasicDBObject("biopolisdata", 1));

        int count = 0;
        while (cur.hasNext()) {
            System.out.println("scan " + seg.from);
            DBObject obj = cur.next();
            if ((count >= seg.from) && (count < seg.from + this.segSZ)) {
                System.out.println("add");
                Long i = (Long) obj.get("biopolisdata");
                resus.add(i);
            }
            count++;
        }
        System.out.println(resus.size());
        return resus;
    }

}
