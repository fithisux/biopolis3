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
public class BiopolisLibraryManagement extends BiopolisManager<BiopolisLibrary> {

    public BiopolisLibraryManagement(BiopolisGraphManagement bgr, BiopolisPersistencyLayer somepl) {
        super(bgr, somepl, "LIBRARY_NODE");
    }

    public Long[] putWithUser(long userid, BiopolisLibrary[] a) throws SQLException, BiopolisGeneralException {
        Long[] result = super.putWithUser(userid, a);
        return result;
    }

    public BiopolisSegmentation getPackages(long ident) throws SQLException, BiopolisGeneralException {
        Long[] ids = {ident};
        BiopolisResult<BiopolisLibrary> lib = this.get(ids).get(0);
        return this.bgr.p_mgmnt.queryPackages(lib.object.query);
    }
}
