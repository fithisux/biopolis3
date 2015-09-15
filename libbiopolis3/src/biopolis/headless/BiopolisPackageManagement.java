/*
 * Viopolis Package Management
 Ο ρόλος αυτού του component είναι να διαχειρίζεται για λογαριασμό του χρήστη τα ΒΙΩΠΟΛΙΣ πακέτα του. 
 * Ο χρήστης μπορεί να ανεβάζει ή να διαγράφει τα πακέτα του. Ο χρήστης δεν μπορεί να βλέπει τα πακέτα τα 
 * οποία ανέβασε παρακάππτοντας το κυρίως Interface για αναζήτηση του ΒΙΩΠΟΛΙΣ.
 */
package biopolis.headless;

import biopolisdata.queries.BiopolisModelQuery;
import biopolisdata.*;
import biopolis.exceptions.system.*;
import biopolisdata.queries.BiopolisPackageQuery;
import biopolisdata.queries.BiopolisTermQuery;
import java.util.*;
import biopolisdata.topological.*;
import biopolisdata.topological.exceptions.*;
import com.mongodb.BasicDBObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.neo4j.jdbc.Neo4jPreparedStatement;

/**
 *
 * @author Manolis
 */
public class BiopolisPackageManagement extends BiopolisManager<BiopolisPackage> {

    public BiopolisPackageManagement(BiopolisGraphManagement bgr, BiopolisPersistencyLayer somepl) {
        super(bgr, somepl, "PACKAGE_NODE");
    }

    public Long[] putWithUser(long userid, BiopolisPackage[] a) throws SQLException, BiopolisGeneralException {
        Long[] result = super.putWithUser(userid, a);

        for (int i = 0; i < a.length; i++) {
            List<Long> ids = this.bgr.v_mgmnt.getWithCoreids(a[i].tags);
            System.out.println("How many " + ids.size() + " of " + a[i].tags.length);
            for (Long id : ids) {
                System.out.println("with coreid = " + id);
                String queryString = "MATCH (n:MODEL_NODE), (m:PACKAGE_NODE)"
                        + " WHERE ( ID(m)={1} and  ID(n)={2} )"
                        + " CREATE (n)-[r:OWNED_EDGE]->(m) RETURN 1";

                Neo4jPreparedStatement stmt = (Neo4jPreparedStatement) this.pl.getConnNeo4j().prepareStatement(queryString);
                stmt.setLong(1, result[i]);
                stmt.setLong(2, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new BiopolisGeneralException("Could not tag package");
                    }
                }
            }
            this.bgr.s_mgmnt.addPlace(result[i], new double[]{a[i].longitude, a[i].latitude});
        }
        return result;
    }

    public String queryPackagesModelQ(BiopolisModelQuery mq) throws SQLException {
        String matchString = " MATCH (x:MODEL_NODE)-[*0..1000]->(b:MODEL_NODE)-[:OWNED_EDGE]->(a:PACKAGE_NODE) ";

        String inString = BiopolisUtilities.toNeo4JArglist(mq.tags);
        if (!inString.isEmpty()) {
            inString = "x.coreid IN " + inString;
        }

        String timeString = "";
        if ((mq.from == null) && (mq.to != null)) {
            timeString = " a.captureTime <=" + mq.to + " ";
        } else if ((mq.from != null) && (mq.to == null)) {
            timeString = " a.captureTime >=" + mq.from + " ";
        } else if ((mq.from != null) && (mq.to != null)) {
            timeString += " ( a.captureTime >=" + mq.from + " AND a.captureTime <=" + mq.to + " ) ";
        }

        String whereString = "";

        if (timeString.isEmpty()) {
            whereString = inString;
        } else {
            if (inString.isEmpty()) {
                whereString = timeString;
            } else {
                whereString = "(" + timeString + " AND " + inString + ") ";
            }
        }

        if (!whereString.isEmpty()) {
            whereString = "WHERE " + whereString + " ";
        }

        String queryString = matchString + whereString + "RETURN DISTINCT ID(a) ";
        return queryString;
    }

    public void packageQueryManager(BiopolisPackageQuery q) throws SQLException, BiopolisGeneralException {
        if (q == null) {
            throw new BiopolisGeneralException("cannot query packages with empty query");
        }
        if ((q.model_query == null) && (q.spatial_query == null) && (q.term_query == null)) {
            throw new BiopolisGeneralException("cannot query packages with empty query");
        }
        this.bgr.seg_mgmnt.initialPhase();
        if (q.model_query != null) {
            String queryString = this.bgr.p_mgmnt.queryPackagesModelQ(q.model_query);
            try (ResultSet rs = this.pl.getConnNeo4j().executeQuery(queryString, null)) {
                this.bgr.seg_mgmnt.iterationPhase(rs);
            }
        }
        if (q.term_query != null) {
            String s = BiopolisUtilities.makeContext(q.term_query);
            if (!s.isEmpty()) {
                String queryString = "START n=node:node_auto_index(\"" + s + "\") RETURN ID(n)";
                try (ResultSet rs = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
                    this.bgr.seg_mgmnt.iterationPhase(rs);
                }
            }
        }
        if (q.spatial_query != null) {
            com.mongodb.DBCursor cur = this.bgr.s_mgmnt.queryPackagesSpatial(q.spatial_query);
            this.bgr.seg_mgmnt.iterationPhase(cur);
        }
    }

    public BiopolisSegmentation queryPackages(BiopolisPackageQuery q) throws SQLException, BiopolisGeneralException {
        this.packageQueryManager(q);
        BiopolisSegmentation segmentation = this.bgr.seg_mgmnt.finalPhase(this.nodetype);
        return segmentation;
    }

    public BiopolisSegmentation getGalleries(long pkgid) throws SQLException, BiopolisGeneralException {
        this.bgr.seg_mgmnt.initialPhase();
        String queryString = "MATCH (a:GALLERY_NODE)-[r:OWNED_EDGE]->(b:" + this.nodetype + ") "
                + "WHERE ID(b)=" + pkgid + " RETURN ID(a)";
        try (ResultSet rs = this.pl.getConnNeo4j().executeQuery(queryString, null)) {
            this.bgr.seg_mgmnt.iterationPhase(rs);
        }
        BiopolisSegmentation segmentation = this.bgr.seg_mgmnt.finalPhase("GALLERY_NODE");
        return segmentation;

    }
}
