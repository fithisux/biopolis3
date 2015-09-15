/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package biopolis.headless;

import biopolis.exceptions.system.*;
import biopolisdata.*;
import biopolisdata.queries.BiopolisPackageQuery;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import org.neo4j.jdbc.*;

/**
 *
 * @author Manolis
 */
public class BiopolisUserManagement extends BiopolisManager<BiopolisUser> {

    public BiopolisUserManagement(BiopolisGraphManagement bgr, BiopolisPersistencyLayer somepl) {
        super(bgr, somepl, "USER_NODE");
    }

    public Long[] putWithUser(BiopolisUser[] a) throws SQLException, BiopolisGeneralException {
        Long[] result = super.put(a);
        return result;
    }

    public BiopolisSegmentation queryUsersPackages(long userid, BiopolisPackageQuery q) throws SQLException, BiopolisGeneralException {
        this.bgr.p_mgmnt.packageQueryManager(q);
        String queryString = this.bgr.p_mgmnt.getFromUserQ(userid);
        try (ResultSet rs = this.pl.getConnNeo4j().executeQuery(queryString, null)) {
            this.bgr.seg_mgmnt.iterationPhase(rs);
        }
        BiopolisSegmentation segmentation = this.bgr.seg_mgmnt.finalPhase("PACKAGE_NODE");
        return segmentation;
    }

}
