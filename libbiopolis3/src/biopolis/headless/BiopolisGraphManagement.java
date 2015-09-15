/**
 * Licensed to Neo Technology under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package biopolis.headless;

import biopolisdata.topological.exceptions.*;
import biopolisdata.topological.*;
import biopolisdata.*;
import java.net.URI;
import java.util.*;
import java.io.*;
import java.net.URISyntaxException;
import biopolis.exceptions.system.*;
import com.google.gson.Gson;
import java.sql.*;
import org.neo4j.jdbc.*;
import org.apache.commons.codec.binary.Base64;

public class BiopolisGraphManagement {

    public BiopolisPackageManagement p_mgmnt;
    public BiopolisUserManagement u_mgmnt;
    public BiopolisGalleryManagement g_mgmnt;
    public BiopolisLibraryManagement l_mgmnt;
    public BiopolisSpatialManagement s_mgmnt;
    public BiopolisVertexManagement v_mgmnt;
    public BiopolisSegmentationManagement seg_mgmnt;
    public BiopolisPersistencyLayer pl;

    public BiopolisGraphManagement(BiopolisPersistencyLayer somepl) throws URISyntaxException, SQLException, BiopolisGeneralException {
        this.pl = somepl;
        this.v_mgmnt = new BiopolisVertexManagement(this, this.pl);
        this.seg_mgmnt = new BiopolisSegmentationManagement(this.pl);
        this.u_mgmnt = new BiopolisUserManagement(this, this.pl);
        this.p_mgmnt = new BiopolisPackageManagement(this, this.pl);
        this.g_mgmnt = new BiopolisGalleryManagement(this, this.pl);
        this.s_mgmnt = new BiopolisSpatialManagement(this.pl);
        this.l_mgmnt = new BiopolisLibraryManagement(this, this.pl);

    }

    public boolean isInitialized() throws SQLException {
        String queryString = "MATCH (n:DESIGN_NODE) return ID(n)";
        try (ResultSet resultSet = this.pl.getConnNeo4j().createStatement().executeQuery(queryString)) {
            return resultSet.next();
        }
    }

    public void reset() throws SQLException {
        String[] nodenames = {"PACKAGE_NODE", "DESIGN_NODE", "MODEL_NODE", "GALLERY_NODE", "LIBRARY_NODE", "USER_NODE"};
        String[] edgenames = {"CHILD_EDGE", "OWNED_EDGE"};
        for (String edgename : edgenames) {
            String queryString = "MATCH (a)-[r:" + edgename + "]->(b) DELETE r";
            Neo4jPreparedStatement stmt = (Neo4jPreparedStatement) this.pl.getConnNeo4j().prepareStatement(queryString);
            try (ResultSet rs = stmt.executeQuery()) {
            }
        }
        for (String nodename : nodenames) {
            String queryString = "MATCH (a:" + nodename + ") DELETE a";
            Neo4jPreparedStatement stmt = (Neo4jPreparedStatement) this.pl.getConnNeo4j().prepareStatement(queryString);
            try (ResultSet rs = stmt.executeQuery()) {
            }
        }
        this.s_mgmnt.clean();
        this.seg_mgmnt.clean();;
    }

    public void putModel(BiopolisModel model) throws BiopolisActiveException, SQLException, BiopolisGeneralException, BiopolisActiveException, BiopolisModelStructureException {
        if (this.isInitialized()) {
            throw new BiopolisActiveException();
        }
        BiopolisSortedModel smodel = new BiopolisSortedModel(model);
        String json = new Gson().toJson(model, BiopolisModel.class);
        this.alterSystem(smodel.asDiff(), json, false);
    }

    public void updateModel(BiopolisModel newmodel) throws BiopolisInactiveException, SQLException, BiopolisGeneralException, BiopolisModelExtensionException, BiopolisModelStructureException {
        WSResult<BiopolisModel> result = this.describeModel();
        BiopolisModel oldmodel = result.result[0];
        BiopolisSortedModel soldmodel = new BiopolisSortedModel(oldmodel);
        BiopolisSortedModel snewmodel = new BiopolisSortedModel(newmodel);
        BiopolisDiff diff = soldmodel.getDiff(snewmodel);
        String json = new Gson().toJson(newmodel, BiopolisModel.class);
        this.alterSystem(diff, json, true);
    }

    public WSResult<BiopolisModel> describeModel() throws SQLException, BiopolisInactiveException {
        try (ResultSet rs = this.pl.getConnNeo4j().executeQuery("MATCH (a:DESIGN_NODE) RETURN a.desci", null)) {
            if (rs.next()) {
                String jsonbytes = rs.getString(1);
                rs.close();
                String json = new String(Base64.decodeBase64(jsonbytes));
                BiopolisModel model = (new Gson()).fromJson(json, BiopolisModel.class);
                WSResult<BiopolisModel> result = new WSResult<BiopolisModel>(model);
                return result;
            } else {
                throw new BiopolisInactiveException();
            }
        }
    }

    public void alterSystem(BiopolisDiff diff, String json, boolean update) throws BiopolisGeneralException, SQLException {
        Long[] result = this.v_mgmnt.put(diff.sortedvertices);

        for (String s : diff.adjacency) {
            String[] a = s.split(";");
            int from = Integer.parseInt(a[0]);
            int to = Integer.parseInt(a[1]);
            String queryString = "MATCH (a:MODEL_NODE), (b:MODEL_NODE) "
                    + "WHERE (a.coreid={1} AND  b.coreid={2}) CREATE (a)-[r:CHILD_EDGE]->(b) RETURN 1";

            Neo4jPreparedStatement stmt = (Neo4jPreparedStatement) this.pl.getConnNeo4j().prepareStatement(queryString);
            stmt.setInt(1, from);
            stmt.setInt(2, to);
            try (ResultSet rs = stmt.executeQuery()) {
            }
        }
        if (update) {
            String queryString = "MATCH (a:DESIGN_NODE) set a.desci = {1} RETURN 1";
            Neo4jPreparedStatement stmt = (Neo4jPreparedStatement) this.pl.getConnNeo4j().prepareStatement(queryString);
            stmt.setString(1, Base64.encodeBase64String(json.getBytes()));
            try (ResultSet rs = stmt.executeQuery()) {
            }
        } else {
            Map<String, Object> v = new HashMap<String, Object>();
            String queryString = "CREATE (a:DESIGN_NODE { desci: {1} } ) RETURN 1";
            Neo4jPreparedStatement stmt = (Neo4jPreparedStatement) this.pl.getConnNeo4j().prepareStatement(queryString);
            stmt.setString(1, Base64.encodeBase64String(json.getBytes()));
            try (ResultSet rs = stmt.executeQuery()) {
            }
        }
    }
}
