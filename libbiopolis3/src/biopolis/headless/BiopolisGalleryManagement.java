/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolis.headless;

import biopolis.exceptions.system.*;
import biopolisdata.queries.*;
import biopolisdata.*;
import com.google.gson.Gson;
import java.util.*;
import org.neo4j.jdbc.*;
import java.sql.*;

/**
 *
 * @author Manolis
 */
public class BiopolisGalleryManagement extends BiopolisManager<BiopolisGallery> {

    public BiopolisGalleryManagement(BiopolisGraphManagement bgr, BiopolisPersistencyLayer somepl) {
        super(bgr, somepl, "GALLERY_NODE");
    }

    public void addPackages(long galleryid, Long[] pkgids) throws SQLException, BiopolisGeneralException {
        String lista = BiopolisUtilities.toNeo4JArglist(pkgids);
        if (lista.isEmpty()) {
            return;
        }
        String queryString = "MATCH (a:GALLERY_NODE), (b:PACKAGE_NODE) "
                + "WHERE ( ID(a)=" + galleryid + " AND ID(b) IN " + lista + ")  "
                + "MERGE (a)-[r:OWNED_EDGE]->(b) RETURN count(*)";
        try (ResultSet rs = this.pl.getConnNeo4j().executeQuery(queryString, null)) {
            if (!rs.next()) {
                throw new BiopolisGeneralException("add package from gallery failed");
            }
        }
    }

    public void removePackages(long galleryid, Long[] pkgids) throws SQLException {
        String lista = BiopolisUtilities.toNeo4JArglist(pkgids);
        if (lista.isEmpty()) {
            return;
        }
        String queryString = "MATCH (a:GALLERY_NODE)-[r:OWNED_EDGE]->(b:PACKAGE_NODE) "
                + "WHERE ( ID(a)=" + galleryid + " AND ID(b) IN " + lista + ")  "
                + "DELETE r";
        try (ResultSet rs = this.pl.getConnNeo4j().executeQuery(queryString, null)) {

        }

    }

    public String getPackagesInternalQ(long galleryid) throws SQLException {
        String queryString = "MATCH (a:GALLERY_NODE)-[r:OWNED_EDGE]->(b:PACKAGE_NODE) "
                + "WHERE ID(a)=" + galleryid + " RETURN ID(b)";
        return queryString;
    }

    public String getPackagesExternalQ(long galleryid) throws SQLException {
        String queryString = "MATCH (a:GALLERY_NODE),(b:PACKAGE_NODE)"
                + "WHERE ( "
                + "(ID(a)=" + galleryid + ")"
                + "AND "
                + "( NOT (a)-[:OWNED_EDGE]->(b) )"
                + ") RETURN ID(b)";
        return queryString;
    }

    public BiopolisSegmentation getPackages(long galleryid) throws SQLException, BiopolisGeneralException {
        if (this.bgr == null) {
            System.out.println("bgr null");
        }
        if (this.bgr.seg_mgmnt == null) {
            System.out.println("seg_mngmt null");
        }
        this.bgr.seg_mgmnt.initialPhase();
        String queryString = this.getPackagesInternalQ(galleryid);
        try (ResultSet rs = this.pl.getConnNeo4j().executeQuery(queryString, null)) {
            this.bgr.seg_mgmnt.iterationPhase(rs);
        }
        BiopolisSegmentation segmentation = this.bgr.seg_mgmnt.finalPhase("PACKAGE_NODE");
        return segmentation;
    }

    public BiopolisSegmentation queryGalleryInternal(long galleryid, BiopolisPackageQuery q) throws SQLException, BiopolisGeneralException {
        this.bgr.p_mgmnt.packageQueryManager(q);
        String queryString = this.getPackagesInternalQ(galleryid);
        try (ResultSet rs = this.pl.getConnNeo4j().executeQuery(queryString, null)) {
            this.bgr.seg_mgmnt.iterationPhase(rs);
        }
        BiopolisSegmentation segmentation = this.bgr.seg_mgmnt.finalPhase("PACKAGE_NODE");
        return segmentation;
    }

    public BiopolisSegmentation queryGalleryExternal(long galleryid, BiopolisPackageQuery q) throws SQLException, BiopolisGeneralException {
        this.bgr.p_mgmnt.packageQueryManager(q);
        String queryString = this.getPackagesExternalQ(galleryid);
        try (ResultSet rs = this.pl.getConnNeo4j().executeQuery(queryString, null)) {
            this.bgr.seg_mgmnt.iterationPhase(rs);
        }
        BiopolisSegmentation segmentation = this.bgr.seg_mgmnt.finalPhase("PACKAGE_NODE");
        return segmentation;
    }
}
