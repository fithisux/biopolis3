/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolis.headless;

import biopolis.exceptions.system.BiopolisGeneralException;
import biopolisdata.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.neo4j.jdbc.Neo4jPreparedStatement;

/**
 *
 * @author vanag
 */
public class BiopolisVertexManagement extends BiopolisManager<BiopolisModelVertex> {

    public BiopolisVertexManagement(BiopolisGraphManagement bgr, BiopolisPersistencyLayer somepl) {
        super(bgr, somepl, "MODEL_NODE");
    }

    public List<BiopolisResult<BiopolisModelVertex>> getWithTags(Long[] coreids) throws SQLException, BiopolisGeneralException {
        String lista = BiopolisUtilities.toNeo4JArglist(coreids);
        if (lista.isEmpty()) {
            return new ArrayList<BiopolisResult<BiopolisModelVertex>>();
        }
        String queryString = " MATCH (n:MODEL_NODE) WHERE n.coreid in" + lista + " RETURN DISTINCT(ID(n))";
        Neo4jPreparedStatement stmt = (Neo4jPreparedStatement) this.pl.getConnNeo4j().prepareStatement(queryString);
        try (ResultSet rs = stmt.executeQuery()) {
            List<Long> ids = new ArrayList<Long>();
            while (rs.next()) {
                Long id = rs.getLong(1);
                ids.add(id);
            }
            return this.get(ids.toArray(new Long[0]));
        }
    }

    public List<Long> getWithCoreids(Long[] coreids) throws SQLException, BiopolisGeneralException {
        String lista = BiopolisUtilities.toNeo4JArglist(coreids);
        System.out.println("lista is " + lista);
        if (lista.isEmpty()) {
            return new ArrayList<Long>();
        }
        String queryString = " MATCH (n:MODEL_NODE) WHERE n.coreid IN" + lista + " RETURN DISTINCT(ID(n))";
        Neo4jPreparedStatement stmt = (Neo4jPreparedStatement) this.pl.getConnNeo4j().prepareStatement(queryString);
        try (ResultSet rs = stmt.executeQuery()) {
            List<Long> ids = new ArrayList<Long>();
            while (rs.next()) {
                Long id = rs.getLong(1);
                ids.add(id);
            }
            return ids;
        }
    }

    public List<Long> getAllCoreids() throws SQLException {
        String queryString = " MATCH (n:MODEL_NODE)  RETURN ID(n)";
        Neo4jPreparedStatement stmt = (Neo4jPreparedStatement) this.pl.getConnNeo4j().prepareStatement(queryString);
        try (ResultSet rs = stmt.executeQuery()) {
            List<Long> ll = new ArrayList<Long>();
            while (rs.next()) {
                ll.add(rs.getLong(1));
            }
            return ll;
        }
    }
}
