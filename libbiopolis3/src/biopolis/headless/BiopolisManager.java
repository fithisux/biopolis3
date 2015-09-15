/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolis.headless;

import biopolisdata.BiopolisDescriptor;
import biopolis.exceptions.system.*;
import biopolisdata.queries.*;
import biopolisdata.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.sql.ResultSet;
import java.util.*;
import org.neo4j.jdbc.*;
import java.sql.*;
import javax.ws.rs.PathParam;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author vanag
 */
public abstract class BiopolisManager<T extends BiopolisDescriptor> {

    BiopolisGraphManagement bgr;
    String nodetype;
    BiopolisPersistencyLayer pl;

    public BiopolisManager(BiopolisGraphManagement bgr, BiopolisPersistencyLayer somepl, String nodetype) {
        this.pl = somepl;
        this.bgr = bgr;
        this.nodetype = nodetype;
    }

    public List<BiopolisResult<T>> get(Long[] ids) throws SQLException, BiopolisGeneralException {
        List<BiopolisResult<T>> objects = new ArrayList<BiopolisResult<T>>();
        String lista = BiopolisUtilities.toNeo4JArglist(ids);
        if (!lista.isEmpty()) {
            String queryString = " MATCH (n:" + this.nodetype + ") WHERE ID(n) in" + lista + "  RETURN ID(n),n.desci";
            try (ResultSet rs = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
                while (rs.next()) {
                    String jsonbytes = rs.getString(2);
                    String json = new String(Base64.decodeBase64(jsonbytes));
                    java.lang.reflect.Type listType = new TypeToken<T>() {
                    }.getType();
                    BiopolisResult<T> result = new BiopolisResult<T>();
                    result.id = rs.getLong(1);
                    result.object = (new Gson()).fromJson(json, listType);
                    objects.add(result);
                }
            }
        }
        return objects;
    }

    public List<Long> getAll() throws SQLException, BiopolisGeneralException {
        List<Long> objects = new ArrayList<>();
        String queryString = " MATCH (n:" + this.nodetype + ")   RETURN ID(n)";
        try (ResultSet rs = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
            while (rs.next()) {
                objects.add(rs.getLong(1));
            }
        }
        return objects;
    }
    
    public Long[] put(T[] a) throws SQLException, BiopolisGeneralException {
        Long[] ids = new Long[a.length];
        int index = 0;

        for (T x : a) {
            System.out.println(index + "  count  " + a.length);
            String json = (new Gson()).toJson(x);
            System.out.println(json);
            Map<String, Object> map = x.getMap();
            String desci = Base64.encodeBase64String(json.getBytes());
            map.put("desci", desci);
            String queryString = " CREATE (n:" + this.nodetype + " {1} )  RETURN ID(n)";
            Map<String, Object> mm = new HashMap<String, Object>();
            mm.put("1", map);
            try (ResultSet rs = this.pl.getConnNeo4j().executeQuery(queryString, mm)) {
                if (!rs.next()) {
                    throw new BiopolisGeneralException("Cannot create " + json);
                } else {
                    Long id = rs.getLong(1);
                    System.out.println(id);
                    ids[index++] = id;
                }
                System.out.println("do");
            }
        }
        System.out.println("ok");
        return ids;
    }

    public Long[] putWithUser(long userid, T[] a) throws SQLException, BiopolisGeneralException {
        if (this.nodetype.equalsIgnoreCase("USER_NODE")) {
            throw new BiopolisGeneralException("User is not owned by a user");
        }
        Long[] ids = this.put(a);
        for (Long id : ids) {
            String queryString = "MATCH (m:USER_NODE),(n:" + this.nodetype + ") "
                    + "WHERE ( ID(m)=" + userid + " AND ID(n)=" + id + ")"
                    + "CREATE (m)-[r:OWNED_EDGE]->(n) RETURN 1";
            try (ResultSet rs = this.pl.getConnNeo4j().executeQuery(queryString, null)) {
                if (!rs.next()) {
                    throw new BiopolisGeneralException("Cannot create edge to " + id);
                }
            }
        }
        return ids;
    }

    public void delete(Long[] ids) throws SQLException {
        String lista = BiopolisUtilities.toNeo4JArglist(ids);
        if (lista.isEmpty()) {
            return;
        }
        String queryString;

        if (this.nodetype.equalsIgnoreCase("USER_NODE")) {
            Long[] garbageids;
            List<Long> usergarbage = new ArrayList<Long>();
            //delete libraries
            queryString = "MATCH (n:USER_NODE)-[r:OWNED_EDGE]->(a:LIBRARY_NODE) WHERE ID(n) in " + lista + " RETURN ID(a)";
            try (ResultSet rs = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
                while (rs.next()) {
                    Long l = rs.getLong(1);
                    usergarbage.add(l);
                }
            }

            garbageids = usergarbage.toArray(new Long[0]);
            this.bgr.l_mgmnt.delete(garbageids);
            usergarbage.clear();
            //delete galleries
            queryString = "MATCH (n:USER_NODE)-[r:OWNED_EDGE]->(a:GALLERY_NODE) WHERE ID(n) in " + lista + " RETURN ID(a)";
            try (ResultSet rs = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
                while (rs.next()) {
                    Long l = rs.getLong(1);
                    usergarbage.add(l);
                }
            }

            garbageids = usergarbage.toArray(new Long[0]);
            this.bgr.g_mgmnt.delete(garbageids);
            usergarbage.clear();
            //delete packages            
            queryString = "MATCH (n:USER_NODE)-[r:OWNED_EDGE]->(a:PACKAGE_NODE) WHERE ID(n) in " + lista + " RETURN ID(a)";
            try (ResultSet rs = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
                while (rs.next()) {
                    Long l = rs.getLong(1);
                    usergarbage.add(l);
                }
            }
            garbageids = usergarbage.toArray(new Long[0]);
            this.bgr.p_mgmnt.delete(garbageids);
            usergarbage.clear();
        } else {
            queryString = "MATCH a-[r:OWNED_EDGE]->(n:" + this.nodetype + ") WHERE ID(n) in " + lista + " DELETE r";
            try (ResultSet rs = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
            }
            queryString = "MATCH (n:" + this.nodetype + ")-[r:OWNED_EDGE]->a WHERE ID(n) in " + lista + " DELETE r";
            try (ResultSet rs = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
            }
            if (this.nodetype.equalsIgnoreCase("PACKAGE_NODE")) {
                for (Long name : ids) {
                    this.bgr.s_mgmnt.removePlace(name);
                }
            }
        }

        queryString = "MATCH (n:" + this.nodetype + ") WHERE ID(n) in " + lista + " DELETE n";
        try (ResultSet rs = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
        }
    }

    public BiopolisResult<BiopolisUser> getUser(long id) throws SQLException, BiopolisGeneralException {
        String queryString = "MATCH (a:USER_NODE)-[r:OWNED_EDGE]->(b:" + this.nodetype + ") "
                + "WHERE ID(b)=" + id + " RETURN ID(a)";
        try (ResultSet rs = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
            if (rs.next()) {
                Long[] ids = new Long[1];
                ids[0] = rs.getLong(1);
                System.out.println("Expected user == " + id);
                return this.bgr.u_mgmnt.get(ids).get(0);
            } else {
                return null;
            }
        }
    }

    public String getFromUserQ(long userid) {
        String queryString = "MATCH (a:USER_NODE)-[r:OWNED_EDGE]->(b:" + this.nodetype + ") "
                + "WHERE ID(a)=" + userid + " RETURN ID(b)";
        return queryString;
    }

    public BiopolisSegmentation getFromUser(long userid) throws SQLException, BiopolisGeneralException {
        this.bgr.seg_mgmnt.initialPhase();
        String queryString = "MATCH (a:USER_NODE)-[r:OWNED_EDGE]->(b:" + this.nodetype + ") "
                + "WHERE ID(a)=" + userid + " RETURN ID(b)";
        try (ResultSet rs = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
            this.bgr.seg_mgmnt.iterationPhase(rs);
        }
        BiopolisSegmentation segmentation = this.bgr.seg_mgmnt.finalPhase(nodetype);
        return segmentation;
    }

    public BiopolisSegmentation queryTerm(BiopolisTermQuery tq) throws SQLException, BiopolisGeneralException {
        String s = BiopolisUtilities.makeContext(tq);
        this.bgr.seg_mgmnt.initialPhase();
        String queryString = "START n=node:node_auto_index(\"" + s + "\") RETURN ID(n)";
        try (ResultSet rs = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
            this.bgr.seg_mgmnt.iterationPhase(rs);
        }
        BiopolisSegmentation segmentation = this.bgr.seg_mgmnt.finalPhase(nodetype);
        return segmentation;
    }

    public BiopolisSegmentation queryUserTerm(long userid, BiopolisTermQuery tq) throws SQLException, BiopolisGeneralException {
        String s = BiopolisUtilities.makeContext(tq);
        this.bgr.seg_mgmnt.initialPhase();
        String queryString = "START n=node:node_auto_index(\"" + s + "\") RETURN ID(n)";
        try (ResultSet rs = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
            this.bgr.seg_mgmnt.iterationPhase(rs);
        }
        queryString = this.getFromUserQ(userid);
        try (ResultSet rs = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
            this.bgr.seg_mgmnt.iterationPhase(rs);
        }
        BiopolisSegmentation segmentation = this.bgr.seg_mgmnt.finalPhase(nodetype);
        return segmentation;
    }

    public WSResult<BiopolisResult<T>> getActive(long userid) {
        try {
            BiopolisSegmentation seg = this.getFromUser(userid);
            BiopolisSegmentQuery seg_q = new BiopolisSegmentQuery();
            seg_q.nodetype = seg.nodetype;
            seg_q.biopolisid = seg.biopolisid;
            seg_q.from = 0;
            List<Long> ll = this.bgr.seg_mgmnt.search(seg_q);
            Long[] ids = ll.toArray(new Long[0]);
            return new WSResult<BiopolisResult<T>>(this.get(ids));
        } catch (SQLException ex) {
            return new WSResult<BiopolisResult<T>>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisResult<T>>(ex.getClass().getName(), ex.getMessage());
        }
    }

    public Long createIT(long userid, T[] payload) throws SQLException, BiopolisGeneralException {
        Long libid = this.putWithUser(userid, payload)[0];
        System.out.println("libid =" + libid);
        return libid;
    }

    public WSResult<String> deleteIT(long pkgid) {
        try {
            Long[] ids = {pkgid};
            this.delete(ids);
            return new WSResult<String>("OK");
        } catch (SQLException ex) {
            return new WSResult<String>(ex.getClass().getName(), ex.getMessage());
        }
    }

    public WSResult<BiopolisResult<T>> getIT(long pkgid) {
        try {
            Long[] ids = {pkgid};
            return new WSResult<BiopolisResult<T>>(this.get(ids));
        } catch (SQLException ex) {
            return new WSResult<BiopolisResult<T>>(ex.getClass().getName(), ex.getMessage());
        } catch (BiopolisGeneralException ex) {
            return new WSResult<BiopolisResult<T>>(ex.getClass().getName(), ex.getMessage());
        }
    }

}
